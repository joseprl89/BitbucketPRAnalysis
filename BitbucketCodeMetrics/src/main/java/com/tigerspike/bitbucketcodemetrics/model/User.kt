package com.tigerspike.bitbucketcodemetrics.model

data class User(
    val display_name: String,
    val uuid: String,
    val links: Map<String, Link>,
    val nickname: String,
    val type: String,
    val account_id: String
)