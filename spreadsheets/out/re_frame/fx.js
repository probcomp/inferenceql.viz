// Compiled by ClojureScript 1.10.520 {}
goog.provide('re_frame.fx');
goog.require('cljs.core');
goog.require('re_frame.router');
goog.require('re_frame.db');
goog.require('re_frame.interceptor');
goog.require('re_frame.interop');
goog.require('re_frame.events');
goog.require('re_frame.registrar');
goog.require('re_frame.loggers');
goog.require('re_frame.trace');
re_frame.fx.kind = new cljs.core.Keyword(null,"fx","fx",-1237829572);
if(cljs.core.truth_(re_frame.registrar.kinds.call(null,re_frame.fx.kind))){
} else {
throw (new Error("Assert failed: (re-frame.registrar/kinds kind)"));
}
/**
 * Register the given effect `handler` for the given `id`.
 * 
 *   `id` is keyword, often namespaced.
 *   `handler` is a side-effecting function which takes a single argument and whose return
 *   value is ignored.
 * 
 *   Example Use
 *   -----------
 * 
 *   First, registration ... associate `:effect2` with a handler.
 * 
 *   (reg-fx
 *   :effect2
 *   (fn [value]
 *      ... do something side-effect-y))
 * 
 *   Then, later, if an event handler were to return this effects map ...
 * 
 *   {...
 * :effect2  [1 2]}
 * 
 * ... then the `handler` `fn` we registered previously, using `reg-fx`, will be
 * called with an argument of `[1 2]`.
 */
re_frame.fx.reg_fx = (function re_frame$fx$reg_fx(id,handler){
return re_frame.registrar.register_handler.call(null,re_frame.fx.kind,id,handler);
});
/**
 * An interceptor whose `:after` actions the contents of `:effects`. As a result,
 *   this interceptor is Domino 3.
 * 
 *   This interceptor is silently added (by reg-event-db etc) to the front of
 *   interceptor chains for all events.
 * 
 *   For each key in `:effects` (a map), it calls the registered `effects handler`
 *   (see `reg-fx` for registration of effect handlers).
 * 
 *   So, if `:effects` was:
 *    {:dispatch  [:hello 42]
 *     :db        {...}
 *     :undo      "set flag"}
 * 
 *   it will call the registered effect handlers for each of the map's keys:
 *   `:dispatch`, `:undo` and `:db`. When calling each handler, provides the map
 *   value for that key - so in the example above the effect handler for :dispatch
 *   will be given one arg `[:hello 42]`.
 * 
 *   You cannot rely on the ordering in which effects are executed.
 */
re_frame.fx.do_fx = re_frame.interceptor.__GT_interceptor.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),new cljs.core.Keyword(null,"do-fx","do-fx",1194163050),new cljs.core.Keyword(null,"after","after",594996914),(function re_frame$fx$do_fx_after(context){
if(re_frame.trace.is_trace_enabled_QMARK_.call(null)){
var _STAR_current_trace_STAR__orig_val__6739 = re_frame.trace._STAR_current_trace_STAR_;
var _STAR_current_trace_STAR__temp_val__6740 = re_frame.trace.start_trace.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"op-type","op-type",-1636141668),new cljs.core.Keyword("event","do-fx","event/do-fx",1357330452)], null));
re_frame.trace._STAR_current_trace_STAR_ = _STAR_current_trace_STAR__temp_val__6740;

