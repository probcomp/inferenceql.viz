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


(deftest log-likelihood-view
  "Tests `log-likelihood-view` by manually checking expected output."
  (let [x       {"color" "red"
                 "height" 6}
        row-id  1
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
        ;;    = log(P["color" = "red" | c_0]) + log(P["height" = 6 | c_0])
        ;;    = log(0.5) + log(P[6 ~ N(6, 1))
        ;;   ~= -0.693 + -0.919
        ;;    = -1.612 -> exp: 0.199
        ll (+ (prim/logpdf (get x "color")
                           (get types "color")
                           (get-in view [:categories 0 :parameters "color"]))
              (prim/logpdf (get x "height")
                           (get types "height")
                           (get-in view [:categories 0 :parameters "height"])))

        ll'   (xcat/log-likelihood-view x row-id types latents view)]

    ;; Checking test arguments.
    (is (spec/valid-local-latents? latents))
    (is (spec/valid-view? view))

    ;; Testing output.
    (is (== ll ll'))))

(deftest log-likelihood-views
  "Tests `log-likelihood-views` by manually checking expected output."
  (let [x       {"color" "red"
                 "height" 6
                 "happy?" true}
        row-id  1
        types   {"color"  :categorical
                 "height" :gaussian
                 "happy?" :bernoulli}
        latents {:global {:alpha 1
                          :counts [2 1]
                          :z {"color" 0
                              "happy?" 1
                              "height" 0}}
                 :local [{:alpha   1
                          :counts [4 6]
                          :y      [1 0 1 0 0 1 1 1 1 0]}
                         {:alpha   1
                          :counts [7 3]
                          :y      [0 1 0 0 0 0 0 1 1 0]}]}

        views   [{:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                           "height" {:sigma {:gamma     {:k     0.5 :theta 0.5}}
                                     :mu    {:beta      {:alpha 0.5 :beta  0.5}}}}
                  :categories [{:parameters {"color"  {:p {"red" 0.5 "green" 0.1 "blue" 0.4}}
                                             "height" {:mu 6 :sigma 1}}}
                               {:parameters {"color"  {:p {"red" 0.3 "green" 0.2 "blue" 0.5}}
                                             "height" {:mu 3 :sigma 1}}}]}
                 {:hypers {"happy?"  {:p     {:beta {:alpha 0.5 :beta 0.5}}}}
                  :categories [{:parameters {"happy?"  {:p 0.1}}}
                               {:parameters {"happy?"  {:p 0.9}}}]}]

        model  {:types types
                :views views}

        ;; logP["color" = "red", "height" = 6 | c_0, view_0] + logP["happy?" = true | c_1, view_1]
        ;;    = log(P["color" = "red" | c_0]) + log(P["height" = 6 | c_0]) + log(P["happy?" = true | c_1)
        ;;    = log(0.5) + log(P[6 ~ N(6, 1)) + log(0.9)
        ;;   ~= -0.693 + -0.919 + -0.105
        ;;    = -1.717 -> exp: 0.180
        ll (+ (prim/logpdf (get x "color")
                           (get types "color")
                           (get-in views [0 :categories 0 :parameters "color"]))
              (prim/logpdf (get x "height")
                           (get types "height")
                           (get-in views [0 :categories 0 :parameters "height"]))
              (prim/logpdf (get x "happy?")
                           (get types "happy?")
                           (get-in views [1 :categories 1 :parameters "happy?"])))

        ll'   (xcat/log-likelihood-views x row-id model latents)]

    ;; Checking test arguments.
    (is (spec/valid-xcat?    model))
    (is (spec/valid-latents? latents))

    ;; Testing output.
    (is (== ll ll'))))

(deftest log-likelihood
  "Tests `log-likelihood` by manually checking expected output."
  (let [data {"color"  ["red" "green"]
              "height" [  6      4   ]
              "happy?" [ true  false ]}
        
        types   {"color"  :categorical
                 "height" :gaussian
                 "happy?" :bernoulli}

        latents {:global {:alpha 1
                          :counts [2 1]
                          :z {"color" 0
                              "happy?" 1
                              "height" 0}}
                 :local [{:alpha   1
                          :counts [4 6]
                          :y      [1 0 1 0 0 1 1 1 1 0]}
                         {:alpha   1
                          :counts [7 3]
                          :y      [0 1 0 0 0 0 0 1 1 0]}]}

        views   [{:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                           "height" {:sigma {:gamma     {:k     0.5 :theta 0.5}}
                                     :mu    {:beta      {:alpha 0.5 :beta  0.5}}}}
                  :categories [{:parameters {"color"  {:p {"red" 0.5 "green" 0.1 "blue" 0.4}}
                                             "height" {:mu 6 :sigma 1}}}
                               {:parameters {"color"  {:p {"red" 0.3 "green" 0.2 "blue" 0.5}}
                                             "height" {:mu 3 :sigma 1}}}]}
                 {:hypers {"happy?"  {:p     {:beta {:alpha 0.5 :beta 0.5}}}}
                  :categories [{:parameters {"happy?"  {:p 0.1}}}
                               {:parameters {"happy?"  {:p 0.9}}}]}]

        model  {:types types
                :views views}

        ll-1 (+ (prim/logpdf "red"
                             (get types "color")
                             (get-in views [0 :categories 1 :parameters "color"]))
                (prim/logpdf 6
                             (get types "height")
                             (get-in views [0 :categories 1 :parameters "height"]))
                (prim/logpdf true
                             (get types "happy?")
                             (get-in views [1 :categories 0 :parameters "happy?"])))

        ll-2 (+ (prim/logpdf "green"
                             (get types "color")
                             (get-in views [0 :categories 0 :parameters "color"]))
                (prim/logpdf 4
                             (get types "height")
                             (get-in views [0 :categories 0 :parameters "height"]))
                (prim/logpdf false
                             (get types "happy?")
                             (get-in views [1 :categories 1 :parameters "happy?"])))

        ll   (+ ll-1 ll-2)
        ll'  (xcat/log-likelihood data model latents)]

    ;; Checking test arguments.
    (is (spec/valid-xcat?    model))
    (is (spec/valid-latents? latents))

    ;; Testing output.
    (is (== ll ll'))))

;; crp-weights
;; simulate-category
;; hyperprior-simulate
;; categorical-param-names
;; generate-category
;; view-category-weights
;; sample-category
;; simulate-view
;; simulate
