// Compiled by ClojureScript 1.10.520 {}
goog.provide('re_frame.trace');
goog.require('cljs.core');
goog.require('re_frame.interop');
goog.require('re_frame.loggers');
goog.require('goog.functions');
re_frame.trace.id = cljs.core.atom.call(null,(0));
re_frame.trace._STAR_current_trace_STAR_ = null;
re_frame.trace.reset_tracing_BANG_ = (function re_frame$trace$reset_tracing_BANG_(){
return cljs.core.reset_BANG_.call(null,re_frame.trace.id,(0));
});

/** @define {boolean} */
goog.define("re_frame.trace.trace_enabled_QMARK_",false);
/**
 * See https://groups.google.com/d/msg/clojurescript/jk43kmYiMhA/IHglVr_TPdgJ for more details
 */
re_frame.trace.is_trace_enabled_QMARK_ = (function re_frame$trace$is_trace_enabled_QMARK_(){
return re_frame.trace.trace_enabled_QMARK_;
});
re_frame.trace.trace_cbs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
if((typeof re_frame !== 'undefined') && (typeof re_frame.trace !== 'undefined') && (typeof re_frame.trace.traces !== 'undefined')){
} else {
re_frame.trace.traces = cljs.core.atom.call(null,cljs.core.PersistentVector.EMPTY);
}
if((typeof re_frame !== 'undefined') && (typeof re_frame.trace !== 'undefined') && (typeof re_frame.trace.next_delivery !== 'undefined')){
} else {
re_frame.trace.next_delivery = cljs.core.atom.call(null,(0));
}
/**
 * Registers a tracing callback function which will receive a collection of one or more traces.
 *   Will replace an existing callback function if it shares the same key.
 */
