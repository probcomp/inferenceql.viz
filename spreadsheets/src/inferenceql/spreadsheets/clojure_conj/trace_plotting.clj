(ns inferenceql.spreadsheets.clojure-conj.trace-plotting
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [metaprob.trace :as trace]
   [hiccup.core :as hiccup]
   [clojure.java.browse :as browse]))

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

(defn plot-trace
  ([trace-json] (plot-trace trace-json 600 600))
  ([trace-json s] (plot-trace trace-json s s))
  ([trace-json w h]
   (let [id (str "svg" (java.util.UUID/randomUUID))
         code (format  "drawTrace(\"%s\", %s, %d, %d);" id, (json/write-str (trace-as-json trace-json)), h, w)]
     [:html
      [:head
       [:meta {:charset "UTF-8"}]
       [:title "Plotting"]]
      [:body
       [:script {:src "js/plotly-latest.min.js"}]
       [:script {:src "js/plot-trace.js"}]

       [:div {:style (format "height: %d; width: %d" (+ h 50) (+ w 100))}
        [:svg {:id id :width (+ w 100) :height h}
          [:script code]]]]])))

(defn trace-to-html
  [trace]
  (hiccup/html (plot-trace trace)))

(defn trace-to-file
  [trace filename]
  (let [html-string (trace-to-html trace)]
    (spit filename html-string)))

(defn view-trace
  [trace]
  (let [base-filename "spreadsheets/src/inferenceql/spreadsheets/clojure_conj/plotting/"
        id (.toString (java.util.UUID/randomUUID))
        filename (str base-filename id ".html")]
    (trace-to-file trace filename)
    (browse/browse-url filename)))
