(ns inferenceql.multimixture.gpm.crosscat
  (:require [inferenceql.utils :as utils]
            [inferenceql.multimixture.crosscat :as xcat]
            [inferenceql.multimixture.gpm.proto :as gpm-proto]))

(defrecord CrossCat
  [model latents]
  gpm-proto/GPM
  (logpdf [this targets constraints inputs]
    (xcat/logpdf-score model latents targets constraints))
  (simulate [this targets constraints n-samples inputs]
    (repeatedly n-samples #(xcat/simulate
                            model
                            latents
                            targets
                            constraints)))
  (mutual-information [this target-a target-b constraints n-samples]
    (let [joint-target    (vector target-a target-b)
          samples         (gpm-proto/simulate
                            this
                            (vector target-a target-b)
                            constraints
                            n-samples
                            {})
          logpdf-estimate (fn [target]
                            (utils/average (map-indexed (fn [i sample]
                                                          (gpm-proto/logpdf
                                                           this
                                                           (select-keys sample target)
                                                           constraints
                                                           {}))
                                                        samples)))
          logpdf-a  (logpdf-estimate target-a)
          logpdf-b  (logpdf-estimate target-b)
          logpdf-ab (logpdf-estimate joint-target)]
      (- logpdf-ab (+ logpdf-a logpdf-b)))))

(let [model   {:types {"foo" :bernoulli
                       "bar" :gaussian}
               :views [{:hypers     {"foo" {:p {:beta {:alpha 0.5 :beta 0.5}}}
                                     "bar" {:mu {:beta {:alpha 0.5 :beta 0.5}}
                                            :sigma {:gamma {:k 1 :theta 5}}}}
                        :categories [{:parameters {"foo" {:p 0.01}
                                                   "bar" {:mu 0 :sigma 0.1}}}
                                     {:parameters {"foo" {:p 0.99}
                                                   "bar" {:mu 5 :sigma 0.1}}}]}]}
      latents {:global {:alpha 1
                        :counts [2]
                        :z  {"foo" 0
                             "bar" 0}}
               :local  [{:alpha  1
                         :counts [5 5]
                         :y [0 1 0 1 0 1 0 1 0 1]}]}
      even-latents {:global {:alpha 1
                        :counts [3]
                        :z  {"x" 0
                             "y" 0
                             "a" 0}}
               :local  [{:alpha  1
                         :counts [5 5 5 5]
                         :y [0 1 0 1 0 1 0 1 0 1
                             2 3 2 3 2 3 2 3 2 3]}]}
      CCat   (->CrossCat model latents)
      targets {"foo" true}
      constraints {"bar" 5}
      target-a "foo"
      target-b "bar"
      constraintsI {}
      N        2
      ; constraints {}
      targets-logpdf ["foo"]
      ]
; (time (gpm-proto/mutual-information
;         (->CrossCat cross-cat latents)
;               "x"
;               "y"
;               {"a" 0}
;               1000)))
; (frequencies (gpm-proto/simulate CCat (vector target-a target-b) constraintsI 100 {})))
; (Math/exp (gpm-proto/logpdf CCat targets constraints  {})))
(gpm-proto/mutual-information CCat target-a target-b constraintsI 10))
