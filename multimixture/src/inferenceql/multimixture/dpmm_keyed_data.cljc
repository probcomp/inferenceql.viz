(ns inferenceql.multimixture.dpmm-keyed-data
  (:import [org.apache.commons.math3.special Gamma])) 

(defn crp-alpha-counts
  "Given alphas and counts of customers per table, returns a categorical variable
  representing the corresponding CRP."
  [alpha counts]
  (let [n (apply + counts)]
    (->> (concat counts [alpha])
         (map-indexed (fn [idx cnt] {idx (double (/ cnt (+ (- n 1) alpha)))}))
         (into {}))))

(defn exp-safe
  "Safe exponentiation function accounting for NaN values."
  [value]
  (if (Double/isNaN value)
    0
    (Math/exp value)))

(defn dist?
  "Verifies whether keyword represents a distribution."
  [value]
  (contains?  #{:beta :bernoulli :categorical :dirichlet :gamma :gaussian} value))

(defn logsumexp
  "Log-sum-exp operation for summing log probabilities without
  leaving the log domain."
  [log-ps]
  (let [log-ps-sorted (sort > log-ps)
        a0            (first log-ps-sorted)
        tail          (drop 1 log-ps-sorted)
        res           (+ a0 (Math/log
                              (+ 1 (reduce + (map #(Math/exp (- % a0))
                                              tail)))))]
    (if (Double/isNaN res) ;; A zero-probability event has occurred.
      ##-Inf
      res)))

(defn transpose
  "Applies the standard tranpose operation to a collection. Assumes that
  `coll` is an object capable of having a transpose."
  [coll]
  (apply map vector coll))

(defn lgamma
  "Returns the log of `x` under a gamma function."
  [x]
  (if (nil? x) 
   0
   (Gamma/logGamma x)))

(defn bernoulli-logpdf
  "Returns log probability of `x` under a bernoulli distribution parameterized
  by `p`."
  [x p]
  (if (nil? x)
    0
    (if (boolean? x)
        (if x
          (Math/log p)
          (Math/log (- 1 p)))
        ##-Inf)))

(defn bernoulli-simulate
  "Generates a sample from a bernoulli distribution with parameter `p`.
  Generates `n` samples, if specified."
  ([p]
    (let [flip (rand)]
      (< flip p)))
  ([p n]
   (repeatedly n (fn [] (bernoulli-simulate p)))))

(defn gamma-logpdf
  "Returns log probability of `x` under a gamma distribution parameterized
  by shape parameter `k`, with optional scale parameter `theta`."
  ([x k]
    (gamma-logpdf k 1 x))
  ([k theta x]
   (if (nil? x)
     0
     (let [Z-inv (- (+ (lgamma k) (* k (Math/log theta))))
           px    (- (* (- k 1) (Math/log x)) (/ x theta))]
       (+ Z-inv px)))))

(defn gamma-simulate
  "Generates a sample from a gamma distribution with shape parameter `k`.
  Based on 'Generating Gamma and Beta Random Variables with Non-Integral Shape Parameters'
  by J Whittaker.
  Generates `n` samples, if specified."
  ([k]
    (if (< k 1)
      (let [u1 (rand)
            u2 (rand)
            u3 (rand)
            S1 (Math/pow u1 k)
            S2 (Math/pow u2 (- 1 k))]
        (if (<= (+ S1 S2) 1)
          (let [Y (/ S1 (+ S1 S2))]
            (* (- (- 1  Y)) (Math/log u3))) ;; Had to switch this,
          (gamma-simulate k)))              ;; contrary to the literature.
      (let [frac-k        (- k (int k))
            gamma-floor-k (- (apply + (repeatedly
                                        (int k)
                                        (fn [] (Math/log (rand))))))
            gamma-frac-k  (if (zero? frac-k) 0 (gamma-simulate frac-k))]
        (+ gamma-floor-k gamma-frac-k))))
  ([k n]
     (repeatedly n (fn [] (gamma-simulate k)))))

(defn beta-logpdf
  "Returns log probability of `x` under a beta distribution parameterized by
  `alpha` and `beta`."
  [x {:keys [:alpha :beta]}]
  (assert (and (pos? alpha) (pos? beta))
          (str "alpha and beta must be positive (" alpha ", " beta ")"))
  (if (nil? x)
    0 
    (let [k (- (lgamma (+ alpha beta))
               (+ (lgamma alpha) (lgamma beta)))
          c (- alpha 1)
          d (- beta 1)]
      (+ k (* c (Math/log x))
         (* d (Math/log (- 1 x)))))))
(beta-logpdf 0.5 {:alpha 0.5 :beta 0.5})

(defn beta-simulate
  "Generates a sample from a beta distribution with parameters `alpha` and `beta`.
  Generates `n` samples, if specified."
  ([{:keys [:alpha :beta]}]
   (let [X1 (gamma-simulate alpha)
         X2 (gamma-simulate beta)]
     (/ X1 (+ X1 X2))))
  ([n {:keys [:alpha :beta] :as params}]
   (repeatedly n (fn [] (beta-simulate params)))))

(defn categorical-logpdf
  "Log PDF for categorical distribution."
  [x ps]
  (if (nil? x)
    0
    (let [prob (get ps x)]
      (if (nil? prob)
        ##-Inf
        (Math/log prob)))))

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
      (first (first (drop-while #(< (second %) flip) cdf)))))
  ([n ps]
   (repeatedly n (fn [] (categorical-simulate ps)))))

(defn dirichlet-logpdf
  "Returns log probability of `x` under a dirichlet distribution parameterized by
  a vector `alpha`."
  [x alpha]
  (assert (= (count alpha) (count x)) "alpha and x must have same length")
  (if (nil? x)
    0
    (let [Z-inv        (- (apply + (map lgamma alpha))
                          (lgamma (apply + alpha)))
          logDirichlet (apply + (map (fn [alpha-k x-k]
                                       (* (- alpha-k 1) (Math/log x-k)))
                                     alpha
                                     x))]
      (+ Z-inv logDirichlet))))

(defn dirichlet-simulate
  "Generates a sample from a dirhlet distribution with vector parameter `alpha`.
  Generates `n` samples, if specified."
  ([alpha]
    (let [y (map gamma-simulate alpha)
          Z (apply + y)]
      (mapv #(/ % Z) y)))
  ([n alpha]
   (repeatedly n (fn [] (dirichlet-simulate alpha)))))

(defn gaussian-logpdf
  "Returns log probability of `x` under a gaussian distribution parameterized
  by shape parameter `mu`, with optional scale parameter `sigma`."
  [x {:keys [:mu :sigma]}]
  (let [Z-inv (- (* 0.5 (+ (Math/log sigma) (Math/log 2) (Math/log Math/PI))))
        px    (* -0.5 (Math/pow (/ (- x mu) sigma) 2))]
    (+ Z-inv px)))

(defn gaussian-simulate
  "Generates a sample from a dirhlet distribution with vector parameter `alpha`.
  Based on a Box-Muller transform.
  Generates `n` samples, if specified."
  ([{:keys [:mu :sigma]}]
   (let [U1 (rand)
         U2 (rand)
         Z0 (* (Math/sqrt (* -2 (Math/log U1))) (Math/cos (* 2 Math/PI U2)))]
     (+ (* Z0 sigma) mu)))
  ([n {:keys [:mu :sigma] :as params}]
   (repeatedly n (fn [] (gaussian-simulate params)))))

(defn primitive-logpdf
  "Given a primitive, its parameters, returns the log probability of
  `x` under said primitive."
  ([x primitive parameters]
   (case primitive
     :bernoulli   (bernoulli-logpdf   x parameters)
     :beta        (beta-logpdf        x parameters)
     :categorical (categorical-logpdf x parameters)
     :dirichlet   (dirichlet-logpdf   x parameters)
     :gamma       (gamma-logpdf       x parameters)
     :gaussian    (gaussian-logpdf    x parameters)))
  ([primitive parameters]
   (partial primitive-logpdf primitive parameters)))

(defn primitive-simulate
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
     (str "Primitive: " primitive " doesn't exist.")))
  ([n primitive parameters]
   (repeatedly n (fn [] (primitive-simulate primitive parameters)))))

(defn category-logpdf
  "Calculates the log probability of data under a given category.
  Assumes `x` contains only columns in that category."
  [x types category] 
  (let [parameters (:parameters category)]
    (apply + (mapv (fn [[col value]]
               (let [col-type   (get types col)
                     col-params (get parameters col)]
                 (primitive-logpdf value col-type col-params))) x))))

(defn view-logpdf
  "Calculates the log probability of data under a given view.
  Assumes `x` contains only columns in that view"
  [x types latents view]
  (let [crp-counts      (:counts latents)
        n               (apply + crp-counts)
        crp-counts-norm (map #(Math/log (/ % n)) crp-counts)
        categories      (:categories view)]
    (->> categories
         (map #(category-logpdf x types %))
         (map (comp #(apply + %) vector) crp-counts-norm)
         logsumexp)))

(defn crosscat-logpdf
  "Calculates the log probability of data under a given CrossCat model."
  [x model latents]
  (let [ types            (:types model)
        view-assignments (get-in latents [:global :z])
        views            (:views model)]
    (->> views
         (map-indexed (fn [view-idx view]
                        (let [x-view (into {} (filter #(= view-idx
                                                          (get view-assignments (first %)))
                                                      x))]
                          (view-logpdf x-view types (get-in latents [:local view-idx]) view))))
         (apply +))))


 


(defn category-assignment-simulate
  "Simulates a category assignment given a view's concentration parameter
  and category-row counts."
  [alpha counts]
  (let [crp-probs    (crp-alpha-counts alpha counts)]
    (categorical-simulate crp-probs)))

(defn hyperprior-simulate
  "Given a hyperprior, simulates a new hyperparameter."
  [hyperprior]
  (let [[primitive parameters] (first (vec hyperprior))]
    (primitive-simulate primitive parameters)))

(defn category-simulate
  "Given a category and statistical types, simulates a value from that category."
  [types category]
  (let [parameters (:parameters category)]
    (into {}
          (pmap (fn [[col-name col-params]]
                  (let [col-type (col-name types)]
                    {col-name (primitive-simulate col-type col-params)}))
                parameters))))

(defn generate-category
  "Given a view and statistical types, simulates a category specification
  from that view."
  [view types]
  (let [hypers     (:hypers view)
        view-types (select-keys types (keys hypers)) ]
    (->> hypers
         (pmap (fn [[col-name hyperpriors]]
                 {col-name (if (> (count (keys hyperpriors)) 1)
                             (into {} (map (fn [[ hyper-name hyper-dist]]
                                             {hyper-name
                                              (hyperprior-simulate hyper-dist)}) hyperpriors))
                             ;; Need the below hack to get keyword arguments
                             ;; for categorical variable.
                            (if (= :categorical (col-name view-types))
                              (zipmap (keys (get-in view [:categories 0 :parameters col-name]))
                                      (hyperprior-simulate hyperpriors))
                              (hyperprior-simulate hyperpriors)))}))
         (into {})
         (assoc {} :parameters))))

(defn view-simulate
  "Given latents and a view, simulates a sample from that view."
  [types latents view]
  (let [alpha         (:alpha latents)
        counts        (:counts latents)
        n-categories  (count counts)
        y             (category-assignment-simulate alpha counts)]
    (if (= y n-categories)
      (category-simulate types (generate-category  view types))
      (category-simulate types (get-in view [:categories y])))))

(defn crosscat-simulate
  "Given a CrossCat model and latent variables, simulates a sample from
  that model."
  [model latents]
  (let [column-types (:types model)
        views        (:views model)
        view-latents (map vector views (:local latents))]
    (into {} (pmap (fn [[view latent]] (view-simulate column-types latent view)) view-latents))))
