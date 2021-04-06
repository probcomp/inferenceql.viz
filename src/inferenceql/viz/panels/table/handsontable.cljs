(ns inferenceql.viz.panels.table.handsontable
  (:require [re-frame.core :as rf]))

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

(defn physical-row-indices
  "Returns the order of physical row indices currently displayed in the handsontable instance.

  Visual indices map to physical indicies (indices of the original dataset sent to handsontable).
  This mapping changes whenever rows are sorted, filtered, added, or removed.

  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [hot]
  (let [num-rows-shown (.countRows hot)
        visual-row-indices (range num-rows-shown)]
    (map #(.toPhysicalRow hot %) visual-row-indices)))

;; Our hook functions call the associated re-frame event and then return true.  Some
;; reframe hooks such as :beforeCreateCol (not used) allow the hook function to return
;; false in order to cancel the event. It is likely that this behaviour exists in
;; other hooks as well it is just not documented. Returning nil or false from a
;; callback function can cause errors in handsontable plugins that also have
;; functions attached to that hook. Therefore, we are always returning true from hook
;; functions.
(def real-hot-hooks {:hot/after-selection-end
                     (fn [hot]
                       (fn [_row-index _col _row2 _col2 _selection-layer-level]
                         (rf/dispatch [:hot/after-selection-end
                                       (js->clj (.getSelected hot))])
                         true))

                     :hot/after-on-cell-mouse-down
                     (fn [_]
                       (fn [mouse-event _coords _TD]
                         (rf/dispatch [:hot/after-on-cell-mouse-down
                                       ;; Whether user held alt during last click.
                                       (js->clj (.-altKey mouse-event))])
                         true))

                     :hot/after-column-move
                     (fn [hot]
                       (fn [_moved-columns _final-index _drop-index _move-possible _order-changed]
                         (rf/dispatch [:hot/after-column-move
                                       (js->clj (.getColHeader hot))])
                         true))

                     :hot/after-column-sort
                     (fn [hot]
                       (fn [_current-sort-config _destination-sort-config]
                         (rf/dispatch [:hot/after-column-sort
                                       (js->clj (.getColHeader hot))
                                       (physical-row-indices hot)])
                         true))

                     :hot/after-create-row
                     (fn [hot]
                       (fn [_index _amount source]
                         (rf/dispatch [:hot/after-create-row
                                       source
                                       (physical-row-indices hot)])
                         true))

                     :hot/after-remove-row
                     (fn [hot]
                       (fn [_index _amount _physical-rows source]
                         (rf/dispatch [:hot/after-remove-row
                                       source
                                       (physical-row-indices hot)])
                         true))

                     :hot/after-filter
                     (fn [hot]
                       (fn [_conditions-stack]
                         (rf/dispatch [:hot/after-filter
                                       (physical-row-indices hot)])
                         true))

                     :hot/before-change
                     (fn [_]
                       (fn [changes source]
                         ;; For the :hot/before-change event we want to use rf/dispatch-sync, so
                         ;; that the event handler can run and mutate the changes argument before
                         ;; returning control to Handsontable. With rf/dispatch, the event will only
                         ;; be queued and control will immediately return to Handsontable.
                         (rf/dispatch-sync [:hot/before-change
                                            changes
                                            source])
                         true))})

(def real-hot-settings (-> default-hot-settings
                           (assoc-in [:hooks] real-hot-hooks)))
