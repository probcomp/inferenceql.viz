(ns inferdb.multimixture-test
  (:require [clojure.test :refer [deftest is testing]]
            [metaprob.distributions :as dist]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.dsl-test :as dsl-test]
            [zane.vega.repl :as vega]))

#_(require )

#_
(let [row-generator (mmix/row-generator dsl-test/multi-mixture)
      samples (repeatedly 600 #(row-generator))
      points (map-indexed (fn [i point]
                            (assoc point "name" (str "P" i)))
                          dsl-test/test-points)]
  (plot! points samples))

#_
(let [row-generator (mmix/row-generator dsl-test/multi-mixture)
      observations (mmix/with-row-values {} {"x" 6})

      samples
      (repeatedly 100 #(first (mmix/importance-resampling :model row-generator
                                                          :observation-trace observations
                                                          :n-particles 100)))

      points (map-indexed (fn [i point]
                            (assoc point "name" (str "P" i)))
                          dsl-test/test-points)]
  (plot! points samples))

#_(let [row-generator (mmix/optimized-row-generator dsl-test/multi-mixture)
        observations (mmix/with-row-values {} {"x" 8})

        samples (repeatedly 100 #(first (mp/infer-and-score :procedure row-generator
                                                            :observation-trace observations)))

        points (map-indexed (fn [i point]
                              (assoc point "name" (str "P" i)))
                            dsl-test/test-points)]
    (plot! points samples))

(defn plot!
  [points samples]
  (vega/vega-lite
   {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
    :width 400
    :height 400
    :datasets {"points" points
               "samples" samples}
    :layer [{:data {:name "samples"}
             :mark {:type "point"
                    :filled true}
             :encoding {:x     {:field "x", :type "quantitative"}
                        :y     {:field "y", :type "quantitative"}
                        :color {:field "a", :type "nominal"}}}
            {:data {:name "points"}
             :mark {:type "point"
                    :shape "cross"
                    :filled true
                    :color "#000000"
                    :opacity 1
                    :size 60}
             :encoding {:x       {:field "x", :type "quantitative"}
                        :y       {:field "y", :type "quantitative"}
                        :tooltip {:field "name", :type "text"}}}
            {:data {:name "points"}
             :mark {:type "text"
                    :dx 12
                    :dy -6}
             :encoding {:text {:field "name", :type "nominal"}
                        :x    {:field "x",    :type "quantitative"}
                        :y    {:field "y",    :type "quantitative"}}}]}))

(deftest smoke
  (testing "does this work at all?"
    (is (map? (mmix/with-row-values {} {"x" 0})))))
