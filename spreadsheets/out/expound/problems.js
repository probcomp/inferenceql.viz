// Compiled by ClojureScript 1.10.520 {}
goog.provide('expound.problems');
goog.require('cljs.core');
goog.require('expound.paths');
goog.require('cljs.spec.alpha');
goog.require('clojure.walk');
goog.require('clojure.string');
goog.require('expound.printer');
goog.require('expound.ansi');
expound.problems.blank_form = (function expound$problems$blank_form(form){
if(cljs.core.map_QMARK_.call(null,form)){
return cljs.core.zipmap.call(null,cljs.core.keys.call(null,form),cljs.core.repeat.call(null,new cljs.core.Keyword("expound.problems","irrelevant","expound.problems/irrelevant",2090226124)));
} else {
if(cljs.core.vector_QMARK_.call(null,form)){
return cljs.core.vec.call(null,cljs.core.repeat.call(null,cljs.core.count.call(null,form),new cljs.core.Keyword("expound.problems","irrelevant","expound.problems/irrelevant",2090226124)));
} else {
if(cljs.core.set_QMARK_.call(null,form)){
return form;
} else {
if(((cljs.core.list_QMARK_.call(null,form)) || (cljs.core.seq_QMARK_.call(null,form)))){
return cljs.core.apply.call(null,cljs.core.list,cljs.core.repeat.call(null,cljs.core.count.call(null,form),new cljs.core.Keyword("expound.problems","irrelevant","expound.problems/irrelevant",2090226124)));
} else {
return new cljs.core.Keyword("expound.problems","irrelevant","expound.problems/irrelevant",2090226124);

}
}
}
}
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.problems","summary-form","expound.problems/summary-form",514693822,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"show-valid-values?","show-valid-values?",-587258094),new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"highlighted-path","highlighted-path",-511870),new cljs.core.Keyword("expound","path","expound/path",-1026376555))),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"show-valid-values?","show-valid-values?",-587258094),new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"highlighted-path","highlighted-path",-511870),new cljs.core.Keyword("expound","path","expound/path",-1026376555)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"show-valid-values?","show-valid-values?",-587258094),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Keyword(null,"highlighted-path","highlighted-path",-511870)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.boolean_QMARK_,cljs.core.any_QMARK_,new cljs.core.Keyword("expound","path","expound/path",-1026376555)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword("expound","path","expound/path",-1026376555)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"show-valid-values?","show-valid-values?",-587258094),new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"highlighted-path","highlighted-path",-511870),new cljs.core.Keyword("expound","path","expound/path",-1026376555)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),cljs.core.any_QMARK_,null,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),null,null,null));
expound.problems.summary_form = (function expound$problems$summary_form(show_valid_values_QMARK_,form,in$){
while(true){
var vec__4401 = in$;
var seq__4402 = cljs.core.seq.call(null,vec__4401);
var first__4403 = cljs.core.first.call(null,seq__4402);
var seq__4402__$1 = cljs.core.next.call(null,seq__4402);
var k = first__4403;
var rst = seq__4402__$1;
var rst__$1 = (function (){var or__4131__auto__ = rst;
if(or__4131__auto__){
return or__4131__auto__;
} else {
return cljs.core.PersistentVector.EMPTY;
}
})();
var displayed_form = (cljs.core.truth_(show_valid_values_QMARK_)?form:expound.problems.blank_form.call(null,form));
if(cljs.core.empty_QMARK_.call(null,in$)){
return new cljs.core.Keyword("expound.problems","relevant","expound.problems/relevant",1188199036);
} else {
if(((cljs.core.map_QMARK_.call(null,form)) && (expound.paths.kps_QMARK_.call(null,k)))){
return cljs.core.assoc.call(null,cljs.core.dissoc.call(null,displayed_form,new cljs.core.Keyword(null,"key","key",-1516042587).cljs$core$IFn$_invoke$arity$1(k)),expound.problems.summary_form.call(null,show_valid_values_QMARK_,new cljs.core.Keyword(null,"key","key",-1516042587).cljs$core$IFn$_invoke$arity$1(k),rst__$1),new cljs.core.Keyword("expound.problems","irrelevant","expound.problems/irrelevant",2090226124));
} else {
if(((cljs.core.map_QMARK_.call(null,form)) && (expound.paths.kvps_QMARK_.call(null,k)))){
var G__4404 = show_valid_values_QMARK_;
var G__4405 = cljs.core.nth.call(null,cljs.core.seq.call(null,form),new cljs.core.Keyword(null,"idx","idx",1053688473).cljs$core$IFn$_invoke$arity$1(k));
var G__4406 = rst__$1;
show_valid_values_QMARK_ = G__4404;
form = G__4405;
in$ = G__4406;
continue;
} else {
if(cljs.core.associative_QMARK_.call(null,form)){
return cljs.core.assoc.call(null,displayed_form,k,expound.problems.summary_form.call(null,show_valid_values_QMARK_,cljs.core.get.call(null,form,k),rst__$1));
} else {
if(((cljs.core.int_QMARK_.call(null,k)) && (cljs.core.seq_QMARK_.call(null,form)))){
return cljs.core.apply.call(null,cljs.core.list,cljs.core.assoc.call(null,cljs.core.vec.call(null,displayed_form),k,expound.problems.summary_form.call(null,show_valid_values_QMARK_,cljs.core.nth.call(null,form,k),rst__$1)));
} else {
if(((cljs.core.int_QMARK_.call(null,k)) && (cljs.core.set_QMARK_.call(null,form)))){
return cljs.core.into.call(null,cljs.core.PersistentHashSet.EMPTY,cljs.core.assoc.call(null,cljs.core.vec.call(null,displayed_form),k,expound.problems.summary_form.call(null,show_valid_values_QMARK_,cljs.core.nth.call(null,cljs.core.seq.call(null,form),k),rst__$1)));
} else {
if(((cljs.core.int_QMARK_.call(null,k)) && (cljs.core.list_QMARK_.call(null,form)))){
return cljs.core.into.call(null,cljs.core.List.EMPTY,cljs.core.assoc.call(null,cljs.core.vec.call(null,displayed_form),k,expound.problems.summary_form.call(null,show_valid_values_QMARK_,cljs.core.nth.call(null,cljs.core.seq.call(null,form),k),rst__$1)));
} else {
if(((cljs.core.int_QMARK_.call(null,k)) && (typeof form === 'string'))){
return clojure.string.join.call(null,cljs.core.assoc.call(null,cljs.core.vec.call(null,form),k,new cljs.core.Keyword("expound.problems","relevant","expound.problems/relevant",1188199036)));
} else {
throw cljs.core.ex_info.call(null,"Cannot find path segment in form. This can be caused by using conformers to transform values, which is not supported in Expound",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"form","form",-1624062471),form,new cljs.core.Keyword(null,"in","in",-1531184865),in$], null));

}
}
}
}
}
}
}
}
break;
}
});
expound.problems.highlight_line = (function expound$problems$highlight_line(prefix,replacement){
var max_width = cljs.core.apply.call(null,cljs.core.max,cljs.core.map.call(null,(function (p1__4407_SHARP_){
return cljs.core.count.call(null,cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__4407_SHARP_));
}),clojure.string.split_lines.call(null,replacement)));
return expound.printer.indent.call(null,cljs.core.count.call(null,cljs.core.str.cljs$core$IFn$_invoke$arity$1(prefix)),cljs.core.apply.call(null,cljs.core.str,cljs.core.repeat.call(null,max_width,"^")));
});
expound.problems.adjust_in = (function expound$problems$adjust_in(form,problem){
var in1 = expound.paths.in_with_kps.call(null,form,new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(problem),new cljs.core.Keyword(null,"in","in",-1531184865).cljs$core$IFn$_invoke$arity$1(problem),cljs.core.PersistentVector.EMPTY);
var in2 = (function (){var paths = expound.paths.paths_to_value.call(null,form,new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(problem),cljs.core.PersistentVector.EMPTY,cljs.core.PersistentVector.EMPTY);
if(cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,paths))){
return cljs.core.first.call(null,paths);
} else {
return null;
}
})();
var in3 = (function (){try{return expound.paths.in_with_kps.call(null,form,cljs.spec.alpha.unform.call(null,cljs.core.last.call(null,new cljs.core.Keyword(null,"via","via",-1904457336).cljs$core$IFn$_invoke$arity$1(problem)),new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(problem)),new cljs.core.Keyword(null,"in","in",-1531184865).cljs$core$IFn$_invoke$arity$1(problem),cljs.core.PersistentVector.EMPTY);
}catch (e4408){var _e = e4408;
return null;
}})();
var new_in = (cljs.core.truth_(in1)?in1:(cljs.core.truth_(in2)?in2:(cljs.core.truth_(in3)?in3:(cljs.core.truth_((function (){var or__4131__auto__ = cljs.core._EQ_.call(null,cljs.core.list(new cljs.core.Symbol(null,"apply","apply",-1334050276,null),new cljs.core.Symbol(null,"fn","fn",465265323,null)),new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem));
if(or__4131__auto__){
return or__4131__auto__;
} else {
return new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"ret","ret",-468222814),null], null), null).call(null,cljs.core.first.call(null,new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(problem)));
}
})())?new cljs.core.Keyword(null,"in","in",-1531184865).cljs$core$IFn$_invoke$arity$1(problem):null
))));
return cljs.core.assoc.call(null,problem,new cljs.core.Keyword("expound","in","expound/in",-1900412298),new_in);
});
expound.problems.adjust_path = (function expound$problems$adjust_path(failure,problem){
return cljs.core.assoc.call(null,problem,new cljs.core.Keyword("expound","path","expound/path",-1026376555),((cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"instrument","instrument",-960698844),failure))?cljs.core.vec.call(null,cljs.core.rest.call(null,new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(problem))):new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(problem)));
});
expound.problems.add_spec = (function expound$problems$add_spec(spec,problem){
return cljs.core.assoc.call(null,problem,new cljs.core.Keyword(null,"spec","spec",347520401),spec);
});
expound.problems.fix_via = (function expound$problems$fix_via(spec,problem){
if(cljs.core._EQ_.call(null,spec,cljs.core.first.call(null,new cljs.core.Keyword(null,"via","via",-1904457336).cljs$core$IFn$_invoke$arity$1(problem)))){
return cljs.core.assoc.call(null,problem,new cljs.core.Keyword("expound","via","expound/via",-595987777),new cljs.core.Keyword(null,"via","via",-1904457336).cljs$core$IFn$_invoke$arity$1(problem));
} else {
return cljs.core.assoc.call(null,problem,new cljs.core.Keyword("expound","via","expound/via",-595987777),cljs.core.into.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec], null),new cljs.core.Keyword(null,"via","via",-1904457336).cljs$core$IFn$_invoke$arity$1(problem)));
}
});
expound.problems.missing_spec_QMARK_ = (function expound$problems$missing_spec_QMARK_(_failure,problem){
return cljs.core._EQ_.call(null,"no method",new cljs.core.Keyword(null,"reason","reason",-2070751759).cljs$core$IFn$_invoke$arity$1(problem));
});
expound.problems.not_in_set_QMARK_ = (function expound$problems$not_in_set_QMARK_(_failure,problem){
return cljs.core.set_QMARK_.call(null,new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem));
});
expound.problems.fspec_exception_failure_QMARK_ = (function expound$problems$fspec_exception_failure_QMARK_(failure,problem){
return ((cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"instrument","instrument",-960698844),failure)) && (cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"check-failed","check-failed",-1316157547),failure)) && (cljs.core._EQ_.call(null,cljs.core.list(new cljs.core.Symbol(null,"apply","apply",-1334050276,null),new cljs.core.Symbol(null,"fn","fn",465265323,null)),new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem))));
});
expound.problems.fspec_ret_failure_QMARK_ = (function expound$problems$fspec_ret_failure_QMARK_(failure,problem){
return ((cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"instrument","instrument",-960698844),failure)) && (cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"check-failed","check-failed",-1316157547),failure)) && (cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"ret","ret",-468222814),cljs.core.first.call(null,new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(problem)))));
});
expound.problems.fspec_fn_failure_QMARK_ = (function expound$problems$fspec_fn_failure_QMARK_(failure,problem){
return ((cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"instrument","instrument",-960698844),failure)) && (cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"check-failed","check-failed",-1316157547),failure)) && (cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"fn","fn",-1175266204),cljs.core.first.call(null,new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(problem)))));
});
expound.problems.check_ret_failure_QMARK_ = (function expound$problems$check_ret_failure_QMARK_(failure,problem){
return ((cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"check-failed","check-failed",-1316157547),failure)) && (cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"ret","ret",-468222814),cljs.core.first.call(null,new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(problem)))));
});
expound.problems.check_fn_failure_QMARK_ = (function expound$problems$check_fn_failure_QMARK_(failure,problem){
return ((cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"check-failed","check-failed",-1316157547),failure)) && (cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"fn","fn",-1175266204),cljs.core.first.call(null,new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(problem)))));
});
expound.problems.missing_key_QMARK_ = (function expound$problems$missing_key_QMARK_(_failure,problem){
var pred = new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem);
return ((cljs.core.seq_QMARK_.call(null,pred)) && (((2) < cljs.core.count.call(null,pred))) && (cljs.spec.alpha.valid_QMARK_.call(null,new cljs.core.Keyword("expound.spec","contains-key-pred","expound.spec/contains-key-pred",-989075236),cljs.core.nth.call(null,pred,(2)))));
});
expound.problems.insufficient_input_QMARK_ = (function expound$problems$insufficient_input_QMARK_(_failure,problem){
return cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, ["Insufficient input",null], null), null),new cljs.core.Keyword(null,"reason","reason",-2070751759).cljs$core$IFn$_invoke$arity$1(problem));
});
expound.problems.extra_input_QMARK_ = (function expound$problems$extra_input_QMARK_(_failure,problem){
return cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, ["Extra input",null], null), null),new cljs.core.Keyword(null,"reason","reason",-2070751759).cljs$core$IFn$_invoke$arity$1(problem));
});
expound.problems.ptype = (function expound$problems$ptype(failure,problem){
if(cljs.core.truth_(new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(problem))){
return new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(problem);
} else {
if(expound.problems.insufficient_input_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","insufficient-input","expound.problem/insufficient-input",1437497436);
} else {
if(expound.problems.extra_input_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","extra-input","expound.problem/extra-input",2043170217);
} else {
if(expound.problems.not_in_set_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","not-in-set","expound.problem/not-in-set",14506077);
} else {
if(expound.problems.missing_key_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","missing-key","expound.problem/missing-key",-750683408);
} else {
if(expound.problems.missing_spec_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","missing-spec","expound.problem/missing-spec",-1439599438);
} else {
if(expound.problems.fspec_exception_failure_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","fspec-exception-failure","expound.problem/fspec-exception-failure",-398312942);
} else {
if(expound.problems.fspec_ret_failure_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","fspec-ret-failure","expound.problem/fspec-ret-failure",1192937934);
} else {
if(expound.problems.fspec_fn_failure_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","fspec-fn-failure","expound.problem/fspec-fn-failure",-814692716);
} else {
if(expound.problems.check_ret_failure_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","check-ret-failure","expound.problem/check-ret-failure",1795987483);
} else {
if(expound.problems.check_fn_failure_QMARK_.call(null,failure,problem)){
return new cljs.core.Keyword("expound.problem","check-fn-failure","expound.problem/check-fn-failure",443478179);
} else {
return new cljs.core.Keyword("expound.problem","unknown","expound.problem/unknown",1364832957);

}
}
}
}
}
}
}
}
}
}
}
});
expound.problems.escape_replacement = (function expound$problems$escape_replacement(pattern,s){
return clojure.string.replace.call(null,s,/\$/,"$$$$");
});
/**
 * Given a problem, returns a pretty printed
 * string that highlights the problem value
 */
