# inferdb

[![CircleCI](https://circleci.com/gh/probcomp/inferenceql.svg?style=svg&circle-token=9068882ad6a95c5a982984cc124815fd2499a86d)](https://circleci.com/gh/joshuathayer/inferdb.spreadsheets)

## inferdb.spreadsheets notes

### building PFCA cache

The spreadsheet product requires a prebuilt
probability-for-cluster-assignment cache in order to run. If the data
in `inferdb.spreadsheets.data` changes, the cache must be
rebuilt. `make pfca-cache` will rebuild the cache and write it to the
appropriate location.

### publishing spreadsheet

`make publish` will deploy the spreadsheet to
https://inferdb-spreadsheet.surge.sh/ . For this to work, you'll need
to [install surge]( https://surge.sh/help/getting-started-with-surge),
and to get added to the project- ask someone with access to add you (they
should follow the instructions at
https://surge.sh/help/adding-collaborators).
