(ns inferenceql.viz.panels.sd2.start.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

(rf/reg-sub :sd2-start/gene-clicked
            :<- [:viz/pts-store]
            (fn [pts-store]
              (when (seq pts-store)
                (first (:values (first pts-store))))))

(defn pts-store-format [gene-name]
  [{:unit "layer_0"
    :fields [{:type "E" :field "gene"}]
    :values [gene-name]}])

(defn set-gene-clicked
  [{:keys [db]} [_ gene-name]]
  (let [embed-obj (get-in db [:viz-panel :instance])]
    {:fx [[:viz/set-pts-store [embed-obj (pts-store-format gene-name)]]]}))
(rf/reg-event-fx :sd2-start/set-gene-clicked
                 event-interceptors
                 set-gene-clicked)
