(ns inferenceql.viz.panels.table.subs
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.viz.panels.table.renderers :as rends]
            [inferenceql.viz.panels.table.handsontable :as hot]
            [inferenceql.viz.panels.table.selections :as selections]
            [inferenceql.viz.panels.table.db :as db]
            [inferenceql.viz.panels.override.views :as modal]))

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

(rf/reg-sub :table/table-headers
            (fn [db _]
              (db/table-headers db)))

(defn table-rows
  [db _]
  (db/table-rows db))
(rf/reg-sub :table/table-rows table-rows)

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
                       (if (= attr hot/label-col-header)
                         {:data attr :readOnly false} ; Make the score column user-editable.
                         {:data attr}))]
    (map settings-map headers)))

;;; Subs related to visual state of the table

(rf/reg-sub :table/visual-headers
            (fn [db _]
              (get-in db [:table-panel :visual-headers])))

(rf/reg-sub :table/visual-rows
            (fn [db _]
              (get-in db [:table-panel :visual-rows])))

(rf/reg-sub :table/selected-row-flags
            :<- [:table/table-rows]
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
            :<- [:table/table-rows]
            show-table-controls)

;;; Subs related to various table settings and state.

(defn ^:sub cells
  "Returns a function used by the :cells property in Handsontable's options.
  Provides special styling for rows selected through vega-lite visualizations."
  [selected-row-flags]
  (fn [row _col prop]
    (let [selected (when row (nth selected-row-flags row))
          label-column-cell (= prop (name :label))

          class-names [(when selected "selected-row")
                       (when label-column-cell "label-cell")]
          class-names-string (str/join ", " (remove nil? class-names))]
      #js {:className class-names-string
           ;; Make cells in the :label column editable
           :readOnly (not label-column-cell)})))

(rf/reg-sub :table/cells
            :<- [:table/selected-row-flags]
            cells)

(defn ^:sub real-hot-props
  [[headers rows context-menu cells selection-coords-active]]
  (-> hot/real-hot-settings
      (assoc-in [:settings :data] rows)
      (assoc-in [:settings :colHeaders] (display-headers headers))
      (assoc-in [:settings :columns] (column-settings headers))
      (assoc-in [:settings :cells] cells)
      (assoc-in [:settings :contextMenu] context-menu)
      (assoc-in [:selections-coords] selection-coords-active)))
(rf/reg-sub :table/real-hot-props
            :<- [:table/table-headers]
            :<- [:table/table-rows]
            :<- [:table/context-menu]
            :<- [:table/cells]
            :<- [:table/selection-coords-active]
            real-hot-props)

(rf/reg-sub
 :table/context-menu
 (fn [_ _]
   {:col-overrides (rf/subscribe [:override/column-overrides])
    :col-names (rf/subscribe [:table/table-headers])})
 (fn [{:keys [col-overrides col-names]}]
   (let [set-function-fn (fn [key selection click-event]
                           (this-as hot
                             (let [last-col-num (.. (first selection) -start -col)
                                   last-col-num-phys (.toPhysicalColumn hot last-col-num)
                                   col-name (nth col-names last-col-num-phys)
                                   fn-text (get col-overrides col-name)

                                   modal-child [modal/js-function-entry-modal col-name fn-text]]
                               (rf/dispatch [:override/set-modal {:child modal-child}]))))

         clear-function-fn (fn [key selection click-event]
                             (this-as hot
                               (let [last-col-num (.. (first selection) -start -col)
                                     last-col-num-phys (.toPhysicalColumn hot last-col-num)
                                     col-name (nth col-names last-col-num-phys)]
                                 (rf/dispatch [:override/clear-column-function col-name]))))

         disable-fn (fn []
                     (this-as hot
                       (let [last-selected (.getSelectedRangeLast hot)
                             from-col (.. last-selected -from -col)
                             to-col (.. last-selected -to -col)
                             from-row (.. last-selected -from -row)]

                         ;; Disable the menu when either more than one column is selected
                         ;; or when the selection does not start from a cell in the header row.
                         (or (not= from-col to-col) (not= from-row 0)))))]
     {:items {"set_function" {:disabled disable-fn
                              :name "Set js function"
                              :callback set-function-fn}
              "clear_function" {:disabled disable-fn
                                :name "Clear js function"
                                :callback clear-function-fn}}})))

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
    :computed-headers (rf/subscribe [:table/table-headers])})
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
