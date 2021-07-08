(ns inferenceql.viz.panels.sd2.start.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

(rf/reg-sub :sd2-start/gene-clicked
            :<- [:viz/pts-store]
            (fn [pts-store]
              (when (seq pts-store)
                (first (:values (first pts-store))))))

