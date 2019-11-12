(ns inferenceql.spreadsheets.clojure-conj.models
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

(def t-flip
  (gen [weight]
    (let [rounded-weight (format "%.3f" weight)
          trace-addr (str "flip: " rounded-weight)]
      (at trace-addr flip weight))))

;----------------------------------

;; TODO look at lecture notes from Ulli.

(def so-model-1
  (gen []
    (let [col-probs {"AWS" 0.2639442971479545
                     "React.js"  0.3056965334809757
                     "Rust"  0.03190626819993377
                     "JavaScript" 0.6762552958238646
                     "C++" 0.2343751784307232
                     "Clojure" 0.01432013612123012
                     "Java" 0.4101565622537656
                     "Docker" 0.3123621676536908
                     "Kubernetes" 0.08468171568748915}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) flip prob))]
      (zipmap (keys col-probs) vals))))

(def so-model-1-row-generator
  (make-generative-function
   so-model-1
   (gen [partial-trace]
     (gen []
       (let [[v t s] (mp/infer-and-score :procedure so-model-1 :observation-trace partial-trace)]
         ;; NOTE: returning the new trace, not the partial trace as in search.clj
         ;; Maybe that was a bug in search.clj?
         [v t s])))))


;(so-model-2-row-generator)
;(tracep/view-trace (second (infer-and-score :procedure so-model-2)))
;(tracep/view-trace (second (infer-and-score :procedure so-model-2-row-generator)))

;--------------------------------

(def so-model-2-clojure
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

(def so-model-2-no-clojure
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

(def so-model-2
  (gen []
    (let-traced [clojure-prob 0.01432013612123012
                 clojure-dev (flip clojure-prob)]
      (if clojure-dev
        (at "clj-count-model" so-model-2-clojure)
        (at "not-clj-count-model" so-model-2-no-clojure)))))

(def so-model-2-row-generator
  (make-generative-function
   so-model-2
   (gen [partial-trace]
     (let [has-clojure (trace/trace-has-value? partial-trace '("clojure-dev"))]
       (if (not has-clojure)
         (let [all-clojure-traces (map #(trace/trace-set-value {} '("clojure-dev") %) [true false])
               all-traces (mapv #(merge partial-trace %) all-clojure-traces)
               all-logscores  (mapv #(last (mp/infer-and-score :procedure so-model-2
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
                   v     (first (mp/infer-and-score :procedure so-model-2
                                                    :observation-trace trace))]
               ;; NOTE: why don't we return the trace returned by infer-and-score here?
               [v trace score])))
        (gen []
          (mp/infer-and-score :procedure so-model-2 :observation-trace partial-trace)))))))

;----------------------------------

(defn demo-simple-table-plot [n]
  (let [rows (repeatedly n so-model-1)
        col-order (->> (keys (first rows))
                       (sort)
                       (map vector (range)))
        make-row (fn [row-id row]
                     (for [[col-idx col-name] col-order]
                       {:row row-id :col col-idx :val (get row col-name) :col-name col-name}))]
    (tablep/spec-with-data (mapcat make-row (range) rows))))

(defn demo-partioned-table-plot []
  (let [row-group-1 (repeatedly 20 so-model-1)
        row-group-2 (repeatedly 15 so-model-1)
        row-group-3 (repeatedly 5 so-model-1)
        all-groups [row-group-1 row-group-2 row-group-3]

        colors [["blue" "lightblue"] ["green" "lightgreen"] ["firebrick" "salmon"]]]
    (tablep/spec-with-mult-partitions all-groups colors)))

(defn demo-partioned-table-plot-2 []
  (let [all-groups [data/data-subset-clj data/data-subset-not-clj]
        colors [["blue" "lightblue"] ["green" "lightgreen"]]]
    (tablep/spec-with-mult-partitions all-groups colors)))

;(demo-simple-table-plot 20)
;(demo-partioned-table-plot)
;(demo-draw-trace)

;(demo-partioned-table-plot-2)

;----------------------------------------------

(comment
  (so-model-1)
  (tracep/view-trace (second (infer-and-score :procedure so-model-1)))

  (so-model-2)
  (tracep/view-trace (second (infer-and-score :procedure so-model-2))))

;----------------------------------------------

(comment
  (def demo-uncond-model
    (gen []
      (let [col-probs {"AWS" 0.45
                       "React"  0.3
                       "Java" 0.9}
            vals (for [[col prob] col-probs]
                   (at col flip prob))]
        (zipmap (keys col-probs) vals))))

  (def trace  {"AWS" {:value true}, "React" {:value true}, "Java" {:value true}})
  (def trace  {"React" {:value true}, "Java" {:value true}})

  (let [[v t s] (infer-and-score :procedure demo-uncond-model :observation-trace trace)]
    (println t)
    (println "score: " (exp s))
    (println "manual score: " (* 0.6 0.3 0.9))
    (= (* 0.6 0.3 0.9) (exp s)))

  ;--------------------------------

  (def demo-uncond-model
    (gen []
      (let [col-probs {"AWS" 0.45
                       "React"  0.3}
            vals (for [[col prob] col-probs]
                   (at col flip prob))]
        (zipmap (keys col-probs) vals))))

  (def trace  {"AWS" {:value true} "React" {:value true}})

  (let [[v t s] (infer-and-score :procedure demo-uncond-model :observation-trace trace)]
    (println t)
    (println "score: " (exp s))
    (println "manual score: " (* 0.6 0.3 0.9))
    (= (* 0.6 0.3 0.9) (exp s)))

  (* 0.45 0.3)

  ;--------------------------------

  (def experimental-model
    (gen []
      (let-traced [clojure-prob 0.4
                   clojure-dev (flip clojure-prob)]
        (if clojure-dev
          {"AWS" (at "AWS" flip 0.2)
           "C++" (at "C++" flip 0.3)}
          {"AWS" (at "AWS" flip 0.6)
           "C++" (at "C++" flip 0.7)}))))

  (experimental-model)


  (def trace {"AWS" {:value true}})
  (let [[v t s] (infer-and-score :procedure experimental-model :observation-trace trace)]
    (println t)
    (exp s))

  ;clojure false case
  (/ (* 0.6 0.6 0.7) (* 0.6 0.7))

  (/ (* 0.4 0.2 0.3) (* 0.4 0.3)))

  ;-----------------------------------
