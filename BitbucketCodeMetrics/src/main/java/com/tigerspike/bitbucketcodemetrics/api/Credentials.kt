package com.tigerspike.bitbucketcodemetrics.api

data class Credentials(val user: String, val appPassword: String) {
    fun basic(): String {
        return okhttp3.Credentials.basic(user, appPassword)
    }
}