(ns inferenceql.spreadsheets.panels.upload.effects
  (:require [re-frame.core :as rf]
            [cljs.core.async :as async :refer [go put! <!]]
            [clojure.string :as str]
            [ajax.core]
            [ajax.edn]
            [clojure.edn :as edn]))

(defn model-type
  "Returns a model-type keyword depending on the filename for the model.

  The filename itself can be pulled from a Javascript file object, url string, or a
  filename string.

  Args:
    `type` - A keyword for the type of `obj`
    `obj` - An obj from which to pull a filename."
  [type obj]
  (let [filename (case type
                   :filename obj
                   :url (last (str/split obj #"/"))
                   :file (.-name obj)
                   nil)
        file-ext (when filename (last (str/split filename #"\.")))]
    (case file-ext
      "edn" :multimix
      "json" :bayes-db-export
       nil)))

(defmulti put-read
  "Reads data given a read-map and puts the completed read-map onto `chan`.

    Args:
      `read` - A read-map which specifies a file object or url to read.
      `chan` - A core.async channel to put the completed read-map."
  (fn [read chan] (:read-type read)))

(defmethod put-read :url [read chan]
  (ajax.core/ajax-request
   {:uri (:target read)
    :method :get
    :handler (fn [[status data]]
               ;; :raw-data may be file data or failure info.
               (put! chan (assoc read :success status :raw-data data)))
    :response-format (ajax.core/text-response-format)}))

(defmethod put-read :file [read chan]
  (let [rdr (js/FileReader.)
        on-load (fn [_]
                  (this-as this
                    (let [contents (.-result this)]
                      (put! chan (assoc read :success true :raw-data contents)))))
        on-error (fn [error]
                   (this-as this
                     (put! chan (assoc read :success false :raw-data error))))]
    (set! (.-onload rdr) on-load)
    (set! (.-onerror rdr) on-error)
    (.readAsText rdr (:target read))))

(defn take-reads
  "Takes a number of file reads from a channel and dispatches a success event with the reads.

  If any of the reads has failed, the failure event is dispatched with an error message.

  Args:
    `file-reads` -- (core.async channel)
    `num-reads` -- (int) number of items to take from `file-reads`.
    `on-success` -- (vector) a reframe event vector for dispatching on success.
    `on-failure` -- (vector) a reframe event vector for dispatching on failure."
  [file-reads num-reads on-success on-failure]
  (let [file-reads-batched (async/into [] (async/take num-reads file-reads))]
    (go
     (let [reads (<! file-reads-batched)]
       (if (every? :success reads)
         (rf/dispatch (conj on-success reads))
         (let [failures (remove :success reads)
               failure-messages (for [{:keys [read-type target] :as failed-read} failures]
                                  (case read-type
                                    :file (str "Failed reading local file " (.-name target))
                                    :url (str "Failed reading url " target)
                                    :else (str "Failed reading the item with the follow read-map " failed-read)))]
           (rf/dispatch (conj on-failure (str/join "\n" failure-messages)))))))))

