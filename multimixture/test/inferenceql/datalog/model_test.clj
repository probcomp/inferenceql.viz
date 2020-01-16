(ns inferenceql.datalog.model-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [datahike.api :as d]
            [inferenceql.datalog.model :as model]
            [inferenceql.datalog.test :refer [with-empty-conn]]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.specification :as spec]))

(def mmix
  {:vars {:x :iql.stattype/gaussian
          :y :iql.stattype/categorical
          :a :iql.stattype/gaussian
          :b :iql.stattype/categorical}
   :views [[{:probability 1
             :parameters {:x {:mu 2 :sigma 3}
                          :y {"0" 0.1 "1" 0.2 "2" 0.3 "3" 0.4}}}]
           [{:probability 0.4
             :parameters {:a {:mu 4 :sigma 5}
                          :b {"0" 0.9, "1" 0.01, "2" 0.02, "3" 0.03, "4" 0.04}}}
            {:probability 0.6
             :parameters {:a {:mu 6 :sigma 7}
                          :b {"0" 0.99, "1" 0.001, "2" 0.002, "3" 0.003, "4" 0.004}}}]]})

(deftest transaction-smoke
  (with-empty-conn conn
    (is (some? (d/transact conn model/schema))
        "Model schema can be transacted")
    (is (some? (d/transact conn (model/model-facts mmix mmix/row-generator)))
        "Model facts can be transacted")))

(deftest rules-smoke
  (with-empty-conn conn
    (d/transact conn model/schema)
    (d/transact conn (model/model-facts mmix mmix/row-generator))
    (is (= 1 (d/q '[:find (count ?model) .
                    :in $ %
                    :where
                    (model? ?model)]
                  (d/db conn)
                  model/rules))
        "Model was transacted")
    (is (= 1 (d/q '[:find (count ?gfn) .
                    :in $ %
                    :where
                    (gfn? ?gfn)]
                  (d/db conn)
                  model/rules))
        "Generative function was transacted")))

(deftest model-metadata
  (with-empty-conn conn
    (d/transact conn model/schema)
    (d/transact conn (model/model-facts mmix mmix/row-generator))

    (testing "statistical type of variable "
      (doseq [variable (spec/variables mmix)]
        (testing variable
          (is (= (spec/stattype mmix variable)
                 (d/q '[:find ?s .
                        :in $ % ?ident
                        :where
                        [?v :db/ident ?ident]
                        (variable-stattype ?v ?s)]
                      (d/db conn)
                      model/rules
                      variable))))))

    (testing "categories for variable "
      (doseq [variable (filter #(spec/nominal? mmix %)
                               (spec/variables mmix))]
        (testing variable
          (is (= (spec/categories mmix variable)
                 (set (d/q '[:find [?c ...]
                             :in $ % ?ident
                             :where
                             [?v :db/ident ?ident]
                             (variable-categories ?v ?c)]
                           (d/db conn)
                           model/rules
                           variable)))))))))
