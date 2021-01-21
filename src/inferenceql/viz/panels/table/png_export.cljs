(ns inferenceql.viz.panels.table.png-export
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]))

(defn render-table-pngs []
  (rf/dispatch-sync [:table/set
                     [{:foo 3 :bar 3} {:foo 4 :bar 4}]
                     [:foo :bar]])
  ;;(rf/dispatch-sync [:table/export-png])
  (let [table (.querySelector js/document "#table-container")]
    (.log js/console :foo table)
    (doto (js/html2canvas table)
      (.then (fn [canvas]
               (let [save-fn (fn [blob]
                               (js/saveAs blob "image.png"))]
                 (.toBlob canvas save-fn))))))

 #_(fn [_ [_]]
     (let [node (.getElementById js/document "table-container")]
       (doto (.toPng js/domtoimage node)
         (.then (fn [data-url]
                  (let [img (js/Image.)]
                    (set! (.-src img) data-url
                           (.appendChild (.-body js/document) img)))))
         (.catch (fn [res]))))
     {}))
