(ns inferdb.spreadsheets.events.interceptors
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [db a-spec]
  (when-not (s/valid? a-spec db)
    (js/console.error (with-out-str (expound/expound a-spec db)))
    (throw (ex-info (str "db does not satisfy spec: " (s/explain-str a-spec db))
                    (s/explain-data a-spec db)))))

(def check-spec (rf/after #(check-and-throw % ::db/db)))
