// Compiled by ClojureScript 1.10.520 {}
goog.provide('metaprob.trace');
goog.require('cljs.core');
metaprob.trace.trace_subtrace = (function metaprob$trace$trace_subtrace(tr,adr){
return ((cljs.core.seq_QMARK_.call(null,adr))?cljs.core.get_in:cljs.core.get).call(null,tr,adr);
});
metaprob.trace.trace_has_value_QMARK_ = (function metaprob$trace$trace_has_value_QMARK_(var_args){
var G__1826 = arguments.length;
switch (G__1826) {
case 1:
return metaprob.trace.trace_has_value_QMARK_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return metaprob.trace.trace_has_value_QMARK_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

metaprob.trace.trace_has_value_QMARK_.cljs$core$IFn$_invoke$arity$1 = (function (tr){
return cljs.core.contains_QMARK_.call(null,tr,new cljs.core.Keyword(null,"value","value",305978217));
});

metaprob.trace.trace_has_value_QMARK_.cljs$core$IFn$_invoke$arity$2 = (function (tr,adr){
return cljs.core.contains_QMARK_.call(null,metaprob.trace.trace_subtrace.call(null,tr,adr),new cljs.core.Keyword(null,"value","value",305978217));
});

metaprob.trace.trace_has_value_QMARK_.cljs$lang$maxFixedArity = 2;

metaprob.trace.trace_value = (function metaprob$trace$trace_value(var_args){
var G__1829 = arguments.length;
switch (G__1829) {
case 1:
return metaprob.trace.trace_value.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return metaprob.trace.trace_value.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

metaprob.trace.trace_value.cljs$core$IFn$_invoke$arity$1 = (function (tr){
return cljs.core.get.call(null,tr,new cljs.core.Keyword(null,"value","value",305978217));
});

metaprob.trace.trace_value.cljs$core$IFn$_invoke$arity$2 = (function (tr,adr){
return cljs.core.get.call(null,metaprob.trace.trace_subtrace.call(null,tr,adr),new cljs.core.Keyword(null,"value","value",305978217));
});

metaprob.trace.trace_value.cljs$lang$maxFixedArity = 2;

metaprob.trace.trace_has_subtrace_QMARK_ = (function metaprob$trace$trace_has_subtrace_QMARK_(tr,adr){
while(true){
if(cljs.core.seq_QMARK_.call(null,adr)){
if(cljs.core.empty_QMARK_.call(null,adr)){
return true;
} else {
if(cljs.core.contains_QMARK_.call(null,tr,cljs.core.first.call(null,adr))){
var G__1831 = cljs.core.get.call(null,tr,cljs.core.first.call(null,adr));
var G__1832 = cljs.core.rest.call(null,adr);
tr = G__1831;
adr = G__1832;
continue;
} else {
return null;
}
}
} else {
return cljs.core.contains_QMARK_.call(null,tr,adr);
}
break;
}
});
metaprob.trace.trace_keys = (function metaprob$trace$trace_keys(tr){
return cljs.core.filter.call(null,(function (x){
return cljs.core.not_EQ_.call(null,x,new cljs.core.Keyword(null,"value","value",305978217));
}),cljs.core.keys.call(null,tr));
});
metaprob.trace.subtrace_count = (function metaprob$trace$subtrace_count(tr){
return (cljs.core.count.call(null,tr) - ((metaprob.trace.trace_has_value_QMARK_.call(null,tr))?(1):(0)));
});
metaprob.trace.trace_set_subtrace = (function metaprob$trace$trace_set_subtrace(tr,adr,sub){
if(cljs.core.seq_QMARK_.call(null,adr)){
if(cljs.core.empty_QMARK_.call(null,adr)){
return sub;
} else {
return cljs.core.assoc.call(null,tr,cljs.core.first.call(null,adr),metaprob.trace.trace_set_subtrace.call(null,cljs.core.get.call(null,tr,cljs.core.first.call(null,adr)),cljs.core.rest.call(null,adr),sub));
}
} else {
return cljs.core.assoc.call(null,tr,adr,sub);
}
});
metaprob.trace.trace_set_value = (function metaprob$trace$trace_set_value(var_args){
var G__1834 = arguments.length;
switch (G__1834) {
case 2:
return metaprob.trace.trace_set_value.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return metaprob.trace.trace_set_value.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

metaprob.trace.trace_set_value.cljs$core$IFn$_invoke$arity$2 = (function (tr,val){
return cljs.core.assoc.call(null,tr,new cljs.core.Keyword(null,"value","value",305978217),val);
});

metaprob.trace.trace_set_value.cljs$core$IFn$_invoke$arity$3 = (function (tr,adr,val){
return metaprob.trace.trace_set_subtrace.call(null,tr,adr,metaprob.trace.trace_set_value.call(null,metaprob.trace.trace_subtrace.call(null,tr,adr),val));
});

metaprob.trace.trace_set_value.cljs$lang$maxFixedArity = 3;

metaprob.trace.trace_clear_value = (function metaprob$trace$trace_clear_value(var_args){
var G__1837 = arguments.length;
switch (G__1837) {
case 1:
return metaprob.trace.trace_clear_value.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return metaprob.trace.trace_clear_value.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

metaprob.trace.trace_clear_value.cljs$core$IFn$_invoke$arity$1 = (function (tr){
return cljs.core.dissoc.call(null,tr,new cljs.core.Keyword(null,"value","value",305978217));
});

metaprob.trace.trace_clear_value.cljs$core$IFn$_invoke$arity$2 = (function (tr,adr){
return metaprob.trace.trace_set_subtrace.call(null,tr,adr,metaprob.trace.trace_clear_value.call(null,metaprob.trace.trace_subtrace.call(null,tr,adr)));
});

metaprob.trace.trace_clear_value.cljs$lang$maxFixedArity = 2;

metaprob.trace.maybe_set_subtrace = (function metaprob$trace$maybe_set_subtrace(output,adr,suboutput){
if(cljs.core.empty_QMARK_.call(null,suboutput)){
return metaprob.trace.trace_clear_subtrace.call(null,output,adr);
} else {
return metaprob.trace.trace_set_subtrace.call(null,output,adr,suboutput);
}
});
metaprob.trace.trace_clear_subtrace = (function metaprob$trace$trace_clear_subtrace(tr,adr){
if(cljs.core.seq_QMARK_.call(null,adr)){
if(cljs.core.empty_QMARK_.call(null,adr)){
return cljs.core.PersistentArrayMap.EMPTY;
} else {
if(cljs.core.empty_QMARK_.call(null,cljs.core.rest.call(null,adr))){
return cljs.core.dissoc.call(null,tr,cljs.core.first.call(null,adr));
} else {
return metaprob.trace.maybe_set_subtrace.call(null,tr,cljs.core.first.call(null,adr),metaprob.trace.trace_clear_subtrace.call(null,metaprob.trace.trace_subtrace.call(null,tr,cljs.core.first.call(null,adr)),cljs.core.rest.call(null,adr)));
}
}
} else {
return cljs.core.dissoc.call(null,tr,adr);
}
});
metaprob.trace.value_only_trace_QMARK_ = (function metaprob$trace$value_only_trace_QMARK_(tr){
return ((metaprob.trace.trace_has_value_QMARK_.call(null,tr)) && (cljs.core._EQ_.call(null,cljs.core.count.call(null,tr),(1))));
});
metaprob.trace.trace_QMARK_ = (function metaprob$trace$trace_QMARK_(s){
return cljs.core.map_QMARK_.call(null,s);
});
metaprob.trace.valid_trace_QMARK_ = (function metaprob$trace$valid_trace_QMARK_(s){
return ((cljs.core.map_QMARK_.call(null,s)) && (cljs.core.every_QMARK_.call(null,(function (k){
return metaprob.trace.trace_QMARK_.call(null,cljs.core.get.call(null,s,k));
}),metaprob.trace.trace_keys.call(null,s))));
});
metaprob.trace.trace_merge = (function metaprob$trace$trace_merge(tr1,tr2){
var merged = cljs.core.into.call(null,tr1,(function (){var iter__4523__auto__ = (function metaprob$trace$trace_merge_$_iter__1839(s__1840){
return (new cljs.core.LazySeq(null,(function (){
var s__1840__$1 = s__1840;
while(true){
var temp__5720__auto__ = cljs.core.seq.call(null,s__1840__$1);
if(temp__5720__auto__){
var s__1840__$2 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__1840__$2)){
var c__4521__auto__ = cljs.core.chunk_first.call(null,s__1840__$2);
var size__4522__auto__ = cljs.core.count.call(null,c__4521__auto__);
var b__1842 = cljs.core.chunk_buffer.call(null,size__4522__auto__);
if((function (){var i__1841 = (0);
while(true){
if((i__1841 < size__4522__auto__)){
var key = cljs.core._nth.call(null,c__4521__auto__,i__1841);
cljs.core.chunk_append.call(null,b__1842,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [key,(cljs.core.truth_(metaprob.trace.trace_has_subtrace_QMARK_.call(null,tr1,key))?metaprob.trace.trace_merge.call(null,metaprob.trace.trace_subtrace.call(null,tr1,key),metaprob.trace.trace_subtrace.call(null,tr2,key)):metaprob.trace.trace_subtrace.call(null,tr2,key))], null));

var G__1843 = (i__1841 + (1));
i__1841 = G__1843;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__1842),metaprob$trace$trace_merge_$_iter__1839.call(null,cljs.core.chunk_rest.call(null,s__1840__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__1842),null);
}
} else {
var key = cljs.core.first.call(null,s__1840__$2);
return cljs.core.cons.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [key,(cljs.core.truth_(metaprob.trace.trace_has_subtrace_QMARK_.call(null,tr1,key))?metaprob.trace.trace_merge.call(null,metaprob.trace.trace_subtrace.call(null,tr1,key),metaprob.trace.trace_subtrace.call(null,tr2,key)):metaprob.trace.trace_subtrace.call(null,tr2,key))], null),metaprob$trace$trace_merge_$_iter__1839.call(null,cljs.core.rest.call(null,s__1840__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__4523__auto__.call(null,metaprob.trace.trace_keys.call(null,tr2));
})());
if(metaprob.trace.trace_has_value_QMARK_.call(null,merged)){
if(metaprob.trace.trace_has_value_QMARK_.call(null,tr2)){
if(cljs.core._EQ_.call(null,metaprob.trace.trace_value.call(null,tr1),metaprob.trace.trace_value.call(null,tr2))){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["incompatible trace values",tr1,tr2], null)),"\n","(= (trace-value tr1) (trace-value tr2))"].join('')));
}
} else {
}

return merged;
} else {
if(metaprob.trace.trace_has_value_QMARK_.call(null,tr2)){
return metaprob.trace.trace_set_value.call(null,merged,metaprob.trace.trace_value.call(null,tr2));
} else {
return merged;
}
}
});
metaprob.trace.maybe_subtrace = (function metaprob$trace$maybe_subtrace(tr,adr){
var or__4131__auto__ = metaprob.trace.trace_subtrace.call(null,tr,adr);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return cljs.core.PersistentArrayMap.EMPTY;
}
});
metaprob.trace.merge_subtrace = (function metaprob$trace$merge_subtrace(trace,addr,subtrace){
return metaprob.trace.trace_merge.call(null,trace,metaprob.trace.maybe_set_subtrace.call(null,cljs.core.PersistentArrayMap.EMPTY,addr,subtrace));
});
metaprob.trace.addresses_of = (function metaprob$trace$addresses_of(tr){
var get_sites = (function metaprob$trace$addresses_of_$_get_sites(tr__$1){
var site_list = cljs.core.mapcat.call(null,(function (key){
return cljs.core.map.call(null,(function (site){
return cljs.core.cons.call(null,key,site);
}),metaprob$trace$addresses_of_$_get_sites.call(null,metaprob.trace.trace_subtrace.call(null,tr__$1,key)));
}),metaprob.trace.trace_keys.call(null,tr__$1));
if(metaprob.trace.trace_has_value_QMARK_.call(null,tr__$1)){
return cljs.core.cons.call(null,cljs.core.List.EMPTY,site_list);
} else {
return site_list;
}
});
var s = get_sites.call(null,tr);
var seq__1844_1848 = cljs.core.seq.call(null,s);
var chunk__1845_1849 = null;
var count__1846_1850 = (0);
var i__1847_1851 = (0);
while(true){
if((i__1847_1851 < count__1846_1850)){
var site_1852 = cljs.core._nth.call(null,chunk__1845_1849,i__1847_1851);
if(metaprob.trace.trace_has_value_QMARK_.call(null,tr,site_1852)){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["missing value at",site_1852], null)),"\n","(trace-has-value? tr site)"].join('')));
}


var G__1853 = seq__1844_1848;
var G__1854 = chunk__1845_1849;
var G__1855 = count__1846_1850;
var G__1856 = (i__1847_1851 + (1));
seq__1844_1848 = G__1853;
chunk__1845_1849 = G__1854;
count__1846_1850 = G__1855;
i__1847_1851 = G__1856;
continue;
} else {
var temp__5720__auto___1857 = cljs.core.seq.call(null,seq__1844_1848);
if(temp__5720__auto___1857){
var seq__1844_1858__$1 = temp__5720__auto___1857;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__1844_1858__$1)){
var c__4550__auto___1859 = cljs.core.chunk_first.call(null,seq__1844_1858__$1);
var G__1860 = cljs.core.chunk_rest.call(null,seq__1844_1858__$1);
var G__1861 = c__4550__auto___1859;
var G__1862 = cljs.core.count.call(null,c__4550__auto___1859);
var G__1863 = (0);
seq__1844_1848 = G__1860;
chunk__1845_1849 = G__1861;
count__1846_1850 = G__1862;
i__1847_1851 = G__1863;
continue;
} else {
var site_1864 = cljs.core.first.call(null,seq__1844_1858__$1);
if(metaprob.trace.trace_has_value_QMARK_.call(null,tr,site_1864)){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["missing value at",site_1864], null)),"\n","(trace-has-value? tr site)"].join('')));
}


var G__1865 = cljs.core.next.call(null,seq__1844_1858__$1);
var G__1866 = null;
var G__1867 = (0);
var G__1868 = (0);
seq__1844_1848 = G__1865;
chunk__1845_1849 = G__1866;
count__1846_1850 = G__1867;
i__1847_1851 = G__1868;
continue;
}
} else {
}
}
break;
}

return s;
});
metaprob.trace.copy_addresses = (function metaprob$trace$copy_addresses(src,dst,paths){

return cljs.core.reduce.call(null,(function (p1__1869_SHARP_,p2__1870_SHARP_){
return metaprob.trace.trace_set_value.call(null,p1__1869_SHARP_,p2__1870_SHARP_,metaprob.trace.trace_value.call(null,src,p2__1870_SHARP_));
}),dst,paths);
});
metaprob.trace.partition_trace = (function metaprob$trace$partition_trace(trace,paths){
var path_set = cljs.core.into.call(null,cljs.core.PersistentHashSet.EMPTY,cljs.core.map.call(null,(function (p1__1871_SHARP_){
if((!(cljs.core.seq_QMARK_.call(null,p1__1871_SHARP_)))){
return (new cljs.core.List(null,p1__1871_SHARP_,null,(1),null));
} else {
return p1__1871_SHARP_;
}
}),paths));
var addresses = cljs.core.into.call(null,cljs.core.PersistentHashSet.EMPTY,metaprob.trace.addresses_of.call(null,trace));
var all_addresses = cljs.core.group_by.call(null,((function (path_set,addresses){
return (function (p1__1872_SHARP_){
return cljs.core.contains_QMARK_.call(null,path_set,p1__1872_SHARP_);
});})(path_set,addresses))
,addresses);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [metaprob.trace.copy_addresses.call(null,trace,cljs.core.PersistentArrayMap.EMPTY,cljs.core.get.call(null,all_addresses,true)),metaprob.trace.copy_addresses.call(null,trace,cljs.core.PersistentArrayMap.EMPTY,cljs.core.get.call(null,all_addresses,false))], null);
});
metaprob.trace.address_contains_QMARK_ = (function metaprob$trace$address_contains_QMARK_(addr,elem){
return cljs.core.some.call(null,cljs.core.PersistentHashSet.createAsIfByAssoc([elem]),addr);
});

//# sourceMappingURL=trace.js.map
