// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.spreadsheets.search');
goog.require('cljs.core');
goog.require('inferdb.search_by_example.main');
goog.require('inferdb.spreadsheets.model');
goog.require('inferdb.spreadsheets.data');
goog.require('inferdb.spreadsheets.pfcas');
inferdb.spreadsheets.search.search_by_example = (function inferdb$spreadsheets$search$search_by_example(example,emphasis,_){
return inferdb.search_by_example.main.cached_search.call(null,inferdb.spreadsheets.model.census_cgpm,inferdb.spreadsheets.model.cluster_data,inferdb.spreadsheets.pfcas.pfcas,example,emphasis);
});

//# sourceMappingURL=search.js.map
