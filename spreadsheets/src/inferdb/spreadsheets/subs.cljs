(ns inferdb.spreadsheets.subs
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [metaprob.distributions :as dist]
            [metaprob.prelude :as mp]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.search :as search]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.vega :as vega]
            [inferdb.spreadsheets.modal :as modal]
            [inferdb.spreadsheets.utils :as utils])
  (:require-macros [reagent.ratom :refer [reaction]]))

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
              (get db :table-last-clicked)))

(rf/reg-sub :other-table
            (fn [db [_sub-name table-picked]]
              (let [[table-1-id table-2-id] (keys (get db :hot-state))]
                (condp = table-picked
                  table-1-id table-2-id
                  table-2-id table-1-id))))

(rf/reg-sub :table-state
            (fn [db [_sub-name table-id]]
              (get-in db [:hot-state table-id])))

(rf/reg-sub :both-table-states
            (fn [db [_sub-name]]
              (get db :hot-state)))

(rf/reg-sub-raw :table-state-active
                (fn [app-db event]
                  (reaction
                    (let [table-id @(rf/subscribe [:table-last-clicked])
                          table-state @(rf/subscribe [:table-state table-id])]
                      table-state))))

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
               :imputed-values (rf/subscribe [:missing-cells-values-above-conf-threshold])
               :conf-mode (rf/subscribe [:confidence-option [:mode]])})
            (fn [{:keys [rows scores labels imputed-values conf-mode]}]
              (let [merge-imputed (= conf-mode :cells-missing)]
                (cond->> (seq rows)
                  ;; Merge in the missing values.
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
  (let [{selections :selections cols :selected-columns row :row-at-selection-start}
        (table-states t-clicked)]
    (when (first selections)
      (clj->js
       (cond (and (= 1 (count cols))
                  (= 1 (count (first selections)))
                  (not (contains? #{"geo_fips" "NAME" vega/label-col-header vega/score-col-header}
                                  (first cols))))
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
                    override-insert-fn (utils/gen-insert-fn override-map)]
                (when (and one-cell-selected
                           ;; TODO clean up this check
                           (not (contains? #{"geo_fips" "NAME" vega/score-col-header vega/label-col-header} col-to-sample)))
                  (let [constraints (mmix/with-row-values {} (-> row
                                                                 (select-keys (keys (:vars model/spec)))
                                                                 (dissoc col-to-sample)))
                        gen-fn #(first (mp/infer-and-score :procedure (search/optimized-row-generator model/spec)
                                                           :observation-trace constraints))
                        negative-salary? #(neg? (% "salary_usd"))]
                    ;; returns the first result of gen-fn that doesn't have a negative salary
                    ;; TODO: This is dataset-specific
                    #(take 1 (map override-insert-fn (remove negative-salary? (repeatedly gen-fn)))))))))

;;; The following subs are for getting information on the likelihood of
;;; prexisting data or imputing missing data and getting its likelihood as well.

(rf/reg-sub
 :row-likelihoods
 (fn [_ _]
   {:table-rows (rf/subscribe [:table-rows])})
 (fn [{:keys [table-rows]}]
   (let [likelihoods (search/row-likelihoods model/spec table-rows)]
     likelihoods)))

(rf/reg-sub
  :row-likelihoods-normed
  (fn [_ _]
    {:row-likelihoods (rf/subscribe [:row-likelihoods])})
  (fn [{:keys [row-likelihoods]}]
    (let [min-val (apply min row-likelihoods)
          max-val (apply max row-likelihoods)
          scale-factor (Math/abs (- max-val min-val))]
      (->> row-likelihoods
        (map #(- % min-val))
        (map #(/ % scale-factor))))))

(rf/reg-sub
 :cell-likelihoods
 (fn [_ _]
   {:table-rows (rf/subscribe [:table-rows])})
 (fn [{:keys [table-rows]}]
   (let [likelihoods (search/cell-likelihoods model/spec table-rows)]
     likelihoods)))

(rf/reg-sub
 :missing-cells
 (fn [_ _]
   {:table-rows (rf/subscribe [:table-rows])
    :headers (rf/subscribe [:table-headers])})
 (fn [{:keys [table-rows headers]}]
   (search/impute-missing-cells model/spec headers table-rows)))

(rf/reg-sub
 :missing-cells-values
 (fn [_ _]
   {:missing-cells (rf/subscribe [:missing-cells])})
 (fn [{:keys [missing-cells]}]
   (map :values missing-cells)))

(rf/reg-sub
 :missing-cells-values-above-conf-threshold
 (fn [_ _]
   {:values (rf/subscribe [:missing-cells-values])
    :likelihoods (rf/subscribe [:missing-cells-likelihoods-normed])
    :conf-threshold (rf/subscribe [:confidence-threshold])})
 (fn [{:keys [values likelihoods conf-threshold]}]
   (for [[row-values row-likelihoods] (map vector values likelihoods)]
     (->> (map (fn [[val-k val-v] [like-k like-v]]
                 (if (>= like-v conf-threshold)
                   [val-k val-v]
                   nil))
               row-values row-likelihoods)
          (remove nil?)
          (into {})))))

(rf/reg-sub
 :missing-cells-likelihoods
 (fn [_ _]
   {:missing-cells (rf/subscribe [:missing-cells])})
 (fn [{:keys [missing-cells]}]
   (map :scores missing-cells)))

(rf/reg-sub
  :missing-cells-likelihoods-normed
  (fn [_ _]
    {:missing-cells-likelihoods (rf/subscribe [:missing-cells-likelihoods])})
  (fn [{:keys [missing-cells-likelihoods]}]
    (let [all-likelihoods (remove nil? (flatten (map vals missing-cells-likelihoods)))
          min-val (apply min all-likelihoods)
          max-val (apply max all-likelihoods)
          scale-factor (Math/abs (- max-val min-val))]
      ;; Each iteration of this for loop handles
      ;; a map of missing values for a row
      (for [row missing-cells-likelihoods]
        (->> row
          (map (fn [[k v]] [k (- v min-val)]))
          (map (fn [[k v]] [k (/ v scale-factor)]))
          (into {}))))))

(rf/reg-sub :confidence-threshold
            (fn [db _]
              (get db ::db/confidence-threshold)))

(rf/reg-sub :confidence-options
            (fn [db _]
              (get db ::db/confidence-options)))

(rf/reg-sub :confidence-option
            (fn [db [_sub-name path]]
              (get-in db (into [::db/confidence-options] path))))

(defn row-wise-likelihood-threshold-renderer [renderer-args row-likelihoods conf-thresh]
  (let [renderer-args-js (clj->js renderer-args)
        [hot td row _col _prop _value _cell-properties] renderer-args

        ; Using physical coords makes rendering resilient to sorting the table.
        row (.toPhysicalRow hot row)

        td-style (.-style td)
        text-render-fn js/Handsontable.renderers.TextRenderer

        likelihood-for-row (nth row-likelihoods row)
        row-above-thresh (>= likelihood-for-row conf-thresh)]

    ;; Performs standard rendering of text in cell
    (this-as this
      (.apply text-render-fn this renderer-args-js))

    (when row-above-thresh
      (set! (.-background td-style) "#CEC"))))

;; TODO: Write this
(defn row-wise-likelihood-gradient-renderer [renderer-args row-likelihoods])

;; TODO: Avoid the extra check on confidences. Simply compare key presence in
;; missing-cells-values and missing-cells-values-above-conf-threshold.
(defn missing-cell-wise-likelihood-threshold-renderer [renderer-args missing-cells-likelihoods computed-headers conf-thresh]
  (let [renderer-args-js (clj->js renderer-args)
        [hot td row col _prop _value _cell-properties] renderer-args

        ; Using physical coords makes rendering resilient to sorting the table.
        row (.toPhysicalRow hot row)
        col (.toPhysicalColumn hot col)

        td-style (.-style td)
        text-render-fn js/Handsontable.renderers.TextRenderer

        prop-name-of-cell (nth computed-headers col)
        likelihoods-for-row (nth missing-cells-likelihoods row)
        likelihood-for-cell (get likelihoods-for-row prop-name-of-cell)]

    ;; Performs standard rendering of text in cell
    (this-as this
      (.apply text-render-fn this renderer-args-js))

    (when likelihood-for-cell
      (let [cell-above-thresh (>= likelihood-for-cell conf-thresh)]
        (if cell-above-thresh
          (set! (.-background td-style) "#CEC")
          (set! (.-background td-style) "#DDD"))))))

(rf/reg-sub
 :cells-style-fn
 (fn [_ _]
   {:cell-renderer-fn (rf/subscribe [:cell-renderer-fn])})
 (fn [{:keys [cell-renderer-fn]}]
   ;; Returns a function used by the :cells option for Handsontable.
   (fn [row col]
     (let [cell-properties {:renderer cell-renderer-fn}]
       (clj->js cell-properties)))))

;; TODO: make this sub not require likelihoods for everything.
;; Do it conditionally base on conf-mode.
(rf/reg-sub
 :cell-renderer-fn
 (fn [_ _]
   {:row-likelihoods (rf/subscribe [:row-likelihoods-normed])
    :missing-cells-likelihoods (rf/subscribe [:missing-cells-likelihoods-normed])
    :conf-thresh (rf/subscribe [:confidence-threshold])
    :conf-mode (rf/subscribe [:confidence-option [:mode]])
    :computed-headers (rf/subscribe [:computed-headers])})
 ;; Returns a cell renderer function used by Handsontable.
 (fn [{:keys [row-likelihoods missing-cells-likelihoods conf-thresh conf-mode computed-headers]}]
   (case conf-mode
     :none
     js/Handsontable.renderers.TextRenderer

     :row
     (fn [& args]
       ;; These render functions are actually called with this args list:
       ;; [hot td row col prop value cell-properties]
       ;; Instead, we are specifying [& args] here to make it cleaner to
       ;; pass in data to custom rendering functions.
       (row-wise-likelihood-threshold-renderer args row-likelihoods conf-thresh))

     :cells-existing
     js/Handsontable.renderers.TextRenderer

     :cells-missing
     (fn [& args]
       (missing-cell-wise-likelihood-threshold-renderer args missing-cells-likelihoods computed-headers conf-thresh)))))

(rf/reg-sub
 :context-menu
 (fn [_ _]
   {})
 (fn []
   (let [set-function-fn (fn [key selection click-event]
                           (this-as hot
                             (let [last-col-num (.. (first selection) -start -col)
                                   last-col-num-phys (.toPhysicalColumn hot last-col-num)]
                               (rf/dispatch [:modal {:show? true
                                                     :child [modal/function-entry last-col-num-phys]}]))))

         clear-function-fn (fn [key selection click-event]
                             ;; no-op
                             (+ 1 1))
         disable-fn (fn []
                     (this-as this
                       ;; TODO: only enable options on column headers >= 2
                       false))]
     {:items {"set_function" {:disabled disable-fn
                              :name "Set js function"
                              :callback set-function-fn}
              "clear_function" {:disabled disable-fn
                                :name "Clear js function"
                                :callback clear-function-fn}}})))

(rf/reg-sub-raw
 :modal
 (fn [db _] (reaction (:modal @db))))

(rf/reg-sub :column-override-fns
            (fn [db _]
              (get db ::db/column-override-fns)))

(rf/reg-sub :column-overrides
            (fn [db _]
              (get db ::db/column-overrides)))
