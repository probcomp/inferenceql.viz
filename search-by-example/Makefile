current-dir  := $(shell pwd)
output-dir   := $(current-dir)/out
output-to    := $(output-dir)/main.js
compile-opts := $(current-dir)/build.edn

all: $(output-to)

clean:
	rm -Rf $(output-dir)

$(output-to):
	cd ..; clojure -M -m cljs.main \
		-co $(compile-opts) \
		-d $(output-dir) \
		-o $(output-to) \
		-c inferenceql.search-by-example.api
