// Compiled by ClojureScript 1.10.520 {}
goog.provide('camel_snake_kebab.core');
goog.require('cljs.core');
goog.require('clojure.string');
goog.require('camel_snake_kebab.internals.misc');
goog.require('camel_snake_kebab.internals.alter_name');
/**
 * Converts the case of a string according to the rule for the first
 *   word, remaining words, and the separator.
 */
camel_snake_kebab.core.convert_case = (function camel_snake_kebab$core$convert_case(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19394 = arguments.length;
var i__4731__auto___19395 = (0);
while(true){
if((i__4731__auto___19395 < len__4730__auto___19394)){
args__4736__auto__.push((arguments[i__4731__auto___19395]));

var G__19396 = (i__4731__auto___19395 + (1));
i__4731__auto___19395 = G__19396;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((4) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((4)),(0),null)):null);
return camel_snake_kebab.core.convert_case.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__4737__auto__);
});

camel_snake_kebab.core.convert_case.cljs$core$IFn$_invoke$arity$variadic = (function (first_fn,rest_fn,sep,s,rest){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,first_fn,rest_fn,sep,s,rest);
});

camel_snake_kebab.core.convert_case.cljs$lang$maxFixedArity = (4);

/** @this {Function} */
camel_snake_kebab.core.convert_case.cljs$lang$applyTo = (function (seq19389){
var G__19390 = cljs.core.first.call(null,seq19389);
var seq19389__$1 = cljs.core.next.call(null,seq19389);
var G__19391 = cljs.core.first.call(null,seq19389__$1);
var seq19389__$2 = cljs.core.next.call(null,seq19389__$1);
var G__19392 = cljs.core.first.call(null,seq19389__$2);
var seq19389__$3 = cljs.core.next.call(null,seq19389__$2);
var G__19393 = cljs.core.first.call(null,seq19389__$3);
var seq19389__$4 = cljs.core.next.call(null,seq19389__$3);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19390,G__19391,G__19392,G__19393,seq19389__$4);
});

