// Compiled by ClojureScript 1.10.520 {}
goog.provide('metaprob.inference');
goog.require('cljs.core');
goog.require('metaprob.distributions');
goog.require('metaprob.generative_functions');
goog.require('metaprob.prelude');
goog.require('metaprob.trace');
metaprob.inference.rejection_sampling = (function metaprob$inference$rejection_sampling(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2272 = arguments.length;
var i__4731__auto___2273 = (0);
while(true){
if((i__4731__auto___2273 < len__4730__auto___2272)){
args__4736__auto__.push((arguments[i__4731__auto___2273]));

var G__2274 = (i__4731__auto___2273 + (1));
i__4731__auto___2273 = G__2274;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.rejection_sampling.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.rejection_sampling.cljs$core$IFn$_invoke$arity$variadic = (function (p__2266){
while(true){
var map__2267 = p__2266;
var map__2267__$1 = (((((!((map__2267 == null))))?(((((map__2267.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2267.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2267):map__2267);
var model = cljs.core.get.call(null,map__2267__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2267__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var observation_trace = cljs.core.get.call(null,map__2267__$1,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),cljs.core.PersistentArrayMap.EMPTY);
var predicate = cljs.core.get.call(null,map__2267__$1,new cljs.core.Keyword(null,"predicate","predicate",-1742501860));
var log_bound = cljs.core.get.call(null,map__2267__$1,new cljs.core.Keyword(null,"log-bound","log-bound",-50199703));
var vec__2269 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),observation_trace);
var _ = cljs.core.nth.call(null,vec__2269,(0),null);
var candidate_trace = cljs.core.nth.call(null,vec__2269,(1),null);
var score = cljs.core.nth.call(null,vec__2269,(2),null);
if(cljs.core.truth_(predicate)){
if(cljs.core.truth_(predicate.call(null,candidate_trace))){
return candidate_trace;
} else {
var G__2275 = (new cljs.core.List(null,new cljs.core.Keyword(null,"model","model",331153215),(new cljs.core.List(null,model,(new cljs.core.List(null,new cljs.core.Keyword(null,"inputs","inputs",865803858),(new cljs.core.List(null,inputs,(new cljs.core.List(null,new cljs.core.Keyword(null,"predicate","predicate",-1742501860),(new cljs.core.List(null,predicate,null,(1),null)),(2),null)),(3),null)),(4),null)),(5),null)),(6),null));
p__2266 = G__2275;
continue;
}
} else {
if(cljs.core.truth_(log_bound)){
if((metaprob.prelude.log.call(null,metaprob.distributions.uniform.call(null,(0),(1))) < (score - log_bound))){
return candidate_trace;
} else {
var G__2276 = (new cljs.core.List(null,new cljs.core.Keyword(null,"model","model",331153215),(new cljs.core.List(null,model,(new cljs.core.List(null,new cljs.core.Keyword(null,"inputs","inputs",865803858),(new cljs.core.List(null,inputs,(new cljs.core.List(null,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),(new cljs.core.List(null,observation_trace,(new cljs.core.List(null,new cljs.core.Keyword(null,"log-bound","log-bound",-50199703),(new cljs.core.List(null,log_bound,null,(1),null)),(2),null)),(3),null)),(4),null)),(5),null)),(6),null)),(7),null)),(8),null));
p__2266 = G__2276;
continue;
}
} else {
return null;
}
}
break;
}
});

metaprob.inference.rejection_sampling.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.rejection_sampling.cljs$lang$applyTo = (function (seq2265){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2265));
});

metaprob.inference.importance_sampling = (function metaprob$inference$importance_sampling(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2284 = arguments.length;
var i__4731__auto___2285 = (0);
while(true){
if((i__4731__auto___2285 < len__4730__auto___2284)){
args__4736__auto__.push((arguments[i__4731__auto___2285]));

var G__2286 = (i__4731__auto___2285 + (1));
i__4731__auto___2285 = G__2286;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.importance_sampling.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.importance_sampling.cljs$core$IFn$_invoke$arity$variadic = (function (p__2278){
var map__2279 = p__2278;
var map__2279__$1 = (((((!((map__2279 == null))))?(((((map__2279.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2279.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2279):map__2279);
var model = cljs.core.get.call(null,map__2279__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2279__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var f = cljs.core.get.call(null,map__2279__$1,new cljs.core.Keyword(null,"f","f",-1597136552));
var observation_trace = cljs.core.get.call(null,map__2279__$1,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),cljs.core.PersistentArrayMap.EMPTY);
var n_particles = cljs.core.get.call(null,map__2279__$1,new cljs.core.Keyword(null,"n-particles","n-particles",1678644863),(1));
var particles = metaprob.prelude.replicate.call(null,n_particles,((function (map__2279,map__2279__$1,model,inputs,f,observation_trace,n_particles){
return (function (){
var vec__2281 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),observation_trace,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs);
var v = cljs.core.nth.call(null,vec__2281,(0),null);
var t = cljs.core.nth.call(null,vec__2281,(1),null);
var s = cljs.core.nth.call(null,vec__2281,(2),null);
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [((metaprob.prelude.exp.call(null,s) * (cljs.core.truth_(f)?f.call(null,t):v)) * s)], null);
});})(map__2279,map__2279__$1,model,inputs,f,observation_trace,n_particles))
);
var normalizer = metaprob.prelude.exp.call(null,metaprob.distributions.logsumexp.call(null,metaprob.prelude.map.call(null,cljs.core.second,particles)));
return (cljs.core.reduce.call(null,cljs.core._PLUS_,metaprob.prelude.map.call(null,cljs.core.first,particles)) / normalizer);
});

metaprob.inference.importance_sampling.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.importance_sampling.cljs$lang$applyTo = (function (seq2277){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2277));
});

metaprob.inference.importance_resampling = (function metaprob$inference$importance_resampling(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2294 = arguments.length;
var i__4731__auto___2295 = (0);
while(true){
if((i__4731__auto___2295 < len__4730__auto___2294)){
args__4736__auto__.push((arguments[i__4731__auto___2295]));

var G__2296 = (i__4731__auto___2295 + (1));
i__4731__auto___2295 = G__2296;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.importance_resampling.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.importance_resampling.cljs$core$IFn$_invoke$arity$variadic = (function (p__2288){
var map__2289 = p__2288;
var map__2289__$1 = (((((!((map__2289 == null))))?(((((map__2289.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2289.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2289):map__2289);
var model = cljs.core.get.call(null,map__2289__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2289__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var observation_trace = cljs.core.get.call(null,map__2289__$1,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),cljs.core.PersistentArrayMap.EMPTY);
var n_particles = cljs.core.get.call(null,map__2289__$1,new cljs.core.Keyword(null,"n-particles","n-particles",1678644863),(1));
var particles = metaprob.prelude.replicate.call(null,n_particles,((function (map__2289,map__2289__$1,model,inputs,observation_trace,n_particles){
return (function (){
var vec__2291 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),observation_trace);
var _ = cljs.core.nth.call(null,vec__2291,(0),null);
var t = cljs.core.nth.call(null,vec__2291,(1),null);
var s = cljs.core.nth.call(null,vec__2291,(2),null);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [t,s], null);
});})(map__2289,map__2289__$1,model,inputs,observation_trace,n_particles))
);
return cljs.core.first.call(null,cljs.core.nth.call(null,particles,metaprob.distributions.log_categorical.call(null,metaprob.prelude.map.call(null,cljs.core.second,particles))));
});

metaprob.inference.importance_resampling.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.importance_resampling.cljs$lang$applyTo = (function (seq2287){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2287));
});

metaprob.inference.likelihood_weighting = (function metaprob$inference$likelihood_weighting(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2304 = arguments.length;
var i__4731__auto___2305 = (0);
while(true){
if((i__4731__auto___2305 < len__4730__auto___2304)){
args__4736__auto__.push((arguments[i__4731__auto___2305]));

var G__2306 = (i__4731__auto___2305 + (1));
i__4731__auto___2305 = G__2306;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.likelihood_weighting.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.likelihood_weighting.cljs$core$IFn$_invoke$arity$variadic = (function (p__2298){
var map__2299 = p__2298;
var map__2299__$1 = (((((!((map__2299 == null))))?(((((map__2299.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2299.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2299):map__2299);
var model = cljs.core.get.call(null,map__2299__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2299__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var observation_trace = cljs.core.get.call(null,map__2299__$1,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),cljs.core.PersistentArrayMap.EMPTY);
var n_particles = cljs.core.get.call(null,map__2299__$1,new cljs.core.Keyword(null,"n-particles","n-particles",1678644863),(1));
var weights = metaprob.prelude.replicate.call(null,n_particles,((function (map__2299,map__2299__$1,model,inputs,observation_trace,n_particles){
return (function (){
var vec__2301 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),observation_trace);
var _ = cljs.core.nth.call(null,vec__2301,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__2301,(1),null);
var s = cljs.core.nth.call(null,vec__2301,(2),null);
return s;
});})(map__2299,map__2299__$1,model,inputs,observation_trace,n_particles))
);
return metaprob.prelude.exp.call(null,metaprob.distributions.logmeanexp.call(null,weights));
});

metaprob.inference.likelihood_weighting.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.likelihood_weighting.cljs$lang$applyTo = (function (seq2297){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2297));
});

metaprob.inference.importance_resampling_custom_proposal = (function metaprob$inference$importance_resampling_custom_proposal(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2314 = arguments.length;
var i__4731__auto___2315 = (0);
while(true){
if((i__4731__auto___2315 < len__4730__auto___2314)){
args__4736__auto__.push((arguments[i__4731__auto___2315]));

var G__2316 = (i__4731__auto___2315 + (1));
i__4731__auto___2315 = G__2316;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.importance_resampling_custom_proposal.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.importance_resampling_custom_proposal.cljs$core$IFn$_invoke$arity$variadic = (function (p__2308){
var map__2309 = p__2308;
var map__2309__$1 = (((((!((map__2309 == null))))?(((((map__2309.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2309.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2309):map__2309);
var model = cljs.core.get.call(null,map__2309__$1,new cljs.core.Keyword(null,"model","model",331153215));
var proposer = cljs.core.get.call(null,map__2309__$1,new cljs.core.Keyword(null,"proposer","proposer",-1530597100));
var inputs = cljs.core.get.call(null,map__2309__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var observation_trace = cljs.core.get.call(null,map__2309__$1,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),cljs.core.PersistentArrayMap.EMPTY);
var n_particles = cljs.core.get.call(null,map__2309__$1,new cljs.core.Keyword(null,"n-particles","n-particles",1678644863),(1));
var custom_proposal = proposer.call(null,observation_trace);
var proposed_traces = metaprob.prelude.replicate.call(null,n_particles,((function (custom_proposal,map__2309,map__2309__$1,model,proposer,inputs,observation_trace,n_particles){
return (function (){
var vec__2311 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),custom_proposal,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs);
var _ = cljs.core.nth.call(null,vec__2311,(0),null);
var t = cljs.core.nth.call(null,vec__2311,(1),null);
var ___$1 = cljs.core.nth.call(null,vec__2311,(2),null);
return metaprob.trace.trace_merge.call(null,t,observation_trace);
});})(custom_proposal,map__2309,map__2309__$1,model,proposer,inputs,observation_trace,n_particles))
);
var scores = metaprob.prelude.map.call(null,((function (custom_proposal,proposed_traces,map__2309,map__2309__$1,model,proposer,inputs,observation_trace,n_particles){
return (function (tr){
return (cljs.core.nth.call(null,metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),tr),(2)) - cljs.core.nth.call(null,metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),custom_proposal,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),tr),(2)));
});})(custom_proposal,proposed_traces,map__2309,map__2309__$1,model,proposer,inputs,observation_trace,n_particles))
,proposed_traces);
return cljs.core.nth.call(null,proposed_traces,metaprob.distributions.log_categorical.call(null,scores));
});

metaprob.inference.importance_resampling_custom_proposal.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.importance_resampling_custom_proposal.cljs$lang$applyTo = (function (seq2307){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2307));
});

metaprob.inference.with_custom_proposal_attached = (function metaprob$inference$with_custom_proposal_attached(orig_generative_function,make_custom_proposer,condition_for_use){
return metaprob.generative_functions.make_generative_function.call(null,orig_generative_function,(function (observations){
if(cljs.core.truth_(condition_for_use.call(null,observations))){
return metaprob.generative_functions.generative_function_from_traced_code.call(null,(function (at,apply_at){
return (function() { 
var G__2326__delegate = function (args){
var custom_proposal = make_custom_proposer.call(null,observations);
var vec__2317 = at.call(null,cljs.core.List.EMPTY,metaprob.prelude.infer_and_score,new cljs.core.Keyword(null,"procedure","procedure",176722572),custom_proposal,new cljs.core.Keyword(null,"inputs","inputs",865803858),args);
var _ = cljs.core.nth.call(null,vec__2317,(0),null);
var tr = cljs.core.nth.call(null,vec__2317,(1),null);
var ___$1 = cljs.core.nth.call(null,vec__2317,(2),null);
var proposed_trace = metaprob.trace.trace_merge.call(null,observations,tr);
var vec__2320 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),orig_generative_function,new cljs.core.Keyword(null,"inputs","inputs",865803858),args,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),proposed_trace);
var v = cljs.core.nth.call(null,vec__2320,(0),null);
var tr2 = cljs.core.nth.call(null,vec__2320,(1),null);
var p_score = cljs.core.nth.call(null,vec__2320,(2),null);
var vec__2323 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),custom_proposal,new cljs.core.Keyword(null,"inputs","inputs",865803858),args,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),proposed_trace);
var ___$2 = cljs.core.nth.call(null,vec__2323,(0),null);
var ___$3 = cljs.core.nth.call(null,vec__2323,(1),null);
var q_score = cljs.core.nth.call(null,vec__2323,(2),null);
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [v,proposed_trace,(p_score - q_score)], null);
};
var G__2326 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__2327__i = 0, G__2327__a = new Array(arguments.length -  0);
while (G__2327__i < G__2327__a.length) {G__2327__a[G__2327__i] = arguments[G__2327__i + 0]; ++G__2327__i;}
  args = new cljs.core.IndexedSeq(G__2327__a,0,null);
} 
return G__2326__delegate.call(this,args);};
G__2326.cljs$lang$maxFixedArity = 0;
G__2326.cljs$lang$applyTo = (function (arglist__2328){
var args = cljs.core.seq(arglist__2328);
return G__2326__delegate(args);
});
G__2326.cljs$core$IFn$_invoke$arity$variadic = G__2326__delegate;
return G__2326;
})()
;
}),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"&","&",-2144855648,null),new cljs.core.Symbol(null,"args","args",-1338879193,null)], null),cljs.core.list(new cljs.core.Symbol(null,"let","let",358118826,null),new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"custom-proposal","custom-proposal",-1336084631,null),cljs.core.list(new cljs.core.Symbol(null,"make-custom-proposer","make-custom-proposer",1902982000,null),new cljs.core.Symbol(null,"observations","observations",1907170252,null)),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"tr","tr",215756881,null),new cljs.core.Symbol(null,"_","_",-1201019570,null)], null),cljs.core.list(new cljs.core.Symbol(null,"at","at",-1177484420,null),cljs.core.list(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.List.EMPTY),new cljs.core.Symbol("mp","infer-and-score","mp/infer-and-score",1850865904,null),new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Symbol(null,"custom-proposal","custom-proposal",-1336084631,null),new cljs.core.Keyword(null,"inputs","inputs",865803858),new cljs.core.Symbol(null,"args","args",-1338879193,null)),new cljs.core.Symbol(null,"proposed-trace","proposed-trace",-1862149588,null),cljs.core.list(new cljs.core.Symbol("trace","trace-merge","trace/trace-merge",-1706867914,null),new cljs.core.Symbol(null,"observations","observations",1907170252,null),new cljs.core.Symbol(null,"tr","tr",215756881,null)),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"v","v",1661996586,null),new cljs.core.Symbol(null,"tr2","tr2",1699737360,null),new cljs.core.Symbol(null,"p-score","p-score",451432532,null)], null),cljs.core.list(new cljs.core.Symbol("mp","infer-and-score","mp/infer-and-score",1850865904,null),new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Symbol(null,"orig-generative-function","orig-generative-function",216050579,null),new cljs.core.Keyword(null,"inputs","inputs",865803858),new cljs.core.Symbol(null,"args","args",-1338879193,null),new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),new cljs.core.Symbol(null,"proposed-trace","proposed-trace",-1862149588,null)),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"q-score","q-score",1558935466,null)], null),cljs.core.list(new cljs.core.Symbol("mp","infer-and-score","mp/infer-and-score",1850865904,null),new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Symbol(null,"custom-proposal","custom-proposal",-1336084631,null),new cljs.core.Keyword(null,"inputs","inputs",865803858),new cljs.core.Symbol(null,"args","args",-1338879193,null),new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),new cljs.core.Symbol(null,"proposed-trace","proposed-trace",-1862149588,null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"v","v",1661996586,null),new cljs.core.Symbol(null,"proposed-trace","proposed-trace",-1862149588,null),cljs.core.list(new cljs.core.Symbol(null,"-","-",-471816912,null),new cljs.core.Symbol(null,"p-score","p-score",451432532,null),new cljs.core.Symbol(null,"q-score","q-score",1558935466,null))], null)))], null));
} else {
return metaprob.generative_functions.make_constrained_generator.call(null,orig_generative_function,observations);
}
}));
});
metaprob.inference.symmetric_proposal_mh_step = (function metaprob$inference$symmetric_proposal_mh_step(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2339 = arguments.length;
var i__4731__auto___2340 = (0);
while(true){
if((i__4731__auto___2340 < len__4730__auto___2339)){
args__4736__auto__.push((arguments[i__4731__auto___2340]));

var G__2341 = (i__4731__auto___2340 + (1));
i__4731__auto___2340 = G__2341;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.symmetric_proposal_mh_step.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.symmetric_proposal_mh_step.cljs$core$IFn$_invoke$arity$variadic = (function (p__2330){
var map__2331 = p__2330;
var map__2331__$1 = (((((!((map__2331 == null))))?(((((map__2331.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2331.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2331):map__2331);
var model = cljs.core.get.call(null,map__2331__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2331__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var proposal = cljs.core.get.call(null,map__2331__$1,new cljs.core.Keyword(null,"proposal","proposal",142522715));
return ((function (map__2331,map__2331__$1,model,inputs,proposal){
return (function (current_trace){
var vec__2333 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),current_trace);
var _ = cljs.core.nth.call(null,vec__2333,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__2333,(1),null);
var current_trace_score = cljs.core.nth.call(null,vec__2333,(2),null);
var proposed_trace = proposal.call(null,current_trace);
var vec__2336 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),proposed_trace);
var ___$2 = cljs.core.nth.call(null,vec__2336,(0),null);
var ___$3 = cljs.core.nth.call(null,vec__2336,(1),null);
var proposed_trace_score = cljs.core.nth.call(null,vec__2336,(2),null);
var log_acceptance_ratio = (function (){var x__4222__auto__ = (0);
var y__4223__auto__ = (proposed_trace_score - current_trace_score);
return ((x__4222__auto__ < y__4223__auto__) ? x__4222__auto__ : y__4223__auto__);
})();
if(cljs.core.truth_(metaprob.distributions.flip.call(null,metaprob.prelude.exp.call(null,log_acceptance_ratio)))){
return proposed_trace;
} else {
return current_trace;
}
});
;})(map__2331,map__2331__$1,model,inputs,proposal))
});

metaprob.inference.symmetric_proposal_mh_step.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.symmetric_proposal_mh_step.cljs$lang$applyTo = (function (seq2329){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2329));
});

metaprob.inference.make_gaussian_drift_proposal = (function metaprob$inference$make_gaussian_drift_proposal(addresses,width){
return (function (current_trace){
return cljs.core.reduce.call(null,(function (tr,addr){
return metaprob.trace.trace_set_value.call(null,tr,addr,metaprob.distributions.gaussian.call(null,metaprob.trace.trace_value.call(null,tr,addr),width));
}),current_trace,addresses);
});
});
metaprob.inference.gaussian_drift_mh_step = (function metaprob$inference$gaussian_drift_mh_step(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2346 = arguments.length;
var i__4731__auto___2347 = (0);
while(true){
if((i__4731__auto___2347 < len__4730__auto___2346)){
args__4736__auto__.push((arguments[i__4731__auto___2347]));

var G__2348 = (i__4731__auto___2347 + (1));
i__4731__auto___2347 = G__2348;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.gaussian_drift_mh_step.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.gaussian_drift_mh_step.cljs$core$IFn$_invoke$arity$variadic = (function (p__2343){
var map__2344 = p__2343;
var map__2344__$1 = (((((!((map__2344 == null))))?(((((map__2344.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2344.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2344):map__2344);
var model = cljs.core.get.call(null,map__2344__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2344__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var addresses = cljs.core.get.call(null,map__2344__$1,new cljs.core.Keyword(null,"addresses","addresses",-559529694));
var address_predicate = cljs.core.get.call(null,map__2344__$1,new cljs.core.Keyword(null,"address-predicate","address-predicate",-1562269396));
var width = cljs.core.get.call(null,map__2344__$1,new cljs.core.Keyword(null,"width","width",-384071477),0.1);
var proposal = (cljs.core.truth_(addresses)?((function (map__2344,map__2344__$1,model,inputs,addresses,address_predicate,width){
return (function (tr){
return metaprob.inference.make_gaussian_drift_proposal.call(null,addresses,width);
});})(map__2344,map__2344__$1,model,inputs,addresses,address_predicate,width))
:((function (map__2344,map__2344__$1,model,inputs,addresses,address_predicate,width){
return (function (tr){
return metaprob.inference.make_gaussian_drift_proposal.call(null,cljs.core.filter.call(null,address_predicate,metaprob.trace.addresses_of.call(null,tr)),width);
});})(map__2344,map__2344__$1,model,inputs,addresses,address_predicate,width))
);
return ((function (proposal,map__2344,map__2344__$1,model,inputs,addresses,address_predicate,width){
return (function (tr){
return metaprob.inference.symmetric_proposal_mh_step.call(null,new cljs.core.Keyword(null,"model","model",331153215),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"proposal","proposal",142522715),proposal.call(null,tr)).call(null,tr);
});
;})(proposal,map__2344,map__2344__$1,model,inputs,addresses,address_predicate,width))
});

metaprob.inference.gaussian_drift_mh_step.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.gaussian_drift_mh_step.cljs$lang$applyTo = (function (seq2342){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2342));
});

metaprob.inference.custom_proposal_mh_step = (function metaprob$inference$custom_proposal_mh_step(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2368 = arguments.length;
var i__4731__auto___2369 = (0);
while(true){
if((i__4731__auto___2369 < len__4730__auto___2368)){
args__4736__auto__.push((arguments[i__4731__auto___2369]));

var G__2370 = (i__4731__auto___2369 + (1));
i__4731__auto___2369 = G__2370;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.custom_proposal_mh_step.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.custom_proposal_mh_step.cljs$core$IFn$_invoke$arity$variadic = (function (p__2350){
var map__2351 = p__2350;
var map__2351__$1 = (((((!((map__2351 == null))))?(((((map__2351.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2351.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2351):map__2351);
var model = cljs.core.get.call(null,map__2351__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2351__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var proposal = cljs.core.get.call(null,map__2351__$1,new cljs.core.Keyword(null,"proposal","proposal",142522715));
return ((function (map__2351,map__2351__$1,model,inputs,proposal){
return (function (current_trace){
var vec__2353 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),current_trace);
var _ = cljs.core.nth.call(null,vec__2353,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__2353,(1),null);
var current_trace_score = cljs.core.nth.call(null,vec__2353,(2),null);
var vec__2356 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),proposal,new cljs.core.Keyword(null,"inputs","inputs",865803858),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [current_trace], null));
var proposed_trace = cljs.core.nth.call(null,vec__2356,(0),null);
var all_proposer_choices = cljs.core.nth.call(null,vec__2356,(1),null);
var ___$2 = cljs.core.nth.call(null,vec__2356,(2),null);
var vec__2359 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),proposed_trace);
var ___$3 = cljs.core.nth.call(null,vec__2359,(0),null);
var ___$4 = cljs.core.nth.call(null,vec__2359,(1),null);
var new_trace_score = cljs.core.nth.call(null,vec__2359,(2),null);
var vec__2362 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),proposal,new cljs.core.Keyword(null,"inputs","inputs",865803858),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [current_trace], null),new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),all_proposer_choices);
var ___$5 = cljs.core.nth.call(null,vec__2362,(0),null);
var ___$6 = cljs.core.nth.call(null,vec__2362,(1),null);
var forward_proposal_score = cljs.core.nth.call(null,vec__2362,(2),null);
var vec__2365 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),proposal,new cljs.core.Keyword(null,"inputs","inputs",865803858),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [proposed_trace], null),new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),proposed_trace);
var ___$7 = cljs.core.nth.call(null,vec__2365,(0),null);
var ___$8 = cljs.core.nth.call(null,vec__2365,(1),null);
var backward_proposal_score = cljs.core.nth.call(null,vec__2365,(2),null);
var log_acceptance_ratio = ((new_trace_score + backward_proposal_score) - (current_trace_score + forward_proposal_score));
if(cljs.core.truth_(metaprob.distributions.flip.call(null,metaprob.prelude.exp.call(null,log_acceptance_ratio)))){
return proposed_trace;
} else {
return current_trace;
}
});
;})(map__2351,map__2351__$1,model,inputs,proposal))
});

metaprob.inference.custom_proposal_mh_step.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.custom_proposal_mh_step.cljs$lang$applyTo = (function (seq2349){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2349));
});

metaprob.inference.make_gibbs_step = (function metaprob$inference$make_gibbs_step(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2375 = arguments.length;
var i__4731__auto___2376 = (0);
while(true){
if((i__4731__auto___2376 < len__4730__auto___2375)){
args__4736__auto__.push((arguments[i__4731__auto___2376]));

var G__2377 = (i__4731__auto___2376 + (1));
i__4731__auto___2376 = G__2377;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return metaprob.inference.make_gibbs_step.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

metaprob.inference.make_gibbs_step.cljs$core$IFn$_invoke$arity$variadic = (function (p__2372){
var map__2373 = p__2372;
var map__2373__$1 = (((((!((map__2373 == null))))?(((((map__2373.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2373.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2373):map__2373);
var model = cljs.core.get.call(null,map__2373__$1,new cljs.core.Keyword(null,"model","model",331153215));
var address = cljs.core.get.call(null,map__2373__$1,new cljs.core.Keyword(null,"address","address",559499426));
var support = cljs.core.get.call(null,map__2373__$1,new cljs.core.Keyword(null,"support","support",1392531368));
var inputs = cljs.core.get.call(null,map__2373__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
return ((function (map__2373,map__2373__$1,model,address,support,inputs){
return (function (current_trace){
var log_scores = metaprob.prelude.map.call(null,((function (map__2373,map__2373__$1,model,address,support,inputs){
return (function (value){
return cljs.core.nth.call(null,metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),metaprob.trace.trace_set_value.call(null,current_trace,address,value)),(2));
});})(map__2373,map__2373__$1,model,address,support,inputs))
,support);
return metaprob.trace.trace_set_value.call(null,current_trace,address,cljs.core.nth.call(null,support,metaprob.distributions.log_categorical.call(null,log_scores)));
});
;})(map__2373,map__2373__$1,model,address,support,inputs))
});

metaprob.inference.make_gibbs_step.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
metaprob.inference.make_gibbs_step.cljs$lang$applyTo = (function (seq2371){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2371));
});

/**
 * @param {...*} var_args
 */
metaprob.inference.make_resimulation_proposal = (function() { 
var metaprob$inference$make_resimulation_proposal__delegate = function (p__2378){
var map__2379 = p__2378;
var map__2379__$1 = (((((!((map__2379 == null))))?(((((map__2379.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__2379.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__2379):map__2379);
var model = cljs.core.get.call(null,map__2379__$1,new cljs.core.Keyword(null,"model","model",331153215));
var inputs = cljs.core.get.call(null,map__2379__$1,new cljs.core.Keyword(null,"inputs","inputs",865803858),cljs.core.PersistentVector.EMPTY);
var addresses = cljs.core.get.call(null,map__2379__$1,new cljs.core.Keyword(null,"addresses","addresses",-559529694));
var address_predicate = cljs.core.get.call(null,map__2379__$1,new cljs.core.Keyword(null,"address-predicate","address-predicate",-1562269396));
var get_addresses = (cljs.core.truth_(address_predicate)?((function (map__2379,map__2379__$1,model,inputs,addresses,address_predicate){
return (function (tr){
return cljs.core.filter.call(null,address_predicate,metaprob.trace.addresses_of.call(null,tr));
});})(map__2379,map__2379__$1,model,inputs,addresses,address_predicate))
:((function (map__2379,map__2379__$1,model,inputs,addresses,address_predicate){
return (function (tr){
return addresses;
});})(map__2379,map__2379__$1,model,inputs,addresses,address_predicate))
);
return metaprob.generative_functions.generative_function_from_traced_code.call(null,((function (get_addresses,map__2379,map__2379__$1,model,inputs,addresses,address_predicate){
return (function (at,apply_at){
return ((function (get_addresses,map__2379,map__2379__$1,model,inputs,addresses,address_predicate){
return (function (old_trace){
var addresses__$1 = get_addresses.call(null,old_trace);
var vec__2381 = metaprob.trace.partition_trace.call(null,old_trace,addresses__$1);
var _ = cljs.core.nth.call(null,vec__2381,(0),null);
var fixed_choices = cljs.core.nth.call(null,vec__2381,(1),null);
var constrained_generator = metaprob.generative_functions.make_constrained_generator.call(null,model,fixed_choices);
var vec__2384 = apply_at.call(null,cljs.core.List.EMPTY,constrained_generator,inputs);
var ___$1 = cljs.core.nth.call(null,vec__2384,(0),null);
var new_trace = cljs.core.nth.call(null,vec__2384,(1),null);
var ___$2 = cljs.core.nth.call(null,vec__2384,(2),null);
return new_trace;
});
;})(get_addresses,map__2379,map__2379__$1,model,inputs,addresses,address_predicate))
});})(get_addresses,map__2379,map__2379__$1,model,inputs,addresses,address_predicate))
,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"generative-source","generative-source",-1373253399),cljs.core.list(new cljs.core.Symbol(null,"gen","gen",1783106829,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"old-trace","old-trace",508065640,null)], null),cljs.core.list(new cljs.core.Symbol(null,"let","let",358118826,null),new cljs.core.PersistentVector(null, 8, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"addresses","addresses",1081001833,null),cljs.core.list(new cljs.core.Symbol(null,"get-addresses","get-addresses",-49784946,null),new cljs.core.Symbol(null,"old-trace","old-trace",508065640,null)),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"fixed-choices","fixed-choices",-558273060,null)], null),cljs.core.list(new cljs.core.Symbol("trace","partition-trace","trace/partition-trace",442297028,null),new cljs.core.Symbol(null,"old-trace","old-trace",508065640,null),new cljs.core.Symbol(null,"addresses","addresses",1081001833,null)),new cljs.core.Symbol(null,"constrained-generator","constrained-generator",-570594735,null),cljs.core.list(new cljs.core.Symbol("gen","make-constrained-generator","gen/make-constrained-generator",-2052380354,null),new cljs.core.Symbol(null,"model","model",1971684742,null),new cljs.core.Symbol(null,"fixed-choices","fixed-choices",-558273060,null)),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"new-trace","new-trace",-544053300,null),new cljs.core.Symbol(null,"_","_",-1201019570,null)], null),cljs.core.list(new cljs.core.Symbol(null,"apply-at","apply-at",1320572267,null),cljs.core.list(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.List.EMPTY),new cljs.core.Symbol(null,"constrained-generator","constrained-generator",-570594735,null),new cljs.core.Symbol(null,"inputs","inputs",-1788631911,null))], null),new cljs.core.Symbol(null,"new-trace","new-trace",-544053300,null)))], null));
};
var metaprob$inference$make_resimulation_proposal = function (var_args){
var p__2378 = null;
if (arguments.length > 0) {
var G__2387__i = 0, G__2387__a = new Array(arguments.length -  0);
while (G__2387__i < G__2387__a.length) {G__2387__a[G__2387__i] = arguments[G__2387__i + 0]; ++G__2387__i;}
  p__2378 = new cljs.core.IndexedSeq(G__2387__a,0,null);
} 
return metaprob$inference$make_resimulation_proposal__delegate.call(this,p__2378);};
metaprob$inference$make_resimulation_proposal.cljs$lang$maxFixedArity = 0;
metaprob$inference$make_resimulation_proposal.cljs$lang$applyTo = (function (arglist__2388){
var p__2378 = cljs.core.seq(arglist__2388);
return metaprob$inference$make_resimulation_proposal__delegate(p__2378);
});
metaprob$inference$make_resimulation_proposal.cljs$core$IFn$_invoke$arity$variadic = metaprob$inference$make_resimulation_proposal__delegate;
return metaprob$inference$make_resimulation_proposal;
})()
;
metaprob.inference.resimulation_mh_move = (function metaprob$inference$resimulation_mh_move(model,inputs,tr,addresses){
var vec__2389 = metaprob.trace.partition_trace.call(null,tr,addresses);
var current_choices = cljs.core.nth.call(null,vec__2389,(0),null);
var fixed_choices = cljs.core.nth.call(null,vec__2389,(1),null);
var vec__2392 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),tr);
var _ = cljs.core.nth.call(null,vec__2392,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__2392,(1),null);
var old_p = cljs.core.nth.call(null,vec__2392,(2),null);
var vec__2395 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),fixed_choices);
var ___$2 = cljs.core.nth.call(null,vec__2395,(0),null);
var proposed = cljs.core.nth.call(null,vec__2395,(1),null);
var new_p_over_forward_q = cljs.core.nth.call(null,vec__2395,(2),null);
var vec__2398 = metaprob.trace.partition_trace.call(null,proposed,addresses);
var ___$3 = cljs.core.nth.call(null,vec__2398,(0),null);
var reverse_move_starting_point = cljs.core.nth.call(null,vec__2398,(1),null);
var vec__2401 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),metaprob.prelude.infer_and_score,new cljs.core.Keyword(null,"inputs","inputs",865803858),new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"procedure","procedure",176722572),model,new cljs.core.Keyword(null,"inputs","inputs",865803858),inputs,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),reverse_move_starting_point], null),new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),current_choices);
var ___$4 = cljs.core.nth.call(null,vec__2401,(0),null);
var ___$5 = cljs.core.nth.call(null,vec__2401,(1),null);
var reverse_q = cljs.core.nth.call(null,vec__2401,(2),null);
var log_ratio = (new_p_over_forward_q + (reverse_q - old_p));
if(cljs.core.truth_(metaprob.distributions.flip.call(null,metaprob.prelude.exp.call(null,log_ratio)))){
return proposed;
} else {
return tr;
}
});
metaprob.inference.sillyplot = (function metaprob$inference$sillyplot(l){
var nbins = cljs.core.count.call(null,l);
var trimmed = (((nbins > (50)))?cljs.core.take.call(null,cljs.core.drop.call(null,l,((nbins - (50)) / (2))),(50)):l);
return cljs.core.println.call(null,cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.vec.call(null,metaprob.prelude.map.call(null,((function (nbins,trimmed){
return (function (p){
return p;
});})(nbins,trimmed))
,trimmed))));
});
metaprob.inference.check_bins_against_pdf = (function metaprob$inference$check_bins_against_pdf(bins,pdf){
var n_samples = cljs.core.reduce.call(null,cljs.core._PLUS_,metaprob.prelude.map.call(null,cljs.core.count,bins));
var abs = ((function (n_samples){
return (function (p1__2404_SHARP_){
return Math.abs(p1__2404_SHARP_);
});})(n_samples))
;
var bin_p = metaprob.prelude.map.call(null,((function (n_samples,abs){
return (function (bin){
return (cljs.core.count.call(null,bin) / n_samples);
});})(n_samples,abs))
,bins);
var bin_q = metaprob.prelude.map.call(null,((function (n_samples,abs,bin_p){
return (function (bin){
var bincount = cljs.core.count.call(null,bin);
return ((cljs.core.reduce.call(null,cljs.core._PLUS_,metaprob.prelude.map.call(null,pdf,bin)) / bincount) * ((cljs.core.nth.call(null,bin,(bincount - (1))) - cljs.core.nth.call(null,bin,(0))) * ((bincount + (1)) / (bincount * 1.0))));
});})(n_samples,abs,bin_p))
,bins);
var discrepancies = cljs.core.map.call(null,((function (n_samples,abs,bin_p,bin_q){
return (function (p1__2405_SHARP_,p2__2406_SHARP_){
return abs.call(null,(p1__2405_SHARP_ - p2__2406_SHARP_));
});})(n_samples,abs,bin_p,bin_q))
,bin_p,bin_q);
var trimmed = cljs.core.rest.call(null,cljs.core.reverse.call(null,cljs.core.rest.call(null,discrepancies)));
var normalization = (cljs.core.count.call(null,discrepancies) / (cljs.core.count.call(null,trimmed) * 1.0));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(normalization * cljs.core.reduce.call(null,cljs.core._PLUS_,trimmed)),bin_p,bin_q], null);
});
metaprob.inference.check_samples_against_pdf = (function metaprob$inference$check_samples_against_pdf(samples,pdf,nbins){
var samples__$1 = cljs.core.vec.call(null,cljs.core.sort.call(null,samples));
var n_samples = cljs.core.count.call(null,samples__$1);
var bin_size = (n_samples / nbins);
var bins = metaprob.prelude.map.call(null,((function (samples__$1,n_samples,bin_size){
return (function (i){
var start = ((i * bin_size) | (0));
var end = (((i + (1)) * bin_size) | (0));
return cljs.core.subvec.call(null,samples__$1,start,end);
});})(samples__$1,n_samples,bin_size))
,cljs.core.range.call(null,nbins));
return metaprob.inference.check_bins_against_pdf.call(null,bins,pdf);
});
metaprob.inference.report_on_elapsed_time = (function metaprob$inference$report_on_elapsed_time(tag,thunk){
var time = (function (){
return (new Date()).getTime();
});
var start = time.call(null);
var ret = thunk.call(null);
var t = Math.round(((time.call(null) - start) / 1000000.0));
if((t > (1))){
cljs.core.print.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(tag),": elapsed time ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(t)," sec\n"].join(''));
} else {
}

return ret;
});
metaprob.inference.assay = (function metaprob$inference$assay(tag,sampler,nsamples,pdf,nbins,threshold){
return metaprob.inference.report_on_elapsed_time.call(null,tag,(function (){
var vec__2407 = metaprob.inference.check_samples_against_pdf.call(null,metaprob.prelude.map.call(null,sampler,cljs.core.range.call(null,nsamples)),pdf,nbins);
var badness = cljs.core.nth.call(null,vec__2407,(0),null);
var bin_p = cljs.core.nth.call(null,vec__2407,(1),null);
var bin_q = cljs.core.nth.call(null,vec__2407,(2),null);
if((((badness > threshold)) || ((badness < (threshold / (2)))))){
cljs.core.println.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(tag),"."," n: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(nsamples)," bins: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(nbins)," badness: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(badness)," threshold: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(threshold)].join(''));

metaprob.inference.sillyplot.call(null,bin_p);

metaprob.inference.sillyplot.call(null,bin_q);
} else {
}

return (badness < (threshold * 1.5));
}));
});
metaprob.inference.badness = (function metaprob$inference$badness(sampler,nsamples,pdf,nbins){
var vec__2410 = metaprob.inference.check_samples_against_pdf.call(null,metaprob.prelude.map.call(null,sampler,cljs.core.range.call(null,nsamples)),pdf,nbins);
var badness = cljs.core.nth.call(null,vec__2410,(0),null);
var bin_p = cljs.core.nth.call(null,vec__2410,(1),null);
var bin_q = cljs.core.nth.call(null,vec__2410,(2),null);
return badness;
});

//# sourceMappingURL=inference.js.map
