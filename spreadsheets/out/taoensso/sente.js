// Compiled by ClojureScript 1.10.520 {}
goog.provide('taoensso.sente');
goog.require('cljs.core');
goog.require('clojure.string');
goog.require('cljs.core.async');
goog.require('taoensso.encore');
goog.require('taoensso.timbre');
goog.require('taoensso.sente.interfaces');
if(cljs.core.vector_QMARK_.call(null,taoensso.encore.encore_version)){
taoensso.encore.assert_min_encore_version.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(2),(79),(1)], null));
} else {
taoensso.encore.assert_min_encore_version.call(null,2.79);
}
/**
 * Useful for identifying client/server mismatch
 */
taoensso.sente.sente_version = new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(1),(11),(0)], null);
taoensso.sente.node_target_QMARK_ = cljs.core._EQ_.call(null,cljs.core._STAR_target_STAR_,"nodejs");
if((typeof taoensso !== 'undefined') && (typeof taoensso.sente !== 'undefined') && (typeof taoensso.sente.debug_mode_QMARK__ !== 'undefined')){
} else {
taoensso.sente.debug_mode_QMARK__ = cljs.core.atom.call(null,false);
}
taoensso.sente.expected = (function taoensso$sente$expected(expected,x){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"expected","expected",1583670997),expected,new cljs.core.Keyword(null,"actual","actual",107306363),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),cljs.core.type.call(null,x),new cljs.core.Keyword(null,"value","value",305978217),x], null)], null);
});
/**
 * Returns nil if given argument is a valid [ev-id ?ev-data] form. Otherwise
 *   returns a map of validation errors like `{:wrong-type {:expected _ :actual _}}`.
 */
taoensso.sente.validate_event = (function taoensso$sente$validate_event(x){
if((!(cljs.core.vector_QMARK_.call(null,x)))){
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"wrong-type","wrong-type",929556915),taoensso.sente.expected.call(null,new cljs.core.Keyword(null,"vector","vector",1902966158),x)], null);
} else {
if(cljs.core.not.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [(1),null,(2),null], null), null).call(null,cljs.core.count.call(null,x)))){
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"wrong-length","wrong-length",1367572281),taoensso.sente.expected.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [(1),null,(2),null], null), null),x)], null);
} else {
var vec__17980 = x;
var ev_id = cljs.core.nth.call(null,vec__17980,(0),null);
var _ = cljs.core.nth.call(null,vec__17980,(1),null);
if((!((ev_id instanceof cljs.core.Keyword)))){
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"wrong-id-type","wrong-id-type",-1213601689),taoensso.sente.expected.call(null,new cljs.core.Keyword(null,"keyword","keyword",811389747),ev_id)], null);
} else {
if(cljs.core.not.call(null,cljs.core.namespace.call(null,ev_id))){
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"unnamespaced-id","unnamespaced-id",1976189772),taoensso.sente.expected.call(null,new cljs.core.Keyword(null,"namespaced-keyword","namespaced-keyword",131372895),ev_id)], null);
} else {
return null;

}
}

}
}
});
/**
 * Returns given argument if it is a valid [ev-id ?ev-data] form. Otherwise
 *   throws a validation exception.
 */
taoensso.sente.assert_event = (function taoensso$sente$assert_event(x){
var temp__5720__auto__ = taoensso.sente.validate_event.call(null,x);
if(cljs.core.truth_(temp__5720__auto__)){
var errs = temp__5720__auto__;
throw cljs.core.ex_info.call(null,"Invalid event",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"given","given",716253602),x,new cljs.core.Keyword(null,"errors","errors",-908790718),errs], null));
} else {
return null;
}
});
/**
 * Valid [ev-id ?ev-data] form?
 */
taoensso.sente.event_QMARK_ = (function taoensso$sente$event_QMARK_(x){
return (taoensso.sente.validate_event.call(null,x) == null);
});
taoensso.sente.as_event = (function taoensso$sente$as_event(x){
var temp__5718__auto__ = taoensso.sente.validate_event.call(null,x);
if(cljs.core.truth_(temp__5718__auto__)){
var errs = temp__5718__auto__;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","bad-event","chsk/bad-event",-565206930),x], null);
} else {
return x;
}
});
taoensso.sente.client_event_msg_QMARK_ = (function taoensso$sente$client_event_msg_QMARK_(x){
var and__4120__auto__ = cljs.core.map_QMARK_.call(null,x);
if(and__4120__auto__){
var and__4120__auto____$1 = taoensso.encore.ks_EQ_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861),null,new cljs.core.Keyword(null,"state","state",-1988618099),null,new cljs.core.Keyword(null,"event","event",301435442),null,new cljs.core.Keyword(null,"id","id",-1388402092),null,new cljs.core.Keyword(null,"?data","?data",-9471433),null,new cljs.core.Keyword(null,"send-fn","send-fn",351002041),null], null), null),x);
if(and__4120__auto____$1){
var map__17987 = x;
var map__17987__$1 = (((((!((map__17987 == null))))?(((((map__17987.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__17987.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__17987):map__17987);
var ch_recv = cljs.core.get.call(null,map__17987__$1,new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861));
var send_fn = cljs.core.get.call(null,map__17987__$1,new cljs.core.Keyword(null,"send-fn","send-fn",351002041));
var state = cljs.core.get.call(null,map__17987__$1,new cljs.core.Keyword(null,"state","state",-1988618099));
var event = cljs.core.get.call(null,map__17987__$1,new cljs.core.Keyword(null,"event","event",301435442));
return ((taoensso.encore.chan_QMARK_.call(null,ch_recv)) && (cljs.core.ifn_QMARK_.call(null,send_fn)) && (taoensso.encore.atom_QMARK_.call(null,state)) && (taoensso.sente.event_QMARK_.call(null,event)));
} else {
return and__4120__auto____$1;
}
} else {
return and__4120__auto__;
}
});
taoensso.sente.server_event_msg_QMARK_ = (function taoensso$sente$server_event_msg_QMARK_(x){
var and__4120__auto__ = cljs.core.map_QMARK_.call(null,x);
if(and__4120__auto__){
var and__4120__auto____$1 = taoensso.encore.ks_EQ_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 10, [new cljs.core.Keyword(null,"?reply-fn","?reply-fn",-1479510592),null,new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861),null,new cljs.core.Keyword(null,"client-id","client-id",-464622140),null,new cljs.core.Keyword(null,"connected-uids","connected-uids",1454332231),null,new cljs.core.Keyword(null,"uid","uid",-1447769400),null,new cljs.core.Keyword(null,"event","event",301435442),null,new cljs.core.Keyword(null,"id","id",-1388402092),null,new cljs.core.Keyword(null,"ring-req","ring-req",-747861961),null,new cljs.core.Keyword(null,"?data","?data",-9471433),null,new cljs.core.Keyword(null,"send-fn","send-fn",351002041),null], null), null),x);
if(and__4120__auto____$1){
var map__17993 = x;
var map__17993__$1 = (((((!((map__17993 == null))))?(((((map__17993.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__17993.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__17993):map__17993);
var ch_recv = cljs.core.get.call(null,map__17993__$1,new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861));
var send_fn = cljs.core.get.call(null,map__17993__$1,new cljs.core.Keyword(null,"send-fn","send-fn",351002041));
var connected_uids = cljs.core.get.call(null,map__17993__$1,new cljs.core.Keyword(null,"connected-uids","connected-uids",1454332231));
var ring_req = cljs.core.get.call(null,map__17993__$1,new cljs.core.Keyword(null,"ring-req","ring-req",-747861961));
var client_id = cljs.core.get.call(null,map__17993__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
var event = cljs.core.get.call(null,map__17993__$1,new cljs.core.Keyword(null,"event","event",301435442));
var _QMARK_reply_fn = cljs.core.get.call(null,map__17993__$1,new cljs.core.Keyword(null,"?reply-fn","?reply-fn",-1479510592));
return ((taoensso.encore.chan_QMARK_.call(null,ch_recv)) && (cljs.core.ifn_QMARK_.call(null,send_fn)) && (taoensso.encore.atom_QMARK_.call(null,connected_uids)) && (cljs.core.map_QMARK_.call(null,ring_req)) && (taoensso.encore.nblank_str_QMARK_.call(null,client_id)) && (taoensso.sente.event_QMARK_.call(null,event)) && ((((_QMARK_reply_fn == null)) || (cljs.core.ifn_QMARK_.call(null,_QMARK_reply_fn)))));
} else {
return and__4120__auto____$1;
}
} else {
return and__4120__auto__;
}
});
/**
 * All server `event-msg`s go through this
 */
taoensso.sente.put_server_event_msg_GT_ch_recv_BANG_ = (function taoensso$sente$put_server_event_msg_GT_ch_recv_BANG_(ch_recv,p__17995){
var map__17996 = p__17995;
var map__17996__$1 = (((((!((map__17996 == null))))?(((((map__17996.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__17996.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__17996):map__17996);
var ev_msg = map__17996__$1;
var event = cljs.core.get.call(null,map__17996__$1,new cljs.core.Keyword(null,"event","event",301435442));
var _QMARK_reply_fn = cljs.core.get.call(null,map__17996__$1,new cljs.core.Keyword(null,"?reply-fn","?reply-fn",-1479510592));
var vec__17998 = taoensso.sente.as_event.call(null,event);
var ev_id = cljs.core.nth.call(null,vec__17998,(0),null);
var ev__QMARK_data = cljs.core.nth.call(null,vec__17998,(1),null);
var valid_event = vec__17998;
var ev_msg_STAR_ = cljs.core.merge.call(null,ev_msg,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"event","event",301435442),valid_event,new cljs.core.Keyword(null,"?reply-fn","?reply-fn",-1479510592),_QMARK_reply_fn,new cljs.core.Keyword(null,"id","id",-1388402092),ev_id,new cljs.core.Keyword(null,"?data","?data",-9471433),ev__QMARK_data], null));
if((!(taoensso.sente.server_event_msg_QMARK_.call(null,ev_msg_STAR_)))){
return taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,187,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (vec__17998,ev_id,ev__QMARK_data,valid_event,ev_msg_STAR_,map__17996,map__17996__$1,ev_msg,event,_QMARK_reply_fn){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Bad ev-msg: %s",ev_msg], null);
});})(vec__17998,ev_id,ev__QMARK_data,valid_event,ev_msg_STAR_,map__17996,map__17996__$1,ev_msg,event,_QMARK_reply_fn))
,null)),null,819643176);
} else {
return cljs.core.async.put_BANG_.call(null,ch_recv,ev_msg_STAR_);
}
});
taoensso.sente.cb_error_QMARK_ = (function taoensso$sente$cb_error_QMARK_(cb_reply_clj){
return new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("chsk","closed","chsk/closed",-922855264),null,new cljs.core.Keyword("chsk","error","chsk/error",-984175439),null,new cljs.core.Keyword("chsk","timeout","chsk/timeout",-319776489),null], null), null).call(null,cb_reply_clj);
});
taoensso.sente.cb_success_QMARK_ = (function taoensso$sente$cb_success_QMARK_(cb_reply_clj){
return cljs.core.not.call(null,taoensso.sente.cb_error_QMARK_.call(null,cb_reply_clj));
});
/**
 * prefixed-pstr->[clj ?cb-uuid]
 */
taoensso.sente.unpack = (function taoensso$sente$unpack(packer,prefixed_pstr){
if(typeof prefixed_pstr === 'string'){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",201,"(string? prefixed-pstr)",prefixed_pstr,null,null);
}

var wrapped_QMARK_ = taoensso.encore.str_starts_with_QMARK_.call(null,prefixed_pstr,"+");
var pstr = cljs.core.subs.call(null,prefixed_pstr,(1));
var clj = (function (){try{return taoensso.sente.interfaces.unpack.call(null,packer,pstr);
}catch (e18004){var t = e18004;
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"debug","debug",-1608172596),"taoensso.sente",null,208,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (t,wrapped_QMARK_,pstr){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Bad package: %s (%s)",pstr,t], null);
});})(t,wrapped_QMARK_,pstr))
,null)),null,1746243568);

return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","bad-package","chsk/bad-package",501893679),pstr], null);
}})();
var vec__18001 = ((wrapped_QMARK_)?clj:new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [clj,null], null));
var clj__$1 = cljs.core.nth.call(null,vec__18001,(0),null);
var _QMARK_cb_uuid = cljs.core.nth.call(null,vec__18001,(1),null);
var _QMARK_cb_uuid__$1 = ((cljs.core._EQ_.call(null,(0),_QMARK_cb_uuid))?new cljs.core.Keyword(null,"ajax-cb","ajax-cb",-807060321):_QMARK_cb_uuid);
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,214,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (wrapped_QMARK_,pstr,clj,vec__18001,clj__$1,_QMARK_cb_uuid,_QMARK_cb_uuid__$1){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Unpacking: %s -> %s",prefixed_pstr,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [clj__$1,_QMARK_cb_uuid__$1], null)], null);
});})(wrapped_QMARK_,pstr,clj,vec__18001,clj__$1,_QMARK_cb_uuid,_QMARK_cb_uuid__$1))
,null)),null,1117955225);

return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [clj__$1,_QMARK_cb_uuid__$1], null);
});
/**
 * clj->prefixed-pstr
 */
taoensso.sente.pack = (function taoensso$sente$pack(var_args){
var G__18006 = arguments.length;
switch (G__18006) {
case 2:
return taoensso.sente.pack.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return taoensso.sente.pack.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

taoensso.sente.pack.cljs$core$IFn$_invoke$arity$2 = (function (packer,clj){
var pstr = ["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(taoensso.sente.interfaces.pack.call(null,packer,clj))].join('');
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,221,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (pstr){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Packing (unwrapped): %s -> %s",clj,pstr], null);
});})(pstr))
,null)),null,-1305698680);

return pstr;
});

taoensso.sente.pack.cljs$core$IFn$_invoke$arity$3 = (function (packer,clj,_QMARK_cb_uuid){
var _QMARK_cb_uuid__$1 = ((cljs.core._EQ_.call(null,_QMARK_cb_uuid,new cljs.core.Keyword(null,"ajax-cb","ajax-cb",-807060321)))?(0):_QMARK_cb_uuid);
var wrapped_clj = (cljs.core.truth_(_QMARK_cb_uuid__$1)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [clj,_QMARK_cb_uuid__$1], null):new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [clj], null));
var pstr = ["+",cljs.core.str.cljs$core$IFn$_invoke$arity$1(taoensso.sente.interfaces.pack.call(null,packer,wrapped_clj))].join('');
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,230,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (_QMARK_cb_uuid__$1,wrapped_clj,pstr){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Packing (wrapped): %s -> %s",wrapped_clj,pstr], null);
});})(_QMARK_cb_uuid__$1,wrapped_clj,pstr))
,null)),null,-2146630173);

return pstr;
});

taoensso.sente.pack.cljs$lang$maxFixedArity = 3;


/**
* @constructor
 * @implements {taoensso.sente.interfaces.IPacker}
*/
taoensso.sente.EdnPacker = (function (){
});
taoensso.sente.EdnPacker.prototype.taoensso$sente$interfaces$IPacker$ = cljs.core.PROTOCOL_SENTINEL;

taoensso.sente.EdnPacker.prototype.taoensso$sente$interfaces$IPacker$pack$arity$2 = (function (_,x){
var self__ = this;
var ___$1 = this;
return taoensso.encore.pr_edn.call(null,x);
});

taoensso.sente.EdnPacker.prototype.taoensso$sente$interfaces$IPacker$unpack$arity$2 = (function (_,s){
var self__ = this;
var ___$1 = this;
return taoensso.encore.read_edn.call(null,s);
});

taoensso.sente.EdnPacker.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

taoensso.sente.EdnPacker.cljs$lang$type = true;

taoensso.sente.EdnPacker.cljs$lang$ctorStr = "taoensso.sente/EdnPacker";

taoensso.sente.EdnPacker.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"taoensso.sente/EdnPacker");
});

/**
 * Positional factory function for taoensso.sente/EdnPacker.
 */
taoensso.sente.__GT_EdnPacker = (function taoensso$sente$__GT_EdnPacker(){
return (new taoensso.sente.EdnPacker());
});

taoensso.sente.default_edn_packer = (new taoensso.sente.EdnPacker());
taoensso.sente.coerce_packer = (function taoensso$sente$coerce_packer(x){
if(cljs.core._EQ_.call(null,x,new cljs.core.Keyword(null,"edn","edn",1317840885))){
return taoensso.sente.default_edn_packer;
} else {
var e = (function (){try{if((function (p1__18008_SHARP_){
if((!((p1__18008_SHARP_ == null)))){
if(((false) || ((cljs.core.PROTOCOL_SENTINEL === p1__18008_SHARP_.taoensso$sente$interfaces$IPacker$)))){
return true;
} else {
if((!p1__18008_SHARP_.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_.call(null,taoensso.sente.interfaces.IPacker,p1__18008_SHARP_);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_.call(null,taoensso.sente.interfaces.IPacker,p1__18008_SHARP_);
}
}).call(null,x)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18009){if((e18009 instanceof Error)){
var e = e18009;
return e;
} else {
throw e18009;

}
}})();
if((e == null)){
return x;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",243,"((fn* [p1__18008#] (satisfies? interfaces/IPacker p1__18008#)) x)",x,e,null);
}
}
});
taoensso.sente.next_idx_BANG_ = taoensso.encore.idx_fn.call(null);


/**
 * Takes a web server adapter[1] and returns a map with keys:
 *  :ch-recv ; core.async channel to receive `event-msg`s (internal or from clients).
 *  :send-fn ; (fn [user-id ev] for server>user push.
 *  :ajax-post-fn                ; (fn [ring-req]) for Ring CSRF-POST + chsk URL.
 *  :ajax-get-or-ws-handshake-fn ; (fn [ring-req]) for Ring GET + chsk URL.
 *  :connected-uids ; Watchable, read-only (atom {:ws #{_} :ajax #{_} :any #{_}}).
 * 
 *   Common options:
 *  :user-id-fn        ; (fn [ring-req]) -> unique user-id for server>user push.
 *  :csrf-token-fn     ; (fn [ring-req]) -> CSRF token for Ajax POSTs.
 *  :handshake-data-fn ; (fn [ring-req]) -> arb user data to append to handshake evs.
 *  :ws-kalive-ms      ; Ping to keep a WebSocket conn alive if no activity
 *                     ; w/in given msecs. Should be different to client's :ws-kalive-ms.
 *  :lp-timeout-ms     ; Timeout (repoll) long-polling Ajax conns after given msecs.
 *  :send-buf-ms-ajax  ; [2]
 *  :send-buf-ms-ws    ; [2]
 *  :packer            ; :edn (default), or an IPacker implementation.
 * 
 *   [1] e.g. `(taoensso.sente.server-adapters.http-kit/get-sch-adapter)` or
 *         `(taoensso.sente.server-adapters.immutant/get-sch-adapter)`.
 *    You must have the necessary web-server dependency in your project.clj and
 *    the necessary entry in your namespace's `ns` form.
 * 
 *   [2] Optimization to allow transparent batching of rapidly-triggered
 *    server>user pushes. This is esp. important for Ajax clients which use a
 *    (slow) reconnecting poller. Actual event dispatch may occur <= given ms
 *    after send call (larger values => larger batch windows).
 */
taoensso.sente.make_channel_socket_server_BANG_ = (function taoensso$sente$make_channel_socket_server_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___18317 = arguments.length;
var i__4731__auto___18318 = (0);
while(true){
if((i__4731__auto___18318 < len__4730__auto___18317)){
args__4736__auto__.push((arguments[i__4731__auto___18318]));

var G__18319 = (i__4731__auto___18318 + (1));
i__4731__auto___18318 = G__18319;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return taoensso.sente.make_channel_socket_server_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

taoensso.sente.make_channel_socket_server_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (web_server_ch_adapter,p__18014){
var vec__18015 = p__18014;
var map__18018 = cljs.core.nth.call(null,vec__18015,(0),null);
var map__18018__$1 = (((((!((map__18018 == null))))?(((((map__18018.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18018.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18018):map__18018);
var ws_kalive_ms = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),taoensso.encore.ms.call(null,new cljs.core.Keyword(null,"secs","secs",1532330091),(25)));
var send_buf_ms_ws = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"send-buf-ms-ws","send-buf-ms-ws",-1149586238),(30));
var lp_timeout_ms = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"lp-timeout-ms","lp-timeout-ms",-1451963133),taoensso.encore.ms.call(null,new cljs.core.Keyword(null,"secs","secs",1532330091),(20)));
var csrf_token_fn = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"csrf-token-fn","csrf-token-fn",-1846298394),((function (vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms){
return (function (ring_req){
var or__4131__auto__ = new cljs.core.Keyword(null,"anti-forgery-token","anti-forgery-token",806990841).cljs$core$IFn$_invoke$arity$1(ring_req);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = cljs.core.get_in.call(null,ring_req,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"session","session",1008279103),new cljs.core.Keyword(null,"csrf-token","csrf-token",-1872302856)], null));
if(cljs.core.truth_(or__4131__auto____$1)){
return or__4131__auto____$1;
} else {
var or__4131__auto____$2 = cljs.core.get_in.call(null,ring_req,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"session","session",1008279103),new cljs.core.Keyword("ring.middleware.anti-forgery","anti-forgery-token","ring.middleware.anti-forgery/anti-forgery-token",571563484)], null));
if(cljs.core.truth_(or__4131__auto____$2)){
return or__4131__auto____$2;
} else {
return cljs.core.get_in.call(null,ring_req,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"session","session",1008279103),"__anti-forgery-token"], null));
}
}
}
});})(vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms))
);
var packer = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"packer","packer",66077544),new cljs.core.Keyword(null,"edn","edn",1317840885));
var send_buf_ms_ajax = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"send-buf-ms-ajax","send-buf-ms-ajax",1546129037),(100));
var handshake_data_fn = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"handshake-data-fn","handshake-data-fn",2011983089),((function (vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax){
return (function (ring_req){
return null;
});})(vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax))
);
var user_id_fn = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"user-id-fn","user-id-fn",-1532150029),((function (vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn){
return (function (ring_req){
return cljs.core.get_in.call(null,ring_req,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"session","session",1008279103),new cljs.core.Keyword(null,"uid","uid",-1447769400)], null));
});})(vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn))
);
var recv_buf_or_n = cljs.core.get.call(null,map__18018__$1,new cljs.core.Keyword(null,"recv-buf-or-n","recv-buf-or-n",1363950355),cljs.core.async.sliding_buffer.call(null,(1000)));
new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var e = (function (){try{if(taoensso.encore.pos_int_QMARK_.call(null,send_buf_ms_ajax)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18020){if((e18020 instanceof Error)){
var e = e18020;
return e;
} else {
throw e18020;

}
}})();
if((e == null)){
return true;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",314,"(enc/pos-int? send-buf-ms-ajax)",send_buf_ms_ajax,e,null);
}
})(),(function (){var e = (function (){try{if(taoensso.encore.pos_int_QMARK_.call(null,send_buf_ms_ws)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18021){if((e18021 instanceof Error)){
var e = e18021;
return e;
} else {
throw e18021;

}
}})();
if((e == null)){
return true;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",314,"(enc/pos-int? send-buf-ms-ws)",send_buf_ms_ws,e,null);
}
})()], null);

