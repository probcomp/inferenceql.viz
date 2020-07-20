# IQL-Viz

## Potential Audiences 

### Group 1: Users with a strong stats background or a strong data engineering background. 

These groups already have their preferred visualization tools, but need a teaching environment
in which to learn about generative modeling, CrossCat, and IQL capabilities.
IQL-Viz can show these users what our tools can do, and it can also frame the
narrative in line with our views on probabilistic programming—models as generative
code, automatic bayesian synthesis of models, etc.
This would not be the case had we just shipped an API.

These users also need a template for (i) imagining interactive UX based on inference,
and (ii) showcasing IQL capabilities to stakeholders.

### Group 2: Domain experts and decision makers.

With this user group IQL-Viz serves as a tool for learning about the capabilities
offered by probabilistic inference, and a scaffolding for learning IQL-SQL.
Long term, this will help grow IQL's use as a medium for thinking and communicating
about a domain probabilistically. 

IQL-SQL will allow domain experts to explore and discover meaningful queries that
highlight anomalies, discover relationships, or pose important scenarios. When
the results from these explorations can be communicated easily, it allows other
domain experts and decision makers to see the power of thinking probabilistically
without much effort. These moments of seeing-is-believing could start a virtuous
cycle where the previously uninitiated are inspired to pose their own questions
in InferenceQL and share further. This elevated level of discourse on the level
of probabilistic queries can eventually spread and transform an organization’s
ways of communicating and making decisions.

IQL-Viz helps this effort by allowing users to explore the results of probabilistic
queries in an effortless and intuitive manner.  


## Teaching Goals:
#### 1. Communicate the concept of models as generative processes.
- Ability to simulate whole rows. 
- Ability to see a sampler generating values for a missing cell or existing cell.   
- Show a maximally local and readable probabilistic program of the underlying baseline model.

#### 2. Allow the user to build confidence (or lack of confidence) in a given model. 
- Ability to compare real and virtual data.
- View a visual representation of the underlying model?
- View model predictions for a cell against its actual value. 

#### 3. Teach the skill of interpreting and formulating conditional queries to domain
experts and decision makers. 
- Ability to compare ‘probability of’ queries to see how various conditioning operations affect probability.  


#### 4. Explore a generative population model and a sample of data from the associated real-world population that it models. (This capability should be part of the basic IQL-Viz spreadsheet app, allowing users to take results from any IQL query, and apply the following GUI operations to them.)
- Find rows similar to some target rows
- Find columns probably predictive of some target column
- Show a "circle view" of the pairwise predictive relationships between variables, as identified by mutual information
- Show a "circle view" for correlation, to remind/teach users about the improvements in accuracy coming from having a population model

## Two levels of IQL-Viz 

#### 1. A set of GUI interactions that produce visualizations
- Simulations - on single cell selection
- Histograms and conditional distributions plots - on column(s) selection
- Multiple selection layers 
    - for comparing across feature dimensions or for comparing between multiple ‘probability of’ columns. 
- Benefits:
    - Simple 
    - Better serves data story-telling with spreadsheets app.
    - Better serves decision makers who want to explore the output of a set of queries—given to the decision maker by someone else—without having to write IQL-Viz queries.  [Users not accustomed to working with programming languages may not be able to handle an additional layer of complexity on top of an IQL-SQL query.

#### 2. A visualization language 
- Declarative 
- Transforms tables to visualizations.
- Can specify a particular type of visualization, scales, etc.  
- Can specify interactivity declaratively. 
- Benefits: 
    - Allows full customization of visualizations without building an intricate GUI
    - Hides this additional complexity from novice users–by making it a language and not a GUI.
