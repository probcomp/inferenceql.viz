(ns inferenceql.viz.panels.table.handsontable)

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
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
              :stretchH            "none"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

;; These keywords refer to events in inferenceql.viz.panels.table.events.
(def real-hot-hooks [:hot/after-selection-end :hot/after-on-cell-mouse-down
                     :hot/after-column-move :hot/after-column-sort :hot/after-filter
                     :hot/before-change])

(def real-hot-settings (-> default-hot-settings
                           (assoc-in [:hooks] real-hot-hooks)))