var e_18320 = (function (){try{if(((function (vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (p1__18011_SHARP_){
if((!((p1__18011_SHARP_ == null)))){
if(((false) || ((cljs.core.PROTOCOL_SENTINEL === p1__18011_SHARP_.taoensso$sente$interfaces$IServerChanAdapter$)))){
return true;
} else {
if((!p1__18011_SHARP_.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_.call(null,taoensso.sente.interfaces.IServerChanAdapter,p1__18011_SHARP_);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_.call(null,taoensso.sente.interfaces.IServerChanAdapter,p1__18011_SHARP_);
}
});})(vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
.call(null,web_server_ch_adapter)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18022){if((e18022 instanceof Error)){
var e = e18022;
return e;
} else {
throw e18022;

}
}})();
if((e_18320 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",315,"((fn* [p1__18011#] (satisfies? interfaces/IServerChanAdapter p1__18011#)) web-server-ch-adapter)",web_server_ch_adapter,e_18320,null);
}

var max_ms_18321 = taoensso.sente.default_client_side_ajax_timeout_ms;
if((lp_timeout_ms >= max_ms_18321)){
throw cljs.core.ex_info.call(null,[":lp-timeout-ms must be < ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(max_ms_18321)].join(''),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"lp-timeout-ms","lp-timeout-ms",-1451963133),lp_timeout_ms,new cljs.core.Keyword(null,"default-client-side-ajax-timeout-ms","default-client-side-ajax-timeout-ms",1149929762),max_ms_18321], null));
} else {
}

var packer__$1 = taoensso.sente.coerce_packer.call(null,packer);
var ch_recv = cljs.core.async.chan.call(null,recv_buf_or_n);
var user_id_fn__$1 = ((function (packer__$1,ch_recv,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (ring_req,client_id){
var or__4131__auto__ = user_id_fn.call(null,cljs.core.assoc.call(null,ring_req,new cljs.core.Keyword(null,"client-id","client-id",-464622140),client_id));
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Keyword("taoensso.sente","nil-uid","taoensso.sente/nil-uid",-2111603486);
}
});})(packer__$1,ch_recv,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
var conns_ = cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"ws","ws",86841443),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"ajax","ajax",814345549),cljs.core.PersistentArrayMap.EMPTY], null));
var send_buffers_ = cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"ws","ws",86841443),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"ajax","ajax",814345549),cljs.core.PersistentArrayMap.EMPTY], null));
var connected_uids_ = cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ws","ws",86841443),cljs.core.PersistentHashSet.EMPTY,new cljs.core.Keyword(null,"ajax","ajax",814345549),cljs.core.PersistentHashSet.EMPTY,new cljs.core.Keyword(null,"any","any",1705907423),cljs.core.PersistentHashSet.EMPTY], null));
var upd_conn_BANG_ = ((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var G__18322 = null;
var G__18322__3 = (function (conn_type,uid,client_id){
return taoensso.encore.swap_in_BANG_.call(null,conns_,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [conn_type,uid,client_id], null),((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (_QMARK_v){
var vec__18024 = _QMARK_v;
var _QMARK_sch = cljs.core.nth.call(null,vec__18024,(0),null);
var _udt = cljs.core.nth.call(null,vec__18024,(1),null);
var new_udt = taoensso.encore.now_udt.call(null);
return taoensso.encore.swapped.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [_QMARK_sch,new_udt], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"init?","init?",438181499),(_QMARK_v == null),new cljs.core.Keyword(null,"udt","udt",2011712751),new_udt,new cljs.core.Keyword(null,"?sch","?sch",2064493898),_QMARK_sch], null));
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);
});
var G__18322__4 = (function (conn_type,uid,client_id,new__QMARK_sch){
return taoensso.encore.swap_in_BANG_.call(null,conns_,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [conn_type,uid,client_id], null),((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (_QMARK_v){
var new_udt = taoensso.encore.now_udt.call(null);
return taoensso.encore.swapped.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new__QMARK_sch,new_udt], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"init?","init?",438181499),(_QMARK_v == null),new cljs.core.Keyword(null,"udt","udt",2011712751),new_udt,new cljs.core.Keyword(null,"?sch","?sch",2064493898),new__QMARK_sch], null));
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);
});
G__18322 = function(conn_type,uid,client_id,new__QMARK_sch){
switch(arguments.length){
case 3:
return G__18322__3.call(this,conn_type,uid,client_id);
case 4:
return G__18322__4.call(this,conn_type,uid,client_id,new__QMARK_sch);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
G__18322.cljs$core$IFn$_invoke$arity$3 = G__18322__3;
G__18322.cljs$core$IFn$_invoke$arity$4 = G__18322__4;
return G__18322;
})()
;})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
var connect_uid_BANG_ = ((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (conn_type,uid){
if(cljs.core.truth_((function (){var e = (function (){try{if(taoensso.truss.impl.some_QMARK_.call(null,uid)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18027){if((e18027 instanceof Error)){
var e = e18027;
return e;
} else {
throw e18027;

}
}})();
if((e == null)){
return true;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",359,"(taoensso.truss.impl/some? uid)",uid,e,null);
}
})())){
} else {
throw (new Error("Assert failed: (have? uid)"));
}

var newly_connected_QMARK_ = taoensso.encore.swap_in_BANG_.call(null,connected_uids_,cljs.core.PersistentVector.EMPTY,((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (p__18028){
var map__18029 = p__18028;
var map__18029__$1 = (((((!((map__18029 == null))))?(((((map__18029.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18029.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18029):map__18029);
var old_m = map__18029__$1;
var ws = cljs.core.get.call(null,map__18029__$1,new cljs.core.Keyword(null,"ws","ws",86841443));
var ajax = cljs.core.get.call(null,map__18029__$1,new cljs.core.Keyword(null,"ajax","ajax",814345549));
var any = cljs.core.get.call(null,map__18029__$1,new cljs.core.Keyword(null,"any","any",1705907423));
var new_m = (function (){var G__18031 = conn_type;
var G__18031__$1 = (((G__18031 instanceof cljs.core.Keyword))?G__18031.fqn:null);
switch (G__18031__$1) {
case "ws":
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ws","ws",86841443),cljs.core.conj.call(null,ws,uid),new cljs.core.Keyword(null,"ajax","ajax",814345549),ajax,new cljs.core.Keyword(null,"any","any",1705907423),cljs.core.conj.call(null,any,uid)], null);

break;
case "ajax":
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ws","ws",86841443),ws,new cljs.core.Keyword(null,"ajax","ajax",814345549),cljs.core.conj.call(null,ajax,uid),new cljs.core.Keyword(null,"any","any",1705907423),cljs.core.conj.call(null,any,uid)], null);

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18031__$1)].join('')));

}
})();
return taoensso.encore.swapped.call(null,new_m,(function (){var old_any = new cljs.core.Keyword(null,"any","any",1705907423).cljs$core$IFn$_invoke$arity$1(old_m);
var new_any = new cljs.core.Keyword(null,"any","any",1705907423).cljs$core$IFn$_invoke$arity$1(new_m);
if((((!(cljs.core.contains_QMARK_.call(null,old_any,uid)))) && (cljs.core.contains_QMARK_.call(null,new_any,uid)))){
return new cljs.core.Keyword(null,"newly-connected","newly-connected",-2029862681);
} else {
return null;
}
})());
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);
return newly_connected_QMARK_;
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
var upd_connected_uid_BANG_ = ((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (uid){
if(cljs.core.truth_((function (){var e = (function (){try{if(taoensso.truss.impl.some_QMARK_.call(null,uid)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18032){if((e18032 instanceof Error)){
var e = e18032;
return e;
} else {
throw e18032;

}
}})();
if((e == null)){
return true;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",376,"(taoensso.truss.impl/some? uid)",uid,e,null);
}
})())){
} else {
throw (new Error("Assert failed: (have? uid)"));
}

var newly_disconnected_QMARK_ = taoensso.encore.swap_in_BANG_.call(null,connected_uids_,cljs.core.PersistentVector.EMPTY,((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (p__18033){
var map__18034 = p__18033;
var map__18034__$1 = (((((!((map__18034 == null))))?(((((map__18034.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18034.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18034):map__18034);
var old_m = map__18034__$1;
var ws = cljs.core.get.call(null,map__18034__$1,new cljs.core.Keyword(null,"ws","ws",86841443));
var ajax = cljs.core.get.call(null,map__18034__$1,new cljs.core.Keyword(null,"ajax","ajax",814345549));
var any = cljs.core.get.call(null,map__18034__$1,new cljs.core.Keyword(null,"any","any",1705907423));
var conns_SINGLEQUOTE_ = cljs.core.deref.call(null,conns_);
var any_ws_clients_QMARK_ = cljs.core.contains_QMARK_.call(null,new cljs.core.Keyword(null,"ws","ws",86841443).cljs$core$IFn$_invoke$arity$1(conns_SINGLEQUOTE_),uid);
var any_ajax_clients_QMARK_ = cljs.core.contains_QMARK_.call(null,new cljs.core.Keyword(null,"ajax","ajax",814345549).cljs$core$IFn$_invoke$arity$1(conns_SINGLEQUOTE_),uid);
var any_clients_QMARK_ = ((any_ws_clients_QMARK_) || (any_ajax_clients_QMARK_));
var new_m = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ws","ws",86841443),((any_ws_clients_QMARK_)?cljs.core.conj.call(null,ws,uid):cljs.core.disj.call(null,ws,uid)),new cljs.core.Keyword(null,"ajax","ajax",814345549),((any_ajax_clients_QMARK_)?cljs.core.conj.call(null,ajax,uid):cljs.core.disj.call(null,ajax,uid)),new cljs.core.Keyword(null,"any","any",1705907423),((any_clients_QMARK_)?cljs.core.conj.call(null,any,uid):cljs.core.disj.call(null,any,uid))], null);
return taoensso.encore.swapped.call(null,new_m,(function (){var old_any = new cljs.core.Keyword(null,"any","any",1705907423).cljs$core$IFn$_invoke$arity$1(old_m);
var new_any = new cljs.core.Keyword(null,"any","any",1705907423).cljs$core$IFn$_invoke$arity$1(new_m);
if(((cljs.core.contains_QMARK_.call(null,old_any,uid)) && ((!(cljs.core.contains_QMARK_.call(null,new_any,uid)))))){
return new cljs.core.Keyword(null,"newly-disconnected","newly-disconnected",-1586164962);
} else {
return null;
}
})());
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);
return newly_disconnected_QMARK_;
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
var send_fn = ((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() { 
var G__18324__delegate = function (user_id,ev,p__18036){
var vec__18037 = p__18036;
var map__18040 = cljs.core.nth.call(null,vec__18037,(0),null);
var map__18040__$1 = (((((!((map__18040 == null))))?(((((map__18040.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18040.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18040):map__18040);
var opts = map__18040__$1;
var flush_QMARK_ = cljs.core.get.call(null,map__18040__$1,new cljs.core.Keyword(null,"flush?","flush?",-108887231));
var uid_18325 = ((cljs.core._EQ_.call(null,user_id,new cljs.core.Keyword("sente","all-users-without-uid","sente/all-users-without-uid",-42979578)))?new cljs.core.Keyword("taoensso.sente","nil-uid","taoensso.sente/nil-uid",-2111603486):user_id);
var __18326 = taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,402,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (uid_18325,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk send: (->uid %s) %s",uid_18325,ev], null);
});})(uid_18325,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,1957670472);
var __18327__$1 = (cljs.core.truth_(uid_18325)?null:(function(){throw (new Error(["Assert failed: ",["Support for sending to `nil` user-ids has been REMOVED. ","Please send to `:sente/all-users-without-uid` instead."].join(''),"\n","uid"].join('')))})());
var __18328__$2 = taoensso.sente.assert_event.call(null,ev);
var ev_uuid_18329 = taoensso.encore.uuid_str.call(null);
var flush_buffer_BANG__18330 = ((function (uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (conn_type){
var temp__5720__auto__ = taoensso.encore.swap_in_BANG_.call(null,send_buffers_,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [conn_type], null),((function (uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (m){
var vec__18042 = cljs.core.get.call(null,m,uid_18325);
var ___$3 = cljs.core.nth.call(null,vec__18042,(0),null);
var ev_uuids = cljs.core.nth.call(null,vec__18042,(1),null);
if(cljs.core.contains_QMARK_.call(null,ev_uuids,ev_uuid_18329)){
return taoensso.encore.swapped.call(null,cljs.core.dissoc.call(null,m,uid_18325),cljs.core.get.call(null,m,uid_18325));
} else {
return taoensso.encore.swapped.call(null,m,null);
}
});})(uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);
if(cljs.core.truth_(temp__5720__auto__)){
var pulled = temp__5720__auto__;
var vec__18045 = pulled;
var buffered_evs = cljs.core.nth.call(null,vec__18045,(0),null);
var ev_uuids = cljs.core.nth.call(null,vec__18045,(1),null);
if(cljs.core.vector_QMARK_.call(null,buffered_evs)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",429,"(vector? buffered-evs)",buffered_evs,null,null);
}

if(cljs.core.set_QMARK_.call(null,ev_uuids)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",430,"(set? ev-uuids)",ev_uuids,null,null);
}

var buffered_evs_ppstr = taoensso.sente.pack.call(null,packer__$1,buffered_evs);
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,433,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (buffered_evs_ppstr,vec__18045,buffered_evs,ev_uuids,pulled,temp__5720__auto__,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["buffered-evs-ppstr: %s",buffered_evs_ppstr], null);
});})(buffered_evs_ppstr,vec__18045,buffered_evs,ev_uuids,pulled,temp__5720__auto__,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,-588737156);

var G__18048 = conn_type;
var G__18048__$1 = (((G__18048 instanceof cljs.core.Keyword))?G__18048.fqn:null);
switch (G__18048__$1) {
case "ws":
return taoensso.sente.send_buffered_server_evs_GT_ws_clients_BANG_.call(null,conns_,uid_18325,buffered_evs_ppstr,upd_conn_BANG_);

break;
case "ajax":
return taoensso.sente.send_buffered_server_evs_GT_ajax_clients_BANG_.call(null,conns_,uid_18325,buffered_evs_ppstr);

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18048__$1)].join('')));

}
} else {
return null;
}
});})(uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
if(cljs.core._EQ_.call(null,ev,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","close","chsk/close",1840295819)], null))){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"debug","debug",-1608172596),"taoensso.sente",null,442,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk closing (client may reconnect): %s",uid_18325], null);
});})(uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,2110203704);

if(cljs.core.truth_(flush_QMARK_)){
flush_buffer_BANG__18330.call(null,new cljs.core.Keyword(null,"ws","ws",86841443));

flush_buffer_BANG__18330.call(null,new cljs.core.Keyword(null,"ajax","ajax",814345549));
} else {
}

var seq__18049_18332 = cljs.core.seq.call(null,cljs.core.vals.call(null,cljs.core.get_in.call(null,cljs.core.deref.call(null,conns_),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ws","ws",86841443),uid_18325], null))));
var chunk__18050_18333 = null;
var count__18051_18334 = (0);
var i__18052_18335 = (0);
while(true){
if((i__18052_18335 < count__18051_18334)){
var vec__18059_18336 = cljs.core._nth.call(null,chunk__18050_18333,i__18052_18335);
var _QMARK_sch_18337 = cljs.core.nth.call(null,vec__18059_18336,(0),null);
var _udt_18338 = cljs.core.nth.call(null,vec__18059_18336,(1),null);
var temp__5720__auto___18339 = _QMARK_sch_18337;
if(cljs.core.truth_(temp__5720__auto___18339)){
var sch_18340 = temp__5720__auto___18339;
taoensso.sente.interfaces.sch_close_BANG_.call(null,sch_18340);
} else {
}


var G__18341 = seq__18049_18332;
var G__18342 = chunk__18050_18333;
var G__18343 = count__18051_18334;
var G__18344 = (i__18052_18335 + (1));
seq__18049_18332 = G__18341;
chunk__18050_18333 = G__18342;
count__18051_18334 = G__18343;
i__18052_18335 = G__18344;
continue;
} else {
var temp__5720__auto___18345 = cljs.core.seq.call(null,seq__18049_18332);
if(temp__5720__auto___18345){
var seq__18049_18346__$1 = temp__5720__auto___18345;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__18049_18346__$1)){
var c__4550__auto___18347 = cljs.core.chunk_first.call(null,seq__18049_18346__$1);
var G__18348 = cljs.core.chunk_rest.call(null,seq__18049_18346__$1);
var G__18349 = c__4550__auto___18347;
var G__18350 = cljs.core.count.call(null,c__4550__auto___18347);
var G__18351 = (0);
seq__18049_18332 = G__18348;
chunk__18050_18333 = G__18349;
count__18051_18334 = G__18350;
i__18052_18335 = G__18351;
continue;
} else {
var vec__18062_18352 = cljs.core.first.call(null,seq__18049_18346__$1);
var _QMARK_sch_18353 = cljs.core.nth.call(null,vec__18062_18352,(0),null);
var _udt_18354 = cljs.core.nth.call(null,vec__18062_18352,(1),null);
var temp__5720__auto___18355__$1 = _QMARK_sch_18353;
if(cljs.core.truth_(temp__5720__auto___18355__$1)){
var sch_18356 = temp__5720__auto___18355__$1;
taoensso.sente.interfaces.sch_close_BANG_.call(null,sch_18356);
} else {
}


var G__18357 = cljs.core.next.call(null,seq__18049_18346__$1);
var G__18358 = null;
var G__18359 = (0);
var G__18360 = (0);
seq__18049_18332 = G__18357;
chunk__18050_18333 = G__18358;
count__18051_18334 = G__18359;
i__18052_18335 = G__18360;
continue;
}
} else {
}
}
break;
}

var seq__18065_18361 = cljs.core.seq.call(null,cljs.core.vals.call(null,cljs.core.get_in.call(null,cljs.core.deref.call(null,conns_),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ajax","ajax",814345549),uid_18325], null))));
var chunk__18066_18362 = null;
var count__18067_18363 = (0);
var i__18068_18364 = (0);
while(true){
if((i__18068_18364 < count__18067_18363)){
var vec__18075_18365 = cljs.core._nth.call(null,chunk__18066_18362,i__18068_18364);
var _QMARK_sch_18366 = cljs.core.nth.call(null,vec__18075_18365,(0),null);
var _udt_18367 = cljs.core.nth.call(null,vec__18075_18365,(1),null);
var temp__5720__auto___18368 = _QMARK_sch_18366;
if(cljs.core.truth_(temp__5720__auto___18368)){
var sch_18369 = temp__5720__auto___18368;
taoensso.sente.interfaces.sch_close_BANG_.call(null,sch_18369);
} else {
}


var G__18370 = seq__18065_18361;
var G__18371 = chunk__18066_18362;
var G__18372 = count__18067_18363;
var G__18373 = (i__18068_18364 + (1));
seq__18065_18361 = G__18370;
chunk__18066_18362 = G__18371;
count__18067_18363 = G__18372;
i__18068_18364 = G__18373;
continue;
} else {
var temp__5720__auto___18374 = cljs.core.seq.call(null,seq__18065_18361);
if(temp__5720__auto___18374){
var seq__18065_18375__$1 = temp__5720__auto___18374;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__18065_18375__$1)){
var c__4550__auto___18376 = cljs.core.chunk_first.call(null,seq__18065_18375__$1);
var G__18377 = cljs.core.chunk_rest.call(null,seq__18065_18375__$1);
var G__18378 = c__4550__auto___18376;
var G__18379 = cljs.core.count.call(null,c__4550__auto___18376);
var G__18380 = (0);
seq__18065_18361 = G__18377;
chunk__18066_18362 = G__18378;
count__18067_18363 = G__18379;
i__18068_18364 = G__18380;
continue;
} else {
var vec__18078_18381 = cljs.core.first.call(null,seq__18065_18375__$1);
var _QMARK_sch_18382 = cljs.core.nth.call(null,vec__18078_18381,(0),null);
var _udt_18383 = cljs.core.nth.call(null,vec__18078_18381,(1),null);
var temp__5720__auto___18384__$1 = _QMARK_sch_18382;
if(cljs.core.truth_(temp__5720__auto___18384__$1)){
var sch_18385 = temp__5720__auto___18384__$1;
taoensso.sente.interfaces.sch_close_BANG_.call(null,sch_18385);
} else {
}


var G__18386 = cljs.core.next.call(null,seq__18065_18375__$1);
var G__18387 = null;
var G__18388 = (0);
var G__18389 = (0);
seq__18065_18361 = G__18386;
chunk__18066_18362 = G__18387;
count__18067_18363 = G__18388;
i__18068_18364 = G__18389;
continue;
}
} else {
}
}
break;
}
} else {
var seq__18081_18390 = cljs.core.seq.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ws","ws",86841443),new cljs.core.Keyword(null,"ajax","ajax",814345549)], null));
var chunk__18082_18391 = null;
var count__18083_18392 = (0);
var i__18084_18393 = (0);
while(true){
if((i__18084_18393 < count__18083_18392)){
var conn_type_18394 = cljs.core._nth.call(null,chunk__18082_18391,i__18084_18393);
taoensso.encore.swap_in_BANG_.call(null,send_buffers_,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [conn_type_18394,uid_18325], null),((function (seq__18081_18390,chunk__18082_18391,count__18083_18392,i__18084_18393,conn_type_18394,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (_QMARK_v){
if(cljs.core.not.call(null,_QMARK_v)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [ev], null),cljs.core.PersistentHashSet.createAsIfByAssoc([ev_uuid_18329])], null);
} else {
var vec__18091 = _QMARK_v;
var buffered_evs = cljs.core.nth.call(null,vec__18091,(0),null);
var ev_uuids = cljs.core.nth.call(null,vec__18091,(1),null);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.conj.call(null,buffered_evs,ev),cljs.core.conj.call(null,ev_uuids,ev_uuid_18329)], null);
}
});})(seq__18081_18390,chunk__18082_18391,count__18083_18392,i__18084_18393,conn_type_18394,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);


var G__18395 = seq__18081_18390;
var G__18396 = chunk__18082_18391;
var G__18397 = count__18083_18392;
var G__18398 = (i__18084_18393 + (1));
seq__18081_18390 = G__18395;
chunk__18082_18391 = G__18396;
count__18083_18392 = G__18397;
i__18084_18393 = G__18398;
continue;
} else {
var temp__5720__auto___18399 = cljs.core.seq.call(null,seq__18081_18390);
if(temp__5720__auto___18399){
var seq__18081_18400__$1 = temp__5720__auto___18399;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__18081_18400__$1)){
var c__4550__auto___18401 = cljs.core.chunk_first.call(null,seq__18081_18400__$1);
var G__18402 = cljs.core.chunk_rest.call(null,seq__18081_18400__$1);
var G__18403 = c__4550__auto___18401;
var G__18404 = cljs.core.count.call(null,c__4550__auto___18401);
var G__18405 = (0);
seq__18081_18390 = G__18402;
chunk__18082_18391 = G__18403;
count__18083_18392 = G__18404;
i__18084_18393 = G__18405;
continue;
} else {
var conn_type_18406 = cljs.core.first.call(null,seq__18081_18400__$1);
taoensso.encore.swap_in_BANG_.call(null,send_buffers_,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [conn_type_18406,uid_18325], null),((function (seq__18081_18390,chunk__18082_18391,count__18083_18392,i__18084_18393,conn_type_18406,seq__18081_18400__$1,temp__5720__auto___18399,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (_QMARK_v){
if(cljs.core.not.call(null,_QMARK_v)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [ev], null),cljs.core.PersistentHashSet.createAsIfByAssoc([ev_uuid_18329])], null);
} else {
var vec__18094 = _QMARK_v;
var buffered_evs = cljs.core.nth.call(null,vec__18094,(0),null);
var ev_uuids = cljs.core.nth.call(null,vec__18094,(1),null);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.conj.call(null,buffered_evs,ev),cljs.core.conj.call(null,ev_uuids,ev_uuid_18329)], null);
}
});})(seq__18081_18390,chunk__18082_18391,count__18083_18392,i__18084_18393,conn_type_18406,seq__18081_18400__$1,temp__5720__auto___18399,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);


var G__18407 = cljs.core.next.call(null,seq__18081_18400__$1);
var G__18408 = null;
var G__18409 = (0);
var G__18410 = (0);
seq__18081_18390 = G__18407;
chunk__18082_18391 = G__18408;
count__18083_18392 = G__18409;
i__18084_18393 = G__18410;
continue;
}
} else {
}
}
break;
}

if(cljs.core.truth_(flush_QMARK_)){
flush_buffer_BANG__18330.call(null,new cljs.core.Keyword(null,"ws","ws",86841443));

flush_buffer_BANG__18330.call(null,new cljs.core.Keyword(null,"ajax","ajax",814345549));
} else {
var ws_timeout_18411 = cljs.core.async.timeout.call(null,send_buf_ms_ws);
var ajax_timeout_18412 = cljs.core.async.timeout.call(null,send_buf_ms_ajax);
var c__8790__auto___18413 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___18413,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___18413,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (state_18101){
var state_val_18102 = (state_18101[(1)]);
if((state_val_18102 === (1))){
var state_18101__$1 = state_18101;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18101__$1,(2),ws_timeout_18411);
} else {
if((state_val_18102 === (2))){
var inst_18098 = (state_18101[(2)]);
var inst_18099 = flush_buffer_BANG__18330.call(null,new cljs.core.Keyword(null,"ws","ws",86841443));
var state_18101__$1 = (function (){var statearr_18103 = state_18101;
(statearr_18103[(7)] = inst_18098);

return statearr_18103;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18101__$1,inst_18099);
} else {
return null;
}
}
});})(c__8790__auto___18413,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
return ((function (switch__8695__auto__,c__8790__auto___18413,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18104 = [null,null,null,null,null,null,null,null];
(statearr_18104[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18104[(1)] = (1));

return statearr_18104;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18101){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18101);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18105){if((e18105 instanceof Object)){
var ex__8699__auto__ = e18105;
var statearr_18106_18414 = state_18101;
(statearr_18106_18414[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18101);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18105;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18415 = state_18101;
state_18101 = G__18415;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18101){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18101);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___18413,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var state__8792__auto__ = (function (){var statearr_18107 = f__8791__auto__.call(null);
(statearr_18107[(6)] = c__8790__auto___18413);

return statearr_18107;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___18413,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);


var c__8790__auto___18416 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___18416,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___18416,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (state_18112){
var state_val_18113 = (state_18112[(1)]);
if((state_val_18113 === (1))){
var state_18112__$1 = state_18112;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18112__$1,(2),ajax_timeout_18412);
} else {
if((state_val_18113 === (2))){
var inst_18109 = (state_18112[(2)]);
var inst_18110 = flush_buffer_BANG__18330.call(null,new cljs.core.Keyword(null,"ajax","ajax",814345549));
var state_18112__$1 = (function (){var statearr_18114 = state_18112;
(statearr_18114[(7)] = inst_18109);

return statearr_18114;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18112__$1,inst_18110);
} else {
return null;
}
}
});})(c__8790__auto___18416,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
return ((function (switch__8695__auto__,c__8790__auto___18416,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18115 = [null,null,null,null,null,null,null,null];
(statearr_18115[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18115[(1)] = (1));

return statearr_18115;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18112){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18112);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18116){if((e18116 instanceof Object)){
var ex__8699__auto__ = e18116;
var statearr_18117_18417 = state_18112;
(statearr_18117_18417[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18112);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18116;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18418 = state_18112;
state_18112 = G__18418;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18112){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18112);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___18416,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var state__8792__auto__ = (function (){var statearr_18118 = f__8791__auto__.call(null);
(statearr_18118[(6)] = c__8790__auto___18416);

return statearr_18118;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___18416,ws_timeout_18411,ajax_timeout_18412,uid_18325,__18326,__18327__$1,__18328__$2,ev_uuid_18329,flush_buffer_BANG__18330,vec__18037,map__18040,map__18040__$1,opts,flush_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);

}
}

return null;
};
var G__18324 = function (user_id,ev,var_args){
var p__18036 = null;
if (arguments.length > 2) {
var G__18419__i = 0, G__18419__a = new Array(arguments.length -  2);
while (G__18419__i < G__18419__a.length) {G__18419__a[G__18419__i] = arguments[G__18419__i + 2]; ++G__18419__i;}
  p__18036 = new cljs.core.IndexedSeq(G__18419__a,0,null);
} 
return G__18324__delegate.call(this,user_id,ev,p__18036);};
G__18324.cljs$lang$maxFixedArity = 2;
G__18324.cljs$lang$applyTo = (function (arglist__18420){
var user_id = cljs.core.first(arglist__18420);
arglist__18420 = cljs.core.next(arglist__18420);
var ev = cljs.core.first(arglist__18420);
var p__18036 = cljs.core.rest(arglist__18420);
return G__18324__delegate(user_id,ev,p__18036);
});
G__18324.cljs$core$IFn$_invoke$arity$variadic = G__18324__delegate;
return G__18324;
})()
;})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
var ev_msg_const = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861),ch_recv,new cljs.core.Keyword(null,"send-fn","send-fn",351002041),send_fn,new cljs.core.Keyword(null,"connected-uids","connected-uids",1454332231),connected_uids_], null);
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861),ch_recv,new cljs.core.Keyword(null,"send-fn","send-fn",351002041),send_fn,new cljs.core.Keyword(null,"connected-uids","connected-uids",1454332231),connected_uids_,new cljs.core.Keyword(null,"ajax-post-fn","ajax-post-fn",1830071264),((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (ring_req){
return taoensso.sente.interfaces.ring_req__GT_server_ch_resp.call(null,web_server_ch_adapter,ring_req,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-open","on-open",-1391088163),((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (server_ch,websocket_QMARK_){
if(cljs.core.not.call(null,websocket_QMARK_)){
} else {
throw (new Error("Assert failed: (not websocket?)"));
}

var params = cljs.core.get.call(null,ring_req,new cljs.core.Keyword(null,"params","params",710516235));
var ppstr = cljs.core.get.call(null,params,new cljs.core.Keyword(null,"ppstr","ppstr",1557495252));
var client_id = cljs.core.get.call(null,params,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
var vec__18119 = taoensso.sente.unpack.call(null,packer__$1,ppstr);
var clj = cljs.core.nth.call(null,vec__18119,(0),null);
var has_cb_QMARK_ = cljs.core.nth.call(null,vec__18119,(1),null);
var reply_fn = (function (){var replied_QMARK__ = cljs.core.atom.call(null,false);
return ((function (replied_QMARK__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (resp_clj){
if(cljs.core.compare_and_set_BANG_.call(null,replied_QMARK__,false,true)){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,511,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (replied_QMARK__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk send (ajax post reply): %s",resp_clj], null);
});})(replied_QMARK__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,1592986260);

return taoensso.sente.interfaces.sch_send_BANG_.call(null,server_ch,websocket_QMARK_,taoensso.sente.pack.call(null,packer__$1,resp_clj));
} else {
return null;
}
});
;})(replied_QMARK__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
taoensso.sente.put_server_event_msg_GT_ch_recv_BANG_.call(null,ch_recv,cljs.core.merge.call(null,ev_msg_const,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"client-id","client-id",-464622140),client_id,new cljs.core.Keyword(null,"ring-req","ring-req",-747861961),ring_req,new cljs.core.Keyword(null,"event","event",301435442),clj,new cljs.core.Keyword(null,"uid","uid",-1447769400),user_id_fn__$1.call(null,ring_req,client_id),new cljs.core.Keyword(null,"?reply-fn","?reply-fn",-1479510592),(cljs.core.truth_(has_cb_QMARK_)?reply_fn:null)], null)));

if(cljs.core.truth_(has_cb_QMARK_)){
var temp__5720__auto__ = lp_timeout_ms;
if(cljs.core.truth_(temp__5720__auto__)){
var ms = temp__5720__auto__;
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__,ms,temp__5720__auto__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,reply_fn,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__,ms,temp__5720__auto__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,reply_fn,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (state_18127){
var state_val_18128 = (state_18127[(1)]);
if((state_val_18128 === (1))){
var inst_18122 = cljs.core.async.timeout.call(null,ms);
var state_18127__$1 = state_18127;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18127__$1,(2),inst_18122);
} else {
if((state_val_18128 === (2))){
var inst_18124 = (state_18127[(2)]);
var inst_18125 = reply_fn.call(null,new cljs.core.Keyword("chsk","timeout","chsk/timeout",-319776489));
var state_18127__$1 = (function (){var statearr_18129 = state_18127;
(statearr_18129[(7)] = inst_18124);

return statearr_18129;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18127__$1,inst_18125);
} else {
return null;
}
}
});})(c__8790__auto__,ms,temp__5720__auto__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,reply_fn,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
return ((function (switch__8695__auto__,c__8790__auto__,ms,temp__5720__auto__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,reply_fn,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18130 = [null,null,null,null,null,null,null,null];
(statearr_18130[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18130[(1)] = (1));

return statearr_18130;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18127){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18127);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18131){if((e18131 instanceof Object)){
var ex__8699__auto__ = e18131;
var statearr_18132_18421 = state_18127;
(statearr_18132_18421[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18127);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18131;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18422 = state_18127;
state_18127 = G__18422;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18127){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18127);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__,ms,temp__5720__auto__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,reply_fn,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var state__8792__auto__ = (function (){var statearr_18133 = f__8791__auto__.call(null);
(statearr_18133[(6)] = c__8790__auto__);

return statearr_18133;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__,ms,temp__5720__auto__,params,ppstr,client_id,vec__18119,clj,has_cb_QMARK_,reply_fn,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);

return c__8790__auto__;
} else {
return null;
}
} else {
return reply_fn.call(null,new cljs.core.Keyword("chsk","dummy-cb-200","chsk/dummy-cb-200",-1663130337));
}
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
], null));
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,new cljs.core.Keyword(null,"ajax-get-or-ws-handshake-fn","ajax-get-or-ws-handshake-fn",-1210409233),((function (packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (ring_req){
var sch_uuid = taoensso.encore.uuid_str.call(null,(6));
var params = cljs.core.get.call(null,ring_req,new cljs.core.Keyword(null,"params","params",710516235));
var client_id = cljs.core.get.call(null,params,new cljs.core.Keyword(null,"client-id","client-id",-464622140));
var csrf_token = csrf_token_fn.call(null,ring_req);
var uid = user_id_fn__$1.call(null,ring_req,client_id);
var receive_event_msg_BANG_ = ((function (sch_uuid,params,client_id,csrf_token,uid,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var taoensso$sente$self = null;
var taoensso$sente$self__1 = (function (event){
return taoensso$sente$self.call(null,event,null);
});
var taoensso$sente$self__2 = (function (event,_QMARK_reply_fn){
return taoensso.sente.put_server_event_msg_GT_ch_recv_BANG_.call(null,ch_recv,cljs.core.merge.call(null,ev_msg_const,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"client-id","client-id",-464622140),client_id,new cljs.core.Keyword(null,"ring-req","ring-req",-747861961),ring_req,new cljs.core.Keyword(null,"event","event",301435442),event,new cljs.core.Keyword(null,"?reply-fn","?reply-fn",-1479510592),_QMARK_reply_fn,new cljs.core.Keyword(null,"uid","uid",-1447769400),uid], null)));
});
taoensso$sente$self = function(event,_QMARK_reply_fn){
switch(arguments.length){
case 1:
return taoensso$sente$self__1.call(this,event);
case 2:
return taoensso$sente$self__2.call(this,event,_QMARK_reply_fn);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$self.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$self__1;
taoensso$sente$self.cljs$core$IFn$_invoke$arity$2 = taoensso$sente$self__2;
return taoensso$sente$self;
})()
;})(sch_uuid,params,client_id,csrf_token,uid,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
var send_handshake_BANG_ = ((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (server_ch,websocket_QMARK_){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,556,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["send-handshake!"], null);
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,-500061260);

var _QMARK_handshake_data = handshake_data_fn.call(null,ring_req);
var handshake_ev = (((_QMARK_handshake_data == null))?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","handshake","chsk/handshake",64910686),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [uid,csrf_token], null)], null):new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","handshake","chsk/handshake",64910686),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [uid,csrf_token,_QMARK_handshake_data], null)], null));
return taoensso.sente.interfaces.sch_send_BANG_.call(null,server_ch,websocket_QMARK_,taoensso.sente.pack.call(null,packer__$1,handshake_ev));
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
if(clojure.string.blank_QMARK_.call(null,client_id)){
var err_msg = "Client's Ring request doesn't have a client id. Does your server have the necessary keyword Ring middleware (`wrap-params` & `wrap-keyword-params`)?";
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"error","error",-978969032),"taoensso.sente",null,567,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (err_msg,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [[err_msg,": %s"].join(''),ring_req], null);
});})(err_msg,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,219851065);

throw cljs.core.ex_info.call(null,err_msg,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"ring-req","ring-req",-747861961),ring_req], null));
} else {
return taoensso.sente.interfaces.ring_req__GT_server_ch_resp.call(null,web_server_ch_adapter,ring_req,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"on-open","on-open",-1391088163),((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (server_ch,websocket_QMARK_){
if(cljs.core.truth_(websocket_QMARK_)){
var _ = taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,576,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["New WebSocket channel: %s (%s)",uid,sch_uuid], null);
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,2146541532);
var updated_conn = upd_conn_BANG_.call(null,new cljs.core.Keyword(null,"ws","ws",86841443),uid,client_id,server_ch);
var udt_open = new cljs.core.Keyword(null,"udt","udt",2011712751).cljs$core$IFn$_invoke$arity$1(updated_conn);
if(cljs.core.truth_(connect_uid_BANG_.call(null,new cljs.core.Keyword(null,"ws","ws",86841443),uid))){
receive_event_msg_BANG_.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","uidport-open","chsk/uidport-open",1685786954),uid], null));
} else {
}

send_handshake_BANG_.call(null,server_ch,websocket_QMARK_);

var temp__5720__auto__ = ws_kalive_ms;
if(cljs.core.truth_(temp__5720__auto__)){
var ms = temp__5720__auto__;
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (state_18169){
var state_val_18170 = (state_18169[(1)]);
if((state_val_18170 === (7))){
var inst_18165 = (state_18169[(2)]);
var state_18169__$1 = state_18169;
var statearr_18171_18423 = state_18169__$1;
(statearr_18171_18423[(2)] = inst_18165);

(statearr_18171_18423[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (1))){
var inst_18134 = udt_open;
var state_18169__$1 = (function (){var statearr_18172 = state_18169;
(statearr_18172[(7)] = inst_18134);

return statearr_18172;
})();
var statearr_18173_18424 = state_18169__$1;
(statearr_18173_18424[(2)] = null);

(statearr_18173_18424[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (4))){
var inst_18143 = (state_18169[(8)]);
var inst_18138 = (state_18169[(2)]);
var inst_18139 = cljs.core.deref.call(null,conns_);
var inst_18140 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18141 = [new cljs.core.Keyword(null,"ws","ws",86841443),uid,client_id];
var inst_18142 = (new cljs.core.PersistentVector(null,3,(5),inst_18140,inst_18141,null));
var inst_18143__$1 = cljs.core.get_in.call(null,inst_18139,inst_18142);
var state_18169__$1 = (function (){var statearr_18174 = state_18169;
(statearr_18174[(9)] = inst_18138);

(statearr_18174[(8)] = inst_18143__$1);

return statearr_18174;
})();
if(cljs.core.truth_(inst_18143__$1)){
var statearr_18175_18425 = state_18169__$1;
(statearr_18175_18425[(1)] = (5));

} else {
var statearr_18176_18426 = state_18169__$1;
(statearr_18176_18426[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (13))){
var inst_18149 = (state_18169[(10)]);
var inst_18158 = (state_18169[(2)]);
var inst_18134 = inst_18149;
var state_18169__$1 = (function (){var statearr_18177 = state_18169;
(statearr_18177[(7)] = inst_18134);

(statearr_18177[(11)] = inst_18158);

return statearr_18177;
})();
var statearr_18178_18427 = state_18169__$1;
(statearr_18178_18427[(2)] = null);

(statearr_18178_18427[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (6))){
var state_18169__$1 = state_18169;
var statearr_18179_18428 = state_18169__$1;
(statearr_18179_18428[(2)] = null);

(statearr_18179_18428[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (3))){
var inst_18167 = (state_18169[(2)]);
var state_18169__$1 = state_18169;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18169__$1,inst_18167);
} else {
if((state_val_18170 === (12))){
var state_18169__$1 = state_18169;
var statearr_18180_18429 = state_18169__$1;
(statearr_18180_18429[(2)] = null);

(statearr_18180_18429[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (2))){
var inst_18136 = cljs.core.async.timeout.call(null,ms);
var state_18169__$1 = state_18169;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18169__$1,(4),inst_18136);
} else {
if((state_val_18170 === (11))){
var inst_18154 = taoensso.sente.pack.call(null,packer__$1,new cljs.core.Keyword("chsk","ws-ping","chsk/ws-ping",191675304));
var inst_18155 = taoensso.sente.interfaces.sch_send_BANG_.call(null,server_ch,websocket_QMARK_,inst_18154);
var state_18169__$1 = state_18169;
var statearr_18181_18430 = state_18169__$1;
(statearr_18181_18430[(2)] = inst_18155);

(statearr_18181_18430[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (9))){
var state_18169__$1 = state_18169;
var statearr_18182_18431 = state_18169__$1;
(statearr_18182_18431[(2)] = null);

(statearr_18182_18431[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (5))){
var inst_18143 = (state_18169[(8)]);
var inst_18148 = cljs.core.nth.call(null,inst_18143,(0),null);
var inst_18149 = cljs.core.nth.call(null,inst_18143,(1),null);
var inst_18150 = taoensso.sente.interfaces.sch_open_QMARK_.call(null,server_ch);
var state_18169__$1 = (function (){var statearr_18183 = state_18169;
(statearr_18183[(12)] = inst_18148);

(statearr_18183[(10)] = inst_18149);

return statearr_18183;
})();
if(cljs.core.truth_(inst_18150)){
var statearr_18184_18432 = state_18169__$1;
(statearr_18184_18432[(1)] = (8));

} else {
var statearr_18185_18433 = state_18169__$1;
(statearr_18185_18433[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (10))){
var inst_18162 = (state_18169[(2)]);
var state_18169__$1 = state_18169;
var statearr_18186_18434 = state_18169__$1;
(statearr_18186_18434[(2)] = inst_18162);

(statearr_18186_18434[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18170 === (8))){
var inst_18134 = (state_18169[(7)]);
var inst_18149 = (state_18169[(10)]);
var inst_18152 = cljs.core._EQ_.call(null,inst_18149,inst_18134);
var state_18169__$1 = state_18169;
if(inst_18152){
var statearr_18187_18435 = state_18169__$1;
(statearr_18187_18435[(1)] = (11));

} else {
var statearr_18188_18436 = state_18169__$1;
(statearr_18188_18436[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
return ((function (switch__8695__auto__,c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18189 = [null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_18189[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18189[(1)] = (1));

return statearr_18189;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18169){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18169);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18190){if((e18190 instanceof Object)){
var ex__8699__auto__ = e18190;
var statearr_18191_18437 = state_18169;
(statearr_18191_18437[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18169);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18190;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18438 = state_18169;
state_18169 = G__18438;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18169){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18169);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var state__8792__auto__ = (function (){var statearr_18192 = f__8791__auto__.call(null);
(statearr_18192[(6)] = c__8790__auto__);

return statearr_18192;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);

return c__8790__auto__;
} else {
return null;
}
} else {
var _ = taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,605,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["New Ajax handshake/poll: %s (%s)",uid,sch_uuid], null);
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,1916769406);
var updated_conn = upd_conn_BANG_.call(null,new cljs.core.Keyword(null,"ajax","ajax",814345549),uid,client_id,server_ch);
var udt_open = new cljs.core.Keyword(null,"udt","udt",2011712751).cljs$core$IFn$_invoke$arity$1(updated_conn);
var handshake_QMARK_ = (function (){var or__4131__auto__ = new cljs.core.Keyword(null,"init?","init?",438181499).cljs$core$IFn$_invoke$arity$1(updated_conn);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Keyword(null,"handshake?","handshake?",-423743093).cljs$core$IFn$_invoke$arity$1(params);
}
})();
if(cljs.core.truth_(connect_uid_BANG_.call(null,new cljs.core.Keyword(null,"ajax","ajax",814345549),uid))){
receive_event_msg_BANG_.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","uidport-open","chsk/uidport-open",1685786954),uid], null));
} else {
}

if(cljs.core.truth_(handshake_QMARK_)){
return send_handshake_BANG_.call(null,server_ch,websocket_QMARK_);
} else {
var temp__5720__auto__ = lp_timeout_ms;
if(cljs.core.truth_(temp__5720__auto__)){
var ms = temp__5720__auto__;
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,handshake_QMARK_,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,handshake_QMARK_,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (state_18218){
var state_val_18219 = (state_18218[(1)]);
if((state_val_18219 === (1))){
var inst_18193 = cljs.core.async.timeout.call(null,ms);
var state_18218__$1 = state_18218;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18218__$1,(2),inst_18193);
} else {
if((state_val_18219 === (2))){
var inst_18200 = (state_18218[(7)]);
var inst_18195 = (state_18218[(2)]);
var inst_18196 = cljs.core.deref.call(null,conns_);
var inst_18197 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18198 = [new cljs.core.Keyword(null,"ajax","ajax",814345549),uid,client_id];
var inst_18199 = (new cljs.core.PersistentVector(null,3,(5),inst_18197,inst_18198,null));
var inst_18200__$1 = cljs.core.get_in.call(null,inst_18196,inst_18199);
var state_18218__$1 = (function (){var statearr_18220 = state_18218;
(statearr_18220[(7)] = inst_18200__$1);

(statearr_18220[(8)] = inst_18195);

return statearr_18220;
})();
if(cljs.core.truth_(inst_18200__$1)){
var statearr_18221_18439 = state_18218__$1;
(statearr_18221_18439[(1)] = (3));

} else {
var statearr_18222_18440 = state_18218__$1;
(statearr_18222_18440[(1)] = (4));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18219 === (3))){
var inst_18200 = (state_18218[(7)]);
var inst_18205 = cljs.core.nth.call(null,inst_18200,(0),null);
var inst_18206 = cljs.core.nth.call(null,inst_18200,(1),null);
var inst_18207 = cljs.core._EQ_.call(null,inst_18206,udt_open);
var state_18218__$1 = (function (){var statearr_18223 = state_18218;
(statearr_18223[(9)] = inst_18205);

return statearr_18223;
})();
if(inst_18207){
var statearr_18224_18441 = state_18218__$1;
(statearr_18224_18441[(1)] = (6));

} else {
var statearr_18225_18442 = state_18218__$1;
(statearr_18225_18442[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18219 === (4))){
var state_18218__$1 = state_18218;
var statearr_18226_18443 = state_18218__$1;
(statearr_18226_18443[(2)] = null);

(statearr_18226_18443[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18219 === (5))){
var inst_18216 = (state_18218[(2)]);
var state_18218__$1 = state_18218;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18218__$1,inst_18216);
} else {
if((state_val_18219 === (6))){
var inst_18209 = taoensso.sente.pack.call(null,packer__$1,new cljs.core.Keyword("chsk","timeout","chsk/timeout",-319776489));
var inst_18210 = taoensso.sente.interfaces.sch_send_BANG_.call(null,server_ch,websocket_QMARK_,inst_18209);
var state_18218__$1 = state_18218;
var statearr_18227_18444 = state_18218__$1;
(statearr_18227_18444[(2)] = inst_18210);

(statearr_18227_18444[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18219 === (7))){
var state_18218__$1 = state_18218;
var statearr_18228_18445 = state_18218__$1;
(statearr_18228_18445[(2)] = null);

(statearr_18228_18445[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18219 === (8))){
var inst_18213 = (state_18218[(2)]);
var state_18218__$1 = state_18218;
var statearr_18229_18446 = state_18218__$1;
(statearr_18229_18446[(2)] = inst_18213);

(statearr_18229_18446[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
});})(c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,handshake_QMARK_,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
return ((function (switch__8695__auto__,c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,handshake_QMARK_,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18230 = [null,null,null,null,null,null,null,null,null,null];
(statearr_18230[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18230[(1)] = (1));

return statearr_18230;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18218){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18218);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18231){if((e18231 instanceof Object)){
var ex__8699__auto__ = e18231;
var statearr_18232_18447 = state_18218;
(statearr_18232_18447[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18218);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18231;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18448 = state_18218;
state_18218 = G__18448;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18218){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18218);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,handshake_QMARK_,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var state__8792__auto__ = (function (){var statearr_18233 = f__8791__auto__.call(null);
(statearr_18233[(6)] = c__8790__auto__);

return statearr_18233;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__,ms,temp__5720__auto__,_,updated_conn,udt_open,handshake_QMARK_,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);

return c__8790__auto__;
} else {
return null;
}
}
}
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,new cljs.core.Keyword(null,"on-msg","on-msg",-2021925279),((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (server_ch,websocket_QMARK_,req_ppstr){
if(cljs.core.truth_(websocket_QMARK_)){
} else {
throw (new Error("Assert failed: websocket?"));
}

upd_conn_BANG_.call(null,new cljs.core.Keyword(null,"ws","ws",86841443),uid,client_id);

var vec__18234 = taoensso.sente.unpack.call(null,packer__$1,req_ppstr);
var clj = cljs.core.nth.call(null,vec__18234,(0),null);
var _QMARK_cb_uuid = cljs.core.nth.call(null,vec__18234,(1),null);
return receive_event_msg_BANG_.call(null,clj,(cljs.core.truth_(_QMARK_cb_uuid)?((function (vec__18234,clj,_QMARK_cb_uuid,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function taoensso$sente$reply_fn(resp_clj){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,635,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (vec__18234,clj,_QMARK_cb_uuid,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk send (ws reply): %s",resp_clj], null);
});})(vec__18234,clj,_QMARK_cb_uuid,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,-958673065);

return taoensso.sente.interfaces.sch_send_BANG_.call(null,server_ch,websocket_QMARK_,taoensso.sente.pack.call(null,packer__$1,resp_clj,_QMARK_cb_uuid));
});})(vec__18234,clj,_QMARK_cb_uuid,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
:null));
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,new cljs.core.Keyword(null,"on-close","on-close",-761178394),((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (server_ch,websocket_QMARK_,_status){
var conn_type = (cljs.core.truth_(websocket_QMARK_)?new cljs.core.Keyword(null,"ws","ws",86841443):new cljs.core.Keyword(null,"ajax","ajax",814345549));
var _ = taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,644,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (conn_type,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, ["%s channel closed: %s (%s)",(cljs.core.truth_(websocket_QMARK_)?"WebSocket":"Ajax"),uid,sch_uuid], null);
});})(conn_type,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,1507924003);
var updated_conn = upd_conn_BANG_.call(null,conn_type,uid,client_id,null);
var udt_close = new cljs.core.Keyword(null,"udt","udt",2011712751).cljs$core$IFn$_invoke$arity$1(updated_conn);
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (state_18288){
var state_val_18289 = (state_18288[(1)]);
if((state_val_18289 === (7))){
var state_18288__$1 = state_18288;
var statearr_18290_18449 = state_18288__$1;
(statearr_18290_18449[(2)] = null);

(statearr_18290_18449[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (1))){
var inst_18237 = cljs.core.async.timeout.call(null,(5000));
var state_18288__$1 = state_18288;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18288__$1,(2),inst_18237);
} else {
if((state_val_18289 === (4))){
var state_18288__$1 = state_18288;
var statearr_18291_18450 = state_18288__$1;
(statearr_18291_18450[(2)] = null);

(statearr_18291_18450[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (13))){
var state_18288__$1 = state_18288;
var statearr_18292_18451 = state_18288__$1;
(statearr_18292_18451[(2)] = null);

(statearr_18292_18451[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (6))){
var inst_18249 = (state_18288[(7)]);
var inst_18248 = (state_18288[(8)]);
var inst_18247 = (state_18288[(9)]);
var inst_18265 = (state_18288[(10)]);
var inst_18260 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18261 = [conn_type,uid,client_id];
var inst_18262 = (new cljs.core.PersistentVector(null,3,(5),inst_18260,inst_18261,null));
var inst_18264 = (function (){var vec__18240 = inst_18247;
var __QMARK_sch = inst_18248;
var udt_t1 = inst_18249;
return ((function (vec__18240,__QMARK_sch,udt_t1,inst_18249,inst_18248,inst_18247,inst_18265,inst_18260,inst_18261,inst_18262,state_val_18289,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (p__18263){
var vec__18293 = p__18263;
var _sch = cljs.core.nth.call(null,vec__18293,(0),null);
var udt_t1__$1 = cljs.core.nth.call(null,vec__18293,(1),null);
if(cljs.core._EQ_.call(null,udt_t1__$1,udt_close)){
return taoensso.encore.swapped.call(null,new cljs.core.Keyword("swap","dissoc","swap/dissoc",-605373782),true);
} else {
return taoensso.encore.swapped.call(null,udt_t1__$1,false);
}
});
;})(vec__18240,__QMARK_sch,udt_t1,inst_18249,inst_18248,inst_18247,inst_18265,inst_18260,inst_18261,inst_18262,state_val_18289,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var inst_18265__$1 = taoensso.encore.swap_in_BANG_.call(null,conns_,inst_18262,inst_18264);
var state_18288__$1 = (function (){var statearr_18296 = state_18288;
(statearr_18296[(10)] = inst_18265__$1);

return statearr_18296;
})();
if(cljs.core.truth_(inst_18265__$1)){
var statearr_18297_18452 = state_18288__$1;
(statearr_18297_18452[(1)] = (9));

} else {
var statearr_18298_18453 = state_18288__$1;
(statearr_18298_18453[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (3))){
var inst_18249 = (state_18288[(7)]);
var inst_18248 = (state_18288[(8)]);
var inst_18247 = (state_18288[(9)]);
var inst_18252 = (function (){var vec__18240 = inst_18247;
var __QMARK_sch = inst_18248;
var udt_t1 = inst_18249;
return ((function (vec__18240,__QMARK_sch,udt_t1,inst_18249,inst_18248,inst_18247,state_val_18289,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, ["close-timeout: %s %s %s %s",conn_type,uid,sch_uuid,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core._EQ_.call(null,udt_t1,udt_close),udt_t1,udt_close], null)], null);
});
;})(vec__18240,__QMARK_sch,udt_t1,inst_18249,inst_18248,inst_18247,state_val_18289,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var inst_18253 = (new cljs.core.Delay(inst_18252,null));
var inst_18254 = taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"debug","debug",-1608172596),"taoensso.sente",null,658,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),inst_18253,null,-456335415);
var state_18288__$1 = state_18288;
var statearr_18299_18454 = state_18288__$1;
(statearr_18299_18454[(2)] = inst_18254);

(statearr_18299_18454[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (12))){
var inst_18274 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18275 = [new cljs.core.Keyword("chsk","uidport-close","chsk/uidport-close",901058678),uid];
var inst_18276 = (new cljs.core.PersistentVector(null,2,(5),inst_18274,inst_18275,null));
var inst_18277 = receive_event_msg_BANG_.call(null,inst_18276);
var state_18288__$1 = state_18288;
var statearr_18300_18455 = state_18288__$1;
(statearr_18300_18455[(2)] = inst_18277);

(statearr_18300_18455[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (2))){
var inst_18247 = (state_18288[(9)]);
var inst_18239 = (state_18288[(2)]);
var inst_18243 = cljs.core.deref.call(null,conns_);
var inst_18244 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18245 = [conn_type,uid,client_id];
var inst_18246 = (new cljs.core.PersistentVector(null,3,(5),inst_18244,inst_18245,null));
var inst_18247__$1 = cljs.core.get_in.call(null,inst_18243,inst_18246);
var inst_18248 = cljs.core.nth.call(null,inst_18247__$1,(0),null);
var inst_18249 = cljs.core.nth.call(null,inst_18247__$1,(1),null);
var inst_18250 = cljs.core.deref.call(null,taoensso.sente.debug_mode_QMARK__);
var state_18288__$1 = (function (){var statearr_18301 = state_18288;
(statearr_18301[(7)] = inst_18249);

(statearr_18301[(8)] = inst_18248);

(statearr_18301[(9)] = inst_18247__$1);

(statearr_18301[(11)] = inst_18239);

return statearr_18301;
})();
if(cljs.core.truth_(inst_18250)){
var statearr_18302_18456 = state_18288__$1;
(statearr_18302_18456[(1)] = (3));

} else {
var statearr_18303_18457 = state_18288__$1;
(statearr_18303_18457[(1)] = (4));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (11))){
var inst_18283 = (state_18288[(2)]);
var state_18288__$1 = state_18288;
var statearr_18304_18458 = state_18288__$1;
(statearr_18304_18458[(2)] = inst_18283);

(statearr_18304_18458[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (9))){
var inst_18249 = (state_18288[(7)]);
var inst_18248 = (state_18288[(8)]);
var inst_18247 = (state_18288[(9)]);
var inst_18265 = (state_18288[(10)]);
var inst_18267 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18268 = [conn_type,uid];
var inst_18269 = (new cljs.core.PersistentVector(null,2,(5),inst_18267,inst_18268,null));
var inst_18270 = (function (){var vec__18240 = inst_18247;
var __QMARK_sch = inst_18248;
var udt_t1 = inst_18249;
var disconnect_QMARK_ = inst_18265;
return ((function (vec__18240,__QMARK_sch,udt_t1,disconnect_QMARK_,inst_18249,inst_18248,inst_18247,inst_18265,inst_18267,inst_18268,inst_18269,state_val_18289,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (_QMARK_m){
if(cljs.core.empty_QMARK_.call(null,_QMARK_m)){
return new cljs.core.Keyword("swap","dissoc","swap/dissoc",-605373782);
} else {
return _QMARK_m;
}
});
;})(vec__18240,__QMARK_sch,udt_t1,disconnect_QMARK_,inst_18249,inst_18248,inst_18247,inst_18265,inst_18267,inst_18268,inst_18269,state_val_18289,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var inst_18271 = taoensso.encore.swap_in_BANG_.call(null,conns_,inst_18269,inst_18270);
var inst_18272 = upd_connected_uid_BANG_.call(null,uid);
var state_18288__$1 = (function (){var statearr_18305 = state_18288;
(statearr_18305[(12)] = inst_18271);

return statearr_18305;
})();
if(cljs.core.truth_(inst_18272)){
var statearr_18306_18459 = state_18288__$1;
(statearr_18306_18459[(1)] = (12));

} else {
var statearr_18307_18460 = state_18288__$1;
(statearr_18307_18460[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (5))){
var inst_18249 = (state_18288[(7)]);
var inst_18257 = (state_18288[(2)]);
var inst_18258 = cljs.core._EQ_.call(null,inst_18249,udt_close);
var state_18288__$1 = (function (){var statearr_18308 = state_18288;
(statearr_18308[(13)] = inst_18257);

return statearr_18308;
})();
if(inst_18258){
var statearr_18309_18461 = state_18288__$1;
(statearr_18309_18461[(1)] = (6));

} else {
var statearr_18310_18462 = state_18288__$1;
(statearr_18310_18462[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (14))){
var inst_18280 = (state_18288[(2)]);
var state_18288__$1 = state_18288;
var statearr_18311_18463 = state_18288__$1;
(statearr_18311_18463[(2)] = inst_18280);

(statearr_18311_18463[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (10))){
var state_18288__$1 = state_18288;
var statearr_18312_18464 = state_18288__$1;
(statearr_18312_18464[(2)] = null);

(statearr_18312_18464[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18289 === (8))){
var inst_18286 = (state_18288[(2)]);
var state_18288__$1 = state_18288;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18288__$1,inst_18286);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
;
return ((function (switch__8695__auto__,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18313 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_18313[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18313[(1)] = (1));

return statearr_18313;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18288){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18288);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18314){if((e18314 instanceof Object)){
var ex__8699__auto__ = e18314;
var statearr_18315_18465 = state_18288;
(statearr_18315_18465[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18288);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18314;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18466 = state_18288;
state_18288 = G__18466;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18288){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18288);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
})();
var state__8792__auto__ = (function (){var statearr_18316 = f__8791__auto__.call(null);
(statearr_18316[(6)] = c__8790__auto__);

return statearr_18316;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__,conn_type,_,updated_conn,udt_close,sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
);

return c__8790__auto__;
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,new cljs.core.Keyword(null,"on-error","on-error",1728533530),((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (server_ch,websocket_QMARK_,error){
return taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"error","error",-978969032),"taoensso.sente",null,680,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n){
return (function (){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, ["ring-req->server-ch-resp error: %s (%s)",error,uid,sch_uuid], null);
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
,null)),null,-976004875);
});})(sch_uuid,params,client_id,csrf_token,uid,receive_event_msg_BANG_,send_handshake_BANG_,packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
], null));
}
});})(packer__$1,ch_recv,user_id_fn__$1,conns_,send_buffers_,connected_uids_,upd_conn_BANG_,connect_uid_BANG_,upd_connected_uid_BANG_,send_fn,ev_msg_const,vec__18015,map__18018,map__18018__$1,ws_kalive_ms,send_buf_ms_ws,lp_timeout_ms,csrf_token_fn,packer,send_buf_ms_ajax,handshake_data_fn,user_id_fn,recv_buf_or_n))
], null);
});

taoensso.sente.make_channel_socket_server_BANG_.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
taoensso.sente.make_channel_socket_server_BANG_.cljs$lang$applyTo = (function (seq18012){
var G__18013 = cljs.core.first.call(null,seq18012);
var seq18012__$1 = cljs.core.next.call(null,seq18012);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__18013,seq18012__$1);
});

/**
 * Actually pushes buffered events (as packed-str) to all uid's WebSocket conns.
 */
taoensso.sente.send_buffered_server_evs_GT_ws_clients_BANG_ = (function taoensso$sente$send_buffered_server_evs_GT_ws_clients_BANG_(conns_,uid,buffered_evs_pstr,upd_conn_BANG_){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,686,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay((function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["send-buffered-server-evs>ws-clients!: %s",buffered_evs_pstr], null);
}),null)),null,-1764956843);

var seq__18467 = cljs.core.seq.call(null,cljs.core.get_in.call(null,cljs.core.deref.call(null,conns_),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ws","ws",86841443),uid], null)));
var chunk__18468 = null;
var count__18469 = (0);
var i__18470 = (0);
while(true){
if((i__18470 < count__18469)){
var vec__18483 = cljs.core._nth.call(null,chunk__18468,i__18470);
var client_id = cljs.core.nth.call(null,vec__18483,(0),null);
var vec__18486 = cljs.core.nth.call(null,vec__18483,(1),null);
var _QMARK_sch = cljs.core.nth.call(null,vec__18486,(0),null);
var _udt = cljs.core.nth.call(null,vec__18486,(1),null);
var temp__5720__auto___18495 = _QMARK_sch;
if(cljs.core.truth_(temp__5720__auto___18495)){
var sch_18496 = temp__5720__auto___18495;
upd_conn_BANG_.call(null,new cljs.core.Keyword(null,"ws","ws",86841443),uid,client_id);

taoensso.sente.interfaces.sch_send_BANG_.call(null,sch_18496,new cljs.core.Keyword(null,"websocket","websocket",-1714963101),buffered_evs_pstr);
} else {
}


var G__18497 = seq__18467;
var G__18498 = chunk__18468;
var G__18499 = count__18469;
var G__18500 = (i__18470 + (1));
seq__18467 = G__18497;
chunk__18468 = G__18498;
count__18469 = G__18499;
i__18470 = G__18500;
continue;
} else {
var temp__5720__auto__ = cljs.core.seq.call(null,seq__18467);
if(temp__5720__auto__){
var seq__18467__$1 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__18467__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__18467__$1);
var G__18501 = cljs.core.chunk_rest.call(null,seq__18467__$1);
var G__18502 = c__4550__auto__;
var G__18503 = cljs.core.count.call(null,c__4550__auto__);
var G__18504 = (0);
seq__18467 = G__18501;
chunk__18468 = G__18502;
count__18469 = G__18503;
i__18470 = G__18504;
continue;
} else {
var vec__18489 = cljs.core.first.call(null,seq__18467__$1);
var client_id = cljs.core.nth.call(null,vec__18489,(0),null);
var vec__18492 = cljs.core.nth.call(null,vec__18489,(1),null);
var _QMARK_sch = cljs.core.nth.call(null,vec__18492,(0),null);
var _udt = cljs.core.nth.call(null,vec__18492,(1),null);
var temp__5720__auto___18505__$1 = _QMARK_sch;
if(cljs.core.truth_(temp__5720__auto___18505__$1)){
var sch_18506 = temp__5720__auto___18505__$1;
upd_conn_BANG_.call(null,new cljs.core.Keyword(null,"ws","ws",86841443),uid,client_id);

taoensso.sente.interfaces.sch_send_BANG_.call(null,sch_18506,new cljs.core.Keyword(null,"websocket","websocket",-1714963101),buffered_evs_pstr);
} else {
}


var G__18507 = cljs.core.next.call(null,seq__18467__$1);
var G__18508 = null;
var G__18509 = (0);
var G__18510 = (0);
seq__18467 = G__18507;
chunk__18468 = G__18508;
count__18469 = G__18509;
i__18470 = G__18510;
continue;
}
} else {
return null;
}
}
break;
}
});
/**
 * Actually pushes buffered events (as packed-str) to all uid's Ajax conns.
 *   Allows some time for possible Ajax poller reconnects.
 */
taoensso.sente.send_buffered_server_evs_GT_ajax_clients_BANG_ = (function taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG_(conns_,uid,buffered_evs_pstr){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,696,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay((function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["send-buffered-server-evs>ajax-clients!: %s",buffered_evs_pstr], null);
}),null)),null,1738449579);

var ms_backoffs = new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [(90),(180),(360),(720),(1440)], null);
var client_ids_unsatisfied = cljs.core.keys.call(null,cljs.core.get_in.call(null,cljs.core.deref.call(null,conns_),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ajax","ajax",814345549),uid], null)));
if(cljs.core.empty_QMARK_.call(null,client_ids_unsatisfied)){
return null;
} else {
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__,ms_backoffs,client_ids_unsatisfied){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__,ms_backoffs,client_ids_unsatisfied){
return (function (state_18557){
var state_val_18558 = (state_18557[(1)]);
if((state_val_18558 === (7))){
var inst_18519 = (state_18557[(7)]);
var inst_18513 = (state_18557[(8)]);
var inst_18512 = (state_18557[(9)]);
var inst_18529 = (function (){var n = inst_18512;
var client_ids_satisfied = inst_18513;
var _QMARK_pulled = inst_18519;
return ((function (n,client_ids_satisfied,_QMARK_pulled,inst_18519,inst_18513,inst_18512,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied){
return (function (s,client_id,p__18528){
var vec__18559 = p__18528;
var _QMARK_sch = cljs.core.nth.call(null,vec__18559,(0),null);
var _udt = cljs.core.nth.call(null,vec__18559,(1),null);
var sent_QMARK_ = (function (){var temp__5720__auto__ = _QMARK_sch;
if(cljs.core.truth_(temp__5720__auto__)){
var sch = temp__5720__auto__;
return taoensso.sente.interfaces.sch_send_BANG_.call(null,_QMARK_sch,cljs.core.not.call(null,new cljs.core.Keyword(null,"websocket","websocket",-1714963101)),buffered_evs_pstr);
} else {
return null;
}
})();
if(cljs.core.truth_(sent_QMARK_)){
return cljs.core.conj.call(null,s,client_id);
} else {
return s;
}
});
;})(n,client_ids_satisfied,_QMARK_pulled,inst_18519,inst_18513,inst_18512,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied))
})();
var inst_18530 = cljs.core.PersistentHashSet.EMPTY;
var inst_18531 = cljs.core.reduce_kv.call(null,inst_18529,inst_18530,inst_18519);
var state_18557__$1 = state_18557;
var statearr_18562_18592 = state_18557__$1;
(statearr_18562_18592[(2)] = inst_18531);

(statearr_18562_18592[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (1))){
var inst_18511 = cljs.core.PersistentHashSet.EMPTY;
var inst_18512 = (0);
var inst_18513 = inst_18511;
var state_18557__$1 = (function (){var statearr_18563 = state_18557;
(statearr_18563[(8)] = inst_18513);

(statearr_18563[(9)] = inst_18512);

return statearr_18563;
})();
var statearr_18564_18593 = state_18557__$1;
(statearr_18564_18593[(2)] = null);

(statearr_18564_18593[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (4))){
var state_18557__$1 = state_18557;
var statearr_18565_18594 = state_18557__$1;
(statearr_18565_18594[(2)] = true);

(statearr_18565_18594[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (15))){
var inst_18550 = (state_18557[(2)]);
var state_18557__$1 = state_18557;
var statearr_18566_18595 = state_18557__$1;
(statearr_18566_18595[(2)] = inst_18550);

(statearr_18566_18595[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (13))){
var inst_18536 = (state_18557[(10)]);
var inst_18541 = cljs.core.rand_int.call(null,inst_18536);
var inst_18542 = (inst_18536 + inst_18541);
var inst_18543 = cljs.core.async.timeout.call(null,inst_18542);
var state_18557__$1 = state_18557;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18557__$1,(16),inst_18543);
} else {
if((state_val_18558 === (6))){
var inst_18519 = (state_18557[(7)]);
var inst_18526 = (state_18557[(2)]);
var state_18557__$1 = (function (){var statearr_18567 = state_18557;
(statearr_18567[(11)] = inst_18526);

return statearr_18567;
})();
if(cljs.core.truth_(inst_18519)){
var statearr_18568_18596 = state_18557__$1;
(statearr_18568_18596[(1)] = (7));

} else {
var statearr_18569_18597 = state_18557__$1;
(statearr_18569_18597[(1)] = (8));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (3))){
var inst_18555 = (state_18557[(2)]);
var state_18557__$1 = state_18557;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18557__$1,inst_18555);
} else {
if((state_val_18558 === (12))){
var inst_18553 = (state_18557[(2)]);
var state_18557__$1 = state_18557;
var statearr_18570_18598 = state_18557__$1;
(statearr_18570_18598[(2)] = inst_18553);

(statearr_18570_18598[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (2))){
var inst_18519 = (state_18557[(7)]);
var inst_18513 = (state_18557[(8)]);
var inst_18512 = (state_18557[(9)]);
var inst_18515 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18516 = [new cljs.core.Keyword(null,"ajax","ajax",814345549),uid];
var inst_18517 = (new cljs.core.PersistentVector(null,2,(5),inst_18515,inst_18516,null));
var inst_18518 = (function (){var n = inst_18512;
var client_ids_satisfied = inst_18513;
return ((function (n,client_ids_satisfied,inst_18519,inst_18513,inst_18512,inst_18515,inst_18516,inst_18517,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied){
return (function (m){
var ks_to_pull = cljs.core.remove.call(null,client_ids_satisfied,cljs.core.keys.call(null,m));
if(cljs.core.empty_QMARK_.call(null,ks_to_pull)){
return taoensso.encore.swapped.call(null,m,null);
} else {
return taoensso.encore.swapped.call(null,cljs.core.reduce.call(null,((function (ks_to_pull,n,client_ids_satisfied,inst_18519,inst_18513,inst_18512,inst_18515,inst_18516,inst_18517,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied){
return (function (m__$1,k){
var vec__18571 = cljs.core.get.call(null,m__$1,k);
var _QMARK_sch = cljs.core.nth.call(null,vec__18571,(0),null);
var udt = cljs.core.nth.call(null,vec__18571,(1),null);
return cljs.core.assoc.call(null,m__$1,k,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [null,udt], null));
});})(ks_to_pull,n,client_ids_satisfied,inst_18519,inst_18513,inst_18512,inst_18515,inst_18516,inst_18517,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied))
,m,ks_to_pull),cljs.core.select_keys.call(null,m,ks_to_pull));
}
});
;})(n,client_ids_satisfied,inst_18519,inst_18513,inst_18512,inst_18515,inst_18516,inst_18517,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied))
})();
var inst_18519__$1 = taoensso.encore.swap_in_BANG_.call(null,conns_,inst_18517,inst_18518);
var inst_18520 = (function (){var n = inst_18512;
var client_ids_satisfied = inst_18513;
var _QMARK_pulled = inst_18519__$1;
return ((function (n,client_ids_satisfied,_QMARK_pulled,inst_18519,inst_18513,inst_18512,inst_18515,inst_18516,inst_18517,inst_18518,inst_18519__$1,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied){
return (function (x){
var or__4131__auto__ = (x == null);
if(or__4131__auto__){
return or__4131__auto__;
} else {
return taoensso.truss.impl.non_throwing.call(null,cljs.core.map_QMARK_).call(null,x);
}
});
;})(n,client_ids_satisfied,_QMARK_pulled,inst_18519,inst_18513,inst_18512,inst_18515,inst_18516,inst_18517,inst_18518,inst_18519__$1,state_val_18558,c__8790__auto__,ms_backoffs,client_ids_unsatisfied))
})();
var inst_18521 = inst_18520.call(null,inst_18519__$1);
var state_18557__$1 = (function (){var statearr_18574 = state_18557;
(statearr_18574[(7)] = inst_18519__$1);

return statearr_18574;
})();
if(cljs.core.truth_(inst_18521)){
var statearr_18575_18599 = state_18557__$1;
(statearr_18575_18599[(1)] = (4));

} else {
var statearr_18576_18600 = state_18557__$1;
(statearr_18576_18600[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (11))){
var state_18557__$1 = state_18557;
var statearr_18577_18601 = state_18557__$1;
(statearr_18577_18601[(2)] = null);

(statearr_18577_18601[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (9))){
var inst_18536 = (state_18557[(10)]);
var inst_18513 = (state_18557[(8)]);
var inst_18512 = (state_18557[(9)]);
var inst_18534 = (state_18557[(2)]);
var inst_18535 = cljs.core.into.call(null,inst_18513,inst_18534);
var inst_18536__$1 = cljs.core.get.call(null,ms_backoffs,inst_18512);
var state_18557__$1 = (function (){var statearr_18578 = state_18557;
(statearr_18578[(10)] = inst_18536__$1);

(statearr_18578[(12)] = inst_18535);

return statearr_18578;
})();
if(cljs.core.truth_(inst_18536__$1)){
var statearr_18579_18602 = state_18557__$1;
(statearr_18579_18602[(1)] = (10));

} else {
var statearr_18580_18603 = state_18557__$1;
(statearr_18580_18603[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (5))){
var inst_18519 = (state_18557[(7)]);
var inst_18524 = taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",723,"([:or nil? map?] ?pulled)",inst_18519,null,null);
var state_18557__$1 = state_18557;
var statearr_18581_18604 = state_18557__$1;
(statearr_18581_18604[(2)] = inst_18524);

(statearr_18581_18604[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (14))){
var state_18557__$1 = state_18557;
var statearr_18582_18605 = state_18557__$1;
(statearr_18582_18605[(2)] = null);

(statearr_18582_18605[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (16))){
var inst_18535 = (state_18557[(12)]);
var inst_18512 = (state_18557[(9)]);
var inst_18545 = (state_18557[(2)]);
var inst_18546 = (inst_18512 + (1));
var inst_18512__$1 = inst_18546;
var inst_18513 = inst_18535;
var state_18557__$1 = (function (){var statearr_18583 = state_18557;
(statearr_18583[(13)] = inst_18545);

(statearr_18583[(8)] = inst_18513);

(statearr_18583[(9)] = inst_18512__$1);

return statearr_18583;
})();
var statearr_18584_18606 = state_18557__$1;
(statearr_18584_18606[(2)] = null);

(statearr_18584_18606[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (10))){
var inst_18535 = (state_18557[(12)]);
var inst_18538 = cljs.core.complement.call(null,inst_18535);
var inst_18539 = taoensso.encore.rsome.call(null,inst_18538,client_ids_unsatisfied);
var state_18557__$1 = state_18557;
if(cljs.core.truth_(inst_18539)){
var statearr_18585_18607 = state_18557__$1;
(statearr_18585_18607[(1)] = (13));

} else {
var statearr_18586_18608 = state_18557__$1;
(statearr_18586_18608[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18558 === (8))){
var state_18557__$1 = state_18557;
var statearr_18587_18609 = state_18557__$1;
(statearr_18587_18609[(2)] = null);

(statearr_18587_18609[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__8790__auto__,ms_backoffs,client_ids_unsatisfied))
;
return ((function (switch__8695__auto__,c__8790__auto__,ms_backoffs,client_ids_unsatisfied){
return (function() {
var taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto__ = null;
var taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto____0 = (function (){
var statearr_18588 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_18588[(0)] = taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto__);

(statearr_18588[(1)] = (1));

return statearr_18588;
});
var taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto____1 = (function (state_18557){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18557);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18589){if((e18589 instanceof Object)){
var ex__8699__auto__ = e18589;
var statearr_18590_18610 = state_18557;
(statearr_18590_18610[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18557);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18589;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18611 = state_18557;
state_18557 = G__18611;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto__ = function(state_18557){
switch(arguments.length){
case 0:
return taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto____1.call(this,state_18557);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto____0;
taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto____1;
return taoensso$sente$send_buffered_server_evs_GT_ajax_clients_BANG__$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__,ms_backoffs,client_ids_unsatisfied))
})();
var state__8792__auto__ = (function (){var statearr_18591 = f__8791__auto__.call(null);
(statearr_18591[(6)] = c__8790__auto__);

return statearr_18591;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__,ms_backoffs,client_ids_unsatisfied))
);

return c__8790__auto__;
}
});
/**
 * Alias of `taoensso.encore/ajax-lite`
 */
taoensso.sente.ajax_lite = taoensso.encore.ajax_lite;

/**
 * @interface
 */
taoensso.sente.IChSocket = function(){};

taoensso.sente._chsk_connect_BANG_ = (function taoensso$sente$_chsk_connect_BANG_(chsk){
if((((!((chsk == null)))) && ((!((chsk.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1 == null)))))){
return chsk.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1(chsk);
} else {
var x__4433__auto__ = (((chsk == null))?null:chsk);
var m__4434__auto__ = (taoensso.sente._chsk_connect_BANG_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,chsk);
} else {
var m__4431__auto__ = (taoensso.sente._chsk_connect_BANG_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,chsk);
} else {
throw cljs.core.missing_protocol.call(null,"IChSocket.-chsk-connect!",chsk);
}
}
}
});

taoensso.sente._chsk_disconnect_BANG_ = (function taoensso$sente$_chsk_disconnect_BANG_(chsk,reason){
if((((!((chsk == null)))) && ((!((chsk.taoensso$sente$IChSocket$_chsk_disconnect_BANG_$arity$2 == null)))))){
return chsk.taoensso$sente$IChSocket$_chsk_disconnect_BANG_$arity$2(chsk,reason);
} else {
var x__4433__auto__ = (((chsk == null))?null:chsk);
var m__4434__auto__ = (taoensso.sente._chsk_disconnect_BANG_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,chsk,reason);
} else {
var m__4431__auto__ = (taoensso.sente._chsk_disconnect_BANG_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,chsk,reason);
} else {
throw cljs.core.missing_protocol.call(null,"IChSocket.-chsk-disconnect!",chsk);
}
}
}
});

taoensso.sente._chsk_reconnect_BANG_ = (function taoensso$sente$_chsk_reconnect_BANG_(chsk){
if((((!((chsk == null)))) && ((!((chsk.taoensso$sente$IChSocket$_chsk_reconnect_BANG_$arity$1 == null)))))){
return chsk.taoensso$sente$IChSocket$_chsk_reconnect_BANG_$arity$1(chsk);
} else {
var x__4433__auto__ = (((chsk == null))?null:chsk);
var m__4434__auto__ = (taoensso.sente._chsk_reconnect_BANG_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,chsk);
} else {
var m__4431__auto__ = (taoensso.sente._chsk_reconnect_BANG_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,chsk);
} else {
throw cljs.core.missing_protocol.call(null,"IChSocket.-chsk-reconnect!",chsk);
}
}
}
});

taoensso.sente._chsk_send_BANG_ = (function taoensso$sente$_chsk_send_BANG_(chsk,ev,opts){
if((((!((chsk == null)))) && ((!((chsk.taoensso$sente$IChSocket$_chsk_send_BANG_$arity$3 == null)))))){
return chsk.taoensso$sente$IChSocket$_chsk_send_BANG_$arity$3(chsk,ev,opts);
} else {
var x__4433__auto__ = (((chsk == null))?null:chsk);
var m__4434__auto__ = (taoensso.sente._chsk_send_BANG_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,chsk,ev,opts);
} else {
var m__4431__auto__ = (taoensso.sente._chsk_send_BANG_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,chsk,ev,opts);
} else {
throw cljs.core.missing_protocol.call(null,"IChSocket.-chsk-send!",chsk);
}
}
}
});

taoensso.sente.chsk_connect_BANG_ = (function taoensso$sente$chsk_connect_BANG_(chsk){
return taoensso.sente._chsk_connect_BANG_.call(null,chsk);
});

taoensso.sente.chsk_disconnect_BANG_ = (function taoensso$sente$chsk_disconnect_BANG_(chsk){
return taoensso.sente._chsk_disconnect_BANG_.call(null,chsk,new cljs.core.Keyword(null,"requested-disconnect","requested-disconnect",1037120641));
});

/**
 * Useful for reauthenticating after login/logout, etc.
 */
taoensso.sente.chsk_reconnect_BANG_ = (function taoensso$sente$chsk_reconnect_BANG_(chsk){
return taoensso.sente._chsk_reconnect_BANG_.call(null,chsk);
});

/**
 * Deprecated
 */
taoensso.sente.chsk_destroy_BANG_ = taoensso.sente.chsk_disconnect_BANG_;
/**
 * Sends `[ev-id ev-?data :as event]`, returns true on apparent success.
 */
taoensso.sente.chsk_send_BANG_ = (function taoensso$sente$chsk_send_BANG_(var_args){
var G__18613 = arguments.length;
switch (G__18613) {
case 2:
return taoensso.sente.chsk_send_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return taoensso.sente.chsk_send_BANG_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 3:
return taoensso.sente.chsk_send_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

taoensso.sente.chsk_send_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (chsk,ev){
return taoensso.sente.chsk_send_BANG_.call(null,chsk,ev,cljs.core.PersistentArrayMap.EMPTY);
});

taoensso.sente.chsk_send_BANG_.cljs$core$IFn$_invoke$arity$4 = (function (chsk,ev,_QMARK_timeout_ms,_QMARK_cb){
return taoensso.sente.chsk_send_BANG_.call(null,chsk,ev,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406),_QMARK_timeout_ms,new cljs.core.Keyword(null,"cb","cb",589947841),_QMARK_cb], null));
});

taoensso.sente.chsk_send_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (chsk,ev,opts){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,773,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay((function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk send: (%s) %s",cljs.core.assoc.call(null,opts,new cljs.core.Keyword(null,"cb","cb",589947841),cljs.core.boolean$.call(null,new cljs.core.Keyword(null,"cb","cb",589947841).cljs$core$IFn$_invoke$arity$1(opts))),ev], null);
}),null)),null,-313251739);

return taoensso.sente._chsk_send_BANG_.call(null,chsk,ev,opts);
});

taoensso.sente.chsk_send_BANG_.cljs$lang$maxFixedArity = 4;

taoensso.sente.chsk_send__GT_closed_BANG_ = (function taoensso$sente$chsk_send__GT_closed_BANG_(_QMARK_cb_fn){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,778,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay((function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk send against closed chsk."], null);
}),null)),null,1189245594);

if(cljs.core.truth_(_QMARK_cb_fn)){
_QMARK_cb_fn.call(null,new cljs.core.Keyword("chsk","closed","chsk/closed",-922855264));
} else {
}

return false;
});
taoensso.sente.assert_send_args = (function taoensso$sente$assert_send_args(x,_QMARK_timeout_ms,_QMARK_cb){
taoensso.sente.assert_event.call(null,x);

if((((((_QMARK_timeout_ms == null)) && ((_QMARK_cb == null)))) || (taoensso.encore.nat_int_QMARK_.call(null,_QMARK_timeout_ms)))){
} else {
throw (new Error(["Assert failed: ",["cb requires a timeout; timeout-ms should be a +ive integer: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(_QMARK_timeout_ms)].join(''),"\n","(or (and (nil? ?timeout-ms) (nil? ?cb)) (and (enc/nat-int? ?timeout-ms)))"].join('')));
}

if((((_QMARK_cb == null)) || (cljs.core.ifn_QMARK_.call(null,_QMARK_cb)) || (taoensso.encore.chan_QMARK_.call(null,_QMARK_cb)))){
return null;
} else {
throw (new Error(["Assert failed: ",["cb should be nil, an ifn, or a channel: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.type.call(null,_QMARK_cb))].join(''),"\n","(or (nil? ?cb) (ifn? ?cb) (enc/chan? ?cb))"].join('')));
}
});
taoensso.sente.pull_unused_cb_fn_BANG_ = (function taoensso$sente$pull_unused_cb_fn_BANG_(cbs_waiting_,_QMARK_cb_uuid){
var temp__5720__auto__ = _QMARK_cb_uuid;
if(cljs.core.truth_(temp__5720__auto__)){
var cb_uuid = temp__5720__auto__;
return taoensso.encore.swap_in_BANG_.call(null,cbs_waiting_,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cb_uuid], null),((function (cb_uuid,temp__5720__auto__){
return (function (_QMARK_f){
return taoensso.encore.swapped.call(null,new cljs.core.Keyword("swap","dissoc","swap/dissoc",-605373782),_QMARK_f);
});})(cb_uuid,temp__5720__auto__))
);
} else {
return null;
}
});
/**
 * Atomically swaps the value of chk's :state_ atom.
 */
taoensso.sente.swap_chsk_state_BANG_ = (function taoensso$sente$swap_chsk_state_BANG_(chsk,f){
var vec__18615 = taoensso.encore.swap_in_BANG_.call(null,new cljs.core.Keyword(null,"state_","state_",957667102).cljs$core$IFn$_invoke$arity$1(chsk),(function (old_state){
var new_state = f.call(null,old_state);
var new_state__$1 = (cljs.core.truth_(new cljs.core.Keyword(null,"first-open?","first-open?",396686530).cljs$core$IFn$_invoke$arity$1(old_state))?cljs.core.assoc.call(null,new_state,new cljs.core.Keyword(null,"first-open?","first-open?",396686530),false):new_state);
var new_state__$2 = (cljs.core.truth_(new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(new_state__$1))?cljs.core.dissoc.call(null,new_state__$1,new cljs.core.Keyword(null,"udt-next-reconnect","udt-next-reconnect",-1990375733)):new_state__$1);
return taoensso.encore.swapped.call(null,new_state__$2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [old_state,new_state__$2], null));
}));
var old_state = cljs.core.nth.call(null,vec__18615,(0),null);
var new_state = cljs.core.nth.call(null,vec__18615,(1),null);
if(cljs.core.not_EQ_.call(null,old_state,new_state)){
var output = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [old_state,new_state], null);
cljs.core.async.put_BANG_.call(null,cljs.core.get_in.call(null,chsk,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"chs","chs",376886120),new cljs.core.Keyword(null,"state","state",-1988618099)], null)),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","state","chsk/state",-1991397620),output], null));

return output;
} else {
return null;
}
});
taoensso.sente.chsk_state__GT_closed = (function taoensso$sente$chsk_state__GT_closed(state,reason){
var e_18620 = (function (){try{if(cljs.core.map_QMARK_.call(null,state)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18618){if((e18618 instanceof Error)){
var e = e18618;
return e;
} else {
throw e18618;

}
}})();
if((e_18620 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",825,"(map? state)",state,e_18620,null);
}

var e_18621 = (function (){try{if((function (x){
return cljs.core.contains_QMARK_.call(null,taoensso.truss.impl.set_STAR_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"requested-disconnect","requested-disconnect",1037120641),null,new cljs.core.Keyword(null,"downgrading-ws-to-ajax","downgrading-ws-to-ajax",402136720),null,new cljs.core.Keyword(null,"unexpected","unexpected",-1137752424),null,new cljs.core.Keyword(null,"requested-reconnect","requested-reconnect",2008347707),null], null), null)),x);
}).call(null,reason)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18619){if((e18619 instanceof Error)){
var e = e18619;
return e;
} else {
throw e18619;

}
}})();
if((e_18621 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",826,"([:el #{:requested-disconnect :downgrading-ws-to-ajax :unexpected :requested-reconnect}] reason)",reason,e_18621,null);
}

if(cljs.core.truth_((function (){var or__4131__auto__ = new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return cljs.core.not_EQ_.call(null,reason,new cljs.core.Keyword(null,"unexpected","unexpected",-1137752424));
}
})())){
return cljs.core.assoc.call(null,cljs.core.dissoc.call(null,state,new cljs.core.Keyword(null,"udt-next-reconnect","udt-next-reconnect",-1990375733)),new cljs.core.Keyword(null,"open?","open?",1238443125),false,new cljs.core.Keyword(null,"last-close","last-close",-2054255782),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"udt","udt",2011712751),taoensso.encore.now_udt.call(null),new cljs.core.Keyword(null,"reason","reason",-2070751759),reason], null));
} else {
return state;
}
});
/**
 * Experimental, undocumented. Allows a core.async channel to be provided
 *   instead of a cb-fn. The channel will receive values of form
 *   [<event-id>.cb <reply>].
 */
taoensso.sente.cb_chan_as_fn = (function taoensso$sente$cb_chan_as_fn(_QMARK_cb,ev){
if((((_QMARK_cb == null)) || (cljs.core.ifn_QMARK_.call(null,_QMARK_cb)))){
return _QMARK_cb;
} else {
var e_18626 = (function (){try{if(taoensso.encore.chan_QMARK_.call(null,_QMARK_cb)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18622){if((e18622 instanceof Error)){
var e = e18622;
return e;
} else {
throw e18622;

}
}})();
if((e_18626 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",847,"(enc/chan? ?cb)",_QMARK_cb,e_18626,null);
}

taoensso.sente.assert_event.call(null,ev);

var vec__18623 = ev;
var ev_id = cljs.core.nth.call(null,vec__18623,(0),null);
var _ = cljs.core.nth.call(null,vec__18623,(1),null);
var cb_ch = _QMARK_cb;
return ((function (vec__18623,ev_id,_,cb_ch){
return (function (reply){
return cljs.core.async.put_BANG_.call(null,cb_ch,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.keyword.call(null,[taoensso.encore.as_qname.call(null,ev_id),".cb"].join('')),reply], null));
});
;})(vec__18623,ev_id,_,cb_ch))
}
});
taoensso.sente.receive_buffered_evs_BANG_ = (function taoensso$sente$receive_buffered_evs_BANG_(chs,clj){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,858,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay((function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["receive-buffered-evs!: %s",clj], null);
}),null)),null,824573);

var buffered_evs = ((cljs.core.vector_QMARK_.call(null,clj))?clj:taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",859,"(vector? clj)",clj,null,null));
var seq__18627 = cljs.core.seq.call(null,buffered_evs);
var chunk__18628 = null;
var count__18629 = (0);
var i__18630 = (0);
while(true){
if((i__18630 < count__18629)){
var ev = cljs.core._nth.call(null,chunk__18628,i__18630);
taoensso.sente.assert_event.call(null,ev);

var vec__18637_18643 = ev;
var id_18644 = cljs.core.nth.call(null,vec__18637_18643,(0),null);
if(cljs.core.not_EQ_.call(null,cljs.core.namespace.call(null,id_18644),"chsk")){
} else {
throw (new Error("Assert failed: (not= (namespace id) \"chsk\")"));
}

cljs.core.async.put_BANG_.call(null,new cljs.core.Keyword(null,"<server","<server",-2135373537).cljs$core$IFn$_invoke$arity$1(chs),ev);


var G__18645 = seq__18627;
var G__18646 = chunk__18628;
var G__18647 = count__18629;
var G__18648 = (i__18630 + (1));
seq__18627 = G__18645;
chunk__18628 = G__18646;
count__18629 = G__18647;
i__18630 = G__18648;
continue;
} else {
var temp__5720__auto__ = cljs.core.seq.call(null,seq__18627);
if(temp__5720__auto__){
var seq__18627__$1 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__18627__$1)){
var c__4550__auto__ = cljs.core.chunk_first.call(null,seq__18627__$1);
var G__18649 = cljs.core.chunk_rest.call(null,seq__18627__$1);
var G__18650 = c__4550__auto__;
var G__18651 = cljs.core.count.call(null,c__4550__auto__);
var G__18652 = (0);
seq__18627 = G__18649;
chunk__18628 = G__18650;
count__18629 = G__18651;
i__18630 = G__18652;
continue;
} else {
var ev = cljs.core.first.call(null,seq__18627__$1);
taoensso.sente.assert_event.call(null,ev);

var vec__18640_18653 = ev;
var id_18654 = cljs.core.nth.call(null,vec__18640_18653,(0),null);
if(cljs.core.not_EQ_.call(null,cljs.core.namespace.call(null,id_18654),"chsk")){
} else {
throw (new Error("Assert failed: (not= (namespace id) \"chsk\")"));
}

cljs.core.async.put_BANG_.call(null,new cljs.core.Keyword(null,"<server","<server",-2135373537).cljs$core$IFn$_invoke$arity$1(chs),ev);


var G__18655 = cljs.core.next.call(null,seq__18627__$1);
var G__18656 = null;
var G__18657 = (0);
var G__18658 = (0);
seq__18627 = G__18655;
chunk__18628 = G__18656;
count__18629 = G__18657;
i__18630 = G__18658;
continue;
}
} else {
return null;
}
}
break;
}
});
taoensso.sente.handshake_QMARK_ = (function taoensso$sente$handshake_QMARK_(x){
var and__4120__auto__ = cljs.core.vector_QMARK_.call(null,x);
if(and__4120__auto__){
var vec__18662 = x;
var x1 = cljs.core.nth.call(null,vec__18662,(0),null);
return cljs.core._EQ_.call(null,x1,new cljs.core.Keyword("chsk","handshake","chsk/handshake",64910686));
} else {
return and__4120__auto__;
}
});
taoensso.sente.receive_handshake_BANG_ = (function taoensso$sente$receive_handshake_BANG_(chsk_type,chsk,clj){
var e_18676 = (function (){try{if((function (x){
return cljs.core.contains_QMARK_.call(null,taoensso.truss.impl.set_STAR_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"ws","ws",86841443),null,new cljs.core.Keyword(null,"ajax","ajax",814345549),null], null), null)),x);
}).call(null,chsk_type)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18666){if((e18666 instanceof Error)){
var e = e18666;
return e;
} else {
throw e18666;

}
}})();
if((e_18676 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",873,"([:el #{:ws :ajax}] chsk-type)",chsk_type,e_18676,null);
}

var e_18677 = (function (){try{if(taoensso.sente.handshake_QMARK_.call(null,clj)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18667){if((e18667 instanceof Error)){
var e = e18667;
return e;
} else {
throw e18667;

}
}})();
if((e_18677 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",874,"(handshake? clj)",clj,e_18677,null);
}

taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,875,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay((function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["receive-handshake! (%s): %s",chsk_type,clj], null);
}),null)),null,-2067755935);

var vec__18668 = clj;
var _ = cljs.core.nth.call(null,vec__18668,(0),null);
var vec__18671 = cljs.core.nth.call(null,vec__18668,(1),null);
var _QMARK_uid = cljs.core.nth.call(null,vec__18671,(0),null);
var _QMARK_csrf_token = cljs.core.nth.call(null,vec__18671,(1),null);
var _QMARK_handshake_data = cljs.core.nth.call(null,vec__18671,(2),null);
var map__18674 = chsk;
var map__18674__$1 = (((((!((map__18674 == null))))?(((((map__18674.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18674.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18674):map__18674);
var chs = cljs.core.get.call(null,map__18674__$1,new cljs.core.Keyword(null,"chs","chs",376886120));
var ever_opened_QMARK__ = cljs.core.get.call(null,map__18674__$1,new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913));
var first_handshake_QMARK_ = cljs.core.compare_and_set_BANG_.call(null,ever_opened_QMARK__,false,true);
var new_state = new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"type","type",1174270348),chsk_type,new cljs.core.Keyword(null,"open?","open?",1238443125),true,new cljs.core.Keyword(null,"ever-opened?","ever-opened?",1128459732),true,new cljs.core.Keyword(null,"uid","uid",-1447769400),_QMARK_uid,new cljs.core.Keyword(null,"csrf-token","csrf-token",-1872302856),_QMARK_csrf_token,new cljs.core.Keyword(null,"handshake-data","handshake-data",-278378864),_QMARK_handshake_data,new cljs.core.Keyword(null,"first-open?","first-open?",396686530),first_handshake_QMARK_], null);
var handshake_ev = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","handshake","chsk/handshake",64910686),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [_QMARK_uid,_QMARK_csrf_token,_QMARK_handshake_data,first_handshake_QMARK_], null)], null);
taoensso.sente.assert_event.call(null,handshake_ev);

if(clojure.string.blank_QMARK_.call(null,_QMARK_csrf_token)){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,894,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (vec__18668,_,vec__18671,_QMARK_uid,_QMARK_csrf_token,_QMARK_handshake_data,map__18674,map__18674__$1,chs,ever_opened_QMARK__,first_handshake_QMARK_,new_state,handshake_ev){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["SECURITY WARNING: no CSRF token available for use by Sente"], null);
});})(vec__18668,_,vec__18671,_QMARK_uid,_QMARK_csrf_token,_QMARK_handshake_data,map__18674,map__18674__$1,chs,ever_opened_QMARK__,first_handshake_QMARK_,new_state,handshake_ev))
,null)),null,-1429343138);
} else {
}

taoensso.sente.swap_chsk_state_BANG_.call(null,chsk,((function (vec__18668,_,vec__18671,_QMARK_uid,_QMARK_csrf_token,_QMARK_handshake_data,map__18674,map__18674__$1,chs,ever_opened_QMARK__,first_handshake_QMARK_,new_state,handshake_ev){
return (function (p1__18665_SHARP_){
return cljs.core.merge.call(null,p1__18665_SHARP_,new_state);
});})(vec__18668,_,vec__18671,_QMARK_uid,_QMARK_csrf_token,_QMARK_handshake_data,map__18674,map__18674__$1,chs,ever_opened_QMARK__,first_handshake_QMARK_,new_state,handshake_ev))
);

cljs.core.async.put_BANG_.call(null,new cljs.core.Keyword(null,"internal","internal",-854870097).cljs$core$IFn$_invoke$arity$1(chs),handshake_ev);

return new cljs.core.Keyword(null,"handled","handled",1889700151);
});
/**
 * nnil iff the websocket npm library[1] is available.
 *   Easiest way to install:
 *     1. Add the lein-npm[2] plugin to your `project.clj`,
 *     2. Add: `:npm {:dependencies [[websocket "1.0.23"]]}`
 * 
 *   [1] Ref. https://www.npmjs.com/package/websocket
 *   [2] Ref. https://github.com/RyanMcG/lein-npm
 */
taoensso.sente._QMARK_node_npm_websocket_ = (new cljs.core.Delay((function (){
if(((taoensso.sente.node_target_QMARK_) && ((typeof require !== 'undefined')))){
try{return require("websocket");
}catch (e18678){var e = e18678;
return null;
}} else {
return null;
}
}),null));

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IKVReduce}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {taoensso.sente.IChSocket}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
taoensso.sente.ChWebSocket = (function (client_id,chs,params,packer,url,ws_kalive_ms,state_,instance_handle_,retry_count_,ever_opened_QMARK__,backoff_ms_fn,cbs_waiting_,socket_,udt_last_comms_,__meta,__extmap,__hash){
this.client_id = client_id;
this.chs = chs;
this.params = params;
this.packer = packer;
this.url = url;
this.ws_kalive_ms = ws_kalive_ms;
this.state_ = state_;
this.instance_handle_ = instance_handle_;
this.retry_count_ = retry_count_;
this.ever_opened_QMARK__ = ever_opened_QMARK__;
this.backoff_ms_fn = backoff_ms_fn;
this.cbs_waiting_ = cbs_waiting_;
this.socket_ = socket_;
this.udt_last_comms_ = udt_last_comms_;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
taoensso.sente.ChWebSocket.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__4385__auto__,k__4386__auto__){
var self__ = this;
var this__4385__auto____$1 = this;
return this__4385__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__4386__auto__,null);
});

taoensso.sente.ChWebSocket.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__4387__auto__,k18685,else__4388__auto__){
var self__ = this;
var this__4387__auto____$1 = this;
var G__18689 = k18685;
var G__18689__$1 = (((G__18689 instanceof cljs.core.Keyword))?G__18689.fqn:null);
switch (G__18689__$1) {
case "client-id":
return self__.client_id;

break;
case "chs":
return self__.chs;

break;
case "params":
return self__.params;

break;
case "packer":
return self__.packer;

break;
case "url":
return self__.url;

break;
case "ws-kalive-ms":
return self__.ws_kalive_ms;

break;
case "state_":
return self__.state_;

break;
case "instance-handle_":
return self__.instance_handle_;

break;
case "retry-count_":
return self__.retry_count_;

break;
case "ever-opened?_":
return self__.ever_opened_QMARK__;

break;
case "backoff-ms-fn":
return self__.backoff_ms_fn;

break;
case "cbs-waiting_":
return self__.cbs_waiting_;

break;
case "socket_":
return self__.socket_;

break;
case "udt-last-comms_":
return self__.udt_last_comms_;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k18685,else__4388__auto__);

}
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__4404__auto__,f__4405__auto__,init__4406__auto__){
var self__ = this;
var this__4404__auto____$1 = this;
return cljs.core.reduce.call(null,((function (this__4404__auto____$1){
return (function (ret__4407__auto__,p__18690){
var vec__18691 = p__18690;
var k__4408__auto__ = cljs.core.nth.call(null,vec__18691,(0),null);
var v__4409__auto__ = cljs.core.nth.call(null,vec__18691,(1),null);
return f__4405__auto__.call(null,ret__4407__auto__,k__4408__auto__,v__4409__auto__);
});})(this__4404__auto____$1))
,init__4406__auto__,this__4404__auto____$1);
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__4399__auto__,writer__4400__auto__,opts__4401__auto__){
var self__ = this;
var this__4399__auto____$1 = this;
var pr_pair__4402__auto__ = ((function (this__4399__auto____$1){
return (function (keyval__4403__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__4400__auto__,cljs.core.pr_writer,""," ","",opts__4401__auto__,keyval__4403__auto__);
});})(this__4399__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__4400__auto__,pr_pair__4402__auto__,"#taoensso.sente.ChWebSocket{",", ","}",opts__4401__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 14, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"client-id","client-id",-464622140),self__.client_id],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"chs","chs",376886120),self__.chs],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"params","params",710516235),self__.params],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"packer","packer",66077544),self__.packer],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"url","url",276297046),self__.url],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),self__.ws_kalive_ms],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),self__.instance_handle_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093),self__.retry_count_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),self__.ever_opened_QMARK__],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),self__.backoff_ms_fn],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061),self__.cbs_waiting_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"socket_","socket_",-361048908),self__.socket_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639),self__.udt_last_comms_],null))], null),self__.__extmap));
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__18684){
var self__ = this;
var G__18684__$1 = this;
return (new cljs.core.RecordIter((0),G__18684__$1,14,new cljs.core.PersistentVector(null, 14, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"client-id","client-id",-464622140),new cljs.core.Keyword(null,"chs","chs",376886120),new cljs.core.Keyword(null,"params","params",710516235),new cljs.core.Keyword(null,"packer","packer",66077544),new cljs.core.Keyword(null,"url","url",276297046),new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),new cljs.core.Keyword(null,"state_","state_",957667102),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061),new cljs.core.Keyword(null,"socket_","socket_",-361048908),new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__4383__auto__){
var self__ = this;
var this__4383__auto____$1 = this;
return self__.__meta;
});

