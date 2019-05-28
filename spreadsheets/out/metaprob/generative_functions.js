// Compiled by ClojureScript 1.10.520 {}
goog.provide('metaprob.generative_functions');
goog.require('cljs.core');
goog.require('cljs.analyzer');
goog.require('metaprob.code_handlers');
goog.require('metaprob.trace');
metaprob.generative_functions.at = (function metaprob$generative_functions$at(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2158 = arguments.length;
var i__4731__auto___2159 = (0);
while(true){
if((i__4731__auto___2159 < len__4730__auto___2158)){
args__4736__auto__.push((arguments[i__4731__auto___2159]));

var G__2160 = (i__4731__auto___2159 + (1));
i__4731__auto___2159 = G__2160;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.generative_functions.at.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.generative_functions.at.cljs$core$IFn$_invoke$arity$variadic = (function (args){
throw (new Error(["Assert failed: ","Cannot invoke at outside of a (gen ...) form.","\n","false"].join('')));

});

metaprob.generative_functions.at.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.generative_functions.at.cljs$lang$applyTo = (function (seq2157){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2157));
});

metaprob.generative_functions.apply_at = (function metaprob$generative_functions$apply_at(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2162 = arguments.length;
var i__4731__auto___2163 = (0);
while(true){
if((i__4731__auto___2163 < len__4730__auto___2162)){
args__4736__auto__.push((arguments[i__4731__auto___2163]));

var G__2164 = (i__4731__auto___2163 + (1));
i__4731__auto___2163 = G__2164;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.generative_functions.apply_at.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.generative_functions.apply_at.cljs$core$IFn$_invoke$arity$variadic = (function (args){
throw (new Error(["Assert failed: ","Cannot invoke apply-at outside of a (gen ...) form.","\n","false"].join('')));

});

metaprob.generative_functions.apply_at.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.generative_functions.apply_at.cljs$lang$applyTo = (function (seq2161){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2161));
});

metaprob.generative_functions.make_generative_function = (function metaprob$generative_functions$make_generative_function(var_args){
var G__2166 = arguments.length;
switch (G__2166) {
case 2:
return metaprob.generative_functions.make_generative_function.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return metaprob.generative_functions.make_generative_function.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

metaprob.generative_functions.make_generative_function.cljs$core$IFn$_invoke$arity$2 = (function (run_in_clojure,make_constrained_generator){
return metaprob.generative_functions.make_generative_function.call(null,run_in_clojure,make_constrained_generator,cljs.core.PersistentArrayMap.EMPTY);
});

metaprob.generative_functions.make_generative_function.cljs$core$IFn$_invoke$arity$3 = (function (run_in_clojure,make_constrained_generator,others){
return cljs.core.with_meta.call(null,run_in_clojure,cljs.core.assoc.call(null,others,new cljs.core.Keyword(null,"make-constrained-generator","make-constrained-generator",602026631),make_constrained_generator));
});

metaprob.generative_functions.make_generative_function.cljs$lang$maxFixedArity = 3;

metaprob.generative_functions.make_constrained_generator = (function metaprob$generative_functions$make_constrained_generator(procedure,observations){
return (function (){var or__4131__auto__ = cljs.core.get.call(null,cljs.core.meta.call(null,procedure),new cljs.core.Keyword(null,"make-constrained-generator","make-constrained-generator",602026631));
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return ((function (or__4131__auto__){
return (function (observations__$1){
return ((function (or__4131__auto__){
return (function() { 
var G__2168__delegate = function (args){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.apply.call(null,procedure,args),cljs.core.PersistentArrayMap.EMPTY,(0)], null);
};
var G__2168 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__2169__i = 0, G__2169__a = new Array(arguments.length -  0);
while (G__2169__i < G__2169__a.length) {G__2169__a[G__2169__i] = arguments[G__2169__i + 0]; ++G__2169__i;}
  args = new cljs.core.IndexedSeq(G__2169__a,0,null);
} 
return G__2168__delegate.call(this,args);};
G__2168.cljs$lang$maxFixedArity = 0;
G__2168.cljs$lang$applyTo = (function (arglist__2170){
var args = cljs.core.seq(arglist__2170);
return G__2168__delegate(args);
});
G__2168.cljs$core$IFn$_invoke$arity$variadic = G__2168__delegate;
return G__2168;
})()
;
;})(or__4131__auto__))
});
;})(or__4131__auto__))
}
})().call(null,observations);
});
metaprob.generative_functions.generative_function_from_traced_code = (function metaprob$generative_functions$generative_function_from_traced_code(fn_accepting_tracers,metadata){
return metaprob.generative_functions.make_generative_function.call(null,fn_accepting_tracers.call(null,(function() { 
var G__2174__delegate = function (addr,proc,args){
return cljs.core.apply.call(null,proc,args);
};
var G__2174 = function (addr,proc,var_args){
var args = null;
if (arguments.length > 2) {
var G__2175__i = 0, G__2175__a = new Array(arguments.length -  2);
while (G__2175__i < G__2175__a.length) {G__2175__a[G__2175__i] = arguments[G__2175__i + 2]; ++G__2175__i;}
  args = new cljs.core.IndexedSeq(G__2175__a,0,null);
} 
return G__2174__delegate.call(this,addr,proc,args);};
G__2174.cljs$lang$maxFixedArity = 2;
G__2174.cljs$lang$applyTo = (function (arglist__2176){
var addr = cljs.core.first(arglist__2176);
arglist__2176 = cljs.core.next(arglist__2176);
var proc = cljs.core.first(arglist__2176);
var args = cljs.core.rest(arglist__2176);
return G__2174__delegate(addr,proc,args);
});
G__2174.cljs$core$IFn$_invoke$arity$variadic = G__2174__delegate;
return G__2174;
})()
,(function (addr,proc,args){
return cljs.core.apply.call(null,proc,args);
})),(function (observations){
return metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function() { 
var G__2177__delegate = function (args){
var score = cljs.core.volatile_BANG_.call(null,0.0);
var trace = cljs.core.volatile_BANG_.call(null,cljs.core.PersistentArrayMap.EMPTY);
var apply_at_impl = ((function (score,trace){
return (function (addr,gf,args__$1){
var vec__2171 = apply_at.call(null,addr,metaprob.generative_functions.make_constrained_generator.call(null,gf,metaprob.trace.maybe_subtrace.call(null,observations,addr)),args__$1);
var v = cljs.core.nth.call(null,vec__2171,(0),null);
var tr = cljs.core.nth.call(null,vec__2171,(1),null);
var s = cljs.core.nth.call(null,vec__2171,(2),null);
cljs.core._vreset_BANG_.call(null,score,(cljs.core._deref.call(null,score) + s));

cljs.core._vreset_BANG_.call(null,trace,metaprob.trace.merge_subtrace.call(null,cljs.core._deref.call(null,trace),addr,tr));

return v;
});})(score,trace))
;
var at_impl = ((function (score,trace,apply_at_impl){
return (function() { 
var G__2178__delegate = function (addr,gf,args__$1){
return apply_at_impl.call(null,addr,gf,args__$1);
};
var G__2178 = function (addr,gf,var_args){
var args__$1 = null;
if (arguments.length > 2) {
var G__2179__i = 0, G__2179__a = new Array(arguments.length -  2);
while (G__2179__i < G__2179__a.length) {G__2179__a[G__2179__i] = arguments[G__2179__i + 2]; ++G__2179__i;}
  args__$1 = new cljs.core.IndexedSeq(G__2179__a,0,null);
} 
return G__2178__delegate.call(this,addr,gf,args__$1);};
G__2178.cljs$lang$maxFixedArity = 2;
G__2178.cljs$lang$applyTo = (function (arglist__2180){
var addr = cljs.core.first(arglist__2180);
arglist__2180 = cljs.core.next(arglist__2180);
var gf = cljs.core.first(arglist__2180);
var args__$1 = cljs.core.rest(arglist__2180);
return G__2178__delegate(addr,gf,args__$1);
});
G__2178.cljs$core$IFn$_invoke$arity$variadic = G__2178__delegate;
return G__2178;
})()
;})(score,trace,apply_at_impl))
;
var result = cljs.core.apply.call(null,fn_accepting_tracers.call(null,at_impl,apply_at_impl),args);
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [result,cljs.core.deref.call(null,trace),cljs.core.deref.call(null,score)], null);
};
var G__2177 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__2181__i = 0, G__2181__a = new Array(arguments.length -  0);
while (G__2181__i < G__2181__a.length) {G__2181__a[G__2181__i] = arguments[G__2181__i + 0]; ++G__2181__i;}
  args = new cljs.core.IndexedSeq(G__2181__a,0,null);
} 
return G__2177__delegate.call(this,args);};
G__2177.cljs$lang$maxFixedArity = 0;
G__2177.cljs$lang$applyTo = (function (arglist__2182){
var args = cljs.core.seq(arglist__2182);
return G__2177__delegate(args);
});
G__2177.cljs$core$IFn$_invoke$arity$variadic = G__2177__delegate;
return G__2177;
})()
;
}),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Symbol(null,"make-constrained-generator-impl","make-constrained-generator-impl",923554373,null)], null));
}),metadata);
});
var ret__4776__auto___2186 = (function (){
metaprob.generative_functions.gen = (function metaprob$generative_functions$gen(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2187 = arguments.length;
var i__4731__auto___2188 = (0);
while(true){
if((i__4731__auto___2188 < len__4730__auto___2187)){
args__4736__auto__.push((arguments[i__4731__auto___2188]));

var G__2189 = (i__4731__auto___2188 + (1));
i__4731__auto___2188 = G__2189;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((2) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((2)),(0),null)):null);
return metaprob.generative_functions.gen.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__4737__auto__);
});

metaprob.generative_functions.gen.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,_){
var expr = _AMPERSAND_form;
var body = metaprob.code_handlers.gen_body.call(null,expr);
var name = metaprob.code_handlers.gen_name.call(null,expr);
var tracer_name = new cljs.core.Symbol(null,"at","at",-1177484420,null);
var apply_tracer_name = new cljs.core.Symbol(null,"apply-at","apply-at",1320572267,null);
var params = metaprob.code_handlers.gen_pattern.call(null,expr);
var thunk_name = (cljs.core.truth_(name)?cljs.core.gensym.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(name),"thunk"].join('')):null);
var named_fn_body = (cljs.core.truth_(name)?cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","let","cljs.core/let",-308701135,null),null,(1),null)),(new cljs.core.List(null,cljs.core.vec.call(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,name,null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,thunk_name,null,(1),null))))),null,(1),null)))))),null,(1),null)),body))),null,(1),null))))):body);
var innermost_fn_expr = cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),null,(1),null)),(new cljs.core.List(null,params,null,(1),null)),named_fn_body)));
var generative_function_expression = cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("metaprob.generative-functions","generative-function-from-traced-code","metaprob.generative-functions/generative-function-from-traced-code",-412033167,null),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),null,(1),null)),(new cljs.core.List(null,cljs.core.vec.call(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,tracer_name,null,(1),null)),(new cljs.core.List(null,apply_tracer_name,null,(1),null)))))),null,(1),null)),(new cljs.core.List(null,innermost_fn_expr,null,(1),null))))),null,(1),null)),(new cljs.core.List(null,cljs.core.apply.call(null,cljs.core.array_map,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Keyword(null,"name","name",1843675177),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),null,(1),null)),(new cljs.core.List(null,name,null,(1),null))))),null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),null,(1),null)),(new cljs.core.List(null,expr,null,(1),null))))),null,(1),null)))))),null,(1),null)))));
if(cljs.core.truth_(name)){
return cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),null,(1),null)),(new cljs.core.List(null,thunk_name,null,(1),null)),(new cljs.core.List(null,cljs.core.vec.call(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null)))),null,(1),null)),(new cljs.core.List(null,generative_function_expression,null,(1),null))))),null,(1),null)))));
} else {
return generative_function_expression;
}
});

