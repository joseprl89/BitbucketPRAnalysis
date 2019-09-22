package com.tigerspike.bitbucketcodemetrics.retrofit

import com.tigerspike.bitbucketcodemetrics.model.Activity
import com.tigerspike.bitbucketcodemetrics.model.FullCommit
import com.tigerspike.bitbucketcodemetrics.model.Paginated
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIDefinition {
    @GET("repositories/{user}/{repositorySlug}/pullrequests")
    suspend fun listRepos(
        @Path("user") user: String,
        @Path("repositorySlug") repositorySlug: String,
        @Query("state") status: String?,
        @Query("page") page: Int?,
        @Query("pagelen") size: Int?
    ): Paginated<PullRequest>


    @GET("repositories/{user}/{repositorySlug}/pullrequests/{pullRequestId}/commits")
    suspend fun listPullRequestCommits(
        @Path("user") user: String,
        @Path("repositorySlug") repositorySlug: String,
        @Path("pullRequestId") pullRequestId: String,
        @Query("page") page: Int?,
        @Query("pagelen") size: Int?
    ): Paginated<FullCommit>

    @GET("repositories/{user}/{repositorySlug}/pullrequests/{pullRequestId}/activity")
    suspend fun listPullRequestActivity(
        @Path("user") user: String,
        @Path("repositorySlug") repositorySlug: String,
        @Path("pullRequestId") pullRequestId: String,
        @Query("page") page: Int?,
        @Query("pagelen") size: Int?
    ): Paginated<Activity>

}
