(ns inferdb.spreadsheets.handsontable
  "A Handsontable Reagent component."
  (:require [yarn.handsontable]
            [camel-snake-kebab.core :as csk]
            [cljsjs.react]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn- random-id
  []
  (-> (Math/random)
      (.toString 36)
      (.substring 5)))

(defn- update-hot!
  [hot-instance new-settings]
  (.updateSettings hot-instance new-settings false))

(defn handsontable
  ([props]
   (handsontable {} [nil 0] props))
  ([attributes [emmitter x-pos] {:keys [settings hooks] :as props}]
   (let [js-settings (clj->js settings)
         hot-instance (reagent/atom nil)
         fn-for-scroll-event (fn [& args]
                               (let [event (first args)
                                     target (-> event .-target)
                                     jq-target (js/$. target)
                                     left-pos (.scrollLeft jq-target)]
                                 (rf/dispatch [:table-scroll jq-target left-pos])))]
     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [dom-node (dom/dom-node this)
               hot (js/Handsontable. dom-node (clj->js (:settings props)))]
           ; add callbacks internal to hot object
           (doseq [key hooks]
             (let [camel-key (csk/->camelCase (clj->js key))]
               (js/Handsontable.hooks.add camel-key
                                          (fn [& args]
                                            (rf/dispatch (into [key hot] args)))
                                          hot)))
           ; add callback to dom internal dom node created by hot
           (let [jq-obj (js/$. dom-node)
                 nested-jq-obj (.find jq-obj ".ht_master > .wtHolder")]
              (.on nested-jq-obj "scroll" fn-for-scroll-event))
           ; set the atom to the hot object
           (reset! hot-instance hot)))

       :component-did-update
       (fn [this old-argv]
         (let [[_ old-attributes [old-emmitter old-x-pos] old-props] old-argv
               [_ new-attributes [new-emmitter new-x-pos] new-props] (reagent/argv this)
               {old-settings :settings, old-hooks :hooks} old-props
               {new-settings :settings, new-hooks :hooks} new-props]
           (if (not= old-settings new-settings)
             (let [sorting-plugin (.getPlugin @hot-instance "columnSorting")
                   sort-config (.getSortConfig sorting-plugin)]
               (update-hot! @hot-instance (clj->js new-settings))
               ; Maintain the same sort order as before the update
               (.sort sorting-plugin sort-config)))
           (if (not= old-x-pos new-x-pos)
             (let [dom-node (dom/dom-node this)
                   jq-obj (js/$. dom-node)
                   nested-jq-obj (.find jq-obj ".ht_master > .wtHolder")]
               (if (not (.is new-emmitter nested-jq-obj))
                 (do
                   (.off nested-jq-obj "scroll")
                   ;(.scrollLeft nested-jq-obj new-x-pos)
                   (.on nested-jq-obj "scroll" fn-for-scroll-event)))))))

       :component-will-unmount
       (fn [this]
         (.destroy @hot-instance))

       :reagent-render
       (fn [this]
         [:div attributes])}))))
