(ns inferenceql.viz.observable.util
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [goog.labs.format.csv :as goog.csv]
            [medley.core :as medley]
            [ajax.core]
            [ajax.edn]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.csv :as csv]
            [inferenceql.viz.observable.score :as score]))

(defn clj-schema
  [js-schema]
  (medley/map-kv (fn [k v]
                   [(keyword k) (keyword v)])
                 (js->clj js-schema)))

(defn ^:export read-schema
  [schema-string]
  (clj->js (edn/read-string schema-string)))

(defn ^:export read-and-coerce-csv
  [csv-text schema]
  (let [csv-vecs (-> csv-text goog.csv/parse js->clj)
        schema (clj-schema schema)]
    (clj->js (csv/clean-csv-maps schema csv-vecs))))

(defn ^:export run-remote-query
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
  [query-fn schema rows num-samples impute-cols]
  (let [rows (vec (->clj rows))
        schema (medley/map-kv (fn [k v] [(keyword k) (keyword v)])
                              (->clj schema))
        query-fn #(->clj (query-fn %))
        impute-cols (map keyword (->clj impute-cols))]
    (clj->js (score/impute-missing-cells query-fn schema rows num-samples impute-cols))))

(defn ^:export impute-missing-cells-queries
  [rows schema impute-cols num-samples]
  (let [rows (vec (->clj rows))
        schema (medley/map-kv (fn [k v] [(keyword k) (keyword v)])
                              (->clj schema))
        impute-cols (map keyword (->clj impute-cols))]
    (clj->js (take 3 (score/impute-missing-cells-queries rows schema impute-cols num-samples)))))
