(ns inferenceql.viz.components.store.events
  "Contains events related to the store panel."
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(defn ^:event-db datasets
  "Stores datasets.

  Args:
    `datasets` - A map of datasets as follows
      {:dataset-name-1 {...} :dataset-name-2 {...}}
      See store-component db for more detailed specs.

  Triggered when:
    New datasets have been loaded into the app via the upload-panel. "
  [db [_ datasets]]
  (update-in db [:store-component :datasets] merge datasets))
(rf/reg-event-db :store/datasets
                 event-interceptors
                 datasets)

(defn ^:event-db models
  "Stores models.

  Args:
    `models` - A map of models as follows
      {:model-name-1 gpm-1 :model-name-2 gpm-2}
      See store-component db for more detailed specs.

  Triggered when:
    New models have been loaded into the app via the upload-panel. "
  [db [_ models]]
  (update-in db [:store-component :models] merge models))
(rf/reg-event-db :store/models
                 event-interceptors
                 models)

(defn ^:event-db geodata
  "Stores geodata.

  Args:
    `geodata` - A map of geodata as follows
      {:geodata-name-1 {...} :geodata-name-2 {...}}
      See store-component db for more detailed specs.

  Triggered when:
    New geodata has been loaded into the app via the upload-panel. "
  [db [_ geodata]]
  (update-in db [:store-component :geodata] merge geodata))
(rf/reg-event-db :store/geodata
                 event-interceptors
                 geodata)
