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
`Correlation between time to merge and Time between approval and merge: 0.9709802409045009
Correlation between time to merge and Time between creation of the PR and approval: 0.9238432789657837
Correlation between time to merge and Commit count: 0.4246474192304717
Correlation between time to merge and Activity count: 0.21370242680885976
Correlation between time to merge and Time between first commit and creation of the PR: 0.17888047419396244
Correlation between time to merge and Merge commit count: 0.175549165584237
Correlation between time to merge and Comment count: 0.0983769845406757
Correlation between time to merge and Task count: NaN
Correlation between time to merge and Tasks used: NaN`

            assert.equal(analysis.pearsonCorrelation(), expected)
        })
    })
});