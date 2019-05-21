yarn-install-opts := --no-progress --frozen-lockfile
compile-opts      := build.edn
main-ns           := inferdb.spreadsheets.core
output-dir        := out
output-to         := out/main.js
chart-namespaces  := select-simulate

chart-dir =  $(output-dir)/charts

clean:
	rm -Rf $(output-dir)
	rm -Rf *.png
.PHONY: clean

node_modules: yarn.lock
	yarn install $(yarn-install-opts)

charts: $(chart-namespaces:%=%.png)
	mkdir -p $(chart-dir)
	cp $< $(chart-dir)

%.vl.json:
	clojure -Ctest -m inferdb.charts.$(basename $(basename $@)) > $@

%.vg.json: %.vl.json node_modules
	yarn run vl2vg $< $@

%.png: %.vg.json node_modules
	yarn run vg2png $< $@

pfca-cache:
	clojure -m inferdb.spreadsheets.build-pfcas
	mv pfcas.cljc src/inferdb/spreadsheets/pfcas.cljc
.PHONY: pfca_cache

watch: node_modules
	clojure -m cljs.main --watch spreadsheets/src -co $(compile-opts) -d $(output-dir) --output-to $(output-to) -c $(main-ns)
.PHONY: watch

publish:
	rm -rf publish/out
	rm -rf publish/node_modules
	mkdir -p publish/node_modules/handsontable/dist/
	cp -r out publish
	cp node_modules/handsontable/dist/handsontable.full.css publish/node_modules/handsontable/dist/
	cp index.html publish
	cp cb_2017_us_cd115_20m-topo.js publish
	cd publish ; surge
.PHONY: publish
