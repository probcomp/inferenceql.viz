(ns inferenceql.datalog.sandbox
  (:require [clojure.spec.alpha :as s]))

;; https://docs.datomic.com/on-prem/query.html

(defn starts-with
  [c]
  (fn starts-with? [s]
    (= c (first (name s)))))

(s/def ::vector (s/and vector? (s/conformer vec vec)))

(s/def ::variable
  (s/and symbol? (starts-with \?)))

(s/def ::constant
  (every-pred (complement coll?)
              (complement symbol?)))

(s/valid? ::variable '?e)
(s/valid? ::variable 'e)

(s/def ::src-var (s/and symbol? (starts-with \$)))

(s/valid? ::src-var '$)
(s/valid? ::src-var '$cat)

(s/def ::rules-var #{'%})

(s/def ::input
  (s/or :src-var ::src-var
        :rules-var ::rules-var))

(s/def ::data-pattern
  (s/and ::vector
         (s/spec (s/cat :src-var (s/? ::src-var)
                        :rest (s/+ (s/or :variable ::variable
                                         :constant ::constant
                                         :ignore #{'_}))))))

(s/def ::query
  (s/and ::vector
         (s/cat :find-spec (s/cat :find #{:find}
                                  :find-rel (s/+ ::variable))
                :inputs (s/? (s/cat :in #{:in}
                                    :inputs (s/+ ::input)))
                :where-clause (s/cat :where #{:where}
                                     :clauses ::data-pattern))))

(comment

  (s/unform
   ::query
   (s/conform ::query '[:find ?e
                        :where
                        [$ _ _ _]]))

  )
