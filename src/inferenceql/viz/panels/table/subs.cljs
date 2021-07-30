(ns inferenceql.viz.panels.table.subs
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.viz.panels.table.renderers :as rends]
            [inferenceql.viz.panels.table.handsontable :as hot]
            [inferenceql.viz.panels.table.selections :as selections]
            [inferenceql.viz.util :refer [coerce-bool]]
            [inferenceql.viz.panels.table.util :refer [merge-row-updates column-settings]]
            [goog.string :refer [format]]
            [handsontable$default :as yarn-handsontable]))

;;; Subs related selection layer color.

(rf/reg-sub :table/highlight-class
            :<- [:control/selection-color]
            (fn [color]
              (case color
                :red "red-highlight"
                :green "green-highlight"
                :blue "blue-highlight")))

;;; Subs related to selections within tables.

(rf/reg-sub :table/selection-layer-coords
            (fn [db [_sub-name]]
              (get-in db [:table-panel :selection-layer-coords])))

(rf/reg-sub :table/selection-layers
            :<- [:table/selection-layer-coords]
            :<- [:table/visual-headers]
            :<- [:table/visual-rows]
            selections/selection-layers)

(def ^:private selection-layer-order
  "This is the order in which selection layers will be returned in certain subscriptions."
  [:blue :green :red])

(rf/reg-sub :table/selection-layers-list
            :<- [:table/selection-layers]
            (fn [selection-layers]
              (keep (fn [layer-name]
                      (let [layer (get selection-layers layer-name)]
                        ;; Only add the selection layer to the list if there is
                        ;; a valid selection in it.
                        (when (some? (get layer :selections))
                          ;; Add in the name of the selection layer into the selection
                          ;; layer map itself because the layers will be embedded in a
                          ;; list, and no longer have keys (their names) associated with
                          ;; them.
                          (assoc layer :id layer-name))))
                    selection-layer-order)))

;;; Subs related to selections within the active selection layer.

(rf/reg-sub :table/selection-coords-active
            :<- [:control/selection-color]
            :<- [:table/selection-layer-coords]
            (fn [[color selection-layer-coords]]
              (get selection-layer-coords color)))

;;; Subs related to populating tables with data.

(rf/reg-sub :table/physical-data
            (fn [db _]
              (get-in db [:table-panel :physical-data])))

(rf/reg-sub :table/physical-headers
            :<- [:table/physical-data]
            (fn [physical-data]
              (get physical-data :headers)))

(rf/reg-sub :table/physical-row-ids
            :<- [:table/physical-data]
            (fn [physical-data]
              (get physical-data :row-ids)))

(rf/reg-sub :table/physical-rows-by-id
            :<- [:table/physical-data]
            (fn [physical-data]
              (get physical-data :rows-by-id)))

(rf/reg-sub :table/physical-rows
            :<- [:table/physical-row-ids]
            :<- [:table/physical-rows-by-id]
            (fn [[row-ids rows-by-id]]
              (mapv rows-by-id row-ids)))

(defn- display-headers
  "Returns an sequence of strings for column name headers to display.
  To be used as the :colHeaders setting for handsontable."
  [headers]
  ;; We guard for nil here beacuse we want to return nil if headers is nil.
  ;; If we instead give handsontable an empty list for headers, there will be a tiny
  ;; rectangle displayed in the top-left corner of the table instead of an empty
  ;; table like we want.
  (when headers
    (let [make-presentable (fn [header]
                             header)] ;; no-op stub for now.
      (map make-presentable headers))))

;;; Subs related to data in handsontable with various user edits incorporated.

(rf/reg-sub :table/rows-by-id
            (fn [db _]
              (get-in db [:table-panel :rows-by-id])))

(rf/reg-sub :table/row-ids
            (fn [db _]
              (get-in db [:table-panel :row-ids])))

(rf/reg-sub :table/rows
            :<- [:table/row-ids]
            :<- [:table/rows-by-id]
            (fn [[row-ids rows-by-id]]
              (mapv rows-by-id row-ids)))

;;; Subs related to visual state of the table.

(rf/reg-sub :table/visual-state
            (fn [db _]
              (get-in db [:table-panel :visual-state])))

(rf/reg-sub :table/visual-headers
            :<- [:table/visual-state]
            (fn [visual-state]
              (get visual-state :headers)))

(rf/reg-sub :table/visual-row-ids
            :<- [:table/visual-state]
            (fn [visual-state]
              (get visual-state :row-ids)))

(rf/reg-sub :table/visual-rows
            :<- [:table/visual-row-ids]
            :<- [:table/rows-by-id]
            (fn [[row-ids rows-by-id]]
              (mapv rows-by-id row-ids)))

(rf/reg-sub :table/selected-row-flags
            :<- [:table/rows]
            :<- [:viz/pts-store-filter]
            (fn [[rows pts-store-filter]]
              (when pts-store-filter
                (mapv pts-store-filter rows))))

;;; Subs related showing/hiding certain columns or table controls.

(defn show-table-controls
  "Returns a value for the css visibility property.
  To be used as re-frame subscription."
  [rows]
  (if (seq rows)
    "visible"
    "hidden"))

