multimixture-dir      := multimixture
search-by-example-dir := search-by-example
spreadsheets-dir      := spreadsheets
node-modules-dir      := node_modules

module-dirs := \
	$(multimixture-dir) \
	$(spreadsheets-dir) \
	$(search-by-example-dir)

.PHONY: all
all:
	for d in $(module-dirs) ; do \
		$(MAKE) -w -C $$d ; \
	done

.PHONY: clean
clean:
	rm -Rf $(node-modules-dir)
	for d in $(module-dirs) ; do \
		$(MAKE) -w -C $$d clean ; \
	done

yarn-install-opts = --no-progress --frozen-lockfile

node_modules: $(node-modules-dir)

$(node-modules-dir): package.json yarn.lock
	yarn install $(yarn-install-opts)