taoensso.sente.ChWebSocket.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__4380__auto__){
var self__ = this;
var this__4380__auto____$1 = this;
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,self__.__hash));
});

taoensso.sente.ChWebSocket.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__4389__auto__){
var self__ = this;
var this__4389__auto____$1 = this;
return (14 + cljs.core.count.call(null,self__.__extmap));
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__4381__auto__){
var self__ = this;
var this__4381__auto____$1 = this;
var h__4243__auto__ = self__.__hash;
if((!((h__4243__auto__ == null)))){
return h__4243__auto__;
} else {
var h__4243__auto____$1 = ((function (h__4243__auto__,this__4381__auto____$1){
return (function (coll__4382__auto__){
return (1998688700 ^ cljs.core.hash_unordered_coll.call(null,coll__4382__auto__));
});})(h__4243__auto__,this__4381__auto____$1))
.call(null,this__4381__auto____$1);
self__.__hash = h__4243__auto____$1;

return h__4243__auto____$1;
}
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this18686,other18687){
var self__ = this;
var this18686__$1 = this;
return (((!((other18687 == null)))) && ((this18686__$1.constructor === other18687.constructor)) && (cljs.core._EQ_.call(null,this18686__$1.client_id,other18687.client_id)) && (cljs.core._EQ_.call(null,this18686__$1.chs,other18687.chs)) && (cljs.core._EQ_.call(null,this18686__$1.params,other18687.params)) && (cljs.core._EQ_.call(null,this18686__$1.packer,other18687.packer)) && (cljs.core._EQ_.call(null,this18686__$1.url,other18687.url)) && (cljs.core._EQ_.call(null,this18686__$1.ws_kalive_ms,other18687.ws_kalive_ms)) && (cljs.core._EQ_.call(null,this18686__$1.state_,other18687.state_)) && (cljs.core._EQ_.call(null,this18686__$1.instance_handle_,other18687.instance_handle_)) && (cljs.core._EQ_.call(null,this18686__$1.retry_count_,other18687.retry_count_)) && (cljs.core._EQ_.call(null,this18686__$1.ever_opened_QMARK__,other18687.ever_opened_QMARK__)) && (cljs.core._EQ_.call(null,this18686__$1.backoff_ms_fn,other18687.backoff_ms_fn)) && (cljs.core._EQ_.call(null,this18686__$1.cbs_waiting_,other18687.cbs_waiting_)) && (cljs.core._EQ_.call(null,this18686__$1.socket_,other18687.socket_)) && (cljs.core._EQ_.call(null,this18686__$1.udt_last_comms_,other18687.udt_last_comms_)) && (cljs.core._EQ_.call(null,this18686__$1.__extmap,other18687.__extmap)));
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__4394__auto__,k__4395__auto__){
var self__ = this;
var this__4394__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 14, [new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),null,new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),null,new cljs.core.Keyword(null,"client-id","client-id",-464622140),null,new cljs.core.Keyword(null,"packer","packer",66077544),null,new cljs.core.Keyword(null,"chs","chs",376886120),null,new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639),null,new cljs.core.Keyword(null,"params","params",710516235),null,new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093),null,new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),null,new cljs.core.Keyword(null,"socket_","socket_",-361048908),null,new cljs.core.Keyword(null,"url","url",276297046),null,new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),null,new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061),null,new cljs.core.Keyword(null,"state_","state_",957667102),null], null), null),k__4395__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__4394__auto____$1),self__.__meta),k__4395__auto__);
} else {
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__4395__auto__)),null));
}
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__4392__auto__,k__4393__auto__,G__18684){
var self__ = this;
var this__4392__auto____$1 = this;
var pred__18694 = cljs.core.keyword_identical_QMARK_;
var expr__18695 = k__4393__auto__;
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"client-id","client-id",-464622140),expr__18695))){
return (new taoensso.sente.ChWebSocket(G__18684,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"chs","chs",376886120),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,G__18684,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"params","params",710516235),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,G__18684,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"packer","packer",66077544),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,G__18684,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"url","url",276297046),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,G__18684,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,G__18684,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"state_","state_",957667102),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,G__18684,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,G__18684,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,G__18684,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,G__18684,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,G__18684,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,G__18684,self__.socket_,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"socket_","socket_",-361048908),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,G__18684,self__.udt_last_comms_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18694.call(null,new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639),expr__18695))){
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,G__18684,self__.__meta,self__.__extmap,null));
} else {
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__4393__auto__,G__18684),null));
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});

