yarn-install-opts := --no-progress --frozen-lockfile
compile-opts      := build.edn
main-ns           := inferdb.spreadsheets.core
output-dir        := out
chart-namespaces  := simulations-x-y simulations-a simulations-b simulations-c

chart-dir =  $(output-dir)/charts

clean:
	rm -Rf *.png
	rm -f $(output-dir)/charts/*.png
.PHONY: clean

clean-json:
	rm  out/json-results/*.json
.PHONY: clean-json

node_modules: yarn.lock
	yarn install $(yarn-install-opts)

charts: $(chart-namespaces:%=%.png)
	mkdir -p $(chart-dir)
	cp $< $(chart-dir)
	mv *.png $(output-dir)/charts

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
	clojure -m cljs.main --watch src -co $(compile-opts) -d $(output-dir) -c $(main-ns)
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
