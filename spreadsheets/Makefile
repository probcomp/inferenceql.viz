current-dir      := $(shell pwd)
output-dir       := $(current-dir)/out
src-dir          := $(current-dir)/src
resource-dir     := $(current-dir)/resources
cache-dir        := $(current-dir)/src/inferdb/spreadsheets
node-modules-dir := $(current-dir)/../node_modules

output-to	 := $(output-dir)/main.js

hot-css-file     := $(node-modules-dir)/handsontable/dist/handsontable.full.css
hot-css-resource := $(resource-dir)/handsontable.full.css

all: js

js: $(output-to)

.PHONY: watch
watch: $(hot-css-resource)
	cd ..; clojure -m cljs.main -w $(src-dir) $(cljs-main-opts)

clean:
	rm -Rf $(output-dir)

### Compilation

compile-opts := $(current-dir)/build.edn
output-to	   := $(output-dir)/main.js
main-ns      := inferdb.spreadsheets.core

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

surge-domain = inferdb-spreadsheet.surge.sh

.PHONY: publish
publish: $(output-to)
	cd ..; bin/publish $(current-dir) $(surge-domain)
