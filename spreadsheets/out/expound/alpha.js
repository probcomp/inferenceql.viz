// Compiled by ClojureScript 1.10.520 {}
goog.provide('expound.alpha');
goog.require('cljs.core');
goog.require('expound.problems');
goog.require('cljs.spec.alpha');
goog.require('clojure.string');
goog.require('clojure.set');
goog.require('clojure.walk');
goog.require('goog.string.format');
goog.require('goog.string');
goog.require('expound.printer');
goog.require('expound.util');
goog.require('expound.ansi');
goog.require('cljs.spec.gen.alpha');
if((typeof expound !== 'undefined') && (typeof expound.alpha !== 'undefined') && (typeof expound.alpha.registry_ref !== 'undefined')){
} else {
expound.alpha.registry_ref = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"count","count",2139924085),(1)),cljs.spec.alpha.every_impl.call(null,new cljs.core.Symbol(null,"any?","any?",-318999933,null),cljs.core.any_QMARK_,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword("cljs.spec.alpha","kind-form","cljs.spec.alpha/kind-form",-1047104697),null,new cljs.core.Keyword("cljs.spec.alpha","cpred","cljs.spec.alpha/cpred",-693471218),(function (G__4424){
return ((cljs.core.coll_QMARK_.call(null,G__4424)) && (cljs.core._EQ_.call(null,(1),cljs.core.bounded_count.call(null,(1),G__4424))));
}),new cljs.core.Keyword(null,"count","count",2139924085),(1),new cljs.core.Keyword("cljs.spec.alpha","conform-all","cljs.spec.alpha/conform-all",45201917),true,new cljs.core.Keyword("cljs.spec.alpha","describe","cljs.spec.alpha/describe",1883026911),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"count","count",2139924085),(1))], null),null));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null),cljs.core.keyword_QMARK_);
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.spec","specs","expound.spec/specs",1949978405),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511)),cljs.spec.alpha.every_impl.call(null,new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword("cljs.spec.alpha","kind-form","cljs.spec.alpha/kind-form",-1047104697),null,new cljs.core.Keyword("cljs.spec.alpha","cpred","cljs.spec.alpha/cpred",-693471218),(function (G__4425){
return cljs.core.coll_QMARK_.call(null,G__4425);
}),new cljs.core.Keyword("cljs.spec.alpha","conform-all","cljs.spec.alpha/conform-all",45201917),true,new cljs.core.Keyword("cljs.spec.alpha","describe","cljs.spec.alpha/describe",1883026911),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511))], null),null));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.spec.problem","via","expound.spec.problem/via",421328647),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"kind","kind",-717265803),new cljs.core.Symbol("cljs.core","vector?","cljs.core/vector?",-1550392028,null)),cljs.spec.alpha.every_impl.call(null,new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword("cljs.spec.alpha","kind-form","cljs.spec.alpha/kind-form",-1047104697),new cljs.core.Symbol("cljs.core","vector?","cljs.core/vector?",-1550392028,null),new cljs.core.Keyword("cljs.spec.alpha","cpred","cljs.spec.alpha/cpred",-693471218),(function (G__4426){
return cljs.core.vector_QMARK_.call(null,G__4426);
}),new cljs.core.Keyword(null,"kind","kind",-717265803),cljs.core.vector_QMARK_,new cljs.core.Keyword("cljs.spec.alpha","conform-all","cljs.spec.alpha/conform-all",45201917),true,new cljs.core.Keyword("cljs.spec.alpha","describe","cljs.spec.alpha/describe",1883026911),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"kind","kind",-717265803),new cljs.core.Symbol("cljs.core","vector?","cljs.core/vector?",-1550392028,null))], null),null));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.spec","problem","expound.spec/problem",628036380),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","keys","cljs.spec.alpha/keys",1109346032,null),new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec.problem","via","expound.spec.problem/via",421328647)], null)),cljs.spec.alpha.map_spec_impl.call(null,cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.Keyword(null,"gfn","gfn",791517474),new cljs.core.Keyword(null,"pred-exprs","pred-exprs",1792271395),new cljs.core.Keyword(null,"keys-pred","keys-pred",858984739),new cljs.core.Keyword(null,"opt-keys","opt-keys",1262688261),new cljs.core.Keyword(null,"req-specs","req-specs",553962313),new cljs.core.Keyword(null,"req","req",-326448303),new cljs.core.Keyword(null,"req-keys","req-keys",514319221),new cljs.core.Keyword(null,"opt-specs","opt-specs",-384905450),new cljs.core.Keyword(null,"pred-forms","pred-forms",172611832),new cljs.core.Keyword(null,"opt","opt",-794706369)],[new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec.problem","via","expound.spec.problem/via",421328647)], null),null,null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (G__4427){
return cljs.core.map_QMARK_.call(null,G__4427);
}),(function (G__4427){
return cljs.core.contains_QMARK_.call(null,G__4427,new cljs.core.Keyword(null,"via","via",-1904457336));
})], null),(function (G__4427){
return ((cljs.core.map_QMARK_.call(null,G__4427)) && (cljs.core.contains_QMARK_.call(null,G__4427,new cljs.core.Keyword(null,"via","via",-1904457336))));
}),cljs.core.PersistentVector.EMPTY,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec.problem","via","expound.spec.problem/via",421328647)], null),null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"via","via",-1904457336)], null),cljs.core.PersistentVector.EMPTY,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Symbol(null,"%","%",-950237169,null))),cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","contains?","cljs.core/contains?",-976526835,null),new cljs.core.Symbol(null,"%","%",-950237169,null),new cljs.core.Keyword(null,"via","via",-1904457336)))], null),null])));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.spec","problems","expound.spec/problems",-1664082731),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Keyword("expound.spec","problem","expound.spec/problem",628036380)),cljs.spec.alpha.every_impl.call(null,new cljs.core.Keyword("expound.spec","problem","expound.spec/problem",628036380),new cljs.core.Keyword("expound.spec","problem","expound.spec/problem",628036380),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword("cljs.spec.alpha","kind-form","cljs.spec.alpha/kind-form",-1047104697),null,new cljs.core.Keyword("cljs.spec.alpha","cpred","cljs.spec.alpha/cpred",-693471218),(function (G__4428){
return cljs.core.coll_QMARK_.call(null,G__4428);
}),new cljs.core.Keyword("cljs.spec.alpha","conform-all","cljs.spec.alpha/conform-all",45201917),true,new cljs.core.Keyword("cljs.spec.alpha","describe","cljs.spec.alpha/describe",1883026911),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Keyword("expound.spec","problem","expound.spec/problem",628036380))], null),null));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.printer","show-valid-values?","expound.printer/show-valid-values?",1382130219),new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),cljs.core.boolean_QMARK_);
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.printer","value-str-fn","expound.printer/value-str-fn",-605841761),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","with-gen","cljs.spec.alpha/with-gen",1999495028,null),new cljs.core.Symbol("cljs.core","ifn?","cljs.core/ifn?",1573873861,null),cljs.core.list(new cljs.core.Symbol(null,"fn*","fn*",-752876845,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol("cljs.spec.gen.alpha","return","cljs.spec.gen.alpha/return",1565518169,null),cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.Symbol(null,"_","_",-1201019570,null)], null),"NOT IMPLEMENTED")))),cljs.spec.alpha.with_gen.call(null,cljs.core.ifn_QMARK_,(function (){
return cljs.spec.gen.alpha.return$.call(null,(function (_,___$1,___$2,___$3){
return "NOT IMPLEMENTED";
}));
})));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.printer","print-specs?","expound.printer/print-specs?",-2143920374),new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),cljs.core.boolean_QMARK_);
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.printer","theme","expound.printer/theme",976967333),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"figwheel-theme","figwheel-theme",1505227343),"null",new cljs.core.Keyword(null,"none","none",1333468478),"null"], null), null),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"figwheel-theme","figwheel-theme",1505227343),null,new cljs.core.Keyword(null,"none","none",1333468478),null], null), null));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.printer","opts","expound.printer/opts",785498940),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","keys","cljs.spec.alpha/keys",1109346032,null),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.printer","show-valid-values?","expound.printer/show-valid-values?",1382130219),new cljs.core.Keyword("expound.printer","value-str-fn","expound.printer/value-str-fn",-605841761),new cljs.core.Keyword("expound.printer","print-specs?","expound.printer/print-specs?",-2143920374),new cljs.core.Keyword("expound.printer","theme","expound.printer/theme",976967333)], null)),cljs.spec.alpha.map_spec_impl.call(null,cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.Keyword(null,"gfn","gfn",791517474),new cljs.core.Keyword(null,"pred-exprs","pred-exprs",1792271395),new cljs.core.Keyword(null,"keys-pred","keys-pred",858984739),new cljs.core.Keyword(null,"opt-keys","opt-keys",1262688261),new cljs.core.Keyword(null,"req-specs","req-specs",553962313),new cljs.core.Keyword(null,"req","req",-326448303),new cljs.core.Keyword(null,"req-keys","req-keys",514319221),new cljs.core.Keyword(null,"opt-specs","opt-specs",-384905450),new cljs.core.Keyword(null,"pred-forms","pred-forms",172611832),new cljs.core.Keyword(null,"opt","opt",-794706369)],[null,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.printer","show-valid-values?","expound.printer/show-valid-values?",1382130219),new cljs.core.Keyword("expound.printer","value-str-fn","expound.printer/value-str-fn",-605841761),new cljs.core.Keyword("expound.printer","print-specs?","expound.printer/print-specs?",-2143920374),new cljs.core.Keyword("expound.printer","theme","expound.printer/theme",976967333)], null),null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (G__4429){
return cljs.core.map_QMARK_.call(null,G__4429);
})], null),(function (G__4429){
return cljs.core.map_QMARK_.call(null,G__4429);
}),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"show-valid-values?","show-valid-values?",-587258094),new cljs.core.Keyword(null,"value-str-fn","value-str-fn",1124137860),new cljs.core.Keyword(null,"print-specs?","print-specs?",146397677),new cljs.core.Keyword(null,"theme","theme",-1247880880)], null),cljs.core.PersistentVector.EMPTY,null,cljs.core.PersistentVector.EMPTY,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.printer","show-valid-values?","expound.printer/show-valid-values?",1382130219),new cljs.core.Keyword("expound.printer","value-str-fn","expound.printer/value-str-fn",-605841761),new cljs.core.Keyword("expound.printer","print-specs?","expound.printer/print-specs?",-2143920374),new cljs.core.Keyword("expound.printer","theme","expound.printer/theme",976967333)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Symbol(null,"%","%",-950237169,null)))], null),null])));
cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","or","cljs.spec.alpha/or",-831679639,null),new cljs.core.Keyword(null,"set","set",304602554),new cljs.core.Symbol("cljs.core","set?","cljs.core/set?",-1176684971,null),new cljs.core.Keyword(null,"pred","pred",1927423397),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","with-gen","cljs.spec.alpha/with-gen",1999495028,null),new cljs.core.Symbol("cljs.core","ifn?","cljs.core/ifn?",1573873861,null),cljs.core.list(new cljs.core.Symbol(null,"fn*","fn*",-752876845,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol("cljs.spec.gen.alpha","elements","cljs.spec.gen.alpha/elements",749148929,null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),new cljs.core.Symbol("cljs.core","int?","cljs.core/int?",50730120,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null),new cljs.core.Symbol("cljs.core","symbol?","cljs.core/symbol?",1422196122,null)], null)))),new cljs.core.Keyword(null,"kw","kw",1158308175),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null),new cljs.core.Keyword(null,"spec","spec",347520401),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","with-gen","cljs.spec.alpha/with-gen",1999495028,null),new cljs.core.Symbol("cljs.spec.alpha","spec?","cljs.spec.alpha/spec?",-2086793671,null),cljs.core.list(new cljs.core.Symbol(null,"fn*","fn*",-752876845,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol("cljs.spec.gen.alpha","elements","cljs.spec.gen.alpha/elements",749148929,null),cljs.core.list(new cljs.core.Symbol("cljs.core","for","cljs.core/for",-89947499,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","pr","cljs.core/pr",1715302632,null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),new cljs.core.Symbol("cljs.core","int?","cljs.core/int?",50730120,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null),new cljs.core.Symbol("cljs.core","symbol?","cljs.core/symbol?",1422196122,null)], null)], null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("cljs.core","pr","cljs.core/pr",1715302632,null))))))),cljs.spec.alpha.or_spec_impl.call(null,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"set","set",304602554),new cljs.core.Keyword(null,"pred","pred",1927423397),new cljs.core.Keyword(null,"kw","kw",1158308175),new cljs.core.Keyword(null,"spec","spec",347520401)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","set?","cljs.core/set?",-1176684971,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","with-gen","cljs.spec.alpha/with-gen",1999495028,null),new cljs.core.Symbol("cljs.core","ifn?","cljs.core/ifn?",1573873861,null),cljs.core.list(new cljs.core.Symbol(null,"fn*","fn*",-752876845,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol("cljs.spec.gen.alpha","elements","cljs.spec.gen.alpha/elements",749148929,null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),new cljs.core.Symbol("cljs.core","int?","cljs.core/int?",50730120,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null),new cljs.core.Symbol("cljs.core","symbol?","cljs.core/symbol?",1422196122,null)], null)))),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","with-gen","cljs.spec.alpha/with-gen",1999495028,null),new cljs.core.Symbol("cljs.spec.alpha","spec?","cljs.spec.alpha/spec?",-2086793671,null),cljs.core.list(new cljs.core.Symbol(null,"fn*","fn*",-752876845,null),cljs.core.PersistentVector.EMPTY,cljs.core.list(new cljs.core.Symbol("cljs.spec.gen.alpha","elements","cljs.spec.gen.alpha/elements",749148929,null),cljs.core.list(new cljs.core.Symbol("cljs.core","for","cljs.core/for",-89947499,null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","pr","cljs.core/pr",1715302632,null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","boolean?","cljs.core/boolean?",1400713761,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),new cljs.core.Symbol("cljs.core","int?","cljs.core/int?",50730120,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null),new cljs.core.Symbol("cljs.core","symbol?","cljs.core/symbol?",1422196122,null)], null)], null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("cljs.core","pr","cljs.core/pr",1715302632,null))))))], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.set_QMARK_,cljs.spec.alpha.with_gen.call(null,cljs.core.ifn_QMARK_,(function (){
return cljs.spec.gen.alpha.elements.call(null,new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.boolean_QMARK_,cljs.core.string_QMARK_,cljs.core.int_QMARK_,cljs.core.keyword_QMARK_,cljs.core.symbol_QMARK_], null));
})),cljs.core.qualified_keyword_QMARK_,cljs.spec.alpha.with_gen.call(null,cljs.spec.alpha.spec_QMARK_,(function (){
return cljs.spec.gen.alpha.elements.call(null,(function (){var iter__4523__auto__ = (function expound$alpha$iter__4430(s__4431){
return (new cljs.core.LazySeq(null,(function (){
var s__4431__$1 = s__4431;
while(true){
var temp__5720__auto__ = cljs.core.seq.call(null,s__4431__$1);
if(temp__5720__auto__){
var s__4431__$2 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__4431__$2)){
var c__4521__auto__ = cljs.core.chunk_first.call(null,s__4431__$2);
var size__4522__auto__ = cljs.core.count.call(null,c__4521__auto__);
var b__4433 = cljs.core.chunk_buffer.call(null,size__4522__auto__);
if((function (){var i__4432 = (0);
while(true){
if((i__4432 < size__4522__auto__)){
var pr = cljs.core._nth.call(null,c__4521__auto__,i__4432);
cljs.core.chunk_append.call(null,b__4433,cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol(null,"pr","pr",1056937027,null),pr,null,null));

var G__4434 = (i__4432 + (1));
i__4432 = G__4434;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4433),expound$alpha$iter__4430.call(null,cljs.core.chunk_rest.call(null,s__4431__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4433),null);
}
} else {
var pr = cljs.core.first.call(null,s__4431__$2);
return cljs.core.cons.call(null,cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol(null,"pr","pr",1056937027,null),pr,null,null),expound$alpha$iter__4430.call(null,cljs.core.rest.call(null,s__4431__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__4523__auto__.call(null,new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.boolean_QMARK_,cljs.core.string_QMARK_,cljs.core.int_QMARK_,cljs.core.keyword_QMARK_,cljs.core.symbol_QMARK_], null));
})());
}))], null),null));
expound.alpha.figwheel_theme = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"good-key","good-key",-1265033983),new cljs.core.Keyword(null,"warning-key","warning-key",-1487066651),new cljs.core.Keyword(null,"correct-key","correct-key",1087492967),new cljs.core.Keyword(null,"highlight","highlight",-800930873),new cljs.core.Keyword(null,"focus-path","focus-path",-2048689431),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659),new cljs.core.Keyword(null,"footer","footer",1606445390),new cljs.core.Keyword(null,"header","header",119441134),new cljs.core.Keyword(null,"good-pred","good-pred",-629085297),new cljs.core.Keyword(null,"good","good",511701169),new cljs.core.Keyword(null,"focus-key","focus-key",2074966449),new cljs.core.Keyword(null,"pointer","pointer",85071187),new cljs.core.Keyword(null,"bad","bad",1127186645),new cljs.core.Keyword(null,"error-key","error-key",-1651308715),new cljs.core.Keyword(null,"none","none",1333468478),new cljs.core.Keyword(null,"message","message",-406056002)],[new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"bold","bold",-116809535)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"bold","bold",-116809535)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"magenta","magenta",1687937081)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"red","red",-969428204)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"cyan","cyan",1118839274)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"cyan","cyan",1118839274)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"green","green",-945526839)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"bold","bold",-116809535)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"magenta","magenta",1687937081)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"red","red",-969428204)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"red","red",-969428204)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"none","none",1333468478)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"magenta","magenta",1687937081)], null)]);
expound.alpha.check_header_size = (45);
expound.alpha.header_size = (35);
expound.alpha.section_size = (25);
expound.alpha._STAR_value_str_fn_STAR_ = (function expound$alpha$_STAR_value_str_fn_STAR_(_,___$1,___$2,___$3){
return "NOT IMPLEMENTED";
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","value-in-context","expound.alpha/value-in-context",-547735824,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"opts","opts",155075701),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Keyword(null,"spec-name","spec-name",1234428066),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null",new cljs.core.Keyword("cljs.spec.alpha","pred","cljs.spec.alpha/pred",-798342594),"null"], null), null)),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"path","path",-188191168),new cljs.core.Keyword("expound","path","expound/path",-1026376555),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"opts","opts",155075701),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Keyword(null,"spec-name","spec-name",1234428066),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null",new cljs.core.Keyword("cljs.spec.alpha","pred","cljs.spec.alpha/pred",-798342594),"null"], null), null)),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"path","path",-188191168),new cljs.core.Keyword("expound","path","expound/path",-1026376555),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"opts","opts",155075701),new cljs.core.Keyword(null,"spec-name","spec-name",1234428066),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Keyword(null,"path","path",-188191168),new cljs.core.Keyword(null,"value","value",305978217)], null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.map_QMARK_,cljs.spec.alpha.nilable_impl.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null",new cljs.core.Keyword("cljs.spec.alpha","pred","cljs.spec.alpha/pred",-798342594),"null"], null), null),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"args","args",1315556576),null,new cljs.core.Keyword(null,"ret","ret",-468222814),null,new cljs.core.Keyword(null,"fn","fn",-1175266204),null,new cljs.core.Keyword("cljs.spec.alpha","pred","cljs.spec.alpha/pred",-798342594),null], null), null),null),cljs.core.any_QMARK_,new cljs.core.Keyword("expound","path","expound/path",-1026376555),cljs.core.any_QMARK_], null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null",new cljs.core.Keyword("cljs.spec.alpha","pred","cljs.spec.alpha/pred",-798342594),"null"], null), null)),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword("expound","path","expound/path",-1026376555),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"opts","opts",155075701),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Keyword(null,"spec-name","spec-name",1234428066),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null",new cljs.core.Keyword("cljs.spec.alpha","pred","cljs.spec.alpha/pred",-798342594),"null"], null), null)),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),new cljs.core.Keyword(null,"path","path",-188191168),new cljs.core.Keyword("expound","path","expound/path",-1026376555),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),cljs.core.string_QMARK_,null,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),null,null,null));
/**
 * Given a form and a path into that form, returns a string
 * that helps the user understand where that path is located
 * in the form
 */
