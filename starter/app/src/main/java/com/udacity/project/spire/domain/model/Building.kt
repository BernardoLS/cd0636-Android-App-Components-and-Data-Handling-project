package com.udacity.project.spire.domain.model

import com.udacity.project.spire.data.remote.dto.CityDto

/**
 * Domain model representing a tall building/skyscraper.
 * This is the core business object used throughout the app.
 *
 * NOTE: This class is complete - no implementation needed.
 * Review the properties to understand the data model before implementing entities.
 */
data class Building(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val heightMeters: Int,
    val floors: Int,
    val yearCompleted: Int,
    val architecturalStyle: String,
    val description: String,
    val visitStatus: VisitStatus,
    val city: String,
    val country: String
)
