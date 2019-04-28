(ns inferdb.spreadsheets.views
  (:require [re-frame.core :as rf]
            [inferdb.spreadsheets.handsontable :as hot]))

(defn app
  []
  (let [hot-props @(rf/subscribe [:hot-props])]
    (js/console.log hot-props)
    [hot/handsontable hot-props]))
