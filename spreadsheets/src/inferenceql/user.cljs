(ns inferenceql.user
  "Functions to be used within user-defined, Javascript, column override functions.")

(defn ^:export gaussianNoise
  "Generates Gaussian noise via a Box-Muller transform.
  `mu` {number} Mean of normal distribution.
  `sigma` {number} Varance of normal distribution."
  [mu sigma]
  (let [u1 (rand)
        u2 (rand)
        mult-1 (Math/sqrt (* -2 (Math/log u1)))
        mult-2 (Math/cos (* 2 Math/PI u2))]
    (+ mu (* sigma mult-1 mult-2))))
