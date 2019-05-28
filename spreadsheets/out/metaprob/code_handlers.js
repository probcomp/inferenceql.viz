// Compiled by ClojureScript 1.10.520 {}
goog.provide('metaprob.code_handlers');
goog.require('cljs.core');
metaprob.code_handlers.name_checker = (function metaprob$code_handlers$name_checker(n){
return (function (x){
return ((cljs.core.seq_QMARK_.call(null,x)) && ((cljs.core.first.call(null,x) instanceof cljs.core.Symbol)) && (cljs.core._EQ_.call(null,cljs.core.name.call(null,cljs.core.first.call(null,x)),n)));
});
});
metaprob.code_handlers.symbol_checker = (function metaprob$code_handlers$symbol_checker(n){
return (function (x){
return ((cljs.core.seq_QMARK_.call(null,x)) && (cljs.core._EQ_.call(null,cljs.core.first.call(null,x),n)));
});
});
metaprob.code_handlers.fn_expr_QMARK_ = metaprob.code_handlers.name_checker.call(null,"fn");
metaprob.code_handlers.let_expr_QMARK_ = metaprob.code_handlers.name_checker.call(null,"let");
metaprob.code_handlers.do_expr_QMARK_ = metaprob.code_handlers.name_checker.call(null,"do");
metaprob.code_handlers.let_traced_expr_QMARK_ = metaprob.code_handlers.name_checker.call(null,"let-traced");
metaprob.code_handlers.gen_expr_QMARK_ = metaprob.code_handlers.name_checker.call(null,"gen");
metaprob.code_handlers.gen_name = (function metaprob$code_handlers$gen_name(expr){
if((cljs.core.second.call(null,expr) instanceof cljs.core.Symbol)){
return cljs.core.second.call(null,expr);
} else {
if(cljs.core.map_QMARK_.call(null,cljs.core.second.call(null,expr))){
return cljs.core.get.call(null,cljs.core.second.call(null,expr),new cljs.core.Keyword(null,"name","name",1843675177));
} else {
return null;

}
}
});
metaprob.code_handlers.gen_annotations = (function metaprob$code_handlers$gen_annotations(expr){
if(cljs.core.map_QMARK_.call(null,cljs.core.second.call(null,expr))){
return cljs.core.second.call(null,expr);
} else {
return cljs.core.PersistentArrayMap.EMPTY;
}
});
metaprob.code_handlers.gen_has_annotations_QMARK_ = (function metaprob$code_handlers$gen_has_annotations_QMARK_(expr){
return (!(cljs.core.vector_QMARK_.call(null,cljs.core.second.call(null,expr))));
});
metaprob.code_handlers.gen_pattern = (function metaprob$code_handlers$gen_pattern(expr){
if(metaprob.code_handlers.gen_has_annotations_QMARK_.call(null,expr)){
return cljs.core.nth.call(null,expr,(2));
} else {
return cljs.core.second.call(null,expr);
}
});
metaprob.code_handlers.gen_body = (function metaprob$code_handlers$gen_body(expr){
if(metaprob.code_handlers.gen_has_annotations_QMARK_.call(null,expr)){
return cljs.core.rest.call(null,cljs.core.rest.call(null,cljs.core.rest.call(null,expr)));
} else {
return cljs.core.rest.call(null,cljs.core.rest.call(null,expr));
}
});
metaprob.code_handlers.map_gen = (function metaprob$code_handlers$map_gen(f,gen_expr){
if(metaprob.code_handlers.gen_has_annotations_QMARK_.call(null,gen_expr)){
return cljs.core.cons.call(null,cljs.core.first.call(null,gen_expr),cljs.core.cons.call(null,cljs.core.second.call(null,gen_expr),cljs.core.cons.call(null,metaprob.code_handlers.gen_pattern.call(null,gen_expr),cljs.core.map.call(null,f,metaprob.code_handlers.gen_body.call(null,gen_expr)))));
} else {
return cljs.core.cons.call(null,cljs.core.first.call(null,gen_expr),cljs.core.cons.call(null,metaprob.code_handlers.gen_pattern.call(null,gen_expr),cljs.core.map.call(null,f,metaprob.code_handlers.gen_body.call(null,gen_expr))));
}
});
metaprob.code_handlers.if_expr_QMARK_ = metaprob.code_handlers.symbol_checker.call(null,new cljs.core.Symbol(null,"if","if",1181717262,null));
metaprob.code_handlers.if_predicate = cljs.core.second;
metaprob.code_handlers.if_then_clause = (function metaprob$code_handlers$if_then_clause(expr){
return cljs.core.nth.call(null,expr,(2));
});
metaprob.code_handlers.if_else_clause = (function metaprob$code_handlers$if_else_clause(expr){
if((cljs.core.count.call(null,expr) < (4))){
return null;
} else {
return cljs.core.nth.call(null,expr,(3));
}
});
metaprob.code_handlers.variable_QMARK_ = cljs.core.symbol_QMARK_;
metaprob.code_handlers.quote_expr_QMARK_ = metaprob.code_handlers.symbol_checker.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null));
metaprob.code_handlers.quote_quoted = cljs.core.second;
metaprob.code_handlers.literal_QMARK_ = (function metaprob$code_handlers$literal_QMARK_(expr){
return (((!(((cljs.core.seq_QMARK_.call(null,expr)) || (cljs.core.vector_QMARK_.call(null,expr)) || (cljs.core.map_QMARK_.call(null,expr)))))) || (cljs.core.empty_QMARK_.call(null,expr)));
});
metaprob.code_handlers.let_bindings = (function metaprob$code_handlers$let_bindings(expr){
return cljs.core.partition.call(null,(2),cljs.core.second.call(null,expr));
});
metaprob.code_handlers.let_body = (function metaprob$code_handlers$let_body(expr){
return cljs.core.rest.call(null,cljs.core.rest.call(null,expr));
});

//# sourceMappingURL=code_handlers.js.map
