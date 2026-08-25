package com.udacity.project.spire.ui.visits

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus
import com.udacity.project.spire.testutil.getOrAwaitValue
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyVisitsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository = mockk<BuildingRepository>()
    private lateinit var viewModel: MyVisitsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock default behavior for init
        every { repository.getBuildingsByVisitStatus(any()) } returns flowOf(emptyList())
        
        viewModel = MyVisitsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial status should be VISITED`() {
        assertEquals(VisitStatus.VISITED, viewModel.currentStatus.getOrAwaitValue())
    }

    @Test
    fun `setFilterStatus updates currentStatus`() {
        // When
        viewModel.setFilterStatus(VisitStatus.BUCKET_LIST)

        // Then
        assertEquals(VisitStatus.BUCKET_LIST, viewModel.currentStatus.getOrAwaitValue())
    }

    @Test
    fun `buildings LiveData updates when status changes`() {
        // Given
        val visitedBuildings = listOf(
            Building(1, "Burj Khalifa", "", 828, 163, 2010, "Neo-futurism", "Tallest", VisitStatus.VISITED, "Dubai", "UAE")
        )
        val bucketListBuildings = listOf(
            Building(2, "Empire State", "", 381, 102, 1931, "Art Deco", "Iconic", VisitStatus.BUCKET_LIST, "New York", "USA")
        )

        every { repository.getBuildingsByVisitStatus(VisitStatus.VISITED) } returns flowOf(visitedBuildings)
        every { repository.getBuildingsByVisitStatus(VisitStatus.BUCKET_LIST) } returns flowOf(bucketListBuildings)

        // Re-init to trigger first emission with visitedBuildings if necessary, 
        // or just rely on the fact that switchMap will react.
        // Actually, the initial status is VISITED, so it should already have visitedBuildings if mocked before init.
        
        val viewModel = MyVisitsViewModel(repository)

        // Then (Initial)
        assertEquals(visitedBuildings, viewModel.buildings.getOrAwaitValue())

        // When
        viewModel.setFilterStatus(VisitStatus.BUCKET_LIST)

        // Then
        assertEquals(bucketListBuildings, viewModel.buildings.getOrAwaitValue())
    }
}
