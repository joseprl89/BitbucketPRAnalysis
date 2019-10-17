package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import kotlinx.coroutines.runBlocking

fun main() {
    val api = promptUserAPICredentials()

    runBlocking {
        do {
            val prs = fetchPullRequests(api)
            val correlationMatrix = prs.correlationBetweenComponents()
            println(correlationMatrix)
        } while (readBoolean("Want to check another repository?"))
    }
}



