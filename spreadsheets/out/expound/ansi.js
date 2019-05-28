// Compiled by ClojureScript 1.10.520 {}
goog.provide('expound.ansi');
goog.require('cljs.core');
goog.require('clojure.string');
/**
 * Map of symbols to numeric SGR (select graphic rendition) codes.
 */
expound.ansi.sgr_code = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"bold","bold",-116809535),new cljs.core.Keyword(null,"blink","blink",-271985917),new cljs.core.Keyword(null,"white","white",-483998618),new cljs.core.Keyword(null,"yellow","yellow",-881035449),new cljs.core.Keyword(null,"bg-red","bg-red",-1645498040),new cljs.core.Keyword(null,"bg-cyan","bg-cyan",-1582237015),new cljs.core.Keyword(null,"green","green",-945526839),new cljs.core.Keyword(null,"bg-green","bg-green",-138353590),new cljs.core.Keyword(null,"fg-reset","fg-reset",1908873578),new cljs.core.Keyword(null,"cyan","cyan",1118839274),new cljs.core.Keyword(null,"bg-black","bg-black",2110303851),new cljs.core.Keyword(null,"fg-256","fg-256",-255017013),new cljs.core.Keyword(null,"underline","underline",2018066703),new cljs.core.Keyword(null,"bg-magenta","bg-magenta",2073641232),new cljs.core.Keyword(null,"strike","strike",-1173815471),new cljs.core.Keyword(null,"bg-yellow","bg-yellow",-1293468429),new cljs.core.Keyword(null,"hidden","hidden",-312506092),new cljs.core.Keyword(null,"red","red",-969428204),new cljs.core.Keyword(null,"blue","blue",-622100620),new cljs.core.Keyword(null,"bg-white","bg-white",-1453241673),new cljs.core.Keyword(null,"bg-256","bg-256",1097886744),new cljs.core.Keyword(null,"magenta","magenta",1687937081),new cljs.core.Keyword(null,"bg-blue","bg-blue",-1747478308),new cljs.core.Keyword(null,"bg-reset","bg-reset",857739453),new cljs.core.Keyword(null,"none","none",1333468478),new cljs.core.Keyword(null,"reverse","reverse",-888455266),new cljs.core.Keyword(null,"black","black",1294279647)],[(1),(5),(37),(33),(41),(46),(32),(42),(39),(36),(40),(38),(3),(45),(9),(43),(8),(31),(34),(47),(48),(35),(44),(49),(0),(7),(30)]);
expound.ansi._STAR_enable_color_STAR_ = false;
/**
 * Returns an ANSI escope string which will apply the given collection of SGR
 *   codes.
 */
expound.ansi.esc = (function expound$ansi$esc(codes){
var codes__$1 = cljs.core.map.call(null,expound.ansi.sgr_code,codes,codes);
var codes__$2 = clojure.string.join.call(null,";",codes__$1);
return ["\u001B","[",cljs.core.str.cljs$core$IFn$_invoke$arity$1(codes__$2),"m"].join('');
});
/**
 * Returns an ANSI escope string which will enact the given SGR codes.
 */
