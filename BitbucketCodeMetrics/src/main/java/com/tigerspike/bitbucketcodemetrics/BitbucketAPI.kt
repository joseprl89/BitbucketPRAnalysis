package com.tigerspike.bitbucketcodemetrics

import com.tigerspike.bitbucketcodemetrics.model.Activity
import com.tigerspike.bitbucketcodemetrics.model.FullCommit
import com.tigerspike.bitbucketcodemetrics.model.Paginated
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import retrofit2.HttpException

class BitbucketAPI private constructor(val client: BitbucketAPIClient) {

    companion object {
        fun client(debug: Boolean = false) = BitbucketAPI(BitbucketAPIClient(debug = debug))
    }

    suspend fun fetchPullRequests(
        user: String,
        repositorySlug: String,
        prState: PullRequest.State?,
        size: Int? = null,
        page: Int? = null
    ): Paginated<PullRequest> {
        val result = client.apiDefinition.listRepos(
            user,
            repositorySlug,
            prState?.toString(),
            size = size,
            page = page
        )

        result.values.filter { it.isValid() }.forEach {
            it.activity = loadActivity(user, repositorySlug, it).loadAll()
            it.commits = loadCommits(user, repositorySlug, it).loadAll().sortedBy { it.date }
        }

        result.pageLoader = { pageToLoad ->
            fetchPullRequests(user, repositorySlug, prState, size, pageToLoad)
        }

        return result
    }

    suspend fun loadCommits(
        user: String,
        repositorySlug: String,
        pullRequest: PullRequest,
        size: Int? = 30,
        page: Int? = null
    ): Paginated<FullCommit> {
        return try {
            client.apiDefinition.listPullRequestCommits(
                user,
                repositorySlug,
                pullRequest.id,
                size = size,
                page = page
            ).apply {
                pageLoader = { page ->
                    loadCommits(user, repositorySlug, pullRequest, size, page)
                }
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                // Some PRs have no commits if another branch merged it.
                return Paginated(10, 0, 1, null, emptyList())
            }
            throw(e)
        }
    }

    suspend fun loadActivity(
        user: String,
        repositorySlug: String,
        pullRequest: PullRequest,
        size: Int? = 30,
        page: Int? = null
    ): Paginated<Activity> {
        return client.apiDefinition.listPullRequestActivity(
            user,
            repositorySlug,
            pullRequest.id,
            page,
            size
        ).apply {
            pageLoader = { page ->
                loadActivity(user, repositorySlug, pullRequest, size, page)
            }
        }
    }
}

