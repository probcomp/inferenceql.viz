(ns inferenceql.multimixture.stattypes.primitives.bernoulli
  (:require [inferenceql.multimixture.gpm.proto :as gpm]
            [inferenceql.multimixture.stattypes.proto :as stattype]
            [inferenceql.multimixture.stattypes.proto.collapsed :as collapsed]
            [inferenceql.multimixture.utils :as iql-utils]
            [inferenceql.multimixture.primitives :as prim]))

(defrecord Bernoulli [var-name parameters hyperparameters suff-stats]
  gpm/GPM
  (logpdf   [this x]
    (let [x-sum  (get suff-stats :x-sum 0)
          n      (get suff-stats :n 0)
          alpha' (+ (:alpha hyperparameters) x-sum)
          beta'  (+ (:beta  hyperparameters) n (* -1 x-sum))
          denom  (Math/log (+ alpha' beta'))]
      (if x
        (- (Math/log alpha') denom)
        (- (Math/log beta')  denom))))
  (simulate [this n-samples]
    (repeatedly n-samples #(< (Math/log (rand))
                              (gpm/logpdf this true))))

  stattype/StatisticalType
  (incorporate [this x]
    (assoc this :suff-stats (-> suff-stats
                                (update :n inc)
                                (update :x-sum #(+ % (if x 1 0))))))
  (unincorporate [this x]
    (assoc this :suff-stats (-> suff-stats
                                (update :n dec)
                                (update :x-sum #(- % (if x 1 0))))))
  (logpdf-score [this x]
    (let [n'     (inc (:n suff-stats))
          x-sum' (+ (:x-sum suff-stats) (if x 1 0))
          alpha  (:alpha hyperparameters)
          beta   (:beta hyperparameters)]
      (- (prim/betaln (+ alpha x-sum')
                      (+ n' (- x-sum') beta))
         (prim/betaln alpha beta))))

  collapsed/Collapsed
  (export [this]
    (let [alpha (:alpha hyperparameters)
          beta  (:beta  hyperparameters)
          x-sum (get suff-stats :x-sum 0)
          n     (get suff-stats :n 0)]
    {var-name {:p (/ (+ alpha x-sum)
                     (+ alpha beta n))}})))

(defrecord BernoulliColumn [var-name hyperparameters data]
  stattype/Column
  (hyper-grid [this res]
    {:alpha (iql-utils/loglinspace 0.5 (max (count data) 1) res)
     :beta  (iql-utils/loglinspace 0.5 (max (count data) 1) res)}))

(defn bernoulli?
  "Checks if the given stattype is Bernoulli."
  [stattype]
  (and (record? stattype)
       (instance? Bernoulli stattype)))

(defn spec->bernoulli
  "Casts a CrossCat category spec to a Bernoulli variable."
  [var-name parameters & {:keys [hyperparameters suff-stats]
                          :or {hyperparameters {:alpha 0.5 :beta 0.5}
                               suff-stats {:n 0 :x-sum 0}}}]
    (->Bernoulli var-name parameters hyperparameters suff-stats))

; (let [bernoulli (->Bernoulli "test" {:p 0.9} {:alpha 0.5 :beta 0.5} {:n     0
;                                                                      :x-sum 0})]
;   (println (frequencies (gpm/simulate bernoulli 1000)))
;   (println (instance? Bernoulli bernoulli))
;   (println bernoulli)
;   ; (println (gpm/logpdf bernoulli true {} {}))
;   (-> bernoulli
;       (stattype/incorporate   true)
;       (stattype/incorporate   true)
;       (stattype/incorporate   true)
;       (stattype/incorporate   true)
;       (stattype/incorporate   true)
;       ; (stattype/incorporate   false)
;       ; (stattype/unincorporate true)
;       (gpm/logpdf             true)))
; ;       ; (collapsed/export)))
; ;       ; (get :suff-stats)))

; (frequencies (repeatedly 1000000 #(let [bernoulli (->Bernoulli "test" {:p 0.9} {:alpha 0.5 :beta 0.5} {:n 0 :x-sum 0})]
;                     (-> bernoulli
;                         (stattype/incorporate   true)
;                         (stattype/incorporate   true)
;                         (get :suff-stats)))))
