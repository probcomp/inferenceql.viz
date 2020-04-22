(ns inferenceql.multimixture.gpm.crosscat
  (:require [inferenceql.utils :as utils]
            [inferenceql.multimixture.crosscat :as xcat]
            [inferenceql.multimixture.gpm.proto :as gpm-proto]))

(defrecord CrossCat
  [model latents]
  gpm-proto/GPM
  (logpdf [this targets constraints inputs])
  (simulate [this targets constraints n-samples inputs]
    (repeatedly n-samples #(xcat/simulate
                            model
                            latents
                            {:targets targets :constraints constraints})))
  (mutual-information [this target-a target-b constraints n-samples]))
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
      CCat   (->CrossCat model latents)
      targets ["foo"]
      constraints {"bar" 5}
      ]
(gpm-proto/simulate CCat targets constraints 1 {}))
