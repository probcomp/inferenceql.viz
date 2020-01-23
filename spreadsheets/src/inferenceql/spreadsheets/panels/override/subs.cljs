(ns inferenceql.spreadsheets.panels.override.subs
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.db :as db]))

(rf/reg-sub :modal
            (fn [db _]
              (::db/modal db)))

(rf/reg-sub :column-override-fns
            (fn [db _]
              (get db ::db/column-override-fns)))

(rf/reg-sub :column-overrides
            (fn [db _]
              (get db ::db/column-overrides)))
