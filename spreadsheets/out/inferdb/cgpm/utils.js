// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.cgpm.utils');
goog.require('cljs.core');
goog.require('metaprob.prelude');
goog.require('metaprob.inference');
inferdb.cgpm.utils.compute_avg = (function inferdb$cgpm$utils$compute_avg(items){
return (cljs.core.reduce.call(null,cljs.core._PLUS_,(0),items) / cljs.core.count.call(null,items));
});
inferdb.cgpm.utils.safe_get = (function inferdb$cgpm$utils$safe_get(collection,item){
if(cljs.core.contains_QMARK_.call(null,collection,item)){
return cljs.core.get.call(null,collection,item);
} else {
throw (new Error(["Assert failed: ",["no such key ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(item)," in set ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.keys.call(null,collection))].join(''),"\n","false"].join('')));

}
});
inferdb.cgpm.utils.validate_cell = (function inferdb$cgpm$utils$validate_cell(stattype,value){
if(cljs.core.truth_(cljs.core.get.call(null,stattype,new cljs.core.Keyword(null,"valid?","valid?",-212412379)).call(null,value))){
return null;
} else {
throw (new Error(["Assert failed: ",["invalid value ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)," for stattype ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.get.call(null,stattype,new cljs.core.Keyword(null,"name","name",1843675177)))].join(''),"\n","((get stattype :valid?) value)"].join('')));
}
});
inferdb.cgpm.utils.validate_row = (function inferdb$cgpm$utils$validate_row(addrs_types,addrs_vals,check_all_exist_QMARK_){
var violations = cljs.core.filter.call(null,(function (p__2591){
var vec__2592 = p__2591;
var k = cljs.core.nth.call(null,vec__2592,(0),null);
var v = cljs.core.nth.call(null,vec__2592,(1),null);
return inferdb.cgpm.utils.validate_cell.call(null,inferdb.cgpm.utils.safe_get.call(null,addrs_types,k),v);
}),addrs_vals);
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,violations),(0))){
} else {
throw (new Error(["Assert failed: ",["invalid values ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(violations)," for types ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(addrs_types)].join(''),"\n","(= (count violations) 0)"].join('')));
}

