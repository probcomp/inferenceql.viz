(ns inferdb.spreadsheets.views
  (:require [re-frame.core :as rf]
            [camel-snake-kebab.core :as csk]
            [oz.core :as oz]
            [inferdb.spreadsheets.handsontable :as hot]))

(def hooks [:after-deselect :after-selection-end])

(defn- hook-hot-settings
  [hooks]
  (reduce (fn [m kebab-hook]
            (let [camel-hook (csk/->camelCase kebab-hook)]
              (assoc m camel-hook (fn dispatch-hook [& args]
                                    (rf/dispatch (into [kebab-hook] args))))))
          {}
          hooks))

(def default-hot-settings
  (-> {:settings {:licenseKey          "non-commercial-and-evaluation"
                  :data                []
                  :row-headers         true
                  :colHeaders          []
                  :filters             true
                  :bindRowsWithHeaders true
                  :columnSorting       true
                  :selectionMode       :multiple}}
      (update :settings merge (hook-hot-settings hooks))))

(defn app
  []
  (let [hot-props @(rf/subscribe [:hot-props])
        selected-maps @(rf/subscribe [:selections])]
    (js/console.log selected-maps)
    [:div
     [hot/handsontable hot-props]]))
