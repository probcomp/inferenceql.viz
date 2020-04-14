(ns inferenceql.multimixture.crosscat.kernels.concentration-params
  (:require [inferenceql.multimixture.crosscat   :as xcat]
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils      :as mmix-utils]))

(defn alpha-scores
  "Given a grid of alpha values, and counts representing a CRP,
  calculates the score of each gridpoint.

  In:
    `grid`   [`vec double`]: vector of candidates for alpha.
    `counts`    [`vec int`]: vector of customer counts of each
                             group.
  Out:
    [`vector double`]: scores of each alpha candidate with
                       respect to the provided counts."
  [grid counts]
  (map #(prim/crp-logpdf counts {:alpha %}) grid))

(defn alpha-sample
  "Given a grid of alpha values and their scores, samples a new
  value of alpha from the normalized categorical distribution
  created by the scores.

  In:
    `grid`   [`vec double`]: vector of candidates for alpha.
    `scores`    [`vec int`]: scores of each alpha candidate
                             in `grid`.
  Out:
    [`int`]: a group assignment sampled from the posterior
             approximated by the weights."
  [grid scores]
  (let [Z       (mmix-utils/logsumexp scores)
        p-tilde (map #(- % Z) scores)
        probs {:p (zipmap grid p-tilde)}]
    (prim/log-categorical-simulate probs)))

(defn kernel-group
  "Given counts representing a CRP, samples a new value of
  alpha from a discretized approximation to the posterior,
  alpha ~ P(alpha | counts).

  In:
    `counts`    [`vec int`]: vector of customer counts of each
                             group.
  Out:
    [`int`]: a group assignment sampled from the posterior
             approximated by the weights."
  [counts]
  (let [n-customers   (reduce + counts)
        n-grid-points 100
        grid          (mmix-utils/loglinspace
                        (/ 1 n-customers)
                        n-customers
                        n-grid-points)
        alpha-scores  (alpha-scores grid counts)
        alpha'        (alpha-sample grid alpha-scores)]
    alpha'))

(defn kernel
  "Concentration hyperparameter inference kernel, as specified
  in the CrossCat paper. Requires only a latents structure.

  In:
    `latents` [`latents`]: specified latents of CrossCat model.
  Out:
    [`latents`]: the result of calling `kernel-group` on each
                 of the latent groups in `latents`."
  [latents]
  (let [global       (:global latents)
        locals       (:local  latents)
        alpha-outer  (kernel-group (:counts global))
        alphas-inner (map-indexed (fn [idx local]
                                    [idx (kernel-group (:counts local))])
                                  locals)]
    (reduce (fn [latents' [idx alpha]]
              (assoc-in latents' [:local idx :alpha] alpha))
            (assoc-in latents [:global :alpha] alpha-outer)
            alphas-inner)))