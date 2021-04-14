(ns inferenceql.viz.panels.sd2.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

(defn toggle-cluster
  [db [_ view-id cluster-id]]
  (update-in db [:sd2-panel :display view-id cluster-id] not))
(rf/reg-event-db :sd2/toggle-cluster
                 event-interceptors
                 toggle-cluster)

(defn cluster-open
  [db [_ view-id cluster-id]]
  (get-in db [:sd2-panel :display view-id cluster-id]))
(rf/reg-sub :sd2/cluster-open
            cluster-open)

