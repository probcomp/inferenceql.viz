(ns inferenceql.multimixture.crosscat.dpmm-mvp
  (:require [inferenceql.multimixture.crosscat :as xcat]
            [inferenceql.multimixture.crosscat.kernels.category :as c]
            [inferenceql.multimixture.crosscat.specification :as spec]))

(def data {"color"  ["red" "blue" "red" "blue"]
           "height" [  6     3      6     3]})
;; true labels         0     1      0     1
;; labels below        0     1      1     0
;;                             wrong^     ^wrong


(def latents {:global {:alpha  1
                       :counts [2]
                       :z      {"color"  0
                                "height" 0}}
              :local  [{:alpha   1
                        :counts [2 2]
                        :y      [0 1 1 0]}]})
(spec/valid-latents? latents)


(def xcat {:types   {"color"  :categorical
                     "height" :gaussian}
           :views  [{:hypers {"color"  {:p     {:dirichlet {:alpha [1 1 1]}}}
                              "height" {:sigma {:gamma     {:k     1   :theta 1}}
                                        :mu    {:beta      {:alpha 0.5 :beta 0.5}}}}
                     :categories [{:parameters {"color"  {:p {"red" 0.8 "green" 0.1 "blue" 0.1}}
                                                "height" {:mu 6 :sigma 1}}}
                                  {:parameters {"color"  {:p {"red" 0.3 "green" 0.1 "blue" 0.6}}
                                                "height" {:mu 3 :sigma 1}}}]}]})
(spec/valid-xcat? xcat)

(let [[xcat' latents'] (c/kernel data xcat latents m)
      constraints      {"height" 6}
      simulated        (xcat/simulate xcat latents constraints)]
  (println "LL old model    : " (xcat/log-likelihood data xcat latents))
  (println "old assignments : " (get-in latents [:local 0 :y]))
  (println "LL new model    : " (xcat/log-likelihood data xcat' latents'))
  (println "new assignments : " (get-in latents' [:local 0 :y]))
  (println "Simulated value : " simulated))
