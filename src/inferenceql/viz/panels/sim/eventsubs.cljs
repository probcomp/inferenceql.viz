(ns inferenceql.viz.panels.sim.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]))

(defn anim-steps-for-view [view-id cluster-id row]
  (if (seq row)
    (let [view-dom-id (name view-id)
          cluster-dom-id (str (name view-id) "--" (name cluster-id))
          row (medley/map-keys name row)]
      [[:sd2/scroll view-dom-id {}]
       [:sd2/set-view-cat-selection view-id (name cluster-id)]
       [:sd2/set-cluster-open view-id cluster-id true]
       [:sd2/set-cluster-output view-id cluster-id (str row)]
       [:sd2/scroll cluster-dom-id]])
    ;; Return no steps.
    []))

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

        cols (get-in db [:sim-panel :columns-used])
        cols (remove (set (keys constraints)) cols)

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

(defn set-expr-level
  [db [_ new-val]]
  (let [target-gene (get-in db [:sim-panel :target-gene])]
    (-> db
        (assoc-in [:sim-panel :expr-level] new-val)
        (assoc-in [:sim-panel :constraints] {target-gene new-val}))))
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

(defn columns-used
  [db [_]]
  (get-in db [:sim-panel :columns-used]))
(rf/reg-sub :sim/columns-used
            columns-used)

(defn all-essential-genes
  [[target-gene datasets]]
  (let [all-genes (keys (get-in datasets [:data :schema]))]
    (remove #{target-gene} all-genes)))
(rf/reg-sub :sim/all-essential-genes
            :<- [:sim/target-gene]
            :<- [:store/datasets]
            all-essential-genes)

(defn add-essential-gene
  [db [_ new-gene]]
  (-> db
      (update-in [:sim-panel :essential-genes] conj new-gene)
      (update-in [:sim-panel :columns-used] conj new-gene)))
(rf/reg-event-db :sim/add-essential-gene
                 event-interceptors
                 add-essential-gene)

(defn remove-essential-gene
  [db [_ gene-to-remove]]
  (-> db
      (update-in [:sim-panel :essential-genes] #(remove (set [gene-to-remove]) %))
      (update-in [:sim-panel :columns-used] disj gene-to-remove)))
(rf/reg-event-db :sim/remove-essential-gene
                 event-interceptors
                 remove-essential-gene)

;; Conditioned.

(defn conditioned
  [db [_]]
  (get-in db [:sim-panel :conditioned]))
(rf/reg-sub :sim/conditioned
            conditioned)

(defn set-conditioned
  [db [_ new-val]]
  (let [target-gene (get-in db [:sim-panel :target-gene])
        expr-level (get-in db [:sim-panel :expr-level])

        constraints (if new-val {target-gene expr-level} {})]
    (-> db
        (assoc-in [:sim-panel :conditioned] new-val)
        (assoc-in [:sim-panel :constraints] constraints))))

(rf/reg-event-db :sim/set-conditioned
                 event-interceptors
                 set-conditioned)

;; Expr-level slider settings.

(defn expr-level-slider-settings
  [[datasets target-gene]]
  (let [rows (get-in datasets [:data :rows])
        vals (map target-gene rows)
        min-val (apply min vals)
        max-val (apply max vals)
        step (/ (- max-val min-val)
                100)]
    {:min min-val :max max-val :step step
     :initial (+ min-val (* 50 step))}))

(rf/reg-sub :sim/expr-level-slider-settings
            :<- [:store/datasets]
            :<- [:sim/target-gene]
            expr-level-slider-settings)