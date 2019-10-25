package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val api = promptUserAPICredentials()

        val pullRequests = mutableListOf<FetchPullRequestResult>()
        forEachPullRequest(api) {
            pullRequests.add(it)
        }

        pullRequests.forEach {
            println()
            println("Correlations for ${it.slug} against time from first commit to merge")
            val correlationMatrix = it.pullRequests.correlationBetweenComponents()
            println(correlationMatrix)
            println()
        }
    }
}


