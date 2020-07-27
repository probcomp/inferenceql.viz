(ns inferenceql.spreadsheets.panels.table.subs
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.spreadsheets.panels.table.renderers :as rends]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.override.views :as modal]
            [inferenceql.spreadsheets.panels.table.eventsupport.before-change :as es.before-change]))

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

(rf/reg-sub :table/selection-layers
            :<- [:table/selection-layer-coords]
            :<- [:table/visual-headers]
            :<- [:table/visual-display-rows]
            (fn [[selection-layers-raw visual-headers visual-display-rows]]
              (medley/map-vals #(add-selection-data % visual-headers visual-display-rows)
                               selection-layers-raw)))

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

(rf/reg-sub :table/physical-headers
            (fn [db _]
              (db/physical-headers db)))

(rf/reg-sub :table/physical-rows-by-id
            (fn [db _]
              (db/physical-rows-by-id db)))

(rf/reg-sub :table/physical-row-order
            (fn [db _]
              (db/physical-row-order db)))

(rf/reg-sub :table/physical-display-rows
            :<- [:table/physical-rows-by-id]
            :<- [:table/physical-row-order]
            (fn [[rows rows-order]]
              (map rows rows-order)))

(rf/reg-sub :table/computed-headers
            :<- [:table/physical-headers]
            :<- [:table/label-column-show]
            (fn [[headers label-column-show]]
              (when headers
                (cond->> headers
                  label-column-show (concat [hot/label-col-header])))))

(rf/reg-sub :table/selected-row-flags
            :<- [:table/physical-rows-by-id-with-changes]
            :<- [:viz/pts-store-filter]
            (fn [[rows-by-id pts-store-filter]]
              (when pts-store-filter
                (medley/map-vals (fn [row] (pts-store-filter row))
                                 rows-by-id))))

(rf/reg-sub :table/computed-rows
            (fn [_ _]
              {:rows (rf/subscribe [:table/physical-display-rows])
               :imputed-values (rf/subscribe [:highlight/missing-cells-vals-above-thresh])
               :conf-mode (rf/subscribe [:control/reagent-form [:confidence-mode]])})
            (fn [{:keys [rows imputed-values conf-mode]}]
              (let [merge-imputed (and (= conf-mode :cells-missing)
                                       (seq imputed-values))]
                (cond->> rows
                  merge-imputed (mapv (fn [imputed-values-in-row row]
                                        (merge row imputed-values-in-row))
                                      imputed-values)))))

(defn- column-settings [headers]
  "Returns an array of objects that define settings for each column
  in the table including which attribute from the underlying map for the row
  is presented."
  (let [settings-map (fn [attr]
                       (if (= attr hot/label-col-header)
                         ;; Grabs the correct data for the label column and makes it user-editable.
                         {:data (name :inferenceql.viz.row/label__) :readOnly false}
                         {:data attr}))]
    (map settings-map headers)))

;;; Sub for getting the instance of Handsontable used within the Reagent component
;;; representing the table.

(rf/reg-sub :table/hot-instance
            (fn [db _]
              (get-in db [:table-panel :hot-instance])))

;;; Subs related to data as it appears to the user given column moves and column sorting performed
;;; by the user in Handsontable.

(rf/reg-sub :table/visual-headers
            (fn [db _]
              (db/visual-headers db)))

(rf/reg-sub :table/visual-row-order
            (fn [db _]
              (db/visual-row-order db)))

(rf/reg-sub :table/physical-rows-by-id-with-changes
            (fn [db _]
              (db/physical-row-by-id-with-changes db)))

(rf/reg-sub :table/visual-display-rows
            :<- [:table/visual-row-order]
            :<- [:table/physical-rows-by-id-with-changes]
            (fn [[rows-order rows-by-id]]
              (mapv rows-by-id rows-order)))

(rf/reg-sub :table/sort-state
            (fn [db _]
              (get-in db [:table-panel :sort-state])))

;;; Subs related showing/hiding certain columns or table controls.

(rf/reg-sub :table/label-column-show
            (fn [db _]
              (get-in db [:table-panel :label-column-show])))

(rf/reg-sub :table/show-table-controls
            :<- [:table/physical-row-order]
            ;; Returns value for css visibility property.
            (fn [rows-order]
              (if (seq rows-order)
                "visible"
                "hidden")))

;;; Subs related to settings and overall state of tables.

(defn ^:sub cells
  "Returns a function used by the :cells property in Handsontable's options."
  [selected-row-flags]
  (fn [row col prop]
    (this-as obj
      (let [hot (.-instance obj)
            ;; NOTE: The documentations claims that the cells function in called with physical cell coordinates for
            ;; `row` and `col` but that does not seem to be the case. There might be some unusual calls made when
            ;; sorting is done in table. Nevertheless, treating `row` and `col` as visual indices seems to work ok.
            row-id (.getDataAtRowProp hot row (name :inferenceql.viz.row/id__))

            user-added (.getDataAtRowProp hot row (name :inferenceql.viz.row/user-added-row__))
            label-column-cell (= prop (name :inferenceql.viz.row/label__))
            selected (get selected-row-flags row-id)

            classes-to-add (remove nil? [(when user-added "editable-cell")
                                         (when label-column-cell "label-cell")
                                         (when selected "selected-row")])
            class-str (str/join " " classes-to-add)]

        (when (seq classes-to-add)
          (.setCellMeta hot row col "className" class-str))
        (when (or user-added label-column-cell)
          (.setCellMeta hot row col "readOnly" false))))))

(rf/reg-sub :table/cells
            :<- [:table/selected-row-flags]
            cells)

(defn ^:sub real-hot-props
  [[headers rows context-menu cells selections-coords sort-state]]
  (-> hot/real-hot-settings
      (assoc-in [:settings :data] rows)
      (assoc-in [:settings :colHeaders] headers)
      (assoc-in [:settings :columns] (column-settings headers))
      (assoc-in [:settings :cells] cells)
      (assoc-in [:settings :contextMenu] context-menu)
      (assoc-in [:selections-coords] selections-coords)
      (assoc-in [:sort-state] sort-state)))
(rf/reg-sub :table/real-hot-props
            :<- [:table/computed-headers]
            :<- [:table/computed-rows]
            :<- [:table/context-menu]
            :<- [:table/cells]
            :<- [:table/selections-coords]
            :<- [:table/sort-state]
            real-hot-props)

(rf/reg-sub
 :table/context-menu
 (fn [_ _]
   {:col-overrides (rf/subscribe [:override/column-overrides])
    :col-names (rf/subscribe [:table/computed-headers])})
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
    :computed-headers (rf/subscribe [:table/computed-headers])})
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
