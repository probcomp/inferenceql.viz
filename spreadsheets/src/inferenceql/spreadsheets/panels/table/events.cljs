(ns inferenceql.spreadsheets.panels.table.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.panels.control.db :as control-db]
            [inferenceql.spreadsheets.util :as util]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm.multimixture.specification :as mm.spec]
            [medley.core :as medley]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [goog.string :as gstring]
            [inferenceql.spreadsheets.data :as data]))

;;; Events that do not correspond to hooks in the Handsontable api.

(rf/reg-event-db
 :table/set
 event-interceptors
 ;; `rows` and `headers` are required arguments essentially, and
 ;; `scores`, 'labels`, and 'virtual' are optional, and they are meant
 ;; to be passed in a map.
 (fn [db [_ rows headers {:keys [virtual]}]]
   (let [rows-order (mapv :inferenceql.viz.row/id__ rows)
         rows-maps (zipmap rows-order rows)

         ;; Casts a value to a vec if it is not nil.
         vec-maybe #(some-> % vec)
         ;; Remove special-columns from headers
         headers (remove #{:inferenceql.viz.row/user-added-row__
                           :inferenceql.viz.row/id__
                           :inferenceql.viz.row/label__}
                         headers)]
     (-> db
         (assoc-in [:table-panel :physical-data :rows-by-id] rows-maps)
         (assoc-in [:table-panel :physical-data :row-order] rows-order)
         (assoc-in [:table-panel :physical-data :headers] (vec-maybe headers))
         (util/assoc-or-dissoc-in [:table-panel :physical-data :virtual] virtual)

         ;; Clear all selections in all selection layers.
         (assoc-in [:table-panel :selection-layers] {})))))

(rf/reg-event-db
 :table/clear
 event-interceptors
 (fn [db [_]]
   (-> db
       (update-in [:table-panel] dissoc :physical-data)
       (assoc-in [:table-panel :selection-layers] {}))))

;; Checks if the selection in the current selection layer is valid.
;; If it is not valid, the current selection layer is cleared.
(rf/reg-event-db
 :table/check-selection
 event-interceptors
 (fn [db _]
   (let [color (control-db/selection-color db)
         selections-coords (get-in db [:table-panel :selection-layers color :coords])

         num-rows (count (db/visual-rows db))
         ;; Takes a selection vector and returns true if that selection represents
         ;; the selection of a single column.
         column-selected (fn [[row-start col-start row-end col-end]]
                           (let [last-row-index (- num-rows 1)]
                             (and (= row-start 0)
                                  (= row-end last-row-index)
                                  (= col-start col-end))))]
     (cond
       ;; These next two cond sections sometimes deselect all cells in the selection layer in order
       ;; to enforce our constraints on what sorts of selections are allowed in each selection layer.

       ;; Deselect all cells in the current selection layer if it is made up of two selections that
       ;; are not both single column selections.
       (and (= (count selections-coords) 2)
            (not-every? column-selected selections-coords))
       (update-in db [:table-panel :selection-layers] dissoc color)

       ;; Deselect all cells in the current selection layer if it is made up of more than
       ;; two selections.
       (> (count selections-coords) 2)
       (update-in db [:table-panel :selection-layers] dissoc color)

       :else
       db))))