expound.alpha.value_in_context = (function expound$alpha$value_in_context(opts,spec_name,form,path,value){
var _STAR_print_namespace_maps_STAR__orig_val__4435 = cljs.core._STAR_print_namespace_maps_STAR_;
var _STAR_print_namespace_maps_STAR__temp_val__4436 = false;
cljs.core._STAR_print_namespace_maps_STAR_ = _STAR_print_namespace_maps_STAR__temp_val__4436;

try{if(cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"fn","fn",-1175266204),spec_name)){
return expound.printer.indent.call(null,expound.ansi.color.call(null,cljs.core.pr_str.call(null,form),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659)));
} else {
if(cljs.core._EQ_.call(null,form,value)){
return expound.printer.indent.call(null,expound.ansi.color.call(null,expound.printer.pprint_str.call(null,value),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659)));
} else {
if(cljs.core.truth_(path)){
return expound.printer.indent.call(null,expound.problems.highlighted_value.call(null,opts,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("expound","form","expound/form",-264680632),form,new cljs.core.Keyword("expound","in","expound/in",-1900412298),path,new cljs.core.Keyword("expound","value","expound/value",-1539618504),value], null)));
} else {
return expound.printer.format.call(null,"Part of the value\n\n%s",expound.printer.indent.call(null,expound.ansi.color.call(null,cljs.core.pr_str.call(null,form),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659))));

}
}
}
}finally {cljs.core._STAR_print_namespace_maps_STAR_ = _STAR_print_namespace_maps_STAR__orig_val__4435;
}});
expound.alpha.spec_str = (function expound$alpha$spec_str(spec){
if((spec instanceof cljs.core.Keyword)){
return expound.printer.format.call(null,"%s:\n%s",spec,expound.printer.indent.call(null,expound.printer.pprint_str.call(null,cljs.spec.alpha.form.call(null,spec))));
} else {
return expound.printer.pprint_str.call(null,cljs.spec.alpha.form.call(null,spec));
}
});
expound.alpha.spec_PLUS_via = (function expound$alpha$spec_PLUS_via(problem){
var map__4437 = problem;
var map__4437__$1 = (((((!((map__4437 == null))))?(((((map__4437.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4437.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4437):map__4437);
var via = cljs.core.get.call(null,map__4437__$1,new cljs.core.Keyword(null,"via","via",-1904457336));
var spec = cljs.core.get.call(null,map__4437__$1,new cljs.core.Keyword(null,"spec","spec",347520401));
if((spec instanceof cljs.core.Keyword)){
return cljs.core.into.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [spec], null),via);
} else {
return via;
}
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","specs","expound.alpha/specs",-1617991929,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"problems","problems",2097327077),new cljs.core.Keyword("expound.spec","problems","expound.spec/problems",-1664082731)),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword("expound.spec","specs","expound.spec/specs",1949978405)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"problems","problems",2097327077),new cljs.core.Keyword("expound.spec","problems","expound.spec/problems",-1664082731)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"problems","problems",2097327077)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec","problems","expound.spec/problems",-1664082731)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec","problems","expound.spec/problems",-1664082731)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"problems","problems",2097327077),new cljs.core.Keyword("expound.spec","problems","expound.spec/problems",-1664082731)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Keyword("expound.spec","specs","expound.spec/specs",1949978405),new cljs.core.Keyword("expound.spec","specs","expound.spec/specs",1949978405),null,null),new cljs.core.Keyword("expound.spec","specs","expound.spec/specs",1949978405),null,null,null));
/**
 * Given a collection of problems, returns the specs for those problems, with duplicates removed
 */
expound.alpha.specs = (function expound$alpha$specs(problems){
return cljs.core.distinct.call(null,cljs.core.flatten.call(null,cljs.core.map.call(null,expound.alpha.spec_PLUS_via,problems)));
});
expound.alpha.specs_str = (function expound$alpha$specs_str(problems){
return clojure.string.join.call(null,"\n",cljs.core.map.call(null,expound.alpha.spec_str,cljs.core.reverse.call(null,expound.alpha.specs.call(null,problems))));
});
expound.alpha.named_QMARK_ = (function expound$alpha$named_QMARK_(x){
if((!((x == null)))){
if(((false) || ((cljs.core.PROTOCOL_SENTINEL === x.cljs$core$INamed$)))){
return true;
} else {
return false;
}
} else {
return false;
}
});
expound.alpha.pr_pred_STAR_ = (function expound$alpha$pr_pred_STAR_(pred){
if((((pred instanceof cljs.core.Symbol)) || (expound.alpha.named_QMARK_.call(null,pred)))){
return cljs.core.name.call(null,pred);
} else {
if(cljs.core.fn_QMARK_.call(null,pred)){
return expound.printer.pprint_fn.call(null,pred);
} else {
return expound.printer.elide_core_ns.call(null,(function (){var _STAR_print_namespace_maps_STAR__orig_val__4440 = cljs.core._STAR_print_namespace_maps_STAR_;
var _STAR_print_namespace_maps_STAR__temp_val__4441 = false;
cljs.core._STAR_print_namespace_maps_STAR_ = _STAR_print_namespace_maps_STAR__temp_val__4441;

try{return expound.printer.pprint_str.call(null,pred);
}finally {cljs.core._STAR_print_namespace_maps_STAR_ = _STAR_print_namespace_maps_STAR__orig_val__4440;
}})());

}
}
});
expound.alpha.pr_pred = (function expound$alpha$pr_pred(pred,spec){
if(cljs.core._EQ_.call(null,new cljs.core.Keyword("cljs.spec.alpha","unknown","cljs.spec.alpha/unknown",651034818),pred)){
return expound.alpha.pr_pred_STAR_.call(null,spec);
} else {
return expound.alpha.pr_pred_STAR_.call(null,pred);
}
});
expound.alpha.show_spec_name = (function expound$alpha$show_spec_name(spec_name,value){
if(cljs.core.truth_(spec_name)){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var G__4443 = spec_name;
var G__4443__$1 = (((G__4443 instanceof cljs.core.Keyword))?G__4443.fqn:null);
switch (G__4443__$1) {
case "cljs.spec.alpha/pred":
return "";

break;
case "args":
return "Function arguments\n\n";

break;
case "ret":
return "Return value\n\n";

break;
case "fn":
return "Function arguments and return value\n\n";

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__4443__$1)].join('')));

}
})()),cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)].join('');
} else {
return value;
}
});
expound.alpha.preds = (function expound$alpha$preds(problems){
return clojure.string.join.call(null,"\n\nor\n\n",cljs.core.distinct.call(null,cljs.core.map.call(null,(function (problem){
return expound.printer.indent.call(null,expound.ansi.color.call(null,expound.alpha.pr_pred.call(null,new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem),new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(problem)),new cljs.core.Keyword(null,"good-pred","good-pred",-629085297)));
}),problems)));
});
expound.alpha.spec_w_error_message_QMARK_ = (function expound$alpha$spec_w_error_message_QMARK_(via,pred){
return cljs.core.boolean$.call(null,(function (){var last_spec = cljs.core.last.call(null,via);
var and__4120__auto__ = cljs.core.not_EQ_.call(null,new cljs.core.Keyword("cljs.spec.alpha","unknown","cljs.spec.alpha/unknown",651034818),pred);
if(and__4120__auto__){
var and__4120__auto____$1 = cljs.core.qualified_keyword_QMARK_.call(null,last_spec);
if(and__4120__auto____$1){
var and__4120__auto____$2 = expound.alpha.error_message.call(null,last_spec);
if(cljs.core.truth_(and__4120__auto____$2)){
return cljs.spec.alpha.get_spec.call(null,last_spec);
} else {
return and__4120__auto____$2;
}
} else {
return and__4120__auto____$1;
}
} else {
return and__4120__auto__;
}
})());
});
expound.alpha.predicate_errors = (function expound$alpha$predicate_errors(problems){
var vec__4445 = cljs.core.juxt.call(null,cljs.core.filter,cljs.core.remove).call(null,(function (p__4448){
var map__4449 = p__4448;
var map__4449__$1 = (((((!((map__4449 == null))))?(((((map__4449.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4449.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4449):map__4449);
var via = cljs.core.get.call(null,map__4449__$1,new cljs.core.Keyword("expound","via","expound/via",-595987777));
var pred = cljs.core.get.call(null,map__4449__$1,new cljs.core.Keyword(null,"pred","pred",1927423397));
return expound.alpha.spec_w_error_message_QMARK_.call(null,via,pred);
}),problems);
var with_msg = cljs.core.nth.call(null,vec__4445,(0),null);
var no_msgs = cljs.core.nth.call(null,vec__4445,(1),null);
return clojure.string.join.call(null,"\n\nor\n\n",cljs.core.remove.call(null,cljs.core.nil_QMARK_,cljs.core.conj.call(null,cljs.core.keep.call(null,((function (vec__4445,with_msg,no_msgs){
return (function (p__4451){
var map__4452 = p__4451;
var map__4452__$1 = (((((!((map__4452 == null))))?(((((map__4452.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4452.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4452):map__4452);
var via = cljs.core.get.call(null,map__4452__$1,new cljs.core.Keyword("expound","via","expound/via",-595987777));
var last_spec = cljs.core.last.call(null,via);
if(cljs.core.qualified_keyword_QMARK_.call(null,last_spec)){
return expound.ansi.color.call(null,expound.alpha.error_message.call(null,last_spec),new cljs.core.Keyword(null,"good","good",511701169));
} else {
return null;
}
});})(vec__4445,with_msg,no_msgs))
,with_msg),((cljs.core.seq.call(null,no_msgs))?expound.printer.format.call(null,"should satisfy\n\n%s",expound.alpha.preds.call(null,no_msgs)):null))));
});
expound.alpha.label = (function expound$alpha$label(var_args){
var G__4455 = arguments.length;
switch (G__4455) {
case 1:
return expound.alpha.label.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return expound.alpha.label.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return expound.alpha.label.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

expound.alpha.label.cljs$core$IFn$_invoke$arity$1 = (function (size){
return cljs.core.apply.call(null,cljs.core.str,cljs.core.repeat.call(null,size,"-"));
});

expound.alpha.label.cljs$core$IFn$_invoke$arity$2 = (function (size,s){
return expound.alpha.label.call(null,size,s,"-");
});

expound.alpha.label.cljs$core$IFn$_invoke$arity$3 = (function (size,s,label_str){
return expound.ansi.color.call(null,(function (){var prefix = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(label_str),cljs.core.str.cljs$core$IFn$_invoke$arity$1(label_str)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)," "].join('');
var chars_left = (size - cljs.core.count.call(null,prefix));
return [prefix,cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.apply.call(null,cljs.core.str,cljs.core.repeat.call(null,chars_left,label_str)))].join('');
})(),new cljs.core.Keyword(null,"header","header",119441134));
});

expound.alpha.label.cljs$lang$maxFixedArity = 3;

expound.alpha.header_label = cljs.core.partial.call(null,expound.alpha.label,expound.alpha.header_size);
expound.alpha.section_label = cljs.core.partial.call(null,expound.alpha.label,expound.alpha.section_size);
expound.alpha.relevant_specs = (function expound$alpha$relevant_specs(problems){
var sp_str = expound.alpha.specs_str.call(null,problems);
if(clojure.string.blank_QMARK_.call(null,sp_str)){
return "";
} else {
return expound.printer.format.call(null,"%s\n\n%s",expound.alpha.section_label.call(null,"Relevant specs"),sp_str);
}
});
expound.alpha.multi_spec_parts = (function expound$alpha$multi_spec_parts(spec_form){
var vec__4457 = spec_form;
var _multi_spec = cljs.core.nth.call(null,vec__4457,(0),null);
var mm = cljs.core.nth.call(null,vec__4457,(1),null);
var retag = cljs.core.nth.call(null,vec__4457,(2),null);
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"mm","mm",-1652850560),mm,new cljs.core.Keyword(null,"retag","retag",-1111558802),retag], null);
});
expound.alpha.multi_spec = (function expound$alpha$multi_spec(pred,spec){
return cljs.core.first.call(null,cljs.core.filter.call(null,(function (p1__4460_SHARP_){
return ((cljs.core.sequential_QMARK_.call(null,p1__4460_SHARP_)) && (((2) <= cljs.core.count.call(null,p1__4460_SHARP_))) && (cljs.core._EQ_.call(null,new cljs.core.Keyword("cljs.spec.alpha","multi-spec","cljs.spec.alpha/multi-spec",-1464710253),cljs.core.keyword.call(null,cljs.core.first.call(null,p1__4460_SHARP_)))) && (cljs.core._EQ_.call(null,pred,cljs.core.second.call(null,p1__4460_SHARP_))));
}),cljs.core.tree_seq.call(null,cljs.core.coll_QMARK_,cljs.core.seq,cljs.spec.alpha.form.call(null,spec))));
});
expound.alpha.no_method = (function expound$alpha$no_method(_spec_name,form,path,problem){
var sp = cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.core","last","cljs.core/last",1273893704,null),cljs.core.list(new cljs.core.Keyword("expound","via","expound/via",-595987777),new cljs.core.Symbol(null,"problem","problem",-1486280621,null))),cljs.core.last.call(null,new cljs.core.Keyword("expound","via","expound/via",-595987777).cljs$core$IFn$_invoke$arity$1(problem)),null,null);
var map__4461 = expound.alpha.multi_spec_parts.call(null,expound.alpha.multi_spec.call(null,new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem),sp));
var map__4461__$1 = (((((!((map__4461 == null))))?(((((map__4461.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4461.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4461):map__4461);
var mm = cljs.core.get.call(null,map__4461__$1,new cljs.core.Keyword(null,"mm","mm",-1652850560));
var retag = cljs.core.get.call(null,map__4461__$1,new cljs.core.Keyword(null,"retag","retag",-1111558802));
return expound.printer.format.call(null," Spec multimethod:      `%s`\n Dispatch function:     `%s`\n Dispatch value:        `%s`",cljs.core.pr_str.call(null,mm),cljs.core.pr_str.call(null,retag),cljs.core.pr_str.call(null,(cljs.core.truth_(retag)?retag.call(null,expound.problems.value_in.call(null,form,path)):null)));
});
if((typeof expound !== 'undefined') && (typeof expound.alpha !== 'undefined') && (typeof expound.alpha.problem_group_str !== 'undefined')){
} else {
expound.alpha.problem_group_str = (function (){var method_table__4613__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__4614__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__4615__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__4616__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__4617__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"expound.alpha","problem-group-str"),((function (method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__,hierarchy__4617__auto__){
return (function (type,_spec_name,_form,_path,_problems,_opts){
return type;
});})(method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__,hierarchy__4617__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__4617__auto__,method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__));
})();
}
if((typeof expound !== 'undefined') && (typeof expound.alpha !== 'undefined') && (typeof expound.alpha.expected_str !== 'undefined')){
} else {
expound.alpha.expected_str = (function (){var method_table__4613__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__4614__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__4615__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__4616__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__4617__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"expound.alpha","expected-str"),((function (method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__,hierarchy__4617__auto__){
return (function (type,_spec_name,_form,_path,_problems,_opts){
return type;
});})(method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__,hierarchy__4617__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__4617__auto__,method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__));
})();
}
if((typeof expound !== 'undefined') && (typeof expound.alpha !== 'undefined') && (typeof expound.alpha.value_str !== 'undefined')){
} else {
expound.alpha.value_str = (function (){var method_table__4613__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__4614__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__4615__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__4616__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__4617__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"expound.alpha","value-str"),((function (method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__,hierarchy__4617__auto__){
return (function (type,_spec_name,_form,_path,_problems,_opts){
return type;
});})(method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__,hierarchy__4617__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__4617__auto__,method_table__4613__auto__,prefer_table__4614__auto__,method_cache__4615__auto__,cached_hierarchy__4616__auto__));
})();
}
expound.alpha.expected_str_STAR_ = (function expound$alpha$expected_str_STAR_(spec_name,problems,opts){
var problem = cljs.core.first.call(null,problems);
var map__4463 = problem;
var map__4463__$1 = (((((!((map__4463 == null))))?(((((map__4463.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4463.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4463):map__4463);
var form = cljs.core.get.call(null,map__4463__$1,new cljs.core.Keyword("expound","form","expound/form",-264680632));
var in$ = cljs.core.get.call(null,map__4463__$1,new cljs.core.Keyword("expound","in","expound/in",-1900412298));
var type = new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(problem);
return expound.alpha.expected_str.call(null,type,spec_name,form,in$,problems,opts);
});
expound.alpha.value_str_STAR_ = (function expound$alpha$value_str_STAR_(spec_name,problems,opts){
var problem = cljs.core.first.call(null,problems);
var map__4465 = problem;
var map__4465__$1 = (((((!((map__4465 == null))))?(((((map__4465.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4465.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4465):map__4465);
var form = cljs.core.get.call(null,map__4465__$1,new cljs.core.Keyword("expound","form","expound/form",-264680632));
var in$ = cljs.core.get.call(null,map__4465__$1,new cljs.core.Keyword("expound","in","expound/in",-1900412298));
var type = new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(problem);
return expound.alpha.value_str.call(null,type,spec_name,form,in$,problems,opts);
});
expound.alpha.conformed_value = (function expound$alpha$conformed_value(problems,invalid_value){
var conformed_val = new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,problems));
if(cljs.core._EQ_.call(null,conformed_val,invalid_value)){
return "";
} else {
return expound.printer.format.call(null,"\n\nwhen conformed as\n\n%s",expound.printer.indent.call(null,expound.ansi.color.call(null,cljs.core.pr_str.call(null,conformed_val),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659))));
}
});
expound.alpha.value_PLUS_conformed_value = (function expound$alpha$value_PLUS_conformed_value(problems,spec_name,form,path,opts){
var map__4467 = opts;
var map__4467__$1 = (((((!((map__4467 == null))))?(((((map__4467.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4467.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4467):map__4467);
var show_conformed_QMARK_ = cljs.core.get.call(null,map__4467__$1,new cljs.core.Keyword(null,"show-conformed?","show-conformed?",-1548441572));
var invalid_value = (((path == null))?new cljs.core.Keyword("expound.alpha","no-value-found","expound.alpha/no-value-found",1205148696):expound.problems.value_in.call(null,form,path));
return expound.printer.format.call(null,"%s%s",expound.alpha._STAR_value_str_fn_STAR_.call(null,spec_name,form,path,invalid_value),(cljs.core.truth_(show_conformed_QMARK_)?expound.alpha.conformed_value.call(null,problems,invalid_value):""));
});
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword(null,"default","default",-1987822328),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.show_spec_name.call(null,spec_name,expound.alpha.value_PLUS_conformed_value.call(null,problems,spec_name,form,path,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"show-conformed?","show-conformed?",-1548441572),true], null)));
}));
expound.alpha.explain_missing_keys = (function expound$alpha$explain_missing_keys(problems){
var missing_keys = cljs.core.map.call(null,(function (p1__4469_SHARP_){
return expound.printer.missing_key.call(null,new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(p1__4469_SHARP_));
}),problems);
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.printer.format.call(null,"should contain %s: %s",((((cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,missing_keys))) && (cljs.core.every_QMARK_.call(null,cljs.core.keyword,missing_keys))))?"key":"keys"),expound.printer.print_missing_keys.call(null,problems))),(function (){var temp__5718__auto__ = expound.printer.print_spec_keys.call(null,problems);
if(cljs.core.truth_(temp__5718__auto__)){
var table = temp__5718__auto__;
return ["\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1(table)].join('');
} else {
return null;
}
})()].join('');
});
expound.alpha.format_str = "%s\n\n%s\n\n%s";
expound.alpha.format_err = (function expound$alpha$format_err(header,type,spec_name,form,in$,problems,opts,expected){
return expound.printer.format.call(null,expound.alpha.format_str,expound.alpha.header_label.call(null,header),expound.alpha.value_str.call(null,type,spec_name,form,in$,problems,opts),expected);
});
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem-group","one-value","expound.problem-group/one-value",-1584327548),(function (_type,spec_name,form,path,problems,opts){
var problem = cljs.core.first.call(null,problems);
var subproblems = new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(problem);
var grouped_subproblems = cljs.core.vals.call(null,cljs.core.group_by.call(null,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659),subproblems));
return clojure.string.join.call(null,"\n\nor\n\n",cljs.core.map.call(null,((function (problem,subproblems,grouped_subproblems){
return (function (p1__4470_SHARP_){
return expound.alpha.expected_str_STAR_.call(null,spec_name,p1__4470_SHARP_,opts);
});})(problem,subproblems,grouped_subproblems))
,grouped_subproblems));
}));
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword("expound.problem-group","one-value","expound.problem-group/one-value",-1584327548),(function (type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

var problem = cljs.core.first.call(null,problems);
var subproblems = new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(problem);
return expound.alpha.value_str_STAR_.call(null,spec_name,subproblems,opts);
}));
expound.alpha.header = (function expound$alpha$header(type){
var G__4471 = type;
var G__4471__$1 = (((G__4471 instanceof cljs.core.Keyword))?G__4471.fqn:null);
switch (G__4471__$1) {
case "expound.problem/missing-spec":
return "Missing spec";

break;
default:
return "Spec failed";

}
});
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem-group","one-value","expound.problem-group/one-value",-1584327548),(function (type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

var problem = cljs.core.first.call(null,problems);
var subproblems = new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(problem);
var map__4473 = cljs.core.first.call(null,subproblems);
var map__4473__$1 = (((((!((map__4473 == null))))?(((((map__4473.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4473.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4473):map__4473);
var form__$1 = cljs.core.get.call(null,map__4473__$1,new cljs.core.Keyword("expound","form","expound/form",-264680632));
var in$ = cljs.core.get.call(null,map__4473__$1,new cljs.core.Keyword("expound","in","expound/in",-1900412298));
return expound.alpha.format_err.call(null,expound.alpha.header.call(null,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,subproblems))),type,spec_name,form__$1,in$,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form__$1,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),(function (type,spec_name,form,path,problems,opts){
var subproblems = new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,problems));
return clojure.string.join.call(null,"\n\nor value\n\n",(function (){var iter__4523__auto__ = ((function (subproblems){
return (function expound$alpha$iter__4475(s__4476){
return (new cljs.core.LazySeq(null,((function (subproblems){
return (function (){
var s__4476__$1 = s__4476;
while(true){
var temp__5720__auto__ = cljs.core.seq.call(null,s__4476__$1);
if(temp__5720__auto__){
var s__4476__$2 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__4476__$2)){
var c__4521__auto__ = cljs.core.chunk_first.call(null,s__4476__$2);
var size__4522__auto__ = cljs.core.count.call(null,c__4521__auto__);
var b__4478 = cljs.core.chunk_buffer.call(null,size__4522__auto__);
if((function (){var i__4477 = (0);
while(true){
if((i__4477 < size__4522__auto__)){
var problem = cljs.core._nth.call(null,c__4521__auto__,i__4477);
cljs.core.chunk_append.call(null,b__4478,expound.printer.format.call(null,"%s\n\n%s",expound.alpha.value_str_STAR_.call(null,spec_name,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [problem], null),opts),expound.alpha.expected_str_STAR_.call(null,spec_name,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [problem], null),opts)));

var G__4479 = (i__4477 + (1));
i__4477 = G__4479;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4478),expound$alpha$iter__4475.call(null,cljs.core.chunk_rest.call(null,s__4476__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4478),null);
}
} else {
var problem = cljs.core.first.call(null,s__4476__$2);
return cljs.core.cons.call(null,expound.printer.format.call(null,"%s\n\n%s",expound.alpha.value_str_STAR_.call(null,spec_name,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [problem], null),opts),expound.alpha.expected_str_STAR_.call(null,spec_name,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [problem], null),opts)),expound$alpha$iter__4475.call(null,cljs.core.rest.call(null,s__4476__$2)));
}
} else {
return null;
}
break;
}
});})(subproblems))
,null,null));
});})(subproblems))
;
return iter__4523__auto__.call(null,subproblems);
})());
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),(function (_type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

return expound.printer.format.call(null,"%s\n\n%s",expound.alpha.header_label.call(null,"Spec failed"),expound.alpha.expected_str.call(null,_type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","missing-key","expound.problem/missing-key",-750683408),(function (_type,spec_name,_form,path,problems,opts){
return expound.alpha.explain_missing_keys.call(null,problems);
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","missing-key","expound.problem/missing-key",-750683408),(function (type,spec_name,form,path,problems,opts){
if(cljs.core.truth_(cljs.core.apply.call(null,cljs.core._EQ_,cljs.core.map.call(null,new cljs.core.Keyword(null,"val","val",128701612),problems)))){
} else {
throw (new Error(["Assert failed: ",[expound.util.assert_message,": All values should be the same, but they are ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(problems)].join(''),"\n","(apply = (map :val problems))"].join('')));
}

return expound.alpha.format_err.call(null,"Spec failed",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","not-in-set","expound.problem/not-in-set",14506077),(function (_type,_spec_name,_form,_path,problems,_opts){
var combined_set = cljs.core.apply.call(null,clojure.set.union,cljs.core.map.call(null,new cljs.core.Keyword(null,"pred","pred",1927423397),problems));
return expound.printer.format.call(null,"should be%s: %s",((cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,combined_set)))?"":" one of"),expound.ansi.color.call(null,clojure.string.join.call(null,", ",cljs.core.map.call(null,((function (combined_set){
return (function (p1__4481_SHARP_){
return expound.ansi.color.call(null,p1__4481_SHARP_,new cljs.core.Keyword(null,"good","good",511701169));
});})(combined_set))
,cljs.core.sort.call(null,cljs.core.map.call(null,((function (combined_set){
return (function (p1__4480_SHARP_){
return ["",cljs.core.pr_str.call(null,p1__4480_SHARP_),""].join('');
});})(combined_set))
,combined_set)))),new cljs.core.Keyword(null,"good","good",511701169)));
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","not-in-set","expound.problem/not-in-set",14506077),(function (type,spec_name,form,path,problems,opts){
if(cljs.core.truth_(cljs.core.apply.call(null,cljs.core._EQ_,cljs.core.map.call(null,new cljs.core.Keyword(null,"val","val",128701612),problems)))){
} else {
throw (new Error(["Assert failed: ",[expound.util.assert_message,": All values should be the same, but they are ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(problems)].join(''),"\n","(apply = (map :val problems))"].join('')));
}

return expound.alpha.format_err.call(null,"Spec failed",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","missing-spec","expound.problem/missing-spec",-1439599438),(function (_type,spec_name,form,path,problems,opts){
return ["with\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.join.call(null,"\n\nor with\n\n",cljs.core.map.call(null,(function (p1__4482_SHARP_){
return expound.alpha.no_method.call(null,spec_name,form,path,p1__4482_SHARP_);
}),problems)))].join('');
}));
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword("expound.problem","missing-spec","expound.problem/missing-spec",-1439599438),(function (_type,spec_name,form,path,problems,opts){
return expound.printer.format.call(null,"Cannot find spec for\n\n%s",expound.alpha.show_spec_name.call(null,spec_name,expound.alpha._STAR_value_str_fn_STAR_.call(null,spec_name,form,path,expound.problems.value_in.call(null,form,path))));
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","missing-spec","expound.problem/missing-spec",-1439599438),(function (type,spec_name,form,path,problems,opts){
return expound.printer.format.call(null,"%s\n\n%s\n\n%s",expound.alpha.header_label.call(null,"Missing spec"),expound.alpha.value_str.call(null,type,spec_name,form,path,problems,opts),expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
expound.alpha.lcs_STAR_ = (function expound$alpha$lcs_STAR_(p__4483,p__4484){
var vec__4485 = p__4483;
var seq__4486 = cljs.core.seq.call(null,vec__4485);
var first__4487 = cljs.core.first.call(null,seq__4486);
var seq__4486__$1 = cljs.core.next.call(null,seq__4486);
var x = first__4487;
var xs = seq__4486__$1;
var vec__4488 = p__4484;
var seq__4489 = cljs.core.seq.call(null,vec__4488);
var first__4490 = cljs.core.first.call(null,seq__4489);
var seq__4489__$1 = cljs.core.next.call(null,seq__4489);
var y = first__4490;
var ys = seq__4489__$1;
if(((cljs.core._EQ_.call(null,x,null)) || (cljs.core._EQ_.call(null,y,null)))){
return null;
} else {
if(cljs.core._EQ_.call(null,x,y)){
return cljs.core.vec.call(null,cljs.core.cons.call(null,x,expound.alpha.lcs_STAR_.call(null,xs,ys)));
} else {
return cljs.core.PersistentVector.EMPTY;

}
}
});
expound.alpha.lcs = (function expound$alpha$lcs(var_args){
var args__4736__auto__ = [];
var len__4730__auto___4492 = arguments.length;
var i__4731__auto___4493 = (0);
while(true){
if((i__4731__auto___4493 < len__4730__auto___4492)){
args__4736__auto__.push((arguments[i__4731__auto___4493]));

var G__4494 = (i__4731__auto___4493 + (1));
i__4731__auto___4493 = G__4494;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((0) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((0)),(0),null)):null);
return expound.alpha.lcs.cljs$core$IFn$_invoke$arity$variadic(argseq__4737__auto__);
});

expound.alpha.lcs.cljs$core$IFn$_invoke$arity$variadic = (function (paths){
return cljs.core.reduce.call(null,(function (xs,ys){
return expound.alpha.lcs_STAR_.call(null,xs,ys);
}),paths);
});

expound.alpha.lcs.cljs$lang$maxFixedArity = (0);

/** @this {Function} */
expound.alpha.lcs.cljs$lang$applyTo = (function (seq4491){
var self__4718__auto__ = this;
return self__4718__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq4491));
});

expound.alpha.alternation = (function expound$alpha$alternation(grp1,grp2){
var xs = new cljs.core.Keyword(null,"path-prefix","path-prefix",-1210521238).cljs$core$IFn$_invoke$arity$1(grp1);
var ys = new cljs.core.Keyword(null,"path-prefix","path-prefix",-1210521238).cljs$core$IFn$_invoke$arity$1(grp2);
var prefix = expound.alpha.lcs.call(null,xs,ys);
if((function (){var and__4120__auto__ = (!((prefix == null)));
if(and__4120__auto__){
var and__4120__auto____$1 = ((cljs.core._EQ_.call(null,new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(grp1)))?true:cljs.core.not_EQ_.call(null,prefix,xs));
if(and__4120__auto____$1){
if(cljs.core._EQ_.call(null,new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(grp2))){
return true;
} else {
return cljs.core.not_EQ_.call(null,prefix,ys);
}
} else {
return and__4120__auto____$1;
}
} else {
return and__4120__auto__;
}
})()){
return grp1;
} else {
return null;
}
});
expound.alpha.problem_group = (function expound$alpha$problem_group(grp1,grp2){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659),new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),new cljs.core.Keyword(null,"path-prefix","path-prefix",-1210521238),expound.alpha.lcs.call(null,new cljs.core.Keyword(null,"path-prefix","path-prefix",-1210521238).cljs$core$IFn$_invoke$arity$1(grp1),new cljs.core.Keyword(null,"path-prefix","path-prefix",-1210521238).cljs$core$IFn$_invoke$arity$1(grp2)),new cljs.core.Keyword(null,"problems","problems",2097327077),cljs.core.into.call(null,((cljs.core._EQ_.call(null,new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(grp1)))?new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(grp1):new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [grp1], null)),((cljs.core._EQ_.call(null,new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(grp2)))?new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(grp2):new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [grp2], null)))], null);
});
expound.alpha.lift_singleton_groups = (function expound$alpha$lift_singleton_groups(groups){
return clojure.walk.postwalk.call(null,(function (form){
if(((cljs.core.map_QMARK_.call(null,form)) && ((!(cljs.core.sorted_QMARK_.call(null,form)))) && (cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword("expound.problem-group","one-value","expound.problem-group/one-value",-1584327548),null,new cljs.core.Keyword("expound.problem-group","many-values","expound.problem-group/many-values",-397006439),null], null), null),new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(form))) && (cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(form)))))){
return cljs.core.first.call(null,new cljs.core.Keyword(null,"problems","problems",2097327077).cljs$core$IFn$_invoke$arity$1(form));
} else {
return form;
}
}),groups);
});
expound.alpha.remove_vec = (function expound$alpha$remove_vec(v,x){
return cljs.core.vec.call(null,cljs.core.remove.call(null,cljs.core.PersistentHashSet.createAsIfByAssoc([x]),v));
});
expound.alpha.groups = (function expound$alpha$groups(problems){
var grouped_by_in_path = cljs.core.map.call(null,(function (grp){
if(cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,grp))){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659),new cljs.core.Keyword("expound.problem-group","one-value","expound.problem-group/one-value",-1584327548),new cljs.core.Keyword(null,"path-prefix","path-prefix",-1210521238),new cljs.core.Keyword("expound","path","expound/path",-1026376555).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,grp)),new cljs.core.Keyword(null,"problems","problems",2097327077),grp], null);
} else {
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659),new cljs.core.Keyword("expound.problem-group","one-value","expound.problem-group/one-value",-1584327548),new cljs.core.Keyword(null,"path-prefix","path-prefix",-1210521238),cljs.core.apply.call(null,expound.alpha.lcs,cljs.core.map.call(null,new cljs.core.Keyword("expound","path","expound/path",-1026376555),grp)),new cljs.core.Keyword(null,"problems","problems",2097327077),grp], null);
}
}),cljs.core.vals.call(null,cljs.core.group_by.call(null,new cljs.core.Keyword("expound","in","expound/in",-1900412298),problems)));
return expound.alpha.lift_singleton_groups.call(null,cljs.core.reduce.call(null,((function (grouped_by_in_path){
return (function (grps,group){
var temp__5718__auto__ = cljs.core.some.call(null,((function (grouped_by_in_path){
return (function (p1__4495_SHARP_){
return expound.alpha.alternation.call(null,p1__4495_SHARP_,group);
});})(grouped_by_in_path))
,grps);
if(cljs.core.truth_(temp__5718__auto__)){
var old_group = temp__5718__auto__;
return cljs.core.conj.call(null,expound.alpha.remove_vec.call(null,grps,old_group),expound.alpha.problem_group.call(null,old_group,group));
} else {
return cljs.core.conj.call(null,grps,group);
}
});})(grouped_by_in_path))
,cljs.core.PersistentVector.EMPTY,grouped_by_in_path));
});
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","insufficient-input","expound.problem/insufficient-input",1437497436),(function (_type,spec_name,form,path,problems,opts){
var problem = cljs.core.first.call(null,problems);
return expound.printer.format.call(null,"should have additional elements. The next element%s %s",(function (){var temp__5722__auto__ = cljs.core.last.call(null,new cljs.core.Keyword("expound","path","expound/path",-1026376555).cljs$core$IFn$_invoke$arity$1(problem));
if((temp__5722__auto__ == null)){
return "";
} else {
var el_name = temp__5722__auto__;
return [" \"",cljs.core.pr_str.call(null,el_name),"\""].join('');
}
})(),(function (){var failure = null;
var non_matching_value = new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound","value-that-should-never-match","expound/value-that-should-never-match",-232021426)], null);
var problems__$1 = expound.alpha.groups.call(null,cljs.core.map.call(null,((function (failure,non_matching_value,problem){
return (function (p1__4497_SHARP_){
return cljs.core.assoc.call(null,p1__4497_SHARP_,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659),expound.problems.type.call(null,failure,p1__4497_SHARP_));
});})(failure,non_matching_value,problem))
,cljs.core.map.call(null,((function (failure,non_matching_value,problem){
return (function (p1__4496_SHARP_){
return cljs.core.dissoc.call(null,p1__4496_SHARP_,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659),new cljs.core.Keyword(null,"reason","reason",-2070751759));
});})(failure,non_matching_value,problem))
,problems)));
return cljs.core.apply.call(null,cljs.core.str,(function (){var iter__4523__auto__ = ((function (failure,non_matching_value,problems__$1,problem){
return (function expound$alpha$iter__4498(s__4499){
return (new cljs.core.LazySeq(null,((function (failure,non_matching_value,problems__$1,problem){
return (function (){
var s__4499__$1 = s__4499;
while(true){
var temp__5720__auto__ = cljs.core.seq.call(null,s__4499__$1);
if(temp__5720__auto__){
var s__4499__$2 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__4499__$2)){
var c__4521__auto__ = cljs.core.chunk_first.call(null,s__4499__$2);
var size__4522__auto__ = cljs.core.count.call(null,c__4521__auto__);
var b__4501 = cljs.core.chunk_buffer.call(null,size__4522__auto__);
if((function (){var i__4500 = (0);
while(true){
if((i__4500 < size__4522__auto__)){
var prob = cljs.core._nth.call(null,c__4521__auto__,i__4500);
cljs.core.chunk_append.call(null,b__4501,(function (){var in$ = new cljs.core.Keyword("expound","in","expound/in",-1900412298).cljs$core$IFn$_invoke$arity$1(prob);
return expound.alpha.expected_str.call(null,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(prob),new cljs.core.Keyword("expound","no-spec-name","expound/no-spec-name",-718645311),non_matching_value,in$,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [prob], null),opts);
})());

var G__4502 = (i__4500 + (1));
i__4500 = G__4502;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4501),expound$alpha$iter__4498.call(null,cljs.core.chunk_rest.call(null,s__4499__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4501),null);
}
} else {
var prob = cljs.core.first.call(null,s__4499__$2);
return cljs.core.cons.call(null,(function (){var in$ = new cljs.core.Keyword("expound","in","expound/in",-1900412298).cljs$core$IFn$_invoke$arity$1(prob);
return expound.alpha.expected_str.call(null,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(prob),new cljs.core.Keyword("expound","no-spec-name","expound/no-spec-name",-718645311),non_matching_value,in$,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [prob], null),opts);
})(),expound$alpha$iter__4498.call(null,cljs.core.rest.call(null,s__4499__$2)));
}
} else {
return null;
}
break;
}
});})(failure,non_matching_value,problems__$1,problem))
,null,null));
});})(failure,non_matching_value,problems__$1,problem))
;
return iter__4523__auto__.call(null,problems__$1);
})());
})());
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","insufficient-input","expound.problem/insufficient-input",1437497436),(function (type,spec_name,form,path,problems,opts){
return expound.alpha.format_err.call(null,"Syntax error",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","extra-input","expound.problem/extra-input",2043170217),(function (_type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

return "has extra input";
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","extra-input","expound.problem/extra-input",2043170217),(function (type,spec_name,form,path,problems,opts){
return expound.alpha.format_err.call(null,"Syntax error",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","fspec-exception-failure","expound.problem/fspec-exception-failure",-398312942),(function (_type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

var problem = cljs.core.first.call(null,problems);
return expound.printer.format.call(null,"threw exception\n\n%s\n\nwith args:\n\n%s",expound.printer.indent.call(null,((typeof new cljs.core.Keyword(null,"reason","reason",-2070751759).cljs$core$IFn$_invoke$arity$1(problem) === 'string')?["\"",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"reason","reason",-2070751759).cljs$core$IFn$_invoke$arity$1(problem)),"\""].join(''):cljs.core.pr_str.call(null,new cljs.core.Keyword(null,"reason","reason",-2070751759).cljs$core$IFn$_invoke$arity$1(problem)))),expound.printer.indent.call(null,clojure.string.join.call(null,", ",new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(problem))));
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","fspec-exception-failure","expound.problem/fspec-exception-failure",-398312942),(function (type,spec_name,form,path,problems,opts){
return expound.alpha.format_err.call(null,"Exception",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","fspec-ret-failure","expound.problem/fspec-ret-failure",1192937934),(function (_type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

var problem = cljs.core.first.call(null,problems);
return expound.printer.format.call(null,"returned an invalid value\n\n%s\n\n%s",expound.ansi.color.call(null,expound.printer.indent.call(null,cljs.core.pr_str.call(null,new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(problem))),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659)),expound.alpha.predicate_errors.call(null,problems));
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","fspec-ret-failure","expound.problem/fspec-ret-failure",1192937934),(function (type,spec_name,form,path,problems,opts){
return expound.alpha.format_err.call(null,"Function spec failed",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword("expound.problem","insufficient-input","expound.problem/insufficient-input",1437497436),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.show_spec_name.call(null,spec_name,expound.alpha.value_PLUS_conformed_value.call(null,problems,spec_name,form,path,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"show-conformed?","show-conformed?",-1548441572),false], null)));
}));
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword("expound.problem","extra-input","expound.problem/extra-input",2043170217),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.show_spec_name.call(null,spec_name,expound.alpha.value_PLUS_conformed_value.call(null,problems,spec_name,form,path,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"show-conformed?","show-conformed?",-1548441572),false], null)));
}));
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword("expound.problem","fspec-fn-failure","expound.problem/fspec-fn-failure",-814692716),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.show_spec_name.call(null,spec_name,expound.alpha.value_PLUS_conformed_value.call(null,problems,spec_name,form,path,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"show-conformed?","show-conformed?",-1548441572),false], null)));
}));
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword("expound.problem","fspec-exception-failure","expound.problem/fspec-exception-failure",-398312942),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.show_spec_name.call(null,spec_name,expound.alpha.value_PLUS_conformed_value.call(null,problems,spec_name,form,path,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"show-conformed?","show-conformed?",-1548441572),false], null)));
}));
cljs.core._add_method.call(null,expound.alpha.value_str,new cljs.core.Keyword("expound.problem","fspec-ret-failure","expound.problem/fspec-ret-failure",1192937934),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.show_spec_name.call(null,spec_name,expound.alpha.value_PLUS_conformed_value.call(null,problems,spec_name,form,path,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"show-conformed?","show-conformed?",-1548441572),false], null)));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","fspec-fn-failure","expound.problem/fspec-fn-failure",-814692716),(function (_type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