taoensso.sente.ChWebSocket.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__4397__auto__){
var self__ = this;
var this__4397__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 14, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"client-id","client-id",-464622140),self__.client_id,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"chs","chs",376886120),self__.chs,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"params","params",710516235),self__.params,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"packer","packer",66077544),self__.packer,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"url","url",276297046),self__.url,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),self__.ws_kalive_ms,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),self__.instance_handle_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093),self__.retry_count_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),self__.ever_opened_QMARK__,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),self__.backoff_ms_fn,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061),self__.cbs_waiting_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"socket_","socket_",-361048908),self__.socket_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639),self__.udt_last_comms_,null))], null),self__.__extmap));
});

taoensso.sente.ChWebSocket.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__4384__auto__,G__18684){
var self__ = this;
var this__4384__auto____$1 = this;
return (new taoensso.sente.ChWebSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.ws_kalive_ms,self__.state_,self__.instance_handle_,self__.retry_count_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.cbs_waiting_,self__.socket_,self__.udt_last_comms_,G__18684,self__.__extmap,self__.__hash));
});

taoensso.sente.ChWebSocket.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__4390__auto__,entry__4391__auto__){
var self__ = this;
var this__4390__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__4391__auto__)){
return this__4390__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__4391__auto__,(0)),cljs.core._nth.call(null,entry__4391__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__4390__auto____$1,entry__4391__auto__);
}
});

