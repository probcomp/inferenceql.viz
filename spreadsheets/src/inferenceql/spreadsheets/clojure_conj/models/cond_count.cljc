(ns inferenceql.spreadsheets.clojure-conj.models.cond-count
  (:refer-clojure :exclude [map replicate apply])
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen let-traced]]))
  (:require
   [metaprob.prelude :as mp :refer [infer-and-score map replicate apply exp]]
   #?(:clj [metaprob.generative-functions :refer [apply-at at make-generative-function gen let-traced]]
      :cljs [metaprob.generative-functions :refer [apply-at at make-generative-function]])
   [metaprob.distributions :as dist :refer [flip uniform gaussian categorical exactly]]
   [metaprob.inference :as inf]
   [metaprob.trace :as trace]
   [clojure.pprint :refer [pprint]]
   [inferenceql.spreadsheets.clojure-conj.data :as data]
   [inferenceql.spreadsheets.clojure-conj.trace-plotting :as tracep]
   [inferenceql.spreadsheets.clojure-conj.table-plotting :as tablep]
   [inferenceql.multimixture.basic-queries :as bq]
   [inferenceql.multimixture :as mmix]))

(def clj-row-gen
  (gen []
    (let [col-probs {"AWS" 0.5233333333333333
                     "React.js" 0.5765054294175715
                     "Rust" 0.1618819776714514
                     "JavaScript" 0.6977671451355661
                     "C++" 0.2575757575757576
                     "Clojure" 1.0
                     "Java" 0.5382775119617225
                     "Docker" 0.6016666666666667
                     "Kubernetes" 0.2341666666666667}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) flip prob))]
      (zipmap (keys col-probs) vals))))

(def no-clj-row-gen
  (gen []
    (let [col-probs {"AWS" 0.2605176767676768
                     "React.js" 0.3016945432884486
                     "Rust" 0.03001795748131843
                     "JavaScript" 0.6759427677692174
                     "C++" 0.2340381162022823
                     "Clojure" 0.0
                     "Java" 0.4082951978219313
                     "Docker" 0.3086742424242424
                     "Kubernetes" 0.08257575757575758}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) flip prob))]
      (zipmap (keys col-probs) vals))))

(def cond-count-model-row-gen
  (gen []
    (let-traced [clojure-prob 0.01432013612123012
                 clojure-dev (flip clojure-prob)]
      (if clojure-dev
        (at '() clj-row-gen)
        (at '() no-clj-row-gen)))))

(defn make-cond-count-model [row-generator]
  (make-generative-function
   row-generator
   (gen [partial-trace]
     (let [has-clojure (trace/trace-has-value? partial-trace '(:columns "Clojure"))]
       (if (not has-clojure)
         (let [all-clojure-traces (map #(trace/trace-set-value {} '("clojure-dev") %) [true false])
               all-traces (mapv #(merge partial-trace %) all-clojure-traces)
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
                   [v t _] (mp/infer-and-score :procedure row-generator :observation-trace trace)]
               ;; NOTE: why don't we return the trace returned by infer-and-score here?
               ;; UPDATE: now, returning trace frm infer-and-score. Check with Ulli to make sure
               ;; this is ok.
               [v t score])))
        (let [clojure-val (trace/trace-value partial-trace '(:columns "Clojure"))
              trace-with-clojure (trace/trace-set-value partial-trace '("clojure-dev") clojure-val)]
          (gen []
            (mp/infer-and-score :procedure row-generator :observation-trace trace-with-clojure))))))))

(def cond-count-model (make-cond-count-model cond-count-model-row-gen))
