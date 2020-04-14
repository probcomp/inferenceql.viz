(ns inferenceql.multimixture.crosscat.column
  (:require [clojure.math.combinatorics :as combo])
  (:require [inferenceql.multimixture.primitives :as prim])
  (:require [inferenceql.multimixture.utils :as mmix-utils]))

(defprotocol Distribution
  "A protocol for distributions. Must have a logpdf and simulate method."
  (logpdf [this x] "Calculates the logpdf of the given Distribution
                   evaluated at `x`.")
  (simulate [this] "Simulates a value from the given Distribution."))

(defrecord Compound [base-type parameters]
  Distribution
  (logpdf
    [this x]
    (let [params (reduce-kv (fn [m k v]
                              (assoc m k (first v)))
                            {}
                            parameters)]
      (logpdf (base-type params) x)))
  (simulate
    [this]
    (let [params (reduce-kv (fn [m k v]
                              (assoc m k (first v)))
                            {}
                            parameters)]
      (simulate (base-type params)))))

(defrecord Bernoulli [parameters]
  Distribution
  (logpdf
    [this x]
    (if x
      (Math/log (:p parameters))
      (Math/log (- 1 (:p parameters)))))
  (simulate [this]
    (let [flip (rand)]
      (< flip (:p parameters)))))

(defrecord Gamma [parameters]
  Distribution
  (logpdf
    [this x]
     (let [k            (:k     parameters)
           theta        (:theta parameters)
           Z-inv (- (+ (prim/lgamma k)
                       (* k (Math/log theta))))
           px    (- (* (- k 1)
                       (Math/log x))
                    (/ x theta))]
       (+ Z-inv px)))
  (simulate [this]
    (let [k            (:k     parameters)
          theta        (:theta parameters)]
          (if (< k 1)
            (let [u1    (rand)
                  u2    (rand)
                  u3    (rand)
                  S1    (Math/pow u1 k)
                  S2    (Math/pow u2 (- 1 k))]
              (if (<= (+ S1 S2) 1)
                (let [Y (/ S1
                           (+ S1 S2))]
                  (* theta
                     (- (- 1  Y))
                     (Math/log u3)))  ; Had to switch this, contrary to the literature.
                (simulate (Gamma. {:k k :theta theta}))))
            (let [theta         (if-not theta 1 theta)
                  frac-k        (- k (int k))
                  gamma-floor-k (- (reduce + (repeatedly
                                              (int k)
                                              #(Math/log (rand)))))
                  gamma-frac-k  (if (zero? frac-k)
                                  0
                                  (simulate (Gamma. {:k frac-k :theta 1})))]
              (* theta (+ gamma-floor-k
                          gamma-frac-k)))))))

(defrecord Beta [parameters]
  Distribution
  (logpdf
    [this x]
    (let [alpha (:alpha parameters)
          beta  (:beta  parameters)]
      (let [k (- (prim/lgamma (+ alpha beta))
                 (+ (prim/lgamma alpha)
                    (prim/lgamma beta)))
            c (- alpha 1)
            d (- beta 1)]
        (+ k (* c (Math/log x))
          (* d (Math/log (- 1 x)))))))
  (simulate [this]
   (let [alpha (:alpha parameters)
         beta  (:beta  parameters)
         X1    (simulate (Gamma. {:k alpha :theta 1}))
         X2    (simulate (Gamma. {:k beta  :theta 1}))]
     (/ X1 (+ X1 X2)))))

(defrecord Categorical [parameters]
  Distribution
  (logpdf
    [this x]
    (let [prob (get-in parameters [:p x])]
      (if-not prob
        ##-Inf
        (Math/log prob))))
  (simulate [this]
    (let [p-sorted  (sort-by last (:p parameters))
          cdf       (second (reduce (fn [[total v] [variable p]]
                                      (let [new-p (if (= total 0)
                                                    p
                                                    (+ total p))
                                            new-entry [variable new-p]]
                                        [new-p (conj v new-entry)]))
                                    [0 []]
                                    p-sorted))
          candidate (first p-sorted)
          flip      (rand)]
      (ffirst (drop-while #(< (second %) flip) cdf)))))

(defrecord Dirichlet [parameters]
  Distribution
  (logpdf
    [this x]
    (let [alpha        (:alpha parameters)
          Z-inv        (- (->> alpha
                               (map prim/lgamma)
                               (reduce +))
                          (prim/lgamma (reduce + alpha)))
          logDirichlet (apply + (map (fn [alpha-k x-k]
                                       (* (- alpha-k 1)
                                          (Math/log x-k)))
                                     alpha
                                     x))]
      (+ Z-inv logDirichlet)))
  (simulate [this]
    (let [alpha (:alpha parameters)
          y     (map #(simulate (Gamma. {:k % :theta 1})) alpha)
          Z     (reduce + y)]
      (mapv #(/ % Z) y))))

(defrecord Normal [parameters]
  Distribution
  (logpdf
    [this x]
  (let [mu    (:mu    parameters)
        sigma (:sigma parameters)
        Z-inv (* -0.5 (+ (Math/log sigma)
                         (Math/log 2)
                         (Math/log Math/PI)))
        px    (* -0.5 (Math/pow (/ (- x mu)
                                   sigma)
                                2))]
    (+ Z-inv px)))
  (simulate [this]
   (let [mu    (:mu    parameters)
         sigma (:sigma parameters)
         U1    (rand)
         U2    (rand)
         Z0    (* (Math/sqrt (* -2 (Math/log U1)))
                  (Math/cos (* 2 Math/PI U2)))]
     (+ (* Z0 sigma) mu))))


(defprotocol Column
  "A protocol for defining a column variable in a CrossCat model."
  (set-hyperparameters [this] "")
  (hyperparameters     [this] "Returns the hyperparameters of the column.")
  (prior               [x    this] "")
  (likelihood          [x    this] "")
  (simulate-col        [this] "")
  (enumerate           [this] "")
  (primitive?          [this] "")
  (suff-stats          [this] ""))

(defrecord BernoulliColumn
  [parameters]
  Column
  (set-hyperparameters [this]
    (assoc this :hypers {:alpha 0.5 :beta 0.5}))
  (hyperparameters [this]
    (get parameters :hypers {:alpha 0.5 :beta 0.5}))
  (prior [this x]
    (logpdf (Beta. (get parameters :hypers {:alpha 0.5 :beta 0.5})) x))
  (likelihood [this x]
    (logpdf (Bernoulli. parameters) x))
  (enumerate [this]
    nil)
  (primitive? [this]
    true)
  (suff-stats [this]
    (get parameters :suff-stats {true 0 false 0}))
  (simulate-col [this]
    (simulate (->Bernoulli parameters))))

(defrecord CategoricalColumn
  [parameters]
  Column
  (set-hyperparameters [this]
    (assoc this :hypers {:alpha (vec (repeat (count (:p parameters)) 1))}))
  (hyperparameters [this]
    (get parameters :hypers {:alpha
                             (vec (repeat (count (:p parameters)) 1))}))
  (prior [this x]
    (logpdf (Dirichlet. (hyperparameters this)) x))
  (likelihood [this x]
    (logpdf (Categorical. parameters) x))
  (enumerate [this]
    nil)
  (primitive? [this]
    true)
  (suff-stats [this]
    (let [options   (keys (:p parameters))
          n-options (count options)]
      (get parameters :suff-stats (zipmap options (repeat n-options 0)))))
  (simulate-col [this]
    (simulate (->Categorical parameters))))

(defrecord NormalColumn
  [parameters]
  Column
  (set-hyperparameters [this]
    (assoc this :hypers {:m  0
                         :r  1
                         :s  1
                         :nu 1}))
  (hyperparameters [this]
    (get parameters :hypers {:m  0
                             :r  1
                             :s  1
                             :nu 1}))
  (prior           [this x]
    (let [rho    (/ 1 (:sigma x))
          mu     (:mu x)
          hypers (hyperparameters this)
          m      (:m hypers)
          r      (:r hypers)
          s      (:s hypers)
          nu     (:nu hypers)
          p-rho  (+ (* -0.5 nu (Math/log 2))
                    (*  0.5 nu (Math/log s))
                    (* -1 (prim/lgamma (* 0.5 nu)))
                    (* (- (* 0.5 nu) 1)
                       (Math/log rho))
                    (* -0.5 s rho))
          p-mu   (+ (* -0.5 (Math/log (* 2 Math/PI)))
                    (*  0.5 (Math/log (* r rho)))
                    (* -0.5 rho (+ (* r (- mu m) (- mu m))
                                   s)))]
      (+ p-mu p-rho)))
  (likelihood      [this x]
    (logpdf (->Normal parameters) x))
  (enumerate       [this]
    nil)
  (primitive?      [this]
    true)
  (suff-stats      [this]
    (get parameters :suff-stats {:sum-of-x    0
                                 :sum-of-x-sq 0
                                 :n           0}))
  (simulate-col [this]
    (simulate (->Normal parameters))))

(def col-bernoulli (->BernoulliColumn {:p 0.9}))
(hyperparameters col-bernoulli)
(prior col-bernoulli 0.9)
(likelihood   col-bernoulli false)
(enumerate    col-bernoulli)
(primitive?   col-bernoulli)
(suff-stats   col-bernoulli)
(simulate-col col-bernoulli)

(def col-categorical (->CategoricalColumn {:p {"red" 0.7 "blue" 0.1 "green" 0.2}}))
(hyperparameters col-categorical)
(prior col-categorical [0.7 0.1 0.2])
(likelihood   col-categorical "red")
(enumerate    col-categorical)
(primitive?   col-categorical)
(suff-stats   col-categorical)
(simulate-col col-categorical)

(def col-normal (->NormalColumn {:mu 0 :sigma 1}))
(hyperparameters col-normal)
(prior col-normal {:mu 0 :sigma 1})
(likelihood   col-normal 0)
(enumerate    col-normal)
(primitive?   col-normal)
(suff-stats   col-normal)
(simulate-col col-normal)

;; Toss a coin with probability p. Bernoulli.
;; If heads: sample from {"red" 0.8 "blue" 0.1 "green" 0.1}
;; If tails: sample from {"red" 0.2 "blue" 0.5 "green" 0.3}
(defrecord Custom
  [parameters]
  Column
  (hyperparameters [this] "Returns the hyperparameters of the column."
    (get parameters :hypers {:flip {:alpha 0.5 :beta 0.5}
                             :alpha-1 {:alpha (vec (repeat (count (:ps-heads parameters)) 1))}
                             :alpha-2 {:alpha (vec (repeat (count (:ps-tails parameters)) 1))}}))
  (prior           [this x]
    (let [hypers (hyperparameters this)]
      (+ (logpdf (->Beta      (:flip    hypers)) (:flip x))
         (logpdf (->Dirichlet (:alpha-1 hypers)) (:alpha-1 x))
         (logpdf (->Dirichlet (:alpha-2 hypers)) (:alpha-2 x)))))
  (likelihood      [this x] ;; x is a color.
    (mmix-utils/logsumexp
      [(+ (logpdf (->Bernoulli   (:p-flip   parameters)) true)
          (logpdf (->Categorical (:ps-heads parameters)) x))
       (+ (logpdf (->Bernoulli   (:p-flip   parameters)) false)
          (logpdf (->Categorical (:ps-tails parameters)) x))]))
  (enumerate       [this]
    ["red" "blue" "green"])
  (primitive?      [this] ""
    false)
  (suff-stats      [this] ""
  nil)
  (simulate-col [this]
    (let [heads? (simulate (->Bernoulli (:p-flip parameters)))]
      (if heads?
        (simulate (->Categorical (:ps-heads parameters)))
        (simulate (->Categorical (:ps-tails parameters)))))))

(def custom-column (->Custom {:p-flip   {:p 0.9}
                              :ps-heads {:p {"red" 0.8 "blue" 0.1 "green" 0.1}}
                              :ps-tails {:p {"red" 0.2 "blue" 0.5 "green" 0.3}}}))
(hyperparameters custom-column)
(prior custom-column `{:flip 0.8
                     :alpha-1 [0.8 0.1 0.1]
                     :alpha-2 [0.5 0.2 0.3]})
(likelihood   custom-column "red")
(enumerate    custom-column)
(primitive?   custom-column)
(suff-stats   custom-column)
(simulate-col custom-column)