var problem = cljs.core.first.call(null,problems);
return expound.printer.format.call(null,"failed spec. Function arguments and return value\n\n%s\n\nshould satisfy\n\n%s",expound.printer.indent.call(null,expound.ansi.color.call(null,cljs.core.pr_str.call(null,new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(problem)),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659))),expound.printer.indent.call(null,expound.ansi.color.call(null,expound.alpha.pr_pred.call(null,new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem),new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(problem)),new cljs.core.Keyword(null,"good-pred","good-pred",-629085297))));
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","fspec-fn-failure","expound.problem/fspec-fn-failure",-814692716),(function (type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

return expound.alpha.format_err.call(null,"Function spec failed",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","check-fn-failure","expound.problem/check-fn-failure",443478179),(function (_type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

var problem = cljs.core.first.call(null,problems);
return expound.printer.format.call(null,"failed spec. Function arguments and return value\n\n%s\n\nshould satisfy\n\n%s",expound.printer.indent.call(null,expound.ansi.color.call(null,cljs.core.pr_str.call(null,new cljs.core.Keyword(null,"val","val",128701612).cljs$core$IFn$_invoke$arity$1(problem)),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659))),expound.printer.indent.call(null,expound.ansi.color.call(null,expound.alpha.pr_pred.call(null,new cljs.core.Keyword(null,"pred","pred",1927423397).cljs$core$IFn$_invoke$arity$1(problem),new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(problem)),new cljs.core.Keyword(null,"good-pred","good-pred",-629085297))));
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","check-fn-failure","expound.problem/check-fn-failure",443478179),(function (_type,spec_name,form,path,problems,opts){
if(cljs.spec.alpha._STAR_compile_asserts_STAR_){
if(cljs.core.truth_(cljs.core.deref.call(null,new cljs.core.Var(function(){return cljs.spec.alpha._STAR_runtime_asserts_STAR_;},new cljs.core.Symbol("cljs.spec.alpha","*runtime-asserts*","cljs.spec.alpha/*runtime-asserts*",-1060443587,null),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"private","private",-558947994),new cljs.core.Keyword(null,"ns","ns",441598760),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"end-column","end-column",1425389514),new cljs.core.Keyword(null,"column","column",2078222095),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"line","line",212345235),new cljs.core.Keyword(null,"end-line","end-line",1837326455),new cljs.core.Keyword(null,"arglists","arglists",1661989754),new cljs.core.Keyword(null,"doc","doc",1913296891),new cljs.core.Keyword(null,"test","test",577538877)],[true,new cljs.core.Symbol(null,"cljs.spec.alpha","cljs.spec.alpha",505122844,null),new cljs.core.Symbol(null,"*runtime-asserts*","*runtime-asserts*",1632801956,null),"/home/joshua/.cljs/.aot_cache/1.10.520/7973E9B/cljs/spec/alpha.cljs",20,1,true,1477,1479,cljs.core.List.EMPTY,null,((cljs.spec.alpha._STAR_runtime_asserts_STAR_)?cljs.spec.alpha._STAR_runtime_asserts_STAR_.cljs$lang$test:null)]))))){
cljs.spec.alpha.assert_STAR_.call(null,new cljs.core.Keyword("expound.alpha","singleton","expound.alpha/singleton",531848121),problems);
} else {
}
} else {
}

