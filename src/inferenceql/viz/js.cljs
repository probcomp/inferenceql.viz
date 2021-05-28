(ns inferenceql.viz.js
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]
            [goog.labs.format.csv :as goog.csv]
            [goog.dom :as dom]
            [reagent.dom :as rdom]
            [inferenceql.query.data :refer [row-coercer]]
            [inferenceql.viz.csv :as csv]
            [inferenceql.viz.panels.table.handsontable :refer [default-hot-settings]]
            [inferenceql.viz.panels.table.views :refer [handsontable]]
            [inferenceql.viz.panels.table.subs :refer [column-settings]]
            [inferenceql.viz.panels.viz.views :as viz-views]
            [inferenceql.viz.panels.viz.vega :as vega]
            [inferenceql.viz.panels.viz.vega2 :as vega2]
            [inferenceql.viz.panels.control.views :as control]
            [inferenceql.query.js]                          ; For the Observable user to run queries. Not used directly.
            [inferenceql.inference.js]
            [medley.core :as medley]
            [ajax.core]
            [ajax.edn]
            [reagent.core :as r]
            [re-com.core :refer [v-box h-box gap]]
            [cljs-bean.core :refer [->clj]]
            [goog.string :refer [format]]))

(defn clj-schema
  [js-schema]
  (medley/map-kv (fn [k v]
                   [(keyword k) (keyword v)])
                 (js->clj js-schema)))

(defn ^:export run-remote-query
  [query query-server-url]
  (js/Promise. (fn [resolve reject]
                 (ajax.core/ajax-request
                  {:method :post
                   :uri query-server-url
                   :params query
                   :timeout 0
                   :format (ajax.core/text-request-format)
                   :response-format (ajax.edn/edn-response-format)
                   :handler (fn [[ok result]]
                              (if ok
                                ;; Success case.
                                (resolve (clj->js result))
                                ;; Failure case.
                                (let [parse-error (get-in result [:response :instaparse/failure])
                                      error-msg (if (some? parse-error)
                                                  ;; Return just the parse error.
                                                  (with-out-str (print parse-error))
                                                  ;; Return the entire error-result as a string.
                                                  (with-out-str (pprint result)))]
                                  (reject (js/Error. error-msg)))))}))))

(defn ^:export read-schema
  [schema-string]
  (clj->js (edn/read-string schema-string)))

(defn ^:export read-and-coerce-csv
  [csv-text schema]
  (let [csv-vecs (-> csv-text goog.csv/parse js->clj)
        schema (clj-schema schema)]
    (clj->js (csv/clean-csv-maps schema csv-vecs))))

