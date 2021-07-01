(ns inferenceql.viz.observable.components
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.panels.table.subs :refer [column-settings]]
            [inferenceql.viz.observable.components.viz :refer [vega-lite]]
            [inferenceql.viz.observable.components.hot :refer [handsontable default-hot-settings]]
            [inferenceql.viz.observable.components.vega :as vega]
            [inferenceql.viz.observable.components.control :as control]
            [inferenceql.viz.observable.util :refer [clj-schema]]))

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

(defn ^:export plot
  [data schema selections]
  (if (some? data)
    (let [selections (for [cols selections]
                       (for [col cols]
                         (keyword col)))
          schema (clj-schema schema)
          data (js->clj data :keywordize-keys true)
          spec (vega/generate-spec schema data selections)
          comp [vega-lite spec {:actions false} nil nil]
          node (dom/createElement "div")]
     (rdom/render comp node)
     node)))

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
