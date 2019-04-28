compile-opts := compile-opts.edn
main-ns      := inferdb.spreadsheets.core
output-dir   := out

clean:
	rm -R $(output-dir)
.PHONY: clean

watch:
	clojure -m cljs.main --watch src -co $(compile-opts) -d $(output-dir) -c $(main-ns)
.PHONY: watch
