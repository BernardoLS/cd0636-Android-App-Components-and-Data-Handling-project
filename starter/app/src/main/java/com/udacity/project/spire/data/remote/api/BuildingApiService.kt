package com.udacity.project.spire.data.remote.api

import com.udacity.project.spire.data.remote.dto.BuildingsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service interface for fetching building data.
 * Define all API endpoints here.
 */
interface BuildingApiService {

    /**
     * Fetch buildings from the remote API with pagination.
     * Default page size is 10 items per page.
     *
     * @param page The page number (starting from 1)
     * @param limit The number of items per page (default: 10)
     * @return BuildingsResponse containing paginated list of buildings with metadata
     */
    @GET("buildings")
    suspend fun getBuildingsPaginated(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10
    ): BuildingsResponse
}