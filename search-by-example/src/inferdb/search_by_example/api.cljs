(ns inferdb.search-by-example.api
  (:require [clojure.string :as str]
            [metaprob.distributions :as dist]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.search :as search]))

(def ^:export data (clj->js data/nyt-data))

(def ^:export columns (clj->js (keys model/stattypes)))

(defn- categorical?
  "Returns `true` if `column` is categorical, `false` otherwise."
  [column]
  (= dist/categorical (get model/stattypes column)))

(defn- valid-values
  "Returns the valid values for categorical column `column`."
  [column]
  (let [valid-value-map (->> model/cluster-data
                             (partition 2)
                             (map second)
                             (reduce merge)
                             (reduce-kv (fn [m k v]
                                          (if (categorical? k) ; only include categorical variables
                                            (assoc m k (-> v first keys set)) ; pull out the valid values
                                            m))
                                        {}))]
    (get valid-value-map column)))

(defn validate
  "Given a example map `example`, returns a potentially empty vector of validation
  error maps."
  [example]
  (let [{valid-keys true, invalid-keys false} (group-by #(contains? columns %) (keys example))
        key-errors (mapv (fn [column]
                           {:type :invalid-column
                            :column column
                            :valid-columns columns})
                         invalid-keys)
        value-errors (keep (fn [column]
                             (let [v (get example column)]
                               (if (categorical? column)
                                 (when-not (contains? (valid-values column) v)
                                   {:type :invalid-value
                                    :column column
                                    :column-type :nominal
                                    :valid-values (valid-values column)
                                    :value v})
                                 (when-not (number? v)
                                   {:type :invalid-value
                                    :column column
                                    :column-type :numeric
                                    :value v}))))
                           valid-keys)]
    (into key-errors value-errors)))

(defn- error-message
  "Given a validation error map, returns a human-readable error message."
  [{:keys [type column] :as error}]
  (case type
    :invalid-column (str "Invalid column: " (pr-str column) "\n"
                         "Valid columns: " (pr-str (into [] columns)) "\n")
    :invalid-value (if (categorical? column)
                     (str "Invalid value for nominal column " (pr-str column) ": " (pr-str (:value error)) "\n"
                          "Valid values: " (pr-str (into [] (:valid-values error))) "\n")
                     (str "Invalid value for numerical column " (pr-str column) ": " (pr-str (:value error)) "\n"
                          "Value must be numeric.\n"))))

(defn- validation-error
  "Given a collection of validation error maps, returns a JavaScript error
  object."
  [errors]
  (let [message (->> errors
                     (map error-message)
                     (str/join "\n"))]
    (js/Error. message)))

(defn ^:export isVeryAnomalous
  "Returns `true` if the provided example is very anomalous."
  [example]
  (if-let [errors (seq (validate example))]
    (throw (validation-error errors))
    (search/very-anomalous? (reduce-kv (fn [m k v]
                                         (assoc m (keyword k) v))
                                       {}
                                       (js->clj example)))))

(defn ^:export search
  "Returns a collection of [row index, similarity score] pairs, sorted by scores."
  [example]
  (if (isVeryAnomalous example)
    (throw (js/Error. "Example is too anomalous!"))
    (clj->js (search/search-by-example (js->clj example)))))
