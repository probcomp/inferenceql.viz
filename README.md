# inferdb

[![CircleCI](https://circleci.com/gh/probcomp/inferenceql.svg?style=svg&circle-token=a7fdbf0f271ddb2a6a9798c3a99bdb21c68080c2)](https://circleci.com/gh/probcomp/inferenceql)
[![Dependencies status](https://versions.deps.co/probcomp/metaprob/status.svg)](https://versions.deps.co/probcomp/metaprob)

## Testing

### Running the tests

```bash
make test
```

### Generating charts

```bash
make charts
```

## inferdb.spreadsheets

### Building PFCA cache

The spreadsheet product requires a prebuilt probability-for-cluster-assignment
cache in order to run. If the data in `inferdb.spreadsheets.data` changes, the
cache must be rebuilt. `make pfca-cache` will rebuild the cache and write it to
the appropriate location.

### Publishing

`make publish` will deploy the spreadsheet to
<https://inferdb-spreadsheet.surge.sh/>. For this to work, you'll need
to [install surge](https://surge.sh/help/getting-started-with-surge),
and to get added to the project- ask someone with access to add you (they
should follow [the instructions](https://surge.sh/help/adding-collaborators)).
