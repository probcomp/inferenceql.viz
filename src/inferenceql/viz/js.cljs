(ns inferenceql.viz.js
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
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
            [inferenceql.viz.panels.control.views :as control]
            [inferenceql.query.js]                          ; For the Observable user to run queries. Not used directly.
            [inferenceql.inference.js]
            [medley.core :as medley]
            [ajax.core]
            [ajax.edn]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]))

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

(defn ^:export cell-by-cell-app [query-fn table-data options]
  (let [node (dom/createElement "div")

        options (js->clj options :keywordize-keys true)

        cols (get options :cols)
        query "SELECT * FROM data;"

        cells-fn (fn [row col prop] #js {})
        options (r/atom (assoc-in options [:settings :cells] cells-fn))


        comp (fn [options]
               [:div
                (make-table-comp table-data @options)
                [:div {:class "observablehq--inspect"
                       :style {:whitespace "pre"}}
                 query]])]

    (js/setTimeout (fn []
                     (let [new-cells (fn [row col prop]
                                       #js {"className" "gold-highlight"}
                                       #_(let [cell-props #js {}]
                                           (when (= row 1)
                                             (set! (.-className cell-props) "gold-highlight"))
                                           cell-props))]
                       (swap! options assoc-in [:settings :cells] new-cells)
                       (.log js/console "here-------")))
                   2000)

    (rdom/render [comp options] node)
    (set! (.-value node) nil)
    node))


(defn ^:export this-function-fails
  []
  (let [inner-fn (fn [] (throw
                         (js/Error. "This is an intentional failure.")))]
    (inner-fn)))
