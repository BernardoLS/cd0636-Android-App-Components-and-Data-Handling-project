package com.udacity.project.spire.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Formattable

/**
 * Room entity representing a city.
 * A city belongs to a country and can have many buildings.
 *
 *  #2: Add Room annotations for the City entity
 *  1. Mark this class as @Entity with tableName = "cities"
 *  2. Add @PrimaryKey annotation to 'id' with autoGenerate = true
 *  3. Add @ForeignKey constraint to link city to country:
 *     - entity = CountryEntity::class
 *     - parentColumns = ["id"]  (CountryEntity's primary key)
 *     - childColumns = ["countryId"]  (CityEntity's foreign key)
 *     - onDelete = ForeignKey.CASCADE  (delete cities when country is deleted)
 *     - onUpdate = ForeignKey.CASCADE  (update cityId when country id changes)
 *  4. Add indices for performance:
 *     - Index on "countryId" (for foreign key lookups)
 *     - Composite unique index on ["name", "countryId"]
 *       (same city name can exist in different countries, but must be unique within a country)
 *
 *  HINT: A city belongs to a country (many-to-one relationship)
 *  HINT: Multiple cities can have the same name (e.g., "Paris, France" and "Paris, Texas")
 *
 *  Required imports:
 *  - androidx.room.Entity
 *  - androidx.room.PrimaryKey
 *  - androidx.room.ForeignKey
 *  - androidx.room.Index
 */
@Entity(
    tableName = "cities",
foreignKeys = [ForeignKey(
    entity = CountryEntity::class,
    parentColumns = ["id"],
    childColumns = ["countryId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)],
    indices = [
        Index("countryId"),
        Index("name", "countryId", unique = true)
    ]
)
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val countryId: Int
)

/**
 * Extension function to convert CityDto to CityEntity.
 */
fun com.udacity.project.spire.data.remote.dto.CityDto.toEntity(countryId: Int): CityEntity {
    return CityEntity(
        id = id,
        name = name,
        countryId = countryId
    )
}