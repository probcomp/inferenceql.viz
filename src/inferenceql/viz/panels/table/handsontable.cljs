(ns inferenceql.viz.panels.table.handsontable)

(defn freeze-label-col-pos
  "Keeps the position of the label column fixed in the table.
  The label column is meant to always be in the 0-th position.
  This function is intended to be used as the value of the :beforeColumnMove setting for
  Handsontable.

  Returns: (bool) Whether to allow the column move or not."
  [moved-columns _final-index drop-index _move-possible]
  (and (not-any? #{0} moved-columns) ; The label column is not being moved.
       (not= drop-index 0))) ; We are not trying to move anything in front of the label column.

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :beforeColumnMove    freeze-label-col-pos
              :manualColumnResize  true
              :autoWrapCol         false
              :autoWrapRow         false
              :filters             false
              ;; TODO: investigate more closely what each of
              ;; these options adds. And if they can be put
              ;; in the context-menu instead.
              #_:dropdownMenu        #_["filter_by_condition"
                                        "filter_operators"
                                        "filter_by_condition2"
                                        "filter_by_value"
                                        "filter_action_bar"]
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :outsideClickDeselects false
              :readOnly            true
              :height              "1000px"
              :stretchH            "none"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

;; These keywords refer to events in inferenceql.viz.panels.table.events.
(def real-hot-hooks [:hot/after-selection-end :hot/after-on-cell-mouse-down
                     :hot/after-column-move :hot/after-column-sort :hot/after-filter])

(def real-hot-settings (-> default-hot-settings
                           (assoc-in [:hooks] real-hot-hooks)
                           (assoc-in [:name] "real-table")))
