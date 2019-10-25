(ns inferenceql.multimixture.search
  (:require [clojure.spec.alpha :as s]
            [metaprob.distributions :as dist]
            [metaprob.generative-functions :as g :refer [at gen]]
            [metaprob.prelude :as mp]
            [inferenceql.distributions :as idbdist]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.basic-queries :as bq]
            [inferenceql.multimixture.specification :as spec]))

(s/fdef optimized-row-generator
  :args (s/cat :spec ::spec/multi-mixture))

#_(clojure.spec.test.alpha/instrument)

#?(:cljs (enable-console-print!))

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

#_(require '[inferenceql.multimixture.specification-test :as spec-test])
#_(optimized-row-generator spec-test/mmix)
#_((optimized-row-generator spec-test/mmix))
#_(second (mp/infer-and-score :procedure (optimized-row-generator spec-test/mmix)))
#_(repeatedly 1 #(mp/infer-and-score :procedure (optimized-row-generator spec-test/mmix)))

(s/fdef generate-1col-binary-extension
  :args (s/cat :spec ::spec/multi-mixture
               :row-count pos-int?
               :colunn-key ::spec/column
               :beta-parameters ::spec/beta-parameters))

(def generate-1col-binary-extension
  (gen [spec row-count column-key {:keys [alpha beta]}]
    (let [view-idx (at :view dist/categorical (mmix/uniform-categorical-params (count (:views spec))))
          new-spec (-> spec
                       (assoc-in [:vars column-key] :binary)
                       (update-in [:views view-idx]
                                  (fn [clusters]
                                    (vec (map-indexed (fn [i cluster]
                                                        (update cluster :parameters
                                                                #(assoc % column-key
                                                                        (at `(:cluster-parameters ~i)
                                                                            #?(:clj idbdist/beta
                                                                               :cljs idbdist/beta)
                                                                            #_
                                                                            alpha
                                                                            #_
                                                                            beta
                                                                            {:alpha alpha
                                                                             :beta beta}))))
                                                      clusters)))))
          new-row-generator (optimized-row-generator new-spec)]
      (doseq [i (range row-count)]
        (at `(:rows ~i) new-row-generator))
      new-spec)))

#_(let [rows [{"x" 0, "z" true}
              {"x" 5, "z" false}]
        spec {:vars {"x" :gaussian}
              :views [[{:probability 0.75
                        :parameters {"x" {:mu 0 :sigma 1}}}
                       {:probability 0.25
                        :parameters {"x" {:mu 5 :sigma 1}}}]]}]
    #_
    (expound.alpha/expound ::spec/multi-mixture spec)
    #_
    (mp/infer-and-score :procedure (optimized-row-generator spec))
    #_
    (mp/infer-and-score :procedure generate-1col-binary-extension
                        :inputs [spec (count rows) "y" {:alpha 0.001 :beta 0.001}])
    (generate-1col-binary-extension spec 10 "z" {:alpha 0.001 :beta 0.001}))

(defn importance-resampling
  [& {:keys [model inputs observation-trace n-particles]
      :or {inputs [], observation-trace {}, n-particles 1}}]
  (let [particles (mp/replicate n-particles
                                #(mp/infer-and-score :procedure model
                                                     :inputs inputs
                                                     :observation-trace observation-trace))]
    (nth particles (dist/log-categorical (map last particles)))))

(s/fdef insert-column
  :args (s/cat :spec ::spec/multi-mixture
               :rows ::spec/rows
               :column-key ::spec/column
               :beta-params ::spec/beta-parameters))

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
                          :n-particles 100)))

#_(insert-column spec-test/mmix [{"z" true}] "z" {:alpha 0.001 :beta 0.001})

(defn score-rows
  [spec rows new-column-key]
  (let [new-column-view (spec/view-index-for-variable spec new-column-key)
        row-generator (optimized-row-generator spec)]
    (mapv (fn [row]
            (let [[_ trace _] (mp/infer-and-score :procedure row-generator
                                                  :observation-trace (mmix/with-row-values {} row))
                  cluster-idx (get-in trace [:cluster-assignments-for-view new-column-view :value])]
              (get-in spec [:views new-column-view cluster-idx :parameters new-column-key])))
          rows)))

