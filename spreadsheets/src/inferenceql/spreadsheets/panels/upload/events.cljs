(ns inferenceql.spreadsheets.panels.upload.events
   "Contains events related to the upload panel."
  (:require [clojure.edn :as edn]
            [re-frame.core :as rf]
            [goog.labs.format.csv :as csv]
            [inferenceql.inference.gpm :as gpm]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.auto-modeling.bayesdb-import :as bayesdb-import]
            [medley.core :refer [index-by map-vals find-first]]))


(defn ^:event-fx read-failed
  "Emits an error message to the browser console.

  Args:
    `error` - (string) The error message to emit to the console.

  Triggered when:
    Effects which process files or urls for new data and models fail."
  [_ [_ error]]
  {:fx [[:js/console-error error]]})
(rf/reg-event-fx :upload/read-failed
                 event-interceptors
                 read-failed)
