package com.tigerspike.bitbucketcodemetrics

import com.tigerspike.bitbucketcodemetrics.model.Paginated
import com.tigerspike.bitbucketcodemetrics.model.PullRequest

class BitbucketAPI private constructor(val client: BitbucketAPIClient) {

    companion object {
        fun client() = BitbucketAPI(BitbucketAPIClient())
    }

    suspend fun fetchPullRequests(
        user: String,
        repositorySlug: String,
        prState: PullRequest.State?,
        size: Int? = null,
        page: Int? = null
    ): Paginated<PullRequest> = client.pullRequestAPI.listRepos(user, repositorySlug, prState?.toString(), size = size, page = page).apply {
        pageLoader = { page ->
            fetchPullRequests(user, repositorySlug, prState, size, page)
        }
    }
}

