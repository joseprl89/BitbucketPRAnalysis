var pcorr = require( 'compute-pcorr' );

function daysBetween(date1, date2) {
    const diffTime = Math.abs(date2 - date1);
    const diffDays = diffTime / (1000 * 60 * 60 * 24);
    return diffDays
}

module.exports = class PullRequestAnalysis {
    constructor(data) {
        this.data = data.filter(pr => pr.commits.length > 0)
    }

    get measures() {
        return this.data.map(this.prToMeasure)
    }

    filterByGitFlow() {
        this.data = this.data.filter(pr => {
            return !(pr.source.branch.name.startsWith('release/') || pr.source.branch.name.startsWith('hotfix/'))
        })
    }

    pearsonCorrelation() {
        if (this.data.length == 0) return null

        const measures = this.measures
        const keys = Object.keys(measures[0]).slice(1)

        function objectToArray(object) {
            return keys.map(key => object[key])
        }

        function sortFunction(a, b) {
            if (isNaN(b.value)) return -1
            if (isNaN(a.value)) return 1
            return b.value - a.value
        }

        // Needs transposing
        const inputMatrix = keys.map(key => measures.map(object => object[key]))
        const correlationsToMergeTime = pcorr( inputMatrix )[0]
        return correlationsToMergeTime
            .map((value, index) => {
                return { value: value, index: index }
            })
            .sort(sortFunction)
            .map(item => {
                return `Correlation between time to merge and ${keys[item.index]}: ${item.value}`
            })
            .slice(1)
            .join('\n')
    }

    prToMeasure(pr) {
        let mergeActivity = pr.activity.filter(a => a.update && a.update.state == "MERGED").slice(-1)[0]
        let approveActivity = pr.activity.filter(a => a.approval).slice(-1)[0]

        let mergeDate = new Date(mergeActivity.update.date)
        let prCreationDate = new Date(pr.created_on)
        let approvalDate = approveActivity != null ? new Date(approveActivity.approval.date) : null
        let firstCommitDate = new Date(pr.commits.slice(-1)[0].date)

        return {
            'Id': pr.id,

            'Time to merge code': daysBetween(firstCommitDate, mergeDate),

            'Time between first commit and creation of the PR': daysBetween(firstCommitDate, prCreationDate),
            'Time between creation of the PR and approval': daysBetween(prCreationDate, approvalDate),
            'Time between approval and merge': daysBetween(approvalDate, mergeDate),

            'Comment count': pr.comment_count,
            'Task count': pr.task_count,
            'Commit count': pr.commits.length,
            'Tasks used': pr.task_count > 0 ? 1 : 0,
            'Activity count': pr.activity.length,
            'Merge commit count': pr.commits.slice(1).filter(commit => commit.parents.length > 1).length
        }
    }
}