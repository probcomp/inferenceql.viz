(ns inferenceql.viz.js.components.table.views
  (:require [handsontable$default :as yarn-handsontable]
            [reagent.core :as reagent]
            [medley.core :refer [filter-kv]]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.panels.table.util :refer [column-settings]]
            [inferenceql.viz.panels.table.views :refer [update-hot!]]
            [inferenceql.viz.panels.table.handsontable :refer [default-hot-settings]]))

(def observable-hot-settings
  (-> default-hot-settings
      (update :settings dissoc :colHeaders :columns :dropdownMenu :filters)
      (assoc-in [:settings :height] "auto")
      (assoc-in [:settings :width] "auto")))

(defn handsontable-base
  "A simplified version of a reagent component for Handsontable."
  ([attributes props]
   (let [hot-instance (reagent/atom nil)
         dom-nodes (reagent/atom {})]

     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [{:keys [settings on-click]} props
               hot (yarn-handsontable. (:table-div @dom-nodes) (clj->js settings))]

           ;; Fix scrolling for HOT in Observable.
           (.add (.-hooks yarn-handsontable)
                 "afterRender"
                 (fn []
                   (.. hot -view -wt -wtOverlays (updateMainScrollableElements)))
                 hot)

           (when on-click
             (.add (.-hooks yarn-handsontable)
                   "afterSelectionEnd"
                   on-click
                   hot))

           ;; Make new HOT instances appear immediately in Observable.
           (.setTimeout js/window (fn [] (.refreshDimensions hot)) 30)

           ;; Save HOT instance.
           (reset! hot-instance hot)))

       :component-did-update
       (fn [this old-argv]
         (let [[_ _old-attributes old-props] old-argv
               [_ _new-attributes new-props] (reagent/argv this)

               old-settings (:settings old-props)
               new-settings (:settings new-props)
               changed-settings (filter-kv (fn [setting-key new-val]
                                             (not= (get old-settings setting-key) new-val))
                                           new-settings)]

           ;; Update settings.
           (when (seq changed-settings)
             (update-hot! @hot-instance changed-settings (:selections-coords new-props)))))

       :component-will-unmount
       (fn [this]
         (when @hot-instance
           (.destroy @hot-instance)))

       :reagent-render
       (fn [attributes props]
         [:div#table-container attributes
          [:div {:ref #(swap! dom-nodes assoc :table-div %)}]])}))))

(defn handsontable
  "A reagent component that dispalys `data` in handsontable.
  It properly transforms data and options and delivers them as props to handsontable."
  [data options]
  (when data
    (let [{:keys [cols height v-scroll cells col-widths on-click]} options
          ;; If no "cols" setting, use the keys in the first row as "cols".
          cols (or cols (->> data first keys (map name)))
          col-headers (for [col cols]
                        (clojure.string/replace col #"_" "_<wbr>"))
          height (cond
                   (false? v-scroll) "auto"
                   (some? height) height
                   :else
                   ;; TODO: may need to adjust these sizes.
                   (let [data-height (+ (* (count data) 22) 38)]
                     (min data-height 500)))
          settings (-> observable-hot-settings
                       (assoc-in [:settings :data] data)
                       (assoc-in [:settings :colHeaders] col-headers)
                       (assoc-in [:settings :columns] (column-settings cols))
                       (assoc-in [:settings :height] height)
                       (assoc-in [:settings :width] "100%"))
          settings (cond-> settings
                           cells (assoc-in [:settings :cells] cells)
                           col-widths (assoc-in [:settings :colWidths] col-widths)
                           on-click (assoc-in [:on-click] on-click))]
      [handsontable-base {:style {:padding-bottom "5px"}} settings])))

;;; Handsontable as React component.

(defn handsontable-react-wrapper
  "Wrapper around handsontable reagent component to enable use as React component.
  Needed because reagent's reactify-component delivers all props as a single map. We then
  need to unpack this map."
  [props]
  (let [{:keys [data options]} (->clj props)]
    (let [data (->clj data)
          options (->clj options)]
      [handsontable data options])))

(def handsontable-react
  "React component for handsontable"
  (r/reactify-component handsontable-react-wrapper))
