(ns inferenceql.spreadsheets.panels.upload.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :upload/display
 (fn [db _]
   (get-in db [:upload-panel :display])))
