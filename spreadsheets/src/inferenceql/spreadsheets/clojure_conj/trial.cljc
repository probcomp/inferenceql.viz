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

;; Use this with `drawTrace` in the browser.
(let [[_ trace _] (infer-and-score :procedure so-model-1 :inputs [])]
  (print (tracep/trace-as-json-str trace)))

(defn spec-simulated-rows [n]
  (let [rows (repeatedly n so-model-1)
        col-order (->> (keys (first rows))
                       (sort)
                       (map vector (range)))
        make-row (fn [row-id row]
                     (for [[col-idx col-name] col-order]
                       {:row row-id :col col-idx :val (get row col-name) :col-name col-name}))]
    (tablep/spec-with-data (mapcat make-row (range) rows))))

(defn spec-simulated-partitioned [n]
  (let [row-group-1 (map #(assoc % "group" 1) (repeatedly n so-model-1))
        row-group-2 (map #(assoc % "group" 2) (repeatedly n so-model-1))
        row-group-3 (map #(assoc % "group" 3) (repeatedly n so-model-1))
        all-groups [row-group-1 row-group-2 row-group-3]

        col-order (->> (keys (first row-group-1))
                       (remove #{"group"})
                       (sort)
                       (map vector (range)))
        col-names (map second col-order)

        separator-group [(zipmap col-names (repeat nil))]

        joined-groups (interpose separator-group all-groups)
        all-rows (flatten joined-groups)

        make-row-elems (fn [row-id row]
                         (for [[col-idx col-name] col-order]
                           (let [sep-cell (nil? (get row col-name))
                                 group-id (get row "group")]
                             {:row row-id :col col-idx :val (get row col-name) :col-name col-name :separator sep-cell :group group-id})))]
    (tablep/spec-with-data (mapcat make-row-elems (range) all-rows))))

(defn spec-simulated-partitioned-v2 [n]
  (let [row-group-1 (repeatedly n so-model-1)
        row-group-2 (repeatedly n so-model-1)
        row-group-3 (repeatedly n so-model-1)
        all-groups [row-group-1 row-group-2 row-group-3]

        colors [["blue" "lightblue"] ["green" "lightgreen"] ["red" "lightred"]]]
    (tablep/spec-with-mult-partitions all-groups colors)))

;(spec-simulated-rows 20)
;(spec-simulated-partitioned 3)

;(spec-simulated-partitioned-v2 3)
