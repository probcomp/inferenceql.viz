(ns inferdb.spreadsheets.util)

(defn filter-nil-kvs [a-map]
  (into {} (remove (comp nil? val) a-map)))
