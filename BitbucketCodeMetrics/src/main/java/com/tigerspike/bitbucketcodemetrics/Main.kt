package com.tigerspike.bitbucketcodemetrics

import com.tigerspike.bitbucketcodemetrics.api.BitbucketAPI
import com.tigerspike.bitbucketcodemetrics.api.Credentials
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import kotlinx.coroutines.runBlocking
import java.lang.NumberFormatException
import java.util.*

fun main() {
    val builder = BitbucketAPI.builder()

    if (readBoolean("Do you want to enter credentials?")) {
        builder.credentials(readCredentials())
    }

    val api = builder.build()

    runBlocking {
        do {
            val repositoryUsername =
                readString("Enter the username that hosts the repo (e.g. atlassian): ")
            val repositorySlug =
                readString("Enter the repository slug (name of the repo used in its URL): ")

            var filterTargetBranch: String? = null
            if (readBoolean("Do you want to filter the PRs by a specific target branch?")) {
                filterTargetBranch =
                    readString("Please, specify which target branch we should filter for.")
            }

            var limit = 5
            if (readBoolean("Do you want to limit how many pages of PRs to load?")) {
                limit = readInt("Please, specify how many pages to load. Each page contains 50 PRs.")
            }

            println("Downloading data. This will take a while...")
            val prs = api.fetchPullRequests(
                repositoryUsername,
                repositorySlug,
                PullRequest.State.MERGED,
                filterTargetBranch = filterTargetBranch,
                size =50
            )
                .loadAll(limit = limit)

            val correlationMatrix = prs.correlationBetweenComponents()

            println(correlationMatrix)

        } while (readBoolean("Want to check another repository?"))
    }
}

fun readCredentials(): Credentials {
    val username = readString("Enter your BitBucket username:")
    val appPassword = readString("Enter your BitBucket app password with access to read PRs:")

    return Credentials(username, appPassword)
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

fun readInt(message: String): Int {
    print("$message ")
    val result = readLine()?.let { it.safeToInt() }

    return if (result != null && readBoolean("You entered \"$result\", is that correct?")) {
        result
    } else {
        readInt(message)
    }
}

private fun String.safeToInt(): Int? {
    try {
        return toInt()
    } catch (e: NumberFormatException) {
        return null
    }
}

fun readBoolean(message: String): Boolean {
    print("$message (y/n) ")
    val result = readLine()

    return when (result?.toLowerCase(Locale.UK)) {
        "y", "yes" -> return true
        "n", "no" -> return false
        else -> readBoolean(message)
    }
}
