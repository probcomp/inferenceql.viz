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

        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;; UNCONSTRAINED.
        ;; For unconstrained calls to logpdf, we weight categories using the CRP prior.

        ;; logP["color" = "red", "height" = 6 | c_0]
        ;;    = log(weight_0) + log(P["color" = "red" | c_0]) + log(P["height" = 6 | c_0])
        ;;    = log(4/10) + log(0.5) + log(P[6 ~ N(6, 1))
        ;;   ~= -0.916 + -0.693 + -0.919
        ;;    = -2.528 -> exp: 0.0798
        score-1-unconstrained (+ (Math/log 0.4)
                                 (prim/logpdf (get x     "color")
                                              (get types "color")
                                              (get-in view [:categories 0 :parameters "color"]))
                                 (prim/logpdf (get x     "height")
                                              (get types "height")
                                              (get-in view [:categories 0 :parameters "height"])))

        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = log(weight_1) + log(P["color" = "red" | c_1]) + log(P["height" = 6 | c_1])
        ;;    = log(6/10) + log(0.3) + log(P[6 ~ N(3, 1))
        ;;   ~= -0.511 + -1.204 + -5.419
        ;;    = -7.134 -> exp: 0.0008
        score-2-unconstrained (+ (Math/log 0.6)
                                 (prim/logpdf (get x     "color")
                                              (get types "color")
                                              (get-in view [:categories 1 :parameters "color"]))
                                 (prim/logpdf (get x     "height")
                                              (get types "height")
                                              (get-in view [:categories 1 :parameters "height"])))

        ;; We need to add these probabilities in the logspace, so we use logsumexp.
        ;; Final score = log(exp(score-1) + exp(score-2))
        ;;            ~= log(0.0806)
        ;;            ~= -2.518
        total-score-unconstrained (mmix-utils/logsumexp [score-1-unconstrained score-2-unconstrained])

        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;; CONSTRAINED.
        ;; For constrained calls to logpdf, we weight categories using normalized values
        ;; found by taking the logpdf of the constraints for each category.

        Z                   (Math/log (+ 0.5 0.3))

        ;; logP["height" = 6 | c_0, "color" = "red"]
        ;;    = -Z + log(P["height" = 6 | c_0]) + log(P["color" = "red" | c_0])
        ;;    = -0.223 + log(0.5) + log(P[6 ~ N(6, 1))
        ;;   ~= -0.223 + -0.693 + -0.919
        ;;    = -1.835 -> exp: 0.160
        score-1-constrained (+ (- Z)
                               (prim/logpdf (get x     "color")
                                            (get types "color")
                                            (get-in view [:categories 0 :parameters "color"]))
                               (prim/logpdf (get x     "height")
                                            (get types "height")
                                            (get-in view [:categories 0 :parameters "height"])))

        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = -Z + log(P["color" = "red" | c_1]) + log(P["height" = 6 | c_1])
        ;;    = -0.223 + log(0.3) + log(P[6 ~ N(3, 1))
        ;;   ~= -0.223 + -1.204 + -5.419
        ;;    = -6.846 -> exp: 0.0011
        score-2-constrained (+ (- Z)
                               (prim/logpdf (get x     "color")
                                            (get types "color")
                                            (get-in view [:categories 1 :parameters "color"]))
                               (prim/logpdf (get x     "height")
                                            (get types "height")
                                            (get-in view [:categories 1 :parameters "height"])))

        total-score-constrained (mmix-utils/logsumexp [score-1-constrained score-2-constrained])

        error       1e-8  ; Accounting for floating point errors.

        targets-no-constraints x
        targets-constraints    (select-keys x ["height"])

        constraints            (select-keys x ["color"])

        logp-unconstrained     (xcat/view-logpdf-score targets-no-constraints {}       types latents view)
        logp-constrained       (xcat/view-logpdf-score targets-constraints constraints types latents view)]

    ;; Checking test arguments.
    (is (spec/valid-local-latents? latents))
    (is (spec/valid-view?          view))

    ;; Checking unconstrained.
    (is (< (Math/abs (- logp-unconstrained total-score-unconstrained))
           error))

    ;; Checking constrained.
    (is (< (Math/abs (- logp-constrained total-score-constrained))
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

        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;; UNCONSTRAINED.

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

        total-score-unconstrained (+ (mmix-utils/logsumexp [score-11 score-12])
                                     (mmix-utils/logsumexp [score-21 score-22]))

        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;; CONSTRAINED.

        Z                   (Math/log (+ 0.5 0.3))

        ;; logP["height" = 6 | c_0, "color" = "red"]
        ;;    = -Z + log(P["height" = 6 | c_0]) + log(P["color" = "red" | c_0])
        ;;    = -0.223 + log(0.5) + log(P[6 ~ N(6, 1))
        ;;   ~= -0.223 + -0.693 + -0.919
        ;;    = -1.835 -> exp: 0.160
        score-11-constrained (+ (* -1 Z)
                                (prim/logpdf (get x     "color")
                                             (get types "color")
                                             (get-in view-1 [:categories 0 :parameters "color"]))
                                (prim/logpdf (get x     "height")
                                             (get types "height")
                                             (get-in view-1 [:categories 0 :parameters "height"])))

        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = -Z + log(P["color" = "red" | c_1]) + log(P["height" = 6 | c_1])
        ;;    = -0.223 + log(0.3) + log(P[6 ~ N(3, 1))
        ;;   ~= -0.223 + -1.204 + -5.419
        ;;    = -6.846 -> exp: 0.0011
        score-12-constrained (+ (- Z)
                                (prim/logpdf (get x     "color")
                                             (get types "color")
                                             (get-in view-1 [:categories 1 :parameters "color"]))
                                (prim/logpdf (get x     "height")
                                             (get types "height")
                                             (get-in view-1 [:categories 1 :parameters "height"])))
        total-score-constrained (+ (mmix-utils/logsumexp [score-11-constrained score-12-constrained])
                                   (mmix-utils/logsumexp [score-21             score-22]))


        error       1e-8  ; Accounting for floating point errors.

        targets-no-constraints x
        targets-constraints    (select-keys x ["height" "happy?"])

        constraints            (select-keys x ["color"])

        logp-unconstrained     (xcat/logpdf-score xcat latents targets-no-constraints {})
        logp-constrained       (xcat/logpdf-score xcat latents targets-constraints    constraints)]

    ;; Checking test arguments.
    (is (spec/valid-xcat? xcat))

    ;; Checking output.
    ; (is (< (Math/abs (- logp total-score))
    ;        error))))
    ;; Checking unconstrained.
    (is (< (Math/abs (- logp-unconstrained total-score-unconstrained))
           error))

    ;; Checking constrained.
    (is (< (Math/abs (- logp-constrained total-score-constrained))
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

(deftest crp-weights
  "Tests `crp-weights` by manually checking expected output for single counts
  (one category or column) and multiple counts (several categories or columns)."
  (let [alpha         0.1
        counts-single [10]
        counts-many   [5 5 5 5 5 5 5 5 5 5]
        output-single [(Math/log (/ 10 (+ 10 alpha))) (Math/log (/ alpha (+ 10 alpha)))]
        output-many   (map #(Math/log %) (concat (repeat (count counts-many)
                                                              (/ 5 (+ 50 alpha)))
                                                      [(/ alpha (+ 50 alpha))]))

        weights-single (xcat/crp-weights alpha counts-single)
        weights-many   (xcat/crp-weights alpha counts-many)]

    ;; Testing output.
    (is (= output-single weights-single))
    (is (= output-many   weights-many))))

(deftest simulate-category
(let [types    {"happy?" :bernoulli
                "height" :gaussian
                "color"  :categorical}

      category {:parameters {"happy?" {:p 0.9}
                             "height" {:mu 6 :sigma 0.01}
                             "color"  {:p {"red" 0.5 "blue" 0.3 "green" 0.2}}}}

      targets-no-constraints ["happy?" "height" "color"]
      targets-constraints    ["happy?" "height"]
      constraints            ["color"]

      n                     10000
      error                 0.05

      samples-unconstrained (repeatedly n #(xcat/simulate-category
                                            category
                                            types
                                            targets-no-constraints
                                            {}))

      samples-constrained   (repeatedly n #(xcat/simulate-category
                                            category
                                            types
                                            targets-constraints
                                            constraints))
      stats-fn              (fn [samples]
                              (reduce (fn [m col]
                                        (let [values (map #(get % col) samples)
                                              stat  (if (= (get types col) :gaussian)
                                                      (/ (reduce + values) n)
                                                      (frequencies values))]
                                          (assoc m col stat)))
                                      {}
                                      (keys types)))
      stats-unconstrained   (stats-fn samples-unconstrained)
      stats-constrained     (stats-fn samples-constrained)]
    ;; Checking test arguments.
    (is (spec/valid-category? category))

    ;; Testing unconstrained output.
    ;; Bernoulli.
    (is (< (Math/abs (- (get-in category [:parameters "happy?" :p])
                        (/ (get-in stats-unconstrained ["happy?" true])
                           n)))
           error))

    ;; Categorical. Only need to check two, since the third is implied.
    (is (< (Math/abs (- (get-in category [:parameters "color" :p "red"])
                        (/ (get-in stats-unconstrained ["color" "red"])
                           n)))
           error))
    (is (< (Math/abs (- (get-in category [:parameters "color" :p "green"])
                        (/ (get-in stats-unconstrained ["color" "green"])
                           n)))
           error))

    ;; Gaussian.
    (is (< (Math/abs (- (get-in category [:parameters "height" :mu])
                        (get stats-unconstrained "height")))
           error))))

(deftest categorical-param-names
  (let [view {:hypers {"color" {:p {:dirichlet {:alpha [1 1 1]}}}}
              :categories [{:parameters {"color" {:p {"red" 0.5 "green" 0.3 "blue" 0.2}}}}]}
        col-name "color"
        names    ["red" "green" "blue"]
        output   (xcat/categorical-param-names view col-name)]
    ;; Checking test arguments.
    (spec/valid-view? view)

    ;; Testing output.
    (is (= (set names) (set output)))))

(deftest generate-category
  (let [types    {"happy?" :bernoulli
                  "height" :gaussian
                  "color"  :categorical}
        view {:hypers {"color" {:p {:dirichlet {:alpha [1 1 1]}}}
                       "height" {:mu {:beta {:alpha 0.5 :beta 0.5}}
                                 :sigma {:gamma {:k 1 :theta 6}}}
                       "happy?" {:p {:beta {:alpha 0.5 :beta 0.5}}}}
              :categories [{:parameters {"color" {:p {"red" 0.5 "green" 0.3 "blue" 0.2}}
                                         "height" {:mu 0 :sigma 1}
                                         "happy?" {:p 0.9}}}]}

        m-single  1
        m-many    5

        samples-single (xcat/generate-category m-single view types)
        samples-many   (xcat/generate-category m-many   view types)]
    ;; Checking test arguments.
    (spec/valid-view? view)

    ;; Testing output.
    (is (every? #(and (spec/valid-category? %)
                      (= (set (keys types))
                         (set (keys (:parameters %))))) samples-single))
    (is (every? #(and (spec/valid-category? %)
                      (= (set (keys types))
                         (set (keys (:parameters %))))) samples-many))))

(deftest mutual-information
  (let [model   {:types {"foo" :bernoulli
                         "bar" :gaussian
                         "baz" :categorical}
                 :views [{:hypers     {"foo" {:p {:beta {:alpha 0.5 :beta 0.5}}}
                                       "bar" {:mu {:beta {:alpha 0.5 :beta 0.5}}
                                              :sigma {:gamma {:k 1 :theta 5}}}
                                       "baz" {:p {:dirichlet {:alpha [1 1 1]}}}}
                          :categories [{:parameters {"foo" {:p 0.01}
                                                     "bar" {:mu 0 :sigma 0.1}
                                                     "baz" {:p {"fizz" 0.8 "bang" 0.1 "boom" 0.1}}}}
                                       {:parameters {"foo" {:p 0.99}
                                                     "bar" {:mu 5 :sigma 0.1}
                                                     "baz" {:p {"fizz" 0.1 "bang" 0.2 "boom" 0.7}}}}]}]}
        latents {:global {:alpha 1
                          :counts [2]
                          :z  {"foo" 0
                               "bar" 0}}
                 :local  [{:alpha  1
                           :counts [5 5]
                           :y [0 1 0 1 0 1 0 1 0 1]}]}
        target-a     "foo"
        target-b     "bar"
        n-samples    1000
        constraints {"foo" true}

        mi-unconstrained (xcat/mutual-information model latents target-a target-b {} n-samples)
        mi-constrained   (xcat/mutual-information model latents target-a target-b constraints n-samples)

        near-zero 1e-15]

    ;; Checking test arguments.
    (spec/valid-xcat? model)
    (spec/valid-latents? latents)

    ;; Testing output.
    (is (< (Math/exp mi-unconstrained)
           near-zero))

    (is (< (Math/exp mi-constrained)
           near-zero))))
