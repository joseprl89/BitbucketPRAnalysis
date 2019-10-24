package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.model.asCSV
import kotlinx.coroutines.runBlocking
import java.io.File

fun main() {
    runBlocking {
        val api = promptUserAPICredentials()

        forEachPullRequest(api) {
            val csv = it.pullRequests.asCSV()

            val fileToWrite = File("output/" + it.slug + ".csv")
            fileToWrite.writeText(csv)
            println("CSV written to " + fileToWrite.absolutePath)
        }
    }
}
