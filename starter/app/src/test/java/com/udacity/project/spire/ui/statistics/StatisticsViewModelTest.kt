package com.udacity.project.spire.ui.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project.spire.data.repository.BuildingRepository
import com.udacity.project.spire.domain.model.BuildingStatistics
import com.udacity.project.spire.testutil.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class StatisticsViewModelTest {

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
    fun `loadStatistics success updates statistics`() = runTest {
        // Given
        val expectedStats = BuildingStatistics(10, 5, 2, 5000, 3)
        coEvery { repository.getStatistics() } returns expectedStats

        // When
        val viewModel = StatisticsViewModel(repository)

        // Then
        assertEquals(expectedStats, viewModel.statistics.getOrAwaitValue())
        assertEquals(false, viewModel.isLoading.getOrAwaitValue())
    }

    @Test
    fun `loadStatistics failure updates errorEvent and sets default stats`() = runTest {
        // Given
        coEvery { repository.getStatistics() } throws Exception("Load failed")

        // When
        val viewModel = StatisticsViewModel(repository)

        // Then
        assertEquals(BuildingStatistics(0, 0, 0, 0, 0), viewModel.statistics.getOrAwaitValue())
        val event = viewModel.errorEvent.getOrAwaitValue()
        assertEquals("Load failed", event.getContentIfNotHandled()?.message)
        assertEquals(false, viewModel.isLoading.getOrAwaitValue())
    }
}
