package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.DiaryEventType
import com.example.model.PlantCategory
import com.example.model.SunlightType

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val name: String,
    val passwordHash: String = "",
    val avatarUrl: String = "",
    val notificationsEnabled: Boolean = true,
    val wateringTimePreference: String = "09:00",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val species: String,
    val category: PlantCategory,
    val imageUrl: String = "",
    val isIndoor: Boolean = true,
    val plantedDate: Long = System.currentTimeMillis(),
    val wateringIntervalDays: Int = 7,
    val lastWateredDate: Long = System.currentTimeMillis(),
    val fertilizerIntervalDays: Int = 30,
    val lastFertilizedDate: Long = System.currentTimeMillis(),
    val sunlightRequirement: SunlightType = SunlightType.BRIGHT_INDIRECT,
    val isHarvestable: Boolean = false,
    val maturityDays: Int = 60,
    val healthStatus: String = "Healthy",
    val notes: String = "",
    val isArchived: Boolean = false
)

@Entity(tableName = "care_schedules")
data class CareScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val careType: String, // "WATERING", "FERTILIZING", "PRUNING", "REPOTTING"
    val intervalDays: Int,
    val lastCompletedDate: Long = System.currentTimeMillis(),
    val nextDueDate: Long = System.currentTimeMillis() + (7L * 86400000L),
    val reminderTime: String = "09:00",
    val notes: String = "",
    val isEnabled: Boolean = true
)

@Entity(tableName = "watering_logs")
data class WateringLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val wateredDate: Long = System.currentTimeMillis(),
    val soilMoistureBefore: String = "Top 2 inches Dry",
    val notes: String = ""
)

@Entity(tableName = "fertilizer_logs")
data class FertilizerLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val appliedDate: Long = System.currentTimeMillis(),
    val fertilizerType: String = "Balanced Organic Liquid (10-10-10)",
    val amount: Float = 5.0f,
    val unit: String = "ml",
    val notes: String = ""
)

@Entity(tableName = "growth_records")
data class GrowthRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val recordedDate: Long = System.currentTimeMillis(),
    val heightCm: Float,
    val leafCount: Int,
    val hasFlowers: Boolean = false,
    val hasFruit: Boolean = false,
    val photoUri: String = "",
    val milestone: String = "",
    val notes: String = ""
)

@Entity(tableName = "sunlight_assessments")
data class SunlightAssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val locationDescription: String,
    val locationType: String,
    val estimatedHoursDirectSun: Float,
    val exposureType: SunlightType,
    val suitabilityRating: String,
    val recommendation: String,
    val recordedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "harvest_records")
data class HarvestRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val harvestDate: Long = System.currentTimeMillis(),
    val quantity: Float,
    val unit: String,
    val photoUri: String = "",
    val notes: String = ""
)

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val plantName: String,
    val title: String,
    val description: String,
    val eventType: DiaryEventType,
    val photoUri: String = "",
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_reports")
data class HealthReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long?,
    val plantName: String,
    val scanDate: Long = System.currentTimeMillis(),
    val symptoms: List<String>,
    val primaryDiagnosis: String,
    val severity: String,
    val possibleCauses: List<String>,
    val environmentalFactors: String,
    val organicRemedies: List<String>,
    val chemicalOptions: List<String>,
    val preventionTips: List<String>,
    val needsExpertHelp: Boolean,
    val confidenceNote: String,
    val photoUri: String = ""
)

@Entity(tableName = "daily_care_tasks")
data class DailyCareTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val plantName: String,
    val species: String,
    val taskType: String, // "WATERING", "PRUNING", "FEEDING"
    val title: String,
    val instructions: String,
    val dueDate: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isCustom: Boolean = false
)
