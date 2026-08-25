package com.udacity.project.spire.data.local.dao

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.udacity.project.spire.data.local.database.SpireDatabase
import com.udacity.project.spire.data.local.entity.BuildingEntity
import com.udacity.project.spire.data.local.entity.CityEntity
import com.udacity.project.spire.data.local.entity.CountryEntity
import com.udacity.project.spire.data.local.entity.VisitStatusEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BuildingDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: SpireDatabase
    private lateinit var buildingDao: BuildingDao
    private lateinit var cityDao: CityDao
    private lateinit var countryDao: CountryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SpireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        buildingDao = database.buildingDao()
        cityDao = database.cityDao()
        countryDao = database.countryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert and get building`() = runTest {
        // Given
        val country = CountryEntity(1, "United Arab Emirates", "UAE")
        countryDao.insertCountry(country)
        
        val city = CityEntity(1, "Dubai", 1)
        cityDao.insertCity(city)
        
        val building = BuildingEntity(
            id = 1,
            name = "Burj Khalifa",
            imageUrl = "http://example.com/image.jpg",
            heightMeters = 828,
            floors = 163,
            yearCompleted = 2010,
            architecturalStyle = "Neo-futurism",
            description = "The tallest building in the world.",
            visitStatus = VisitStatusEntity.VISITED,
            cityId = 1
        )
        
        // When
        buildingDao.insertBuildings(listOf(building))
        
        // Then
        val result = buildingDao.getBuildingById(1).first()
        assertEquals("Burj Khalifa", result?.building?.name)
        assertEquals("Dubai", result?.city?.city?.name)
        assertEquals("United Arab Emirates", result?.city?.country?.name)
    }

    @Test
    fun `getBuildingsByVisitStatus returns filtered results`() = runTest {
        // Given
        val country = CountryEntity(1, "UAE", "UAE")
        countryDao.insertCountry(country)
        val city = CityEntity(1, "Dubai", 1)
        cityDao.insertCity(city)

        val building1 = BuildingEntity(1, "B1", "", 100, 10, 2000, "", "", VisitStatusEntity.VISITED, 1)
        val building2 = BuildingEntity(2, "B2", "", 200, 20, 2010, "", "", VisitStatusEntity.BUCKET_LIST, 1)
        
        buildingDao.insertBuildings(listOf(building1, building2))

        // When
        val visited = buildingDao.getBuildingsByVisitStatus(VisitStatusEntity.VISITED).first()
        val bucketList = buildingDao.getBuildingsByVisitStatus(VisitStatusEntity.BUCKET_LIST).first()

        // Then
        assertEquals(1, visited.size)
        assertEquals("B1", visited[0].building.name)
        assertEquals(1, bucketList.size)
        assertEquals("B2", bucketList[0].building.name)
    }
    
    @Test
    fun `updateBuilding updates status`() = runTest {
         // Given
        val country = CountryEntity(1, "UAE", "UAE")
        countryDao.insertCountry(country)
        val city = CityEntity(1, "Dubai", 1)
        cityDao.insertCity(city)

        val building = BuildingEntity(1, "B1", "", 100, 10, 2000, "", "", VisitStatusEntity.NOT_VISITED, 1)
        buildingDao.insertBuildings(listOf(building))
        
        // When
        val updatedBuilding = building.copy(visitStatus = VisitStatusEntity.VISITED)
        buildingDao.updateBuilding(updatedBuilding)
        
        // Then
        val result = buildingDao.getBuildingById(1).first()
        assertEquals(VisitStatusEntity.VISITED, result?.building?.visitStatus)
    }
}
