(ns inferenceql.viz.panels.table.views-simple
  "Reagent component for Handsontable that can be used independently from iql.viz app."
  (:require [handsontable$default :as yarn-handsontable]
            [reagent.core :as reagent]
            [medley.core :refer [filter-kv]]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.panels.table.util :refer [column-settings]]
            [inferenceql.viz.panels.table.handsontable :refer [default-hot-settings]]
            [inferenceql.viz.panels.table.views-base :refer [handsontable-base]]))

(defn handsontable-reagent
  "A Reagent component for Handsontable that uses an atom as a datastore for the Handsontable
  instance."
  [attributes props]
  (let [hot-instance (reagent/atom nil)
        hot-reset #(reset! hot-instance %)]
    (fn [attributes props]
      [handsontable-base hot-instance hot-reset attributes props])))

(defn handsontable-reagent-observable
  "A Reagent component for Handsontable that includes fixes for glitches with scrolling
  and refreshing when used in Observable notebooks. This uses an atom as a datastore for the
  Handsontable instance."
  [attributes props]
  (let [hot-instance (reagent/atom nil)
        hot-reset #(reset! hot-instance %)]
    (reagent/create-class
     {:component-did-mount
      (fn [this]
        ;; Make new HOT instances appear immediately in Observable.
        (.setTimeout js/window (fn [] (.refreshDimensions @hot-instance)) 30))

      :reagent-render
      (fn [attributes props]
        (let [props (assoc-in props [:hooks :afterRender]
                              (fn [hot]
                                ;; Fix scrolling for Handsontable in Observable.
                                (fn [] (.. hot -view -wt -wtOverlays (updateMainScrollableElements)))))])

        [handsontable-base hot-instance hot-reset attributes props])})))

(def simple-hot-settings
  (-> default-hot-settings
      (update :settings dissoc :colHeaders :columns :dropdownMenu :filters)
      (assoc-in [:settings :height] "auto")
      (assoc-in [:settings :width] "auto")))

(defn handsontable
  "A reagent component that displays `data` in handsontable.
  It properly transforms `data` and `options` and delivers them as `props` to the base
  handsontable component.

  `options` - A map which contains various options about how the table is displayed. All keys
    are optional. Some keys simply map to the same setting in Handsontable library. See the official
    Handsontable documentation for more details on those options.
      cols - Which columns from `data` to display.
        Default will show all columns (keys) from the first row of data.
      height - Handsontable height setting.
      width - Handsontable width setting.
      v-scroll - Set to false so the full table is drawn with no scrollbars.
      cells - Handsontable cells setting. Can be used a variety of ways including cell highlighting.
      col-widths - Handsontable colWidths setting.

    `observable-fixes` - (boolean) Enable certain settings and fixes for Observable notebooks."
  [attributues data options observable-fixes]
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
          width (or width "100%")
          props (-> simple-hot-settings
                    (assoc-in [:settings :data] data)
                    (assoc-in [:settings :colHeaders] col-headers)
                    (assoc-in [:settings :columns] (column-settings cols))
                    (assoc-in [:settings :height] height)
                    (assoc-in [:settings :width] width))
          props (cond-> props
                        cells (assoc-in [:settings :cells] cells)
                        col-widths (assoc-in [:settings :colWidths] col-widths))]
      (if observable-fixes
        [handsontable-reagent-observable attributues props]
        [handsontable-reagent attributues props]))))
