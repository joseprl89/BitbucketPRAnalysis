package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.model.asCSV
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    val api = promptUserAPICredentials()

    runBlocking {
        do {
            val result = fetchPullRequests(api)
            val csv = result.pullRequests.asCSV()

            val fileToWrite = File(result.slug + ".csv")
            fileToWrite.writeText(csv)
            println("CSV written to " + fileToWrite.absolutePath)
        } while (readBoolean("Want to check another repository?"))
    }
}
