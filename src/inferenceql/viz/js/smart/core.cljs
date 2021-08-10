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
            [inferenceql.viz.js.smart.views :refer [anomaly-plot sim-plot]]
            [inferenceql.viz.js.util :refer [clj-schema]]
            [medley.core :as medley]))

(defn binding-string
  [row cols schema]
  (let [cells (for [c cols]
                {:col c :val (get row c)})
        cells (filter #(some? (:val %)) cells)
        cells (map (fn [{:keys [col val]}]
                     (if (= (get schema col) :numerical)
                       (format "%s=%s" (name col) val)
                       (format "%s=\"%s\"" (name col) val)))
                   cells)]
    (string/join " AND " cells)))

(def col-group [:ld1 :ld2 :ld3 :ld4 :ld5])

(defn simulate-cell
  [query-fn col col-order row schema]
  (let [sample-count 10
        rem-cols (remove #{col} col-order)
        b-str (binding-string row rem-cols schema)
        generate-query (format "SELECT * FROM (GENERATE %s UNDER model CONDITIONED BY %s) LIMIT %s;"
                               (name col) b-str sample-count)]
    (->> (query-fn generate-query)
      (map #(get (->clj %) col)))))

(defn simulate-row
  [query-fn col-order row schema]
  (into {}
    (for [c col-group]
      [c (simulate-cell query-fn c col-order row schema)])))

;------------------------------------------

(defn quote-val
  [val]
  (format "\"%s\"" val))

(defn uncond-query [col row schema]
  (let [col-type (get schema col)
        val (->> (get row col)
                 str)
        val (if (= col-type :numerical) val (quote-val val))]
    (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model AS p) FROM data LIMIT 1;" (name col) val)))

(defn cond-query [col col-order row schema]
  (let [col-type (get schema col)
        val (->> (get row col)
                 str)
        val (if (= col-type :numerical) val (quote-val val))

        other-cols (remove #{col} col-order)
        b-str (binding-string row other-cols schema)]
    (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model CONDITIONED BY %s AS p) FROM data LIMIT 1;"
            (name col) val b-str)))

;------------------------------------------

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
        sim-plot-data (r/atom nil)

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
                                       [sim-plot @sim-plot-data]
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
                              q1 (uncond-query (:column chk) row schema)
                              q2 (cond-query (:column chk) cols row schema)

                              ;; TODO: refactor this into a function to run anomaly query.
                              ;; Run it for all the cell in the row.
                              ;; Return: query-strings, values, and anomaly-status.

                              q1-val (-> (query-fn q1) first (.-p))
                              q2-val (-> (query-fn q2) first (.-p))
                              anomaly (and (< q2-val q1-val)
                                           (< q2-val thresh))
                              sims (simulate-row query-fn cols row schema)]
                          ;; Update query.
                          (reset! query (string/join "\n\n" [q1 (str q1-val) q2 (str q2-val)]))

                          ;; Update highlighted point in plot.
                          (reset! cur-row (:row chk))
                          (reset! cur-cell-status anomaly)

                          ;; Update sim plot.
                          (reset! sim-plot-data {:sims sims :row row} #_(if anomaly "true" "false"))

                          ;; Switch to next check.
                          (swap! checks rest)

                          ;; Update table highlighting.
                          ;; TODO: Move this into a specialized table component.
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
