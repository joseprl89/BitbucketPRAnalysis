const BitbucketAPI = require('./BitbucketAPI')
const PullRequestAnalysis = require('./PullRequestAnalysis.js')
const xl = require('excel4node');

var config = null
try {
    config = require('../config.json')
} catch (error) {
    console.error("Config file not found. Please ensure you create one as detailed in the readme file.")
    return
}

const api = new BitbucketAPI(config.authorization, config.filterByGitFlow, 10)

async function main() {
    const allRepositories = await Promise.all(config.repositories.map(async repo => {
        return {
            repository: repo,
            pullRequests: await api.loadRepository(repo, config.pagesToLoad)
        }
    }))

    var wb = new xl.Workbook();

    allRepositories.forEach(repoData => {
        let analysis = new PullRequestAnalysis(repoData.pullRequests)
        if (config.filterByGitFlow) analysis.filterByGitFlow()
        let measures = analysis.measures

        if (measures.length == 0) return;
        const keys = Object.keys(measures[0])

        var ws = wb.addWorksheet(repoData.repository);
        // header
        keys.forEach((value, index) => {
            ws.cell(1, index + 1).string(value)
        })
        measures.forEach((values, index) => {
            let row = index + 2 // index based on 1 plus header
            keys.forEach((key, column) => {
                ws.cell(row, column + 1).number(values[key])
            })
        })
    })

    wb.write('output.xlsx');
}

main()
