# Test suite.

(All of the below is work-in-progress.)


## Outline

1. Design
2. View 1
3. View 2
4. For which datapoints  do we "know something" about LogPDF and Simulate
5. For which datapoints  do we "know something" about KL 
6. For which datapoints  do we "know something" about MI

## 1. Design


The idea behind the test suite is that we start from "things we know". There are
different "things" we can now for testing different aspects inferenceQL, depending
on whether we are testing:

1. Logpdf and simluate 
2. KL/distance functions
3. MI and entropy

## 2. View 1

We'll use the following row generator structure to test: 
```
(def multi-mixture
   {:vars {"x" :gaussian
           "y" :gaussian
           "z" :gaussian
           "a" :categorical
           "b" :categorical
           "c" :categorical}
    :views [[  {:probability 0.166666666
                :parameters {"x" {:mu 3 :sigma 1}
                             "y" {:mu 4 :sigma 0.1}
                             "a" {"0" 1.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.95, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 3 :sigma 0.1}
                             "y" {:mu 4 :sigma 1}
                             "a" {"0" 0.0 "1" 1.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.95, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 8  :sigma 0.5}
                             "y" {:mu 10 :sigma 1}
                             "a" {"0" 0.0 "1" 0.0 "2" 1.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.95, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 14  :sigma 0.5}
                             "y" {:mu  7  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 1.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.95, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 16  :sigma 0.5}
                             "y" {:mu  9  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 1.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.95, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu  9  :sigma 2.5}
                             "y" {:mu 16  :sigma 0.1}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 1.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.95}}}]
              [{:probability 0.25
                :parameters {"z" {:mu 0 :sigma 1}
                             "c" {"0" 1.0, "1" 0.0, "2" 0.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 15 :sigma 1}
                             "c" {"0" 0.0, "1" 1.0, "2" 0.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 30 :sigma 1}
                             "c" {"0" 0.0, "1" 0.0, "2" 1.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 15 :sigma 8}
                             "c" {"0" 0.0, "1" 0.0, "2" 0.0, "3" 1.0}}}]]})
                             
(def row-generator (search/optimized-row-generator multi-mixture))
```
Column `a` is a categorical that also serves as a deterministic indicator of the
Gaussian components in `x` and `y`. `b` is a noisy copy of `a`. 

Below, we explain how we test the following points `P 1` to `P 6`:
```
(def test-points [{:tx 3  :ty 4  :test-point "P 1"}
                  {:tx 8  :ty 10 :test-point "P 2"}
                  {:tx 14 :ty 7  :test-point "P 3"}
                  {:tx 15 :ty 8  :test-point "P 4"}
                  {:tx 16 :ty 9  :test-point "P 5"}
                  {:tx 9  :ty 16 :test-point "P 6"}])
```
We can plot simulations for the first view from `generate-crosscat-row` and `test-points`:
![Data](https://probcomp-3.csail.mit.edu/1b2e3ccb909da5afc7a7e497785197b8/n/simulations-x-y.png)

## View 2
![Data](https://probcomp-3.csail.mit.edu/1b2e3ccb909da5afc7a7e497785197b8/n/simulations-z.png)


## 4. For which datapoints  do we "know something" about LogPDF and Simulate

All but `P 1` and `P 4` of the test points map to a single component. For all
the other test points, we know about the following:

1. If we conditioning on the cluster ID that corresponds to the component of which the point marks a cluster center, then we know that

    a. Simulations of `x` and `y` should be close to the point.
    
    b. Simulations for `a`, `b` are determined by the cluster ID.
    
    c. LogPDF of the point will be equivalent to the product of the univariate gaussian probabilities of each dim of the point and determined by the component.
    
    d. LogPDF for `a`, `b` is determined by the cluster ID.
    
2. Conditioning the point

    a. If we simulate the cluster-ID given the point, only a single ID should come up.
    
    b. The probability of the correct cluster-ID should be one.
    
    c. Simulations for `a`, `b` are determined by the point which in turn determines the cluster ID.
    
    d. LogPDF for `a`, `b` and be should are determined by the point which in turn determines the cluster ID.
    


For `P 1` and `P 4` the above also holds, only that we need to take into account two clusters, not one.

## 5. For which datapoints  do we "know something" about KL 

TODO.

## 6. For which columns  do we "know something" about MI

Assuming the following model; we can test three things:
```
(def generate-crosscat-row
  (multi-mixture
    (view
      {"x" gaussian
       "y" gaussian
       "a" categorical}
      (clusters
       0.25 {"x" [1 0.1]
             "y" [1 0.1]
             "a" [[1 0 0 0]]}
       0.25 {"x" [2 0.1]
             "y" [2 0.1]
             "a" [[0 1 0 0]]}
       0.25 {"x" [3 0.1]
             "y" [3 0.1]
             "a" [[0 0 1 0]]}
       0.25 {"x" [4 0.1]
             "y" [4 0.1]
             "a" [[0 0 0 1]]}))
    (view
      {"v" gaussian
       "w" categorical}
      (clusters
       1.00 {"v" [1 1]
             "w" [1 1] }))))
```
See samples from the first view here:

![Data](https://probcomp-3.csail.mit.edu/1b2e3ccb909da5afc7a7e497785197b8/n/simulations-for-mi-x-y.png)



and samples from the second view here:

![Data](https://probcomp-3.csail.mit.edu/1b2e3ccb909da5afc7a7e497785197b8/n/simulations-for-mi-v-w.png)

#### Invariants

For the Multimix model we specified to test MI, we know that three invariants about 
MI hold given this model:

1. MI of `x` and `y is larger than  0` because `x` carries information about `y`.

2. CMI of `x` and `y | a = 0`. Column `a` is a deterministic indicator of the
   cluster ID. Conditioning on `a = 0` simplifies the distribution of `x` and `y`
   to a simple bivariate Gaussian with a diagonal covariance matrix. This implies
   that `x` and `y` are statistically independent and there is no information
   flowing between them.

3. MI of `v` and `w` is equal to 0. Again, the joint is equal to a bivariate
   Gaussian distribution with a diagonal covariance matrix and no information is
   flowing between `v` and `w`.

#### Multivariate Gaussian

In addition, if we had a multivariate normal CGPM, we could analytically compute
the mutual information between two dimensions. We don't have this CGPM yet.
Once we have it, we'll add it and test against an analytically computed result.
