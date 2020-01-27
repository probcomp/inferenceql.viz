(ns inferenceql.spreadsheets.subs
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.spreadsheets.db :as db]
            [inferenceql.spreadsheets.views :as views]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.vega :as vega]
            [inferenceql.spreadsheets.modal :as modal]
            [inferenceql.spreadsheets.column-overrides :as co]
            [inferenceql.spreadsheets.renderers :as rends]
            [medley.core :as medley])
  (:require-macros [reagent.ratom :refer [reaction]]))

;;; Specs related to subscriptions for missing cell values

(s/def :ms/values-map-for-row (s/map-of ::db/column-name :ms/value))
(s/def :ms/missing-cells-values (s/coll-of :ms/values-map-for-row))

(rf/reg-sub :scores
            (fn [db _]
              (db/scores db)))

(rf/reg-sub :virtual-scores
            (fn [db _]
              (db/virtual-scores db)))

(rf/reg-sub :labels
            (fn [db _]
              (db/labels db)))

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table-headers table-headers)

(rf/reg-sub :table-last-clicked
            (fn [db _]
              (get db ::db/table-last-clicked)))

(rf/reg-sub :other-table
            (fn [db [_sub-name table-picked]]
              (let [[table-1-id table-2-id] (keys (get db ::db/hot-state))]
                (condp = table-picked
                  table-1-id table-2-id
                  table-2-id table-1-id))))

(rf/reg-sub :table-state
            (fn [db [_sub-name table-id]]
              (get-in db [::db/hot-state table-id])))

(rf/reg-sub :both-table-states
            (fn [db [_sub-name]]
              (get db ::db/hot-state)))

(rf/reg-sub :table-state-active
            (fn [_ _]
              {:table-id (rf/subscribe [:table-last-clicked])
               :table-states (rf/subscribe [:both-table-states])})
            (fn [{:keys [table-id table-states]}]
              (get table-states table-id)))

(rf/reg-sub :computed-headers
            (fn [_ _]
              (rf/subscribe [:table-headers]))
            (fn [headers]
              (into [vega/label-col-header vega/score-col-header] headers)))

(rf/reg-sub :computed-rows
            (fn [_ _]
              {:rows (rf/subscribe [:table-rows])
               :scores (rf/subscribe [:scores])
               :labels (rf/subscribe [:labels])
               :imputed-values (rf/subscribe [:missing-cells-vals-above-thresh])
               :conf-mode (rf/subscribe [:confidence-option [:mode]])})
            (fn [{:keys [rows scores labels imputed-values conf-mode]}]
              (let [merge-imputed (and (= conf-mode :cells-missing)
                                       (seq imputed-values))]
                (cond->> rows
                  merge-imputed (mapv (fn [imputed-values-in-row row]
                                        (merge row imputed-values-in-row))
                                      imputed-values)
                  scores (mapv (fn [score row]
                                 (assoc row vega/score-col-header score))
                               scores)
                  labels (mapv (fn [label row]
                                 (assoc row vega/label-col-header label))
                               labels)))))

(rf/reg-sub :virtual-computed-rows
  (fn [_ _]
    {:rows (rf/subscribe [:virtual-rows])
     :scores (rf/subscribe [:virtual-scores])})
  (fn [{:keys [rows scores]}]
    (let [num-missing-scores (- (count rows) (count scores))
          dummy-scores (repeat num-missing-scores nil)
          scores (concat dummy-scores scores)]

      ;; Creation of dummy scores allows correct attaching of old scores to
      ;; rows even when new rows are generated after a scoring event.
      (mapv (fn [score row] (assoc row vega/score-col-header score))
            scores rows))))

(defn table-rows
  [db _]
  (db/table-rows db))
(rf/reg-sub :table-rows table-rows)

(defn virtual-rows
  [db _]
  (db/virtual-rows db))
(rf/reg-sub :virtual-rows virtual-rows)

(defn- cell-vector
  "Takes tabular data represented as a sequence of maps and reshapes the data as a
  2D vector of cells and a vector of headers."
  [headers rows]
  (into []
        (map (fn [row]
               (into []
                     (map #(get row %))
                     headers)))
        rows))

(defn real-hot-props
  [{:keys [headers rows cells-style-fn context-menu]} _]
  (let [data (cell-vector headers rows)

        num-columns (count headers)
        initial-column-setting {:readOnly false}
        rem-column-settings (repeat (dec num-columns) {})

        all-column-settings (cons initial-column-setting rem-column-settings)]
    (-> views/real-hot-settings
        (assoc-in [:settings :data] data)
        (assoc-in [:settings :colHeaders] headers)
        (assoc-in [:settings :columns] all-column-settings)
        (assoc-in [:settings :cells] cells-style-fn)
        (assoc-in [:settings :contextMenu] context-menu))))
(rf/reg-sub :real-hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:computed-headers])
               :rows    (rf/subscribe [:computed-rows])
               :cells-style-fn (rf/subscribe [:cells-style-fn])
               :context-menu (rf/subscribe [:context-menu])})
            real-hot-props)

