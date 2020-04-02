(ns inferenceql.multimixture.crosscat.kernels
  (:require [inferenceql.multimixture.crosscat   :as xcat] 
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils      :as mmix-utils])) 

(defn category-weights
  "Returns the weights of each category in the view, based on
  the number of data in each category.
  Refer to Algoirthm 8 of
  the Neal paper for more information."
  [latents y-current singleton? m]
 (let [counts          (:counts latents)
       alpha           (:alpha latents)
       weight-aux      (Math/log (/ alpha m))
       n               (reduce + counts)
       norm            (+ n -1 alpha)
       weights         (map-indexed (fn [idx cnt]
                                      (cond
                                        (= idx y-current) (if singleton?
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
 (let [logps (map (comp #(reduce + %) vector) weights scores)
       Z     (mmix-utils/logsumexp logps)]
    (->> logps
         (map-indexed (fn [idx logp] {idx (- logp Z)}))
         (into {})
         (prim/log-categorical-simulate))))

(defn latents-update
  "Update the view latents data structure.
  If set to delete, we take extra precautions outlined
  in `kernel-row`."
  [row-id y-current y-new latents delete?]
  (-> latents
      (update-in [:counts y-current] dec)
      (update-in [:counts y-new]     #(if-not %
                                        1   
                                        (inc %)))
      (assoc-in [:y row-id] y-new)
      ((fn [latents]
        (if delete?
           (-> latents 
               (update :counts (comp vec #(remove #{0} %)))
               (update :y #(mapv (fn [y] 
                                  (if (> y y-current)
                                    (dec y)
                                    y)) %)))
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
        y                   (:y      latents)
        alpha               (:alpha  latents)
        y-current           (nth y row-id)
        n-clusters          (count (:categories view)) 
        singleton?          (= 1 (nth category-counts y-current))
        [new-m weights]     (category-weights latents y-current singleton? m)
        [categories scores] (category-scores x view types new-m)
        y-new               (category-sample weights scores)
        category-new        (nth categories y-new)
        category-current    (nth categories y-current)]
    (if (= y-current y-new)
      [latents view]  ; Return latents and view unchanged.
      (if singleton?
        (if (>= y-new n-clusters)  ; Don't delete, just replace with new parameters.
          [latents (assoc-in view [:categories y-current] category-new)]
          [(latents-update row-id y-current y-new latents true)
           (update view :categories #(mmix-utils/vec-remove y-current %))]) ; Update and delete. 
        (let [latents-new  (latents-update row-id y-current y-new latents false)]
          (if (>= y-new n-clusters)
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
        types (:types model)]
    (pmap #(kernel-view data-formatted %1 types %2 m)
          (:views model)
          (:local latents))))

;; 1 | green | true  | 6.0
;; 2 | red   | false | 4.2
;; 3 | black | false | 3.8
;; 4 | green |       | 5.7
;; 5 | red   | false | 3.5
;; 6 | black | false | 3.0
;; 7 | green | true  | 6.5
;; 8 | green |       | 6.4
;; 9 | black | false | 5.1
;; 10| red   |       | 3.8
(let [data    {:color ["red" "black" "red" "green" "black"
                       "red" "black" "green" "black"]
               :happy? [true false false false nil
                        false nil false true nil]}
      model {:types {:color :categorical :happy? :bernoulli :height :gaussian}  ;; Stat. types of each column.
             :u     [true true]                               ;; Booleans indicating uncollapsed col.
             :views [{:hypers {:color  {:dirichlet [1 1 1]}
                               :happy? {:beta      {:alpha 0.5 :beta 0.5}}}
                      :categories [{:parameters  {:color {"green" 0.8 "red" 0.1 "black" 0.1}
                                                  :happy? 0.8}}
                                   {:parameters  {:color {"green" 0.2 "red" 0.4 "black" 0.4}
                                                  :happy? 0.4}}]}
                     {:hypers {:height {:mu    {:beta {:alpha 0.5 :beta 0.5}}
                                        :sigma {:gamma {:k 0.9}}}}
                      :categories [{:parameters {:height {:mu 5.5 :sigma 0.5}}}
                                   {:parameters {:height {:mu 3.4 :sigma 0.2}}}]}]}
      latents {:global {:alpha 1
                        :z {:color 0 :happy? 0 :height 1}
                        :counts [2 1]}
               :local [{:alpha 1
                        :y [0 1 1 1 0 0 1 0 1 0]
                        :counts [5 5]}
                       {:alpha 1
                        :y [0 1 1 1 1 1 0 0 1 1]
                        :counts [3 7]}]}
      x {:color "black" :happy? false}
      m 1]
  (kernel data model latents m))
