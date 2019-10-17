package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.model.asCSV
import kotlinx.coroutines.runBlocking

fun main() {
    val api = promptUserAPICredentials()

    runBlocking {
        do {
            val prs = fetchPullRequests(api)
            val csv = prs.asCSV()
            println(csv)
        } while (readBoolean("Want to check another repository?"))
    }
}
