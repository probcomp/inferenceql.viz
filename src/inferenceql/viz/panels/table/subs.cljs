(ns inferenceql.viz.panels.table.subs
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.viz.panels.table.renderers :as rends]
            [inferenceql.viz.panels.table.handsontable :as hot]
            [inferenceql.viz.panels.table.db :as db]
            [inferenceql.viz.panels.override.views :as modal]))

;;; Specs for validating the output of the :table/selection-layers sub.

(s/def ::selections (s/coll-of ::db/row))
(s/def ::selected-columns (s/coll-of ::db/header))
(s/def ::row-at-selection-start ::db/row)
(s/def ::selection-state-augments (s/keys :opt-un [::row-at-selection-start
                                                   ::selections
                                                   ::selected-columns]))
(s/def ::selection-state (s/merge ::db/selection-state ::selection-state-augments))
(s/def ::selection-layers (s/map-of ::db/selection-color ::selection-state))

;;; Subs related selection layer color.

(rf/reg-sub :table/highlight-class
            :<- [:control/selection-color]
            (fn [color]
              (case color
                :red "red-highlight"
                :green "green-highlight"
                :blue "blue-highlight")))

;;; Subs related to selections within tables.

(def ^:private selection-layer-order
  "This is the order in which selection layers will be returned in certain subscriptions."
  [:blue :green :red])

(defn header-for-selection
  "Return the headers in `visual-headers` as indexed by `selection-rectangle`.

  When the user selects two columns in a single selection rectangle, they can
  do so in any order. (e.g. A higher indexed column first and then a lower indexed one.)
  If they did so, we want to reflect this in the order of the column headers returned
  here unless :ascending true in which case the headers are always returned in
  ascending order."
  [visual-headers selection-rectangle & {:keys [ascending] :or {ascending false}}]
  (let [[_ col-start _ col-end] selection-rectangle
        headers (subvec visual-headers (min col-start col-end) (inc (max col-start col-end)))]
    (if (or ascending (< col-start col-end))
      headers
      (reverse headers))))

(defn get-selected-columns
  "Returns the column names selected in a sequence of selection rectangles, `coords`."
  [coords headers]
  (mapcat #(header-for-selection headers %) coords))

(defn get-selections
  "Returns the data in `rows` corresponding to the selection rectangles in `coords`.

  Data returned is a sequence of maps representing a subset of the data in `rows`.
  If the selection rectangles in `coords` are of different heights or have different starting rows,
  the data rows returned may have mismatched data from different rows in `rows`."
  [coords headers rows]
  (let [data-by-layer (for [layer coords]
                        (let [[r1 _c1 r2 _c2] layer]
                          ;; NOTE: This returns full rows corresponding to the rows in the
                          ;; selection rectangle, but does not subset to the columns selected.
                          ;; This is done intentionally so that full rows are returns as selections.
                          (subvec rows (min r1 r2) (inc (max r1 r2)))))]
    ;; Merging the row-wise data for each selection layer.
    (apply mapv merge data-by-layer)))

(defn get-row-at-selection-start
  "Returns the row in `rows` indexed by the start of the last selection rectangle in `coords`."
  [coords rows]
  (let [[r1 _c1 _r2 _c2] (last coords)]
    (nth rows r1)))

(defn valid-coords?
  "Checks whether the bounds of the selection rectangles in `coords` fit the data table size."
  [coords table-width table-height]
  (if (seq coords)
    (let [check-fn (fn [[r1 c1 r2 c2 :as coords]]
                     (and (every? nat-int? coords)
                          (< r1 table-height)
                          (< r2 table-height)
                          (< c1 table-width)
                          (< c2 table-width)))]
      (every? check-fn coords))
    false))

(defn add-selection-data
  "Augments `selection-layer` with derived data computed off the :coords in `selection-layer`."
  [selection-layer headers rows]
  (let [coords (:coords selection-layer)]
    ;; We don't want to compute and assoc-in derived data if the bounds of the coords
    ;; are beyond the data we currently have.
    (cond-> selection-layer
            (valid-coords? coords (count headers) (count rows))
            (assoc :selected-columns (get-selected-columns coords headers)
                   :selections (get-selections coords headers rows)
                   :row-at-selection-start (get-row-at-selection-start coords rows)))))

(rf/reg-sub :table/selection-layer-coords
            (fn [db [_sub-name]]
              (let [layers (get-in db [:table-panel :selection-layers])]
                (medley/map-vals #(select-keys % [:coords]) layers))))

(defn ^:sub selection-layers
  "Merges in data pertaining to the selection-layer-coords"
  [[selection-layer-coords visual-headers visual-display-rows]]
  (medley/map-vals #(add-selection-data % visual-headers visual-display-rows)
                   selection-layer-coords))
(s/fdef selection-layers :ret ::selection-layers)

(rf/reg-sub :table/selection-layers
            :<- [:table/selection-layer-coords]
            :<- [:table/visual-headers]
            :<- [:table/visual-rows]
            selection-layers)

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

(rf/reg-sub :table/selection-layer-active
            :<- [:control/selection-color]
            :<- [:table/selection-layers]
            (fn [[color selection-layers]]
              (get selection-layers color)))

(rf/reg-sub :table/selections-coords
            :<- [:table/selection-layer-active]
            (fn [selection-state]
              (get selection-state :coords)))

;;; Subs related to the type of data in the table.

(rf/reg-sub :table/virtual
            (fn [db _]
              (get-in db [:table-panel :virtual])))

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

;;; Subs related to various table settings and state.

(defn ^:sub cells
  "Returns a function used by the :cells property in Handsontable's options.
  Provides special styling for rows selected through vega-lite visualizations."
  [selected-row-flags]
  (fn [row col _prop]
    (this-as obj
      (let [hot (.-instance obj)
            v-row (.toVisualRow hot row)
            v-col (.toVisualColumn hot col)

            selected (when row
                       (nth selected-row-flags row false))]
        (when selected
          (.setCellMeta hot v-row v-col "className" "selected-row"))))))

(rf/reg-sub :table/cells
            :<- [:table/selected-row-flags]
            cells)

(defn ^:sub real-hot-props
  [[headers rows context-menu cells selections-coords]]
  (-> hot/real-hot-settings
      (assoc-in [:settings :data] rows)
      (assoc-in [:settings :colHeaders] (display-headers headers))
      (assoc-in [:settings :columns] (column-settings headers))
      (assoc-in [:settings :cells] cells)
      (assoc-in [:settings :contextMenu] context-menu)
      (assoc-in [:selections-coords] selections-coords)))
(rf/reg-sub :table/real-hot-props
            :<- [:table/table-headers]
            :<- [:table/table-rows]
            :<- [:table/context-menu]
            :<- [:table/cells]
            :<- [:table/selections-coords]
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
