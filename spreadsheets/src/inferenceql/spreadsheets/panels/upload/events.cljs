(ns inferenceql.spreadsheets.panels.upload.events
   "Contains events related to the upload panel."
  (:require [clojure.edn :as edn]
            [re-frame.core :as rf]
            [goog.labs.format.csv :as csv]
            [inferenceql.inference.gpm :as gpm]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.auto-modeling.bayesdb-import :as bayesdb-import]
            [clojure.string :as str]
            [medley.core :as medley]))

(defn ^:event-fx read-files
  "Starts the processing of a form for selecting a new dataset and model via local files.

  Args:
    `form-data` - A map of data entered in the form. This includes File objects that were selected
      via browser file selectors.

  Triggered when:
    The user submits the form.

  Effects returned:
    :upload/read-files-effect -- Continues the processing of the submitted files.
    :dispatch [:upload/set-display] -- Clears the display of the form.
    :dispatch [:table/clear] -- Clears all data displayed in the table."
  [{:keys [_db]} [_ form-data]]
  (let [{:keys [dataset-name model-name
                dataset-file dataset-schema-file model-file]} form-data]
    {:upload/read-files-effect {:dataset-name dataset-name
                                :model-name model-name
                                :dataset-file dataset-file
                                :dataset-schema-file dataset-schema-file
                                :model-file model-file
                                :on-success [:upload/process-config]
                                :on-failure [:upload/read-failed]}}))
(rf/reg-event-fx :upload/read-files
                 event-interceptors
                 read-files)

(defn ^:event-fx read-url
  "Starts the processing of a form for submitting a magic-url which will load a new demo

  A loading a demo means loading all datasets, models, and geodata related to a particular demo.

  Args:
    `form-data` - A map of data entered in the form.

  Triggered when:
    The user submits the form.

  Effects returned:
    :upload/read-url-effect -- Continues the processing of the submitted url.
    :dispatch [:upload/set-display] -- Clears the display of the form.
    :dispatch [:table/clear] -- Clears all data displayed in the table."
  [{:keys [_db]} [_ form-data]]
  (let [{:keys [url use-creds]} form-data]
    {:upload/read-url-effect {:url url
                              :use-creds use-creds
                              :on-success [:upload/process-config]
                              :on-failure [:upload/read-failed]}}))
(rf/reg-event-fx :upload/read-url
                 event-interceptors
                 read-url)

(defn ^:event-fx process-config
  "Processes a config map that contains datasets, models, and geodata and stores them.

  The sections in a config map contain fairly raw data. They need to be transformed before saving
  them in the app-db.

  Args:
    `config` - A map containing sections of new datasets, models, and geodata.
    {:datasets {:dataset-name {...}}
     :models {:model-name {...}}
     :geodata {:geodata-name {...}}}

  Triggered when:
    Effects which process files or urls for new data and models succeed.

  Effects returned:
    :dispatch [:store/datasets] -- Stores datasets read from config.
    :dispatch [:store/models] -- Stores models read from config.
    :dispatch [:store/geodata] -- Stores geodata read from config.
    :dispatch [:upload/read-failed] -- Outputs an error message about the failure."
  [{:keys [_db]} [_ config]]
  (let [datasets (medley/map-vals (fn [dataset]
                                    (let [schema (edn/read-string (:schema dataset))
                                          csv-data (csv/parse (:data dataset))
                                          rows (csv-utils/csv-data->clean-maps
                                                schema csv-data {:keywordize-cols true})]
                                      (merge dataset {:schema schema
                                                      :csv-data csv-data
                                                      :rows rows})))
                                  (:datasets config))

        models (medley/map-vals (fn [model]
                                  (let [model-extension (last (str/split (:filename model) #"\."))
                                        csv-data (get-in datasets [(:dataset model) :rows])
                                        model-obj (case model-extension
                                                    "edn"
                                                    (gpm/Multimixture (edn/read-string (:data model)))
                                                    "json"
                                                    (first
                                                     (bayesdb-import/xcat-gpms
                                                      (js->clj (.parse js/JSON (:data model)))
                                                      csv-data)))]
                                    (assoc model :model-obj model-obj)))
                                (:models config))

        geodata (medley/map-vals (fn [geodatum] (update geodatum :data #(.parse js/JSON %)))
                                 (:geodata config))

        ;; Selecting the necessary keys before storing.
        datasets-to-store (medley/map-vals
                           #(select-keys % [:rows :schema :default-model
                                            :geodata-name :geo-id-col])
                           datasets)
        ;; Selecting just the actual gpm.
        models-to-store (medley/map-vals :model-obj models)]
    ;; TODO: catch conversion errors.
    (if true
      {:dispatch-n [[:store/datasets datasets-to-store]
                    [:store/models models-to-store]
                    (when geodata
                      [:store/geodata geodata])]}
      {:dispatch [:upload/read-failed "TODO: write error message for conversion."]})))
(rf/reg-event-fx :upload/process-config
                 event-interceptors
                 process-config)

(defn ^:event-fx read-failed
  ""
  [{:keys [_db]} [_ error]]
  {:js/console-error error})
(rf/reg-event-fx :upload/read-failed
                 event-interceptors
                 read-failed)
