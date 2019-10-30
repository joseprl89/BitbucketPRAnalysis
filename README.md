# BitBucket PR Analysis

Provides utilities to study pull requests (PRs) merged in a Bitbucket repo.

Currently it supports using a Pearson correlation coefficient to help identify what bottlenecks a team is facing, and exporting some relevant PR measures as a CSV.

![sample csv export results](img/sample-csv-export-results.png)

This data can be leveraged by engineering teams to identify bottlenecks on their flows, measure the progress since an experiment was executed,...

## Getting Started

### Prerequisites

Software requirements:

* [NodeJS](https://nodejs.org/)
* A [BitBucket App password](https://confluence.atlassian.com/bitbucket/app-passwords-828781300.html) with "Read pull request" permission.

To ensure the dependencies are installed, execute:

```bash
npm install
```

### Creating your configuration file

Before executing the scripts, you'll have to setup your configuration file, which defines:

* What credentials to use to fetch data
* Which repositories to read
* How many pages of PRs to load
* Whether to filter the PRs to ensure we don't measure git flow special branches such as:
  * Merge to master
  * Release or hotfix branches being merged to develop or master

To do so, you'll need to define a config.json file at the root of the project with the following format:

```javascript
{
    "authorization": {
        "username": "joseprl89",
        "password": "my bitbucket app password"
    },
    "repositories": [
        "myUser/myRepository",
        "myUser/myOtherRepository",
        "myOtherUser/yetAnotherRepository"
    ],
    "pagesToLoad": 2,
    "filterByGitFlow": true
}
```

You can use the utility script to guide you through the creation of this file by executing:

```bash
npm run create-config --silent
```

## Running the tests

Tests can be run using `npm test`.

## Analysing your BitBucket repo

You can analyse a repo by running the NPM scripts available. Each analysis has its own section below.

### Exporting measures to excel

Exports an xlsx file to enable to further analyse the data and visualise it in charts.

You can run the export using:

```bash
npm run --silent export-to-sheet
```

### Correlations between measures

Analyses the correlation between metrics using the [Pearson product-moment correlation coefficient (PPMCC)](https://en.wikipedia.org/wiki/Pearson_correlation_coefficient) through the [compute-pcorr](https://www.npmjs.com/package/compute-pcorr) node module.

This analysis is useful to identify which metric will have the highest impact on your overall performance once improved and to discover patterns that are hard to visualise.

You can run the analysis using:

```bash
npm run --silent correlations
```

As a hypothetical example, the number of comments might correlate with your cycle time, so you should target reducing the **need** for those comments.

#### Interpreting the result

After analysing the last pull requests of your repo, the system will output a set of correlations between the time between first commit, to merging your PR.

The values can range from -1.0 to 1.0, where:

* 0 means the variable does not impact one way or another the time it takes to merge a PR, and therefore there's no need to optimise it.
* 1 means direct correlation. When the measure increases, so will the time between first commit and merge, thus you'll want to reduce this metric to reduce the time it takes to merge your code.
* -1 means inverse correlation. When the measure increases, it will cause a decrease of the time between first commit and merge, thus you'll want to reduce this metric to reduce the time it takes to merge your code.

As an example, these are results from a private repository:

```text
Correlation between time to merge and Time between first commit and creation of the PR: 0.7894789362173071
Correlation between time to merge and Commit count: 0.5624755997810248
Correlation between time to merge and Time between approval and merge: 0.5611995438606215
Correlation between time to merge and Time between creation of the PR and approval: 0.5609924985817157
Correlation between time to merge and Merge commit count: 0.5534876138808782
Correlation between time to merge and Comment count: 0.4261628055496479
Correlation between time to merge and Activity count: 0.42079914430331505
Correlation between time to merge and Tasks used: NaN
Correlation between time to merge and Task count: NaN
```

Based on these numbers, we should prioritise improvements on the following order:

* The time between first commit and creation of the PR.
* Number of commits in the PR.
* Time between approval and merge.
* Merge commit count

#### Caveats

PPMCC doesn't allow to easily compare discrete data (E.g. does the programming language impact the type?).

Therefore, we can't really measure things like "Would using language A, B or C correlate with the time to merge?"

## Built With

* [NodeJS](https://nodejs.org/)
* [Mocha](https://mochajs.org/index.html) - Test harness
* [compute-pcorr](https://www.npmjs.com/package/compute-pcorr) - Pearson correlation implementation
* [Excel4node](https://www.npmjs.com/package/excel4node) - Excel exporter
* [Node-fetch](https://www.npmjs.com/package/node-fetch) - REST API Client

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Relevant Links

* [Pearson product-moment correlation coefficient (PPMCC)](https://en.wikipedia.org/wiki/Pearson_correlation_coefficient) and [compute-pcorr](https://www.npmjs.com/package/compute-pcorr), The node module used as an implementation.
* [Creating a BitBucket App password](https://confluence.atlassian.com/bitbucket/app-passwords-828781300.html)
