(ns inferenceql.spreadsheets.clojure-conj.trace-plotting
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [metaprob.trace :as trace]))

(defn trace-as-json
  [tr]
  (let [base (if (trace/trace-has-value? tr)
               (let [v (trace/trace-value tr)]
                 {:value (if (float? v)
                           (format "%f" v)
                           (pr-str v))})
               {})
        children (for [key (trace/trace-keys tr)]
                   (into (trace-as-json (trace/trace-subtrace tr key)) [[:name (pr-str key)]]))]
    (into base [[:children (vec children)]])))

(defn trace-as-json-str
  [tr]
  (json/write-str (trace-as-json tr)))

;; TODO: delete this?
(defn- plot-trace
  ([trace-json] (plot-trace trace-json 600 600))
  ([trace-json s] (plot-trace trace-json s s))
  ([trace-json w h]
   (let [id (str "svg" (java.util.UUID/randomUUID))
         code (format  "drawTrace(\"%s\", %s, %d, %d);" id, (json/write-str (trace-as-json trace-json)), h, w)]
     [:div {:style (format "height: %d; width: %d" (+ h 50) (+ w 100))}
      [:svg {:id id :width (+ w 100) :height h}
        [:script code]]])))
