(ns inferenceql.multimixture.crosscat-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as test :refer [deftest testing is]]
            [expound.alpha :as expound]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [inferenceql.multimixture.utils :as mmix-utils]
            [inferenceql.multimixture.crosscat :as xcat]
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.crosscat.specification :as spec]))

(deftest category-logpdf-score
  "Tests `category-logpdf-score` by manually checking expected output."
  (let [x        {"color" "red"
                  "height" 6}
        types    {"color"  :categorical
                  "height" :gaussian}
        category {:parameters {"color" {:p {"red" 0.5 "blue" 0.3 "green" 0.2}}
                               "height" {:mu 6 :sigma 1}}}
        score    (+ (Math/log 0.5)
                    (prim/logpdf (get x "height")
                                 (get types "height")
                                 (get-in category [:parameters "height"])))
        logp     (xcat/category-logpdf-score x types category)]
    ;; Checking test arguments.
    (is (spec/valid-category? category))

    ;; Checking output.
    (is (== score logp))))

(deftest view-logpdf-score
  "Tests `view-logpdf-score` by manually checking expected output
  with unevenly-weighted categories."
  (let [x       {"color" "red"
                 "height" 6}
        types   {"color"  :categorical
                 "height" :gaussian}
        latents {:alpha   1
                 :counts [4 6]
                 :y      [1 0 1 0 0 1 1 1 1 0]}
        view    {:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                          "height" {:sigma {:gamma     {:k     0.5 :theta 0.5}}
                                    :mu    {:beta      {:alpha 0.5 :beta  0.5}}}}
                 :categories [{:parameters {"color"  {:p {"red" 0.5 "green" 0.1 "blue" 0.4}}
                                            "height" {:mu 6 :sigma 1}}}
                              {:parameters {"color"  {:p {"red" 0.3 "green" 0.2 "blue" 0.5}}
                                            "height" {:mu 3 :sigma 1}}}]}

        ;; logP["color" = "red", "height" = 6 | c_0]
        ;;    = log(weight_0) + log(P["color" = "red" | c_0]) + log(P["height" = 6 | c_0])
        ;;    = log(4/10) + log(0.5) + log(P[6 ~ N(6, 1))
        ;;   ~= -0.916 + -0.693 + -0.919
        ;;    = -2.528 -> exp: 0.0798
        score-1 (+ (Math/log 0.4)
                   (Math/log 0.5)
                   (prim/logpdf (get x "height")
                                (get types "height")
                                (get-in view [:categories 0 :parameters "height"])))

        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = log(weight_1) + log(P["color" = "red" | c_1]) + log(P["height" = 6 | c_1])
        ;;    = log(6/10) + log(0.3) + log(P[6 ~ N(3, 1))
        ;;   ~= -0.511 + -1.204 + -5.419
        ;;    = -7.134 -> exp: 0.0008
        score-2 (+ (Math/log 0.6)
                   (Math/log 0.3)
                   (prim/logpdf (get x "height")
                                (get types "height")
                                (get-in view [:categories 1 :parameters "height"])))

        ;; We need to add these probabilities in the logspace, so we use logsumexp.
        ;; Final score = log(exp(score-1) + exp(score-2))
        ;;            ~= log(0.0806)
        ;;            ~= -2.518
        total-score (mmix-utils/logsumexp [score-1 score-2])
        error       1e-8  ; Accounting for floating point errors.
        logp        (xcat/view-logpdf-score x types latents view)]

    ;; Checking test arguments.
    (is (spec/valid-local-latents? latents))
    (is (spec/valid-view?          view))

    ;; Checking output.
    (is (< (Math/abs (- logp total-score))
           error))))

(deftest logpdf-score
  "Tests `logpdf-score` by manually checking expected output
  with unevenly-weighted categories within one of the views.
  Note that there is no weighting between views, since columns
  are independent given views, by assumption."
  (let [x         {"color" "red"
                   "height" 6
                   "happy?" true}
        types     {"color"  :categorical
                   "height" :gaussian
                   "happy?" :bernoulli}
        latents-l1  {:alpha   1
                     :counts [4 6]
                     :y      [1 0 1 0 0 1 1 1 1 0]}
        latents-l2  {:alpha   1
                     :counts [8 2]
                     :y      [0 0 1 0 0 1 0 0 0 0]}
        latents-g   {:alpha   2
                     :counts [2 1]
                     :z      {"color" 0
                              "height" 0
                              "happy?" 1}}
        latents   {:global latents-g
                   :local [latents-l1 latents-l2]}

        view-1    {:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                            "height" {:sigma {:gamma     {:k     0.5 :theta 0.5}}
                                      :mu    {:beta      {:alpha 0.5 :beta  0.5}}}}
                   :categories [{:parameters  {"color"  {:p {"red" 0.5 "green" 0.1 "blue" 0.4}}
                                               "height" {:mu 6 :sigma 1}}}
                                {:parameters {"color"  {:p {"red" 0.3 "green" 0.2 "blue" 0.5}}
                                              "height" {:mu 3 :sigma 1}}}]}
        view-2    {:hypers {"happy?"  {:p     {:beta {:alpha 0.5 :beta 0.5}}}}
                   :categories [{:parameters {"happy?" {:p 0.7}}}
                                {:parameters {"happy?" {:p 0.5}}}]}
        views    [view-1 view-2]
        xcat     {:types   types
                  :latents latents
                  :views   views}

        ;; logP["color" = "red", "height" = 6 | c_0]
        ;;    = log(weight_0) + log(P["color" = "red" | c_0]) + log(P["height" = 6 | c_0])
        ;;    = log(4/10) + log(0.5) + log(P[6 ~ N(6, 1))
        ;;   ~= -0.916 + -0.693 + -0.919
        ;;    = -2.528 -> exp: 0.0798
        score-11 (+ (Math/log 0.4)
                    (prim/logpdf (get x     "color")
                                 (get types "color")
                                 (get-in view-1 [:categories 0 :parameters "color"]))
                    (prim/logpdf (get x     "height")
                                 (get types "height")
                                 (get-in view-1 [:categories 0 :parameters "height"])))

        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = log(weight_1) + log(P["color" = "red" | c_1]) + log(P["height" = 6 | c_1])
        ;;    = log(6/10) + log(0.3) + log(P[6 ~ N(3, 1))
        ;;   ~= -0.511 + -1.204 + -5.419
        ;;    = -7.134 -> exp: 0.0008
        score-12 (+ (Math/log 0.6)
                    (prim/logpdf (get x     "color")
                                 (get types "color")
                                 (get-in view-1 [:categories 1 :parameters "color"]))
                    (prim/logpdf (get x     "height")
                                 (get types "height")
                                 (get-in view-1 [:categories 1 :parameters "height"])))
        ;; logP["color" = "red", "height" = 6 | v_0]
        ;;    = log(weight_v_0) + logsumexp(score-11 + score-12)

        ;; logP["happy?" = true | c_0]
        ;;    = log(weight_0) + log(P["happy?" = true | c_0])
        ;;    = log(8/10) + log(0.7)
        ;;   ~= -0.223 + -0.357
        ;;    = -0.580 -> exp: 0.5599
        score-21 (+ (Math/log 0.8)
                    (prim/logpdf (get x     "happy?")
                                 (get types "happy?")
                                 (get-in view-2 [:categories 0 :parameters "happy?"])))

        ;; logP["happy?" = true | c_1]
        ;;    = log(weight_1) + log(P["happy?" = true | c_1])
        ;;    = log(2/10) + log(0.5)
        ;;   ~= -1.609 + -0.693
        ;;    = -2.302 -> exp: 0.1001
        score-22 (+ (Math/log 0.2)
                    (prim/logpdf (get x     "happy?")
                                 (get types "happy?")
                                 (get-in view-2 [:categories 1 :parameters "happy?"])))
        ;; logP["color" = "red", "height" = 6 | v_0]
        ;;    = log(weight_v_0) + logsumexp(score-11 + score-12)

        ;; We add these two probabilities together (since dimensions are considered independent)
        ;; to get the final probability.
        ;;    logP["color" = "red", "height" = 6 | xcat]
        ;;        = logp_v_0 + logp_v_1
        ;;        = logsumexp(score-11 + score-12)
        ;;            + logsumexp(score-21 + score-22)
        ;;       ~= -2.518 + -0.416
        ;;        = -2.934 -> exp: 0.0532

        total-score (+ (mmix-utils/logsumexp [score-11 score-12])
                       (mmix-utils/logsumexp [score-21 score-22]))
        error       1e-8  ; Accounting for floating point errors.
        logp        (xcat/logpdf-score x xcat latents)]
    ;; Checking test arguments.
    (is (spec/valid-xcat? xcat))
    ;; Checking output.
    (is (< (Math/abs (- logp total-score))
           error))))

(deftest crp-alpha-counts
  "Tests `crp-alpha-counts` by manually checking expected output."
  (let [alpha     4
        counts    [3 4 4 5]

        ;; The distribution for a CRP involves taking the counts
        ;; of customers at each table and adding an auxiliary table
        ;; with weight alpha (its concentration parameter) and
        ;; normalizing.
        probs     {:p {0 0.15
                       1 0.20
                       2 0.20
                       3 0.25
                       4 0.20}}
        probs-out (xcat/crp-alpha-counts alpha counts)]

    ;; Checking test arguments.
    (is (spec/valid-dist-params? probs))

    ;; Checking output.
    (is (spec/valid-dist-params? probs-out))
    (is (= probs probs-out))))

(deftest category-assignment-simulate
  "Tests `category-assignment-simulate` by calculating the empirical
  distribution and thresholding it with respect to the sampled distribution."
  (let [alpha   4
        counts  [3 4 4 5]
        probs      {:p {0 0.15
                        1 0.20
                        2 0.20
                        3 0.25
                        4 0.20}}
        iters   1000
        samples (repeatedly iters #(xcat/category-assignment-simulate alpha counts))
        error   (* 0.01 (count counts))]

    ;; Checking test arguments.
    (is (spec/valid-dist-params? probs))

    ;; Checking output.
    (mapv (fn [[idx cnt]]
            (is (< (Math/abs (- (/ cnt
                                   iters)
                                (get-in probs [:p idx])))
                   error)))
          (frequencies samples))))

;; 1. category-assignment-simulate
;; 2. hyperprior-simulate
;; 3. category-simulate
;; 4. categorical-param-names
;; 5. generate-category
;; 6. view-simulate
;; 7. simulate
