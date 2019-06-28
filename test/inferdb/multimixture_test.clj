(ns inferdb.multimixture-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [inferdb.cgpm.main :as cgpm]
            [inferdb.utils :as utils]
            [inferdb.multimixture.data :as data]
            [inferdb.plotting.generate-vljson :as plot]
            [metaprob.distributions :as dist]))

;; XXX: why is this still here?
(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map name output-addrs)]
    (zipmap output-addrs trace-addrs)))

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
  [{:vars {"x" dist/gaussian
           "y" dist/gaussian
           "a" dist/categorical
           "b" dist/categorical}
    :clusters [{:probability 0.166666666
                :args {"x" [3 1]
                       "y" [4 0.1]
                       "a" [[1 0 0 0 0 0]]
                       "b" [[0.95 0.01 0.01 0.01 0.01 0.01]]}}
               {:probability 0.166666666
                :args {"x" [3 0.1]
                       "y" [4 1]
                       "a" [[0 1 0 0 0 0]]
                       "b" [[0.01 0.95 0.01 0.01 0.01 0.01]]}}
               {:probability 0.166666667
                :args {"x" [8 0.5]
                       "y" [10 1]
                       "a" [[0 0 1 0 0 0]]
                       "b" [[0.01 0.01 0.95 0.01 0.01 0.01]]}}
               {:probability 0.166666666
                :args {"x" [14 0.5]
                       "y" [7 0.5]
                       "a" [[0 0 0 1 0 0]]
                       "b" [[0.01 0.01 0.01 0.95 0.01 0.01]]}}
               {:probability 0.166666666
                :args {"x" [16 0.5]
                       "y" [9 0.5]
                       "a" [[0 0 0 0 1 0]]
                       "b" [[0.01 0.01 0.01 0.01 0.95 0.01]]}}
               {:probability 0.166666666
                :args {"x" [9  2.5]
                       "y" [16 0.1]
                       "a" [[0 0 0 0 0 1]]
                       "b" [[0.01 0.01 0.01 0.01 0.01 0.95]]}}]}
   {:vars {"z" dist/gaussian
           "c" dist/categorical}
    :clusters [{:probability 0.25
                :args {"z" [0 1]
                       "c" [[1 0 0 0]]}}
               {:probability 0.25
                :args {"z" [15 1]
                       "c" [[0 1 0 0]]}}
               {:probability 0.25
                :args {"z" [30 1]
                       "c" [[0 0 1 0]]}}
               {:probability 0.25
                :args {"z" [15 8]
                       "c" [[0 0 0 1]]}}]}])

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

