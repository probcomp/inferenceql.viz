(ns inferenceql.viz.panels.modal.subs
  (:require [re-frame.core :as rf]))

(defn ^:sub content
  "Returns the hiccup to be displayed as the content of the modal panel."
  [db _]
  (get-in db [:modal-panel :content]))
(rf/reg-sub :modal/content
            content)
