(ns inferenceql.spreadsheets.panels.viz.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
  :viz/set-pts-store
  event-interceptors
  (fn [db [_ new-val]]
      (let [new-pts-store (js->clj new-val)
            cleaned-pts-store (when new-pts-store
                                (for [store-elem new-pts-store]
                                  ;; Remove the "getter" attribute in all of the field maps in this `store-elem`.
                                  ;; The "getter" attribute is a function added by vega that we don't need to save.
                                  ;; Saving it also causes the issue of making pts-store appear updated
                                  ;; (triggering subscribing components to be updated),
                                  ;; when in fact only this "getter" attribute has changed.
                                  (let [clean-fields (fn [fields] (map #(dissoc % "getter") fields))]
                                    (update store-elem "fields" clean-fields))))]
        (assoc-in db [:viz-panel :pts-store] cleaned-pts-store))))
