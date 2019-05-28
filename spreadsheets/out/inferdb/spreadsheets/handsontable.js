// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.spreadsheets.handsontable');
goog.require('cljs.core');
goog.require('yarn.handsontable');
goog.require('camel_snake_kebab.core');
goog.require('cljsjs.react');
goog.require('re_frame.core');
goog.require('reagent.core');
goog.require('reagent.dom');
inferdb.spreadsheets.handsontable.random_id = (function inferdb$spreadsheets$handsontable$random_id(){
return Math.random().toString((36)).substring((5));
});
inferdb.spreadsheets.handsontable.update_hot_BANG_ = (function inferdb$spreadsheets$handsontable$update_hot_BANG_(hot_instance,new_settings){
return hot_instance.updateSettings(new_settings,false);
});
inferdb.spreadsheets.handsontable.handsontable = (function inferdb$spreadsheets$handsontable$handsontable(var_args){
var G__1775 = arguments.length;
switch (G__1775) {
case 1:
return inferdb.spreadsheets.handsontable.handsontable.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return inferdb.spreadsheets.handsontable.handsontable.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

inferdb.spreadsheets.handsontable.handsontable.cljs$core$IFn$_invoke$arity$1 = (function (props){
return inferdb.spreadsheets.handsontable.handsontable.call(null,cljs.core.PersistentArrayMap.EMPTY,props);
});

inferdb.spreadsheets.handsontable.handsontable.cljs$core$IFn$_invoke$arity$2 = (function (attributes,p__1776){
var map__1777 = p__1776;
var map__1777__$1 = (((((!((map__1777 == null))))?(((((map__1777.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__1777.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__1777):map__1777);
var props = map__1777__$1;
var settings = cljs.core.get.call(null,map__1777__$1,new cljs.core.Keyword(null,"settings","settings",1556144875));
var hooks = cljs.core.get.call(null,map__1777__$1,new cljs.core.Keyword(null,"hooks","hooks",-413590103));
var js_settings = cljs.core.clj__GT_js.call(null,settings);
var hot_instance = reagent.core.atom.call(null,null);
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"display-name","display-name",694513143),"handsontable-reagent",new cljs.core.Keyword(null,"component-did-mount","component-did-mount",-1126910518),((function (js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks){
return (function (this$){
var dom_node = reagent.dom.dom_node.call(null,this$);
var hot = (new Handsontable(dom_node,cljs.core.clj__GT_js.call(null,new cljs.core.Keyword(null,"settings","settings",1556144875).cljs$core$IFn$_invoke$arity$1(props))));
var seq__1779_1794 = cljs.core.seq.call(null,hooks);
var chunk__1780_1795 = null;
var count__1781_1796 = (0);
var i__1782_1797 = (0);
while(true){
if((i__1782_1797 < count__1781_1796)){
var key_1798 = cljs.core._nth.call(null,chunk__1780_1795,i__1782_1797);
var camel_key_1799 = camel_snake_kebab.core.__GT_camelCase.call(null,cljs.core.clj__GT_js.call(null,key_1798));
Handsontable.hooks.add(camel_key_1799,((function (seq__1779_1794,chunk__1780_1795,count__1781_1796,i__1782_1797,camel_key_1799,key_1798,dom_node,hot,js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks){
return (function() { 
var G__1800__delegate = function (args){
return re_frame.core.dispatch.call(null,cljs.core.into.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [key_1798,hot], null),args));
};
var G__1800 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__1801__i = 0, G__1801__a = new Array(arguments.length -  0);
while (G__1801__i < G__1801__a.length) {G__1801__a[G__1801__i] = arguments[G__1801__i + 0]; ++G__1801__i;}
  args = new cljs.core.IndexedSeq(G__1801__a,0,null);
} 
return G__1800__delegate.call(this,args);};
G__1800.cljs$lang$maxFixedArity = 0;
G__1800.cljs$lang$applyTo = (function (arglist__1802){
var args = cljs.core.seq(arglist__1802);
return G__1800__delegate(args);
});
G__1800.cljs$core$IFn$_invoke$arity$variadic = G__1800__delegate;
return G__1800;
})()
;})(seq__1779_1794,chunk__1780_1795,count__1781_1796,i__1782_1797,camel_key_1799,key_1798,dom_node,hot,js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks))
,hot);


var G__1803 = seq__1779_1794;
var G__1804 = chunk__1780_1795;
var G__1805 = count__1781_1796;
var G__1806 = (i__1782_1797 + (1));
seq__1779_1794 = G__1803;
chunk__1780_1795 = G__1804;
count__1781_1796 = G__1805;
i__1782_1797 = G__1806;
continue;
} else {
var temp__5720__auto___1807 = cljs.core.seq.call(null,seq__1779_1794);
if(temp__5720__auto___1807){
var seq__1779_1808__$1 = temp__5720__auto___1807;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__1779_1808__$1)){
var c__4550__auto___1809 = cljs.core.chunk_first.call(null,seq__1779_1808__$1);
var G__1810 = cljs.core.chunk_rest.call(null,seq__1779_1808__$1);
var G__1811 = c__4550__auto___1809;
var G__1812 = cljs.core.count.call(null,c__4550__auto___1809);
var G__1813 = (0);
seq__1779_1794 = G__1810;
chunk__1780_1795 = G__1811;
count__1781_1796 = G__1812;
i__1782_1797 = G__1813;
continue;
} else {
var key_1814 = cljs.core.first.call(null,seq__1779_1808__$1);
var camel_key_1815 = camel_snake_kebab.core.__GT_camelCase.call(null,cljs.core.clj__GT_js.call(null,key_1814));
Handsontable.hooks.add(camel_key_1815,((function (seq__1779_1794,chunk__1780_1795,count__1781_1796,i__1782_1797,camel_key_1815,key_1814,seq__1779_1808__$1,temp__5720__auto___1807,dom_node,hot,js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks){
return (function() { 
var G__1816__delegate = function (args){
return re_frame.core.dispatch.call(null,cljs.core.into.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [key_1814,hot], null),args));
};
var G__1816 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__1817__i = 0, G__1817__a = new Array(arguments.length -  0);
while (G__1817__i < G__1817__a.length) {G__1817__a[G__1817__i] = arguments[G__1817__i + 0]; ++G__1817__i;}
  args = new cljs.core.IndexedSeq(G__1817__a,0,null);
} 
return G__1816__delegate.call(this,args);};
G__1816.cljs$lang$maxFixedArity = 0;
G__1816.cljs$lang$applyTo = (function (arglist__1818){
var args = cljs.core.seq(arglist__1818);
return G__1816__delegate(args);
});
G__1816.cljs$core$IFn$_invoke$arity$variadic = G__1816__delegate;
return G__1816;
})()
;})(seq__1779_1794,chunk__1780_1795,count__1781_1796,i__1782_1797,camel_key_1815,key_1814,seq__1779_1808__$1,temp__5720__auto___1807,dom_node,hot,js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks))
,hot);


var G__1819 = cljs.core.next.call(null,seq__1779_1808__$1);
var G__1820 = null;
var G__1821 = (0);
var G__1822 = (0);
seq__1779_1794 = G__1819;
chunk__1780_1795 = G__1820;
count__1781_1796 = G__1821;
i__1782_1797 = G__1822;
continue;
}
} else {
}
}
break;
}

return cljs.core.reset_BANG_.call(null,hot_instance,hot);
});})(js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks))
,new cljs.core.Keyword(null,"should-component-update","should-component-update",2040868163),((function (js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks){
return (function (this$,p__1783,p__1784){
var vec__1785 = p__1783;
var _ = cljs.core.nth.call(null,vec__1785,(0),null);
var ___$1 = cljs.core.nth.call(null,vec__1785,(1),null);
var old_props = cljs.core.nth.call(null,vec__1785,(2),null);
var vec__1788 = p__1784;
var ___$2 = cljs.core.nth.call(null,vec__1788,(0),null);
var ___$3 = cljs.core.nth.call(null,vec__1788,(1),null);
var map__1791 = cljs.core.nth.call(null,vec__1788,(2),null);
var map__1791__$1 = (((((!((map__1791 == null))))?(((((map__1791.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__1791.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__1791):map__1791);
var new_props = map__1791__$1;
var new_settings = cljs.core.get.call(null,map__1791__$1,new cljs.core.Keyword(null,"settings","settings",1556144875));
inferdb.spreadsheets.handsontable.update_hot_BANG_.call(null,cljs.core.deref.call(null,hot_instance),cljs.core.clj__GT_js.call(null,new_settings));

return false;
});})(js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks))
,new cljs.core.Keyword(null,"component-did-update","component-did-update",-1468549173),((function (js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks){
return (function (this$,old_argv){
var new_argv = cljs.core.rest.call(null,reagent.core.argv.call(null,this$));
return null;
});})(js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks))
,new cljs.core.Keyword(null,"component-will-unmount","component-will-unmount",-2058314698),((function (js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks){
return (function (this$){
return cljs.core.deref.call(null,hot_instance).destroy();
});})(js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks))
,new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),((function (js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks){
return (function (this$){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),attributes], null);
});})(js_settings,hot_instance,map__1777,map__1777__$1,props,settings,hooks))
], null));
});

inferdb.spreadsheets.handsontable.handsontable.cljs$lang$maxFixedArity = 2;


//# sourceMappingURL=handsontable.js.map
