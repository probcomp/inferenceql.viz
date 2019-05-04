compile-opts := build.edn
main-ns      := inferdb.spreadsheets.core
output-dir   := out

clean:
	rm -R $(output-dir)
.PHONY: clean

node_modules: yarn.lock
	yarn install

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
	cd publish ; surge --domain inferdb-spreadsheet.surge.sh
.PHONY: publish
