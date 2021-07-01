(ns inferenceql.viz.observable.error)

(defn ^:export this-function-fails
  []
  (let [inner-fn (fn [] (throw
                         (js/Error. "This is an intentional failure.")))]
    (inner-fn)))
