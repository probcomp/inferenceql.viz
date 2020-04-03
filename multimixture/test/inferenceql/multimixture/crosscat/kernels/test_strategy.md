## Testing plan for the `category` CrossCat kernel

In its current state there are 7 methods:
- `category-weights`
- `category-scores`
- `category-sample`
- `latents-update`
- `kernel-row`
- `kernel-view`
- `kernel`

These seem like a reasonable modularization of the function of the kernel and things need not be broken down further. That being said, most of these are non-deterministic and will require some care for testing.

Before we get to method defition and cases to analyze, we must define specs of the common types of arguments we will be working with.

#### `latents`
```
latents {:global {:z      []
                  :counts []}
         :local  [{:y      []
                   :counts []}
                   ...
                  {:y      []
                   :counts []}]}

datum {:col-0 val-0 ... :col-d val-d}

data {:col-0 [val-0 ... val-n]
      ..
      :col-m [val-0 ... val-m]

types {:col-0 type-0
       ...
       :col-d type-d}

view {:hypers {:col-0 {prior params}
               ...
               :col-d {prior params}}
      :categories [{:parameters  {:col-0 params
                                  ...
                                  :col-d params}}
                   ...
                   {:parameters  {:col-0 params
                                  ...
                                  :col-d params}}]}
```
where `latents-g` refers to `(:global latents)` and `latents-l` refers to some element of `(:local latents)`.

#### `category-weights`
- In:
  - `latents` [`latents-l`]: local latents of the current view.
  - `y`             [`int`]: current category assignment index.
  - `singleton?`   [`bool`]: indicates whether current category is a singleton.
  - `m`             [`int`]: number of auxiliary categories to consider.
- Out:
  - [`int` `vec double`]: updated value of `m` (decrements if `singleton?`), vector of weights for each cluster (based on CRP prior)
- Cases to cover:
  - `singleton?` is `true`, `m` is `1`
    - Return: `0`, vector of the length equal to number of existing clusters (weight at `y` should be as specified in the algorithm).
  - `singleton?` is `false`, `m` is `1`
    - Return: `1`, vector of length `n-clusters + 1`, last weight as specified in the paper.
  - `singleton?` is `true`, `m` is `2`
    - Return: `1`, vector of length `n-clusters + 1`, weight at `y` and last weight as specified in the paper.
  - `singleton?` is `true`, `m` is `3` (captures all greater cases)
    - Return: `2`, vector of length `n-clusters + 2`, weight at `y` and last two weights as specified in the paper.
  - `singleton?` is `false`, `m` > `1`
    - Return: `m`, vector of length `n-clusters + m`, last weights as specified in the paper.

#### `category-scores`
- In:
  - `x`     [`datum`]: datum to score against categories.
  - `view`   [`view`]: current view.
  - `types` [`types`]: statistical types of data, specified in the model.
  - `m`       [`int`]: number of auxiliary categories to consider.
- Out:
  - [`vec double`]: vector of log probabilities that each category (including auxiliary) generated `x`.
- Cases to cover:
  - `m` = `1`
    - Return: vector of length `n-clusters + 1`, can confirm the first `n-clusters` manually, the last cluster is tested implicitly by the tests from `xcat/generate-category`.
  - `m` > `1`
    - Return: vector of length `n-clusters + m`, can confirm the first `n-clusters` manually, the last `m` clusters are tested implicitly by the tests from `xcat/generate-category`.

#### `category-sample`
- In:
  - `weights` [`vec double`]: weights of categories by member count.
  - `scores`  [`vec double`]: scores of categories against a datum.
- Out:
  - [`int`]: sampled category index.
- Cases to cover:
  - Basic functionality
    - Run this many times on set values of `weights` and `scores` and check that the empirical distribution of the function aligns with the manual calculation of normalized `weights + scores`.

