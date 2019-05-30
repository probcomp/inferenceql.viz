(ns inferdb.plotting.generate-vljson
  (:require [cheshire.core :as cheshire]))

(defn scatter-plot-json
  [columns values test-points domain title]
    (cheshire/generate-string
     {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
      :background "white"
      :data {:values (concat values test-points)}
      :title title
      :layer [{:width 700
               :height 700
               :mark {:type "point" :filled true}
               :encoding {
                  :x {:field (first columns)
                      :title (name (first columns))
                      :type "quantitative"
                      :scale {:domain domain}},
                  :y {:field (second columns)
                      :title (name (second columns))
                      :type "quantitative"
                      :scale {:domain domain}}
                  :color {:field "a"
                          :type "nominal"}
                  :shape {:field "b"
                          :type "nominal"}}}
              {:width 700
               :height 700
               :mark {:type "text" :dx 15 :dy -10}
               :encoding {
                  :text {:field "test-point"
                      :type "nominal"}
                  :x {:field "tx"
                      :type "quantitative"}
                  :y {:field "ty"
                      :type "quantitative"}}}
              {:width 700
               :height 700
               :mark {:type "square"
                      :filled true
                      :color "#030303"
                      :size 50}
               :encoding {
                  :x {:field "tx"
                      :type "quantitative"}
                  :y {:field "ty"
                      :type "quantitative"}}}]}))

(defn bar-plot
  [samples title n]
    (let [get-counts (fn [item] {
                           "category" (first (vals (first item)))
                           ;; XXX: the 1000 below should be supplied as param.
                           "probability" (float (/ (second item) n))})]
      (cheshire/generate-string
       {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
        :background "white"
        :data {:values (map get-counts (frequencies samples))}
        :width 200
        :height 300
        :mark "bar"
        :title title
        :encoding {
           :y {
             :field "category"
             :type "ordinal"}
           :x {
             :field "probability"
             :type "quantitative"}}})))

(defn hist-plot
  [samples columns title]
    (let
      [xlabel (str (first columns) " (binned) ")]
      (cheshire/generate-string
       {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
        :background "white"
        :data {:values  samples}
        :width 200
        :height 300
        :mark "bar"
        :title title
        :transform [
           {
             :bin {:binned true :step 1},
             :field (first columns)
             :as xlabel
           }
         ]
        :encoding {
           :x {:field xlabel
               :title (name (first columns))
               :bin {:binned true :step 1}
               :type "quantitative"}
           :x2 {:field (str xlabel "_end")}
           :y {:aggregate "count"
               :type "quantitative"
               }
           :color {:field (second columns)
                   :type "nominal"}}})))

