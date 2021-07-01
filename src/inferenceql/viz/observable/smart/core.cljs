(ns inferenceql.viz.observable.smart.core
  (:require [clojure.string :as string]
            [clojure.walk :refer [postwalk]]
            [goog.dom :as dom]
            [reagent.dom :as rdom]
            [medley.core :as medley]
            [reagent.core :as r]
            [re-com.core :refer [v-box h-box box gap]]
            [cljs-bean.core :refer [->clj]]
            [goog.string :refer [format]]
            [inferenceql.viz.observable.components.hot :refer [handsontable-wrapper]]
            [inferenceql.viz.observable.components.viz :refer [vega-lite]]
            [inferenceql.viz.observable.smart.vega :as vega]
            [inferenceql.viz.observable.util :refer [clj-schema]]))

(defn plot-help
  [data schema selections pts-store]
  (if (some? data)
    (let [selections (postwalk #(if (string? %) (keyword  %) %)
                               selections)
          schema (clj-schema schema)
          data (->clj data)
          spec (vega/generate-spec schema data selections)]
      [vega-lite spec {:actions false} nil pts-store])))

(defn ^:export app [query-fn table-data schema num-rows thresh step-time options]
  (let [table-data (vec (take num-rows (->clj table-data)))
        schema (medley/map-kv (fn [k v] [(keyword k) (keyword v)])
                              (->clj schema))

        options (js->clj options :keywordize-keys true)
        cols (map keyword (get options :cols))

        query (r/atom "SELECT * FROM data;")
        plot-data (r/atom nil)
        options (r/atom (assoc options :cells (fn [row col prop] #js {})))
        pts-store (r/atom nil)

        checks (for [c cols i (range (count table-data))] {:column c :row i})
        checks (cycle (filter #(some? (get-in table-data [(:row %) (:column %)])) checks))
        checks (r/atom checks)
        cur-col (r/atom nil)
        cur-row (r/atom nil)
        cur-cell-status (r/atom false)

        node (dom/createElement "div")
        comp (fn [options]
               [v-box
                :min-height "1100px"
                :children [[v-box
                            :class "cell-by-cell-app"
                            :style {:border-width "3px"
                                    :border-style "solid"
                                    :padding "20px 20px 20px 20px"
                                    :margin "25px 0px 25px 0px"
                                    :border-radius "7px"
                                    :border-color "grey"}
                            :children [[handsontable-wrapper table-data @options]
                                       [gap :size "20px"]
                                       [h-box
                                        :children [[gap :size "25px"]
                                                   (when (and (some? @plot-data) (some? @cur-row) (some? @cur-cell-status))
                                                     (let [plot-rows (some->> (:rows @plot-data) ->clj vec)
                                                           plot-rows (mapv #(assoc % :anomaly "undefined") plot-rows)
                                                           plot-rows (some-> plot-rows (assoc-in [@cur-row :anomaly] @cur-cell-status))]
                                                       [plot-help plot-rows schema [(:col-names @plot-data)] nil]))]]
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
                              val (if (= col-type :gaussian) val (quote-val val))

                              q1 (format "SELECT (PROBABILITY DENSITY OF %s=%s UNDER model AS p) FROM data LIMIT 1;" col-name val)

                              row-rest (dissoc row (:column chk))
                              cells (for [c other-cols]
                                      {:col c :val (get row-rest c)})
                              cells (filter #(some? (:val %)) cells)
                              cells (map (fn [{:keys [col val]}]
                                           (if (= (get schema col) :gaussian)
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
                          ;;(reset! pts-store [{:fields [{:field "rowid" :type "E"}] :values [(:row chk)]}])
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
