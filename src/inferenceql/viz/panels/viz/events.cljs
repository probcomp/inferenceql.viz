(ns inferenceql.viz.panels.viz.events
  "Contains events related to the viz panel and its portion of the db."
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
  :viz/set-pts-store
  event-interceptors
  (fn [db [_ id new-val]]
      (let [new-pts-store (js->clj new-val :keywordize-keys true)
            cleaned-pts-store (when (seq new-pts-store)
                                (for [store-elem new-pts-store]
                                  ;; Remove the "getter" attribute in all of the field maps in this `store-elem`.
                                  ;; The "getter" attribute is a function added by vega that we don't need to save.
                                  ;; Saving it also causes the issue of making pts-store appear updated
                                  ;; (triggering subscribing components to be updated),
                                  ;; when in fact only this "getter" attribute has changed.
                                  (let [clean-fields (fn [fields] (mapv #(dissoc % :getter) fields))]
                                    (update store-elem :fields clean-fields))))]
        (if cleaned-pts-store
          (assoc-in db [:viz-panel :pts-store id] cleaned-pts-store)
          (update-in db [:viz-panel :pts-store] dissoc id)))))

(rf/reg-event-db
  :viz/clear-pts-store
  event-interceptors
  (fn [db _]
    (update-in db [:viz-panel] dissoc :pts-store :pts-store-staged)))

(rf/reg-event-db
  :viz/set-instance
  event-interceptors
  (fn [db [_ id new-val]]
    (assoc-in db [:viz-panel :instance id] new-val)))

(rf/reg-event-db
  :viz/clear-instance
  event-interceptors
  (fn [db [_ id]]
    (update-in db [:viz-panel :instance] dissoc id)))
