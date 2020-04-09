(ns inferenceql.multimixture.crosscat.kernels.category
  (:require [inferenceql.multimixture.crosscat   :as xcat]
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils      :as mmix-utils]))

(defn category-scores
  "Returns logpdf score of x under each of the categories in the view.

  In:
    `x`     [`datum`]: datum to score against categories.
    `view`   [`view`]: current view.
    `types` [`types`]: statistical types of data, specified in the model.
    `m`       [`int`]: number of auxiliary categories to consider.
  Out:
    [`vec categories` `vec double`]: vector of log probabilities that each category
                                     (including auxiliary) generated `x`."
  [x view types m]
  (let [aux-categories (xcat/generate-category m view types)
        categories     (:categories view)
        all-categories (concat categories aux-categories)]
    [all-categories
     (map #(xcat/category-logpdf-score x types %) all-categories)]))

(defn category-weights
  "Returns the weights of each category in the view, based on
  the number of data in each category.

  Refer to Algorithm 8 of the Neal paper for more information:
    http://www.stat.columbia.edu/npbayes/papers/neal_sampling.pdf

  In:
    `latents` [`latents-l`]: local latents of the current view.
    `y`             [`int`]: current category assignment index.
    `singleton?`   [`bool`]: indicates whether current category is a singleton.
    `m`             [`int`]: number of auxiliary categories to consider.
  Out:
    [`int` `vec double`]: updated value of `m` (decrements if `singleton?`),
                          vector of weights for each cluster (based on CRP prior)."
  [latents y singleton? m]
 (let [counts          (:counts latents)
       alpha           (:alpha latents)
       n               (reduce + counts)
       norm            (+ n -1 alpha)
       weight-aux      (Math/log (/ (/ alpha m)
                                    norm))
       weights         (map-indexed (fn [idx cnt]
                                      (cond
                                        (= idx y) (if singleton?
                                                             weight-aux
                                                             (Math/log (/ (dec cnt)
                                                                          norm)))
                                        :else (Math/log (/ cnt norm))))
                                    counts)
       new-m           (if singleton? (- m 1) m)]
   [new-m  (->> weight-aux
                (repeat new-m)
                (concat weights))]))

(defn category-sample
  "Sample a new category assignment based on weighted log scores
  of a datum against all clusters (including auxiliary ones).

  In:
    `weights` [`vec double`]: weights of categories by member count.
    `scores`  [`vec double`]: scores of categories against a datum.
  Out:
    [`int`]: sampled category index."
  [weights scores]
 (let [logps (pmap (comp #(reduce + %) vector) weights scores)
       Z     (mmix-utils/logsumexp logps)]
    (->> logps
         (map-indexed (fn [idx logp] {idx (- logp Z)}))
         (into {})
         (assoc {} :p)
         (prim/log-categorical-simulate))))

(defn latents-update
  "Update the view latents data structure.
  If set to delete, we take extra precautions outlined in `kernel-row`.
  In:
    `row-id`        [`int`]: index of row, used for identifying
                             latent category assignment.
    `y`             [`int`]: current category assignment index.
    `y'`            [`int`]: future category assignment index.
    `latents` [`latents-l`]: local latents of the current view.
    `delete?`      [`bool`]: indicates if a cluster is to be
                               deleted from the latent assignments.
  Out:
    [`latents-l`]: Updated latent assignments and counts per category."
  [row-id y y' latents delete?]
  (let [y' (min (count (:counts latents)) y')]
    (-> latents
        (update-in [:counts y] dec)
        (update-in [:counts y'] #(if-not %
                                   1
                                   (inc %)))
        (assoc-in [:y row-id] y')
        ((fn [latents]
           (if delete?
             (-> latents
                 (update :counts (comp vec #(remove #{0} %)))
                 (update :y #(mapv (fn [assignment]
                                    (if (> assignment y)
                                      (dec assignment)
                                      assignment)) %)))
             latents))))))

(defn kernel-row
  "Category inference kernel for a specific row.

  In:
    `x`           [`datum`]: datum to score against categories.
    `row-id`        [`int`]: index of row, used for identifying latent
                             category assignment.
    `m`             [`int`]: number of auxiliary categories to consider.
    `types`       [`types`]: statistical types of data, specified in the model.
    `latents` [`latents-l`]: local latents of the current view.
    `view`         [`view`]: current view.

  Out:
    [`vec latents-l view`]: updated local latents reflecting potential category
                            assignment change, updated view reflecting categories
                            being potentially added or deleted.

  There are several cases to consider that make this a little tough
  to decipher.

  Define:
    y : Current category.
    y': New category.

  Enumeration of cases:
    1. y is a singleton, y' is empty.
      -- Swap the parameters of y' into y, which avoids
         unnecessary deleting. Update latents.
    2. y is a singleton, y' is not a singleton.
      -- Delete y, update latents making sure to subtract
         one from every assignment > y. Update latents.
    3. y is not a singleton and y' is empty.
      -- Add the new category to the view, update latents.
    4. y is not a singleton, y' is not a singleton.
      -- Update latents; the simplest."
  [x row-id m types latents view]
  (let [category-counts     (:counts latents)
        ys                  (:y      latents)
        alpha               (:alpha  latents)
        y                   (nth ys row-id)
        n-clusters          (count (:categories view))
        singleton?          (= 1 (nth category-counts y))
        [new-m weights]     (category-weights latents y singleton? m)
        [categories scores] (category-scores x view types new-m)
        y'                  (category-sample weights scores)
        category-new        (nth categories y')
        category            (nth categories y)]
    (if (= y y')
      [latents view]  ; Return latents and view unchanged.
      (if singleton?
        (if (>= y' n-clusters)  ; Don't delete, just replace with new parameters.
          [latents (assoc-in view [:categories y] category-new)]
          [(latents-update row-id y y' latents true)
           (update view :categories #(mmix-utils/vec-remove y %))])  ; Update and delete.
        (let [latents-new  (latents-update row-id y y' latents false)]
          (if (>= y' n-clusters)
            [latents-new
             (update view :categories #(conj % category-new))]
            [latents-new
             view]))))))

(defn kernel-view
  "Category inference kernel for one view.
  `latents` must be view-specific, not the entire latents data structure.
  In:
    `data`         [`data`]: input data.
    `view`         [`view`]: current view.
    `types`       [`types`]: statistical types of data, specified in the model.
    `latents` [`latents-l`]: local latents of the current view.
    `m`             [`int`]: number of auxiliary categories to consider.
  Out:
    [`vec latents-l view`]: updated local latents reflecting potential category
                            assignment changes, updated view reflecting categories
                            being potentially added or deleted."
  [data view types latents m]
  (let [view-columns  (keys (:hypers view))
        data-filtered (map #(select-keys % view-columns) data)
        data-row-ids  (map-indexed vector data-filtered)]
    ;; The output of the kernel for the row are the updated
    ;; latents and view. These are fed as input when run on
    ;; subsequent rows.
    (->> data-row-ids
         (reduce (fn [[latents view] [row-id x]]
                   (kernel-row x row-id m types latents view))
                 [latents view])
         (vec))))

(defn kernel
  "Category inference kernel, as specified in the CrossCat paper.
  Requires data, model, latents, and m, the number of auxiliary
  categories to add to the sampler.

  In:
    `data`       [`data`]: input data.
    `model`     [`model`]: CrossCat model.
    `latents` [`latents`]: specified latents of CrossCat model.
    `m`           [`int`]: number of auxiliary categories to consider.
  Out:
    [`vec model latents`]: the result of calling `kernel-view` on each
                           of the views of the model."
  [data model latents m]
  (let [data-formatted (->> data
                            (map (fn [[col-name values]]
                                    (map (fn [value]
                                            {col-name value})
                                          values)))
                            (mmix-utils/transpose)
                            (map #(apply merge %)))
        types (:types model)
        [local-latents views] (mmix-utils/transpose (pmap #(kernel-view data-formatted %1 types %2 m)
                                (:views model)
                                (:local latents)))]
  [(assoc model   :views views)
   (assoc latents :local local-latents)]))
