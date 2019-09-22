package com.tigerspike.bitbucketcodemetrics.model

import java.time.ZonedDateTime

data class Activity(val update: Update?, val approval: Approval?)

data class Update(
    val description: String,
    val title: String,
    val destination: CommitAddress,
    val reason: String,
    val source: CommitAddress,
    val state: PullRequest.State,
    val author: User,
    val date: ZonedDateTime
)

data class Approval(
    val user: User,
    val date: ZonedDateTime
)