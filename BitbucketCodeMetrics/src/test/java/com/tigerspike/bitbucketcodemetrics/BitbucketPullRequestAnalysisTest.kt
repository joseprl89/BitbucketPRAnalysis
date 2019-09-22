package com.tigerspike.bitbucketcodemetrics

import com.tigerspike.bitbucketcodemetrics.api.BitbucketAPI
import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.apache.commons.math3.linear.RealMatrix
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class BitbucketAPITest {

    private val user = "atlassian"
    private val repositorySlug = "teams-in-space-demo"
    private val prState = PullRequest.State.MERGED

    private val sut = BitbucketAPI.builder()
        .build()

    @Test
    fun testBitbucketAPI() {
        runBlocking {
            val pullRequests = sut.fetchPullRequests(user, repositorySlug, prState, size = 50).loadAll(
                10
            )
            assertThat(pullRequests.count(), `is`(not(equalTo(0))))

            val correlationMatrix = pullRequests.correlationBetweenComponents()

            print(correlationMatrix)
        }
    }
}

private fun RealMatrix.userFriendlyString(): String {
    return data.joinToString(separator = "\n\t", prefix = "Matrix: \n\t") {
        it.joinToString { value -> String.format("%.2f", value) }
    }
}
