(ns inferenceql.viz.observable.table
  (:require [handsontable$default :as yarn-handsontable]
            [reagent.core :as reagent]
            [medley.core :refer [filter-kv]]))

(def default-hot-settings
  {:settings {:data                []
              ;; :colHeaders optional.
              ;; :columns optional.
              ;; :colWidths optional.
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
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :outsideClickDeselects false
              :readOnly            true
              :height              "auto"
              :width               "auto"
              :stretchH            "none"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

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
   (let [hot-instance (reagent/atom nil)
         dom-nodes (reagent/atom {})]

     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [{:keys [settings hooks]} props
               hot (yarn-handsontable. (:table-div @dom-nodes) (clj->js settings))]

           ;; Fix scrolling for HOT in Observable.
           (.add (.-hooks yarn-handsontable)
                 "afterRender"
                 (fn []
                   (.. hot -view -wt -wtOverlays (updateMainScrollableElements)))
                 hot)

           ;; Make new HOT instances appear immediately in Observable.
           (.setTimeout js/window (fn [] (.refreshDimensions hot)) 30)

           ;; Save HOT instance.
           (reset! hot-instance hot)))

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
             (update-hot! @hot-instance changed-settings (:selections-coords new-props)))))

       :component-will-unmount
       (fn [this]
         (when @hot-instance
           (.destroy @hot-instance)))

       :reagent-render
       (fn [attributes props]
         [:div#table-container attributes
          [:div {:ref #(swap! dom-nodes assoc :table-div %)}]])}))))