(defn transpose
  [coll]
  (apply map vector coll))

(defn constraints-for-scoring-p
  [target-col constraint-cols row]
  (->> (if (= (first constraint-cols) "ROW")
         (remove (comp #{target-col} key)
                 row)
         (select-keys row constraint-cols))
       (remove (comp nil? val))
       (into {})))

(defn  score-row-probability
  [row-generator target-col constraint-cols row]
  (let [target (select-keys row [target-col])
        constraints (constraints-for-scoring-p target-col constraint-cols row)]
    (if (nil? (get target target-col))
      1
      (Math/exp (bq/logpdf row-generator target constraints)))))

(defn anomaly-search
  [spec target-col conditional-cols data]
  (let [row-generator (optimized-row-generator spec)]
    (map #(score-row-probability row-generator target-col conditional-cols %) data)))

(defn search
  [spec new-column-key known-rows unknown-rows n-models beta-params]
  (let [specs (repeatedly n-models #(insert-column spec known-rows new-column-key beta-params))
        predictions (mapv #(score-rows % unknown-rows new-column-key)
                          specs)]
    (into []
          (map #(/ (reduce + %)
                   n-models))
          (transpose predictions))))

(comment

  (let [unknown-rows [{"x" 0}
                      {"x" 5}]
        known-rows [{"x" 0 "y" true}
                    {"x" 5 "y" false}]
        spec {:vars {"x" :gaussian
                     "z" :gaussian}
              :views [[{:probability 0.5
                        :parameters {"x" {:mu 0 :sigma 2}}}
                       {:probability 0.5
                        :parameters {"x" {:mu 5 :sigma 2}}}]
                      [{:probability 0.5
                        :parameters {"z" {:mu 0 :sigma 2}}}
                       {:probability 0.5
                        :parameters {"z" {:mu 5 :sigma 2}}}]]}]
    (search spec "y" known-rows unknown-rows 10 {:alpha 0.001 :beta 0.001}))

  (let [unknown-rows [{"x" true}
                      {"x" false}]
        known-rows [{"x" true "y" true}]
        beta-params {:alpha 0.01 :beta 0.01}
        spec {:vars {"x" :categorical}
              :views [[{:probability 0.5
                        :parameters {"x" {true 1 false 0}}}
                       {:probability 0.5
                        :parameters {"x" {true 0 false 1}}}]]}]
    #_(generate-1col-binary-extension spec 100 "y" beta-params)
    #_(insert-column spec known-rows "y" beta-params)
    #_(mp/exp (last (mp/infer-and-score :procedure (optimized-row-generator spec)
                                        :observation-trace (mmix/with-row-values {} {"x" true}))))
    #_ (update-in (mp/infer-and-score :procedure (mmix/row-generator spec)
                                      :observation-trace #_{} (mmix/with-row-values {} {"x" true}))
                  [2]
                  mp/exp)
    #_(mp/exp (last (mp/infer-and-score :procedure (optimized-row-generator spec)
                                        :observation-trace (mmix/with-row-values {} {"x" true}))))
    (search spec "y" known-rows unknown-rows 100 beta-params))

  (require '[clojure.spec.test.alpha])
  (require 'cljs.spec.test.alpha)

  (dist/beta 0.001 0.001)

  (frequencies (repeatedly 100 #(dist/categorical {true 1 false 0})))

  (require '[inferenceql.spreadsheets.model :as model])
  (require '[inferenceql.spreadsheets.data :as data])
  (zipmap (map #(select-keys % ["git"]) data/nyt-data)
          (search model/spec "new" [{"git" "True", "new" true}
                                    {"git" "False", "new" false}]
                  data/nyt-data 5 {:alpha 0.001 :beta 0.001}))

  (second (mp/infer-and-score :procedure (mmix/row-generator model/spec)))

  (require 'inferenceql.spreadsheets.model)
  (require 'cljs.pprint)
  (cljs.pprint/pprint (insert-column inferenceql.spreadsheets.model/spec [] "z" {:alpha 0.001 :beta 0.001}))


  (cljs.pprint/pprint [{:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 1}, 2 {:value 1}}}]))