metaprob.generative_functions.gen.cljs$lang$maxFixedArity = (2);

/** @this {Function} */
metaprob.generative_functions.gen.cljs$lang$applyTo = (function (seq2183){
var G__2184 = cljs.core.first.call(null,seq2183);
var seq2183__$1 = cljs.core.next.call(null,seq2183);
var G__2185 = cljs.core.first.call(null,seq2183__$1);
var seq2183__$2 = cljs.core.next.call(null,seq2183__$1);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__2184,G__2185,seq2183__$2);
});

return null;
})()
;
metaprob.generative_functions.gen.cljs$lang$macro = true;

var ret__4776__auto___2199 = (function (){
metaprob.generative_functions.let_traced = (function metaprob$generative_functions$let_traced(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2200 = arguments.length;
var i__4731__auto___2201 = (0);
while(true){
if((i__4731__auto___2201 < len__4730__auto___2200)){
args__4736__auto__.push((arguments[i__4731__auto___2201]));

var G__2202 = (i__4731__auto___2201 + (1));
i__4731__auto___2201 = G__2202;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((3) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((3)),(0),null)):null);
return metaprob.generative_functions.let_traced.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__4737__auto__);
});

metaprob.generative_functions.let_traced.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
var binding_pairs = cljs.core.partition.call(null,(2),bindings);
var trace_with_name = ((function (binding_pairs){
return (function metaprob$generative_functions$trace_with_name(expr,name){
while(true){
if(cljs.core.truth_(metaprob.code_handlers.if_expr_QMARK_.call(null,expr))){
return cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"if","if",1181717262,null),null,(1),null)),(new cljs.core.List(null,metaprob$generative_functions$trace_with_name.call(null,metaprob.code_handlers.if_predicate.call(null,expr),name),null,(1),null)),(new cljs.core.List(null,metaprob$generative_functions$trace_with_name.call(null,metaprob.code_handlers.if_then_clause.call(null,expr),name),null,(1),null)),(new cljs.core.List(null,metaprob$generative_functions$trace_with_name.call(null,metaprob.code_handlers.if_else_clause.call(null,expr),name),null,(1),null)))));
} else {
if(cljs.core.truth_(metaprob.code_handlers.do_expr_QMARK_.call(null,expr))){
return cljs.core.cons.call(null,new cljs.core.Symbol(null,"do","do",1686842252,null),cljs.core.map.call(null,((function (expr,name,binding_pairs){
return (function (p1__2190_SHARP_){
return metaprob$generative_functions$trace_with_name.call(null,p1__2190_SHARP_,name);
});})(expr,name,binding_pairs))
,cljs.core.rest.call(null,expr)));
} else {
if(cljs.core.truth_((function (){var or__4131__auto__ = (!(cljs.core.seq_QMARK_.call(null,expr)));
if(or__4131__auto__){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = cljs.core.special_symbol_QMARK_.call(null,cljs.core.first.call(null,expr));
if(or__4131__auto____$1){
return or__4131__auto____$1;
} else {
var or__4131__auto____$2 = metaprob.code_handlers.let_expr_QMARK_.call(null,expr);
if(cljs.core.truth_(or__4131__auto____$2)){
return or__4131__auto____$2;
} else {
var or__4131__auto____$3 = metaprob.code_handlers.let_traced_expr_QMARK_.call(null,expr);
if(cljs.core.truth_(or__4131__auto____$3)){
return or__4131__auto____$3;
} else {
var or__4131__auto____$4 = metaprob.code_handlers.fn_expr_QMARK_.call(null,expr);
if(cljs.core.truth_(or__4131__auto____$4)){
return or__4131__auto____$4;
} else {
return metaprob.code_handlers.gen_expr_QMARK_.call(null,expr);
}
}
}
}
}
})())){
return expr;
} else {
if(cljs.core.not_EQ_.call(null,cljs.analyzer.macroexpand_1.call(null,_AMPERSAND_env,expr),expr)){
var G__2203 = cljs.analyzer.macroexpand_1.call(null,_AMPERSAND_env,expr);
var G__2204 = name;
expr = G__2203;
name = G__2204;
continue;
} else {
return cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"at","at",-1177484420,null),null,(1),null)),(new cljs.core.List(null,name,null,(1),null)),expr)));

}
}
}
}
break;
}
});})(binding_pairs))
;
var convert_binding = ((function (binding_pairs,trace_with_name){
return (function (p__2195){
var vec__2196 = p__2195;
var lhs = cljs.core.nth.call(null,vec__2196,(0),null);
var rhs = cljs.core.nth.call(null,vec__2196,(1),null);
if((lhs instanceof cljs.core.Symbol)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [lhs,trace_with_name.call(null,rhs,cljs.core.str.cljs$core$IFn$_invoke$arity$1(lhs))], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [lhs,rhs], null);
}
});})(binding_pairs,trace_with_name))
;
var new_bindings = cljs.core.vec.call(null,cljs.core.apply.call(null,cljs.core.concat,cljs.core.map.call(null,convert_binding,binding_pairs)));
return cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","let","cljs.core/let",-308701135,null),null,(1),null)),(new cljs.core.List(null,new_bindings,null,(1),null)),body)));
});

metaprob.generative_functions.let_traced.cljs$lang$maxFixedArity = (3);

/** @this {Function} */
metaprob.generative_functions.let_traced.cljs$lang$applyTo = (function (seq2191){
var G__2192 = cljs.core.first.call(null,seq2191);
var seq2191__$1 = cljs.core.next.call(null,seq2191);
var G__2193 = cljs.core.first.call(null,seq2191__$1);
var seq2191__$2 = cljs.core.next.call(null,seq2191__$1);
var G__2194 = cljs.core.first.call(null,seq2191__$2);
var seq2191__$3 = cljs.core.next.call(null,seq2191__$2);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__2192,G__2193,G__2194,seq2191__$3);
});

return null;
})()
;
metaprob.generative_functions.let_traced.cljs$lang$macro = true;


//# sourceMappingURL=generative_functions.js.map
