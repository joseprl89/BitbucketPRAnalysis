package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import kotlinx.coroutines.runBlocking

fun main() {
    val api = promptUserAPICredentials()

    runBlocking {
        do {
            val result = fetchPullRequests(api)
            val correlationMatrix = result.pullRequests.correlationBetweenComponents()
            println(correlationMatrix)
        } while (readBoolean("Want to check another repository?"))
    }
}



