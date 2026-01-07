# Data Transfer Objects (DTOs)

## Overview

This package should contain DTO (Data Transfer Object) classes for deserializing JSON responses from the API. DTOs are used to map the API's JSON structure to Kotlin objects.

**You need to create these DTO classes from scratch based on the API documentation below.**

---

## Why DTOs?

DTOs serve as an intermediary layer between the API and your domain models:

1. **Separation of Concerns**: API structure is separate from domain model structure
2. **API Changes**: If the API changes, you only need to update DTOs and mapping logic
3. **Flexibility**: API uses snake_case, domain models use camelCase
4. **Data Transformation**: Convert API data types to domain types (e.g., ISO dates to Date objects)

---

## TODO #23-27: Create the following DTO classes (5 classes)

### 1. BuildingsResponse

The root response object from the `/api/buildings` endpoint.

**JSON Structure:**
```json
{
  "buildings": [...],
  "pagination": {
    "current_page": 1,
    "page_size": 10,
    "total_items": 50,
    "total_pages": 5,
    "has_next": true,
    "has_previous": false
  }
}
```

**Implementation Guide:**
```kotlin
data class BuildingsResponse(
    val buildings: List<BuildingDto>,
    val pagination: PaginationMetadata?
)
```

**Notes:**
- `pagination` is nullable because some responses might not include it
- Used by `BuildingApiService.getBuildingsPaginated()`

---

### 2. PaginationMetadata

Contains pagination information from the API.

**JSON Structure:**
```json
{
  "current_page": 1,
  "page_size": 10,
  "total_items": 50,
  "total_pages": 5,
  "has_next": true,
  "has_previous": false
}
```

**Implementation Guide:**
```kotlin
import com.google.gson.annotations.SerializedName

data class PaginationMetadata(
    @SerializedName("current_page")
    val currentPage: Int,

    @SerializedName("page_size")
    val pageSize: Int,

    @SerializedName("total_items")
    val totalItems: Int,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("has_next")
    val hasNext: Boolean,

    @SerializedName("has_previous")
    val hasPrevious: Boolean
)
```

**Notes:**
- `@SerializedName` maps snake_case JSON keys to camelCase Kotlin properties
- All fields are non-nullable because the API always provides them

---

### 3. CountryDto

Represents a country in the API response.

**JSON Structure:**
```json
{
  "id": 1,
  "name": "United States",
  "code": "US"
}
```

**Implementation Guide:**
```kotlin
data class CountryDto(
    val id: Int,
    val name: String,
    val code: String
)
```

**Notes:**
- Simple DTO with no snake_case fields, so no @SerializedName needed

---

### 4. CityDto

Represents a city in the API response.

**JSON Structure:**
```json
{
  "id": 1,
  "name": "New York",
  "country_id": 1
}
```

**Implementation Guide:**
```kotlin
import com.google.gson.annotations.SerializedName

data class CityDto(
    val id: Int,
    val name: String,

    @SerializedName("country_id")
    val countryId: Int
)
```

**Notes:**
- `country_id` in JSON maps to `countryId` in Kotlin

---

### 5. BuildingDto

The main building data from the API. This is the most complex DTO.

**JSON Structure:**
```json
{
  "id": 1,
  "name": "Empire State Building",
  "city": {
    "id": 1,
    "name": "New York",
    "country_id": 1
  },
  "country": {
    "id": 1,
    "name": "United States",
    "code": "US"
  },
  "height_m": 381,
  "floors": 102,
  "year_completed": 1931,
  "architectural_style": "Art Deco",
  "image_url": "https://example.com/empire-state.jpg",
  "description": "An iconic skyscraper in Midtown Manhattan..."
}
```

**Implementation Guide:**
```kotlin
import com.google.gson.annotations.SerializedName

data class BuildingDto(
    val id: Int,
    val name: String,
    val city: CityDto,
    val country: CountryDto,

    @SerializedName("height_m")
    val heightMeters: Int,

    val floors: Int,

    @SerializedName("year_completed")
    val yearCompleted: Int,

    @SerializedName("architectural_style")
    val architecturalStyle: String,

    @SerializedName("image_url")
    val imageUrl: String,

    val description: String
)
```

