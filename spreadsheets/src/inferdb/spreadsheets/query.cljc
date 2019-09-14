(ns inferdb.spreadsheets.query
  (:require [clojure.java.io :as io]
            [instaparse.core :as insta]))

#_(let [parser (insta/parser (io/resource "query.bnf"))]
    (parser "GENERATE"))
