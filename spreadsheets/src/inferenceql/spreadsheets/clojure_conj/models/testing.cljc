(ns inferenceql.spreadsheets.clojure-conj.models.testing
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
   [inferenceql.spreadsheets.clojure-conj.trace-plotting.core :as tracep]
   [inferenceql.multimixture.basic-queries :as bq]
   [inferenceql.multimixture :as mmix]))

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
