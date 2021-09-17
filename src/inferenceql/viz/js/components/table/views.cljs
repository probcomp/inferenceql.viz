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
      (dissoc :colHeaders :columns :dropdownMenu)
      (assoc :height "auto")
      (assoc :width "auto")))

(defn handsontable-base
  "A simplified version of a reagent component for Handsontable."
  ([attributes props]
   (let [hot-instance (reagent/atom nil)
         dom-nodes (reagent/atom {})]

     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [{:keys [settings hooks]} props
               hot (yarn-handsontable. (:table-div @dom-nodes) (clj->js settings))]

           ;; Fix scrolling for HOT in Observable.
           (.add (.-hooks yarn-handsontable)
                 "afterRender"
                 (fn []
                   (.. hot -view -wt -wtOverlays (updateMainScrollableElements)))
                 hot)

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
  It properly transforms data and options and delivers them as props to handsontable.

  `options` - A map which contains various options about how the table is displayed. All keys
    are optional. Some keys simply map to the same setting in Handsontable library. See the official
    Handsontable documentation for more details on those options.
      cols - Which columns from `data` to display.
        Default will show all columns (keys) from the first row of data.
      height - Handsontable height setting.
      v-scroll - Set to false so the full table is drawn with no scrollbars.
      cells - Handsontable cells setting. Can be used a variety of ways including cell highlighting.
      col-widths - Handsontable colWidths setting."
  [data options]
  (when data
    (let [{:keys [cols height width v-scroll cells col-widths]} options
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
                       (assoc-in [:settings :width] width))
          settings (cond-> settings
                           cells (assoc-in [:settings :cells] cells)
                           col-widths (assoc-in [:settings :colWidths] col-widths))]
      [handsontable-base {:style {:padding-bottom "5px"}} settings])))

