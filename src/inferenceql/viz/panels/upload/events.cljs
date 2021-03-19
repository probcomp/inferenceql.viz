(ns inferenceql.viz.panels.upload.events
   "Contains events related to the upload panel."
  (:require [clojure.edn :as edn]
            [re-frame.core :as rf]
            [goog.labs.format.csv :as csv]
            [medley.core :refer [index-by map-vals find-first]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.csv :as csv-utils]
            [inferenceql.auto-modeling.bayesdb-import :as bayesdb-import]
            [inferenceql.inference.gpm :as gpm]))

(defn ^:event-fx read-form
  "Starts the processing of a form for uploading a new dataset and model.

  Args:
    `form-data` - A map of data entered in the form.

  Triggered when:
    The user clicks the submit button in the upload panel UI.

  Effects returned:
    :upload/read-form-effect -- Continues the processing of the submitted files."
  [{:keys [_db]} [_ form-data]]
  (let [{:keys [dataset schema model]} form-data]
    (if (every? some? [dataset schema model])
      {:fx [[:upload/read-form-effect {:dataset dataset
                                       :schema schema
                                       :model model
                                       :on-success [:upload/process-reads]
                                       :on-failure [:upload/read-failed]}]]}
      {:fx [[:dispatch [:upload/read-failed
                        (str "Error processing form: Not all files were specified.\n"
                             "Please specify a dataset, schema, and model.")]]]})))
(rf/reg-event-fx :upload/read-form
                 event-interceptors
                 read-form)

(defn ^:event-fx read-query-string-params
  "Starts the processing of query string parameters that specify urls to load.

  Args:
    `query-params` - A map of query string parameters that specify urls to load.
       Should look like {:data some-url :schema some-url :model some-url} when the user
       is specifying urls for individual files.

       Should look like {:config some-url} when the user is specifying a config url.

       May also be an empty map when the app is started without any query parameters.

  Triggered when:
    The entire app starts up.

  Effects returned:
    :upload/read-config-url-effect -- Continues the processing of the config url.
    :upload/read-individual-urls-effect -- Continues the processing of the data, schema,
      and model urls."
  [_ [_ query-params]]
  (let [{:keys [schema data model config]} query-params]
    (cond
      (some? config) ; A url for a config.edn was specified. Prefer this.
      {:fx [[:upload/read-config-url-effect {:config-url config
                                             :on-success [:upload/process-reads]
                                             :on-failure [:upload/read-failed]}]]}

      (every? some? [schema data model])
      {:fx [[:upload/read-individual-urls-effect {:schema-url schema
                                                  :data-url data
                                                  :model-url model
                                                  :on-success [:upload/process-reads]
                                                  :on-failure [:upload/read-failed]}]]}
      (some some? [schema data model])
      {:fx [[:dispatch [:upload/read-failed
                        (str "Error processing query string parameters: \n"
                             "Not all required parameters were specified.\n"
                             "Please specify a url for the dataset, schema, "
                             "and model parameters.")]]]}

      :else
      {})))
(rf/reg-event-fx :upload/read-query-string-params
                 event-interceptors
                 read-query-string-params)

(defn ^:event-fx process-reads
  "Processes a collection of file-reads and stores them in the app-db.

  Args:
    `reads` - A collection of file-read maps for datasets, schemas, models, and geodata.

  Triggered when:
    Effects which process files or urls for new data, schemas, models, and geodata succeed.

  Effects returned:
    :dispatch [:store/datasets] -- Stores new datasets.
    :dispatch [:store/models] -- Stores new models.
    :dispatch [:store/geodata] -- Stores new geodata.
    :dispatch [:upload/read-failed] -- Outputs an error message about the failure."
  [{:keys [_db]} [_ reads]]
  (try
    (let [;; Process reads of datasets and schemas together.
          dataset-related? (fn [read] (contains? #{:dataset :schema} (:kind read)))
          dataset-read-pairs (->> reads
                                  (filter dataset-related?)
                                  (group-by :id))

          ;; Check that we actually have pairs.
          _ (assert (every? #(= (count %) 2) (vals dataset-read-pairs)))

          datasets (map-vals (fn [read-pair]
                               (let [schema-read (find-first #(= (:kind %) :schema) read-pair)
                                     dataset-read (find-first #(= (:kind %) :dataset) read-pair)

                                     schema (edn/read-string (:raw-data schema-read))
                                     csv-data (csv/parse (:raw-data dataset-read))
                                     rows (csv-utils/csv-data->clean-maps
                                           schema csv-data {:keywordize-cols true})]

                                 (merge (:details dataset-read)
                                        {:schema schema :rows rows})))
                             dataset-read-pairs)

          model-reads (->> reads
                           (filter #(= (:kind %) :model))
                           (index-by :id))

          models (map-vals (fn [model-read]
                             (case (:model-type model-read)
                               :bayes-db-export
                               (let [dataset-name (get model-read :dataset)
                                     dataset-rows (get-in datasets [dataset-name :rows])]
                                 (first ; Taking the first model.
                                  (bayesdb-import/xcat-gpms
                                   (js->clj (.parse js/JSON (:raw-data model-read)))
                                   dataset-rows)))
                               :edn
                               (edn/read-string {:readers gpm/readers}
                                                (:raw-data model-read))))
                           model-reads)

          geodata-reads (->> reads
                             (filter #(= (:kind %) :geodata))
                             (index-by :id))

          geodata (map-vals (fn [geodata-read]
                              (let [json-data (.parse js/JSON (:raw-data geodata-read))]
                                (merge (:details geodata-read)
                                       {:data json-data})))
                            geodata-reads)]
      ;; TODO: Check datasets, schemas, and geodata against spec before storing.
      {:fx [[:dispatch [:store/datasets datasets]]
            [:dispatch [:store/models models]]
            (when (seq geodata) [:dispatch [:store/geodata geodata]])]})
    (catch js/Error e
      {:fx [[:dispatch [:upload/read-failed (str "Error processing reads.\n" (.-stack e))]]]})))

(rf/reg-event-fx :upload/process-reads
                 event-interceptors
                 process-reads)

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
