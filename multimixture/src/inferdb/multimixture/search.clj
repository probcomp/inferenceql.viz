(ns inferdb.multimixture.search
  (:require [metaprob.distributions :as dist]
            [metaprob.generative-functions :as g :refer [at gen]]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.specification :as spec]))

(defn optimized-row-generator
  [spec]
  (let [row-generator (mmix/row-generator spec)]
    (g/make-generative-function
     row-generator
     (gen [partial-trace]
       (let [all-latents    (mmix/all-latents spec)
             all-traces     (mapv #(merge partial-trace %)
                                  all-latents)
             all-logscores  (mapv #(last (mp/infer-and-score :procedure row-generator
                                                             :observation-trace %))
                                  all-traces)
             all-scores (map mp/exp all-logscores)
             all-zeroes (every? #(== 0 %) all-scores)
             log-normalizer (if all-zeroes ##-Inf (dist/logsumexp all-logscores))
             score          log-normalizer
             categorical-params (if all-zeroes
                                  (mmix/uniform-categorical-params (count all-scores))
                                  (dist/normalize-numbers all-scores))]
         (gen []
           (let [i     (dist/categorical categorical-params)
                 trace (nth all-traces i)
                 v     (first (mp/infer-and-score :procedure row-generator
                                                  :observation-trace trace))]
             [v trace score])))))))

#_(require '[inferdb.multimixture.dsl-test :as dsl-test])
#_(optimized-row-generator dsl-test/multi-mixture)
#_((optimized-row-generator dsl-test/multi-mixture))
#_(repeatedly 100 #(mp/infer-and-score :procedure (optimized-row-generator dsl-test/multi-mixture)))

(def generate-1col-binary-extension
  (gen [spec row-count column-key {:keys [alpha beta]}]
    (let [view-idx (at :view dist/categorical (mmix/uniform-categorical-params (count spec)))
          new-spec (-> spec
                       (assoc-in [view-idx :vars column-key] :binary)
                       (update-in [view-idx :clusters]
                                  (fn [clusters]
                                    (vec (map-indexed (fn [i cluster]
                                                        (update cluster :parameters
                                                                #(assoc % column-key
                                                                        [(at `(:cluster-parameters ~i)
                                                                             dist/beta alpha beta)])))
                                                      clusters)))))
          new-row-generator (optimized-row-generator new-spec)]
      (doseq [i (range row-count)]
        (at `(:rows ~i) new-row-generator))
      new-spec)))

#_(let [rows [{"x" 0, "y" true}
              {"x" 5, "y" false}]
        spec [{:vars {"x" :gaussian}
               :clusters [{:probability 0.75
                           :parameters {"x" [0 1]}}
                          {:probability 0.25
                           :parameters {"x" [5 1]}}]}]]
    #_
    (mp/infer-and-score :procedure (optimized-row-generator spec))
    (mp/infer-and-score :procedure generate-1col-binary-extension
                        :inputs [spec (count rows) "y" {:alpha 0.001 :beta 0.001}]))

(defn importance-resampling
  [& {:keys [model inputs observation-trace n-particles]
      :or {inputs [], observation-trace {}, n-particles 1}}]
  (let [particles (mp/replicate n-particles
                                (fn []
                                  (mp/infer-and-score :procedure model
                                                      :inputs inputs
                                                      :observation-trace observation-trace)))]
    (nth particles (dist/log-categorical (map (fn [[_ _ s]] s) particles)))))

(defn insert-column
  "Takes a multimixture specification, views, and a set of rows that have a value
  in the new column that is being added. Returns an updated multimixture
  specification."
  [spec rows column-key beta-params]
  (first
   ;; TODO: Setting n-particles to 1 causes IOB errors
   (importance-resampling :model generate-1col-binary-extension
                          :inputs [spec (count rows) column-key beta-params]
                          :observation-trace (mmix/with-rows {} rows)
                          :n-particles 10)))

(defn score-rows
  [spec rows new-column-key]
  (let [new-column-view (spec/view-index-for-variable spec new-column-key)
        row-generator (optimized-row-generator spec)]
    (mapv (fn [row]
            (let [[_ trace _] (mp/infer-and-score :procedure row-generator
                                                  :observation-trace (mmix/with-row-values {} row))
                  cluster-idx (get-in trace [:cluster-assignments-for-view new-column-view :value])]
              (get-in spec [new-column-view :clusters cluster-idx :parameters new-column-key 0])))
          rows)))

#_(let [rows [{"x" 0}
              {"x" 5}]
        spec [{:vars {"x" :gaussian
                      "y" :binary}
               :clusters [{:probability 0.75
                           :parameters {"x" [0 1]
                                        "y" [0]}}
                          {:probability 0.25
                           :parameters {"x" [5 1]
                                        "y" [1]}}]}]]
    (score-rows spec rows "y"))

(defn search
  [spec new-column-key known-rows unknown-rows n-models beta-params]
  (let [specs (repeatedly n-models #(insert-column spec known-rows new-column-key beta-params))
        scores (map (fn [spec]
                      (score-rows spec unknown-rows new-column-key))
                    specs)]
    (mapv (fn [i]
            (/ (reduce + (map #(nth % i) scores))
               n-models))
          (range (count unknown-rows)))))

#_(let [unknown-rows (repeat 100 {"x" 0})
        known-rows [{"x" 0 "y" true}
                    {"x" 5 "y" false}]
        spec [{:vars {"x" :gaussian}
               :clusters [{:probability 0.5
                           :parameters {"x" [0 2]}}
                          {:probability 0.5
                           :parameters {"x" [5 2]}}]}
              {:vars {"z" :gaussian}
               :clusters [{:probability 0.5
                           :parameters {"z" [0 2]}}
                          {:probability 0.5
                           :parameters {"z" [5 2]}}]}]]
    (time (search spec "y" known-rows unknown-rows 10 {:alpha 0.001 :beta 0.001})))
