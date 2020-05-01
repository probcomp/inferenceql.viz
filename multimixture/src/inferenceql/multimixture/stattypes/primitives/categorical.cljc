(ns inferenceql.multimixture.stattypes.primitives.categorical
  (:require [inferenceql.multimixture.gpm.proto :as gpm]
            [inferenceql.multimixture.stattypes.proto :as stattype]
            [inferenceql.multimixture.stattypes.proto.collapsed :as collapsed]
            [inferenceql.multimixture.utils :as iql-utils]
            [inferenceql.multimixture.primitives :as prim]))

(defrecord Categorical [var-name parameters hyperparameters suff-stats]
  gpm/GPM
  (logpdf   [this x]
    (let [counts (:counts suff-stats)
          alpha  (:alpha hyperparameters)
          numer  (Math/log (+ alpha (get counts x)))
          denom  (Math/log (+ (* alpha (count counts))
                              (reduce + (vals counts))))]
      (- numer denom)))
  (simulate [this n-samples]
    (let [p (->> (keys (:p parameters))
                 (reduce (fn [m k]
                           (assoc m k (gpm/logpdf this k)))
                         {})
                 (assoc {} :p))]
    (prim/simulate n-samples :log-categorical p)))

  stattype/StatisticalType
  (incorporate [this x]
    (assoc this :suff-stats (-> suff-stats
                                (update :n inc)
                                (update-in [:counts x] inc))))
  (unincorporate [this x]
    (assoc this :suff-stats (-> suff-stats
                                (update :n dec)
                                (update-in [:counts x] dec))))
  (logpdf-score [this x]
    (let [counts (:counts suff-stats)
          n      (:n suff-stats)
          k      (count counts)
          alpha  (:alpha hyperparameters)
          a      (* k alpha)
          lg     (reduce +
                         (map (fn [v]
                                (prim/lgamma (+ v alpha)))
                              (vals counts)))]
      (+ (prim/lgamma a)
         (- (prim/lgamma (+ a n)))
         lg
         (* -1 k (prim/lgamma alpha)))))

  collapsed/Collapsed
  (export [this]
    (let [counts  (:counts suff-stats)
          alpha   (:alpha hyperparameters)
          weights (map #(+ % alpha) (vals counts))
          z       (reduce + weights)]
    {var-name {:p (zipmap (keys counts) (map #(/ % z) weights))}})))

(defrecord CategoricalColumn [var-name hyperparameters data]
  stattype/Column
  (hyper-grid [this res]
    {:alpha (iql-utils/loglinspace 0.5 (max (count data) 1) res)}))

(defn categorical?
  "Checks if the given stattype is Categorical."
  [stattype]
  (and (record? stattype)
       (instance? Categorical stattype)))

(defn spec->categorical
  "Casts a CrossCat category spec to a Categorical variable."
  ([var-name parameters]
   (let [probs   (:p parameters)
         options (keys probs)
         counts  (repeat (count options) 0)]
     (spec->categorical var-name parameters {:alpha 1} {:n 0
                                                        :counts (zipmap options counts)})))
  ([var-name parameters hyperparameters]
   (let [probs   (:p parameters)
         options (keys probs)
         counts  (repeat (count options) 0)]
     (spec->categorical var-name parameters hyperparameters {:n 0
                                                             :counts (zipmap options counts)})))
  ([var-name parameters hyperparameters suff-stats]
   (->Categorical var-name parameters hyperparameters suff-stats)))


; (let [categorical (->Categorical "test"
;                                  {:p {"red" 0.8 "blue" 0.1 "green" 0.1}}
;                                  {:alpha 1}
;                                  {:n 0
;                                   :counts {"red" 0 "blue" 0 "green" 0}})]
;   (println (frequencies (gpm/simulate categorical 1000)))
;   (-> categorical
;       (stattype/incorporate "red")
;       (stattype/incorporate "red")
;       (stattype/incorporate "blue")
;       (stattype/incorporate "blue")
;       (stattype/unincorporate "red")
;       (#(do
;           (println (gpm/logpdf % "red"))
;           %))
;       ; ; (stattype/logpdf-score "red")))
;       ; (collapsed/export)))
;       (get :suff-stats)))

