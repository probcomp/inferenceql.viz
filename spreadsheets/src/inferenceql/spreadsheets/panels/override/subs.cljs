(ns inferenceql.spreadsheets.panels.override.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :modal
            (fn [db _]
              (get-in db [:override-panel :modal])))

(rf/reg-sub :column-override-fns
            (fn [db _]
              (get-in db [:override-panel :column-override-fns])))

(rf/reg-sub :column-overrides
            (fn [db _]
              (get-in db [:override-panel :column-overrides])))
