(ns inferenceql.multimixture.crosscat.specification
  (:require [clojure.spec.alpha :as s]))

;; Distributions.


;; CrossCat model

;; Latents data structure.
(s/def ::count (s/and integer? (comp not neg?)))
(s/def ::counts (s/coll-of ::count :into []))

(s/def ::y ::counts)
(s/def ::alpha pos?)

(defn verify-counts-assignments?
  [counts assignments]
  (= (->> assignments
          (frequencies)
          (sort-by key)
          (vals)
          (vec))
     counts))

(s/def ::latents-local (s/and (s/keys :req-un [::counts ::y ::alpha])
                              #(verify-counts-assignments?
                                 (:counts %)
                                 (:y %))))

(s/def ::z ::counts)

(s/def ::global (s/and (s/keys :req-un [::alpha ::counts ::z])
                       #(verify-counts-assignments?
                         (:counts %)
                         (:z %))))

(s/def ::local (s/and (s/coll-of ::latents-local :into [])
                      #(->> %
                            (map (fn [latent] (count (:y latent))))
                            (distinct)
                            (count)
                            (= 1))))

(s/def ::latents (s/and (s/keys :req-un [::local ::global])
                        #(= (count (:local %))
                                   (count (get-in % [:global :counts])))))

(defn valid-local-latents?
  [local-latents]
  (s/valid? ::latents-local local-latents))
