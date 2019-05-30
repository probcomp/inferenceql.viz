yarn-install-opts := --no-progress --frozen-lockfile
compile-opts      := build.edn
main-ns           := inferdb.spreadsheets.core
spreadsheet-dir   := spreadsheets
output-dir        := $(spreadsheet-dir)/out
output-to         := $(output-dir)/main.js
output-resource-dir := $(spreadsheet-dir)/resources
chart-namespaces  := select-simulate
surge-domain      := inferdb-spreadsheet.surge.sh


chart-dir =  $(output-dir)/charts

$(output-resource-dir):
	mkdir -p $(output-resource-dir)

$(output-resource-dir)/handsontable.full.css: $(output-resource-dir)
	cp node_modules/handsontable/dist/handsontable.full.css $(output-resource-dir)/

spreadsheet: node_modules $(output-dir)/main.js $(output-resource-dir)/handsontable.full.css

$(output-dir)/main.js:
	clojure -m cljs.main -co $(compile-opts) -d $(output-dir) \
	--output-to $(output-to) -c $(main-ns)

watch: node_modules $(output-resource-dir)/handsontable.full.css
	clojure -m cljs.main --watch spreadsheets/src -co $(compile-opts) \
	-d $(output-dir) --output-to $(output-to) -c $(main-ns)
.PHONY: watch

clean:
	rm -Rf $(output-dir)
	rm -Rf *.png
	rm -f $(output-resource-dir)/handsontable.full.css
.PHONY: clean

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

pfca-cache:
	clojure -m inferdb.spreadsheets.build-pfcas
	mv pfcas.cljc src/inferdb/spreadsheets/pfcas.cljc
.PHONY: pfca_cache

publish: spreadsheet
	bin/publish $(spreadsheet-dir) $(surge-domain)
.PHONY: publish