(defn ^:export table
  ([data]
   (table data {}))
  ([data options]
   (let [options (js->clj options)
         {:strs [cols height v-scroll cells col-widths]} options

         ;; Potentially grabbing the columns from the keys in the first row of data.
         cols (or cols (->> data first js-keys))

         col-headers (for [col cols]
                       (clojure.string/replace col #"_" "_<wbr>"))
         height (cond
                  (false? v-scroll) "auto"
                  (some? height) height
                  :else
                  ;; TODO: may need to adjust these sizes.
                  (let [data-height (+ (* (count data) 22) 38)]
                    (min data-height 500)))

         settings (-> default-hot-settings
                      (assoc-in [:settings :data] data)
                      (assoc-in [:settings :colHeaders] col-headers)
                      (assoc-in [:settings :columns] (column-settings cols))
                      (assoc-in [:settings :height] height)
                      (assoc-in [:settings :width] "100%"))
         settings (cond-> settings
                    cells (assoc-in [:settings :cells] cells)
                    col-widths (assoc-in [:settings :colWidths] col-widths))

         node (dom/createElement "div")
         hot-component [handsontable {:style {:padding-bottom "5px"}} settings]]
     (rdom/render hot-component node)
     node)))

(defn ^:export waiting-msg []
  (let [node (dom/createElement "div")
        comp [:span {:class "observablehq--inspect"} "Waiting for query results."]]
    (rdom/render comp node)
    node))

(defn ^:export plot
  [data schema selections]
  (if (some? data)
    (let [selections (for [cols selections]
                       (for [col cols]
                         (keyword col)))
          schema (clj-schema schema)
          data (js->clj data :keywordize-keys true)
          spec (vega/generate-spec schema data selections)
          comp [viz-views/vega-lite spec {:actions false} nil nil]
          node (dom/createElement "div")]
     (rdom/render comp node)
     node)
    (waiting-msg)))

(defn plot-help
  [data schema selections pts-store]
  (if (some? data)
    (let [selections (for [cols selections]
                       (for [col cols]
                         (keyword col)))
          schema (clj-schema schema)
          data (js->clj data :keywordize-keys true)
          spec (vega2/generate-spec schema data selections)]
      [viz-views/vega-lite spec {:actions false} nil pts-store])))

(defn make-table-comp
  ([data]
   (make-table-comp data {}))
  ([data options]
   (when data
     (let [{:keys [cols height v-scroll cells col-widths]} options

           ;; Potentially grabbing the columns from the keys in the first row of data.
           cols (or cols (->> data first js-keys))

           col-headers (for [col cols]
                         (clojure.string/replace col #"_" "_<wbr>"))
           height (cond
                    (false? v-scroll) "auto"
                    (some? height) height
                    :else
                    ;; TODO: may need to adjust these sizes.
                    (let [data-height (+ (* (count data) 22) 38)]
                      (min data-height 500)))

           settings (-> default-hot-settings
                        (assoc-in [:settings :data] data)
                        (assoc-in [:settings :colHeaders] col-headers)
                        (assoc-in [:settings :columns] (column-settings cols))
                        (assoc-in [:settings :height] height)
                        (assoc-in [:settings :width] "100%"))
           settings (cond-> settings
                      cells (assoc-in [:settings :cells] cells)
                      col-widths (assoc-in [:settings :colWidths] col-widths))]
       [handsontable {:style {:padding-bottom "5px"}} settings]))))


(defn ^:export mini-app [query-fn options]
  (let [node (dom/createElement "div")

        options (->clj options)
        query (get options :query "SELECT * FROM data;")

        input-text (r/atom query)
        results (r/atom nil)
        failure (r/atom nil)
        running (r/atom false)

        update-results #(do
                          (reset! failure nil)
                          (reset! results %)
                          (set! (.-value node) %)
                          (.dispatchEvent node (js/CustomEvent. "input")))

        update-failure #(do
                          (reset! failure %)
                          (reset! results nil)
                          (set! (.-value node) nil)
                          (.dispatchEvent node (js/CustomEvent. "input")))

        ;; TODO find a good way to remove this trackers
        ;; when component is unmounted. Otherwise, we have a
        ;; memory leak.
        ;;_ (r/track! #(set! (.-query node) @input-text))

        comp (fn []
               [:div
                [control/panel input-text running query-fn update-results update-failure]
                (if (some? @failure)
                  [:div {:class "observablehq--inspect"
                         :style {:whitespace "pre"
                                 :color "red"}}
                   @failure]

                  (make-table-comp @results options))])]


    (rdom/render [comp] node)
    (set! (.-value node) nil)
    node))

(defn ^:export cell-by-cell-app [query-fn table-data schema num-rows thresh step-time options]
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
        checks (filter #(some? (get-in table-data [(:row %) (:column %)])) checks)
        checks (r/atom checks)
        cur-col (r/atom nil)
        cur-row (r/atom nil)
        cur-cell-status (r/atom false)

        node (dom/createElement "div")
        comp (fn [options]
               [v-box
                :class "cell-by-cell-app"
                :style {:border-width "3px"
                        :border-style "solid"
                        :padding "20px 20px 20px 20px"
                        :margin "25px 0px 25px 0px"
                        :border-radius "7px"
                        :border-color "grey"}
                :children [[make-table-comp table-data @options]
                           [gap :size "20px"]
                           [h-box
                            :children [[gap :size "25px"]
                                       (when (and (some? @plot-data) (some? @cur-row) (some? @cur-cell-status))
                                         (let [plot-rows (some->> (:rows @plot-data) ->clj (take num-rows) vec)
                                               plot-rows (mapv #(assoc % :anomaly "undefined") plot-rows)
                                               plot-rows (some-> plot-rows (assoc-in [@cur-row :anomaly] @cur-cell-status))]
                                           [plot-help plot-rows schema [(:col-names @plot-data)] nil]))]]
                           [gap :size "20px"]
                           [:div {:class "observablehq--inspect"
                                  :style {:white-space "pre-wrap"}}
                            @query]]])

        anim-step (fn anim-step []
                    ;; When there are checks left to be made.
                    (when-let [chk (first @checks)]
                      (let [other-cols (remove #{(:column chk)} cols)
                            col-name (name (:column chk))]
                        ;; Update the plot.
                        (if (not= @cur-col (:column chk))
                          (let [all-bindings (string/join " AND " (map name other-cols))
                                all-columns (string/join ", " (map name cols))
                                q-conditional-all (format "SELECT rowid, %s, (PROBABILITY DENSITY OF %s UNDER model CONDITIONED BY %s AS p) FROM data;"
                                                          all-columns col-name all-bindings)]
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


(defn ^:export this-function-fails
  []
  (let [inner-fn (fn [] (throw
                         (js/Error. "This is an intentional failure.")))]
    (inner-fn)))
