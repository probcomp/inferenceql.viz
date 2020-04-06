(ns inferenceql.spreadsheets.panels.table.handsontable)

(def label-col-header
  "Header text for the column used for labeling rows as examples."
  "🏷")
(def score-col-header
  "Header text for the column that shows scores."
  "probability")

(defn freeze-col-1-2-fn [columns-moving target]
  "Prevents the movement of the first two columns in the table.
  Also prevents other columns from moving into those frist two spots."
  (let [first-unfrozen-index 2
        first-col-moving (first (js->clj columns-moving))]
    (not (or (< first-col-moving first-unfrozen-index)
             (< target first-unfrozen-index)))))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :beforeColumnMove    freeze-col-1-2-fn
              :manualColumnResize  true
              :autoWrapCol         false
              :autoWrapRow         false
              :filters             true
              ;; TODO: investigate more closely what each of
              ;; these options adds. And if they can be put
              ;; in the context-menu instead.
              :dropdownMenu        ["filter_by_condition"
                                    "filter_operators"
                                    "filter_by_condition2"
                                    "filter_by_value"
                                    "filter_action_bar"]
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :outsideClickDeselects false
              :readOnly            true
              :height              "50vh"
              :width               "100vw"
              :stretchH            "all"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

;; These keywords refer to events in inferenceql.spreadsheets.panels.table.events.
(def real-hot-hooks [:hot/after-selection-end :hot/after-on-cell-mouse-down :hot/before-change
                     :hot/after-column-move :hot/after-column-sort :hot/after-filter])

(def real-hot-settings (-> default-hot-settings
                           (assoc-in [:hooks] real-hot-hooks)
                           (assoc-in [:name] "real-table")))
