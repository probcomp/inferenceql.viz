# IQL-SQL

(name to be improved)
Loosely speaking, we can think of the functional capabilities of this language in terms of a non-relational subset of SQL (**no joins**) but with a few extensions:

## SELECT 
```
SELECT * FROM data
```
Returns the data table
```
SELECT * FROM (GENERATE x, y  UNDER model) LIMIT 100
```
Returns 100 synthetic samples of x,y from model


```
SELECT x, y, PROBABILITY OF x = 3 GIVEN *
```

Note: this is equivalent to 

```
SELECT x, y, (PROBABILITY OF x = 3 GIVEN * UNDER model) FROM data 
```


```
SELECT x, (PROBABILITY OF x = 4 UNDER (GENERATE x, y GIVEN z = 3)) LIMIT 100
```


```
SELECT x, (PROBABILITY OF x GIVEN y UNDER model1), (PROBABILITY OF x GIVEN y UNDER model2) FROM data
```


```
SELECT x, (PROBABILITY OF x GIVEN y UNDER model1),(PROBABILITY OF x GIVEN y UNDER model2) FROM (GENERATE x, y UNDER model3)
```


```
SELECT * FROM data WHERE x IS NOT NULL AND x < 3 AND y = "foo" 
```

Note: right now, we only support two binary predicates, “=”, “>” and two unary special predicate forms "<x> IS NOT NULL" and "<x> IS NULL". = works on numerical and categorical/discrete/enum-type values. > only works on numerical values. WHERE clauses can include sub-clauses with AND and OR. That's it!


```
SELECT … FROM (SELECT PROBABILITY OF x = 3 GIVEN *))
```

## GENERATE

here is a global default table ("data") and model ("model") which are always available to users. When the interpreter is instantiated, the data table and GPM used for these are specified manually. Conceptually, “data” has columns that are the columns in the underlying Data Table concept. Besides the default model, we assume there is a registry of models, mapping names to Clojure objects implementing the model, supplied when the IQL Interpreter is created. Every model can be queried using its name, for example in a query of the form:

```
GENERATE x, y GIVEN z=5 UNDER my_named_model
```

We want to have a default model that can be referenced without specifying "UNDER ..." so that people can use the system w/o worrying about that, and then switch to specifying models explicitly once they're a little more bought-in.





There are a family of dynamically-defined Generative Population Models defined by sub-expressions of the form

```
GENERATE <target_cols> [GIVEN <input_cols=vals>] [UNDER <model>]
```

These generative model expressions can be dynamically promoted into virtual tables, when they occur in SELECT … FROM <generative model expression>].

Eventually we'll want a notion of a "schema" for tables and a "schema" for models --- each specifying a set of columns and their model types --- and we will want to detect and catch type errors and schema mismatch errors (and throw them as exceptions) for bad invocations of of GENERATE in model postion, for example:

```
SELECT x, (PROBABILITY OF x = 4 UNDER (GENERATE y GIVEN z = 3))    
                           FROM data_table LIMIT 100
```




## PROBABILITY OF / PROBABILITY DENSITY OF


There is a  family of derived functions defined by sub-expressions of the form 

```
PROBABILITY OF <target_cols> [GIVEN <input_cols=vals>] [UNDER <model>]
```

and 

```
PROBABILITY DENSITY OF <target_cols> [GIVEN <input_cols=vals>] [UNDER <model>]
```

`PROBABILITY OF` always returns a normalized probability. 
`PROBABILITY DENSITY OF` can report unnormalized probabilities as this will
not violate the expectation a user might hold. The two keywords will interact as follows:

These sub-expressions can be used only in `SELECT` expressions, for now. 
-`PROBABILITY OF x='a' [...]` evaluates to `PROBABILITY DENSITY OF x='a' [...]` (below) if x has statistical type `NOMINAL`.
-`PROBABILITY OF x=3.14 [...]` where x has statistical type `NUMERICAL` throws an error.
-`PROBABILITY DENSITY OF x=3.14` [...] returns an unnormalized probability.


[-> back](../index.md)
