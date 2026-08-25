package com.udacity.project.spire.ui.buildings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.udacity.project.spire.data.repository.BuildingRepository
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
class BuildingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository = mockk<BuildingRepository>()
    private lateinit var viewModel: BuildingsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getBuildings() } returns flowOf(PagingData.empty())
        viewModel = BuildingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh failure updates errorEvent`() = runTest {
        // Given
        val exception = Exception("Refresh failed")
        coEvery { repository.refreshBuildings() } returns Result.failure(exception)

        // When
        viewModel.refresh()

        // Then
        val event = viewModel.errorEvent.getOrAwaitValue()
        val errorEvent = event.getContentIfNotHandled()
        assertEquals("Refresh failed", errorEvent?.message)
    }
}
