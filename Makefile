compile-opts := build.edn
main-ns      := inferdb.spreadsheets.core
output-dir   := out

test: cljtest
.PHONY: test

cljtest:
	clojure -Atest
.PHONY: cljtest


clean:
	rm -R $(output-dir)
.PHONY: clean

node_modules: yarn.lock
	yarn install

pfca-cache:
	clj -m inferdb.spreadsheets.build-pfcas
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
