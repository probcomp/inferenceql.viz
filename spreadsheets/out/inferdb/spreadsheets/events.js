// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.spreadsheets.events');
goog.require('cljs.core');
goog.require('clojure.edn');
goog.require('re_frame.core');
goog.require('inferdb.spreadsheets.db');
goog.require('inferdb.spreadsheets.events.interceptors');
goog.require('inferdb.spreadsheets.search');
inferdb.spreadsheets.events.hooks = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"after-deselect","after-deselect",782914204),new cljs.core.Keyword(null,"after-selection-end","after-selection-end",89158061)], null);
re_frame.core.reg_event_db.call(null,new cljs.core.Keyword(null,"initialize-db","initialize-db",230998432),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [inferdb.spreadsheets.events.interceptors.check_spec], null),(function (db,_){
return inferdb.spreadsheets.db.default_db.call(null);
}));
re_frame.core.reg_event_db.call(null,new cljs.core.Keyword(null,"after-selection-end","after-selection-end",89158061),(function (db,p__2627){
var vec__2628 = p__2627;
var _ = cljs.core.nth.call(null,vec__2628,(0),null);
var hot = cljs.core.nth.call(null,vec__2628,(1),null);
var row = cljs.core.nth.call(null,vec__2628,(2),null);
var col = cljs.core.nth.call(null,vec__2628,(3),null);
var row2 = cljs.core.nth.call(null,vec__2628,(4),null);
var col2 = cljs.core.nth.call(null,vec__2628,(5),null);
var prevent_scrolling = cljs.core.nth.call(null,vec__2628,(6),null);
var selection_layer_level = cljs.core.nth.call(null,vec__2628,(7),null);
var headers = cljs.core.map.call(null,((function (vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level){
return (function (p1__2626_SHARP_){
return hot.getColHeader(p1__2626_SHARP_);
});})(vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level))
,cljs.core.range.call(null,(function (){var x__4222__auto__ = col;
var y__4223__auto__ = col2;
return ((x__4222__auto__ < y__4223__auto__) ? x__4222__auto__ : y__4223__auto__);
})(),((function (){var x__4219__auto__ = col;
var y__4220__auto__ = col2;
return ((x__4219__auto__ > y__4220__auto__) ? x__4219__auto__ : y__4220__auto__);
})() + (1))));
var selected_maps = cljs.core.into.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.comp.call(null,cljs.core.map.call(null,((function (headers,vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level){
return (function (p__2631){
var vec__2632 = p__2631;
var row__$1 = cljs.core.nth.call(null,vec__2632,(0),null);
var col__$1 = cljs.core.nth.call(null,vec__2632,(1),null);
var row2__$1 = cljs.core.nth.call(null,vec__2632,(2),null);
var col2__$1 = cljs.core.nth.call(null,vec__2632,(3),null);
return cljs.core.js__GT_clj.call(null,hot.getData(row__$1,col__$1,row2__$1,col2__$1));
});})(headers,vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level))
),cljs.core.map.call(null,cljs.core.js__GT_clj),cljs.core.map.call(null,((function (headers,vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level){
return (function (rows){
return cljs.core.into.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.map.call(null,((function (headers,vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level){
return (function (row__$1){
return cljs.core.zipmap.call(null,headers,row__$1);
});})(headers,vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level))
),rows);
});})(headers,vec__2628,_,hot,row,col,row2,col2,prevent_scrolling,selection_layer_level))
)),hot.getSelected());
var selected_columns = (((col <= col2))?headers:cljs.core.reverse.call(null,headers));
return inferdb.spreadsheets.db.with_selected_row_index.call(null,inferdb.spreadsheets.db.with_selections.call(null,inferdb.spreadsheets.db.with_selected_columns.call(null,db,selected_columns),selected_maps),row);
}));
re_frame.core.reg_event_db.call(null,new cljs.core.Keyword(null,"after-deselect","after-deselect",782914204),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [inferdb.spreadsheets.events.interceptors.check_spec], null),(function (db,_){
console.log("DESELECT");

return inferdb.spreadsheets.db.clear_selections.call(null,db);
}));
re_frame.core.reg_event_db.call(null,new cljs.core.Keyword(null,"search","search",1564939822),(function (db,p__2635){
var vec__2636 = p__2635;
var _ = cljs.core.nth.call(null,vec__2636,(0),null);
var text = cljs.core.nth.call(null,vec__2636,(1),null);
var row_2639 = clojure.edn.read_string.call(null,text);
var result_2640 = inferdb.spreadsheets.search.search_by_example.call(null,row_2639,new cljs.core.Keyword(null,"cluster-for-percap","cluster-for-percap",-1014643745),(1));
re_frame.core.dispatch.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"search-result","search-result",528142443),result_2640], null));

return db;
}));
re_frame.core.reg_event_db.call(null,new cljs.core.Keyword(null,"search-result","search-result",528142443),(function (db,p__2641){
var vec__2642 = p__2641;
var _ = cljs.core.nth.call(null,vec__2642,(0),null);
var result = cljs.core.nth.call(null,vec__2642,(1),null);
return inferdb.spreadsheets.db.with_scores.call(null,db,cljs.core.mapv.call(null,cljs.core.second,cljs.core.sort_by.call(null,cljs.core.first,result)));
}));

//# sourceMappingURL=events.js.map
