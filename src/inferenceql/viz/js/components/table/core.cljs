(ns inferenceql.viz.js.components.table.core
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.js.components.table.views :refer [handsontable]]))

(defn table
  "Javascript interface to table UI component. Returns a DOM node with the table.
  `data` - an array of js objects representing rows.
  `options` - a js object with options for the table. See the handsontable component
    for details on options."
  ([data]
   (table data {}))
  ([data options]
   (let [data (->clj data)
         options (->clj options)

         node (dom/createElement "div")
         component [handsontable {:style {:padding-bottom "5px"}} data options :reagent-observable]]
     (rdom/render component node)
     node)))
