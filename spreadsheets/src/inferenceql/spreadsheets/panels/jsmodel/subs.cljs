(ns inferenceql.spreadsheets.panels.jsmodel.subs
  (:require [re-frame.core :as rf]))

(defn ^:sub show-model
  "Returns whether we should show the panel displaying the js-model source code.
  The value retuned is a boolean."
  [db _]
  (get-in db [:show-model]))

(rf/reg-sub :jsmodel/show-model
            show-model)
