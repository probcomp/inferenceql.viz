(ns inferenceql.spreadsheets.panels.viz.circle)

(defn tree [node-names]
  (let [root-id -1
        root-node {:name "root" :id root-id :alpha 0.5 :beta 0}

        num-other-nodes (count node-names)

        other-nodes-ids (map inc (range num-other-nodes))
        other-nodes-locs
        #_(if (> num-other-nodes 10)
              (map #(* (/ 1 num-other-nodes)
                       (- % 0.5))
                   other-nodes-ids)
              (map #(* (/ 1 (dec num-other-nodes))
                       (- % 1))
                   other-nodes-ids))
        (map #(* (/ 1 num-other-nodes)
                 (- % 0.5))
             other-nodes-ids)

        other-nodes (for [[name id loc] (map vector node-names other-nodes-ids other-nodes-locs)]
                      {:name name :id id :parent root-id :alpha loc :beta 1 :status nil})]

    (concat [root-node] other-nodes)))

(defn dependencies [dependencies]
  (for [d dependencies]
    (assoc d :edge-present true :infected nil)))

(defn spec [tree dependencies extent rotate]
  {:legends [],
   :width 700,
   :height 500,
   :padding 10,

   :scales [{:name "color",
             :type "ordinal",
             :domain ["depends on" "imported by"],
             :range [{:signal "colorIn"} {:signal "colorOut"}]}],

   :marks
   [{:type "text",
     :interactive true
     :from {:data "leaves"},
     :encode
     {:enter {:text {:field "name"},
              :baseline {:value "middle"}},
      :update
      {:x {:field "x"},
       :y {:field "y"},
       :limit {:value 140}
       :dx {:signal "textOffset * (datum.leftside ? -1 : 1)"},
       :angle
       {:signal "datum.leftside ? datum.angle - 180 : datum.angle"},
       :align {:signal "datum.leftside ? 'right' : 'left'"},
       :fontSize {:signal "textSize"},
       :fontWeight
       [{:test "indata('selected', 'source-id', datum.id)",
         :value "bold"}
        {:test "indata('selected', 'target-id', datum.id)",
         :value "bold"}
        {:value nil}],
       :fill
       [{:test "datum.id === active", :value "black"}
        {:test "indata('selected', 'source-id', datum.id)",
         :signal "colorIn"}
        {:test "indata('selected', 'target-id', datum.id)",
         :signal "colorOut"}
        {:test "datum.status == 'infected'",
         :value "orange"}
        {:test "datum.status == 'source'",
         :value "red"}
        {:value "black"}]}}}

    {:type "group",
     :from {:facet {:name "path", :data "dependencies", :field "treepath"}},
     :signals [{:name "edgeClicked",
                :push "outer",
                :on [{:events "line:click", :update "parent"}]}],
     :marks
     [{:type "line",
       :interactive true,
       :from {:data "path"},
       :encode
       {:enter {:interpolate {:value "bundle"}, :strokeWidth {:value 3}},
        :update
        {:stroke
         [{:test
           "parent['source-id'] === activeSource || parent['target-id'] === activeTarget",
           :signal "colorOut"}
          {:test "parent['infected']",
           :value "red"}
          {:value "steelblue"},]
         :strokeOpacity
         [{:test
           "parent['source-id'] === activeSource || parent['target-id'] === activeTarget",
           :value 0.5}
          {:value 0.2}],
         :tension {:signal "tension"},
         :x {:field "x"},
         :y {:field "y"}}}}]}],

   :$schema "https://vega.github.io/schema/vega/v5.json",

   :signals
   [{:name "tension", :value 0.33}
    {:name "radius", :value 200}
    {:name "extent", :value extent}
    {:name "rotate", :value rotate}
    {:name "textSize", :value 12}
    {:name "textOffset", :value 2}
    {:name "layout", :value "cluster"}
    {:name "colorIn", :value "firebrick"}
    {:name "colorOut", :value "firebrick"}
    {:name "originX", :update "width / 2"}
    {:name "originY", :update "height / 2"}
    {:name "active",
     :value nil,
     :on
     [{:events "text:mouseover", :update "datum.id"}
      {:events "mouseover[!event.item]", :update "null"}]}
    {:name "activeSource",
     :value nil,
     :update
     "active"}
    {:name "activeTarget",
     :value nil,
     :update
     "active"}
    {:name "edgeClicked"}
    {:name "edgeThreshold"}
    {:name "confidenceThreshold"}],

   :data
   [{:name "tree",
     :values tree
     :transform
     [{:type "stratify", :key "id", :parentKey "parent"}
      {:type "tree",
       :method {:signal "layout"},
       :size [1 1],
       :as ["alpha-ignore" "beta-ignore" "depth" "children"]}
      {:type "formula",
       :expr "(rotate + extent * datum.alpha + 270) % 360",
       :as "angle"}
      {:type "formula",
       :expr "inrange(datum.angle, [90, 270])",
       :as "leftside"}
      {:type "formula",
       :expr
       "originX + radius * datum.beta * cos(PI * datum.angle / 180)",
       :as "x"}
      {:type "formula",
       :expr
       "originY + radius * datum.beta * sin(PI * datum.angle / 180)",
       :as "y"}]}

    {:name "leaves",
     :source "tree",
     :transform [{:type "filter", :expr "!datum.children"}]}

    {:name "dependencies",
     :values dependencies
     :transform
     [{:type "formula",
       :expr "treePath('tree', datum['source-id'], datum['target-id'])",
       :as "treepath",
       :initonly true}]}

    {:name "selected",
     :source "dependencies",
     :transform
     [{:type "filter",
       :expr
       "datum['source-id'] === activeSource || datum['target-id'] === activeTarget"}]}]})
