(ns inferenceql.datalog-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [datascript.core :as d]
            [inferenceql.datalog :as datalog]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.specification :as spec]))

(def mmix
  "Multimixture specification for use in tests."
  {:vars {"x" :gaussian
          "y" :categorical
          "a" :gaussian
          "b" :categorical}
   :views [[{:probability 1
             :parameters {"x" {:mu 2 :sigma 3}
                          "y" {"0" 0.1 "1" 0.2 "2" 0.3 "3" 0.4}}}]
           [{:probability 0.4
             :parameters {"a" {:mu 4 :sigma 5}
                          "b" {"0" 0.9, "1" 0.01, "2" 0.02, "3" 0.03, "4" 0.04}}}
            {:probability 0.6
             :parameters {"a" {:mu 6 :sigma 7}
                          "b" {"0" 0.99, "1" 0.001, "2" 0.002, "3" 0.003, "4" 0.004}}}]]})

(def column-idents
  "Mapping from variable names to their :db/idents."
  {"x" :test/x
   "y" :test/y
   "a" :test/a
   "b" :test/b})

(def column-types
  "Mapping from variable names to their :db/valueTypes."
  {"x" :db.type/double
   "y" :db.type/string
   "a" :db.type/double
   "b" :db.type/string})

(deftest transaction-smoke
  (is (some? (doto (d/create-conn datalog/schema)
               (d/transact! (datalog/model-facts mmix mmix/row-generator))))))

(def rows
  [{"x" 1.5, "y" "3", "a" 3.5, "b" "0"}
   {"x" 2.5, "y" "2", "a" 4.5, "b" "4"}])

(def variable-column-map
  {"x" "x"
   "y" "y"
   "a" "a"
   "b" "b"})

(def db
  (let [model-facts       (datalog/model-facts mmix mmix/row-generator)
        table-facts       (datalog/table-facts column-idents column-types rows)
        model-table-facts (map #(apply datalog/model-table-fact %) variable-column-map)]
    (-> (doto (d/create-conn datalog/schema)
          (d/transact! model-facts)
          (d/transact! table-facts)
          (d/transact! model-table-facts))
        (d/db))))

(deftest rules-smoke
  (is (= 1 (d/q '[:find (count ?model) .
                  :in $ %
                  :where
                  (model? ?model)]
                db
                datalog/rules))
      "Model was transacted")
  (is (= 1 (d/q '[:find (count ?gfn) .
                  :in $ %
                  :where
                  (gfn? ?gfn)]
                db
                datalog/rules))
      "Generative function was transacted")
  (is (= (count (:vars mmix))
         (d/q '[:find (count ?variable) .
                :in $ %
                :where
                (variable? ?variable)]
              db
              datalog/rules))))

(deftest model-metadata
  (testing "statistical type of variable"
    (doseq [variable (spec/variables mmix)]
      (testing variable
        (is (= (spec/stattype mmix variable)
               (d/q '[:find ?s .
                      :in $ % ?v
                      :where
                      (variable-stattype ?v ?s)]
                    db
                    datalog/rules
                    variable))))))

  (testing "categories for variable"
    (doseq [variable (filter #(spec/nominal? mmix %)
                             (spec/variables mmix))]
      (testing variable
        (is (= (spec/categories mmix variable)
               (set (d/q '[:find [?c ...]
                           :in $ % ?v
                           :where
                           (variable-categories ?v ?c)]
                         db
                         datalog/rules
                         variable))))))))

(deftest table-data
  (is (= (set rows)
         (set (d/q '[:find [?row ...]
                     :in $ %
                     :where
                     (row ?e)
                     [(inferenceql.datalog/pull-row $ ?e) ?row]]
                   db
                   datalog/rules)))))
