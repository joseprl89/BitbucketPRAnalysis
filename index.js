const BitbucketAPI = require('./src/BitbucketAPI')
const PullRequestAnalysis = require('./src/PullRequestAnalysis.js')
const xl = require('excel4node');

const config = require('./config.json')
const api = new BitbucketAPI(config.authorization)

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
