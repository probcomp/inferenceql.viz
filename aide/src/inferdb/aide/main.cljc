(ns inferdb.aide.main
  (:refer-clojure :exclude [map apply replicate])
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen]]))
  (:require
   #?(:clj [metaprob.generative-functions :refer [apply-at at gen make-constrained-generator]]
      :cljs [metaprob.generative-functions :refer [apply-at at make-constrained-generator]])
   [metaprob.trace :as trace]
   [metaprob.prelude :refer [map replicate infer-and-score]]
   [metaprob.distributions :as dist]
   [metaprob.inference :as inf]))

(def log-likelihood-weighting
  (gen [& {:keys [model inputs observations n-particles]
           :or {inputs []}}]
    (let [scores
          (map (fn [i]
                 (let [[_ _ s]
                       (at i infer-and-score :procedure model :inputs inputs :observation-trace observations)]
                   s))
               (range n-particles))]
      (dist/logmeanexp scores))))

(defn intervene [f t]
  (gen [& args] (first (apply-at '() (make-constrained-generator f t) args))))

;; Calculate the mean of some numbers
(defn avg [xs] (/ (reduce + xs) (count xs)))

;; Compare two generative functions at some subset of addresses.
;; Returns an unbiased estimate of an upper bound on the symmetric KL divergence
;; between f and g.
(defn compare-generative-functions [f g addresses Nf Mf Ng Mg]
  (let [f-traces
        (map #(trace/partition-trace % addresses)
             (replicate Nf #(nth (infer-and-score :procedure f) 1)))

        g-traces
        (map #(trace/partition-trace % addresses)
             (replicate Ng #(nth (infer-and-score :procedure g) 1)))

        ;; Estimate the expectation, when x ~ g, of log P_f(x), using likelihood weighting
        f-scores-on-g-samples
        (map (fn [[x _]] (log-likelihood-weighting :model f, :observations x, :n-particles Mf))
             g-traces)

        ;; Estimate the expectation, when x ~ f, of log P_g(x), using likelihood weighting
        g-scores-on-f-samples
        (map (fn [[x _]] (log-likelihood-weighting :model g, :observations x, :n-particles Mg))
             f-traces)

        ;; Estimate the expectation, when x ~ f, of log P_f(x)
        f-scores-on-f-samples
        (map (fn [[x u]] ((intervene log-likelihood-weighting {0 u}) :model f, :observations x, :n-particles Mf)) f-traces)

        g-scores-on-g-samples
        (map (fn [[x v]] ((intervene log-likelihood-weighting {0 v}) :model g, :observations x, :n-particles Mg)) g-traces)]

    ;; Use Clojure's version of `map`, which can take two lists l and m,
    ;; and apply a function (like -) to l[0],m[0], l[1],m[1], etc.
    (+ (avg (clojure.core/map - f-scores-on-f-samples g-scores-on-f-samples))
       (avg (clojure.core/map - g-scores-on-g-samples f-scores-on-g-samples)))))

(def importance-resampling-model
  (gen [model inputs observations N]
    (let [;; Generate N particles of the form [retval trace weight],
          ;; tracing the ith particle at '("particles" i)
          particles
          (map (fn [i]
                 (at `("particles" ~i)
                     infer-and-score
                     :procedure model,
                     :inputs inputs,
                     :observation-trace observations))
               (range N))

          ;; Choose one of the particles, according to their weights
          chosen-index
          (at "chosen-index" dist/log-categorical (map #(nth % 2) particles))

          ;; Pull out the trace of the chosen particle
          chosen-particle-trace
          (let [[retval trace score] (nth particles chosen-index)] trace)]

      ;; We need the chosen trace -- the "inference answer" that we're giving
      ;; to exist in _our_ trace at a predictable address. If the model we're
      ;; doing inference about has latent variables at addresses "x" and "y",
      ;; for example, then our inferred latent variables should have addresses (say)
      ;; '("inferred-trace" "x") and '("inferred-trace" "y").
      ;; We do this below by looping through every variable in our inferred trace,
      ;; and "sampling" it again, using the deterministic `exactly` distribution:
      (map (fn [model-addr]
             (at `("inferred-trace" ~@model-addr)
                dist/exactly (trace/trace-value chosen-particle-trace model-addr)))
           (trace/addresses-of chosen-particle-trace))

      ;; Return the chosen particle
      chosen-particle-trace)))

(defn make-smart-importance-resampling-proposer
  [meta-observation-trace]
  (let [inferred-trace (trace/trace-subtrace meta-observation-trace "inferred-trace")]
    (gen [model inputs observations N]
      (let [chosen-index
            (at "chosen-index" dist/uniform-discrete (range N))

            other-indices
            (filter (fn [i] (not= i chosen-index)) (range N))]

        ;; Randomly sample particles at the other indices
        (map (fn [i]
               (at `("particles" ~i)
                  infer-and-score :procedure model :inputs inputs :observation-trace observations))
             other-indices)

        ;; Force exact samples of the inferred trace's choices at the chosen index
        (map (fn [addr]
               (at `("particles" ~chosen-index ~@addr) dist/exactly (trace/trace-value inferred-trace addr)))
             (trace/addresses-of inferred-trace))))))

(def importance-resampling-gf
  (inf/with-custom-proposal-attached
    importance-resampling-model
    make-smart-importance-resampling-proposer
    (fn [tr] (trace/trace-has-subtrace? tr "inferred-trace"))))
