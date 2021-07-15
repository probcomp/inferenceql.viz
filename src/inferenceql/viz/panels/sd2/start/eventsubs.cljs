(ns inferenceql.viz.panels.sd2.start.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

(rf/reg-sub :sd2-start/gene-clicked
            :<- [:viz/pts-store :start-page]
            (fn [pts-store]
              (when (seq pts-store)
                (first (:values (first pts-store))))))

(defn pts-store-format [gene-name]
  (when gene-name
    [{:unit "layer_0"
      :fields [{:type "E" :field "gene"}]
      :values [gene-name]}]))

(defn set-gene-clicked
  [{:keys [db]} [_ gene-name]]
  (let [embed-obj (get-in db [:viz-panel :instance :start-page])]
    {:fx [[:viz/set-pts-store [embed-obj (pts-store-format gene-name)]]]}))
(rf/reg-event-fx :sd2-start/set-gene-clicked
                 event-interceptors
                 set-gene-clicked)

;-----------------------------

(defn rec-genes-filter
  [db _]
  (get-in db [:sd2-start-panel :rec-genes-filter]))

(rf/reg-sub :sd2-start/rec-gene-filter
            rec-genes-filter)

(defn set-rec-genes-filter
  [db [_ new-val]]
  (-> db
      (assoc-in [:sd2-start-panel :rec-genes-filter] new-val)))

(rf/reg-event-db :sd2-start/set-rec-genes-filter
                 event-interceptors
                 set-rec-genes-filter)

(defn not-rec-genes-filter
  [db _]
  (get-in db [:sd2-start-panel :not-rec-genes-filter]))

(rf/reg-sub :sd2-start/not-rec-gene-filter
            not-rec-genes-filter)

(defn set-not-rec-genes-filter
  [db [_ new-val]]
  (-> db
      (assoc-in [:sd2-start-panel :not-rec-genes-filter] new-val)))

(rf/reg-event-db :sd2-start/set-not-rec-genes-filter
                 event-interceptors
                 set-not-rec-genes-filter)

;-----------------------------

(rf/reg-sub :sd2-start/gene-selection-list-rec
            (fn [db _]
              (get-in db [:sd2-start-panel :gene-selection-list-rec])))

(rf/reg-sub :sd2-start/gene-selection-list-not-rec
            (fn [db _]
              (get-in db [:sd2-start-panel :gene-selection-list-not-rec])))

(rf/reg-sub :sd2-start/gene-selection-list
            :<- [:sd2-start/gene-selection-list-rec]
            :<- [:sd2-start/gene-selection-list-not-rec]
            :<- [:sd2-start/rec-gene-filter]
            :<- [:sd2-start/not-rec-gene-filter]
            (fn [[list-rec list-not-rec rec-filter not-rec-filter]]
              (cond->> []
                rec-filter (concat list-rec)
                not-rec-filter (concat list-not-rec)
                :always (sort-by first >))))

(rf/reg-sub :sd2-start/plot-data-rec
            (fn [db _]
              (get-in db [:sd2-start-panel :plot-data-rec])))

(rf/reg-sub :sd2-start/plot-data-not-rec
            (fn [db _]
              (get-in db [:sd2-start-panel :plot-data-not-rec])))

(rf/reg-sub :sd2-start/plot-data
            :<- [:sd2-start/plot-data-rec]
            :<- [:sd2-start/plot-data-not-rec]
            :<- [:sd2-start/rec-gene-filter]
            :<- [:sd2-start/not-rec-gene-filter]
            (fn [[plot-data-rec plot-data-not-rec rec-filter not-rec-filter]]
              (cond->> []
                rec-filter (concat plot-data-rec)
                not-rec-filter (concat plot-data-not-rec))))
