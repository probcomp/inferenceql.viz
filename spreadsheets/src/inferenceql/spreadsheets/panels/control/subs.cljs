(ns inferenceql.spreadsheets.panels.control.subs
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.db :as db]))

(rf/reg-sub :confidence-threshold
            (fn [db _]
              (get db ::db/confidence-threshold)))

(rf/reg-sub :confidence-options
            (fn [db _]
              (get db ::db/confidence-options)))

(rf/reg-sub :confidence-option
            (fn [db [_sub-name path]]
              (get-in db (into [::db/confidence-options] path))))

(rf/reg-sub :query-string
            (fn [db _]
              (get db ::db/query-string)))
