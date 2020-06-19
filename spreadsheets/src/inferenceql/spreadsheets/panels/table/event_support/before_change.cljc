(ns inferenceql.spreadsheets.panels.table.event-support.before-change
  "Supporting functions for the :hot/before-change event"
  (:require [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm.multimixture.specification :as mm.spec]
            [medley.core :as medley]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [goog.string :as gstring]))

(s/def ::row-id ::db/row-id)
(s/def ::row-data ::db/row)
(s/def ::col ::db/header)
(s/def ::new-val any?)
(s/def ::valid boolean?)
(s/def ::error string?)

(s/def ::change-map (s/keys :req-un [::row-id ::row-data ::col ::new-val]))
(s/def ::validation-and-cast (s/keys :req-un [::valid]
                                     :opt-un [::error ::new-val]))
(s/def ::change-maps (s/coll-of ::change-map))
(s/def ::change-maps-returned (s/coll-of (s/merge ::change-map ::validation-and-cast)))

(defn assert-permitted-changes
  "Asserts that `changes` only occur in cells and via sources allowed by our Handsontable settings."
  [changes source]
  (let [valid-change-sources #{"edit" "CopyPaste.paste" "Autofill.fill"}]

    ;; Changes should only be happening in the label column or in user-added rows.
    ;; This should be enforced by Hansontable settings.
    (doseq [{:keys [col row-data]} changes]
      (assert (or (= :inferenceql.viz.row/label__ col)
                  (:inferenceql.viz.row/user-added-row__ row-data))))

    ;; Changes should only be the result of user edits, copy paste, or drag and autofill.
    ;; This should be enforced by Hansontable settings.
    (assert (valid-change-sources source))))
(s/fdef assert-permitted-changes :args (s/cat :changes ::change-maps :source string?))