(defn virtual-hot-props
  [{:keys [headers rows]} _]
  (let [data (cell-vector headers rows)
        num-columns (count headers)
        column-settings (repeat num-columns {})]
    (-> views/virtual-hot-settings
        (assoc-in [:settings :data] data)
        (assoc-in [:settings :colHeaders] headers)
        (assoc-in [:settings :columns] column-settings))))
(rf/reg-sub :virtual-hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:computed-headers])
               :rows    (rf/subscribe [:virtual-computed-rows])})
            virtual-hot-props)

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
  [{:keys [labels]} _]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(pos-label? (first %)) labels-with-ids)
                 (map second))]
    ids))
(rf/reg-sub :row-ids-labeled-pos
            (fn [_ _]
              {:labels (rf/subscribe [:labels])})
            row-ids-labeled-pos)

(defn row-ids-labeled-neg
  [{:keys [labels]} _]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(neg-label? (first %)) labels-with-ids)
                 (map second))]
    ids))
(rf/reg-sub :row-ids-labeled-neg
            (fn [_ _]
              {:labels (rf/subscribe [:labels])})
            row-ids-labeled-neg)

(defn row-ids-unlabeled
  [{:keys [labels]} _]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(unlabeled? (first %)) labels-with-ids)
                 (map second))]
    ids))
(rf/reg-sub :row-ids-unlabeled
            (fn [_ _]
              {:labels (rf/subscribe [:labels])})
            row-ids-unlabeled)

