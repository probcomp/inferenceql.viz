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

(def count-model-row-gen
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

(defn make-count-model [row-generator]
  (make-generative-function
   row-generator
   (gen [partial-trace]
     (gen []
       (let [[v t s] (mp/infer-and-score :procedure row-generator :observation-trace partial-trace)]
         ;; NOTE: returning the new trace, not the partial trace as in search.clj
         ;; Maybe that was a bug in search.clj?
         [v t s])))))

(def count-model (make-count-model count-model-row-gen))

;--------------------------------

(def clj-row-gen
  (gen []
    (let [col-probs {"AWS" 0.40
                     "Clojure" 1.0
                     "Java" 0.25}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) flip prob))]
      (zipmap (keys col-probs) vals))))

(def no-clj-row-gen
  (gen []
    (let [col-probs {"AWS" 0.80
                     "Clojure" 0
                     "Java" 0.90}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) flip prob))]
      (zipmap (keys col-probs) vals))))

(def cond-count-model-row-gen
  (gen []
    (let-traced [clojure-prob 0.20
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
                   v     (first (mp/infer-and-score :procedure row-generator
                                                    :observation-trace trace))]
               ;; NOTE: why don't we return the trace returned by infer-and-score here?
               [v trace score])))
        (let [clojure-val (trace/trace-value partial-trace '(:columns "Clojure"))
              trace-with-clojure (trace/trace-set-value partial-trace '("clojure-dev") clojure-val)]
          (gen []
            (mp/infer-and-score :procedure row-generator :observation-trace trace-with-clojure))))))))

(def cond-count-model (make-cond-count-model cond-count-model-row-gen))

;--------------------------

;; Testing Simulate()

(defn get-frequency-of [rows col-name]
  (let [results (frequencies (map #(get % col-name) rows))
        total (reduce + (vals results))]
    (double (/ (get results true) total))))

(def cc-model-test (bq/simulate cond-count-model {"Clojure" false} 5000))
(get-frequency-of cc-model-test "AWS")

(def cc-model-test (bq/simulate cond-count-model {"AWS" true} 5000))
(get-frequency-of cc-model-test "Clojure")

(def cc-model-test (bq/simulate cond-count-model {"AWS" true} 5000))
(get-frequency-of cc-model-test "Java")

;----------------------------------


(defn demo-simple-table-plot [n]
  (let [rows (repeatedly n count-model)
        col-order (->> (keys (first rows))
                       (sort)
                       (map vector (range)))
        make-row (fn [row-id row]
                     (for [[col-idx col-name] col-order]
                       {:row row-id :col col-idx :val (get row col-name) :col-name col-name}))]
    (tablep/spec-with-data (mapcat make-row (range) rows))))

(defn demo-partioned-table-plot []
  (let [row-group-1 (repeatedly 20 count-model)
        row-group-2 (repeatedly 15 count-model)
        row-group-3 (repeatedly 5 count-model)
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
  (count-model)
  (tracep/view-trace (second (infer-and-score :procedure count-model))))

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
