(ns inferdb.multimixture.basic-queries-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as test :refer [deftest testing is]]
            [clojure.walk :as walk :refer [stringify-keys]]
            [expound.alpha :as expound]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [inferdb.utils :as utils]
            [inferdb.plotting.generate-vljson :as plot]
            [inferdb.multimixture.specification :as spec]
            [inferdb.multimixture.basic-queries :as bq]
            [metaprob.distributions :as dist]
            [inferdb.multimixture.search :as search] ;; XXX: why is the "optimized" row generator in search?
            ))

;; The following data generator has some interesting properties:
;; - clusters 0 and 1 in view 0 share the samme mu parameter.
;; - a is a deterministic indicator of the cluster.
;; - b is a noisy copy of a.
;; - in both views, clusters are equally weighted.
;; - in view 1, the third Gaussian components (cluster 0) "spans" the domain of
;; all the other components and share a center with cluster 1.
;;
;; I'd encourage everyone who works with the file to run the tests in this file
;; and then run make charts to see how the components relate.
(def multi-mixture
   {:vars {"x" :gaussian
           "y" :gaussian
           "z" :gaussian
           "a" :categorical
           "b" :categorical
           "c" :categorical}
    :views [[  {:probability 0.1666666666666
                :parameters {"x" {:mu 3 :sigma 1}
                             "y" {:mu 4 :sigma 0.1}
                             "a" {"0" 1.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.95, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability  0.1666666666666
                :parameters {"x" {:mu 3 :sigma 0.1}
                             "y" {:mu 4 :sigma 1}
                             "a" {"0" 0.0 "1" 1.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.95, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.1666666666666
                :parameters {"x" {:mu 8  :sigma 0.5}
                             "y" {:mu 10 :sigma 1}
                             "a" {"0" 0.0 "1" 0.0 "2" 1.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.95, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.1666666666666
                :parameters {"x" {:mu 14  :sigma 0.5}
                             "y" {:mu  7  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 1.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.95, "4" 0.01, "5" 0.01}}}
               {:probability 0.1666666666666
                :parameters {"x" {:mu 16  :sigma 0.5}
                             "y" {:mu  9  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 1.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.95, "5" 0.01}}}
               {:probability 0.16666666666667
                :parameters {"x" {:mu  9  :sigma 2.5}
                             "y" {:mu 16  :sigma 0.1}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 1.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.95}}}]
              [{:probability 0.25
                :parameters {"z" {:mu 0 :sigma 1}
                             "c" {"0" 1.0, "1" 0.0, "2" 0.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 15 :sigma 1}
                             "c" {"0" 0.0, "1" 1.0, "2" 0.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 30 :sigma 1}
                             "c" {"0" 0.0, "1" 0.0, "2" 1.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 15 :sigma 8}
                             "c" {"0" 0.0, "1" 0.0, "2" 0.0, "3" 1.0}}}]]})

(deftest multi-mixture-is-valid
  (when-not (s/valid? ::spec/multi-mixture multi-mixture)
    (expound/expound ::spec/multi-mixture multi-mixture))
  (is (s/valid? ::spec/multi-mixture multi-mixture)))

(def test-points
  [{:x 3  :y 4}
   {:x 8  :y 10}
   {:x 14 :y 7}
   {:x 15 :y 8}
   {:x 16 :y 9}
   {:x 9  :y 16}])

(def cluster-point-mapping
  ;; Maps each cluster index to the ID of the point that is at the cluster's
  ;; center. Note that not all points have a cluster around them.
  {0 1
   1 1
   2 2
   3 3
   ;; P4 between clusters 4 and 5
   4 5
   5 6})

(defn test-point
  "Retrieves a given point given its ID. Note that point IDs are different from
  their indexes in `test-points`: Point IDs are 1-indexed."
  [point-id]
  (nth test-points (dec point-id)))

(defn invert-map
  "Reverse the keys/values of a map"
  [m]
  (reduce-kv (fn [m k v]
               (update m v (fnil conj #{}) k))
             {}
             m))


(defn euclidean-distance
  [p1 p2]
  (Math/sqrt (->> (map - p1 p2)
                  (map #(Math/pow % 2))
                  (reduce +))))

(deftest points-equidistant-from-cluster-centers
  (doseq [[point-id clusters] (invert-map cluster-point-mapping)]
    (testing (str "Each cluster in " clusters " should be an equal distance from P" point-id)
      ;; Subsequent tests rely on this property of the test multimixture.
      (let [{:keys [x y] :as _point} (test-point point-id)
            cluster-center (fn [cluster]
                             [(spec/mu multi-mixture "x" cluster)
                              (spec/mu multi-mixture "y" cluster)])
            distances (->> clusters
                           (map cluster-center)
                           (map #(euclidean-distance [x y] %)))]
        (is (apply = distances))))))

;; XXX When I ported this, I got really derailed, because the whole machinery
;; below relies on variables not being all variables in the spec, but just the
;; variables in the first view.
(def variables (spec/view-variables (first (:views multi-mixture))))
(def numerical-variables
  (into #{}
        (filter #(spec/numerical? multi-mixture %))
        variables))
(def categorical-variables
  (into #{}
        (filter #(spec/nominal? multi-mixture %))
        variables))

(deftest test-cluster-point-mapping
  ;; This test verifies that we've constructed our multi-mixture correctly such
  ;; that the subsequent tests can succeed. Clusters that have a point at their
  ;; center should have that point's variables as the mu for each gaussian that
  ;; makes up the cluster gaussian that makes up the cluster.
  (doseq [[cluster point-id] cluster-point-mapping]
    (doseq [variable #{:x :y}]
      (let [point-value (get (test-point point-id) variable)
            mu (spec/mu multi-mixture (name variable) cluster)]
        (is (= point-value mu))))))

(def row-generator (search/optimized-row-generator multi-mixture))

;; Some smoke tests.
(deftest test-smoke-row-generator
 (is (map? (row-generator))))

(deftest test-smoke-simulate
 (is (= 3 (count (bq/simulate row-generator {} 3)))))

(deftest test-smoke-simulate-conditional
 (is (= 999. (get (first (bq/simulate row-generator {"x" 999.} 3)) "x"))))

(deftest test-smoke-logpdf
 (is (float? (bq/logpdf row-generator {"x" 0.} {"y" 1.}))))

(def plot-point-count 1000)
;; The purpose of this test is to help the reader understand the test suite. It
;; generates Vega-Lite JSON as a side effect which can be rendered into charts.
;; See https://github.com/probcomp/inferenceql/issues/81 for why it is
;; Clojure-only.
;; XXX: Ulli's REPL doesn't allow for conditional reads.
(deftest visual-test
          ;; This tests saves plots for all simulated data in out/json results/
          ;; Charts can be generated with `make charts`.
          (testing "(smoke) simulate n complete rows and save them as vl-json"
            (let [num-samples plot-point-count
                  point-data (map-indexed (fn [index point]
                                            (reduce-kv (fn [m k v]
                                                         (assoc m (keyword (str "t" (name k))) v))
                                                       {:test-point (str "P " (inc index))}
                                                       point))
                                          test-points)
                  samples (take plot-point-count (repeatedly row-generator))]
              (utils/save-json "simulations-x-y"
                               (plot/scatter-plot-json ["x" "y"]
                                                       samples
                                                       point-data
                                                       [0 18]
                                                       "View 1: X, Y, A, B"))
              (utils/save-json "simulations-z"
                               (plot/hist-plot (utils/column-subset samples ["z" "c"])
                                               ["z" "c"]
                                               "Dim Z and C"))
              (doseq [variable #{"a" "b" "c"}]
                (utils/save-json (str "simulations-" (name variable))
                                 (plot/bar-plot (utils/column-subset samples [variable])
                                                (str "Dim " (-> variable name str/upper-case))
                                                plot-point-count)))
              (is (= (count samples) plot-point-count)))))


(def simulation-count 100)
(def threshold 0.5)

(defn- almost-equal? [a b] (utils/almost-equal? a b utils/relerr threshold))
(defn- almost-equal-vectors? [a b] (utils/almost-equal-vectors? a b utils/relerr threshold))
(defn- almost-equal-maps? [a b] (utils/almost-equal-maps? a b utils/relerr threshold))
(defn- almost-equal-p? [a b] (utils/almost-equal? a b utils/relerr 0.01))

(deftest simulations-conditioned-on-determinstic-category
  (doseq [[cluster point-id] cluster-point-mapping]
    (testing (str "Conditioned on  deterministic category" cluster)
      ;; We simulate all variables together in a single test like this because
      ;; there's currently a performance benefit to doing so.
      (let [point (test-point point-id)
            samples (bq/simulate row-generator
                                 {"a" (str cluster)}
                                 simulation-count)]
        (doseq [variable variables]
            (cond (spec/numerical? multi-mixture variable)
                  (let [samples (utils/col variable samples)]
          (testing (str "validate variable " variable)
                    (testing "mean"
                      (is (almost-equal? (get point (keyword variable))
                                         (utils/average samples))))
                    #_(testing "standard deviation"
                      (let [analytical-std (spec/sigma multi-mixture variable cluster)]
                        (is (utils/within-factor? analytical-std
                                                  (utils/std samples)
                                                  2)))))

                  (spec/nominal? multi-mixture variable)
                  #_(testing "validate simulated categorical probabilities"
                    (let [variable-samples (utils/column-subset samples [variable])
                          actual-probabilities (spec/categorical-probabilities multi-mixture variable cluster)
                          possible-values (range 6)
                          probabilities (utils/probability-vector variable-samples possible-values)]
                      (is (almost-equal-vectors? probabilities actual-probabilities)))))))))))

(def point-cluster-mapping (invert-map cluster-point-mapping))
(defn- true-categorical-p
  [point-cluster-mapping point]
  (let [possible-clusters (get point-cluster-mapping point)]
  (into {} (map (fn [cluster] [(str cluster)
                               (if (contains? possible-clusters cluster)
                                 (/ 1 (count possible-clusters))
                                 0.)])
                (range 6)))))

;; Below, we're making use of the fact that each value of "a" determines a
;; cluster.
(deftest simulations-conditioned-on-points
  (doseq [[point-id clusters] (invert-map cluster-point-mapping)]
    (testing (str "Conditioned on point P" point-id)
      (let [point (stringify-keys (test-point point-id))
            samples (bq/simulate row-generator
                                 point
                                 simulation-count)]
        (testing "validate cluster assignments/categorical distribution"
          (let [samples-a (utils/column-subset samples ["a"])
                cluster-p-fraction (utils/probability-for-categories samples-a (map str (range 6)))
                true-p-category (true-categorical-p point-cluster-mapping point-id)]
            (is (almost-equal-maps? true-p-category cluster-p-fraction))))))))

(deftest logpdf-numerical-given-categorical
  (doseq [[cluster point-id] cluster-point-mapping]
    (let [point (stringify-keys
                  (select-keys (test-point point-id) (map keyword numerical-variables)))
          analytical-logpdf (transduce (map (fn [variable]
                                              (let [mu (spec/mu multi-mixture variable cluster)
                                                    sigma (spec/sigma multi-mixture variable cluster)]
                                                (dist/score-gaussian (get point variable) [mu sigma]))))
                                       +
                                       numerical-variables)
          queried-logpdf (bq/logpdf row-generator
                                    point
                                    {"a" (str cluster)})]

      (is (almost-equal-p? analytical-logpdf queried-logpdf)))))

;; I've added those because it for debugging purposes.
(deftest logpdf-categoricals-given-point-one-component-model
  (let [mmix-simple {:vars {"x" :gaussian
                            "a" :categorical
                            "b" :categorical}
                     :views [[{:probability 1.
                               :parameters {"x" {:mu 3 :sigma 1}
                                            "a" {"0" 1.0 "1" 0.0}
                                            "b" {"0" 0.95, "1" 0.05}}}]]}
        simple-row-gen (search/optimized-row-generator mmix-simple)]
    (is (= 0.95 (Math/exp (bq/logpdf simple-row-gen {"b" "0"} {"x" 3.}))))))
(logpdf-categoricals-given-point-one-component-model)

(deftest logpdf-categoricals-given-point-two-component-mix
  (let [mmix-simple {:vars {"x" :gaussian
                            "a" :categorical
                            "b" :categorical}
                     :views [[{:probability 0.95
                               :parameters {"x" {:mu 3 :sigma 1}
                                            "a" {"0" 1.0 "1" 0.0}
                                            "b" {"0" 1.0, "1" 0.0}}}
                              {:probability 0.05
                               :parameters {"x" {:mu 3 :sigma 1}
                                            "a" {"0" 1.0 "1" 0.0}
                                            "b" {"0" 0.0 "1" 1.0 }}}]]}
        simple-row-gen (search/optimized-row-generator mmix-simple)]
    (is (almost-equal-p? 0.95 (Math/exp (bq/logpdf simple-row-gen {"b" "0"} {"x" 3.}))))))

(def points-unique-cluster-mapping (select-keys
                                     point-cluster-mapping
                                     (for [[k v] point-cluster-mapping :when (= (count v) 1)] k)))

(def categories
  (keys
    (get (:parameters (first (first (multi-mixture :views)))) "a")))

(deftest logpdf-categoricals-given-points-that-identify-unique-cluster
  (doseq [[point-id cluster-set] points-unique-cluster-mapping]
    (doseq [category categories]
      (testing (str "Point " point-id " Observing b=" category)
        (let [point (stringify-keys (test-point point-id))
              cluster (first cluster-set)
              analytical-pdf (get
                               (get (:parameters (nth (first (:views multi-mixture )) cluster)) "b") category)
              queried-pdf   (Math/exp (bq/logpdf row-generator {"b" category} point))]
          (is (almost-equal-p? analytical-pdf queried-pdf)))))))

(use 'clojure.test)
(run-tests)
