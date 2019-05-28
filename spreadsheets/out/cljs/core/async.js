// Compiled by ClojureScript 1.10.520 {}
goog.provide('cljs.core.async');
goog.require('cljs.core');
goog.require('cljs.core.async.impl.protocols');
goog.require('cljs.core.async.impl.channels');
goog.require('cljs.core.async.impl.buffers');
goog.require('cljs.core.async.impl.timers');
goog.require('cljs.core.async.impl.dispatch');
goog.require('cljs.core.async.impl.ioc_helpers');
cljs.core.async.fn_handler = (function cljs$core$async$fn_handler(var_args){
var G__8850 = arguments.length;
switch (G__8850) {
case 1:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1 = (function (f){
return cljs.core.async.fn_handler.call(null,f,true);
});

cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2 = (function (f,blockable){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async8851 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async8851 = (function (f,blockable,meta8852){
this.f = f;
this.blockable = blockable;
this.meta8852 = meta8852;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async8851.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_8853,meta8852__$1){
var self__ = this;
var _8853__$1 = this;
return (new cljs.core.async.t_cljs$core$async8851(self__.f,self__.blockable,meta8852__$1));
});

cljs.core.async.t_cljs$core$async8851.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_8853){
var self__ = this;
var _8853__$1 = this;
return self__.meta8852;
});

cljs.core.async.t_cljs$core$async8851.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async8851.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
});

cljs.core.async.t_cljs$core$async8851.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.blockable;
});

cljs.core.async.t_cljs$core$async8851.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.f;
});

cljs.core.async.t_cljs$core$async8851.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"blockable","blockable",-28395259,null),new cljs.core.Symbol(null,"meta8852","meta8852",2055267604,null)], null);
});

cljs.core.async.t_cljs$core$async8851.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async8851.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async8851";

cljs.core.async.t_cljs$core$async8851.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async8851");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async8851.
 */
cljs.core.async.__GT_t_cljs$core$async8851 = (function cljs$core$async$__GT_t_cljs$core$async8851(f__$1,blockable__$1,meta8852){
return (new cljs.core.async.t_cljs$core$async8851(f__$1,blockable__$1,meta8852));
});

}

return (new cljs.core.async.t_cljs$core$async8851(f,blockable,cljs.core.PersistentArrayMap.EMPTY));
});

cljs.core.async.fn_handler.cljs$lang$maxFixedArity = 2;

/**
 * Returns a fixed buffer of size n. When full, puts will block/park.
 */
cljs.core.async.buffer = (function cljs$core$async$buffer(n){
return cljs.core.async.impl.buffers.fixed_buffer.call(null,n);
});
/**
 * Returns a buffer of size n. When full, puts will complete but
 *   val will be dropped (no transfer).
 */
cljs.core.async.dropping_buffer = (function cljs$core$async$dropping_buffer(n){
return cljs.core.async.impl.buffers.dropping_buffer.call(null,n);
});
/**
 * Returns a buffer of size n. When full, puts will complete, and be
 *   buffered, but oldest elements in buffer will be dropped (not
 *   transferred).
 */
cljs.core.async.sliding_buffer = (function cljs$core$async$sliding_buffer(n){
return cljs.core.async.impl.buffers.sliding_buffer.call(null,n);
});
/**
 * Returns true if a channel created with buff will never block. That is to say,
 * puts into this buffer will never cause the buffer to be full. 
 */
cljs.core.async.unblocking_buffer_QMARK_ = (function cljs$core$async$unblocking_buffer_QMARK_(buff){
if((!((buff == null)))){
if(((false) || ((cljs.core.PROTOCOL_SENTINEL === buff.cljs$core$async$impl$protocols$UnblockingBuffer$)))){
return true;
} else {
if((!buff.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,buff);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,buff);
}
});
/**
 * Creates a channel with an optional buffer, an optional transducer (like (map f),
 *   (filter p) etc or a composition thereof), and an optional exception handler.
 *   If buf-or-n is a number, will create and use a fixed buffer of that size. If a
 *   transducer is supplied a buffer must be specified. ex-handler must be a
 *   fn of one argument - if an exception occurs during transformation it will be called
 *   with the thrown value as an argument, and any non-nil return value will be placed
 *   in the channel.
 */
cljs.core.async.chan = (function cljs$core$async$chan(var_args){
var G__8857 = arguments.length;
switch (G__8857) {
case 0:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.chan.call(null,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1 = (function (buf_or_n){
return cljs.core.async.chan.call(null,buf_or_n,null,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2 = (function (buf_or_n,xform){
return cljs.core.async.chan.call(null,buf_or_n,xform,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3 = (function (buf_or_n,xform,ex_handler){
var buf_or_n__$1 = ((cljs.core._EQ_.call(null,buf_or_n,(0)))?null:buf_or_n);
if(cljs.core.truth_(xform)){
if(cljs.core.truth_(buf_or_n__$1)){
} else {
throw (new Error(["Assert failed: ","buffer must be supplied when transducer is","\n","buf-or-n"].join('')));
}
} else {
}

return cljs.core.async.impl.channels.chan.call(null,((typeof buf_or_n__$1 === 'number')?cljs.core.async.buffer.call(null,buf_or_n__$1):buf_or_n__$1),xform,ex_handler);
});

cljs.core.async.chan.cljs$lang$maxFixedArity = 3;

/**
 * Creates a promise channel with an optional transducer, and an optional
 *   exception-handler. A promise channel can take exactly one value that consumers
 *   will receive. Once full, puts complete but val is dropped (no transfer).
 *   Consumers will block until either a value is placed in the channel or the
 *   channel is closed. See chan for the semantics of xform and ex-handler.
 */
cljs.core.async.promise_chan = (function cljs$core$async$promise_chan(var_args){
var G__8860 = arguments.length;
switch (G__8860) {
case 0:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.promise_chan.call(null,null);
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1 = (function (xform){
return cljs.core.async.promise_chan.call(null,xform,null);
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2 = (function (xform,ex_handler){
return cljs.core.async.chan.call(null,cljs.core.async.impl.buffers.promise_buffer.call(null),xform,ex_handler);
});

cljs.core.async.promise_chan.cljs$lang$maxFixedArity = 2;

/**
 * Returns a channel that will close after msecs
 */
cljs.core.async.timeout = (function cljs$core$async$timeout(msecs){
return cljs.core.async.impl.timers.timeout.call(null,msecs);
});
/**
 * takes a val from port. Must be called inside a (go ...) block. Will
 *   return nil if closed. Will park if nothing is available.
 *   Returns true unless port is already closed
 */
cljs.core.async._LT__BANG_ = (function cljs$core$async$_LT__BANG_(port){
throw (new Error("<! used not in (go ...) block"));
});
/**
 * Asynchronously takes a val from port, passing to fn1. Will pass nil
 * if closed. If on-caller? (default true) is true, and value is
 * immediately available, will call fn1 on calling thread.
 * Returns nil.
 */
cljs.core.async.take_BANG_ = (function cljs$core$async$take_BANG_(var_args){
var G__8863 = arguments.length;
switch (G__8863) {
case 2:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,fn1){
return cljs.core.async.take_BANG_.call(null,port,fn1,true);
});

cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,fn1,on_caller_QMARK_){
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.fn_handler.call(null,fn1));
if(cljs.core.truth_(ret)){
var val_8865 = cljs.core.deref.call(null,ret);
if(cljs.core.truth_(on_caller_QMARK_)){
fn1.call(null,val_8865);
} else {
cljs.core.async.impl.dispatch.run.call(null,((function (val_8865,ret){
return (function (){
return fn1.call(null,val_8865);
});})(val_8865,ret))
);
}
} else {
}

return null;
});

cljs.core.async.take_BANG_.cljs$lang$maxFixedArity = 3;

cljs.core.async.nop = (function cljs$core$async$nop(_){
return null;
});
cljs.core.async.fhnop = cljs.core.async.fn_handler.call(null,cljs.core.async.nop);
/**
 * puts a val into port. nil values are not allowed. Must be called
 *   inside a (go ...) block. Will park if no buffer space is available.
 *   Returns true unless port is already closed.
 */
cljs.core.async._GT__BANG_ = (function cljs$core$async$_GT__BANG_(port,val){
throw (new Error(">! used not in (go ...) block"));
});
/**
 * Asynchronously puts a val into port, calling fn1 (if supplied) when
 * complete. nil values are not allowed. Will throw if closed. If
 * on-caller? (default true) is true, and the put is immediately
 * accepted, will call fn1 on calling thread.  Returns nil.
 */
cljs.core.async.put_BANG_ = (function cljs$core$async$put_BANG_(var_args){
var G__8867 = arguments.length;
switch (G__8867) {
case 2:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,val){
var temp__5718__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fhnop);
if(cljs.core.truth_(temp__5718__auto__)){
var ret = temp__5718__auto__;
return cljs.core.deref.call(null,ret);
} else {
return true;
}
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,val,fn1){
return cljs.core.async.put_BANG_.call(null,port,val,fn1,true);
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4 = (function (port,val,fn1,on_caller_QMARK_){
var temp__5718__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fn_handler.call(null,fn1));
if(cljs.core.truth_(temp__5718__auto__)){
var retb = temp__5718__auto__;
var ret = cljs.core.deref.call(null,retb);
if(cljs.core.truth_(on_caller_QMARK_)){
fn1.call(null,ret);
} else {
cljs.core.async.impl.dispatch.run.call(null,((function (ret,retb,temp__5718__auto__){
return (function (){
return fn1.call(null,ret);
});})(ret,retb,temp__5718__auto__))
);
}

return ret;
} else {
return true;
}
});

cljs.core.async.put_BANG_.cljs$lang$maxFixedArity = 4;

cljs.core.async.close_BANG_ = (function cljs$core$async$close_BANG_(port){
return cljs.core.async.impl.protocols.close_BANG_.call(null,port);
});
cljs.core.async.random_array = (function cljs$core$async$random_array(n){
var a = (new Array(n));
var n__4607__auto___8869 = n;
var x_8870 = (0);
while(true){
if((x_8870 < n__4607__auto___8869)){
(a[x_8870] = (0));

var G__8871 = (x_8870 + (1));
x_8870 = G__8871;
continue;
} else {
}
break;
}

var i = (1);
while(true){
if(cljs.core._EQ_.call(null,i,n)){
return a;
} else {
var j = cljs.core.rand_int.call(null,i);
(a[i] = (a[j]));

(a[j] = i);

var G__8872 = (i + (1));
i = G__8872;
continue;
}
break;
}
});
cljs.core.async.alt_flag = (function cljs$core$async$alt_flag(){
var flag = cljs.core.atom.call(null,true);
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async8873 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async8873 = (function (flag,meta8874){
this.flag = flag;
this.meta8874 = meta8874;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async8873.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (flag){
return (function (_8875,meta8874__$1){
var self__ = this;
var _8875__$1 = this;
return (new cljs.core.async.t_cljs$core$async8873(self__.flag,meta8874__$1));
});})(flag))
;

cljs.core.async.t_cljs$core$async8873.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (flag){
return (function (_8875){
var self__ = this;
var _8875__$1 = this;
return self__.meta8874;
});})(flag))
;

cljs.core.async.t_cljs$core$async8873.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async8873.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.deref.call(null,self__.flag);
});})(flag))
;

cljs.core.async.t_cljs$core$async8873.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
return true;
});})(flag))
;

cljs.core.async.t_cljs$core$async8873.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.flag,null);

return true;
});})(flag))
;

cljs.core.async.t_cljs$core$async8873.getBasis = ((function (flag){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"meta8874","meta8874",-10827802,null)], null);
});})(flag))
;

cljs.core.async.t_cljs$core$async8873.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async8873.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async8873";

cljs.core.async.t_cljs$core$async8873.cljs$lang$ctorPrWriter = ((function (flag){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async8873");
});})(flag))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async8873.
 */
cljs.core.async.__GT_t_cljs$core$async8873 = ((function (flag){
return (function cljs$core$async$alt_flag_$___GT_t_cljs$core$async8873(flag__$1,meta8874){
return (new cljs.core.async.t_cljs$core$async8873(flag__$1,meta8874));
});})(flag))
;

}

return (new cljs.core.async.t_cljs$core$async8873(flag,cljs.core.PersistentArrayMap.EMPTY));
});
cljs.core.async.alt_handler = (function cljs$core$async$alt_handler(flag,cb){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async8876 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async8876 = (function (flag,cb,meta8877){
this.flag = flag;
this.cb = cb;
this.meta8877 = meta8877;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async8876.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_8878,meta8877__$1){
var self__ = this;
var _8878__$1 = this;
return (new cljs.core.async.t_cljs$core$async8876(self__.flag,self__.cb,meta8877__$1));
});

cljs.core.async.t_cljs$core$async8876.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_8878){
var self__ = this;
var _8878__$1 = this;
return self__.meta8877;
});

cljs.core.async.t_cljs$core$async8876.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async8876.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.flag);
});

cljs.core.async.t_cljs$core$async8876.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
});

cljs.core.async.t_cljs$core$async8876.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.async.impl.protocols.commit.call(null,self__.flag);

return self__.cb;
});

cljs.core.async.t_cljs$core$async8876.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"cb","cb",-2064487928,null),new cljs.core.Symbol(null,"meta8877","meta8877",-746212578,null)], null);
});

cljs.core.async.t_cljs$core$async8876.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async8876.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async8876";

cljs.core.async.t_cljs$core$async8876.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async8876");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async8876.
 */
cljs.core.async.__GT_t_cljs$core$async8876 = (function cljs$core$async$alt_handler_$___GT_t_cljs$core$async8876(flag__$1,cb__$1,meta8877){
return (new cljs.core.async.t_cljs$core$async8876(flag__$1,cb__$1,meta8877));
});

}

return (new cljs.core.async.t_cljs$core$async8876(flag,cb,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * returns derefable [val port] if immediate, nil if enqueued
 */
cljs.core.async.do_alts = (function cljs$core$async$do_alts(fret,ports,opts){
var flag = cljs.core.async.alt_flag.call(null);
var n = cljs.core.count.call(null,ports);
var idxs = cljs.core.async.random_array.call(null,n);
var priority = new cljs.core.Keyword(null,"priority","priority",1431093715).cljs$core$IFn$_invoke$arity$1(opts);
var ret = (function (){var i = (0);
while(true){
if((i < n)){
var idx = (cljs.core.truth_(priority)?i:(idxs[i]));
var port = cljs.core.nth.call(null,ports,idx);
var wport = ((cljs.core.vector_QMARK_.call(null,port))?port.call(null,(0)):null);
var vbox = (cljs.core.truth_(wport)?(function (){var val = port.call(null,(1));
return cljs.core.async.impl.protocols.put_BANG_.call(null,wport,val,cljs.core.async.alt_handler.call(null,flag,((function (i,val,idx,port,wport,flag,n,idxs,priority){
return (function (p1__8879_SHARP_){
return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__8879_SHARP_,wport], null));
});})(i,val,idx,port,wport,flag,n,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.alt_handler.call(null,flag,((function (i,idx,port,wport,flag,n,idxs,priority){
return (function (p1__8880_SHARP_){
return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__8880_SHARP_,port], null));
});})(i,idx,port,wport,flag,n,idxs,priority))
)));
if(cljs.core.truth_(vbox)){
return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.deref.call(null,vbox),(function (){var or__4131__auto__ = wport;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return port;
}
})()], null));
} else {
var G__8881 = (i + (1));
i = G__8881;
continue;
}
} else {
return null;
}
break;
}
})();
var or__4131__auto__ = ret;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
if(cljs.core.contains_QMARK_.call(null,opts,new cljs.core.Keyword(null,"default","default",-1987822328))){
var temp__5720__auto__ = (function (){var and__4120__auto__ = cljs.core.async.impl.protocols.active_QMARK_.call(null,flag);
if(cljs.core.truth_(and__4120__auto__)){
return cljs.core.async.impl.protocols.commit.call(null,flag);
} else {
return and__4120__auto__;
}
})();
if(cljs.core.truth_(temp__5720__auto__)){
var got = temp__5720__auto__;
return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"default","default",-1987822328).cljs$core$IFn$_invoke$arity$1(opts),new cljs.core.Keyword(null,"default","default",-1987822328)], null));
} else {
return null;
}
} else {
return null;
}
}
});
/**
 * Completes at most one of several channel operations. Must be called
 * inside a (go ...) block. ports is a vector of channel endpoints,
 * which can be either a channel to take from or a vector of
 *   [channel-to-put-to val-to-put], in any combination. Takes will be
 *   made as if by <!, and puts will be made as if by >!. Unless
 *   the :priority option is true, if more than one port operation is
 *   ready a non-deterministic choice will be made. If no operation is
 *   ready and a :default value is supplied, [default-val :default] will
 *   be returned, otherwise alts! will park until the first operation to
 *   become ready completes. Returns [val port] of the completed
 *   operation, where val is the value taken for takes, and a
 *   boolean (true unless already closed, as per put!) for puts.
 * 
 *   opts are passed as :key val ... Supported options:
 * 
 *   :default val - the value to use if none of the operations are immediately ready
 *   :priority true - (default nil) when true, the operations will be tried in order.
 * 
 *   Note: there is no guarantee that the port exps or val exprs will be
 *   used, nor in what order should they be, so they should not be
 *   depended upon for side effects.
 */
