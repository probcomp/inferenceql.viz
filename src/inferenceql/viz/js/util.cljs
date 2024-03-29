(ns inferenceql.viz.js.util
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [goog.labs.format.csv :as goog.csv]
            [ajax.core]
            [ajax.edn]
            [cljs-bean.core :refer [->clj]]
            [medley.core :as medley]
            [inferenceql.viz.csv :as csv]
            [inferenceql.viz.imputation :as imputation]))

(defn clj-schema
  "Takes a js-object representing an iql schema and returns it as a clojure map.
  This map will be properly keywordized."
  [js-schema]
  (medley/map-kv (fn [k v]
                   [(keyword k) (keyword v)])
                 (->clj js-schema)))

(defn ^:export read-schema
  "Reads and edn file with an iql schema. Returns it as a js object."
  [schema-string]
  (clj->js (edn/read-string schema-string)))

(defn ^:export read-and-coerce-csv
  "Takes raw csv text and returns a collection of maps for all the csv rows.
  Values are coerced based on data types in `schema`."
  [csv-text schema]
  (let [csv-vecs (-> csv-text goog.csv/parse js->clj)
        schema (clj-schema schema)]
    (clj->js (csv/clean-csv-maps schema csv-vecs))))

(defn ^:export run-remote-query
  "Runs `query` on a iql.query server."
  [query query-server-url]
  (js/Promise. (fn [resolve reject]
                 (ajax.core/ajax-request
                  {:method :post
                   :uri query-server-url
                   :params query
                   :timeout 0
                   :format (ajax.core/text-request-format)
                   :response-format (ajax.edn/edn-response-format)
                   :handler (fn [[ok result]]
                              (if ok
                                ;; Success case.
                                (resolve (clj->js result))
                                ;; Failure case.
                                (let [parse-error (get-in result [:response :instaparse/failure])
                                      error-msg (if (some? parse-error)
                                                  ;; Return just the parse error.
                                                  (with-out-str (print parse-error))
                                                  ;; Return the entire error-result as a string.
                                                  (with-out-str (pprint result)))]
                                  (reject (js/Error. error-msg)))))}))))

(defn ^:export impute-missing-cells
  "Returns imputed values and normalized scores for all missing values in `rows`"
  [query-fn rows schema impute-cols num-samples]
  (let [rows (vec (->clj rows))
        schema (clj-schema schema)
        query-fn #(->clj (query-fn %))
        impute-cols (map keyword (->clj impute-cols))]
    (clj->js (imputation/impute-missing-cells query-fn rows schema impute-cols num-samples))))

(defn ^:export imputation-queries
  "Returns a collection of queries that would be used to impute missing values in `rows`"
  [rows schema impute-cols num-samples]
  (let [rows (vec (->clj rows))
        schema (clj-schema schema)
        impute-cols (map keyword (->clj impute-cols))]
    (clj->js (imputation/imputation-queries rows schema impute-cols num-samples))))
