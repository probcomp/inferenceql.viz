compile-opts = build.edn
main-ns      = inferdb.spreadsheets.core

yarn-install-opts = --no-progress --frozen-lockfile
chart-namespaces  = select-simulate
surge-domain      = inferdb-spreadsheet.surge.sh

spreadsheet-dir := spreadsheets
output-dir      := $(spreadsheet-dir)/out
chart-dir       := $(output-dir)/charts
cache-dir       := $(spreadsheet-dir)/src/inferdb/spreadsheets

data-file  := $(cache-dir)/data.cljc
cache-file := $(cache-dir)/pfcas.cljc

output-to           := $(output-dir)/main.js
output-resource-dir := $(spreadsheet-dir)/resources

$(output-resource-dir):
	mkdir -p $(output-resource-dir)

$(output-resource-dir)/handsontable.full.css: $(output-resource-dir)
	cp node_modules/handsontable/dist/handsontable.full.css $(output-resource-dir)/

spreadsheet: node_modules $(output-dir)/main.js $(output-resource-dir)/handsontable.full.css

$(output-dir)/main.js:
	clojure -m cljs.main -co $(compile-opts) -d $(output-dir) \
	--output-to $(output-to) -c $(main-ns)

.PHONY: watch
watch:
	clojure -m cljs.main --watch spreadsheets/src -co $(compile-opts) \
	-d $(output-dir) --output-to $(output-to) -c $(main-ns)

.PHONY: clean
clean:
	rm -Rf $(output-dir)
	rm -f $(cache-file)
	rm -Rf *.png
	rm -f $(output-resource-dir)/handsontable.full.css

node_modules: yarn.lock
	yarn install $(yarn-install-opts)

charts: $(chart-namespaces:%=%.png)
	mkdir -p $(chart-dir)
	cp $< $(chart-dir)

%.vl.json:
	clojure -Ctest -Rtest -m inferdb.charts.$(basename $(basename $@)) > $@

%.vg.json: %.vl.json node_modules
	yarn run vl2vg $< $@

%.png: %.vg.json node_modules
	yarn run vg2png $< $@

$(cache-file): $(data-file)
	bin/build-cache

cache: $(cache-file)

.PHONY: publish
publish: spreadsheet
	bin/publish $(spreadsheet-dir) $(surge-domain)
