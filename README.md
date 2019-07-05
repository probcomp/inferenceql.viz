# inferdb

[![CircleCI](https://circleci.com/gh/probcomp/inferenceql.svg?style=shield&circle-token=a7fdbf0f271ddb2a6a9798c3a99bdb21c68080c2)](https://circleci.com/gh/probcomp/inferenceql)
![Stability: Experimental](https://img.shields.io/badge/stability-experimental-orange.svg)

## Testing

### Running the tests

Tests can be run either [from the command-line](https://cljdoc.org/d/lambdaisland/kaocha/0.0-418/doc/4-running-kaocha-cli)

```bash
bin/kaocha
```

or [from the REPL](https://cljdoc.org/d/lambdaisland/kaocha/0.0-418/doc/5-running-kaocha-from-the-repl).

```clojure
(require '[kaocha.repl :as kaocha])
(kaocha/run)
```

Kaocha provides a variety of options for running the test suite. For full
details refer to its [documentation](https://cljdoc.org/d/lambdaisland/kaocha/).

### Generating charts

Once you have run the tests you can generate `.png` charts:

```bash
make charts
```

See `inferdb.utils/save-json` for where chart metadata and images are saved.

## inferdb.spreadsheets

### Building the cache

The spreadsheet product requires a pre-built cache cache in order to run. If the
data in `inferdb.spreadsheets.data` changes, the cache must be rebuilt. `make
cache` will rebuild the cache and write it to the appropriate location.

### Publishing

`make publish` will deploy the spreadsheet to
<https://inferdb-spreadsheet.surge.sh/>. For this to work, you'll need
to [install surge](https://surge.sh/help/getting-started-with-surge),
and to get added to the project- ask someone with access to add you (they
should follow [the instructions](https://surge.sh/help/adding-collaborators)).
