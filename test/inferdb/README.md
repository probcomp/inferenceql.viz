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
(def generate-crosscat-row
  (multi-mixture
    (view
      {"x" gaussian
       "y" gaussian
       "a" categorical
       "b" categorical}
      (clusters
       0.166666666 {"x" [3 1]
                    "y" [4 0.1]
                    "a" [[1 0 0 0 0 0]]
                    "b" [[0.95 0.01 0.01 0.01 0.01 0.01]]}
       0.166666666 {"x" [3 0.1]
                    "y" [4 1]
                    "a" [[0 1 0 0 0 0]]
                    "b" [[0.01 0.95 0.01 0.01 0.01 0.01]]}
       0.166666667 {"x" [8 0.5]
                    "y" [10 1]
                    "a" [[0 0 1 0 0 0]]
                    "b" [[0.01 0.01 0.95 0.01 0.01 0.01]]}
       0.166666666 {"x" [14 0.5]
                    "y" [7 0.5]
                    "a" [[0 0 0 1 0 0]]
                    "b" [[0.01 0.01 0.01 0.95 0.01 0.01]]}
       0.166666666 {"x" [16 0.5]
                    "y" [9 0.5]
                    "a" [[0 0 0 0 1 0]]
                    "b" [[0.01 0.01 0.01 0.01 0.95 0.01]]}
       0.166666666 {"x" [9  2.5]
                    "y" [16 0.1]
                    "a" [[0 0 0 0 0 1]]
                    "b" [[0.01 0.01 0.01 0.01 0.01 0.95]]}))
    (view
      {"z" gaussian
       "c" categorical}
      (clusters
       0.25 {"z" [0 1]
             "c" [[1 0 0 0]]}
       0.25 {"z" [10 1]
             "c" [[0 1 0 0]]}
       0.25 {"z" [20 1]
             "c" [[0 0 1 0]]}
       0.25 {"z" [30 1]
             "c" [[0 0 0 1]]}))))
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
See plots below.

#### Invariants

We know three invariants about MI:

1. MI of x and y >  0

2. CMI of x and y | a = 0.

3. MI of w and v = 0.

In addition, if we had a multivariate normal CGPM, we can analyltically compute
the mutual information between two dimenstions. We don't have this CGPM yet.
Once we have it, we'll add it and test against an analytically computed result.



