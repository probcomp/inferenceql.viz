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
   (handsontable {} 0 props))
  ([attributes x-pos {:keys [settings hooks] :as props}]
   (let [js-settings (clj->js settings)
         hot-instance (reagent/atom nil)]
     (reagent/create-class
      {:display-name "handsontable-reagent"

       :component-did-mount
       (fn [this]
         (let [dom-node (dom/dom-node this)
               hot (js/Handsontable. dom-node (clj->js (:settings props)))]
           (doseq [key hooks]
             (let [camel-key (csk/->camelCase (clj->js key))]
               (js/Handsontable.hooks.add camel-key
                                          (fn [& args]
                                            (rf/dispatch (into [key hot] args)))
                                          hot)))
           (reset! hot-instance hot)))

       :component-did-update
       (fn [this old-argv]
         (let [[_ old-attributes old-x-pos {:keys [old-settings old-hooks] :as old-props}] old-argv
               [_ new-attributes new-x-pos {:keys [new-settings new-hooks] :as new-props}] (reagent/argv this)]
           (if (not= old-settings new-settings)
             (update-hot! @hot-instance (clj->js new-settings)))
           (if (not= old-x-pos new-x-pos)
             (let [dom-node (dom/dom-node this)
                   jq-obj (js/$. dom-node)
                   nested-jq-obj (.find jq-obj ".ht_master > .wtHolder")
                   left-pos (.scrollLeft nested-jq-obj)]
               (.log js/console "old pos:")
               (.log js/console left-pos)
               (.scrollLeft nested-jq-obj new-x-pos)))))


       :component-will-unmount
       (fn [this]
         (.destroy @hot-instance))

       :reagent-render
       (fn [this]
         [:div attributes])}))))