return expound.printer.format.call(null,expound.alpha.format_str,expound.alpha.header_label.call(null,"Function spec failed"),expound.ansi.color.call(null,expound.printer.indent.call(null,cljs.core.pr_str.call(null,new cljs.core.Keyword("expound","check-fn-call","expound/check-fn-call",-300245931).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,problems)))),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659)),expound.alpha.expected_str.call(null,_type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","check-ret-failure","expound.problem/check-ret-failure",1795987483),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.predicate_errors.call(null,problems);
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","check-ret-failure","expound.problem/check-ret-failure",1795987483),(function (_type,spec_name,form,path,problems,opts){
return expound.printer.format.call(null,"%s\n\n%s\n\nreturned an invalid value.\n\n%s\n\n%s",expound.alpha.header_label.call(null,"Function spec failed"),expound.ansi.color.call(null,expound.printer.indent.call(null,cljs.core.pr_str.call(null,new cljs.core.Keyword("expound","check-fn-call","expound/check-fn-call",-300245931).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,problems)))),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659)),expound.alpha._STAR_value_str_fn_STAR_.call(null,spec_name,form,path,expound.problems.value_in.call(null,form,path)),expound.alpha.expected_str.call(null,_type,spec_name,form,path,problems,opts));
}));
cljs.core._add_method.call(null,expound.alpha.expected_str,new cljs.core.Keyword("expound.problem","unknown","expound.problem/unknown",1364832957),(function (_type,spec_name,form,path,problems,opts){
return expound.alpha.predicate_errors.call(null,problems);
}));
cljs.core._add_method.call(null,expound.alpha.problem_group_str,new cljs.core.Keyword("expound.problem","unknown","expound.problem/unknown",1364832957),(function (type,spec_name,form,path,problems,opts){
if(cljs.core.truth_(cljs.core.apply.call(null,cljs.core._EQ_,cljs.core.map.call(null,new cljs.core.Keyword(null,"val","val",128701612),problems)))){
} else {
throw (new Error(["Assert failed: ",[expound.util.assert_message,": All values should be the same, but they are ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(problems)].join(''),"\n","(apply = (map :val problems))"].join('')));
}

return expound.alpha.format_err.call(null,"Spec failed",type,spec_name,form,path,problems,opts,expound.alpha.expected_str.call(null,type,spec_name,form,path,problems,opts));
}));
expound.alpha.instrumentation_info = (function expound$alpha$instrumentation_info(failure,caller){
if(cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"instrument","instrument",-960698844),failure)){
return expound.printer.format.call(null,"%s:%s\n\n",new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$2(caller,"<filename missing>"),new cljs.core.Keyword(null,"line","line",212345235).cljs$core$IFn$_invoke$arity$2(caller,"<line number missing>"));
} else {
return "";
}
});
expound.alpha.spec_name = (function expound$alpha$spec_name(ed){
if(cljs.core.truth_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"instrument","instrument",-960698844),null], null), null).call(null,new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592).cljs$core$IFn$_invoke$arity$1(ed)))){
if(cljs.core.truth_(new cljs.core.Keyword("cljs.spec.alpha","args","cljs.spec.alpha/args",1870769783).cljs$core$IFn$_invoke$arity$1(ed))){
return new cljs.core.Keyword(null,"args","args",1315556576);
} else {
return cljs.core.first.call(null,new cljs.core.Keyword(null,"path","path",-188191168).cljs$core$IFn$_invoke$arity$1(cljs.core.first.call(null,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814).cljs$core$IFn$_invoke$arity$1(ed))));

}
} else {
return null;
}
});
expound.alpha.print_explain_data = (function expound$alpha$print_explain_data(opts,explain_data){
if(cljs.core.not.call(null,explain_data)){
return "Success!\n";
} else {
var explain_data_SINGLEQUOTE_ = expound.problems.annotate.call(null,explain_data);
var map__4503 = explain_data_SINGLEQUOTE_;
var map__4503__$1 = (((((!((map__4503 == null))))?(((((map__4503.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4503.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4503):map__4503);
var caller = cljs.core.get.call(null,map__4503__$1,new cljs.core.Keyword("expound","caller","expound/caller",-503638870));
var form = cljs.core.get.call(null,map__4503__$1,new cljs.core.Keyword("expound","form","expound/form",-264680632));
var failure = cljs.core.get.call(null,map__4503__$1,new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592));
var problems = expound.alpha.groups.call(null,new cljs.core.Keyword("expound","problems","expound/problems",1257773984).cljs$core$IFn$_invoke$arity$1(explain_data_SINGLEQUOTE_));
return expound.printer.no_trailing_whitespace.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.ansi.color.call(null,expound.alpha.instrumentation_info.call(null,failure,caller),new cljs.core.Keyword(null,"none","none",1333468478))),cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.printer.format.call(null,"%s%s\n%s %s %s\n",cljs.core.apply.call(null,cljs.core.str,(function (){var iter__4523__auto__ = ((function (explain_data_SINGLEQUOTE_,map__4503,map__4503__$1,caller,form,failure,problems){
return (function expound$alpha$print_explain_data_$_iter__4509(s__4510){
return (new cljs.core.LazySeq(null,((function (explain_data_SINGLEQUOTE_,map__4503,map__4503__$1,caller,form,failure,problems){
return (function (){
var s__4510__$1 = s__4510;
while(true){
var temp__5720__auto__ = cljs.core.seq.call(null,s__4510__$1);
if(temp__5720__auto__){
var s__4510__$2 = temp__5720__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__4510__$2)){
var c__4521__auto__ = cljs.core.chunk_first.call(null,s__4510__$2);
var size__4522__auto__ = cljs.core.count.call(null,c__4521__auto__);
var b__4512 = cljs.core.chunk_buffer.call(null,size__4522__auto__);
if((function (){var i__4511 = (0);
while(true){
if((i__4511 < size__4522__auto__)){
var prob = cljs.core._nth.call(null,c__4521__auto__,i__4511);
cljs.core.chunk_append.call(null,b__4512,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.alpha.problem_group_str.call(null,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(prob),expound.alpha.spec_name.call(null,explain_data_SINGLEQUOTE_),form,new cljs.core.Keyword("expound","in","expound/in",-1900412298).cljs$core$IFn$_invoke$arity$1(prob),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [prob], null),opts)),"\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var s = (cljs.core.truth_(new cljs.core.Keyword(null,"print-specs?","print-specs?",146397677).cljs$core$IFn$_invoke$arity$1(opts))?expound.alpha.relevant_specs.call(null,new cljs.core.Keyword("expound","problems","expound/problems",1257773984).cljs$core$IFn$_invoke$arity$1(explain_data_SINGLEQUOTE_)):"");
if(cljs.core.empty_QMARK_.call(null,s)){
return s;
} else {
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(s),"\n\n"].join('');
}
})())].join(''));

var G__4513 = (i__4511 + (1));
i__4511 = G__4513;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4512),expound$alpha$print_explain_data_$_iter__4509.call(null,cljs.core.chunk_rest.call(null,s__4510__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__4512),null);
}
} else {
var prob = cljs.core.first.call(null,s__4510__$2);
return cljs.core.cons.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.alpha.problem_group_str.call(null,new cljs.core.Keyword("expound.spec.problem","type","expound.spec.problem/type",-862044659).cljs$core$IFn$_invoke$arity$1(prob),expound.alpha.spec_name.call(null,explain_data_SINGLEQUOTE_),form,new cljs.core.Keyword("expound","in","expound/in",-1900412298).cljs$core$IFn$_invoke$arity$1(prob),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [prob], null),opts)),"\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var s = (cljs.core.truth_(new cljs.core.Keyword(null,"print-specs?","print-specs?",146397677).cljs$core$IFn$_invoke$arity$1(opts))?expound.alpha.relevant_specs.call(null,new cljs.core.Keyword("expound","problems","expound/problems",1257773984).cljs$core$IFn$_invoke$arity$1(explain_data_SINGLEQUOTE_)):"");
if(cljs.core.empty_QMARK_.call(null,s)){
return s;
} else {
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(s),"\n\n"].join('');
}
})())].join(''),expound$alpha$print_explain_data_$_iter__4509.call(null,cljs.core.rest.call(null,s__4510__$2)));
}
} else {
return null;
}
break;
}
});})(explain_data_SINGLEQUOTE_,map__4503,map__4503__$1,caller,form,failure,problems))
,null,null));
});})(explain_data_SINGLEQUOTE_,map__4503,map__4503__$1,caller,form,failure,problems))
;
return iter__4523__auto__.call(null,problems);
})()),expound.ansi.color.call(null,expound.alpha.section_label.call(null),new cljs.core.Keyword(null,"footer","footer",1606445390)),expound.ansi.color.call(null,"Detected",new cljs.core.Keyword(null,"footer","footer",1606445390)),expound.ansi.color.call(null,cljs.core.count.call(null,problems),new cljs.core.Keyword(null,"footer","footer",1606445390)),expound.ansi.color.call(null,((cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,problems)))?"error":"errors"),new cljs.core.Keyword(null,"footer","footer",1606445390))))].join(''));
}
});
expound.alpha.minimal_fspec = (function expound$alpha$minimal_fspec(form){
var fspec_sp = cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"sym","sym",-1444860305),new cljs.core.Keyword(null,"args","args",1315556576)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.qualified_symbol_QMARK_,cljs.spec.alpha.rep_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null"], null), null),new cljs.core.Keyword(null,"v","v",21465059),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Keyword(null,"v","v",21465059)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"args","args",1315556576),null,new cljs.core.Keyword(null,"ret","ret",-468222814),null,new cljs.core.Keyword(null,"fn","fn",-1175266204),null], null), null),cljs.core.any_QMARK_], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null"], null), null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)], null)))], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","qualified-symbol?","cljs.core/qualified-symbol?",1570873476,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","*","cljs.spec.alpha/*",-1238084288,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"args","args",1315556576),"null",new cljs.core.Keyword(null,"ret","ret",-468222814),"null",new cljs.core.Keyword(null,"fn","fn",-1175266204),"null"], null), null),new cljs.core.Keyword(null,"v","v",21465059),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)))], null));
return cljs.spec.alpha.unform.call(null,fspec_sp,cljs.core.update.call(null,cljs.spec.alpha.conform.call(null,fspec_sp,form),new cljs.core.Keyword(null,"args","args",1315556576),((function (fspec_sp){
return (function (args){
return cljs.core.filter.call(null,((function (fspec_sp){
return (function (p1__4514_SHARP_){
return (!((new cljs.core.Keyword(null,"v","v",21465059).cljs$core$IFn$_invoke$arity$1(p1__4514_SHARP_) == null)));
});})(fspec_sp))
,args);
});})(fspec_sp))
));
});
expound.alpha.print_check_result = (function expound$alpha$print_check_result(check_result){
var map__4516 = check_result;
var map__4516__$1 = (((((!((map__4516 == null))))?(((((map__4516.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__4516.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__4516):map__4516);
var sym = cljs.core.get.call(null,map__4516__$1,new cljs.core.Keyword(null,"sym","sym",-1444860305),new cljs.core.Symbol(null,"<unknown>","<unknown>",868184816,null));
var spec = cljs.core.get.call(null,map__4516__$1,new cljs.core.Keyword(null,"spec","spec",347520401));
var failure = cljs.core.get.call(null,map__4516__$1,new cljs.core.Keyword(null,"failure","failure",720415879));
var ret = new cljs.core.Keyword("clojure.test.check","ret","clojure.test.check/ret",1393978960).cljs$core$IFn$_invoke$arity$1(check_result);
var explain_data = cljs.core.ex_data.call(null,failure);
var bad_args = (function (){var or__4131__auto__ = new cljs.core.Keyword("cljs.spec.test.alpha","args","cljs.spec.test.alpha/args",78409593).cljs$core$IFn$_invoke$arity$1(explain_data);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return cljs.core.first.call(null,new cljs.core.Keyword(null,"fail","fail",1706214930).cljs$core$IFn$_invoke$arity$1(ret));
}
})();
var failure_reason = new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592).cljs$core$IFn$_invoke$arity$1(explain_data);
var sym__$1 = (function (){var or__4131__auto__ = sym;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return new cljs.core.Symbol(null,"<unknown>","<unknown>",868184816,null);
}
})();
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.alpha.label.call(null,expound.alpha.check_header_size,["Checked ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(sym__$1)].join(''),"=")),"\n\n",(cljs.core.truth_((function (){var and__4120__auto__ = failure;
if(cljs.core.truth_(and__4120__auto__)){
return cljs.core.re_matches.call(null,/Unable to construct gen at.*/,failure.message);
} else {
return and__4120__auto__;
}
})())?(function (){var path = new cljs.core.Keyword("cljs.spec.alpha","path","cljs.spec.alpha/path",-1788851481).cljs$core$IFn$_invoke$arity$1(explain_data);
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(failure.message)," in\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.printer.indent.call(null,cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.spec.alpha.form.call(null,new cljs.core.Keyword(null,"args","args",1315556576).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"spec","spec",347520401).cljs$core$IFn$_invoke$arity$1(check_result)))))),"\n"].join('');
})():((cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"no-args-spec","no-args-spec",-1769472786),failure_reason))?["Failed to check function.\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.ansi.color.call(null,expound.printer.indent.call(null,expound.printer.pprint_str.call(null,expound.alpha.minimal_fspec.call(null,cljs.spec.alpha.form.call(null,spec)))),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659))),"\n\nshould contain an :args spec\n"].join(''):((cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"no-fn","no-fn",-353517111),failure_reason))?(((!((sym__$1 == null))))?["Failed to check function.\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.ansi.color.call(null,expound.printer.indent.call(null,cljs.core.pr_str.call(null,sym__$1)),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659))),"\n\nis not defined\n"].join(''):"Cannot check undefined function\n"):(cljs.core.truth_((function (){var and__4120__auto__ = explain_data;
if(cljs.core.truth_(and__4120__auto__)){
return cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"check-failed","check-failed",-1316157547),new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592).cljs$core$IFn$_invoke$arity$1(explain_data));
} else {
return and__4120__auto__;
}
})())?(function (){var sb__4661__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__4522_4526 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__4523_4527 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__4524_4528 = true;
var _STAR_print_fn_STAR__temp_val__4525_4529 = ((function (_STAR_print_newline_STAR__orig_val__4522_4526,_STAR_print_fn_STAR__orig_val__4523_4527,_STAR_print_newline_STAR__temp_val__4524_4528,sb__4661__auto__,map__4516,map__4516__$1,sym,spec,failure,ret,explain_data,bad_args,failure_reason,sym__$1){
return (function (x__4662__auto__){
return sb__4661__auto__.append(x__4662__auto__);
});})(_STAR_print_newline_STAR__orig_val__4522_4526,_STAR_print_fn_STAR__orig_val__4523_4527,_STAR_print_newline_STAR__temp_val__4524_4528,sb__4661__auto__,map__4516,map__4516__$1,sym,spec,failure,ret,explain_data,bad_args,failure_reason,sym__$1))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__4524_4528;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__4525_4529;

try{cljs.spec.alpha._STAR_explain_out_STAR_.call(null,cljs.core.update.call(null,explain_data,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),((function (_STAR_print_newline_STAR__orig_val__4522_4526,_STAR_print_fn_STAR__orig_val__4523_4527,_STAR_print_newline_STAR__temp_val__4524_4528,_STAR_print_fn_STAR__temp_val__4525_4529,sb__4661__auto__,map__4516,map__4516__$1,sym,spec,failure,ret,explain_data,bad_args,failure_reason,sym__$1){
return (function (p1__4515_SHARP_){
return cljs.core.map.call(null,((function (_STAR_print_newline_STAR__orig_val__4522_4526,_STAR_print_fn_STAR__orig_val__4523_4527,_STAR_print_newline_STAR__temp_val__4524_4528,_STAR_print_fn_STAR__temp_val__4525_4529,sb__4661__auto__,map__4516,map__4516__$1,sym,spec,failure,ret,explain_data,bad_args,failure_reason,sym__$1){
return (function (p){
return cljs.core.assoc.call(null,p,new cljs.core.Keyword("expound","check-fn-call","expound/check-fn-call",-300245931),cljs.core.concat.call(null,(new cljs.core.List(null,sym__$1,null,(1),null)),bad_args));
});})(_STAR_print_newline_STAR__orig_val__4522_4526,_STAR_print_fn_STAR__orig_val__4523_4527,_STAR_print_newline_STAR__temp_val__4524_4528,_STAR_print_fn_STAR__temp_val__4525_4529,sb__4661__auto__,map__4516,map__4516__$1,sym,spec,failure,ret,explain_data,bad_args,failure_reason,sym__$1))
,p1__4515_SHARP_);
});})(_STAR_print_newline_STAR__orig_val__4522_4526,_STAR_print_fn_STAR__orig_val__4523_4527,_STAR_print_newline_STAR__temp_val__4524_4528,_STAR_print_fn_STAR__temp_val__4525_4529,sb__4661__auto__,map__4516,map__4516__$1,sym,spec,failure,ret,explain_data,bad_args,failure_reason,sym__$1))
));
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__4523_4527;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__4522_4526;
}
return cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__4661__auto__);
})():(cljs.core.truth_(failure)?[cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.ansi.color.call(null,expound.printer.indent.call(null,expound.printer.pprint_str.call(null,cljs.core.concat.call(null,(new cljs.core.List(null,sym__$1,null,(1),null)),bad_args))),new cljs.core.Keyword(null,"bad-value","bad-value",-139100659))),"\n\n threw error\n\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1(expound.printer.pprint_str.call(null,failure))].join(''):"Success!\n"
)))))].join('');
});
expound.alpha.explain_data_QMARK_ = (function expound$alpha$explain_data_QMARK_(data){
return cljs.spec.alpha.valid_QMARK_.call(null,cljs.spec.alpha.map_spec_impl.call(null,cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.Keyword(null,"gfn","gfn",791517474),new cljs.core.Keyword(null,"pred-exprs","pred-exprs",1792271395),new cljs.core.Keyword(null,"keys-pred","keys-pred",858984739),new cljs.core.Keyword(null,"opt-keys","opt-keys",1262688261),new cljs.core.Keyword(null,"req-specs","req-specs",553962313),new cljs.core.Keyword(null,"req","req",-326448303),new cljs.core.Keyword(null,"req-keys","req-keys",514319221),new cljs.core.Keyword(null,"opt-specs","opt-specs",-384905450),new cljs.core.Keyword(null,"pred-forms","pred-forms",172611832),new cljs.core.Keyword(null,"opt","opt",-794706369)],[null,null,null,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (G__4530){
return cljs.core.map_QMARK_.call(null,G__4530);
}),(function (G__4530){
return cljs.core.contains_QMARK_.call(null,G__4530,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814));
}),(function (G__4530){
return cljs.core.contains_QMARK_.call(null,G__4530,new cljs.core.Keyword("cljs.spec.alpha","spec","cljs.spec.alpha/spec",1947137578));
}),(function (G__4530){
return cljs.core.contains_QMARK_.call(null,G__4530,new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274));
})], null),(function (G__4530){
return ((cljs.core.map_QMARK_.call(null,G__4530)) && (cljs.core.contains_QMARK_.call(null,G__4530,new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814))) && (cljs.core.contains_QMARK_.call(null,G__4530,new cljs.core.Keyword("cljs.spec.alpha","spec","cljs.spec.alpha/spec",1947137578))) && (cljs.core.contains_QMARK_.call(null,G__4530,new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274))));
}),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),new cljs.core.Keyword("cljs.spec.alpha","spec","cljs.spec.alpha/spec",1947137578),new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),new cljs.core.Keyword("cljs.spec.alpha","spec","cljs.spec.alpha/spec",1947137578),new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814),new cljs.core.Keyword("cljs.spec.alpha","spec","cljs.spec.alpha/spec",1947137578),new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Symbol(null,"%","%",-950237169,null))),cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","contains?","cljs.core/contains?",-976526835,null),new cljs.core.Symbol(null,"%","%",-950237169,null),new cljs.core.Keyword("cljs.spec.alpha","problems","cljs.spec.alpha/problems",447400814))),cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","contains?","cljs.core/contains?",-976526835,null),new cljs.core.Symbol(null,"%","%",-950237169,null),new cljs.core.Keyword("cljs.spec.alpha","spec","cljs.spec.alpha/spec",1947137578))),cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","contains?","cljs.core/contains?",-976526835,null),new cljs.core.Symbol(null,"%","%",-950237169,null),new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274)))], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.spec.alpha","failure","cljs.spec.alpha/failure",188258592)], null)])),data);
});
expound.alpha.check_result_QMARK_ = (function expound$alpha$check_result_QMARK_(data){
return cljs.spec.alpha.valid_QMARK_.call(null,cljs.spec.alpha.map_spec_impl.call(null,cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.Keyword(null,"gfn","gfn",791517474),new cljs.core.Keyword(null,"pred-exprs","pred-exprs",1792271395),new cljs.core.Keyword(null,"keys-pred","keys-pred",858984739),new cljs.core.Keyword(null,"opt-keys","opt-keys",1262688261),new cljs.core.Keyword(null,"req-specs","req-specs",553962313),new cljs.core.Keyword(null,"req","req",-326448303),new cljs.core.Keyword(null,"req-keys","req-keys",514319221),new cljs.core.Keyword(null,"opt-specs","opt-specs",-384905450),new cljs.core.Keyword(null,"pred-forms","pred-forms",172611832),new cljs.core.Keyword(null,"opt","opt",-794706369)],[new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.alpha","spec","expound.alpha/spec",999405232)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.alpha","sym","expound.alpha/sym",1887308696),new cljs.core.Keyword("expound.alpha","failure","expound.alpha/failure",1137826194),new cljs.core.Keyword("clojure.spec.test.check","ret","clojure.spec.test.check/ret",-1173350899)], null),null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (G__4531){
return cljs.core.map_QMARK_.call(null,G__4531);
}),(function (G__4531){
return cljs.core.contains_QMARK_.call(null,G__4531,new cljs.core.Keyword(null,"spec","spec",347520401));
})], null),(function (G__4531){
return ((cljs.core.map_QMARK_.call(null,G__4531)) && (cljs.core.contains_QMARK_.call(null,G__4531,new cljs.core.Keyword(null,"spec","spec",347520401))));
}),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"sym","sym",-1444860305),new cljs.core.Keyword(null,"failure","failure",720415879),new cljs.core.Keyword(null,"ret","ret",-468222814)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.alpha","spec","expound.alpha/spec",999405232)], null),null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.alpha","sym","expound.alpha/sym",1887308696),new cljs.core.Keyword("expound.alpha","failure","expound.alpha/failure",1137826194),new cljs.core.Keyword("clojure.spec.test.check","ret","clojure.spec.test.check/ret",-1173350899)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Symbol(null,"%","%",-950237169,null))),cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","contains?","cljs.core/contains?",-976526835,null),new cljs.core.Symbol(null,"%","%",-950237169,null),new cljs.core.Keyword(null,"spec","spec",347520401)))], null),null])),data);
});
expound.alpha.printer_str = (function expound$alpha$printer_str(opts,data){
var opts_SINGLEQUOTE_ = cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"show-valid-values?","show-valid-values?",-587258094),false,new cljs.core.Keyword(null,"print-specs?","print-specs?",146397677),true], null),opts);
var enable_color_QMARK_ = ((cljs.core.not_EQ_.call(null,new cljs.core.Keyword(null,"none","none",1333468478),cljs.core.get.call(null,opts,new cljs.core.Keyword(null,"theme","theme",-1247880880),new cljs.core.Keyword(null,"none","none",1333468478)))) || (expound.ansi._STAR_enable_color_STAR_));
var _STAR_value_str_fn_STAR__orig_val__4532 = expound.alpha._STAR_value_str_fn_STAR_;
var _STAR_enable_color_STAR__orig_val__4533 = expound.ansi._STAR_enable_color_STAR_;
var _STAR_print_styles_STAR__orig_val__4534 = expound.ansi._STAR_print_styles_STAR_;
var _STAR_value_str_fn_STAR__temp_val__4535 = cljs.core.get.call(null,opts,new cljs.core.Keyword(null,"value-str-fn","value-str-fn",1124137860),cljs.core.partial.call(null,expound.alpha.value_in_context,opts_SINGLEQUOTE_));
var _STAR_enable_color_STAR__temp_val__4536 = enable_color_QMARK_;
var _STAR_print_styles_STAR__temp_val__4537 = (function (){var G__4538 = cljs.core.get.call(null,opts,new cljs.core.Keyword(null,"theme","theme",-1247880880),((enable_color_QMARK_)?new cljs.core.Keyword(null,"figwheel-theme","figwheel-theme",1505227343):new cljs.core.Keyword(null,"none","none",1333468478)));
var G__4538__$1 = (((G__4538 instanceof cljs.core.Keyword))?G__4538.fqn:null);
switch (G__4538__$1) {
case "figwheel-theme":
return expound.alpha.figwheel_theme;

break;
case "none":
return cljs.core.PersistentArrayMap.EMPTY;

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__4538__$1)].join('')));

}
})();
expound.alpha._STAR_value_str_fn_STAR_ = _STAR_value_str_fn_STAR__temp_val__4535;

