(ns inferenceql.spreadsheets.panels.upload.effects
  (:require [re-frame.core :as rf]
            [cljs.core.async :as async :refer [go put! <!]]
            [goog.string :as gstring]
            [clojure.string :as str]
            [ajax.core]
            [ajax.edn]
            [lambdaisland.uri :as uri]
            [clojure.edn :as edn]))

(defn handle-reads
  "Reads files off of a channel and places them in a config map.

  This reads `num-reads` times off of the `file-reads` channel. It takes each read and places it in
  the appropriate section of a config map. When all the reads have been processed, the
  `on-success` event vector is dispatched with the new config map.

  Args:
    `file-reads` -- (core.async channel)
    `num-reads` -- (int) number of files to read off of `file-reads`.
    `config` -- (map) a config map that will have file data inserted into it. This map comes from
      a config file specified as part of a demo, or a mock one is submitted for a basic dataset
      and model.
    `on-success` -- (vector) a reframe event vector for dispatching on success.
    `on-failure` -- (vector) a reframe event vector for dispatching on failure."
  [file-reads num-reads config on-success on-failure]
  (let [file-reads-batched (async/into [] (async/take num-reads file-reads))]
    (go
     (let [reads (<! file-reads-batched)]
       (if (every? :success reads)
         (let [config-with-data (reduce (fn [acc r]
                                          (let [{:keys [config-path data]} r]
                                            (assoc-in acc config-path data)))
                                        config
                                        reads)]
           (rf/dispatch (conj on-success config-with-data)))
         (let [failures (remove :success reads)
               failure-messages (->> (for [{:keys [filename config-path file-url]} failures]
                                       (str (gstring/format "Failed reading %s at the config path %s." filename config-path)
                                            (when file-url (gstring/format " (using url %s" file-url))))
                                     (str/join "\n"))]
           (rf/dispatch (conj on-failure failure-messages))))))))

(defn ^:effect read-files-effect
  "Reads files selected as part of form for adding a new dataset and model.

  This reads the specified files and places them in a mock config map which will passed to the
  :on-success event specified.

  Args:
    `params` -- A map of parameters with the following keys.
      :dataset-name -- (string) A name for the new dataset.
      :model-name -- (string) A name for the new model.
      :dataset-file -- (File) File object specifiying a dataset.
      :dataset-schema-file -- (File) File object specifiying a schema for the dataset.
      :model-file -- (File) File object specifiying a model.
      :on-success -- (vector) A reframe event vector to dispatch on success.
      :on-failure -- (vector) A reframe event vector to dispatch on failure.

  Dispatched by:
    The re-frame event :upload/read-files, which was dispatched when the form was originally
      submitted."
  [params]
  (let [{:keys [dataset-name model-name
                dataset-file dataset-schema-file model-file
                on-success on-failure]} params
        required-files [dataset-file dataset-schema-file model-file]]
    (if-not (every? #(instance? js/File %) required-files)
      (rf/dispatch (conj on-failure "Not all required files were specified."))
      (let [[dataset-name model-name] (map keyword [dataset-name model-name])
            mock-config {:datasets {dataset-name {:filename (.-name dataset-file)
                                                  :schema-filename (.-name dataset-schema-file)
                                                  :default-model model-name}}
                         :models {model-name {:filename (.-name model-file)
                                              :dataset dataset-name}}}
            mock-config-paths [[:datasets :data :data]
                               [:datasets :data :schema]
                               [:models :model :data]]
            file-reads (async/chan)]
        (doseq [[config-path file-obj] (map vector mock-config-paths required-files)]
          (let [put-map {:config-path config-path
                         :filename (.-name file-obj)
                         :file-type (.-type file-obj)
                         :success nil ; To be set.
                         :data nil} ; To be set.
                rdr (js/FileReader.)
                on-load (fn [_]
                          (this-as this
                            (let [contents (.-result this)]
                              (put! file-reads (merge put-map {:success true
                                                               :data contents})))))
                on-error (fn [error]
                           (this-as this
                             (put! file-reads (merge put-map {:success false
                                                              :data error}))))]
            (set! (.-onload rdr) on-load)
            (set! (.-onerror rdr) on-error)
            (.readAsText rdr file-obj)))

        (handle-reads file-reads (count required-files) mock-config on-success on-failure)))))
(rf/reg-fx :upload/read-files-effect
           read-files-effect)

(defn end-with-slash
  "Adds a final forward slash at the end of `url` if it is not already present."
  [url]
  (if (not= "/" (last (seq url))) ;; Last character is not a slash
    (str url "/")
    url))

(defn ^:effect read-url-effect
  "Uses a magic-url to make https requests obtaining many files for loading a demo.

  This first makes a requests for a config.edn file. Based on the data in that config.edn file,
  many other http requests will be made for datasets, scheme files, models, and geodata files.

  Args:
    `params` -- A map of parameters with the following keys.
      :url -- (string) The url which will be used as the base url for making http requests for
        files.
      :use-creds -- (bool) Whether to used http basic authentication for the http requests.
      :on-success -- (vector) A reframe event vector to dispatch on success.
      :on-failure -- (vector) A reframe event vector to dispatch on failure.

  Dispatched by:
    The re-frame event :upload/read-url, which was dispatched when the form was originally
      submitted."
  [params]
  (let [{:keys [url use-creds on-success on-failure]} params
        cors-proxy-url "https://limitless-dusk-04385.herokuapp.com/"
        url (->> url
                 (end-with-slash)
                 (str cors-proxy-url))

        config-read (async/chan)
        config-edn-url (uri/join url "config.edn")]

    (ajax.core/ajax-request
      {:uri config-edn-url
       :method :get
       :with-credentials use-creds
       :handler #(put! config-read %)
       :response-format (ajax.core/text-response-format)})

    (go
     (let [[status data] (<! config-read)]
       (if (false? status)
         (let [failure-msg (str "Could not read config.edn at " config-edn-url)]
           (rf/dispatch (conj on-failure failure-msg)))

         (let [config (edn/read-string data)
               data-to-fetch (-> (for [category [:datasets :models :geodata]]
                                   (let [items-in-category (get config category)]
                                     (for [[item-key item] items-in-category]
                                       (case category
                                         :datasets [{:config-path [category item-key :data]
                                                     :filename (:filename item)}
                                                    ;; Datasets have this extra path for the schema
                                                    ;; that we have to fetch.
                                                    {:config-path [category item-key :schema]
                                                     :filename (:schema-filename item)}]
                                         {:config-path [category item-key :data]
                                          :filename (:filename item)}))))
                                 (flatten))

               file-reads (async/chan)]

           (doseq [{:keys [config-path filename]} data-to-fetch]
             (let [file-url (uri/join url filename)]
               (ajax.core/ajax-request
                {:uri file-url
                 :method :get
                 :with-credentials use-creds
                 :handler (fn [[status data]]
                            (put! file-reads {:success status
                                              :config-path config-path
                                              :filename filename
                                              :file-url file-url
                                              :data data})) ;; May be file data or failure data.
                 :response-format (ajax.core/text-response-format)})))

           (handle-reads file-reads (count data-to-fetch) config on-success on-failure)))))))
(rf/reg-fx :upload/read-url-effect
           read-url-effect)
