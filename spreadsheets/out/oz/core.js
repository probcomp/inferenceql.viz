// Compiled by ClojureScript 1.10.520 {}
goog.provide('oz.core');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('clojure.string');
goog.require('cljs.core.async');
goog.require('taoensso.encore');
goog.require('taoensso.timbre');
goog.require('taoensso.sente');
goog.require('taoensso.sente.packers.transit');
goog.require('cljsjs.vega');
goog.require('cljsjs.vega_lite');
goog.require('cljsjs.vega_embed');
goog.require('cljsjs.vega_tooltip');
taoensso.timbre.set_level_BANG_.call(null,new cljs.core.Keyword(null,"info","info",-317069002));
cljs.core.enable_console_print_BANG_.call(null);
oz.core.log = (function oz$core$log(a_thing){
return console.log(a_thing);
});
oz.core.render_vega_lite = (function oz$core$render_vega_lite(spec,elem){
if(cljs.core.truth_(spec)){
var spec__$1 = cljs.core.clj__GT_js.call(null,spec);
var opts = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"renderer","renderer",336841071),"canvas",new cljs.core.Keyword(null,"mode","mode",654403691),"vega-lite"], null);
var vega_spec = vl.compile(spec__$1);
oz.core.log.call(null,"Vega-lite translates to:");

oz.core.log.call(null,vega_spec);

return vegaEmbed(elem,spec__$1,cljs.core.clj__GT_js.call(null,opts)).then(((function (spec__$1,opts,vega_spec){
return (function (res){
return vegaTooltip.vegaLite(res.view,spec__$1);
});})(spec__$1,opts,vega_spec))
).catch(((function (spec__$1,opts,vega_spec){
return (function (err){
return oz.core.log.call(null,err);
});})(spec__$1,opts,vega_spec))
);
} else {
return null;
}
});
oz.core.render_vega = (function oz$core$render_vega(spec,elem){
if(cljs.core.truth_(spec)){
var spec__$1 = cljs.core.clj__GT_js.call(null,spec);
var opts = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"renderer","renderer",336841071),"canvas",new cljs.core.Keyword(null,"mode","mode",654403691),"vega"], null);
return vegaEmbed(elem,spec__$1,cljs.core.clj__GT_js.call(null,opts)).then(((function (spec__$1,opts){
return (function (res){
return vegaTooltip.vega(res.view,spec__$1);
});})(spec__$1,opts))
).catch(((function (spec__$1,opts){
return (function (err){
return oz.core.log.call(null,err);
});})(spec__$1,opts))
);
} else {
return null;
}
});
/**
 * Reagent component that renders vega-lite.
 */
oz.core.vega_lite = (function oz$core$vega_lite(spec){
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"display-name","display-name",694513143),"vega-lite",new cljs.core.Keyword(null,"component-did-mount","component-did-mount",-1126910518),(function (this$){
return oz.core.render_vega_lite.call(null,spec,reagent.core.dom_node.call(null,this$));
}),new cljs.core.Keyword(null,"component-will-update","component-will-update",335247566),(function (this$,p__19176){
var vec__19177 = p__19176;
var _ = cljs.core.nth.call(null,vec__19177,(0),null);
var new_spec = cljs.core.nth.call(null,vec__19177,(1),null);
return oz.core.render_vega_lite.call(null,new_spec,reagent.core.dom_node.call(null,this$));
}),new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),(function (spec__$1){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div#vis","div#vis",730109291)], null);
})], null));
});
/**
 * Reagent component that renders vega
 */
oz.core.vega = (function oz$core$vega(spec){
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"display-name","display-name",694513143),"vega",new cljs.core.Keyword(null,"component-did-mount","component-did-mount",-1126910518),(function (this$){
return oz.core.render_vega.call(null,spec,reagent.core.dom_node.call(null,this$));
}),new cljs.core.Keyword(null,"component-will-update","component-will-update",335247566),(function (this$,p__19180){
var vec__19181 = p__19180;
var _ = cljs.core.nth.call(null,vec__19181,(0),null);
var new_spec = cljs.core.nth.call(null,vec__19181,(1),null);
return oz.core.render_vega.call(null,new_spec,reagent.core.dom_node.call(null,this$));
}),new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),(function (spec__$1){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div#vis","div#vis",730109291)], null);
})], null));
});
oz.core.view_spec = (function oz$core$view_spec(spec){
return clojure.walk.prewalk.call(null,(function (x){
if(cljs.core.truth_((function (){var and__4120__auto__ = cljs.core.coll_QMARK_.call(null,x);
if(and__4120__auto__){
return new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"vega","vega",-1657843081),null,new cljs.core.Keyword(null,"vega-lite","vega-lite",-650304770),null], null), null).call(null,cljs.core.first.call(null,x));
} else {
return and__4120__auto__;
}
})())){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (){var G__19184 = cljs.core.first.call(null,x);
var G__19184__$1 = (((G__19184 instanceof cljs.core.Keyword))?G__19184.fqn:null);
switch (G__19184__$1) {
case "vega":
return oz.core.vega;

break;
case "vega-lite":
return oz.core.vega_lite;

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__19184__$1)].join('')));

}
})(),cljs.core.reduce.call(null,cljs.core.merge,cljs.core.rest.call(null,x))], null);
} else {
return x;
}
}),spec);
});

//# sourceMappingURL=core.js.map