cljs.core.async.alts_BANG_ = (function cljs$core$async$alts_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___8887 = arguments.length;
var i__4731__auto___8888 = (0);
while(true){
if((i__4731__auto___8888 < len__4730__auto___8887)){
args__4736__auto__.push((arguments[i__4731__auto___8888]));

var G__8889 = (i__4731__auto___8888 + (1));
i__4731__auto___8888 = G__8889;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ports,p__8884){
var map__8885 = p__8884;
var map__8885__$1 = (((((!((map__8885 == null))))?(((((map__8885.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__8885.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__8885):map__8885);
var opts = map__8885__$1;
throw (new Error("alts! used not in (go ...) block"));
});

cljs.core.async.alts_BANG_.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
cljs.core.async.alts_BANG_.cljs$lang$applyTo = (function (seq8882){
var G__8883 = cljs.core.first.call(null,seq8882);
var seq8882__$1 = cljs.core.next.call(null,seq8882);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__8883,seq8882__$1);
});

/**
 * Puts a val into port if it's possible to do so immediately.
 *   nil values are not allowed. Never blocks. Returns true if offer succeeds.
 */
cljs.core.async.offer_BANG_ = (function cljs$core$async$offer_BANG_(port,val){
var ret = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fn_handler.call(null,cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref.call(null,ret);
} else {
return null;
}
});
/**
 * Takes a val from port if it's possible to do so immediately.
 *   Never blocks. Returns value if successful, nil otherwise.
 */
cljs.core.async.poll_BANG_ = (function cljs$core$async$poll_BANG_(port){
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.fn_handler.call(null,cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref.call(null,ret);
} else {
return null;
}
});
/**
 * Takes elements from the from channel and supplies them to the to
 * channel. By default, the to channel will be closed when the from
 * channel closes, but can be determined by the close?  parameter. Will
 * stop consuming the from channel if the to channel closes
 */
cljs.core.async.pipe = (function cljs$core$async$pipe(var_args){
var G__8891 = arguments.length;
switch (G__8891) {
case 2:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2 = (function (from,to){
return cljs.core.async.pipe.call(null,from,to,true);
});

cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3 = (function (from,to,close_QMARK_){
var c__8790__auto___8937 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___8937){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___8937){
return (function (state_8915){
var state_val_8916 = (state_8915[(1)]);
if((state_val_8916 === (7))){
var inst_8911 = (state_8915[(2)]);
var state_8915__$1 = state_8915;
var statearr_8917_8938 = state_8915__$1;
(statearr_8917_8938[(2)] = inst_8911);

(statearr_8917_8938[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (1))){
var state_8915__$1 = state_8915;
var statearr_8918_8939 = state_8915__$1;
(statearr_8918_8939[(2)] = null);

(statearr_8918_8939[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (4))){
var inst_8894 = (state_8915[(7)]);
var inst_8894__$1 = (state_8915[(2)]);
var inst_8895 = (inst_8894__$1 == null);
var state_8915__$1 = (function (){var statearr_8919 = state_8915;
(statearr_8919[(7)] = inst_8894__$1);

return statearr_8919;
})();
if(cljs.core.truth_(inst_8895)){
var statearr_8920_8940 = state_8915__$1;
(statearr_8920_8940[(1)] = (5));

} else {
var statearr_8921_8941 = state_8915__$1;
(statearr_8921_8941[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (13))){
var state_8915__$1 = state_8915;
var statearr_8922_8942 = state_8915__$1;
(statearr_8922_8942[(2)] = null);

(statearr_8922_8942[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (6))){
var inst_8894 = (state_8915[(7)]);
var state_8915__$1 = state_8915;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_8915__$1,(11),to,inst_8894);
} else {
if((state_val_8916 === (3))){
var inst_8913 = (state_8915[(2)]);
var state_8915__$1 = state_8915;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_8915__$1,inst_8913);
} else {
if((state_val_8916 === (12))){
var state_8915__$1 = state_8915;
var statearr_8923_8943 = state_8915__$1;
(statearr_8923_8943[(2)] = null);

(statearr_8923_8943[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (2))){
var state_8915__$1 = state_8915;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_8915__$1,(4),from);
} else {
if((state_val_8916 === (11))){
var inst_8904 = (state_8915[(2)]);
var state_8915__$1 = state_8915;
if(cljs.core.truth_(inst_8904)){
var statearr_8924_8944 = state_8915__$1;
(statearr_8924_8944[(1)] = (12));

} else {
var statearr_8925_8945 = state_8915__$1;
(statearr_8925_8945[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (9))){
var state_8915__$1 = state_8915;
var statearr_8926_8946 = state_8915__$1;
(statearr_8926_8946[(2)] = null);

(statearr_8926_8946[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (5))){
var state_8915__$1 = state_8915;
if(cljs.core.truth_(close_QMARK_)){
var statearr_8927_8947 = state_8915__$1;
(statearr_8927_8947[(1)] = (8));

} else {
var statearr_8928_8948 = state_8915__$1;
(statearr_8928_8948[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (14))){
var inst_8909 = (state_8915[(2)]);
var state_8915__$1 = state_8915;
var statearr_8929_8949 = state_8915__$1;
(statearr_8929_8949[(2)] = inst_8909);

(statearr_8929_8949[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (10))){
var inst_8901 = (state_8915[(2)]);
var state_8915__$1 = state_8915;
var statearr_8930_8950 = state_8915__$1;
(statearr_8930_8950[(2)] = inst_8901);

(statearr_8930_8950[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8916 === (8))){
var inst_8898 = cljs.core.async.close_BANG_.call(null,to);
var state_8915__$1 = state_8915;
var statearr_8931_8951 = state_8915__$1;
(statearr_8931_8951[(2)] = inst_8898);

(statearr_8931_8951[(1)] = (10));


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
});})(c__8790__auto___8937))
;
return ((function (switch__8695__auto__,c__8790__auto___8937){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_8932 = [null,null,null,null,null,null,null,null];
(statearr_8932[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_8932[(1)] = (1));

return statearr_8932;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_8915){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_8915);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e8933){if((e8933 instanceof Object)){
var ex__8699__auto__ = e8933;
var statearr_8934_8952 = state_8915;
(statearr_8934_8952[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_8915);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e8933;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__8953 = state_8915;
state_8915 = G__8953;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_8915){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_8915);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___8937))
})();
var state__8792__auto__ = (function (){var statearr_8935 = f__8791__auto__.call(null);
(statearr_8935[(6)] = c__8790__auto___8937);

return statearr_8935;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___8937))
);


return to;
});

cljs.core.async.pipe.cljs$lang$maxFixedArity = 3;

cljs.core.async.pipeline_STAR_ = (function cljs$core$async$pipeline_STAR_(n,to,xf,from,close_QMARK_,ex_handler,type){
if((n > (0))){
} else {
throw (new Error("Assert failed: (pos? n)"));
}

var jobs = cljs.core.async.chan.call(null,n);
var results = cljs.core.async.chan.call(null,n);
var process = ((function (jobs,results){
return (function (p__8954){
var vec__8955 = p__8954;
var v = cljs.core.nth.call(null,vec__8955,(0),null);
var p = cljs.core.nth.call(null,vec__8955,(1),null);
var job = vec__8955;
if((job == null)){
cljs.core.async.close_BANG_.call(null,results);

return null;
} else {
var res = cljs.core.async.chan.call(null,(1),xf,ex_handler);
var c__8790__auto___9126 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___9126,res,vec__8955,v,p,job,jobs,results){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___9126,res,vec__8955,v,p,job,jobs,results){
return (function (state_8962){
var state_val_8963 = (state_8962[(1)]);
if((state_val_8963 === (1))){
var state_8962__$1 = state_8962;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_8962__$1,(2),res,v);
} else {
if((state_val_8963 === (2))){
var inst_8959 = (state_8962[(2)]);
var inst_8960 = cljs.core.async.close_BANG_.call(null,res);
var state_8962__$1 = (function (){var statearr_8964 = state_8962;
(statearr_8964[(7)] = inst_8959);

return statearr_8964;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_8962__$1,inst_8960);
} else {
return null;
}
}
});})(c__8790__auto___9126,res,vec__8955,v,p,job,jobs,results))
;
return ((function (switch__8695__auto__,c__8790__auto___9126,res,vec__8955,v,p,job,jobs,results){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0 = (function (){
var statearr_8965 = [null,null,null,null,null,null,null,null];
(statearr_8965[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__);

(statearr_8965[(1)] = (1));

return statearr_8965;
});
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1 = (function (state_8962){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_8962);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e8966){if((e8966 instanceof Object)){
var ex__8699__auto__ = e8966;
var statearr_8967_9127 = state_8962;
(statearr_8967_9127[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_8962);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e8966;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9128 = state_8962;
state_8962 = G__9128;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = function(state_8962){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1.call(this,state_8962);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___9126,res,vec__8955,v,p,job,jobs,results))
})();
var state__8792__auto__ = (function (){var statearr_8968 = f__8791__auto__.call(null);
(statearr_8968[(6)] = c__8790__auto___9126);

return statearr_8968;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___9126,res,vec__8955,v,p,job,jobs,results))
);


cljs.core.async.put_BANG_.call(null,p,res);

return true;
}
});})(jobs,results))
;
var async = ((function (jobs,results,process){
return (function (p__8969){
var vec__8970 = p__8969;
var v = cljs.core.nth.call(null,vec__8970,(0),null);
var p = cljs.core.nth.call(null,vec__8970,(1),null);
var job = vec__8970;
if((job == null)){
cljs.core.async.close_BANG_.call(null,results);

return null;
} else {
var res = cljs.core.async.chan.call(null,(1));
xf.call(null,v,res);

cljs.core.async.put_BANG_.call(null,p,res);

return true;
}
});})(jobs,results,process))
;
var n__4607__auto___9129 = n;
var __9130 = (0);
while(true){
if((__9130 < n__4607__auto___9129)){
var G__8973_9131 = type;
var G__8973_9132__$1 = (((G__8973_9131 instanceof cljs.core.Keyword))?G__8973_9131.fqn:null);
switch (G__8973_9132__$1) {
case "compute":
var c__8790__auto___9134 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (__9130,c__8790__auto___9134,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (__9130,c__8790__auto___9134,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async){
return (function (state_8986){
var state_val_8987 = (state_8986[(1)]);
if((state_val_8987 === (1))){
var state_8986__$1 = state_8986;
var statearr_8988_9135 = state_8986__$1;
(statearr_8988_9135[(2)] = null);

(statearr_8988_9135[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8987 === (2))){
var state_8986__$1 = state_8986;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_8986__$1,(4),jobs);
} else {
if((state_val_8987 === (3))){
var inst_8984 = (state_8986[(2)]);
var state_8986__$1 = state_8986;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_8986__$1,inst_8984);
} else {
if((state_val_8987 === (4))){
var inst_8976 = (state_8986[(2)]);
var inst_8977 = process.call(null,inst_8976);
var state_8986__$1 = state_8986;
if(cljs.core.truth_(inst_8977)){
var statearr_8989_9136 = state_8986__$1;
(statearr_8989_9136[(1)] = (5));

} else {
var statearr_8990_9137 = state_8986__$1;
(statearr_8990_9137[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8987 === (5))){
var state_8986__$1 = state_8986;
var statearr_8991_9138 = state_8986__$1;
(statearr_8991_9138[(2)] = null);

(statearr_8991_9138[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8987 === (6))){
var state_8986__$1 = state_8986;
var statearr_8992_9139 = state_8986__$1;
(statearr_8992_9139[(2)] = null);

(statearr_8992_9139[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_8987 === (7))){
var inst_8982 = (state_8986[(2)]);
var state_8986__$1 = state_8986;
var statearr_8993_9140 = state_8986__$1;
(statearr_8993_9140[(2)] = inst_8982);

(statearr_8993_9140[(1)] = (3));


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
});})(__9130,c__8790__auto___9134,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async))
;
return ((function (__9130,switch__8695__auto__,c__8790__auto___9134,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0 = (function (){
var statearr_8994 = [null,null,null,null,null,null,null];
(statearr_8994[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__);

(statearr_8994[(1)] = (1));

return statearr_8994;
});
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1 = (function (state_8986){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_8986);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e8995){if((e8995 instanceof Object)){
var ex__8699__auto__ = e8995;
var statearr_8996_9141 = state_8986;
(statearr_8996_9141[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_8986);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e8995;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9142 = state_8986;
state_8986 = G__9142;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = function(state_8986){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1.call(this,state_8986);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__;
})()
;})(__9130,switch__8695__auto__,c__8790__auto___9134,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async))
})();
var state__8792__auto__ = (function (){var statearr_8997 = f__8791__auto__.call(null);
(statearr_8997[(6)] = c__8790__auto___9134);

return statearr_8997;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(__9130,c__8790__auto___9134,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async))
);


break;
case "async":
var c__8790__auto___9143 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (__9130,c__8790__auto___9143,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (__9130,c__8790__auto___9143,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async){
return (function (state_9010){
var state_val_9011 = (state_9010[(1)]);
if((state_val_9011 === (1))){
var state_9010__$1 = state_9010;
var statearr_9012_9144 = state_9010__$1;
(statearr_9012_9144[(2)] = null);

(statearr_9012_9144[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9011 === (2))){
var state_9010__$1 = state_9010;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9010__$1,(4),jobs);
} else {
if((state_val_9011 === (3))){
var inst_9008 = (state_9010[(2)]);
var state_9010__$1 = state_9010;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9010__$1,inst_9008);
} else {
if((state_val_9011 === (4))){
var inst_9000 = (state_9010[(2)]);
var inst_9001 = async.call(null,inst_9000);
var state_9010__$1 = state_9010;
if(cljs.core.truth_(inst_9001)){
var statearr_9013_9145 = state_9010__$1;
(statearr_9013_9145[(1)] = (5));

} else {
var statearr_9014_9146 = state_9010__$1;
(statearr_9014_9146[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9011 === (5))){
var state_9010__$1 = state_9010;
var statearr_9015_9147 = state_9010__$1;
(statearr_9015_9147[(2)] = null);

(statearr_9015_9147[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9011 === (6))){
var state_9010__$1 = state_9010;
var statearr_9016_9148 = state_9010__$1;
(statearr_9016_9148[(2)] = null);

(statearr_9016_9148[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9011 === (7))){
var inst_9006 = (state_9010[(2)]);
var state_9010__$1 = state_9010;
var statearr_9017_9149 = state_9010__$1;
(statearr_9017_9149[(2)] = inst_9006);

(statearr_9017_9149[(1)] = (3));


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
});})(__9130,c__8790__auto___9143,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async))
;
return ((function (__9130,switch__8695__auto__,c__8790__auto___9143,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0 = (function (){
var statearr_9018 = [null,null,null,null,null,null,null];
(statearr_9018[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__);

(statearr_9018[(1)] = (1));

return statearr_9018;
});
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1 = (function (state_9010){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9010);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9019){if((e9019 instanceof Object)){
var ex__8699__auto__ = e9019;
var statearr_9020_9150 = state_9010;
(statearr_9020_9150[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9010);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9019;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9151 = state_9010;
state_9010 = G__9151;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = function(state_9010){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1.call(this,state_9010);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__;
})()
;})(__9130,switch__8695__auto__,c__8790__auto___9143,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async))
})();
var state__8792__auto__ = (function (){var statearr_9021 = f__8791__auto__.call(null);
(statearr_9021[(6)] = c__8790__auto___9143);

return statearr_9021;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(__9130,c__8790__auto___9143,G__8973_9131,G__8973_9132__$1,n__4607__auto___9129,jobs,results,process,async))
);


break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__8973_9132__$1)].join('')));

}

var G__9152 = (__9130 + (1));
__9130 = G__9152;
continue;
} else {
}
break;
}

var c__8790__auto___9153 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___9153,jobs,results,process,async){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___9153,jobs,results,process,async){
return (function (state_9043){
var state_val_9044 = (state_9043[(1)]);
if((state_val_9044 === (7))){
var inst_9039 = (state_9043[(2)]);
var state_9043__$1 = state_9043;
var statearr_9045_9154 = state_9043__$1;
(statearr_9045_9154[(2)] = inst_9039);

(statearr_9045_9154[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9044 === (1))){
var state_9043__$1 = state_9043;
var statearr_9046_9155 = state_9043__$1;
(statearr_9046_9155[(2)] = null);

(statearr_9046_9155[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9044 === (4))){
var inst_9024 = (state_9043[(7)]);
var inst_9024__$1 = (state_9043[(2)]);
var inst_9025 = (inst_9024__$1 == null);
var state_9043__$1 = (function (){var statearr_9047 = state_9043;
(statearr_9047[(7)] = inst_9024__$1);

return statearr_9047;
})();
if(cljs.core.truth_(inst_9025)){
var statearr_9048_9156 = state_9043__$1;
(statearr_9048_9156[(1)] = (5));

} else {
var statearr_9049_9157 = state_9043__$1;
(statearr_9049_9157[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9044 === (6))){
var inst_9024 = (state_9043[(7)]);
var inst_9029 = (state_9043[(8)]);
var inst_9029__$1 = cljs.core.async.chan.call(null,(1));
var inst_9030 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_9031 = [inst_9024,inst_9029__$1];
var inst_9032 = (new cljs.core.PersistentVector(null,2,(5),inst_9030,inst_9031,null));
var state_9043__$1 = (function (){var statearr_9050 = state_9043;
(statearr_9050[(8)] = inst_9029__$1);

return statearr_9050;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_9043__$1,(8),jobs,inst_9032);
} else {
if((state_val_9044 === (3))){
var inst_9041 = (state_9043[(2)]);
var state_9043__$1 = state_9043;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9043__$1,inst_9041);
} else {
if((state_val_9044 === (2))){
var state_9043__$1 = state_9043;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9043__$1,(4),from);
} else {
if((state_val_9044 === (9))){
var inst_9036 = (state_9043[(2)]);
var state_9043__$1 = (function (){var statearr_9051 = state_9043;
(statearr_9051[(9)] = inst_9036);

return statearr_9051;
})();
var statearr_9052_9158 = state_9043__$1;
(statearr_9052_9158[(2)] = null);

(statearr_9052_9158[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9044 === (5))){
var inst_9027 = cljs.core.async.close_BANG_.call(null,jobs);
var state_9043__$1 = state_9043;
var statearr_9053_9159 = state_9043__$1;
(statearr_9053_9159[(2)] = inst_9027);

(statearr_9053_9159[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9044 === (8))){
var inst_9029 = (state_9043[(8)]);
var inst_9034 = (state_9043[(2)]);
var state_9043__$1 = (function (){var statearr_9054 = state_9043;
(statearr_9054[(10)] = inst_9034);

return statearr_9054;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_9043__$1,(9),results,inst_9029);
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
});})(c__8790__auto___9153,jobs,results,process,async))
;
return ((function (switch__8695__auto__,c__8790__auto___9153,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0 = (function (){
var statearr_9055 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_9055[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__);

(statearr_9055[(1)] = (1));

return statearr_9055;
});
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1 = (function (state_9043){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9043);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9056){if((e9056 instanceof Object)){
var ex__8699__auto__ = e9056;
var statearr_9057_9160 = state_9043;
(statearr_9057_9160[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9043);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9056;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9161 = state_9043;
state_9043 = G__9161;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = function(state_9043){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1.call(this,state_9043);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___9153,jobs,results,process,async))
})();
var state__8792__auto__ = (function (){var statearr_9058 = f__8791__auto__.call(null);
(statearr_9058[(6)] = c__8790__auto___9153);

return statearr_9058;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___9153,jobs,results,process,async))
);


var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__,jobs,results,process,async){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__,jobs,results,process,async){
return (function (state_9096){
var state_val_9097 = (state_9096[(1)]);
if((state_val_9097 === (7))){
var inst_9092 = (state_9096[(2)]);
var state_9096__$1 = state_9096;
var statearr_9098_9162 = state_9096__$1;
(statearr_9098_9162[(2)] = inst_9092);

(statearr_9098_9162[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (20))){
var state_9096__$1 = state_9096;
var statearr_9099_9163 = state_9096__$1;
(statearr_9099_9163[(2)] = null);

(statearr_9099_9163[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (1))){
var state_9096__$1 = state_9096;
var statearr_9100_9164 = state_9096__$1;
(statearr_9100_9164[(2)] = null);

(statearr_9100_9164[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (4))){
var inst_9061 = (state_9096[(7)]);
var inst_9061__$1 = (state_9096[(2)]);
var inst_9062 = (inst_9061__$1 == null);
var state_9096__$1 = (function (){var statearr_9101 = state_9096;
(statearr_9101[(7)] = inst_9061__$1);

return statearr_9101;
})();
if(cljs.core.truth_(inst_9062)){
var statearr_9102_9165 = state_9096__$1;
(statearr_9102_9165[(1)] = (5));

} else {
var statearr_9103_9166 = state_9096__$1;
(statearr_9103_9166[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (15))){
var inst_9074 = (state_9096[(8)]);
var state_9096__$1 = state_9096;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_9096__$1,(18),to,inst_9074);
} else {
if((state_val_9097 === (21))){
var inst_9087 = (state_9096[(2)]);
var state_9096__$1 = state_9096;
var statearr_9104_9167 = state_9096__$1;
(statearr_9104_9167[(2)] = inst_9087);

(statearr_9104_9167[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (13))){
var inst_9089 = (state_9096[(2)]);
var state_9096__$1 = (function (){var statearr_9105 = state_9096;
(statearr_9105[(9)] = inst_9089);

return statearr_9105;
})();
var statearr_9106_9168 = state_9096__$1;
(statearr_9106_9168[(2)] = null);

(statearr_9106_9168[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (6))){
var inst_9061 = (state_9096[(7)]);
var state_9096__$1 = state_9096;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9096__$1,(11),inst_9061);
} else {
if((state_val_9097 === (17))){
var inst_9082 = (state_9096[(2)]);
var state_9096__$1 = state_9096;
if(cljs.core.truth_(inst_9082)){
var statearr_9107_9169 = state_9096__$1;
(statearr_9107_9169[(1)] = (19));

} else {
var statearr_9108_9170 = state_9096__$1;
(statearr_9108_9170[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (3))){
var inst_9094 = (state_9096[(2)]);
var state_9096__$1 = state_9096;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9096__$1,inst_9094);
} else {
if((state_val_9097 === (12))){
var inst_9071 = (state_9096[(10)]);
var state_9096__$1 = state_9096;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9096__$1,(14),inst_9071);
} else {
if((state_val_9097 === (2))){
var state_9096__$1 = state_9096;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9096__$1,(4),results);
} else {
if((state_val_9097 === (19))){
var state_9096__$1 = state_9096;
var statearr_9109_9171 = state_9096__$1;
(statearr_9109_9171[(2)] = null);

(statearr_9109_9171[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (11))){
var inst_9071 = (state_9096[(2)]);
var state_9096__$1 = (function (){var statearr_9110 = state_9096;
(statearr_9110[(10)] = inst_9071);

return statearr_9110;
})();
var statearr_9111_9172 = state_9096__$1;
(statearr_9111_9172[(2)] = null);

(statearr_9111_9172[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (9))){
var state_9096__$1 = state_9096;
var statearr_9112_9173 = state_9096__$1;
(statearr_9112_9173[(2)] = null);

(statearr_9112_9173[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (5))){
var state_9096__$1 = state_9096;
if(cljs.core.truth_(close_QMARK_)){
var statearr_9113_9174 = state_9096__$1;
(statearr_9113_9174[(1)] = (8));

} else {
var statearr_9114_9175 = state_9096__$1;
(statearr_9114_9175[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (14))){
var inst_9076 = (state_9096[(11)]);
var inst_9074 = (state_9096[(8)]);
var inst_9074__$1 = (state_9096[(2)]);
var inst_9075 = (inst_9074__$1 == null);
var inst_9076__$1 = cljs.core.not.call(null,inst_9075);
var state_9096__$1 = (function (){var statearr_9115 = state_9096;
(statearr_9115[(11)] = inst_9076__$1);

(statearr_9115[(8)] = inst_9074__$1);

return statearr_9115;
})();
if(inst_9076__$1){
var statearr_9116_9176 = state_9096__$1;
(statearr_9116_9176[(1)] = (15));

} else {
var statearr_9117_9177 = state_9096__$1;
(statearr_9117_9177[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (16))){
var inst_9076 = (state_9096[(11)]);
var state_9096__$1 = state_9096;
var statearr_9118_9178 = state_9096__$1;
(statearr_9118_9178[(2)] = inst_9076);

(statearr_9118_9178[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (10))){
var inst_9068 = (state_9096[(2)]);
var state_9096__$1 = state_9096;
var statearr_9119_9179 = state_9096__$1;
(statearr_9119_9179[(2)] = inst_9068);

(statearr_9119_9179[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (18))){
var inst_9079 = (state_9096[(2)]);
var state_9096__$1 = state_9096;
var statearr_9120_9180 = state_9096__$1;
(statearr_9120_9180[(2)] = inst_9079);

(statearr_9120_9180[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9097 === (8))){
var inst_9065 = cljs.core.async.close_BANG_.call(null,to);
var state_9096__$1 = state_9096;
var statearr_9121_9181 = state_9096__$1;
(statearr_9121_9181[(2)] = inst_9065);

(statearr_9121_9181[(1)] = (10));


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
}
}
}
}
}
});})(c__8790__auto__,jobs,results,process,async))
;
return ((function (switch__8695__auto__,c__8790__auto__,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0 = (function (){
var statearr_9122 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_9122[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__);

(statearr_9122[(1)] = (1));

return statearr_9122;
});
var cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1 = (function (state_9096){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9096);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9123){if((e9123 instanceof Object)){
var ex__8699__auto__ = e9123;
var statearr_9124_9182 = state_9096;
(statearr_9124_9182[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9096);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9123;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9183 = state_9096;
state_9096 = G__9183;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__ = function(state_9096){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1.call(this,state_9096);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__8696__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__,jobs,results,process,async))
})();
var state__8792__auto__ = (function (){var statearr_9125 = f__8791__auto__.call(null);
(statearr_9125[(6)] = c__8790__auto__);

return statearr_9125;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__,jobs,results,process,async))
);

return c__8790__auto__;
});
/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the async function af, with parallelism n. af
 *   must be a function of two arguments, the first an input value and
 *   the second a channel on which to place the result(s). af must close!
 *   the channel before returning.  The presumption is that af will
 *   return immediately, having launched some asynchronous operation
 *   whose completion/callback will manipulate the result channel. Outputs
 *   will be returned in order relative to  the inputs. By default, the to
 *   channel will be closed when the from channel closes, but can be
 *   determined by the close?  parameter. Will stop consuming the from
 *   channel if the to channel closes.
 */
cljs.core.async.pipeline_async = (function cljs$core$async$pipeline_async(var_args){
var G__9185 = arguments.length;
switch (G__9185) {
case 4:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4 = (function (n,to,af,from){
return cljs.core.async.pipeline_async.call(null,n,to,af,from,true);
});

cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5 = (function (n,to,af,from,close_QMARK_){
return cljs.core.async.pipeline_STAR_.call(null,n,to,af,from,close_QMARK_,null,new cljs.core.Keyword(null,"async","async",1050769601));
});

cljs.core.async.pipeline_async.cljs$lang$maxFixedArity = 5;

/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the transducer xf, with parallelism n. Because
 *   it is parallel, the transducer will be applied independently to each
 *   element, not across elements, and may produce zero or more outputs
 *   per input.  Outputs will be returned in order relative to the
 *   inputs. By default, the to channel will be closed when the from
 *   channel closes, but can be determined by the close?  parameter. Will
 *   stop consuming the from channel if the to channel closes.
 * 
 *   Note this is supplied for API compatibility with the Clojure version.
 *   Values of N > 1 will not result in actual concurrency in a
 *   single-threaded runtime.
 */
cljs.core.async.pipeline = (function cljs$core$async$pipeline(var_args){
var G__9188 = arguments.length;
switch (G__9188) {
case 4:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
case 6:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4 = (function (n,to,xf,from){
return cljs.core.async.pipeline.call(null,n,to,xf,from,true);
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5 = (function (n,to,xf,from,close_QMARK_){
return cljs.core.async.pipeline.call(null,n,to,xf,from,close_QMARK_,null);
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6 = (function (n,to,xf,from,close_QMARK_,ex_handler){
return cljs.core.async.pipeline_STAR_.call(null,n,to,xf,from,close_QMARK_,ex_handler,new cljs.core.Keyword(null,"compute","compute",1555393130));
});

cljs.core.async.pipeline.cljs$lang$maxFixedArity = 6;

/**
 * Takes a predicate and a source channel and returns a vector of two
 *   channels, the first of which will contain the values for which the
 *   predicate returned true, the second those for which it returned
 *   false.
 * 
 *   The out channels will be unbuffered by default, or two buf-or-ns can
 *   be supplied. The channels will close after the source channel has
 *   closed.
 */
cljs.core.async.split = (function cljs$core$async$split(var_args){
var G__9191 = arguments.length;
switch (G__9191) {
case 2:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.split.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.split.call(null,p,ch,null,null);
});

cljs.core.async.split.cljs$core$IFn$_invoke$arity$4 = (function (p,ch,t_buf_or_n,f_buf_or_n){
var tc = cljs.core.async.chan.call(null,t_buf_or_n);
var fc = cljs.core.async.chan.call(null,f_buf_or_n);
var c__8790__auto___9240 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___9240,tc,fc){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___9240,tc,fc){
return (function (state_9217){
var state_val_9218 = (state_9217[(1)]);
if((state_val_9218 === (7))){
var inst_9213 = (state_9217[(2)]);
var state_9217__$1 = state_9217;
var statearr_9219_9241 = state_9217__$1;
(statearr_9219_9241[(2)] = inst_9213);

(statearr_9219_9241[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (1))){
var state_9217__$1 = state_9217;
var statearr_9220_9242 = state_9217__$1;
(statearr_9220_9242[(2)] = null);

(statearr_9220_9242[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (4))){
var inst_9194 = (state_9217[(7)]);
var inst_9194__$1 = (state_9217[(2)]);
var inst_9195 = (inst_9194__$1 == null);
var state_9217__$1 = (function (){var statearr_9221 = state_9217;
(statearr_9221[(7)] = inst_9194__$1);

return statearr_9221;
})();
if(cljs.core.truth_(inst_9195)){
var statearr_9222_9243 = state_9217__$1;
(statearr_9222_9243[(1)] = (5));

} else {
var statearr_9223_9244 = state_9217__$1;
(statearr_9223_9244[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (13))){
var state_9217__$1 = state_9217;
var statearr_9224_9245 = state_9217__$1;
(statearr_9224_9245[(2)] = null);

(statearr_9224_9245[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (6))){
var inst_9194 = (state_9217[(7)]);
var inst_9200 = p.call(null,inst_9194);
var state_9217__$1 = state_9217;
if(cljs.core.truth_(inst_9200)){
var statearr_9225_9246 = state_9217__$1;
(statearr_9225_9246[(1)] = (9));

} else {
var statearr_9226_9247 = state_9217__$1;
(statearr_9226_9247[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (3))){
var inst_9215 = (state_9217[(2)]);
var state_9217__$1 = state_9217;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9217__$1,inst_9215);
} else {
if((state_val_9218 === (12))){
var state_9217__$1 = state_9217;
var statearr_9227_9248 = state_9217__$1;
(statearr_9227_9248[(2)] = null);

(statearr_9227_9248[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (2))){
var state_9217__$1 = state_9217;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9217__$1,(4),ch);
} else {
if((state_val_9218 === (11))){
var inst_9194 = (state_9217[(7)]);
var inst_9204 = (state_9217[(2)]);
var state_9217__$1 = state_9217;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_9217__$1,(8),inst_9204,inst_9194);
} else {
if((state_val_9218 === (9))){
var state_9217__$1 = state_9217;
var statearr_9228_9249 = state_9217__$1;
(statearr_9228_9249[(2)] = tc);

(statearr_9228_9249[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (5))){
var inst_9197 = cljs.core.async.close_BANG_.call(null,tc);
var inst_9198 = cljs.core.async.close_BANG_.call(null,fc);
var state_9217__$1 = (function (){var statearr_9229 = state_9217;
(statearr_9229[(8)] = inst_9197);

return statearr_9229;
})();
var statearr_9230_9250 = state_9217__$1;
(statearr_9230_9250[(2)] = inst_9198);

(statearr_9230_9250[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (14))){
var inst_9211 = (state_9217[(2)]);
var state_9217__$1 = state_9217;
var statearr_9231_9251 = state_9217__$1;
(statearr_9231_9251[(2)] = inst_9211);

(statearr_9231_9251[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (10))){
var state_9217__$1 = state_9217;
var statearr_9232_9252 = state_9217__$1;
(statearr_9232_9252[(2)] = fc);

(statearr_9232_9252[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9218 === (8))){
var inst_9206 = (state_9217[(2)]);
var state_9217__$1 = state_9217;
if(cljs.core.truth_(inst_9206)){
var statearr_9233_9253 = state_9217__$1;
(statearr_9233_9253[(1)] = (12));

} else {
var statearr_9234_9254 = state_9217__$1;
(statearr_9234_9254[(1)] = (13));

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
});})(c__8790__auto___9240,tc,fc))
;
return ((function (switch__8695__auto__,c__8790__auto___9240,tc,fc){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_9235 = [null,null,null,null,null,null,null,null,null];
(statearr_9235[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_9235[(1)] = (1));

return statearr_9235;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_9217){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9217);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9236){if((e9236 instanceof Object)){
var ex__8699__auto__ = e9236;
var statearr_9237_9255 = state_9217;
(statearr_9237_9255[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9217);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9236;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9256 = state_9217;
state_9217 = G__9256;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_9217){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_9217);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___9240,tc,fc))
})();
var state__8792__auto__ = (function (){var statearr_9238 = f__8791__auto__.call(null);
(statearr_9238[(6)] = c__8790__auto___9240);

return statearr_9238;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___9240,tc,fc))
);


return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [tc,fc], null);
});

cljs.core.async.split.cljs$lang$maxFixedArity = 4;

/**
 * f should be a function of 2 arguments. Returns a channel containing
 *   the single result of applying f to init and the first item from the
 *   channel, then applying f to that result and the 2nd item, etc. If
 *   the channel closes without yielding items, returns init and f is not
 *   called. ch must close before reduce produces a result.
 */
cljs.core.async.reduce = (function cljs$core$async$reduce(f,init,ch){
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__){
return (function (state_9277){
var state_val_9278 = (state_9277[(1)]);
if((state_val_9278 === (7))){
var inst_9273 = (state_9277[(2)]);
var state_9277__$1 = state_9277;
var statearr_9279_9297 = state_9277__$1;
(statearr_9279_9297[(2)] = inst_9273);

(statearr_9279_9297[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9278 === (1))){
var inst_9257 = init;
var state_9277__$1 = (function (){var statearr_9280 = state_9277;
(statearr_9280[(7)] = inst_9257);

return statearr_9280;
})();
var statearr_9281_9298 = state_9277__$1;
(statearr_9281_9298[(2)] = null);

(statearr_9281_9298[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9278 === (4))){
var inst_9260 = (state_9277[(8)]);
var inst_9260__$1 = (state_9277[(2)]);
var inst_9261 = (inst_9260__$1 == null);
var state_9277__$1 = (function (){var statearr_9282 = state_9277;
(statearr_9282[(8)] = inst_9260__$1);

return statearr_9282;
})();
if(cljs.core.truth_(inst_9261)){
var statearr_9283_9299 = state_9277__$1;
(statearr_9283_9299[(1)] = (5));

} else {
var statearr_9284_9300 = state_9277__$1;
(statearr_9284_9300[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9278 === (6))){
var inst_9257 = (state_9277[(7)]);
var inst_9264 = (state_9277[(9)]);
var inst_9260 = (state_9277[(8)]);
var inst_9264__$1 = f.call(null,inst_9257,inst_9260);
var inst_9265 = cljs.core.reduced_QMARK_.call(null,inst_9264__$1);
var state_9277__$1 = (function (){var statearr_9285 = state_9277;
(statearr_9285[(9)] = inst_9264__$1);

return statearr_9285;
})();
if(inst_9265){
var statearr_9286_9301 = state_9277__$1;
(statearr_9286_9301[(1)] = (8));

} else {
var statearr_9287_9302 = state_9277__$1;
(statearr_9287_9302[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9278 === (3))){
var inst_9275 = (state_9277[(2)]);
var state_9277__$1 = state_9277;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9277__$1,inst_9275);
} else {
if((state_val_9278 === (2))){
var state_9277__$1 = state_9277;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9277__$1,(4),ch);
} else {
if((state_val_9278 === (9))){
var inst_9264 = (state_9277[(9)]);
var inst_9257 = inst_9264;
var state_9277__$1 = (function (){var statearr_9288 = state_9277;
(statearr_9288[(7)] = inst_9257);

return statearr_9288;
})();
var statearr_9289_9303 = state_9277__$1;
(statearr_9289_9303[(2)] = null);

(statearr_9289_9303[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9278 === (5))){
var inst_9257 = (state_9277[(7)]);
var state_9277__$1 = state_9277;
var statearr_9290_9304 = state_9277__$1;
(statearr_9290_9304[(2)] = inst_9257);

(statearr_9290_9304[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9278 === (10))){
var inst_9271 = (state_9277[(2)]);
var state_9277__$1 = state_9277;
var statearr_9291_9305 = state_9277__$1;
(statearr_9291_9305[(2)] = inst_9271);

(statearr_9291_9305[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9278 === (8))){
var inst_9264 = (state_9277[(9)]);
var inst_9267 = cljs.core.deref.call(null,inst_9264);
var state_9277__$1 = state_9277;
var statearr_9292_9306 = state_9277__$1;
(statearr_9292_9306[(2)] = inst_9267);

(statearr_9292_9306[(1)] = (10));


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
});})(c__8790__auto__))
;
return ((function (switch__8695__auto__,c__8790__auto__){
return (function() {
var cljs$core$async$reduce_$_state_machine__8696__auto__ = null;
var cljs$core$async$reduce_$_state_machine__8696__auto____0 = (function (){
var statearr_9293 = [null,null,null,null,null,null,null,null,null,null];
(statearr_9293[(0)] = cljs$core$async$reduce_$_state_machine__8696__auto__);

(statearr_9293[(1)] = (1));

return statearr_9293;
});
var cljs$core$async$reduce_$_state_machine__8696__auto____1 = (function (state_9277){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9277);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9294){if((e9294 instanceof Object)){
var ex__8699__auto__ = e9294;
var statearr_9295_9307 = state_9277;
(statearr_9295_9307[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9277);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9294;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9308 = state_9277;
state_9277 = G__9308;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$reduce_$_state_machine__8696__auto__ = function(state_9277){
switch(arguments.length){
case 0:
return cljs$core$async$reduce_$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$reduce_$_state_machine__8696__auto____1.call(this,state_9277);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$reduce_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$reduce_$_state_machine__8696__auto____0;
cljs$core$async$reduce_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$reduce_$_state_machine__8696__auto____1;
return cljs$core$async$reduce_$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__))
})();
var state__8792__auto__ = (function (){var statearr_9296 = f__8791__auto__.call(null);
(statearr_9296[(6)] = c__8790__auto__);

return statearr_9296;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__))
);

return c__8790__auto__;
});
/**
 * async/reduces a channel with a transformation (xform f).
 *   Returns a channel containing the result.  ch must close before
 *   transduce produces a result.
 */
cljs.core.async.transduce = (function cljs$core$async$transduce(xform,f,init,ch){
var f__$1 = xform.call(null,f);
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__,f__$1){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__,f__$1){
return (function (state_9314){
var state_val_9315 = (state_9314[(1)]);
if((state_val_9315 === (1))){
var inst_9309 = cljs.core.async.reduce.call(null,f__$1,init,ch);
var state_9314__$1 = state_9314;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9314__$1,(2),inst_9309);
} else {
if((state_val_9315 === (2))){
var inst_9311 = (state_9314[(2)]);
var inst_9312 = f__$1.call(null,inst_9311);
var state_9314__$1 = state_9314;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9314__$1,inst_9312);
} else {
return null;
}
}
});})(c__8790__auto__,f__$1))
;
return ((function (switch__8695__auto__,c__8790__auto__,f__$1){
return (function() {
var cljs$core$async$transduce_$_state_machine__8696__auto__ = null;
var cljs$core$async$transduce_$_state_machine__8696__auto____0 = (function (){
var statearr_9316 = [null,null,null,null,null,null,null];
(statearr_9316[(0)] = cljs$core$async$transduce_$_state_machine__8696__auto__);

(statearr_9316[(1)] = (1));

return statearr_9316;
});
var cljs$core$async$transduce_$_state_machine__8696__auto____1 = (function (state_9314){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9314);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9317){if((e9317 instanceof Object)){
var ex__8699__auto__ = e9317;
var statearr_9318_9320 = state_9314;
(statearr_9318_9320[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9314);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9317;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9321 = state_9314;
state_9314 = G__9321;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$transduce_$_state_machine__8696__auto__ = function(state_9314){
switch(arguments.length){
case 0:
return cljs$core$async$transduce_$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$transduce_$_state_machine__8696__auto____1.call(this,state_9314);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$transduce_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$transduce_$_state_machine__8696__auto____0;
cljs$core$async$transduce_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$transduce_$_state_machine__8696__auto____1;
return cljs$core$async$transduce_$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__,f__$1))
})();
var state__8792__auto__ = (function (){var statearr_9319 = f__8791__auto__.call(null);
(statearr_9319[(6)] = c__8790__auto__);

return statearr_9319;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__,f__$1))
);

return c__8790__auto__;
});
/**
 * Puts the contents of coll into the supplied channel.
 * 
 *   By default the channel will be closed after the items are copied,
 *   but can be determined by the close? parameter.
 * 
 *   Returns a channel which will close after the items are copied.
 */
cljs.core.async.onto_chan = (function cljs$core$async$onto_chan(var_args){
var G__9323 = arguments.length;
switch (G__9323) {
case 2:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2 = (function (ch,coll){
return cljs.core.async.onto_chan.call(null,ch,coll,true);
});

cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3 = (function (ch,coll,close_QMARK_){
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__){
return (function (state_9348){
var state_val_9349 = (state_9348[(1)]);
if((state_val_9349 === (7))){
var inst_9330 = (state_9348[(2)]);
var state_9348__$1 = state_9348;
var statearr_9350_9371 = state_9348__$1;
(statearr_9350_9371[(2)] = inst_9330);

(statearr_9350_9371[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (1))){
var inst_9324 = cljs.core.seq.call(null,coll);
var inst_9325 = inst_9324;
var state_9348__$1 = (function (){var statearr_9351 = state_9348;
(statearr_9351[(7)] = inst_9325);

return statearr_9351;
})();
var statearr_9352_9372 = state_9348__$1;
(statearr_9352_9372[(2)] = null);

(statearr_9352_9372[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (4))){
var inst_9325 = (state_9348[(7)]);
var inst_9328 = cljs.core.first.call(null,inst_9325);
var state_9348__$1 = state_9348;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_9348__$1,(7),ch,inst_9328);
} else {
if((state_val_9349 === (13))){
var inst_9342 = (state_9348[(2)]);
var state_9348__$1 = state_9348;
var statearr_9353_9373 = state_9348__$1;
(statearr_9353_9373[(2)] = inst_9342);

(statearr_9353_9373[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (6))){
var inst_9333 = (state_9348[(2)]);
var state_9348__$1 = state_9348;
if(cljs.core.truth_(inst_9333)){
var statearr_9354_9374 = state_9348__$1;
(statearr_9354_9374[(1)] = (8));

} else {
var statearr_9355_9375 = state_9348__$1;
(statearr_9355_9375[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (3))){
var inst_9346 = (state_9348[(2)]);
var state_9348__$1 = state_9348;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9348__$1,inst_9346);
} else {
if((state_val_9349 === (12))){
var state_9348__$1 = state_9348;
var statearr_9356_9376 = state_9348__$1;
(statearr_9356_9376[(2)] = null);

(statearr_9356_9376[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (2))){
var inst_9325 = (state_9348[(7)]);
var state_9348__$1 = state_9348;
if(cljs.core.truth_(inst_9325)){
var statearr_9357_9377 = state_9348__$1;
(statearr_9357_9377[(1)] = (4));

} else {
var statearr_9358_9378 = state_9348__$1;
(statearr_9358_9378[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (11))){
var inst_9339 = cljs.core.async.close_BANG_.call(null,ch);
var state_9348__$1 = state_9348;
var statearr_9359_9379 = state_9348__$1;
(statearr_9359_9379[(2)] = inst_9339);

(statearr_9359_9379[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (9))){
var state_9348__$1 = state_9348;
if(cljs.core.truth_(close_QMARK_)){
var statearr_9360_9380 = state_9348__$1;
(statearr_9360_9380[(1)] = (11));

} else {
var statearr_9361_9381 = state_9348__$1;
(statearr_9361_9381[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (5))){
var inst_9325 = (state_9348[(7)]);
var state_9348__$1 = state_9348;
var statearr_9362_9382 = state_9348__$1;
(statearr_9362_9382[(2)] = inst_9325);

(statearr_9362_9382[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (10))){
var inst_9344 = (state_9348[(2)]);
var state_9348__$1 = state_9348;
var statearr_9363_9383 = state_9348__$1;
(statearr_9363_9383[(2)] = inst_9344);

(statearr_9363_9383[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9349 === (8))){
var inst_9325 = (state_9348[(7)]);
var inst_9335 = cljs.core.next.call(null,inst_9325);
var inst_9325__$1 = inst_9335;
var state_9348__$1 = (function (){var statearr_9364 = state_9348;
(statearr_9364[(7)] = inst_9325__$1);

return statearr_9364;
})();
var statearr_9365_9384 = state_9348__$1;
(statearr_9365_9384[(2)] = null);

(statearr_9365_9384[(1)] = (2));


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
});})(c__8790__auto__))
;
return ((function (switch__8695__auto__,c__8790__auto__){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_9366 = [null,null,null,null,null,null,null,null];
(statearr_9366[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_9366[(1)] = (1));

return statearr_9366;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_9348){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9348);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9367){if((e9367 instanceof Object)){
var ex__8699__auto__ = e9367;
var statearr_9368_9385 = state_9348;
(statearr_9368_9385[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9348);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9367;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9386 = state_9348;
state_9348 = G__9386;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_9348){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_9348);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__))
})();
var state__8792__auto__ = (function (){var statearr_9369 = f__8791__auto__.call(null);
(statearr_9369[(6)] = c__8790__auto__);

return statearr_9369;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__))
);

return c__8790__auto__;
});

cljs.core.async.onto_chan.cljs$lang$maxFixedArity = 3;

/**
 * Creates and returns a channel which contains the contents of coll,
 *   closing when exhausted.
 */
cljs.core.async.to_chan = (function cljs$core$async$to_chan(coll){
var ch = cljs.core.async.chan.call(null,cljs.core.bounded_count.call(null,(100),coll));
cljs.core.async.onto_chan.call(null,ch,coll);

return ch;
});

/**
 * @interface
 */
cljs.core.async.Mux = function(){};

cljs.core.async.muxch_STAR_ = (function cljs$core$async$muxch_STAR_(_){
if((((!((_ == null)))) && ((!((_.cljs$core$async$Mux$muxch_STAR_$arity$1 == null)))))){
return _.cljs$core$async$Mux$muxch_STAR_$arity$1(_);
} else {
var x__4433__auto__ = (((_ == null))?null:_);
var m__4434__auto__ = (cljs.core.async.muxch_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,_);
} else {
var m__4431__auto__ = (cljs.core.async.muxch_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,_);
} else {
throw cljs.core.missing_protocol.call(null,"Mux.muxch*",_);
}
}
}
});


/**
 * @interface
 */
cljs.core.async.Mult = function(){};

cljs.core.async.tap_STAR_ = (function cljs$core$async$tap_STAR_(m,ch,close_QMARK_){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$tap_STAR_$arity$3 == null)))))){
return m.cljs$core$async$Mult$tap_STAR_$arity$3(m,ch,close_QMARK_);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.tap_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch,close_QMARK_);
} else {
var m__4431__auto__ = (cljs.core.async.tap_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch,close_QMARK_);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.tap*",m);
}
}
}
});

cljs.core.async.untap_STAR_ = (function cljs$core$async$untap_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mult$untap_STAR_$arity$2(m,ch);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.untap_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch);
} else {
var m__4431__auto__ = (cljs.core.async.untap_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.untap*",m);
}
}
}
});

cljs.core.async.untap_all_STAR_ = (function cljs$core$async$untap_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mult$untap_all_STAR_$arity$1(m);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.untap_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m);
} else {
var m__4431__auto__ = (cljs.core.async.untap_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.untap-all*",m);
}
}
}
});

/**
 * Creates and returns a mult(iple) of the supplied channel. Channels
 *   containing copies of the channel can be created with 'tap', and
 *   detached with 'untap'.
 * 
 *   Each item is distributed to all taps in parallel and synchronously,
 *   i.e. each tap must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow taps from holding up the mult.
 * 
 *   Items received when there are no taps get dropped.
 * 
 *   If a tap puts to a closed channel, it will be removed from the mult.
 */
cljs.core.async.mult = (function cljs$core$async$mult(ch){
var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var m = (function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async9387 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.Mult}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async9387 = (function (ch,cs,meta9388){
this.ch = ch;
this.cs = cs;
this.meta9388 = meta9388;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs){
return (function (_9389,meta9388__$1){
var self__ = this;
var _9389__$1 = this;
return (new cljs.core.async.t_cljs$core$async9387(self__.ch,self__.cs,meta9388__$1));
});})(cs))
;

cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs){
return (function (_9389){
var self__ = this;
var _9389__$1 = this;
return self__.meta9388;
});})(cs))
;

cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
});})(cs))
;

cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$async$Mult$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = ((function (cs){
return (function (_,ch__$1,close_QMARK_){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch__$1,close_QMARK_);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = ((function (cs){
return (function (_,ch__$1){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch__$1);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async9387.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = ((function (cs){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async9387.getBasis = ((function (cs){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"meta9388","meta9388",1208317834,null)], null);
});})(cs))
;

cljs.core.async.t_cljs$core$async9387.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async9387.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async9387";

cljs.core.async.t_cljs$core$async9387.cljs$lang$ctorPrWriter = ((function (cs){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async9387");
});})(cs))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async9387.
 */
cljs.core.async.__GT_t_cljs$core$async9387 = ((function (cs){
return (function cljs$core$async$mult_$___GT_t_cljs$core$async9387(ch__$1,cs__$1,meta9388){
return (new cljs.core.async.t_cljs$core$async9387(ch__$1,cs__$1,meta9388));
});})(cs))
;

}

return (new cljs.core.async.t_cljs$core$async9387(ch,cs,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var dchan = cljs.core.async.chan.call(null,(1));
var dctr = cljs.core.atom.call(null,null);
var done = ((function (cs,m,dchan,dctr){
return (function (_){
if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.call(null,dchan,true);
} else {
return null;
}
});})(cs,m,dchan,dctr))
;
var c__8790__auto___9609 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___9609,cs,m,dchan,dctr,done){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___9609,cs,m,dchan,dctr,done){
return (function (state_9524){
var state_val_9525 = (state_9524[(1)]);
if((state_val_9525 === (7))){
var inst_9520 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9526_9610 = state_9524__$1;
(statearr_9526_9610[(2)] = inst_9520);

(statearr_9526_9610[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (20))){
var inst_9423 = (state_9524[(7)]);
var inst_9435 = cljs.core.first.call(null,inst_9423);
var inst_9436 = cljs.core.nth.call(null,inst_9435,(0),null);
var inst_9437 = cljs.core.nth.call(null,inst_9435,(1),null);
var state_9524__$1 = (function (){var statearr_9527 = state_9524;
(statearr_9527[(8)] = inst_9436);

return statearr_9527;
})();
if(cljs.core.truth_(inst_9437)){
var statearr_9528_9611 = state_9524__$1;
(statearr_9528_9611[(1)] = (22));

} else {
var statearr_9529_9612 = state_9524__$1;
(statearr_9529_9612[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (27))){
var inst_9392 = (state_9524[(9)]);
var inst_9472 = (state_9524[(10)]);
var inst_9467 = (state_9524[(11)]);
var inst_9465 = (state_9524[(12)]);
var inst_9472__$1 = cljs.core._nth.call(null,inst_9465,inst_9467);
var inst_9473 = cljs.core.async.put_BANG_.call(null,inst_9472__$1,inst_9392,done);
var state_9524__$1 = (function (){var statearr_9530 = state_9524;
(statearr_9530[(10)] = inst_9472__$1);

return statearr_9530;
})();
if(cljs.core.truth_(inst_9473)){
var statearr_9531_9613 = state_9524__$1;
(statearr_9531_9613[(1)] = (30));

} else {
var statearr_9532_9614 = state_9524__$1;
(statearr_9532_9614[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (1))){
var state_9524__$1 = state_9524;
var statearr_9533_9615 = state_9524__$1;
(statearr_9533_9615[(2)] = null);

(statearr_9533_9615[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (24))){
var inst_9423 = (state_9524[(7)]);
var inst_9442 = (state_9524[(2)]);
var inst_9443 = cljs.core.next.call(null,inst_9423);
var inst_9401 = inst_9443;
var inst_9402 = null;
var inst_9403 = (0);
var inst_9404 = (0);
var state_9524__$1 = (function (){var statearr_9534 = state_9524;
(statearr_9534[(13)] = inst_9402);

(statearr_9534[(14)] = inst_9403);

(statearr_9534[(15)] = inst_9442);

(statearr_9534[(16)] = inst_9401);

(statearr_9534[(17)] = inst_9404);

return statearr_9534;
})();
var statearr_9535_9616 = state_9524__$1;
(statearr_9535_9616[(2)] = null);

(statearr_9535_9616[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (39))){
var state_9524__$1 = state_9524;
var statearr_9539_9617 = state_9524__$1;
(statearr_9539_9617[(2)] = null);

(statearr_9539_9617[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (4))){
var inst_9392 = (state_9524[(9)]);
var inst_9392__$1 = (state_9524[(2)]);
var inst_9393 = (inst_9392__$1 == null);
var state_9524__$1 = (function (){var statearr_9540 = state_9524;
(statearr_9540[(9)] = inst_9392__$1);

return statearr_9540;
})();
if(cljs.core.truth_(inst_9393)){
var statearr_9541_9618 = state_9524__$1;
(statearr_9541_9618[(1)] = (5));

} else {
var statearr_9542_9619 = state_9524__$1;
(statearr_9542_9619[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (15))){
var inst_9402 = (state_9524[(13)]);
var inst_9403 = (state_9524[(14)]);
var inst_9401 = (state_9524[(16)]);
var inst_9404 = (state_9524[(17)]);
var inst_9419 = (state_9524[(2)]);
var inst_9420 = (inst_9404 + (1));
var tmp9536 = inst_9402;
var tmp9537 = inst_9403;
var tmp9538 = inst_9401;
var inst_9401__$1 = tmp9538;
var inst_9402__$1 = tmp9536;
var inst_9403__$1 = tmp9537;
var inst_9404__$1 = inst_9420;
var state_9524__$1 = (function (){var statearr_9543 = state_9524;
(statearr_9543[(18)] = inst_9419);

(statearr_9543[(13)] = inst_9402__$1);

(statearr_9543[(14)] = inst_9403__$1);

(statearr_9543[(16)] = inst_9401__$1);

(statearr_9543[(17)] = inst_9404__$1);

return statearr_9543;
})();
var statearr_9544_9620 = state_9524__$1;
(statearr_9544_9620[(2)] = null);

(statearr_9544_9620[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (21))){
var inst_9446 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9548_9621 = state_9524__$1;
(statearr_9548_9621[(2)] = inst_9446);

(statearr_9548_9621[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (31))){
var inst_9472 = (state_9524[(10)]);
var inst_9476 = done.call(null,null);
var inst_9477 = cljs.core.async.untap_STAR_.call(null,m,inst_9472);
var state_9524__$1 = (function (){var statearr_9549 = state_9524;
(statearr_9549[(19)] = inst_9476);

return statearr_9549;
})();
var statearr_9550_9622 = state_9524__$1;
(statearr_9550_9622[(2)] = inst_9477);

(statearr_9550_9622[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (32))){
var inst_9466 = (state_9524[(20)]);
var inst_9467 = (state_9524[(11)]);
var inst_9464 = (state_9524[(21)]);
var inst_9465 = (state_9524[(12)]);
var inst_9479 = (state_9524[(2)]);
var inst_9480 = (inst_9467 + (1));
var tmp9545 = inst_9466;
var tmp9546 = inst_9464;
var tmp9547 = inst_9465;
var inst_9464__$1 = tmp9546;
var inst_9465__$1 = tmp9547;
var inst_9466__$1 = tmp9545;
var inst_9467__$1 = inst_9480;
var state_9524__$1 = (function (){var statearr_9551 = state_9524;
(statearr_9551[(20)] = inst_9466__$1);

(statearr_9551[(11)] = inst_9467__$1);

(statearr_9551[(21)] = inst_9464__$1);

(statearr_9551[(22)] = inst_9479);

(statearr_9551[(12)] = inst_9465__$1);

return statearr_9551;
})();
var statearr_9552_9623 = state_9524__$1;
(statearr_9552_9623[(2)] = null);

(statearr_9552_9623[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (40))){
var inst_9492 = (state_9524[(23)]);
var inst_9496 = done.call(null,null);
var inst_9497 = cljs.core.async.untap_STAR_.call(null,m,inst_9492);
var state_9524__$1 = (function (){var statearr_9553 = state_9524;
(statearr_9553[(24)] = inst_9496);

return statearr_9553;
})();
var statearr_9554_9624 = state_9524__$1;
(statearr_9554_9624[(2)] = inst_9497);

(statearr_9554_9624[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (33))){
var inst_9483 = (state_9524[(25)]);
var inst_9485 = cljs.core.chunked_seq_QMARK_.call(null,inst_9483);
var state_9524__$1 = state_9524;
if(inst_9485){
var statearr_9555_9625 = state_9524__$1;
(statearr_9555_9625[(1)] = (36));

} else {
var statearr_9556_9626 = state_9524__$1;
(statearr_9556_9626[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (13))){
var inst_9413 = (state_9524[(26)]);
var inst_9416 = cljs.core.async.close_BANG_.call(null,inst_9413);
var state_9524__$1 = state_9524;
var statearr_9557_9627 = state_9524__$1;
(statearr_9557_9627[(2)] = inst_9416);

(statearr_9557_9627[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (22))){
var inst_9436 = (state_9524[(8)]);
var inst_9439 = cljs.core.async.close_BANG_.call(null,inst_9436);
var state_9524__$1 = state_9524;
var statearr_9558_9628 = state_9524__$1;
(statearr_9558_9628[(2)] = inst_9439);

(statearr_9558_9628[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (36))){
var inst_9483 = (state_9524[(25)]);
var inst_9487 = cljs.core.chunk_first.call(null,inst_9483);
var inst_9488 = cljs.core.chunk_rest.call(null,inst_9483);
var inst_9489 = cljs.core.count.call(null,inst_9487);
var inst_9464 = inst_9488;
var inst_9465 = inst_9487;
var inst_9466 = inst_9489;
var inst_9467 = (0);
var state_9524__$1 = (function (){var statearr_9559 = state_9524;
(statearr_9559[(20)] = inst_9466);

(statearr_9559[(11)] = inst_9467);

(statearr_9559[(21)] = inst_9464);

(statearr_9559[(12)] = inst_9465);

return statearr_9559;
})();
var statearr_9560_9629 = state_9524__$1;
(statearr_9560_9629[(2)] = null);

(statearr_9560_9629[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (41))){
var inst_9483 = (state_9524[(25)]);
var inst_9499 = (state_9524[(2)]);
var inst_9500 = cljs.core.next.call(null,inst_9483);
var inst_9464 = inst_9500;
var inst_9465 = null;
var inst_9466 = (0);
var inst_9467 = (0);
var state_9524__$1 = (function (){var statearr_9561 = state_9524;
(statearr_9561[(27)] = inst_9499);

(statearr_9561[(20)] = inst_9466);

(statearr_9561[(11)] = inst_9467);

(statearr_9561[(21)] = inst_9464);

(statearr_9561[(12)] = inst_9465);

return statearr_9561;
})();
var statearr_9562_9630 = state_9524__$1;
(statearr_9562_9630[(2)] = null);

(statearr_9562_9630[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (43))){
var state_9524__$1 = state_9524;
var statearr_9563_9631 = state_9524__$1;
(statearr_9563_9631[(2)] = null);

(statearr_9563_9631[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (29))){
var inst_9508 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9564_9632 = state_9524__$1;
(statearr_9564_9632[(2)] = inst_9508);

(statearr_9564_9632[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (44))){
var inst_9517 = (state_9524[(2)]);
var state_9524__$1 = (function (){var statearr_9565 = state_9524;
(statearr_9565[(28)] = inst_9517);

return statearr_9565;
})();
var statearr_9566_9633 = state_9524__$1;
(statearr_9566_9633[(2)] = null);

(statearr_9566_9633[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (6))){
var inst_9456 = (state_9524[(29)]);
var inst_9455 = cljs.core.deref.call(null,cs);
var inst_9456__$1 = cljs.core.keys.call(null,inst_9455);
var inst_9457 = cljs.core.count.call(null,inst_9456__$1);
var inst_9458 = cljs.core.reset_BANG_.call(null,dctr,inst_9457);
var inst_9463 = cljs.core.seq.call(null,inst_9456__$1);
var inst_9464 = inst_9463;
var inst_9465 = null;
var inst_9466 = (0);
var inst_9467 = (0);
var state_9524__$1 = (function (){var statearr_9567 = state_9524;
(statearr_9567[(29)] = inst_9456__$1);

(statearr_9567[(20)] = inst_9466);

(statearr_9567[(11)] = inst_9467);

(statearr_9567[(21)] = inst_9464);

(statearr_9567[(30)] = inst_9458);

(statearr_9567[(12)] = inst_9465);

return statearr_9567;
})();
var statearr_9568_9634 = state_9524__$1;
(statearr_9568_9634[(2)] = null);

(statearr_9568_9634[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (28))){
var inst_9483 = (state_9524[(25)]);
var inst_9464 = (state_9524[(21)]);
var inst_9483__$1 = cljs.core.seq.call(null,inst_9464);
var state_9524__$1 = (function (){var statearr_9569 = state_9524;
(statearr_9569[(25)] = inst_9483__$1);

return statearr_9569;
})();
if(inst_9483__$1){
var statearr_9570_9635 = state_9524__$1;
(statearr_9570_9635[(1)] = (33));

} else {
var statearr_9571_9636 = state_9524__$1;
(statearr_9571_9636[(1)] = (34));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (25))){
var inst_9466 = (state_9524[(20)]);
var inst_9467 = (state_9524[(11)]);
var inst_9469 = (inst_9467 < inst_9466);
var inst_9470 = inst_9469;
var state_9524__$1 = state_9524;
if(cljs.core.truth_(inst_9470)){
var statearr_9572_9637 = state_9524__$1;
(statearr_9572_9637[(1)] = (27));

} else {
var statearr_9573_9638 = state_9524__$1;
(statearr_9573_9638[(1)] = (28));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (34))){
var state_9524__$1 = state_9524;
var statearr_9574_9639 = state_9524__$1;
(statearr_9574_9639[(2)] = null);

(statearr_9574_9639[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (17))){
var state_9524__$1 = state_9524;
var statearr_9575_9640 = state_9524__$1;
(statearr_9575_9640[(2)] = null);

(statearr_9575_9640[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (3))){
var inst_9522 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9524__$1,inst_9522);
} else {
if((state_val_9525 === (12))){
var inst_9451 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9576_9641 = state_9524__$1;
(statearr_9576_9641[(2)] = inst_9451);

(statearr_9576_9641[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (2))){
var state_9524__$1 = state_9524;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9524__$1,(4),ch);
} else {
if((state_val_9525 === (23))){
var state_9524__$1 = state_9524;
var statearr_9577_9642 = state_9524__$1;
(statearr_9577_9642[(2)] = null);

(statearr_9577_9642[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (35))){
var inst_9506 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9578_9643 = state_9524__$1;
(statearr_9578_9643[(2)] = inst_9506);

(statearr_9578_9643[(1)] = (29));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (19))){
var inst_9423 = (state_9524[(7)]);
var inst_9427 = cljs.core.chunk_first.call(null,inst_9423);
var inst_9428 = cljs.core.chunk_rest.call(null,inst_9423);
var inst_9429 = cljs.core.count.call(null,inst_9427);
var inst_9401 = inst_9428;
var inst_9402 = inst_9427;
var inst_9403 = inst_9429;
var inst_9404 = (0);
var state_9524__$1 = (function (){var statearr_9579 = state_9524;
(statearr_9579[(13)] = inst_9402);

(statearr_9579[(14)] = inst_9403);

(statearr_9579[(16)] = inst_9401);

(statearr_9579[(17)] = inst_9404);

return statearr_9579;
})();
var statearr_9580_9644 = state_9524__$1;
(statearr_9580_9644[(2)] = null);

(statearr_9580_9644[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (11))){
var inst_9423 = (state_9524[(7)]);
var inst_9401 = (state_9524[(16)]);
var inst_9423__$1 = cljs.core.seq.call(null,inst_9401);
var state_9524__$1 = (function (){var statearr_9581 = state_9524;
(statearr_9581[(7)] = inst_9423__$1);

return statearr_9581;
})();
if(inst_9423__$1){
var statearr_9582_9645 = state_9524__$1;
(statearr_9582_9645[(1)] = (16));

} else {
var statearr_9583_9646 = state_9524__$1;
(statearr_9583_9646[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (9))){
var inst_9453 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9584_9647 = state_9524__$1;
(statearr_9584_9647[(2)] = inst_9453);

(statearr_9584_9647[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (5))){
var inst_9399 = cljs.core.deref.call(null,cs);
var inst_9400 = cljs.core.seq.call(null,inst_9399);
var inst_9401 = inst_9400;
var inst_9402 = null;
var inst_9403 = (0);
var inst_9404 = (0);
var state_9524__$1 = (function (){var statearr_9585 = state_9524;
(statearr_9585[(13)] = inst_9402);

(statearr_9585[(14)] = inst_9403);

(statearr_9585[(16)] = inst_9401);

(statearr_9585[(17)] = inst_9404);

return statearr_9585;
})();
var statearr_9586_9648 = state_9524__$1;
(statearr_9586_9648[(2)] = null);

(statearr_9586_9648[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (14))){
var state_9524__$1 = state_9524;
var statearr_9587_9649 = state_9524__$1;
(statearr_9587_9649[(2)] = null);

(statearr_9587_9649[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (45))){
var inst_9514 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9588_9650 = state_9524__$1;
(statearr_9588_9650[(2)] = inst_9514);

(statearr_9588_9650[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (26))){
var inst_9456 = (state_9524[(29)]);
var inst_9510 = (state_9524[(2)]);
var inst_9511 = cljs.core.seq.call(null,inst_9456);
var state_9524__$1 = (function (){var statearr_9589 = state_9524;
(statearr_9589[(31)] = inst_9510);

return statearr_9589;
})();
if(inst_9511){
var statearr_9590_9651 = state_9524__$1;
(statearr_9590_9651[(1)] = (42));

} else {
var statearr_9591_9652 = state_9524__$1;
(statearr_9591_9652[(1)] = (43));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (16))){
var inst_9423 = (state_9524[(7)]);
var inst_9425 = cljs.core.chunked_seq_QMARK_.call(null,inst_9423);
var state_9524__$1 = state_9524;
if(inst_9425){
var statearr_9592_9653 = state_9524__$1;
(statearr_9592_9653[(1)] = (19));

} else {
var statearr_9593_9654 = state_9524__$1;
(statearr_9593_9654[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (38))){
var inst_9503 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9594_9655 = state_9524__$1;
(statearr_9594_9655[(2)] = inst_9503);

(statearr_9594_9655[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (30))){
var state_9524__$1 = state_9524;
var statearr_9595_9656 = state_9524__$1;
(statearr_9595_9656[(2)] = null);

(statearr_9595_9656[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (10))){
var inst_9402 = (state_9524[(13)]);
var inst_9404 = (state_9524[(17)]);
var inst_9412 = cljs.core._nth.call(null,inst_9402,inst_9404);
var inst_9413 = cljs.core.nth.call(null,inst_9412,(0),null);
var inst_9414 = cljs.core.nth.call(null,inst_9412,(1),null);
var state_9524__$1 = (function (){var statearr_9596 = state_9524;
(statearr_9596[(26)] = inst_9413);

return statearr_9596;
})();
if(cljs.core.truth_(inst_9414)){
var statearr_9597_9657 = state_9524__$1;
(statearr_9597_9657[(1)] = (13));

} else {
var statearr_9598_9658 = state_9524__$1;
(statearr_9598_9658[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (18))){
var inst_9449 = (state_9524[(2)]);
var state_9524__$1 = state_9524;
var statearr_9599_9659 = state_9524__$1;
(statearr_9599_9659[(2)] = inst_9449);

(statearr_9599_9659[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (42))){
var state_9524__$1 = state_9524;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9524__$1,(45),dchan);
} else {
if((state_val_9525 === (37))){
var inst_9392 = (state_9524[(9)]);
var inst_9483 = (state_9524[(25)]);
var inst_9492 = (state_9524[(23)]);
var inst_9492__$1 = cljs.core.first.call(null,inst_9483);
var inst_9493 = cljs.core.async.put_BANG_.call(null,inst_9492__$1,inst_9392,done);
var state_9524__$1 = (function (){var statearr_9600 = state_9524;
(statearr_9600[(23)] = inst_9492__$1);

return statearr_9600;
})();
if(cljs.core.truth_(inst_9493)){
var statearr_9601_9660 = state_9524__$1;
(statearr_9601_9660[(1)] = (39));

} else {
var statearr_9602_9661 = state_9524__$1;
(statearr_9602_9661[(1)] = (40));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9525 === (8))){
var inst_9403 = (state_9524[(14)]);
var inst_9404 = (state_9524[(17)]);
var inst_9406 = (inst_9404 < inst_9403);
var inst_9407 = inst_9406;
var state_9524__$1 = state_9524;
if(cljs.core.truth_(inst_9407)){
var statearr_9603_9662 = state_9524__$1;
(statearr_9603_9662[(1)] = (10));

} else {
var statearr_9604_9663 = state_9524__$1;
(statearr_9604_9663[(1)] = (11));

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
});})(c__8790__auto___9609,cs,m,dchan,dctr,done))
;
return ((function (switch__8695__auto__,c__8790__auto___9609,cs,m,dchan,dctr,done){
return (function() {
var cljs$core$async$mult_$_state_machine__8696__auto__ = null;
var cljs$core$async$mult_$_state_machine__8696__auto____0 = (function (){
var statearr_9605 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_9605[(0)] = cljs$core$async$mult_$_state_machine__8696__auto__);

(statearr_9605[(1)] = (1));

return statearr_9605;
});
var cljs$core$async$mult_$_state_machine__8696__auto____1 = (function (state_9524){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9524);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9606){if((e9606 instanceof Object)){
var ex__8699__auto__ = e9606;
var statearr_9607_9664 = state_9524;
(statearr_9607_9664[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9524);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9606;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9665 = state_9524;
state_9524 = G__9665;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$mult_$_state_machine__8696__auto__ = function(state_9524){
switch(arguments.length){
case 0:
return cljs$core$async$mult_$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$mult_$_state_machine__8696__auto____1.call(this,state_9524);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mult_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mult_$_state_machine__8696__auto____0;
cljs$core$async$mult_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mult_$_state_machine__8696__auto____1;
return cljs$core$async$mult_$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___9609,cs,m,dchan,dctr,done))
})();
var state__8792__auto__ = (function (){var statearr_9608 = f__8791__auto__.call(null);
(statearr_9608[(6)] = c__8790__auto___9609);

return statearr_9608;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___9609,cs,m,dchan,dctr,done))
);


return m;
});
/**
 * Copies the mult source onto the supplied channel.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.tap = (function cljs$core$async$tap(var_args){
var G__9667 = arguments.length;
switch (G__9667) {
case 2:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2 = (function (mult,ch){
return cljs.core.async.tap.call(null,mult,ch,true);
});

cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3 = (function (mult,ch,close_QMARK_){
cljs.core.async.tap_STAR_.call(null,mult,ch,close_QMARK_);

return ch;
});

cljs.core.async.tap.cljs$lang$maxFixedArity = 3;

/**
 * Disconnects a target channel from a mult
 */
cljs.core.async.untap = (function cljs$core$async$untap(mult,ch){
return cljs.core.async.untap_STAR_.call(null,mult,ch);
});
/**
 * Disconnects all target channels from a mult
 */
cljs.core.async.untap_all = (function cljs$core$async$untap_all(mult){
return cljs.core.async.untap_all_STAR_.call(null,mult);
});

/**
 * @interface
 */
cljs.core.async.Mix = function(){};

cljs.core.async.admix_STAR_ = (function cljs$core$async$admix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$admix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$admix_STAR_$arity$2(m,ch);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.admix_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch);
} else {
var m__4431__auto__ = (cljs.core.async.admix_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.admix*",m);
}
}
}
});

cljs.core.async.unmix_STAR_ = (function cljs$core$async$unmix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$unmix_STAR_$arity$2(m,ch);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.unmix_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch);
} else {
var m__4431__auto__ = (cljs.core.async.unmix_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.unmix*",m);
}
}
}
});

cljs.core.async.unmix_all_STAR_ = (function cljs$core$async$unmix_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mix$unmix_all_STAR_$arity$1(m);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.unmix_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m);
} else {
var m__4431__auto__ = (cljs.core.async.unmix_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.unmix-all*",m);
}
}
}
});

cljs.core.async.toggle_STAR_ = (function cljs$core$async$toggle_STAR_(m,state_map){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$toggle_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$toggle_STAR_$arity$2(m,state_map);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.toggle_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,state_map);
} else {
var m__4431__auto__ = (cljs.core.async.toggle_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,state_map);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.toggle*",m);
}
}
}
});

cljs.core.async.solo_mode_STAR_ = (function cljs$core$async$solo_mode_STAR_(m,mode){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$solo_mode_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$solo_mode_STAR_$arity$2(m,mode);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.solo_mode_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,mode);
} else {
var m__4431__auto__ = (cljs.core.async.solo_mode_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,mode);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.solo-mode*",m);
}
}
}
});

cljs.core.async.ioc_alts_BANG_ = (function cljs$core$async$ioc_alts_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___9679 = arguments.length;
var i__4731__auto___9680 = (0);
while(true){
if((i__4731__auto___9680 < len__4730__auto___9679)){
args__4736__auto__.push((arguments[i__4731__auto___9680]));

var G__9681 = (i__4731__auto___9680 + (1));
i__4731__auto___9680 = G__9681;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((3) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((3)),(0),null)):null);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__4737__auto__);
});

cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (state,cont_block,ports,p__9673){
var map__9674 = p__9673;
var map__9674__$1 = (((((!((map__9674 == null))))?(((((map__9674.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__9674.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__9674):map__9674);
var opts = map__9674__$1;
var statearr_9676_9682 = state;
(statearr_9676_9682[(1)] = cont_block);


var temp__5720__auto__ = cljs.core.async.do_alts.call(null,((function (map__9674,map__9674__$1,opts){
return (function (val){
var statearr_9677_9683 = state;
(statearr_9677_9683[(2)] = val);


return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state);
});})(map__9674,map__9674__$1,opts))
,ports,opts);
if(cljs.core.truth_(temp__5720__auto__)){
var cb = temp__5720__auto__;
var statearr_9678_9684 = state;
(statearr_9678_9684[(2)] = cljs.core.deref.call(null,cb));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
});

cljs.core.async.ioc_alts_BANG_.cljs$lang$maxFixedArity = (3);

/** @this {Function} */
cljs.core.async.ioc_alts_BANG_.cljs$lang$applyTo = (function (seq9669){
var G__9670 = cljs.core.first.call(null,seq9669);
var seq9669__$1 = cljs.core.next.call(null,seq9669);
var G__9671 = cljs.core.first.call(null,seq9669__$1);
var seq9669__$2 = cljs.core.next.call(null,seq9669__$1);
var G__9672 = cljs.core.first.call(null,seq9669__$2);
var seq9669__$3 = cljs.core.next.call(null,seq9669__$2);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__9670,G__9671,G__9672,seq9669__$3);
});

/**
 * Creates and returns a mix of one or more input channels which will
 *   be put on the supplied out channel. Input sources can be added to
 *   the mix with 'admix', and removed with 'unmix'. A mix supports
 *   soloing, muting and pausing multiple inputs atomically using
 *   'toggle', and can solo using either muting or pausing as determined
 *   by 'solo-mode'.
 * 
 *   Each channel can have zero or more boolean modes set via 'toggle':
 * 
 *   :solo - when true, only this (ond other soloed) channel(s) will appear
 *        in the mix output channel. :mute and :pause states of soloed
 *        channels are ignored. If solo-mode is :mute, non-soloed
 *        channels are muted, if :pause, non-soloed channels are
 *        paused.
 * 
 *   :mute - muted channels will have their contents consumed but not included in the mix
 *   :pause - paused channels will not have their contents consumed (and thus also not included in the mix)
 */
cljs.core.async.mix = (function cljs$core$async$mix(out){
var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var solo_modes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pause","pause",-2095325672),null,new cljs.core.Keyword(null,"mute","mute",1151223646),null], null), null);
var attrs = cljs.core.conj.call(null,solo_modes,new cljs.core.Keyword(null,"solo","solo",-316350075));
var solo_mode = cljs.core.atom.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646));
var change = cljs.core.async.chan.call(null);
var changed = ((function (cs,solo_modes,attrs,solo_mode,change){
return (function (){
return cljs.core.async.put_BANG_.call(null,change,true);
});})(cs,solo_modes,attrs,solo_mode,change))
;
var pick = ((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (attr,chs){
return cljs.core.reduce_kv.call(null,((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (ret,c,v){
if(cljs.core.truth_(attr.call(null,v))){
return cljs.core.conj.call(null,ret,c);
} else {
return ret;
}
});})(cs,solo_modes,attrs,solo_mode,change,changed))
,cljs.core.PersistentHashSet.EMPTY,chs);
});})(cs,solo_modes,attrs,solo_mode,change,changed))
;
var calc_state = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick){
return (function (){
var chs = cljs.core.deref.call(null,cs);
var mode = cljs.core.deref.call(null,solo_mode);
var solos = pick.call(null,new cljs.core.Keyword(null,"solo","solo",-316350075),chs);
var pauses = pick.call(null,new cljs.core.Keyword(null,"pause","pause",-2095325672),chs);
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"solos","solos",1441458643),solos,new cljs.core.Keyword(null,"mutes","mutes",1068806309),pick.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646),chs),new cljs.core.Keyword(null,"reads","reads",-1215067361),cljs.core.conj.call(null,((((cljs.core._EQ_.call(null,mode,new cljs.core.Keyword(null,"pause","pause",-2095325672))) && ((!(cljs.core.empty_QMARK_.call(null,solos))))))?cljs.core.vec.call(null,solos):cljs.core.vec.call(null,cljs.core.remove.call(null,pauses,cljs.core.keys.call(null,chs)))),change)], null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick))
;
var m = (function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async9685 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mix}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async9685 = (function (change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta9686){
this.change = change;
this.solo_mode = solo_mode;
this.pick = pick;
this.cs = cs;
this.calc_state = calc_state;
this.out = out;
this.changed = changed;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.meta9686 = meta9686;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_9687,meta9686__$1){
var self__ = this;
var _9687__$1 = this;
return (new cljs.core.async.t_cljs$core$async9685(self__.change,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta9686__$1));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_9687){
var self__ = this;
var _9687__$1 = this;
return self__.meta9686;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.out;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mix$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,state_map){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.partial.call(null,cljs.core.merge_with,cljs.core.merge),state_map);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,mode){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_(self__.solo_modes.call(null,mode))){
} else {
throw (new Error(["Assert failed: ",["mode must be one of: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)].join(''),"\n","(solo-modes mode)"].join('')));
}

cljs.core.reset_BANG_.call(null,self__.solo_mode,mode);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.getBasis = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (){
return new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"change","change",477485025,null),new cljs.core.Symbol(null,"solo-mode","solo-mode",2031788074,null),new cljs.core.Symbol(null,"pick","pick",1300068175,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"calc-state","calc-state",-349968968,null),new cljs.core.Symbol(null,"out","out",729986010,null),new cljs.core.Symbol(null,"changed","changed",-2083710852,null),new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"attrs","attrs",-450137186,null),new cljs.core.Symbol(null,"meta9686","meta9686",662130721,null)], null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async9685.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async9685.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async9685";

cljs.core.async.t_cljs$core$async9685.cljs$lang$ctorPrWriter = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async9685");
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async9685.
 */
cljs.core.async.__GT_t_cljs$core$async9685 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function cljs$core$async$mix_$___GT_t_cljs$core$async9685(change__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta9686){
return (new cljs.core.async.t_cljs$core$async9685(change__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta9686));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

}

return (new cljs.core.async.t_cljs$core$async9685(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var c__8790__auto___9849 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___9849,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___9849,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (state_9789){
var state_val_9790 = (state_9789[(1)]);
if((state_val_9790 === (7))){
var inst_9704 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
var statearr_9791_9850 = state_9789__$1;
(statearr_9791_9850[(2)] = inst_9704);

(statearr_9791_9850[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (20))){
var inst_9716 = (state_9789[(7)]);
var state_9789__$1 = state_9789;
var statearr_9792_9851 = state_9789__$1;
(statearr_9792_9851[(2)] = inst_9716);

(statearr_9792_9851[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (27))){
var state_9789__$1 = state_9789;
var statearr_9793_9852 = state_9789__$1;
(statearr_9793_9852[(2)] = null);

(statearr_9793_9852[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (1))){
var inst_9691 = (state_9789[(8)]);
var inst_9691__$1 = calc_state.call(null);
var inst_9693 = (inst_9691__$1 == null);
var inst_9694 = cljs.core.not.call(null,inst_9693);
var state_9789__$1 = (function (){var statearr_9794 = state_9789;
(statearr_9794[(8)] = inst_9691__$1);

return statearr_9794;
})();
if(inst_9694){
var statearr_9795_9853 = state_9789__$1;
(statearr_9795_9853[(1)] = (2));

} else {
var statearr_9796_9854 = state_9789__$1;
(statearr_9796_9854[(1)] = (3));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (24))){
var inst_9740 = (state_9789[(9)]);
var inst_9749 = (state_9789[(10)]);
var inst_9763 = (state_9789[(11)]);
var inst_9763__$1 = inst_9740.call(null,inst_9749);
var state_9789__$1 = (function (){var statearr_9797 = state_9789;
(statearr_9797[(11)] = inst_9763__$1);

return statearr_9797;
})();
if(cljs.core.truth_(inst_9763__$1)){
var statearr_9798_9855 = state_9789__$1;
(statearr_9798_9855[(1)] = (29));

} else {
var statearr_9799_9856 = state_9789__$1;
(statearr_9799_9856[(1)] = (30));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (4))){
var inst_9707 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
if(cljs.core.truth_(inst_9707)){
var statearr_9800_9857 = state_9789__$1;
(statearr_9800_9857[(1)] = (8));

} else {
var statearr_9801_9858 = state_9789__$1;
(statearr_9801_9858[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (15))){
var inst_9734 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
if(cljs.core.truth_(inst_9734)){
var statearr_9802_9859 = state_9789__$1;
(statearr_9802_9859[(1)] = (19));

} else {
var statearr_9803_9860 = state_9789__$1;
(statearr_9803_9860[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (21))){
var inst_9739 = (state_9789[(12)]);
var inst_9739__$1 = (state_9789[(2)]);
var inst_9740 = cljs.core.get.call(null,inst_9739__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_9741 = cljs.core.get.call(null,inst_9739__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_9742 = cljs.core.get.call(null,inst_9739__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var state_9789__$1 = (function (){var statearr_9804 = state_9789;
(statearr_9804[(12)] = inst_9739__$1);

(statearr_9804[(13)] = inst_9741);

(statearr_9804[(9)] = inst_9740);

return statearr_9804;
})();
return cljs.core.async.ioc_alts_BANG_.call(null,state_9789__$1,(22),inst_9742);
} else {
if((state_val_9790 === (31))){
var inst_9771 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
if(cljs.core.truth_(inst_9771)){
var statearr_9805_9861 = state_9789__$1;
(statearr_9805_9861[(1)] = (32));

} else {
var statearr_9806_9862 = state_9789__$1;
(statearr_9806_9862[(1)] = (33));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (32))){
var inst_9748 = (state_9789[(14)]);
var state_9789__$1 = state_9789;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_9789__$1,(35),out,inst_9748);
} else {
if((state_val_9790 === (33))){
var inst_9739 = (state_9789[(12)]);
var inst_9716 = inst_9739;
var state_9789__$1 = (function (){var statearr_9807 = state_9789;
(statearr_9807[(7)] = inst_9716);

return statearr_9807;
})();
var statearr_9808_9863 = state_9789__$1;
(statearr_9808_9863[(2)] = null);

(statearr_9808_9863[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (13))){
var inst_9716 = (state_9789[(7)]);
var inst_9723 = inst_9716.cljs$lang$protocol_mask$partition0$;
var inst_9724 = (inst_9723 & (64));
var inst_9725 = inst_9716.cljs$core$ISeq$;
var inst_9726 = (cljs.core.PROTOCOL_SENTINEL === inst_9725);
var inst_9727 = ((inst_9724) || (inst_9726));
var state_9789__$1 = state_9789;
if(cljs.core.truth_(inst_9727)){
var statearr_9809_9864 = state_9789__$1;
(statearr_9809_9864[(1)] = (16));

} else {
var statearr_9810_9865 = state_9789__$1;
(statearr_9810_9865[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (22))){
var inst_9749 = (state_9789[(10)]);
var inst_9748 = (state_9789[(14)]);
var inst_9747 = (state_9789[(2)]);
var inst_9748__$1 = cljs.core.nth.call(null,inst_9747,(0),null);
var inst_9749__$1 = cljs.core.nth.call(null,inst_9747,(1),null);
var inst_9750 = (inst_9748__$1 == null);
var inst_9751 = cljs.core._EQ_.call(null,inst_9749__$1,change);
var inst_9752 = ((inst_9750) || (inst_9751));
var state_9789__$1 = (function (){var statearr_9811 = state_9789;
(statearr_9811[(10)] = inst_9749__$1);

(statearr_9811[(14)] = inst_9748__$1);

return statearr_9811;
})();
if(cljs.core.truth_(inst_9752)){
var statearr_9812_9866 = state_9789__$1;
(statearr_9812_9866[(1)] = (23));

} else {
var statearr_9813_9867 = state_9789__$1;
(statearr_9813_9867[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (36))){
var inst_9739 = (state_9789[(12)]);
var inst_9716 = inst_9739;
var state_9789__$1 = (function (){var statearr_9814 = state_9789;
(statearr_9814[(7)] = inst_9716);

return statearr_9814;
})();
var statearr_9815_9868 = state_9789__$1;
(statearr_9815_9868[(2)] = null);

(statearr_9815_9868[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (29))){
var inst_9763 = (state_9789[(11)]);
var state_9789__$1 = state_9789;
var statearr_9816_9869 = state_9789__$1;
(statearr_9816_9869[(2)] = inst_9763);

(statearr_9816_9869[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (6))){
var state_9789__$1 = state_9789;
var statearr_9817_9870 = state_9789__$1;
(statearr_9817_9870[(2)] = false);

(statearr_9817_9870[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (28))){
var inst_9759 = (state_9789[(2)]);
var inst_9760 = calc_state.call(null);
var inst_9716 = inst_9760;
var state_9789__$1 = (function (){var statearr_9818 = state_9789;
(statearr_9818[(7)] = inst_9716);

(statearr_9818[(15)] = inst_9759);

return statearr_9818;
})();
var statearr_9819_9871 = state_9789__$1;
(statearr_9819_9871[(2)] = null);

(statearr_9819_9871[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (25))){
var inst_9785 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
var statearr_9820_9872 = state_9789__$1;
(statearr_9820_9872[(2)] = inst_9785);

(statearr_9820_9872[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (34))){
var inst_9783 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
var statearr_9821_9873 = state_9789__$1;
(statearr_9821_9873[(2)] = inst_9783);

(statearr_9821_9873[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (17))){
var state_9789__$1 = state_9789;
var statearr_9822_9874 = state_9789__$1;
(statearr_9822_9874[(2)] = false);

(statearr_9822_9874[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (3))){
var state_9789__$1 = state_9789;
var statearr_9823_9875 = state_9789__$1;
(statearr_9823_9875[(2)] = false);

(statearr_9823_9875[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (12))){
var inst_9787 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9789__$1,inst_9787);
} else {
if((state_val_9790 === (2))){
var inst_9691 = (state_9789[(8)]);
var inst_9696 = inst_9691.cljs$lang$protocol_mask$partition0$;
var inst_9697 = (inst_9696 & (64));
var inst_9698 = inst_9691.cljs$core$ISeq$;
var inst_9699 = (cljs.core.PROTOCOL_SENTINEL === inst_9698);
var inst_9700 = ((inst_9697) || (inst_9699));
var state_9789__$1 = state_9789;
if(cljs.core.truth_(inst_9700)){
var statearr_9824_9876 = state_9789__$1;
(statearr_9824_9876[(1)] = (5));

} else {
var statearr_9825_9877 = state_9789__$1;
(statearr_9825_9877[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (23))){
var inst_9748 = (state_9789[(14)]);
var inst_9754 = (inst_9748 == null);
var state_9789__$1 = state_9789;
if(cljs.core.truth_(inst_9754)){
var statearr_9826_9878 = state_9789__$1;
(statearr_9826_9878[(1)] = (26));

} else {
var statearr_9827_9879 = state_9789__$1;
(statearr_9827_9879[(1)] = (27));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (35))){
var inst_9774 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
if(cljs.core.truth_(inst_9774)){
var statearr_9828_9880 = state_9789__$1;
(statearr_9828_9880[(1)] = (36));

} else {
var statearr_9829_9881 = state_9789__$1;
(statearr_9829_9881[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (19))){
var inst_9716 = (state_9789[(7)]);
var inst_9736 = cljs.core.apply.call(null,cljs.core.hash_map,inst_9716);
var state_9789__$1 = state_9789;
var statearr_9830_9882 = state_9789__$1;
(statearr_9830_9882[(2)] = inst_9736);

(statearr_9830_9882[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (11))){
var inst_9716 = (state_9789[(7)]);
var inst_9720 = (inst_9716 == null);
var inst_9721 = cljs.core.not.call(null,inst_9720);
var state_9789__$1 = state_9789;
if(inst_9721){
var statearr_9831_9883 = state_9789__$1;
(statearr_9831_9883[(1)] = (13));

} else {
var statearr_9832_9884 = state_9789__$1;
(statearr_9832_9884[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (9))){
var inst_9691 = (state_9789[(8)]);
var state_9789__$1 = state_9789;
var statearr_9833_9885 = state_9789__$1;
(statearr_9833_9885[(2)] = inst_9691);

(statearr_9833_9885[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (5))){
var state_9789__$1 = state_9789;
var statearr_9834_9886 = state_9789__$1;
(statearr_9834_9886[(2)] = true);

(statearr_9834_9886[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (14))){
var state_9789__$1 = state_9789;
var statearr_9835_9887 = state_9789__$1;
(statearr_9835_9887[(2)] = false);

(statearr_9835_9887[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (26))){
var inst_9749 = (state_9789[(10)]);
var inst_9756 = cljs.core.swap_BANG_.call(null,cs,cljs.core.dissoc,inst_9749);
var state_9789__$1 = state_9789;
var statearr_9836_9888 = state_9789__$1;
(statearr_9836_9888[(2)] = inst_9756);

(statearr_9836_9888[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (16))){
var state_9789__$1 = state_9789;
var statearr_9837_9889 = state_9789__$1;
(statearr_9837_9889[(2)] = true);

(statearr_9837_9889[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (38))){
var inst_9779 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
var statearr_9838_9890 = state_9789__$1;
(statearr_9838_9890[(2)] = inst_9779);

(statearr_9838_9890[(1)] = (34));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (30))){
var inst_9741 = (state_9789[(13)]);
var inst_9740 = (state_9789[(9)]);
var inst_9749 = (state_9789[(10)]);
var inst_9766 = cljs.core.empty_QMARK_.call(null,inst_9740);
var inst_9767 = inst_9741.call(null,inst_9749);
var inst_9768 = cljs.core.not.call(null,inst_9767);
var inst_9769 = ((inst_9766) && (inst_9768));
var state_9789__$1 = state_9789;
var statearr_9839_9891 = state_9789__$1;
(statearr_9839_9891[(2)] = inst_9769);

(statearr_9839_9891[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (10))){
var inst_9691 = (state_9789[(8)]);
var inst_9712 = (state_9789[(2)]);
var inst_9713 = cljs.core.get.call(null,inst_9712,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_9714 = cljs.core.get.call(null,inst_9712,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_9715 = cljs.core.get.call(null,inst_9712,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var inst_9716 = inst_9691;
var state_9789__$1 = (function (){var statearr_9840 = state_9789;
(statearr_9840[(7)] = inst_9716);

(statearr_9840[(16)] = inst_9713);

(statearr_9840[(17)] = inst_9714);

(statearr_9840[(18)] = inst_9715);

return statearr_9840;
})();
var statearr_9841_9892 = state_9789__$1;
(statearr_9841_9892[(2)] = null);

(statearr_9841_9892[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (18))){
var inst_9731 = (state_9789[(2)]);
var state_9789__$1 = state_9789;
var statearr_9842_9893 = state_9789__$1;
(statearr_9842_9893[(2)] = inst_9731);

(statearr_9842_9893[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (37))){
var state_9789__$1 = state_9789;
var statearr_9843_9894 = state_9789__$1;
(statearr_9843_9894[(2)] = null);

(statearr_9843_9894[(1)] = (38));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9790 === (8))){
var inst_9691 = (state_9789[(8)]);
var inst_9709 = cljs.core.apply.call(null,cljs.core.hash_map,inst_9691);
var state_9789__$1 = state_9789;
var statearr_9844_9895 = state_9789__$1;
(statearr_9844_9895[(2)] = inst_9709);

(statearr_9844_9895[(1)] = (10));


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
}
}
}
}
}
}
});})(c__8790__auto___9849,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
;
return ((function (switch__8695__auto__,c__8790__auto___9849,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function() {
var cljs$core$async$mix_$_state_machine__8696__auto__ = null;
var cljs$core$async$mix_$_state_machine__8696__auto____0 = (function (){
var statearr_9845 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_9845[(0)] = cljs$core$async$mix_$_state_machine__8696__auto__);

(statearr_9845[(1)] = (1));

return statearr_9845;
});
var cljs$core$async$mix_$_state_machine__8696__auto____1 = (function (state_9789){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9789);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e9846){if((e9846 instanceof Object)){
var ex__8699__auto__ = e9846;
var statearr_9847_9896 = state_9789;
(statearr_9847_9896[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9789);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e9846;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__9897 = state_9789;
state_9789 = G__9897;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$mix_$_state_machine__8696__auto__ = function(state_9789){
switch(arguments.length){
case 0:
return cljs$core$async$mix_$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$mix_$_state_machine__8696__auto____1.call(this,state_9789);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mix_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mix_$_state_machine__8696__auto____0;
cljs$core$async$mix_$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mix_$_state_machine__8696__auto____1;
return cljs$core$async$mix_$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___9849,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
})();
var state__8792__auto__ = (function (){var statearr_9848 = f__8791__auto__.call(null);
(statearr_9848[(6)] = c__8790__auto___9849);

return statearr_9848;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___9849,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
);


return m;
});
/**
 * Adds ch as an input to the mix
 */
cljs.core.async.admix = (function cljs$core$async$admix(mix,ch){
return cljs.core.async.admix_STAR_.call(null,mix,ch);
});
/**
 * Removes ch as an input to the mix
 */
cljs.core.async.unmix = (function cljs$core$async$unmix(mix,ch){
return cljs.core.async.unmix_STAR_.call(null,mix,ch);
});
/**
 * removes all inputs from the mix
 */
cljs.core.async.unmix_all = (function cljs$core$async$unmix_all(mix){
return cljs.core.async.unmix_all_STAR_.call(null,mix);
});
/**
 * Atomically sets the state(s) of one or more channels in a mix. The
 *   state map is a map of channels -> channel-state-map. A
 *   channel-state-map is a map of attrs -> boolean, where attr is one or
 *   more of :mute, :pause or :solo. Any states supplied are merged with
 *   the current state.
 * 
 *   Note that channels can be added to a mix via toggle, which can be
 *   used to add channels in a particular (e.g. paused) state.
 */
cljs.core.async.toggle = (function cljs$core$async$toggle(mix,state_map){
return cljs.core.async.toggle_STAR_.call(null,mix,state_map);
});
/**
 * Sets the solo mode of the mix. mode must be one of :mute or :pause
 */
cljs.core.async.solo_mode = (function cljs$core$async$solo_mode(mix,mode){
return cljs.core.async.solo_mode_STAR_.call(null,mix,mode);
});

/**
 * @interface
 */
cljs.core.async.Pub = function(){};

cljs.core.async.sub_STAR_ = (function cljs$core$async$sub_STAR_(p,v,ch,close_QMARK_){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$sub_STAR_$arity$4 == null)))))){
return p.cljs$core$async$Pub$sub_STAR_$arity$4(p,v,ch,close_QMARK_);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.sub_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p,v,ch,close_QMARK_);
} else {
var m__4431__auto__ = (cljs.core.async.sub_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p,v,ch,close_QMARK_);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.sub*",p);
}
}
}
});

cljs.core.async.unsub_STAR_ = (function cljs$core$async$unsub_STAR_(p,v,ch){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_STAR_$arity$3 == null)))))){
return p.cljs$core$async$Pub$unsub_STAR_$arity$3(p,v,ch);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.unsub_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p,v,ch);
} else {
var m__4431__auto__ = (cljs.core.async.unsub_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p,v,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_ = (function cljs$core$async$unsub_all_STAR_(var_args){
var G__9899 = arguments.length;
switch (G__9899) {
case 1:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1 = (function (p){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$1 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$1(p);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p);
} else {
var m__4431__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (p,v){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$2 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p,v);
} else {
var m__4431__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p,v);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_.cljs$lang$maxFixedArity = 2;


/**
 * Creates and returns a pub(lication) of the supplied channel,
 *   partitioned into topics by the topic-fn. topic-fn will be applied to
 *   each value on the channel and the result will determine the 'topic'
 *   on which that value will be put. Channels can be subscribed to
 *   receive copies of topics using 'sub', and unsubscribed using
 *   'unsub'. Each topic will be handled by an internal mult on a
 *   dedicated channel. By default these internal channels are
 *   unbuffered, but a buf-fn can be supplied which, given a topic,
 *   creates a buffer with desired properties.
 * 
 *   Each item is distributed to all subs in parallel and synchronously,
 *   i.e. each sub must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow subs from holding up the pub.
 * 
 *   Items received when there are no matching subs get dropped.
 * 
 *   Note that if buf-fns are used then each topic is handled
 *   asynchronously, i.e. if a channel is subscribed to more than one
 *   topic it should not expect them to be interleaved identically with
 *   the source.
 */
cljs.core.async.pub = (function cljs$core$async$pub(var_args){
var G__9903 = arguments.length;
switch (G__9903) {
case 2:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2 = (function (ch,topic_fn){
return cljs.core.async.pub.call(null,ch,topic_fn,cljs.core.constantly.call(null,null));
});

cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3 = (function (ch,topic_fn,buf_fn){
var mults = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var ensure_mult = ((function (mults){
return (function (topic){
var or__4131__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,mults),topic);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return cljs.core.get.call(null,cljs.core.swap_BANG_.call(null,mults,((function (or__4131__auto__,mults){
return (function (p1__9901_SHARP_){
if(cljs.core.truth_(p1__9901_SHARP_.call(null,topic))){
return p1__9901_SHARP_;
} else {
return cljs.core.assoc.call(null,p1__9901_SHARP_,topic,cljs.core.async.mult.call(null,cljs.core.async.chan.call(null,buf_fn.call(null,topic))));
}
});})(or__4131__auto__,mults))
),topic);
}
});})(mults))
;
var p = (function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async9904 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.Pub}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async9904 = (function (ch,topic_fn,buf_fn,mults,ensure_mult,meta9905){
this.ch = ch;
this.topic_fn = topic_fn;
this.buf_fn = buf_fn;
this.mults = mults;
this.ensure_mult = ensure_mult;
this.meta9905 = meta9905;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (mults,ensure_mult){
return (function (_9906,meta9905__$1){
var self__ = this;
var _9906__$1 = this;
return (new cljs.core.async.t_cljs$core$async9904(self__.ch,self__.topic_fn,self__.buf_fn,self__.mults,self__.ensure_mult,meta9905__$1));
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (mults,ensure_mult){
return (function (_9906){
var self__ = this;
var _9906__$1 = this;
return self__.meta9905;
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$async$Pub$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$1,close_QMARK_){
var self__ = this;
var p__$1 = this;
var m = self__.ensure_mult.call(null,topic);
return cljs.core.async.tap.call(null,m,ch__$1,close_QMARK_);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$1){
var self__ = this;
var p__$1 = this;
var temp__5720__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,self__.mults),topic);
if(cljs.core.truth_(temp__5720__auto__)){
var m = temp__5720__auto__;
return cljs.core.async.untap.call(null,m,ch__$1);
} else {
return null;
}
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.reset_BANG_.call(null,self__.mults,cljs.core.PersistentArrayMap.EMPTY);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = ((function (mults,ensure_mult){
return (function (_,topic){
var self__ = this;
var ___$1 = this;
return cljs.core.swap_BANG_.call(null,self__.mults,cljs.core.dissoc,topic);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.getBasis = ((function (mults,ensure_mult){
return (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"topic-fn","topic-fn",-862449736,null),new cljs.core.Symbol(null,"buf-fn","buf-fn",-1200281591,null),new cljs.core.Symbol(null,"mults","mults",-461114485,null),new cljs.core.Symbol(null,"ensure-mult","ensure-mult",1796584816,null),new cljs.core.Symbol(null,"meta9905","meta9905",1670297276,null)], null);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async9904.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async9904.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async9904";

cljs.core.async.t_cljs$core$async9904.cljs$lang$ctorPrWriter = ((function (mults,ensure_mult){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async9904");
});})(mults,ensure_mult))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async9904.
 */
cljs.core.async.__GT_t_cljs$core$async9904 = ((function (mults,ensure_mult){
return (function cljs$core$async$__GT_t_cljs$core$async9904(ch__$1,topic_fn__$1,buf_fn__$1,mults__$1,ensure_mult__$1,meta9905){
return (new cljs.core.async.t_cljs$core$async9904(ch__$1,topic_fn__$1,buf_fn__$1,mults__$1,ensure_mult__$1,meta9905));
});})(mults,ensure_mult))
;

}

return (new cljs.core.async.t_cljs$core$async9904(ch,topic_fn,buf_fn,mults,ensure_mult,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var c__8790__auto___10024 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10024,mults,ensure_mult,p){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10024,mults,ensure_mult,p){
return (function (state_9978){
var state_val_9979 = (state_9978[(1)]);
if((state_val_9979 === (7))){
var inst_9974 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
var statearr_9980_10025 = state_9978__$1;
(statearr_9980_10025[(2)] = inst_9974);

(statearr_9980_10025[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (20))){
var state_9978__$1 = state_9978;
var statearr_9981_10026 = state_9978__$1;
(statearr_9981_10026[(2)] = null);

(statearr_9981_10026[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (1))){
var state_9978__$1 = state_9978;
var statearr_9982_10027 = state_9978__$1;
(statearr_9982_10027[(2)] = null);

(statearr_9982_10027[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (24))){
var inst_9957 = (state_9978[(7)]);
var inst_9966 = cljs.core.swap_BANG_.call(null,mults,cljs.core.dissoc,inst_9957);
var state_9978__$1 = state_9978;
var statearr_9983_10028 = state_9978__$1;
(statearr_9983_10028[(2)] = inst_9966);

(statearr_9983_10028[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (4))){
var inst_9909 = (state_9978[(8)]);
var inst_9909__$1 = (state_9978[(2)]);
var inst_9910 = (inst_9909__$1 == null);
var state_9978__$1 = (function (){var statearr_9984 = state_9978;
(statearr_9984[(8)] = inst_9909__$1);

return statearr_9984;
})();
if(cljs.core.truth_(inst_9910)){
var statearr_9985_10029 = state_9978__$1;
(statearr_9985_10029[(1)] = (5));

} else {
var statearr_9986_10030 = state_9978__$1;
(statearr_9986_10030[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (15))){
var inst_9951 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
var statearr_9987_10031 = state_9978__$1;
(statearr_9987_10031[(2)] = inst_9951);

(statearr_9987_10031[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (21))){
var inst_9971 = (state_9978[(2)]);
var state_9978__$1 = (function (){var statearr_9988 = state_9978;
(statearr_9988[(9)] = inst_9971);

return statearr_9988;
})();
var statearr_9989_10032 = state_9978__$1;
(statearr_9989_10032[(2)] = null);

(statearr_9989_10032[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (13))){
var inst_9933 = (state_9978[(10)]);
var inst_9935 = cljs.core.chunked_seq_QMARK_.call(null,inst_9933);
var state_9978__$1 = state_9978;
if(inst_9935){
var statearr_9990_10033 = state_9978__$1;
(statearr_9990_10033[(1)] = (16));

} else {
var statearr_9991_10034 = state_9978__$1;
(statearr_9991_10034[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (22))){
var inst_9963 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
if(cljs.core.truth_(inst_9963)){
var statearr_9992_10035 = state_9978__$1;
(statearr_9992_10035[(1)] = (23));

} else {
var statearr_9993_10036 = state_9978__$1;
(statearr_9993_10036[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (6))){
var inst_9909 = (state_9978[(8)]);
var inst_9957 = (state_9978[(7)]);
var inst_9959 = (state_9978[(11)]);
var inst_9957__$1 = topic_fn.call(null,inst_9909);
var inst_9958 = cljs.core.deref.call(null,mults);
var inst_9959__$1 = cljs.core.get.call(null,inst_9958,inst_9957__$1);
var state_9978__$1 = (function (){var statearr_9994 = state_9978;
(statearr_9994[(7)] = inst_9957__$1);

(statearr_9994[(11)] = inst_9959__$1);

return statearr_9994;
})();
if(cljs.core.truth_(inst_9959__$1)){
var statearr_9995_10037 = state_9978__$1;
(statearr_9995_10037[(1)] = (19));

} else {
var statearr_9996_10038 = state_9978__$1;
(statearr_9996_10038[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (25))){
var inst_9968 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
var statearr_9997_10039 = state_9978__$1;
(statearr_9997_10039[(2)] = inst_9968);

(statearr_9997_10039[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (17))){
var inst_9933 = (state_9978[(10)]);
var inst_9942 = cljs.core.first.call(null,inst_9933);
var inst_9943 = cljs.core.async.muxch_STAR_.call(null,inst_9942);
var inst_9944 = cljs.core.async.close_BANG_.call(null,inst_9943);
var inst_9945 = cljs.core.next.call(null,inst_9933);
var inst_9919 = inst_9945;
var inst_9920 = null;
var inst_9921 = (0);
var inst_9922 = (0);
var state_9978__$1 = (function (){var statearr_9998 = state_9978;
(statearr_9998[(12)] = inst_9944);

(statearr_9998[(13)] = inst_9921);

(statearr_9998[(14)] = inst_9919);

(statearr_9998[(15)] = inst_9920);

(statearr_9998[(16)] = inst_9922);

return statearr_9998;
})();
var statearr_9999_10040 = state_9978__$1;
(statearr_9999_10040[(2)] = null);

(statearr_9999_10040[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (3))){
var inst_9976 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_9978__$1,inst_9976);
} else {
if((state_val_9979 === (12))){
var inst_9953 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
var statearr_10000_10041 = state_9978__$1;
(statearr_10000_10041[(2)] = inst_9953);

(statearr_10000_10041[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (2))){
var state_9978__$1 = state_9978;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_9978__$1,(4),ch);
} else {
if((state_val_9979 === (23))){
var state_9978__$1 = state_9978;
var statearr_10001_10042 = state_9978__$1;
(statearr_10001_10042[(2)] = null);

(statearr_10001_10042[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (19))){
var inst_9909 = (state_9978[(8)]);
var inst_9959 = (state_9978[(11)]);
var inst_9961 = cljs.core.async.muxch_STAR_.call(null,inst_9959);
var state_9978__$1 = state_9978;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_9978__$1,(22),inst_9961,inst_9909);
} else {
if((state_val_9979 === (11))){
var inst_9933 = (state_9978[(10)]);
var inst_9919 = (state_9978[(14)]);
var inst_9933__$1 = cljs.core.seq.call(null,inst_9919);
var state_9978__$1 = (function (){var statearr_10002 = state_9978;
(statearr_10002[(10)] = inst_9933__$1);

return statearr_10002;
})();
if(inst_9933__$1){
var statearr_10003_10043 = state_9978__$1;
(statearr_10003_10043[(1)] = (13));

} else {
var statearr_10004_10044 = state_9978__$1;
(statearr_10004_10044[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (9))){
var inst_9955 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
var statearr_10005_10045 = state_9978__$1;
(statearr_10005_10045[(2)] = inst_9955);

(statearr_10005_10045[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (5))){
var inst_9916 = cljs.core.deref.call(null,mults);
var inst_9917 = cljs.core.vals.call(null,inst_9916);
var inst_9918 = cljs.core.seq.call(null,inst_9917);
var inst_9919 = inst_9918;
var inst_9920 = null;
var inst_9921 = (0);
var inst_9922 = (0);
var state_9978__$1 = (function (){var statearr_10006 = state_9978;
(statearr_10006[(13)] = inst_9921);

(statearr_10006[(14)] = inst_9919);

(statearr_10006[(15)] = inst_9920);

(statearr_10006[(16)] = inst_9922);

return statearr_10006;
})();
var statearr_10007_10046 = state_9978__$1;
(statearr_10007_10046[(2)] = null);

(statearr_10007_10046[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (14))){
var state_9978__$1 = state_9978;
var statearr_10011_10047 = state_9978__$1;
(statearr_10011_10047[(2)] = null);

(statearr_10011_10047[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (16))){
var inst_9933 = (state_9978[(10)]);
var inst_9937 = cljs.core.chunk_first.call(null,inst_9933);
var inst_9938 = cljs.core.chunk_rest.call(null,inst_9933);
var inst_9939 = cljs.core.count.call(null,inst_9937);
var inst_9919 = inst_9938;
var inst_9920 = inst_9937;
var inst_9921 = inst_9939;
var inst_9922 = (0);
var state_9978__$1 = (function (){var statearr_10012 = state_9978;
(statearr_10012[(13)] = inst_9921);

(statearr_10012[(14)] = inst_9919);

(statearr_10012[(15)] = inst_9920);

(statearr_10012[(16)] = inst_9922);

return statearr_10012;
})();
var statearr_10013_10048 = state_9978__$1;
(statearr_10013_10048[(2)] = null);

(statearr_10013_10048[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (10))){
var inst_9921 = (state_9978[(13)]);
var inst_9919 = (state_9978[(14)]);
var inst_9920 = (state_9978[(15)]);
var inst_9922 = (state_9978[(16)]);
var inst_9927 = cljs.core._nth.call(null,inst_9920,inst_9922);
var inst_9928 = cljs.core.async.muxch_STAR_.call(null,inst_9927);
var inst_9929 = cljs.core.async.close_BANG_.call(null,inst_9928);
var inst_9930 = (inst_9922 + (1));
var tmp10008 = inst_9921;
var tmp10009 = inst_9919;
var tmp10010 = inst_9920;
var inst_9919__$1 = tmp10009;
var inst_9920__$1 = tmp10010;
var inst_9921__$1 = tmp10008;
var inst_9922__$1 = inst_9930;
var state_9978__$1 = (function (){var statearr_10014 = state_9978;
(statearr_10014[(13)] = inst_9921__$1);

(statearr_10014[(17)] = inst_9929);

(statearr_10014[(14)] = inst_9919__$1);

(statearr_10014[(15)] = inst_9920__$1);

(statearr_10014[(16)] = inst_9922__$1);

return statearr_10014;
})();
var statearr_10015_10049 = state_9978__$1;
(statearr_10015_10049[(2)] = null);

(statearr_10015_10049[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (18))){
var inst_9948 = (state_9978[(2)]);
var state_9978__$1 = state_9978;
var statearr_10016_10050 = state_9978__$1;
(statearr_10016_10050[(2)] = inst_9948);

(statearr_10016_10050[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_9979 === (8))){
var inst_9921 = (state_9978[(13)]);
var inst_9922 = (state_9978[(16)]);
var inst_9924 = (inst_9922 < inst_9921);
var inst_9925 = inst_9924;
var state_9978__$1 = state_9978;
if(cljs.core.truth_(inst_9925)){
var statearr_10017_10051 = state_9978__$1;
(statearr_10017_10051[(1)] = (10));

} else {
var statearr_10018_10052 = state_9978__$1;
(statearr_10018_10052[(1)] = (11));

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
}
}
}
}
}
}
}
}
}
});})(c__8790__auto___10024,mults,ensure_mult,p))
;
return ((function (switch__8695__auto__,c__8790__auto___10024,mults,ensure_mult,p){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10019 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_10019[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10019[(1)] = (1));

return statearr_10019;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_9978){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_9978);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10020){if((e10020 instanceof Object)){
var ex__8699__auto__ = e10020;
var statearr_10021_10053 = state_9978;
(statearr_10021_10053[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_9978);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10020;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10054 = state_9978;
state_9978 = G__10054;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_9978){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_9978);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10024,mults,ensure_mult,p))
})();
var state__8792__auto__ = (function (){var statearr_10022 = f__8791__auto__.call(null);
(statearr_10022[(6)] = c__8790__auto___10024);

return statearr_10022;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10024,mults,ensure_mult,p))
);


return p;
});

cljs.core.async.pub.cljs$lang$maxFixedArity = 3;

/**
 * Subscribes a channel to a topic of a pub.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.sub = (function cljs$core$async$sub(var_args){
var G__10056 = arguments.length;
switch (G__10056) {
case 3:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3 = (function (p,topic,ch){
return cljs.core.async.sub.call(null,p,topic,ch,true);
});

cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4 = (function (p,topic,ch,close_QMARK_){
return cljs.core.async.sub_STAR_.call(null,p,topic,ch,close_QMARK_);
});

cljs.core.async.sub.cljs$lang$maxFixedArity = 4;

/**
 * Unsubscribes a channel from a topic of a pub
 */
cljs.core.async.unsub = (function cljs$core$async$unsub(p,topic,ch){
return cljs.core.async.unsub_STAR_.call(null,p,topic,ch);
});
/**
 * Unsubscribes all channels from a pub, or a topic of a pub
 */
cljs.core.async.unsub_all = (function cljs$core$async$unsub_all(var_args){
var G__10059 = arguments.length;
switch (G__10059) {
case 1:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1 = (function (p){
return cljs.core.async.unsub_all_STAR_.call(null,p);
});

cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2 = (function (p,topic){
return cljs.core.async.unsub_all_STAR_.call(null,p,topic);
});

cljs.core.async.unsub_all.cljs$lang$maxFixedArity = 2;

/**
 * Takes a function and a collection of source channels, and returns a
 *   channel which contains the values produced by applying f to the set
 *   of first items taken from each source channel, followed by applying
 *   f to the set of second items from each channel, until any one of the
 *   channels is closed, at which point the output channel will be
 *   closed. The returned channel will be unbuffered by default, or a
 *   buf-or-n can be supplied
 */
cljs.core.async.map = (function cljs$core$async$map(var_args){
var G__10062 = arguments.length;
switch (G__10062) {
case 2:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.map.cljs$core$IFn$_invoke$arity$2 = (function (f,chs){
return cljs.core.async.map.call(null,f,chs,null);
});

cljs.core.async.map.cljs$core$IFn$_invoke$arity$3 = (function (f,chs,buf_or_n){
var chs__$1 = cljs.core.vec.call(null,chs);
var out = cljs.core.async.chan.call(null,buf_or_n);
var cnt = cljs.core.count.call(null,chs__$1);
var rets = cljs.core.object_array.call(null,cnt);
var dchan = cljs.core.async.chan.call(null,(1));
var dctr = cljs.core.atom.call(null,null);
var done = cljs.core.mapv.call(null,((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (i){
return ((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (ret){
(rets[i] = ret);

if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.call(null,dchan,rets.slice((0)));
} else {
return null;
}
});
;})(chs__$1,out,cnt,rets,dchan,dctr))
});})(chs__$1,out,cnt,rets,dchan,dctr))
,cljs.core.range.call(null,cnt));
var c__8790__auto___10129 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10129,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10129,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (state_10101){
var state_val_10102 = (state_10101[(1)]);
if((state_val_10102 === (7))){
var state_10101__$1 = state_10101;
var statearr_10103_10130 = state_10101__$1;
(statearr_10103_10130[(2)] = null);

(statearr_10103_10130[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (1))){
var state_10101__$1 = state_10101;
var statearr_10104_10131 = state_10101__$1;
(statearr_10104_10131[(2)] = null);

(statearr_10104_10131[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (4))){
var inst_10065 = (state_10101[(7)]);
var inst_10067 = (inst_10065 < cnt);
var state_10101__$1 = state_10101;
if(cljs.core.truth_(inst_10067)){
var statearr_10105_10132 = state_10101__$1;
(statearr_10105_10132[(1)] = (6));

} else {
var statearr_10106_10133 = state_10101__$1;
(statearr_10106_10133[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (15))){
var inst_10097 = (state_10101[(2)]);
var state_10101__$1 = state_10101;
var statearr_10107_10134 = state_10101__$1;
(statearr_10107_10134[(2)] = inst_10097);

(statearr_10107_10134[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (13))){
var inst_10090 = cljs.core.async.close_BANG_.call(null,out);
var state_10101__$1 = state_10101;
var statearr_10108_10135 = state_10101__$1;
(statearr_10108_10135[(2)] = inst_10090);

(statearr_10108_10135[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (6))){
var state_10101__$1 = state_10101;
var statearr_10109_10136 = state_10101__$1;
(statearr_10109_10136[(2)] = null);

(statearr_10109_10136[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (3))){
var inst_10099 = (state_10101[(2)]);
var state_10101__$1 = state_10101;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10101__$1,inst_10099);
} else {
if((state_val_10102 === (12))){
var inst_10087 = (state_10101[(8)]);
var inst_10087__$1 = (state_10101[(2)]);
var inst_10088 = cljs.core.some.call(null,cljs.core.nil_QMARK_,inst_10087__$1);
var state_10101__$1 = (function (){var statearr_10110 = state_10101;
(statearr_10110[(8)] = inst_10087__$1);

return statearr_10110;
})();
if(cljs.core.truth_(inst_10088)){
var statearr_10111_10137 = state_10101__$1;
(statearr_10111_10137[(1)] = (13));

} else {
var statearr_10112_10138 = state_10101__$1;
(statearr_10112_10138[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (2))){
var inst_10064 = cljs.core.reset_BANG_.call(null,dctr,cnt);
var inst_10065 = (0);
var state_10101__$1 = (function (){var statearr_10113 = state_10101;
(statearr_10113[(9)] = inst_10064);

(statearr_10113[(7)] = inst_10065);

return statearr_10113;
})();
var statearr_10114_10139 = state_10101__$1;
(statearr_10114_10139[(2)] = null);

(statearr_10114_10139[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (11))){
var inst_10065 = (state_10101[(7)]);
var _ = cljs.core.async.impl.ioc_helpers.add_exception_frame.call(null,state_10101,(10),Object,null,(9));
var inst_10074 = chs__$1.call(null,inst_10065);
var inst_10075 = done.call(null,inst_10065);
var inst_10076 = cljs.core.async.take_BANG_.call(null,inst_10074,inst_10075);
var state_10101__$1 = state_10101;
var statearr_10115_10140 = state_10101__$1;
(statearr_10115_10140[(2)] = inst_10076);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10101__$1);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (9))){
var inst_10065 = (state_10101[(7)]);
var inst_10078 = (state_10101[(2)]);
var inst_10079 = (inst_10065 + (1));
var inst_10065__$1 = inst_10079;
var state_10101__$1 = (function (){var statearr_10116 = state_10101;
(statearr_10116[(10)] = inst_10078);

(statearr_10116[(7)] = inst_10065__$1);

return statearr_10116;
})();
var statearr_10117_10141 = state_10101__$1;
(statearr_10117_10141[(2)] = null);

(statearr_10117_10141[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (5))){
var inst_10085 = (state_10101[(2)]);
var state_10101__$1 = (function (){var statearr_10118 = state_10101;
(statearr_10118[(11)] = inst_10085);

return statearr_10118;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10101__$1,(12),dchan);
} else {
if((state_val_10102 === (14))){
var inst_10087 = (state_10101[(8)]);
var inst_10092 = cljs.core.apply.call(null,f,inst_10087);
var state_10101__$1 = state_10101;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10101__$1,(16),out,inst_10092);
} else {
if((state_val_10102 === (16))){
var inst_10094 = (state_10101[(2)]);
var state_10101__$1 = (function (){var statearr_10119 = state_10101;
(statearr_10119[(12)] = inst_10094);

return statearr_10119;
})();
var statearr_10120_10142 = state_10101__$1;
(statearr_10120_10142[(2)] = null);

(statearr_10120_10142[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (10))){
var inst_10069 = (state_10101[(2)]);
var inst_10070 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);
var state_10101__$1 = (function (){var statearr_10121 = state_10101;
(statearr_10121[(13)] = inst_10069);

return statearr_10121;
})();
var statearr_10122_10143 = state_10101__$1;
(statearr_10122_10143[(2)] = inst_10070);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10101__$1);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10102 === (8))){
var inst_10083 = (state_10101[(2)]);
var state_10101__$1 = state_10101;
var statearr_10123_10144 = state_10101__$1;
(statearr_10123_10144[(2)] = inst_10083);

(statearr_10123_10144[(1)] = (5));


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
});})(c__8790__auto___10129,chs__$1,out,cnt,rets,dchan,dctr,done))
;
return ((function (switch__8695__auto__,c__8790__auto___10129,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10124 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_10124[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10124[(1)] = (1));

return statearr_10124;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_10101){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10101);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10125){if((e10125 instanceof Object)){
var ex__8699__auto__ = e10125;
var statearr_10126_10145 = state_10101;
(statearr_10126_10145[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10101);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10125;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10146 = state_10101;
state_10101 = G__10146;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_10101){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_10101);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10129,chs__$1,out,cnt,rets,dchan,dctr,done))
})();
var state__8792__auto__ = (function (){var statearr_10127 = f__8791__auto__.call(null);
(statearr_10127[(6)] = c__8790__auto___10129);

return statearr_10127;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10129,chs__$1,out,cnt,rets,dchan,dctr,done))
);


return out;
});

cljs.core.async.map.cljs$lang$maxFixedArity = 3;

/**
 * Takes a collection of source channels and returns a channel which
 *   contains all values taken from them. The returned channel will be
 *   unbuffered by default, or a buf-or-n can be supplied. The channel
 *   will close after all the source channels have closed.
 */
cljs.core.async.merge = (function cljs$core$async$merge(var_args){
var G__10149 = arguments.length;
switch (G__10149) {
case 1:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1 = (function (chs){
return cljs.core.async.merge.call(null,chs,null);
});

cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2 = (function (chs,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__8790__auto___10203 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10203,out){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10203,out){
return (function (state_10181){
var state_val_10182 = (state_10181[(1)]);
if((state_val_10182 === (7))){
var inst_10161 = (state_10181[(7)]);
var inst_10160 = (state_10181[(8)]);
var inst_10160__$1 = (state_10181[(2)]);
var inst_10161__$1 = cljs.core.nth.call(null,inst_10160__$1,(0),null);
var inst_10162 = cljs.core.nth.call(null,inst_10160__$1,(1),null);
var inst_10163 = (inst_10161__$1 == null);
var state_10181__$1 = (function (){var statearr_10183 = state_10181;
(statearr_10183[(7)] = inst_10161__$1);

(statearr_10183[(9)] = inst_10162);

(statearr_10183[(8)] = inst_10160__$1);

return statearr_10183;
})();
if(cljs.core.truth_(inst_10163)){
var statearr_10184_10204 = state_10181__$1;
(statearr_10184_10204[(1)] = (8));

} else {
var statearr_10185_10205 = state_10181__$1;
(statearr_10185_10205[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10182 === (1))){
var inst_10150 = cljs.core.vec.call(null,chs);
var inst_10151 = inst_10150;
var state_10181__$1 = (function (){var statearr_10186 = state_10181;
(statearr_10186[(10)] = inst_10151);

return statearr_10186;
})();
var statearr_10187_10206 = state_10181__$1;
(statearr_10187_10206[(2)] = null);

(statearr_10187_10206[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10182 === (4))){
var inst_10151 = (state_10181[(10)]);
var state_10181__$1 = state_10181;
return cljs.core.async.ioc_alts_BANG_.call(null,state_10181__$1,(7),inst_10151);
} else {
if((state_val_10182 === (6))){
var inst_10177 = (state_10181[(2)]);
var state_10181__$1 = state_10181;
var statearr_10188_10207 = state_10181__$1;
(statearr_10188_10207[(2)] = inst_10177);

(statearr_10188_10207[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10182 === (3))){
var inst_10179 = (state_10181[(2)]);
var state_10181__$1 = state_10181;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10181__$1,inst_10179);
} else {
if((state_val_10182 === (2))){
var inst_10151 = (state_10181[(10)]);
var inst_10153 = cljs.core.count.call(null,inst_10151);
var inst_10154 = (inst_10153 > (0));
var state_10181__$1 = state_10181;
if(cljs.core.truth_(inst_10154)){
var statearr_10190_10208 = state_10181__$1;
(statearr_10190_10208[(1)] = (4));

} else {
var statearr_10191_10209 = state_10181__$1;
(statearr_10191_10209[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10182 === (11))){
var inst_10151 = (state_10181[(10)]);
var inst_10170 = (state_10181[(2)]);
var tmp10189 = inst_10151;
var inst_10151__$1 = tmp10189;
var state_10181__$1 = (function (){var statearr_10192 = state_10181;
(statearr_10192[(10)] = inst_10151__$1);

(statearr_10192[(11)] = inst_10170);

return statearr_10192;
})();
var statearr_10193_10210 = state_10181__$1;
(statearr_10193_10210[(2)] = null);

(statearr_10193_10210[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10182 === (9))){
var inst_10161 = (state_10181[(7)]);
var state_10181__$1 = state_10181;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10181__$1,(11),out,inst_10161);
} else {
if((state_val_10182 === (5))){
var inst_10175 = cljs.core.async.close_BANG_.call(null,out);
var state_10181__$1 = state_10181;
var statearr_10194_10211 = state_10181__$1;
(statearr_10194_10211[(2)] = inst_10175);

(statearr_10194_10211[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10182 === (10))){
var inst_10173 = (state_10181[(2)]);
var state_10181__$1 = state_10181;
var statearr_10195_10212 = state_10181__$1;
(statearr_10195_10212[(2)] = inst_10173);

(statearr_10195_10212[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10182 === (8))){
var inst_10161 = (state_10181[(7)]);
var inst_10151 = (state_10181[(10)]);
var inst_10162 = (state_10181[(9)]);
var inst_10160 = (state_10181[(8)]);
var inst_10165 = (function (){var cs = inst_10151;
var vec__10156 = inst_10160;
var v = inst_10161;
var c = inst_10162;
return ((function (cs,vec__10156,v,c,inst_10161,inst_10151,inst_10162,inst_10160,state_val_10182,c__8790__auto___10203,out){
return (function (p1__10147_SHARP_){
return cljs.core.not_EQ_.call(null,c,p1__10147_SHARP_);
});
;})(cs,vec__10156,v,c,inst_10161,inst_10151,inst_10162,inst_10160,state_val_10182,c__8790__auto___10203,out))
})();
var inst_10166 = cljs.core.filterv.call(null,inst_10165,inst_10151);
var inst_10151__$1 = inst_10166;
var state_10181__$1 = (function (){var statearr_10196 = state_10181;
(statearr_10196[(10)] = inst_10151__$1);

return statearr_10196;
})();
var statearr_10197_10213 = state_10181__$1;
(statearr_10197_10213[(2)] = null);

(statearr_10197_10213[(1)] = (2));


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
});})(c__8790__auto___10203,out))
;
return ((function (switch__8695__auto__,c__8790__auto___10203,out){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10198 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_10198[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10198[(1)] = (1));

return statearr_10198;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_10181){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10181);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10199){if((e10199 instanceof Object)){
var ex__8699__auto__ = e10199;
var statearr_10200_10214 = state_10181;
(statearr_10200_10214[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10181);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10199;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10215 = state_10181;
state_10181 = G__10215;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_10181){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_10181);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10203,out))
})();
var state__8792__auto__ = (function (){var statearr_10201 = f__8791__auto__.call(null);
(statearr_10201[(6)] = c__8790__auto___10203);

return statearr_10201;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10203,out))
);


return out;
});

cljs.core.async.merge.cljs$lang$maxFixedArity = 2;

/**
 * Returns a channel containing the single (collection) result of the
 *   items taken from the channel conjoined to the supplied
 *   collection. ch must close before into produces a result.
 */
cljs.core.async.into = (function cljs$core$async$into(coll,ch){
return cljs.core.async.reduce.call(null,cljs.core.conj,coll,ch);
});
/**
 * Returns a channel that will return, at most, n items from ch. After n items
 * have been returned, or ch has been closed, the return chanel will close.
 * 
 *   The output channel is unbuffered by default, unless buf-or-n is given.
 */
cljs.core.async.take = (function cljs$core$async$take(var_args){
var G__10217 = arguments.length;
switch (G__10217) {
case 2:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.take.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.take.call(null,n,ch,null);
});

cljs.core.async.take.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__8790__auto___10262 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10262,out){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10262,out){
return (function (state_10241){
var state_val_10242 = (state_10241[(1)]);
if((state_val_10242 === (7))){
var inst_10223 = (state_10241[(7)]);
var inst_10223__$1 = (state_10241[(2)]);
var inst_10224 = (inst_10223__$1 == null);
var inst_10225 = cljs.core.not.call(null,inst_10224);
var state_10241__$1 = (function (){var statearr_10243 = state_10241;
(statearr_10243[(7)] = inst_10223__$1);

return statearr_10243;
})();
if(inst_10225){
var statearr_10244_10263 = state_10241__$1;
(statearr_10244_10263[(1)] = (8));

} else {
var statearr_10245_10264 = state_10241__$1;
(statearr_10245_10264[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (1))){
var inst_10218 = (0);
var state_10241__$1 = (function (){var statearr_10246 = state_10241;
(statearr_10246[(8)] = inst_10218);

return statearr_10246;
})();
var statearr_10247_10265 = state_10241__$1;
(statearr_10247_10265[(2)] = null);

(statearr_10247_10265[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (4))){
var state_10241__$1 = state_10241;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10241__$1,(7),ch);
} else {
if((state_val_10242 === (6))){
var inst_10236 = (state_10241[(2)]);
var state_10241__$1 = state_10241;
var statearr_10248_10266 = state_10241__$1;
(statearr_10248_10266[(2)] = inst_10236);

(statearr_10248_10266[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (3))){
var inst_10238 = (state_10241[(2)]);
var inst_10239 = cljs.core.async.close_BANG_.call(null,out);
var state_10241__$1 = (function (){var statearr_10249 = state_10241;
(statearr_10249[(9)] = inst_10238);

return statearr_10249;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10241__$1,inst_10239);
} else {
if((state_val_10242 === (2))){
var inst_10218 = (state_10241[(8)]);
var inst_10220 = (inst_10218 < n);
var state_10241__$1 = state_10241;
if(cljs.core.truth_(inst_10220)){
var statearr_10250_10267 = state_10241__$1;
(statearr_10250_10267[(1)] = (4));

} else {
var statearr_10251_10268 = state_10241__$1;
(statearr_10251_10268[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (11))){
var inst_10218 = (state_10241[(8)]);
var inst_10228 = (state_10241[(2)]);
var inst_10229 = (inst_10218 + (1));
var inst_10218__$1 = inst_10229;
var state_10241__$1 = (function (){var statearr_10252 = state_10241;
(statearr_10252[(10)] = inst_10228);

(statearr_10252[(8)] = inst_10218__$1);

return statearr_10252;
})();
var statearr_10253_10269 = state_10241__$1;
(statearr_10253_10269[(2)] = null);

(statearr_10253_10269[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (9))){
var state_10241__$1 = state_10241;
var statearr_10254_10270 = state_10241__$1;
(statearr_10254_10270[(2)] = null);

(statearr_10254_10270[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (5))){
var state_10241__$1 = state_10241;
var statearr_10255_10271 = state_10241__$1;
(statearr_10255_10271[(2)] = null);

(statearr_10255_10271[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (10))){
var inst_10233 = (state_10241[(2)]);
var state_10241__$1 = state_10241;
var statearr_10256_10272 = state_10241__$1;
(statearr_10256_10272[(2)] = inst_10233);

(statearr_10256_10272[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10242 === (8))){
var inst_10223 = (state_10241[(7)]);
var state_10241__$1 = state_10241;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10241__$1,(11),out,inst_10223);
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
});})(c__8790__auto___10262,out))
;
return ((function (switch__8695__auto__,c__8790__auto___10262,out){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10257 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_10257[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10257[(1)] = (1));

return statearr_10257;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_10241){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10241);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10258){if((e10258 instanceof Object)){
var ex__8699__auto__ = e10258;
var statearr_10259_10273 = state_10241;
(statearr_10259_10273[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10241);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10258;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10274 = state_10241;
state_10241 = G__10274;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_10241){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_10241);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10262,out))
})();
var state__8792__auto__ = (function (){var statearr_10260 = f__8791__auto__.call(null);
(statearr_10260[(6)] = c__8790__auto___10262);

return statearr_10260;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10262,out))
);


return out;
});

cljs.core.async.take.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_LT_ = (function cljs$core$async$map_LT_(f,ch){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async10276 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async10276 = (function (f,ch,meta10277){
this.f = f;
this.ch = ch;
this.meta10277 = meta10277;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10278,meta10277__$1){
var self__ = this;
var _10278__$1 = this;
return (new cljs.core.async.t_cljs$core$async10276(self__.f,self__.ch,meta10277__$1));
});

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10278){
var self__ = this;
var _10278__$1 = this;
return self__.meta10277;
});

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,(function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async10279 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async10279 = (function (f,ch,meta10277,_,fn1,meta10280){
this.f = f;
this.ch = ch;
this.meta10277 = meta10277;
this._ = _;
this.fn1 = fn1;
this.meta10280 = meta10280;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async10279.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (___$1){
return (function (_10281,meta10280__$1){
var self__ = this;
var _10281__$1 = this;
return (new cljs.core.async.t_cljs$core$async10279(self__.f,self__.ch,self__.meta10277,self__._,self__.fn1,meta10280__$1));
});})(___$1))
;

cljs.core.async.t_cljs$core$async10279.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (___$1){
return (function (_10281){
var self__ = this;
var _10281__$1 = this;
return self__.meta10280;
});})(___$1))
;

cljs.core.async.t_cljs$core$async10279.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10279.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.fn1);
});})(___$1))
;

cljs.core.async.t_cljs$core$async10279.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
return true;
});})(___$1))
;

cljs.core.async.t_cljs$core$async10279.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
var f1 = cljs.core.async.impl.protocols.commit.call(null,self__.fn1);
return ((function (f1,___$2,___$1){
return (function (p1__10275_SHARP_){
return f1.call(null,(((p1__10275_SHARP_ == null))?null:self__.f.call(null,p1__10275_SHARP_)));
});
;})(f1,___$2,___$1))
});})(___$1))
;

cljs.core.async.t_cljs$core$async10279.getBasis = ((function (___$1){
return (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta10277","meta10277",266641154,null),cljs.core.with_meta(new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core.async","t_cljs$core$async10276","cljs.core.async/t_cljs$core$async10276",1235777415,null)], null)),new cljs.core.Symbol(null,"fn1","fn1",895834444,null),new cljs.core.Symbol(null,"meta10280","meta10280",-630958556,null)], null);
});})(___$1))
;

cljs.core.async.t_cljs$core$async10279.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async10279.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async10279";

cljs.core.async.t_cljs$core$async10279.cljs$lang$ctorPrWriter = ((function (___$1){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async10279");
});})(___$1))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async10279.
 */
cljs.core.async.__GT_t_cljs$core$async10279 = ((function (___$1){
return (function cljs$core$async$map_LT__$___GT_t_cljs$core$async10279(f__$1,ch__$1,meta10277__$1,___$2,fn1__$1,meta10280){
return (new cljs.core.async.t_cljs$core$async10279(f__$1,ch__$1,meta10277__$1,___$2,fn1__$1,meta10280));
});})(___$1))
;

}

return (new cljs.core.async.t_cljs$core$async10279(self__.f,self__.ch,self__.meta10277,___$1,fn1,cljs.core.PersistentArrayMap.EMPTY));
})()
);
if(cljs.core.truth_((function (){var and__4120__auto__ = ret;
if(cljs.core.truth_(and__4120__auto__)){
return (!((cljs.core.deref.call(null,ret) == null)));
} else {
return and__4120__auto__;
}
})())){
return cljs.core.async.impl.channels.box.call(null,self__.f.call(null,cljs.core.deref.call(null,ret)));
} else {
return ret;
}
});

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10276.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
});

cljs.core.async.t_cljs$core$async10276.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta10277","meta10277",266641154,null)], null);
});

cljs.core.async.t_cljs$core$async10276.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async10276.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async10276";

cljs.core.async.t_cljs$core$async10276.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async10276");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async10276.
 */
cljs.core.async.__GT_t_cljs$core$async10276 = (function cljs$core$async$map_LT__$___GT_t_cljs$core$async10276(f__$1,ch__$1,meta10277){
return (new cljs.core.async.t_cljs$core$async10276(f__$1,ch__$1,meta10277));
});

}

return (new cljs.core.async.t_cljs$core$async10276(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_GT_ = (function cljs$core$async$map_GT_(f,ch){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async10282 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async10282 = (function (f,ch,meta10283){
this.f = f;
this.ch = ch;
this.meta10283 = meta10283;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10284,meta10283__$1){
var self__ = this;
var _10284__$1 = this;
return (new cljs.core.async.t_cljs$core$async10282(self__.f,self__.ch,meta10283__$1));
});

cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10284){
var self__ = this;
var _10284__$1 = this;
return self__.meta10283;
});

cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});

cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10282.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,self__.f.call(null,val),fn1);
});

cljs.core.async.t_cljs$core$async10282.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta10283","meta10283",-1537533489,null)], null);
});

cljs.core.async.t_cljs$core$async10282.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async10282.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async10282";

cljs.core.async.t_cljs$core$async10282.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async10282");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async10282.
 */
cljs.core.async.__GT_t_cljs$core$async10282 = (function cljs$core$async$map_GT__$___GT_t_cljs$core$async10282(f__$1,ch__$1,meta10283){
return (new cljs.core.async.t_cljs$core$async10282(f__$1,ch__$1,meta10283));
});

}

return (new cljs.core.async.t_cljs$core$async10282(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_GT_ = (function cljs$core$async$filter_GT_(p,ch){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async10285 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async10285 = (function (p,ch,meta10286){
this.p = p;
this.ch = ch;
this.meta10286 = meta10286;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10287,meta10286__$1){
var self__ = this;
var _10287__$1 = this;
return (new cljs.core.async.t_cljs$core$async10285(self__.p,self__.ch,meta10286__$1));
});

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10287){
var self__ = this;
var _10287__$1 = this;
return self__.meta10286;
});

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async10285.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_(self__.p.call(null,val))){
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
} else {
return cljs.core.async.impl.channels.box.call(null,cljs.core.not.call(null,cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch)));
}
});

cljs.core.async.t_cljs$core$async10285.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"p","p",1791580836,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta10286","meta10286",372614496,null)], null);
});

cljs.core.async.t_cljs$core$async10285.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async10285.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async10285";

cljs.core.async.t_cljs$core$async10285.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async10285");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async10285.
 */
cljs.core.async.__GT_t_cljs$core$async10285 = (function cljs$core$async$filter_GT__$___GT_t_cljs$core$async10285(p__$1,ch__$1,meta10286){
return (new cljs.core.async.t_cljs$core$async10285(p__$1,ch__$1,meta10286));
});

}

return (new cljs.core.async.t_cljs$core$async10285(p,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_GT_ = (function cljs$core$async$remove_GT_(p,ch){
return cljs.core.async.filter_GT_.call(null,cljs.core.complement.call(null,p),ch);
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_LT_ = (function cljs$core$async$filter_LT_(var_args){
var G__10289 = arguments.length;
switch (G__10289) {
case 2:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.filter_LT_.call(null,p,ch,null);
});

cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__8790__auto___10329 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10329,out){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10329,out){
return (function (state_10310){
var state_val_10311 = (state_10310[(1)]);
if((state_val_10311 === (7))){
var inst_10306 = (state_10310[(2)]);
var state_10310__$1 = state_10310;
var statearr_10312_10330 = state_10310__$1;
(statearr_10312_10330[(2)] = inst_10306);

(statearr_10312_10330[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (1))){
var state_10310__$1 = state_10310;
var statearr_10313_10331 = state_10310__$1;
(statearr_10313_10331[(2)] = null);

(statearr_10313_10331[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (4))){
var inst_10292 = (state_10310[(7)]);
var inst_10292__$1 = (state_10310[(2)]);
var inst_10293 = (inst_10292__$1 == null);
var state_10310__$1 = (function (){var statearr_10314 = state_10310;
(statearr_10314[(7)] = inst_10292__$1);

return statearr_10314;
})();
if(cljs.core.truth_(inst_10293)){
var statearr_10315_10332 = state_10310__$1;
(statearr_10315_10332[(1)] = (5));

} else {
var statearr_10316_10333 = state_10310__$1;
(statearr_10316_10333[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (6))){
var inst_10292 = (state_10310[(7)]);
var inst_10297 = p.call(null,inst_10292);
var state_10310__$1 = state_10310;
if(cljs.core.truth_(inst_10297)){
var statearr_10317_10334 = state_10310__$1;
(statearr_10317_10334[(1)] = (8));

} else {
var statearr_10318_10335 = state_10310__$1;
(statearr_10318_10335[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (3))){
var inst_10308 = (state_10310[(2)]);
var state_10310__$1 = state_10310;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10310__$1,inst_10308);
} else {
if((state_val_10311 === (2))){
var state_10310__$1 = state_10310;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10310__$1,(4),ch);
} else {
if((state_val_10311 === (11))){
var inst_10300 = (state_10310[(2)]);
var state_10310__$1 = state_10310;
var statearr_10319_10336 = state_10310__$1;
(statearr_10319_10336[(2)] = inst_10300);

(statearr_10319_10336[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (9))){
var state_10310__$1 = state_10310;
var statearr_10320_10337 = state_10310__$1;
(statearr_10320_10337[(2)] = null);

(statearr_10320_10337[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (5))){
var inst_10295 = cljs.core.async.close_BANG_.call(null,out);
var state_10310__$1 = state_10310;
var statearr_10321_10338 = state_10310__$1;
(statearr_10321_10338[(2)] = inst_10295);

(statearr_10321_10338[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (10))){
var inst_10303 = (state_10310[(2)]);
var state_10310__$1 = (function (){var statearr_10322 = state_10310;
(statearr_10322[(8)] = inst_10303);

return statearr_10322;
})();
var statearr_10323_10339 = state_10310__$1;
(statearr_10323_10339[(2)] = null);

(statearr_10323_10339[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10311 === (8))){
var inst_10292 = (state_10310[(7)]);
var state_10310__$1 = state_10310;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10310__$1,(11),out,inst_10292);
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
});})(c__8790__auto___10329,out))
;
return ((function (switch__8695__auto__,c__8790__auto___10329,out){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10324 = [null,null,null,null,null,null,null,null,null];
(statearr_10324[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10324[(1)] = (1));

return statearr_10324;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_10310){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10310);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10325){if((e10325 instanceof Object)){
var ex__8699__auto__ = e10325;
var statearr_10326_10340 = state_10310;
(statearr_10326_10340[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10310);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10325;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10341 = state_10310;
state_10310 = G__10341;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_10310){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_10310);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10329,out))
})();
var state__8792__auto__ = (function (){var statearr_10327 = f__8791__auto__.call(null);
(statearr_10327[(6)] = c__8790__auto___10329);

return statearr_10327;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10329,out))
);


return out;
});

cljs.core.async.filter_LT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_LT_ = (function cljs$core$async$remove_LT_(var_args){
var G__10343 = arguments.length;
switch (G__10343) {
case 2:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.remove_LT_.call(null,p,ch,null);
});

cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
return cljs.core.async.filter_LT_.call(null,cljs.core.complement.call(null,p),ch,buf_or_n);
});

cljs.core.async.remove_LT_.cljs$lang$maxFixedArity = 3;

cljs.core.async.mapcat_STAR_ = (function cljs$core$async$mapcat_STAR_(f,in$,out){
var c__8790__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto__){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto__){
return (function (state_10406){
var state_val_10407 = (state_10406[(1)]);
if((state_val_10407 === (7))){
var inst_10402 = (state_10406[(2)]);
var state_10406__$1 = state_10406;
var statearr_10408_10446 = state_10406__$1;
(statearr_10408_10446[(2)] = inst_10402);

(statearr_10408_10446[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (20))){
var inst_10372 = (state_10406[(7)]);
var inst_10383 = (state_10406[(2)]);
var inst_10384 = cljs.core.next.call(null,inst_10372);
var inst_10358 = inst_10384;
var inst_10359 = null;
var inst_10360 = (0);
var inst_10361 = (0);
var state_10406__$1 = (function (){var statearr_10409 = state_10406;
(statearr_10409[(8)] = inst_10359);

(statearr_10409[(9)] = inst_10361);

(statearr_10409[(10)] = inst_10360);

(statearr_10409[(11)] = inst_10383);

(statearr_10409[(12)] = inst_10358);

return statearr_10409;
})();
var statearr_10410_10447 = state_10406__$1;
(statearr_10410_10447[(2)] = null);

(statearr_10410_10447[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (1))){
var state_10406__$1 = state_10406;
var statearr_10411_10448 = state_10406__$1;
(statearr_10411_10448[(2)] = null);

(statearr_10411_10448[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (4))){
var inst_10347 = (state_10406[(13)]);
var inst_10347__$1 = (state_10406[(2)]);
var inst_10348 = (inst_10347__$1 == null);
var state_10406__$1 = (function (){var statearr_10412 = state_10406;
(statearr_10412[(13)] = inst_10347__$1);

return statearr_10412;
})();
if(cljs.core.truth_(inst_10348)){
var statearr_10413_10449 = state_10406__$1;
(statearr_10413_10449[(1)] = (5));

} else {
var statearr_10414_10450 = state_10406__$1;
(statearr_10414_10450[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (15))){
var state_10406__$1 = state_10406;
var statearr_10418_10451 = state_10406__$1;
(statearr_10418_10451[(2)] = null);

(statearr_10418_10451[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (21))){
var state_10406__$1 = state_10406;
var statearr_10419_10452 = state_10406__$1;
(statearr_10419_10452[(2)] = null);

(statearr_10419_10452[(1)] = (23));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (13))){
var inst_10359 = (state_10406[(8)]);
var inst_10361 = (state_10406[(9)]);
var inst_10360 = (state_10406[(10)]);
var inst_10358 = (state_10406[(12)]);
var inst_10368 = (state_10406[(2)]);
var inst_10369 = (inst_10361 + (1));
var tmp10415 = inst_10359;
var tmp10416 = inst_10360;
var tmp10417 = inst_10358;
var inst_10358__$1 = tmp10417;
var inst_10359__$1 = tmp10415;
var inst_10360__$1 = tmp10416;
var inst_10361__$1 = inst_10369;
var state_10406__$1 = (function (){var statearr_10420 = state_10406;
(statearr_10420[(8)] = inst_10359__$1);

(statearr_10420[(9)] = inst_10361__$1);

(statearr_10420[(10)] = inst_10360__$1);

(statearr_10420[(12)] = inst_10358__$1);

(statearr_10420[(14)] = inst_10368);

return statearr_10420;
})();
var statearr_10421_10453 = state_10406__$1;
(statearr_10421_10453[(2)] = null);

(statearr_10421_10453[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (22))){
var state_10406__$1 = state_10406;
var statearr_10422_10454 = state_10406__$1;
(statearr_10422_10454[(2)] = null);

(statearr_10422_10454[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (6))){
var inst_10347 = (state_10406[(13)]);
var inst_10356 = f.call(null,inst_10347);
var inst_10357 = cljs.core.seq.call(null,inst_10356);
var inst_10358 = inst_10357;
var inst_10359 = null;
var inst_10360 = (0);
var inst_10361 = (0);
var state_10406__$1 = (function (){var statearr_10423 = state_10406;
(statearr_10423[(8)] = inst_10359);

(statearr_10423[(9)] = inst_10361);

(statearr_10423[(10)] = inst_10360);

(statearr_10423[(12)] = inst_10358);

return statearr_10423;
})();
var statearr_10424_10455 = state_10406__$1;
(statearr_10424_10455[(2)] = null);

(statearr_10424_10455[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (17))){
var inst_10372 = (state_10406[(7)]);
var inst_10376 = cljs.core.chunk_first.call(null,inst_10372);
var inst_10377 = cljs.core.chunk_rest.call(null,inst_10372);
var inst_10378 = cljs.core.count.call(null,inst_10376);
var inst_10358 = inst_10377;
var inst_10359 = inst_10376;
var inst_10360 = inst_10378;
var inst_10361 = (0);
var state_10406__$1 = (function (){var statearr_10425 = state_10406;
(statearr_10425[(8)] = inst_10359);

(statearr_10425[(9)] = inst_10361);

(statearr_10425[(10)] = inst_10360);

(statearr_10425[(12)] = inst_10358);

return statearr_10425;
})();
var statearr_10426_10456 = state_10406__$1;
(statearr_10426_10456[(2)] = null);

(statearr_10426_10456[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (3))){
var inst_10404 = (state_10406[(2)]);
var state_10406__$1 = state_10406;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10406__$1,inst_10404);
} else {
if((state_val_10407 === (12))){
var inst_10392 = (state_10406[(2)]);
var state_10406__$1 = state_10406;
var statearr_10427_10457 = state_10406__$1;
(statearr_10427_10457[(2)] = inst_10392);

(statearr_10427_10457[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (2))){
var state_10406__$1 = state_10406;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10406__$1,(4),in$);
} else {
if((state_val_10407 === (23))){
var inst_10400 = (state_10406[(2)]);
var state_10406__$1 = state_10406;
var statearr_10428_10458 = state_10406__$1;
(statearr_10428_10458[(2)] = inst_10400);

(statearr_10428_10458[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (19))){
var inst_10387 = (state_10406[(2)]);
var state_10406__$1 = state_10406;
var statearr_10429_10459 = state_10406__$1;
(statearr_10429_10459[(2)] = inst_10387);

(statearr_10429_10459[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (11))){
var inst_10358 = (state_10406[(12)]);
var inst_10372 = (state_10406[(7)]);
var inst_10372__$1 = cljs.core.seq.call(null,inst_10358);
var state_10406__$1 = (function (){var statearr_10430 = state_10406;
(statearr_10430[(7)] = inst_10372__$1);

return statearr_10430;
})();
if(inst_10372__$1){
var statearr_10431_10460 = state_10406__$1;
(statearr_10431_10460[(1)] = (14));

} else {
var statearr_10432_10461 = state_10406__$1;
(statearr_10432_10461[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (9))){
var inst_10394 = (state_10406[(2)]);
var inst_10395 = cljs.core.async.impl.protocols.closed_QMARK_.call(null,out);
var state_10406__$1 = (function (){var statearr_10433 = state_10406;
(statearr_10433[(15)] = inst_10394);

return statearr_10433;
})();
if(cljs.core.truth_(inst_10395)){
var statearr_10434_10462 = state_10406__$1;
(statearr_10434_10462[(1)] = (21));

} else {
var statearr_10435_10463 = state_10406__$1;
(statearr_10435_10463[(1)] = (22));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (5))){
var inst_10350 = cljs.core.async.close_BANG_.call(null,out);
var state_10406__$1 = state_10406;
var statearr_10436_10464 = state_10406__$1;
(statearr_10436_10464[(2)] = inst_10350);

(statearr_10436_10464[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (14))){
var inst_10372 = (state_10406[(7)]);
var inst_10374 = cljs.core.chunked_seq_QMARK_.call(null,inst_10372);
var state_10406__$1 = state_10406;
if(inst_10374){
var statearr_10437_10465 = state_10406__$1;
(statearr_10437_10465[(1)] = (17));

} else {
var statearr_10438_10466 = state_10406__$1;
(statearr_10438_10466[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (16))){
var inst_10390 = (state_10406[(2)]);
var state_10406__$1 = state_10406;
var statearr_10439_10467 = state_10406__$1;
(statearr_10439_10467[(2)] = inst_10390);

(statearr_10439_10467[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10407 === (10))){
var inst_10359 = (state_10406[(8)]);
var inst_10361 = (state_10406[(9)]);
var inst_10366 = cljs.core._nth.call(null,inst_10359,inst_10361);
var state_10406__$1 = state_10406;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10406__$1,(13),out,inst_10366);
} else {
if((state_val_10407 === (18))){
var inst_10372 = (state_10406[(7)]);
var inst_10381 = cljs.core.first.call(null,inst_10372);
var state_10406__$1 = state_10406;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10406__$1,(20),out,inst_10381);
} else {
if((state_val_10407 === (8))){
var inst_10361 = (state_10406[(9)]);
var inst_10360 = (state_10406[(10)]);
var inst_10363 = (inst_10361 < inst_10360);
var inst_10364 = inst_10363;
var state_10406__$1 = state_10406;
if(cljs.core.truth_(inst_10364)){
var statearr_10440_10468 = state_10406__$1;
(statearr_10440_10468[(1)] = (10));

} else {
var statearr_10441_10469 = state_10406__$1;
(statearr_10441_10469[(1)] = (11));

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
}
}
}
}
}
}
}
});})(c__8790__auto__))
;
return ((function (switch__8695__auto__,c__8790__auto__){
return (function() {
var cljs$core$async$mapcat_STAR__$_state_machine__8696__auto__ = null;
var cljs$core$async$mapcat_STAR__$_state_machine__8696__auto____0 = (function (){
var statearr_10442 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_10442[(0)] = cljs$core$async$mapcat_STAR__$_state_machine__8696__auto__);

(statearr_10442[(1)] = (1));

return statearr_10442;
});
var cljs$core$async$mapcat_STAR__$_state_machine__8696__auto____1 = (function (state_10406){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10406);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10443){if((e10443 instanceof Object)){
var ex__8699__auto__ = e10443;
var statearr_10444_10470 = state_10406;
(statearr_10444_10470[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10406);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10443;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10471 = state_10406;
state_10406 = G__10471;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$mapcat_STAR__$_state_machine__8696__auto__ = function(state_10406){
switch(arguments.length){
case 0:
return cljs$core$async$mapcat_STAR__$_state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$mapcat_STAR__$_state_machine__8696__auto____1.call(this,state_10406);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mapcat_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mapcat_STAR__$_state_machine__8696__auto____0;
cljs$core$async$mapcat_STAR__$_state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mapcat_STAR__$_state_machine__8696__auto____1;
return cljs$core$async$mapcat_STAR__$_state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto__))
})();
var state__8792__auto__ = (function (){var statearr_10445 = f__8791__auto__.call(null);
(statearr_10445[(6)] = c__8790__auto__);

return statearr_10445;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto__))
);

return c__8790__auto__;
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_LT_ = (function cljs$core$async$mapcat_LT_(var_args){
var G__10473 = arguments.length;
switch (G__10473) {
case 2:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2 = (function (f,in$){
return cljs.core.async.mapcat_LT_.call(null,f,in$,null);
});

cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3 = (function (f,in$,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
cljs.core.async.mapcat_STAR_.call(null,f,in$,out);

return out;
});

cljs.core.async.mapcat_LT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_GT_ = (function cljs$core$async$mapcat_GT_(var_args){
var G__10476 = arguments.length;
switch (G__10476) {
case 2:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2 = (function (f,out){
return cljs.core.async.mapcat_GT_.call(null,f,out,null);
});

cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3 = (function (f,out,buf_or_n){
var in$ = cljs.core.async.chan.call(null,buf_or_n);
cljs.core.async.mapcat_STAR_.call(null,f,in$,out);

return in$;
});

cljs.core.async.mapcat_GT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.unique = (function cljs$core$async$unique(var_args){
var G__10479 = arguments.length;
switch (G__10479) {
case 1:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1 = (function (ch){
return cljs.core.async.unique.call(null,ch,null);
});

cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2 = (function (ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__8790__auto___10526 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10526,out){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10526,out){
return (function (state_10503){
var state_val_10504 = (state_10503[(1)]);
if((state_val_10504 === (7))){
var inst_10498 = (state_10503[(2)]);
var state_10503__$1 = state_10503;
var statearr_10505_10527 = state_10503__$1;
(statearr_10505_10527[(2)] = inst_10498);

(statearr_10505_10527[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10504 === (1))){
var inst_10480 = null;
var state_10503__$1 = (function (){var statearr_10506 = state_10503;
(statearr_10506[(7)] = inst_10480);

return statearr_10506;
})();
var statearr_10507_10528 = state_10503__$1;
(statearr_10507_10528[(2)] = null);

(statearr_10507_10528[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10504 === (4))){
var inst_10483 = (state_10503[(8)]);
var inst_10483__$1 = (state_10503[(2)]);
var inst_10484 = (inst_10483__$1 == null);
var inst_10485 = cljs.core.not.call(null,inst_10484);
var state_10503__$1 = (function (){var statearr_10508 = state_10503;
(statearr_10508[(8)] = inst_10483__$1);

return statearr_10508;
})();
if(inst_10485){
var statearr_10509_10529 = state_10503__$1;
(statearr_10509_10529[(1)] = (5));

} else {
var statearr_10510_10530 = state_10503__$1;
(statearr_10510_10530[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10504 === (6))){
var state_10503__$1 = state_10503;
var statearr_10511_10531 = state_10503__$1;
(statearr_10511_10531[(2)] = null);

(statearr_10511_10531[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10504 === (3))){
var inst_10500 = (state_10503[(2)]);
var inst_10501 = cljs.core.async.close_BANG_.call(null,out);
var state_10503__$1 = (function (){var statearr_10512 = state_10503;
(statearr_10512[(9)] = inst_10500);

return statearr_10512;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10503__$1,inst_10501);
} else {
if((state_val_10504 === (2))){
var state_10503__$1 = state_10503;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10503__$1,(4),ch);
} else {
if((state_val_10504 === (11))){
var inst_10483 = (state_10503[(8)]);
var inst_10492 = (state_10503[(2)]);
var inst_10480 = inst_10483;
var state_10503__$1 = (function (){var statearr_10513 = state_10503;
(statearr_10513[(7)] = inst_10480);

(statearr_10513[(10)] = inst_10492);

return statearr_10513;
})();
var statearr_10514_10532 = state_10503__$1;
(statearr_10514_10532[(2)] = null);

(statearr_10514_10532[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10504 === (9))){
var inst_10483 = (state_10503[(8)]);
var state_10503__$1 = state_10503;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10503__$1,(11),out,inst_10483);
} else {
if((state_val_10504 === (5))){
var inst_10480 = (state_10503[(7)]);
var inst_10483 = (state_10503[(8)]);
var inst_10487 = cljs.core._EQ_.call(null,inst_10483,inst_10480);
var state_10503__$1 = state_10503;
if(inst_10487){
var statearr_10516_10533 = state_10503__$1;
(statearr_10516_10533[(1)] = (8));

} else {
var statearr_10517_10534 = state_10503__$1;
(statearr_10517_10534[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10504 === (10))){
var inst_10495 = (state_10503[(2)]);
var state_10503__$1 = state_10503;
var statearr_10518_10535 = state_10503__$1;
(statearr_10518_10535[(2)] = inst_10495);

(statearr_10518_10535[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10504 === (8))){
var inst_10480 = (state_10503[(7)]);
var tmp10515 = inst_10480;
var inst_10480__$1 = tmp10515;
var state_10503__$1 = (function (){var statearr_10519 = state_10503;
(statearr_10519[(7)] = inst_10480__$1);

return statearr_10519;
})();
var statearr_10520_10536 = state_10503__$1;
(statearr_10520_10536[(2)] = null);

(statearr_10520_10536[(1)] = (2));


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
});})(c__8790__auto___10526,out))
;
return ((function (switch__8695__auto__,c__8790__auto___10526,out){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10521 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_10521[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10521[(1)] = (1));

return statearr_10521;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_10503){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10503);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10522){if((e10522 instanceof Object)){
var ex__8699__auto__ = e10522;
var statearr_10523_10537 = state_10503;
(statearr_10523_10537[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10503);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10522;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10538 = state_10503;
state_10503 = G__10538;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_10503){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_10503);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10526,out))
})();
var state__8792__auto__ = (function (){var statearr_10524 = f__8791__auto__.call(null);
(statearr_10524[(6)] = c__8790__auto___10526);

return statearr_10524;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10526,out))
);


return out;
});

cljs.core.async.unique.cljs$lang$maxFixedArity = 2;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition = (function cljs$core$async$partition(var_args){
var G__10540 = arguments.length;
switch (G__10540) {
case 2:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.partition.call(null,n,ch,null);
});

cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__8790__auto___10606 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10606,out){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10606,out){
return (function (state_10578){
var state_val_10579 = (state_10578[(1)]);
if((state_val_10579 === (7))){
var inst_10574 = (state_10578[(2)]);
var state_10578__$1 = state_10578;
var statearr_10580_10607 = state_10578__$1;
(statearr_10580_10607[(2)] = inst_10574);

(statearr_10580_10607[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (1))){
var inst_10541 = (new Array(n));
var inst_10542 = inst_10541;
var inst_10543 = (0);
var state_10578__$1 = (function (){var statearr_10581 = state_10578;
(statearr_10581[(7)] = inst_10543);

(statearr_10581[(8)] = inst_10542);

return statearr_10581;
})();
var statearr_10582_10608 = state_10578__$1;
(statearr_10582_10608[(2)] = null);

(statearr_10582_10608[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (4))){
var inst_10546 = (state_10578[(9)]);
var inst_10546__$1 = (state_10578[(2)]);
var inst_10547 = (inst_10546__$1 == null);
var inst_10548 = cljs.core.not.call(null,inst_10547);
var state_10578__$1 = (function (){var statearr_10583 = state_10578;
(statearr_10583[(9)] = inst_10546__$1);

return statearr_10583;
})();
if(inst_10548){
var statearr_10584_10609 = state_10578__$1;
(statearr_10584_10609[(1)] = (5));

} else {
var statearr_10585_10610 = state_10578__$1;
(statearr_10585_10610[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (15))){
var inst_10568 = (state_10578[(2)]);
var state_10578__$1 = state_10578;
var statearr_10586_10611 = state_10578__$1;
(statearr_10586_10611[(2)] = inst_10568);

(statearr_10586_10611[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (13))){
var state_10578__$1 = state_10578;
var statearr_10587_10612 = state_10578__$1;
(statearr_10587_10612[(2)] = null);

(statearr_10587_10612[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (6))){
var inst_10543 = (state_10578[(7)]);
var inst_10564 = (inst_10543 > (0));
var state_10578__$1 = state_10578;
if(cljs.core.truth_(inst_10564)){
var statearr_10588_10613 = state_10578__$1;
(statearr_10588_10613[(1)] = (12));

} else {
var statearr_10589_10614 = state_10578__$1;
(statearr_10589_10614[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (3))){
var inst_10576 = (state_10578[(2)]);
var state_10578__$1 = state_10578;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10578__$1,inst_10576);
} else {
if((state_val_10579 === (12))){
var inst_10542 = (state_10578[(8)]);
var inst_10566 = cljs.core.vec.call(null,inst_10542);
var state_10578__$1 = state_10578;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10578__$1,(15),out,inst_10566);
} else {
if((state_val_10579 === (2))){
var state_10578__$1 = state_10578;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10578__$1,(4),ch);
} else {
if((state_val_10579 === (11))){
var inst_10558 = (state_10578[(2)]);
var inst_10559 = (new Array(n));
var inst_10542 = inst_10559;
var inst_10543 = (0);
var state_10578__$1 = (function (){var statearr_10590 = state_10578;
(statearr_10590[(7)] = inst_10543);

(statearr_10590[(10)] = inst_10558);

(statearr_10590[(8)] = inst_10542);

return statearr_10590;
})();
var statearr_10591_10615 = state_10578__$1;
(statearr_10591_10615[(2)] = null);

(statearr_10591_10615[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (9))){
var inst_10542 = (state_10578[(8)]);
var inst_10556 = cljs.core.vec.call(null,inst_10542);
var state_10578__$1 = state_10578;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10578__$1,(11),out,inst_10556);
} else {
if((state_val_10579 === (5))){
var inst_10543 = (state_10578[(7)]);
var inst_10546 = (state_10578[(9)]);
var inst_10551 = (state_10578[(11)]);
var inst_10542 = (state_10578[(8)]);
var inst_10550 = (inst_10542[inst_10543] = inst_10546);
var inst_10551__$1 = (inst_10543 + (1));
var inst_10552 = (inst_10551__$1 < n);
var state_10578__$1 = (function (){var statearr_10592 = state_10578;
(statearr_10592[(11)] = inst_10551__$1);

(statearr_10592[(12)] = inst_10550);

return statearr_10592;
})();
if(cljs.core.truth_(inst_10552)){
var statearr_10593_10616 = state_10578__$1;
(statearr_10593_10616[(1)] = (8));

} else {
var statearr_10594_10617 = state_10578__$1;
(statearr_10594_10617[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (14))){
var inst_10571 = (state_10578[(2)]);
var inst_10572 = cljs.core.async.close_BANG_.call(null,out);
var state_10578__$1 = (function (){var statearr_10596 = state_10578;
(statearr_10596[(13)] = inst_10571);

return statearr_10596;
})();
var statearr_10597_10618 = state_10578__$1;
(statearr_10597_10618[(2)] = inst_10572);

(statearr_10597_10618[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (10))){
var inst_10562 = (state_10578[(2)]);
var state_10578__$1 = state_10578;
var statearr_10598_10619 = state_10578__$1;
(statearr_10598_10619[(2)] = inst_10562);

(statearr_10598_10619[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10579 === (8))){
var inst_10551 = (state_10578[(11)]);
var inst_10542 = (state_10578[(8)]);
var tmp10595 = inst_10542;
var inst_10542__$1 = tmp10595;
var inst_10543 = inst_10551;
var state_10578__$1 = (function (){var statearr_10599 = state_10578;
(statearr_10599[(7)] = inst_10543);

(statearr_10599[(8)] = inst_10542__$1);

return statearr_10599;
})();
var statearr_10600_10620 = state_10578__$1;
(statearr_10600_10620[(2)] = null);

(statearr_10600_10620[(1)] = (2));


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
});})(c__8790__auto___10606,out))
;
return ((function (switch__8695__auto__,c__8790__auto___10606,out){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10601 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_10601[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10601[(1)] = (1));

return statearr_10601;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_10578){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10578);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10602){if((e10602 instanceof Object)){
var ex__8699__auto__ = e10602;
var statearr_10603_10621 = state_10578;
(statearr_10603_10621[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10578);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10602;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10622 = state_10578;
state_10578 = G__10622;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_10578){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_10578);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10606,out))
})();
var state__8792__auto__ = (function (){var statearr_10604 = f__8791__auto__.call(null);
(statearr_10604[(6)] = c__8790__auto___10606);

return statearr_10604;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10606,out))
);


return out;
});

cljs.core.async.partition.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition_by = (function cljs$core$async$partition_by(var_args){
var G__10624 = arguments.length;
switch (G__10624) {
case 2:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2 = (function (f,ch){
return cljs.core.async.partition_by.call(null,f,ch,null);
});

cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3 = (function (f,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__8790__auto___10694 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__8790__auto___10694,out){
return (function (){
var f__8791__auto__ = (function (){var switch__8695__auto__ = ((function (c__8790__auto___10694,out){
return (function (state_10666){
var state_val_10667 = (state_10666[(1)]);
if((state_val_10667 === (7))){
var inst_10662 = (state_10666[(2)]);
var state_10666__$1 = state_10666;
var statearr_10668_10695 = state_10666__$1;
(statearr_10668_10695[(2)] = inst_10662);

(statearr_10668_10695[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (1))){
var inst_10625 = [];
var inst_10626 = inst_10625;
var inst_10627 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);
var state_10666__$1 = (function (){var statearr_10669 = state_10666;
(statearr_10669[(7)] = inst_10626);

(statearr_10669[(8)] = inst_10627);

return statearr_10669;
})();
var statearr_10670_10696 = state_10666__$1;
(statearr_10670_10696[(2)] = null);

(statearr_10670_10696[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (4))){
var inst_10630 = (state_10666[(9)]);
var inst_10630__$1 = (state_10666[(2)]);
var inst_10631 = (inst_10630__$1 == null);
var inst_10632 = cljs.core.not.call(null,inst_10631);
var state_10666__$1 = (function (){var statearr_10671 = state_10666;
(statearr_10671[(9)] = inst_10630__$1);

return statearr_10671;
})();
if(inst_10632){
var statearr_10672_10697 = state_10666__$1;
(statearr_10672_10697[(1)] = (5));

} else {
var statearr_10673_10698 = state_10666__$1;
(statearr_10673_10698[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (15))){
var inst_10656 = (state_10666[(2)]);
var state_10666__$1 = state_10666;
var statearr_10674_10699 = state_10666__$1;
(statearr_10674_10699[(2)] = inst_10656);

(statearr_10674_10699[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (13))){
var state_10666__$1 = state_10666;
var statearr_10675_10700 = state_10666__$1;
(statearr_10675_10700[(2)] = null);

(statearr_10675_10700[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (6))){
var inst_10626 = (state_10666[(7)]);
var inst_10651 = inst_10626.length;
var inst_10652 = (inst_10651 > (0));
var state_10666__$1 = state_10666;
if(cljs.core.truth_(inst_10652)){
var statearr_10676_10701 = state_10666__$1;
(statearr_10676_10701[(1)] = (12));

} else {
var statearr_10677_10702 = state_10666__$1;
(statearr_10677_10702[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (3))){
var inst_10664 = (state_10666[(2)]);
var state_10666__$1 = state_10666;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10666__$1,inst_10664);
} else {
if((state_val_10667 === (12))){
var inst_10626 = (state_10666[(7)]);
var inst_10654 = cljs.core.vec.call(null,inst_10626);
var state_10666__$1 = state_10666;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10666__$1,(15),out,inst_10654);
} else {
if((state_val_10667 === (2))){
var state_10666__$1 = state_10666;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10666__$1,(4),ch);
} else {
if((state_val_10667 === (11))){
var inst_10634 = (state_10666[(10)]);
var inst_10630 = (state_10666[(9)]);
var inst_10644 = (state_10666[(2)]);
var inst_10645 = [];
var inst_10646 = inst_10645.push(inst_10630);
var inst_10626 = inst_10645;
var inst_10627 = inst_10634;
var state_10666__$1 = (function (){var statearr_10678 = state_10666;
(statearr_10678[(11)] = inst_10646);

(statearr_10678[(12)] = inst_10644);

(statearr_10678[(7)] = inst_10626);

(statearr_10678[(8)] = inst_10627);

return statearr_10678;
})();
var statearr_10679_10703 = state_10666__$1;
(statearr_10679_10703[(2)] = null);

(statearr_10679_10703[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (9))){
var inst_10626 = (state_10666[(7)]);
var inst_10642 = cljs.core.vec.call(null,inst_10626);
var state_10666__$1 = state_10666;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10666__$1,(11),out,inst_10642);
} else {
if((state_val_10667 === (5))){
var inst_10634 = (state_10666[(10)]);
var inst_10630 = (state_10666[(9)]);
var inst_10627 = (state_10666[(8)]);
var inst_10634__$1 = f.call(null,inst_10630);
var inst_10635 = cljs.core._EQ_.call(null,inst_10634__$1,inst_10627);
var inst_10636 = cljs.core.keyword_identical_QMARK_.call(null,inst_10627,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));
var inst_10637 = ((inst_10635) || (inst_10636));
var state_10666__$1 = (function (){var statearr_10680 = state_10666;
(statearr_10680[(10)] = inst_10634__$1);

return statearr_10680;
})();
if(cljs.core.truth_(inst_10637)){
var statearr_10681_10704 = state_10666__$1;
(statearr_10681_10704[(1)] = (8));

} else {
var statearr_10682_10705 = state_10666__$1;
(statearr_10682_10705[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (14))){
var inst_10659 = (state_10666[(2)]);
var inst_10660 = cljs.core.async.close_BANG_.call(null,out);
var state_10666__$1 = (function (){var statearr_10684 = state_10666;
(statearr_10684[(13)] = inst_10659);

return statearr_10684;
})();
var statearr_10685_10706 = state_10666__$1;
(statearr_10685_10706[(2)] = inst_10660);

(statearr_10685_10706[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (10))){
var inst_10649 = (state_10666[(2)]);
var state_10666__$1 = state_10666;
var statearr_10686_10707 = state_10666__$1;
(statearr_10686_10707[(2)] = inst_10649);

(statearr_10686_10707[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_10667 === (8))){
var inst_10634 = (state_10666[(10)]);
var inst_10630 = (state_10666[(9)]);
var inst_10626 = (state_10666[(7)]);
var inst_10639 = inst_10626.push(inst_10630);
var tmp10683 = inst_10626;
var inst_10626__$1 = tmp10683;
var inst_10627 = inst_10634;
var state_10666__$1 = (function (){var statearr_10687 = state_10666;
(statearr_10687[(7)] = inst_10626__$1);

(statearr_10687[(8)] = inst_10627);

(statearr_10687[(14)] = inst_10639);

return statearr_10687;
})();
var statearr_10688_10708 = state_10666__$1;
(statearr_10688_10708[(2)] = null);

(statearr_10688_10708[(1)] = (2));


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
});})(c__8790__auto___10694,out))
;
return ((function (switch__8695__auto__,c__8790__auto___10694,out){
return (function() {
var cljs$core$async$state_machine__8696__auto__ = null;
var cljs$core$async$state_machine__8696__auto____0 = (function (){
var statearr_10689 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_10689[(0)] = cljs$core$async$state_machine__8696__auto__);

(statearr_10689[(1)] = (1));

return statearr_10689;
});
var cljs$core$async$state_machine__8696__auto____1 = (function (state_10666){
while(true){
var ret_value__8697__auto__ = (function (){try{while(true){
var result__8698__auto__ = switch__8695__auto__.call(null,state_10666);
if(cljs.core.keyword_identical_QMARK_.call(null,result__8698__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__8698__auto__;
}
break;
}
}catch (e10690){if((e10690 instanceof Object)){
var ex__8699__auto__ = e10690;
var statearr_10691_10709 = state_10666;
(statearr_10691_10709[(5)] = ex__8699__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10666);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e10690;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__8697__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__10710 = state_10666;
state_10666 = G__10710;
continue;
} else {
return ret_value__8697__auto__;
}
break;
}
});
cljs$core$async$state_machine__8696__auto__ = function(state_10666){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__8696__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__8696__auto____1.call(this,state_10666);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__8696__auto____0;
cljs$core$async$state_machine__8696__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__8696__auto____1;
return cljs$core$async$state_machine__8696__auto__;
})()
;})(switch__8695__auto__,c__8790__auto___10694,out))
})();
var state__8792__auto__ = (function (){var statearr_10692 = f__8791__auto__.call(null);
(statearr_10692[(6)] = c__8790__auto___10694);

return statearr_10692;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__8792__auto__);
});})(c__8790__auto___10694,out))
);


return out;
});

cljs.core.async.partition_by.cljs$lang$maxFixedArity = 3;


//# sourceMappingURL=async.js.map
