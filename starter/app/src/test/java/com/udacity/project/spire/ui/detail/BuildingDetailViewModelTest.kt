package com.udacity.project.spire.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus
import com.udacity.project.spire.testutil.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BuildingDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository = mockk<BuildingRepository>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads building detail on init`() = runTest {
        // Given
        val buildingId = 1
        val expectedBuilding = Building(buildingId, "Burj Khalifa", "", 828, 163, 2010, "Neo-futurism", "Tallest", VisitStatus.NOT_VISITED, "Dubai", "UAE")
        val savedStateHandle = SavedStateHandle(mapOf("buildingId" to buildingId))
        every { repository.getBuildingById(buildingId) } returns flowOf(expectedBuilding)

        // When
        val viewModel = BuildingDetailViewModel(repository, savedStateHandle)

        // Then
        assertEquals(expectedBuilding, viewModel.building.getOrAwaitValue())
    }

    @Test
    fun `updateVisitStatus success updates updateSuccess`() = runTest {
        // Given
        val buildingId = 1
        val savedStateHandle = SavedStateHandle(mapOf("buildingId" to buildingId))
        every { repository.getBuildingById(buildingId) } returns flowOf(null)
        coEvery { repository.updateBuildingVisitStatus(buildingId, VisitStatus.VISITED) } returns Result.success(Unit)

        val viewModel = BuildingDetailViewModel(repository, savedStateHandle)

        // When
        viewModel.updateVisitStatus(VisitStatus.VISITED)

        // Then
        val event = viewModel.updateSuccess.getOrAwaitValue()
        assertEquals("Building 1 updated with success", event.getContentIfNotHandled())
    }
}
