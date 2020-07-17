(ns inferenceql.spreadsheets.panels.table.views
  (:require [yarn.handsontable]
            [camel-snake-kebab.core :as csk]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn controls
  "Controls for a handsontable instance."
  [show-table-controls]
  [:div#table-controls {:style {:visibility show-table-controls}}
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/toggle-label-column])
                 (.blur (.-target e)))} ; Clear focus off of button after click.
    "labels"]
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/add-row])
                 (.blur (.-target e)))} ; Clear focus off of button after click.
    "+row"]
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/delete-row])
                 (.blur (.-target e)))} ; Clear focus off of button after click.
    "-row"]])

(defn- update-hot!
  "A helper function for updating the settings in a handsontable."
  [hot-instance new-settings]
  (let [{:keys [data]} new-settings]
    (if (= (keys new-settings) [:data])
      ;; When data is the only updated setting, use a different update function.
      (.loadData hot-instance (clj->js data))
      (.updateSettings hot-instance (clj->js new-settings) false))))

(defn handsontable
  ([props]
   (handsontable {} props))
  ([attributes props]
   (let [hot-instance (rf/subscribe [:table/hot-instance])]
     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [{:keys [settings name hooks]} props
               dom-node (dom/dom-node this)
               hot (js/Handsontable. dom-node (clj->js settings))
               unique-id (keyword name)]

           ;; add callbacks internal to hot object
           (doseq [key hooks]
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
                                            (rf/dispatch-sync (into [key hot unique-id] args))
                                            true)
                                          hot)))
           ;; Save the hot object in the app db.
           (rf/dispatch [:table/set-hot-instance hot])))

       :component-did-update
       (fn [this old-argv]
         (when @hot-instance
           (let [[_ _old-attributes old-props] old-argv
                 [_ _new-attributes new-props] (reagent/argv this)

                 {old-settings :settings
                  old-selections-coords :selections-coords
                  old-sort-state :sort-state} old-props

                 {new-settings :settings
                  new-selections-coords :selections-coords
                  new-sort-state :sort-state
                  new-behaviour :behavior} new-props

                 changed-settings (into {} (filter (fn [[setting-key new-setting-value]]
                                                     (let [old-setting-value (get old-settings setting-key)]
                                                       (not= new-setting-value old-setting-value)))
                                                   new-settings))

                 {:keys [jump-to-selection] :or {jump-to-selection false}} new-behaviour

                 {data-changed :data col-headers-changed :colHeaders} changed-settings
                 {new-data :data new-col-headers :colHeaders} new-settings]


             (when (not= old-settings new-settings)
               (let [dataset-empty (and (empty? new-data) (empty? new-col-headers))
                     dataset-size-changed (or data-changed col-headers-changed)]
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

                 ;; Maintain the same sort order as before the update or a new order if provided.
                 (let [sorting-plugin (.getPlugin @hot-instance "multiColumnSorting")]
                   (when-let [config (clj->js new-sort-state)]
                     (.sort sorting-plugin config)))

                 ;; If we cleared selections because of a dataset-size change, apply the latest
                 ;; selection state from props.
                 (when dataset-size-changed
                   (when-let [coords (clj->js new-selections-coords)]
                     (.selectCells @hot-instance coords jump-to-selection)))))


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
             (when (and (not= new-selections-coords (js->clj (.getSelected @hot-instance)))
                        (not= new-selections-coords old-selections-coords))
               (if-let [coords (clj->js new-selections-coords)]
                 (.selectCells @hot-instance coords jump-to-selection)
                 ;; When coords is nil it means nothing should be selected in the table.
                 (.deselectCell @hot-instance)))

             (let [sorting-plugin (.getPlugin @hot-instance "multiColumnSorting")]
               (when (and (not= new-sort-state (js->clj (.getSortConfig sorting-plugin)
                                                        :keywordize-keys true))
                          (not= new-sort-state old-sort-state))
                 (when-let [config (clj->js new-sort-state)]
                   (.sort sorting-plugin config))))

             (when jump-to-selection
               (rf/dispatch [:table/jump-to-selection-done])))))
       :component-will-unmount
       (fn [this]
         (when-let [hot @hot-instance]
           (.destroy hot)
           (rf/dispatch [:table/unset-hot-instance])))

       :reagent-render
       (fn [attributes props]
         [:div attributes])}))))
