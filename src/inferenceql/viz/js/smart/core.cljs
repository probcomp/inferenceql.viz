(ns inferenceql.viz.js.smart.core
  (:require [clojure.string :as string]
            [clojure.walk :refer [postwalk]]
            [goog.dom :as dom]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [re-com.core :refer [v-box h-box box gap]]
            [cljs-bean.core :refer [->clj]]
            [goog.string :refer [format]]
            [inferenceql.viz.js.components.table.views :refer [handsontable]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.js.smart.views :refer [anomaly-plot]]
            [inferenceql.viz.js.util :refer [clj-schema]]
            [medley.core :as medley]))

(defn ^:export app
  "Javascript interface for displaying the SMART app"
  [query-fn table-data schema num-rows thresh step-time options]
  (let [schema (clj-schema schema)
        table-data (vec (take num-rows (->clj table-data)))
        table-data-rounded (for [r table-data]
                             (let [round (fn [col val]
                                           (if (= (get schema col) :numerical)
                                             (.toFixed val 2)
                                             val))]
                               (medley/map-kv-vals round r)))

        options (->clj options)
        cols (map keyword (:cols options))

        query (r/atom "SELECT * FROM data;")
        options (r/atom (assoc options :cells (fn [row col prop] #js {})))

        checks (for [c cols i (range (count table-data))] {:column c :row i})
        checks (cycle (filter #(some? (get-in table-data [(:row %) (:column %)])) checks))
        checks (r/atom checks)
        cur-col (r/atom nil)
        cur-row (r/atom nil)
        cur-cell-status (r/atom false)
        plot-data (r/atom nil)

        node (dom/createElement "div")
        comp (fn [options]
               [v-box
                :style {:min-height "1100px"
                        :padding "20px"
                        :border-width "3px"
                        :border-style "solid"
                        :border-radius "7px"
                        :border-color "grey"}
                :children [[v-box
                            :class "cell-by-cell-app"
                            :children [[handsontable table-data-rounded @options]
                                       [gap :size "20px"]
                                       [h-box
                                        :children [[gap :size "25px"]
                                                   ;; TODO: Move this into the anomaly-plot component.
                                                   (when (every? some? [@plot-data @cur-row @cur-cell-status])
                                                     (let [plot-rows (some->> (:rows @plot-data) ->clj vec)
                                                           plot-rows (mapv #(assoc % :anomaly "undefined") plot-rows)
                                                           plot-rows (some-> plot-rows (assoc-in [@cur-row :anomaly] @cur-cell-status))]
                                                       [anomaly-plot plot-rows schema [(:col-names @plot-data)]]))]]
                                       [gap :size "20px"]
                                       [:div {:class "observablehq--inspect"
                                              :style {:white-space "pre-wrap"}}
                                        @query]]]]])

        anim-step (fn anim-step []
                    ;; When there are checks left to be made.
                    (when-let [chk (first @checks)]
                      (let [other-cols (remove #{(:column chk)} cols)
                            col-name (name (:column chk))]
                        ;; Update the plot.
                        (if (not= @cur-col (:column chk))
                          (let [all-bindings (string/join " AND " (map name other-cols))
                                all-columns (string/join ", " (map name cols))
                                q-conditional-all (format "SELECT rowid, %s, (PROBABILITY DENSITY OF %s UNDER model CONDITIONED BY %s AS p) FROM data LIMIT %s;"
                                                          all-columns col-name all-bindings num-rows)]
                            (reset! plot-data {:col-names [col-name "p"]
                                               :rows (query-fn q-conditional-all)})
                            (reset! cur-col (:column chk))))

                        ;; Update the table.
                        (let [row (nth table-data (:row chk))

                              quote-val (fn [val] (format "\"%s\"" val))

                              col-type (get schema (:column chk))
                              val (str (get row (:column chk)))
                              val (if (= col-type :numerical) val (quote-val val))

                              q1 (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model AS p) FROM data LIMIT 1;" col-name val)

                              row-rest (dissoc row (:column chk))
                              cells (for [c other-cols]
                                      {:col c :val (get row-rest c)})
                              cells (filter #(some? (:val %)) cells)
                              cells (map (fn [{:keys [col val]}]
                                           (if (= (get schema col) :numerical)
                                             (format "%s=%s" (name col) val)
                                             (format "%s=\"%s\"" (name col) val)))
                                         cells)
                              bind-str (string/join " AND " cells)
                              q2 (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model CONDITIONED BY %s AS p) FROM data LIMIT 1;"
                                         col-name val bind-str)

                              q1-val (-> (query-fn q1) first (.-p))
                              q2-val (-> (query-fn q2) first (.-p))
                              anomaly (and (< q2-val q1-val)
                                           (< q2-val thresh))]
                          ;; Update query.
                          (reset! query (string/join "\n\n" [q1 (str q1-val) q2 (str q2-val)]))

                          ;; Update highlighted point in plot.
                          (reset! cur-row (:row chk))
                          (reset! cur-cell-status anomaly)

                          ;; Switch to next check.
                          (swap! checks rest)

                          ;; Update table highlighting.
                          (let [new-cells (fn [row col prop]
                                            (let [cell-props #js {}]
                                              (when (and (= row (:row chk))
                                                         (= prop (name (:column chk))))
                                                (let [color (if anomaly
                                                              "red-highlight"
                                                              "blue-highlight")]
                                                  (set! (.-className cell-props) color)))
                                              cell-props))]
                            (swap! options assoc :cells new-cells))
                          ;; Setup next iteration.
                          (js/setTimeout anim-step step-time)))))]

    ;; Start animation.
    (js/setTimeout anim-step step-time)
    (rdom/render [comp options] node)
    node))
