spreadsheets-dir      := spreadsheets
node-modules-dir      := node_modules
out-dir               := out

module-dirs := \
	$(spreadsheets-dir) \

.PHONY: all
all:
	for d in $(module-dirs) ; do \
		$(MAKE) -w -C $$d ; \
	done

.PHONY: clean
clean:
	rm -Rf $(node-modules-dir) $(out-dir)
	for d in $(module-dirs) ; do \
		$(MAKE) -w -C $$d clean ; \
	done

yarn-install-opts = --no-progress --frozen-lockfile

node_modules: $(node-modules-dir)

$(node-modules-dir): package.json yarn.lock
	yarn install $(yarn-install-opts)
