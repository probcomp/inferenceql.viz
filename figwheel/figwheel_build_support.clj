(ns figwheel-build-support
  (:require [me.raynes.fs :as fs]
            [clojure.java.shell :refer [sh]]))

(defn get-node-modules
  "Runs make command so node_modules/ folder is setup with js dependencies."
  ;; Receives system configuration info and build options from Figwheel.
  [_sys-config]

  ;; Runs the makefile command.
  (let [ret (sh "make" "node_modules")]
    (println "[Figwheel - Pre Build Hook] Ran 'make node_modules' to setup node_modules/ dir.")
    (println "Output from 'make node_modules':")

    ;; TODO Indent output
    (print (:out ret))
    (println "End of output from 'make node_modules':"))

  ;; TODO Modify the makefile for spreadsheets, so I can call it to do this copy action.
  (sh "cp" "node_modules/handsontable/dist/handsontable.full.css" "spreadsheets/resources/handsontable.full.css"))

(defn copy-static-files
  "Copies static files needed by the spreadsheets app to the target/public/ build directory"
  ;; Receives system configuration info and build options from Figwheel.
  [_sys-config]

  ;; TODO Only copy files if they have changed.

  ;; Copy static JS, CSS, and other files needed by the spreadsheets app.
  (fs/copy-dir "spreadsheets/resources" "target/public/")
  ;; Copy the index.html file for the spreadsheets app.
  (fs/copy "spreadsheets/index.html" "target/public/index.html")

  (println "[Figwheel - Post Build Hook] Copied static files to the target/public/ directory."))
