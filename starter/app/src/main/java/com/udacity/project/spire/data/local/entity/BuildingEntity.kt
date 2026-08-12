package com.udacity.project.spire.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus
import java.sql.RowId

/**
 * Room entity representing a building in the local database.
 * A building belongs to a city, which belongs to a country.
 *
 * Database Relationship:
 * Country (1) ──→ (Many) City (1) ──→ (Many) Building
 * Building → City (Many-to-One)
 * City → Country (Many-to-One)
 * Building → Country (Indirect, through City)
 *
 *#3: Add Room annotations for the Building entity
 *
 */
@Entity(
    tableName = "buildings",
    foreignKeys = [ForeignKey(
        entity = CityEntity::class,
        parentColumns = ["id"],
        childColumns = ["cityId"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("cityId"), Index("name")]
)
data class BuildingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imageUrl: String,
    val heightMeters: Int,
    val floors: Int,
    val yearCompleted: Int,
    val architecturalStyle: String,
    val description: String,
    val visitStatus: VisitStatusEntity,
    val cityId: Int
)

/**
 * Data class representing a building with its city and country details.
 * Used for JOIN queries to get complete building information.
 *
 * #4: Add Room relation annotations
 *  1. Mark 'building' as @Embedded
 *     - This includes all BuildingEntity fields in the query result
 *  2. Add @Relation annotation to 'city':
 *     - entity = CityEntity::class
 *     - parentColumn = "cityId"  (from BuildingEntity)
 *     - entityColumn = "id"  (from CityEntity)
 *     - This tells Room to JOIN CityEntity where building.cityId = city.id
 *
 *  HINT: Room will automatically load the related CityWithCountry object
 *  HINT: This enables multi-level relationships: Building → City → Country
 *
 *  Required imports:
 *  - androidx.room.Embedded
 *  - androidx.room.Relation
 */
data class BuildingWithDetails(
    @Embedded
    val building: BuildingEntity,
    @Relation(
        entity = CityEntity::class,
        parentColumn = "cityId",
        entityColumn = "id",
    )
    val city: CityWithCountry
)

/**
 * Data class representing a city with its country details.
 *
 * #5: Add Room relation annotations
 *  1. Mark 'city' as @Embedded
 *     - This includes all CityEntity fields in the query result
 *  2. Add @Relation annotation to 'country':
 *     - parentColumn = "countryId"  (from CityEntity)
 *     - entityColumn = "id"  (from CountryEntity)
 *     - This tells Room to JOIN CountryEntity where city.countryId = country.id
 *
 *  HINT: This enables the second level of the relationship chain
 *
 *  Required imports:
 *  - androidx.room.Embedded
 *  - androidx.room.Relation
 */
data class CityWithCountry(
    @Embedded
    val city: CityEntity,
    @Relation(
        entity = CountryEntity::class,
        parentColumn = "countryId",
        entityColumn = "id"
    )
    val country: CountryEntity
)

/**
 * Extension function to convert BuildingWithDetails to domain Building model.
 */
fun BuildingWithDetails.toDomainModel(): Building {
    return Building(
        id = building.id,
        name = building.name,
        imageUrl = building.imageUrl,
        heightMeters = building.heightMeters,
        floors = building.floors,
        yearCompleted = building.yearCompleted,
        architecturalStyle = building.architecturalStyle,
        description = building.description,
        visitStatus = building.visitStatus.toDomainModel(),
        city = city.city.name,
        country = city.country.name
    )
}

/**
 * Extension function to convert VisitStatusEntity to domain VisitStatus model.
 */
fun VisitStatusEntity.toDomainModel(): VisitStatus {
    return when (this) {
        VisitStatusEntity.NOT_VISITED -> VisitStatus.NOT_VISITED
        VisitStatusEntity.BUCKET_LIST -> VisitStatus.BUCKET_LIST
        VisitStatusEntity.VISITED -> VisitStatus.VISITED
    }
}

/**
 * Extension function to convert domain VisitStatus to VisitStatusEntity.
 */
fun VisitStatus.toEntity(): VisitStatusEntity {
    return when (this) {
        VisitStatus.NOT_VISITED -> VisitStatusEntity.NOT_VISITED
        VisitStatus.BUCKET_LIST -> VisitStatusEntity.BUCKET_LIST
        VisitStatus.VISITED -> VisitStatusEntity.VISITED
    }
}

/**
 * Extension function to convert domain Building model to BuildingEntity.
 * @param cityId The ID of the city this building belongs to
 */
fun Building.toEntity(cityId: Int): BuildingEntity {
    return BuildingEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        heightMeters = heightMeters,
        floors = floors,
        yearCompleted = yearCompleted,
        architecturalStyle = architecturalStyle,
        description = description,
        visitStatus = visitStatus.toEntity(),
        cityId = cityId
    )
}

/**
 * Extension function to convert BuildingDto to BuildingEntity.
 * @param cityId The ID of the city this building belongs to
 */
fun com.udacity.project.spire.data.remote.dto.BuildingDto.toEntity(cityId: Int): BuildingEntity {
    return BuildingEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        heightMeters = heightMeters,
        floors = floors,
        yearCompleted = yearCompleted,
        architecturalStyle = architecturalStyle,
        description = description,
        visitStatus = VisitStatusEntity.NOT_VISITED,
        cityId = cityId
    )
}