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

