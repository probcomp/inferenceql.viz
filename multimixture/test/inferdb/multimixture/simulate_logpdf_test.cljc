(ns inferdb.multimixture.simulate-logpdf-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as test :refer [deftest testing is]]
            [expound.alpha :as expound]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [inferdb.utils :as utils]
            [inferdb.plotting.generate-vljson :as plot]
            [inferdb.multimixture.specification :as spec]
            [inferdb.multimixture.search :as search] ;; XXX: why on earth is the "optimized" row generator in search???
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
    :views [[  {:probability 0.166666666
                :parameters {"x" {:mu 3 :sigma 1}
                             "y" {:mu 4 :sigma 0.1}
                             "a" {"0" 1.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.95, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 3 :sigma 0.1}
                             "y" {:mu 4 :sigma 1}
                             "a" {"0" 0.0 "1" 1.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.95, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 8  :sigma 0.5}
                             "y" {:mu 10 :sigma 1}
                             "a" {"0" 0.0 "1" 0.0 "2" 1.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.95, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 14  :sigma 0.5}
                             "y" {:mu  7  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 1.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.95, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 16  :sigma 0.5}
                             "y" {:mu  9  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 1.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.95, "5" 0.01}}}
               {:probability 0.166666666
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

(spec/view-variables (first (:views multi-mixture)))


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


(def variables (keys (:vars multi-mixture)))

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

(deftest test-smoke-row-generator
 (is (map? (row-generator))))

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

(use 'clojure.test)
(run-tests)