(rf/reg-sub :table/show-table-controls
            :<- [:table/rows]
            show-table-controls)

(rf/reg-sub :table/show-label-column
            (fn [db _]
              (get-in db [:table-panel :show-label-column])))

(defn hidden-columns
  "Returns a value for the Handsontable hiddenColumns setting.
  Hides the first column when `show-label-column` is false.
  To be used as a re-frame subscriptions."
  [show-label-column]
  (if show-label-column
    {}
    {:columns [0]
     :indicators true}))

(rf/reg-sub :table/hidden-columns
            :<- [:table/show-label-column]
            hidden-columns)

;;; Subs related to various table settings and state.

(defn ^:sub cells
  "Returns a function used by the :cells property in Handsontable's options.
  Provides special styling for rows selected through vega-lite visualizations."
  [selected-row-flags]
  (fn [row _col prop]
    (this-as obj
      (let [hot (.-instance obj)
            editable (true? (.getDataAtRowProp hot row (name :editable)))
            selected (when row (nth selected-row-flags row false))
            label-column-cell (= prop (name :label))

            class-names [(when editable "editable-cell")
                         (when selected "selected-row")
                         (when label-column-cell "label-cell")]
            class-names-string (str/join " " (remove nil? class-names))

            ;; Make the :label column editable.
            ;; Make editable row editable.
            read-only (and (not label-column-cell) (not editable))]
        #js {:className class-names-string
             :readOnly read-only}))))

(rf/reg-sub :table/cells
            :<- [:table/selected-row-flags]
            cells)

(defn row-headers
  "Returns a function to be used as the rowHeaders option in Handsontable.
  To be used as a re-frame subscription."
  [hot]
  (fn [row-physical-index]
    (let [v-row (.toVisualRow hot row-physical-index)]
      (.getDataAtRowProp hot v-row (name :rowid)))))

(rf/reg-sub :table/row-headers
            :<- [:table/hot-instance]
            row-headers)

(def context-menu
  {:items {"incorp_new_vals" {:disabled false
                              :name "INCORPORATE all new values into model"
                              :callback (fn [_key _selection _click-event]
                                          (rf/dispatch [:control/incorp-new-vals-in-query]))}}})

(defn ^:sub real-hot-props
  [[table-headers row-headers rows cells hidden-columns selection-coords-active]]
  (-> hot/real-hot-settings
      (assoc-in [:settings :data] rows)
      (assoc-in [:settings :colHeaders] (display-headers table-headers))
      (assoc-in [:settings :columns] (column-settings table-headers))
      (assoc-in [:settings :rowHeaders] row-headers)
      (assoc-in [:settings :cells] cells)
      (assoc-in [:settings :contextMenu] context-menu)
      (assoc-in [:settings :hiddenColumns] hidden-columns)
      (assoc-in [:selections-coords] selection-coords-active)))
(rf/reg-sub :table/real-hot-props
            :<- [:table/physical-headers]
            :<- [:table/row-headers]
            :<- [:table/physical-rows]
            :<- [:table/cells]
            :<- [:table/hidden-columns]
            :<- [:table/selection-coords-active]
            real-hot-props)

(rf/reg-sub
 :table/cells-style-fn
 (fn [_ _]
   {:cell-renderer-fn (rf/subscribe [:table/cell-renderer-fn])})
 (fn [{:keys [cell-renderer-fn]}]
   ;; Returns a function used by the :cells property in Handsontable's options.
   (fn [row col]
     (clj->js {:renderer cell-renderer-fn}))))

(rf/reg-sub
 :table/cell-renderer-fn
 (fn [_ _]
   {:row-likelihoods (rf/subscribe [:highlight/row-likelihoods-normed])
    :missing-cells-flagged (rf/subscribe [:highlight/missing-cells-flagged])
    :conf-thresh (rf/subscribe [:control/confidence-threshold])
    :conf-mode (rf/subscribe [:control/reagent-form [:confidence-mode]])
    :computed-headers (rf/subscribe [:table/physical-headers])})
 ;; Returns a cell renderer function used by Handsontable.
 (fn [{:keys [row-likelihoods missing-cells-flagged conf-thresh conf-mode computed-headers]}]
   (case conf-mode
     :none
     (.. yarn-handsontable -renderers -TextRenderer)

     :row
     (fn [& args]
       ;; These render functions are actually called with this args list:
       ;; [hot td row col prop value cell-properties]
       ;; Instead, we are specifying [& args] here to make it cleaner to
       ;; pass in data to custom rendering functions.
       (rends/row-wise-likelihood-threshold-renderer args row-likelihoods conf-thresh))

     :cells-existing
     (.. yarn-handsontable -renderers -TextRenderer)

     :cells-missing
     (fn [& args]
       (rends/missing-cell-wise-likelihood-threshold-renderer args missing-cells-flagged computed-headers)))))

(defn hot-instance
  "Returns the instance of Handsontable used to display the table.
  To be used as a re-frame subscription."
  [db _]
  (get-in db [:table-panel :hot-instance]))

(rf/reg-sub :table/hot-instance
            hot-instance)
