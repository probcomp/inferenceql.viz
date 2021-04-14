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

;; add event :sd2/highlight-cluster-weight
;; add sub :sd2/cluster-weight-highlighted

;; add event :sd2/set-cluster-output
;; add sub :sd2/cluster-output

;; add event :sd2/set-model-output
;; add sub :sd2/model-ouput

;; add event :sd2/reset
;; clears all open displays and highlights