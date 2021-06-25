(ns inferenceql.viz.panels.more.views
  "Reagent components related to displaying a more icon and menu."
  (:require [re-frame.core :as rf]
            [re-com.core :refer [button box h-box v-box gap]]
            [re-com.popover :refer [popover-anchor-wrapper popover-content-wrapper]]
            [inferenceql.viz.panels.jsmodel.views :as jsmodel.views]
            [inferenceql.viz.panels.upload.views :as upload.views]))

(def more-icon-inlined
  "This is an inlined version of the icon located at resources/icons/more_vert-24px.svg
  Inlining the icon allows for more easily inlined the entire iql.viz app into a single html file."

  "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg'
  height='24' viewBox='0 0 24 24' width='24'%3E%3Cstyle type='text/css'
  %3E%3C!%5BCDATA%5B .more-icon-dots %7B fill: %23AAA; %7D
  %5D%5D%3E%3C/style%3E%3Cpath d='M0 0h24v24H0z' fill='none'/%3E%3Cpath
  class='more-icon-dots' d='M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9
  2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2
  .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z'/%3E%3C/svg%3E")

(defn more-button
  "An icon that when clicked will open the more menu.

  Args:
    `pressed` - A boolean of whether the more menu is open or not. Different styling
      will be applied to the button when it is open.

  Returns:
    a reagent component."
  [pressed]
  [:button.toolbar-button.pure-button.more-button
   {:class (when pressed "pure-button-active pure-button-hover")
    :on-click (fn [e]
                (rf/dispatch [:more/toggle-show-menu])
                (.blur (.-target e)))}
   [:object.more-icon {:type "image/svg+xml"
                       :data more-icon-inlined}
    "More icon"]])

(defn menu-item
  "A single item in a menu that when clicked will perform an action and close the menu.

  Args:
    `text` - A string of text to display as the menu option.
    `action`- A function to run when the menu item is clicked.

  Returns:
    a reagent component."
  [text action]
  (let [on-click (fn []
                   (rf/dispatch [:more/set-show-menu false])
                   (action))]
    [box :class "more-menu-item no-select"
         :child text
         :attr {:on-click on-click}]))

(defn menu-body
  "A more menu that contains various menu items.

  Returns:
    a reagent component."
  []
  [v-box
   :class "more-menu"
   :children [[gap :size "10px"]
              [menu-item "Show JS model" #(rf/dispatch
                                           [:modal/set-content [jsmodel.views/display]])]
              [menu-item "Change data and model" #(rf/dispatch
                                                   [:modal/set-content [upload.views/panel-contents]])]
              [gap :size "10px"]]])

(defn menu
  "A more icon that when clicked reveals a more-menu.

  Args:
    `show-menu` - A boolean that determines whether the more menu body is shown or not.

  Returns:
    a reagent component."
  [show-menu]
  [popover-anchor-wrapper
   ;; This z-index is chosen so that the background overlay and menu is drawn on top
   ;; of all Handsontable DOM elements.
   :style {:z-index "200"}
   :showing? show-menu
   :position :below-center
   :anchor [more-button @show-menu]
   :popover  [popover-content-wrapper
              :padding "0px"
              :arrow-length 0
              :arrow-gap 0
              :arrow-width 0
              :on-cancel #(rf/dispatch [:more/set-show-menu false])
              :body [menu-body]]])
