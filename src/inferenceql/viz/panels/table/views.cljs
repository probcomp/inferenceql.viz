(ns inferenceql.viz.panels.table.views
  (:require [handsontable$default :as yarn-handsontable]
            [camel-snake-kebab.core :as csk]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [reagent.dom :as dom]
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

(defn update-hot!
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
  "mode -- can be one of the following keywords.
    :reagent - standard reagent component.
    :reagent-observable - enables options to get handsontable to display correctly in
       observable notebooks.
    :re-frame - stores the Handsontable object in the re-frame app-db instead of a local atom.

   attributes -- dom node attributes for the table container.
   props -- map with keys :settings and :hooks.
     :settings -- settings for Handsontable, see Handsontable docs.
     :hooks -- (optional) hooks for Handsontable, see Handsontable docs.
     :selections-coords -- (optional) current selection in table to reapply after update."
  ([hot-instance hot-reset! attributes props]
   (let [dom-nodes (reagent/atom {})]
     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [{:keys [settings hooks]} props
               hot (yarn-handsontable. (:table-div @dom-nodes) (clj->js settings))]

           ;; Add callbacks internal to hot object.
           (doseq [[key callback-gen] hooks]
             (let [camel-key (csk/->camelCase (clj->js key))]
               (.add (.-hooks yarn-handsontable)
                     camel-key
                     (callback-gen hot)
                     hot)))

           ;; Save HOT instance.
           (hot-reset! hot)))

       :component-did-update
       (fn [this old-argv]
         (let [[_ _old-mode _old-attributes old-props] old-argv
               [_ _new-mode _new-attributes new-props] (reagent/argv this)

               old-settings (:settings old-props)
               new-settings (:settings new-props)
               changed-settings (filter-kv (fn [setting-key new-val]
                                             (not= (get old-settings setting-key) new-val))
                                           new-settings)]

           ;; Update settings.
           (when (seq changed-settings)
             (update-hot! @hot-instance changed-settings (:selections-coords new-props)))))

       :component-will-unmount
       (fn [this]
         (when @hot-instance
           (.destroy @hot-instance)
           (hot-reset! nil)))

       :reagent-render
       (fn [mode attributes props]
         [:div#table-container attributes
          [:div {:ref #(swap! dom-nodes assoc :table-div %)}]])}))))

(defn handsontable-rf
  [attributes props]
  (let [hot-instance (rf/subscribe [:table/hot-instance])
        hot-reset (fn [new-val]
                    (if new-val
                      (rf/dispatch [:table/set-hot-instance new-val])
                      (rf/dispatch [:table/unset-hot-instance])))]
    (fn [attributes props]
      [handsontable hot-instance hot-reset attributes props])))

(defn handsontable-re
  [attributes props]
  (let [hot-instance (reagent/atom nil)
        hot-reset #(reset! hot-instance %)]
    (fn [attributes props]
      [handsontable hot-instance hot-reset attributes props])))

(defn handsontable-re-obs
  [attributes props]
  (let [hot-instance (reagent/atom nil)
        hot-reset #(reset! hot-instance %)]
    (reagent/create-class
     {:component-did-mount
      (fn [this]
        ;; Make new HOT instances appear immediately in Observable.
        (.setTimeout js/window (fn [] (.refreshDimensions @hot-instance)) 30))

      :reagent-render
      (fn [attributes props]
        (let [props (assoc-in props [:hooks :afterRender]
                      (fn [hot]
                        ;; Fix scrolling for Handsontable in Observable.
                        (fn [] (.. hot -view -wt -wtOverlays (updateMainScrollableElements)))))])

        [handsontable hot-instance hot-reset attributes props])})))

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
                 (.blur (.-target e)))
     :disabled true}
    "-row"]])
