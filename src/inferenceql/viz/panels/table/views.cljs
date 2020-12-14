(ns inferenceql.viz.panels.table.views
  (:require [yarn.handsontable]
            [camel-snake-kebab.core :as csk]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [reagent.dom :as dom]
            [inferenceql.viz.panels.table.selections :as selections]))

(defn- update-hot!
  "A helper function for updating the settings in a handsontable."
  [hot-instance new-settings]
  (let [{:keys [data]} new-settings]
    (if (= (keys new-settings) [:data])
      ;; When data is the only updated setting, use a different update function.
      (.loadData hot-instance (clj->js data))
      (.updateSettings hot-instance (clj->js new-settings) false))))

(defn handsontable
  ([attributes props]
   (let [hot-instance (reagent/atom nil)
         dom-nodes (reagent/atom {})]
     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [hot (js/Handsontable. (:table-div @dom-nodes) (clj->js (:settings props)))
               unique-id (keyword (:name props))]

           ;; add callbacks internal to hot object
           (doseq [key (:hooks props)]
             (let [camel-key (csk/->camelCase (clj->js key))]
               ;; Our hook functions call the associated re-frame event and then return true.  Some
               ;; reframe hooks such as :beforeCreateCol (not used) allow the hook function to return
               ;; false in order to cancel the event. It is likely that this behaviour exists in
               ;; other hooks as well it is just not documented. Returning nil or false from a
               ;; callback function can cause errors in handsontable plugins that also have
               ;; functions attached to that hook. Therefore, we are always returning true from hook
               ;; functions.
               (js/Handsontable.hooks.add camel-key
                                          (fn [& args]
                                            (rf/dispatch (into [key hot unique-id] args))
                                            true)
                                          hot)))
           ;; set the atom to the hot object
           (reset! hot-instance hot)))

       :component-did-update
       (fn [this old-argv]
         (let [[_ old-attributes old-props] old-argv
               [_ new-attributes new-props] (reagent/argv this)

               {old-settings :settings
                old-selections-coords :selections-coords
                old-sort-state :sort-state} old-props

               {new-settings :settings
                new-selections-coords :selections-coords
                new-sort-state :sort-state} new-props

               changed-settings (into {} (filter (fn [[setting-key new-setting-value]]
                                                   (let [old-setting-value (get old-settings setting-key)]
                                                     (not= new-setting-value old-setting-value)))
                                                 new-settings))]

           (when (seq changed-settings)
             (let [sorting-plugin (.getPlugin @hot-instance "multiColumnSorting")
                   sort-config (.getSortConfig sorting-plugin)

                   dataset-empty (and (empty? (get-in new-props [:settings :data]))
                                      (empty? (get-in new-props [:settings :colHeaders])))
                   dataset-size-changed (or (not= (count (get-in new-props [:settings :data]))
                                                  (count (get-in old-props [:settings :data])))
                                            (not= (count (get-in new-props [:settings :colHeaders]))
                                                  (count (get-in old-props [:settings :colHeaders]))))]
               ;; Whenever we insert new data into the table, we sometimes deselect all cells
               ;; before updating in order to prevent the following issues.

               ;; 1. When the table data is updated with an empty set of rows and the table previously
               ;; had rows and a selection in it, there will be a small rectangle element left over
               ;; in the DOM that is the top-left corner of the table even though the table should
               ;; be completely gone from the DOM. Clearing all selections before updating prevents
               ;; this.

               ;; 2. When the table data is updated with fewer rows than previously set and there
               ;; was a selection on the previously set data that was larger in the number of row and
               ;; columns that can be accomodated in new data, Handsontable will make a new smaller
               ;; selection on the new data. Deselecting all cells before updating, prevents this as
               ;; well.

               ;; We do not always want to perform this deselecting all and restoring behaviour.
               ;; Doing this for example when updating the labels column can lead to a race condition.
               ;; Hence the special conditions here. See the large comment below for more info on
               ;; this case.
               (when (or dataset-empty dataset-size-changed)
                 (.deselectCell @hot-instance))

               (update-hot! @hot-instance changed-settings)

               ;; Maintain the same sort order as before the update
               (.sort sorting-plugin sort-config)

               ;; If we cleared selections because of a dataset-size change, apply the latest
               ;; selection state from props.
               (when dataset-size-changed
                 (when-let [coords (clj->js (:selections-coords new-props))]
                   (.selectCells @hot-instance coords false)))))


           ;;; This next piece of code updates the selection state in handsontable depending on
           ;;; :selections-coords passed in via new-props to the reagent component. The conditions under
           ;;; which we perform the update are tricky. These notes are meant the clarify the reasoning
           ;;; for the conditions chosen.

           ;; We of course only want to update the selection state in the table if it differs from
           ;; (:selections-coords new-props) as to prevent unneeded emmissions of the
           ;; :hot/after-selection-end event.

           ;; However, sometimes the table selection state (.getSelected @hot-instance) will be out
           ;; of sync with current selection as specified by (:selections-coords new-props) but we
           ;; do not want to update the selection unless we have explicitly passed in a new
           ;; selection state, hence the second condition of
           ;; (not= (:selections-coords new-props) (:selections-coords old-props))

           ;; The reason for the table selection state getting out of sync is that the handsontable
           ;; instance might at any point change its selection due to a number of  different
           ;; factors. We don't want to undo that. Eventually an :hot/after-selection-end event will be
           ;; emitted by handsontable for this change and that will update the value of
           ;; :selections-coords in the db, and then there will be nothing to update here.

           ;; Here are the reasons table selection state might be out of sync with our current
           ;; selection as specified by (:selections-coords new-props).

           ;; 1. We inserted new data into the table that is smaller in size that the current selection.
           ;; Handsontable will simply make a new smaller selection for us in that case and emit an
           ;; :hot/after-selection-end event.

           ;; 2. We inserted an empty data set into handsontable. Handsontable will clear the selection
           ;; state and emit an :after-deselect event (which we are not listenting to).

           ;; Both 1 & 2 we avoid by performing a (.deselectCell @hot-instance) before updating.
           ;; See the large comment block above.

           ;; 3. Clicking out of a cell after inserting a new value in it--specifically in the
           ;; labels column. The new cell we clicked on will be the current selection state in the
           ;; table but the :hot/after-selection-end event for it may not have run yet when we are
           ;; here performing an update triggered by the :hot/before-change event, which put a new
           ;; value in the db for the previously selected cell. This is main reason we only update
           ;; when we have explicitly passed a new value of :selections-coords through props.
           (let [current-selection (selections/normalize
                                    (js->clj (.getSelected @hot-instance)))]
             (when (and (not= (:selections-coords new-props) current-selection)
                        (not= (:selections-coords new-props) (:selections-coords old-props)))
               (if-let [coords (clj->js (:selections-coords new-props))]
                 (.selectCells @hot-instance coords false)
                 ;; When coords is nil it means nothing should be selected in the table.
                 (.deselectCell @hot-instance))))))

       :component-will-unmount
       (fn [this]
         (.destroy @hot-instance))

       :reagent-render
       (fn [attributes props]
         [:div#table-container attributes
           [:div {:ref #(swap! dom-nodes assoc :table-div %)}]])}))))
