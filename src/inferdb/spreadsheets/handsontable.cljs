(ns inferdb.spreadsheets.handsontable
  "A Handsontable Reagent component."
  (:require [yarn.handsontable]
            [cljsjs.react]
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
  [{:keys [settings id class-name style] :as props}]
  (let [js-settings (clj->js settings)
        hot-instance (reagent/atom nil)]
    (reagent/create-class
     {:display-name "handsontable-reagent"

      :component-did-mount
      (fn [this]
        (let [dom-node (dom/dom-node this)
              hot (js/Handsontable. dom-node (clj->js (:settings props)))]
          (reset! hot-instance hot)))

      :should-component-update
      (fn [this [_ old-props] [_ {new-settings :settings :as new-props}]]
        (update-hot! @hot-instance (clj->js new-settings))
        false)

      :component-did-update
      (fn [this old-argv]
        (let [new-argv (rest (reagent/argv this))]
          ;; do something
          ))

      :component-will-unmount
      (fn [this]
        (.destroy @hot-instance))

      :reagent-render
      (fn [this]
        [:div {:id         (clj->js (or id         (random-id)))
               :class-name (clj->js (or class-name ""))
               :style      (clj->js (or style      {}))}])})))
