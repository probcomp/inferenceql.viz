(ns inferenceql.multimixture.crosscat.kernels.category-test
  (:require [clojure.test :as test :refer [deftest is]]
            [inferenceql.multimixture.crosscat.kernels.category :as c]
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils :as mmix-utils]
            [clojure.spec.alpha :as s]
            [inferenceql.multimixture.crosscat.specification :as xcats]))

(deftest category-weights-singleton
  "Tests `category-weights` by verifying that the singleton's weight
  is deferred to the weight of the auxiliary category weight. Other weights
  are manually checked."
  (let [latents          {:alpha  2
                          :counts [1 4 1]
                          :y      [0 1 1 2 1 1]}
        alpha            (:alpha latents)
        y                0
        singleton?       true

        ;; When singleton? is true and a datum is currently contained in
        ;; that category, we treat one of the auxiliary categories as the
        ;; current category for convenience. This is mentioned in Algorithm 8
        ;; of http://www.stat.columbia.edu/npbayes/papers/neal_sampling.pdf .

        m1               1  ; One auxiliary category.
        m2               2  ; Two auxiliary categories.
        m3               3  ; Three auxiliary categories.
        n                (count (:y latents))
        norm             (+ n -1 alpha)

        ;; Auxiliary weights.
        aux1             (/ alpha m1)
        aux2             (/ alpha m2)
        aux3             (/ alpha m3)
        [m1' weights1]   (c/category-weights latents y singleton? m1)
        [m2' weights2]   (c/category-weights latents y singleton? m2)
        [m3' weights3]   (c/category-weights latents y singleton? m3)]
    ;; Checking test arguments.
    (is (xcats/valid-local-latents? latents))

    ;; m = 1
    (is (= 0 m1'))
    (is (= (mapv #(Math/log (/ % norm)) [aux1 4 1]) weights1))
    (is (== 1.0 (reduce + (map #(Math/exp %) weights1))))

    ;; m = 2
    (is (= 1 m2'))
    (is (= (mapv #(Math/log (/ % norm)) [aux2 4 1 aux2]) weights2))
    (is (== 1.0 (reduce + (map #(Math/exp %) weights2))))

    ;; m = 3
    (is (= 2 m3'))
    (is (= (mapv #(Math/log (/ % norm)) [aux3 4 1 aux3 aux3]) weights3))
    (is (== 1.0 (reduce + (map #(Math/exp %) weights3))))))

(deftest category-weights-no-singleton
  "Tests `category-weights` by verifying that the count of the current
  cluster is decremented before calculating the weight. Other weights are
  manually checked."
  (let [latents          {:alpha  2
                          :counts [2 4 1]
                          :y      [0 1 1 0 2 1 1]}
        alpha            (:alpha latents)
        y                0
        singleton?       false

        ;; When singleton? is false and a datum is currently contained in
        ;; that category, we decrement the count of that category before
        ;; using said count as its weight. This is mentioned in Algorithm 8
        ;; of http://www.stat.columbia.edu/npbayes/papers/neal_sampling.pdf .

        m1               1  ; One auxiliary category.
        m2               2  ; Two auxiliary categories.
        m3               3  ; Three auxiliary categories.
        n                (count (:y latents))
        norm             (+ n -1 alpha)

        ;; Auxiliary weights.
        aux1             (/ alpha m1)
        aux2             (/ alpha m2)
        aux3             (/ alpha m3)
        [m1' weights1]   (c/category-weights latents y singleton? m1)
        [m2' weights2]   (c/category-weights latents y singleton? m2)
        [m3' weights3]   (c/category-weights latents y singleton? m3)]
    ;; Checking test arguments.
    (is (xcats/valid-local-latents? latents))

    ;; m = 1
    (is (= 1 m1'))
    (is (= (mapv #(Math/log (/ % norm)) [1 4 1 aux1]) weights1))
    (is (== 1.0 (reduce + (map #(Math/exp %) weights1))))

    ;; m = 2
    (is (= 2 m2'))
    (is (= (mapv #(Math/log (/ % norm)) [1 4 1 aux2 aux2]) weights2))
    (is (== 1.0 (reduce + (map #(Math/exp %) weights2))))

    ;; m = 3
    (is (= 3 m3'))
    (is (= (mapv #(Math/log (/ % norm)) [1 4 1 aux3 aux3 aux3]) weights3))
    (is (== 1.0 (reduce + (map #(Math/exp %) weights3))))))

(deftest category-scores
  "Tests `category-scores` by verifying that the first categories returned
  are the same as originally specified, and manually checking those scores."
  (let [x   {"color" "red"
             "height" 6}
        types {"color"  :categorical
               "height" :gaussian}
        view {:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                       "height" {:sigma {:beta      {:alpha 0.5 :beta 0.5}}
                                 :mu    {:beta      {:alpha 0.5 :beta 0.5}}}}
              :categories [{:parameters {"color"  {:p {"red" 0.5 "green" 0.1 "blue" 0.4}}
                                         "height" {:mu 6 :sigma 1}}}
                           {:parameters {"color"  {:p {"red" 0.3 "green" 0.2 "blue" 0.5}}
                                         "height" {:mu 5 :sigma 1}}}]}
        ;; logP for category c_0 for datum = {"color" "red", "height" 6}.
        ;; logP["color" = "red", "height" = 6 | c_0]
        ;;    = logP["color" = "red" | c_0] + logP["height" = 6 | c_0]
        ;;    = log(0.5) + log(P[6 ~ N(6, 1))
        ;;   ~= -0.693 + -0.919
        ;;    = -1.612 -> exp: 0.199
        score-1 (+ (Math/log 0.5)
                   (prim/logpdf (get x "height")
                                (get types "height")
                                (get-in view [:categories 0 :parameters "height"])))

        ;; logP for category c_1 for datum = {"color" "red", "height" 6}.
        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = logP["color" = "red" | c_1] + logP["height" = 6 | c_1]
        ;;    = log(0.3) + log(P[6 ~ N(3, 1))
        ;;   ~= -1.204 + -5.419
        ;;    = -6.623 -> exp: 0.001
        score-2 (+ (Math/log 0.3)
                   (prim/logpdf (get x "height")
                                (get types "height")
                                (get-in view [:categories 1 :parameters "height"])))
        m1 1  ; One auxiliary category.
        m2 2  ; Two auxiliary categories.
        [[cat1 cat2 _]    [s1 s2 _]]     (c/category-scores x view types m1)
        [[cat1' cat2' _ _][s1' s2' _ _]] (c/category-scores x view types m2)]
    ;; Checking test arguments.
    (is (xcats/valid-view? view))

    ;; m = 1
    (is (= score-1 s1))
    (is (= score-2 s2))
    (is (= cat1 (get-in view [:categories 0])))
    (is (= cat2 (get-in view [:categories 1])))
    (is xcats/valid-category? cat1)
    (is xcats/valid-category? cat2)

    ;; m = 2
    (is (= score-1 s1'))
    (is (= score-2 s2'))
    (is (= cat1' (get-in view [:categories 0])))
    (is (= cat2' (get-in view [:categories 1])))
    (is xcats/valid-category? cat1')
    (is xcats/valid-category? cat2')))

(deftest category-sample
  "Tests `category-sample` by checking empirical mean with the given
  distribution being sampled."
  (let [weights   (mapv #(Math/log %) [0.2 0.3  0.1 0.4])
        scores    (mapv #(Math/log %) [0.1 0.05 0.1 0.3])

        ;; The probabilities will be the result of weighting each score
        ;; with the appropriate weight and normalizing.
        probs     (map (comp (partial apply +) vector) weights scores)
        Z         (mmix-utils/logsumexp probs)
        probs     (mapv #(- % Z) probs)

        ;; We verify the sampling function by taking a relatively large
        ;; number of examples and calculating the empirical distribution.
        n         1000
        samples   (doall (repeatedly n #(c/category-sample weights scores)))
        counts    (sort-by key (frequencies samples))
        emp-probs (mapv #(double (/ % n)) (vals counts))

        ;; We define a residual error of 0.01 for each of the weights.
        error     (* 0.01 (count weights))
        residual  (reduce + (map - emp-probs (map #(Math/exp %) probs)))]
    (is (< residual error))))

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

        ;; Visualizing counts and assignments as customers in a CRP:
        ;;   [1 1 0 1]        ->        [2] [0 1 3]
        ;;      ^assignments               ^customers at tables

        ;; When y' is 0, that represents the following shift:
        ;;    [2] [0 1 3] -> [1 2] [0 3] -> counts [2 2], assignments [1 0 0 1]

        latents'      (c/latents-update row-id y y' latents delete?)
        counts'       (:counts latents')
        ys'           (:y latents')]
    (is (xcats/valid-local-latents? latents))
    (is (xcats/valid-local-latents? latents'))
    (is (= [1 0 0 1] ys'))
    (is (= [2 2] counts'))))

(deftest latents-update-no-delete-auxiliary
  "Tests `latents-update` when the datum is shifting assignment
  from one non-singleton category to an auxiliary category."
  (let [latents       {:alpha  1
                       :counts [1 3]
                       :y      [1 1 0 1]}
        row-id        1
        y             (get-in latents [:y row-id])
        y'-aux        2
        delete?       false

        ;; Visualizing counts and assignments as customers in a CRP:
        ;;   [1 1 0 1]        ->        [2] [0 1 3]
        ;;      ^assignments               ^customers at tables

        ;; When y' is 2, that represents the following shift:
        ;;    [2] [0 1 3] -> [2] [0 3] [1] -> counts [1 2 1], assignments [1 2 0 1]

        latents'-aux  (c/latents-update row-id y y'-aux latents delete?)
        counts'-aux   (:counts latents'-aux)
        ys'-aux       (:y latents'-aux)]
    (is (xcats/valid-local-latents? latents))
    (is (xcats/valid-local-latents? latents'-aux))
    (is (= [1 2 0 1] ys'-aux))
    (is (= [1 2 1] counts'-aux))))

(deftest latents-update-delete-no-auxiliary
  "Tests `latents-update` when the datum is shifting assignment
  from a singleton category to an existing category."
  (let [latents       {:alpha  1
                       :counts [1 3]
                       :y      [0 1 1 1]}
        row-id        0
        y             (get-in latents [:y row-id])
        y'            1
        delete?       true

        ;; Visualizing counts and assignments as customers in a CRP:
        ;;   [0 1 1 1]        ->        [0] [1 2 3]
        ;;      ^assignments               ^customers at tables

        ;; When y' is 1 (and delete? is true), that represents the following shift:
        ;;    [0] [1 2 3] -> [] [0 1 2 3] -> [0 1 2 3] -> counts [1 2 1], assignments [1 2 0 1]
        ;;                    ^ we get rid of the empty table, since this is zero-probability
        ;;                      event as defined by a CRP.

        latents'      (c/latents-update row-id y y' latents delete?)
        counts'       (:counts latents')
        ys'           (:y latents')]
    (is (xcats/valid-local-latents? latents))
    (is (xcats/valid-local-latents? latents'))
    (is (= [0 0 0 0] ys'))
    (is (= [4] counts'))))

(deftest kernel-row-mislabeled-category
  "Tests `row-kernel` by specifying two distinct categories with the target row
  labeled in the wrong category. The output should contains a `latents-l` structure
  that reflects the target row being moved to the other category at least 95% of the time."
  (let [x       {"color" "red"
                 "height" 6}
        types   {"color"  :categorical
                 "height" :gaussian}
        latents {:alpha   1
                 :counts [5 5]
                 :y      [1 0 0 0 0 1 1 1 1 0]}
        view    {:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                          "height" {:sigma {:beta      {:alpha 0.5 :beta 0.5}}
                                    :mu    {:beta      {:alpha 0.5 :beta 0.5}}}}
                 :categories [{:parameters  {"color"  {:p {"red" 0.5 "green" 0.1 "blue" 0.4}}
                                             "height" {:mu 6 :sigma 1}}}
                              {:parameters {"color"  {:p {"red" 0.3 "green" 0.2 "blue" 0.5}}
                                            "height" {:mu 3 :sigma 1}}}]}

        ;; logPs for categories c_0, c_1, for datum = {"color" "red", "height" 6}.
        ;; logP["color" = "red", "height" = 6 | c_0]
        ;;    = log(weight_0 * P["color" = "red" | c_0]) + log(weight_1 * P["height" = 6 | c_0])
        ;;    = log(0.5) + log(P[6 ~ N(6, 1))         -- ignoring the weight, because they are same.
        ;;   ~= -0.693 + -0.919
        ;;    = -1.612 -> exp: 0.199

        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = log(weight_1 * P["color" = "red" | c_1]) + log(weight_1 * P["height" = 6 | c_1])
        ;;    = log(0.3) + log(P[6 ~ N(3, 1))         -- ignoring the weight, because they are same.
        ;;   ~= -1.204 + -5.419
        ;;    = -6.623 -> exp: 0.001

        ;; This implies that the transition should occur roughly 0.5% (1/200) of the time.
        ;; We adjust the switch-% below to be 97.5% to give some leeway for our sample size.

        m1         1      ; One auxiliary category.
        m2         2      ; Two auxiliary categories.
        row-id     0
        desired-y' 0      ; The mislabeled point should go from category 1 to 0.
        switch-%   0.975  ; The threshold proporation of switching to the other category.
        iters      1000
        [latents-1' view-1'] (mmix-utils/transpose
                               (repeatedly iters #(c/kernel-row x row-id m1 types latents view)))
        ys-1'                (frequencies (mapv #(get-in % [:y row-id]) latents-1'))
        [latents-2' view-2'] (mmix-utils/transpose
                               (repeatedly iters #(c/kernel-row x row-id m2 types latents view)))
        ys-2'                (frequencies (mapv #(get-in % [:y row-id]) latents-2'))]
   ;; Checking test arguments.
   (is (xcats/valid-local-latents? latents))
   (is (xcats/valid-view?          view))

   ;; m = 1
   (is (every? xcats/valid-local-latents? latents-1'))
   (is (every? xcats/valid-view?          view-1'))
   (is (>= (get ys-1' desired-y') (* switch-% iters)))

   ;; m = 2
   (is (every? xcats/valid-local-latents? latents-2'))
   (is (every? xcats/valid-view?          view-2'))
   (is (>= (get ys-1' desired-y') (* switch-% iters)))
   (is (>= (get ys-2' desired-y') (* switch-% iters)))))

(deftest row-kernel-equally-likely-categories
  "Tests `row-kernel` by specifying two equally likely categories with the target row
  labeled in one of those categories. The output should contains a `latents-l` structure
  that reflects the target row being moved to the other category at most 70% of the time."
  (let [x       {"color" "red"
                 "height" 6}
        types   {"color"  :categorical
                 "height" :gaussian}
        latents {:alpha   1
                 :counts [5 5]
                 :y      [1 0 0 0 0 1 1 1 1 0]}
        view    {:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                          "height" {:sigma {:beta  {:alpha 0.5 :beta 0.5}}
                                    :mu    {:beta  {:alpha 0.5 :beta 0.5}}}}
                 :categories [{:parameters {"color"  {:p {"red" 0.5 "green" 0.1 "blue" 0.4}}
                                            "height" {:mu 5 :sigma 2}}}
                              {:parameters {"color" {:p {"red" 0.5 "green" 0.2 "blue" 0.3}}
                                            "height" {:mu 7 :sigma 2}}}]}

        ;; logPs for categories c_0, c_1, for datum = {"color" "red", "height" 6}.
        ;; logP["color" = "red", "height" = 6 | c_0]
        ;;    = log(weight_0 * P["color" = "red" | c_0]) + log(weight_1 * P["height" = 6 | c_0])
        ;;    = log(0.5) + log(P[6 ~ N(5, 2))         -- ignoring the weight, because they are same.
        ;;   ~= -0.693 + -1.737
        ;;    = -2.430 -> exp: 0.088

        ;; logP["color" = "red", "height" = 6 | c_1]
        ;;    = log(weight_1 * P["color" = "red" | c_1]) + log(weight_1 * P["height" = 6 | c_1])
        ;;    = log(0.5) + log(P[6 ~ N(7, 2))         -- ignoring the weight, because they are same.
        ;;   ~= -0.693 + -1.737
        ;;    = -2.430 -> exp: 0.088

        ;; Not surprisingly, this implies that the transition should occur roughly 50% of the time.
        ;; We adjust the switch-% below to be 45% to give some leeway for our sample size.

        score-1 (+ (Math/log 0.5)
                   (prim/logpdf (get x "height")
                                (get types "height")
                                (get-in view [:categories 0 :parameters "height"])))
        score-2 (+ (Math/log 0.3)
                   (prim/logpdf (get x "height")
                                (get types "height")
                                (get-in view [:categories 1 :parameters "height"])))

        m1         1     ; One auxiliary category.
        m2         2     ; Two auxiliary categories.
        row-id     0
        desired-y' 0     ; The mislabeled point should go from category 1 to 0.
        switch-%   0.45  ; The threshold proporation of switching to the other category.
        iters      1000

        [latents-1' view-1'] (mmix-utils/transpose
                               (repeatedly iters #(c/kernel-row x 0 m1 types latents view)))
        ys-1'                (frequencies  (mapv #(get-in % [:y row-id]) latents-1'))
        [latents-2' view-2'] (mmix-utils/transpose
                               (repeatedly iters #(c/kernel-row x 0 m2 types latents view)))
        ys-2'                (frequencies  (mapv #(get-in % [:y row-id]) latents-2'))]
    ;; Checking test arguments.
    (is (xcats/valid-local-latents? latents))

    ;; m = 1
    (is (every? xcats/valid-local-latents? latents-1'))
    (is (every? xcats/valid-view?          view-1'))
    (is (>= (get ys-1' 0) (* switch-% iters)))

    ;; m = 2
    (is (every? xcats/valid-local-latents? latents-2'))
    (is (every? xcats/valid-view?          view-2'))
    (is (>= (get ys-2' 0) (* switch-% iters)))))
