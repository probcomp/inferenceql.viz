(ns inferenceql.viz.js.components.smart.core
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
            [inferenceql.viz.js.components.smart.views :refer [anomaly-plot sim-plot]]
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
  (when (get row col)
    (let [col-type (get schema col)
          val (->> (get row col)
                   str)
          val (if (= col-type :numerical) val (quote-val val))]
      (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model AS p) FROM data LIMIT 1;" (name col) val))))

(defn query-cond [col col-order row schema]
  (when (get row col)
    (let [col-type (get schema col)
          val (->> (get row col)
                   str)
          val (if (= col-type :numerical) val (quote-val val))

          other-cols (remove #{col} col-order)
          b-str (binding-string row other-cols schema)]
      (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model CONDITIONED BY %s AS p) FROM data LIMIT 1;"
              (name col) val b-str))))

(defn anomaly-query-results
  [query-fn thresh alpha col col-order row schema]
  (let [q1 (query-uncond col row schema)
        q2 (query-cond col col-order row schema)
        q1-val (some-> q1 query-fn first (.-p))
        q2-val (some-> q2 query-fn first (.-p))

        anomaly (and q1-val q2-val
                     (< q2-val (* alpha q1-val))
                     (< q2-val thresh))]
    {:q-uncond q1 :q-uncond-v q1-val
     :q-cond q2 :q-cond-v q2-val
     :anomaly-status anomaly}))


(defn query-display [aqr]
  (let [{:keys [q-uncond q-uncond-v q-cond q-cond-v]} aqr]
    (string/join "\n\n" [q-uncond (str q-uncond-v) q-cond (str q-cond-v)])))

(defn row-anomaly-statuses
  [query-fn thresh alpha cols row schema]
  (let [aqrs (for [c cols]
               (anomaly-query-results query-fn thresh alpha c cols row schema))]
    (zipmap cols aqrs)))

;------------------------------------------

(defn anomaly-helper [cur-col-uncond-p cur-col-cond-p chk row cols schema thresh alpha]
  (let [q-uncond-v (-> cur-col-uncond-p
                       (nth (:row chk))
                       (:p))
        q-cond-v (-> cur-col-cond-p
                     (nth (:row chk))
                     (:p))
        anomaly (and (< q-cond-v (* alpha q-uncond-v))
                     (< q-cond-v thresh))
        user-text (query-display
                   {:q-uncond (query-uncond (:column chk) row schema)
                    :q-uncond-v q-uncond-v
                    :q-cond (query-cond (:column chk) cols row schema)
                    :q-cond-v q-cond-v})]
    {:anomaly anomaly
     :user-text user-text}))

;------------------------------------------

(defn app
  "Javascript interface for displaying the SMART app"
  [query-fn table-data schema num-rows thresh alpha step-time invalidation options]
  (let [schema (clj-schema schema)
        table-data (vec (take num-rows (->clj table-data)))
        table-data-rounded (for [r table-data]
                             (let [round (fn [col val]
                                           (if (and (= (get schema col) :numerical)
                                                    (number? val))
                                             (.toFixed val 2)
                                             val))]
                               (medley/map-kv-vals round r)))

        options (->clj options)
        cols (map keyword (:cols options))
        query-user-text (r/atom "SELECT * FROM data;")

        checks (for [c cols i (range (count table-data))] {:column c :row i})
        checks (filter #(some? (get-in table-data [(:row %) (:column %)])) checks)
        checks (r/atom checks)
        cur-col (r/atom nil)
        cur-row (r/atom nil)
        cur-cell-anom (r/atom false)
        cur-col-cond-p (r/atom nil)
        cur-col-uncond-p (r/atom nil)
        sim-plot-data (r/atom nil)
        sim-plot-cache (r/atom {})
        timer (r/atom nil)
        row-to-anomaly (atom {})

        selected-cell-anomalous (r/atom nil)
        selected-cell (r/atom nil)

        cols-with-index (vec (:cols options))
        update-sim-plot (fn [row-num col-num]
                          (let [col (nth (map keyword cols-with-index) col-num)
                                anomalous (get @row-to-anomaly {:row row-num :col (name col)})]
                            (reset! selected-cell-anomalous anomalous)
                            (reset! selected-cell {:row row-num :col (name col)})
                            ;; Clear the previous sim-plot.
                            (reset! sim-plot-data nil)

                            (let [work #(when anomalous
                                          (let [col (nth (map keyword cols-with-index) col-num)
                                                row (nth table-data row-num)]

                                            (let [cache-index [row-num]
                                                  cache-hit (get @sim-plot-cache cache-index)
                                                  new-data (or cache-hit
                                                               (row-anomaly-statuses query-fn thresh alpha cols row schema))]

                                              ;; Update the cache if needed.
                                              (when-not cache-hit
                                                (swap! sim-plot-cache assoc cache-index new-data))

                                              ;; Update sim plot.
                                              (reset! sim-plot-data new-data))))]
                              (js/setTimeout work 100))))

        options (r/atom (-> options
                            (assoc :cells (fn [row col prop] #js {}))
                            (assoc :cols cols-with-index)
                            (assoc :current-row-class "currentRow")
                            (assoc :on-click
                                   (fn [row-1 col-1 _ _ _]
                                     (update-sim-plot row-1 col-1)))))

        anim-step (fn anim-step []
                    ;; There should always be another check in the list.
                    (when-let [chk (first @checks)]
                      (let [other-cols (remove #{(:column chk)} cols)
                            col-name (name (:column chk))]

                        ;; Update the condition and un-conditional probs for the current columns.
                        ;; Update the plot.
                        (if (not= @cur-col (:column chk))
                          (let [all-columns (string/join ", " cols-with-index)
                                all-bindings (string/join " AND " (map name other-cols))
                                ;; TODO: Can I change this to only get values for the current column?
                                q-cond (format "SELECT %s, (PROBABILITY DENSITY OF %s UNDER model CONDITIONED BY %s AS p) FROM data LIMIT %s;"
                                               all-columns col-name all-bindings num-rows)
                                q-uncond (format "SELECT %s, (PROBABILITY DENSITY OF %s UNDER model AS p) FROM data LIMIT %s;"
                                                 col-name col-name num-rows)]
                            (reset! cur-col-cond-p (-> (query-fn q-cond) ->clj vec))
                            (reset! cur-col-uncond-p (-> (query-fn q-uncond) ->clj vec))
                            (reset! cur-col (:column chk))))

                        ;; Update the table.
                        (let [row (nth table-data (:row chk))

                              ah (anomaly-helper @cur-col-uncond-p @cur-col-cond-p chk row cols schema thresh alpha)
                              {:keys [user-text anomaly]} ah]

                          ;; Update query.
                          (reset! query-user-text user-text)

                          ;; Update highlighted point in plot.
                          (reset! cur-row (:row chk))
                          (reset! cur-cell-anom anomaly)

                          ;; Switch to next check.
                          (swap! checks rest)

                          ;; Update table highlighting.
                          ;; TODO: Move this into a specialized table component.
                          (swap! row-to-anomaly assoc {:row (:row chk) :col (name (:column chk))} anomaly)
                          (let [new-cells (fn [row col prop]
                                            (let [cell-props #js {}]
                                              (let [curr-anom (get @row-to-anomaly {:row row :col prop})]
                                                (when-not (nil? curr-anom)
                                                  (let [color (if curr-anom
                                                                "red-highlight"
                                                                "blue-highlight")] ;; should be blue
                                                    (set! (.-className cell-props) color))))
                                              cell-props))]
                            (swap! options assoc :cells new-cells))

                          ;; Setup next iteration.
                          (reset! timer (js/setTimeout anim-step step-time))))))

        node (dom/createElement "div")
        comp (fn [options]
               [v-box
                :style {:min-height "600px"
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
                                                   #_(when (every? some? [@cur-col-cond-p @cur-row @cur-col @cur-cell-anom])
                                                       (let [plot-rows @cur-col-cond-p
                                                             plot-rows (mapv #(assoc % :anomaly "undefined") plot-rows)
                                                             plot-rows (some-> plot-rows (assoc-in [@cur-row :anomaly] @cur-cell-anom))]
                                                         [anomaly-plot plot-rows schema @cur-col]))
                                                   [gap :size "200px"]
                                                   [sim-plot @selected-cell-anomalous @sim-plot-data
                                                    (:row @selected-cell)
                                                    cols-with-index]]]
                                       [gap :size "20px"]
                                       [:div {:class "observablehq--inspect"
                                              :style {:white-space "pre-wrap"}}
                                        @query-user-text]]]]])]

    ;; Stop the animation when the cell is invalided.
    (.then invalidation #(when @timer (js/clearTimeout @timer)))
    ;; Start animation.
    (reset! timer (js/setTimeout anim-step 1000))
    (rdom/render [comp options] node)
    node))
