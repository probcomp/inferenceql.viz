(ns inferenceql.spreadsheets.clojure-conj.trial
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen let-traced]]))
  (:require
   [metaprob.prelude :as mp :refer [infer-and-score]]
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
    (let [row {"AWS" (at "AWS" flip 0.2639442971479545),
               "React.js" (at "React.js" flip 0.3056965334809757),
               "Rust" (at "Rust" flip 0.03190626819993377),
               "JavaScript" (at "JavaScript" flip 0.6762552958238646),
               "C++" (at "C++" flip 0.2343751784307232),
               "Clojure" (at "Clojure" flip 0.01432013612123012),
               "Java" (at "Java" flip 0.4101565622537656),
               "Docker" (at "Docker" flip 0.3123621676536908),
               "Kubernetes" (at "Kubernetes" flip 0.08468171568748915)}]
      row)))

(defn draw-trace-command []
  ;; Use this with `drawTrace` in the browser.
  (let [[_ trace _] (infer-and-score :procedure so-model-1 :inputs [])]
    (print (tracep/trace-as-json-str trace))))

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
