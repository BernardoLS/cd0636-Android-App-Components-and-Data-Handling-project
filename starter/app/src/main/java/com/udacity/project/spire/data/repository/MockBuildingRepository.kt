package com.udacity.project.spire.data.repository

import androidx.paging.PagingData
import com.udacity.project.spire.data.remote.dto.PaginationMetadata
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.BuildingStatistics
import com.udacity.project.spire.domain.model.VisitStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Mock implementation of BuildingRepository for temporary use.
 * Provides stub data to allow the app to compile and run while students
 * are implementing the real repository.
 *
 * NOTE: This is a TEMPORARY mock - students will replace this with DefaultBuildingRepository
 * once they complete the implementation tasks.
 */
class MockBuildingRepository : BuildingRepository {

    override fun getBuildings(): Flow<PagingData<Building>> {
        // Return empty PagingData
        return flowOf(PagingData.empty())
    }

    override fun getBuildingById(id: Int): Flow<Building?> {
        // Return null (no building found)
        return flowOf(null)
    }

    override suspend fun refreshBuildings(): Result<Unit> {
        // Always succeed with no action
        return Result.success(Unit)
    }

    override suspend fun refreshBuildingsPaginated(
        page: Int,
        limit: Int
    ): Result<PaginationMetadata?> {
        // Return success with null metadata
        return Result.success(null)
    }

    override suspend fun updateBuildingVisitStatus(
        buildingId: Int,
        status: VisitStatus
    ): Result<Unit> {
        // Always succeed with no action
        return Result.success(Unit)
    }

    override fun getBuildingsByCountry(country: String): Flow<List<Building>> {
        // Return empty list
        return flowOf(emptyList())
    }

    override fun getBuildingsByVisitStatus(status: VisitStatus): Flow<List<Building>> {
        // Return empty list
        return flowOf(emptyList())
    }

    override fun getAllCountries(): Flow<List<String>> {
        // Return empty list
        return flowOf(emptyList())
    }

    override suspend fun getStatistics(): BuildingStatistics {
        // Return empty statistics
        return BuildingStatistics(
            totalBuildings = 0,
            visitedCount = 0,
            bucketListCount = 0,
            totalMetersClimbed = 0,
            countriesExplored = 0
        )
    }
}
