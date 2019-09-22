package com.tigerspike.bitbucketcodemetrics

import com.tigerspike.bitbucketcodemetrics.api.BitbucketAPI
import com.tigerspike.bitbucketcodemetrics.api.Credentials
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import kotlinx.coroutines.runBlocking
import java.util.*

fun main() {
    val builder = BitbucketAPI.builder()

    if (readBoolean("Do you want to enter credentials?")) {
        builder.credentials(readCredentials())
    }

    val api = builder.build()

    runBlocking {
        do {
            val repositoryUsername = readString("Enter the username that hosts the repo (e.g. atlassian): ")
            val repositorySlug = readString("Enter the repository slug (name of the repo used in its URL): ")

            println("Downloading data. This will take a while...")
            val prs = api.fetchPullRequests(repositoryUsername, repositorySlug, PullRequest.State.MERGED, 50)
                .loadAll()

            val correlationMatrix = prs.correlationBetweenComponents()

            println(correlationMatrix)
        } while (readBoolean("Want to check another repository?"))
    }
}

fun readCredentials(): Credentials {
    val username = readString("Enter your BitBucket username:")
    val appPassword = readString("Enter your BitBucket app password with access to read PRs:")

    val credentials = Credentials(username, appPassword)
    println("Credentials: $credentials")
    println("Basic: ${credentials.basic()}")
    return credentials
}

fun readString(message: String): String {
    print("$message ")
    val result = readLine()

    return if (result != null && readBoolean("You entered \"$result\", is that correct?")) {
        result
    } else {
        readString(message)
    }
}

fun readBoolean(message: String): Boolean {
    print("$message (y/n) ")
    val result = readLine()

    return when(result?.toLowerCase(Locale.UK)) {
        "y", "yes" -> return true
        "n", "no" -> return false
        else -> readBoolean(message)
    }
}
