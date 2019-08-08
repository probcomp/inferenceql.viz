(ns inferdb.multimixture.search
  (:require [clojure.spec.alpha :as s]
            [kixi.stats.distribution :as kixi]
            [metaprob.distributions :as dist]
            [metaprob.generative-functions :as g :refer [at gen]]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.specification :as spec]))

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

#_(require '[inferdb.multimixture.specification-test :as spec-test])
#_(optimized-row-generator spec-test/mmix)
#_((optimized-row-generator spec-test/mmix))
#_(second (mp/infer-and-score :procedure (optimized-row-generator spec-test/mmix)))
#_(repeatedly 1 #(mp/infer-and-score :procedure (optimized-row-generator spec-test/mmix)))

(s/fdef generate-1col-binary-extension
  :args (s/cat :spec ::spec/multi-mixture
               :row-count pos-int?
               :colunn-key ::spec/column
               :beta-parameters ::spec/beta-parameters))

;; TODO: Implement for real
#?(:cljs (def stubbed-beta
           (mp/make-primitive
            (fn [_ _]
              1)
            (fn [_ [_ _]]
              (mp/log 1)))))

;; https://rosettacode.org/wiki/Gamma_function#Clojure
(defn gamma
  "Returns Gamma(z + 1 = number) using Lanczos approximation."
  [number]
  (if (< number 0.5)
    (/ Math/PI (* (Math/sin (* Math/PI number))
                  (gamma (- 1 number))))
    (let [n (dec number)
          c [0.99999999999980993 676.5203681218851 -1259.1392167224028
             771.32342877765313 -176.61502916214059 12.507343278686905
             -0.13857109526572012 9.9843695780195716e-6 1.5056327351493116e-7]]
      (* (Math/sqrt (* 2 Math/PI))
         (Math/pow (+ n 7 0.5) (+ n 0.5))
         (Math/exp (- (+ n 7 0.5)))
         (+ (first c)
            (apply + (map-indexed #(/ %2 (+ n %1 1)) (next c))))))))

(defn beta-pdf
  [v [alpha beta]]
  (let [b (fn [alpha beta]
            (/ (* (gamma alpha)
                  (gamma beta))
               (gamma (+ alpha beta))))]
    (/ (* (mp/expt v
                   (- alpha 1))
          (mp/expt (- 1 v)
                   (- beta 1)))
       (b alpha beta))))

(def cljs-beta
  (mp/make-primitive
   (fn [alpha beta]
     (= 1 (first (kixi/sample 1 (kixi/beta {:alpha alpha :beta beta})))))
   (fn [v [alpha beta]]
     (beta-pdf (if v 1 0) [alpha beta]))))

#_(mp/infer-and-score :procedure cljs-beta :inputs [0.001 0.001])

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
                                                                            #?(:clj dist/beta
                                                                               :cljs cljs-beta)
                                                                            alpha
                                                                            beta))))
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
    (nth particles (dist/log-categorical (map (fn [[_ _ s]] s) particles)))))

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
        predictions (map #(score-rows % unknown-rows new-column-key)
                         specs)]
    (mapv (fn [i]
            (/ (transduce (map #(nth % i))
                          +
                          predictions)
               n-models))
          (range (count unknown-rows)))))

#_
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

#_(require 'inferdb.spreadsheets.model)
#_(require 'cljs.pprint)
#_(cljs.pprint/pprint (insert-column inferdb.spreadsheets.model/spec [] "z" {:alpha 0.001 :beta 0.001}))


#_(cljs.pprint/pprint [{:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 0}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 1}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 2}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 3}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 4}, 1 {:value 1}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 0}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 0}, 2 {:value 1}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 1}, 2 {:value 0}}} {:columns {"c#" {:value "True"}, "new property" {:value true}}, :cluster-assignments-for-view {0 {:value 5}, 1 {:value 1}, 2 {:value 1}}}])
