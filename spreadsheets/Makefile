current-dir      := $(shell pwd)
output-dir       := $(current-dir)/out
src-dir          := $(current-dir)/src
resource-dir     := $(current-dir)/resources
cache-dir        := $(current-dir)/src/inferenceql/spreadsheets
node-modules-dir := $(current-dir)/../node_modules

output-to	 := $(output-dir)/main.js

hot-css-file     := $(node-modules-dir)/handsontable/dist/handsontable.full.css
hot-css-resource := $(resource-dir)/handsontable.full.css

model-edn-file := $(resource-dir)/model.edn

### Definitions for publishing.

publish-dir := $(current-dir)/.publish
auth-file := $(current-dir)/AUTH
cname-file := $(current-dir)/CNAME

### Definitions for Figwheel.

figwheel-build-dir := $(current-dir)/figwheel-target
figwheel-public-dir := $(current-dir)/figwheel-target/public
figwheel-resource-dir := $(figwheel-public-dir)/resources
figwheel-index-file := $(figwheel-public-dir)/index.html

all: js

$(model-edn-file):
	cd ..; bin/build-model-spec

js: $(output-to)

.PHONY: watch
watch: $(hot-css-resource)
	cd ..; clojure -m cljs.main -w $(src-dir) $(cljs-main-opts)

clean:
	rm -Rf $(output-dir)
	rm -Rf $(figwheel-build-dir)
	rm -Rf $(publish-dir)

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

.PHONY: publish-dir
publish-dir: $(model-edn-file) js $(publish-dir)

$(publish-dir):
	mkdir -p $(publish-dir)
 	## Copy static index.html file.
	cp $(current-dir)/index.html $(publish-dir)
	## Copy static resource files.
	cp -r $(resource-dir) $(publish-dir)
	## Copy compiled js files.
	cp -r $(output-dir) $(publish-dir)
	## Copy AUTH file if it exists.
	-[ -f $(auth-file) ] && cp $(auth-file) $(publish-dir)
	## Copy CNAME file if it exists.
	-[ -f $(cname-file) ] && cp $(cname-file) $(publish-dir)
	## Prevent ignore of node-modules dir in surge publish.
	echo "!node_modules" > $(publish-dir)/.surgeignore

## This target can be called like `make publish DOMAIN=probcomp.surge.sh`
.PHONY: publish
publish: publish-dir
	cd ..; bin/publish $(DOMAIN)

### Compilation with Figwheel

$(figwheel-public-dir):
	mkdir -p $(figwheel-public-dir)

$(figwheel-index-file): $(figwheel-public-dir)
	## Copy static index.html file.
	cp $(current-dir)/index.html $(figwheel-index-file)

$(figwheel-resource-dir): $(figwheel-public-dir) $(hot-css-resource)
	## Copy static resource files.
	cp -r $(resource-dir) $(figwheel-resource-dir)

# Deletes the entire figwheel build directory and also the model file.
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
# The mostly likely files to have changed are CSS files that have been
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
