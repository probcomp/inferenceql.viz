(ns inferenceql.spreadsheets.clojure-conj.models
  (:refer-clojure :exclude [map replicate apply])
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen let-traced]]))
  (:require
   [metaprob.prelude :as mp :refer [infer-and-score map replicate apply]]
   #?(:clj [metaprob.generative-functions :refer [apply-at at gen let-traced]]
      :cljs [metaprob.generative-functions :refer [apply-at at]])
   [metaprob.distributions :refer [flip uniform gaussian categorical exactly]]
   [metaprob.inference :as inf]
   [metaprob.trace :as trace]
   [clojure.pprint :refer [pprint]]
   [inferenceql.spreadsheets.clojure-conj.data :as data]
   [inferenceql.spreadsheets.clojure-conj.trace-plotting :as tracep]
   [inferenceql.spreadsheets.clojure-conj.table-plotting :as tablep]))

(def t-flip
  (gen [weight]
    (let [rounded-weight (format "%.3f" weight)
          trace-addr (str "flip: " rounded-weight)]
      (at trace-addr flip weight))))

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
                 (at col t-flip prob))]
      (zipmap (keys col-probs) vals))))

;----------------------------------

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
                 (at col t-flip prob))]
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
                 (at col t-flip prob))]
      (zipmap (keys col-probs) vals))))

(def so-model-2
  (gen []
    (let-traced [clojure-prob 0.01432013612123012
                 clojure-dev (t-flip clojure-prob)]
      (if clojure-dev
        (at "clj-count-model" so-model-2-clojure)
        (at "not-clj-count-model" so-model-2-no-clojure)))))

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

;(demo-simple-table-plot 20)
;(demo-partioned-table-plot)
;(demo-draw-trace)

;----------------------------------------------

(comment
  (so-model-1)
  (tracep/view-trace (second (infer-and-score :procedure so-model-1)))

  (so-model-2)
  (tracep/view-trace (second (infer-and-score :procedure so-model-2))))
