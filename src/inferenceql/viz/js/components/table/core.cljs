(ns inferenceql.viz.js.components.table.core
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.js.components.table.views :refer [handsontable]]))

(defn table
  "Javascript interface to table UI component. Returns a DOM node with the table."
  ([data]
   (table data {}))
  ([data options]
   (let [data (->clj data)
         options (->clj options)

         node (dom/createElement "div")
         component [handsontable data options]]
     (rdom/render component node)
     node)))
