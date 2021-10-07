(ns inferenceql.viz.panels.table.views
  (:require [handsontable$default :as yarn-handsontable]
            [reagent.core :as reagent]
            [medley.core :refer [filter-kv]]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.store :refer [rows col-ordering num-points-at-iter
                                           xcat-models]]
            [inferenceql.viz.model.xcat-util :as xcat-util]
            [medley.core :as medley]))

(defn column-settings [headers]
  "Returns an array of objects that define settings for each column
  in the table including which attribute from the underlying map for the row
  is presented."
  (let [settings-map (fn [attr]
                       {:data attr})]
    (map settings-map headers)))

(defn- sort-state-applicable
  "Determines whether `sort-config` can be applied to the columns.

  This simply checks column numbers referenced `sort-config` are applicable to the number for
  `columns` present.

  Args:
    columns: A vector of column names.
    sort-config: (js-object) A sort config returned by Handsontable.
  Returns:
    A boolean if `sort-config` is applicable."
  [columns sort-config]
  (let [col-nums-refed (map :column (js->clj sort-config :keywordize-keys true))
        num-cols (count columns)]
    (every? #(< % num-cols) col-nums-refed)))

(defn update-hot!
  "A helper function for updating the settings in a handsontable."
  [hot-instance new-settings current-selection]
  (let [;; Stores whether settings that determine the data displayed have changed.
        table-changed (some new-settings [:data :colHeaders])
        sorting-plugin (.getPlugin hot-instance "multiColumnSorting")
        sort-config (.getSortConfig sorting-plugin)]

    (when table-changed
      (.deselectCell hot-instance))

    ;; When data is the only updated setting, use a different, potentially faster update function.
    (if (= (keys new-settings) [:data])
      (.loadData hot-instance (clj->js (:data new-settings)))
      (.updateSettings hot-instance (clj->js new-settings)))

    (when table-changed
      ;; Maintain the same sort order as before the update.
      ;; If colHeaders hasn't changed, we can apply the previous sort state.
      ;; Or if the sort-state is applicable to the current columns, we can as well.
      (when (or (nil? (:colHeaders new-settings))
                (sort-state-applicable (:colHeaders new-settings) sort-config))
        (.sort sorting-plugin sort-config))

      ;; Reapply selections present before the update.
      (when-let [coords (clj->js current-selection)]
        (.selectCells hot-instance coords false)))))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :manualColumnResize  true
              :autoWrapCol         false
              :autoWrapRow         false
              :filters             true
              ;; TODO: investigate more closely what each of
              ;; these options adds. And if they can be put
              ;; in the context-menu instead.
              :dropdownMenu        ["filter_by_condition"
                                    "filter_operators"
                                    "filter_by_condition2"
                                    "filter_by_value"
                                    "filter_action_bar"]
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :outsideClickDeselects false
              :readOnly            true
              :height              "50vh"
              :width               "100vw"
              :stretchH            "none"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

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
          [:div {:style {:margin-left "-1px" :margin-top "-1px"}}
           [:div {:ref #(swap! dom-nodes assoc :table-div %)}]]])}))))

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
      [handsontable-base {:style {:width width}} settings])))

(def default-cells-fn
  (fn [_ _ _] #js {}))

(defn cells-fn [xcat-model cluster-selected]
  (if-not cluster-selected
    default-cells-fn
    (let [cols-set (set (xcat-util/columns-in-view xcat-model (:view-id cluster-selected)))
          rows-set (set (xcat-util/rows-in-view-cluster xcat-model
                                                        (:view-id cluster-selected)
                                                        (:cluster-id cluster-selected)))]
      (fn [row _col prop]
        (if (and (rows-set row)
                 (cols-set (keyword prop)))
          #js {:className "blue-highlight"}
          #js {})))))

(defn data-table
  "Reagent component for data table."
  [iteration cluster-selected]
  (let [xcat-model (nth xcat-models iteration)
        num-points (nth num-points-at-iter iteration)
        modeled-cols (-> (set (xcat-util/columns-in-model xcat-model))
                         ;; Get modeled columns in the correct order by picking items in order
                         ;; from col-ordering.
                         (keep col-ordering))]

    [handsontable (take num-points rows)
     {:height "400px"
      :width (str 1390 "px")
      :cols (map name modeled-cols)
      :cells (cells-fn xcat-model cluster-selected)}]))

