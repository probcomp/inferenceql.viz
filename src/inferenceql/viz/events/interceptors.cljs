(ns inferenceql.viz.events.interceptors
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [re-frame.core :as rf]
            [re-frame.interceptor :refer [->interceptor get-coeffect]]
            [re-frame.loggers :refer [console]]
            [inferenceql.viz.db :as db]
            [inferenceql.viz.config :as config]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [db a-spec]
  (when-not (s/valid? a-spec db)
    (js/console.error (with-out-str (expound/expound a-spec db)))
    (throw (ex-info (str "db does not satisfy spec: " (s/explain-str a-spec db))
                    (s/explain-data a-spec db)))))

(def check-spec
  "An interceptor which validates the entire db against its spec."
  (rf/after #(check-and-throw % ::db/db)))

(def log-name
  "An interceptor which logs an event handler's name to the console.
  Output includes the event vector. "
  (->interceptor
    :id :log-name
    :before (fn debug-before
              [context]
              (console :log "Handling re-frame event:" (get-coeffect context :event))
              context)
    :after  (fn debug-after
              [context]
              context)))

(def event-interceptors
  "A default set of event interceptors to use within events across the app."
  (if (get config/config :enable-debug-interceptors false)  ;; Pulling out the relevant application setting.
    [rf/debug check-spec]
    [log-name]))
