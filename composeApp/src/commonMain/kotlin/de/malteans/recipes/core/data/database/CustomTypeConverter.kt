package de.malteans.recipes.core.data.database

import androidx.room.TypeConverter
import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import kotlinx.datetime.LocalDate

object CustomTypeConverter {

    @TypeConverter
    fun fromTimeOfDay(value: String?): TimeOfDay? {
        return value?.let { TimeOfDay.valueOf(it) }
    }

    @TypeConverter
    fun toTimeOfDay(value: TimeOfDay?): String? {
        return value?.name
    }

    @TypeConverter
    fun fromLocalDate(value: Int?): LocalDate? {
        return value?.let { LocalDate.fromEpochDays(it) }
    }

    @TypeConverter
    fun toLocalDate(value: LocalDate?): Int? {
        return value?.toEpochDays()
    }
}