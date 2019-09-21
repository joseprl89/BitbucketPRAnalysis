package com.tigerspike.bitbucketcodemetrics

import com.tigerspike.bitbucketcodemetrics.model.Paginated
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class BitbucketAPITest {

    val user = "atlassian"
    val repositorySlug = "teams-in-space-demo"
    val prState = PullRequest.State.MERGED

    val sut = BitbucketAPI.client()

    @Test
    fun testBitbucketAPI() {
        runBlocking {
            val result = sut.fetchPullRequests(user, repositorySlug, prState, size = 50)
            assertThat(result.values.count(), `is`(not(equalTo(0))))

            val pullRequests = mutableListOf<PullRequest>()
            var nextPage: Paginated<PullRequest>? = result
            do {
                nextPage?.let { pullRequests.addAll(it.values) }
                nextPage = nextPage?.loadNextPage()
                assertThat(nextPage?.values?.count(), `is`(not(equalTo(0))))
            } while (nextPage != null)

            assertThat(pullRequests.count(), `is`(equalTo(result.size)))
        }
    }
}