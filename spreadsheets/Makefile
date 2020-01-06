current-dir      := $(shell pwd)
output-dir       := $(current-dir)/out
src-dir          := $(current-dir)/src
resource-dir     := $(current-dir)/resources
cache-dir        := $(current-dir)/src/inferenceql/spreadsheets
node-modules-dir := $(current-dir)/../node_modules

output-to	 := $(output-dir)/main.js

hot-css-file     := $(node-modules-dir)/handsontable/dist/handsontable.full.css
hot-css-resource := $(resource-dir)/handsontable.full.css

### Definitions for Figwheel.

figwheel-build-dir := $(current-dir)/figwheel-target/public
figwheel-resource-dir := $(figwheel-build-dir)/resources
figwheel-index-file := $(figwheel-build-dir)/index.html

all: js

js: $(output-to)

.PHONY: watch
watch: $(hot-css-resource)
	cd ..; clojure -m cljs.main -w $(src-dir) $(cljs-main-opts)

clean:
	rm -Rf $(output-dir)
	rm -Rf $(figwheel-build-dir)

### Compilation

compile-opts := $(current-dir)/build.edn
output-to	   := $(output-dir)/main.js
main-ns      := inferenceql.spreadsheets.core

cljs-main-opts := \
		-co $(compile-opts) \
		-d $(output-dir) \
		-o $(output-to) \
		-c $(main-ns)

$(output-to): $(hot-css-resource)
	cd ..; clojure -m cljs.main $(cljs-main-opts)

$(node-modules-dir):
	cd ..; $(MAKE) node_modules

$(hot-css-file): $(node-modules-dir)

$(hot-css-resource): $(hot-css-file)
	cp $(hot-css-file) $(resource-dir)

### Publishing

surge-domain = inferenceql-spreadsheet.surge.sh

.PHONY: publish
publish: $(output-to)
	cd ..; bin/publish $(current-dir) $(surge-domain)

### Compilation with Figwheel

$(figwheel-build-dir):
	mkdir -p $(figwheel-build-dir)

$(figwheel-index-file): $(figwheel-build-dir)
	## Copy static index.html file.
	cp $(current-dir)/index.html $(figwheel-index-file)

$(figwheel-resource-dir): $(figwheel-build-dir) $(hot-css-resource)
	## Copy static resource files.
	cp -r $(resource-dir) $(figwheel-resource-dir)

.PHONY: figwheel-clean
figwheel-clean:
	rm -Rf $(figwheel-build-dir)

.PHONY: figwheel
## Starts the spreadsheets app using Figwheel.
figwheel: figwheel-clean $(figwheel-resource-dir) $(figwheel-index-file)
	cd .. && clojure -A:figwheel

.PHONY: figwheel-10x
## Starts the spreadsheets app using Figwheel and Re-frame-10x.
figwheel-10x: figwheel-clean $(figwheel-resource-dir) $(figwheel-index-file)
	cd .. && clojure -A:figwheel:10x
