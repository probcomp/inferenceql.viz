(ns inferenceql.viz.panels.sd2.start.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

(comment
  (defn toggle-cluster
    [db [_ view-id cluster-id]]
    (update-in db [:sd2-model-panel :display view-id cluster-id] not))
  (rf/reg-event-db :sd2/toggle-cluster
                   event-interceptors
                   toggle-cluster)

  (defn set-cluster-open
    [db [_ view-id cluster-id new-val]]
    (assoc-in db [:sd2-model-panel :display view-id cluster-id] new-val))
  (rf/reg-event-db :sd2/set-cluster-open
                   event-interceptors
                   set-cluster-open)

  (defn cluster-open
    [db [_ view-id cluster-id]]
    (get-in db [:sd2-start-panel :good-gene]))
  (rf/reg-sub :sd2-start/good-geens
              cluster-open))
