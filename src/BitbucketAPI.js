const fetch = require("node-fetch");

let pagelen = 50

module.exports = class BitbuketAPI {

  constructor(authorization) {
    if (authorization) {
      let headers = new fetch.Headers();
      let hash = Buffer.from(authorization.username + ":" + authorization.password).toString('base64')
      headers.set('Authorization', 'Basic ' + hash);

      this.headers = headers
    } else { 
      this.headers = new fetch.Headers()
    }
  }

  async loadRepository(repo, pagesToLoad) {
    let pullRequests = await this.loadMergedPullRequests(repo, pagesToLoad)
    await Promise.all(pullRequests.map(async pr => await this.loadPullRequestMetadata(repo, pr)))
    return pullRequests.sort((a, b) => { return parseInt(b.id) - parseInt(a.id)})
  }

  async loadPullRequestMetadata(repo, pullRequest) {
    pullRequest.commits = []
    pullRequest.activity = []

    try {
        pullRequest.commits = await this.loadPullRequestsCommits(repo, pullRequest)
    } catch(error) {
        console.error(`Failed to load commits for PR ${pullRequest.id} ${error}`)
    }

    try {
        pullRequest.activity = await this.loadPullRequestsActivity(repo, pullRequest)
    } catch(error) {
        console.error(`Failed to load activity for PR ${pullRequest.id} ${error}`)
    }
  }

  async loadMergedPullRequests(repository, pagesToLoad) {
    return this._loadPagesAt(
      `https://api.bitbucket.org/2.0/repositories/${repository}/pullrequests?state=MERGED&pagelen=${pagelen}`, 
      pagesToLoad
    )
  }

  async loadPullRequestsCommits(repository, pullRequest) {
    const pullRequestId = pullRequest.id
    return this._loadPagesAt(
      `https://api.bitbucket.org/2.0/repositories/${repository}/pullrequests/${pullRequestId}/commits?pagelen=${pagelen}`,
      10000
    )
  }

  async loadPullRequestsActivity(repository, pullRequest) {
    const pullRequestId = pullRequest.id
    return this._loadPagesAt(
      `https://api.bitbucket.org/2.0/repositories/${repository}/pullrequests/${pullRequestId}/activity?pagelen=${pagelen}`,
      10000
    )
  }

  async _loadPagesAt(url, pagesToLoad) {
    var result = []

    do {
      const json = await this._getData(url)
      
      if (!json) {
        console.error(`No JSON object found at url ${url}!`)
        return result
      }

      if (!json.values) {
        console.error(`No values found in a response to url ${url}!`)
        return result
      }

      result = result.concat(json.values)

      if (!json.next) {
        return result
      }

      pagesToLoad--
      url = json.next
    } while (pagesToLoad > 0 && url)
    return result
  }
  
  async _getData(url, retryCount = 3) {
    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: this.headers
      });
      const json = await response.json();

      if (!json) {
        if (retryCount < 1) {
          throw `There was an error while loading data from ${url}`
        }
        return this._getData(url, retryCount - 1)
      } else {
        return json;
      }
    } catch (error) {
      console.error(error);
    }
  };
  
}