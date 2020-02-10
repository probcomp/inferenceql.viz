(ns inferenceql.spreadsheets.panels.control.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :control/confidence-threshold
            (fn [db _]
              (get-in db [:control-panel :confidence-threshold])))

(rf/reg-sub :control/confidence-options
            (fn [db _]
              (get-in db [:control-panel :confidence-options])))

(rf/reg-sub :control/confidence-option
            (fn [db [_sub-name path]]
              (get-in db (into [:control-panel :confidence-options] path))))

(rf/reg-sub :control/query-string
            (fn [db _]
              (get-in db [:control-panel :query-string])))
