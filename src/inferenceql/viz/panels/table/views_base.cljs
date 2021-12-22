(ns inferenceql.viz.panels.table.views-base
  "Base Reagent component for Handsontable."
  (:require [handsontable$default :as yarn-handsontable]
            [camel-snake-kebab.core :as csk]
            [reagent.core :as reagent]
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

(defn handsontable-base
  "Low-level reagent component that wraps Handsontable.
  Not meant to be used directly as it requires an atom-like store for the instance of
  Handontable used by each instance of the component.

  Args:
    hot-instance -- an atom-like object that when de-referenced gets the current instance of
      Handsontable for this component.
    hot-reset! -- a function that resets the value of `hot-instance` to the new value provided.
    attributes -- dom node attributes for the table container.
    props -- map with keys :settings and :hooks.
      :settings -- settings for Handsontable, see Handsontable docs.
      :hooks -- (optional) hooks for Handsontable, see Handsontable docs.
      :selections-coords -- (optional) current selection in table to reapply after update."
  ([hot-instance hot-reset! attributes props]
   (let [dom-nodes (reagent/atom {})]
     (reagent/create-class
      {:display-name "handsontable-base"

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
       (fn [this argv-old]
         ;; This update function assumes that only the
         ;; `props` argument to the component changes.
         (let [[_ _ _ _ props-old] argv-old
               [_ _ _ _ props] (reagent/argv this)

               old-settings (:settings props-old)
               settings (:settings props)
               changed-settings (filter-kv (fn [setting-key new-val]
                                             (not= (get old-settings setting-key) new-val))
                                           settings)]

           ;; Update settings.
           (when (seq changed-settings)
             (update-hot! @hot-instance changed-settings (:selections-coords props)))))

       :component-will-unmount
       (fn [this]
         (when @hot-instance
           (.destroy @hot-instance)
           (hot-reset! nil)))

       :reagent-render
       (fn [_ _ attributes _]
         [:div#table-container attributes
          [:div {:ref #(swap! dom-nodes assoc :table-div %)}]])}))))
