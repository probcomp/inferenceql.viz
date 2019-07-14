current-dir  := $(shell pwd)
output-dir   := $(current-dir)/out
vl-json      := $(wildcard $(output-dir)/*.vl.json)
pngs         := $(vl-json:.vl.json=.png)

all: charts

charts: $(output-dir) $(pngs)

$(output-dir):
	mkdir -p $(output-dir)

node_modules:
	cd ..; $(MAKE) node_modules

%.vg.json: %.vl.json node_modules
	cd ..; yarn run vl2vg $< $@

%.png: %.vg.json node_modules
	cd ..; yarn run vg2png $< $@

clean:
	rm -Rf $(output-dir)
