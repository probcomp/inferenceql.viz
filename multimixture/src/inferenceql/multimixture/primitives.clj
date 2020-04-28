(ns inferenceql.multimixture.primitives
  (:require [inferenceql.multimixture.utils :as mmix-utils])
  (:import [org.apache.commons.math3.special Gamma]))

(defn lgamma
  "Returns the log of `x` under a gamma function."
  [x]
  (if-not x
   0
   (Gamma/logGamma x)))

(defn bernoulli-logpdf
  "Returns log probability of `x` under a bernoulli distribution parameterized
  by `p`."
  [x {:keys [:p]}]
  (if x
    (Math/log p)
    (Math/log (- 1 p))))

(defn bernoulli-simulate
  "Generates a sample from a bernoulli distribution with parameter `p`.
  Generates `n` samples, if specified."
  ([{:keys [p]}]
   (let [flip (rand)]
     (< flip p)))
  ([n parameters]
   (repeatedly n #(bernoulli-simulate parameters))))

(defn gamma-logpdf
  "Returns log probability of `x` under a gamma distribution parameterized
  by shape parameter `k`, with optional scale parameter `theta`."
  [x {:keys [k theta]}]
  (let [z-inv (- (+ (lgamma k)
                    (* k (Math/log theta))))
        px    (- (* (- k 1)
                    (Math/log x))
                 (/ x theta))]
    (+ z-inv px)))

(defn gamma-simulate
  "Generates a sample from a gamma distribution with shape parameter `k`.
  Based on 'Generating Gamma and Beta Random Variables with Non-Integral Shape Parameters'
  by J Whittaker.
  Generates `n` samples, if specified."
  ([{:keys [k theta]}]
   (if (< k 1)
     (let [u1    (rand)
           u2    (rand)
           u3    (rand)
           s1    (Math/pow u1 k)
           s2    (Math/pow u2 (- 1 k))
           theta (if-not theta 1 theta)]
       (if (<= (+ s1 s2) 1)
         (let [y (/ s1
                    (+ s1 s2))]
           (* theta
              (- (- 1 y))
              (Math/log u3)))  ; Had to switch this, contrary to the literature.
         (gamma-simulate {:k k :theta theta})))
     (let [theta         (if-not theta 1 theta)
           frac-k        (- k (int k))
           gamma-floor-k (- (reduce + (repeatedly
                                        (int k)
                                        #(Math/log (rand)))))
           gamma-frac-k  (if (zero? frac-k) 0 (gamma-simulate {:k frac-k}))]
       (* theta (+ gamma-floor-k gamma-frac-k)))))
  ([n parameters]
   (repeatedly n #(gamma-simulate parameters))))

(defn beta-logpdf
  "Returns log probability of `x` under a beta distribution parameterized by
  `alpha` and `beta`."
  [x {:keys [alpha beta]}]
  (assert (and (pos? alpha) (pos? beta))
          (str "alpha and beta must be positive (" alpha ", " beta ")"))
  (let [k (- (lgamma (+ alpha beta))
             (+ (lgamma alpha)
                (lgamma beta)))
        c (- alpha 1)
        d (- beta 1)]
    (+ k (* c (Math/log x))
       (* d (Math/log (- 1 x))))))

(defn beta-simulate
  "Generates a sample from a beta distribution with parameters `alpha` and `beta`.
  Generates `n` samples, if specified."
  ([{:keys [alpha beta]}]
    (let [x1 (gamma-simulate {:k alpha})
          x2 (gamma-simulate {:k beta})]
      (/ x1 (+ x1 x2))))
  ([n parameters]
   (repeatedly n #(beta-simulate parameters))))

(defn categorical-logpdf
  "Log PDF for categorical distribution."
  [x {:keys [p]}]
  (let [prob (get p x)]
    (if (or (zero? prob) (nil? prob))
      ##-Inf
      (Math/log prob))))

(defn categorical-simulate
  "Generates a sample from a categorical distribution with vector parameters `p`.
  Generates `n` samples, if specified."
  ([{:keys [p]}]
    (let [p-sorted  (sort-by last p)
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
      (ffirst (drop-while #(< (second %) flip) cdf))))
  ([n parameters]
   (repeatedly n #(categorical-simulate parameters))))

(defn log-categorical-simulate
  "Generates a sample from a categorical distribution with parameters `ps`,
  which are log probabilities.
  Generates `n` samples, if specified."
  ([{:keys [p]}]
    (let [p-sorted (sort-by last p)
          cdf       (second (reduce (fn [[total v] [variable p]]
                                      (let [new-p (if (= total 0)
                                                    p
                                                    (+ (mmix-utils/logsumexp [total p])))
                                            new-entry [variable new-p]]
                                        [new-p (conj v new-entry)]))
                                    [0 []]
                                    p-sorted))
          candidate (first p-sorted)
          flip      (Math/log (rand))]
      (ffirst (drop-while #(< (second %) flip) cdf))))
  ([n parameters]
   (repeatedly n #(log-categorical-simulate parameters))))

(defn crp-logpdf
  "Returns log probability of table counts `x` under a Chinese Restaurant Process
  parameterized by a number `alpha`."
  [x {:keys [alpha]}]
  (let [n       (reduce + x)
        p-tilde (+ (* (count x) (Math/log alpha))
                   (->> x
                        (map #(lgamma %))
                        (reduce +)))
        z       (- (lgamma (+ n alpha))
                   (lgamma alpha))]
    (- p-tilde z)))

(defn crp-simulate
  "Returns log probability of table counts `x` under a Chinese Restaurant Process
  parameterized by a number `alpha`."
  [n {:keys [alpha]}]
  (reduce (fn [[counts assignments] i]
            (let [probs-tilde  (conj counts alpha)
                  z            (reduce + probs-tilde)
                  probs        (zipmap (range (+ 1 alpha))
                                       (map #(/ % z) probs-tilde))
                  c-i          (categorical-simulate probs)
                  assignments' (conj assignments c-i)
                  counts'      (update counts c-i #(if-not %
                                                        1
                                                        (inc %)))]
              [counts' assignments']))
          [[1] [0]]
          (range 1 n)))

(defn dirichlet-logpdf
  "Returns log probability of `x` under a dirichlet distribution parameterized by
  a vector `alpha`."
  [x {:keys [alpha]}]
  (assert (= (count alpha) (count x)) "alpha and x must have same length")
  (let [z-inv        (- (->> alpha
                             (map lgamma)
                             (reduce +))
                        (lgamma (reduce + alpha)))
        logDirichlet (apply + (map (fn [alpha-k x-k]
                                     (* (- alpha-k 1)
                                        (Math/log x-k)))
                                   alpha
                                   x))]
    (+ z-inv logDirichlet)))

(defn dirichlet-simulate
  "Generates a sample from a dirichlet distribution with vector parameter `alpha`.
  Generates `n` samples, if specified."
  ([{:keys [alpha]}]
    (let [y (map #(gamma-simulate {:k % :theta 1}) alpha)
          z (reduce + y)]
      (mapv #(/ % z) y)))
  ([n parameters]
   (repeatedly n #(dirichlet-simulate parameters))))

(defn gaussian-logpdf
  "Returns log probability of `x` under a gaussian distribution parameterized
  by shape parameter `mu`, with optional scale parameter `sigma`."
  [x {:keys [mu sigma]}]
  (let [z-inv (* -0.5 (+ (Math/log sigma)
                         (Math/log 2)
                         (Math/log Math/PI)))
        px    (* -0.5 (Math/pow (/ (- x mu)
                                   sigma)
                                2))]
    (+ z-inv px)))

(defn gaussian-simulate
  "Generates a sample from a dirhlet distribution with vector parameter `alpha`.
  Based on a Box-Muller transform.
  Generates `n` samples, if specified."
  ([{:keys [mu sigma]}]
   (let [u1 (rand)
         u2 (rand)
         z0 (* (Math/sqrt (* -2 (Math/log u1)))
               (Math/cos (* 2 Math/PI u2)))]
     (+ (* z0 sigma) mu)))
  ([n parameters]
   (repeatedly n #(gaussian-simulate parameters))))

(defn logpdf
  "Given a primitive, its parameters, returns the log probability of
  `x` under said primitive."
  ([x primitive parameters]
   (case primitive
     :bernoulli   (bernoulli-logpdf   x parameters)
     :beta        (beta-logpdf        x parameters)
     :categorical (categorical-logpdf x parameters)
     :crp         (crp-logpdf         x parameters)
     :dirichlet   (dirichlet-logpdf   x parameters)
     :gamma       (gamma-logpdf       x parameters)
     :gaussian    (gaussian-logpdf    x parameters)
     (throw  (ex-info (str  "Primitive doesn't exist: " primitive) {:primitive primitive}))))
  ([primitive parameters]
   (partial logpdf primitive parameters)))

(defn simulate
  "Given a primitive and its parameters, generates a sample from the primitive.
  Generates `n` samples, if specified."
  ([primitive parameters]
   (case primitive
     :bernoulli       (bernoulli-simulate       parameters)
     :beta            (beta-simulate            parameters)
     :categorical     (categorical-simulate     parameters)
     :dirichlet       (dirichlet-simulate       parameters)
     :gamma           (gamma-simulate           parameters)
     :gaussian        (gaussian-simulate        parameters)
     :log-categorical (log-categorical-simulate parameters)
     (throw (ex-info (str  "Primitive doesn't exist: " primitive) {:primitive primitive}))))
  ([n primitive parameters]
   (repeatedly n #(simulate primitive parameters))))
