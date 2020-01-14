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

figwheel-build-dir := $(current-dir)/figwheel-target
figwheel-public-dir := $(current-dir)/figwheel-target/public
figwheel-resource-dir := $(figwheel-public-dir)/resources
figwheel-index-file := $(figwheel-public-dir)/index.html

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

$(figwheel-public-dir):
	mkdir -p $(figwheel-public-dir)

$(figwheel-index-file): $(figwheel-public-dir)
	## Copy static index.html file.
	cp $(current-dir)/index.html $(figwheel-index-file)

$(figwheel-resource-dir): $(figwheel-public-dir) $(hot-css-resource)
	## Copy static resource files.
	cp -r $(resource-dir) $(figwheel-resource-dir)

# Deletes the entire figwheel build directory.
.PHONY: figwheel-clean
figwheel-clean:
	rm -Rf $(figwheel-build-dir)

# Deletes the static files in the figwheel build directory.
.PHONY: figwheel-clean-static
figwheel-clean-static:
	rm -Rf $(figwheel-resource-dir)
	rm -Rf $(figwheel-index-file)

# Deletes the static files in the figwheel build directory and
# copies them over again. This is useful for updating static files
# that have changed while an instance of figwheel is running.
# The mostly likely files to have changed are CSS files that have
# recently edited.
.PHONY: figwheel-static
figwheel-static: figwheel-clean-static $(figwheel-public-dir) $(figwheel-resource-dir) $(figwheel-index-file)

## Starts the spreadsheets app using Figwheel.
.PHONY: figwheel
figwheel: figwheel-clean $(figwheel-resource-dir) $(figwheel-index-file)
	cd .. && clojure -A:figwheel

## Starts the spreadsheets app using Figwheel and Re-frame-10x.
.PHONY: figwheel-10x
figwheel-10x: figwheel-clean $(figwheel-resource-dir) $(figwheel-index-file)
	cd .. && clojure -A:figwheel:10x
