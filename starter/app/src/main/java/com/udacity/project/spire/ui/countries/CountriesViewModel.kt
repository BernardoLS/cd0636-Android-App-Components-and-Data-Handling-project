package com.udacity.project.spire.ui.countries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.ui.common.ErrorEvent
import com.udacity.project.spire.ui.common.Event
import kotlinx.coroutines.flow.catch

/**
 * ViewModel for CountriesFragment.
 * Loads list of unique countries from database.
 *
 * TODO #41: Implement CountriesViewModel
 *
 * This ViewModel:
 * 1. Loads all unique countries from repository
 * 2. Handles errors during data loading
 *
 * KEY CONCEPTS:
 * - Flow.catch(): Handle errors in Flow stream
 * - Flow.asLiveData(): Convert Flow to LiveData
 * - Simple ViewModel with read-only data
 */
class CountriesViewModel(
    repository: BuildingRepository
) : ViewModel() {

    // Error state exposed to UI
    private val _errorEvent = MutableLiveData<Event<ErrorEvent>>()
    val errorEvent: LiveData<Event<ErrorEvent>> = _errorEvent

    /**
     * TODO #41a: Initialize countries property
     *
     * Load all unique countries from repository with error handling.
     *
     * HINTS:
     * - Call repository.getAllCountries() to get Flow<List<String>>
     * - Use .catch() to handle errors and emit empty list
     * - Use .asLiveData() to convert Flow to LiveData
     * - Fragment displays this list in a RecyclerView
     */
    val countries: LiveData<List<String>>
        get() = TODO("Initialize countries LiveData - see TODO comment above")
}

/**
 * ViewModelFactory for CountriesViewModel.
 */
class CountriesViewModelFactory(
    private val repository: BuildingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountriesViewModel::class.java)) {
            return CountriesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