camel_snake_kebab.core.__GT_PascalCase = (function camel_snake_kebab$core$__GT_PascalCase(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19405 = arguments.length;
var i__4731__auto___19406 = (0);
while(true){
if((i__4731__auto___19406 < len__4730__auto___19405)){
args__4736__auto__.push((arguments[i__4731__auto___19406]));

var G__19407 = (i__4731__auto___19406 + (1));
i__4731__auto___19406 = G__19407;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_PascalCase.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_PascalCase.cljs$core$IFn$_invoke$arity$variadic = (function (s__19352__auto__,rest__19353__auto__){
var convert_case__19354__auto__ = (function (p1__19351__19355__auto__){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"",p1__19351__19355__auto__,rest__19353__auto__);
});
return camel_snake_kebab.internals.alter_name.alter_name.call(null,s__19352__auto__,convert_case__19354__auto__);
});

camel_snake_kebab.core.__GT_PascalCase.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_PascalCase.cljs$lang$applyTo = (function (seq19397){
var G__19398 = cljs.core.first.call(null,seq19397);
var seq19397__$1 = cljs.core.next.call(null,seq19397);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19398,seq19397__$1);
});


camel_snake_kebab.core.__GT_PascalCaseString = (function camel_snake_kebab$core$__GT_PascalCaseString(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19408 = arguments.length;
var i__4731__auto___19409 = (0);
while(true){
if((i__4731__auto___19409 < len__4730__auto___19408)){
args__4736__auto__.push((arguments[i__4731__auto___19409]));

var G__19410 = (i__4731__auto___19409 + (1));
i__4731__auto___19409 = G__19410;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_PascalCaseString.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_PascalCaseString.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.identity.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_PascalCaseString.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_PascalCaseString.cljs$lang$applyTo = (function (seq19399){
var G__19400 = cljs.core.first.call(null,seq19399);
var seq19399__$1 = cljs.core.next.call(null,seq19399);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19400,seq19399__$1);
});


camel_snake_kebab.core.__GT_PascalCaseSymbol = (function camel_snake_kebab$core$__GT_PascalCaseSymbol(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19411 = arguments.length;
var i__4731__auto___19412 = (0);
while(true){
if((i__4731__auto___19412 < len__4730__auto___19411)){
args__4736__auto__.push((arguments[i__4731__auto___19412]));

var G__19413 = (i__4731__auto___19412 + (1));
i__4731__auto___19412 = G__19413;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_PascalCaseSymbol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_PascalCaseSymbol.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.symbol.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_PascalCaseSymbol.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_PascalCaseSymbol.cljs$lang$applyTo = (function (seq19401){
var G__19402 = cljs.core.first.call(null,seq19401);
var seq19401__$1 = cljs.core.next.call(null,seq19401);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19402,seq19401__$1);
});


camel_snake_kebab.core.__GT_PascalCaseKeyword = (function camel_snake_kebab$core$__GT_PascalCaseKeyword(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19414 = arguments.length;
var i__4731__auto___19415 = (0);
while(true){
if((i__4731__auto___19415 < len__4730__auto___19414)){
args__4736__auto__.push((arguments[i__4731__auto___19415]));

var G__19416 = (i__4731__auto___19415 + (1));
i__4731__auto___19415 = G__19416;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_PascalCaseKeyword.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_PascalCaseKeyword.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.keyword.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_PascalCaseKeyword.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_PascalCaseKeyword.cljs$lang$applyTo = (function (seq19403){
var G__19404 = cljs.core.first.call(null,seq19403);
var seq19403__$1 = cljs.core.next.call(null,seq19403);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19404,seq19403__$1);
});

camel_snake_kebab.core.__GT_Camel_Snake_Case = (function camel_snake_kebab$core$__GT_Camel_Snake_Case(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19425 = arguments.length;
var i__4731__auto___19426 = (0);
while(true){
if((i__4731__auto___19426 < len__4730__auto___19425)){
args__4736__auto__.push((arguments[i__4731__auto___19426]));

var G__19427 = (i__4731__auto___19426 + (1));
i__4731__auto___19426 = G__19427;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_Camel_Snake_Case.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_Camel_Snake_Case.cljs$core$IFn$_invoke$arity$variadic = (function (s__19352__auto__,rest__19353__auto__){
var convert_case__19354__auto__ = (function (p1__19351__19355__auto__){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"_",p1__19351__19355__auto__,rest__19353__auto__);
});
return camel_snake_kebab.internals.alter_name.alter_name.call(null,s__19352__auto__,convert_case__19354__auto__);
});

camel_snake_kebab.core.__GT_Camel_Snake_Case.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_Camel_Snake_Case.cljs$lang$applyTo = (function (seq19417){
var G__19418 = cljs.core.first.call(null,seq19417);
var seq19417__$1 = cljs.core.next.call(null,seq19417);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19418,seq19417__$1);
});


camel_snake_kebab.core.__GT_Camel_Snake_Case_String = (function camel_snake_kebab$core$__GT_Camel_Snake_Case_String(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19428 = arguments.length;
var i__4731__auto___19429 = (0);
while(true){
if((i__4731__auto___19429 < len__4730__auto___19428)){
args__4736__auto__.push((arguments[i__4731__auto___19429]));

var G__19430 = (i__4731__auto___19429 + (1));
i__4731__auto___19429 = G__19430;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_Camel_Snake_Case_String.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_Camel_Snake_Case_String.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.identity.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_Camel_Snake_Case_String.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_Camel_Snake_Case_String.cljs$lang$applyTo = (function (seq19419){
var G__19420 = cljs.core.first.call(null,seq19419);
var seq19419__$1 = cljs.core.next.call(null,seq19419);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19420,seq19419__$1);
});


camel_snake_kebab.core.__GT_Camel_Snake_Case_Symbol = (function camel_snake_kebab$core$__GT_Camel_Snake_Case_Symbol(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19431 = arguments.length;
var i__4731__auto___19432 = (0);
while(true){
if((i__4731__auto___19432 < len__4730__auto___19431)){
args__4736__auto__.push((arguments[i__4731__auto___19432]));

var G__19433 = (i__4731__auto___19432 + (1));
i__4731__auto___19432 = G__19433;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_Camel_Snake_Case_Symbol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_Camel_Snake_Case_Symbol.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.symbol.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_Camel_Snake_Case_Symbol.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_Camel_Snake_Case_Symbol.cljs$lang$applyTo = (function (seq19421){
var G__19422 = cljs.core.first.call(null,seq19421);
var seq19421__$1 = cljs.core.next.call(null,seq19421);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19422,seq19421__$1);
});


camel_snake_kebab.core.__GT_Camel_Snake_Case_Keyword = (function camel_snake_kebab$core$__GT_Camel_Snake_Case_Keyword(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19434 = arguments.length;
var i__4731__auto___19435 = (0);
while(true){
if((i__4731__auto___19435 < len__4730__auto___19434)){
args__4736__auto__.push((arguments[i__4731__auto___19435]));

var G__19436 = (i__4731__auto___19435 + (1));
i__4731__auto___19435 = G__19436;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_Camel_Snake_Case_Keyword.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_Camel_Snake_Case_Keyword.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.keyword.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.capitalize,clojure.string.capitalize,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_Camel_Snake_Case_Keyword.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_Camel_Snake_Case_Keyword.cljs$lang$applyTo = (function (seq19423){
var G__19424 = cljs.core.first.call(null,seq19423);
var seq19423__$1 = cljs.core.next.call(null,seq19423);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19424,seq19423__$1);
});

camel_snake_kebab.core.__GT_camelCase = (function camel_snake_kebab$core$__GT_camelCase(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19445 = arguments.length;
var i__4731__auto___19446 = (0);
while(true){
if((i__4731__auto___19446 < len__4730__auto___19445)){
args__4736__auto__.push((arguments[i__4731__auto___19446]));

var G__19447 = (i__4731__auto___19446 + (1));
i__4731__auto___19446 = G__19447;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_camelCase.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_camelCase.cljs$core$IFn$_invoke$arity$variadic = (function (s__19352__auto__,rest__19353__auto__){
var convert_case__19354__auto__ = (function (p1__19351__19355__auto__){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.capitalize,"",p1__19351__19355__auto__,rest__19353__auto__);
});
return camel_snake_kebab.internals.alter_name.alter_name.call(null,s__19352__auto__,convert_case__19354__auto__);
});

camel_snake_kebab.core.__GT_camelCase.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_camelCase.cljs$lang$applyTo = (function (seq19437){
var G__19438 = cljs.core.first.call(null,seq19437);
var seq19437__$1 = cljs.core.next.call(null,seq19437);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19438,seq19437__$1);
});


camel_snake_kebab.core.__GT_camelCaseString = (function camel_snake_kebab$core$__GT_camelCaseString(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19448 = arguments.length;
var i__4731__auto___19449 = (0);
while(true){
if((i__4731__auto___19449 < len__4730__auto___19448)){
args__4736__auto__.push((arguments[i__4731__auto___19449]));

var G__19450 = (i__4731__auto___19449 + (1));
i__4731__auto___19449 = G__19450;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_camelCaseString.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_camelCaseString.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.identity.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.capitalize,"",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_camelCaseString.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_camelCaseString.cljs$lang$applyTo = (function (seq19439){
var G__19440 = cljs.core.first.call(null,seq19439);
var seq19439__$1 = cljs.core.next.call(null,seq19439);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19440,seq19439__$1);
});


camel_snake_kebab.core.__GT_camelCaseSymbol = (function camel_snake_kebab$core$__GT_camelCaseSymbol(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19451 = arguments.length;
var i__4731__auto___19452 = (0);
while(true){
if((i__4731__auto___19452 < len__4730__auto___19451)){
args__4736__auto__.push((arguments[i__4731__auto___19452]));

var G__19453 = (i__4731__auto___19452 + (1));
i__4731__auto___19452 = G__19453;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_camelCaseSymbol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_camelCaseSymbol.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.symbol.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.capitalize,"",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_camelCaseSymbol.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_camelCaseSymbol.cljs$lang$applyTo = (function (seq19441){
var G__19442 = cljs.core.first.call(null,seq19441);
var seq19441__$1 = cljs.core.next.call(null,seq19441);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19442,seq19441__$1);
});


camel_snake_kebab.core.__GT_camelCaseKeyword = (function camel_snake_kebab$core$__GT_camelCaseKeyword(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19454 = arguments.length;
var i__4731__auto___19455 = (0);
while(true){
if((i__4731__auto___19455 < len__4730__auto___19454)){
args__4736__auto__.push((arguments[i__4731__auto___19455]));

var G__19456 = (i__4731__auto___19455 + (1));
i__4731__auto___19455 = G__19456;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_camelCaseKeyword.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_camelCaseKeyword.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.keyword.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.capitalize,"",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_camelCaseKeyword.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_camelCaseKeyword.cljs$lang$applyTo = (function (seq19443){
var G__19444 = cljs.core.first.call(null,seq19443);
var seq19443__$1 = cljs.core.next.call(null,seq19443);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19444,seq19443__$1);
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE = (function camel_snake_kebab$core$__GT_SCREAMING_SNAKE_CASE(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19465 = arguments.length;
var i__4731__auto___19466 = (0);
while(true){
if((i__4731__auto___19466 < len__4730__auto___19465)){
args__4736__auto__.push((arguments[i__4731__auto___19466]));

var G__19467 = (i__4731__auto___19466 + (1));
i__4731__auto___19466 = G__19467;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE.cljs$core$IFn$_invoke$arity$variadic = (function (s__19352__auto__,rest__19353__auto__){
var convert_case__19354__auto__ = (function (p1__19351__19355__auto__){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.upper_case,clojure.string.upper_case,"_",p1__19351__19355__auto__,rest__19353__auto__);
});
return camel_snake_kebab.internals.alter_name.alter_name.call(null,s__19352__auto__,convert_case__19354__auto__);
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE.cljs$lang$applyTo = (function (seq19457){
var G__19458 = cljs.core.first.call(null,seq19457);
var seq19457__$1 = cljs.core.next.call(null,seq19457);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19458,seq19457__$1);
});


camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_STRING = (function camel_snake_kebab$core$__GT_SCREAMING_SNAKE_CASE_STRING(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19468 = arguments.length;
var i__4731__auto___19469 = (0);
while(true){
if((i__4731__auto___19469 < len__4730__auto___19468)){
args__4736__auto__.push((arguments[i__4731__auto___19469]));

var G__19470 = (i__4731__auto___19469 + (1));
i__4731__auto___19469 = G__19470;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_STRING.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_STRING.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.identity.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.upper_case,clojure.string.upper_case,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_STRING.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_STRING.cljs$lang$applyTo = (function (seq19459){
var G__19460 = cljs.core.first.call(null,seq19459);
var seq19459__$1 = cljs.core.next.call(null,seq19459);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19460,seq19459__$1);
});


camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_SYMBOL = (function camel_snake_kebab$core$__GT_SCREAMING_SNAKE_CASE_SYMBOL(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19471 = arguments.length;
var i__4731__auto___19472 = (0);
while(true){
if((i__4731__auto___19472 < len__4730__auto___19471)){
args__4736__auto__.push((arguments[i__4731__auto___19472]));

var G__19473 = (i__4731__auto___19472 + (1));
i__4731__auto___19472 = G__19473;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_SYMBOL.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_SYMBOL.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.symbol.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.upper_case,clojure.string.upper_case,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_SYMBOL.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_SYMBOL.cljs$lang$applyTo = (function (seq19461){
var G__19462 = cljs.core.first.call(null,seq19461);
var seq19461__$1 = cljs.core.next.call(null,seq19461);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19462,seq19461__$1);
});


camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_KEYWORD = (function camel_snake_kebab$core$__GT_SCREAMING_SNAKE_CASE_KEYWORD(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19474 = arguments.length;
var i__4731__auto___19475 = (0);
while(true){
if((i__4731__auto___19475 < len__4730__auto___19474)){
args__4736__auto__.push((arguments[i__4731__auto___19475]));

var G__19476 = (i__4731__auto___19475 + (1));
i__4731__auto___19475 = G__19476;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_KEYWORD.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_KEYWORD.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.keyword.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.upper_case,clojure.string.upper_case,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_KEYWORD.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_SCREAMING_SNAKE_CASE_KEYWORD.cljs$lang$applyTo = (function (seq19463){
var G__19464 = cljs.core.first.call(null,seq19463);
var seq19463__$1 = cljs.core.next.call(null,seq19463);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19464,seq19463__$1);
});

camel_snake_kebab.core.__GT_snake_case = (function camel_snake_kebab$core$__GT_snake_case(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19485 = arguments.length;
var i__4731__auto___19486 = (0);
while(true){
if((i__4731__auto___19486 < len__4730__auto___19485)){
args__4736__auto__.push((arguments[i__4731__auto___19486]));

var G__19487 = (i__4731__auto___19486 + (1));
i__4731__auto___19486 = G__19487;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_snake_case.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_snake_case.cljs$core$IFn$_invoke$arity$variadic = (function (s__19352__auto__,rest__19353__auto__){
var convert_case__19354__auto__ = (function (p1__19351__19355__auto__){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"_",p1__19351__19355__auto__,rest__19353__auto__);
});
return camel_snake_kebab.internals.alter_name.alter_name.call(null,s__19352__auto__,convert_case__19354__auto__);
});

camel_snake_kebab.core.__GT_snake_case.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_snake_case.cljs$lang$applyTo = (function (seq19477){
var G__19478 = cljs.core.first.call(null,seq19477);
var seq19477__$1 = cljs.core.next.call(null,seq19477);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19478,seq19477__$1);
});


camel_snake_kebab.core.__GT_snake_case_string = (function camel_snake_kebab$core$__GT_snake_case_string(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19488 = arguments.length;
var i__4731__auto___19489 = (0);
while(true){
if((i__4731__auto___19489 < len__4730__auto___19488)){
args__4736__auto__.push((arguments[i__4731__auto___19489]));

var G__19490 = (i__4731__auto___19489 + (1));
i__4731__auto___19489 = G__19490;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_snake_case_string.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_snake_case_string.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.identity.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_snake_case_string.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_snake_case_string.cljs$lang$applyTo = (function (seq19479){
var G__19480 = cljs.core.first.call(null,seq19479);
var seq19479__$1 = cljs.core.next.call(null,seq19479);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19480,seq19479__$1);
});


camel_snake_kebab.core.__GT_snake_case_symbol = (function camel_snake_kebab$core$__GT_snake_case_symbol(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19491 = arguments.length;
var i__4731__auto___19492 = (0);
while(true){
if((i__4731__auto___19492 < len__4730__auto___19491)){
args__4736__auto__.push((arguments[i__4731__auto___19492]));

var G__19493 = (i__4731__auto___19492 + (1));
i__4731__auto___19492 = G__19493;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_snake_case_symbol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_snake_case_symbol.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.symbol.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_snake_case_symbol.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_snake_case_symbol.cljs$lang$applyTo = (function (seq19481){
var G__19482 = cljs.core.first.call(null,seq19481);
var seq19481__$1 = cljs.core.next.call(null,seq19481);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19482,seq19481__$1);
});


camel_snake_kebab.core.__GT_snake_case_keyword = (function camel_snake_kebab$core$__GT_snake_case_keyword(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19494 = arguments.length;
var i__4731__auto___19495 = (0);
while(true){
if((i__4731__auto___19495 < len__4730__auto___19494)){
args__4736__auto__.push((arguments[i__4731__auto___19495]));

var G__19496 = (i__4731__auto___19495 + (1));
i__4731__auto___19495 = G__19496;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_snake_case_keyword.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_snake_case_keyword.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.keyword.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"_",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_snake_case_keyword.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_snake_case_keyword.cljs$lang$applyTo = (function (seq19483){
var G__19484 = cljs.core.first.call(null,seq19483);
var seq19483__$1 = cljs.core.next.call(null,seq19483);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19484,seq19483__$1);
});

camel_snake_kebab.core.__GT_kebab_case = (function camel_snake_kebab$core$__GT_kebab_case(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19505 = arguments.length;
var i__4731__auto___19506 = (0);
while(true){
if((i__4731__auto___19506 < len__4730__auto___19505)){
args__4736__auto__.push((arguments[i__4731__auto___19506]));

var G__19507 = (i__4731__auto___19506 + (1));
i__4731__auto___19506 = G__19507;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_kebab_case.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_kebab_case.cljs$core$IFn$_invoke$arity$variadic = (function (s__19352__auto__,rest__19353__auto__){
var convert_case__19354__auto__ = (function (p1__19351__19355__auto__){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"-",p1__19351__19355__auto__,rest__19353__auto__);
});
return camel_snake_kebab.internals.alter_name.alter_name.call(null,s__19352__auto__,convert_case__19354__auto__);
});

camel_snake_kebab.core.__GT_kebab_case.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_kebab_case.cljs$lang$applyTo = (function (seq19497){
var G__19498 = cljs.core.first.call(null,seq19497);
var seq19497__$1 = cljs.core.next.call(null,seq19497);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19498,seq19497__$1);
});


camel_snake_kebab.core.__GT_kebab_case_string = (function camel_snake_kebab$core$__GT_kebab_case_string(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19508 = arguments.length;
var i__4731__auto___19509 = (0);
while(true){
if((i__4731__auto___19509 < len__4730__auto___19508)){
args__4736__auto__.push((arguments[i__4731__auto___19509]));

var G__19510 = (i__4731__auto___19509 + (1));
i__4731__auto___19509 = G__19510;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_kebab_case_string.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_kebab_case_string.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.identity.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"-",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_kebab_case_string.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_kebab_case_string.cljs$lang$applyTo = (function (seq19499){
var G__19500 = cljs.core.first.call(null,seq19499);
var seq19499__$1 = cljs.core.next.call(null,seq19499);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19500,seq19499__$1);
});


camel_snake_kebab.core.__GT_kebab_case_symbol = (function camel_snake_kebab$core$__GT_kebab_case_symbol(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19511 = arguments.length;
var i__4731__auto___19512 = (0);
while(true){
if((i__4731__auto___19512 < len__4730__auto___19511)){
args__4736__auto__.push((arguments[i__4731__auto___19512]));

var G__19513 = (i__4731__auto___19512 + (1));
i__4731__auto___19512 = G__19513;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_kebab_case_symbol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_kebab_case_symbol.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.symbol.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"-",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_kebab_case_symbol.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_kebab_case_symbol.cljs$lang$applyTo = (function (seq19501){
var G__19502 = cljs.core.first.call(null,seq19501);
var seq19501__$1 = cljs.core.next.call(null,seq19501);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19502,seq19501__$1);
});


camel_snake_kebab.core.__GT_kebab_case_keyword = (function camel_snake_kebab$core$__GT_kebab_case_keyword(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19514 = arguments.length;
var i__4731__auto___19515 = (0);
while(true){
if((i__4731__auto___19515 < len__4730__auto___19514)){
args__4736__auto__.push((arguments[i__4731__auto___19515]));

var G__19516 = (i__4731__auto___19515 + (1));
i__4731__auto___19515 = G__19516;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_kebab_case_keyword.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_kebab_case_keyword.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.keyword.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,clojure.string.lower_case,clojure.string.lower_case,"-",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_kebab_case_keyword.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_kebab_case_keyword.cljs$lang$applyTo = (function (seq19503){
var G__19504 = cljs.core.first.call(null,seq19503);
var seq19503__$1 = cljs.core.next.call(null,seq19503);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19504,seq19503__$1);
});

camel_snake_kebab.core.__GT_HTTP_Header_Case = (function camel_snake_kebab$core$__GT_HTTP_Header_Case(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19525 = arguments.length;
var i__4731__auto___19526 = (0);
while(true){
if((i__4731__auto___19526 < len__4730__auto___19525)){
args__4736__auto__.push((arguments[i__4731__auto___19526]));

var G__19527 = (i__4731__auto___19526 + (1));
i__4731__auto___19526 = G__19527;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_HTTP_Header_Case.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_HTTP_Header_Case.cljs$core$IFn$_invoke$arity$variadic = (function (s__19352__auto__,rest__19353__auto__){
var convert_case__19354__auto__ = (function (p1__19351__19355__auto__){
return cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,camel_snake_kebab.internals.misc.capitalize_http_header,camel_snake_kebab.internals.misc.capitalize_http_header,"-",p1__19351__19355__auto__,rest__19353__auto__);
});
return camel_snake_kebab.internals.alter_name.alter_name.call(null,s__19352__auto__,convert_case__19354__auto__);
});

camel_snake_kebab.core.__GT_HTTP_Header_Case.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_HTTP_Header_Case.cljs$lang$applyTo = (function (seq19517){
var G__19518 = cljs.core.first.call(null,seq19517);
var seq19517__$1 = cljs.core.next.call(null,seq19517);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19518,seq19517__$1);
});


camel_snake_kebab.core.__GT_HTTP_Header_Case_String = (function camel_snake_kebab$core$__GT_HTTP_Header_Case_String(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19528 = arguments.length;
var i__4731__auto___19529 = (0);
while(true){
if((i__4731__auto___19529 < len__4730__auto___19528)){
args__4736__auto__.push((arguments[i__4731__auto___19529]));

var G__19530 = (i__4731__auto___19529 + (1));
i__4731__auto___19529 = G__19530;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_HTTP_Header_Case_String.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_HTTP_Header_Case_String.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.identity.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,camel_snake_kebab.internals.misc.capitalize_http_header,camel_snake_kebab.internals.misc.capitalize_http_header,"-",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_HTTP_Header_Case_String.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_HTTP_Header_Case_String.cljs$lang$applyTo = (function (seq19519){
var G__19520 = cljs.core.first.call(null,seq19519);
var seq19519__$1 = cljs.core.next.call(null,seq19519);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19520,seq19519__$1);
});


camel_snake_kebab.core.__GT_HTTP_Header_Case_Symbol = (function camel_snake_kebab$core$__GT_HTTP_Header_Case_Symbol(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19531 = arguments.length;
var i__4731__auto___19532 = (0);
while(true){
if((i__4731__auto___19532 < len__4730__auto___19531)){
args__4736__auto__.push((arguments[i__4731__auto___19532]));

var G__19533 = (i__4731__auto___19532 + (1));
i__4731__auto___19532 = G__19533;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_HTTP_Header_Case_Symbol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_HTTP_Header_Case_Symbol.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.symbol.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,camel_snake_kebab.internals.misc.capitalize_http_header,camel_snake_kebab.internals.misc.capitalize_http_header,"-",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_HTTP_Header_Case_Symbol.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_HTTP_Header_Case_Symbol.cljs$lang$applyTo = (function (seq19521){
var G__19522 = cljs.core.first.call(null,seq19521);
var seq19521__$1 = cljs.core.next.call(null,seq19521);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19522,seq19521__$1);
});


camel_snake_kebab.core.__GT_HTTP_Header_Case_Keyword = (function camel_snake_kebab$core$__GT_HTTP_Header_Case_Keyword(var_args){
var args__4736__auto__ = [];
var len__4730__auto___19534 = arguments.length;
var i__4731__auto___19535 = (0);
while(true){
if((i__4731__auto___19535 < len__4730__auto___19534)){
args__4736__auto__.push((arguments[i__4731__auto___19535]));

var G__19536 = (i__4731__auto___19535 + (1));
i__4731__auto___19535 = G__19536;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return camel_snake_kebab.core.__GT_HTTP_Header_Case_Keyword.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

camel_snake_kebab.core.__GT_HTTP_Header_Case_Keyword.cljs$core$IFn$_invoke$arity$variadic = (function (s__19357__auto__,rest__19358__auto__){
if((!((s__19357__auto__ == null)))){
} else {
throw (new Error("Assert failed: (clojure.core/not (clojure.core/nil? s__19357__auto__))"));
}

return cljs.core.keyword.call(null,cljs.core.apply.call(null,camel_snake_kebab.internals.misc.convert_case,camel_snake_kebab.internals.misc.capitalize_http_header,camel_snake_kebab.internals.misc.capitalize_http_header,"-",cljs.core.name.call(null,s__19357__auto__),rest__19358__auto__));
});

camel_snake_kebab.core.__GT_HTTP_Header_Case_Keyword.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
camel_snake_kebab.core.__GT_HTTP_Header_Case_Keyword.cljs$lang$applyTo = (function (seq19523){
var G__19524 = cljs.core.first.call(null,seq19523);
var seq19523__$1 = cljs.core.next.call(null,seq19523);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__19524,seq19523__$1);
});


//# sourceMappingURL=core.js.map
