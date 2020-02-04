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

;;; Subs related to selections within tables.

(rf/reg-sub :table/one-cell-selected
            (fn [_ _]
              (rf/subscribe [:table/table-state-active]))
            (fn [{:keys [selections]}]
              (= 1
                 (count selections)
                 (count (first selections))
                 (count (keys (first selections))))))

(rf/reg-sub :table/table-last-clicked
            (fn [db _]
              (get-in db [:table-panel :table-last-clicked])))

(rf/reg-sub :table/both-table-states
            (fn [db [_sub-name]]
              (get-in db [:table-panel :hot-state])))

(rf/reg-sub :table/table-state-active
            (fn [_ _]
              {:table-id (rf/subscribe [:table/table-last-clicked])
               :table-states (rf/subscribe [:table/both-table-states])})
            (fn [{:keys [table-id table-states]}]
              (get table-states table-id)))

(rf/reg-sub :table/selections
            :<- [:table/table-state-active]
            (fn [table-state]
              (get table-state :selections)))

(rf/reg-sub :table/selected-columns
            :<- [:table/table-state-active]
            (fn [table-state]
              (get table-state :selected-columns)))

(rf/reg-sub :table/row-at-selection-start
            :<- [:table/table-state-active]
            (fn [table-state]
              (get table-state :row-at-selection-start)))

;;; Subs related to scores computed on rows in the tables.

(rf/reg-sub :table/scores
            (fn [db _]
              (db/scores db)))

(rf/reg-sub :table/virtual-scores
            (fn [db _]
              (db/virtual-scores db)))

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
               :conf-mode (rf/subscribe [:control/confidence-option [:mode]])})
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

(rf/reg-sub :table/virtual-computed-rows
  (fn [_ _]
    {:rows (rf/subscribe [:table/virtual-rows])
     :scores (rf/subscribe [:table/virtual-scores])})
  (fn [{:keys [rows scores]}]
    (let [num-missing-scores (- (count rows) (count scores))
          dummy-scores (repeat num-missing-scores nil)
          scores (concat dummy-scores scores)]

      ;; Creation of dummy scores allows correct attaching of old scores to
      ;; rows even when new rows are generated after a scoring event.
      (mapv (fn [score row] (assoc row hot/score-col-header score))
            scores rows))))

(defn table-rows
  [db _]
  (db/table-rows db))
(rf/reg-sub :table/table-rows table-rows)

(defn virtual-rows
  [db _]
  (db/virtual-rows db))
(rf/reg-sub :table/virtual-rows virtual-rows)

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
  [{:keys [headers rows cells-style-fn context-menu]} _]
  (-> hot/real-hot-settings
      (assoc-in [:settings :data] rows)
      (assoc-in [:settings :colHeaders] headers)
      (assoc-in [:settings :columns] (column-settings headers))
      (assoc-in [:settings :cells] cells-style-fn)
      (assoc-in [:settings :contextMenu] context-menu)))
(rf/reg-sub :table/real-hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:table/computed-headers])
               :rows    (rf/subscribe [:table/computed-rows])
               :cells-style-fn (rf/subscribe [:table/cells-style-fn])
               :context-menu (rf/subscribe [:table/context-menu])})
            real-hot-props)

(defn virtual-hot-props
  [{:keys [headers rows]} _]
  (-> hot/virtual-hot-settings
      (assoc-in [:settings :data] rows)
      (assoc-in [:settings :colHeaders] headers)
      (assoc-in [:settings :columns] (column-settings headers))))
(rf/reg-sub :table/virtual-hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:table/computed-headers])
               :rows    (rf/subscribe [:table/virtual-computed-rows])})
            virtual-hot-props)

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
    :conf-mode (rf/subscribe [:control/confidence-option [:mode]])
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
