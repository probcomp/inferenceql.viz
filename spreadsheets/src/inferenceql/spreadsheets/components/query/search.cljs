(ns inferenceql.spreadsheets.components.query.search
  (:require [medley.core :as medley]
            [clojure.string :as str]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm.multimixture.search :as search]
            [inferenceql.spreadsheets.panels.table.db :as table-db]))

(def search-by-label-query
  "The query used to trigger search-by-label."
  "SELECT (PROBABILITY OF label=\"True\" GIVEN * UNDER model AS prob-label-true), *")
(def ^:private search-column "new-property")
(def ^:private n-models 10)
(def ^:private beta-params {:alpha 0.001, :beta 0.001})

(defn label-set?
  "Converts a label string into a boolean indicating whether the label sets the row as an example."
  [val]
  (when val
    (let [clean-label (str/lower-case (str/trim val))]
      (cond
        ;; Positive example.
        (#{"true" "1" "t"} clean-label)
        true

        ;; Negative example.
        (#{"false" "0" "f"} clean-label)
        false

        ;; Not set.
        :else
        nil))))

(defn make-example
  "Returns an example row built from `row` to be used in the search-by-label search implementation."
  [row]
  (let [label-state (:inferenceql.viz.row/label__ row)]
    (merge row {search-column label-state})))

(defn clean
  [row]
  (as-> row $
        (dissoc $
                :inferenceql.viz.row/id__
                :inferenceql.viz.row/user-added-row__
                :inferenceql.viz.row/label__
                :inferenceql.viz.row/row-number__
                :inferenceql.viz.row/id__)
        (medley/remove-vals nil? $)))

(defn perform-search-by-label
  "Performs a search by label query over `rows`.
  Adds a new column, prob-label-true, to result rows to show resulting scores.
  Returns a re-frame event map to be returned by a re-frame fx-event."
  [db]
  (let [rows-by-id (table-db/physical-row-by-id-with-changes db)
        row-order (table-db/physical-row-order-all db)
        rows (->> row-order
                  (map rows-by-id)
                  (map #(medley/remove-vals nil? %)))
        ;;headers (conj (table-db/dataset-headers db) :inferenceql.viz.row/label__)
        headers (table-db/dataset-headers db)

        ;;-------------------------------------

        rows (map #(update % :inferenceql.viz.row/label__ label-set?) rows)

        labeled-rows (->> rows
                          (filter (comp some? :inferenceql.viz.row/label__)))
        labeled-ids (map :inferenceql.viz.row/id__ labeled-rows)
        labeled-scores (map (comp {true 1 false 0} :inferenceql.viz.row/label__) labeled-rows)

        unlabeled-rows (->> rows
                            (filter (comp nil? :inferenceql.viz.row/label__)))
        unlabeled-ids (map :inferenceql.viz.row/id__ unlabeled-rows)
        unlabeled-scores (search/search model/spec search-column
                                        (map #(clean (make-example %)) labeled-rows)
                                        (map clean unlabeled-rows)
                                        n-models beta-params)

        _ (.log js/console :labeled-rows-------- (map #(clean (make-example %)) labeled-rows))
        _ (.log js/console :unlabeled-rows-------- (map clean unlabeled-rows))

        ids (concat labeled-ids unlabeled-ids)
        scores (for [score (concat labeled-scores unlabeled-scores)]
                 {:prob-label-true score})

        scores-map (zipmap ids scores)
        display-headers (concat [:prob-label-true] headers)
        display-rows-map (merge-with merge rows-by-id scores-map)
        display-rows (map display-rows-map row-order)]
    {:dispatch [:table/set display-rows display-headers {:virtual false}]}))

