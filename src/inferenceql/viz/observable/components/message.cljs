(ns inferenceql.viz.observable.components.message)

(defn failure-msg [msg]
  [:div {:class "observablehq--inspect"
         :style {:whitespace "pre" :color "red"}}
   msg])