expound.ansi._STAR_enable_color_STAR_ = _STAR_enable_color_STAR__temp_val__4536;

expound.ansi._STAR_print_styles_STAR_ = _STAR_print_styles_STAR__temp_val__4537;

try{if(((expound.alpha.explain_data_QMARK_.call(null,data)) || ((data == null)))){
return expound.alpha.print_explain_data.call(null,opts_SINGLEQUOTE_,data);
} else {
if(expound.alpha.check_result_QMARK_.call(null,data)){
return expound.alpha.print_check_result.call(null,data);
} else {
throw cljs.core.ex_info.call(null,"Unknown data:\n\n",new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),data], null));

}
}
}finally {expound.ansi._STAR_print_styles_STAR_ = _STAR_print_styles_STAR__orig_val__4534;

expound.ansi._STAR_enable_color_STAR_ = _STAR_enable_color_STAR__orig_val__4533;

expound.alpha._STAR_value_str_fn_STAR_ = _STAR_value_str_fn_STAR__orig_val__4532;
}});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","error-message","expound.alpha/error-message",596661929,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null)),new cljs.core.Keyword(null,"ret","ret",-468222814),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null))),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"k","k",-2146297393)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.qualified_keyword_QMARK_], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null)),cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),cljs.spec.alpha.nilable_impl.call(null,new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),cljs.core.string_QMARK_,null),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),null,null,null));
/**
 * Given a spec named `k`, return its human-readable error message.
 */
