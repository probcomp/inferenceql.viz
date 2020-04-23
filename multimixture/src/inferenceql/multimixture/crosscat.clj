(ns inferenceql.multimixture.crosscat
  (:require [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils      :as mmix-utils]))

(defn category-logpdf-score
  "Calculates the log probability of data under a given category.
  Assumes `x` contains only columns in that category."
  [x types category]
  (let [parameters (:parameters category)]
    (apply + (mapv (fn [[col value]]
               (let [col-type   (get types col)
                     col-params (get parameters col)]
                 (prim/logpdf value col-type col-params))) x))))

(defn view-logpdf-score
  "Calculates the log probability of data under a given view.
  Assumes `x` contains only columns in that view."
  [targets constraints types latents view]
  (let [crp-counts      (:counts latents)
        n               (apply + crp-counts)
        crp-counts-norm (map #(Math/log (/ % n)) crp-counts)
        categories      (:categories view)
        ll              (map #(category-logpdf-score targets types %) categories)
        weights         (if (empty? constraints)
                          crp-counts-norm
                          (let [unnorm (map #(category-logpdf-score constraints types %) categories)
                                Z      (mmix-utils/logsumexp unnorm)]
                            (map #(- % Z) unnorm)))]
   (mmix-utils/logsumexp
     (map (comp #(apply + %) vector)
          ll weights))))

(defn filter-columns
  "Given view assignments, filters `columns` to contain only relevant views."
  [view-idx view-assignments columns]
  (into {} (filter #(= view-idx
                       (get view-assignments (first %)))
                   columns)))

(defn logpdf-score
  "Calculates the log probability of data under a given CrossCat model."
  [model latents targets constraints]
  (let [types            (:types model)
        view-assignments (get-in latents [:global :z])
        views            (:views model)]
    (->> views
         (map-indexed (fn [view-idx view]
                        (let [targets-view     (filter-columns view-idx view-assignments targets)
                              constraints-view (filter-columns view-idx view-assignments constraints)
                              latents-view     (get-in latents [:local view-idx])]
                          (view-logpdf-score targets-view constraints-view types latents-view view))))
         (reduce +))))

;; "Log likelihood" is used to evaluate the current latent assignments
;; of the data contained in the model.
(defn log-likelihood-view
  "Given a datum, its category assignment, and the corresponding view,
  returns the log-likelihood that the datum was generated by that view."
  [x row-id types latents view]
  (let [assignment      (get-in latents [:y row-id])
        category        (get-in view    [:categories assignment])]
    (category-logpdf-score x types category)))

(defn log-likelihood-views
  "Given a datum, its category assignments across views, and the corresponding model,
  returns the log-likelihood that the datum was generated by that model."
  [x row-id model latents]
  (let [types            (:types model)
        views            (:views model)
        view-assignments (get-in latents [:global :z])]
    (->> views
         (map-indexed (fn [view-idx view]
                        (let [x-view (into {} (filter #(= view-idx
                                                          (get view-assignments (first %)))
                                                      x))]
                          (log-likelihood-view
                            x-view
                            row-id
                            types
                            (get-in latents [:local view-idx])
                            view))))
         (reduce +))))

(defn log-likelihood
  "Given a dataset, returns the likelihood of the dataset under the model,
  as well as the latents structure, which is data-specific."
  [data model latents]
  (let [data-formatted   (->> data
                              (map (fn [[col-name values]]
                                     (map (fn [value]
                                            {col-name value})
                                          values)))
                              (mmix-utils/transpose)
                              (map #(apply merge %)))
        ]
      (->> data-formatted
           (map-indexed (fn [row-id x]
                          (log-likelihood-views x row-id model latents)))
           (reduce +))))

(defn crp-weights
  "Given alphas and counts of customers per table, returns weights
  representing the corresponding CRP."
  [alpha counts]
  (let [n (apply + counts)]
    (->> (concat counts [alpha])
         (map (fn [cnt] (Math/log (/ cnt (+ n alpha))))))))

(defn simulate-category
  "Given a category, statistical types, and constraints, simulates unconstrained values."
  [category types targets constraints]
  (let [parameters-to-sample (->> (:parameters category)
                                  (filter (fn [[k params]]
                                            (and (not (contains? constraints k))
                                                 (some #(= % k) targets))))
                                  (into {}))]
    (->> parameters-to-sample
         (map (fn [[col-name col-params]]
                (let [col-type (get types col-name)]
                  {col-name (prim/simulate col-type col-params)})))
         (into {}))))
         ; (merge constraints)))) need to choose whether to return with/out constraints

(defn hyperprior-simulate
  "Given a hyperprior, simulates a new hyperparameter."
  [hyperprior]
  (let [[primitive parameters] (first (vec hyperprior))]
    (prim/simulate primitive parameters)))

(defn categorical-param-names
  [view col-name]
  (keys (get-in view [:categories 0 :parameters col-name :p])))

(defn generate-category
  "Given a view and statistical types, simulates a category specification
  from that view."
  [m view types]
  (repeatedly m #(let [hypers     (:hypers view)
                       view-types (select-keys types (keys hypers)) ]
                   (->> hypers
                        (map (fn [[col-name hyperpriors]]
                               {col-name (into {} (map (fn [[hyper-name hyper-dist]]
                                                         (if (= :categorical (get view-types col-name))
                                                           {hyper-name (zipmap (categorical-param-names view col-name)
                                                                               (hyperprior-simulate hyper-dist))}
                                                           {hyper-name (hyperprior-simulate hyper-dist)}))
                                                       hyperpriors))}))
                        (into {})
                        (assoc {} :parameters)))))

(defn view-category-weights
  "Returns weights of all categories. When `constraints` is non-empty, the weights
  are reweighted by likelihood of categories generating the constrained values."
  [categories latents types constraints]
  (let [alpha       (:alpha latents)
        counts      (:counts latents)
        crp-weights (crp-weights alpha counts)]
    (if (empty? constraints)
      crp-weights
      (let [adjusted-weights (map + crp-weights (map (fn [category]
                                                       (category-logpdf-score
                                                         constraints
                                                         types
                                                         category))
                                                     categories))
            Z (mmix-utils/logsumexp adjusted-weights)]
        (map (fn [weight] (- weight Z)) adjusted-weights)))))

(defn sample-category
  "Given weights and a list of categories of equal length, samples a category."
  [weights categories]
  (let [probs {:p (zipmap (range (count categories)) weights)}]
    (nth categories (prim/simulate :log-categorical probs))))

(defn simulate-view
  "Given a view and constraints, simulates unconstrained values from that view."
  [view latents types targets constraints & {:keys [:m] :or {m 1}}]
  (let [aux-categories (generate-category m view types)
        categories     (concat (:categories view) aux-categories)
        weights        (view-category-weights categories latents types constraints)
        category       (sample-category weights categories)]
    (simulate-category category types targets constraints)))

(defn simulate
  "Given a model, latents, and possible constraints, simulates
  unconstrained values from the model."
 [model latents targets constraints]
   (let [views (:views model)
         types (:types model)]
     (->> views
          (map-indexed (fn [view-idx view]
                         (let [view-latents     (get-in latents [:local view-idx])]
                           (simulate-view view view-latents types targets constraints))))
          (into {}))))
