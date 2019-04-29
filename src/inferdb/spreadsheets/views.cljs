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
                  :rowHeaders          true
                  :colHeaders          []
                  :filters             true
                  :bindRowsWithHeaders true
                  :selectionMode       :multiple
                  :readOnly            true
                  :height              "100vh"
                  :width               "60vw"
                  :stretchH            "all"}}
      (update :settings merge (hook-hot-settings events/hooks))))

(defn app
  []
  (let [hot-props @(rf/subscribe [:hot-props])
        selected-maps @(rf/subscribe [:selected-maps])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])]
    [:div {:style {:display "flex", :flex-wrap "wrap"}}
     [hot/handsontable {:style {:overflow "hidden"}} hot-props]
     [:div {:style {}} [oz/vega-lite vega-lite-spec]]
     #_[:pre (with-out-str (cljs.pprint/pprint @(rf/subscribe [:vega-lite-spec])))]]))