taoensso.sente.ChWebSocket.prototype.taoensso$sente$IChSocket$ = cljs.core.PROTOCOL_SENTINEL;

taoensso.sente.ChWebSocket.prototype.taoensso$sente$IChSocket$_chsk_disconnect_BANG_$arity$2 = (function (chsk,reason){
var self__ = this;
var chsk__$1 = this;
cljs.core.reset_BANG_.call(null,self__.instance_handle_,null);

taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (chsk__$1){
return (function (p1__18679_SHARP_){
return taoensso.sente.chsk_state__GT_closed.call(null,p1__18679_SHARP_,reason);
});})(chsk__$1))
);

var temp__5720__auto__ = cljs.core.deref.call(null,self__.socket_);
if(cljs.core.truth_(temp__5720__auto__)){
var s = temp__5720__auto__;
return s.close((1000),"CLOSE_NORMAL");
} else {
return null;
}
});

taoensso.sente.ChWebSocket.prototype.taoensso$sente$IChSocket$_chsk_reconnect_BANG_$arity$1 = (function (chsk){
var self__ = this;
var chsk__$1 = this;
chsk__$1.taoensso$sente$IChSocket$_chsk_disconnect_BANG_$arity$2(null,new cljs.core.Keyword(null,"requested-reconnect","requested-reconnect",2008347707));

return chsk__$1.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1(null);
});

taoensso.sente.ChWebSocket.prototype.taoensso$sente$IChSocket$_chsk_send_BANG_$arity$3 = (function (chsk,ev,opts){
var self__ = this;
var chsk__$1 = this;
var map__18697 = opts;
var map__18697__$1 = (((((!((map__18697 == null))))?(((((map__18697.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18697.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18697):map__18697);
var _QMARK_timeout_ms = cljs.core.get.call(null,map__18697__$1,new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406));
var _QMARK_cb = cljs.core.get.call(null,map__18697__$1,new cljs.core.Keyword(null,"cb","cb",589947841));
var flush_QMARK_ = cljs.core.get.call(null,map__18697__$1,new cljs.core.Keyword(null,"flush?","flush?",-108887231));
var _ = taoensso.sente.assert_send_args.call(null,ev,_QMARK_timeout_ms,_QMARK_cb);
var _QMARK_cb_fn = taoensso.sente.cb_chan_as_fn.call(null,_QMARK_cb,ev);
if(cljs.core.not.call(null,new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,self__.state_)))){
return taoensso.sente.chsk_send__GT_closed_BANG_.call(null,_QMARK_cb_fn);
} else {
var _QMARK_cb_uuid = (cljs.core.truth_(_QMARK_cb_fn)?taoensso.encore.uuid_str.call(null,(6)):null);
var ppstr = taoensso.sente.pack.call(null,self__.packer,ev,_QMARK_cb_uuid);
var temp__5720__auto___18777 = _QMARK_cb_uuid;
if(cljs.core.truth_(temp__5720__auto___18777)){
var cb_uuid_18778 = temp__5720__auto___18777;
taoensso.encore.reset_in_BANG_.call(null,self__.cbs_waiting_,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cb_uuid_18778], null),(function (){var e = (function (){try{if(taoensso.truss.impl.some_QMARK_.call(null,_QMARK_cb_fn)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18699){if((e18699 instanceof Error)){
var e = e18699;
return e;
} else {
throw e18699;

}
}})();
if((e == null)){
return _QMARK_cb_fn;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",964,"(taoensso.truss.impl/some? ?cb-fn)",_QMARK_cb_fn,e,null);
}
})());

var temp__5720__auto___18779__$1 = _QMARK_timeout_ms;
if(cljs.core.truth_(temp__5720__auto___18779__$1)){
var timeout_ms_18780 = temp__5720__auto___18779__$1;
var c__8790__auto___18781 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___18781,timeout_ms_18780,temp__5720__auto___18779__$1,cb_uuid_18778,temp__5720__auto___18777,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___18781,timeout_ms_18780,temp__5720__auto___18779__$1,cb_uuid_18778,temp__5720__auto___18777,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function (state_18710){
var state_val_18711 = (state_18710[(1)]);
if((state_val_18711 === (1))){
var inst_18700 = cljs.core.async.timeout.call(null,timeout_ms_18780);
var state_18710__$1 = state_18710;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18710__$1,(2),inst_18700);
} else {
if((state_val_18711 === (2))){
var inst_18703 = (state_18710[(7)]);
var inst_18702 = (state_18710[(2)]);
var inst_18703__$1 = taoensso.sente.pull_unused_cb_fn_BANG_.call(null,self__.cbs_waiting_,_QMARK_cb_uuid);
var state_18710__$1 = (function (){var statearr_18712 = state_18710;
(statearr_18712[(8)] = inst_18702);

(statearr_18712[(7)] = inst_18703__$1);

return statearr_18712;
})();
if(cljs.core.truth_(inst_18703__$1)){
var statearr_18713_18782 = state_18710__$1;
(statearr_18713_18782[(1)] = (3));

} else {
var statearr_18714_18783 = state_18710__$1;
(statearr_18714_18783[(1)] = (4));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18711 === (3))){
var inst_18703 = (state_18710[(7)]);
var inst_18705 = inst_18703.call(null,new cljs.core.Keyword("chsk","timeout","chsk/timeout",-319776489));
var state_18710__$1 = state_18710;
var statearr_18715_18784 = state_18710__$1;
(statearr_18715_18784[(2)] = inst_18705);

(statearr_18715_18784[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18711 === (4))){
var state_18710__$1 = state_18710;
var statearr_18716_18785 = state_18710__$1;
(statearr_18716_18785[(2)] = null);

(statearr_18716_18785[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18711 === (5))){
var inst_18708 = (state_18710[(2)]);
var state_18710__$1 = state_18710;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18710__$1,inst_18708);
} else {
return null;
}
}
}
}
}
});})(c__8790__auto___18781,timeout_ms_18780,temp__5720__auto___18779__$1,cb_uuid_18778,temp__5720__auto___18777,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
;
return ((function (switch__8695__auto__,c__8790__auto___18781,timeout_ms_18780,temp__5720__auto___18779__$1,cb_uuid_18778,temp__5720__auto___18777,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18717 = [null,null,null,null,null,null,null,null,null];
(statearr_18717[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18717[(1)] = (1));

return statearr_18717;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18710){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18710);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18718){if((e18718 instanceof Object)){
var ex__8699__auto__ = e18718;
var statearr_18719_18786 = state_18710;
(statearr_18719_18786[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18710);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18718;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18787 = state_18710;
state_18710 = G__18787;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18710){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18710);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___18781,timeout_ms_18780,temp__5720__auto___18779__$1,cb_uuid_18778,temp__5720__auto___18777,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
})();
var state__8792__auto__ = (function (){var statearr_18720 = f__8791__auto__.call(null);
(statearr_18720[(6)] = c__8790__auto___18781);

return statearr_18720;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___18781,timeout_ms_18780,temp__5720__auto___18779__$1,cb_uuid_18778,temp__5720__auto___18777,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
);

} else {
}
} else {
}

try{cljs.core.deref.call(null,self__.socket_).send(ppstr);

cljs.core.reset_BANG_.call(null,self__.udt_last_comms_,taoensso.encore.now_udt.call(null));

return new cljs.core.Keyword(null,"apparent-success","apparent-success",242592222);
}catch (e18721){var e = e18721;
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"error","error",-978969032),"taoensso.sente",null,976,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (e,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [e,"Chsk send error"], null);
});})(e,_QMARK_cb_uuid,ppstr,map__18697,map__18697__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
,null)),null,1607868005);

var temp__5720__auto___18788 = _QMARK_cb_uuid;
if(cljs.core.truth_(temp__5720__auto___18788)){
var cb_uuid_18789 = temp__5720__auto___18788;
var cb_fn_STAR__18790 = (function (){var or__4131__auto__ = taoensso.sente.pull_unused_cb_fn_BANG_.call(null,self__.cbs_waiting_,cb_uuid_18789);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var e__$1 = (function (){try{if(taoensso.truss.impl.some_QMARK_.call(null,_QMARK_cb_fn)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18722){if((e18722 instanceof Error)){
var e__$1 = e18722;
return e__$1;
} else {
throw e18722;

}
}})();
if((e__$1 == null)){
return _QMARK_cb_fn;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",979,"(taoensso.truss.impl/some? ?cb-fn)",_QMARK_cb_fn,e__$1,null);
}
}
})();
cb_fn_STAR__18790.call(null,new cljs.core.Keyword("chsk","error","chsk/error",-984175439));
} else {
}

return false;
}}
});

taoensso.sente.ChWebSocket.prototype.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1 = (function (chsk){
var self__ = this;
var chsk__$1 = this;
var temp__5720__auto__ = (function (){var or__4131__auto__ = taoensso.encore.oget.call(null,goog.global,"WebSocket");
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = taoensso.encore.oget.call(null,goog.global,"MozWebSocket");
if(cljs.core.truth_(or__4131__auto____$1)){
return or__4131__auto____$1;
} else {
return taoensso.encore.oget.call(null,cljs.core.deref.call(null,taoensso.sente._QMARK_node_npm_websocket_),"w3cwebsocket");
}
}
})();
if(cljs.core.truth_(temp__5720__auto__)){
var WebSocket = temp__5720__auto__;
var instance_handle = cljs.core.reset_BANG_.call(null,self__.instance_handle_,taoensso.encore.uuid_str.call(null));
var have_handle_QMARK_ = ((function (instance_handle,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
return cljs.core._EQ_.call(null,cljs.core.deref.call(null,self__.instance_handle_),instance_handle);
});})(instance_handle,WebSocket,temp__5720__auto__,chsk__$1))
;
var connect_fn = ((function (instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function taoensso$sente$connect_fn(){
if(have_handle_QMARK_.call(null)){
var retry_fn = ((function (instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
if(have_handle_QMARK_.call(null)){
var retry_count_STAR_ = cljs.core.swap_BANG_.call(null,self__.retry_count_,cljs.core.inc);
var backoff_ms = self__.backoff_ms_fn.call(null,retry_count_STAR_);
var udt_next_reconnect = (taoensso.encore.now_udt.call(null) + backoff_ms);
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1001,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk is closed: will try reconnect attempt (%s) in %s ms",retry_count_STAR_,backoff_ms], null);
});})(retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
,null)),null,250501979);

goog.global.setTimeout(taoensso$sente$connect_fn,backoff_ms);

return taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (p1__18680_SHARP_){
return cljs.core.assoc.call(null,p1__18680_SHARP_,new cljs.core.Keyword(null,"udt-next-reconnect","udt-next-reconnect",-1990375733),udt_next_reconnect);
});})(retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
);
} else {
return null;
}
});})(instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
;
var _QMARK_socket = (function (){try{return (new WebSocket(taoensso.encore.merge_url_with_query_string.call(null,self__.url,cljs.core.merge.call(null,self__.params,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"client-id","client-id",-464622140),self__.client_id], null)))));
}catch (e18723){var e = e18723;
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"error","error",-978969032),"taoensso.sente",null,1015,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (e,retry_fn,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [e,"WebSocket error"], null);
});})(e,retry_fn,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
,null)),null,511504839);

return null;
}})();
if(cljs.core.not.call(null,_QMARK_socket)){
return retry_fn.call(null);
} else {
return cljs.core.reset_BANG_.call(null,self__.socket_,(function (){var G__18724 = _QMARK_socket;
(G__18724["onerror"] = ((function (G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (ws_ev){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"error","error",-978969032),"taoensso.sente",null,1025,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["WebSocket error: %s",(function (){try{return cljs.core.js__GT_clj.call(null,ws_ev);
}catch (e18725){var _ = e18725;
return ws_ev;
}})()], null);
});})(G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
,null)),null,-1746786890);

var last_ws_error = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"udt","udt",2011712751),taoensso.encore.now_udt.call(null),new cljs.core.Keyword(null,"ev","ev",-406827324),ws_ev], null);
return taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (last_ws_error,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (p1__18681_SHARP_){
return cljs.core.assoc.call(null,p1__18681_SHARP_,new cljs.core.Keyword(null,"last-ws-error","last-ws-error",-820288502),last_ws_error);
});})(last_ws_error,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
);
});})(G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
);

(G__18724["onmessage"] = ((function (G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (ws_ev){
var ppstr = taoensso.encore.oget.call(null,ws_ev,"data");
var vec__18726 = taoensso.sente.unpack.call(null,self__.packer,ppstr);
var clj = cljs.core.nth.call(null,vec__18726,(0),null);
var _QMARK_cb_uuid = cljs.core.nth.call(null,vec__18726,(1),null);
cljs.core.reset_BANG_.call(null,self__.udt_last_comms_,taoensso.encore.now_udt.call(null));

var or__4131__auto__ = ((taoensso.sente.handshake_QMARK_.call(null,clj))?(function (){
taoensso.sente.receive_handshake_BANG_.call(null,new cljs.core.Keyword(null,"ws","ws",86841443),chsk__$1,clj);

cljs.core.reset_BANG_.call(null,self__.retry_count_,(0));

return new cljs.core.Keyword(null,"handshake","handshake",68079331);
})()
:null);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = ((cljs.core._EQ_.call(null,clj,new cljs.core.Keyword("chsk","ws-ping","chsk/ws-ping",191675304)))?(function (){
cljs.core.async.put_BANG_.call(null,new cljs.core.Keyword(null,"<server","<server",-2135373537).cljs$core$IFn$_invoke$arity$1(self__.chs),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","ws-ping","chsk/ws-ping",191675304)], null));

return new cljs.core.Keyword(null,"noop","noop",-673731258);
})()
:null);
if(cljs.core.truth_(or__4131__auto____$1)){
return or__4131__auto____$1;
} else {
var temp__5718__auto__ = _QMARK_cb_uuid;
if(cljs.core.truth_(temp__5718__auto__)){
var cb_uuid = temp__5718__auto__;
var temp__5718__auto____$1 = taoensso.sente.pull_unused_cb_fn_BANG_.call(null,self__.cbs_waiting_,cb_uuid);
if(cljs.core.truth_(temp__5718__auto____$1)){
var cb_fn = temp__5718__auto____$1;
return cb_fn.call(null,clj);
} else {
return taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1061,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (temp__5718__auto____$1,cb_uuid,temp__5718__auto__,or__4131__auto____$1,or__4131__auto__,ppstr,vec__18726,clj,_QMARK_cb_uuid,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Cb reply w/o local cb-fn: %s",clj], null);
});})(temp__5718__auto____$1,cb_uuid,temp__5718__auto__,or__4131__auto____$1,or__4131__auto__,ppstr,vec__18726,clj,_QMARK_cb_uuid,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
,null)),null,250383804);
}
} else {
var buffered_evs = clj;
return taoensso.sente.receive_buffered_evs_BANG_.call(null,self__.chs,buffered_evs);
}
}
}
});})(G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
);

(G__18724["onclose"] = ((function (G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (ws_ev){
var clean_QMARK_ = taoensso.encore.oget.call(null,ws_ev,"wasClean");
var code = taoensso.encore.oget.call(null,ws_ev,"code");
var reason = taoensso.encore.oget.call(null,ws_ev,"reason");
var last_ws_close = new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"udt","udt",2011712751),taoensso.encore.now_udt.call(null),new cljs.core.Keyword(null,"ev","ev",-406827324),ws_ev,new cljs.core.Keyword(null,"clean?","clean?",-1675631009),clean_QMARK_,new cljs.core.Keyword(null,"code","code",1586293142),code,new cljs.core.Keyword(null,"reason","reason",-2070751759),reason], null);
if(cljs.core.truth_(clean_QMARK_)){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"debug","debug",-1608172596),"taoensso.sente",null,1083,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (clean_QMARK_,code,reason,last_ws_close,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Clean WebSocket close, will not attempt reconnect"], null);
});})(clean_QMARK_,code,reason,last_ws_close,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
,null)),null,288407429);

return taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (clean_QMARK_,code,reason,last_ws_close,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (p1__18682_SHARP_){
return cljs.core.assoc.call(null,p1__18682_SHARP_,new cljs.core.Keyword(null,"last-ws-close","last-ws-close",-798104932),last_ws_close);
});})(clean_QMARK_,code,reason,last_ws_close,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
);
} else {
taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (clean_QMARK_,code,reason,last_ws_close,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1){
return (function (p1__18683_SHARP_){
return cljs.core.assoc.call(null,taoensso.sente.chsk_state__GT_closed.call(null,p1__18683_SHARP_,new cljs.core.Keyword(null,"unexpected","unexpected",-1137752424)),new cljs.core.Keyword(null,"last-ws-close","last-ws-close",-798104932),last_ws_close);
});})(clean_QMARK_,code,reason,last_ws_close,G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
);

