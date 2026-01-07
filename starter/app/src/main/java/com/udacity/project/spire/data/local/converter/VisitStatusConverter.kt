package com.udacity.project.spire.data.local.converter

import androidx.room.TypeConverter
import com.udacity.project.spire.data.local.entity.VisitStatusEntity

/**
 * Room TypeConverter for VisitStatusEntity enum.
 * Converts between enum values and String for database storage.
 */
class VisitStatusConverter {

    @TypeConverter
    fun fromVisitStatus(status: VisitStatusEntity): String {
        return status.name
    }

    @TypeConverter
    fun toVisitStatus(value: String): VisitStatusEntity {
        return VisitStatusEntity.valueOf(value)
    }
}