(ns inferenceql.distributions
  (:require [clojure.spec.alpha :as s]
            [metaprob.prelude :as mp]
            [metaprob.distributions :as mpd]
            #?(:clj [incanter.distributions :as distributions])
            [inferenceql.multimixture.specification :as spec]))

;; https://rosettacode.org/wiki/Gamma_function#Clojure
(s/fdef gamma
  :args (s/cat :number pos?))

(defn gamma
  "Returns Gamma(z + 1 = number) using Lanczos approximation."
  [number]
  (if (< number 0.5)
    (/ Math/PI (* (Math/sin (* Math/PI number))
                  (gamma (- 1 number))))
    (let [n (dec number)
          c [0.99999999999980993 676.5203681218851 -1259.1392167224028
             771.32342877765313 -176.61502916214059 12.507343278686905
             -0.13857109526572012 9.9843695780195716e-6 1.5056327351493116e-7]]
      (* (Math/sqrt (* 2 Math/PI))
         (Math/pow (+ n 7 0.5) (+ n 0.5))
         (Math/exp (- (+ n 7 0.5)))
         (+ (first c)
            (apply + (map-indexed #(/ %2 (+ n %1 1)) (next c))))))))

(defn log-gamma
  "Returns Gamma(z + 1 = number) using Lanczos approximation."
  [number]
  (if (< number 0.5)
    (- (Math/log Math/PI)
       (+ (Math/log (Math/sin (* Math/PI number)))
          (log-gamma (- 1 number))))
    (let [n (dec number)
          c [0.99999999999980993 676.5203681218851 -1259.1392167224028
             771.32342877765313 -176.61502916214059 12.507343278686905
             -0.13857109526572012 9.9843695780195716e-6 1.5056327351493116e-7]]
      (+ (* 0.5 (Math/log (* 2 Math/PI)))
         (* (Math/log (+ n 7 0.5))
            (+ n 0.5))
         (- (+ n 7 0.5))
         (Math/log (+ (first c)
                      (apply + (map-indexed #(/ %2 (+ n %1 1)) (next c)))))))))

(defn gamma-dist [k]
  ;; Gamma usually takes a `s` also, but Ulrich says it's not needed.
  (loop []
    (let [e Math/E
          u (rand)
          v (rand)
          w (rand)
          condition-1 (<= u (/ e (+ e k)))
          eps (if condition-1
                (Math/pow v (/ 1 k))
                (- 1 (Math/log v) ))
          n   (if condition-1
                (* w (Math/pow eps (- k 1)))
                (* w (Math/pow e (- 0 eps))))
          condition-2 (> n (* (Math/pow eps (- k 1))
                              (Math/pow e (- 0 eps))))]
      (if condition-2 (recur) eps))))

#_(mp/infer-and-score :procedure cljs-beta :inputs [0.001 0.001])

(s/fdef beta-pdf
  :args (s/cat :v ::spec/probability
               :args (s/tuple ::spec/alpha ::spec/beta)))

(defn beta-pdf
  [v [alpha beta]]
  (let [b (fn [alpha beta]
            (/ (* (gamma alpha)
                  (gamma beta))
               (gamma (+ alpha beta))))]
    (/ (* (mp/expt v
                   (- alpha 1))
          (mp/expt (- 1 v)
                   (- beta 1)))
       (b alpha beta))))

(defn beta-logpdf
  [v [alpha beta]]
  (let [log-b (fn [alpha beta]
                (- (+ (log-gamma alpha)
                      (log-gamma beta))
                   (log-gamma (+ alpha beta))))]
    (- (+ (* (Math/log v)
             (- alpha 1))
          (* (Math/log (- 1 v))
             (- beta 1)))
       (log-b alpha beta))))


(s/fdef beta-sampler
  :args (s/cat :params ::spec/beta-parameters))

(defn- beta-sampler
  [{:keys [alpha beta]}]
  (let [x (gamma-dist alpha)
        y (gamma-dist beta)
        denom (+ x y)]
    (if (zero? denom)
      0
      (/ x denom))))

(s/fdef beta-scorer
  :args (s/cat :v ::spec/probability
               :args (s/tuple ::spec/beta-parameters)))

(defn- beta-scorer
  [v [{:keys [alpha beta]}]]
  (beta-logpdf v [alpha beta]))

(def beta (mp/make-primitive beta-sampler beta-scorer))

;; TODO: Remove. This is Metaprob's implementation, inlined.
#?(:clj (defn mp-beta-scorer
          [x [alpha beta]]
          (mp/log (distributions/pdf
                   (distributions/beta-distribution alpha beta)
                   x))))

;; Chinese restaurant process.
;; A typical counts vector for alpha=2: [5 65 25 2 1 2]

(def crp
  (gen [counts alpha]
    (mpd/categorical (concat counts [alpha]))))

(defn crp-incorporate [counts new-seating]
  (if (>= new-seating (count counts))
    (vec (concat counts
                 (repeat (- new-seating (count counts)) 0)
                 [1]))
    (assoc counts
           new-seating
           (+ (counts new-seating) 1))))

;; Normal distribution formulated as member of conjugate prior family.
;; Cribbed from cgpm/src/primitives/normal.py.  This version relies on
;; metaprob to compute the score but I strongly suspect that the
;; answer won't be correct and we'll need a custom scoring formula.
;; However I tried to extract one from normal.py and was not happy
;; with how that went - it's buried inside of complexity we don't need.
;; -JAR

;; Currently untested.

(def generate
  (gen [m r s nu]
    (let [rho (mpd/gamma (/ nu 2.0) (/ 2.0 s))
          sigma (Math/exp (* rho r) -0.5)
          mu (mpd/gaussian m (Math/exp (* rho r) -0.5))]
      (mpd/gaussian mu (Math/exp rho -0.5)))))

;; I'm putting the following as a comment about the categorical
;; distributions that Crosscat needs, for lack of a better home (was
;; sitting in slack chat with Ulli): -JAR
;;
;; (categorical (map (fn [weight] (+ weight alpha)) weights))



(comment
  (require '[zane.vega.repl :as vega])
  (let [alpha 0.01
        beta 0.01
        n-samples 10000
        mp-samples (mapv #(hash-map :beta % :type "metaprob") (repeatedly n-samples #(metaprob.distributions/beta alpha beta)))
        our-samples (mapv #(hash-map :beta % :type "custom") (repeatedly n-samples #(beta-sampler {:alpha alpha :beta beta})))
        step 0.01
        #_#(beta-sampler {:alpha 0.01 :beta 0.01})]
    (vega/vega {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
                :width 600
                :height 600
                :layer [{:data {:values mp-samples}
                         :mark {:type "bar"
                                :color "red"
                                :opacity 0.5}
                         :encoding {:x {:bin {:step step}
                                        :field "beta"
                                        :type "quantitative"}
                                    :y {:aggregate "count"
                                        :type "quantitative"}}}
                        {:data {:values our-samples}
                         :mark {:type "bar"
                                :color "blue"
                                :opacity 0.5}
                         :encoding {:x {:bin {:step step}
                                        :field "beta"
                                        :type "quantitative"}
                                    :y {:aggregate "count"
                                        :type "quantitative"}}}]}))

  (let [alpha 0.01
        beta 0.01
        our-pdfs (->> (range 1/100 1 1/100)
                      (map #(hash-map :x (float %)
                                      :y (beta-scorer % [{:alpha alpha :beta beta}]))))
        mp-pdfs (->> (range 1/100 1 1/100)
                     (map #(hash-map :x (float %)
                                     :y (mp-beta-scorer % [alpha beta]))))]
    (vega/vega {:width 600
                :height 600
                :layer [{:data {:values mp-pdfs}
                         :mark {:type "line"
                                :color "red"
                                :opacity 0.5}
                         :encoding
                         {:x {:field "x" :type "quantitative"}
                          :y {:field "y" :type "quantitative"}}}
                        {:data {:values our-pdfs}
                         :mark {:type "line"
                                :color "blue"
                                :opacity 0.5}
                         :encoding
                         {:x {:field "x" :type "quantitative"}
                          :y {:field "y" :type "quantitative"}}}]}))

  )
