export

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

MAINOPTS = -co $(COPTS) -d $(OUTDIR) -c $(MAINNS)
CLJFLAGS = -m cljs.main $(MAINOPTS)

JSOUT  := main.js
CSSOUT := styles.css
SYMLINK = $(CURDIR)/$(DISTDIR)/$(JSOUT)

test:
	echo "$(HOTDISTDIR)"

prod: prod-env $(DISTDIR) build
	cp $(JSFILE) $(DISTDIR)

prod-env:
COPTS    = env/prod/build.edn
CSSFILE  = $(HOTDISTDIR)/handsontable.full.min.css

dev-env:
COPTS   = env/dev/build.edn
CSSFILE = $(HOTDISTDIR)/handsontable.full.css

watch-env:
CLJFLAGS += -watch $(SRCDIR)

watch: dev-env watch-env build $(SYMLINK)

all: dist

build: $(JSFILE)

prod-dist: prod dist

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
