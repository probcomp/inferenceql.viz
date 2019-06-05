(ns inferdb.multimixture-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [metaprob.distributions :refer :all]
            [inferdb.cgpm.main :refer :all]
            [semantic-csv.core :as sc]
            [clojure.data.csv :as cd-csv]
            [inferdb.utils :refer :all]
            [inferdb.plotting.generate-vljson :refer :all]
            [inferdb.multimixture.dsl :refer :all]))

; XXX: why is this still here?
(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map clojure.core/name output-addrs)]
    (clojure.core/zipmap output-addrs trace-addrs)))

;; The following data generator has some interesting properties:
;; - clusters 0 and 1 in view 0 share the samme mu parameter.
;; - a is a determinstic indicator of the cluster.
;; - b is a noisy copy of a.
;; - in both views, clusters are equally weighted.
;; - in view 1, the third Gaussian components (cluster 0) "spans" the domain of
;; all the other components and share a center with cluster 1.
;;
;; I'd encourage everyone who works with the file to run the tests in this file
;; and then run make charts to see how the components relate.
(def generate-crosscat-row
  (multi-mixture
    (view
      {"x" gaussian
       "y" gaussian
       "z" categorical}
      (clusters
       0.166666666 {"x" [3 1]
                    "y" [4 0.1]
                    "z" [[0.98 0.01 0.01]]}
       0.166666666 {"x" [3 0.1]
                    "y" [4 1]
                    "z" [[0.98 0.01 0.01]]}
       0.166666667 {"x" [8 0.5]
                    "y" [10 1]
                    "z" [[0.01 0.98 0.01]]}
       0.166666666 {"x" [14 0.5]
                    "y" [7 0.5]
                    "z" [[0.01 0.01 0.98]]}
       0.166666666 {"x" [16 0.5]
                    "y" [9 0.5]
                    "z" [[0.01 0.01 0.98]]}
       0.166666666 {"x" [9  2.5]
                    "y" [16 0.1]
                    "z" [[0.01 0.98 0.01]]}))))

(def crosscat-cgpm
  (let [outputs-addrs-types {;; Variables in the table.
                             :x real-type
                             :y real-type
                             :z integer-type
                             ;; Exposed latent variables.
                             :cluster-for-x integer-type
                             :cluster-for-y integer-type
                             :cluster-for-z integer-type}
        output-addr-map (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types {}
        input-addr-map {}]
    (make-cgpm generate-crosscat-row
               outputs-addrs-types
               inputs-addrs-types
               output-addr-map
               input-addr-map)))


(defn to-csv [ samples file-name]
  (with-open [out-file (io/writer file-name)]
    (->> samples
         (sc/cast-with {(first (keys (first samples)))#(-> % float str)})
         sc/vectorize
         (cd-csv/write-csv out-file))))

; How many points do we want to create for our plot?
(def n 1000)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y :z]
                    {}
                    {}
                    n))
(to-csv samples "results/csv-files/data.csv")


(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:z 0}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-given-z-eq-0.csv")
(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:z 1}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-given-z-eq-1.csv")

(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:z 2}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-given-z-eq-2.csv")

(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x]
                    {:y 16}
                    {}
                    n))
(to-csv samples "results/csv-files/x-given-y-eq-16.csv")


(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x]
                    {:y 8}
                    {}
                    n))
(to-csv samples "results/csv-files/x-given-y-eq-8.csv")


(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:z]
                    {:x 3}
                    {}
                    n))
(to-csv samples "results/csv-files/z-given-x-eq-3.csv")

(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:z]
                    {:y 9}
                    {}
                    n))
(to-csv samples "results/csv-files/z-given-y-eq-9.csv")

(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 0}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-given-cluster-eq-0.csv")


(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 1}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-given-cluster-eq-1.csv")


(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:cluster-for-x]
                    {:x 8 :y 10}
                    {}
                    n))
(to-csv samples "results/csv-files/cluster-given-x-eq-8-y-eq-10.csv")

(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:cluster-for-x]
                    {:x 16}
                    {}
                    n))
(to-csv samples "results/csv-files/cluster-given-x-eq-16.csv")


(def n 100)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y :z]
                    {:cluster-for-x 0}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-given-cluster-eq-1.csv")



(def n 1000)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y :z]
                    {:cluster-for-x 0}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-z-given-cluster-eq-0.csv")


(def n 1000)
(def samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y :z]
                    {:cluster-for-x 1}
                    {}
                    n))
(to-csv samples "results/csv-files/x-y-z-given-cluster-eq-1.csv")
