(ns inferenceql.viz.panels.viz.regression
  "Functions that modify vega lite specs."
  (:require [inferenceql.viz.panels.viz.util :refer [regression-color]]
            #?(:cljs [goog.string :refer [format]])))

(defn line
  "Adds regression line and text info as new layers in vega-lite `base-spec`.
  If correlation is not available, returns `base-spec`."
  [x-col y-col correlation samples base-spec]
  (if-let [{:keys [slope intercept r-value p-value]} (get-in correlation [x-col y-col])]
    (let [r-info-string (str (format "R²: %.2f" (* r-value r-value))
                             "   "
                             (format "p-value: %.2f" p-value))
          ;; Function y(x) for regression line.
          y (fn [x] (+ (* slope x) intercept))

          x-vals (->> samples
                      (filter (comp #{"observed"} :collection))
                      (map x-col)
                      (filter some?))
          [min-x max-x] [(apply min x-vals) (apply max x-vals)]

          r-line-spec {:data {:values [{:x min-x :y (y min-x)} {:x max-x :y (y max-x)}]}
                       :mark {:type "line"
                              :strokeDash [4 4]
                              :color regression-color}
                       :encoding {:x {:field "x"
                                      :type "quantitative"}
                                  :y {:field "y"
                                      :type "quantitative"}
                                  :opacity {:condition {:param "showRegression"
                                                        :value 1}
                                            :value 0}}}
          r-text-spec {:data {:values [{:r-info-string r-info-string}]}
                       :mark {:type "text"
                              :color regression-color
                              :x "width"
                              :align "right"
                              :y -5
                              :clip false}
                       :encoding {:text {:field "r-info-string" :type "nominal"}
                                  :opacity {:condition {:param "showRegression"
                                                        :value 1}
                                            :value 0}}}]
      (update base-spec :layer conj r-line-spec r-text-spec))
    ;; If no correlation info, just return base-spec.
    base-spec))

