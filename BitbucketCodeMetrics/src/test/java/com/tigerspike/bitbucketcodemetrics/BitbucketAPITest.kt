package com.tigerspike.bitbucketcodemetrics

import com.tigerspike.bitbucketcodemetrics.model.PullRequest
import com.tigerspike.bitbucketcodemetrics.model.correlationBetweenComponents
import kotlinx.coroutines.runBlocking
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
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
            val pullRequests = sut.fetchPullRequests(user, repositorySlug, prState, size = 50).loadAll()
            assertThat(pullRequests.count(), `is`(not(equalTo(0))))

            val correlationMatrix = pullRequests.correlationBetweenComponents()

            print(correlationMatrix)
        }
    }
}

private fun RealMatrix.userFriendlyString(): String {
    return data.joinToString(separator = "\n\t", prefix = "Matrix: \n\t") {
        it.joinToString { value -> "%.2f".format(value) }
    }
}
