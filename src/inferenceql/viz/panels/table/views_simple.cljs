(ns inferenceql.viz.panels.table.views-simple
  (:require [handsontable$default :as yarn-handsontable]
            [reagent.core :as reagent]
            [medley.core :refer [filter-kv]]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.panels.table.util :refer [column-settings]]
            [inferenceql.viz.panels.table.handsontable :refer [default-hot-settings]]
            [inferenceql.viz.panels.table.views :as tv]))

(def simple-hot-settings
  (-> default-hot-settings
      (update :settings dissoc :colHeaders :columns :dropdownMenu :filters)
      (assoc-in [:settings :height] "auto")
      (assoc-in [:settings :width] "auto")))

(defn handsontable
  "A reagent component that dispalys `data` in handsontable.
  It is a simple version of the full handsonatable component, that properly transforms `data` and
  `options` and delivers them as `props` to the full handsontable component.

   `mode` - can be :reagent or :reagent-observable. Using :reagent-observable will enable certain
      fixes for Observable notebooks.

  `options` - A map which contains various options about how the table is displayed. All keys
    are optional. Some keys simply map to the same setting in Handsontable library. See the official
    Handsontable documentation for more details on those options.
      cols - Which columns from `data` to display.
        Default will show all columns (keys) from the first row of data.
      height - Handsontable height setting.
      width - Handsontable width setting.
      v-scroll - Set to false so the full table is drawn with no scrollbars.
      cells - Handsontable cells setting. Can be used a variety of ways including cell highlighting.
      col-widths - Handsontable colWidths setting. "
  [mode attributues data options]
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
      [tv/handsontable mode attributues props])))
