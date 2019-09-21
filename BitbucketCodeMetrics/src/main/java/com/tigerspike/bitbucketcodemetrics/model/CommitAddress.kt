package com.tigerspike.bitbucketcodemetrics.model

data class CommitAddress(val commit: Commit, val branch: Branch, val repository: Repository)

data class Commit(val type: String, val hash: String, val links: Map<String, Link>)

data class Branch(val name: String)

data class Repository(
    val type: String,
    val name: String,
    val full_name: String,
    val uuid: String,
    val links: Map<String, Link>
)