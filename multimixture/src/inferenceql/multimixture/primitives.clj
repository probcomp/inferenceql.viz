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
  [x p]
  (if x
    (Math/log p)
    (Math/log (- 1 p))))

(defn bernoulli-simulate
  "Generates a sample from a bernoulli distribution with parameter `p`.
  Generates `n` samples, if specified."
  ([p]
    (let [flip (rand)]
      (< flip p)))
  ([n p]
   (repeatedly n #(bernoulli-simulate p))))

(defn gamma-logpdf
  "Returns log probability of `x` under a gamma distribution parameterized
  by shape parameter `k`, with optional scale parameter `theta`."
  [x {:keys [:k :theta]}]
   (let [theta (if-not theta 1 theta)  ; Hack because defaults don't work.
         Z-inv (- (+ (lgamma k)
                     (* k (Math/log theta))))
         px    (- (* (- k 1)
                     (Math/log x))
                  (/ x theta))]
     (+ Z-inv px)))

(defn gamma-simulate
  "Generates a sample from a gamma distribution with shape parameter `k`.
  Based on 'Generating Gamma and Beta Random Variables with Non-Integral Shape Parameters'
  by J Whittaker.
  Generates `n` samples, if specified."
  ([{:keys [:k :theta]}]
    (if (< k 1)
      (let [u1    (rand)
            u2    (rand)
            u3    (rand)
            S1    (Math/pow u1 k)
            S2    (Math/pow u2 (- 1 k))
            theta (if-not theta 1 theta)]
        (if (<= (+ S1 S2) 1)
          (let [Y (/ S1
                     (+ S1 S2))]
            (* theta
               (- (- 1  Y))
               (Math/log u3)))  ; Had to switch this, contrary to the literature.
          (gamma-simulate {:k k :theta theta})))
      (let [theta         (if-not theta 1 theta)
            frac-k        (- k (int k))
            gamma-floor-k (- (reduce + (repeatedly
                                        (int k)
                                        #(Math/log (rand)))))
            gamma-frac-k  (if (zero? frac-k) 0 (gamma-simulate {:k frac-k}))]
        (* theta (+ gamma-floor-k gamma-frac-k)))))
  ([n {:keys [:k :theta] :as parameters}]
     (repeatedly n #(gamma-simulate parameters))))

(defn beta-logpdf
  "Returns log probability of `x` under a beta distribution parameterized by
  `alpha` and `beta`."
  [x {:keys [:alpha :beta]}]
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
  ([{:keys [:alpha :beta]}]
   (let [X1 (gamma-simulate {:k alpha})
         X2 (gamma-simulate {:k beta})]
     (/ X1 (+ X1 X2))))
  ([n {:keys [:alpha :beta] :as params}]
   (repeatedly n #(beta-simulate params))))

(defn categorical-logpdf
  "Log PDF for categorical distribution."
  [x ps]
  ; (assert (= 1.0 (reduce + (vals ps))) ps)
  (let [prob (get ps x)]
    (if-not prob
      ##-Inf
      (Math/log prob))))

(defn categorical-simulate
  "Generates a sample from a categorical distribution with parameters `ps`.
  Generates `n` samples, if specified."
  ([ps]
    (let [ps-sorted (sort-by last ps)
          cdf       (second (reduce (fn [[total v] [variable p]]
                                      (let [new-p (if (= total 0)
                                                    p
                                                    (+ total p))
                                            new-entry [variable new-p]]
                                        [new-p (conj v new-entry)]))
                                    [0 []]
                                    ps-sorted))
          candidate (first ps-sorted)
          flip      (rand)]
      (ffirst (drop-while #(< (second %) flip) cdf))))
  ([n ps]
   (repeatedly n #(categorical-simulate ps))))

(defn log-categorical-simulate
  "Generates a sample from a categorical distribution with parameters `ps`,
  which are log probabilities.
  Generates `n` samples, if specified."
  ([ps]
    (let [ps-sorted (sort-by last ps)
          cdf       (second (reduce (fn [[total v] [variable p]]
                                      (let [new-p (if (= total 0)
                                                    p
                                                    (+ (mmix-utils/logsumexp [total p])))
                                            new-entry [variable new-p]]
                                        [new-p (conj v new-entry)]))
                                    [0 []]
                                    ps-sorted))
          candidate (first ps-sorted)
          flip      (Math/log (rand))]
      (ffirst (drop-while #(< (second %) flip) cdf))))
  ([n ps]
   (repeatedly n #(categorical-simulate ps))))

(defn dirichlet-logpdf
  "Returns log probability of `x` under a dirichlet distribution parameterized by
  a vector `alpha`."
  [x alpha]
  (assert (= (count alpha) (count x)) "alpha and x must have same length")
  (let [Z-inv        (- (->> alpha
                             (map lgamma)
                             (reduce +))
                        (lgamma (reduce + alpha)))
        logDirichlet (apply + (map (fn [alpha-k x-k]
                                     (* (- alpha-k 1)
                                        (Math/log x-k)))
                                   alpha
                                   x))]
    (+ Z-inv logDirichlet)))

(defn dirichlet-simulate
  "Generates a sample from a dirhlet distribution with vector parameter `alpha`.
  Generates `n` samples, if specified."
  ([alpha]
    (let [y (map #(gamma-simulate {:k %}) alpha)
          Z (reduce + y)]
      (mapv #(/ % Z) y)))
  ([n alpha]
   (repeatedly n #(dirichlet-simulate alpha))))

(defn gaussian-logpdf
  "Returns log probability of `x` under a gaussian distribution parameterized
  by shape parameter `mu`, with optional scale parameter `sigma`."
  [x {:keys [:mu :sigma]}]
  (let [Z-inv (* -0.5 (+ (Math/log sigma)
                         (Math/log 2)
                         (Math/log Math/PI)))
        px    (* -0.5 (Math/pow (/ (- x mu)
                                   sigma)
                                2))]
    (+ Z-inv px)))

(defn gaussian-simulate
  "Generates a sample from a dirhlet distribution with vector parameter `alpha`.
  Based on a Box-Muller transform.
  Generates `n` samples, if specified."
  ([{:keys [:mu :sigma]}]
   (let [U1 (rand)
         U2 (rand)
         Z0 (* (Math/sqrt (* -2 (Math/log U1)))
               (Math/cos (* 2 Math/PI U2)))]
     (+ (* Z0 sigma) mu)))
  ([n {:keys [:mu :sigma] :as params}]
   (repeatedly n #(gaussian-simulate params))))

(defn logpdf
  "Given a primitive, its parameters, returns the log probability of
  `x` under said primitive."
  ([x primitive parameters]
   (case primitive
     :bernoulli   (bernoulli-logpdf   x parameters)
     :beta        (beta-logpdf        x parameters)
     :categorical (categorical-logpdf x parameters)
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
     :bernoulli   (bernoulli-simulate   parameters)
     :beta        (beta-simulate        parameters)
     :categorical (categorical-simulate parameters)
     :dirichlet   (dirichlet-simulate   parameters)
     :gamma       (gamma-simulate       parameters)
     :gaussian    (gaussian-simulate    parameters)
     (throw (ex-info "Primitive doesn't exist." {:primitive primitive}))))
  ([n primitive parameters]
   (repeatedly n #(simulate primitive parameters))))
