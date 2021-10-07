(ns inferenceql.viz.model.cgpm
  (:refer-clojure :exclude [import])
  (:require [clojure.walk :refer [keywordize-keys]]
            [medley.core :as medley]
            [inferenceql.viz.model.xcat :as xcat]
            [inferenceql.inference.gpm.crosscat :as crosscat]))

(defn ^:export import
  [cgpm-json data mapping-table schema]
  (let [schema (js->clj schema) ; KVs do not have to be keywords.
        mapping-table (js->clj mapping-table) ; Ks do not have to be keywords.
        ;; Ks do not have to be keywords.
        cgpm-model (-> cgpm-json js->clj keywordize-keys xcat/fix-cgpm-maps)

        ;; Expects a collection of maps with values appropriately cast.
        data (->> (js->clj data)
                  (map #(medley/map-keys keyword %))
                  (zipmap (range)))

        ;; Dummy version of numericalized. This should not ever get used because
        ;; model-model contains our column names.
        numericalized [[]]
        spec (xcat/spec numericalized schema cgpm-model)
        latents (xcat/latents cgpm-model)
        options (xcat/options mapping-table)]
    (crosscat/construct-xcat-from-latents spec latents data {:options options})))
