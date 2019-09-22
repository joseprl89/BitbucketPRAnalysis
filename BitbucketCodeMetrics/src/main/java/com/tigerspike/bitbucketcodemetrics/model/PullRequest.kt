package com.tigerspike.bitbucketcodemetrics.model

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
import java.lang.Exception
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class PullRequest(
    val id: String,
    val description: String,
    val title: String,
    val close_source_branch: Boolean,
    val created_on: ZonedDateTime,
    val comment_count: Int,
    val task_count: Int,
    val updated_on: ZonedDateTime,
    val destination: CommitAddress,
    val source: CommitAddress,
    val links: Map<String, Link>,
    val type: String,
    val state: State,
    val reason: String,
    val author: User,
    val merge_commit: Commit,
    val closed_by: User
) {
    var activity: List<Activity>? = null
    var commits: List<FullCommit>? = null

    enum class State {
        MERGED, OPEN, SUPERSEDED, DECLINED
    }

    companion object {
        fun componentNames() = listOf(
            "Time between first commit and PR creation",
            "Time between creation and approval",
            "Time between first commit and approval",
            "Time between approval and merge",
            "Number of comments",
            "Number of tasks",
            "Number of commits",
            "Using tasks",
            "Activity in PR",
            "Merge commit count"
        )
    }

    fun components(): DoubleArray {
        val firstApproval = firstApproval()
        val firstCommitDate = firstCommitDate()
        val mergeTime = mergeTime()

        return doubleArrayOf(
            ChronoUnit.SECONDS.between(firstCommitDate, mergeTime).toDouble(),
            ChronoUnit.SECONDS.between(firstCommitDate, created_on).toDouble(),
            ChronoUnit.SECONDS.between(created_on, firstApproval).toDouble(),
            ChronoUnit.SECONDS.between(firstCommitDate, firstApproval).toDouble(),
            ChronoUnit.SECONDS.between(firstApproval, mergeTime).toDouble(),
            comment_count.toDouble(),
            task_count.toDouble(),
            commits!!.size.toDouble(),
            if (task_count != 0) 1.0 else 0.0,
            activity!!.count().toDouble(),
            mergeCommitCount()
        )
    }

    private fun mergeCommitCount() = commits!!.count { it.parents.count() > 1 }.toDouble()

    private fun firstCommitDate() = commits!!.first().date

    private fun mergeTime(): ZonedDateTime {
        try {
            return activity!!.first { it.update?.state == State.MERGED }.update!!.date
        } catch (e: Exception) {
            throw Exception("Failed to retrieve merge time for PR ${this.id} with activity: ${this.activity}", e)
        }
    }

    private fun firstApproval(): ZonedDateTime? {
        return activity?.mapNotNull { it.approval?.date }?.min()
    }

    fun isValid() = hasApprovals() && hasCommits()
    fun hasApprovals() = firstApproval() != null
    fun hasCommits() = commits!!.isNotEmpty()
}

fun List<PullRequest>.correlationBetweenComponents(): String {
    val pearsonCorrelationData = filter { it.isValid() }.map { it.components() }.toTypedArray()
    val corrInstance = PearsonsCorrelation(pearsonCorrelationData)

    val correlationsToTimeToMerge = corrInstance.correlationMatrix.getColumn(0).drop(1)

    return correlationsToTimeToMerge
        .zip(PullRequest.componentNames())
        .sortedByDescending { if (it.first.isNaN()) -1.0 else abs(it.first) }
        .map { "${it.second}:\t\t${it.first.format(2)}" }
        .joinToString(separator = "\n")
}

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