expound.alpha.error_message = (function expound$alpha$error_message(k){
return cljs.core.get.call(null,cljs.core.deref.call(null,expound.alpha.registry_ref),k);
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","custom-printer","expound.alpha/custom-printer",2045191946,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"opts","opts",155075701),new cljs.core.Keyword("expound.printer","opts","expound.printer/opts",785498940)),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","ifn?","cljs.core/ifn?",1573873861,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"opts","opts",155075701),new cljs.core.Keyword("expound.printer","opts","expound.printer/opts",785498940)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"opts","opts",155075701)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.printer","opts","expound.printer/opts",785498940)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.printer","opts","expound.printer/opts",785498940)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"opts","opts",155075701),new cljs.core.Keyword("expound.printer","opts","expound.printer/opts",785498940)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","ifn?","cljs.core/ifn?",1573873861,null),cljs.core.ifn_QMARK_,null,null),new cljs.core.Symbol("cljs.core","ifn?","cljs.core/ifn?",1573873861,null),null,null,null));
/**
 * Returns a printer.
 * 
 *   Options:
 * - `:show-valid-values?` - if `false`, replaces valid values with "..."
 * - `:value-str-fn`       - function to print bad values
 * - `:print-specs?`       - if `true`, display "Relevant specs" section. Otherwise, omit that section.
 * - `:theme`               - enables color theme. Possible values: `:figwheel-theme`, `:none`
 */
