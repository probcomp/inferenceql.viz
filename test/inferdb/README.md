# Test suite.

(All of the below is work-in-progress.)
## Outline
The test suite is organized around data points and related invariants; meaning
we will define a set of points and "know" the system should do for
a given point. We will be testing:
1. Invariants and qualitatively known behavior for a set of points in 2-d (x and y; both numerical).
2. Invariants and qualitatively known behavior for a set of points in 1-d.
3. Invariants and qualitatively know behavior for MI.

## 1. Invariants and qualitatively known behavior for a set of points in 2-d (x and y; both numerical).

We'll use the following row generator structure to test: ```
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

We 'll test the following points `P 1` to `P 6`:
```
(def test-points [{:tx 3  :ty 4  :test-point "P 1"}
                  {:tx 8  :ty 10 :test-point "P 2"}
                  {:tx 14 :ty 7  :test-point "P 3"}
                  {:tx 15 :ty 8  :test-point "P 4"}
                  {:tx 16 :ty 9  :test-point "P 5"}
                  {:tx 9  :ty 16 :test-point "P 6"}])
```
We can plot simulations from `generate-crosscat-row` and `test-points`:
![Data]("simulations-x-y.png")


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
3.  We can compute the KL of the probability vector on cluster-IDs given the point and a point that implies
    a. a similar probability vector on cluster-IDs
    b. a dissimilar probability vector on cluster-IDs

For `P 1` and `P 4` the above also holds, only that we need to take into account two clusters, not one.
