(ns inferenceql.multimixture.crosscat.kernels.category
  (:require [inferenceql.multimixture.crosscat   :as xcat] 
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils      :as mmix-utils])) 

(defn category-weights
  "Returns the weights of each category in the view, based on
  the number of data in each category.
  Refer to Algoirthm 8 of the Neal paper for more information."
  [latents y singleton? m]
 (let [counts          (:counts latents)
       alpha           (:alpha latents)
       weight-aux      (Math/log (/ alpha m))
       n               (reduce + counts)
       norm            (+ n -1 alpha)
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

(defn category-scores
  "Returns logpdf score of x under each of the categories in the view."
  [x view types m] 
  (let [aux-categories (xcat/generate-category m view types)
        categories     (:categories view)
        all-categories (concat categories aux-categories)]
    [all-categories
     (pmap #(xcat/category-logpdf x types %) all-categories)]))

(defn category-sample
  "Sample a new category assignment based on weighted log scores
  of a datum against all clusters (including auxiliary ones)."
  [weights scores]
 (let [logps (pmap (comp #(reduce + %) vector) weights scores)
       Z     (mmix-utils/logsumexp logps)]
    (->> logps
         (map-indexed (fn [idx logp] {idx (- logp Z)}))
         (into {})
         (prim/log-categorical-simulate))))

(defn latents-update
  "Update the view latents data structure.
  If set to delete, we take extra precautions outlined
  in `kernel-row`."
  [row-id y y' latents delete?]
  (-> latents
      (update-in [:counts y] dec)
      (update-in [:counts y']     #(if-not %
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
          latents)))))

(defn kernel-row
  "Category inference kernel for a specific row.
  There are several cases to consider that make this
  a little tough to decipher. 
  y : Current category.
  y': New category.
  category, and y' be the new category. 
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
        y'               (category-sample weights scores)
        category-new        (nth categories y')
        category    (nth categories y)]
    (if (= y y')
      [latents view]  ; Return latents and view unchanged.
      (if singleton?
        (if (>= y' n-clusters)  ; Don't delete, just replace with new parameters.
          [latents (assoc-in view [:categories y] category-new)]
          [(latents-update row-id y y' latents true)
           (update view :categories #(mmix-utils/vec-remove y %))]) ; Update and delete. 
        (let [latents-new  (latents-update row-id y y' latents false)]
          (if (>= y' n-clusters)
            [latents-new
             (update view :categories #(conj % category-new))]
            [latents-new
             view]))))))
        
(defn kernel-view
  "Category inference kernel for one view.
  `latents` must be view-specific, not the entire latents
  data structure."
  [data view types latents m]
  (let [view-columns (keys (:hypers view))
        data-filtered (pmap #(select-keys % view-columns) data)
        n             (count data-filtered)
        data-row-ids (map-indexed vector data-filtered)]
    ;; The output of the kernel for the row are the updated
    ;; latents and view. These are fed as input when run on
    ;; subsequent rows.
    (reduce (fn [[latents view] [row-id x]]
              (kernel-row x row-id m types latents view))
            [latents view]
            data-row-ids)))

(defn kernel
  "Category inference kernel, as specified in the CrossCat paper.
  Requires data, model, latents, and m, the number of auxiliary
  categories to add to the sampler."
  [data model latents m]
  (let [data-formatted (->> data
                            (mapv (fn [[col-name values]]
                                    (mapv (fn [value]
                                            {col-name value})
                                          values)))
                            (mmix-utils/transpose)
                            (mapv #(apply merge %)))
        types (:types model)
        [local-latents views] (mmix-utils/transpose (pmap #(kernel-view data-formatted %1 types %2 m)
                                (:views model)
                                (:local latents)))]
  [(assoc model :views views) 
   (assoc latents :local local-latents)]))