try{try{var seq__6741 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"effects","effects",-282369292).cljs$core$IFn$_invoke$arity$1(context));
var chunk__6742 = null;
var count__6743 = (0);
var i__6744 = (0);
while(true){
if((i__6744 < count__6743)){
var vec__6751 = cljs.core._nth.call(null,chunk__6742,i__6744);
var effect_key = cljs.core.nth.call(null,vec__6751,(0),null);
var effect_value = cljs.core.nth.call(null,vec__6751,(1),null);
var temp__5718__auto___6773 = re_frame.registrar.get_handler.call(null,re_frame.fx.kind,effect_key,false);
if(cljs.core.truth_(temp__5718__auto___6773)){
var effect_fn_6774 = temp__5718__auto___6773;
effect_fn_6774.call(null,effect_value);
} else {
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: no handler registered for effect:",effect_key,". Ignoring.");
}


var G__6775 = seq__6741;
var G__6776 = chunk__6742;
var G__6777 = count__6743;
var G__6778 = (i__6744 + (1));
seq__6741 = G__6775;
chunk__6742 = G__6776;
count__6743 = G__6777;
i__6744 = G__6778;
continue;
} else {
var temp__5720__auto__ = cljs.core.seq.call(null,seq__6741);
if(temp__5720__auto__){
var seq__6741__$1 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__6741__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__6741__$1);
var G__6779 = cljs.core.chunk_rest.call(null,seq__6741__$1);
var G__6780 = c__4550__auto__;
var G__6781 = cljs.core.count.call(null,c__4550__auto__);
var G__6782 = (0);
seq__6741 = G__6779;
chunk__6742 = G__6780;
count__6743 = G__6781;
i__6744 = G__6782;
continue;
} else {
var vec__6754 = cljs.core.first.call(null,seq__6741__$1);
var effect_key = cljs.core.nth.call(null,vec__6754,(0),null);
var effect_value = cljs.core.nth.call(null,vec__6754,(1),null);
var temp__5718__auto___6783 = re_frame.registrar.get_handler.call(null,re_frame.fx.kind,effect_key,false);
if(cljs.core.truth_(temp__5718__auto___6783)){
var effect_fn_6784 = temp__5718__auto___6783;
effect_fn_6784.call(null,effect_value);
} else {
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: no handler registered for effect:",effect_key,". Ignoring.");
}


var G__6785 = cljs.core.next.call(null,seq__6741__$1);
var G__6786 = null;
var G__6787 = (0);
var G__6788 = (0);
seq__6741 = G__6785;
chunk__6742 = G__6786;
count__6743 = G__6787;
i__6744 = G__6788;
continue;
}
} else {
return null;
}
}
break;
}
}finally {if(re_frame.trace.is_trace_enabled_QMARK_.call(null)){
var end__6568__auto___6789 = re_frame.interop.now.call(null);
var duration__6569__auto___6790 = (end__6568__auto___6789 - new cljs.core.Keyword(null,"start","start",-355208981).cljs$core$IFn$_invoke$arity$1(re_frame.trace._STAR_current_trace_STAR_));
cljs.core.swap_BANG_.call(null,re_frame.trace.traces,cljs.core.conj,cljs.core.assoc.call(null,re_frame.trace._STAR_current_trace_STAR_,new cljs.core.Keyword(null,"duration","duration",1444101068),duration__6569__auto___6790,new cljs.core.Keyword(null,"end","end",-268185958),re_frame.interop.now.call(null)));

re_frame.trace.run_tracing_callbacks_BANG_.call(null,end__6568__auto___6789);
} else {
}
}}finally {re_frame.trace._STAR_current_trace_STAR_ = _STAR_current_trace_STAR__orig_val__6739;
}} else {
var seq__6757 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"effects","effects",-282369292).cljs$core$IFn$_invoke$arity$1(context));
var chunk__6758 = null;
var count__6759 = (0);
var i__6760 = (0);
while(true){
if((i__6760 < count__6759)){
var vec__6767 = cljs.core._nth.call(null,chunk__6758,i__6760);
var effect_key = cljs.core.nth.call(null,vec__6767,(0),null);
var effect_value = cljs.core.nth.call(null,vec__6767,(1),null);
var temp__5718__auto___6791 = re_frame.registrar.get_handler.call(null,re_frame.fx.kind,effect_key,false);
if(cljs.core.truth_(temp__5718__auto___6791)){
var effect_fn_6792 = temp__5718__auto___6791;
effect_fn_6792.call(null,effect_value);
} else {
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: no handler registered for effect:",effect_key,". Ignoring.");
}


var G__6793 = seq__6757;
var G__6794 = chunk__6758;
var G__6795 = count__6759;
var G__6796 = (i__6760 + (1));
seq__6757 = G__6793;
chunk__6758 = G__6794;
count__6759 = G__6795;
i__6760 = G__6796;
continue;
} else {
var temp__5720__auto__ = cljs.core.seq.call(null,seq__6757);
if(temp__5720__auto__){
var seq__6757__$1 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__6757__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__6757__$1);
var G__6797 = cljs.core.chunk_rest.call(null,seq__6757__$1);
var G__6798 = c__4550__auto__;
var G__6799 = cljs.core.count.call(null,c__4550__auto__);
var G__6800 = (0);
seq__6757 = G__6797;
chunk__6758 = G__6798;
count__6759 = G__6799;
i__6760 = G__6800;
continue;
} else {
var vec__6770 = cljs.core.first.call(null,seq__6757__$1);
var effect_key = cljs.core.nth.call(null,vec__6770,(0),null);
var effect_value = cljs.core.nth.call(null,vec__6770,(1),null);
var temp__5718__auto___6801 = re_frame.registrar.get_handler.call(null,re_frame.fx.kind,effect_key,false);
if(cljs.core.truth_(temp__5718__auto___6801)){
var effect_fn_6802 = temp__5718__auto___6801;
effect_fn_6802.call(null,effect_value);
} else {
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: no handler registered for effect:",effect_key,". Ignoring.");
}


var G__6803 = cljs.core.next.call(null,seq__6757__$1);
var G__6804 = null;
var G__6805 = (0);
var G__6806 = (0);
seq__6757 = G__6803;
chunk__6758 = G__6804;
count__6759 = G__6805;
i__6760 = G__6806;
continue;
}
} else {
return null;
}
}
break;
}
}
}));
re_frame.fx.reg_fx.call(null,new cljs.core.Keyword(null,"dispatch-later","dispatch-later",291951390),(function (value){
var seq__6807 = cljs.core.seq.call(null,cljs.core.remove.call(null,cljs.core.nil_QMARK_,value));
var chunk__6808 = null;
var count__6809 = (0);
var i__6810 = (0);
while(true){
if((i__6810 < count__6809)){
var map__6815 = cljs.core._nth.call(null,chunk__6808,i__6810);
var map__6815__$1 = (((((!((map__6815 == null))))?(((((map__6815.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__6815.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__6815):map__6815);
var effect = map__6815__$1;
var ms = cljs.core.get.call(null,map__6815__$1,new cljs.core.Keyword(null,"ms","ms",-1152709733));
var dispatch = cljs.core.get.call(null,map__6815__$1,new cljs.core.Keyword(null,"dispatch","dispatch",1319337009));
if(((cljs.core.empty_QMARK_.call(null,dispatch)) || ((!(typeof ms === 'number'))))){
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: ignoring bad :dispatch-later value:",effect);
} else {
re_frame.interop.set_timeout_BANG_.call(null,((function (seq__6807,chunk__6808,count__6809,i__6810,map__6815,map__6815__$1,effect,ms,dispatch){
return (function (){
return re_frame.router.dispatch.call(null,dispatch);
});})(seq__6807,chunk__6808,count__6809,i__6810,map__6815,map__6815__$1,effect,ms,dispatch))
,ms);
}


var G__6819 = seq__6807;
var G__6820 = chunk__6808;
var G__6821 = count__6809;
var G__6822 = (i__6810 + (1));
seq__6807 = G__6819;
chunk__6808 = G__6820;
count__6809 = G__6821;
i__6810 = G__6822;
continue;
} else {
var temp__5720__auto__ = cljs.core.seq.call(null,seq__6807);
if(temp__5720__auto__){
var seq__6807__$1 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__6807__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__6807__$1);
var G__6823 = cljs.core.chunk_rest.call(null,seq__6807__$1);
var G__6824 = c__4550__auto__;
var G__6825 = cljs.core.count.call(null,c__4550__auto__);
var G__6826 = (0);
seq__6807 = G__6823;
chunk__6808 = G__6824;
count__6809 = G__6825;
i__6810 = G__6826;
continue;
} else {
var map__6817 = cljs.core.first.call(null,seq__6807__$1);
var map__6817__$1 = (((((!((map__6817 == null))))?(((((map__6817.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__6817.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__6817):map__6817);
var effect = map__6817__$1;
var ms = cljs.core.get.call(null,map__6817__$1,new cljs.core.Keyword(null,"ms","ms",-1152709733));
var dispatch = cljs.core.get.call(null,map__6817__$1,new cljs.core.Keyword(null,"dispatch","dispatch",1319337009));
if(((cljs.core.empty_QMARK_.call(null,dispatch)) || ((!(typeof ms === 'number'))))){
re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: ignoring bad :dispatch-later value:",effect);
} else {
re_frame.interop.set_timeout_BANG_.call(null,((function (seq__6807,chunk__6808,count__6809,i__6810,map__6817,map__6817__$1,effect,ms,dispatch,seq__6807__$1,temp__5720__auto__){
return (function (){
return re_frame.router.dispatch.call(null,dispatch);
});})(seq__6807,chunk__6808,count__6809,i__6810,map__6817,map__6817__$1,effect,ms,dispatch,seq__6807__$1,temp__5720__auto__))
,ms);
}


var G__6827 = cljs.core.next.call(null,seq__6807__$1);
var G__6828 = null;
var G__6829 = (0);
var G__6830 = (0);
seq__6807 = G__6827;
chunk__6808 = G__6828;
count__6809 = G__6829;
i__6810 = G__6830;
continue;
}
} else {
return null;
}
}
break;
}
}));
re_frame.fx.reg_fx.call(null,new cljs.core.Keyword(null,"dispatch","dispatch",1319337009),(function (value){
if((!(cljs.core.vector_QMARK_.call(null,value)))){
return re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: ignoring bad :dispatch value. Expected a vector, but got:",value);
} else {
return re_frame.router.dispatch.call(null,value);
}
}));
re_frame.fx.reg_fx.call(null,new cljs.core.Keyword(null,"dispatch-n","dispatch-n",-504469236),(function (value){
if((!(cljs.core.sequential_QMARK_.call(null,value)))){
return re_frame.loggers.console.call(null,new cljs.core.Keyword(null,"error","error",-978969032),"re-frame: ignoring bad :dispatch-n value. Expected a collection, got got:",value);
} else {
var seq__6831 = cljs.core.seq.call(null,cljs.core.remove.call(null,cljs.core.nil_QMARK_,value));
var chunk__6832 = null;
var count__6833 = (0);
var i__6834 = (0);
while(true){
if((i__6834 < count__6833)){
var event = cljs.core._nth.call(null,chunk__6832,i__6834);
re_frame.router.dispatch.call(null,event);


var G__6835 = seq__6831;
var G__6836 = chunk__6832;
var G__6837 = count__6833;
var G__6838 = (i__6834 + (1));
seq__6831 = G__6835;
chunk__6832 = G__6836;
count__6833 = G__6837;
i__6834 = G__6838;
continue;
} else {
var temp__5720__auto__ = cljs.core.seq.call(null,seq__6831);
if(temp__5720__auto__){
var seq__6831__$1 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__6831__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__6831__$1);
var G__6839 = cljs.core.chunk_rest.call(null,seq__6831__$1);
var G__6840 = c__4550__auto__;
var G__6841 = cljs.core.count.call(null,c__4550__auto__);
var G__6842 = (0);
seq__6831 = G__6839;
chunk__6832 = G__6840;
count__6833 = G__6841;
i__6834 = G__6842;
continue;
} else {
var event = cljs.core.first.call(null,seq__6831__$1);
re_frame.router.dispatch.call(null,event);


var G__6843 = cljs.core.next.call(null,seq__6831__$1);
var G__6844 = null;
var G__6845 = (0);
var G__6846 = (0);
seq__6831 = G__6843;
chunk__6832 = G__6844;
count__6833 = G__6845;
i__6834 = G__6846;
continue;
}
} else {
return null;
}
}
break;
}
}
}));
re_frame.fx.reg_fx.call(null,new cljs.core.Keyword(null,"deregister-event-handler","deregister-event-handler",-1096518994),(function (value){
var clear_event = cljs.core.partial.call(null,re_frame.registrar.clear_handlers,re_frame.events.kind);
if(cljs.core.sequential_QMARK_.call(null,value)){
var seq__6847 = cljs.core.seq.call(null,value);
var chunk__6848 = null;
var count__6849 = (0);
var i__6850 = (0);
while(true){
if((i__6850 < count__6849)){
var event = cljs.core._nth.call(null,chunk__6848,i__6850);
clear_event.call(null,event);


var G__6851 = seq__6847;
var G__6852 = chunk__6848;
var G__6853 = count__6849;
var G__6854 = (i__6850 + (1));
seq__6847 = G__6851;
chunk__6848 = G__6852;
count__6849 = G__6853;
i__6850 = G__6854;
continue;
} else {
var temp__5720__auto__ = cljs.core.seq.call(null,seq__6847);
if(temp__5720__auto__){
var seq__6847__$1 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__6847__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__6847__$1);
var G__6855 = cljs.core.chunk_rest.call(null,seq__6847__$1);
var G__6856 = c__4550__auto__;
var G__6857 = cljs.core.count.call(null,c__4550__auto__);
var G__6858 = (0);
seq__6847 = G__6855;
chunk__6848 = G__6856;
count__6849 = G__6857;
i__6850 = G__6858;
continue;
} else {
var event = cljs.core.first.call(null,seq__6847__$1);
clear_event.call(null,event);


var G__6859 = cljs.core.next.call(null,seq__6847__$1);
var G__6860 = null;
var G__6861 = (0);
var G__6862 = (0);
seq__6847 = G__6859;
chunk__6848 = G__6860;
count__6849 = G__6861;
i__6850 = G__6862;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return clear_event.call(null,value);
}
}));
re_frame.fx.reg_fx.call(null,new cljs.core.Keyword(null,"db","db",993250759),(function (value){
if((!((cljs.core.deref.call(null,re_frame.db.app_db) === value)))){
return cljs.core.reset_BANG_.call(null,re_frame.db.app_db,value);
} else {
return null;
}
}));

//# sourceMappingURL=fx.js.map
