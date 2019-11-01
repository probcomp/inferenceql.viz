(ns inferenceql.spreadsheets.clojure-conj.trial
  (:require-macros [metaprob.generative-functions :refer [gen let-traced]])
  (:require
   [metaprob.prelude :as mp :refer [infer-and-score]]
   [metaprob.generative-functions :as g :refer [apply-at at]]

   [metaprob.distributions :refer [flip uniform gaussian categorical]]
   [metaprob.inference :as inf]
   [metaprob.trace :as trace]
   [clojure.pprint :refer [pprint]]

   [inferenceql.spreadsheets.clojure-conj.data :as data]))

   ;; prelude distributions generative-functions

(def flip-n-coins
  (gen [n]
    (let-traced [tricky (flip 0.1)
                 p (if tricky (uniform 0 1) 0.5)]
      (map (fn [i] (at i flip p)) (range n)))))

(defn coin-flips-demo-n-flips
  [n]
  (let [[_ trace-with-n-flips _]
        (infer-and-score :procedure flip-n-coins
                         :inputs [n])]
    (infer-and-score :procedure flip-n-coins
                     :inputs [n]
                     :observation-trace trace-with-n-flips)))
