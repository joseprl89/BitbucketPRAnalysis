package com.tigerspike.bitbucketcodemetrics.retrofit

import com.tigerspike.bitbucketcodemetrics.model.Paginated
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BitbucketPullRequestDefinition {
    @GET("repositories/{user}/{repositorySlug}/pullrequests")
    suspend fun listRepos(
        @Path("user") user: String,
        @Path("repositorySlug") repositorySlug: String,
        @Query("state") status: String?,
        @Query("page") page: Int?,
        @Query("pagelen") size: Int?
    ): Paginated<PullRequest>
}
