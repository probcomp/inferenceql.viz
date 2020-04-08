(ns inferenceql.multimixture.crosscat.specification
  (:require [clojure.spec.alpha :as s]
            [clojure.set :refer [intersection]]
            [clojure.test.check :as tc]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils      :as mmix-utils]))

(defn gen-from-spec
  "Generates a single value from a spec."
  [spec]
  (->> spec
       (s/gen)
       (gen/generate)))

(defn pos-number
  "Generator for positive number within reason."
  []
  (gen/double* {:infinite? false :NaN? false :min 1e-10}))

(defn nonneg-number
  "Generator for non-negative number within reason."
  []
  (gen/double* {:infinite? false :NaN? false :min 0}))

(defn number
  "Generate for a number within reason."
  []
  (gen/double* {:infinite? false :NaN? false}))

(defn prob
  "Generator for number between 0 and 1, within reason."
  []
  (gen/double* {:infinite? false :NaN? false :min 1e-10 :max (- 1 1e-10)}))


;; General probability.
(s/def ::probability (s/with-gen (s/and number? #(< 0 % 1))
                       prob))
(s/def ::probability-distribution (s/with-gen (s/and (s/coll-of ::probability)
                                                     #(< (Math/abs (- 1 (reduce + %)))
                                                         1e-7))
                                     #(->> ::probability
                                          (s/gen)
                                          (gen/vector)
                                          (gen/not-empty)
                                          (gen/fmap mmix-utils/normalize))))


