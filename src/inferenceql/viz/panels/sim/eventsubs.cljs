(ns inferenceql.viz.panels.sim.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]))

(defn anim-steps-for-view [view-id cluster-id row]
  ;; todo: make this take a keyword
  [[:sd2/scroll (name view-id) {}]
   [:sd2/set-view-cat-selection view-id (name cluster-id)]
   [:sd2/set-cluster-open view-id cluster-id true]
   [:sd2/set-cluster-output view-id cluster-id (str row)]])

(defn animation-steps [model row cols]
  (let [view-ids (-> model :views keys sort)
        view-cols (medley/map-vals (fn [view] (-> view :columns keys))
                                   (:views model))
        steps-per-view (for [view-id view-ids]
                         (let [cluster-id (get row view-id)
                               row (select-keys row (get view-cols view-id))]
                           (anim-steps-for-view view-id cluster-id row)))
        steps-per-view (conj (vec steps-per-view)
                             [[:sd2/set-model-output (str (select-keys row cols))]])]
    (apply concat steps-per-view)))



(comment
 (rf/dispatch-sync [:sd2/stage-animation [[:sd2/scroll "view_0" {}]
                                          [:sd2/set-view-cat-selection :view_0 "cluster_0"]
                                          [:sd2/set-cluster-open :view_0 :cluster_0 true]
                                          [:sd2/set-cluster-output :view_0 :cluster_0 "{:age 22}"]
                                          [:sd2/scroll "view_1" {}]
                                          [:sd2/set-view-cat-selection :view_1 "cluster_0"]
                                          [:sd2/set-cluster-open :view_1 :cluster_0 true]
                                          [:sd2/set-cluster-output :view_1 :cluster_0 "{:gender \"female\" :height 99}"]
                                          [:sd2/set-model-output "{:age 22 .....}"]]]))


(defn one
  [{:keys [db]} [_]]
  (let [model (get-in db [:store-component :models :model])
        cols (keys (get-in db [:store-component :datasets :data :schema]))
        ;; todo: I should be able to do this if I update iql.inference.
        ;;cols (gpm/variables model)
        row (gpm/simulate model cols {})
        steps (animation-steps model row cols)]
    {:db (update-in db [:sim-panel :simulations] (fnil conj []) row)
     :fx [[:dispatch [:sd2/clear-animation]]
          [:dispatch [:sd2/stage-animation steps]]
          [:dispatch [:sd2/start-animation]]]}))
(rf/reg-event-fx :sim/one
                 event-interceptors
                 one)

(defn clear
  [db [_]]
  (assoc-in db [:sim-panel :simulations] nil))
(rf/reg-event-db :sim/clear
                 event-interceptors
                 clear)
