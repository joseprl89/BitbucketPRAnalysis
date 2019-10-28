const assert = require('assert');
const PullRequestAnalysis = require('../src/PullRequestAnalysis.js')

describe('PullRequestAnalysis', () => {
    describe('correlation', () => {        
        it('returns null if there\'s no PR\'s', () => {
            analysis = new PullRequestAnalysis([])
            const csv = analysis.pearsonCorrelation()
    
            assert.equal(csv, null)

        })

        it('generates the correct csv result', () => {
            const sampleData = require('./data/complexData.json')
            analysis = new PullRequestAnalysis(sampleData)
            const expected = 
`Correlation between time to merge and timeToMergeCode: 1
Correlation between time to merge and timeBetweenApprovalAndMerge: 0.9709802409045009
Correlation between time to merge and timeBetweenCreationAndApproval: 0.9238432789657837
Correlation between time to merge and commitCount: 0.4246474192304717
Correlation between time to merge and activityCount: 0.21370242680885976
Correlation between time to merge and timeBetweenFirstCommitAndCreation: 0.17888047419396244
Correlation between time to merge and mergeCommitCount: 0.175549165584237
Correlation between time to merge and commentCount: 0.0983769845406757
Correlation between time to merge and tasksUsed: NaN
Correlation between time to merge and taskCount: NaN`

            assert.equal(analysis.pearsonCorrelation(), expected)
        })
    })
});