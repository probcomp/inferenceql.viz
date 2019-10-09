(ns inferdb.cgpm.main
  (:refer-clojure :exclude [map reduce apply])
  (:require
   [metaprob.prelude :as prelude :refer [map]]
   [metaprob.distributions]
   [inferdb.cgpm.utils :as utils]
   [metaprob.inference]))

;; ----------------------
;; STATISTICAL DATA TYPES
;; ----------------------

;; Constructor of real statistical type with support [low, high].
(defn make-ranged-real-type
  [low high]
  {:name (str "real[low=" low " high=" high "]")
   :valid? number?
   :in-support? (fn [x] (and (< low x) (< x high)))
   :base-measure :continuous})

;; Constructor of real statistical type with support {low, ..., high}.
(defn make-ranged-integer-type
  [low high]
  {:name (str "integer[low=" low " high=" high "]")
   :valid? number?
   :in-support? (fn [x] (and (int? x) (<= low x) (<= x high)))
   :base-measure :discrete})

;; Constructor of a nominal statistical type with the given categories.
(defn make-nominal-type
  [categories]
  {:name (str "nominal[low=" categories  "]")
   :valid? (fn [x] (clojure.core/contains? categories x))
   :in-support? (fn [x] (clojure.core/contains? categories x))
   :base-measure :discrete})

;; The real statistical type i.e. support in [-infinity, infinity].
(def real-type
  {:name "real"
   :valid? number?
   :in-support? number?
   :base-measure :continuous})

;; The integer statistical type i.e. support {-infinity, ..., infinity}.
(def integer-type
  {:name "integer"
   :valid? number?
   :in-support? int?
   :base-measure :discrete})

;; --------------
;; CGPM INTERFACE
;; --------------

;; INITIALIZE

(defn make-cgpm
  [proc output-addrs-types input-addrs-types output-address-map input-address-map]
  (let [output-addrs (set (keys output-addrs-types))
        input-addrs (set (keys input-addrs-types))]

    ;; TODO: replace this with specs lib -- typechecking that can be done
    ;; outside the body of the function.
    (utils/assert-no-overlap output-addrs input-addrs :outputs :inputs)
    (utils/assert-has-keys output-address-map output-addrs)
    (utils/assert-has-keys input-address-map input-addrs)
    (utils/assert-valid-output-address-map output-address-map)
    (utils/assert-valid-input-address-map input-address-map)
    {:proc proc
     :output-addrs-types output-addrs-types
     :input-addrs-types input-addrs-types
     :output-address-map output-address-map
     :input-address-map input-address-map}))

;; LOGPDF

(defn validate-cgpm-logpdf
  [cgpm target-addrs-vals constraint-addrs-vals input-addrs-vals]
  ;; Confirm addresses are valid and do not overlap.
  (let [target-addrs (set (keys target-addrs-vals))
        constraint-addrs (set (keys constraint-addrs-vals))
        input-addrs (set (keys input-addrs-vals))]
    (utils/assert-no-overlap target-addrs constraint-addrs :targets :constraints)
    (utils/assert-has-keys (get cgpm :output-addrs-types) target-addrs)
    (utils/assert-has-keys (get cgpm :output-addrs-types) constraint-addrs)
    (utils/assert-has-keys (get cgpm :input-addrs-types) input-addrs)
    ;; Confirm values match the statistical data types.
    (utils/validate-row (get cgpm :output-addrs-types) target-addrs-vals false)
    (utils/validate-row (get cgpm :output-addrs-types) constraint-addrs-vals false)
    (utils/validate-row (get cgpm :input-addrs-types) input-addrs-vals true)))

