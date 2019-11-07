(ns inferenceql.spreadsheets.clojure-conj.models
  (:refer-clojure :exclude [map replicate apply])
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen let-traced]]))
  (:require
   [metaprob.prelude :as mp :refer [infer-and-score map replicate apply]]
   #?(:clj [metaprob.generative-functions :refer [apply-at at gen let-traced]]
      :cljs [metaprob.generative-functions :refer [apply-at at]])
   [metaprob.distributions :refer [flip uniform gaussian categorical]]
   [metaprob.inference :as inf]
   [metaprob.trace :as trace]
   [clojure.pprint :refer [pprint]]
   [inferenceql.spreadsheets.clojure-conj.data :as data]
   [inferenceql.spreadsheets.clojure-conj.trace-plotting :as tracep]
   [inferenceql.spreadsheets.clojure-conj.table-plotting :as tablep]))

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
                 (at col flip prob))]
      (zipmap (keys col-probs) vals))))

;----------------------------------

(defn so-model-2
  (gen []
    (let-traced [clojure-prob 0.1 ;; TODO add a real probability here
                 clojure-dev (flip clojure-prob)]
      (if clojure-dev
        (so-model-2-clojure)
        (so-model-2-no-clojure)))))

(defn so-model-2-clojure
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
                 (at col flip prob))]
      (zipmap (keys col-probs) vals))))

(defn so-model-2-no-clojure
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
                 (at col flip prob))]
      (zipmap (keys col-probs) vals))))

;----------------------------------

(defn demo-draw-trace []
  (let [[_ trace _] (infer-and-score :procedure so-model-1 :inputs [])]
    (tracep/view-trace trace)))

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

(comment
  (let [[_ trace-with-n-flips _]
        (infer-and-score :procedure flip-n-coins :inputs [5])]
    (tracep/view-trace trace-with-n-flips))
  (infer-and-score :procedure flip-n-coins :inputs [5]))

;------

(comment
  (so-model-1)
  (tracep/view-trace (second (infer-and-score :procedure so-model-1))))
