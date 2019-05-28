// Compiled by ClojureScript 1.10.520 {}
goog.provide('metaprob.prelude');
goog.require('cljs.core');
goog.require('clojure.set');
goog.require('metaprob.trace');
goog.require('metaprob.generative_functions');
metaprob.prelude.exp = (function metaprob$prelude$exp(x){
return Math.exp(x);
});
metaprob.prelude.expt = (function metaprob$prelude$expt(x,y){
return Math.pow(x,y);
});
metaprob.prelude.sqrt = (function metaprob$prelude$sqrt(x){
return Math.sqrt(x);
});
metaprob.prelude.log = (function metaprob$prelude$log(x){
return Math.log(x);
});
metaprob.prelude.cos = (function metaprob$prelude$cos(x){
return Math.cos(x);
});
metaprob.prelude.sin = (function metaprob$prelude$sin(x){
return Math.sin(x);
});
metaprob.prelude.log1p = (function metaprob$prelude$log1p(x){
return Math.log1p(x);
});
metaprob.prelude.floor = (function metaprob$prelude$floor(x){
return Math.floor(x);
});
metaprob.prelude.round = (function metaprob$prelude$round(x){
return Math.round(x);
});
metaprob.prelude.negative_infinity = Number.NEGATIVE_INFINITY;
metaprob.prelude.sample_uniform = (function metaprob$prelude$sample_uniform(var_args){
var G__2208 = arguments.length;
switch (G__2208) {
case 0:
return metaprob.prelude.sample_uniform.cljs$core$IFn$_invoke$arity$0();

break;
case 2:
return metaprob.prelude.sample_uniform.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

metaprob.prelude.sample_uniform.cljs$core$IFn$_invoke$arity$0 = (function (){
return Math.random();
});

metaprob.prelude.sample_uniform.cljs$core$IFn$_invoke$arity$2 = (function (a,b){
return (a + (Math.random() * (b - a)));
});

metaprob.prelude.sample_uniform.cljs$lang$maxFixedArity = 2;

metaprob.prelude.set_difference = (function metaprob$prelude$set_difference(s1,s2){
return cljs.core.seq.call(null,clojure.set.difference.call(null,cljs.core.set.call(null,s1),cljs.core.set.call(null,s2)));
});
metaprob.prelude.apply = cljs.core.with_meta.call(null,cljs.core.apply,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"apply?","apply?",-887799265),true], null));
metaprob.prelude.map = metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function (f,l){
return cljs.core.doall.call(null,cljs.core.map_indexed.call(null,(function (i,x){
return at.call(null,i,f,x);
}),l));
});
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"l","l",-1258542346,null)], null),cljs.core.list(new cljs.core.Symbol(null,"doall","doall",988520834,null),cljs.core.list(new cljs.core.Symbol(null,"map-indexed","map-indexed",-1391025435,null),cljs.core.list(new cljs.core.Symbol(null,"fn","fn",465265323,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"i","i",253690212,null),new cljs.core.Symbol(null,"x","x",-555367584,null)], null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),new cljs.core.Symbol(null,"i","i",253690212,null),new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"x","x",-555367584,null))),new cljs.core.Symbol(null,"l","l",-1258542346,null))))], null));
metaprob.prelude.map_xform = metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function (f){
return cljs.core.map_indexed.call(null,(function (i,x){
return at.call(null,i,f,x);
}));
});
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null)], null),cljs.core.list(new cljs.core.Symbol(null,"map-indexed","map-indexed",-1391025435,null),cljs.core.list(new cljs.core.Symbol(null,"fn","fn",465265323,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"i","i",253690212,null),new cljs.core.Symbol(null,"x","x",-555367584,null)], null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),new cljs.core.Symbol(null,"i","i",253690212,null),new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"x","x",-555367584,null)))))], null));
metaprob.prelude.replicate = metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function (n,f){
return metaprob.prelude.map.call(null,(function (i){
return at.call(null,i,f);
}),cljs.core.range.call(null,n));
});
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"n","n",-2092305744,null),new cljs.core.Symbol(null,"f","f",43394975,null)], null),cljs.core.list(new cljs.core.Symbol(null,"map","map",-1282745308,null),cljs.core.list(new cljs.core.Symbol(null,"fn","fn",465265323,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"i","i",253690212,null)], null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),new cljs.core.Symbol(null,"i","i",253690212,null),new cljs.core.Symbol(null,"f","f",43394975,null))),cljs.core.list(new cljs.core.Symbol(null,"range","range",-1014743483,null),new cljs.core.Symbol(null,"n","n",-2092305744,null))))], null));
metaprob.prelude.doall_STAR_ = (function metaprob$prelude$doall_STAR_(s){
cljs.core.dorun.call(null,cljs.core.tree_seq.call(null,cljs.core.seq_QMARK_,cljs.core.seq,s));

return s;
});
metaprob.prelude.make_primitive = (function metaprob$prelude$make_primitive(sampler,scorer){
return metaprob.generative_functions.make_generative_function.call(null,sampler,(function (observations){
if(metaprob.trace.trace_has_value_QMARK_.call(null,observations)){
return metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function() { 
var G__2210__delegate = function (args){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [metaprob.trace.trace_value.call(null,observations),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),metaprob.trace.trace_value.call(null,observations)], null),scorer.call(null,metaprob.trace.trace_value.call(null,observations),args)], null);
};
var G__2210 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__2211__i = 0, G__2211__a = new Array(arguments.length -  0);
while (G__2211__i < G__2211__a.length) {G__2211__a[G__2211__i] = arguments[G__2211__i + 0]; ++G__2211__i;}
  args = new cljs.core.IndexedSeq(G__2211__a,0,null);
} 
return G__2210__delegate.call(this,args);};
G__2210.cljs$lang$maxFixedArity = 0;
G__2210.cljs$lang$applyTo = (function (arglist__2212){
var args = cljs.core.seq(arglist__2212);
return G__2210__delegate(args);
});
G__2210.cljs$core$IFn$_invoke$arity$variadic = G__2210__delegate;
return G__2210;
})()
;
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"&","&",-2144855648,null),new cljs.core.Symbol(null,"args","args",-1338879193,null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("trace","trace-value","trace/trace-value",1155812744,null),new cljs.core.Symbol(null,"observations","observations",1907170252,null)),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),cljs.core.list(new cljs.core.Symbol("trace","trace-value","trace/trace-value",1155812744,null),new cljs.core.Symbol(null,"observations","observations",1907170252,null))], null),cljs.core.list(new cljs.core.Symbol(null,"scorer","scorer",-2003501089,null),cljs.core.list(new cljs.core.Symbol("trace","trace-value","trace/trace-value",1155812744,null),new cljs.core.Symbol(null,"observations","observations",1907170252,null)),new cljs.core.Symbol(null,"args","args",-1338879193,null))], null))], null));
} else {
return metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function() { 
var G__2213__delegate = function (args){
var result = apply_at.call(null,cljs.core.List.EMPTY,metaprob.prelude.make_primitive.call(null,sampler,scorer),args);
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [result,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),result], null),(0)], null);
};
var G__2213 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__2214__i = 0, G__2214__a = new Array(arguments.length -  0);
while (G__2214__i < G__2214__a.length) {G__2214__a[G__2214__i] = arguments[G__2214__i + 0]; ++G__2214__i;}
  args = new cljs.core.IndexedSeq(G__2214__a,0,null);
} 
return G__2213__delegate.call(this,args);};
G__2213.cljs$lang$maxFixedArity = 0;
G__2213.cljs$lang$applyTo = (function (arglist__2215){
var args = cljs.core.seq(arglist__2215);
return G__2213__delegate(args);
});
G__2213.cljs$core$IFn$_invoke$arity$variadic = G__2213__delegate;
return G__2213;
})()
;
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"&","&",-2144855648,null),new cljs.core.Symbol(null,"args","args",-1338879193,null)], null),cljs.core.list(new cljs.core.Symbol(null,"let","let",358118826,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"result","result",-1239343558,null),cljs.core.list(new cljs.core.Symbol(null,"apply-at","apply-at",1320572267,null),cljs.core.list(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.List.EMPTY),cljs.core.list(new cljs.core.Symbol(null,"make-primitive","make-primitive",-395497920,null),new cljs.core.Symbol(null,"sampler","sampler",-585545905,null),new cljs.core.Symbol(null,"scorer","scorer",-2003501089,null)),new cljs.core.Symbol(null,"args","args",-1338879193,null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"result","result",-1239343558,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"result","result",-1239343558,null)], null),(0)], null)))], null));
}
}));
});
metaprob.prelude.infer_and_score = metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function() { 
var G__2219__delegate = function (p__2216){
var map__2217 = p__2216;
var map__2217__$1 = (((((!((map__2217 == null))))?(((((map__2217.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2217.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2217):map__2217);
var procedure = cljs.core.get.call(null,map__2217__$1,new cljs.core.Keyword(null,"procedure","procedure",176722572));
var inputs = cljs.core.get.call(null,map__2217__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var observation_trace = cljs.core.get.call(null,map__2217__$1,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),cljs.core.PersistentArrayMap.EMPTY);
return apply_at.call(null,cljs.core.List.EMPTY,metaprob.generative_functions.make_constrained_generator.call(null,procedure,observation_trace),inputs);
};
var G__2219 = function (var_args){
var p__2216 = null;
if (arguments.length > 0) {
var G__2220__i = 0, G__2220__a = new Array(arguments.length -  0);
while (G__2220__i < G__2220__a.length) {G__2220__a[G__2220__i] = arguments[G__2220__i + 0]; ++G__2220__i;}
  p__2216 = new cljs.core.IndexedSeq(G__2220__a,0,null);
} 
return G__2219__delegate.call(this,p__2216);};
G__2219.cljs$lang$maxFixedArity = 0;
G__2219.cljs$lang$applyTo = (function (arglist__2221){
var p__2216 = cljs.core.seq(arglist__2221);
return G__2219__delegate(p__2216);
});
G__2219.cljs$core$IFn$_invoke$arity$variadic = G__2219__delegate;
return G__2219;
})()
;
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"&","&",-2144855648,null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"keys","keys",1068423698),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"procedure","procedure",1817254099,null),new cljs.core.Symbol(null,"inputs","inputs",-1788631911,null),new cljs.core.Symbol(null,"observation-trace","observation-trace",1038510714,null)], null),new cljs.core.Keyword(null,"or","or",235744169),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Symbol(null,"inputs","inputs",-1788631911,null),cljs.core.PersistentVector.EMPTY,new cljs.core.Symbol(null,"observation-trace","observation-trace",1038510714,null),cljs.core.PersistentArrayMap.EMPTY], null)], null)], null),cljs.core.list(new cljs.core.Symbol(null,"apply-at","apply-at",1320572267,null),cljs.core.list(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.List.EMPTY),cljs.core.list(new cljs.core.Symbol(null,"make-constrained-generator","make-constrained-generator",-2052409138,null),new cljs.core.Symbol(null,"procedure","procedure",1817254099,null),new cljs.core.Symbol(null,"observation-trace","observation-trace",1038510714,null)),new cljs.core.Symbol(null,"inputs","inputs",-1788631911,null)))], null));

//# sourceMappingURL=prelude.js.map
