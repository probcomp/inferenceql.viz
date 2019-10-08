current-dir  := $(shell pwd)
output-dir   := $(current-dir)/out
output-to    := $(output-dir)/main.js
compile-opts := $(current-dir)/build.edn

all: $(output-to)

clean:
	rm -Rf $(output-dir)

$(output-to):
	cd ..; clojure -m cljs.main \
		-co $(compile-opts) \
		-d $(output-dir) \
		-o $(output-to) \
		-c inferdb.search-by-example.api
