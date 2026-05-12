package com.udacity.project.spire.domain.model

/**
 * Represents the visit status of a building.
 * This enum is used to track whether a user has visited a building,
 * plans to visit it, or hasn't considered it yet.
 */
enum class VisitStatus {
    /** Building has not been visited and is not on the bucket list */
    NOT_VISITED,

    /** Building is on the user's bucket list to visit */
    BUCKET_LIST,

    /** Building has been visited by the user */
    VISITED
}