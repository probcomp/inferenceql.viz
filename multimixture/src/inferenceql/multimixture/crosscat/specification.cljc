(ns inferenceql.multimixture.crosscat.specification
  (:require [clojure.spec.alpha :as s]
            [clojure.set :refer [intersection]]
            [clojure.test.check :as tc]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [inferenceql.multimixture.primitives :as prim]))

(defn normalize
  "Normalizes a collection of numbers."
  [col]
  (mapv #(double ( / % ( reduce + col))) col))

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
  (gen/double* {:infinite? false :NaN? false :min 0 :max 1}))


;; General probability.
(s/def ::probability (s/with-gen (s/and number? #(<= 0 % 1))
                       prob))
(s/def ::probability-distribution (s/with-gen (s/and (s/coll-of ::probability)
                                                     #(== 1 (reduce + %)))
                                     #(->> ::probability
                                          (s/gen)
                                          (gen/vector)
                                          (gen/not-empty)
                                          (gen/fmap normalize))))

;; Distribution types.
(s/def ::dist-types #{:bernoulli
                      :beta
                      :categorical
                      :dirichlet
                      :gamma
                      :gaussian})

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
(s/def :categorical/option (s/or :string (s/and string?
                                                (comp not empty?))
                                 :integer nat-int?))
(s/def :categorical/p (s/with-gen (s/and (s/map-of :categorical/option ::probability)
                                                   #(== 1 (reduce + (vals %))))
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
(gen-from-spec :gaussian/gaussian)

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
(s/def :bernoulli-prior/bernoulli :bernoulli-prior/p)

(s/def :categorical-prior/p (s/keys :req-un [:dirichlet/dirichlet]))
(s/def :categorical-prior/categorical :categorical-prior/p)

(s/def :gaussian-prior/mu       (s/keys :req-un [:beta/beta]))  ; Fake for now.
(s/def :gaussian-prior/sigma    (s/keys :req-un [:gamma/gamma]))
(s/def :gaussian-prior/gaussian (s/keys :req-un [:gaussian-prior/mu :gaussian-prior/sigma]))

(s/def ::priors (s/and (s/keys :opt-un [:bernoulli-prior/bernoulli
                                        :categorical-prior/categorical
                                        :gaussian-prior/gaussian])
                       #((comp not empty?) %)))


;; CrossCat model
;; Latents data structure.
(s/def ::count (s/and integer? (comp not neg?)))
(s/def ::counts (s/coll-of ::count :into [] :min-count 1))

(s/def ::y ::counts)
(s/def ::alpha (s/with-gen pos?
                 pos-number))

(defn verify-counts-assignments?
  [counts assignments]
  (= (->> assignments
          (frequencies)
          (sort-by key)
          (vals)
          (vec))
     counts))

(defn crp-simulate-n
  "Returns log probability of table counts `x` under a Chinese Restaurant Process
  parameterized by a number `alpha`."
  [assignment-key n]
  (let [alpha (gen/generate (pos-number))
        [counts assignments] (reduce (fn [[counts assignments] i]
                                       (let [probs-tilde  (conj counts alpha)
                                             Z            (reduce + probs-tilde)
                                             probs        (zipmap (range (+ 1 i))
                                                                  (map #(/ % Z) probs-tilde))
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
  "Returns log probability of table counts `x` under a Chinese Restaurant Process
  parameterized by a number `alpha`."
  [assignment-key alpha]
  (let [n (gen/generate (gen/large-integer* {:min 1 :max 100}))
        [counts assignments] (reduce (fn [[counts assignments] i]
                                       (let [probs-tilde  (conj counts alpha)
                                             Z            (reduce + probs-tilde)
                                             probs        (zipmap (range (+ 1 i))
                                                                  (map #(/ % Z) probs-tilde))
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
  [assignment-key k]
  (let [n (gen/generate (gen/large-integer* {:min 1 :max 500}))]
  (into [] (repeatedly k #(crp-simulate-n assignment-key n)))))

(s/def ::latents-local (s/with-gen (s/and (s/keys :req-un [::counts ::y ::alpha])
                                          #(verify-counts-assignments? (:counts %) (:y %)))
                         #(->> ::alpha
                               (s/gen)
                               (gen/fmap (partial crp-simulate-alpha :y)))))

(s/def ::z ::counts)
(s/def ::global (s/with-gen (s/and (s/keys :req-un [::alpha ::counts ::z])
                                   #(verify-counts-assignments?
                                     (:counts %)
                                     (:z %)))
                         #(->> ::alpha
                               (s/gen)
                               (gen/fmap (partial crp-simulate-alpha :z)))))


(s/def ::local (s/with-gen (s/and (s/coll-of ::latents-local :into [])
                                  ;; Make sure there are the same number of assignments
                                  ;; in each category.
                                  #(->> %
                                       (map (fn [latent] (count (:y latent))))
                                       (distinct)
                                       (count)
                                       (= 1)))
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

(defn valid-stat-types?
  [types]
  (s/valid? ::types types))

;; model data structure.
(s/def ::column string?)
(s/def ::parameters (s/map-of ::column ::distribution-parameters))
(s/def ::category (s/keys :req-un [::parameters]))

(defn same-parameters-across-categories?
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

(s/def ::categories (s/coll-of ::category ::into []))
(s/def ::hypers (s/map-of ::column ::priors))
(s/def ::view (s/keys :req-un [::hypers ::categories]))

(defn no-overlapping-columns?
  [views]
  (let [num-columns (apply + (map #(count (:hypers %)) views))
        set-columns  (set (apply concat (map #(keys (:hypers %)) views)))]
    (= (count set-columns)
       num-columns)))

(s/def ::views (s/and (s/coll-of ::view)
                      no-overlapping-columns?))


(s/def ::types (s/map-of ::column ::dist-types))
(s/def ::u     (s/coll-of boolean?))
(s/def ::xcat (s/and (s/keys :req-un [::types ::views] :opt-un [::u])
                     #(= (count (:types %))
                         (count (:u %)))))
;; TODO - Also need to check for correct column labels and whatnot.

; (s/valid? ::xcat
;          {:types {"color" :categorical "happy" :bernoulli "height" :gaussian}  ;; Stat. types of each column.
;              :u     [true true true]                               ;; Booleans indicating uncollapsed col.
;              :views [{:hypers {"color"  {:dirichlet {:alpha [1 1 1]}}
;                                "happy?" {:beta      {:alpha 0.5 :beta 0.5}}}
;                       :categories [{:parameters  {"color" {:p {"green" 0.8 "red" 0.1 "black" 0.1}}
;                                                   "happy" {:p 0.8}}}
;                                    {:parameters  {"color" {:p {"green" 0.2 "red" 0.4 "black" 0.4}}
;                                                   "happy" {:p 0.4}}}]}
;                      {:hypers {"height" {:mu    {:beta  {:alpha 0.5 :beta 0.5}}
;                                          :sigma {:gamma {:k 0.9 :shape 1}}}}
;                       :categories [{:parameters {"height" {:mu 5.5 :sigma 0.5}}}
;                                    {:parameters {"height" {:mu 3.4 :sigma 0.2}}}]}]} )