return retry_fn.call(null);
}
});})(G__18724,retry_fn,_QMARK_socket,instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
);

return G__18724;
})());
}
} else {
return null;
}
});})(instance_handle,have_handle_QMARK_,WebSocket,temp__5720__auto__,chsk__$1))
;
var temp__5720__auto___18791__$1 = self__.ws_kalive_ms;
if(cljs.core.truth_(temp__5720__auto___18791__$1)){
var ms_18792 = temp__5720__auto___18791__$1;
var c__8790__auto___18793 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___18793,ms_18792,temp__5720__auto___18791__$1,instance_handle,have_handle_QMARK_,connect_fn,WebSocket,temp__5720__auto__,chsk__$1){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___18793,ms_18792,temp__5720__auto___18791__$1,instance_handle,have_handle_QMARK_,connect_fn,WebSocket,temp__5720__auto__,chsk__$1){
return (function (state_18756){
var state_val_18757 = (state_18756[(1)]);
if((state_val_18757 === (7))){
var inst_18752 = (state_18756[(2)]);
var state_18756__$1 = state_18756;
var statearr_18758_18794 = state_18756__$1;
(statearr_18758_18794[(2)] = inst_18752);

(statearr_18758_18794[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18757 === (1))){
var state_18756__$1 = state_18756;
var statearr_18759_18795 = state_18756__$1;
(statearr_18759_18795[(2)] = null);

(statearr_18759_18795[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18757 === (4))){
var inst_18733 = (state_18756[(2)]);
var inst_18734 = have_handle_QMARK_.call(null);
var state_18756__$1 = (function (){var statearr_18760 = state_18756;
(statearr_18760[(7)] = inst_18733);

return statearr_18760;
})();
if(inst_18734){
var statearr_18761_18796 = state_18756__$1;
(statearr_18761_18796[(1)] = (5));

} else {
var statearr_18762_18797 = state_18756__$1;
(statearr_18762_18797[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18757 === (6))){
var state_18756__$1 = state_18756;
var statearr_18763_18798 = state_18756__$1;
(statearr_18763_18798[(2)] = null);

(statearr_18763_18798[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18757 === (3))){
var inst_18754 = (state_18756[(2)]);
var state_18756__$1 = state_18756;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18756__$1,inst_18754);
} else {
if((state_val_18757 === (2))){
var inst_18730 = cljs.core.deref.call(null,self__.udt_last_comms_);
var inst_18731 = cljs.core.async.timeout.call(null,ms_18792);
var state_18756__$1 = (function (){var statearr_18764 = state_18756;
(statearr_18764[(8)] = inst_18730);

return statearr_18764;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_18756__$1,(4),inst_18731);
} else {
if((state_val_18757 === (9))){
var state_18756__$1 = state_18756;
var statearr_18765_18799 = state_18756__$1;
(statearr_18765_18799[(2)] = null);

(statearr_18765_18799[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18757 === (5))){
var inst_18730 = (state_18756[(8)]);
var inst_18736 = cljs.core.deref.call(null,self__.udt_last_comms_);
var inst_18737 = cljs.core._EQ_.call(null,inst_18730,inst_18736);
var state_18756__$1 = state_18756;
if(inst_18737){
var statearr_18766_18800 = state_18756__$1;
(statearr_18766_18800[(1)] = (8));

} else {
var statearr_18767_18801 = state_18756__$1;
(statearr_18767_18801[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18757 === (10))){
var inst_18748 = (state_18756[(2)]);
var state_18756__$1 = (function (){var statearr_18768 = state_18756;
(statearr_18768[(9)] = inst_18748);

return statearr_18768;
})();
var statearr_18769_18802 = state_18756__$1;
(statearr_18769_18802[(2)] = null);

(statearr_18769_18802[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18757 === (8))){
var inst_18739 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18740 = [new cljs.core.Keyword("chsk","ws-ping","chsk/ws-ping",191675304)];
var inst_18741 = (new cljs.core.PersistentVector(null,1,(5),inst_18739,inst_18740,null));
var inst_18742 = [new cljs.core.Keyword(null,"flush?","flush?",-108887231)];
var inst_18743 = [true];
var inst_18744 = cljs.core.PersistentHashMap.fromArrays(inst_18742,inst_18743);
var inst_18745 = taoensso.sente._chsk_send_BANG_.call(null,chsk__$1,inst_18741,inst_18744);
var state_18756__$1 = state_18756;
var statearr_18770_18803 = state_18756__$1;
(statearr_18770_18803[(2)] = inst_18745);

(statearr_18770_18803[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
});})(c__8790__auto___18793,ms_18792,temp__5720__auto___18791__$1,instance_handle,have_handle_QMARK_,connect_fn,WebSocket,temp__5720__auto__,chsk__$1))
;
return ((function (switch__8695__auto__,c__8790__auto___18793,ms_18792,temp__5720__auto___18791__$1,instance_handle,have_handle_QMARK_,connect_fn,WebSocket,temp__5720__auto__,chsk__$1){
return (function() {
var taoensso$sente$state_machine__8696__auto__ = null;
var taoensso$sente$state_machine__8696__auto____0 = (function (){
var statearr_18771 = [null,null,null,null,null,null,null,null,null,null];
(statearr_18771[(0)] = taoensso$sente$state_machine__8696__auto__);

(statearr_18771[(1)] = (1));

return statearr_18771;
});
var taoensso$sente$state_machine__8696__auto____1 = (function (state_18756){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18756);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18772){if((e18772 instanceof Object)){
var ex__8699__auto__ = e18772;
var statearr_18773_18804 = state_18756;
(statearr_18773_18804[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18756);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18772;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18805 = state_18756;
state_18756 = G__18805;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$state_machine__8696__auto__ = function(state_18756){
switch(arguments.length){
case 0:
return taoensso$sente$state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$state_machine__8696__auto____1.call(this,state_18756);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$state_machine__8696__auto____0;
taoensso$sente$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$state_machine__8696__auto____1;
return taoensso$sente$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___18793,ms_18792,temp__5720__auto___18791__$1,instance_handle,have_handle_QMARK_,connect_fn,WebSocket,temp__5720__auto__,chsk__$1))
})();
var state__8792__auto__ = (function (){var statearr_18774 = f__8791__auto__.call(null);
(statearr_18774[(6)] = c__8790__auto___18793);

return statearr_18774;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___18793,ms_18792,temp__5720__auto___18791__$1,instance_handle,have_handle_QMARK_,connect_fn,WebSocket,temp__5720__auto__,chsk__$1))
);

} else {
}

cljs.core.reset_BANG_.call(null,self__.retry_count_,(0));

connect_fn.call(null);

return chsk__$1;
} else {
return null;
}
});

taoensso.sente.ChWebSocket.getBasis = (function (){
return new cljs.core.PersistentVector(null, 14, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"client-id","client-id",1175909387,null),new cljs.core.Symbol(null,"chs","chs",2017417647,null),new cljs.core.Symbol(null,"params","params",-1943919534,null),new cljs.core.Symbol(null,"packer","packer",1706609071,null),new cljs.core.Symbol(null,"url","url",1916828573,null),new cljs.core.Symbol(null,"ws-kalive-ms","ws-kalive-ms",-1212255801,null),new cljs.core.Symbol(null,"state_","state_",-1696768667,null),new cljs.core.Symbol(null,"instance-handle_","instance-handle_",-282852930,null),new cljs.core.Symbol(null,"retry-count_","retry-count_",1660769620,null),new cljs.core.Symbol(null,"ever-opened?_","ever-opened?_",-1013096856,null),new cljs.core.Symbol(null,"backoff-ms-fn","backoff-ms-fn",-1881539814,null),new cljs.core.Symbol(null,"cbs-waiting_","cbs-waiting_",121502466,null),new cljs.core.Symbol(null,"socket_","socket_",1279482619,null),new cljs.core.Symbol(null,"udt-last-comms_","udt-last-comms_",1494731888,null)], null);
});

taoensso.sente.ChWebSocket.cljs$lang$type = true;

taoensso.sente.ChWebSocket.cljs$lang$ctorPrSeq = (function (this__4428__auto__){
return (new cljs.core.List(null,"taoensso.sente/ChWebSocket",null,(1),null));
});

taoensso.sente.ChWebSocket.cljs$lang$ctorPrWriter = (function (this__4428__auto__,writer__4429__auto__){
return cljs.core._write.call(null,writer__4429__auto__,"taoensso.sente/ChWebSocket");
});

/**
 * Positional factory function for taoensso.sente/ChWebSocket.
 */
taoensso.sente.__GT_ChWebSocket = (function taoensso$sente$__GT_ChWebSocket(client_id,chs,params,packer,url,ws_kalive_ms,state_,instance_handle_,retry_count_,ever_opened_QMARK__,backoff_ms_fn,cbs_waiting_,socket_,udt_last_comms_){
return (new taoensso.sente.ChWebSocket(client_id,chs,params,packer,url,ws_kalive_ms,state_,instance_handle_,retry_count_,ever_opened_QMARK__,backoff_ms_fn,cbs_waiting_,socket_,udt_last_comms_,null,null,null));
});

/**
 * Factory function for taoensso.sente/ChWebSocket, taking a map of keywords to field values.
 */
taoensso.sente.map__GT_ChWebSocket = (function taoensso$sente$map__GT_ChWebSocket(G__18688){
var extmap__4424__auto__ = (function (){var G__18775 = cljs.core.dissoc.call(null,G__18688,new cljs.core.Keyword(null,"client-id","client-id",-464622140),new cljs.core.Keyword(null,"chs","chs",376886120),new cljs.core.Keyword(null,"params","params",710516235),new cljs.core.Keyword(null,"packer","packer",66077544),new cljs.core.Keyword(null,"url","url",276297046),new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),new cljs.core.Keyword(null,"state_","state_",957667102),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061),new cljs.core.Keyword(null,"socket_","socket_",-361048908),new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639));
if(cljs.core.record_QMARK_.call(null,G__18688)){
return cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,G__18775);
} else {
return G__18775;
}
})();
return (new taoensso.sente.ChWebSocket(new cljs.core.Keyword(null,"client-id","client-id",-464622140).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"chs","chs",376886120).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"params","params",710516235).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"packer","packer",66077544).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"state_","state_",957667102).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"socket_","socket_",-361048908).cljs$core$IFn$_invoke$arity$1(G__18688),new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639).cljs$core$IFn$_invoke$arity$1(G__18688),null,cljs.core.not_empty.call(null,extmap__4424__auto__),null));
});

taoensso.sente.new_ChWebSocket = (function taoensso$sente$new_ChWebSocket(opts){
return taoensso.sente.map__GT_ChWebSocket.call(null,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"state_","state_",957667102),cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"ws","ws",86841443),new cljs.core.Keyword(null,"open?","open?",1238443125),false,new cljs.core.Keyword(null,"ever-opened?","ever-opened?",1128459732),false], null)),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),cljs.core.atom.call(null,null),new cljs.core.Keyword(null,"retry-count_","retry-count_",20238093),cljs.core.atom.call(null,(0)),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),cljs.core.atom.call(null,false),new cljs.core.Keyword(null,"cbs-waiting_","cbs-waiting_",-1519029061),cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY),new cljs.core.Keyword(null,"socket_","socket_",-361048908),cljs.core.atom.call(null,null),new cljs.core.Keyword(null,"udt-last-comms_","udt-last-comms_",-145799639),cljs.core.atom.call(null,null)], null),opts));
});
/**
 * We must set *some* client-side timeout otherwise an unpredictable (and
 *   probably too short) browser default will be used. Must be > server's
 *   :lp-timeout-ms.
 */
taoensso.sente.default_client_side_ajax_timeout_ms = taoensso.encore.ms.call(null,new cljs.core.Keyword(null,"secs","secs",1532330091),(60));

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IKVReduce}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {taoensso.sente.IChSocket}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
taoensso.sente.ChAjaxSocket = (function (client_id,chs,params,packer,url,state_,instance_handle_,ever_opened_QMARK__,backoff_ms_fn,ajax_opts,curr_xhr_,__meta,__extmap,__hash){
this.client_id = client_id;
this.chs = chs;
this.params = params;
this.packer = packer;
this.url = url;
this.state_ = state_;
this.instance_handle_ = instance_handle_;
this.ever_opened_QMARK__ = ever_opened_QMARK__;
this.backoff_ms_fn = backoff_ms_fn;
this.ajax_opts = ajax_opts;
this.curr_xhr_ = curr_xhr_;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
taoensso.sente.ChAjaxSocket.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__4385__auto__,k__4386__auto__){
var self__ = this;
var this__4385__auto____$1 = this;
return this__4385__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__4386__auto__,null);
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__4387__auto__,k18813,else__4388__auto__){
var self__ = this;
var this__4387__auto____$1 = this;
var G__18817 = k18813;
var G__18817__$1 = (((G__18817 instanceof cljs.core.Keyword))?G__18817.fqn:null);
switch (G__18817__$1) {
case "client-id":
return self__.client_id;

break;
case "chs":
return self__.chs;

break;
case "params":
return self__.params;

break;
case "packer":
return self__.packer;

break;
case "url":
return self__.url;

break;
case "state_":
return self__.state_;

break;
case "instance-handle_":
return self__.instance_handle_;

break;
case "ever-opened?_":
return self__.ever_opened_QMARK__;

break;
case "backoff-ms-fn":
return self__.backoff_ms_fn;

break;
case "ajax-opts":
return self__.ajax_opts;

break;
case "curr-xhr_":
return self__.curr_xhr_;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k18813,else__4388__auto__);

}
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__4404__auto__,f__4405__auto__,init__4406__auto__){
var self__ = this;
var this__4404__auto____$1 = this;
return cljs.core.reduce.call(null,((function (this__4404__auto____$1){
return (function (ret__4407__auto__,p__18818){
var vec__18819 = p__18818;
var k__4408__auto__ = cljs.core.nth.call(null,vec__18819,(0),null);
var v__4409__auto__ = cljs.core.nth.call(null,vec__18819,(1),null);
return f__4405__auto__.call(null,ret__4407__auto__,k__4408__auto__,v__4409__auto__);
});})(this__4404__auto____$1))
,init__4406__auto__,this__4404__auto____$1);
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__4399__auto__,writer__4400__auto__,opts__4401__auto__){
var self__ = this;
var this__4399__auto____$1 = this;
var pr_pair__4402__auto__ = ((function (this__4399__auto____$1){
return (function (keyval__4403__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__4400__auto__,cljs.core.pr_writer,""," ","",opts__4401__auto__,keyval__4403__auto__);
});})(this__4399__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__4400__auto__,pr_pair__4402__auto__,"#taoensso.sente.ChAjaxSocket{",", ","}",opts__4401__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 11, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"client-id","client-id",-464622140),self__.client_id],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"chs","chs",376886120),self__.chs],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"params","params",710516235),self__.params],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"packer","packer",66077544),self__.packer],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"url","url",276297046),self__.url],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),self__.instance_handle_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),self__.ever_opened_QMARK__],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),self__.backoff_ms_fn],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109),self__.ajax_opts],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696),self__.curr_xhr_],null))], null),self__.__extmap));
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__18812){
var self__ = this;
var G__18812__$1 = this;
return (new cljs.core.RecordIter((0),G__18812__$1,11,new cljs.core.PersistentVector(null, 11, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"client-id","client-id",-464622140),new cljs.core.Keyword(null,"chs","chs",376886120),new cljs.core.Keyword(null,"params","params",710516235),new cljs.core.Keyword(null,"packer","packer",66077544),new cljs.core.Keyword(null,"url","url",276297046),new cljs.core.Keyword(null,"state_","state_",957667102),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109),new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__4383__auto__){
var self__ = this;
var this__4383__auto____$1 = this;
return self__.__meta;
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__4380__auto__){
var self__ = this;
var this__4380__auto____$1 = this;
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,self__.__hash));
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__4389__auto__){
var self__ = this;
var this__4389__auto____$1 = this;
return (11 + cljs.core.count.call(null,self__.__extmap));
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__4381__auto__){
var self__ = this;
var this__4381__auto____$1 = this;
var h__4243__auto__ = self__.__hash;
if((!((h__4243__auto__ == null)))){
return h__4243__auto__;
} else {
var h__4243__auto____$1 = ((function (h__4243__auto__,this__4381__auto____$1){
return (function (coll__4382__auto__){
return (-266770752 ^ cljs.core.hash_unordered_coll.call(null,coll__4382__auto__));
});})(h__4243__auto__,this__4381__auto____$1))
.call(null,this__4381__auto____$1);
self__.__hash = h__4243__auto____$1;

return h__4243__auto____$1;
}
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this18814,other18815){
var self__ = this;
var this18814__$1 = this;
return (((!((other18815 == null)))) && ((this18814__$1.constructor === other18815.constructor)) && (cljs.core._EQ_.call(null,this18814__$1.client_id,other18815.client_id)) && (cljs.core._EQ_.call(null,this18814__$1.chs,other18815.chs)) && (cljs.core._EQ_.call(null,this18814__$1.params,other18815.params)) && (cljs.core._EQ_.call(null,this18814__$1.packer,other18815.packer)) && (cljs.core._EQ_.call(null,this18814__$1.url,other18815.url)) && (cljs.core._EQ_.call(null,this18814__$1.state_,other18815.state_)) && (cljs.core._EQ_.call(null,this18814__$1.instance_handle_,other18815.instance_handle_)) && (cljs.core._EQ_.call(null,this18814__$1.ever_opened_QMARK__,other18815.ever_opened_QMARK__)) && (cljs.core._EQ_.call(null,this18814__$1.backoff_ms_fn,other18815.backoff_ms_fn)) && (cljs.core._EQ_.call(null,this18814__$1.ajax_opts,other18815.ajax_opts)) && (cljs.core._EQ_.call(null,this18814__$1.curr_xhr_,other18815.curr_xhr_)) && (cljs.core._EQ_.call(null,this18814__$1.__extmap,other18815.__extmap)));
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__4394__auto__,k__4395__auto__){
var self__ = this;
var this__4394__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 11, [new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696),null,new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),null,new cljs.core.Keyword(null,"client-id","client-id",-464622140),null,new cljs.core.Keyword(null,"packer","packer",66077544),null,new cljs.core.Keyword(null,"chs","chs",376886120),null,new cljs.core.Keyword(null,"params","params",710516235),null,new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),null,new cljs.core.Keyword(null,"url","url",276297046),null,new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),null,new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109),null,new cljs.core.Keyword(null,"state_","state_",957667102),null], null), null),k__4395__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__4394__auto____$1),self__.__meta),k__4395__auto__);
} else {
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__4395__auto__)),null));
}
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__4392__auto__,k__4393__auto__,G__18812){
var self__ = this;
var this__4392__auto____$1 = this;
var pred__18822 = cljs.core.keyword_identical_QMARK_;
var expr__18823 = k__4393__auto__;
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"client-id","client-id",-464622140),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(G__18812,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"chs","chs",376886120),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,G__18812,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"params","params",710516235),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,G__18812,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"packer","packer",66077544),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,G__18812,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"url","url",276297046),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,G__18812,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"state_","state_",957667102),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,G__18812,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,G__18812,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,G__18812,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,G__18812,self__.ajax_opts,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,G__18812,self__.curr_xhr_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18822.call(null,new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696),expr__18823))){
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,G__18812,self__.__meta,self__.__extmap,null));
} else {
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__4393__auto__,G__18812),null));
}
}
}
}
}
}
}
}
}
}
}
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__4397__auto__){
var self__ = this;
var this__4397__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 11, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"client-id","client-id",-464622140),self__.client_id,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"chs","chs",376886120),self__.chs,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"params","params",710516235),self__.params,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"packer","packer",66077544),self__.packer,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"url","url",276297046),self__.url,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),self__.instance_handle_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),self__.ever_opened_QMARK__,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),self__.backoff_ms_fn,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109),self__.ajax_opts,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696),self__.curr_xhr_,null))], null),self__.__extmap));
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__4384__auto__,G__18812){
var self__ = this;
var this__4384__auto____$1 = this;
return (new taoensso.sente.ChAjaxSocket(self__.client_id,self__.chs,self__.params,self__.packer,self__.url,self__.state_,self__.instance_handle_,self__.ever_opened_QMARK__,self__.backoff_ms_fn,self__.ajax_opts,self__.curr_xhr_,G__18812,self__.__extmap,self__.__hash));
});

taoensso.sente.ChAjaxSocket.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__4390__auto__,entry__4391__auto__){
var self__ = this;
var this__4390__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__4391__auto__)){
return this__4390__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__4391__auto__,(0)),cljs.core._nth.call(null,entry__4391__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__4390__auto____$1,entry__4391__auto__);
}
});

taoensso.sente.ChAjaxSocket.prototype.taoensso$sente$IChSocket$ = cljs.core.PROTOCOL_SENTINEL;

taoensso.sente.ChAjaxSocket.prototype.taoensso$sente$IChSocket$_chsk_disconnect_BANG_$arity$2 = (function (chsk,reason){
var self__ = this;
var chsk__$1 = this;
cljs.core.reset_BANG_.call(null,self__.instance_handle_,null);

taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (chsk__$1){
return (function (p1__18806_SHARP_){
return taoensso.sente.chsk_state__GT_closed.call(null,p1__18806_SHARP_,reason);
});})(chsk__$1))
);

var temp__5720__auto__ = cljs.core.deref.call(null,self__.curr_xhr_);
if(cljs.core.truth_(temp__5720__auto__)){
var x = temp__5720__auto__;
return x.abort();
} else {
return null;
}
});

taoensso.sente.ChAjaxSocket.prototype.taoensso$sente$IChSocket$_chsk_reconnect_BANG_$arity$1 = (function (chsk){
var self__ = this;
var chsk__$1 = this;
chsk__$1.taoensso$sente$IChSocket$_chsk_disconnect_BANG_$arity$2(null,new cljs.core.Keyword(null,"requested-reconnect","requested-reconnect",2008347707));

return chsk__$1.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1(null);
});

taoensso.sente.ChAjaxSocket.prototype.taoensso$sente$IChSocket$_chsk_send_BANG_$arity$3 = (function (chsk,ev,opts){
var self__ = this;
var chsk__$1 = this;
var map__18825 = opts;
var map__18825__$1 = (((((!((map__18825 == null))))?(((((map__18825.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18825.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18825):map__18825);
var _QMARK_timeout_ms = cljs.core.get.call(null,map__18825__$1,new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406));
var _QMARK_cb = cljs.core.get.call(null,map__18825__$1,new cljs.core.Keyword(null,"cb","cb",589947841));
var flush_QMARK_ = cljs.core.get.call(null,map__18825__$1,new cljs.core.Keyword(null,"flush?","flush?",-108887231));
var _ = taoensso.sente.assert_send_args.call(null,ev,_QMARK_timeout_ms,_QMARK_cb);
var _QMARK_cb_fn = taoensso.sente.cb_chan_as_fn.call(null,_QMARK_cb,ev);
if(cljs.core.not.call(null,new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,self__.state_)))){
return taoensso.sente.chsk_send__GT_closed_BANG_.call(null,_QMARK_cb_fn);
} else {
var csrf_token = new cljs.core.Keyword(null,"csrf-token","csrf-token",-1872302856).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,self__.state_));
taoensso.sente.ajax_lite.call(null,self__.url,cljs.core.merge.call(null,self__.ajax_opts,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"method","method",55703592),new cljs.core.Keyword(null,"post","post",269697687),new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406),(function (){var or__4131__auto__ = _QMARK_timeout_ms;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406).cljs$core$IFn$_invoke$arity$1(self__.ajax_opts);
if(cljs.core.truth_(or__4131__auto____$1)){
return or__4131__auto____$1;
} else {
return taoensso.sente.default_client_side_ajax_timeout_ms;
}
}
})(),new cljs.core.Keyword(null,"resp-type","resp-type",1050675962),new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.Keyword(null,"headers","headers",-835030129),cljs.core.merge.call(null,new cljs.core.Keyword(null,"headers","headers",-835030129).cljs$core$IFn$_invoke$arity$1(self__.ajax_opts),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"X-CSRF-Token","X-CSRF-Token",1562992453),csrf_token], null)),new cljs.core.Keyword(null,"params","params",710516235),(function (){var ppstr = taoensso.sente.pack.call(null,self__.packer,ev,(cljs.core.truth_(_QMARK_cb_fn)?new cljs.core.Keyword(null,"ajax-cb","ajax-cb",-807060321):null));
return cljs.core.merge.call(null,self__.params,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"udt","udt",2011712751),taoensso.encore.now_udt.call(null),new cljs.core.Keyword(null,"csrf-token","csrf-token",-1872302856),csrf_token,new cljs.core.Keyword(null,"client-id","client-id",-464622140),self__.client_id,new cljs.core.Keyword(null,"ppstr","ppstr",1557495252),ppstr], null));
})()], null)),((function (csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function taoensso$sente$ajax_cb(p__18827){
var map__18828 = p__18827;
var map__18828__$1 = (((((!((map__18828 == null))))?(((((map__18828.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18828.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18828):map__18828);
var _QMARK_error = cljs.core.get.call(null,map__18828__$1,new cljs.core.Keyword(null,"?error","?error",1070752222));
var _QMARK_content = cljs.core.get.call(null,map__18828__$1,new cljs.core.Keyword(null,"?content","?content",1697782054));
if(cljs.core.truth_(_QMARK_error)){
if(cljs.core._EQ_.call(null,_QMARK_error,new cljs.core.Keyword(null,"timeout","timeout",-318625318))){
if(cljs.core.truth_(_QMARK_cb_fn)){
return _QMARK_cb_fn.call(null,new cljs.core.Keyword("chsk","timeout","chsk/timeout",-319776489));
} else {
return null;
}
} else {
taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (map__18828,map__18828__$1,_QMARK_error,_QMARK_content,csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function (p1__18807_SHARP_){
return taoensso.sente.chsk_state__GT_closed.call(null,p1__18807_SHARP_,new cljs.core.Keyword(null,"unexpected","unexpected",-1137752424));
});})(map__18828,map__18828__$1,_QMARK_error,_QMARK_content,csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
);

if(cljs.core.truth_(_QMARK_cb_fn)){
return _QMARK_cb_fn.call(null,new cljs.core.Keyword("chsk","error","chsk/error",-984175439));
} else {
return null;
}
}
} else {
var content = _QMARK_content;
var resp_ppstr = content;
var vec__18830 = taoensso.sente.unpack.call(null,self__.packer,resp_ppstr);
var resp_clj = cljs.core.nth.call(null,vec__18830,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__18830,(1),null);
if(cljs.core.truth_(_QMARK_cb_fn)){
_QMARK_cb_fn.call(null,resp_clj);
} else {
if(cljs.core.not_EQ_.call(null,resp_clj,new cljs.core.Keyword("chsk","dummy-cb-200","chsk/dummy-cb-200",-1663130337))){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1203,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (content,resp_ppstr,vec__18830,resp_clj,___$1,map__18828,map__18828__$1,_QMARK_error,_QMARK_content,csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Cb reply w/o local cb-fn: %s",resp_clj], null);
});})(content,resp_ppstr,vec__18830,resp_clj,___$1,map__18828,map__18828__$1,_QMARK_error,_QMARK_content,csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
,null)),null,745733701);
} else {
}
}

return taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (content,resp_ppstr,vec__18830,resp_clj,___$1,map__18828,map__18828__$1,_QMARK_error,_QMARK_content,csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1){
return (function (p1__18808_SHARP_){
return cljs.core.assoc.call(null,p1__18808_SHARP_,new cljs.core.Keyword(null,"open?","open?",1238443125),true);
});})(content,resp_ppstr,vec__18830,resp_clj,___$1,map__18828,map__18828__$1,_QMARK_error,_QMARK_content,csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
);
}
});})(csrf_token,map__18825,map__18825__$1,_QMARK_timeout_ms,_QMARK_cb,flush_QMARK_,_,_QMARK_cb_fn,chsk__$1))
);

return new cljs.core.Keyword(null,"apparent-success","apparent-success",242592222);
}
});

taoensso.sente.ChAjaxSocket.prototype.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1 = (function (chsk){
var self__ = this;
var chsk__$1 = this;
var instance_handle = cljs.core.reset_BANG_.call(null,self__.instance_handle_,taoensso.encore.uuid_str.call(null));
var have_handle_QMARK_ = ((function (instance_handle,chsk__$1){
return (function (){
return cljs.core._EQ_.call(null,cljs.core.deref.call(null,self__.instance_handle_),instance_handle);
});})(instance_handle,chsk__$1))
;
var poll_fn = ((function (instance_handle,have_handle_QMARK_,chsk__$1){
return (function taoensso$sente$poll_fn(retry_count){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,1213,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (instance_handle,have_handle_QMARK_,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["async-poll-for-update!"], null);
});})(instance_handle,have_handle_QMARK_,chsk__$1))
,null)),null,501410951);

if(have_handle_QMARK_.call(null)){
var retry_fn = ((function (instance_handle,have_handle_QMARK_,chsk__$1){
return (function (){
if(have_handle_QMARK_.call(null)){
var retry_count_STAR_ = (retry_count + (1));
var backoff_ms = self__.backoff_ms_fn.call(null,retry_count_STAR_);
var udt_next_reconnect = (taoensso.encore.now_udt.call(null) + backoff_ms);
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1221,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Chsk is closed: will try reconnect attempt (%s) in %s ms",retry_count_STAR_,backoff_ms], null);
});})(retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,chsk__$1))
,null)),null,1343401102);

goog.global.setTimeout(((function (retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,chsk__$1){
return (function (){
return taoensso$sente$poll_fn.call(null,retry_count_STAR_);
});})(retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,chsk__$1))
,backoff_ms);

return taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,chsk__$1){
return (function (p1__18809_SHARP_){
return cljs.core.assoc.call(null,p1__18809_SHARP_,new cljs.core.Keyword(null,"udt-next-reconnect","udt-next-reconnect",-1990375733),udt_next_reconnect);
});})(retry_count_STAR_,backoff_ms,udt_next_reconnect,instance_handle,have_handle_QMARK_,chsk__$1))
);
} else {
return null;
}
});})(instance_handle,have_handle_QMARK_,chsk__$1))
;
return cljs.core.reset_BANG_.call(null,self__.curr_xhr_,taoensso.sente.ajax_lite.call(null,self__.url,cljs.core.merge.call(null,self__.ajax_opts,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"method","method",55703592),new cljs.core.Keyword(null,"get","get",1683182755),new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406),(function (){var or__4131__auto__ = new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406).cljs$core$IFn$_invoke$arity$1(self__.ajax_opts);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return taoensso.sente.default_client_side_ajax_timeout_ms;
}
})(),new cljs.core.Keyword(null,"resp-type","resp-type",1050675962),new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.Keyword(null,"params","params",710516235),cljs.core.merge.call(null,self__.params,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"udt","udt",2011712751),taoensso.encore.now_udt.call(null),new cljs.core.Keyword(null,"client-id","client-id",-464622140),self__.client_id], null),(cljs.core.truth_(new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,self__.state_)))?null:new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"handshake?","handshake?",-423743093),true], null)))], null)),((function (retry_fn,instance_handle,have_handle_QMARK_,chsk__$1){
return (function taoensso$sente$poll_fn_$_ajax_cb(p__18833){
var map__18834 = p__18833;
var map__18834__$1 = (((((!((map__18834 == null))))?(((((map__18834.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18834.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18834):map__18834);
var _QMARK_error = cljs.core.get.call(null,map__18834__$1,new cljs.core.Keyword(null,"?error","?error",1070752222));
var _QMARK_content = cljs.core.get.call(null,map__18834__$1,new cljs.core.Keyword(null,"?content","?content",1697782054));
if(cljs.core.truth_(_QMARK_error)){
if(cljs.core._EQ_.call(null,_QMARK_error,new cljs.core.Keyword(null,"timeout","timeout",-318625318))){
return taoensso$sente$poll_fn.call(null,(0));
} else {
taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (map__18834,map__18834__$1,_QMARK_error,_QMARK_content,retry_fn,instance_handle,have_handle_QMARK_,chsk__$1){
return (function (p1__18810_SHARP_){
return taoensso.sente.chsk_state__GT_closed.call(null,p1__18810_SHARP_,new cljs.core.Keyword(null,"unexpected","unexpected",-1137752424));
});})(map__18834,map__18834__$1,_QMARK_error,_QMARK_content,retry_fn,instance_handle,have_handle_QMARK_,chsk__$1))
);

return retry_fn.call(null);

}
} else {
var content = _QMARK_content;
var ppstr = content;
var vec__18836 = taoensso.sente.unpack.call(null,self__.packer,ppstr);
var clj = cljs.core.nth.call(null,vec__18836,(0),null);
var handshake_QMARK_ = taoensso.sente.handshake_QMARK_.call(null,clj);
if(handshake_QMARK_){
taoensso.sente.receive_handshake_BANG_.call(null,new cljs.core.Keyword(null,"ajax","ajax",814345549),chsk__$1,clj);
} else {
}

taoensso.sente.swap_chsk_state_BANG_.call(null,chsk__$1,((function (content,ppstr,vec__18836,clj,handshake_QMARK_,map__18834,map__18834__$1,_QMARK_error,_QMARK_content,retry_fn,instance_handle,have_handle_QMARK_,chsk__$1){
return (function (p1__18811_SHARP_){
return cljs.core.assoc.call(null,p1__18811_SHARP_,new cljs.core.Keyword(null,"open?","open?",1238443125),true);
});})(content,ppstr,vec__18836,clj,handshake_QMARK_,map__18834,map__18834__$1,_QMARK_error,_QMARK_content,retry_fn,instance_handle,have_handle_QMARK_,chsk__$1))
);

taoensso$sente$poll_fn.call(null,(0));

if(handshake_QMARK_){
return null;
} else {
var or__4131__auto__ = ((cljs.core._EQ_.call(null,clj,new cljs.core.Keyword("chsk","timeout","chsk/timeout",-319776489)))?(function (){
if(cljs.core.truth_(cljs.core.deref.call(null,taoensso.sente.debug_mode_QMARK__))){
taoensso.sente.receive_buffered_evs_BANG_.call(null,self__.chs,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("debug","timeout","debug/timeout",309499949)], null)], null));
} else {
}

return new cljs.core.Keyword(null,"noop","noop",-673731258);
})()
:null);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var buffered_evs = clj;
return taoensso.sente.receive_buffered_evs_BANG_.call(null,self__.chs,buffered_evs);
}
}
}
});})(retry_fn,instance_handle,have_handle_QMARK_,chsk__$1))
));
} else {
return null;
}
});})(instance_handle,have_handle_QMARK_,chsk__$1))
;
poll_fn.call(null,(0));

return chsk__$1;
});

taoensso.sente.ChAjaxSocket.getBasis = (function (){
return new cljs.core.PersistentVector(null, 11, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"client-id","client-id",1175909387,null),new cljs.core.Symbol(null,"chs","chs",2017417647,null),new cljs.core.Symbol(null,"params","params",-1943919534,null),new cljs.core.Symbol(null,"packer","packer",1706609071,null),new cljs.core.Symbol(null,"url","url",1916828573,null),new cljs.core.Symbol(null,"state_","state_",-1696768667,null),new cljs.core.Symbol(null,"instance-handle_","instance-handle_",-282852930,null),new cljs.core.Symbol(null,"ever-opened?_","ever-opened?_",-1013096856,null),new cljs.core.Symbol(null,"backoff-ms-fn","backoff-ms-fn",-1881539814,null),new cljs.core.Symbol(null,"ajax-opts","ajax-opts",1122292418,null),new cljs.core.Symbol(null,"curr-xhr_","curr-xhr_",321757831,null)], null);
});

taoensso.sente.ChAjaxSocket.cljs$lang$type = true;

taoensso.sente.ChAjaxSocket.cljs$lang$ctorPrSeq = (function (this__4428__auto__){
return (new cljs.core.List(null,"taoensso.sente/ChAjaxSocket",null,(1),null));
});

taoensso.sente.ChAjaxSocket.cljs$lang$ctorPrWriter = (function (this__4428__auto__,writer__4429__auto__){
return cljs.core._write.call(null,writer__4429__auto__,"taoensso.sente/ChAjaxSocket");
});

/**
 * Positional factory function for taoensso.sente/ChAjaxSocket.
 */
taoensso.sente.__GT_ChAjaxSocket = (function taoensso$sente$__GT_ChAjaxSocket(client_id,chs,params,packer,url,state_,instance_handle_,ever_opened_QMARK__,backoff_ms_fn,ajax_opts,curr_xhr_){
return (new taoensso.sente.ChAjaxSocket(client_id,chs,params,packer,url,state_,instance_handle_,ever_opened_QMARK__,backoff_ms_fn,ajax_opts,curr_xhr_,null,null,null));
});

/**
 * Factory function for taoensso.sente/ChAjaxSocket, taking a map of keywords to field values.
 */
taoensso.sente.map__GT_ChAjaxSocket = (function taoensso$sente$map__GT_ChAjaxSocket(G__18816){
var extmap__4424__auto__ = (function (){var G__18839 = cljs.core.dissoc.call(null,G__18816,new cljs.core.Keyword(null,"client-id","client-id",-464622140),new cljs.core.Keyword(null,"chs","chs",376886120),new cljs.core.Keyword(null,"params","params",710516235),new cljs.core.Keyword(null,"packer","packer",66077544),new cljs.core.Keyword(null,"url","url",276297046),new cljs.core.Keyword(null,"state_","state_",957667102),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109),new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696));
if(cljs.core.record_QMARK_.call(null,G__18816)){
return cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,G__18839);
} else {
return G__18839;
}
})();
return (new taoensso.sente.ChAjaxSocket(new cljs.core.Keyword(null,"client-id","client-id",-464622140).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"chs","chs",376886120).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"params","params",710516235).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"packer","packer",66077544).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"state_","state_",957667102).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109).cljs$core$IFn$_invoke$arity$1(G__18816),new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696).cljs$core$IFn$_invoke$arity$1(G__18816),null,cljs.core.not_empty.call(null,extmap__4424__auto__),null));
});

taoensso.sente.new_ChAjaxSocket = (function taoensso$sente$new_ChAjaxSocket(opts){
return taoensso.sente.map__GT_ChAjaxSocket.call(null,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"state_","state_",957667102),cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"ajax","ajax",814345549),new cljs.core.Keyword(null,"open?","open?",1238443125),false,new cljs.core.Keyword(null,"ever-opened?","ever-opened?",1128459732),false], null)),new cljs.core.Keyword(null,"instance-handle_","instance-handle_",-1923384457),cljs.core.atom.call(null,null),new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913),cljs.core.atom.call(null,false),new cljs.core.Keyword(null,"curr-xhr_","curr-xhr_",-1318773696),cljs.core.atom.call(null,null)], null),opts));
});

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IKVReduce}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {taoensso.sente.IChSocket}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
taoensso.sente.ChAutoSocket = (function (ws_chsk_opts,ajax_chsk_opts,state_,impl_,__meta,__extmap,__hash){
this.ws_chsk_opts = ws_chsk_opts;
this.ajax_chsk_opts = ajax_chsk_opts;
this.state_ = state_;
this.impl_ = impl_;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2230716170;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
taoensso.sente.ChAutoSocket.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__4385__auto__,k__4386__auto__){
var self__ = this;
var this__4385__auto____$1 = this;
return this__4385__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__4386__auto__,null);
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__4387__auto__,k18842,else__4388__auto__){
var self__ = this;
var this__4387__auto____$1 = this;
var G__18846 = k18842;
var G__18846__$1 = (((G__18846 instanceof cljs.core.Keyword))?G__18846.fqn:null);
switch (G__18846__$1) {
case "ws-chsk-opts":
return self__.ws_chsk_opts;

break;
case "ajax-chsk-opts":
return self__.ajax_chsk_opts;

break;
case "state_":
return self__.state_;

break;
case "impl_":
return self__.impl_;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k18842,else__4388__auto__);

}
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IKVReduce$_kv_reduce$arity$3 = (function (this__4404__auto__,f__4405__auto__,init__4406__auto__){
var self__ = this;
var this__4404__auto____$1 = this;
return cljs.core.reduce.call(null,((function (this__4404__auto____$1){
return (function (ret__4407__auto__,p__18847){
var vec__18848 = p__18847;
var k__4408__auto__ = cljs.core.nth.call(null,vec__18848,(0),null);
var v__4409__auto__ = cljs.core.nth.call(null,vec__18848,(1),null);
return f__4405__auto__.call(null,ret__4407__auto__,k__4408__auto__,v__4409__auto__);
});})(this__4404__auto____$1))
,init__4406__auto__,this__4404__auto____$1);
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__4399__auto__,writer__4400__auto__,opts__4401__auto__){
var self__ = this;
var this__4399__auto____$1 = this;
var pr_pair__4402__auto__ = ((function (this__4399__auto____$1){
return (function (keyval__4403__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__4400__auto__,cljs.core.pr_writer,""," ","",opts__4401__auto__,keyval__4403__auto__);
});})(this__4399__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__4400__auto__,pr_pair__4402__auto__,"#taoensso.sente.ChAutoSocket{",", ","}",opts__4401__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104),self__.ws_chsk_opts],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327),self__.ajax_chsk_opts],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"impl_","impl_",1218818179),self__.impl_],null))], null),self__.__extmap));
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__18841){
var self__ = this;
var G__18841__$1 = this;
return (new cljs.core.RecordIter((0),G__18841__$1,4,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104),new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327),new cljs.core.Keyword(null,"state_","state_",957667102),new cljs.core.Keyword(null,"impl_","impl_",1218818179)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__4383__auto__){
var self__ = this;
var this__4383__auto____$1 = this;
return self__.__meta;
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__4380__auto__){
var self__ = this;
var this__4380__auto____$1 = this;
return (new taoensso.sente.ChAutoSocket(self__.ws_chsk_opts,self__.ajax_chsk_opts,self__.state_,self__.impl_,self__.__meta,self__.__extmap,self__.__hash));
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__4389__auto__){
var self__ = this;
var this__4389__auto____$1 = this;
return (4 + cljs.core.count.call(null,self__.__extmap));
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__4381__auto__){
var self__ = this;
var this__4381__auto____$1 = this;
var h__4243__auto__ = self__.__hash;
if((!((h__4243__auto__ == null)))){
return h__4243__auto__;
} else {
var h__4243__auto____$1 = ((function (h__4243__auto__,this__4381__auto____$1){
return (function (coll__4382__auto__){
return (-1193508708 ^ cljs.core.hash_unordered_coll.call(null,coll__4382__auto__));
});})(h__4243__auto__,this__4381__auto____$1))
.call(null,this__4381__auto____$1);
self__.__hash = h__4243__auto____$1;

return h__4243__auto____$1;
}
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this18843,other18844){
var self__ = this;
var this18843__$1 = this;
return (((!((other18844 == null)))) && ((this18843__$1.constructor === other18844.constructor)) && (cljs.core._EQ_.call(null,this18843__$1.ws_chsk_opts,other18844.ws_chsk_opts)) && (cljs.core._EQ_.call(null,this18843__$1.ajax_chsk_opts,other18844.ajax_chsk_opts)) && (cljs.core._EQ_.call(null,this18843__$1.state_,other18844.state_)) && (cljs.core._EQ_.call(null,this18843__$1.impl_,other18844.impl_)) && (cljs.core._EQ_.call(null,this18843__$1.__extmap,other18844.__extmap)));
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__4394__auto__,k__4395__auto__){
var self__ = this;
var this__4394__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"impl_","impl_",1218818179),null,new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104),null,new cljs.core.Keyword(null,"state_","state_",957667102),null,new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327),null], null), null),k__4395__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__4394__auto____$1),self__.__meta),k__4395__auto__);
} else {
return (new taoensso.sente.ChAutoSocket(self__.ws_chsk_opts,self__.ajax_chsk_opts,self__.state_,self__.impl_,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__4395__auto__)),null));
}
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__4392__auto__,k__4393__auto__,G__18841){
var self__ = this;
var this__4392__auto____$1 = this;
var pred__18851 = cljs.core.keyword_identical_QMARK_;
var expr__18852 = k__4393__auto__;
if(cljs.core.truth_(pred__18851.call(null,new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104),expr__18852))){
return (new taoensso.sente.ChAutoSocket(G__18841,self__.ajax_chsk_opts,self__.state_,self__.impl_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18851.call(null,new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327),expr__18852))){
return (new taoensso.sente.ChAutoSocket(self__.ws_chsk_opts,G__18841,self__.state_,self__.impl_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18851.call(null,new cljs.core.Keyword(null,"state_","state_",957667102),expr__18852))){
return (new taoensso.sente.ChAutoSocket(self__.ws_chsk_opts,self__.ajax_chsk_opts,G__18841,self__.impl_,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__18851.call(null,new cljs.core.Keyword(null,"impl_","impl_",1218818179),expr__18852))){
return (new taoensso.sente.ChAutoSocket(self__.ws_chsk_opts,self__.ajax_chsk_opts,self__.state_,G__18841,self__.__meta,self__.__extmap,null));
} else {
return (new taoensso.sente.ChAutoSocket(self__.ws_chsk_opts,self__.ajax_chsk_opts,self__.state_,self__.impl_,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__4393__auto__,G__18841),null));
}
}
}
}
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__4397__auto__){
var self__ = this;
var this__4397__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.MapEntry(new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104),self__.ws_chsk_opts,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327),self__.ajax_chsk_opts,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_,null)),(new cljs.core.MapEntry(new cljs.core.Keyword(null,"impl_","impl_",1218818179),self__.impl_,null))], null),self__.__extmap));
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__4384__auto__,G__18841){
var self__ = this;
var this__4384__auto____$1 = this;
return (new taoensso.sente.ChAutoSocket(self__.ws_chsk_opts,self__.ajax_chsk_opts,self__.state_,self__.impl_,G__18841,self__.__extmap,self__.__hash));
});

taoensso.sente.ChAutoSocket.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__4390__auto__,entry__4391__auto__){
var self__ = this;
var this__4390__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__4391__auto__)){
return this__4390__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__4391__auto__,(0)),cljs.core._nth.call(null,entry__4391__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__4390__auto____$1,entry__4391__auto__);
}
});

taoensso.sente.ChAutoSocket.prototype.taoensso$sente$IChSocket$ = cljs.core.PROTOCOL_SENTINEL;

taoensso.sente.ChAutoSocket.prototype.taoensso$sente$IChSocket$_chsk_disconnect_BANG_$arity$2 = (function (chsk,reason){
var self__ = this;
var chsk__$1 = this;
var temp__5720__auto__ = cljs.core.deref.call(null,self__.impl_);
if(cljs.core.truth_(temp__5720__auto__)){
var impl = temp__5720__auto__;
return taoensso.sente._chsk_disconnect_BANG_.call(null,impl,reason);
} else {
return null;
}
});

taoensso.sente.ChAutoSocket.prototype.taoensso$sente$IChSocket$_chsk_reconnect_BANG_$arity$1 = (function (chsk){
var self__ = this;
var chsk__$1 = this;
var temp__5720__auto__ = cljs.core.deref.call(null,self__.impl_);
if(cljs.core.truth_(temp__5720__auto__)){
var impl = temp__5720__auto__;
taoensso.sente._chsk_disconnect_BANG_.call(null,impl,new cljs.core.Keyword(null,"requested-reconnect","requested-reconnect",2008347707));

return chsk__$1.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1(null);
} else {
return null;
}
});

taoensso.sente.ChAutoSocket.prototype.taoensso$sente$IChSocket$_chsk_send_BANG_$arity$3 = (function (chsk,ev,opts){
var self__ = this;
var chsk__$1 = this;
var temp__5718__auto__ = cljs.core.deref.call(null,self__.impl_);
if(cljs.core.truth_(temp__5718__auto__)){
var impl = temp__5718__auto__;
return taoensso.sente._chsk_send_BANG_.call(null,impl,ev,opts);
} else {
var map__18854 = opts;
var map__18854__$1 = (((((!((map__18854 == null))))?(((((map__18854.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18854.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18854):map__18854);
var _QMARK_cb = cljs.core.get.call(null,map__18854__$1,new cljs.core.Keyword(null,"cb","cb",589947841));
var _QMARK_cb_fn = taoensso.sente.cb_chan_as_fn.call(null,_QMARK_cb,ev);
return taoensso.sente.chsk_send__GT_closed_BANG_.call(null,_QMARK_cb_fn);
}
});

taoensso.sente.ChAutoSocket.prototype.taoensso$sente$IChSocket$_chsk_connect_BANG_$arity$1 = (function (chsk){
var self__ = this;
var chsk__$1 = this;
var ajax_chsk_opts__$1 = cljs.core.assoc.call(null,self__.ajax_chsk_opts,new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_);
var ws_chsk_opts__$1 = cljs.core.assoc.call(null,self__.ws_chsk_opts,new cljs.core.Keyword(null,"state_","state_",957667102),self__.state_);
var ajax_conn_BANG_ = ((function (ajax_chsk_opts__$1,ws_chsk_opts__$1,chsk__$1){
return (function (){
cljs.core.remove_watch.call(null,self__.state_,new cljs.core.Keyword("chsk","auto-ajax-downgrade","chsk/auto-ajax-downgrade",-831528080));

return taoensso.sente._chsk_connect_BANG_.call(null,taoensso.sente.new_ChAjaxSocket.call(null,ajax_chsk_opts__$1));
});})(ajax_chsk_opts__$1,ws_chsk_opts__$1,chsk__$1))
;
var ws_conn_BANG_ = ((function (ajax_chsk_opts__$1,ws_chsk_opts__$1,ajax_conn_BANG_,chsk__$1){
return (function (){
var downgraded_QMARK___18858 = cljs.core.atom.call(null,false);
cljs.core.add_watch.call(null,self__.state_,new cljs.core.Keyword("chsk","auto-ajax-downgrade","chsk/auto-ajax-downgrade",-831528080),((function (downgraded_QMARK___18858,ajax_chsk_opts__$1,ws_chsk_opts__$1,ajax_conn_BANG_,chsk__$1){
return (function (_,___$1,old_state,new_state){
var temp__5720__auto__ = cljs.core.deref.call(null,self__.impl_);
if(cljs.core.truth_(temp__5720__auto__)){
var impl = temp__5720__auto__;
var temp__5720__auto____$1 = new cljs.core.Keyword(null,"ever-opened?_","ever-opened?_",1641338913).cljs$core$IFn$_invoke$arity$1(impl);
if(cljs.core.truth_(temp__5720__auto____$1)){
var ever_opened_QMARK__ = temp__5720__auto____$1;
if(cljs.core.truth_(cljs.core.deref.call(null,ever_opened_QMARK__))){
return null;
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"last-ws-error","last-ws-error",-820288502).cljs$core$IFn$_invoke$arity$1(new_state))){
if(cljs.core.compare_and_set_BANG_.call(null,downgraded_QMARK___18858,false,true)){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1349,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (ever_opened_QMARK__,temp__5720__auto____$1,impl,temp__5720__auto__,downgraded_QMARK___18858,ajax_chsk_opts__$1,ws_chsk_opts__$1,ajax_conn_BANG_,chsk__$1){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Permanently downgrading :auto chsk -> :ajax"], null);
});})(ever_opened_QMARK__,temp__5720__auto____$1,impl,temp__5720__auto__,downgraded_QMARK___18858,ajax_chsk_opts__$1,ws_chsk_opts__$1,ajax_conn_BANG_,chsk__$1))
,null)),null,-1288939954);

taoensso.sente._chsk_disconnect_BANG_.call(null,impl,new cljs.core.Keyword(null,"downgrading-ws-to-ajax","downgrading-ws-to-ajax",402136720));

return cljs.core.reset_BANG_.call(null,self__.impl_,ajax_conn_BANG_.call(null));
} else {
return null;
}
} else {
return null;
}
}
} else {
return null;
}
} else {
return null;
}
});})(downgraded_QMARK___18858,ajax_chsk_opts__$1,ws_chsk_opts__$1,ajax_conn_BANG_,chsk__$1))
);

return taoensso.sente._chsk_connect_BANG_.call(null,taoensso.sente.new_ChWebSocket.call(null,ws_chsk_opts__$1));
});})(ajax_chsk_opts__$1,ws_chsk_opts__$1,ajax_conn_BANG_,chsk__$1))
;
cljs.core.reset_BANG_.call(null,self__.impl_,(function (){var or__4131__auto__ = ws_conn_BANG_.call(null);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return ajax_conn_BANG_.call(null);
}
})());

return chsk__$1;
});

taoensso.sente.ChAutoSocket.getBasis = (function (){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ws-chsk-opts","ws-chsk-opts",-349638577,null),new cljs.core.Symbol(null,"ajax-chsk-opts","ajax-chsk-opts",-1051844442,null),new cljs.core.Symbol(null,"state_","state_",-1696768667,null),new cljs.core.Symbol(null,"impl_","impl_",-1435617590,null)], null);
});

taoensso.sente.ChAutoSocket.cljs$lang$type = true;

taoensso.sente.ChAutoSocket.cljs$lang$ctorPrSeq = (function (this__4428__auto__){
return (new cljs.core.List(null,"taoensso.sente/ChAutoSocket",null,(1),null));
});

taoensso.sente.ChAutoSocket.cljs$lang$ctorPrWriter = (function (this__4428__auto__,writer__4429__auto__){
return cljs.core._write.call(null,writer__4429__auto__,"taoensso.sente/ChAutoSocket");
});

/**
 * Positional factory function for taoensso.sente/ChAutoSocket.
 */
taoensso.sente.__GT_ChAutoSocket = (function taoensso$sente$__GT_ChAutoSocket(ws_chsk_opts,ajax_chsk_opts,state_,impl_){
return (new taoensso.sente.ChAutoSocket(ws_chsk_opts,ajax_chsk_opts,state_,impl_,null,null,null));
});

/**
 * Factory function for taoensso.sente/ChAutoSocket, taking a map of keywords to field values.
 */
taoensso.sente.map__GT_ChAutoSocket = (function taoensso$sente$map__GT_ChAutoSocket(G__18845){
var extmap__4424__auto__ = (function (){var G__18856 = cljs.core.dissoc.call(null,G__18845,new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104),new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327),new cljs.core.Keyword(null,"state_","state_",957667102),new cljs.core.Keyword(null,"impl_","impl_",1218818179));
if(cljs.core.record_QMARK_.call(null,G__18845)){
return cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,G__18856);
} else {
return G__18856;
}
})();
return (new taoensso.sente.ChAutoSocket(new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104).cljs$core$IFn$_invoke$arity$1(G__18845),new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327).cljs$core$IFn$_invoke$arity$1(G__18845),new cljs.core.Keyword(null,"state_","state_",957667102).cljs$core$IFn$_invoke$arity$1(G__18845),new cljs.core.Keyword(null,"impl_","impl_",1218818179).cljs$core$IFn$_invoke$arity$1(G__18845),null,cljs.core.not_empty.call(null,extmap__4424__auto__),null));
});

taoensso.sente.new_ChAutoSocket = (function taoensso$sente$new_ChAutoSocket(opts){
return taoensso.sente.map__GT_ChAutoSocket.call(null,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"state_","state_",957667102),cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"auto","auto",-566279492),new cljs.core.Keyword(null,"open?","open?",1238443125),false,new cljs.core.Keyword(null,"ever-opened?","ever-opened?",1128459732),false], null)),new cljs.core.Keyword(null,"impl_","impl_",1218818179),cljs.core.atom.call(null,null)], null),opts));
});
taoensso.sente.get_chsk_url = (function taoensso$sente$get_chsk_url(protocol,host,path,type){
var protocol__$1 = (function (){var G__18859 = protocol;
var G__18859__$1 = (((G__18859 instanceof cljs.core.Keyword))?G__18859.fqn:null);
switch (G__18859__$1) {
case "http":
return "http:";

break;
case "https":
return "https:";

break;
default:
return protocol;

}
})();
var protocol__$2 = (function (){var e = (function (){try{if(((function (protocol__$1){
return (function (x){
return cljs.core.contains_QMARK_.call(null,taoensso.truss.impl.set_STAR_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["https:",null,"http:",null], null), null)),x);
});})(protocol__$1))
.call(null,protocol__$1)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18860){if((e18860 instanceof Error)){
var e = e18860;
return e;
} else {
throw e18860;

}
}})();
if((e == null)){
return protocol__$1;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",1369,"([:el #{\"https:\" \"http:\"}] protocol)",protocol__$1,e,null);
}
})();
var protocol__$3 = (function (){var G__18861 = type;
var G__18861__$1 = (((G__18861 instanceof cljs.core.Keyword))?G__18861.fqn:null);
switch (G__18861__$1) {
case "ajax":
return protocol__$2;

break;
case "ws":
var G__18862 = protocol__$2;
switch (G__18862) {
case "https:":
return "wss:";

break;
case "http:":
return "ws:";

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18862)].join('')));

}

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18861__$1)].join('')));

}
})();
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(protocol__$3),"//",taoensso.encore.path.call(null,host,path)].join('');
});
/**
 * Returns nil on failure, or a map with keys:
 *     :ch-recv ; core.async channel to receive `event-msg`s (internal or from
 *              ; clients). May `put!` (inject) arbitrary `event`s to this channel.
 *     :send-fn ; (fn [event & [?timeout-ms ?cb-fn]]) for client>server send.
 *     :state   ; Watchable, read-only (atom {:type _ :open? _ :uid _ :csrf-token _}).
 *     :chsk    ; IChSocket implementer. You can usu. ignore this.
 * 
 *   Common options:
 *     :type           ; e/o #{:auto :ws :ajax}. You'll usually want the default (:auto).
 *     :protocol       ; Server protocol, e/o #{:http :https}.
 *     :host           ; Server host (defaults to current page's host).
 *     :params         ; Map of any params to incl. in chsk Ring requests (handy
 *                     ; for application-level auth, etc.).
 *     :packer         ; :edn (default), or an IPacker implementation.
 *     :ajax-opts      ; Base opts map provided to `taoensso.encore/ajax-lite`.
 *     :wrap-recv-evs? ; Should events from server be wrapped in [:chsk/recv _]?
 *     :ws-kalive-ms   ; Ping to keep a WebSocket conn alive if no activity
 *                     ; w/in given msecs. Should be different to server's :ws-kalive-ms.
 */