(def crosscat-cgpm
  (let [generate-crosscat-row (data/crosscat-row-generator multi-mixture)
        outputs-addrs-types {;; Variables in the table.
                             :x cgpm/real-type
                             :y cgpm/real-type
                             :z cgpm/real-type
                             :a cgpm/integer-type
                             :b cgpm/integer-type
                             :c cgpm/integer-type
                             ;; Exposed latent variables.
                             :cluster-for-x cgpm/integer-type
                             :cluster-for-y cgpm/integer-type
                             :cluster-for-z cgpm/integer-type
                             :cluster-for-a cgpm/integer-type
                             :cluster-for-b cgpm/integer-type
                             :cluster-for-c cgpm/integer-type}
        output-addr-map (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types {}
        input-addr-map {}]
    (cgpm/make-cgpm generate-crosscat-row
                    outputs-addrs-types
                    inputs-addrs-types
                    output-addr-map
                    input-addr-map)))

(defn test-point
  "Retrieves a given point given its ID. Note that point IDs are different from
  their indexes in `test-points`: Point IDs are 1-indexed."
  [point-id]
  (nth test-points (dec point-id)))

(def variables (data/view-variables (first multi-mixture)))

(def nominal-variables
  (into #{}
        (filter #(data/numerical? multi-mixture %))
        variables))

(def categorical-variables
  (into #{}
        (filter #(data/nominal? multi-mixture %))
        variables))

(deftest test-cluster-point-mapping
  ;; This test verifies that we've constructed our multi-mixture correctly such
  ;; that the subsequent tests can succeed. Clusters that have a point at their
  ;; center should have that point's variables as the mu for each gaussian that
  ;; makes up the cluster gaussian that makes up the cluster.
  (doseq [[cluster point-id] cluster-point-mapping]
    (doseq [variable #{:x :y}]
      (let [point-value (get (test-point point-id) variable)
            mu (data/mu multi-mixture variable cluster)]
        (is (= point-value mu))))))

(def plot-point-count 1000)

(deftest crosscat-simulate-joint
  ;; This test saves plots for all simulated data. See `README.md` for more
  ;; information on chart generation.
  (testing "(smoke) simulate n complete rows and save them as vl-json"
    (let [data (map-indexed (fn [index point]
                              (assoc point :test-point (inc index)))
                            test-points)
          sample-count plot-point-count
          samples (cgpm/cgpm-simulate crosscat-cgpm
                                      [:x :y :z :a :b :c]
                                      {}
                                      {}
                                      sample-count)]
      (utils/save-json "simulations-x-y"
                       (plot/scatter-plot-json ["x" "y"]
                                               samples
                                               data
                                               [0 18]
                                               "View 1: X, Y, A, B"))
      (utils/save-json "simulations-z"
                       (plot/hist-plot
                        (utils/column-subset samples [:z :c])
                        [:z :c]
                        "Dim Z and C"))
      (utils/save-json "simulations-a"
                       (plot/bar-plot (utils/column-subset samples [:a]) "Dim A" plot-point-count))
      (utils/save-json "simulations-b"
                       (plot/bar-plot (utils/column-subset samples [:b]) "Dim B" plot-point-count))
      (utils/save-json "simulations-c"
                       (plot/bar-plot (utils/column-subset samples [:c]) "Dim C" plot-point-count))
      (is (= (count samples) plot-point-count)))))

(def simulation-count 100)
(def threshold 0.5)

(defn- almost-equal? [a b] (utils/almost-equal? a b utils/relerr threshold))
(defn- almost-equal-vectors? [a b] (utils/almost-equal-vectors? a b utils/relerr threshold))
(defn- almost-equal-p? [a b] (utils/almost-equal? a b utils/relerr 0.01))

(deftest mean-stdev-conditioned-on-cluster
  (doseq [[cluster point-id] cluster-point-mapping]
    (testing (str "Simulations conditioned on cluster " cluster)
      (let [samples (cgpm/cgpm-simulate crosscat-cgpm
                                        variables
                                        {:cluster-for-x cluster}
                                        {}
                                        simulation-count)]
        (doseq [variable nominal-variables]
          (testing (str "of variable " variable)
            (let [samples (utils/col variable samples)]
              (testing "mean"
                (is (almost-equal? (get (test-point point-id) variable)
                                   (utils/average samples))))
              (testing "standard deviation"
                (let [analytical-std (data/sigma multi-mixture variable cluster)]
                  (is (utils/within-factor? analytical-std
                                            (utils/std samples)
                                            2)))))))))))

(deftest nominal-logpdf-point
  (doseq [[cluster point-id] cluster-point-mapping]
    (let [point (select-keys (test-point point-id)
                             nominal-variables)
          analytical-logpdf (transduce (map (fn [variable]
                                              (let [mu (data/mu multi-mixture variable cluster)
                                                    sigma (data/sigma multi-mixture variable cluster)]
                                                (dist/score-gaussian mu [mu sigma]))))
                                       +
                                       nominal-variables)
          queried-logpdf (cgpm/cgpm-logpdf crosscat-cgpm
                                           point
                                           {:cluster-for-x cluster}
                                           {})]
      (is (almost-equal-p? analytical-logpdf queried-logpdf)))))

(deftest simulated-categorical-probabilities
  (doseq [cluster (keys cluster-point-mapping)]
    (testing (str "Conditioned on cluster " cluster)
      (let [all-samples (cgpm/cgpm-simulate crosscat-cgpm
                                            categorical-variables
                                            {:cluster-for-x cluster}
                                            {}
                                            simulation-count)]
        (doseq [variable categorical-variables]
          (testing (str "simulations of categorical variable " variable)
            (let [samples (utils/column-subset all-samples [variable])
                  actual-probabilities (get-in multi-mixture [0
                                                              :clusters cluster
                                                              :args (name variable)
                                                              0])
                  possible-values (range 6)
                  probabilities (utils/probability-vector samples possible-values)]
              (is (almost-equal-vectors? probabilities actual-probabilities)))))))))

(deftest logpdf-categoricals-conditioned-on-cluster
  (let [clusters (get-in multi-mixture [0 :clusters])]
    (testing "Across all clusters most likely categorical indexes are distinct"
      (is (distinct? (mapcat (fn [cluster]
                               (map (comp utils/max-index first)
                                    (vals (select-keys (:args cluster) (map name categorical-variables)))))
                             clusters))))
    (doseq [[cluster {:keys [args]}] (map-indexed vector clusters)]
      (testing (str "Conditioned on cluster " cluster)
        (let [categorical-args (select-keys args (map name categorical-variables))]
          (testing "maximum indexes agree"
            (is (= 1 (->> (vals categorical-args)
                          (map first)
                          (map utils/max-index)
                          (distinct)
                          (count)))))
          (testing "logpdf"
            (let [;; Constrain all categorical variables to their most likely values
                  ;; for the cluster.
                  target (reduce-kv (fn [m variable [probabilities]]
                                      (assoc m (keyword variable) (utils/max-index probabilities)))
                                    {}
                                    categorical-args)
                  queried-logpdf (cgpm/cgpm-logpdf crosscat-cgpm
                                                   target
                                                   {:cluster-for-x cluster}
                                                   {})
                  analytical-logpdf (Math/log (apply max (data/categorical-probabilities multi-mixture :b cluster)))]
              (is (almost-equal-p? analytical-logpdf queried-logpdf)))))))))

(deftest simulations-conditioned-on-points
  (doseq [[cluster point-id] cluster-point-mapping]
    (when-not (= 1 point-id) ; TODO: Handle this later
      (testing (str "Conditioned on point P" point-id)
        (let [point (test-point point-id)
              samples (cgpm/cgpm-simulate crosscat-cgpm
                                          (into categorical-variables
                                                [:cluster-for-x :cluster-for-y])
                                          point
                                          {}
                                          simulation-count)]
          (testing "validate cluster assignments of samples"
            (let [id-samples-x (utils/column-subset samples [:cluster-for-x])
                  id-samples-y (utils/column-subset samples [:cluster-for-y])
                  cluster-p-fraction (utils/probability-vector id-samples-x (range 6))
                  true-p-cluster (data/categorical-probabilities multi-mixture :a cluster)]
              (is (utils/equal-sample-values id-samples-x id-samples-y))
              (is (almost-equal-vectors? cluster-p-fraction true-p-cluster))))
          (testing "validate distribution of categorical"
            (doseq [variable categorical-variables]
              (testing (str "variable " variable " in cluster " cluster)
                (let [variable-samples (utils/column-subset samples [variable])
                      possible-values (range 6)
                      true-probabilities (data/categorical-probabilities multi-mixture variable cluster)
                      p-fraction (utils/probability-vector variable-samples possible-values)]
                  (is (almost-equal-vectors? true-probabilities p-fraction)))))))))))

(deftest crosscat-logpdf-cluster-id-conditioned-on-points
  (doseq [[cluster point-id] cluster-point-mapping]
    (when-not (= 1 point-id)
      (testing (str "Validate queried logPDF matches analytical logPDF for point P" point-id
                    " given cluster assignment " cluster)
        (let [point (test-point point-id)
              queried-logpdf (cgpm/cgpm-logpdf crosscat-cgpm
                                               {:cluster-for-x cluster}
                                               point
                                               {})]
          (is (almost-equal-p? 0 queried-logpdf)))))))

(deftest crosscat-logpdf-categoricals-conditioned-on-points
  (doseq [[cluster point-id] cluster-point-mapping]
    (when-not (= 1 point-id)
      (testing (str "Validate queried logPDF matches analytical logpdf for point P" point-id
                    " given cluster assignment " cluster)
        (let [point (test-point point-id)
              ;; Returns the most likely categorical for a given categorical
              ;; variable. For example, for a categorical variable with
              ;; probabilities [0.01 0.97 0.01 0.01] it will return 1.
              most-likely-category (fn [variable]
                                     (utils/max-index
                                      (data/categorical-probabilities multi-mixture
                                                                      variable
                                                                      cluster)))
              highest-probability (apply min (map #(apply max (data/categorical-probabilities multi-mixture % cluster))
                                                  categorical-variables))
              ;; The target here takes advantage of the structure of the
              ;; multimixture. In particular, this test assumes that all the
              ;; categorical variables in a given cluster will have the same
              ;; index for the most likely category.
              target (zipmap categorical-variables
                             (map most-likely-category categorical-variables))
              analytical-logpdf (Math/log highest-probability)
              queried-logpdf (cgpm/cgpm-logpdf crosscat-cgpm
                                       target
                                       point
                                       {})]
          (is (almost-equal-p? analytical-logpdf queried-logpdf)))))))