**Notes:**
- Contains nested `CityDto` and `CountryDto` objects
- Several fields use snake_case in JSON, so need `@SerializedName`
- `height_m` in JSON maps to `heightMeters` in Kotlin

---

## Mapping DTOs to Domain Models

### Extension Function: BuildingDto.toDomainModel()

**IMPORTANT:** After creating `BuildingDto`, you must add an extension function to convert it to the domain `Building` model.

**Implementation Guide:**
```kotlin
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus

/**
 * Convert BuildingDto to domain Building model.
 * Used by the repository to transform API data to domain data.
 */
fun BuildingDto.toDomainModel(): Building {
    return Building(
        id = this.id,
        name = this.name,
        city = this.city.name,
        country = this.country.name,
        heightMeters = this.heightMeters,
        floors = this.floors,
        yearCompleted = this.yearCompleted,
        architecturalStyle = this.architecturalStyle,
        imageUrl = this.imageUrl,
        description = this.description,
        visitStatus = VisitStatus.NOT_VISITED  // Default for new buildings
    )
}
```

**Why this is needed:**
- Repository layer converts DTOs to domain models
- Domain models are what the UI layer uses
- Separates API concerns from business logic

**Where it's used:**
- `BuildingRepository.refreshBuildings()`
- `BuildingRemoteMediator.buildingDtoToEntity()`

---

## API Endpoint Reference

**Base URL:** `https://spire-3oxocbdifa-uc.a.run.app/api/`

**Endpoint:** `GET /buildings`

**Query Parameters:**
- `page` (Int): Page number (1-indexed)
- `limit` (Int): Number of items per page (default: 10)

**Example Request:**
```
GET https://spire-3oxocbdifa-uc.a.run.app/api/buildings?page=1&limit=10
```

**Example Response:**
```json
{
  "buildings": [
    {
      "id": 1,
      "name": "Empire State Building",
      "city": {"id": 1, "name": "New York", "country_id": 1},
      "country": {"id": 1, "name": "United States", "code": "US"},
      "height_m": 381,
      "floors": 102,
      "year_completed": 1931,
      "architectural_style": "Art Deco",
      "image_url": "https://...",
      "description": "..."
    }
  ],
  "pagination": {
    "current_page": 1,
    "page_size": 10,
    "total_items": 50,
    "total_pages": 5,
    "has_next": true,
    "has_previous": false
  }
}
```

---

## Testing Your DTOs

After creating your DTOs, you can test them by:

1. **Run the app** and trigger a network request
2. **Check Logcat** for Retrofit logs (if logging interceptor is enabled)
3. **Debug breakpoints** in `BuildingRemoteMediator` to inspect deserialized DTOs
4. **Unit tests** (optional): Create test files to verify JSON parsing

---

## Common Mistakes to Avoid

1. **Forgetting @SerializedName**: If JSON key is snake_case, you MUST use `@SerializedName`
2. **Wrong nullability**: Make fields nullable (`?`) only if the API can omit them
3. **Missing imports**: Don't forget `import com.google.gson.annotations.SerializedName`
4. **Wrong types**: Match API types (Int, String, Boolean) exactly
5. **Missing toDomainModel()**: Repository won't compile without this extension function

---

## Checklist

Before moving on to the Repository implementation, ensure you have:

- [ ] Created `BuildingsResponse.kt`
- [ ] Created `PaginationMetadata.kt` with all @SerializedName annotations
- [ ] Created `CountryDto.kt`
- [ ] Created `CityDto.kt` with @SerializedName for `country_id`
- [ ] Created `BuildingDto.kt` with all @SerializedName annotations
- [ ] Added `BuildingDto.toDomainModel()` extension function
- [ ] Verified all imports are correct
- [ ] Checked that field types match the JSON structure

---

## Resources

- [Gson User Guide](https://github.com/google/gson/blob/master/UserGuide.md)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [@SerializedName Documentation](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/annotations/SerializedName.html)

---

**Good luck! This is a great opportunity to practice working with APIs and JSON serialization.**
