(ns inferenceql.spreadsheets.panels.viz.subs-circle
  (:require [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.spreadsheets.panels.viz.circle :as circle]))

(rf/reg-sub :circle/threshold
            (fn [db]
              (get-in db [:viz-panel :circle :threshold] 0)))

(rf/reg-sub :circle/col-names
            :<- [:table/table-rows]
            (fn [rows]
              ;; XXX: Likely to be changed.
              (seq (set (map (comp keyword :column-1) rows)))))

(rf/reg-sub :circle/tree
            :<- [:circle/col-names]
            (fn [col-names]
              (circle/tree col-names)))


(rf/reg-sub :circle/dependencies
            :<- [:table/table-rows]
            :<- [:circle/tree]
            :<- [:circle/threshold]
            (fn [[rows tree threshold]]
              (let [tree (remove (comp #(= % -1) :id) tree) ;; Remove the root node.
                    col-ids (zipmap (map :name tree) (map :id tree))

                    mi-vals (->> rows
                                 (map (fn [row]
                                        (let [{:keys [column-1 column-2 mi]} row
                                              col-set (set (map keyword [column-1 column-2]))]
                                          {col-set mi})))
                                 (reduce merge)
                                 ;; Only allow entries between two different columns.
                                 (medley/filter-keys #(= 2 (count %)))
                                 ;; Only allow entries above our edge threshold.
                                 (medley/filter-vals #(>= % threshold)))

                    dependencies (for [[col-set mi] mi-vals]
                                   (let [[col-1 col-2] (seq col-set)
                                         col-2 (or col-2 col-1)]
                                     {:source-id (get col-ids col-1)
                                      :target-id (get col-ids col-2)
                                      :source-name col-1
                                      :target-name col-2
                                      :edge-val mi}))]
                (circle/dependencies dependencies))))

(rf/reg-sub :circle/spec
            :<- [:circle/tree]
            :<- [:circle/dependencies]
            :<- [:table/mi]
            (fn [[tree dependencies mi]]
              (when mi
                (let [[extent rotate] (if (<= (count tree) 10)
                                          [180 270] ;; Half circle viz.
                                          [360 0]) ;; Full circle viz.
                      spec (clj->js
                             (circle/spec tree dependencies extent rotate))]
                  spec))))