re_frame.trace.register_trace_cb = (function re_frame$trace$register_trace_cb(key,f){
if(re_frame.trace.trace_enabled_QMARK_){
return cljs.core.swap_BANG_.call(null,re_frame.trace.trace_cbs,cljs.core.assoc,key,f);
} else {
return re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"warn","warn",-436710552),"Tracing is not enabled. Please set {\"re_frame.trace.trace_enabled_QMARK_\" true} in :closure-defines. See: https://github.com/Day8/re-frame-10x#installation.");
}
});
re_frame.trace.remove_trace_cb = (function re_frame$trace$remove_trace_cb(key){
cljs.core.swap_BANG_.call(null,re_frame.trace.trace_cbs,cljs.core.dissoc,key);

return null;
});
re_frame.trace.next_id = (function re_frame$trace$next_id(){
return cljs.core.swap_BANG_.call(null,re_frame.trace.id,cljs.core.inc);
});
re_frame.trace.start_trace = (function re_frame$trace$start_trace(p__6590){
var map__6591 = p__6590;
var map__6591__$1 = (((((!((map__6591 == null))))?(((((map__6591.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__6591.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__6591):map__6591);
var operation = cljs.core.get.call(null,map__6591__$1,new cljs.core.Keyword(null,"operation","operation",-1267664310));
var op_type = cljs.core.get.call(null,map__6591__$1,new cljs.core.Keyword(null,"op-type","op-type",-1636141668));
var tags = cljs.core.get.call(null,map__6591__$1,new cljs.core.Keyword(null,"tags","tags",1771418977));
var child_of = cljs.core.get.call(null,map__6591__$1,new cljs.core.Keyword(null,"child-of","child-of",-903376662));
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"id","id",-1388402092),re_frame.trace.next_id.call(null),new cljs.core.Keyword(null,"operation","operation",-1267664310),operation,new cljs.core.Keyword(null,"op-type","op-type",-1636141668),op_type,new cljs.core.Keyword(null,"tags","tags",1771418977),tags,new cljs.core.Keyword(null,"child-of","child-of",-903376662),(function (){var or__4131__auto__ = child_of;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(re_frame.trace._STAR_current_trace_STAR_);
}
})(),new cljs.core.Keyword(null,"start","start",-355208981),re_frame.interop.now.call(null)], null);
});
re_frame.trace.debounce_time = (50);
re_frame.trace.debounce = (function re_frame$trace$debounce(f,interval){
return goog.functions.debounce(f,interval);
});
re_frame.trace.schedule_debounce = re_frame.trace.debounce.call(null,(function re_frame$trace$tracing_cb_debounced(){
var seq__6593_6613 = cljs.core.seq.call(null,cljs.core.deref.call(null,re_frame.trace.trace_cbs));
var chunk__6594_6614 = null;
var count__6595_6615 = (0);
var i__6596_6616 = (0);
while(true){
if((i__6596_6616 < count__6595_6615)){
var vec__6605_6617 = cljs.core._nth.call(null,chunk__6594_6614,i__6596_6616);
var k_6618 = cljs.core.nth.call(null,vec__6605_6617,(0),null);
var cb_6619 = cljs.core.nth.call(null,vec__6605_6617,(1),null);
try{cb_6619.call(null,cljs.core.deref.call(null,re_frame.trace.traces));
}catch (e6608){var e_6620 = e6608;
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"Error thrown from trace cb",k_6618,"while storing",cljs.core.deref.call(null,re_frame.trace.traces),e_6620);
}

var G__6621 = seq__6593_6613;
var G__6622 = chunk__6594_6614;
var G__6623 = count__6595_6615;
var G__6624 = (i__6596_6616 + (1));
seq__6593_6613 = G__6621;
chunk__6594_6614 = G__6622;
count__6595_6615 = G__6623;
i__6596_6616 = G__6624;
continue;
} else {
var temp__5720__auto___6625 = cljs.core.seq.call(null,seq__6593_6613);
if(temp__5720__auto___6625){
var seq__6593_6626__$1 = temp__5720__auto___6625;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__6593_6626__$1)){
var c__4550__auto___6627 = cljs.core.chunk_first.call(null,seq__6593_6626__$1);
var G__6628 = cljs.core.chunk_rest.call(null,seq__6593_6626__$1);
var G__6629 = c__4550__auto___6627;
var G__6630 = cljs.core.count.call(null,c__4550__auto___6627);
var G__6631 = (0);
seq__6593_6613 = G__6628;
chunk__6594_6614 = G__6629;
count__6595_6615 = G__6630;
i__6596_6616 = G__6631;
continue;
} else {
var vec__6609_6632 = cljs.core.first.call(null,seq__6593_6626__$1);
var k_6633 = cljs.core.nth.call(null,vec__6609_6632,(0),null);
var cb_6634 = cljs.core.nth.call(null,vec__6609_6632,(1),null);
try{cb_6634.call(null,cljs.core.deref.call(null,re_frame.trace.traces));
}catch (e6612){var e_6635 = e6612;
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"Error thrown from trace cb",k_6633,"while storing",cljs.core.deref.call(null,re_frame.trace.traces),e_6635);
}

var G__6636 = cljs.core.next.call(null,seq__6593_6626__$1);
var G__6637 = null;
var G__6638 = (0);
var G__6639 = (0);
seq__6593_6613 = G__6636;
chunk__6594_6614 = G__6637;
count__6595_6615 = G__6638;
i__6596_6616 = G__6639;
continue;
}
} else {
}
}
break;
}

return cljs.core.reset_BANG_.call(null,re_frame.trace.traces,cljs.core.PersistentVector.EMPTY);
}),re_frame.trace.debounce_time);
re_frame.trace.run_tracing_callbacks_BANG_ = (function re_frame$trace$run_tracing_callbacks_BANG_(now){
if(((cljs.core.deref.call(null,re_frame.trace.next_delivery) - (25)) < now)){
re_frame.trace.schedule_debounce.call(null);

return cljs.core.reset_BANG_.call(null,re_frame.trace.next_delivery,(now + re_frame.trace.debounce_time));
} else {
return null;
}
});

//# sourceMappingURL=trace.js.map
