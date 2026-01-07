package com.udacity.project.spire.data.local.entity

/**
 * Room entity representing a city.
 * A city belongs to a country and can have many buildings.
 *
 * TODO #2: Add Room annotations for the City entity
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
data class CityEntity(
    val id: Int = 0,
    val name: String,
    val countryId: Int
)