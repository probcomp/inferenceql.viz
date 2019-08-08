(ns inferdb.spreadsheets.build-model
  (:require [clojure.data.json :as json]))

(defn vars-in-first-cluster [model]
  (-> model
      (get "views")
      first
      first
      keys))

(defn cluster-data [model]
  (let [wrap-map-in-vec #(if (map? %) [%] %)]
    (reduce (fn [acc c]
              (conj acc
                    (get c "p")
                    (reduce-kv (fn [a k v]
                                 (assoc a k (wrap-map-in-vec v)))
                               {}
                               (dissoc c "p"))))
            []
            (-> model
                (get "views")
                first))))

(defn stat-types [model]
  (let [stat-type-map {"gaussian"     'dist/gaussian
                       "categorical"  'dist/categorical}]
    (reduce-kv (fn [a k v]
                 (assoc a k (stat-type-map v)))
               {}
               (get model "columns"))))

(defn table-vars [model]
  (let [categories (get model "categories")]
    (reduce-kv (fn [a k v]
                 (case v
                   "gaussian"
                   (assoc a (keyword k) 'cgpm/real-type)

                   "categorical"
                   (assoc a (keyword k)
                          `(cgpm/make-nominal-type
                            ~(->> k
                                  (get categories)
                                  vals
                                  set)))))
               {}
               (select-keys
                (get model "columns")
                (vars-in-first-cluster model)))))

(defn latent-vars [model]
  (reduce-kv (fn [a k v]
               (assoc a (keyword (str "cluster-for-" k))
                      'cgpm/integer-type))
             {}
             (select-keys
              (get model "columns")
              (vars-in-first-cluster model))))

(defn make-model [model]
  ['(ns inferdb.spreadsheets.model
      (:require [inferdb.multimixture.dsl :refer [multi-mixture view clusters]]
                [inferdb.cgpm.main :as cgpm]
                [metaprob.distributions :as dist]))

   `(def ~'cluster-data ~(cluster-data model))

   `(def ~'stattypes ~(stat-types model))

   '(def generate-census-row (multi-mixture (view stattypes
                                                  (apply clusters cluster-data))))

   '(defn make-identity-output-addr-map
      [output-addrs-types]
      (let [output-addrs (keys output-addrs-types)
            trace-addrs  (map clojure.core/name output-addrs)]
        (clojure.core/zipmap output-addrs trace-addrs)))

   `(def ~'model-cgpm
      (let [~'table-variables ~(table-vars model)
            ~'latent-variables ~(latent-vars model)
            ~'outputs-addrs-types (into ~'table-variables ~'latent-variables)
            ~'output-addr-map     (~'make-identity-output-addr-map ~'outputs-addrs-types)
            ~'inputs-addrs-types  {}
            ~'input-addr-map      {}]
        (~'cgpm/make-cgpm
         ~'generate-census-row
         ~'outputs-addrs-types
         ~'inputs-addrs-types
         ~'output-addr-map
         ~'input-addr-map)))])

(defn write-model [model fn]
  (with-open [w (clojure.java.io/writer fn)]
    (doseq [form (->> model make-model (interpose nil))]
      (if form
        (clojure.pprint/pprint form w)
        (.write w "\n")))))
