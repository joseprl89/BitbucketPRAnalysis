const readline = require('readline');
const fs = require('fs')

function askQuestion(query) {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
    });

    return new Promise(resolve => rl.question(query, ans => {
        rl.close();
        resolve(ans);
    }))
}

async function askBoolean(query) {
    let result = await askQuestion(query)
    if (result == 'y') return true
    if (result == 'n') return false
    return askBoolean(query)
}

async function main() {
    const username = await askQuestion("Enter your username: ");
    const bitbucketPassword = await askQuestion(`Enter your bitbucket app password ( see https://bitbucket.org/account/user/${username}/app-passwords ): `);

    var repositories = []
    do {
        let repository = await askQuestion("Enter repository (e.g. user/repository), leave empty to move on: ")
        if (repository == "") break
        repositories.push(repository)
    } while (true)

    let pagesToLoad = await askQuestion("Enter pages to load (each has roughly 50 PRs): ")

    let filterByGitFlow = await askBoolean("Do you want to filter branches to focus on git flow features (avoid hotfix/release/merges to master): (y/n) ")

    const config = {
        "authorization": {
            "username": username,
            "password": bitbucketPassword
        },
        "repositories": repositories,
        "pagesToLoad": pagesToLoad,
        "filterByGitFlow": filterByGitFlow
    }

    console.log("Configuration entered: ")
    console.log()
    console.log(JSON.stringify(config, null, 2))
    console.log()

    if (await askBoolean("Is the information entered correct? (y/n) ")) {
        if (fs.existsSync('config.json') && !await askBoolean('Config file already exists. Do you want to overwrite it? (y/n) ')) {
            return
        }

        console.log("Writing to config.json...")
        fs.writeFileSync('config.json', JSON.stringify(config, null, 2))
    } else {
        await main()
    }
}

main()