expound.ansi.escape = (function expound$ansi$escape(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2105 = arguments.length;
var i__4731__auto___2106 = (0);
while(true){
if((i__4731__auto___2106 < len__4730__auto___2105)){
args__4736__auto__.push((arguments[i__4731__auto___2106]));

var G__2107 = (i__4731__auto___2106 + (1));
i__4731__auto___2106 = G__2107;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return expound.ansi.escape.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

expound.ansi.escape.cljs$core$IFn$_invoke$arity$variadic = (function (codes){
return expound.ansi.esc.call(null,codes);
});

expound.ansi.escape.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
expound.ansi.escape.cljs$lang$applyTo = (function (seq2104){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq2104));
});

/**
 * Wraps the given string with SGR escapes to apply the given codes, then reset
 *   the graphics.
 */
expound.ansi.sgr = (function expound$ansi$sgr(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2110 = arguments.length;
var i__4731__auto___2111 = (0);
while(true){
if((i__4731__auto___2111 < len__4730__auto___2110)){
args__4736__auto__.push((arguments[i__4731__auto___2111]));

var G__2112 = (i__4731__auto___2111 + (1));
i__4731__auto___2111 = G__2112;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return expound.ansi.sgr.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

expound.ansi.sgr.cljs$core$IFn$_invoke$arity$variadic = (function (string,codes){
return [expound.ansi.esc.call(null,codes),cljs.core.str.cljs$core$IFn$_invoke$arity$1(string),expound.ansi.escape.call(null,new cljs.core.Keyword(null,"none","none",1333468478))].join('');
});

expound.ansi.sgr.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
expound.ansi.sgr.cljs$lang$applyTo = (function (seq2108){
var G__2109 = cljs.core.first.call(null,seq2108);
var seq2108__$1 = cljs.core.next.call(null,seq2108);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__2109,seq2108__$1);
});

/**
 * Removes color codes from the given string.
 */
expound.ansi.strip = (function expound$ansi$strip(string){
return clojure.string.replace.call(null,string,/\u001b\[[0-9;]*[mK]/,"");
});
var ret__4776__auto___2116 = (function (){
expound.ansi.with_color = (function expound$ansi$with_color(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2117 = arguments.length;
var i__4731__auto___2118 = (0);
while(true){
if((i__4731__auto___2118 < len__4730__auto___2117)){
args__4736__auto__.push((arguments[i__4731__auto___2118]));

var G__2119 = (i__4731__auto___2118 + (1));
i__4731__auto___2118 = G__2119;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((2) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((2)),(0),null)):null);
return expound.ansi.with_color.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__4737__auto__);
});

expound.ansi.with_color.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,body){
return cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","binding","cljs.core/binding",2050379843,null),null,(1),null)),(new cljs.core.List(null,cljs.core.vec.call(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("expound.ansi","*enable-color*","expound.ansi/*enable-color*",-970177940,null),null,(1),null)),(new cljs.core.List(null,true,null,(1),null)))))),null,(1),null)),body)));
});

expound.ansi.with_color.cljs$lang$maxFixedArity = (2);

/** @this {Function} */
expound.ansi.with_color.cljs$lang$applyTo = (function (seq2113){
var G__2114 = cljs.core.first.call(null,seq2113);
var seq2113__$1 = cljs.core.next.call(null,seq2113);
var G__2115 = cljs.core.first.call(null,seq2113__$1);
var seq2113__$2 = cljs.core.next.call(null,seq2113__$1);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__2114,G__2115,seq2113__$2);
});

return null;
})()
;
expound.ansi.with_color.cljs$lang$macro = true;

var ret__4776__auto___2124 = (function (){
expound.ansi.with_color_when = (function expound$ansi$with_color_when(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2125 = arguments.length;
var i__4731__auto___2126 = (0);
while(true){
if((i__4731__auto___2126 < len__4730__auto___2125)){
args__4736__auto__.push((arguments[i__4731__auto___2126]));

var G__2127 = (i__4731__auto___2126 + (1));
i__4731__auto___2126 = G__2127;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((3) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((3)),(0),null)):null);
return expound.ansi.with_color_when.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__4737__auto__);
});

expound.ansi.with_color_when.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,b,body){
return cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"if","if",1181717262,null),null,(1),null)),(new cljs.core.List(null,b,null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("expound.ansi","with-color","expound.ansi/with-color",-531311072,null),null,(1),null)),body))),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"do","do",1686842252,null),null,(1),null)),body))),null,(1),null)))));
});

expound.ansi.with_color_when.cljs$lang$maxFixedArity = (3);

/** @this {Function} */
expound.ansi.with_color_when.cljs$lang$applyTo = (function (seq2120){
var G__2121 = cljs.core.first.call(null,seq2120);
var seq2120__$1 = cljs.core.next.call(null,seq2120);
var G__2122 = cljs.core.first.call(null,seq2120__$1);
var seq2120__$2 = cljs.core.next.call(null,seq2120__$1);
var G__2123 = cljs.core.first.call(null,seq2120__$2);
var seq2120__$3 = cljs.core.next.call(null,seq2120__$2);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__2121,G__2122,G__2123,seq2120__$3);
});

