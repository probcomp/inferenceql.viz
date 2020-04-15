(ns inferenceql.spreadsheets.panels.viz.tree)

(defn spec [tree]
  {:description
   "An example of Cartesian layouts for a node-link diagram of hierarchical data.",
   :width 700,
   :height 600,
   :title {:text "Infection Tree"}
   :padding 25,
   :config { :title { :fontSize 16}}
   :scales
   [{:name "color",
     :type "linear",
     :range {:scheme "magma"},
     :domain {:data "tree", :field "depth"},
     :zero true}],
   :marks
   [{:type "path",
     :from {:data "links"},
     :encode {:update {:path {:field "path"}, :stroke {:value "#ccc"}}}}
    {:type "symbol",
     :from {:data "tree"},
     :encode
     {:enter {:size {:value 100}, :stroke {:value "#fff"}},
      :update
      {:x {:field "x"},
       :y {:field "y"},
       :fill {:scale "color", :field "depth"}}}}
    {:type "text",
     :from {:data "tree"},
     :encode
     {:enter
      {:text {:field "name"},
       :fontSize {:value 9},
       :baseline {:value "middle"}},
      :update
      {:x {:field "x"},
       :y {:field "y"},
       :dx {:signal "datum.children ? -7 : 7"},
       :align {:signal "datum.children ? 'right' : 'left'"},
       :opacity {:signal "labels ? 1 : 0"}}}}],
   :$schema "https://vega.github.io/schema/vega/v5.json",
   :signals
   [{:name "labels", :value true,}
    {:name "layout",
     :value "tidy",}
    {:name "links",
     :value "diagonal",}
    {:name "separation", :value false}] 
   :data
   [{:name "tree",
     :values tree
     :transform
     [{:type "stratify", :key "id", :parentKey "parent"}
      {:type "tree",
       :method {:signal "layout"},
       :size [{:signal "height"} {:signal "width - 100"}],
       :separation {:signal "separation"},
       :as ["y" "x" "depth" "children"]}]}
    {:name "links",
     :source "tree",
     :transform
     [{:type "treelinks"}
      {:type "linkpath",
       :orient "horizontal",
       :shape {:signal "links"}}]}]})
