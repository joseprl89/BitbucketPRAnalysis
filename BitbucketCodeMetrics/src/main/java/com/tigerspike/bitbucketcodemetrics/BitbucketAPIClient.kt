package com.tigerspike.bitbucketcodemetrics

import com.google.gson.GsonBuilder
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import com.tigerspike.bitbucketcodemetrics.retrofit.BitbucketPullRequestDefinition
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BitbucketAPIClient(val baseURL: String = "https://api.bitbucket.org/2.0/") {

    val pullRequestAPI: BitbucketPullRequestDefinition

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(createOkHTTPClient())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        pullRequestAPI = retrofit.create(BitbucketPullRequestDefinition::class.java)
    }

    private fun createOkHTTPClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
