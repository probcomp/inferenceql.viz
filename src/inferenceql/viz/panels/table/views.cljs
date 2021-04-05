(ns inferenceql.viz.panels.table.views
  (:require [yarn.handsontable]
            [camel-snake-kebab.core :as csk]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [reagent.dom :as dom]
            [inferenceql.viz.panels.table.selections :as selections]
            [medley.core :refer [filter-kv]]))

(defn- sort-state-applicable
  "Determines whether `sort-config` can be applied to the columns.

  This simply checks column numbers referenced `sort-config` are applicable to the number for
  `columns` present.

  Args:
    columns: A vector of column names.
    sort-config: (js-object) A sort config returned by Handsontable.
  Returns:
    A boolean if `sort-config` is applicable."
  [columns sort-config]
  (let [col-nums-refed (map :column (js->clj sort-config :keywordize-keys true))
        num-cols (count columns)]
    (every? #(< % num-cols) col-nums-refed)))

(defn- update-hot!
  "A helper function for updating the settings in a handsontable."
  [hot-instance new-settings current-selection]
  (let [;; Stores whether settings that determine the data displayed have changed.
        table-changed (some new-settings [:data :colHeaders])
        sorting-plugin (.getPlugin hot-instance "multiColumnSorting")
        sort-config (.getSortConfig sorting-plugin)]

    (when table-changed
      (.deselectCell hot-instance))

    ;; When data is the only updated setting, use a different, potentially faster update function.
    (if (= (keys new-settings) [:data])
      (.loadData hot-instance (clj->js (:data new-settings)))
      (.updateSettings hot-instance (clj->js new-settings)))

    (when table-changed
      ;; Maintain the same sort order as before the update.
      ;; If colHeaders hasn't changed, we can apply the previous sort state.
      ;; Or if the sort-state is applicable to the current columns, we can as well.
      (when (or (nil? (:colHeaders new-settings))
                (sort-state-applicable (:colHeaders new-settings) sort-config))
        (.sort sorting-plugin sort-config))

      ;; Reapply selections present before the update.
      (when-let [coords (clj->js current-selection)]
        (.selectCells hot-instance coords false)))))

(defn handsontable
  ([attributes props]
   (let [hot-instance (rf/subscribe [:table/hot-instance])
         dom-nodes (reagent/atom {})]
     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [{:keys [settings name hooks]} props
               hot (js/Handsontable. (:table-div @dom-nodes) (clj->js settings))
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
                                            (rf/dispatch (into [key hot unique-id] args))
                                            true)
                                          hot)))
           ;; Save the hot object in the app db.
           (rf/dispatch [:table/set-hot-instance hot])))

       :component-did-update
       (fn [this old-argv]
         (let [[_ _old-attributes old-props] old-argv
               [_ _new-attributes new-props] (reagent/argv this)

               old-settings (:settings old-props)
               new-settings (:settings new-props)
               changed-settings (filter-kv (fn [setting-key new-val]
                                             (not= (get old-settings setting-key) new-val))
                                           new-settings)]

           ;; Update settings.
           (when (seq changed-settings)
             (update-hot! @hot-instance changed-settings (:selections-coords new-props)))

           ;; Update selections.
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
         (when @hot-instance
           (rf/dispatch [:table/unset-hot-instance])
           (.destroy @hot-instance)))

       :reagent-render
       (fn [attributes props]
         [:div#table-container attributes
          [:div {:ref #(swap! dom-nodes assoc :table-div %)}]])}))))

(defn controls
  "Controls for a handsontable instance."
  [show-table-controls]
  [:div#table-controls {:style {:visibility show-table-controls}}
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/toggle-label-column])
                 (.blur (.-target e)))}
    "labels"]
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/add-row])
                 (.blur (.-target e)))}
    "+row"]
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/delete-row])
                 (.blur (.-target e)))}
    "-row"]])
