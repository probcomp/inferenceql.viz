// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.spreadsheets.core');
goog.require('cljs.core');
goog.require('goog.dom');
goog.require('re_frame.core');
goog.require('reagent.core');
goog.require('inferdb.spreadsheets.events');
goog.require('inferdb.spreadsheets.subs');
goog.require('inferdb.spreadsheets.views');
cljs.core.enable_console_print_BANG_.call(null);
re_frame.core.dispatch_sync.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"initialize-db","initialize-db",230998432)], null));
inferdb.spreadsheets.core._main = (function inferdb$spreadsheets$core$_main(){
return reagent.core.render.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [inferdb.spreadsheets.views.app], null),goog.dom.$("app"));
});
goog.exportSymbol('inferdb.spreadsheets.core._main', inferdb.spreadsheets.core._main);

//# sourceMappingURL=core.js.map
