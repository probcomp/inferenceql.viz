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

;; XXX this is not all that elegant: for plotting, I need all the test points in
;; that format.
(def test-points
  [{:x 3  :y 4  :test-point "P 1"}
   {:x 8  :y 10 :test-point "P 2"}
   {:x 14 :y 7  :test-point "P 3"}
   {:x 15 :y 8  :test-point "P 4"}
   {:x 16 :y 9  :test-point "P 5"}
   {:x 9  :y 16 :test-point "P 6"}])

(def variables (data/view-variables (first multi-mixture)))

(def nominal-variables
  (into #{}
        (filter #(data/nominal? multi-mixture %))
        variables))

(def categorical-variables
  (into #{}
        (filter #(data/categorical? multi-mixture %))
        variables))

(defn- test-point
  "Retrieves a given point given its ID. Note that point IDs are different from
  their indexes in `test-points`: Point IDs are 1-indexed."
  [point-id]
  (select-keys (nth test-points (dec point-id))
               variables))

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

(defn test-point-coordinates
  "A function to extract a relevant point from the array above."
  [name]
  (dissoc (first (filter #(= (:test-point %) name) test-points))
          :test-point))

;; How many points do we want to create for our plot?
(def plot-point-count 1000)

(deftest crosscatsimulate-simulate-joint
  "This tests saves plots for all simulated data in out/json results/"
  ;; Charts can be generated with make charts.
  (testing "(smoke) simulate n complete rows and save them as vl-json"
    (let [sample-count plot-point-count
          samples (cgpm/cgpm-simulate crosscat-cgpm
                                      [:x :y :z :a :b :c]
                                      {}
                                      {}
                                      sample-count)]
      (utils/save-json "simulations-x-y"
                       (plot/scatter-plot-json ["x" "y"]
                                               samples
                                               test-points
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

(deftest nominal-simulations
  (doseq [[cluster point-id] cluster-point-mapping]
    (testing (str "Simulations conditioned on P" point-id)
      (let [samples (cgpm/cgpm-simulate crosscat-cgpm
                                        variables
                                        {:cluster-for-x cluster}
                                        {}
                                        simulation-count)]
        (doseq [variable nominal-variables]
          (testing (str "of " variable)
            (let [samples (utils/col variable samples)]
              (testing "mean"
                (is (almost-equal? (get (test-point point-id) variable)
                                   (utils/average samples))))
              (testing "standard deviation"
                (let [actual-std (get-in multi-mixture [0
                                                        :clusters cluster
                                                        :args (name variable)
                                                        1])]
                  (is (utils/within-factor? actual-std
                                            (utils/std samples)
                                            2)))))))))))

(deftest nominal-logpdf-point
  (doseq [[cluster point-id] cluster-point-mapping]
    (let [point (select-keys (test-point point-id)
                             nominal-variables)
          analytical-logpdf (transduce (map (fn [variable]
                                              (let [mu (data/mu multi-mixture variable cluster)
                                                    sigma (data/sigma multi-mixture variable cluster)]
                                                (println mu sigma)
                                                (dist/score-gaussian mu [mu sigma]))))
                                       +
                                       nominal-variables)
          simulated-logpdf (cgpm/cgpm-logpdf crosscat-cgpm
                                             point
                                             {:cluster-for-x cluster}
                                             {})]
      (is (almost-equal-p? analytical-logpdf simulated-logpdf)))))

(deftest simulated-categorical-probabilities
  (doseq [cluster (keys cluster-point-mapping)]
    (testing (str "For categorical cluster " cluster "simulations")
      (let [all-samples (cgpm/cgpm-simulate crosscat-cgpm
                                            categorical-variables
                                            {:cluster-for-x cluster}
                                            {}
                                            simulation-count)]
        (doseq [variable categorical-variables]
          (testing (str "of categorical variable " variable)
            (let [samples (utils/column-subset all-samples [variable])
                  actual-probabilities (get-in multi-mixture [0
                                                              :clusters cluster
                                                              :args (name variable)
                                                              0])
                  possible-values (range 6)
                  probabilities (utils/probability-vector samples possible-values)]
              (is (almost-equal-vectors? probabilities actual-probabilities)))))))))

(defn max-index
  "Returns the index of the maximum value in the provided vector."
  [xs]
  (first (apply max-key second (map-indexed vector xs))))

(deftest logpdf-categoricals-conditioned-on-cluster
  (let [clusters (get-in multi-mixture [0 :clusters])]
    (testing "Across all clusters most likely categorical indexes are distinct"
      (is (distinct? (mapcat (fn [cluster]
                               (map (comp max-index first)
                                    (vals (select-keys (:args cluster) (map name categorical-variables)))))
                             clusters))))
    (doseq [[cluster {:keys [args]}] (map-indexed vector clusters)]
      (testing (str "Conditioned on cluster " cluster)
        (let [categorical-args (select-keys args (map name categorical-variables))]
          (testing "maximum indexes agree"
            (is (= 1 (->> (vals categorical-args)
                          (map first)
                          (map max-index)
                          (distinct)
                          (count)))))
          (testing "logpdf"
            (let [;; Constrain all categorical variables to their most likely values
                  ;; for the cluster.
                  constraints (reduce-kv (fn [m variable [probabilities]]
                                           (assoc m (keyword variable) (max-index probabilities)))
                                         {}
                                         categorical-args)
                  simulated-logpdf (cgpm/cgpm-logpdf crosscat-cgpm
                                                     constraints
                                                     {:cluster-for-x cluster}
                                                     {})
                  analytical-logpdf (Math/log (apply max (get-in multi-mixture [0 :clusters cluster :args "b" 0])))]
              (is (almost-equal-p? analytical-logpdf simulated-logpdf)))))))))

(deftest simulations-conditioned-on-points
  (doseq [[cluster point-id] cluster-point-mapping]
    (when-not (= 1 point-id) ; TODO: Handle this later
      (testing (str "Conditioned on point " point-id)
        (let [point (test-point point-id)
              samples (cgpm/cgpm-simulate crosscat-cgpm
                                          [:cluster-for-x :cluster-for-y]
                                          point
                                          {}
                                          simulation-count)]
          (testing "simulate clusters"
            (let [id-samples-x (utils/column-subset samples [:cluster-for-x])
                  id-samples-y (utils/column-subset samples [:cluster-for-y])
                  cluster-p-fraction (utils/probability-vector id-samples-x (range 6))
                  true-p-cluster (data/categorical-probabilities multi-mixture :a cluster)]
              (is (utils/equal-sample-values id-samples-x id-samples-y))
              (is (almost-equal-vectors? cluster-p-fraction true-p-cluster)))))))))

(deftest crosscat-logpdf-cluster-id-conditioned-on-points
  (doseq [[cluster point-id] cluster-point-mapping]
    (when-not (= 1 point-id)
      (testing (str "P" point-id)
        (let [point (test-point point-id)
              simulated-logpdf (cgpm/cgpm-logpdf crosscat-cgpm
                                                 {:cluster-for-x cluster}
                                                 point
                                                 {})]
          (is (almost-equal-p? 0 simulated-logpdf)))))))

(deftest crosscat-simulate-categoricals-conditioned-on-points
  (doseq [[cluster point-id] cluster-point-mapping]
    (when-not (= 1 point-id)
      (testing (str "When conditioned on point P" point-id)
        (let [point (test-point point-id)
              samples (cgpm/cgpm-simulate crosscat-cgpm
                                          [:a :b]
                                          point
                                          {}
                                          simulation-count)]
          (doseq [variable #{:a :b}]
            (testing (str "check probabilities for variable " variable " in cluster " cluster)
              (let [variable-samples (utils/column-subset samples [variable])
                    possible-values (range 6)
                    true-probabilities (data/categorical-probabilities multi-mixture variable cluster)
                    p-fraction (utils/probability-vector variable-samples possible-values)]
                (is (almost-equal-vectors? true-probabilities p-fraction))))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 2 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def p2 (test-point-coordinates "P 2"))

;; Testing invariants conditioning on the point ID = 2 which corresponds to
;; the component that of which p2 is a point center.

(deftest crosscat-logpdf-categoricals-conditioned-on-p2
  (testing "logPDF of categoricals conditioned on P2"
    (let [simulated-logpdf (cgpm/cgpm-logpdf
                            crosscat-cgpm
                            {:a 2  :b 2}
                            {:x (:x p2) :y (:y p2)}
                            {})
          analytical-logpdf (Math/log 0.95)]
      (is (almost-equal-p?  simulated-logpdf analytical-logpdf)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 3 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def p3 (test-point-coordinates "P 3"))

;; Testing invariants conditioning on the point ID = 3 which corresponds to the component
;; that of which p3 is a point center.

(deftest crosscat-logpdf-categoricals-conditioned-on-p3
  (testing "logPDF of categoricals conditioned on P3"
    (let [simulated-logpdf (cgpm/cgpm-logpdf
                            crosscat-cgpm
                            {:a 3  :b 3}
                            {:x (:x p3) :y (:y p3)}
                            {})
          analytical-logpdf (Math/log 0.95)]
      (is (almost-equal-p?  simulated-logpdf analytical-logpdf)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 4 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 5 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 6 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO
