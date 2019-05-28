// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.search_by_example.main');
goog.require('cljs.core');
goog.require('inferdb.multimixture.dsl');
goog.require('metaprob.prelude');
goog.require('metaprob.trace');
inferdb.search_by_example.main.kli = (function inferdb$search_by_example$main$kli(p_in,q_in){
var p = (function (){var x__4219__auto__ = 1.0E-300;
var y__4220__auto__ = p_in;
return ((x__4219__auto__ > y__4220__auto__) ? x__4219__auto__ : y__4220__auto__);
})();
var q = (function (){var x__4219__auto__ = 1.0E-300;
var y__4220__auto__ = q_in;
return ((x__4219__auto__ > y__4220__auto__) ? x__4219__auto__ : y__4220__auto__);
})();
return (p * Math.log((p / q)));
});
inferdb.search_by_example.main.kl = (function inferdb$search_by_example$main$kl(ps,qs){

return cljs.core.apply.call(null,cljs.core._PLUS_,cljs.core.map.call(null,(function (p,q){
return inferdb.search_by_example.main.kli.call(null,p,q);
}),ps,qs));
});
inferdb.search_by_example.main.symmetrized_kl = (function inferdb$search_by_example$main$symmetrized_kl(ps,qs){
return (inferdb.search_by_example.main.kl.call(null,ps,qs) + inferdb.search_by_example.main.kl.call(null,qs,ps));
});
/**
 * Constrains the given trace such that the values chosen for each
 *   column are the ones in the provided row.
 */
inferdb.search_by_example.main.constrain_by_row = (function inferdb$search_by_example$main$constrain_by_row(trace,row){
return cljs.core.reduce.call(null,(function (trace__$1,p__2464){
var vec__2465 = p__2464;
var column = cljs.core.nth.call(null,vec__2465,(0),null);
var value = cljs.core.nth.call(null,vec__2465,(1),null);
return metaprob.trace.trace_set_value.call(null,trace__$1,column,value);
}),trace,row);
});
/**
 * Constrains the given trace with such that the view chosen is the
 *   one provided.
 */
inferdb.search_by_example.main.constrain_by_view = (function inferdb$search_by_example$main$constrain_by_view(trace,cluster,view){
return metaprob.trace.trace_set_value.call(null,trace,inferdb.multimixture.dsl.view_cluster_address.call(null,view),cluster);
});
/**
 * Constrains the given trace such that the cluster chosen is the one
 *   provided.
 */
inferdb.search_by_example.main.constrain_by_cluster = (function inferdb$search_by_example$main$constrain_by_cluster(trace,cluster,columns){
return cljs.core.reduce.call(null,(function (trace__$1,column){
return metaprob.trace.trace_set_value.call(null,trace__$1,inferdb.multimixture.dsl.column_cluster_address.call(null,column),cluster);
}),trace,columns);
});
/**
 * Normalizes a vector of numbers such that they sum to 1.
 */
inferdb.search_by_example.main.normalize = (function inferdb$search_by_example$main$normalize(ns){
var sum = cljs.core.apply.call(null,cljs.core._PLUS_,ns);
return cljs.core.mapv.call(null,((function (sum){
return (function (p1__2468_SHARP_){
return (p1__2468_SHARP_ / sum);
});})(sum))
,ns);
});
inferdb.search_by_example.main.probability_distribution_on_cluster = (function inferdb$search_by_example$main$probability_distribution_on_cluster(model,clusters,row,view){
var columns = cljs.core.keys.call(null,cljs.core.second.call(null,clusters));
var cluster_addresses = cljs.core.range.call(null,inferdb.multimixture.dsl.cluster_count.call(null,clusters));
return inferdb.search_by_example.main.normalize.call(null,cljs.core.map.call(null,((function (columns,cluster_addresses){
return (function (cluster_address){
var trace = inferdb.search_by_example.main.constrain_by_row.call(null,inferdb.search_by_example.main.constrain_by_cluster.call(null,inferdb.search_by_example.main.constrain_by_view.call(null,cljs.core.PersistentArrayMap.EMPTY,cluster_address,view),cluster_address,columns),row);
var vec__2469 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),trace);
var _ = cljs.core.nth.call(null,vec__2469,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__2469,(1),null);
var score = cljs.core.nth.call(null,vec__2469,(2),null);
return metaprob.prelude.exp.call(null,score);
});})(columns,cluster_addresses))
,cluster_addresses));
});
inferdb.search_by_example.main.rowwise_similarity = (function inferdb$search_by_example$main$rowwise_similarity(cgpm,clusters,view,example_pfca,row,emphasis){
return inferdb.search_by_example.main.kl.call(null,example_pfca,inferdb.search_by_example.main.probability_distribution_on_cluster.call(null,cgpm,clusters,row,view));
});
inferdb.search_by_example.main.search = (function inferdb$search_by_example$main$search(cgpm,clusters,rows,example,emphasis){
var view = inferdb.multimixture.dsl.view_for_column.call(null,emphasis);
var example_pfca = inferdb.search_by_example.main.probability_distribution_on_cluster.call(null,new cljs.core.Keyword(null,"proc","proc",2011328965).cljs$core$IFn$_invoke$arity$1(cgpm),clusters,example,emphasis);
return cljs.core.sort_by.call(null,cljs.core.second,cljs.core.map_indexed.call(null,((function (view,example_pfca){
return (function (index,row){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [index,inferdb.search_by_example.main.rowwise_similarity.call(null,new cljs.core.Keyword(null,"proc","proc",2011328965).cljs$core$IFn$_invoke$arity$1(cgpm),clusters,view,example_pfca,row,emphasis)], null);
});})(view,example_pfca))
,rows));
});
inferdb.search_by_example.main.cached_search = (function inferdb$search_by_example$main$cached_search(cgpm,clusters,pfcas,example,emphasis){
var view = inferdb.multimixture.dsl.view_for_column.call(null,emphasis);
var example_pfca = inferdb.search_by_example.main.probability_distribution_on_cluster.call(null,new cljs.core.Keyword(null,"proc","proc",2011328965).cljs$core$IFn$_invoke$arity$1(cgpm),clusters,example,emphasis);
return cljs.core.sort_by.call(null,cljs.core.second,cljs.core.map_indexed.call(null,((function (view,example_pfca){
return (function (index,pfca){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [index,inferdb.search_by_example.main.symmetrized_kl.call(null,example_pfca,pfca)], null);
});})(view,example_pfca))
,pfcas));
});

//# sourceMappingURL=main.js.map
