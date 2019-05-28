// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.spreadsheets.events.interceptors');
goog.require('cljs.core');
goog.require('cljs.spec.alpha');
goog.require('expound.alpha');
goog.require('re_frame.core');
goog.require('inferdb.spreadsheets.db');
/**
 * Throws an exception if `db` doesn't match the Spec `a-spec`.
 */
inferdb.spreadsheets.events.interceptors.check_and_throw = (function inferdb$spreadsheets$events$interceptors$check_and_throw(db,a_spec){
if(cljs.spec.alpha.valid_QMARK_.call(null,a_spec,db)){
return null;
} else {
console.error((function (){var sb__4661__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__2580_2584 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__2581_2585 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__2582_2586 = true;
var _STAR_print_fn_STAR__temp_val__2583_2587 = ((function (_STAR_print_newline_STAR__orig_val__2580_2584,_STAR_print_fn_STAR__orig_val__2581_2585,_STAR_print_newline_STAR__temp_val__2582_2586,sb__4661__auto__){
return (function (x__4662__auto__){
return sb__4661__auto__.append(x__4662__auto__);
});})(_STAR_print_newline_STAR__orig_val__2580_2584,_STAR_print_fn_STAR__orig_val__2581_2585,_STAR_print_newline_STAR__temp_val__2582_2586,sb__4661__auto__))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__2582_2586;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__2583_2587;

try{expound.alpha.expound.call(null,a_spec,db);
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__2581_2585;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__2580_2584;
}
return cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__4661__auto__);
})());

throw cljs.core.ex_info.call(null,["db does not satisfy spec: ",cljs.spec.alpha.explain_str.call(null,a_spec,db)].join(''),cljs.spec.alpha.explain_data.call(null,a_spec,db));
}
});
inferdb.spreadsheets.events.interceptors.check_spec = re_frame.core.after.call(null,(function (p1__2588_SHARP_){
return inferdb.spreadsheets.events.interceptors.check_and_throw.call(null,p1__2588_SHARP_,new cljs.core.Keyword("inferdb.spreadsheets.db","db","inferdb.spreadsheets.db/db",1271599814));
}));

//# sourceMappingURL=interceptors.js.map