;; Distributions.
;; Bernoulli distribution.
(s/def :bernoulli/p ::probability)
(s/def :bernoulli/parameters (s/keys :req-un [:bernoulli/p]))
(s/def :bernoulli/parameters-list (s/coll-of #{:p} :min-count 1 :distinct true))
(s/def :bernoulli/bernoulli :bernoulli/parameters)

;; Beta distribution.
(s/def :beta-dist/alpha (s/with-gen (s/and number? pos?)
                          pos-number))
(s/def :beta-dist/beta  (s/with-gen (s/and number? pos?)
                          pos-number))
(s/def :beta-dist/parameters (s/keys :req-un [:beta-dist/alpha :beta-dist/beta]))
(s/def :beta-dist/parameters-list (s/coll-of #{:alpha :beta} :min-count 2 :distinct true))
(s/def :beta/beta :beta-dist/parameters)

;; Categorical distribution.
(s/def :categorical/option (s/or :string  string?
                                 :integer nat-int?))
(s/def :categorical/p (s/with-gen (s/and (s/map-of :categorical/option ::probability :min-count 2)
                                                   #(< (Math/abs (- 1 (reduce + (vals %))))
                                                       1e-7))
                                  #(->> ::probability-distribution
                                        (s/gen)
                                        (gen/fmap (fn [values]
                                                    (->> values
                                                         (map
                                                          (fn [v]
                                                            {(gen-from-spec :categorical/option)
                                                             v}))
                                                         (into {})))))))

(s/def :categorical/parameters (s/keys :req-un [:categorical/p]))
(s/def :categorical/parameters-list (s/coll-of #{:p} :min-count 1 :distinct true))
(s/def :categorical/categorical :categorical/parameters)

;; Dirichlet distribution.
(s/def :dirichlet/alpha (s/with-gen (s/coll-of (s/and number? pos?) :kind #(or (list? %)
                                                                               (vector? %)) :into [])
                           #(->> (pos-number)
                                 (gen/vector)
                                 (gen/not-empty))))
(s/def :dirichlet/parameters (s/keys :req-un [:dirichlet/alpha]))
(s/def :dirichlet/parameters-list (s/coll-of #{:alpha} :min-count 1 :distinct true))
(s/def :dirichlet/dirichlet :dirichlet/parameters)

;; Gamma distribution.
(s/def :gamma/k (s/with-gen number?
                  number))
(s/def :gamma/theta (s/with-gen (s/and number? pos?)
                      pos-number))
(s/def :gamma/parameters (s/keys :req-un [:gamma/k :gamma/theta]))
(s/def :gamma/parameters-list (s/coll-of #{:k :theta} :min-count 2 :distinct true))
(s/def :gamma/gamma :gamma/parameters)

;; Gaussian distribution.
(s/def :gaussian/mu (s/with-gen number?
                      number))
(s/def :gaussian/sigma (s/with-gen (s/and number? pos?)
                         pos-number))
(s/def :gaussian/parameters (s/keys :req-un [:gaussian/mu :gaussian/sigma]))
(s/def :gaussian/parameters-list (s/coll-of #{:mu :sigma} :min-count 2 :distinct true))
(s/def :gaussian/gaussian :gaussian/parameters)

;; All together!
(s/def ::distribution-parameters (s/or :bernoulli   :bernoulli/parameters
                                       :beta        :beta-dist/parameters
                                       :categorical :categorical/parameters
                                       :dirichlet   :dirichlet/parameters
                                       :gamma       :gamma/parameters
                                       :gaussian    :gaussian/parameters))

(s/def ::distribution (s/or :bernoulli   :bernoulli/bernoulli
                            :beta        :beta/beta
                            :categorical :categorical/categorical
                            :dirichlet   :dirichlet/dirichlet
                            :gamma       :gamma/gamma
                            :gaussian    :gaussian/gaussian))

(s/def ::distribution-parameters-list (s/or :bernoulli   :bernoulli/parameters-list
                                            :beta        :beta-dist/parameters-list
                                            :categorical :categorical/parameters-list
                                            :dirichlet   :dirichlet/parameters-list
                                            :gamma       :gamma/parameters-list
                                            :gaussian    :gaussian/parameters-list))

(s/def ::distribution-name #{:bernoulli
                             :beta
                             :categorical
                             :dirichlet
                             :gamma
                             :gaussian})

;; Defining hyper-distributions and priors.
(s/def :bernoulli-prior/p (s/keys :req-un [:beta/beta]))
(s/def :bernoulli-prior/bernoulli (s/keys :req-un [:bernoulli-prior/p]))

(s/def :categorical-prior/p (s/keys :req-un [:dirichlet/dirichlet]))
(s/def :categorical-prior/categorical (s/keys :req-un [:categorical-prior/p]))

(s/def :gaussian-prior/mu       (s/keys :req-un [:beta/beta]))  ; Fake for now.
(s/def :gaussian-prior/sigma    (s/keys :req-un [:gamma/gamma]))
(s/def :gaussian-prior/gaussian (s/keys :req-un [:gaussian-prior/mu
                                                 :gaussian-prior/sigma]))

(s/def ::prior (s/with-gen (s/or :bernoulli   :bernoulli-prior/bernoulli
                                 :categorical :categorical-prior/categorical
                                 :gaussian    :gaussian-prior/gaussian)
                  #(gen/one-of [(s/gen :bernoulli-prior/bernoulli)
                                (s/gen :categorical-prior/categorical)
                                (s/gen :gaussian-prior/gaussian)])))

(defn valid-dist-params?
  "Validator for the given distribution parameters."
  [params]
  (s/valid? ::distribution-parameters params))

;; CrossCat model
;; Latents data structure.
(s/def ::count (s/and integer? (comp not neg?)))
(s/def ::counts (s/coll-of ::count :into [] :min-count 1))

(s/def ::y ::counts)
(s/def ::alpha (s/with-gen pos?
                 pos-number))

(defn verify-counts-assignments?
  "Given counts and assignments, verifies that they make sense.

  e.g. `counts`      = [4 1 2]
       `assignments` = [0 0 1 2 0 2 0]
      => true; since we can visualize the input as the CRP:
                 [1 2 5 7] [3] [4 6]

  e.g. `counts`      = [4 1 1]
       `assignments` = [0 0 1 2 0 2 0]
      => false; again we visualize the input as the CRP:
                 [1 2 5 7] [3] [4 6]
                                  ^ not allowed in our spec,
                                    since this table's count
                                    is supposed to be 1!
  If given assignments are given as a map, applies a call to
  `vals` before continuing.
  "
  [counts assignments]
  (if (map? assignments)
    (= (->> assignments
            (vals)
            (frequencies)
            (sort-by key)
            (vals)
            (vec))
       counts)
    (= (->> assignments
            (frequencies)
            (sort-by key)
            (vals)
            (vec))
       counts)))

(defn crp-simulate-n
  "Given a keyword, simulate `n` samples from a CRP with a runtime-simulated
  concentration parameter alpha. Returns a `latents` structure with the specified
  assignment key (either :y or :z, representing rows and column assignments,
  respectively)."
  [assignment-key n]
  (let [alpha (gen/generate (pos-number))
        [counts assignments] (reduce (fn [[counts assignments] i]
                                       (let [probs-tilde  (conj counts alpha)
                                             Z            (reduce + probs-tilde)
                                             probs        {:p (zipmap (range (+ 1 i))
                                                                  (map #(/ % Z) probs-tilde))}
                                             c-i          (prim/categorical-simulate probs)
                                             assignments' (conj assignments c-i)
                                             counts'      (update counts c-i #(if-not %
                                                                                1
                                                                                (inc %)))]
                                         [counts' assignments']))
                                     [[1] [0]]
                                     (range 1 n))]
    {:alpha alpha
     :counts counts
     assignment-key assignments}))

(defn crp-simulate-alpha
  "Given a keyword, simulates a CRP with a runtime-simulated with the specified
  concentration parameter alpha, producing a runtime-generated number of samples.
  Returns a `latents` structure with the specified assignment key (either :y or
  :z, representing rows and column assignments, respectively)."
  [assignment-key alpha]
  (let [n (gen/generate (gen/large-integer* {:min 1 :max 50}))
        [counts assignments] (reduce (fn [[counts assignments] i]
                                       (let [probs-tilde  (conj counts alpha)
                                             Z            (reduce + probs-tilde)
                                             probs        {:p (zipmap (range (+ 1 i))
                                                                  (map #(/ % Z) probs-tilde))}
                                             c-i          (prim/categorical-simulate probs)
                                             assignments' (conj assignments c-i)
                                             counts'      (update counts c-i #(if-not %
                                                                                1
                                                                                (inc %)))]
                                         [counts' assignments']))
                                     [[1] [0]]
                                     (range 1 n))]
    {:alpha alpha
     :counts counts
     assignment-key assignments}))

(defn latents-simulate
  "Given a keyword, simulates `k` `latents` objects, one for each of the implied
  `k` categories. Ensures that each object contains the same number of labels
  representing the same number of rows."
  [assignment-key k]
  (let [n (gen/generate (gen/large-integer* {:min 1 :max 10}))]
  (into [] (repeatedly k #(crp-simulate-n assignment-key n)))))

(s/def ::latents-local (s/with-gen (s/and (s/keys :req-un [::counts ::y ::alpha])
                                          #(verify-counts-assignments? (:counts %) (:y %)))
                         ;; Alpha is randomly generated, as opposed to n.
                         #(->> ::alpha
                               (s/gen)
                               (gen/fmap (partial crp-simulate-alpha :y)))))
(s/def ::z (s/map-of string? #(and (integer? %) (not (neg? %)))))
(s/def ::global (s/with-gen (s/and (s/keys :req-un [::alpha ::counts ::z])
                                   #(verify-counts-assignments?
                                     (:counts %)
                                     (:z %)))
                         ;; Alpha is randomly generated, as opposed to n.
                         #(->> ::alpha
                               (s/gen)
                               (gen/fmap (partial crp-simulate-alpha :z)))))

(s/def ::local (s/with-gen (s/and (s/coll-of ::latents-local :into [])
                                  ;; Make sure there are the same number of assignments
                                  ;; (rows) in each category.
                                  #(->> %
                                       (map (fn [latent] (count (:y latent))))
                                       (distinct)
                                       (count)
                                       (= 1)))
                 ;; Generator function for full `latents-l` structure.
                 #(->> (gen/large-integer* {:min 1 :max 10})
                       (gen/fmap (partial latents-simulate :y)))))

(s/def ::latents (s/with-gen (s/keys :req-un [::local ::global])
                   #(gen/hash-map :local (s/gen ::local)
                                  :global (s/gen ::global))))

(defn valid-local-latents?
  "Validator of a `latents-l` structure."
  [local-latents]
  (s/valid? ::latents-local local-latents))

(defn valid-latents?
  "Validator of a `latents` structure."
  [latents]
  (s/valid? ::latents latents))

;; Model data structure.
(s/def ::column string?)
(s/def ::parameters (s/map-of ::column ::distribution-parameters))
(s/def ::category (s/keys :req-un [::parameters]))

(defn valid-category?
  "Validator of a `category` structure."
  [category]
  (s/valid? ::category category))

;; Distribution types (FOR INFERENCE ONLY).
(s/def ::dist-type #{:bernoulli :categorical :gaussian} )
(s/def ::dist-map (s/map-of ::column ::dist-type :min-count 1 :max-count 1))
(s/def :dist/types (s/map-of ::column ::dist-type :min-count 1))

(defn same-parameters-across-categories?
  "Given a view, confirms that every category contains the same
  column variables and the same parameters (not necessarily their values)."
  [view]
  (let [categories (:categories view)
        base-params (:parameters (nth categories 0))
        base-keys   (set (keys base-params))]
    (every? (fn [category]
              (let [params (:parameters category)]
                (and (= (set (keys params)) (set base-keys))  ; Check for same vars.
                     (every? (fn [[col col-params]]           ; Check for same types.
                               (= (first col-params)
                                  (first (get base-params col))))
                             params))))
            categories)))

(s/def ::categories (s/coll-of ::category ::into [] :min-count 1))
(s/def ::hypers (s/map-of ::column ::prior))

(defn valid-hypers?
  "Validator of a `hypers` structure."
  [hypers]
  (s/valid? ::hypers hypers))

(s/def ::view (s/and (s/keys :req-un [::hypers ::categories])
                     ;; Verify that the hypers variable set is the same
                     ;; for each category.
                     (fn [view]
                       (every? (fn [category]
                                 (= (set (keys (:parameters category)))
                                    (set (keys (:hypers view)))))
                              (:categories view)))))

(defn valid-view?
  "Validator of a `view` structure."
  [view]
  (s/valid? ::view view))

(defn no-overlapping-columns?
  "Given a set of views, confirms that no column variable is shared across views."
  [views]
  (let [num-columns (apply + (map #(count (:hypers %)) views))
        set-columns  (set (apply concat (map #(keys (:hypers %)) views)))]
    (= (count set-columns)
       num-columns)))

(s/def ::views (s/and (s/coll-of ::view)
                      #(no-overlapping-columns? %)))

(defn valid-views?
  "Validator of a collection of `view` structures.
  Enforces that no column is shared between views."
  [views]
  (s/valid? ::views views))

(defn categorical-options
  "Generates options for categorical variable value names.
  Min is 2, max is 10."
  []
  (gen/generate (gen/vector (s/gen :categorical/option) 2 10)))

(defn generate-category
  "Given a map of column names to column distribution types, as well
  as pre-generated categorical option map for any potential categorical
  variables, generates a category. Used for ::xcat model generation."
  [types categorical-options]
  (->> types
       (map (fn [[column-name col-type]]
              (if (= :categorical col-type)
                (let [options   (get categorical-options column-name)
                      n-options (count options)
                      probs-gen (s/gen ::probability)
                      probs     (gen/generate (gen/fmap mmix-utils/normalize
                                                        (gen/vector probs-gen n-options)))]
                  {column-name {:p (zipmap options probs)}})
                {column-name (gen/generate (s/gen (case col-type
                                                    :bernoulli :bernoulli/bernoulli
                                                    :categorical :categorical/categorical
                                                    :gaussian :gaussian/gaussian)))})))
       (into {})
       (assoc {} :parameters)))

(defn generate-hyper
  "Given a column name and distribution type, generates a hyper distribution for
  the variable type. Possible hyper distributions are specified in the beginning of
  this file."
  [column dist-type]
  {column (gen/generate (s/gen
                         (case dist-type
                           :bernoulli :bernoulli-prior/bernoulli
                           :categorical :categorical-prior/categorical
                           :gaussian :gaussian-prior/gaussian) ))})

(defn partition-by-sizes
  "Given a coll and a tuple of sizes of partitions, returns a seq of elements
  from the coll, partitioned into the desired sizes, from left to right.
  The sum of `sizes` must equal the number of elements in the coll."
  [coll sizes]
  (->> sizes
      (reduce (fn [[result left] size]
                [(conj result (take size left))
                 (drop size left)])
              [[] coll])
       (first)))

(defn generate-xcat-from-latents
  "Generating function for a `xcat` model, given a valid `latents` structure."
  [latents]
  (let [latents-global      (:global latents)
        types               (into {} (gen/generate (gen/vector
                                                     (s/gen ::dist-map)
                                                     (reduce + (:counts latents-global)))))
        type-partitions     (partition-by-sizes types (:counts latents-global))
        options             (into {} (map (fn [[col dist-type]]  ; Taking extra care of categoricals.
                                            (if (= :categorical dist-type)
                                              {col (categorical-options)}
                                              {}))
                                          types))
        views (mapv (fn [dist-types local-latent]
                      (let [n-categories (count (:counts local-latent))
                            categories   (vec (repeatedly n-categories #(generate-category
                                                                         dist-types
                                                                         options)))
                            ;; TODO , make sure Dirichlet prior returns a specified length.
                            hypers (into {} (map (fn [[col-name dist]]
                                                   (generate-hyper col-name dist))
                                                 dist-types))]
                        {:hypers     hypers
                         :categories categories}))
                    type-partitions
                    (:local latents))]
    {:types types
     :views  views }))

(s/def ::u     (s/coll-of boolean?))
(s/def ::xcat  (s/with-gen (s/keys :req-un [:dist/types ::views ::latents] :opt-un [::u])
                 #(->> (s/gen ::latents)
                       (gen/fmap (fn [latents] (generate-xcat-from-latents latents))))))

(defn valid-xcat?
  "Validator of a collection of an `xcat` structure."
  [xcat]
  (s/valid? ::xcat xcat))
