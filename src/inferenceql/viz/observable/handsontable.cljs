(ns inferenceql.viz.observable.handsontable
  (:require [re-frame.core :as rf]))

(def default-hot-settings
  {:settings {:data                []
              ;; :colHeaders optional.
              ;; :columns optional.
              ;; :colWidths optional.
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :manualColumnResize  true
              :autoWrapCol         false
              :autoWrapRow         false
              :filters             true
              ;; TODO: investigate more closely what each of
              ;; these options adds. And if they can be put
              ;; in the context-menu instead.
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :outsideClickDeselects false
              :readOnly            true
              :height              "auto"
              :width               "auto"
              :stretchH            "none"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})
