// Compiled by ClojureScript 1.10.520 {}
goog.provide('metaprob.distributions');
goog.require('cljs.core');
goog.require('metaprob.prelude');
metaprob.distributions.exactly = metaprob.prelude.make_primitive.call(null,(function (x){
return x;
}),(function (y,p__2224){
var vec__2225 = p__2224;
var x = cljs.core.nth.call(null,vec__2225,(0),null);
if(cljs.core.not_EQ_.call(null,y,x)){
return metaprob.prelude.negative_infinity;
} else {
return (0);
}
}));
metaprob.distributions.uniform = metaprob.prelude.make_primitive.call(null,(function (a,b){
return metaprob.prelude.sample_uniform.call(null,a,b);
}),(function (x,p__2228){
var vec__2229 = p__2228;
var a = cljs.core.nth.call(null,vec__2229,(0),null);
var b = cljs.core.nth.call(null,vec__2229,(1),null);
if((((a <= x)) && ((x <= b)))){
return (- metaprob.prelude.log.call(null,(b - a)));
} else {
return metaprob.prelude.negative_infinity;
}
}));
metaprob.distributions.uniform_discrete = metaprob.prelude.make_primitive.call(null,(function (items){
return cljs.core.nth.call(null,items,Math.floor((metaprob.prelude.sample_uniform.call(null) * cljs.core.count.call(null,items))));
}),(function (item,p__2233){
var vec__2234 = p__2233;
var items = cljs.core.nth.call(null,vec__2234,(0),null);
return (metaprob.prelude.log.call(null,cljs.core.count.call(null,cljs.core.filter.call(null,((function (vec__2234,items){
return (function (p1__2232_SHARP_){
return cljs.core._EQ_.call(null,p1__2232_SHARP_,item);
});})(vec__2234,items))
,items))) - metaprob.prelude.log.call(null,cljs.core.count.call(null,items)));
}));
metaprob.distributions.flip = metaprob.prelude.make_primitive.call(null,(function (weight){
return (metaprob.prelude.sample_uniform.call(null) < weight);
}),(function (value,p__2237){
var vec__2238 = p__2237;
var weight = cljs.core.nth.call(null,vec__2238,(0),null);
if(cljs.core.truth_(value)){
return metaprob.prelude.log.call(null,weight);
} else {
return metaprob.prelude.log1p.call(null,(- weight));
}
}));
metaprob.distributions.normalize_numbers = (function metaprob$distributions$normalize_numbers(nums){
var total = cljs.core.reduce.call(null,cljs.core._PLUS_,nums);
return metaprob.prelude.map.call(null,((function (total){
return (function (p1__2241_SHARP_){
return (p1__2241_SHARP_ / total);
});})(total))
,nums);
});
metaprob.distributions.categorical = metaprob.prelude.make_primitive.call(null,(function (probs){
if(cljs.core.map_QMARK_.call(null,probs)){
return cljs.core.nth.call(null,cljs.core.keys.call(null,probs),metaprob.distributions.categorical.call(null,metaprob.distributions.normalize_numbers.call(null,cljs.core.vals.call(null,probs))));
} else {
var total = cljs.core.reduce.call(null,cljs.core._PLUS_,probs);
var r = (metaprob.prelude.sample_uniform.call(null) * total);
var i = (0);
var sum = (0);
while(true){
if((r < (cljs.core.nth.call(null,probs,i) + sum))){
return i;
} else {
var G__2246 = (i + (1));
var G__2247 = (cljs.core.nth.call(null,probs,i) + sum);
i = G__2246;
sum = G__2247;
continue;
}
break;
}
}
}),(function (i,p__2242){
var vec__2243 = p__2242;
var probs = cljs.core.nth.call(null,vec__2243,(0),null);
if(cljs.core.map_QMARK_.call(null,probs)){
if((!(cljs.core.contains_QMARK_.call(null,probs,i)))){
return metaprob.prelude.negative_infinity;
} else {
return (metaprob.prelude.log.call(null,cljs.core.get.call(null,probs,i)) - metaprob.prelude.log.call(null,cljs.core.reduce.call(null,cljs.core._PLUS_,cljs.core.vals.call(null,probs))));
}
} else {
return metaprob.prelude.log.call(null,cljs.core.nth.call(null,probs,i));
}
}));
metaprob.distributions.logsumexp = (function metaprob$distributions$logsumexp(scores){
var max_score = metaprob.prelude.apply.call(null,cljs.core.max,scores);
var weights = metaprob.prelude.map.call(null,((function (max_score){
return (function (p1__2248_SHARP_){
return Math.exp((p1__2248_SHARP_ - max_score));
});})(max_score))
,scores);
return (Math.log(cljs.core.reduce.call(null,cljs.core._PLUS_,weights)) + max_score);
});
metaprob.distributions.logmeanexp = (function metaprob$distributions$logmeanexp(scores){
return (metaprob.distributions.logsumexp.call(null,scores) - metaprob.prelude.log.call(null,cljs.core.count.call(null,scores)));
});
metaprob.distributions.log_scores_to_probabilities = (function metaprob$distributions$log_scores_to_probabilities(scores){
var log_normalizer = metaprob.distributions.logsumexp.call(null,scores);
return metaprob.prelude.map.call(null,((function (log_normalizer){
return (function (p1__2249_SHARP_){
return Math.exp((p1__2249_SHARP_ - log_normalizer));
});})(log_normalizer))
,scores);
});
metaprob.distributions.log_categorical = metaprob.prelude.make_primitive.call(null,(function (scores){
var probs = ((cljs.core.map_QMARK_.call(null,scores))?cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,cljs.core.map.call(null,(function (a,b){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [a,b], null);
}),cljs.core.keys.call(null,scores),metaprob.distributions.log_scores_to_probabilities.call(null,cljs.core.vals.call(null,scores)))):metaprob.distributions.log_scores_to_probabilities.call(null,scores));
return metaprob.distributions.categorical.call(null,probs);
}),(function (i,p__2250){
var vec__2251 = p__2250;
var scores = cljs.core.nth.call(null,vec__2251,(0),null);
var probs = ((cljs.core.map_QMARK_.call(null,scores))?cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,cljs.core.map.call(null,((function (vec__2251,scores){
return (function (a,b){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [a,b], null);
});})(vec__2251,scores))
,cljs.core.keys.call(null,scores),metaprob.distributions.log_scores_to_probabilities.call(null,cljs.core.vals.call(null,scores)))):metaprob.distributions.log_scores_to_probabilities.call(null,scores));
if(cljs.core.map_QMARK_.call(null,probs)){
if((!(cljs.core.contains_QMARK_.call(null,probs,i)))){
return metaprob.prelude.negative_infinity;
} else {
return (metaprob.prelude.log.call(null,cljs.core.get.call(null,probs,i)) - metaprob.prelude.log.call(null,cljs.core.reduce.call(null,cljs.core._PLUS_,cljs.core.vals.call(null,probs))));
}
} else {
return metaprob.prelude.log.call(null,cljs.core.nth.call(null,probs,i));
}
}));
metaprob.distributions.generate_gaussian = (function metaprob$distributions$generate_gaussian(mu,sigma){
return (mu + ((sigma * Math.sqrt(((-2) * Math.log(metaprob.prelude.sample_uniform.call(null))))) * Math.cos((((2) * Math.PI) * metaprob.prelude.sample_uniform.call(null)))));
});
metaprob.distributions.standard_gaussian_log_density = (function metaprob$distributions$standard_gaussian_log_density(x){
return (-0.5 * (Math.log(((2) * Math.PI)) + (x * x)));
});
metaprob.distributions.score_gaussian = (function metaprob$distributions$score_gaussian(x,p__2254){
var vec__2255 = p__2254;
var mu = cljs.core.nth.call(null,vec__2255,(0),null);
var sigma = cljs.core.nth.call(null,vec__2255,(1),null);
return (metaprob.distributions.standard_gaussian_log_density.call(null,((x - mu) / sigma)) - Math.log(sigma));
});
metaprob.distributions.gaussian = metaprob.prelude.make_primitive.call(null,metaprob.distributions.generate_gaussian,metaprob.distributions.score_gaussian);
metaprob.distributions.geometric = metaprob.prelude.make_primitive.call(null,(function (p){
var i = (0);
while(true){
if(cljs.core.truth_(metaprob.distributions.flip.call(null,p))){
var G__2262 = (i + (1));
i = G__2262;
continue;
} else {
return i;
}
break;
}
}),(function (v,p__2258){
var vec__2259 = p__2258;
var p = cljs.core.nth.call(null,vec__2259,(0),null);
return (metaprob.prelude.log1p.call(null,(- p)) + (metaprob.prelude.log.call(null,p) * v));
}));

//# sourceMappingURL=distributions.js.map
