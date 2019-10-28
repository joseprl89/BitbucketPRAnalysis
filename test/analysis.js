const assert = require('assert');
const PullRequestAnalysis = require('../src/PullRequestAnalysis.js')

describe('PullRequestAnalysis', () => {
    describe('mocked pull request measures', () => {
        const sampleData = require('./data/prMocked.json')
        analysis = new PullRequestAnalysis(sampleData)
        const measures = analysis.measures[0]

        it('includes measure for comment number', () => {
            assert.equal(measures.commentCount, 2)
        })

        it('includes measure for task number', () => {
            assert.equal(measures.taskCount, 0)
        })

        it('includes measure for commit number', () => {
            assert.equal(measures.commitCount, 4)
        })

        it('includes binary measure for whether tasks are used', () => {
            assert.equal(measures.tasksUsed, 0)
        })

        it('includes measure for activity count', () => {
            assert.equal(measures.activityCount, 8)
        })

        it('includes measure for Merge commit count', () => {
            assert.equal(measures.mergeCommitCount, 1)
        })
    })

    describe('sample measures', () => {
        const sampleData = require('./data/prSample.json')
        analysis = new PullRequestAnalysis(sampleData)
        const measures = analysis.measures[0]

        it('should generate timeBetweenFirstCommitAndCreation', () => {
            assert.equal(measures.timeBetweenFirstCommitAndCreation, 0.012604143518518518);
        })
        it('should generate timeBetweenCreationAndApproval', () => {
            assert.equal(measures.timeBetweenCreationAndApproval, 0.023549641203703703);
        })
        it('should generate timeBetweenApprovalAndMerge', () => {
            assert.equal(measures.timeBetweenApprovalAndMerge, 0.020895752314814813);
        })
        
        it('generates time to merge code which is equal to all the time combined', () => {
            assert.equal(
                measures.timeToMergeCode,
                measures.timeBetweenFirstCommitAndCreation +
                 measures.timeBetweenCreationAndApproval +
                 measures.timeBetweenApprovalAndMerge
            )
        });
    })
});