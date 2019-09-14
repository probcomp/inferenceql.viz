(ns inferdb.search-by-example.api
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.search :as search]
            [inferdb.multimixture.specification :as spec]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.model :as model]))

#?(:clj (def clj->js identity))
#?(:clj (def js->clj identity))

(def ^:export data (clj->js data/nyt-data))
(def ^:export columns (clj->js (spec/variables model/spec)))

(defn- valid-values
  "Returns the valid values for categorical column `column`."
  [spec column]
  (-> (spec/parameters spec column 0)
      keys
      set))

(defn- validation-errors
  "Given a example map `example`, returns a potentially empty vector of validation
  error maps."
  [spec example]
  (let [{valid-columns true, invalid-columns false} (group-by #(contains? (spec/variables spec) %) (keys example))
        key-errors (mapv (fn [column]
                           {:type :invalid-column
                            :column column
                            :valid-columns columns})
                         invalid-columns)
        value-errors (keep (fn [column]
                             (let [v (get example column)]
                               (if (spec/nominal? spec column)
                                 (when-not (contains? (valid-values spec column) v)
                                   {:type :invalid-value
                                    :column column
                                    :column-type :nominal
                                    :valid-values (valid-values spec column)
                                    :value v})
                                 (when-not (number? v)
                                   {:type :invalid-value
                                    :column column
                                    :column-type :numeric
                                    :value v}))))
                           valid-columns)]
    (into key-errors value-errors)))

(defn- error-message
  "Given a validation error map, returns a human-readable error message."
  [{:keys [type column] :as error}]
  (case type
    :invalid-column (str "Invalid column: " (pr-str column) "\n"
                         "Valid columns: " (pr-str (into [] columns)) "\n")
    :invalid-value (if (spec/nominal? model/spec column)
                     (str "Invalid value for nominal column " (pr-str column) ": " (pr-str (:value error)) "\n"
                          "Valid values: " (pr-str (into [] (:valid-values error))) "\n")
                     (str "Invalid value for numerical column " (pr-str column) ": " (pr-str (:value error)) "\n"
                          "Value must be numeric.\n"))))

(defn- js-error
  "Given a collection of validation error maps, returns a JavaScript error
  object."
  [errors]
  (let [message (->> errors
                     (map error-message)
                     (str/join "\n"))]
    #?(:clj (ex-info message {})
       :cljs (js/Error. message))))

(defn ^:export simulate
  "Simulates a row from the data table."
  []
  (let [generate-row (mmix/row-generator model/spec)]
    (clj->js (generate-row))))

(def ^:private n-models 1)
(def ^:private beta-params {:alpha 0.01 :beta 0.01})

(defn ^:export search
  "Returns a collection of [row index, similarity score] pairs, sorted by scores."
  [positive-indexes negative-indexes]
  (let [positive-indexes (js->clj positive-indexes)
        negative-indexes (js->clj negative-indexes)
        new-column-key "search"
        all-rows data/nyt-data
        known-indexes (set/union (set positive-indexes) (set negative-indexes))
        unknown-indexes (set/difference (set (range (count all-rows)))
                                        known-indexes)
        unknown-rows (mapv #(nth all-rows %) unknown-indexes)
        known-rows (mapv #(assoc (nth all-rows %)
                                 new-column-key
                                 (contains? positive-indexes %))
                         known-indexes)]
    (->> (search/search model/spec
                        new-column-key
                        known-rows
                        unknown-rows
                        n-models
                        beta-params)
         (zipmap unknown-indexes)
         (into [])
         (sort-by second >)
         (clj->js))))

#_(search/search model/spec
                 "test"
                 [(first data/nyt-data)]
                 (->> data/nyt-data (drop 1) (take 10))
                 1
                 {:alpha 0.01 :beta 0.01})

#_(search [0] [])
