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
  (.updateSettings hot-instance new-settings false))

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
               [_ new-attributes new-props] (reagent/argv this)]
           (when (not= (:settings old-props) (:settings new-props))
             (let [sorting-plugin (.getPlugin @hot-instance "multiColumnSorting")
                   sort-config (.getSortConfig sorting-plugin)

                   dataset-empty (and (empty? (get-in new-props [:settings :data]))
                                      (empty? (get-in new-props [:settings :colHeaders])))
                   dataset-size-changed (or (not= (count (get-in new-props [:settings :data]))
                                                  (count (get-in old-props [:settings :data])))
                                            (not= (count (get-in new-props [:settings :colHeaders]))
                                                  (count (get-in old-props [:settings :colHeaders]))))]

               (when (or dataset-empty dataset-size-changed)
                 (.deselectCell @hot-instance))

               (update-hot! @hot-instance (clj->js (:settings new-props)))

               ;; Maintain the same sort order as before the update
               (.sort sorting-plugin sort-config)

               ;; If we cleared selections because of a dataset-size change, apply the latest
               ;; selection state from props.
               (when dataset-size-changed
                 (when-let [coords (clj->js (:selections-coords new-props))]
                   (.selectCells @hot-instance coords false)))))

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