(defn vega-lite-spec
  [{:keys [table-states t-clicked]}]
  (let [{selections :selections cols :selected-columns row :row-at-selection-start} (table-states t-clicked)
        ;; These are column names that cannot be simulated
        ;; `vega/label-col-header` and `vega/score-col-header` are not part of any dataset.
        ;; And `geo-fips` and `NAME` are columns from the NYTimes dataset that have been excluded.
        invalid-for-sim #{"geo_fips" "NAME" vega/label-col-header vega/score-col-header}]
    (when (first selections)
      (clj->js
       (cond (and (= 1 (count cols))
                  (= 1 (count (first selections)))
                  (not (invalid-for-sim (first cols))))
             (vega/gen-simulate-plot cols row t-clicked)

             (= 1 (count cols))
             (vega/gen-histogram table-states t-clicked)

             (some #{"geo_fips"} cols)
             (vega/gen-choropleth selections cols)

             :else
             (vega/gen-comparison-plot table-states t-clicked))))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:table-states (rf/subscribe [:both-table-states])
               :t-clicked (rf/subscribe [:table-last-clicked])})
            (fn [data-for-spec]
              (vega-lite-spec data-for-spec)))

(rf/reg-sub :one-cell-selected
            (fn [_ _]
              (rf/subscribe [:table-state-active]))
            (fn [{:keys [selections]}]
              (= 1
                 (count selections)
                 (count (first selections))
                 (count (keys (first selections))))))

(rf/reg-sub :vega-lite-log-level
            :<- [:one-cell-selected]
            (fn [one-cell-selected]
              (if one-cell-selected
                (.-Error js/vega)
                (.-Warn js/vega))))

(rf/reg-sub :generator
            (fn [_ _]
              {:selection-info (rf/subscribe [:table-state-active])
               :one-cell-selected (rf/subscribe [:one-cell-selected])
               :override-fns (rf/subscribe [:column-override-fns])})
            (fn [{:keys [selection-info one-cell-selected override-fns]}]
              (let [row (:row-at-selection-start selection-info)
                    columns (:selected-columns selection-info)
                    col-to-sample (first columns)
                    override-map (select-keys override-fns [col-to-sample])
                    override-insert-fn (co/gen-insert-fn override-map)]
                (when (and one-cell-selected
                           ;; TODO clean up this check
                           (not (contains? #{"geo_fips" "NAME" vega/score-col-header vega/label-col-header} col-to-sample)))
                  (let [constraints (mmix/with-row-values {} (-> row
                                                                 (select-keys (keys (:vars model/spec)))
                                                                 (dissoc col-to-sample)))
                        gen-fn #(first (mp/infer-and-score :procedure (search/optimized-row-generator model/spec)
                                                           :observation-trace constraints))
                        has-negative-vals? #(some (every-pred number? neg?) (vals %))]
                    ;; returns the first result of gen-fn that doesn't have a negative salary
                    ;; TODO: (remove negative-vals? ...) is a hack for StrangeLoop2019
                    #(take 1 (map override-insert-fn (remove has-negative-vals? (repeatedly gen-fn)))))))))

(rf/reg-sub :confidence-threshold
            (fn [db _]
              (get db ::db/confidence-threshold)))

(rf/reg-sub :confidence-options
            (fn [db _]
              (get db ::db/confidence-options)))

(rf/reg-sub :confidence-option
            (fn [db [_sub-name path]]
              (get-in db (into [::db/confidence-options] path))))

(rf/reg-sub :query-string
            (fn [db _]
              (get db ::db/query-string)))

(rf/reg-sub
 :context-menu
 (fn [_ _]
   {:col-overrides (rf/subscribe [:column-overrides])
    :col-names (rf/subscribe [:computed-headers])})
 (fn [{:keys [col-overrides col-names]}]
   (let [set-function-fn (fn [key selection click-event]
                           (this-as hot
                             (let [last-col-num (.. (first selection) -start -col)
                                   last-col-num-phys (.toPhysicalColumn hot last-col-num)
                                   col-name (nth col-names last-col-num-phys)
                                   fn-text (get col-overrides col-name)

                                   modal-child [modal/js-function-entry-modal col-name fn-text]]
                               (rf/dispatch [:set-modal {:child modal-child}]))))

         clear-function-fn (fn [key selection click-event]
                             (this-as hot
                               (let [last-col-num (.. (first selection) -start -col)
                                     last-col-num-phys (.toPhysicalColumn hot last-col-num)
                                     col-name (nth col-names last-col-num-phys)]
                                 (rf/dispatch [:clear-column-function col-name]))))

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

(rf/reg-sub :modal
            (fn [db _]
              (::db/modal db)))

(rf/reg-sub :column-override-fns
            (fn [db _]
              (get db ::db/column-override-fns)))

(rf/reg-sub :column-overrides
            (fn [db _]
              (get db ::db/column-overrides)))

(rf/reg-sub
  :row-likelihoods-normed
  (fn [db _]
    (get db ::db/row-likelihoods)))

(rf/reg-sub
  ;; Returns values and scores of missing-cells.
  :missing-cells
  (fn [db _]
    (get db ::db/missing-cells)))

(rf/reg-sub
 :missing-cells-flagged
 ;; This is like the :missing-cells sub, but it includes an extra flag for whether the
 ;; imputed value for each cell meets the set confidence threshold.
 :<- [:missing-cells]
 :<- [:confidence-threshold]
 (fn [[missing-cells confidence-threshold] _]
   ;; validate output using spec
   {:post [(s/valid? ::db/missing-cells %)]}
   (let [add-threshold-flags (fn [val-and-score-map]
                               (let [meets-threshold (>= (:score val-and-score-map) confidence-threshold)]
                                 (assoc val-and-score-map :meets-threshold meets-threshold)))
         ;; Missing cell info with :meets-threshold flags added.
         with-flags (map (fn [row] (medley/map-vals add-threshold-flags row))
                         missing-cells)]
     (vec with-flags))))

(rf/reg-sub
 :missing-cells-vals-above-thresh
 ;; This is like the :missing-cells sub, but it only includes cells that have meet the confidence
 ;; threshold. And it only has missing cells values, no score info.
 :<- [:missing-cells-flagged]
 (fn [missing-cells-flagged _]
   ;; validate output using spec
   {:post [(s/valid? :ms/missing-cells-values %)]}
   (for [row missing-cells-flagged]
     (->> row
          (medley/filter-vals :meets-threshold)
          (medley/map-vals :value)))))

(rf/reg-sub
 :cells-style-fn
 (fn [_ _]
   {:cell-renderer-fn (rf/subscribe [:cell-renderer-fn])})
 (fn [{:keys [cell-renderer-fn]}]
   ;; Returns a function used by the :cells property in Handsontable's options.
   (fn [row col]
     (clj->js {:renderer cell-renderer-fn}))))

(rf/reg-sub
 :cell-renderer-fn
 (fn [_ _]
   {:row-likelihoods (rf/subscribe [:row-likelihoods-normed])
    :missing-cells-flagged (rf/subscribe [:missing-cells-flagged])
    :conf-thresh (rf/subscribe [:confidence-threshold])
    :conf-mode (rf/subscribe [:confidence-option [:mode]])
    :computed-headers (rf/subscribe [:computed-headers])})
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
