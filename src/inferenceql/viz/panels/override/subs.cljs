(ns inferenceql.spreadsheets.panels.override.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :override/column-override-fns
            (fn [db _]
              (get-in db [:override-panel :column-override-fns])))

(rf/reg-sub :override/column-overrides
            (fn [db _]
              (get-in db [:override-panel :column-overrides])))
