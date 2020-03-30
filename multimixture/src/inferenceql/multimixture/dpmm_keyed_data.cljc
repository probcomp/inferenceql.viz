(ns inferenceql.multimixture.dpmm-keyed-data
  (:import [org.apache.commons.math3.special Gamma])) 

(defn transpose
  "Applies the standard tranpose operation to a collection. Assumes that
  `coll` is an object capable of having a transpose."
  [coll]
  (apply map vector coll))

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

(defn lgamma
  "Returns the log of `x` under a gamma function."
  [x]
  (if (nil? x) 
   0
   (Gamma/logGamma x)))

(defn bernoulli-logpdf
  "Returns log probability of `x` under a bernoulli distribution parameterized
  by `p`."
  [p x]
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
  ([k x]
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
  [alpha beta x]
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

(defn beta-simulate
  "Generates a sample from a beta distribution with parameters `alpha` and `beta`.
  Generates `n` samples, if specified."
  ([alpha beta]
   (let [X1 (gamma-simulate alpha)
         X2 (gamma-simulate beta)]
     (/ X1 (+ X1 X2))))
  ([alpha beta n]
   (repeatedly n (fn [] (beta-simulate alpha beta)))))

(defn categorical-logpdf
  "Log PDF for categorical distribution.

  `ps` must be a vector with one argument as a map, with keywords as variables
  and values as probabilities (e.g. [{:brown 0.8 :red 0.2}]."
  [ps x]
  (if (nil? x)
    0
    (let [prob (get ps x)]
      (if (nil? prob)
        ##-Inf
        (Math/log prob)))))

(defn categorical-simulate
  "Generates a sample from a categorical distribution with parameters `ps`.
  Generates `n` samples, if specified.

  `ps` must be a vector with one argument as a map, with keywords as variables
  and values as probabilities (e.g. [{:brown 0.8 :red 0.2}]."
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
  ([ps n]
   (repeatedly n (fn [] (categorical-simulate ps)))))

(categorical-simulate `{:brown 0.5 :red 0.4 :green 0.1})

(defn dirichlet-logpdf
  "Returns log probability of `x` under a dirichlet distribution parameterized by
  a vector `alpha`."
  [alpha x]
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
  ([alpha n]
   (repeatedly n (fn [] (dirichlet-simulate alpha)))))

(defn gaussian-logpdf
  "Returns log probability of `x` under a gaussian distribution parameterized
  by shape parameter `mu`, with optional scale parameter `sigma`."
  [mu sigma x]
  (let [Z-inv (- (* 0.5 (+ (Math/log sigma) (Math/log (* 2 Math/PI)))))
        px    (* (/ 1 (* 2 sigma)) (Math/pow (- x mu) 2))]
    (+ Z-inv px)))

(defn gaussian-simulate
  "Generates a sample from a dirhlet distribution with vector parameter `alpha`.
  Based on a Box-Muller transform.
  Generates `n` samples, if specified."
  ([mu sigma]
   (let [U1 (rand)
         U2 (rand)
         Z0 (* (Math/sqrt (* -2 (Math/log U1))) (Math/cos (* 2 Math/PI U2)))]
     (+ (* Z0 sigma) mu)))
  ([mu sigma n]
   (repeatedly n (fn [] (gaussian-simulate mu sigma)))))

(defn primitive-logpdf
  "Given a primitive, its parameters, returns the log probability of
  `x` under said primitive."
  ([primitive parameters x]
   (case primitive
     :bernoulli   (bernoulli-logpdf   (get parameters :p)  x)
     :beta        (beta-logpdf        (get parameters :alpha) (get parameters :beta) x)
     :categorical (categorical-logpdf (get parameters :ps) x)
     :dirichlet   (dirichlet-logpdf   (get parameters :alpha) x)
     :gamma       (gamma-logpdf       (get parameters :k) x)
     :gaussian    (gaussian-logpdf    (get parameters :mu) (get parameters :sigma) x)))
  ([primitive parameters]
   (partial primitive-logpdf primitive parameters)))

(defn primitive-simulate
  "Given a primitive and its parameters, generates a sample from the primitive.
  Generates `n` samples, if specified."
  ([primitive parameters]
   (case primitive
     :bernoulli   (bernoulli-simulate   (get parameters :p))
     :beta        (beta-simulate        (get parameters :alpha) (get parameters :beta))
     :categorical (categorical-simulate (get parameters :ps))
     :dirichlet   (dirichlet-simulate   (get parameters :alpha))
     :gamma       (gamma-simulate       (get parameters :k))
     :gaussian    (gaussian-simulate    (get parameters :mu) (get parameters :sigma))
     (str "Primitive: " primitive " doesn't exist.")))
  ([primitive parameters n]
   (repeatedly n (fn [] (primitive-simulate primitive parameters)))))

(defn category-column-logpdf
  "Given a category and column variable, calculates the log probability
  of that variable in the given category."
  [category column]
  (let [column-name  (get column :name)
        column-type  (get column :type)
        column-value (get column :value)
        parameters   (get-in category [column-name :parameters])]
    (primitive-logpdf column-type parameters column-value)))  

(defn categories-column-logpdf
  "Returns the log probability of a given value in the specified category."
  [categories column]
  (pmap #(category-column-logpdf % column) categories))

(defn separate-datum
  "Formates datum to be used by logpdf infrastructure."
  [datum types]
  (map (fn [[k v]] {:name  k
                    :value v
                    :type (get types k)}) datum))

(defn dpmm-logpdf
  "Returns the log probability of the value under the given DPMM model and latent values.
  `val` is of the form {:var-0 val-0 ... :var-d val-d}"
  [model latents value]
  (let [dpmm              (get-in model [:views 0])
        vars              (get dpmm :vars)
        categories        (get dpmm :categories)
        counts            (get-in latents [:local 0 :counts])
        n-rows            (apply + counts)
        n-dims            (count vars)
        normalized-counts (map #(Math/log (/ % n-rows)) counts)]
    (let [column-logps  (->> (get model :types)
                             (separate-datum value)
                             (pmap (partial categories-column-logpdf categories)))
          category-logps (->> column-logps
                              (transpose)
                              (map #(reduce + %))
                              (map + normalized-counts))]
      (logsumexp category-logps))))


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

(defn column-parameters-simulate
  "Samples new hyperparameters for a given column."
  [parameters-hyperpriors]
  (into {} (map (fn [[parameter hyperprior]]
                  {parameter (hyperprior-simulate hyperprior)})
                parameters-hyperpriors)))

(defn view-simulate
  "Simulates a datum from a view, following the correct specification."
  [view model-types latents]
  (let [hypers              (get view :hypers)
        columns             (keys hypers)
        view-types          (select-keys model-types columns)
        alpha               (get latents :alpha)
        counts              (get latents :counts)
        n-categories        (count counts)
        category-assignment (category-assignment-simulate
                              alpha
                              counts)]
    (if (= category-assignment n-categories)
      ;; Create new category and sample from it. 
      (->> hypers
           (map (fn [[column-name column-hypers]]
                    (let [new-hypers  (column-parameters-simulate column-hypers)
                          column-type (get view-types column-name)]
                    (if (= :categorical column-type)  ;; Adds keywords to new categorical parameters.
                      (let [categorical-options (keys (get-in view [:categories 0 column-name :parameters :ps]))]  
                        {column-name (primitive-simulate
                                      column-type
                                      (update new-hypers :ps #(zipmap categorical-options %)))})
                      {column-name (primitive-simulate column-type new-hypers)}))))
           (into {}))
      ;; Simulate from existing category.
      (let [category (get-in view [:categories category-assignment])]
        (map (fn [[column-name params-stats]]
                {column-name
                 (primitive-simulate
                   (get view-types column-name)
                   (get params-stats :parameters))})
             category)))))
    
<<<<<<< HEAD
=======
(let [view {:hypers {:color  {:ps {:dirichlet {:alpha [1 1 1]}}}
                     :happy? {:p  {:beta      {:alpha 0.5 :beta 0.5}}}}
            :categories [{:color  {:parameters {:ps {:green 0.8 :red 0.1 :black 0.1}}
                                   :suff-stats {:counts {:green 2 :red 0 :black 3}}}             
                          :happy? {:parameters {:p 0.8}
                                   :suff-stats {:counts {true 1 false 2}}}}
                         {:color  {:parameters {:ps {:green 0.2 :red 0.4 :black 0.4}}
                                   :suff-stats {:counts {:green 1 :red 3 :black 1}}}             
                          :happy? {:parameters {:p 0.4}
                                   :suff-stats {:counts {true 1 false 3}}}}]}
      model-types {:color :categorical :happy? :bernoulli} 
      latents {:alpha 10 :z [0 1 1 1 0 0 1 0 1 0] :counts [5 5]} ]
     (view-simulate view model-types latents))

>>>>>>> Added a bunch of primitives, logpdf and simulate for DPMM
(let [data    {:columns {:color {:type :categorical
                                 :vals [:red :black :red :green :black
                                        :red :black :green :black]}
                         :happy? {:type :bernoulli 
                                  :vals [true false false false nil
                                         false nil false true nil]}}}
      latents {:global {}
               :local [{:alpha 10000
                        :z [0 1 1 1 0 0 1 0 1 0]
                        :counts [5 5]}]}
      model   {:types {:color :categorical :happy? :bernoulli}  ;; Stat. types of each column.
               :u     [true true]                               ;; Booleans indicating uncollapsed col.
               :views [{:hypers {:color  {:ps {:dirichlet {:alpha [1 1 1]}}}
                                 :happy? {:p  {:beta      {:alpha 0.5 :beta 0.5}}}}
                        :categories [{:color  {:parameters {:ps {:green 0.8 :red 0.1 :black 0.1}}
                                               :suff-stats {:counts {:green 2 :red 0 :black 3}}}             
                                      :happy? {:parameters {:p 0.8}
                                               :suff-stats {:counts {true 1 false 2}}}}
                                     {:color  {:parameters {:ps {:green 0.2 :red 0.4 :black 0.4}}
                                               :suff-stats {:counts {:green 1 :red 3 :black 1}}}             
                                      :happy? {:parameters {:p 0.4}
                                               :suff-stats {:counts {true 1 false 3}}}}]}]}]
  (view-simulate (get-in model [:views 0]) (get model :types) (get-in latents [:local 0])))
