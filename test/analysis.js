const assert = require('assert');
const PullRequestAnalysis = require('../src/PullRequestAnalysis.js')

describe('PullRequestAnalysis', () => {
    describe('mocked pull request measures', () => {
        const sampleData = require('./data/prMocked.json')
        analysis = new PullRequestAnalysis(sampleData)
        const measures = analysis.measures[0]

        it('includes measure for comment number', () => {
            assert.equal(measures['Comment count'], 2)
        })

        it('includes measure for task number', () => {
            assert.equal(measures['Task count'], 0)
        })

        it('includes measure for commit number', () => {
            assert.equal(measures['Commit count'], 4)
        })

        it('includes binary measure for whether tasks are used', () => {
            assert.equal(measures['Tasks used'], 0)
        })

        it('includes measure for activity count', () => {
            assert.equal(measures['Activity count'], 8)
        })

        it('includes measure for Merge commit count', () => {
            assert.equal(measures['Merge commit count'], 1)
        })
    })

    describe('sample measures', () => {
        const sampleData = require('./data/prSample.json')
        analysis = new PullRequestAnalysis(sampleData)
        const measures = analysis.measures[0]

        it('should generate Time between first commit and creation of the PR', () => {
            assert.equal(measures['Time between first commit and creation of the PR'], 0.012604143518518518);
        })
        it('should generate Time between creation of the PR and approval', () => {
            assert.equal(measures['Time between creation of the PR and approval'], 0.023549641203703703);
        })
        it('should generate Time between approval and merge', () => {
            assert.equal(measures['Time between approval and merge'], 0.020895752314814813);
        })
        
        it('generates time to merge code which is equal to all the time combined', () => {
            assert.equal(
                measures['Time to merge code'],
                measures['Time between first commit and creation of the PR'] +
                 measures['Time between creation of the PR and approval'] +
                 measures['Time between approval and merge']
            )
        });
    })
});