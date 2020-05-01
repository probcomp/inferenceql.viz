(ns inferenceql.multimixture.stattypes.primitives.gaussian
  (:require [inferenceql.multimixture.gpm.proto :as gpm]
            [inferenceql.multimixture.stattypes.proto :as stattype]
            [inferenceql.multimixture.stattypes.proto.collapsed :as collapsed]
            [inferenceql.multimixture.utils :as iql-utils]
            [inferenceql.multimixture.primitives :as prim]))

(defn posterior-hypers
  [n sum-x sum-x-sq hyperparameters]
  (let [m    (:m hyperparameters)
        r    (:r hyperparameters)
        s    (:s hyperparameters)
        nu   (:nu hyperparameters)
        r-n  (+ r n)
        nu-n (+ nu n)
        m-n  (/ (+ (* r m) sum-x))
        s-n  (+ s
                sum-x-sq
                (* r m m)
                (* -1 r m-n m-n))]
    [m-n r-n s-n nu-n]))

(defn calc-z
  [r s nu]
  (+ (* (Math/log 2) (/ (+ nu 1) 2))
     (* 0.5 (Math/log Math/PI))
     (* -0.5 (Math/log r))
     (* (/ nu -2) (Math/log s))
     (prim/lgamma (/ nu 2))))

(defrecord Gaussian [var-name parameters hyperparameters suff-stats]
  gpm/GPM
  (logpdf   [this x]
    (let [n                (:n suff-stats)
          sum-x            (:sum-x suff-stats)
          sum-x-sq         (:sum-x-sq suff-stats)
          [_ r-n s-n nu-n] (posterior-hypers n
                                             sum-x
                                             sum-x-sq
                                             hyperparameters)
          [_ r-m s-m nu-m] (posterior-hypers (+ n 1)
                                             (+ sum-x x)
                                             (+ sum-x-sq (* x x))
                                             hyperparameters)
          z-n              (calc-z r-n s-n nu-n)
          z-m              (calc-z r-m s-m nu-m)]
      (+ (* -0.5 (+ (Math/log 2) (Math/log Math/PI)))
         z-m
         (- z-n))))
  (simulate [this n-samples]
    (let [[m-n r-n s-n nu-n] (posterior-hypers (:n suff-stats)
                                               (:sum-x suff-stats)
                                               (:sum-x-sq suff-stats)
                                               hyperparameters)
          rho (prim/simulate :gamma
                             {:k (/ nu-n 2) :theta (/ 2 s-n)})
          mu  (prim/simulate :gaussian
                             {:mu m-n
                              :sigma (/ 1 (Math/pow (* rho r-n) 0.5))})]
      (prim/simulate n-samples :gaussian {:mu mu :sigma (/ 1 rho)})))

  stattype/StatisticalType
  (incorporate [this x]
    (assoc this :suff-stats (-> suff-stats
                                (update :n inc)
                                (update :sum-x #(+ % x))
                                (update :sum-x-sq #(+ % (* x x))))))
  (unincorporate [this x]
    (assoc this :suff-stats (-> suff-stats
                                (update :n dec)
                                (update :sum-x #(- % x))
                                (update :sum-x-sq #(- % (* x x))))))
  (logpdf-score [this x]
    (let [n                (inc (:n suff-stats))
          sum-x            (+ x (:sum-x suff-stats))
          sum-x-sq         (+ (* x x) (:sum-x-sq suff-stats))
          [_ r-n s-n nu-n] (posterior-hypers n
                                             sum-x
                                             sum-x-sq
                                             hyperparameters)
          z-n              (calc-z r-n s-n nu-n)
          z-0              (calc-z (:r hyperparameters) (:s hyperparameters) (:nu hyperparameters))]
      (+ (* -0.5 n (+ (Math/log 2) (Math/log Math/PI)))
         z-n
         (- z-0))))

  collapsed/Collapsed
  (export [this]
    (let [m            (:m hyperparameters)
          s            (:s hyperparameters)
          nu           (:nu hyperparameters)
          k            (/ nu 2)
          theta        (/ 2 s)
          rho-expected (* k theta)]
      ;; Using the expectation of the gamma distribution for rho.
      {var-name {:mu m :sigma (double (/ 1 rho-expected))}})))

(defrecord GaussianColumn [var-name hyperparameters data]
  stattype/Column
  (hyper-grid [this res]
    {:alpha (iql-utils/loglinspace 0.5 (max (count data) 1) res)}))

(defn gaussian?
  "Checks if the given stattype is Gaussian."
  [stattype]
  (and (record? stattype)
       (instance? Gaussian stattype)))

(defn spec->gaussian
  "Casts a CrossCat category spec to a Gaussian variable."
  ([var-name parameters]
   (spec->gaussian var-name parameters {:m 1 :r 1 :s 1 :nu 1} {:n 0 :sum-x 0 :sum-x-sq 0}))
  ([var-name parameters hyperparameters]
   (spec->gaussian var-name parameters hyperparameters {:n 0 :sum-x 0 :sum-x-sq 0}))
  ([var-name parameters hyperparameters suff-stats]
   (->Gaussian var-name parameters hyperparameters suff-stats)))

; (let [gaussian (->Gaussian "test" {:mu 0 :sigma 0} {:m 1 :r 1 :s 1 :nu 1} {:n        0
;                                                                            :sum-x    0
;                                                                            :sum-x-sq 0})]
;   ; (println (frequencies (gpm/simulate bernoulli {} {} 1000)))
;   (println (gpm/simulate gaussian 5))
;   ; (println (gpm/logpdf bernoulli true {} {}))
;   (-> gaussian
;       (stattype/incorporate   2)
;       ; (stattype/incorporate   3)
;       ; (stattype/unincorporate 1)
;       ; (gpm/logpdf 2)))
;       (collapsed/export)))
;       ; (get :suff-stats)))

; (frequencies (repeatedly 1000000 #(let [gaussian (->Gaussian "test" {:mu 0 :sigma 0} {:m 1 :r 1 :s 1 :nu 1} {:n 0 :sum-x 0 :sum-x-sq 0})]
;                     (-> gaussian
;                         (stattype/incorporate   1)
;                         (stattype/incorporate   2)
;                         (get :suff-stats)))))
