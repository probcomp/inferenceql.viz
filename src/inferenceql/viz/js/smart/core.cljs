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
  [query-fn col cols row schema]
  (let [sample-count 70
        rem-cols (remove #{col} cols)
        b-str (binding-string row rem-cols schema)
        generate-query (format "SELECT * FROM (GENERATE %s UNDER model CONDITIONED BY %s) LIMIT %s;"
                               (name col) b-str sample-count)]
    (->> (query-fn generate-query)
      (map #(get (->clj %) col)))))

(defn simulate-row
  [query-fn cols ts-cols row schema]
  (into {}
        (for [c ts-cols]
          [c (simulate-cell query-fn c cols row schema)])))

;------------------------------------------

(defn quote-val
  [val]
  (format "\"%s\"" val))

(defn query-uncond [col row schema]
  (let [col-type (get schema col)
        val (->> (get row col)
                 str)
        val (if (= col-type :numerical) val (quote-val val))]
    (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model AS p) FROM data LIMIT 1;" (name col) val)))

(defn query-cond [col col-order row schema]
  (let [col-type (get schema col)
        val (->> (get row col)
                 str)
        val (if (= col-type :numerical) val (quote-val val))

        other-cols (remove #{col} col-order)
        b-str (binding-string row other-cols schema)]
    (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model CONDITIONED BY %s AS p) FROM data LIMIT 1;"
            (name col) val b-str)))

(defn anomaly-query-results
  [query-fn thresh col col-order row schema]
  (let [q1 (query-uncond col row schema)
        q2 (query-cond col col-order row schema)
        q1-val (-> (query-fn q1) first (.-p))
        q2-val (-> (query-fn q2) first (.-p))
        anomaly (and (< q2-val q1-val)
                     (< q2-val thresh))]
    {:q-uncond q1 :q-uncond-v q1-val
     :q-cond q2 :q-cond-v q2-val
     :anomaly-status anomaly}))

(defn query-display [aqr]
  (let [{:keys [q-uncond q-uncond-v q-cond q-cond-v]} aqr]
    (string/join "\n\n" [q-uncond (str q-uncond-v) q-cond (str q-cond-v)])))

(defn row-anomaly-statuses
  [query-fn thresh cols ts-cols row schema]
  (let [aqrs (for [c ts-cols]
               (anomaly-query-results query-fn thresh c cols row schema))
        anomaly-statuses (map :anomaly-status aqrs)]
    (zipmap ts-cols anomaly-statuses)))

;------------------------------------------

(defn ^:export app
  "Javascript interface for displaying the SMART app"
  [query-fn table-data schema num-rows thresh step-time options invalidation]
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

        anim-step (fn anim-step []
                    ;; There should always be another check in the list.
                    (when-let [chk (first @checks)]
                      (let [other-cols (remove #{(:column chk)} cols)
                            col-name (name (:column chk))]
                        ;; Update the plot.
                        (if (not= @cur-col (:column chk))
                          (let [all-bindings (string/join " AND " (map name other-cols))
                                q-conditional-all (format "SELECT rowid, %s, (PROBABILITY DENSITY OF %s UNDER model CONDITIONED BY %s AS p) FROM data LIMIT %s;"
                                                          col-name col-name all-bindings num-rows)]
                            (reset! plot-data {:col-names [col-name "p"]
                                               :rows (query-fn q-conditional-all)})
                            (reset! cur-col (:column chk))))

                        ;; Update the table.
                        (let [row (nth table-data (:row chk))
                              aqr (anomaly-query-results query-fn thresh (:column chk) cols row schema)
                              anomaly-status (:anomaly-status aqr)

                              update-sim-plot-data
                              (fn []
                                (let [ts-col-sets (->> (take-last 15 cols)
                                                       (partition 5)
                                                       (map set))
                                      ts-col-set (some (fn [s] (when (s (:column chk)) s))
                                                       ts-col-sets)]
                                  (if ts-col-set
                                    (let [sims (simulate-row query-fn cols ts-col-set row schema)
                                          row-anom (row-anomaly-statuses query-fn thresh cols ts-col-set row schema)]

                                      ;; Update sim plot.
                                      (reset! sim-plot-data {:sims sims :row row :row-anom row-anom})

                                      (let [new-cells (fn [row col prop]
                                                        (let [cell-props #js {}
                                                              color
                                                              (cond (and (= row (:row chk))
                                                                         (= prop (name (:column chk))))
                                                                    "red-highlight"

                                                                    (and (= row (:row chk))
                                                                         (get row-anom (keyword prop)))
                                                                    "light-red-highlight"

                                                                    (and (= row (:row chk))
                                                                         (ts-col-set (keyword prop)))
                                                                    "grey-highlight"

                                                                    :else nil)]
                                                          (when color
                                                            (set! (.-className cell-props) color))
                                                          cell-props))]
                                        (swap! options assoc :cells new-cells))))))]

                          ;; Update query.
                          (reset! query (query-display aqr))

                          ;; Clear the sim-plot.
                          (reset! sim-plot-data nil)

                          ;; Update highlighted point in plot.
                          (reset! cur-row (:row chk))
                          (reset! cur-cell-status anomaly-status)

                          ;; Switch to next check.
                          (swap! checks rest)

                          ;; Update table highlighting.
                          ;; TODO: Move this into a specialized table component.
                          (let [new-cells (fn [row col prop]
                                            (let [cell-props #js {}]
                                              (when (and (= row (:row chk))
                                                         (= prop (name (:column chk))))
                                                (let [color (if anomaly-status
                                                              "red-highlight"
                                                              "blue-highlight")]
                                                  (set! (.-className cell-props) color)))
                                              cell-props))]
                            (swap! options assoc :cells new-cells))

                          (if anomaly-status
                            ;; Update the simulation plot.
                            (js/setTimeout update-sim-plot-data 0)
                            ;; Setup next iteration.
                            (js/setTimeout anim-step step-time))))))

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
                                        :children [;; TODO: Move this into the anomaly-plot component.
                                                   (when (every? some? [@plot-data @cur-row @cur-cell-status])
                                                     (let [plot-rows (some->> (:rows @plot-data) ->clj vec)
                                                           plot-rows (mapv #(assoc % :anomaly "undefined") plot-rows)
                                                           plot-rows (some-> plot-rows (assoc-in [@cur-row :anomaly] @cur-cell-status))]
                                                       [anomaly-plot plot-rows schema [(:col-names @plot-data)]]))
                                                   [gap :size "20px"]
                                                   (when @cur-cell-status
                                                     [sim-plot @sim-plot-data anim-step])]]
                                       [gap :size "20px"]
                                       [:div {:class "observablehq--inspect"
                                              :style {:white-space "pre-wrap"}}
                                        @query]]]]])]

    ;; Start animation.
    (js/setTimeout anim-step step-time)
    (rdom/render [comp options] node)
    node))
