package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.api.BitbucketAPI
import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val api = promptUserAPICredentials()

        forEachPullRequest(api) {
            val correlationMatrix = it.pullRequests.correlationBetweenComponents()
            println(correlationMatrix)
        }
    }
}


