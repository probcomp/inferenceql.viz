(ns inferenceql.multimixture.crosscat.kernels
  (:require [clojure.test :as test :refer [deftest is]]
            [inferenceql.multimixture.crosscat.kernels.category :as c]
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils :as mmx-utils]
            [clojure.spec.alpha :as s]
            [inferenceql.multimixture.crosscat.specification :as xcats]))

(deftest latents-update-no-delete-no-auxiliary
  "Tests `latents-update` when the datum is shifting assignment
  from one non-singleton category to another."
  (let [latents       {:alpha  1
                       :counts [1 3]
                       :y      [1 1 0 1]}
        row-id        1
        y             (get-in latents [:y row-id])
        y'            0
        delete?       false
        latents'      (c/latents-update row-id y y' latents delete?) 
        counts'       (:counts latents')
        ys'           (:y latents')]
    (is (xcats/valid-local-latents? latents))
    (is (xcats/valid-local-latents? latents'))
    (is (= [1 0 0 1] ys'))
    (is (= [2 2] counts'))))

(deftest latents-update-no-delete-auxiliary
  "Tests `latents-update when the datum is shifting assignment
  from one non-singleton category to an auxiliary category."
  (let [latents       {:alpha  1
                       :counts [1 3]
                       :y      [1 1 0 1]}
        row-id        1
        y             (get-in latents [:y row-id])
        y'-aux        2
        delete?       false
        latents'-aux  (c/latents-update row-id y y'-aux latents delete?)
        counts'-aux   (:counts latents'-aux)
        ys'-aux       (:y latents'-aux)]
    (is (xcats/valid-local-latents? latents))
    (is (xcats/valid-local-latents? latents'-aux))
    (is (= [1 2 0 1] ys'-aux))
    (is (= [1 2 1] counts'-aux))))

(deftest latents-update-delete-no-auxiliary
  "Tests `latents-update when the datum is shifting assignment
  from a singleton category to an existing category."
  (let [latents       {:alpha  1
                       :counts [1 3]
                       :y      [0 1 1 1]}
        row-id        0
        y             (get-in latents [:y row-id])
        y'            1
        delete?       true
        latents'      (c/latents-update row-id y y' latents delete?) 
        counts'       (:counts latents')
        ys'           (:y latents')]
    (is (xcats/valid-local-latents? latents))
    (is (xcats/valid-local-latents? latents'))
    (is (= [0 0 0 0] ys'))
    (is (= [4] counts'))))