if(cljs.core.truth_(check_all_exist_QMARK_)){
if(cljs.core._EQ_.call(null,cljs.core.set.call(null,cljs.core.keys.call(null,addrs_types)),cljs.core.set.call(null,cljs.core.keys.call(null,addrs_vals)))){
return null;
} else {
throw (new Error(["Assert failed: ",["row ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(addrs_vals)," must have values for ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(addrs_types)].join(''),"\n","(= (set (keys addrs-types)) (set (keys addrs-vals)))"].join('')));
}
} else {
return null;
}
});
inferdb.cgpm.utils.assert_same_length = (function inferdb$cgpm$utils$assert_same_length(set_a,set_b,name_a,name_b){
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,set_a),cljs.core.count.call(null,set_b))){
return null;
} else {
throw (new Error(["Assert failed: ",[cljs.core.str.cljs$core$IFn$_invoke$arity$1(name_a)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(set_a)," and ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(name_b)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(set_b)," must have same length"].join(''),"\n","(= (count set-a) (count set-b))"].join('')));
}
});
inferdb.cgpm.utils.assert_no_overlap = (function inferdb$cgpm$utils$assert_no_overlap(vector_a,set_b,name_a,name_b){
var set_a = cljs.core.set.call(null,vector_a);
var overlap = clojure.set.intersection.call(null,set_a,set_b);
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,overlap),(0))){
return null;
} else {
throw (new Error(["Assert failed: ",[cljs.core.str.cljs$core$IFn$_invoke$arity$1(name_a)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(set_a)," and ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(name_b)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(set_b)," must be disjoint"].join(''),"\n","(= (count overlap) 0)"].join('')));
}
});
inferdb.cgpm.utils.assert_has_keys = (function inferdb$cgpm$utils$assert_has_keys(collection,items){
var collection_keys = cljs.core.set.call(null,cljs.core.keys.call(null,collection));
var invalid_items = clojure.set.difference.call(null,items,collection_keys);
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,invalid_items),(0))){
return null;
} else {
throw (new Error(["Assert failed: ",["key set ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(collection_keys)," does not have some of the keys in ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(items)].join(''),"\n","(= (count invalid-items) 0)"].join('')));
}
});
inferdb.cgpm.utils.assert_valid_input_address_map = (function inferdb$cgpm$utils$assert_valid_input_address_map(input_address_map){
var invalid_address_values = cljs.core.filter.call(null,(function (p__2595){
var vec__2596 = p__2595;
var k = cljs.core.nth.call(null,vec__2596,(0),null);
var v = cljs.core.nth.call(null,vec__2596,(1),null);
return (!(cljs.core.int_QMARK_.call(null,v)));
}),input_address_map);
var _ = ((cljs.core._EQ_.call(null,cljs.core.count.call(null,invalid_address_values),(0)))?null:(function(){throw (new Error(["Assert failed: ",["input addresses must map to integers ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(input_address_map)].join(''),"\n","(= (count invalid-address-values) 0)"].join('')))})());
var sorted_address_values = cljs.core.sort.call(null,cljs.core.vals.call(null,input_address_map));
var num_inputs = cljs.core.count.call(null,sorted_address_values);
if(cljs.core._EQ_.call(null,cljs.core.range.call(null,(0),num_inputs),sorted_address_values)){
return null;
} else {
throw (new Error(["Assert failed: ",["input addresses must map to consecutive integers ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(input_address_map)].join(''),"\n","(= (range 0 num-inputs) sorted-address-values)"].join('')));
}
});
inferdb.cgpm.utils.assert_valid_output_address_map = (function inferdb$cgpm$utils$assert_valid_output_address_map(output_address_map){
var values = cljs.core.vals.call(null,output_address_map);
if(cljs.core._EQ_.call(null,cljs.core.count.call(null,cljs.core.set.call(null,values)),cljs.core.count.call(null,values))){
return null;
} else {
throw (new Error(["Assert failed: ",["addresses should have distinct values ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(output_address_map)].join(''),"\n","(= (count (set values)) (count values))"].join('')));
}
});
inferdb.cgpm.utils.rekey_addrs_vals = (function inferdb$cgpm$utils$rekey_addrs_vals(address_map,addrs_vals){
var converter = (function (p__2599){
var vec__2600 = p__2599;
var k = cljs.core.nth.call(null,vec__2600,(0),null);
var v = cljs.core.nth.call(null,vec__2600,(1),null);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [inferdb.cgpm.utils.safe_get.call(null,address_map,k),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),v], null)], null);
});
return cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,metaprob.prelude.map.call(null,converter,addrs_vals));
});
inferdb.cgpm.utils.rekey_addrs = (function inferdb$cgpm$utils$rekey_addrs(address_map,addrs){
var convert = (function (k){
return inferdb.cgpm.utils.safe_get.call(null,address_map,k);
});
return metaprob.prelude.map.call(null,convert,addrs);
});
inferdb.cgpm.utils.extract_input_list = (function inferdb$cgpm$utils$extract_input_list(address_map,addrs_vals){
var compr = (function (k1,k2){
return (cljs.core.get.call(null,address_map,k1) < cljs.core.get.call(null,address_map,k2));
});
var ordered_keys = cljs.core.sort.call(null,compr,cljs.core.keys.call(null,addrs_vals));
return metaprob.prelude.map.call(null,((function (compr,ordered_keys){
return (function (k){
return cljs.core.get.call(null,addrs_vals,k);
});})(compr,ordered_keys))
,ordered_keys);
});
inferdb.cgpm.utils.extract_samples_from_trace = (function inferdb$cgpm$utils$extract_samples_from_trace(trace,target_addrs,output_addr_map){
var extract = (function (k){
var result = cljs.core.get.call(null,trace,inferdb.cgpm.utils.safe_get.call(null,output_addr_map,k));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [k,inferdb.cgpm.utils.safe_get.call(null,result,new cljs.core.Keyword(null,"value","value",305978217))], null);
});
return cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,metaprob.prelude.map.call(null,extract,target_addrs));
});
inferdb.cgpm.utils.rekey_dict = (function inferdb$cgpm$utils$rekey_dict(keymap,dict){
return cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,metaprob.prelude.map.call(null,(function (p__2603){
var vec__2604 = p__2603;
var k = cljs.core.nth.call(null,vec__2604,(0),null);
var v = cljs.core.nth.call(null,vec__2604,(1),null);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [inferdb.cgpm.utils.safe_get.call(null,keymap,k),v], null);
}),dict));
});

//# sourceMappingURL=utils.js.map
