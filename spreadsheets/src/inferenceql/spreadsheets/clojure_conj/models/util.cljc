(ns inferenceql.spreadsheets.clojure-conj.models.util
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
