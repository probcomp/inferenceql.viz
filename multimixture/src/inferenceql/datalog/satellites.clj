(ns inferenceql.datalog.satellites
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [clojure-csv.core :as csv]
            [datahike.api :as d]
            [jsonista.core :as json]
            [semantic-csv.core :as scsv]
            [inferenceql.datalog.model :as model]
            [inferenceql.datalog.table :as table]
            [inferenceql.datalog.util :as util :refer [map-keys map-vals filter-vals remove-vals]]
            [inferenceql.multimixture.specification :as spec]))

(def types
  "Map from Datahike attribute :db/ident to Datahike attribute :db/type."
  #:satellite{:anticipated-lifetime :db.type/double
              :apogee               :db.type/double
              :orbit-class          :db.type/string
              :contractor           :db.type/string
              :contractor-country   :db.type/string
              :operator-country     :db.type/string
              :launch-date          :db.type/double
              :mass                 :db.type/double
              :eccentricity         :db.type/double
              :inclination          :db.type/double
              :launch-mass          :db.type/double
              :launch-site          :db.type/string
              :launch-vehicle       :db.type/string
              :name                 :db.type/string
              :owner                :db.type/string
              :perigee              :db.type/double
              :period               :db.type/double
              :power                :db.type/double
              :purpose              :db.type/string
              :source               :db.type/string
              :orbit-type           :db.type/string
              :users                :db.type/string
              :longitude            :db.type/double})

(def parsers
  #:db.type{:double util/parse-double})

(defn row-facts
  "`types` is a map from Datahike attribute :db/ident to Datahike
  attribute :db/type. `column-idents` is a map from column names as they appear
  in the model JSON "
  [csv-path types column-idents] ; "/Users/zane/Desktop/satellites.csv"
  (with-open [in-file (io/reader csv-path)]
    (let [casts (map-vals types #(get parsers % identity))]
      (->> (csv/parse-csv in-file)
           (scsv/mappify {:keyify false})
           (map #(filter-vals % (partial not= "NaN")))
           (map #(filter-vals % (partial not= "null")))
           (map #(map-keys % column-idents))
           (map #(remove-vals % nil?))
           (scsv/cast-with casts)
           (map #(remove-vals % nil?))
           (map table/row-fact)
           (take 1)
           (into [])))))

(defn model-specs
  "When called on a directory path reads in all the files in the directory at that
  path and returns a vector of the model JSON from those files."
  [path]
  (->> (file-seq (io/file path))
       (filter (memfn isFile))
       (map #(-> % json/read-value spec/from-json))))

#_
(defn db
  [path column-idents ident-types]
  (let [uri "datahike:mem://table"
        model-facts (->> (model-jsons path column-idents)
                         (mapcat model/model-facts))
        column-facts (column-facts ident-types)]
    (d/create-database uri :schema-on-read true)
    (let [conn (d/connect uri)]
      (d/db (doto conn
              (d/transact model/schema)
              (d/transact model-facts)

              (d/transact table/schema)
              (d/transact column-facts)
              (d/transact category-facts)
              (d/transact row-facts))))))
