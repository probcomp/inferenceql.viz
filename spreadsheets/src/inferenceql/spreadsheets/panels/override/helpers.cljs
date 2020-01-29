(ns inferenceql.spreadsheets.panels.override.helpers
  (:require [clojure.string :as str]))

(defn- get-args
  "Returns an array of strings representing the argument names of `func`.
  Based off of https://davidwalsh.name/javascript-arguments
  `func` {function} A javascript function object."
  [func]
  (let [args-portion-regex #"function.*?\(([^)]*)\)"
        func-text (.toString func)

        ;; First match everything inside the function argument parens.)
        match-vector (re-find args-portion-regex func-text)
        args-portion-text (second match-vector)

        ;; Split the arguments string into an array comma delimited.
        args-text (str/split args-portion-text ",")]

        ;; TODO Trim Javascript inline comments that might appear between argument names.
        ;; example: (arg1, arg2 /* comment */, arg 3)
        ;; Currently, we are not removing these inline comments because Clojurescript has an bug
        ;; with regexes that start with a forward slash.

    ;; Trim the whitespace next to args.
    (map str/trim args-text)))

(defn gen-insert-fn
  "Returns a function that takes `row` and applies the given overrides.
  Overrides as defined in `override-map`. This does not take into
  account what order the overrides get applied. For now we assume no
  dependencies, so we can apply them in any order."
  [override-map]
  (fn [row]
    (reduce-kv (fn [row oride-col oride-fn]
                 (let [func-deps (get-args oride-fn)
                       dep-vals (map #(get row %) func-deps)
                       new-col-val (apply oride-fn dep-vals)]
                   (assoc row oride-col new-col-val)))
               row
               override-map)))
