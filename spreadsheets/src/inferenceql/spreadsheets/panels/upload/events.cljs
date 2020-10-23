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
    :upload/read-files-effect -- Continues the processing of the submitted files."
  [{:keys [_db]} [_ form-data]]
  (let [{:keys [dataset-name model-name
                dataset-file dataset-schema-file model-file]} form-data]
    {:fx [[:upload/read-files-effect {:dataset-name dataset-name
                                      :model-name model-name
                                      :dataset-file dataset-file
                                      :dataset-schema-file dataset-schema-file
                                      :model-file model-file
                                      :on-success [:upload/process-config]
                                      :on-failure [:upload/read-failed]}]]}))
(rf/reg-event-fx :upload/read-files
                 event-interceptors
                 read-files)

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
                                                schema csv-data {:keywordize-cols true})
                                          dataset (merge dataset {:schema schema
                                                                  :rows rows})]
                                      ;; Selecting just the keys needed to store the dataset.
                                      (select-keys dataset [:rows :schema :default-model
                                                            :geodata-name :geo-id-col])))
                                  (:datasets config))

        models (medley/map-vals (fn [model]
                                  ;; Switching on the model extension.
                                  (case (last (str/split (:filename model) #"\."))
                                    "edn"
                                    (gpm/Multimixture (edn/read-string (:data model)))
                                    "json"
                                    (first ; Taking the first model.
                                     (bayesdb-import/xcat-gpms
                                      (js->clj (.parse js/JSON (:data model)))
                                      ;; This argument is dataset rows associated with this model.
                                      (get-in datasets [(:dataset model) :rows])))))
                                (:models config))

        geodata (medley/map-vals (fn [geodatum] (update geodatum :data #(.parse js/JSON %)))
                                 (:geodata config))]
    ;; TODO: catch conversion errors.
    (if true
      {:fx [[:dispatch [:store/datasets datasets]]
            [:dispatch [:store/models models]
             (when geodata [:dispatch [:store/geodata geodata]])]]}
      {:fx [[:dispatch [:upload/read-failed "TODO: write error message for conversion."]]]})))
(rf/reg-event-fx :upload/process-config
                 event-interceptors
                 process-config)

(defn ^:event-fx read-failed
  "Emits an error message to the browser console.

  Args:
    `error` - (string) The error message to emit to the console.

  Triggered when:
    Effects which process files or urls for new data and models fail."
  [_ [_ error]]
  {:fx [[:js/console-error error]]})
(rf/reg-event-fx :upload/read-failed
                 event-interceptors
                 read-failed)
