// Compiled by ClojureScript 1.10.520 {}
goog.provide('inferdb.spreadsheets.subs');
goog.require('cljs.core');
goog.require('clojure.walk');
goog.require('re_frame.core');
goog.require('inferdb.spreadsheets.db');
goog.require('inferdb.spreadsheets.views');
goog.require('inferdb.cgpm.main');
goog.require('inferdb.spreadsheets.model');
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"scores","scores",-1267421800),(function (db,_){
return inferdb.spreadsheets.db.scores.call(null,db);
}));
inferdb.spreadsheets.subs.table_headers = (function inferdb$spreadsheets$subs$table_headers(db,_){
return inferdb.spreadsheets.db.table_headers.call(null,db);
});
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"table-headers","table-headers",-1687849963),inferdb.spreadsheets.subs.table_headers);
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"selected-row-index","selected-row-index",1041627456),(function (db,_){
return inferdb.spreadsheets.db.selected_row_index.call(null,db);
}));
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"selected-row","selected-row",-750259683),(function (_,___$1){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"computed-rows","computed-rows",-1553800417),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"computed-rows","computed-rows",-1553800417)], null)),new cljs.core.Keyword(null,"selected-row-index","selected-row-index",1041627456),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"selected-row-index","selected-row-index",1041627456)], null))], null);
}),(function (p__10723,_){
var map__10724 = p__10723;
var map__10724__$1 = (((((!((map__10724 == null))))?(((((map__10724.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__10724.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__10724):map__10724);
var selected_row_index = cljs.core.get.call(null,map__10724__$1,new cljs.core.Keyword(null,"selected-row-index","selected-row-index",1041627456));
var computed_rows = cljs.core.get.call(null,map__10724__$1,new cljs.core.Keyword(null,"computed-rows","computed-rows",-1553800417));
console.log("computed-rows",cljs.core.take.call(null,(10),computed_rows));

console.log("reloading selected row");

if(cljs.core.truth_(selected_row_index)){
return cljs.core.nth.call(null,computed_rows,selected_row_index);
} else {
return null;
}
}));
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"computed-headers","computed-headers",-62800495),(function (_,___$1){
return re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"table-headers","table-headers",-1687849963)], null));
}),(function (headers){
return cljs.core.into.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["score"], null),headers);
}));
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"computed-rows","computed-rows",-1553800417),(function (_,___$1){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"rows","rows",850049680),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"table-rows","table-rows",282178535)], null)),new cljs.core.Keyword(null,"scores","scores",-1267421800),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"scores","scores",-1267421800)], null))], null);
}),(function (p__10726){
var map__10727 = p__10726;
var map__10727__$1 = (((((!((map__10727 == null))))?(((((map__10727.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__10727.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__10727):map__10727);
var rows = cljs.core.get.call(null,map__10727__$1,new cljs.core.Keyword(null,"rows","rows",850049680));
var scores = cljs.core.get.call(null,map__10727__$1,new cljs.core.Keyword(null,"scores","scores",-1267421800));
console.log("reloading computed rows");

var G__10729 = rows;
if(cljs.core.truth_(scores)){
return cljs.core.mapv.call(null,((function (G__10729,map__10727,map__10727__$1,rows,scores){
return (function (score,row){
return cljs.core.assoc.call(null,row,"score",score);
});})(G__10729,map__10727,map__10727__$1,rows,scores))
,scores,G__10729);
} else {
return G__10729;
}
}));
inferdb.spreadsheets.subs.table_rows = (function inferdb$spreadsheets$subs$table_rows(db,_){
return inferdb.spreadsheets.db.table_rows.call(null,db);
});
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"table-rows","table-rows",282178535),inferdb.spreadsheets.subs.table_rows);
/**
 * Takes tabular data represented as a sequence of maps and reshapes the data as a
 *   2D vector of cells and a vector of headers.
 */
inferdb.spreadsheets.subs.cell_vector = (function inferdb$spreadsheets$subs$cell_vector(headers,rows){
return cljs.core.into.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.map.call(null,(function (row){
return cljs.core.into.call(null,cljs.core.PersistentVector.EMPTY,cljs.core.map.call(null,(function (p1__10730_SHARP_){
return cljs.core.get.call(null,row,p1__10730_SHARP_);
})),headers);
})),rows);
});
inferdb.spreadsheets.subs.hot_props = (function inferdb$spreadsheets$subs$hot_props(p__10731,_){
var map__10732 = p__10731;
var map__10732__$1 = (((((!((map__10732 == null))))?(((((map__10732.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__10732.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__10732):map__10732);
var headers = cljs.core.get.call(null,map__10732__$1,new cljs.core.Keyword(null,"headers","headers",-835030129));
var rows = cljs.core.get.call(null,map__10732__$1,new cljs.core.Keyword(null,"rows","rows",850049680));
var data = inferdb.spreadsheets.subs.cell_vector.call(null,headers,rows);
return cljs.core.assoc_in.call(null,cljs.core.assoc_in.call(null,inferdb.spreadsheets.views.default_hot_settings,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"settings","settings",1556144875),new cljs.core.Keyword(null,"data","data",-232669377)], null),data),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"settings","settings",1556144875),new cljs.core.Keyword(null,"colHeaders","colHeaders",-361008132)], null),headers);
});
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"hot-props","hot-props",-1702307913),(function (_,___$1){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"headers","headers",-835030129),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"computed-headers","computed-headers",-62800495)], null)),new cljs.core.Keyword(null,"rows","rows",850049680),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"computed-rows","computed-rows",-1553800417)], null))], null);
}),inferdb.spreadsheets.subs.hot_props);
inferdb.spreadsheets.subs.selections = (function inferdb$spreadsheets$subs$selections(db,_){
return inferdb.spreadsheets.db.selections.call(null,db);
});
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"selections","selections",-1277610233),inferdb.spreadsheets.subs.selections);
inferdb.spreadsheets.subs.selected_columns = (function inferdb$spreadsheets$subs$selected_columns(db,_){
return inferdb.spreadsheets.db.selected_columns.call(null,db);
});
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"selected-columns","selected-columns",-1576017596),inferdb.spreadsheets.subs.selected_columns);
inferdb.spreadsheets.subs.topojson_feature = "cb_2017_us_cd115_20m";
inferdb.spreadsheets.subs.left_pad = (function inferdb$spreadsheets$subs$left_pad(s,n,c){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.apply.call(null,cljs.core.str,cljs.core.repeat.call(null,(function (){var x__4219__auto__ = (0);
var y__4220__auto__ = (n - cljs.core.count.call(null,s));
return ((x__4219__auto__ > y__4220__auto__) ? x__4219__auto__ : y__4220__auto__);
})(),c))),cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)].join('');
});
inferdb.spreadsheets.subs.vega_lite_spec = (function inferdb$spreadsheets$subs$vega_lite_spec(p__10736){
var map__10737 = p__10736;
var map__10737__$1 = (((((!((map__10737 == null))))?(((((map__10737.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__10737.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__10737):map__10737);
var selected_row = cljs.core.get.call(null,map__10737__$1,new cljs.core.Keyword(null,"selected-row","selected-row",-750259683));
var selections = cljs.core.get.call(null,map__10737__$1,new cljs.core.Keyword(null,"selections","selections",-1277610233));
var selected_columns = cljs.core.get.call(null,map__10737__$1,new cljs.core.Keyword(null,"selected-columns","selected-columns",-1576017596));
console.log("reloading vega-lite");

var temp__5720__auto__ = cljs.core.first.call(null,selections);
if(cljs.core.truth_(temp__5720__auto__)){
var selection = temp__5720__auto__;
return cljs.core.clj__GT_js.call(null,((((cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,selected_columns))) && (cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,cljs.core.first.call(null,selections)))) && ((!(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["score",null,"geo_fips",null,"district_name",null], null), null),cljs.core.first.call(null,selected_columns)))))))?(function (){var selected_row_kw = clojure.walk.keywordize_keys.call(null,selected_row);
var selected_column_kw = cljs.core.keyword.call(null,cljs.core.first.call(null,selected_columns));
var values = inferdb.cgpm.main.cgpm_simulate.call(null,inferdb.spreadsheets.model.census_cgpm,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [selected_column_kw], null),cljs.core.dissoc.call(null,selected_row_kw,selected_column_kw,new cljs.core.Keyword(null,"district_name","district_name",2013284048),new cljs.core.Keyword(null,"geo_fips","geo_fips",-1275154844),new cljs.core.Keyword(null,"score","score",-1963588780)),cljs.core.PersistentArrayMap.EMPTY,(100));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"$schema","$schema",1635092088),"https://vega.github.io/schema/vega-lite/v3.json",new cljs.core.Keyword(null,"data","data",-232669377),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"values","values",372645556),values], null),new cljs.core.Keyword(null,"layer","layer",-1601820589),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"mark","mark",-373816345),"bar",new cljs.core.Keyword(null,"encoding","encoding",1728578272),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"bin","bin",-200999690),true,new cljs.core.Keyword(null,"field","field",-1302436500),selected_column_kw,new cljs.core.Keyword(null,"type","type",1174270348),"quantitative"], null),new cljs.core.Keyword(null,"y","y",-1757859776),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"aggregate","aggregate",1511468442),"count",new cljs.core.Keyword(null,"type","type",1174270348),"quantitative",new cljs.core.Keyword(null,"axis","axis",-1215390822),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"title","title",636505583),"distribution of probable values"], null)], null)], null)], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"data","data",-232669377),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"values","values",372645556),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.PersistentArrayMap.createAsIfByAssoc([selected_column_kw,cljs.core.get.call(null,selected_row,cljs.core.first.call(null,selected_columns)),new cljs.core.Keyword(null,"label","label",1718410804),"Selected row"])], null)], null),new cljs.core.Keyword(null,"mark","mark",-373816345),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),"rule",new cljs.core.Keyword(null,"color","color",1011675173),"red"], null),new cljs.core.Keyword(null,"encoding","encoding",1728578272),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"field","field",-1302436500),selected_column_kw,new cljs.core.Keyword(null,"type","type",1174270348),"quantitative"], null)], null)], null)], null)], null);
})():((cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,selected_columns)))?new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"$schema","$schema",1635092088),"https://vega.github.io/schema/vega-lite/v3.json",new cljs.core.Keyword(null,"data","data",-232669377),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"values","values",372645556),selection], null),new cljs.core.Keyword(null,"mark","mark",-373816345),"bar",new cljs.core.Keyword(null,"encoding","encoding",1728578272),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"bin","bin",-200999690),true,new cljs.core.Keyword(null,"field","field",-1302436500),cljs.core.first.call(null,selected_columns),new cljs.core.Keyword(null,"type","type",1174270348),"quantitative"], null),new cljs.core.Keyword(null,"y","y",-1757859776),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"aggregate","aggregate",1511468442),"count",new cljs.core.Keyword(null,"type","type",1174270348),"quantitative"], null)], null)], null):(cljs.core.truth_(cljs.core.some.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, ["geo_fips",null], null), null),selected_columns))?(function (){var map_column = cljs.core.first.call(null,cljs.core.filter.call(null,((function (selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns){
return (function (p1__10734_SHARP_){
return cljs.core.not_EQ_.call(null,"geo_fips",p1__10734_SHARP_);
});})(selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns))
,selected_columns));
var transformed_selection = cljs.core.mapv.call(null,((function (map_column,selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns){
return (function (row){
return cljs.core.update.call(null,row,"geo_fips",((function (map_column,selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns){
return (function (p1__10735_SHARP_){
return inferdb.spreadsheets.subs.left_pad.call(null,cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__10735_SHARP_),(4),"0");
});})(map_column,selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns))
);
});})(map_column,selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns))
,selection);
var name = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"field","field",-1302436500),"NAME",new cljs.core.Keyword(null,"type","type",1174270348),"nominal"], null);
var color = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"field","field",-1302436500),map_column,new cljs.core.Keyword(null,"type","type",1174270348),"quantitative"], null);
return new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"$schema","$schema",1635092088),"https://vega.github.io/schema/vega-lite/v3.json",new cljs.core.Keyword(null,"width","width",-384071477),(500),new cljs.core.Keyword(null,"height","height",1025178622),(300),new cljs.core.Keyword(null,"data","data",-232669377),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"values","values",372645556),topojson,new cljs.core.Keyword(null,"format","format",-1306924766),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),"topojson",new cljs.core.Keyword(null,"feature","feature",27242652),inferdb.spreadsheets.subs.topojson_feature], null)], null),new cljs.core.Keyword(null,"transform","transform",1381301764),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"lookup","lookup",1225356838),"properties.GEOID",new cljs.core.Keyword(null,"from","from",1815293044),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"data","data",-232669377),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"values","values",372645556),transformed_selection], null),new cljs.core.Keyword(null,"key","key",-1516042587),"geo_fips","fields",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"field","field",-1302436500).cljs$core$IFn$_invoke$arity$1(name),new cljs.core.Keyword(null,"field","field",-1302436500).cljs$core$IFn$_invoke$arity$1(color)], null)], null)], null)], null),new cljs.core.Keyword(null,"projection","projection",-412523042),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),"albersUsa"], null),new cljs.core.Keyword(null,"mark","mark",-373816345),"geoshape",new cljs.core.Keyword(null,"encoding","encoding",1728578272),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"tooltip","tooltip",-1809677058),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [name,color], null),new cljs.core.Keyword(null,"color","color",1011675173),color], null)], null);
})():new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"$schema","$schema",1635092088),"https://vega.github.io/schema/vega-lite/v3.json",new cljs.core.Keyword(null,"data","data",-232669377),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"values","values",372645556),selection], null),new cljs.core.Keyword(null,"mark","mark",-373816345),"circle",new cljs.core.Keyword(null,"encoding","encoding",1728578272),cljs.core.reduce.call(null,((function (selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns){
return (function (acc,p__10739){
var vec__10740 = p__10739;
var k = cljs.core.nth.call(null,vec__10740,(0),null);
var field = cljs.core.nth.call(null,vec__10740,(1),null);
return cljs.core.assoc.call(null,acc,k,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"field","field",-1302436500),field,new cljs.core.Keyword(null,"type","type",1174270348),"quantitative"], null));
});})(selection,temp__5720__auto__,map__10737,map__10737__$1,selected_row,selections,selected_columns))
,cljs.core.PersistentArrayMap.EMPTY,cljs.core.map.call(null,cljs.core.vector,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.Keyword(null,"y","y",-1757859776)], null),cljs.core.take.call(null,(2),selected_columns)))], null)
))));
} else {
return null;
}
});
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"vega-lite-spec","vega-lite-spec",-1661799754),(function (_,___$1){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"selected-row","selected-row",-750259683),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"selected-row","selected-row",-750259683)], null)),new cljs.core.Keyword(null,"selections","selections",-1277610233),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"selections","selections",-1277610233)], null)),new cljs.core.Keyword(null,"selected-columns","selected-columns",-1576017596),re_frame.core.subscribe.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"selected-columns","selected-columns",-1576017596)], null))], null);
}),inferdb.spreadsheets.subs.vega_lite_spec);
re_frame.core.reg_sub.call(null,new cljs.core.Keyword(null,"whole-db","whole-db",-1329432213),(function (db){
return db;
}));

//# sourceMappingURL=subs.js.map