expound.problems.highlighted_value = (function expound$problems$highlighted_value(opts,problem){
var map__4409 = problem;
var map__4409__$1 = (((((!((map__4409 == null))))?(((((map__4409.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4409.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4409):map__4409);
var form = cljs.core.get.call(null,map__4409__$1,new cljs.core.Keyword("expound","form","expound/form",-264680632));
var in$ = cljs.core.get.call(null,map__4409__$1,new cljs.core.Keyword("expound","in","expound/in",-1900412298));
var map__4410 = opts;
var map__4410__$1 = (((((!((map__4410 == null))))?(((((map__4410.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4410.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4410):map__4410);
var show_valid_values_QMARK_ = cljs.core.get.call(null,map__4410__$1,new cljs.core.Keyword(null,"show-valid-values?","show-valid-values?",-587258094),false);
var printed_val = expound.printer.pprint_str.call(null,expound.paths.value_in.call(null,form,in$));
var relevant = ["(",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword("expound.problems","relevant","expound.problems/relevant",1188199036)),"|(",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword("expound.problems","kv-relevant","expound.problems/kv-relevant",229013575)),"\\s+",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword("expound.problems","kv-relevant","expound.problems/kv-relevant",229013575)),"))"].join('');
var regex = cljs.core.re_pattern.call(null,["(.*)",relevant,".*"].join(''));
var s = (function (){var _STAR_print_namespace_maps_STAR__orig_val__4416 = cljs.core._STAR_print_namespace_maps_STAR_;
var _STAR_print_namespace_maps_STAR__temp_val__4417 = false;
cljs.core._STAR_print_namespace_maps_STAR_ = _STAR_print_namespace_maps_STAR__temp_val__4417;

try{return expound.printer.pprint_str.call(null,clojure.walk.prewalk_replace.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("expound.problems","irrelevant","expound.problems/irrelevant",2090226124),new cljs.core.Symbol(null,"...","...",-1926939749,null)], null),expound.problems.summary_form.call(null,show_valid_values_QMARK_,form,in$)));
}finally {cljs.core._STAR_print_namespace_maps_STAR_ = _STAR_print_namespace_maps_STAR__orig_val__4416;
}})();
var vec__4411 = cljs.core.re_find.call(null,regex,s);
var seq__4412 = cljs.core.seq.call(null,vec__4411);
var first__4413 = cljs.core.first.call(null,seq__4412);
var seq__4412__$1 = cljs.core.next.call(null,seq__4412);
var line = first__4413;
var first__4413__$1 = cljs.core.first.call(null,seq__4412__$1);
var seq__4412__$2 = cljs.core.next.call(null,seq__4412__$1);
var prefix = first__4413__$1;
var _more = seq__4412__$2;
var highlighted_line = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.replace.call(null,line,cljs.core.re_pattern.call(null,relevant),expound.problems.escape_replacement.call(null,cljs.core.re_pattern.call(null,relevant),expound.printer.indent.call(null,(0),cljs.core.count.call(null,prefix),expound.ansi.color.call(null,printed_val,new cljs.core.Keyword(null,"bad-value","bad-value",-139100659)))))),"\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.ansi.color.call(null,expound.problems.highlight_line.call(null,prefix,printed_val),new cljs.core.Keyword(null,"pointer","pointer",85071187)))].join('');
return expound.printer.no_trailing_whitespace.call(null,clojure.string.replace.call(null,s,line,expound.problems.escape_replacement.call(null,line,highlighted_line)));
});
expound.problems.annotate = (function expound$problems$annotate(explain_data){
var map__4420 = explain_data;
var map__4420__$1 = (((((!((map__4420 == null))))?(((((map__4420.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4420.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4420):map__4420);
var problems = cljs.core.get.call(null,map__4420__$1,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814));
var value = cljs.core.get.call(null,map__4420__$1,new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274));
var args = cljs.core.get.call(null,map__4420__$1,new cljs.core.Keyword("cljs.spec.alpha","args","cljs.spec.alpha/args",1870769783));
var ret = cljs.core.get.call(null,map__4420__$1,new cljs.core.Keyword("cljs.spec.alpha","ret","cljs.spec.alpha/ret",1165997503));
var fn = cljs.core.get.call(null,map__4420__$1,new cljs.core.Keyword("cljs.spec.alpha","fn","cljs.spec.alpha/fn",408600443));
var failure = cljs.core.get.call(null,map__4420__$1,new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592));
var spec = cljs.core.get.call(null,map__4420__$1,new cljs.core.Keyword("cljs.spec.alpha","spec","cljs.spec.alpha/spec",1947137578));
var caller = (function (){var or__4131__auto__ = new cljs.core.Keyword("clojure.spec.test.alpha","caller","clojure.spec.test.alpha/caller",-706822212).cljs$core$IFn$_invoke$arity$1(explain_data);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Keyword("orchestra.spec.test","caller","orchestra.spec.test/caller",-686413347).cljs$core$IFn$_invoke$arity$1(explain_data);
}
})();
var form = ((cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"instrument","instrument",-960698844),failure))?value:((cljs.core.contains_QMARK_.call(null,explain_data,new cljs.core.Keyword("cljs.spec.alpha","ret","cljs.spec.alpha/ret",1165997503)))?ret:((cljs.core.contains_QMARK_.call(null,explain_data,new cljs.core.Keyword("cljs.spec.alpha","args","cljs.spec.alpha/args",1870769783)))?args:((cljs.core.contains_QMARK_.call(null,explain_data,new cljs.core.Keyword("cljs.spec.alpha","fn","cljs.spec.alpha/fn",408600443)))?fn:null))));
var problems_SINGLEQUOTE_ = cljs.core.map.call(null,cljs.core.comp.call(null,cljs.core.partial.call(null,expound.problems.adjust_in,form),cljs.core.partial.call(null,expound.problems.adjust_path,failure),cljs.core.partial.call(null,expound.problems.add_spec,spec),cljs.core.partial.call(null,expound.problems.fix_via,spec),((function (map__4420,map__4420__$1,problems,value,args,ret,fn,failure,spec,caller,form){
return (function (p1__4418_SHARP_){
return cljs.core.assoc.call(null,p1__4418_SHARP_,new cljs.core.Keyword("expound","form","expound/form",-264680632),form);
});})(map__4420,map__4420__$1,problems,value,args,ret,fn,failure,spec,caller,form))
,((function (map__4420,map__4420__$1,problems,value,args,ret,fn,failure,spec,caller,form){
return (function (p1__4419_SHARP_){
return cljs.core.assoc.call(null,p1__4419_SHARP_,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659),expound.problems.ptype.call(null,failure,p1__4419_SHARP_));
});})(map__4420,map__4420__$1,problems,value,args,ret,fn,failure,spec,caller,form))
),problems);
return cljs.core.assoc.call(null,explain_data,new cljs.core.Keyword("expound","form","expound/form",-264680632),form,new cljs.core.Keyword("expound","caller","expound/caller",-503638870),caller,new cljs.core.Keyword("expound","problems","expound/problems",1257773984),problems_SINGLEQUOTE_);
});
expound.problems.type = expound.problems.ptype;
expound.problems.value_in = expound.paths.value_in;

//# sourceMappingURL=problems.js.map
