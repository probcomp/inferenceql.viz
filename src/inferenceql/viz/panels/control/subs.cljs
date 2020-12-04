(ns inferenceql.viz.panels.control.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :control/confidence-threshold
            (fn [db _]
              (get-in db [:control-panel :confidence-threshold])))

(rf/reg-sub :control/reagent-forms
            (fn [db _]
              (get-in db [:control-panel :reagent-forms])))

(rf/reg-sub :control/reagent-form
            (fn [db [_sub-name path]]
              (get-in db (into [:control-panel :reagent-forms] path))))

(rf/reg-sub :control/query-string
            (fn [db _]
              (get-in db [:control-panel :query-string])))

(rf/reg-sub :control/selection-color
            (fn [db _]
              (get-in db [:control-panel :selection-color])))
