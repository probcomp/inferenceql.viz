(ns inferenceql.spreadsheets.effects
  "A set of re-frame effects used across the whole application.
  These effects are generally related to browser APIs."
  (:require [re-frame.core :as rf]))

(defn console-error
  "Prints a string `msg` to the browser console error log."
  [msg]
  (.error js/console msg))

(rf/reg-fx :js/console-error console-error)

(defn console-warn
  "Prints a string `msg` to the browser console warn log."
  [msg]
  (.warn js/console msg))

(rf/reg-fx :js/console-warn console-warn)

(defn alert
  "Displays a string `msg` in a js alert popup."
  [msg]
  (js/alert msg))

(rf/reg-fx :js/alert alert)