return null;
})()
;
expound.ansi.with_color_when.cljs$lang$macro = true;

expound.ansi.ansi_code_QMARK_ = expound.ansi.sgr_code;
expound.ansi._STAR_print_styles_STAR_ = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"good-key","good-key",-1265033983),new cljs.core.Keyword(null,"warning-key","warning-key",-1487066651),new cljs.core.Keyword(null,"correct-key","correct-key",1087492967),new cljs.core.Keyword(null,"highlight","highlight",-800930873),new cljs.core.Keyword(null,"focus-path","focus-path",-2048689431),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659),new cljs.core.Keyword(null,"footer","footer",1606445390),new cljs.core.Keyword(null,"header","header",119441134),new cljs.core.Keyword(null,"good-pred","good-pred",-629085297),new cljs.core.Keyword(null,"good","good",511701169),new cljs.core.Keyword(null,"focus-key","focus-key",2074966449),new cljs.core.Keyword(null,"pointer","pointer",85071187),new cljs.core.Keyword(null,"bad","bad",1127186645),new cljs.core.Keyword(null,"error-key","error-key",-1651308715),new cljs.core.Keyword(null,"none","none",1333468478),new cljs.core.Keyword(null,"message","message",-406056002)],[new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"bold","bold",-116809535)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"bold","bold",-116809535)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"magenta","magenta",1687937081)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"red","red",-969428204)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"cyan","cyan",1118839274)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"cyan","cyan",1118839274)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"bold","bold",-116809535)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"magenta","magenta",1687937081)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"red","red",-969428204)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"red","red",-969428204)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"none","none",1333468478)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"magenta","magenta",1687937081)], null)]);
var ret__4776__auto___2130 = expound.ansi.black_and_white = (function expound$ansi$black_and_white(_AMPERSAND_form,_AMPERSAND_env,body){
var _STAR_print_styles_STAR__orig_val__2128 = expound.ansi._STAR_print_styles_STAR_;
var _STAR_print_styles_STAR__temp_val__2129 = cljs.core.PersistentArrayMap.EMPTY;
expound.ansi._STAR_print_styles_STAR_ = _STAR_print_styles_STAR__temp_val__2129;

try{return cljs.core.unquote.call(null,body);
}finally {expound.ansi._STAR_print_styles_STAR_ = _STAR_print_styles_STAR__orig_val__2128;
}});
expound.ansi.black_and_white.cljs$lang$macro = true;

