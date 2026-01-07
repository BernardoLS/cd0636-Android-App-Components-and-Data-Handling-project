package com.udacity.project.spire.domain.model

/**
 * Statistics about the user's building collection and visits.
 * Used to display progress and achievements.
 */
data class BuildingStatistics(
    val totalBuildings: Int,
    val visitedCount: Int,
    val bucketListCount: Int,
    val totalMetersClimbed: Int,
    val countriesExplored: Int
)