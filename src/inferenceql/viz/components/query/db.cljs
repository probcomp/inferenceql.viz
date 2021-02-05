(ns inferenceql.viz.components.query.db
  "Contains the initial state of the db corresponding to the query component
  along with related specs."
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.components.store.db :as store]))

(def default-db
  ;; NOTE: We currently assume the dataset and model referenced
  ;; in the current query is always :data and :model, respectively.
  {:query-component {:dataset-name :data
                     :model-name :model}})


(s/def ::query-component (s/keys :req-un [::dataset-name
                                          ::model-name]

                                 :opt-un [::virtual
                                          ::column-details
                                          ::query-displayed]))

;; The dataset referenced in the last query executed.
(s/def ::dataset-name keyword?)
;; The model referenced in the last query executed.
(s/def ::model-name keyword?)

;; Whether the last query executed produced virtual data.
(s/def ::virtual boolean?)

;;; Specs related to ::column-details

(s/def ::detail-type #{:rename :new-column-schema})
(s/def ::old-name ::store/column-name)
(s/def ::new-name ::store/column-name)
(s/def ::name ::store/column-name)

(defmulti column-detail-type :detail-type)

;; Columns that will be renamed from their original names in the resultset of the
;; last query executed.
(defmethod column-detail-type :rename [_]
  (s/keys :req-un [::detail-type ::old-name ::new-name]))

;; Stat types for new columns that were produced as part of the last query executed.
(defmethod column-detail-type :new-column-schema [_]
  (s/keys :req-un [::detail-type ::name ::store/stat-type]))

(s/def ::column-details (s/coll-of ::column-detail))
(s/def ::column-detail (s/multi-spec column-detail-type ::detail-type))

;; Edits to the table in the UI use the WITH expression in this query.
(s/def ::query-displayed string?)

;;; Accessor functions for indexing into parts of the query-panel's db.

(defn query-displayed [db]
  (get-in db [:query-panel :query-displayed]))
