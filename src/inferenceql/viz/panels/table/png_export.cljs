(ns inferenceql.viz.panels.table.png-export
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]))

(defn render-table-pngs []
  (rf/dispatch-sync [:table/set
                     [{:foo 3 :bar 3} {:foo 4 :bar 4}]
                     [:foo :bar]]))
  


(rf/reg-event-fx
 :table/render-png
 event-interceptors
 (fn [_ [_]]
   #_(let [node (.getElementById js/document "table-container")]
       (doto (.toPng js/domtoimage node)
         (.then (fn [data-url]
                  (let [img (js/Image.)]
                    (set! (.-src img) data-url
                           (.appendChild (.-body js/document) img)))))
         (.catch (fn [res]))))
   {}))
