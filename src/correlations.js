const BitbucketAPI = require('./BitbucketAPI')
const PullRequestAnalysis = require('./PullRequestAnalysis.js')

var config
try {
    config = require('../config.json')
} catch (error) {
    console.error("Config file not found. Please ensure you create one as detailed in the readme file.")
    return
}

const api = new BitbucketAPI(config.authorization, config.filterByGitFlow || false, 10)

async function main() {
    const allRepositories = await Promise.all(config.repositories.map(async repo => {
        return {
            repository: repo,
            pullRequests: await api.loadRepository(repo, config.pagesToLoad)
        }
    }))

    console.log()
    allRepositories.forEach(repoData => {
        let analysis = new PullRequestAnalysis(repoData.pullRequests)
        if (config.filterByGitFlow) analysis.filterByGitFlow()
        
        console.log(`Analysis for ${repoData.repository}`)
        console.log()
        console.log(analysis.pearsonCorrelation())
        console.log()
        console.log()
    })
}

main()
