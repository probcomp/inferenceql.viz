(ns inferenceql.viz.panels.table.subs
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.viz.panels.table.renderers :as rends]
            [inferenceql.viz.panels.table.handsontable :as hot]
            [inferenceql.viz.panels.table.selections :as selections]
            [inferenceql.viz.panels.table.db :as db]
            [inferenceql.viz.panels.override.views :as modal]
            [inferenceql.viz.util :refer [coerce-bool]]
            [inferenceql.viz.panels.table.util :refer [merge-row-updates]]))


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

(rf/reg-sub :table/physical-row-order
            :<- [:table/physical-data]
            (fn [physical-data]
              (get physical-data :row-order)))

(rf/reg-sub :table/physical-rows-by-id
            :<- [:table/physical-data]
            (fn [physical-data]
              (get physical-data :rows-by-id)))

(rf/reg-sub :table/physical-rows
            :<- [:table/physical-row-order]
            :<- [:table/physical-rows-by-id]
            (fn [[row-order rows-by-id]]
              (mapv rows-by-id row-order)))

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

(defn- column-settings [headers]
  "Returns an array of objects that define settings for each column
  in the table including which attribute from the underlying map for the row
  is presented."
  (let [settings-map (fn [attr]
                       {:data attr})]
    (map settings-map headers)))

;;; Subs related to changes in the table data via the UI.

(rf/reg-sub :table/changes-existing
            (fn [db _]
              (get-in db [:table-panel :changes :existing])))

(rf/reg-sub :table/new-row-order
            (fn [db _]
              (get-in db [:table-panel :changes :new-row-order])))

(rf/reg-sub :table/rows-by-id-with-changes
            :<- [:table/physical-rows-by-id]
            :<- [:table/changes-existing]
            (fn [[rows-by-id changes]]
              (merge-row-updates rows-by-id changes)))

(rf/reg-sub :table/label-values
            :<- [:table/rows-by-id-with-changes]
            (fn [rows-by-id]
              (->> rows-by-id
                   (medley/map-vals :label)
                   (medley/filter-vals some?)
                   (medley/map-vals coerce-bool))))

(rf/reg-sub :table/row-order-all
            :<- [:table/physical-row-order]
            :<- [:table/new-row-order]
            (fn [[orig-rows new-rows]]
              (vec (concat orig-rows new-rows))))

(rf/reg-sub :table/new-rowid
            :<- [:table/row-order-all]
            (fn [row-order]
              (inc (count row-order))))

(rf/reg-sub :table/rows-all
            :<- [:table/row-order-all]
            :<- [:table/rows-by-id-with-changes]
            (fn [[row-order rows-by-id]]
              (mapv rows-by-id row-order)))

;;; Subs related to visual state of the table.

(rf/reg-sub :table/visual-state
            (fn [db _]
              (get-in db [:table-panel :visual-state])))

(rf/reg-sub :table/visual-headers
            :<- [:table/visual-state]
            (fn [visual-state]
              (get visual-state :headers)))

(rf/reg-sub :table/visual-row-order
            :<- [:table/visual-state]
            (fn [visual-state]
              (get visual-state :row-order)))

(rf/reg-sub :table/visual-rows
            :<- [:table/visual-row-order]
            :<- [:table/rows-by-id-with-changes]
            (fn [[row-order rows-by-id]]
              (mapv rows-by-id row-order)))

(rf/reg-sub :table/selected-row-flags
            :<- [:table/rows-all]
            :<- [:viz/pts-store-filter]
            (fn [[rows pts-store-filter]]
              (when pts-store-filter
                (map pts-store-filter rows))))

;;; Subs related showing/hiding certain columns or table controls.

(defn show-table-controls
  "Returns a value for the css visibility property.

  To be used as re-frame subscription."
  [rows]
  (if (seq rows)
    "visible"
    "hidden"))

(rf/reg-sub :table/show-table-controls
            :<- [:table/physical-rows]
            show-table-controls)

(rf/reg-sub :table/show-label-column
            (fn [db _]
              (get-in db [:table-panel :show-label-column])))

(defn hidden-columns
  "Returns a value for the Handsontable hiddenColumns setting.
  Hides the first column when `label-column-show` is false.
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

(defn ^:sub real-hot-props
  [[table-headers row-headers rows context-menu cells hidden-columns selection-coords-active]]
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
            :<- [:table/context-menu]
            :<- [:table/cells]
            :<- [:table/hidden-columns]
            :<- [:table/selection-coords-active]
            real-hot-props)

(rf/reg-sub
 :table/context-menu
 (fn [_ _]
   {:col-overrides (rf/subscribe [:override/column-overrides])
    :col-names (rf/subscribe [:table/physical-headers])
    :label-values (rf/subscribe [:table/label-values])})
 (fn [{:keys [_col-overrides _col-names label-values]}]
   (let [incorp-label-col (fn [_key _selection _click-event]
                            (rf/dispatch [:control/incorp-label-values label-values]))
         disable-fn (fn []
                     (this-as hot
                       (let [last-selected (.getSelectedRangeLast hot)
                             from-col (.. last-selected -from -col)
                             to-col (.. last-selected -to -col)
                             from-row (.. last-selected -from -row)

                             prop-name (keyword (.colToProp hot to-col))]
                         ;; Disable the menu when either more than one column is selected
                         ;; or when the selection does not start from a cell in the header row.
                         ;; or when the selection is not in the label column.
                         (or (not= from-col to-col)
                             (not= from-row -1)
                             (not= prop-name :label)))))]
     {:items {"incorp_label_col" {:disabled disable-fn
                                  :name "INCORPORATE values into model"
                                  :callback incorp-label-col}}})))

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
     js/Handsontable.renderers.TextRenderer

     :row
     (fn [& args]
       ;; These render functions are actually called with this args list:
       ;; [hot td row col prop value cell-properties]
       ;; Instead, we are specifying [& args] here to make it cleaner to
       ;; pass in data to custom rendering functions.
       (rends/row-wise-likelihood-threshold-renderer args row-likelihoods conf-thresh))

     :cells-existing
     js/Handsontable.renderers.TextRenderer

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
