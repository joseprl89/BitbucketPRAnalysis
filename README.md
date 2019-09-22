# Bitbucket PR Analysis

Generate an analysis of the pull requests (PRs) in a Bitbucket repo using a Pearson correlation coefficient to help identify what bottlenecks a team is facing.

## Getting Started

### Prerequisites

Software requirements:

* Android Studio or IntellijIDEA

## Running an analysis on your Bitbucket repo

Use the `Main.kt` script to access a Command-Line Interface that requests all data needed in order to analyse a repository.

### Analysing a private repo

Private repos require an app password with `Read Pull Requests` authorization in order to access them.

These credentials can be generated in the User settings section in the BitBucket page following this [guide](https://confluence.atlassian.com/bitbucket/app-passwords-828781300.html).

The `Main.kt` script will prompt at the beginning to provide your username and app passwords.

## Running the tests

Right click on the BitbucketPullRequestAnalysisTest and select run tests to execute a test on a public repo.

## How are the Pull Requests analysed?

This software will perform two tasks to analyse your PRs.

* Downloading PR metadata using the Bitbucket v2.0 API
* Execute a Pearson product-moment correlation coefficient (PPMCC) using the [Apache commons framework](https://commons.apache.org/proper/commons-math/javadocs/api-3.3/org/apache/commons/math3/stat/correlation/PearsonsCorrelation.html)

A PPMCC is an analysis over a matrix of data of size NxM (M measures of N variables) that yields a square matrix of NxN. In the resulting matrix, each cell with index i, j defines how strongly the variables with index i and j are correlated.

### How to understand the results

As detailed above, the result of the PPMCC is a square matrix detailing how strongly the two variables correlate to each other.

After the last ~250 PRs have been analysed, the system will output a set of one line per variable analysed detailing how they correlated to the interval between the first commit and merging the PR.

The values can range from -1.0 to 1.0, where:

* 0 means the variable does not impact one way or another the time it takes to merge a PR.
* 1 means the variable impacts positively how long it takes to merge a PR (takes more time).
* -1 means the variable impacts negatively how long it takes to merge a PR (takes less time).

Therefore, if a variable is close to 1, the team should take actions to reduce that variable, as it will result in an increase in the overall throughput of it. If, on the other hand, the value is close to -1, you want to maximise that variable.

For example, if the number of commits has a value of -1, and the number of comments of 1, you should aim to take actions to increase the number of commits and reduce the number of comments raised in the PR.

### Example PPMCC analysis

As an example, let's assume we use as input data three variables, time between first commit and merge of the PR, time between first commit and creation of the PR, and number of comments, using data like so:

| Seconds between first commit and merge | Seconds between first commit and creation of PR | comments |
|----------------------------------------|-------------------------------------------------|----------|
| 4000                                   | 3500                                            | 10       |
| 8000                                   | 5000                                            | 20       |
| 5000                                   | 2000                                            | 4        |
| 1000                                   | 700                                             | 0        |
| 600                                    | 200                                             | 0        |

Upon executing the PPMCC we'll have a square 3x3 Matrix defining how strongly correlated the variables are, where 1 means that the data is very positively correlated, and -1 that it's very negatively correlated.

Example result (not accurate):

| Seconds between first commit and merge | Seconds between first commit and creation of PR | comments |
|----------------------------------------|-------------------------------------------------|----------|
| 1                                   | 0.7                                            | -0.3       |
| 0.7                                   | 1                                            | -0.2       |
| -0.3                                   | -0.2                                            | 1        |

In this fictional result, we'd see that the time between first commit and merge is strongly positively correlated to time between first commit and creation of PR, and those two are negatively correlated to the number of comments.

With this information, a team could aim to improve their performance by:

* Taking actions to minimise the number of comments in a PR (e.g. pre-implementation analysis of the feature)
* Taking actions to reduce the time it takes to create the PR (e.g. smaller batches)

## Caveats

PPMCC doesn't allow to easily compare discrete data (E.g. does the programming language impact the type?).