taoensso.sente.make_channel_socket_client_BANG_ = (function taoensso$sente$make_channel_socket_client_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___18883 = arguments.length;
var i__4731__auto___18884 = (0);
while(true){
if((i__4731__auto___18884 < len__4730__auto___18883)){
args__4736__auto__.push((arguments[i__4731__auto___18884]));

var G__18885 = (i__4731__auto___18884 + (1));
i__4731__auto___18884 = G__18885;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return taoensso.sente.make_channel_socket_client_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

taoensso.sente.make_channel_socket_client_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (path,p__18868){
var vec__18869 = p__18868;
var map__18872 = cljs.core.nth.call(null,vec__18869,(0),null);
var map__18872__$1 = (((((!((map__18872 == null))))?(((((map__18872.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18872.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18872):map__18872);
var opts = map__18872__$1;
var ajax_opts = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109));
var ws_kalive_ms = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),taoensso.encore.ms.call(null,new cljs.core.Keyword(null,"secs","secs",1532330091),(20)));
var client_id = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"client-id","client-id",-464622140),(function (){var or__4131__auto__ = new cljs.core.Keyword(null,"client-uuid","client-uuid",-1717531965).cljs$core$IFn$_invoke$arity$1(opts);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return taoensso.encore.uuid_str.call(null);
}
})());
var protocol = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"protocol","protocol",652470118));
var packer = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"packer","packer",66077544),new cljs.core.Keyword(null,"edn","edn",1317840885));
var params = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"params","params",710516235));
var type = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"auto","auto",-566279492));
var host = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"host","host",-1558485167));
var recv_buf_or_n = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"recv-buf-or-n","recv-buf-or-n",1363950355),cljs.core.async.sliding_buffer.call(null,(2048)));
var backoff_ms_fn = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),taoensso.encore.exp_backoff);
var wrap_recv_evs_QMARK_ = cljs.core.get.call(null,map__18872__$1,new cljs.core.Keyword(null,"wrap-recv-evs?","wrap-recv-evs?",-1996694153),true);
var _deprecated_more_opts = cljs.core.nth.call(null,vec__18869,(1),null);
var e_18886 = (function (){try{if(((function (vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts){
return (function (x){
return cljs.core.contains_QMARK_.call(null,taoensso.truss.impl.set_STAR_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ws","ws",86841443),null,new cljs.core.Keyword(null,"ajax","ajax",814345549),null,new cljs.core.Keyword(null,"auto","auto",-566279492),null], null), null)),x);
});})(vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts))
.call(null,type)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18874){if((e18874 instanceof Error)){
var e = e18874;
return e;
} else {
throw e18874;

}
}})();
if((e_18886 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",1411,"([:in #{:ws :ajax :auto}] type)",type,e_18886,null);
}

var e_18887 = (function (){try{if(taoensso.encore.nblank_str_QMARK_.call(null,client_id)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18875){if((e18875 instanceof Error)){
var e = e18875;
return e;
} else {
throw e18875;

}
}})();
if((e_18887 == null)){
} else {
taoensso.truss.impl._invar_violation_BANG_.call(null,true,"taoensso.sente",1412,"(enc/nblank-str? client-id)",client_id,e_18887,null);
}

if((!((_deprecated_more_opts == null)))){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1414,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["`make-channel-socket-client!` fn signature CHANGED with Sente v0.10.0."], null);
});})(vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts))
,null)),null,1925554679);
} else {
}

if(cljs.core.contains_QMARK_.call(null,opts,new cljs.core.Keyword(null,"lp-timeout","lp-timeout",1149461302))){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1415,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [":lp-timeout opt has CHANGED; please use :lp-timout-ms."], null);
});})(vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts))
,null)),null,1930734559);
} else {
}

var packer__$1 = taoensso.sente.coerce_packer.call(null,packer);
var vec__18876 = (function (){var win_loc = taoensso.encore.get_win_loc.call(null);
var path__$1 = (function (){var or__4131__auto__ = path;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Keyword(null,"pathname","pathname",-1420497528).cljs$core$IFn$_invoke$arity$1(win_loc);
}
})();
var temp__5718__auto__ = new cljs.core.Keyword(null,"chsk-url-fn","chsk-url-fn",1968894294).cljs$core$IFn$_invoke$arity$1(opts);
if(cljs.core.truth_(temp__5718__auto__)){
var f = temp__5718__auto__;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [f.call(null,path__$1,win_loc,new cljs.core.Keyword(null,"ws","ws",86841443)),f.call(null,path__$1,win_loc,new cljs.core.Keyword(null,"ajax","ajax",814345549))], null);
} else {
var protocol__$1 = (function (){var or__4131__auto__ = protocol;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
var or__4131__auto____$1 = new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(win_loc);
if(cljs.core.truth_(or__4131__auto____$1)){
return or__4131__auto____$1;
} else {
return new cljs.core.Keyword(null,"http","http",382524695);
}
}
})();
var host__$1 = (function (){var or__4131__auto__ = host;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Keyword(null,"host","host",-1558485167).cljs$core$IFn$_invoke$arity$1(win_loc);
}
})();
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [taoensso.sente.get_chsk_url.call(null,protocol__$1,host__$1,path__$1,new cljs.core.Keyword(null,"ws","ws",86841443)),taoensso.sente.get_chsk_url.call(null,protocol__$1,host__$1,path__$1,new cljs.core.Keyword(null,"ajax","ajax",814345549))], null);
}
})();
var ws_url = cljs.core.nth.call(null,vec__18876,(0),null);
var ajax_url = cljs.core.nth.call(null,vec__18876,(1),null);
var private_chs = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"internal","internal",-854870097),cljs.core.async.chan.call(null,cljs.core.async.sliding_buffer.call(null,(128))),new cljs.core.Keyword(null,"state","state",-1988618099),cljs.core.async.chan.call(null,cljs.core.async.sliding_buffer.call(null,(10))),new cljs.core.Keyword(null,"<server","<server",-2135373537),(function (){var buf = cljs.core.async.sliding_buffer.call(null,(512));
if(cljs.core.truth_(wrap_recv_evs_QMARK_)){
return cljs.core.async.chan.call(null,buf,cljs.core.map.call(null,((function (buf,packer__$1,vec__18876,ws_url,ajax_url,vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts){
return (function (ev){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("chsk","recv","chsk/recv",561097091),ev], null);
});})(buf,packer__$1,vec__18876,ws_url,ajax_url,vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts))
));
} else {
return cljs.core.async.chan.call(null,buf);
}
})()], null);
var common_chsk_opts = new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"client-id","client-id",-464622140),client_id,new cljs.core.Keyword(null,"chs","chs",376886120),private_chs,new cljs.core.Keyword(null,"params","params",710516235),params,new cljs.core.Keyword(null,"packer","packer",66077544),packer__$1,new cljs.core.Keyword(null,"ws-kalive-ms","ws-kalive-ms",1442179968),ws_kalive_ms], null);
var ws_chsk_opts = cljs.core.merge.call(null,common_chsk_opts,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"url","url",276297046),ws_url,new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),backoff_ms_fn], null));
var ajax_chsk_opts = cljs.core.merge.call(null,common_chsk_opts,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"url","url",276297046),ajax_url,new cljs.core.Keyword(null,"ajax-opts","ajax-opts",-518239109),ajax_opts,new cljs.core.Keyword(null,"backoff-ms-fn","backoff-ms-fn",772895955),backoff_ms_fn], null));
var auto_chsk_opts = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"ws-chsk-opts","ws-chsk-opts",-1990170104),ws_chsk_opts,new cljs.core.Keyword(null,"ajax-chsk-opts","ajax-chsk-opts",1602591327),ajax_chsk_opts], null);
var _QMARK_chsk = taoensso.sente._chsk_connect_BANG_.call(null,(function (){var G__18879 = type;
var G__18879__$1 = (((G__18879 instanceof cljs.core.Keyword))?G__18879.fqn:null);
switch (G__18879__$1) {
case "ws":
return taoensso.sente.new_ChWebSocket.call(null,ws_chsk_opts);

break;
case "ajax":
return taoensso.sente.new_ChAjaxSocket.call(null,ajax_chsk_opts);

break;
case "auto":
return taoensso.sente.new_ChAutoSocket.call(null,auto_chsk_opts);

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__18879__$1)].join('')));

}
})());
var temp__5718__auto__ = _QMARK_chsk;
if(cljs.core.truth_(temp__5718__auto__)){
var chsk = temp__5718__auto__;
var chsk_state_ = new cljs.core.Keyword(null,"state_","state_",957667102).cljs$core$IFn$_invoke$arity$1(chsk);
var internal_ch = new cljs.core.Keyword(null,"internal","internal",-854870097).cljs$core$IFn$_invoke$arity$1(private_chs);
var send_fn = cljs.core.partial.call(null,taoensso.sente.chsk_send_BANG_,chsk);
var ev_ch = cljs.core.async.merge.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"internal","internal",-854870097).cljs$core$IFn$_invoke$arity$1(private_chs),new cljs.core.Keyword(null,"state","state",-1988618099).cljs$core$IFn$_invoke$arity$1(private_chs),new cljs.core.Keyword(null,"<server","<server",-2135373537).cljs$core$IFn$_invoke$arity$1(private_chs)], null),recv_buf_or_n);
var ev_msg_ch = cljs.core.async.chan.call(null,(1),cljs.core.map.call(null,((function (chsk_state_,internal_ch,send_fn,ev_ch,chsk,temp__5718__auto__,packer__$1,vec__18876,ws_url,ajax_url,private_chs,common_chsk_opts,ws_chsk_opts,ajax_chsk_opts,auto_chsk_opts,_QMARK_chsk,vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts){
return (function (ev){
var vec__18880 = taoensso.sente.as_event.call(null,ev);
var ev_id = cljs.core.nth.call(null,vec__18880,(0),null);
var ev__QMARK_data = cljs.core.nth.call(null,vec__18880,(1),null);
var ev__$1 = vec__18880;
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861),internal_ch,new cljs.core.Keyword(null,"send-fn","send-fn",351002041),send_fn,new cljs.core.Keyword(null,"state","state",-1988618099),chsk_state_,new cljs.core.Keyword(null,"event","event",301435442),ev__$1,new cljs.core.Keyword(null,"id","id",-1388402092),ev_id,new cljs.core.Keyword(null,"?data","?data",-9471433),ev__QMARK_data], null);
});})(chsk_state_,internal_ch,send_fn,ev_ch,chsk,temp__5718__auto__,packer__$1,vec__18876,ws_url,ajax_url,private_chs,common_chsk_opts,ws_chsk_opts,ajax_chsk_opts,auto_chsk_opts,_QMARK_chsk,vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts))
));
cljs.core.async.pipe.call(null,ev_ch,ev_msg_ch);

return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"chsk","chsk",-863703081),chsk,new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861),ev_msg_ch,new cljs.core.Keyword(null,"send-fn","send-fn",351002041),send_fn,new cljs.core.Keyword(null,"state","state",-1988618099),new cljs.core.Keyword(null,"state_","state_",957667102).cljs$core$IFn$_invoke$arity$1(chsk)], null);
} else {
return taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"warn","warn",-436710552),"taoensso.sente",null,1503,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (temp__5718__auto__,packer__$1,vec__18876,ws_url,ajax_url,private_chs,common_chsk_opts,ws_chsk_opts,ajax_chsk_opts,auto_chsk_opts,_QMARK_chsk,vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts){
return (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Failed to create channel socket"], null);
});})(temp__5718__auto__,packer__$1,vec__18876,ws_url,ajax_url,private_chs,common_chsk_opts,ws_chsk_opts,ajax_chsk_opts,auto_chsk_opts,_QMARK_chsk,vec__18869,map__18872,map__18872__$1,opts,ajax_opts,ws_kalive_ms,client_id,protocol,packer,params,type,host,recv_buf_or_n,backoff_ms_fn,wrap_recv_evs_QMARK_,_deprecated_more_opts))
,null)),null,-2002106676);
}
});

taoensso.sente.make_channel_socket_client_BANG_.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
taoensso.sente.make_channel_socket_client_BANG_.cljs$lang$applyTo = (function (seq18866){
var G__18867 = cljs.core.first.call(null,seq18866);
var seq18866__$1 = cljs.core.next.call(null,seq18866);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__18867,seq18866__$1);
});

taoensso.sente._start_chsk_router_BANG_ = (function taoensso$sente$_start_chsk_router_BANG_(server_QMARK_,ch_recv,event_msg_handler,opts){
var map__18889 = opts;
var map__18889__$1 = (((((!((map__18889 == null))))?(((((map__18889.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18889.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18889):map__18889);
var trace_evs_QMARK_ = cljs.core.get.call(null,map__18889__$1,new cljs.core.Keyword(null,"trace-evs?","trace-evs?",1502453512));
var error_handler = cljs.core.get.call(null,map__18889__$1,new cljs.core.Keyword(null,"error-handler","error-handler",-484945776));
var simple_auto_threading_QMARK_ = cljs.core.get.call(null,map__18889__$1,new cljs.core.Keyword(null,"simple-auto-threading?","simple-auto-threading?",1950754184));
var ch_ctrl = cljs.core.async.chan.call(null);
var execute1 = ((function (map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl){
return (function (f){
return f.call(null);
});})(map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl))
;
var c__8790__auto___18968 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function (state_18938){
var state_val_18939 = (state_18938[(1)]);
if((state_val_18939 === (7))){
var inst_18934 = (state_18938[(2)]);
var state_18938__$1 = state_18938;
var statearr_18940_18969 = state_18938__$1;
(statearr_18940_18969[(2)] = inst_18934);

(statearr_18940_18969[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (1))){
var state_18938__$1 = state_18938;
var statearr_18941_18970 = state_18938__$1;
(statearr_18941_18970[(2)] = null);

(statearr_18941_18970[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (4))){
var inst_18900 = (state_18938[(7)]);
var inst_18899 = (state_18938[(8)]);
var inst_18901 = (state_18938[(9)]);
var inst_18904 = (state_18938[(10)]);
var inst_18899__$1 = (state_18938[(2)]);
var inst_18900__$1 = cljs.core.nth.call(null,inst_18899__$1,(0),null);
var inst_18901__$1 = cljs.core.nth.call(null,inst_18899__$1,(1),null);
var inst_18902 = cljs.core._EQ_.call(null,inst_18901__$1,ch_ctrl);
var inst_18903 = (inst_18900__$1 == null);
var inst_18904__$1 = ((inst_18902) || (inst_18903));
var state_18938__$1 = (function (){var statearr_18942 = state_18938;
(statearr_18942[(7)] = inst_18900__$1);

(statearr_18942[(8)] = inst_18899__$1);

(statearr_18942[(9)] = inst_18901__$1);

(statearr_18942[(10)] = inst_18904__$1);

return statearr_18942;
})();
if(cljs.core.truth_(inst_18904__$1)){
var statearr_18943_18971 = state_18938__$1;
(statearr_18943_18971[(1)] = (5));

} else {
var statearr_18944_18972 = state_18938__$1;
(statearr_18944_18972[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (15))){
var inst_18900 = (state_18938[(7)]);
var state_18938__$1 = state_18938;
var statearr_18945_18973 = state_18938__$1;
(statearr_18945_18973[(2)] = inst_18900);

(statearr_18945_18973[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (13))){
var inst_18920 = (state_18938[(2)]);
var state_18938__$1 = state_18938;
var statearr_18946_18974 = state_18938__$1;
(statearr_18946_18974[(2)] = inst_18920);

(statearr_18946_18974[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (6))){
var inst_18900 = (state_18938[(7)]);
var inst_18909 = (inst_18900 == null);
var inst_18910 = cljs.core.not.call(null,inst_18909);
var state_18938__$1 = state_18938;
if(inst_18910){
var statearr_18947_18975 = state_18938__$1;
(statearr_18947_18975[(1)] = (8));

} else {
var statearr_18948_18976 = state_18938__$1;
(statearr_18948_18976[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (3))){
var inst_18936 = (state_18938[(2)]);
var state_18938__$1 = state_18938;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_18938__$1,inst_18936);
} else {
if((state_val_18939 === (12))){
var state_18938__$1 = state_18938;
var statearr_18949_18977 = state_18938__$1;
(statearr_18949_18977[(2)] = false);

(statearr_18949_18977[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (2))){
var inst_18895 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_18896 = [ch_recv,ch_ctrl];
var inst_18897 = (new cljs.core.PersistentVector(null,2,(5),inst_18895,inst_18896,null));
var state_18938__$1 = state_18938;
return cljs.core.async.ioc_alts_BANG_.call(null,state_18938__$1,(4),inst_18897);
} else {
if((state_val_18939 === (11))){
var state_18938__$1 = state_18938;
var statearr_18950_18978 = state_18938__$1;
(statearr_18950_18978[(2)] = true);

(statearr_18950_18978[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (9))){
var state_18938__$1 = state_18938;
var statearr_18951_18979 = state_18938__$1;
(statearr_18951_18979[(2)] = false);

(statearr_18951_18979[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (5))){
var state_18938__$1 = state_18938;
var statearr_18952_18980 = state_18938__$1;
(statearr_18952_18980[(2)] = null);

(statearr_18952_18980[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (14))){
var inst_18900 = (state_18938[(7)]);
var inst_18925 = cljs.core.apply.call(null,cljs.core.hash_map,inst_18900);
var state_18938__$1 = state_18938;
var statearr_18953_18981 = state_18938__$1;
(statearr_18953_18981[(2)] = inst_18925);

(statearr_18953_18981[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (16))){
var inst_18900 = (state_18938[(7)]);
var inst_18899 = (state_18938[(8)]);
var inst_18901 = (state_18938[(9)]);
var inst_18904 = (state_18938[(10)]);
var inst_18928 = (state_18938[(2)]);
var inst_18929 = cljs.core.get.call(null,inst_18928,new cljs.core.Keyword(null,"event","event",301435442));
var inst_18930 = (function (){var vec__18892 = inst_18899;
var v = inst_18900;
var p = inst_18901;
var stop_QMARK_ = inst_18904;
var map__18907 = inst_18928;
var event_msg = inst_18928;
var event = inst_18929;
return ((function (vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function (){
try{if(cljs.core.truth_(trace_evs_QMARK_)){
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"trace","trace",-1082747415),"taoensso.sente",null,1530,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Pre-handler event: %s",event], null);
});})(vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
,null)),null,627811680);
} else {
}

return event_msg_handler.call(null,(cljs.core.truth_(server_QMARK_)?(function (){var e = (function (){try{if(taoensso.sente.server_event_msg_QMARK_.call(null,event_msg)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18956){if((e18956 instanceof Error)){
var e = e18956;
return e;
} else {
throw e18956;

}
}})();
if((e == null)){
return event_msg;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,null,"taoensso.sente",1533,"(server-event-msg? event-msg)",event_msg,e,null);
}
})():(function (){var e = (function (){try{if(taoensso.sente.client_event_msg_QMARK_.call(null,event_msg)){
return null;
} else {
return taoensso.truss.impl._dummy_error;
}
}catch (e18957){if((e18957 instanceof Error)){
var e = e18957;
return e;
} else {
throw e18957;

}
}})();
if((e == null)){
return event_msg;
} else {
return taoensso.truss.impl._invar_violation_BANG_.call(null,null,"taoensso.sente",1534,"(client-event-msg? event-msg)",event_msg,e,null);
}
})()));
}catch (e18954){if((e18954 instanceof Error)){
var e1 = e18954;
try{var temp__5718__auto__ = error_handler;
if(cljs.core.truth_(temp__5718__auto__)){
var eh = temp__5718__auto__;
return error_handler.call(null,e1,event_msg);
} else {
return taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"error","error",-978969032),"taoensso.sente",null,1539,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (temp__5718__auto__,e1,vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [e1,"Chsk router `event-msg-handler` error: %s",event], null);
});})(temp__5718__auto__,e1,vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
,null)),null,380378065);
}
}catch (e18955){if((e18955 instanceof Error)){
var e2 = e18955;
return taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"error","error",-978969032),"taoensso.sente",null,1540,new cljs.core.Keyword(null,"f","f",-1597136552),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (e2,e1,vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [e2,"Chsk router `error-handler` error: %s",event], null);
});})(e2,e1,vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
,null)),null,2088597668);
} else {
throw e18955;

}
}} else {
throw e18954;

}
}});
;})(vec__18892,v,p,stop_QMARK_,map__18907,event_msg,event,inst_18900,inst_18899,inst_18901,inst_18904,inst_18928,inst_18929,state_val_18939,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
})();
var inst_18931 = execute1.call(null,inst_18930);
var state_18938__$1 = (function (){var statearr_18958 = state_18938;
(statearr_18958[(11)] = inst_18931);

return statearr_18958;
})();
var statearr_18959_18982 = state_18938__$1;
(statearr_18959_18982[(2)] = null);

(statearr_18959_18982[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (10))){
var inst_18923 = (state_18938[(2)]);
var state_18938__$1 = state_18938;
if(cljs.core.truth_(inst_18923)){
var statearr_18960_18983 = state_18938__$1;
(statearr_18960_18983[(1)] = (14));

} else {
var statearr_18961_18984 = state_18938__$1;
(statearr_18961_18984[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_18939 === (8))){
var inst_18900 = (state_18938[(7)]);
var inst_18912 = inst_18900.cljs$lang$protocol_mask$partition0$;
var inst_18913 = (inst_18912 & (64));
var inst_18914 = inst_18900.cljs$core$ISeq$;
var inst_18915 = (cljs.core.PROTOCOL_SENTINEL === inst_18914);
var inst_18916 = ((inst_18913) || (inst_18915));
var state_18938__$1 = state_18938;
if(cljs.core.truth_(inst_18916)){
var statearr_18962_18985 = state_18938__$1;
(statearr_18962_18985[(1)] = (11));

} else {
var statearr_18963_18986 = state_18938__$1;
(statearr_18963_18986[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
;
return ((function (switch__8695__auto__,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function() {
var taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto__ = null;
var taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto____0 = (function (){
var statearr_18964 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_18964[(0)] = taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto__);

(statearr_18964[(1)] = (1));

return statearr_18964;
});
var taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto____1 = (function (state_18938){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_18938);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e18965){if((e18965 instanceof Object)){
var ex__8699__auto__ = e18965;
var statearr_18966_18987 = state_18938;
(statearr_18966_18987[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_18938);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e18965;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__18988 = state_18938;
state_18938 = G__18988;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto__ = function(state_18938){
switch(arguments.length){
case 0:
return taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto____0.call(this);
case 1:
return taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto____1.call(this,state_18938);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto____0;
taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto____1;
return taoensso$sente$_start_chsk_router_BANG__$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
})();
var state__8792__auto__ = (function (){var statearr_18967 = f__8791__auto__.call(null);
(statearr_18967[(6)] = c__8790__auto___18968);

return statearr_18967;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___18968,map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
);


return ((function (map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1){
return (function taoensso$sente$_start_chsk_router_BANG__$_stop_BANG_(){
return cljs.core.async.close_BANG_.call(null,ch_ctrl);
});
;})(map__18889,map__18889__$1,trace_evs_QMARK_,error_handler,simple_auto_threading_QMARK_,ch_ctrl,execute1))
});
/**
 * Creates a simple go-loop to call `(event-msg-handler <server-event-msg>)`
 *   and log any errors. Returns a `(fn stop! [])`. Note that advanced users may
 *   prefer to just write their own loop against `ch-recv`.
 * 
 *   Nb performance note: since your `event-msg-handler` fn will be executed
 *   within a simple go block, you'll want this fn to be ~non-blocking
 *   (you'll especially want to avoid blocking IO) to avoid starving the
 *   core.async thread pool under load. To avoid blocking, you can use futures,
 *   agents, core.async, etc. as appropriate.
 * 
 *   Or for simple automatic future-based threading of every request, enable
 *   the `:simple-auto-threading?` opt (disabled by default).
 */
taoensso.sente.start_server_chsk_router_BANG_ = (function taoensso$sente$start_server_chsk_router_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___18998 = arguments.length;
var i__4731__auto___18999 = (0);
while(true){
if((i__4731__auto___18999 < len__4730__auto___18998)){
args__4736__auto__.push((arguments[i__4731__auto___18999]));

var G__19000 = (i__4731__auto___18999 + (1));
i__4731__auto___18999 = G__19000;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((2) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((2)),(0),null)):null);
return taoensso.sente.start_server_chsk_router_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__4737__auto__);
});

taoensso.sente.start_server_chsk_router_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ch_recv,event_msg_handler,p__18992){
var vec__18993 = p__18992;
var map__18996 = cljs.core.nth.call(null,vec__18993,(0),null);
var map__18996__$1 = (((((!((map__18996 == null))))?(((((map__18996.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__18996.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__18996):map__18996);
var opts = map__18996__$1;
var trace_evs_QMARK_ = cljs.core.get.call(null,map__18996__$1,new cljs.core.Keyword(null,"trace-evs?","trace-evs?",1502453512));
var error_handler = cljs.core.get.call(null,map__18996__$1,new cljs.core.Keyword(null,"error-handler","error-handler",-484945776));
var simple_auto_threading_QMARK_ = cljs.core.get.call(null,map__18996__$1,new cljs.core.Keyword(null,"simple-auto-threading?","simple-auto-threading?",1950754184));
return taoensso.sente._start_chsk_router_BANG_.call(null,new cljs.core.Keyword(null,"server","server",1499190120),ch_recv,event_msg_handler,opts);
});

taoensso.sente.start_server_chsk_router_BANG_.cljs$lang$maxFixedArity = (2);

/** @this {Function} */
taoensso.sente.start_server_chsk_router_BANG_.cljs$lang$applyTo = (function (seq18989){
var G__18990 = cljs.core.first.call(null,seq18989);
var seq18989__$1 = cljs.core.next.call(null,seq18989);
var G__18991 = cljs.core.first.call(null,seq18989__$1);
var seq18989__$2 = cljs.core.next.call(null,seq18989__$1);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__18990,G__18991,seq18989__$2);
});

/**
 * Creates a simple go-loop to call `(event-msg-handler <server-event-msg>)`
 *   and log any errors. Returns a `(fn stop! [])`. Note that advanced users may
 *   prefer to just write their own loop against `ch-recv`.
 * 
 *   Nb performance note: since your `event-msg-handler` fn will be executed
 *   within a simple go block, you'll want this fn to be ~non-blocking
 *   (you'll especially want to avoid blocking IO) to avoid starving the
 *   core.async thread pool under load. To avoid blocking, you can use futures,
 *   agents, core.async, etc. as appropriate.
 */
taoensso.sente.start_client_chsk_router_BANG_ = (function taoensso$sente$start_client_chsk_router_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19010 = arguments.length;
var i__4731__auto___19011 = (0);
while(true){
if((i__4731__auto___19011 < len__4730__auto___19010)){
args__4736__auto__.push((arguments[i__4731__auto___19011]));

var G__19012 = (i__4731__auto___19011 + (1));
i__4731__auto___19011 = G__19012;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((2) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((2)),(0),null)):null);
return taoensso.sente.start_client_chsk_router_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__4737__auto__);
});

taoensso.sente.start_client_chsk_router_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ch_recv,event_msg_handler,p__19004){
var vec__19005 = p__19004;
var map__19008 = cljs.core.nth.call(null,vec__19005,(0),null);
var map__19008__$1 = (((((!((map__19008 == null))))?(((((map__19008.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__19008.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__19008):map__19008);
var opts = map__19008__$1;
var trace_evs_QMARK_ = cljs.core.get.call(null,map__19008__$1,new cljs.core.Keyword(null,"trace-evs?","trace-evs?",1502453512));
var error_handler = cljs.core.get.call(null,map__19008__$1,new cljs.core.Keyword(null,"error-handler","error-handler",-484945776));
return taoensso.sente._start_chsk_router_BANG_.call(null,cljs.core.not.call(null,new cljs.core.Keyword(null,"server","server",1499190120)),ch_recv,event_msg_handler,opts);
});

taoensso.sente.start_client_chsk_router_BANG_.cljs$lang$maxFixedArity = (2);

/** @this {Function} */
taoensso.sente.start_client_chsk_router_BANG_.cljs$lang$applyTo = (function (seq19001){
var G__19002 = cljs.core.first.call(null,seq19001);
var seq19001__$1 = cljs.core.next.call(null,seq19001);
var G__19003 = cljs.core.first.call(null,seq19001__$1);
var seq19001__$2 = cljs.core.next.call(null,seq19001__$1);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19002,G__19003,seq19001__$2);
});

taoensso.sente.event_msg_QMARK_ = taoensso.sente.client_event_msg_QMARK_;
/**
 * Platform-specific alias for `make-channel-socket-server!` or
 *   `make-channel-socket-client!`. Please see the appropriate aliased fn
 * docstring for details.
 */
taoensso.sente.make_channel_socket_BANG_ = taoensso.sente.make_channel_socket_client_BANG_;
/**
 * Platform-specific alias for `start-server-chsk-router!` or
 *   `start-client-chsk-router!`. Please see the appropriate aliased fn
 *   docstring for details.
 */
taoensso.sente.start_chsk_router_BANG_ = taoensso.sente.start_client_chsk_router_BANG_;
/**
 * DEPRECATED: Please use `start-chsk-router!` instead
 */
taoensso.sente.start_chsk_router_loop_BANG_ = (function taoensso$sente$start_chsk_router_loop_BANG_(event_handler,ch_recv){
return taoensso.sente.start_client_chsk_router_BANG_.call(null,ch_recv,(function (ev_msg){
return event_handler.call(null,new cljs.core.Keyword(null,"event","event",301435442).cljs$core$IFn$_invoke$arity$1(ev_msg),new cljs.core.Keyword(null,"ch-recv","ch-recv",-990916861).cljs$core$IFn$_invoke$arity$1(ev_msg));
}));
});

/**
 * DEPRECATED. Please use `timbre/set-level!` instead
 */
taoensso.sente.set_logging_level_BANG_ = taoensso.timbre.set_level_BANG_;

/**
 * DEPRECATED: Please use `ajax-lite` instead
 */
taoensso.sente.ajax_call = taoensso.encore.ajax_lite;

/**
 * DEPRECATED
 */
taoensso.sente.default_chsk_url_fn = (function taoensso$sente$default_chsk_url_fn(path,p__19013,websocket_QMARK_){
var map__19014 = p__19013;
var map__19014__$1 = (((((!((map__19014 == null))))?(((((map__19014.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__19014.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__19014):map__19014);
var location = map__19014__$1;
var protocol = cljs.core.get.call(null,map__19014__$1,new cljs.core.Keyword(null,"protocol","protocol",652470118));
var host = cljs.core.get.call(null,map__19014__$1,new cljs.core.Keyword(null,"host","host",-1558485167));
var pathname = cljs.core.get.call(null,map__19014__$1,new cljs.core.Keyword(null,"pathname","pathname",-1420497528));
var protocol__$1 = (cljs.core.truth_(websocket_QMARK_)?((cljs.core._EQ_.call(null,protocol,"https:"))?"wss:":"ws:"):protocol);
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(protocol__$1),"//",cljs.core.str.cljs$core$IFn$_invoke$arity$1(host),cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__4131__auto__ = path;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return pathname;
}
})())].join('');
});

//# sourceMappingURL=sente.js.map
