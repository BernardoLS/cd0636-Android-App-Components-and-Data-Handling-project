package com.udacity.project.spire.domain.model

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
    val heightMeter: Float,
    val floors: Int,
    val yearCompleted: Int,
    val architecturalStyle: String,
    val description: String,
    val visitStatus: VisitStatus,
    val cityName: String,
    val countryName: String
)