(defn cgpm-logpdf
  [cgpm target-addrs-vals constraint-addrs-vals input-addrs-vals]
  ;; Error checking on the arguments.
  ;; (validate-cgpm-logpdf
  ;;   cgpm target-addrs-vals constraint-addrs-vals input-addrs-vals)
  ;; Convert target, constraint, and input addresses from CGPM to inf.
  (let [target-addrs-vals' (utils/rekey-addrs-vals
                            (get cgpm :output-address-map)
                            target-addrs-vals)
        constraint-addrs-vals' (utils/rekey-addrs-vals
                                (get cgpm :output-address-map)
                                constraint-addrs-vals)
        target-constraint-addrs-vals (merge target-addrs-vals'
                                            constraint-addrs-vals')

        input-args (utils/extract-input-list
                    (get cgpm :input-address-map)
                    input-addrs-vals)

        ;; Run infer to obtain probabilities.
        [retval trace log-weight-numer] (prelude/infer-and-score
                                         :procedure (:proc cgpm)
                                         :inputs input-args
                                         :observation-trace
                                         target-constraint-addrs-vals)

        log-weight-denom (if (empty? constraint-addrs-vals')
                           ;; There are no constraints: log weight is zero.
                           0
                           ;; There are constraints: find marginal probability of constraints.
                           (let [[retval trace weight] (prelude/infer-and-score
                                                        :procedure (:proc cgpm)
                                                        :inputs input-args
                                                        :observation-trace constraint-addrs-vals')]
                             weight))]
    (- log-weight-numer log-weight-denom)))

;; SIMULATE

(defn validate-cgpm-simulate
  [cgpm target-addrs constraint-addrs-vals input-addrs-vals]
  ;; Confirm addresses are valid and do not overlap.
  (let  [constraint-addrs (set (keys constraint-addrs-vals))
         input-addrs (set (keys input-addrs-vals))]
    (utils/assert-no-overlap target-addrs constraint-addrs :targets :constraints)
    (utils/assert-has-keys (get cgpm :output-addrs-types) (set target-addrs))
    (utils/assert-has-keys (get cgpm :output-addrs-types) constraint-addrs)
    (utils/assert-has-keys (get cgpm :input-addrs-types) input-addrs)
    ;; Confirm values match the statistical data types.
    (utils/validate-row (get cgpm :output-addrs-types) constraint-addrs-vals false)
    (utils/validate-row (get cgpm :input-addrs-types) input-addrs-vals true)))

(defn cgpm-simulate
  [cgpm target-addrs constraint-addrs-vals input-addrs-vals num-samples]
  ;; Error checking on the arguments.
  (validate-cgpm-simulate
   cgpm target-addrs constraint-addrs-vals input-addrs-vals)

  ;; Convert target, constraint, and input addresses from CGPM to inf.
  (let [target-addrs' (utils/rekey-addrs
                       (get cgpm :output-address-map) target-addrs)
        constraint-addrs-vals' (utils/rekey-addrs-vals
                                (get cgpm :output-address-map)
                                constraint-addrs-vals)
        input-args (utils/extract-input-list
                    (get cgpm :input-address-map) input-addrs-vals)]
    ;; Run infer to obtain the samples.
    (repeatedly num-samples
                (fn []
                  (let [[retval trace log-weight-numer] (prelude/infer-and-score
                                                         :procedure (:proc cgpm)
                                                         :inputs input-args
                                                         :observation-trace constraint-addrs-vals')]
                    ;; Extract and return the requested samples.
                    (utils/extract-samples-from-trace
                     trace target-addrs (get cgpm :output-address-map)))))))

;; MUTUAL INFORMATION

(defn compute-mi
  [cgpm target-addrs-0 target-addrs-1 constraint-addrs-vals input-addrs-vals num-samples]
  ;; Obtain samples for simple Monte Carlo integration.
    (let [samples (cgpm-simulate cgpm
                                 (into [] (concat target-addrs-0 target-addrs-1))
                                 constraint-addrs-vals input-addrs-vals num-samples)
          ;; Compute joint log probabilities.
          logp-joint      (map
                           (fn [sample] (cgpm-logpdf
                                         cgpm
                                         sample
                                         constraint-addrs-vals
                                         input-addrs-vals))
                           samples)
          ;; Compute marginal log probabilities.
          logp-marginal-0 (map
                           (fn [sample] (cgpm-logpdf
                                         cgpm
                                         (select-keys sample target-addrs-0)
                                         constraint-addrs-vals input-addrs-vals))
                           samples)
          logp-marginal-1 (map
                           (fn [sample] (cgpm-logpdf
                                         cgpm (select-keys sample target-addrs-1)
                                         constraint-addrs-vals input-addrs-vals))
                           samples)]
      ;; MI is average log joint minus the average sum of log marginals.
      (- (utils/compute-avg logp-joint)
         (+ (utils/compute-avg logp-marginal-0)
            (utils/compute-avg logp-marginal-1)))))

(defn cgpm-mutual-information
  [cgpm target-addrs-0 target-addrs-1 controlling-addrs constraint-addrs-vals
   input-addrs-vals num-samples-inner num-samples-outer]

  ;; Make sure that fixed and controlling constraints do not overlap.
  (utils/assert-no-overlap (set controlling-addrs)
                           (set (keys constraint-addrs-vals))
                           :constraint-addrs
                           :constraint-addrs-vals)

  ;; Determine whether to average over the controlling variables.
  (if (= (count controlling-addrs) 0)
    ;; No controlling variables: compute and return the MI directly.
    (compute-mi cgpm target-addrs-0 target-addrs-1 constraint-addrs-vals
                input-addrs-vals num-samples-inner)

    ;; Controlling variables: compute MI by averaging over them.
    (let [;; Obtain samples for simple Monte Carlo integration.
          samples (cgpm-simulate cgpm controlling-addrs
                                 constraint-addrs-vals
                                 input-addrs-vals num-samples-outer)

          ;; Pool the sampled constraints with user-provided constraints.
          constraints-merged (map
                              (fn [sample] (merge sample constraint-addrs-vals))
                              samples)

          ;; Compute the MI for each sample.
          mutinf-values (map (fn [constraints]
                               (compute-mi cgpm target-addrs-0
                                           target-addrs-1
                                           constraints input-addrs-vals
                                           num-samples-inner))
                             constraints-merged)]

      ;; Return the average MI value.
      (utils/compute-avg mutinf-values))))

;; KL DIVERGENCE.

(defn validate-cgpm-kl-divergence
  [cgpm target-addrs-0 target-addrs-1]
  ;; Confirm target address have same length.
  (utils/assert-same-length target-addrs-0 target-addrs-1
                            :target-addrs-0 :target-addrs-1)
  ;; Confirm base measures agree.
    (let [output-addrs-types (get cgpm :output-addrs-types)
          gbm (fn [t] (get (utils/safe-get output-addrs-types t) :base-measure))
          base-measures-0 (map gbm target-addrs-0)
          base-measures-1 (map gbm target-addrs-1)]
      (assert (= base-measures-0 base-measures-1)
              (str "targets "
                   target-addrs-0
                   " and "
                   target-addrs-1
                   "must have same base measures"))))

(defn cgpm-kl-divergence
  [cgpm target-addrs-0 target-addrs-1 constraint-addrs-vals-0 constraint-addrs-vals-1
   input-addrs-vals num-samples]
  ;; Make sure the base measures match.
  (validate-cgpm-kl-divergence cgpm target-addrs-0 target-addrs-1)
  ;; Obtain the p samples for simple Monte Carlo integration.
  (let [samples-p (cgpm-simulate cgpm
                                 target-addrs-0 constraint-addrs-vals-0
                                 input-addrs-vals num-samples)

        ;; Obtain the q samples.
        keymap (zipmap target-addrs-0 target-addrs-1)
        samples-q (map (fn [sample] (utils/rekey-dict keymap sample)) samples-p)

        ;; Compute the probabilities under p.
        logp-p (map (fn [sample]
                      (cgpm-logpdf cgpm sample constraint-addrs-vals-0
                                   input-addrs-vals))
                    samples-p)
        ;; Compute the probabilities under q.
        logp-q (map (fn [sample]
                      (cgpm-logpdf cgpm sample constraint-addrs-vals-1
                          input-addrs-vals))
                    samples-q)]
      ;; KL is average log ratio.
      (- (utils/compute-avg logp-p)
         (utils/compute-avg logp-q))))
