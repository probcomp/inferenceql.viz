(ns inferenceql.multimixture.crosscat
  (:require [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils      :as mmix-utils]))

(defn crp-alpha-counts
  "Given alphas and counts of customers per table, returns a categorical variable
  representing the corresponding CRP."
  [alpha counts]
  (let [n (apply + counts)]
    (->> (concat counts [alpha])
         (map-indexed (fn [idx cnt] {idx (double (/ cnt (+ n alpha)))}))
         (into {})
         (assoc {} :p))))

(defn category-logpdf
  "Calculates the log probability of data under a given category.
  Assumes `x` contains only columns in that category."
  [x types category]
  (let [parameters (:parameters category)]
    (apply + (mapv (fn [[col value]]
               (let [col-type   (get types col)
                     col-params (get parameters col)]
                 (prim/logpdf value col-type col-params))) x))))

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
         mmix-utils/logsumexp)))

(defn logpdf
  "Calculates the log probability of data under a given CrossCat model."
  [x model latents]
  (let [types            (:types model)
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
  (let [crp-probs (crp-alpha-counts alpha counts)]
    (prim/categorical-simulate crp-probs)))

(defn hyperprior-simulate
  "Given a hyperprior, simulates a new hyperparameter."
  [hyperprior]
  (let [[primitive parameters] (first (vec hyperprior))]
    (prim/simulate primitive parameters)))

(defn category-simulate
  "Given a category and statistical types, simulates a value from that category."
  [types category]
  (let [parameters (:parameters category)]
    (into {}
          (pmap (fn [[col-name col-params]]
                  (let [col-type (col-name types)]
                    {col-name (prim/simulate col-type col-params)}))
                parameters))))

(defn categorical-param-names
  [view col-name]
  (keys (get-in view [:categories 0 :parameters col-name :p])))

(defn generate-category
  "Given a view and statistical types, simulates a category specification
  from that view."
  ([view types]
   (let [hypers     (:hypers view)
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
          (assoc {} :parameters))))
  ([n view types]
   (repeatedly n #(generate-category view types))))

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

(defn simulate
  "Given a CrossCat model and latent variables, simulates a sample from
  that model."
  [model latents]
  (let [column-types (:types model)
        views        (:views model)
        view-latents (map vector views (:local latents))]
    (into {} (pmap (fn [[view latent]] (view-simulate column-types latent view)) view-latents))))
