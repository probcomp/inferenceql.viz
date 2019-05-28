// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.cgpm.main');
goog.require('cljs.core');
goog.require('metaprob.prelude');
goog.require('metaprob.distributions');
goog.require('inferdb.cgpm.utils');
goog.require('metaprob.inference');
inferdb.cgpm.main.make_ranged_real_type = (function inferdb$cgpm$main$make_ranged_real_type(low,high){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),["real[low=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(low)," high=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(high),"]"].join(''),new cljs.core.Keyword(null,"valid?","valid?",-212412379),cljs.core.number_QMARK_,new cljs.core.Keyword(null,"in-support?","in-support?",205932104),(function (x){
return (((low < x)) && ((x < high)));
}),new cljs.core.Keyword(null,"base-measure","base-measure",846358992),new cljs.core.Keyword(null,"continuous","continuous",1446172908)], null);
});
inferdb.cgpm.main.make_ranged_integer_type = (function inferdb$cgpm$main$make_ranged_integer_type(low,high){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),["integer[low=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(low)," high=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(high),"]"].join(''),new cljs.core.Keyword(null,"valid?","valid?",-212412379),cljs.core.number_QMARK_,new cljs.core.Keyword(null,"in-support?","in-support?",205932104),(function (x){
return ((cljs.core.int_QMARK_.call(null,x)) && ((low <= x)) && ((x <= high)));
}),new cljs.core.Keyword(null,"base-measure","base-measure",846358992),new cljs.core.Keyword(null,"discrete","discrete",-1733670397)], null);
});
inferdb.cgpm.main.make_nominal_type = (function inferdb$cgpm$main$make_nominal_type(categories){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),["nominal[low=",cljs.core.str.cljs$core$IFn$_invoke$arity$1(categories),"]"].join(''),new cljs.core.Keyword(null,"valid?","valid?",-212412379),(function (x){
return cljs.core.contains_QMARK_.call(null,categories,x);
}),new cljs.core.Keyword(null,"in-support?","in-support?",205932104),(function (x){
return cljs.core.contains_QMARK_.call(null,categories,x);
}),new cljs.core.Keyword(null,"base-measure","base-measure",846358992),new cljs.core.Keyword(null,"discrete","discrete",-1733670397)], null);
});
inferdb.cgpm.main.real_type = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),"real",new cljs.core.Keyword(null,"valid?","valid?",-212412379),cljs.core.number_QMARK_,new cljs.core.Keyword(null,"in-support?","in-support?",205932104),cljs.core.number_QMARK_,new cljs.core.Keyword(null,"base-measure","base-measure",846358992),new cljs.core.Keyword(null,"continuous","continuous",1446172908)], null);
inferdb.cgpm.main.integer_type = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),"integer",new cljs.core.Keyword(null,"valid?","valid?",-212412379),cljs.core.number_QMARK_,new cljs.core.Keyword(null,"in-support?","in-support?",205932104),cljs.core.int_QMARK_,new cljs.core.Keyword(null,"base-measure","base-measure",846358992),new cljs.core.Keyword(null,"discrete","discrete",-1733670397)], null);
inferdb.cgpm.main.make_cgpm = (function inferdb$cgpm$main$make_cgpm(proc,output_addrs_types,input_addrs_types,output_address_map,input_address_map){
var output_addrs = cljs.core.set.call(null,cljs.core.keys.call(null,output_addrs_types));
var input_addrs = cljs.core.set.call(null,cljs.core.keys.call(null,input_addrs_types));
inferdb.cgpm.utils.assert_no_overlap.call(null,output_addrs,input_addrs,new cljs.core.Keyword(null,"outputs","outputs",-1896513034),new cljs.core.Keyword(null,"inputs","inputs",865803858));

inferdb.cgpm.utils.assert_has_keys.call(null,output_address_map,output_addrs);

inferdb.cgpm.utils.assert_has_keys.call(null,input_address_map,input_addrs);

inferdb.cgpm.utils.assert_valid_output_address_map.call(null,output_address_map);

inferdb.cgpm.utils.assert_valid_input_address_map.call(null,input_address_map);

return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"proc","proc",2011328965),proc,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407),output_addrs_types,new cljs.core.Keyword(null,"input-addrs-types","input-addrs-types",-1207695273),input_addrs_types,new cljs.core.Keyword(null,"output-address-map","output-address-map",-434413931),output_address_map,new cljs.core.Keyword(null,"input-address-map","input-address-map",-2038492917),input_address_map], null);
});
inferdb.cgpm.main.validate_cgpm_logpdf = (function inferdb$cgpm$main$validate_cgpm_logpdf(cgpm,target_addrs_vals,constraint_addrs_vals,input_addrs_vals){
var target_addrs = cljs.core.set.call(null,cljs.core.keys.call(null,target_addrs_vals));
var constraint_addrs = cljs.core.set.call(null,cljs.core.keys.call(null,constraint_addrs_vals));
var input_addrs = cljs.core.set.call(null,cljs.core.keys.call(null,input_addrs_vals));
inferdb.cgpm.utils.assert_no_overlap.call(null,target_addrs,constraint_addrs,new cljs.core.Keyword(null,"targets","targets",2014963406),new cljs.core.Keyword(null,"constraints","constraints",422775616));

inferdb.cgpm.utils.assert_has_keys.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407)),target_addrs);

