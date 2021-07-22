(ns inferenceql.viz.panels.sd2.sim.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:sd2-sim-panel {:target-gene :stoA
                   :essential-genes []
                   :essential-genes-available [:dnaG :dnaX :holB :hbs :smc :rpoB :rpoC :sigA :cca
                                               :rnc :rnpA :nusA :rplB :rplC :rplD :rplW :rpmA
                                               :rpmI :rpsB :rpsJ :rpsO :rpsS :argS :asnS :pheS
                                               :pheT :serS :trpS :gatA :gatB :gatC :fmt :frr :groEL
                                               :groES :acpS :birA :fabG :glmS :dapF :murB :murC
                                               :murG :tagD :divIB :divIC :ftsL :ftsW :pbpB :mreC
                                               :fbaA :prs :menB :trxA :trxB :nrdE :nrdF :pyrG
                                               :tmk :nadE :mrpD :ppaC :odhB :pdhA]

                   ;; Keys related to constraining target gene expr level.
                   :conditioned false
                   :expr-level nil
                   :constraints {}}})

(s/def ::sd2-sim-panel (s/keys :req-un []))
