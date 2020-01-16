(ns inferenceql.datalog.test)

(defmacro with-empty-conn
  [sym & body]
  `(let [uri# (str "datahike:mem://" (gensym))]
     (d/create-database uri# :schema-on-read true)
     (let [~sym (d/connect uri#)
           result# (do ~@body)]
       (d/release ~sym)
       result#)))
