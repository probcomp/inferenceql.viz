(ns inferenceql.viz.panels.sd2.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

;; Opening and closing clusters.

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

;; Highlighting cluster weights.

(defn highlight-cluster-weight
  [db [_ view-id cluster-id]]
  (update-in db [:sd2-panel :weights-highlighted view-id cluster-id] not))
(rf/reg-event-db :sd2/highlight-cluster-weight
                 event-interceptors
                 highlight-cluster-weight)

(defn cluster-weight-highlighted
  [db [_ view-id cluster-id]]
  (get-in db [:sd2-panel :weights-highlighted view-id cluster-id]))
(rf/reg-sub :sd2/cluster-weight-highlighted
            cluster-weight-highlighted)

;; Output of a clusters.

(defn set-cluster-output
  [db [_ view-id cluster-id new-val]]
  (assoc-in db [:sd2-panel :cluster-output view-id cluster-id] new-val))
(rf/reg-event-db :sd2/set-cluster-output
                 event-interceptors
                 set-cluster-output)

(defn cluster-output
  [db [_ view-id cluster-id]]
  (get-in db [:sd2-panel :cluster-output view-id cluster-id]))
(rf/reg-sub :sd2/cluster-output
            cluster-output)

;; Output of the model

(defn set-model-output
  [db [_ new-val]]
  (assoc-in db [:sd2-panel :model-output] new-val))
(rf/reg-event-db :sd2/set-model-output
                 event-interceptors
                 set-model-output)

(defn model-output
  [db [_]]
  (get-in db [:sd2-panel :model-output] "{:foo 3 :bar 9}"))
(rf/reg-sub :sd2/model-output
            model-output)

;; add event :sd2/reset
;; clears all open displays and highlights