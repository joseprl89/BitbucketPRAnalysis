package com.tigerspike.bitbucketcodemetrics.api

import com.tigerspike.bitbucketcodemetrics.model.Activity
import com.tigerspike.bitbucketcodemetrics.model.FullCommit
import com.tigerspike.bitbucketcodemetrics.model.Paginated
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import retrofit2.HttpException

class BitbucketAPI private constructor(val client: BitbucketAPIClient) {

    class Builder {
        private var debug = false
        private var credentials: Credentials? = null

        fun debug(debug: Boolean): Builder {
            this.debug = debug
            return this
        }

        fun credentials(credentials: Credentials): Builder {
            this.credentials = credentials
            return this
        }

        fun build() = BitbucketAPI(BitbucketAPIClient(debug = debug, credentials = credentials))
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }

    suspend fun fetchPullRequests(
        user: String,
        repositorySlug: String,
        prState: PullRequest.State?,
        size: Int? = null,
        page: String? = null,
        filterTargetBranch: String? = null
    ): Paginated<PullRequest> {
        val result = client.apiDefinition.listRepos(
            user,
            repositorySlug,
            prState?.toString(),
            size = size,
            page = page
        )

        result.values
            .filter { filterTargetBranch == null || it.destination.branch.name == filterTargetBranch }
            .forEach {
                it.activity = loadActivity(user, repositorySlug, it).loadAll(10)
                it.commits = loadCommits(user, repositorySlug, it).loadAll(10).sortedBy { it.date }
            }

        result.pageLoader = { pageToLoad ->
            fetchPullRequests(user, repositorySlug, prState, size, pageToLoad, filterTargetBranch)
        }

        return result
    }

    suspend fun loadCommits(
        user: String,
        repositorySlug: String,
        pullRequest: PullRequest,
        size: Int? = 50,
        page: String? = null
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
                return Paginated(10, 0, "1", null, emptyList())
            }
            throw(e)
        }
    }

    suspend fun loadActivity(
        user: String,
        repositorySlug: String,
        pullRequest: PullRequest,
        size: Int? = 50,
        page: String? = null
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

