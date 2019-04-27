(ns table.core
  (:require [cljsjs.handsontable]
            [cljsjs.react]
            [reagent.core :as reagent]
            [reagent.dom :as dom]))

(defn grid
  "Takes tabular data represented as a sequence of maps and reshapes the data as a
  2D vector of cells and a vector of headers."
  [headers rows]
  (into []
        (map (fn [row]
               (into []
                     (map #(get row %))
                     headers)))
        rows))

(defn initial-state
  []
  {:settings {:data         [["2017" 10 11 12 13]
                             ["2018" 20 11 14 13]
                             ["2019" 30 15 12 13]]
              :row-headers  true
              :colHeaders   ["" "Ford" "Tesla" "Toyota" "Honda"]
              :filters      true
              :dropdownMenu true}})

(def props
  (reagent/atom (initial-state)))

(defn add-column
  [data name v]
  (mapv (fn [row]
          (conj row v))
        data))

(def settings
  (reagent/cursor props [:settings]))

(def data
  (reagent/cursor props [:settings :data]))

(defn update-hot!
  [hot-instance new-settings]
  (.updateSettings hot-instance new-settings false))

(defn random-id
  []
  (.substring (.toString (Math/random)
                         36)
              5))

(defn handsontable
  [{:keys [settings id class-name style] :as props}]
  (let [js-settings (clj->js settings)
        hot-instance (reagent/atom nil)]
    (reagent/create-class
     {:display-name  "handsontable-reagent"

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
        [:div {:id (clj->js (or id (random-id)))
               :class-name (clj->js (or class-name ""))
               :style (clj->js (or style {}))}])})))

(defn app
  []
  [handsontable @props])

(defn -main
  [& _]
  (reagent/render [app] (js/document.getElementById "app")))

(-main)

#_{:file "resources/libs/calculator.js"
   :provides ["calculator"]
   :module-type :commonjs}
