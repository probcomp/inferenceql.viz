(ns inferenceql.spreadsheets.panels.control.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:control-panel {:confidence-threshold 0.9
                   :parts {:part_laci 0.0
                           :part_psra 0.0
                           :part_yfp 0.0
                           :part_phlf 0.0
                           :part_amerv2 0.0
                           :part_arac 0.0
                           :part_kanr 0.0
                           :part_bm3r1 0.0}
                   :reagent-forms {:confidence-mode :none
                                   :arabinose :0.0
                                   :iptg :7.98e-05
                                   :timepoint :18.0}

                   :query-string "SELECT *"
                   :selection-color :blue}})


(s/def ::control-panel (s/keys :req-un [::confidence-threshold
                                        ::parts
                                        ::reagent-forms
                                        ::query-string
                                        ::selection-color]))
(s/def ::confidence-threshold number?)
(s/def ::reagent-forms (s/keys :req-un [::confidence-mode]))
;; TODO add entries for experimental-conditions.
(s/def ::confidence-mode keyword?)
(s/def ::selection-color keyword?)
(s/def ::query-string string?)
;; TODO add more detailed specs.
(s/def ::parts any?)


;; Accessor functions for indexing into parts of the control-panel's db.

(defn selection-color [db]
  (get-in db [:control-panel :selection-color]))