expound.alpha.custom_printer = (function expound$alpha$custom_printer(opts){
return (function (explain_data){
return cljs.core.print.call(null,expound.alpha.printer_str.call(null,opts,explain_data));
});
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","printer","expound.alpha/printer",-1055631074,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"explain-data","explain-data",-1124944340),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"explain-data","explain-data",-1124944340),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"explain-data","explain-data",-1124944340)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.map_QMARK_], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"explain-data","explain-data",-1124944340),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),cljs.core.nil_QMARK_,null,null),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),null,null,null));
/**
 * Prints `explain-data` in a human-readable format.
 */
expound.alpha.printer = (function expound$alpha$printer(explain_data){
return expound.alpha.custom_printer.call(null,cljs.core.PersistentArrayMap.EMPTY).call(null,explain_data);
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","expound-str","expound.alpha/expound-str",-1476944198,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"form","form",-1624062471)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),cljs.core.any_QMARK_], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),cljs.core.string_QMARK_,null,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),null,null,null));
/**
 * Given a `spec` and a `form`, either returns success message or a human-readable error message.
 */
expound.alpha.expound_str = (function expound$alpha$expound_str(spec,form){
var explain_data = cljs.spec.alpha.explain_data.call(null,spec,form);
return expound.alpha.printer_str.call(null,cljs.core.PersistentArrayMap.EMPTY,(cljs.core.truth_(explain_data)?cljs.core.assoc.call(null,explain_data,new cljs.core.Keyword("cljs.spec.alpha","value","cljs.spec.alpha/value",1974786274),form):null));
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","expound","expound.alpha/expound",1096575731,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"form","form",-1624062471)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),cljs.core.any_QMARK_], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword("expound.spec","spec","expound.spec/spec",-184988511),new cljs.core.Keyword(null,"form","form",-1624062471),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),cljs.core.nil_QMARK_,null,null),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),null,null,null));
/**
 * Given a `spec` and a `form`, either prints a success message or a human-readable error message.
 */
expound.alpha.expound = (function expound$alpha$expound(spec,form){
return cljs.core.print.call(null,expound.alpha.expound_str.call(null,spec,form));
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","defmsg","expound.alpha/defmsg",-1469554987,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null),new cljs.core.Keyword(null,"error-message","error-message",1756021561),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null),new cljs.core.Keyword(null,"error-message","error-message",1756021561),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Keyword(null,"error-message","error-message",1756021561)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.qualified_keyword_QMARK_,cljs.core.string_QMARK_], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"k","k",-2146297393),new cljs.core.Symbol("cljs.core","qualified-keyword?","cljs.core/qualified-keyword?",-308091478,null),new cljs.core.Keyword(null,"error-message","error-message",1756021561),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),cljs.core.nil_QMARK_,null,null),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),null,null,null));
/**
 * Associates the spec named `k` with `error-message`.
 */
expound.alpha.defmsg = (function expound$alpha$defmsg(k,error_message){
cljs.core.swap_BANG_.call(null,expound.alpha.registry_ref,cljs.core.assoc,k,error_message);

return null;
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","explain-result","expound.alpha/explain-result",-1675766338,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-result","check-result",164617515),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-result","check-result",164617515),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"check-result","check-result",164617515)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.nilable_impl.call(null,new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),cljs.core.map_QMARK_,null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-result","check-result",164617515),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),cljs.core.nil_QMARK_,null,null),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),null,null,null));
/**
 * Given a result from `clojure.spec.test.alpha/check`, prints a summary of the result.
 */
expound.alpha.explain_result = (function expound$alpha$explain_result(check_result){
if(cljs.core._EQ_.call(null,cljs.spec.alpha._STAR_explain_out_STAR_,cljs.spec.alpha.explain_printer)){
throw cljs.core.ex_info.call(null,"Cannot print check results with default printer. Use 'set!' or 'binding' to use Expound printer.",cljs.core.PersistentArrayMap.EMPTY);
} else {
}

return cljs.spec.alpha._STAR_explain_out_STAR_.call(null,check_result);
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","explain-result-str","expound.alpha/explain-result-str",-1526943386,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-result","check-result",164617515),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-result","check-result",164617515),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"check-result","check-result",164617515)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.nilable_impl.call(null,new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),cljs.core.map_QMARK_,null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-result","check-result",164617515),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null))),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),cljs.core.string_QMARK_,null,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),null,null,null));
/**
 * Given a result from `clojure.spec.test.alpha/check`, returns a string summarizing the result.
 */
