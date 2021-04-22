(ns inferenceql.viz.panels.sim.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]))

(defn anim-steps-for-view [view-id cluster-id row]
  (let [view-dom-id (name view-id)
        cluster-dom-id (str (name view-id) "--" (name cluster-id))
        row (medley/map-keys name row)]
    [[:sd2/scroll view-dom-id {}]
     [:sd2/set-view-cat-selection view-id (name cluster-id)]
     [:sd2/set-cluster-open view-id cluster-id true]
     [:sd2/set-cluster-output view-id cluster-id (str row)]
     [:sd2/scroll cluster-dom-id]]))

(defn animation-steps [model row cols]
  (let [view-ids (-> model :views keys sort)
        view-cols (medley/map-vals (fn [view] (-> view :columns keys))
                                   (:views model))
        steps-per-view (for [view-id view-ids]
                         (let [cluster-id (get row view-id)
                               row (select-keys row (get view-cols view-id))]
                           (anim-steps-for-view view-id cluster-id row)))

        ;; Adding model output animation step.
        model-output (as-> row $
                           (select-keys $ cols)
                           (medley/map-keys name $)
                           (str $))
        steps-per-view (conj (vec steps-per-view)
                             [[:sd2/set-model-output model-output]
                              [:sd2/scroll "model-output"]])]
    (apply concat steps-per-view)))

;--------------------------

;;; Simulating points.

(defn simulations
  [db [_]]
  (get-in db [:sim-panel :simulations]))
(rf/reg-sub :sim/simulations
            simulations)

(defn simulate-one
  [{:keys [db]} [_]]
  (let [model (get-in db [:store-component :models :model])
        constraints (get-in db [:sim-panel :constraints])
        cols (keys (get-in db [:store-component :datasets :data :schema]))
        cols (remove (set (keys constraints)) cols)

        ;; todo: I should be able to do this if I update iql.inference.
        ;;cols (gpm/variables model)

        row (gpm/simulate model cols constraints)
        row (merge row constraints)

        steps (animation-steps model row cols)]
    {:db (update-in db [:sim-panel :simulations] (fnil conj []) row)
     :fx [[:dispatch [:sd2/clear-animation]]
          [:dispatch [:sd2/stage-animation steps]]
          [:dispatch [:sd2/start-animation]]]}))
(rf/reg-event-fx :sim/simulate-one
                 event-interceptors
                 simulate-one)

(defn simulate-many
  [db [_]]
  (let [model (get-in db [:store-component :models :model])
        constraints (get-in db [:sim-panel :constraints])
        cols (keys (get-in db [:store-component :datasets :data :schema]))
        cols (remove (set (keys constraints)) cols)

        rows (repeatedly 10 #(-> (gpm/simulate model cols constraints)
                                 (merge constraints)))]
    (update-in db [:sim-panel :simulations] (fnil concat []) rows)))
(rf/reg-event-db :sim/simulate-many
                 event-interceptors
                 simulate-many)

(defn clear-simulations
  [db [_]]
  (assoc-in db [:sim-panel :simulations] nil))
(rf/reg-event-db :sim/clear-simulations
                 event-interceptors
                 clear-simulations)

;;; Target gene expression level.

(defn expr-level
  [db [_]]
  (get-in db [:sim-panel :expr-level]))
(rf/reg-sub :sim/expr-level
            expr-level)

(defn constraints
  [db [_]]
  (get-in db [:sim-panel :constraints]))
(rf/reg-sub :sim/constraints
            constraints)

(defn make-constraints [new-val]
  (if new-val
    {:age new-val}
    {}))

(defn set-expr-level
  [db [_ new-val]]
  (-> db
      (assoc-in [:sim-panel :expr-level] new-val)
      (assoc-in [:sim-panel :constraints] (make-constraints new-val))))
(rf/reg-event-db :sim/set-expr-level
                 event-interceptors
                 set-expr-level)

;; Points count.

(defn points-count
  [simulations [_ view-id cluster-id]]
  (->> simulations
       (map view-id)
       (map #{cluster-id})
       (filter some?)
       (count)))

(rf/reg-sub :sim/points-count
            :<- [:sim/simulations]
            points-count)

;; Target gene and essential genes.

(defn target-gene
  [db [_]]
  (get-in db [:sim-panel :target-gene]))
(rf/reg-sub :sim/target-gene
            target-gene)

(defn essential-genes
  [db [_]]
  (get-in db [:sim-panel :essential-genes]))
(rf/reg-sub :sim/essential-genes
            essential-genes)

;; Conditioned.

(defn conditioned
  [db [_]]
  (get-in db [:sim-panel :conditioned]))
(rf/reg-sub :sim/conditioned
            conditioned)

(defn set-conditioned
  [db [_ new-val]]
  (let [expr-level (get-in db [:sim-panel :expr-level])
        constraints (if new-val (make-constraints expr-level) {})]
    (-> db
        (assoc-in [:sim-panel :conditioned] new-val)
        (assoc-in [:sim-panel :constraints] constraints))))

(rf/reg-event-db :sim/set-conditioned
                 event-interceptors
                 set-conditioned)