inferdb.cgpm.utils.assert_has_keys.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407)),constraint_addrs);

inferdb.cgpm.utils.assert_has_keys.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"input-addrs-types","input-addrs-types",-1207695273)),input_addrs);

inferdb.cgpm.utils.validate_row.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407)),target_addrs_vals,false);

inferdb.cgpm.utils.validate_row.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407)),constraint_addrs_vals,false);

return inferdb.cgpm.utils.validate_row.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"input-addrs-types","input-addrs-types",-1207695273)),input_addrs_vals,true);
});
inferdb.cgpm.main.cgpm_logpdf = (function inferdb$cgpm$main$cgpm_logpdf(cgpm,target_addrs_vals,constraint_addrs_vals,input_addrs_vals){
var target_addrs_vals_SINGLEQUOTE_ = inferdb.cgpm.utils.rekey_addrs_vals.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-address-map","output-address-map",-434413931)),target_addrs_vals);
var constraint_addrs_vals_SINGLEQUOTE_ = inferdb.cgpm.utils.rekey_addrs_vals.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-address-map","output-address-map",-434413931)),constraint_addrs_vals);
var target_constraint_addrs_vals = cljs.core.merge.call(null,target_addrs_vals_SINGLEQUOTE_,constraint_addrs_vals_SINGLEQUOTE_);
var input_args = inferdb.cgpm.utils.extract_input_list.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"input-address-map","input-address-map",-2038492917)),input_addrs_vals);
var vec__2609 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Keyword(null,"proc","proc",2011328965).cljs$core$IFn$_invoke$arity$1(cgpm),new cljs.core.Keyword(null,"inputs","inputs",865803858),input_args,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),target_constraint_addrs_vals);
var retval = cljs.core.nth.call(null,vec__2609,(0),null);
var trace = cljs.core.nth.call(null,vec__2609,(1),null);
var log_weight_numer = cljs.core.nth.call(null,vec__2609,(2),null);
var log_weight_denom = ((cljs.core.empty_QMARK_.call(null,constraint_addrs_vals_SINGLEQUOTE_))?(0):(function (){var vec__2612 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Keyword(null,"proc","proc",2011328965).cljs$core$IFn$_invoke$arity$1(cgpm),new cljs.core.Keyword(null,"inputs","inputs",865803858),input_args,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),constraint_addrs_vals_SINGLEQUOTE_);
var retval__$1 = cljs.core.nth.call(null,vec__2612,(0),null);
var trace__$1 = cljs.core.nth.call(null,vec__2612,(1),null);
var weight = cljs.core.nth.call(null,vec__2612,(2),null);
return weight;
})());
return (log_weight_numer - log_weight_denom);
});
inferdb.cgpm.main.validate_cgpm_simulate = (function inferdb$cgpm$main$validate_cgpm_simulate(cgpm,target_addrs,constraint_addrs_vals,input_addrs_vals){
var constraint_addrs = cljs.core.set.call(null,cljs.core.keys.call(null,constraint_addrs_vals));
var input_addrs = cljs.core.set.call(null,cljs.core.keys.call(null,input_addrs_vals));
inferdb.cgpm.utils.assert_no_overlap.call(null,target_addrs,constraint_addrs,new cljs.core.Keyword(null,"targets","targets",2014963406),new cljs.core.Keyword(null,"constraints","constraints",422775616));

inferdb.cgpm.utils.assert_has_keys.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407)),cljs.core.set.call(null,target_addrs));

inferdb.cgpm.utils.assert_has_keys.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407)),constraint_addrs);

inferdb.cgpm.utils.assert_has_keys.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"input-addrs-types","input-addrs-types",-1207695273)),input_addrs);

