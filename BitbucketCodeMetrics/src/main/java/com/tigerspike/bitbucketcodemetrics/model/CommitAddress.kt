package com.tigerspike.bitbucketcodemetrics.model

import java.time.ZonedDateTime

data class CommitAddress(val commit: Commit, val branch: Branch, val repository: Repository)

data class Commit(val type: String, val hash: String, val links: Map<String, Link>)

data class FullCommit(
    val type: String,
    val hash: String,
    val links: Map<String, Link>,
    // Extra data
    val date: ZonedDateTime,
    val message: String,
    val summary: Summary,
    val parents: List<Commit>,
    val repository: Repository,
    val author: User
)

data class Branch(val name: String)

data class Repository(
    val type: String,
    val name: String,
    val full_name: String,
    val uuid: String,
    val links: Map<String, Link>
)