(ns inferenceql.spreadsheets.panels.crosscat.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [box h-box v-box gap single-dropdown button]]
            [inferenceql.spreadsheets.panels.viz.views :refer [vega-lite]]))

;;; Vega-lite specs.

(def base-spec {:$schema "https://vega.github.io/schema/vega-lite/v4.json",
                :description "A simple bar chart with embedded data.",
                :data {:values [{:a "A", :b 28}
                                {:a "B", :b 55}
                                {:a "C", :b 43}
                                {:a "D", :b 91}
                                {:a "E", :b 81}
                                {:a "F", :b 53}
                                {:a "G", :b 19}
                                {:a "H", :b 87}
                                {:a "I", :b 52}]},
                :mark "bar",
                :encoding {:x {:field "a", :type "nominal", :axis {:labelAngle 0}},
                           :y {:field "b", :type "quantitative"}}})

(def specs {:alpha base-spec
            :beta (assoc-in base-spec [:data :values] [{:a "C" :b 30000}
                                                       {:a "D" :b 4000}])
            :gamma (assoc-in base-spec [:data :values] [{:a "A" :b 99}
                                                        {:a "B" :b 300}])})

;;; Main reagent component for Crosscat Viz.

(defn viz
  "A reagant component for displaying a Crossscat visualization."
  []
  (let [option @(rf/subscribe [:crosscat/option])
        spec (get specs option)

        model @(rf/subscribe [:query/model])
        dataset @(rf/subscribe [:query/dataset])
        visual-headers @(rf/subscribe [:table/visual-headers])
        visual-rows @(rf/subscribe [:table/visual-rows])
        selection-layers @(rf/subscribe [:table/selection-layers])]

    ;; Logging various subs for learning purposes.
    (.log js/console "------------Logging Misc Subs--------------------")
    (.log js/console :model model)
    (.log js/console :dataset dataset)
    (.log js/console :visual-headers visual-headers)
    (.log js/console :visual-rows visual-rows)
    (.log js/console :selection-layers selection-layers)

    ;; The actual component returned.
    [v-box
     :gap "10px"
     :margin "10px 10px 10px 10px"
     :children [[:h4 "Crosscat Viz"]
                [:h5 (str "Current value for option is " option)]
                [single-dropdown
                 :choices   [{:id :alpha :label "Alpha"}
                             {:id :beta :label "Beta"}
                             {:id :gamma :label "Gamma"}]
                 :model     option
                 :width     "100px"
                 :on-change #(rf/dispatch [:crosscat/set-option %])]
                [button
                 :label "Set option to :gamma"
                 :on-click #(rf/dispatch [:crosscat/set-option :gamma])]
                [vega-lite spec {:actions false} nil nil]]]))