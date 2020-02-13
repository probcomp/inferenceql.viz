(ns inferenceql.spreadsheets.panels.table.subs
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.table.renderers :as rends]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.override.views :as modal]))

;;; Subs related to entries in the user-editable labels column within the real-data table.

(def clean-label
  "Prepares the user-typed label for checking."
  (fnil (comp str/upper-case str/trim) ""))

(defn- pos-label? [label-str]
  (let [f (clean-label label-str)]
    ;; TODO: add more truthy values
    (or (= f "TRUE")
        (= f "1"))))

(defn- neg-label? [label-str]
  (let [f (clean-label label-str)]
    ;; TODO: add more falsey values
    (or (= f "FALSE")
        (= f "0"))))

(defn- unlabeled? [label-str]
  (and (not (pos-label? label-str))
       (not (neg-label? label-str))))

(defn row-ids-labeled-pos
  [labels]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(pos-label? (first %)) labels-with-ids)
                 (map second))]
    ids))

(defn row-ids-labeled-neg
  [labels]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(neg-label? (first %)) labels-with-ids)
                 (map second))]
    ids))

(defn row-ids-unlabeled
  [labels]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(unlabeled? (first %)) labels-with-ids)
                 (map second))]
    ids))

(rf/reg-sub
 :table/labels
 (fn [db _]
   (db/labels db)))

(rf/reg-sub
 :table/rows-label-info
 :<- [:table/labels]
 (fn [labels _]
   {:pos-ids (row-ids-labeled-pos labels)
    :neg-ids (row-ids-labeled-neg labels)
    :unlabeled-ids (row-ids-unlabeled labels)}))

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

(rf/reg-sub :table/one-cell-selected
            (fn [_ _]
              (rf/subscribe [:table/selection-layer-active]))
            (fn [{:keys [selections]}]
              (= 1
                 (count selections) ; One row selected.
                 (count (keys (first selections)))))) ; One column selected.

(rf/reg-sub :table/selection-layers
            (fn [db [_sub-name]]
              (get-in db [:table-panel :selection-layers])))

(rf/reg-sub :table/selection-layers-list
            :<- [:table/selection-layers]
            (fn [selection-layers]
              (let [layers-list (for [layer-name selection-layer-order]
                                  (let [layer (get selection-layers layer-name)]
                                    ;; Only add the selection layer to the list if there is
                                    ;; a valid selection in it.
                                    (when (some? (get layer :selections))
                                      ;; Add in the name of the selection layer into the selection
                                      ;; layer map itself because the layers will be embedded in a
                                      ;; list, and no longer have keys (their names) associated with
                                      ;; them.
                                      (assoc layer :id layer-name))))]
                (remove nil? layers-list))))

;;; Subs related to selections within the active selection layer.

(rf/reg-sub :table/selection-layer-active
            :<- [:control/selection-color]
            :<- [:table/selection-layers]
            (fn [[color selection-layers]]
              (get selection-layers color)))

(rf/reg-sub :table/selections
            :<- [:table/selection-layer-active]
            (fn [selection-state]
              (get selection-state :selections)))

(rf/reg-sub :table/selected-columns
            :<- [:table/selection-layer-active]
            (fn [selection-state]
              (get selection-state :selected-columns)))

(rf/reg-sub :table/row-at-selection-start
            :<- [:table/selection-layer-active]
            (fn [selection-state]
              (get selection-state :row-at-selection-start)))

(rf/reg-sub :table/selections-coords
            :<- [:table/selection-layer-active]
            (fn [selection-state]
              (get selection-state :coords)))

;;; Subs related to scores computed on rows in the tables.

(rf/reg-sub :table/scores
            (fn [db _]
              (db/scores db)))

;;; Subs related to populating tables with data.

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table/table-headers table-headers)

(rf/reg-sub :table/computed-headers
            (fn [_ _]
              (rf/subscribe [:table/table-headers]))
            (fn [headers]
              (into [hot/label-col-header hot/score-col-header] headers)))

(rf/reg-sub :table/computed-rows
            (fn [_ _]
              {:rows (rf/subscribe [:table/table-rows])
               :scores (rf/subscribe [:table/scores])
               :labels (rf/subscribe [:table/labels])
               :imputed-values (rf/subscribe [:highlight/missing-cells-vals-above-thresh])
               :conf-mode (rf/subscribe [:control/reagent-form [:confidence-mode]])})
            (fn [{:keys [rows scores labels imputed-values conf-mode]}]
              (let [merge-imputed (and (= conf-mode :cells-missing)
                                       (seq imputed-values))]
                (cond->> rows
                  merge-imputed (mapv (fn [imputed-values-in-row row]
                                        (merge row imputed-values-in-row))
                                      imputed-values)
                  scores (mapv (fn [score row]
                                 (assoc row hot/score-col-header score))
                               scores)
                  labels (mapv (fn [label row]
                                 (assoc row hot/label-col-header label))
                               labels)))))

(defn table-rows
  [db _]
  (db/table-rows db))
(rf/reg-sub :table/table-rows table-rows)

(defn- column-settings [headers]
  "Returns an array of objects that define settings for each column
  in the table including which attribute from the underlying map for the row
  is presented."
  (let [settings-map (fn [attr]
                       (if (= attr hot/label-col-header)
                         {:data attr :readOnly false} ; Make the score column user-editable.
                         {:data attr}))]
    (map settings-map headers)))

;;; Subs related to settings and overall state of tables.

(defn real-hot-props
  [{:keys [headers rows cells-style-fn context-menu selections-coords selection-color]} _]
  (-> hot/real-hot-settings
      (assoc-in [:settings :data] rows)
      (assoc-in [:settings :colHeaders] headers)
      (assoc-in [:settings :columns] (column-settings headers))
      (assoc-in [:settings :cells] cells-style-fn)
      (assoc-in [:settings :contextMenu] context-menu)
      (assoc-in [:selections-coords] selections-coords)
      (assoc-in [:selection-color] selection-color)))
(rf/reg-sub :table/real-hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:table/computed-headers])
               :rows    (rf/subscribe [:table/computed-rows])
               :cells-style-fn (rf/subscribe [:table/cells-style-fn])
               :context-menu (rf/subscribe [:table/context-menu])
               :selections-coords (rf/subscribe [:table/selections-coords])
               :selection-color (rf/subscribe [:control/selection-color])})
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