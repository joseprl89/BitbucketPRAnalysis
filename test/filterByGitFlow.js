const assert = require('assert');
const PullRequestAnalysis = require('../src/PullRequestAnalysis.js')

describe('PullRequestAnalysis', () => {
    function samplePullRequest() {
        return {
            source: {
                branch: {
                    name: "feature/123"
                }
            },
            commits: [{}],
            activity: [{update: {state: "MERGED"}}]
        }
    }

    describe('filterByGitFlow', () => {        
        it('Does not remove feature branches', () => {
            var pr = samplePullRequest()
            pr.source.branch.name = "feature/123"
            analysis = new PullRequestAnalysis([pr])
            analysis.filterByGitFlow()
    
            assert.equal(analysis.measures.length, 1)
        })

        it('Does not remove other branches', () => {
            var pr = samplePullRequest()
            pr.source.branch.name = "random-branch"
            analysis = new PullRequestAnalysis([pr])
            analysis.filterByGitFlow()
    
            assert.equal(analysis.measures.length, 1)
        })

        it('Removes PRs from release/*', () => {
            var pr = samplePullRequest()
            pr.source.branch.name = "release/1.2.3"
            analysis = new PullRequestAnalysis([pr])
            analysis.filterByGitFlow()
    
            assert.equal(analysis.measures.length, 0)
        })

        it('Removes PRs from hotfix/*', () => {
            var pr = samplePullRequest()
            pr.source.branch.name = "hotfix/1.2.3"
            analysis = new PullRequestAnalysis([pr])
            analysis.filterByGitFlow()
    
            assert.equal(analysis.measures.length, 0)
        })
    })
})