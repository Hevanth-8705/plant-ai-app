package com.example.model

enum class PlantCategory(val label: String) {
    ALL("All Plants"),
    INDOOR("Indoor"),
    OUTDOOR("Outdoor"),
    VEGETABLE("Vegetables"),
    HERB("Herbs"),
    FLOWER("Flowers"),
    SUCCULENT("Succulents")
}

enum class SunlightType(val label: String, val idealHours: String) {
    FULL_SUN("Full Direct Sunlight", "6+ hours direct"),
    BRIGHT_INDIRECT("Bright Indirect Light", "4-6 hours filtered"),
    PARTIAL_SHADE("Partial Shade", "2-4 hours gentle light"),
    LOW_LIGHT("Low Light Tolerant", "Under 2 hours or indirect")
}

enum class SoilMoisture(val label: String) {
    DRY("Completely Dry"),
    SLIGHTLY_DRY("Top 2 inches Dry"),
    MOIST("Evenly Moist"),
    WET("Saturated / Wet")
}

enum class CareUrgency {
    OVERDUE,
    DUE_TODAY,
    UPCOMING,
    COMPLETED
}

enum class DiaryEventType(val label: String, val iconName: String) {
    WATERING("Watering", "water_drop"),
    FERTILIZING("Fertilizing", "eco"),
    PRUNING("Pruning & Trimming", "content_cut"),
    REPOTTING("Repotting & Soil Refresh", "yard"),
    NEW_LEAVES("New Growth Observed", "spa"),
    FLOWERING("Flowering & Blooms", "local_florist"),
    PEST_ISSUES("Pest Detected / Treated", "bug_report"),
    DISEASE_SYMPTOMS("Health Check / Symptoms", "health_and_safety"),
    HARVESTING("Harvested Produce", "shopping_basket"),
    GENERAL("General Observation", "notes")
}

data class PlantItem(
    val id: Long = 0,
    val name: String,
    val species: String,
    val category: PlantCategory,
    val imageUrl: String = "",
    val isIndoor: Boolean = true,
    val plantedDate: Long = System.currentTimeMillis(),
    val wateringIntervalDays: Int = 7,
    val lastWateredDate: Long = System.currentTimeMillis() - (86400000L * 3),
    val fertilizerIntervalDays: Int = 30,
    val lastFertilizedDate: Long = System.currentTimeMillis() - (86400000L * 14),
    val sunlightRequirement: SunlightType = SunlightType.BRIGHT_INDIRECT,
    val isHarvestable: Boolean = false,
    val maturityDays: Int = 70, // Days from planting to harvest
    val healthStatus: String = "Healthy", // "Healthy", "Needs Water", "Watch Leaves", "Under Treatment"
    val notes: String = "",
    val isArchived: Boolean = false
) {
    val nextWateringDate: Long
        get() = lastWateredDate + (wateringIntervalDays * 86400000L)

    val nextFertilizerDate: Long
        get() = lastFertilizedDate + (fertilizerIntervalDays * 86400000L)

    val daysUntilWatering: Int
        get() {
            val diff = nextWateringDate - System.currentTimeMillis()
            return (diff / 86400000L).toInt()
        }

    val wateringUrgency: CareUrgency
        get() = when {
            daysUntilWatering < 0 -> CareUrgency.OVERDUE
            daysUntilWatering == 0 -> CareUrgency.DUE_TODAY
            else -> CareUrgency.UPCOMING
        }

    val daysUntilFertilizer: Int
        get() {
            val diff = nextFertilizerDate - System.currentTimeMillis()
            return (diff / 86400000L).toInt()
        }

    val fertilizerUrgency: CareUrgency
        get() = when {
            daysUntilFertilizer < 0 -> CareUrgency.OVERDUE
            daysUntilFertilizer == 0 -> CareUrgency.DUE_TODAY
            else -> CareUrgency.UPCOMING
        }

    val ageInDays: Int
        get() = ((System.currentTimeMillis() - plantedDate) / 86400000L).toInt().coerceAtLeast(0)
}

data class TodayCareTask(
    val plantId: Long,
    val plantName: String,
    val species: String,
    val taskType: String, // "Watering" or "Fertilizer"
    val dueDate: Long,
    val urgency: CareUrgency,
    val details: String,
    val actionLabel: String
)

enum class CareTaskCategory(val label: String, val rawType: String) {
    ALL("All Tasks", "ALL"),
    WATERING("Watering", "WATERING"),
    PRUNING("Pruning", "PRUNING"),
    FEEDING("Feeding", "FEEDING")
}

data class DailyCareTask(
    val id: Long = 0,
    val plantId: Long,
    val plantName: String,
    val species: String,
    val taskType: String, // "WATERING", "PRUNING", "FEEDING"
    val title: String,
    val instructions: String,
    val dueDate: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isCustom: Boolean = false,
    val urgency: CareUrgency = CareUrgency.DUE_TODAY
)

data class GrowthEntry(
    val id: Long = 0,
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

data class SunlightAssessment(
    val id: Long = 0,
    val plantId: Long,
    val locationDescription: String,
    val locationType: String, // "Indoor Window", "Balcony", "Patio", "Garden Bed", "Greenhouse"
    val estimatedHoursDirectSun: Float,
    val exposureType: SunlightType,
    val suitabilityRating: String, // "Low Light", "Suitable", "Potentially Excessive Light"
    val recommendation: String,
    val recordedDate: Long = System.currentTimeMillis()
)

data class HarvestEntry(
    val id: Long = 0,
    val plantId: Long,
    val harvestDate: Long = System.currentTimeMillis(),
    val quantity: Float,
    val unit: String, // "pieces", "grams", "lbs", "bunches"
    val photoUri: String = "",
    val notes: String = ""
)

data class DiaryItem(
    val id: Long = 0,
    val plantId: Long,
    val plantName: String,
    val title: String,
    val description: String,
    val eventType: DiaryEventType,
    val photoUri: String = "",
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class HealthScanReport(
    val id: Long = 0,
    val plantId: Long?,
    val plantName: String,
    val scanDate: Long = System.currentTimeMillis(),
    val symptoms: List<String>,
    val primaryDiagnosis: String,
    val severity: String, // "Mild", "Moderate", "Severe", "Inconclusive"
    val possibleCauses: List<String>,
    val environmentalFactors: String,
    val organicRemedies: List<String>,
    val chemicalOptions: List<String>,
    val preventionTips: List<String>,
    val needsExpertHelp: Boolean,
    val confidenceNote: String,
    val photoUri: String = ""
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "ASSISTANT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val plantContextName: String? = null,
    val isError: Boolean = false
)

data class DailyGardenTip(
    val title: String,
    val category: String,
    val season: String,
    val tip: String,
    val actionItem: String,
    val funFact: String,
    val dateStr: String,
    val isLiveAi: Boolean = false
)

