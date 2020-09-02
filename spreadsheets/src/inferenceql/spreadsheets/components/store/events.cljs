(ns inferenceql.spreadsheets.components.store.events
  "Contains events related to the store panel."
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.inference.gpm :as gpm]
            [clojure.edn :as edn]
            [goog.labs.format.csv :as csv]))

(rf/reg-event-db
 :store/dataset
 event-interceptors
 (fn [db [_ dataset-name schema default-model dataset-data]]
   (let [csv-data (csv/parse dataset-data)
         rows (csv-utils/csv-data->clean-maps schema csv-data {:keywordize-cols true})
         entry {:rows rows
                :schema schema
                :default-model default-model}]
     (-> db
         (assoc-in [:store-component :datasets dataset-name] entry)))))

(rf/reg-event-db
 :store/model
 event-interceptors
 (fn [db [_ model-name model-data]]
   (let [model (gpm/Multimixture (edn/read-string model-data))]
     (-> db
         (assoc-in [:store-component :models model-name] model)))))