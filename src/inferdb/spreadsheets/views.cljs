(ns inferdb.spreadsheets.views
  (:require [re-frame.core :as rf]
            [camel-snake-kebab.core :as csk]
            [oz.core :as oz]
            [inferdb.spreadsheets.events :as events]
            [inferdb.spreadsheets.handsontable :as hot]))

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
                  :selectionMode       :multiple}}
      (update :settings merge (hook-hot-settings events/hooks))))

(defn app
  []
  (let [hot-props @(rf/subscribe [:hot-props])
        selected-maps @(rf/subscribe [:selected-maps])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])]
    [:div
     [hot/handsontable hot-props]
     [oz/vega-lite vega-lite-spec]
     #_
     (let [vega-lite-spec ]
       [:pre (with-out-str (cljs.pprint/pprint @(rf/subscribe [:vega-lite-spec])))])]))
