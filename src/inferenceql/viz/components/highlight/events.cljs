(ns inferenceql.viz.components.highlight.events
  (:require [re-frame.core :as rf]
            [inferenceql.viz.db :as db]
            [inferenceql.viz.panels.table.db :as table-db]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.score :as score]))

;; TODO: Update this code no longer depend on a static multimix spec definition.
;; `spec` here is just a dummy placeholder for a multimix spec.
;; Instead, this code can grab the current model (XCat or other) from the app-db.
(def spec nil)

(rf/reg-event-db
 :highlight/compute-row-likelihoods
 event-interceptors
 (fn [db [_]]
   (let [table-rows (table-db/table-rows db)
         likelihoods (score/row-likelihoods spec table-rows)]
     (assoc-in db [:highlight-component :row-likelihoods] likelihoods))))

(rf/reg-event-db
 :highlight/compute-missing-cells
 event-interceptors
 (fn [db [_]]
   (let [table-rows (table-db/table-rows db)
         headers (table-db/table-headers db)
         missing-cells (score/impute-missing-cells spec headers table-rows)]
     (assoc-in db [:highlight-component :missing-cells] missing-cells))))
