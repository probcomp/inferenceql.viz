(ns inferenceql.viz.panels.upload.effects
  (:require [re-frame.core :as rf]
            [cljs.core.async :as async :refer [go put! <!]]
            [clojure.string :as str]
            [ajax.core]
            [ajax.edn]
            [clojure.edn :as edn]))

(defn model-type
  "Returns a model-type keyword depending on the filename for the model.

  The filename itself can be pulled from a Javascript file object, url string, or a
  filename string.

  Args:
    `type` - A keyword for the type of `obj`
    `obj` - An obj from which to pull a filename."
  [type obj]
  (let [filename (case type
                   :filename obj
                   :url (last (str/split obj #"/"))
                   :file (.-name obj)
                   nil)
        file-ext (when filename (last (str/split filename #"\.")))]
    (case file-ext
      "edn" :multimix
      "json" :bayes-db-export
       nil)))

(defmulti put-read
  "Reads data given a read-map and puts the completed read-map onto `chan`.

    Args:
      `read` - A read-map which specifies a file object or url to read.
      `chan` - A core.async channel to put the completed read-map."
  (fn [read chan] (:read-type read)))

(defmethod put-read :url [read chan]
  (ajax.core/ajax-request
   {:uri (:target read)
    :method :get
    :handler (fn [[status data]]
               ;; :raw-data may be file data or failure info.
               (put! chan (assoc read :success status :raw-data data)))
    :response-format (ajax.core/text-response-format)}))

(defmethod put-read :file [read chan]
  (let [rdr (js/FileReader.)
        on-load (fn [_]
                  (this-as this
                    (let [contents (.-result this)]
                      (put! chan (assoc read :success true :raw-data contents)))))
        on-error (fn [error]
                   (this-as this
                     (put! chan (assoc read :success false :raw-data error))))]
    (set! (.-onload rdr) on-load)
    (set! (.-onerror rdr) on-error)
    (.readAsText rdr (:target read))))

(defn take-reads
  "Takes a number of read-maps from a channel and dispatches a success event with the reads.

  If any of the reads has failed, the failure event is dispatched with an error message.

  Args:
    `chan` -- (core.async channel) Used to take an unknown number of read-maps. The
      read-maps will not be taken until this channel has been closed.
    `on-success` -- (vector) a reframe event vector for dispatching on success.
    `on-failure` -- (vector) a reframe event vector for dispatching on failure."
  [chan on-success on-failure]
  (go
   (let [reads (<! (async/into [] chan))]
     (if (every? :success reads)
       (rf/dispatch (conj on-success reads))
       (let [failures (remove :success reads)
             failure-messages (for [{:keys [read-type target] :as failed-read} failures]
                                (case read-type
                                  :file (str "Failed reading local file " (.-name target))
                                  :url (str "Failed reading url " target)
                                  :else (str "Failed reading the item with the follow read-map " failed-read)))]
         (rf/dispatch (conj on-failure (str/join "\n" failure-messages))))))))

(defn ^:effect read-form-effect
  "Reads files selected as part of form for adding a new dataset and model.

  This reads the specified files or urls and passes them to the :on-success event specified.
  It does by creating a number of readmaps and passing them to the `put-read` function, which
  does the actually reading.

  Args:
    `params` -- A map of parameters with the following keys.
      :dataset -- A vector with two elements. A read-type and a file object
        or url specifiying a dataset.
      :schema -- A vector with two elements. A read-type and a file object
        or url specifiying a schema for the dataset.
      :model -- A vector with two elements. A read-type and a file object
        or url specifiying a model.
      :on-success -- (vector) A reframe event vector to dispatch on success.
      :on-failure -- (vector) A reframe event vector to dispatch on failure.

  Dispatched by:
    The re-frame event :upload/read-form, which was dispatched when the form was originally
      submitted."
  [params]
  (let [{:keys [dataset model on-success on-failure]} params
        ;; Accessors functions
        read-type first
        target second

        ;; Will be used to put all file reads.
        file-reads (async/chan)
        items-to-fetch [;; Dataset read.
                        {:read-type (read-type dataset)
                         :kind :dataset
                         :id :data
                         :target (target dataset)
                         :details {:default-model :model}}
                        ;; Model read.
                        {:read-type (read-type model)
                         :kind :model
                         :id :model
                         :target (target model)
                         :model-type (apply model-type model)
                         :dataset :data}]]
    (doseq [item items-to-fetch]
      (put-read item file-reads))
    (let [closing-chan (async/take (count items-to-fetch) file-reads)]
      (take-reads closing-chan on-success on-failure))))
(rf/reg-fx :upload/read-form-effect
           read-form-effect)

(defn ^:effect read-config-url-effect
  "Reads a config url and many other urls for loading a particular demo configuration of the app.

  This first makes a request for a config.edn file. Based on the data in that config.edn file,
  many other http requests will be made for datasets, schema files, models, and geodata files.
  When the reads are completed, they will be dispatched to the `on-success` event.

  Args:
    `params` -- A map of parameters with the following keys.
      :config-url -- (string) The url used to fetch a config.edn file. It will also be used
        to obtain the base url for making http requests for other files.
      :on-success -- (vector) A reframe event vector to dispatch on success.
      :on-failure -- (vector) A reframe event vector to dispatch on failure.

  Dispatched by:
    The re-frame event :upload/read-query-string-params."
  [params]
  (let [{:keys [config-url on-success on-failure]} params
        [_ base-url] (re-matches #"^(.*/).*$" config-url)

        ;; Used to build urls for additional files we have to fetch.
        url-for (fn [filename] (str base-url filename))

        ;; Will be used to put the config.edn file read.
        config-read (async/chan)]

    (ajax.core/ajax-request
      {:uri config-url
       :method :get
       :handler #(put! config-read %)
       :response-format (ajax.core/text-response-format)})

    (go
     (let [[status data] (<! config-read)]
       (if (false? status)
         (let [failure-msg (str "Could not read config.edn at " config-url)]
           (rf/dispatch (conj on-failure failure-msg)))

         (let [config (edn/read-string data)

               ;; Will be used to put all the additional file reads.
               file-reads (async/chan)

               items-to-fetch (flatten
                               (for [category [:datasets :models :geodata]]
                                 (let [items-in-category (get config category)]
                                   (for [[item-key item] items-in-category]
                                     (case category
                                       :datasets {:read-type :url
                                                  :kind :dataset
                                                  :id item-key
                                                  :target (str base-url (:filename item))
                                                  :details (select-keys item [:default-model :geodata-name :geo-id-col])}

                                       :models  {:read-type :url
                                                 :kind :model
                                                 :id item-key
                                                 :target (url-for (:filename item))
                                                 :model-type (model-type :filename (:filename item))
                                                 :dataset (:dataset item)}

                                       :geodata {:read-type :url
                                                 :kind :geodata
                                                 :id item-key
                                                 :target (url-for (:filename item))
                                                 :details (select-keys item [:filetype :id-prop :projection-type])})))))]
           (doseq [item items-to-fetch]
             (put-read item file-reads))
           (let [closing-chan (async/take (count items-to-fetch) file-reads)]
             (take-reads closing-chan on-success on-failure))))))))
(rf/reg-fx :upload/read-config-url-effect
           read-config-url-effect)

(defn ^:effect read-individual-urls-effect
  "Reads a number of urls for loading a particular demo configuration of the app.

  This will read urls for a dataset, schema, and model. When the reads are completed,
  they will be dispatched to the `on-success` event.

  Args:
    `params` -- A map of parameters with the following keys.
      :schema-url -- (string) The url used to fetch a schema file.
      :data-url -- (string) The url used to fetch a dataset file.
      :model-url -- (string) The url used to fetch a model file.
      :on-success -- (vector) A reframe event vector to dispatch on success.
      :on-failure -- (vector) A reframe event vector to dispatch on failure.

  Dispatched by:
    The re-frame event :upload/read-query-string-params."
  [params]
  (let [{:keys [data-url model-url on-success on-failure]} params
        items-to-fetch [{:read-type :url
                         :kind :dataset
                         :id :data
                         :target data-url
                         :details {:default-model :model}}
                        {:read-type :url
                         :kind :model
                         :id :model
                         :target model-url
                         :model-type (model-type :url model-url)
                         :dataset :data}]
        ;; Used to put all file reads.
        file-reads (async/chan)]
    (doseq [item items-to-fetch]
      (put-read item file-reads))
    (let [closing-chan (async/take (count items-to-fetch) file-reads)]
      (take-reads closing-chan on-success on-failure))))
(rf/reg-fx :upload/read-individual-urls-effect
           read-individual-urls-effect)
