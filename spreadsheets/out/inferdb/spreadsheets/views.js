// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.spreadsheets.views');
goog.require('cljs.core');
goog.require('oz.core');
goog.require('reagent.core');
goog.require('re_frame.core');
goog.require('inferdb.spreadsheets.events');
goog.require('inferdb.spreadsheets.handsontable');
inferdb.spreadsheets.views.default_hot_settings = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"settings","settings",1556144875),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"stretchH","stretchH",-331926457),new cljs.core.Keyword(null,"filters","filters",974726919),new cljs.core.Keyword(null,"licenseKey","licenseKey",-2055348343),new cljs.core.Keyword(null,"width","width",-384071477),new cljs.core.Keyword(null,"columnSorting","columnSorting",-1295196052),new cljs.core.Keyword(null,"readOnly","readOnly",-1749118317),new cljs.core.Keyword(null,"bindRowsWithHeaders","bindRowsWithHeaders",1627500085),new cljs.core.Keyword(null,"selectionMode","selectionMode",1264773367),new cljs.core.Keyword(null,"colHeaders","colHeaders",-361008132),new cljs.core.Keyword(null,"manualColumnMove","manualColumnMove",-418146915),new cljs.core.Keyword(null,"rowHeaders","rowHeaders",1560051454),new cljs.core.Keyword(null,"height","height",1025178622),new cljs.core.Keyword(null,"data","data",-232669377)],["all",true,"non-commercial-and-evaluation","100vw",true,false,true,new cljs.core.Keyword(null,"multiple","multiple",1244445549),cljs.core.PersistentVector.EMPTY,true,true,"30vh",cljs.core.PersistentVector.EMPTY]),new cljs.core.Keyword(null,"hooks","hooks",-413590103),inferdb.spreadsheets.events.hooks], null);
inferdb.spreadsheets.views.default_search_string = cljs.core.pr_str.call(null,new cljs.core.PersistentArrayMap(null, 1, ["percent_black",0.4], null));
inferdb.spreadsheets.views.search_form = (function inferdb$spreadsheets$views$search_form(name){
var input_text = reagent.core.atom.call(null,inferdb.spreadsheets.views.default_search_string);
return ((function (input_text){
return (function (){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"display","display",242065432),"flex"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),"search",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),"100%"], null),new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (input_text){
return (function (p1__10720_SHARP_){
return cljs.core.reset_BANG_.call(null,input_text,p1__10720_SHARP_.target.value);
});})(input_text))
,new cljs.core.Keyword(null,"value","value",305978217),cljs.core.deref.call(null,input_text)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"button","button",1456579943),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (input_text){
return (function (){
return re_frame.core.dispatch.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"search","search",1564939822),cljs.core.deref.call(null,input_text)], null));
});})(input_text))
,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"float","float",-1732389368),"right"], null)], null),"Search"], null)], null);
});
;})(input_text))
});
inferdb.spreadsheets.views.app = (function inferdb$spreadsheets$views$app(){
var hot_props = cljs.core.deref.call(null,re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hot-props","hot-props",-1702307913)], null)));
var selected_maps = cljs.core.deref.call(null,re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"selections","selections",-1277610233)], null)));
var vega_lite_spec = cljs.core.deref.call(null,re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"vega-lite-spec","vega-lite-spec",-1661799754)], null)));
var scores = cljs.core.deref.call(null,re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"scores","scores",-1267421800)], null)));
var db = cljs.core.deref.call(null,re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"whole-db","whole-db",-1329432213)], null)));
var selected_row = cljs.core.deref.call(null,re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"selected-row","selected-row",-750259683)], null)));
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [inferdb.spreadsheets.handsontable.handsontable,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"overflow","overflow",2058931880),"hidden"], null)], null),hot_props], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [inferdb.spreadsheets.views.search_form,"Zane"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"display","display",242065432),"flex",new cljs.core.Keyword(null,"justify-content","justify-content",-1990475787),"center"], null)], null),(cljs.core.truth_(vega_lite_spec)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [oz.core.vega_lite,vega_lite_spec], null):null)], null)], null);
});

//# sourceMappingURL=views.js.map
