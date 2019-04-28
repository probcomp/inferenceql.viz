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
