package com.udacity.project.spire

import android.app.Application
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.data.repository.MockBuildingRepository

class SpireApplication : Application() {

    lateinit var buildingRepository: BuildingRepository
        private set

    override fun onCreate() {
        super.onCreate()
        initializeRepository()
    }

    private fun initializeRepository() {
        // TODO: Replace MockBuildingRepository with DefaultBuildingRepository
        // after implementing Room database, API service, and repository methods
        //
        // Expected implementation:
        // val database = SpireDatabase.getInstance(this)
        // val apiService = ApiServiceProvider.getApiService()
        // buildingRepository = DefaultBuildingRepository(database, apiService)

        // Temporary mock for app to run while implementing:
        buildingRepository = MockBuildingRepository()
    }
}