package com.udacity.project.spire.ui.common

/**
 * Represents an error event that should be shown to the user.
 */
data class ErrorEvent(
    val message: String,
    val throwable: Throwable? = null
)

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * This prevents the same event from being processed multiple times (e.g., after configuration change).
 */
class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}