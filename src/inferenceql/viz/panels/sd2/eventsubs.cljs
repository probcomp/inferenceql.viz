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

(defn set-cluster-open
  [db [_ view-id cluster-id new-val]]
  (assoc-in db [:sd2-panel :display view-id cluster-id] new-val))
(rf/reg-event-db :sd2/set-cluster-open
                 event-interceptors
                 set-cluster-open)

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
  (get-in db [:sd2-panel :model-output]))
(rf/reg-sub :sd2/model-output
            model-output)

;; add event :sd2/reset
;; clears all open displays and highlights

(defn stage-animation
  [db [_ events]]
  (-> db
      (assoc-in [:sd2-panel :animation :events] events)
      (assoc-in [:sd2-panel :animation :running] false)))
(rf/reg-event-db :sd2/stage-animation
                 event-interceptors
                 stage-animation)

(defn start-animation
  [{:keys [db]} [_]]
  (when (seq (get-in db [:sd2-panel :animation :events]))
    {:db (assoc-in db [:sd2-panel :animation :running] true)
     :fx [[:dispatch [:sd2/continue-animation]]]}))
(rf/reg-event-fx :sd2/start-animation
                 event-interceptors
                 start-animation)

(defn continue-animation
  [{:keys [db]} [_]]
  (let [events (get-in db [:sd2-panel :animation :events])]
    (if (seq events)
      {:db (update-in db [:sd2-panel :animation :events] rest)
       :fx [[:dispatch (first events)]
            [:dispatch-later [{:ms 1000 :dispatch [:sd2/continue-animation]}]]]}
      {:db (assoc-in db [:sd2-panel :animation :running] false)})))
(rf/reg-event-fx :sd2/continue-animation
                 event-interceptors
                 continue-animation)

