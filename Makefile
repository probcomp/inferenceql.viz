MAINNS         := inferdb.spreadsheets.core
NODEMODULESDIR := node_modules
SRCDIR         := src
OUTDIR         := out

HTMLFILE := index.html
JSFILE    = $(OUTDIR)/main.js

PACKAGE  = inferdb.spreadsheets
VERSION  = `date "+%Y.%m.%d"`
DISTDIR  = $(PACKAGE)-$(VERSION)
DISTFILE = $(DISTDIR).tar.gz

COPTS      := env/dev/build.edn
HOTDISTDIR  = $(NODEMODULESDIR)/handsontable/dist/

test:
	echo "$(HOTDISTDIR)"

prod: COPTS   ?= env/prod/build.edn
prod: CSSFILE  = $(HOTDISTDIR)/handsontable.full.min.css
prod: build
	cp $(JSFILE) $(DISTDIR)

dev: COPTS   ?= env/dev/build.edn
dev: CSSFILE  = $(HOTDISTDIR)/handsontable.full.css
dev: $(SYMLINK)

watch: CLJFLAGS += -watch $(SRCDIR)
watch: build

MAINOPTS = -co $(COPTS) -d $(OUTDIR) -c $(MAINNS)
CLJFLAGS = -m cljs.main $(MAINOPTS)

JSOUT  := main.js
CSSOUT := styles.css
SYMLINK = $(CURDIR)/$(DISTDIR)/$(JSOUT)

all: dist

build: $(JSFILE)

dist: build $(DISTDIR) $(HTMLFILE) $(CSSFILE)
	cp $(HTMLFILE) $(DISTDIR)
	echo "css file is $(CSSFILE)"
	cp $(CSSFILE) $(DISTDIR)/$(CSSOUT)
	tar -cvzf $(DISTFILE) $(DISTDIR)

$(JSFILE): $(NODEMODULESDIR)
	clojure $(CLJFLAGS)

$(DISTDIR):
	mkdir -p $(DISTDIR)

$(SYMLINK): $(DISTDIR)
	ln -s $(JSFILE) $(SYMLINK)

$(NODEMODULESDIR): yarn.lock
	yarn install

clean: mostlyclean
	rm -Rf $(NODEMODULESDIR) $(DISTDIR) $(DISTFILE)
.PHONY: clean

mostlyclean:
	rm -Rf $(OUTDIR)
.PHONY: mostlyclean