#### `latents-update`
- In:
  - `row-id`        [`int`]: index of row, used for identifying latent category assignment.
  - `y`             [`int`]: current category assignment index.
  - `y'`            [`int`]: future category assignment index.
  - `latents` [`latents-l`]: local latents of the current view.
  - `delete?`      [`bool`]: indicates if a cluster is to be deleted from the latent assignments. 
- Out:
    - [`latents-l`]: Updated latent assignments and counts per category.
- Cases to cover:
  - `y'` is a new category entirely, `delete?` is `false`
    - Decrement current category count, append 1 to end of counts (new category).
    - Update category assignments.
  - `y'` is a new category entirely, `delete?` is `true`
    - This won't ever be called. There's a safeguard in place in `kernel-row` where latents are not updated in this case, only the parameters of the category.
  - `y'` is an existing category, `delete?` is `false`
    - Decrement current category count, increment future category count.
    - Update category assignments.
  - `y'` is an existing category, `delete?` is `true`
    - Decrement current category count, increment future category count.
      - Must remove current category count from list (count is 0), and decrement all assignments that are greater than the current assignment being deleted.
        - e.g. For row 5 to go from category 1 -> 0 => counts: [4 1 2] -> [5 2], assignments: [0 2 0 0 2 1 0] -> [0 1 0 0 1 0 0]
    - Update category assignments.
    

#### `kernel-row`
- In:
  - `x`           [`datum`]: datum to score against categories.
  - `row-id`        [`int`]: index of row, used for identifying latent category assignment.
  - `m`             [`int`]: number of auxiliary categories to consider.
  - `types`       [`types`]: statistical types of data, specified in the model.
  - `latents` [`latents-l`]: local latents of the current view.
  - `view`         [`view`]: current view.
- Out:
  - [`vec latents-l view`]: updated local latents reflecting potential category assignment change, updated view reflecting clusters being potentially added or deleted.
- Cases to cover:
  - Two very distinct categories, with `x` in the wrong category.
    - Run a few times and verify it is switching clusters by comparing the resulting `latents` and `view` to what is expected. Allow for a little noise because of auxiliary clusters.
  - Two equally likely categories.
    - Same procedure as above, verify that that the assignments are roughly split between the two, again accounting for noise from the auxiliary categories.
  - `m = 1`
    - Verify the procedure runs and output matches spec. Could group with the first case.
  - `m > 1`
    - Verify the procedure runs and output matches spec. Could group with the second case.


#### `kernel-view`
- In:
  - `data`         [`data`]: input data.
  - `view`         [`view`]: current view.
  - `types`       [`types`]: statistical types of data, specified in the model.
  - `latents` [`latents-l`]: local latents of the current view.
  - `m`             [`int`]: number of auxiliary categories to consider.
- Out:
  - [`vec latents-l view`]: updated local latents reflecting potential category assignment changes, updated view reflecting categories being potentially added or deleted.
- Cases to cover:
  - `data` contains 1 row.
    - This is equivalent to running `kernel-row` after filtering `data` for view-specific columns. If `kernel-rows` tests pass, and this runs, then it can be said to work.
  - `data` contains 2 rows (captures `n > 1`).
    - Need to find a way to randomly seed. Then one could capture the output of the before test, and ensure that by running `kernel-row` on said output, we get the desired result from running `kernel-view` on `data`.
  

#### `kernel`
- In:
  - `data`       [`data`]: input data.
  - `model`     [`model`]: CrossCat model.
  - `latents` [`latents`]: specified latents of CrossCat model.
  - `m`           [`int`]: number of auxiliary categories to consider.
- Out:
  - [`vec model latents`]: the result of calling `kernel-view` on each of the views of the model.
- Cases to cover:
  - Basic functionality
    - Can confirm it's working by testing `kernel-view` on two different views and local latents, and verifying that the results are correctly formatted (will likely be more spec testing that anything else).
      - Might need a custom `rand` function in order to seed for testing, which shouldn't be that hard in `.clj` files.

