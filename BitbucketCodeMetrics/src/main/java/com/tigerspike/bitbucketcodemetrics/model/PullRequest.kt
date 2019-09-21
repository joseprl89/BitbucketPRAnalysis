package com.tigerspike.bitbucketcodemetrics.model

data class PullRequest(
    val id: String,
    val description: String,
    val title: String,
    val close_source_branch: Boolean,
    val created_on: String,
    val comment_count: Int,
    val task_count: Int,
    val updated_on: String,
    val destination: CommitAddress,
    val source: CommitAddress,
    val links: Map<String, Link>,
    val type: String,
    val state: State,
    val reason: String,
    val author: User,
    val merge_commit: Commit,
    val closed_by: User
) {
    enum class State {
        MERGED, OPEN, SUPERSEDED, DECLINED
    }
}