package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.DiaryEventType
import com.example.model.PlantCategory
import com.example.model.SunlightType

class Converters {
    @TypeConverter
    fun fromPlantCategory(value: PlantCategory): String = value.name

    @TypeConverter
    fun toPlantCategory(value: String): PlantCategory = try {
        PlantCategory.valueOf(value)
    } catch (e: Exception) {
        PlantCategory.INDOOR
    }

    @TypeConverter
    fun fromSunlightType(value: SunlightType): String = value.name

    @TypeConverter
    fun toSunlightType(value: String): SunlightType = try {
        SunlightType.valueOf(value)
    } catch (e: Exception) {
        SunlightType.BRIGHT_INDIRECT
    }

    @TypeConverter
    fun fromDiaryEventType(value: DiaryEventType): String = value.name

    @TypeConverter
    fun toDiaryEventType(value: String): DiaryEventType = try {
        DiaryEventType.valueOf(value)
    } catch (e: Exception) {
        DiaryEventType.GENERAL
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String = list?.joinToString(";;") ?: ""

    @TypeConverter
    fun toStringList(value: String?): List<String> = if (value.isNullOrBlank()) {
        emptyList()
    } else {
        value.split(";;").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