expound.ansi.resolve_styles = (function expound$ansi$resolve_styles(styles){
var temp__5718__auto__ = cljs.core.not_empty.call(null,cljs.core.mapcat.call(null,(function (p1__2131_SHARP_){
var or__4131__auto__ = (function (){var temp__5720__auto__ = expound.ansi._STAR_print_styles_STAR_.call(null,p1__2131_SHARP_);
if(cljs.core.truth_(temp__5720__auto__)){
var res = temp__5720__auto__;
return res;
} else {
return null;
}
})();
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__2131_SHARP_], null);
}
}),styles));
if(cljs.core.truth_(temp__5718__auto__)){
var res = temp__5718__auto__;
return res;
} else {
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"bold","bold",-116809535)], null);
}
});
expound.ansi.color = (function expound$ansi$color(var_args){
var args__4736__auto__ = [];
var len__4730__auto___2134 = arguments.length;
var i__4731__auto___2135 = (0);
while(true){
if((i__4731__auto___2135 < len__4730__auto___2134)){
args__4736__auto__.push((arguments[i__4731__auto___2135]));

var G__2136 = (i__4731__auto___2135 + (1));
i__4731__auto___2135 = G__2136;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return expound.ansi.color.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

expound.ansi.color.cljs$core$IFn$_invoke$arity$variadic = (function (s,styles){
if(expound.ansi._STAR_enable_color_STAR_){
return cljs.core.apply.call(null,expound.ansi.sgr,s,expound.ansi.resolve_styles.call(null,styles));
} else {
return s;
}
});

expound.ansi.color.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
expound.ansi.color.cljs$lang$applyTo = (function (seq2132){
var G__2133 = cljs.core.first.call(null,seq2132);
var seq2132__$1 = cljs.core.next.call(null,seq2132);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__2133,seq2132__$1);
});

var ret__4776__auto___2137 = expound.ansi.print_color_text = (function expound$ansi$print_color_text(_AMPERSAND_form,_AMPERSAND_env,codes,body){
return cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"if","if",1181717262,null),null,(1),null)),(new cljs.core.List(null,new cljs.core.Symbol("expound.ansi","*enable-color*","expound.ansi/*enable-color*",-970177940,null),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,"do","do",1686842252,null),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,".write",".write",1632359621,null),null,(1),null)),(new cljs.core.List(null,cljs.core.with_meta.call(null,new cljs.core.Symbol("cljs.core","*out*","cljs.core/*out*",-1813565621,null),cljs.core.apply.call(null,cljs.core.array_map,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Keyword(null,"file","file",-1269645878),null,(1),null)),(new cljs.core.List(null,"/home/joshua/.cljs/.aot_cache/1.10.520/D260CBD/expound/ansi.cljc",null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"line","line",212345235),null,(1),null)),(new cljs.core.List(null,117,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"column","column",2078222095),null,(1),null)),(new cljs.core.List(null,32,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"end-line","end-line",1837326455),null,(1),null)),(new cljs.core.List(null,117,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"end-column","end-column",1425389514),null,(1),null)),(new cljs.core.List(null,37,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"tag","tag",-1290361223),null,(1),null)),(new cljs.core.List(null,new cljs.core.Symbol(null,"java.io.Writer","java.io.Writer",-1891611896,null),null,(1),null))))))),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","apply","cljs.core/apply",1757277831,null),null,(1),null)),(new cljs.core.List(null,new cljs.core.Symbol("cljs.core","str","cljs.core/str",-1971828991,null),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("expound.ansi","esc","expound.ansi/esc",509051398,null),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("expound.ansi","resolve-styles","expound.ansi/resolve-styles",450366791,null),null,(1),null)),(new cljs.core.List(null,codes,null,(1),null))))),null,(1),null))))),null,(1),null))))),null,(1),null))))),null,(1),null)),(new cljs.core.List(null,body,null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol(null,".write",".write",1632359621,null),null,(1),null)),(new cljs.core.List(null,cljs.core.with_meta.call(null,new cljs.core.Symbol("cljs.core","*out*","cljs.core/*out*",-1813565621,null),cljs.core.apply.call(null,cljs.core.array_map,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Keyword(null,"file","file",-1269645878),null,(1),null)),(new cljs.core.List(null,"/home/joshua/.cljs/.aot_cache/1.10.520/D260CBD/expound/ansi.cljc",null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"line","line",212345235),null,(1),null)),(new cljs.core.List(null,119,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"column","column",2078222095),null,(1),null)),(new cljs.core.List(null,32,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"end-line","end-line",1837326455),null,(1),null)),(new cljs.core.List(null,119,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"end-column","end-column",1425389514),null,(1),null)),(new cljs.core.List(null,37,null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"tag","tag",-1290361223),null,(1),null)),(new cljs.core.List(null,new cljs.core.Symbol(null,"java.io.Writer","java.io.Writer",-1891611896,null),null,(1),null))))))),null,(1),null)),(new cljs.core.List(null,cljs.core.sequence.call(null,cljs.core.seq.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,new cljs.core.Symbol("expound.ansi","escape","expound.ansi/escape",114469759,null),null,(1),null)),(new cljs.core.List(null,new cljs.core.Keyword(null,"none","none",1333468478),null,(1),null))))),null,(1),null))))),null,(1),null))))),null,(1),null)),(new cljs.core.List(null,body,null,(1),null)))));
});
expound.ansi.print_color_text.cljs$lang$macro = true;


//# sourceMappingURL=ansi.js.map
