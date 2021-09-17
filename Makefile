current-dir      := $(shell pwd)
output-dir       := $(current-dir)/out
output-dir-worker:= $(current-dir)/out-worker
src-dir          := $(current-dir)/src
resource-dir     := $(current-dir)/resources
cache-dir        := $(current-dir)/src/inferenceql/spreadsheets
node-modules-dir := $(current-dir)/node_modules

output-to	 := $(output-dir)/main.js

hot-css-file     := $(node-modules-dir)/handsontable/dist/handsontable.full.css
hot-css-resource := $(resource-dir)/css/handsontable.full.css
transitions-js := $(resource-dir)/transitions.js
transitions-json := $(resource-dir)/transitions.json

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

clean:
	rm -Rf $(output-dir)
	rm -Rf $(output-dir-worker)
	rm -Rf $(figwheel-build-dir)
	rm -Rf $(publish-dir)
	rm -Rf $(node-modules-dir)
	rm -Rf $(transitions-js)

### Spreadsheets app compilation.

compile-opts := $(current-dir)/compiler_options/app/build.edn
compile-opts-advn := $(current-dir)/compiler_options/app/build-advanced.edn
compile-opts-advn-min := $(current-dir)/compiler_options/app/build-advanced-min.edn

.PHONY: watch
watch: $(hot-css-resource)
	clojure -M -m cljs.main -w $(src-dir) -co $(compile-opts) -c inferenceql.viz.core

.PHONY: watch-advanced
watch-advanced: $(hot-css-resource)
	clojure -M -m cljs.main -w $(src-dir) -co $(compile-opts-advn) -c inferenceql.viz.core

.PHONY: watch-advanced-min
watch-advanced-min: $(hot-css-resource)
	clojure -M -m cljs.main -w $(src-dir) -co $(compile-opts-advn-min) -c inferenceql.viz.core

.PHONY: js
js: $(hot-css-resource) $(transitions-js)
	clojure -J-Xmx4G -M -m cljs.main -co $(compile-opts) -c inferenceql.viz.core

.PHONY: js-advanced
js-advanced: $(hot-css-resource) $(transitions-js)
	clojure -J-Xmx4G -M -m cljs.main -co $(compile-opts-advn) -c inferenceql.viz.core

.PHONY: js-advanced-min
js-advanced-min: $(hot-css-resource) $(transitions-js)
	clojure -J-Xmx4G -M -m cljs.main -co $(compile-opts-advn-min) -c inferenceql.viz.core

### Observable components compilation.

observable-compile-opts := $(current-dir)/compiler_options/observable/build-advanced.edn
observable-worker-compile-opts := $(current-dir)/compiler_options/observable/build-advanced-worker.edn

.PHONY: watch-observable
watch-observable: $(hot-css-resource)
	clojure -M -m cljs.main -w $(src-dir) -co $(observable-compile-opts) -c inferenceql.viz.js.observable.notebook

.PHONY: observable
observable: $(hot-css-resource)
	## Compile js-bundle for notebooks.
	clojure -M -m cljs.main -co $(observable-compile-opts) -c inferenceql.viz.js.observable.notebook

.PHONY: observable-worker
observable-worker:
	## Compile js-bundle for web-workers.
	clojure -M -m cljs.main -co $(observable-worker-compile-opts) -c inferenceql.viz.js.observable.worker

.PHONY: observable-all
observable-all: observable observable-worker

### Supporting defs for compilation.

yarn-install-opts = --no-progress --frozen-lockfile

$(node-modules-dir): package.json yarn.lock
	yarn install $(yarn-install-opts)

$(hot-css-file): $(node-modules-dir)

$(hot-css-resource): $(hot-css-file)
	## This copies the Handsontable CSS file from the Handsontable NPM dependency to the
	## app's css folder.
	cp $(hot-css-file) $(resource-dir)/css

$(transitions-js): $(transitions-json)
	bin/js-ify-transitions $(transitions-json) $(transitions-js)

### Publishing

.PHONY: publish-dir
publish-dir: js $(publish-dir)

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
	bin/publish $(DOMAIN)

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
	rm -Rf $(node-modules-dir)

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

figwheel-compile-opts := $(current-dir)/compiler_options/figwheel/build.edn
reframe-10x-compile-opts := $(current-dir)/compiler_options/reframe-10x/support.edn

## Starts the spreadsheets app using Figwheel.
.PHONY: figwheel
figwheel: figwheel-clean $(figwheel-resource-dir) $(figwheel-index-file)
	clojure -J-Xmx4G -A:figwheel -M -m figwheel.main \
	-co  $(figwheel-compile-opts) \
	-c inferenceql.viz.core --repl

## Starts the spreadsheets app using Figwheel and Re-frame-10x.
.PHONY: figwheel-10x
figwheel-10x: figwheel-clean $(figwheel-resource-dir) $(figwheel-index-file)
	clojure -J-Xmx4G -A:figwheel:reframe-10x -M -m figwheel.main \
	-co $(figwheel-compile-opts):$(reframe-10x-compile-opts) \
	-c inferenceql.viz.core --repl
