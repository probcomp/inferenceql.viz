// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.multimixture.dsl');
goog.require('cljs.core');
goog.require('metaprob.trace');
goog.require('metaprob.prelude');
goog.require('metaprob.distributions');
goog.require('metaprob.inference');
inferdb.multimixture.dsl.view_cluster_address = (function inferdb$multimixture$dsl$view_cluster_address(v){
return ["cluster-for-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)].join('');
});
inferdb.multimixture.dsl.column_cluster_address = (function inferdb$multimixture$dsl$column_cluster_address(column){
return ["cluster-for-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(column)].join('');
});
inferdb.multimixture.dsl.view_for_column = (function inferdb$multimixture$dsl$view_for_column(column){
return (0);
});
inferdb.multimixture.dsl.make_view = (function inferdb$multimixture$dsl$make_view(p__2415){
var vec__2416 = p__2415;
var vars_and_dists = cljs.core.nth.call(null,vec__2416,(0),null);
var vec__2419 = cljs.core.nth.call(null,vec__2416,(1),null);
var cluster_probs = cljs.core.nth.call(null,vec__2419,(0),null);
var cluster_params = cljs.core.nth.call(null,vec__2419,(1),null);
var view_name = ["view",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.gensym.call(null))].join('');
var var_names = cljs.core.keys.call(null,vars_and_dists);
var cluster_addr = inferdb.multimixture.dsl.view_cluster_address.call(null,view_name);
var sampler = metaprob.generative_functions.generative_function_from_traced_code.call(null,((function (view_name,var_names,cluster_addr,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (at,apply_at){
return ((function (view_name,var_names,cluster_addr,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (){
var cluster_idx = at.call(null,cluster_addr,metaprob.distributions.categorical,cluster_probs);
var params = cljs.core.nth.call(null,cluster_params,cluster_idx);
var seq__2422_2429 = cljs.core.seq.call(null,var_names);
var chunk__2423_2430 = null;
var count__2424_2431 = (0);
var i__2425_2432 = (0);
while(true){
if((i__2425_2432 < count__2424_2431)){
var v_2433 = cljs.core._nth.call(null,chunk__2423_2430,i__2425_2432);
at.call(null,inferdb.multimixture.dsl.column_cluster_address.call(null,v_2433),metaprob.distributions.exactly,cluster_idx);


var G__2434 = seq__2422_2429;
var G__2435 = chunk__2423_2430;
var G__2436 = count__2424_2431;
var G__2437 = (i__2425_2432 + (1));
seq__2422_2429 = G__2434;
chunk__2423_2430 = G__2435;
count__2424_2431 = G__2436;
i__2425_2432 = G__2437;
continue;
} else {
var temp__5720__auto___2438 = cljs.core.seq.call(null,seq__2422_2429);
if(temp__5720__auto___2438){
var seq__2422_2439__$1 = temp__5720__auto___2438;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__2422_2439__$1)){
var c__4550__auto___2440 = cljs.core.chunk_first.call(null,seq__2422_2439__$1);
var G__2441 = cljs.core.chunk_rest.call(null,seq__2422_2439__$1);
var G__2442 = c__4550__auto___2440;
var G__2443 = cljs.core.count.call(null,c__4550__auto___2440);
var G__2444 = (0);
seq__2422_2429 = G__2441;
chunk__2423_2430 = G__2442;
count__2424_2431 = G__2443;
i__2425_2432 = G__2444;
continue;
} else {
var v_2445 = cljs.core.first.call(null,seq__2422_2439__$1);
at.call(null,inferdb.multimixture.dsl.column_cluster_address.call(null,v_2445),metaprob.distributions.exactly,cluster_idx);


var G__2446 = cljs.core.next.call(null,seq__2422_2439__$1);
var G__2447 = null;
var G__2448 = (0);
var G__2449 = (0);
seq__2422_2429 = G__2446;
chunk__2423_2430 = G__2447;
count__2424_2431 = G__2448;
i__2425_2432 = G__2449;
continue;
}
} else {
}
}
break;
}

return cljs.core.mapv.call(null,((function (cluster_idx,params,view_name,var_names,cluster_addr,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (v){
return apply_at.call(null,v,cljs.core.get.call(null,vars_and_dists,v),cljs.core.get.call(null,params,v));
});})(cluster_idx,params,view_name,var_names,cluster_addr,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
,var_names);
});
;})(view_name,var_names,cluster_addr,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
});})(view_name,var_names,cluster_addr,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol(null,"let","let",358118826,null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"cluster-idx","cluster-idx",371803371,null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),new cljs.core.Symbol(null,"cluster-addr","cluster-addr",-1702691718,null),new cljs.core.Symbol(null,"categorical","categorical",-831264963,null),new cljs.core.Symbol(null,"cluster-probs","cluster-probs",-665370861,null)),new cljs.core.Symbol(null,"params","params",-1943919534,null),cljs.core.list(new cljs.core.Symbol(null,"nth","nth",1529209554,null),new cljs.core.Symbol(null,"cluster-params","cluster-params",-1707326085,null),new cljs.core.Symbol(null,"cluster-idx","cluster-idx",371803371,null))], null),cljs.core.list(new cljs.core.Symbol(null,"doseq","doseq",221164135,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"v","v",1661996586,null),new cljs.core.Symbol(null,"var-names","var-names",699879546,null)], null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),cljs.core.list(new cljs.core.Symbol(null,"column-cluster-address","column-cluster-address",237681099,null),new cljs.core.Symbol(null,"v","v",1661996586,null)),new cljs.core.Symbol(null,"exactly","exactly",-1350336536,null),new cljs.core.Symbol(null,"cluster-idx","cluster-idx",371803371,null))),cljs.core.list(new cljs.core.Symbol(null,"mapv","mapv",-241595241,null),cljs.core.list(new cljs.core.Symbol(null,"fn","fn",465265323,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"v","v",1661996586,null)], null),cljs.core.list(new cljs.core.Symbol(null,"apply-at","apply-at",1320572267,null),new cljs.core.Symbol(null,"v","v",1661996586,null),cljs.core.list(new cljs.core.Symbol(null,"get","get",-971253014,null),new cljs.core.Symbol(null,"vars-and-dists","vars-and-dists",2125644746,null),new cljs.core.Symbol(null,"v","v",1661996586,null)),cljs.core.list(new cljs.core.Symbol(null,"get","get",-971253014,null),new cljs.core.Symbol(null,"params","params",-1943919534,null),new cljs.core.Symbol(null,"v","v",1661996586,null)))),new cljs.core.Symbol(null,"var-names","var-names",699879546,null))))], null));
return metaprob.inference.with_custom_proposal_attached.call(null,sampler,((function (view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (observations){
return metaprob.generative_functions.generative_function_from_traced_code.call(null,((function (view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (at,apply_at){
return ((function (view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (){
var score_cluster = ((function (view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (idx){
var new_obs = metaprob.trace.trace_set_value.call(null,observations,cluster_addr,idx);
var vec__2426 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),sampler,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),new_obs);
var _ = cljs.core.nth.call(null,vec__2426,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__2426,(1),null);
var s = cljs.core.nth.call(null,vec__2426,(2),null);
return s;
});})(view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
;
var cluster_scores = metaprob.prelude.map.call(null,score_cluster,cljs.core.range.call(null,cljs.core.count.call(null,cluster_probs)));
var chosen_cluster = at.call(null,cluster_addr,metaprob.distributions.log_categorical,cluster_scores);
return at.call(null,cljs.core.List.EMPTY,metaprob.prelude.infer_and_score,new cljs.core.Keyword(null,"procedure","procedure",176722572),sampler,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),metaprob.trace.trace_set_value.call(null,observations,cluster_addr,chosen_cluster));
});
;})(view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
});})(view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol(null,"let","let",358118826,null),new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"score-cluster","score-cluster",651738465,null),cljs.core.list(new cljs.core.Symbol(null,"fn","fn",465265323,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"idx","idx",-1600747296,null)], null),cljs.core.list(new cljs.core.Symbol(null,"let","let",358118826,null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"new-obs","new-obs",429230138,null),cljs.core.list(new cljs.core.Symbol(null,"trace-set-value","trace-set-value",-641500588,null),new cljs.core.Symbol(null,"observations","observations",1907170252,null),new cljs.core.Symbol(null,"cluster-addr","cluster-addr",-1702691718,null),new cljs.core.Symbol(null,"idx","idx",-1600747296,null)),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"s","s",-948495851,null)], null),cljs.core.list(new cljs.core.Symbol(null,"infer-and-score","infer-and-score",1850862231,null),new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Symbol(null,"sampler","sampler",-585545905,null),new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),new cljs.core.Symbol(null,"new-obs","new-obs",429230138,null))], null),new cljs.core.Symbol(null,"s","s",-948495851,null))),new cljs.core.Symbol(null,"cluster-scores","cluster-scores",-299633830,null),cljs.core.list(new cljs.core.Symbol(null,"map","map",-1282745308,null),new cljs.core.Symbol(null,"score-cluster","score-cluster",651738465,null),cljs.core.list(new cljs.core.Symbol(null,"range","range",-1014743483,null),cljs.core.list(new cljs.core.Symbol(null,"count","count",-514511684,null),new cljs.core.Symbol(null,"cluster-probs","cluster-probs",-665370861,null)))),new cljs.core.Symbol(null,"chosen-cluster","chosen-cluster",516744362,null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),new cljs.core.Symbol(null,"cluster-addr","cluster-addr",-1702691718,null),new cljs.core.Symbol(null,"log-categorical","log-categorical",-332522738,null),new cljs.core.Symbol(null,"cluster-scores","cluster-scores",-299633830,null))], null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),cljs.core.list(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.List.EMPTY),new cljs.core.Symbol(null,"infer-and-score","infer-and-score",1850862231,null),new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Symbol(null,"sampler","sampler",-585545905,null),new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),cljs.core.list(new cljs.core.Symbol(null,"trace-set-value","trace-set-value",-641500588,null),new cljs.core.Symbol(null,"observations","observations",1907170252,null),new cljs.core.Symbol(null,"cluster-addr","cluster-addr",-1702691718,null),new cljs.core.Symbol(null,"chosen-cluster","chosen-cluster",516744362,null)))))], null));
});})(view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
,((function (view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params){
return (function (tr){
return (!(metaprob.trace.trace_has_value_QMARK_.call(null,tr,cluster_addr)));
});})(view_name,var_names,cluster_addr,sampler,vec__2416,vars_and_dists,vec__2419,cluster_probs,cluster_params))
);
});
inferdb.multimixture.dsl.make_multi_mixture = (function inferdb$multimixture$dsl$make_multi_mixture(views){
return metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function (){
return cljs.core.into.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.comp.call(null,metaprob.prelude.map_xform.call(null,(function (view){
return at.call(null,cljs.core.List.EMPTY,view);
})),cljs.core.cat),views);
});
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol(null,"into","into",1489695498,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol(null,"comp","comp",-1462482139,null),cljs.core.list(new cljs.core.Symbol(null,"map-xform","map-xform",1428138476,null),cljs.core.list(new cljs.core.Symbol(null,"fn","fn",465265323,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"view","view",-1406440955,null)], null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),cljs.core.list(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.List.EMPTY),new cljs.core.Symbol(null,"view","view",-1406440955,null)))),new cljs.core.Symbol(null,"cat","cat",182721320,null)),new cljs.core.Symbol(null,"views","views",-1204280282,null)))], null));
});
inferdb.multimixture.dsl.cluster_count = (function inferdb$multimixture$dsl$cluster_count(clusters){
return (cljs.core.count.call(null,clusters) / (2));
});
inferdb.multimixture.dsl.multi_mixture = (function inferdb$multimixture$dsl$multi_mixture(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2451 = arguments.length;
var i__4731__auto___2452 = (0);
while(true){
if((i__4731__auto___2452 < len__4730__auto___2451)){
args__4736__auto__.push((arguments[i__4731__auto___2452]));

var G__2453 = (i__4731__auto___2452 + (1));
i__4731__auto___2452 = G__2453;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return inferdb.multimixture.dsl.multi_mixture.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

inferdb.multimixture.dsl.multi_mixture.cljs$core$IFn$_invoke$arity$variadic = (function (viewspecs){
return inferdb.multimixture.dsl.make_multi_mixture.call(null,metaprob.prelude.map.call(null,inferdb.multimixture.dsl.make_view,viewspecs));
});

inferdb.multimixture.dsl.multi_mixture.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
inferdb.multimixture.dsl.multi_mixture.cljs$lang$applyTo = (function (seq2450){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2450));
});

inferdb.multimixture.dsl.view = (function inferdb$multimixture$dsl$view(vars,p__2454){
var vec__2455 = p__2454;
var probs = cljs.core.nth.call(null,vec__2455,(0),null);
var params = cljs.core.nth.call(null,vec__2455,(1),null);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [vars,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [probs,params], null)], null);
});
inferdb.multimixture.dsl.clusters = (function inferdb$multimixture$dsl$clusters(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2459 = arguments.length;
var i__4731__auto___2460 = (0);
while(true){
if((i__4731__auto___2460 < len__4730__auto___2459)){
args__4736__auto__.push((arguments[i__4731__auto___2460]));

var G__2461 = (i__4731__auto___2460 + (1));
i__4731__auto___2460 = G__2461;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return inferdb.multimixture.dsl.clusters.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

inferdb.multimixture.dsl.clusters.cljs$core$IFn$_invoke$arity$variadic = (function (args){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.take_nth.call(null,(2),args),cljs.core.take_nth.call(null,(2),cljs.core.rest.call(null,args))], null);
});

inferdb.multimixture.dsl.clusters.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
inferdb.multimixture.dsl.clusters.cljs$lang$applyTo = (function (seq2458){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2458));
});


//# sourceMappingURL=dsl.js.map
