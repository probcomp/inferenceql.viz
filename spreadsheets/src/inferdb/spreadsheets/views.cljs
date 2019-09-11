(ns inferdb.spreadsheets.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-forms.core :refer [bind-fields]]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.events :as events]
            [inferdb.spreadsheets.handsontable :as hot]
            [inferdb.spreadsheets.vega :as vega]
            [inferdb.spreadsheets.modal :as modal]))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :beforeColumnMove    hot/freeze-col-1-2-fn
              :manualColumnResize  true
              :autoWrapCol         false
              :autoWrapRow         false
              :filters             true
              ;; TODO: investigate more closely what each of
              ;; these options adds. And if they can be put
              ;; in the context-menu instead.
              :dropdownMenu        ["filter_by_condition"
                                    "filter_operators"
                                    "filter_by_condition2"
                                    "filter_by_value"
                                    "filter_action_bar"]
              :bindRowsWithHeaders true
              :selectionMode       :range
              :outsideClickDeselects false
              :readOnly            true
              :height              "32vh"
              :width               "100vw"
              :stretchH            "all"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

(def real-hot-settings (assoc default-hot-settings
                         :hooks events/real-hot-hooks
                         :name "real-table"))
(def virtual-hot-settings (assoc default-hot-settings
                            :hooks events/virtual-hot-hooks
                            :name "virtual-table"))

(def ^:private default-search-string "GENERATE ROW")

(defn confidence-slider []
  (let [cur-val @(rf/subscribe [:confidence-threshold])]
    [:div
      [:span "Confidence Threshold: "]
      [:input {:type :range :name :confidence-threshold
               :min 0 :max 1 :step 0.01
                       :value cur-val
                       :on-change (fn [e]
                                    ;; TODO: find a way to debounce this callback
                                    (let [new-val (js/parseFloat (-> e .-target .-value))]
                                      (rf/dispatch [:set-confidence-threshold new-val])))}]
      [:label cur-val]]))

(def confidence-options
  [:div.condition-set
    [:div.condition-option
      [:label "Mode:"]
      [:select.form-control {:field :list :id :mode}
       [:option {:key :none} "none"]
       [:option {:key :row} "row-wise"]
       [:option {:key :cells-existing} "cell-wise (existing)"]
       [:option {:key :cells-missing} "cell-wise (missing)"]]]])

;; Function map that allows reagent forms to communicate with the reframe db
(def events-for-conf-options
  {:get (fn [path] @(rf/subscribe [:confidence-option path]))
   :save! (fn [path value] (rf/dispatch [:set-confidence-options path value]))
   :update! (fn [path save-fn value]
              ; save-fn should accept two arguments: old-value, new-value
              (rf/dispatch [:update-confidence-options save-fn path value]))
   :doc (fn [] @(rf/subscribe [:confidence-options]))})

(defn search-form
  [name]
  (let [input-text (r/atom default-search-string)]
    (fn []
      [:div {:style {:display "flex"}}
       [:input {:type "search"
                :style {:width "40%"}
                :on-change #(reset! input-text (-> % .-target .-value))
                :on-key-press (fn [e] (if (= (.-key e) "Enter")
                                        (rf/dispatch [:parse-query @input-text])))
                :value @input-text}]
       [:button {:on-click #(rf/dispatch [:parse-query @input-text])
                 :style {:float "right"}}
        "Run InferenceQL"]
       [:button {:on-click #(rf/dispatch [:clear-virtual-data])
                 :style {:float "right"}}
        "Delete virtual data"]
       [confidence-slider]
       [:pre "  "]
       [bind-fields confidence-options events-for-conf-options]])))

(defn app
  []
  (let [real-hot-props      @(rf/subscribe [:real-hot-props])
        virtual-hot-props @(rf/subscribe [:virtual-hot-props])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        scores         @(rf/subscribe [:scores])
        generator      @(rf/subscribe [:generator])]
    [:div
     [search-form "Zane"]
     [:div.table-title
       [:div.main-title
         [:span "Real Data"]]
       (comment
         [:div.sub-title
             [:pre "    rows: real developers    columns: real survey answers"]])]
     [hot/handsontable {:style {:overflow "hidden"}}  real-hot-props]
     [:div.table-title
       [:div.main-title
         [:span "Virtual Data"]]
       (comment
         [:div.sub-title
           [:pre "    rows: virtual developers    columns: virtual survey answers"]])]
     [hot/handsontable {:style {:overflow "hidden"} :class "virtual-hot"} virtual-hot-props]
     [:div {:style {:display "flex"
                    :justify-content "center"}}
      (when vega-lite-spec
        [vega/vega-lite vega-lite-spec {:actions false} generator])]
     [modal/modal]]))
