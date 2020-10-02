(ns inferenceql.spreadsheets.panels.modal.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [modal-panel]]))

(defn modal
  "A reagant component for displaying content in a modal-dialog.

  Args:
    `content` - Hiccup to be displayed as the content of the model."
  [content]
  (when content
   [modal-panel
    :backdrop-color   "grey"
    :backdrop-opacity 0.7
    :child content
    :wrap-nicely? false
    :backdrop-on-click #(rf/dispatch [:modal/clear])]))