(defn shift-sort
  "Shift the column numbers in `sort-config` by `amount`."
  [sort-config amount]
  (when sort-config
    (map #(update % :column + amount) sort-config)))

(defn shift-selections
  "Shift the column numbers in layers in `selection-layers` by `amount`."
  [selection-layers amount]
  (let [shift-cols (fn [[r1 c1 r2 c2]]
                     [r1 (+ c1 amount) r2 (+ c2 amount)])
        shift-layer (fn [layer-map]
                      (-> layer-map
                        (update :coords (partial mapv shift-cols))
                        (update :coords-physical (partial mapv shift-cols))))]
    (medley/map-vals shift-layer selection-layers)))

(defn adjust-headers
  "Adds or removes a dummy column to the front of `headers` depending on `amount`."
  [headers amount]
  (case amount
    1 (into [:dummy-col] headers)
    -1 (subvec headers 1)))

(rf/reg-event-db
  :table/toggle-label-column
  event-interceptors
  (fn [db [_]]
    (let [new-state (not (get-in db [:table-panel :label-column-show]))
          shift-amount (if new-state 1 -1)]
      (-> db
          (assoc-in [:table-panel :label-column-show] new-state)
          (update-in [:table-panel :sort-state] shift-sort shift-amount)
          (update-in [:table-panel :selection-layers] shift-selections shift-amount)
          ;; Hack: This prevents a flicker in visualizations.

          (update-in [:table-panel :visual-data :headers] adjust-headers shift-amount)))))

(rf/reg-event-db
  :table/add-row
  event-interceptors
  (fn [db [_]]
    (let [new-row-id (data/generate-row-id)
          color (control-db/selection-color db)

          new-row-num (count (get-in db [:table-panel :physical-data :row-order]))
          ;; Selects the first cell of the new row we are adding.
          new-selection [[new-row-num 0 new-row-num 0]]

          new-row {:inferenceql.viz.row/user-added-row__ true
                   :inferenceql.viz.row/id__             new-row-id}]
      (-> db
          ;; Update the currently displayed data in the table.
          (update-in [:table-panel :physical-data :rows-by-id] assoc new-row-id new-row)
          (update-in [:table-panel :physical-data :row-order] conj new-row-id)
          ;; Update the original dataset.
          (update-in [:table-panel :dataset :rows-by-id] assoc new-row-id new-row)
          (update-in [:table-panel :dataset :row-order] conj new-row-id)

          ;; TODO: scroll the viewport to this cell.
          (assoc-in [:table-panel :selection-layers color :coords] new-selection)))))

(rf/reg-event-db
  :table/delete-row
  event-interceptors
  (fn [db [_]]
    (let [color (control-db/selection-color db)
          selections-coords (get-in db [:table-panel :selection-layers color :coords-physical])
          [r1 _c1 r2 _c2] (first selections-coords)

          row-id (get-in db [:table-panel :physical-data :row-order r1])
          user-row-ids (db/user-added-row-ids db)]
      ;; Only remove a row when there is only one selection rectangle and
      ;; it is set on a single row. The number of columns spanned does not matter.
      ;; The selected row must also be a user-added row.
      (if (and (= 1 (count selections-coords))
               (= r1 r2)
               (contains? user-row-ids row-id))
        (-> db
            ;; Update the currently displayed data in the table.
            (update-in [:table-panel :physical-data :rows-by-id] dissoc row-id)
            (update-in [:table-panel :physical-data :row-order] (comp vec (partial remove #{row-id})))
            ;; Update the original dataset.
            (update-in [:table-panel :dataset :rows-by-id] dissoc row-id)
            (update-in [:table-panel :dataset :row-order] (comp vec (partial remove #{row-id})))

            ;; Update the selection to the first cell in the row before the deleted row.
            (assoc-in [:table-panel :selection-layers color :coords] [[(dec r1) 0 (dec r1) 0]]))
        db))))

;;; Events that correspond to hooks in the Handsontable API

(defn merge-row-updates
  "Merges `updates` into `rows`.
  Both `updates` and `rows` are a maps where keys are row-ids and vals are rows
  (or row updates) in the case of `updates`."
  [rows updates]
  (let [merged-rows (merge-with merge rows updates)

        ;; Updates will sometimes have nil or "" as the new value for a particular attribute
        ;; in a row. This means the user has entered "" in the cell or has deleted the cell's value.
        ;; For these cases we want to remove these values and their corresponding keys
        ;; from the map representing the row.
        filter-pred #(or (nil? %) (= "" %))
        pairs (for [[row-id row] merged-rows]
                [row-id (medley/remove-vals filter-pred row)])]
    (into {} pairs)))
(s/fdef merge-row-updates :args (s/cat :rows ::db/rows-by-id :updates ::db/rows-by-id)
                          :ret ::db/rows-by-id)

(defn validate-and-cast-changes
  "Validates the changes in `change-maps`. If necessary, casts the new value supplied.
  This is done according to the datatype for the column specified in the model-spec."
  [change-maps]
  (let [model-variables (mm.spec/variables model/spec)
        validate-and-cast (fn [index change]
                            (let [{:keys [row-id col new-val]} change
                                  col-type (mm.spec/stattype model/spec col)

                                  ;; Always contains the key :valid -- Change is valid.
                                  ;; May contain key :error -- Error that caused validation to fail.
                                  ;; May contain key :new-val -- New value casted appropriately.
                                  ret (cond
                                        (= col :inferenceql.viz.row/label__)
                                        {:valid true}

                                        (= col-type :gaussian)
                                        (try
                                          (let [new-val-casted (edn/read-string new-val)]
                                            (if (or (number? new-val-casted)
                                                    (nil? new-val-casted))
                                              {:valid true :new-val new-val-casted}
                                              {:valid false :error (gstring/format "The value '%s' is not a number. New values for column '%s' must be numbers." new-val col)}))
                                          (catch ExceptionInfo e
                                            {:valid false :error (ex-message e)}))

                                        (= col-type :categorical)
                                        {:valid true}

                                        :else
                                        {:valid false :error (gstring/format (str "Column \"%s\" is not in the set of model variables: %s. "
                                                                                  "Therefore inserting new values is not allowed.")
                                                                             col model-variables)})]
                                 ;; Add in :change-index and new (or updated data) contained in ret.
                                 (merge change {:change-index index} ret)))]
    (map-indexed validate-and-cast change-maps)))

;; Specs for `validate-and-cast-changes`.

(s/def ::row-id ::db/row-id)
(s/def ::row-data ::db/row)
(s/def ::col ::db/header)
(s/def ::new-val any?)
(s/def ::valid boolean?)
(s/def ::error string?)

(s/def ::change-map (s/keys :req-un [::row-id ::row-data ::col ::new-val]))
(s/def ::validation-and-cast (s/keys :req-un [::valid]
                                     :opt-un [::error ::new-val]))
(s/def ::change-maps (s/coll-of ::change-map))
(s/def ::change-maps-returned (s/coll-of (s/merge ::change-map ::validation-and-cast)))
(s/fdef validate-and-cast-changes :args (s/cat :change-maps ::change-maps)
                                  :ret ::change-maps-returned)

(defn assert-permitted-changes
  "Asserts that `changes` only occur in cells and via sources allowed by our Handsontable settings."
  [changes source]
  (let [valid-change-sources #{"edit" "CopyPaste.paste" "Autofill.fill"}]

    ;; Changes should only be happening in the label column or in user-added rows.
    ;; This should be enforced by Hansontable settings.
    (doseq [{:keys [col row-data]} changes]
      (assert (or (= :inferenceql.viz.row/label__ col)
                  (:inferenceql.viz.row/user-added-row__ row-data))))

    ;; Changes should only be the result of user edits, copy paste, or drag and autofill.
    ;; This should be enforced by Hansontable settings.
    (assert (valid-change-sources source))))
;; TODO: why doesn't this work with s/tuple for args? It appears args is coming in as a list not vector.
;; Therefore s/tuple can't match it.?
(s/fdef assert-permitted-changes :args (s/cat :changes ::change-maps :source string?))

(rf/reg-event-db
 :hot/before-change
 event-interceptors
 (fn [db [_ hot id changes source]]
   (let [change-maps (for [[row col _prev-val new-val] changes]
                       (let [p-row (.toPhysicalRow hot row)
                             row-id (get-in db [:table-panel :physical-data :row-order p-row])
                             row-data (get-in db [:table-panel :physical-data :rows-by-id row-id])

                             ;; Handsontable does not save fully qualified keywords. The label column
                             ;; is the only column that we save as a fully qualified keyword so this is
                             ;; a special case for it.
                             col-kw (if (= col "label__")
                                        :inferenceql.viz.row/label__
                                        (keyword col))]
                         {:row-id row-id :row-data row-data :col col-kw :new-val new-val}))]

     (assert-permitted-changes change-maps source)

     (let [change-maps-checked (validate-and-cast-changes change-maps)
           valid-change-maps (filter :valid change-maps-checked)
           invalid-change-maps (remove :valid change-maps-checked)

           reduce-changes-for-id (fn [changes]
                                   (let [simple-change-maps (for [change changes]
                                                              {(:col change) (:new-val change)})]
                                     (apply merge simple-change-maps)))
           updates (->> valid-change-maps
                     (group-by :row-id)
                     (medley/map-vals reduce-changes-for-id))]

       (doseq [c-map invalid-change-maps]
         ;; Cancel invalid changes in Handsontable by mutating js-object `changes`.
         (aset changes (:change-index c-map) nil)
         ;; Print reasons why changes are invalid in error log.
         (.error js/console (:error c-map)))

       (-> db
         ;; Update the currently displayed data in the table.
         (update-in [:table-panel :physical-data :rows-by-id] merge-row-updates updates)
         ;; Update the original dataset.
         (update-in [:table-panel :dataset :rows-by-id] merge-row-updates updates))))))

(rf/reg-event-fx
 :hot/after-selection-end
 event-interceptors
 (fn [{:keys [db]} [_ hot id row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-layers (js->clj (.getSelected hot))
         physical-selection-layers (vec (for [[r1 c1 r2 c2] selection-layers]
                                          (let [rp1 (.toPhysicalRow hot r1)
                                                cp1 (.toPhysicalColumn hot c1)
                                                rp2 (.toPhysicalRow hot r2)
                                                cp2 (.toPhysicalColumn hot c2)]
                                              [rp1 cp1 rp2 cp2])))
         color (control-db/selection-color db)]
     {:db (-> db
            (assoc-in [:table-panel :selection-layers color :coords] selection-layers)
            (assoc-in [:table-panel :selection-layers color :coords-physical] physical-selection-layers))
      :dispatch [:table/check-selection]})))

(rf/reg-event-db
 :hot/after-on-cell-mouse-down
 event-interceptors
 (fn [db [_ hot id mouse-event coords _TD]]
   (let [;; Stores whether the user clicked on one of the column headers.
         header-clicked-flag (= -1 (.-row coords))

         ;; Stores whether the user held alt during the click.
         alt-key-pressed (.-altKey mouse-event)
         color (control-db/selection-color db)]

     (if alt-key-pressed
       ; Deselect all cells in selection layer on alt-click.
       (update-in db [:table-panel :selection-layers] dissoc color)
       ;; Otherwise just save whether a header was clicked or not.
       (assoc-in db [:table-panel :selection-layers color :header-clicked] header-clicked-flag)))))

(defn assoc-visual-table-state
  "Associates the displayed stated of `hot` into `db`.
  The visual table state includes data changes caused by filtering, re-ordering columns, sorting columns, etc.
  We use this visual state to along with selection coordinates to produce the data subset selected.
  This gets passed onto the visualization code--all via subscriptions."
  [db hot]
  (let [rows (js->clj (.getData hot))
        headers (mapv keyword (js->clj (.getColHeader hot)))
        row-maps (mapv #(zipmap headers %) rows)]
    (-> db
        (assoc-in [:table-panel :visual-data :rows] row-maps)
        (assoc-in [:table-panel :visual-data :headers] headers))))

(rf/reg-event-db
 :hot/after-change
 event-interceptors
 (fn [db [_ hot _id _changes _source]]
   (assoc-visual-table-state db hot)))

(rf/reg-event-db
 :hot/after-column-sort
 event-interceptors
 (fn [db [_ hot _id _current-sort-config destination-sort-config]]
   (assoc-in db [:table-panel :sort-state]
             (js->clj destination-sort-config :keywordize-keys true))))

