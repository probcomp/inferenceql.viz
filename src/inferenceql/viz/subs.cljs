(ns inferenceql.viz.subs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.db :as db]))

(rf/reg-event-db
 :ze-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))