expound.alpha.explain_result_str = (function expound$alpha$explain_result_str(check_result){
var sb__4661__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__4540_4544 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__4541_4545 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__4542_4546 = true;
var _STAR_print_fn_STAR__temp_val__4543_4547 = ((function (_STAR_print_newline_STAR__orig_val__4540_4544,_STAR_print_fn_STAR__orig_val__4541_4545,_STAR_print_newline_STAR__temp_val__4542_4546,sb__4661__auto__){
return (function (x__4662__auto__){
return sb__4661__auto__.append(x__4662__auto__);
});})(_STAR_print_newline_STAR__orig_val__4540_4544,_STAR_print_fn_STAR__orig_val__4541_4545,_STAR_print_newline_STAR__temp_val__4542_4546,sb__4661__auto__))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__4542_4546;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__4543_4547;

try{expound.alpha.explain_result.call(null,check_result);
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__4541_4545;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__4540_4544;
}
return cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__4661__auto__);
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","explain-results","expound.alpha/explain-results",854308104,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-results","check-results",1484458047),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-results","check-results",1484458047),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"check-results","check-results",1484458047)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.every_impl.call(null,cljs.core.list(new cljs.core.Symbol("s","nilable","s/nilable",-812128520,null),new cljs.core.Symbol(null,"map?","map?",-1780568534,null)),cljs.spec.alpha.nilable_impl.call(null,new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),cljs.core.map_QMARK_,null),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword("cljs.spec.alpha","kind-form","cljs.spec.alpha/kind-form",-1047104697),null,new cljs.core.Keyword("cljs.spec.alpha","cpred","cljs.spec.alpha/cpred",-693471218),(function (G__4548){
return cljs.core.coll_QMARK_.call(null,G__4548);
}),new cljs.core.Keyword("cljs.spec.alpha","conform-all","cljs.spec.alpha/conform-all",45201917),true,new cljs.core.Keyword("cljs.spec.alpha","describe","cljs.spec.alpha/describe",1883026911),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))], null),null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-results","check-results",1484458047),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),cljs.core.nil_QMARK_,null,null),new cljs.core.Symbol("cljs.core","nil?","cljs.core/nil?",945071861,null),null,null,null));
/**
 * Given a sequence of results from `clojure.spec.test.alpha/check`, prints a summary of the results.
 */
expound.alpha.explain_results = (function expound$alpha$explain_results(check_results){
var seq__4549_4553 = cljs.core.seq.call(null,cljs.core.butlast.call(null,check_results));
var chunk__4550_4554 = null;
var count__4551_4555 = (0);
var i__4552_4556 = (0);
while(true){
if((i__4552_4556 < count__4551_4555)){
var check_result_4557 = cljs.core._nth.call(null,chunk__4550_4554,i__4552_4556);
expound.alpha.explain_result.call(null,check_result_4557);

cljs.core.print.call(null,"\n\n");


var G__4558 = seq__4549_4553;
var G__4559 = chunk__4550_4554;
var G__4560 = count__4551_4555;
var G__4561 = (i__4552_4556 + (1));
seq__4549_4553 = G__4558;
chunk__4550_4554 = G__4559;
count__4551_4555 = G__4560;
i__4552_4556 = G__4561;
continue;
} else {
var temp__5720__auto___4562 = cljs.core.seq.call(null,seq__4549_4553);
if(temp__5720__auto___4562){
var seq__4549_4563__$1 = temp__5720__auto___4562;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__4549_4563__$1)){
var c__4550__auto___4564 = cljs.core.chunk_first.call(null,seq__4549_4563__$1);
var G__4565 = cljs.core.chunk_rest.call(null,seq__4549_4563__$1);
var G__4566 = c__4550__auto___4564;
var G__4567 = cljs.core.count.call(null,c__4550__auto___4564);
var G__4568 = (0);
seq__4549_4553 = G__4565;
chunk__4550_4554 = G__4566;
count__4551_4555 = G__4567;
i__4552_4556 = G__4568;
continue;
} else {
var check_result_4569 = cljs.core.first.call(null,seq__4549_4563__$1);
expound.alpha.explain_result.call(null,check_result_4569);

cljs.core.print.call(null,"\n\n");


var G__4570 = cljs.core.next.call(null,seq__4549_4563__$1);
var G__4571 = null;
var G__4572 = (0);
var G__4573 = (0);
seq__4549_4553 = G__4570;
chunk__4550_4554 = G__4571;
count__4551_4555 = G__4572;
i__4552_4556 = G__4573;
continue;
}
} else {
}
}
break;
}

return expound.alpha.explain_result.call(null,cljs.core.last.call(null,check_results));
});
cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("expound.alpha","explain-results-str","expound.alpha/explain-results-str",-1024157844,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-results","check-results",1484458047),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-results","check-results",1484458047),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"check-results","check-results",1484458047)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.every_impl.call(null,cljs.core.list(new cljs.core.Symbol("s","nilable","s/nilable",-812128520,null),new cljs.core.Symbol(null,"map?","map?",-1780568534,null)),cljs.spec.alpha.nilable_impl.call(null,new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),cljs.core.map_QMARK_,null),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword("cljs.spec.alpha","kind-form","cljs.spec.alpha/kind-form",-1047104697),null,new cljs.core.Keyword("cljs.spec.alpha","cpred","cljs.spec.alpha/cpred",-693471218),(function (G__4574){
return cljs.core.coll_QMARK_.call(null,G__4574);
}),new cljs.core.Keyword("cljs.spec.alpha","conform-all","cljs.spec.alpha/conform-all",45201917),true,new cljs.core.Keyword("cljs.spec.alpha","describe","cljs.spec.alpha/describe",1883026911),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))], null),null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"check-results","check-results",1484458047),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","nilable","cljs.spec.alpha/nilable",1628308748,null),new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null)))),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),cljs.core.string_QMARK_,null,null),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),null,null,null));
/**
 * Given a sequence of results from `clojure.spec.test.alpha/check`, returns a string summarizing the results.
 */
expound.alpha.explain_results_str = (function expound$alpha$explain_results_str(check_results){
var sb__4661__auto__ = (new goog.string.StringBuffer());
var _STAR_print_newline_STAR__orig_val__4575_4579 = cljs.core._STAR_print_newline_STAR_;
var _STAR_print_fn_STAR__orig_val__4576_4580 = cljs.core._STAR_print_fn_STAR_;
var _STAR_print_newline_STAR__temp_val__4577_4581 = true;
var _STAR_print_fn_STAR__temp_val__4578_4582 = ((function (_STAR_print_newline_STAR__orig_val__4575_4579,_STAR_print_fn_STAR__orig_val__4576_4580,_STAR_print_newline_STAR__temp_val__4577_4581,sb__4661__auto__){
return (function (x__4662__auto__){
return sb__4661__auto__.append(x__4662__auto__);
});})(_STAR_print_newline_STAR__orig_val__4575_4579,_STAR_print_fn_STAR__orig_val__4576_4580,_STAR_print_newline_STAR__temp_val__4577_4581,sb__4661__auto__))
;
cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__temp_val__4577_4581;

cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__temp_val__4578_4582;

try{expound.alpha.explain_results.call(null,check_results);
}finally {cljs.core._STAR_print_fn_STAR_ = _STAR_print_fn_STAR__orig_val__4576_4580;

cljs.core._STAR_print_newline_STAR_ = _STAR_print_newline_STAR__orig_val__4575_4579;
}
return cljs.core.str.cljs$core$IFn$_invoke$arity$1(sb__4661__auto__);
});

//# sourceMappingURL=alpha.js.map