inferdb.cgpm.utils.validate_row.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407)),constraint_addrs_vals,false);

return inferdb.cgpm.utils.validate_row.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"input-addrs-types","input-addrs-types",-1207695273)),input_addrs_vals,true);
});
inferdb.cgpm.main.cgpm_simulate = (function inferdb$cgpm$main$cgpm_simulate(cgpm,target_addrs,constraint_addrs_vals,input_addrs_vals,num_samples){
inferdb.cgpm.main.validate_cgpm_simulate.call(null,cgpm,target_addrs,constraint_addrs_vals,input_addrs_vals);

var target_addrs_SINGLEQUOTE_ = inferdb.cgpm.utils.rekey_addrs.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-address-map","output-address-map",-434413931)),target_addrs);
var constraint_addrs_vals_SINGLEQUOTE_ = inferdb.cgpm.utils.rekey_addrs_vals.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-address-map","output-address-map",-434413931)),constraint_addrs_vals);
var input_args = inferdb.cgpm.utils.extract_input_list.call(null,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"input-address-map","input-address-map",-2038492917)),input_addrs_vals);
return cljs.core.repeatedly.call(null,num_samples,((function (target_addrs_SINGLEQUOTE_,constraint_addrs_vals_SINGLEQUOTE_,input_args){
return (function (){
var vec__2615 = metaprob.prelude.infer_and_score.call(null,new cljs.core.Keyword(null,"procedure","procedure",176722572),new cljs.core.Keyword(null,"proc","proc",2011328965).cljs$core$IFn$_invoke$arity$1(cgpm),new cljs.core.Keyword(null,"inputs","inputs",865803858),input_args,new cljs.core.Keyword(null,"observation-trace","observation-trace",-602020813),constraint_addrs_vals_SINGLEQUOTE_);
var retval = cljs.core.nth.call(null,vec__2615,(0),null);
var trace = cljs.core.nth.call(null,vec__2615,(1),null);
var log_weight_numer = cljs.core.nth.call(null,vec__2615,(2),null);
return inferdb.cgpm.utils.extract_samples_from_trace.call(null,trace,target_addrs,cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-address-map","output-address-map",-434413931)));
});})(target_addrs_SINGLEQUOTE_,constraint_addrs_vals_SINGLEQUOTE_,input_args))
);
});
inferdb.cgpm.main.compute_mi = (function inferdb$cgpm$main$compute_mi(cgpm,target_addrs_0,target_addrs_1,constraint_addrs_vals,input_addrs_vals,num_samples){
var samples = inferdb.cgpm.main.cgpm_simulate.call(null,cgpm,cljs.core.into.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.concat.call(null,target_addrs_0,target_addrs_1)),constraint_addrs_vals,input_addrs_vals,num_samples);
var logp_joint = metaprob.prelude.map.call(null,((function (samples){
return (function (sample){
return inferdb.cgpm.main.cgpm_logpdf.call(null,cgpm,sample,constraint_addrs_vals,input_addrs_vals);
});})(samples))
,samples);
var logp_marginal_0 = metaprob.prelude.map.call(null,((function (samples,logp_joint){
return (function (sample){
return inferdb.cgpm.main.cgpm_logpdf.call(null,cgpm,cljs.core.select_keys.call(null,sample,target_addrs_0),constraint_addrs_vals,input_addrs_vals);
});})(samples,logp_joint))
,samples);
var logp_marginal_1 = metaprob.prelude.map.call(null,((function (samples,logp_joint,logp_marginal_0){
return (function (sample){
return inferdb.cgpm.main.cgpm_logpdf.call(null,cgpm,cljs.core.select_keys.call(null,sample,target_addrs_1),constraint_addrs_vals,input_addrs_vals);
});})(samples,logp_joint,logp_marginal_0))
,samples);
return (inferdb.cgpm.utils.compute_avg.call(null,logp_joint) - (inferdb.cgpm.utils.compute_avg.call(null,logp_marginal_0) + inferdb.cgpm.utils.compute_avg.call(null,logp_marginal_1)));
});
inferdb.cgpm.main.cgpm_mutual_information = (function inferdb$cgpm$main$cgpm_mutual_information(cgpm,target_addrs_0,target_addrs_1,controlling_addrs,constraint_addrs_vals,input_addrs_vals,num_samples_inner,num_samples_outer){
inferdb.cgpm.utils.assert_no_overlap.call(null,cljs.core.set.call(null,controlling_addrs),cljs.core.set.call(null,cljs.core.keys.call(null,constraint_addrs_vals)),new cljs.core.Keyword(null,"constraint-addrs","constraint-addrs",-788172200),new cljs.core.Keyword(null,"constraint-addrs-vals","constraint-addrs-vals",-1039619429));

if(cljs.core._EQ_.call(null,cljs.core.count.call(null,controlling_addrs),(0))){
return inferdb.cgpm.main.compute_mi.call(null,cgpm,target_addrs_0,target_addrs_1,constraint_addrs_vals,input_addrs_vals,num_samples_inner);
} else {
var samples = inferdb.cgpm.main.cgpm_simulate.call(null,cgpm,controlling_addrs,constraint_addrs_vals,input_addrs_vals,num_samples_outer);
var constraints_merged = metaprob.prelude.map.call(null,((function (samples){
return (function (sample){
return cljs.core.merge.call(null,sample,constraint_addrs_vals);
});})(samples))
,samples);
var mutinf_values = metaprob.prelude.map.call(null,((function (samples,constraints_merged){
return (function (constraints){
return inferdb.cgpm.main.compute_mi.call(null,cgpm,target_addrs_0,target_addrs_1,constraints,input_addrs_vals,num_samples_inner);
});})(samples,constraints_merged))
,constraints_merged);
return inferdb.cgpm.utils.compute_avg.call(null,mutinf_values);
}
});
inferdb.cgpm.main.validate_cgpm_kl_divergence = (function inferdb$cgpm$main$validate_cgpm_kl_divergence(cgpm,target_addrs_0,target_addrs_1){
inferdb.cgpm.utils.assert_same_length.call(null,target_addrs_0,target_addrs_1,new cljs.core.Keyword(null,"target-addrs-0","target-addrs-0",2011173192),new cljs.core.Keyword(null,"target-addrs-1","target-addrs-1",-763671255));

var output_addrs_types = cljs.core.get.call(null,cgpm,new cljs.core.Keyword(null,"output-addrs-types","output-addrs-types",1951273407));
var gbm = ((function (output_addrs_types){
return (function (t){
return cljs.core.get.call(null,inferdb.cgpm.utils.safe_get.call(null,output_addrs_types,t),new cljs.core.Keyword(null,"base-measure","base-measure",846358992));
});})(output_addrs_types))
;
var base_measures_0 = metaprob.prelude.map.call(null,gbm,target_addrs_0);
var base_measures_1 = metaprob.prelude.map.call(null,gbm,target_addrs_1);
if(cljs.core._EQ_.call(null,base_measures_0,base_measures_1)){
return null;
} else {
throw (new Error(["Assert failed: ",["targets ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(target_addrs_0)," and ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(target_addrs_1),"must have same base measures"].join(''),"\n","(= base-measures-0 base-measures-1)"].join('')));
}
});
inferdb.cgpm.main.cgpm_kl_divergence = (function inferdb$cgpm$main$cgpm_kl_divergence(cgpm,target_addrs_0,target_addrs_1,constraint_addrs_vals_0,constraint_addrs_vals_1,input_addrs_vals,num_samples){
inferdb.cgpm.main.validate_cgpm_kl_divergence.call(null,cgpm,target_addrs_0,target_addrs_1);

var samples_p = inferdb.cgpm.main.cgpm_simulate.call(null,cgpm,target_addrs_0,constraint_addrs_vals_0,input_addrs_vals,num_samples);
var keymap = cljs.core.zipmap.call(null,target_addrs_0,target_addrs_1);
var samples_q = metaprob.prelude.map.call(null,((function (samples_p,keymap){
return (function (sample){
return inferdb.cgpm.utils.rekey_dict.call(null,keymap,sample);
});})(samples_p,keymap))
,samples_p);
var logp_p = metaprob.prelude.map.call(null,((function (samples_p,keymap,samples_q){
return (function (sample){
return inferdb.cgpm.main.cgpm_logpdf.call(null,cgpm,sample,constraint_addrs_vals_0,input_addrs_vals);
});})(samples_p,keymap,samples_q))
,samples_p);
var logp_q = metaprob.prelude.map.call(null,((function (samples_p,keymap,samples_q,logp_p){
return (function (sample){
return inferdb.cgpm.main.cgpm_logpdf.call(null,cgpm,sample,constraint_addrs_vals_1,input_addrs_vals);
});})(samples_p,keymap,samples_q,logp_p))
,samples_q);
return (inferdb.cgpm.utils.compute_avg.call(null,logp_p) - inferdb.cgpm.utils.compute_avg.call(null,logp_q));
});

//# sourceMappingURL=main.js.map
