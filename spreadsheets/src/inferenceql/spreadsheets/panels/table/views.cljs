(ns inferenceql.spreadsheets.panels.table.views
  (:require [yarn.handsontable]
            [camel-snake-kebab.core :as csk]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn- update-hot!
  "A helper function for updating the settings in a handsontable."
  [hot-instance new-settings]
  (.updateSettings hot-instance new-settings false))

(defn handsontable
  ([props]
   (handsontable {} props))
  ([attributes {:keys [settings hooks name] :as props}]
   (let [js-settings (clj->js settings)
         hot-instance (reagent/atom nil)]
     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [dom-node (dom/dom-node this)
               hot (js/Handsontable. dom-node (clj->js (:settings props)))
               unique-id (keyword name)]

           ;; add callbacks internal to hot object
           (doseq [key hooks]
             (let [camel-key (csk/->camelCase (clj->js key))]
               (js/Handsontable.hooks.add camel-key
                                          (fn [& args]
                                            (rf/dispatch (into [key hot unique-id] args)))
                                          hot)))
           ;; set the atom to the hot object
           (reset! hot-instance hot)))

       :component-did-update
       (fn [this old-argv]
         (let [[_ old-attributes old-props] old-argv
               [_ new-attributes new-props] (reagent/argv this)
               {old-settings :settings, old-hooks :hooks} old-props
               {new-settings :settings, new-hooks :hooks} new-props]
           (if (not= old-settings new-settings)
             (let [sorting-plugin (.getPlugin @hot-instance "multiColumnSorting")
                   sort-config (.getSortConfig sorting-plugin)]
               (update-hot! @hot-instance (clj->js new-settings))
               ;; Maintain the same sort order as before the update
               (.sort sorting-plugin sort-config)))))

       :component-will-unmount
       (fn [this]
         (.destroy @hot-instance))

       :reagent-render
       (fn [this]
         [:div attributes])}))))
