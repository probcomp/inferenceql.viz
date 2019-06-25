compile-opts := build.edn
main-ns      := inferdb.spreadsheets.core

yarn-install-opts = --no-progress --frozen-lockfile
surge-domain      = inferdb-spreadsheet.surge.sh

spreadsheet-dir := spreadsheets
output-dir      := $(spreadsheet-dir)/out
resource-dir    := $(spreadsheet-dir)/resources
cache-dir       := $(spreadsheet-dir)/src/inferdb/spreadsheets
chart-dir       := $(output-dir)/charts

output-to        := $(output-dir)/main.js
data-file        := $(cache-dir)/data.cljc
cache-file       := $(cache-dir)/pfcas.cljc
model-file       := $(cache-dir)/model.cljc
vl-json          := $(wildcard $(chart-dir)/*.vl.json)
pngs             := $(vl-json:.vl.json=.png)
hot-css-file     := node_modules/handsontable/dist/handsontable.full.css
hot-css-resource := $(resource-dir)/handsontable.full.css

generated-dirs  := $(output-dir) $(chart-dir)
generated-files := $(output-to) $(data-file) $(model-file) $(cache-file) $(hot-css-resource)
generated       := $(generated-dirs) $(generated-files)

cljs-main-opts := \
		-co $(compile-opts) \
		-d $(output-dir) \
		-o $(output-to) \
		-c $(main-ns)

all: out/main.js

out/main.js:
	clojure -m cljs.main -co api.edn -d out -o out/main.js -c inferdb.search-by-example.api

.PHONY: watch
watch: node_modules $(hot-css-resource) $(model-file) $(data-file) $(cache-file)
	clojure -m cljs.main -w $(spreadsheet-dir)/src $(cljs-main-opts)

.PHONY: clean
clean:
	rm -Rf $(generated)

.PHONY: publish
publish: spreadsheet
	bin/publish $(spreadsheet-dir) $(surge-domain)

node_modules: yarn.lock
	yarn install $(yarn-install-opts)

spreadsheet: node_modules $(hot-css-resource) $(data-file) $(model-file) $(cache-file) $(output-to)

charts: $(chart-dir) $(pngs)

cache: model data $(cache-file)

$(generated-dirs):
	mkdir -p $($@)

$(hot-css-resource): $(hot-css-file)
	cp $(hot-css-file) $(resource-dir)

$(output-to):
	clojure -m cljs.main $(cljs-main-opts)

%.vg.json: %.vl.json node_modules
	yarn run vl2vg $< $@

%.png: %.vg.json node_modules
	yarn run vg2png $< $@

$(model-file):
	bin/build-model $(model-file) model.json

model: model.json $(model-file)

$(data-file):
	bin/build-data $(data-file) data.csv

data: data.csv $(data-file)

$(cache-file): $(data-file)
	bin/build-cache
