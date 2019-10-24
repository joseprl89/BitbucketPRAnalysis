package com.tigerspike.bitbucketcodemetrics.executables

import com.tigerspike.bitbucketcodemetrics.api.BitbucketAPI
import com.tigerspike.bitbucketcodemetrics.api.Credentials
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import java.io.File
import java.lang.NumberFormatException
import java.util.*
import java.io.IOException
import java.io.FileInputStream

data class PullRequestAddress(val username: String, val slug: String) {
    companion object {
        fun from(address: String): PullRequestAddress? {
            val split = address.split("/")
            if (split.size != 2) {
                return null
            }
            return PullRequestAddress(split[0], split[1])
        }
    }
}

data class FetchPullRequestResult(val slug: String, val pullRequests: List<PullRequest>)

suspend fun forEachPullRequest(api: BitbucketAPI, function: suspend (FetchPullRequestResult) -> Unit) {
    if (!withProperties { prop ->
            val repositories = extractRepositories(prop.getProperty("bitbucket.repositories"))
            val pagesToLoad = prop.getProperty("bitbucket.pages.to.load").toIntOrNull() ?: 1

            repositories.forEach {
                val result = fetchPullRequestResult(
                    api,
                    it,
                    prop.getProperty("bitbucket.filter.target.branch"),
                    pagesToLoad
                )

                function(result)
            }

            repositories.isNotEmpty()
        }) {

        do {
            val result = fetchPullRequests(api)
            val correlationMatrix = result.pullRequests.correlationBetweenComponents()
            println(correlationMatrix)
        } while (readBoolean("Want to check another repository?"))

    }
}

fun extractRepositories(property: String?): List<PullRequestAddress> {
    return property?.split(",")?.map { PullRequestAddress.from(it) }?.filterNotNull() ?: emptyList()
}

suspend fun fetchPullRequests(api: BitbucketAPI): FetchPullRequestResult {
    val repositoryUsername =
        readString("Enter the username that hosts the repo (e.g. atlassian): ")
    val repositorySlug =
        readString("Enter the repository slug (name of the repo used in its URL): ")

    var filterTargetBranch: String? = null
    if (readBoolean("Do you want to filter the PRs by a specific target branch?")) {
        filterTargetBranch =
            readString("Please, specify which target branch we should filter for.")
    }

    var limit = 1
    if (readBoolean("Do you want to limit how many pages of PRs to load (1 page of 50 PRs by default)?")) {
        limit =
            readInt("Please, specify how many pages to load. Each page contains 50 PRs.")
    }

    return fetchPullRequestResult(
        api,
        PullRequestAddress(repositoryUsername, repositorySlug),
        filterTargetBranch,
        limit
    )
}

suspend fun fetchPullRequestResult(
    api: BitbucketAPI,
    address: PullRequestAddress,
    filterTargetBranch: String?,
    limit: Int
): FetchPullRequestResult {
    println("Downloading data for $address. This will take a while...")

    return optRetry {
        FetchPullRequestResult(
            address.slug, api.fetchPullRequests(
                address.username,
                address.slug,
                PullRequest.State.MERGED,
                filterTargetBranch = filterTargetBranch,
                size = 50
            ).loadAll(limit = limit)
        )
    }
}

suspend fun <T> optRetry(lambda: suspend () -> T): T {
    return try {
        lambda()
    } catch (e: Exception) {
        println("Exception!")
        e.printStackTrace()
        if (readBoolean("Want to retry?")) optRetry(lambda) else throw e
    }
}

suspend fun promptUserAPICredentials(): BitbucketAPI {
    val builder = BitbucketAPI.builder()

    if (!withProperties {
            val username = it.getProperty("bitbucket.username")
            val password = it.getProperty("bitbucket.password")
            builder.credentials(Credentials(username, password))
            true
        }) {
        if (readBoolean("Do you want to enter credentials?")) {
            builder.credentials(readCredentials())
        }
    }

    return builder.build()
}

suspend fun withProperties(function: suspend (Properties) -> Boolean): Boolean {
    try {
        val file = File("config.properties")
        println(file.absolutePath)
        FileInputStream(file).use { input ->

            val prop = Properties()

            // load a properties file
            prop.load(input)

            return function(prop)
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
        return false
    }
}

fun readCredentials(): Credentials {
    val username =
        readString("Enter your BitBucket username:")
    val appPassword =
        readString("Enter your BitBucket app password with access to read PRs:")

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
    val result = readLine()?.safeToInt()

    return if (result != null && readBoolean("You entered \"$result\", is that correct?")) {
        result
    } else {
        readInt(message)
    }
}

private fun String.safeToInt(): Int? {
    return try {
        toInt()
    } catch (e: NumberFormatException) {
        null
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