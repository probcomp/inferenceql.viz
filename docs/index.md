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
