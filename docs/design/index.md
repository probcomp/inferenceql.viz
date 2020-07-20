# Welcome to InferenceQL 

:shipit:

## Table of contents

1. [What is InferenceQL?](#what-is-inferenceql)
1. [IQL-Query](query/index.md)
    1. [IQL-SQL](query/sql/index.md)
    1. [IQL-Datalog](query/datalog/index.md)
1. [IQL-Viz](viz/index.md)
1. [IQL-Inference](viz/index.md)
1. [IQL-Auto-modeling](viz/index.md)

## What is InferenceQL?

Many potential users of probabilistic inference do not need to know the details of the models that are being used to answer their questions, so long as they can build confidence that the results are reasonable. InferenceQL is a probabilistic programming platform that at its core implements a SQL-like language (IQL-SQL) that insulates users from knowledge of the underlying models and the data on which these models are based. 

InferenceQL further includes a language for inference query plans (IQL-Datalog), embedded in Clojure and Datalog, that also insulates users from the model representation. Both IQL-SQL and IQL-Datalog can specify workflows combining exploratory data analysis, inferential statistics, predictive modeling, and ad-hoc data transformations, all without referencing specific model parameters, model structure, or inference algorithms.

The underlying models are represented as generative probabilistic programs that satisfy a
simple black-box interface, and can sometimes be built automatically from data, using
Bayesian probabilistic program synthesis techniques (IQL-Auto-modeling).

InferenceQL comes with a visualization language (IQL-Viz) which is intended to be suitable for applications in which end users have the domain knowledge needed to pose meaningful questions, but lack the time or expertise needed to design and implement models and inference algorithms themselves. InferenceQL also may help teach concepts of conditional probability to a broad audience.

## Capabilities for InferenceQL

InferenceQL aims to implement the following capabilities (a check mark implies that 
a capability is merged into master and can be demonstrated).

- [x] Generate multivariate and conditional simulations.
- [x] Demonstrate "uncertainty" in analysis results (e.g. prediction)
- [x] Find outliers/surprising rows.
- [x] Teach conditional probabilities
- [ ] Users can generate models via automated data analysis.
- [ ] Impute missing values<sup>1</sup>
- [ ] Search for similar rows/few shot learning<sup>2</sup>
- [ ] Model comparison
- [ ] Predictive data analysis<sup>3</sup>
- [ ] Find predictors for a variable/column in a model.
- [ ] Find interactions between variables/columns.
- [ ] Allow users to dynamically define boolean "events" as functions 

## Concepts and programming model for InferenceQL

This section is in design/draft stage. Find the current draft [here](https://docs.google.com/document/d/14LnXWlDB07B4nyj8AyRvgQf7muJw6SJ1Ger2XhRPGhw/edit#heading=h.an7vxaeb4i0y).



#### Notes:
1: We had a UI feature for this but disabled it (this needs to be supported by a IQL-Datalog and an IQL-SQL extension).

2: We have this working using a multimix implementation based on Metaprob in the UI (i.e. not in IQL-datalog or IQL-SQL). It runs only on tiny datasets; to consider it done we need to stop depending on Metaprob.

3: Demonstrable predictive data analysis depends on model comparison.