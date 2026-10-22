package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.local.CareLogDao
import com.example.data.local.Converters
import com.example.data.local.DailyCareTaskDao
import com.example.data.local.DailyCareTaskEntity
import com.example.data.local.DiaryDao
import com.example.data.local.DiaryEntryEntity
import com.example.data.local.FertilizerLogEntity
import com.example.data.local.GrowthDao
import com.example.data.local.GrowthRecordEntity
import com.example.data.local.HarvestDao
import com.example.data.local.HarvestRecordEntity
import com.example.data.local.HealthReportDao
import com.example.data.local.HealthReportEntity
import com.example.data.local.PlantDao
import com.example.data.local.PlantEntity
import com.example.data.local.SunlightAssessmentEntity
import com.example.data.local.SunlightDao
import com.example.data.local.WateringLogEntity
import com.example.data.remote.BotanicalExpertFallback
import com.example.data.remote.GeminiApiService
import com.example.model.CareUrgency
import com.example.model.DailyCareTask
import com.example.model.DailyGardenTip
import com.example.model.DiaryEventType
import com.example.model.DiaryItem
import com.example.model.GrowthEntry
import com.example.model.HarvestEntry
import com.example.model.HealthScanReport
import com.example.model.PlantCategory
import com.example.model.PlantItem
import com.example.model.SunlightAssessment
import com.example.model.SunlightType
import com.example.model.TodayCareTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlantRepository(
    private val plantDao: PlantDao,
    private val careLogDao: CareLogDao,
    private val growthDao: GrowthDao,
    private val sunlightDao: SunlightDao,
    private val harvestDao: HarvestDao,
    private val diaryDao: DiaryDao,
    private val healthReportDao: HealthReportDao,
    private val dailyCareTaskDao: DailyCareTaskDao,
    private val geminiApiService: GeminiApiService = GeminiApiService()
) {

    val allPlants: Flow<List<PlantItem>> = plantDao.getAllActivePlants().map { entities ->
        entities.map { it.toDomain() }
    }

    val allDiaryEntries: Flow<List<DiaryItem>> = diaryDao.getAllDiaryEntries().map { entities ->
        entities.map { it.toDomain() }
    }

    val allHealthReports: Flow<List<HealthScanReport>> = healthReportDao.getAllHealthReports().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun initializeStarterGardenIfEmpty() {
        if (plantDao.getPlantCount() == 0) {
            val starterPlants = listOf(
                PlantEntity(
                    name = "Monstera Deliciosa",
                    species = "Monstera deliciosa (Swiss Cheese Plant)",
                    category = PlantCategory.INDOOR,
                    isIndoor = true,
                    plantedDate = System.currentTimeMillis() - (86400000L * 180),
                    wateringIntervalDays = 7,
                    lastWateredDate = System.currentTimeMillis() - (86400000L * 6), // Due tomorrow/soon
                    fertilizerIntervalDays = 30,
                    lastFertilizedDate = System.currentTimeMillis() - (86400000L * 25),
                    sunlightRequirement = SunlightType.BRIGHT_INDIRECT,
                    isHarvestable = false,
                    notes = "Loves bright indirect sun and moderate humidity. Wipe broad fenestrated leaves monthly."
                ),
                PlantEntity(
                    name = "Snake Plant",
                    species = "Sansevieria trifasciata (Laurentii)",
                    category = PlantCategory.SUCCULENT,
                    isIndoor = true,
                    plantedDate = System.currentTimeMillis() - (86400000L * 90),
                    wateringIntervalDays = 14,
                    lastWateredDate = System.currentTimeMillis() - (86400000L * 15), // OVERDUE!
                    fertilizerIntervalDays = 60,
                    lastFertilizedDate = System.currentTimeMillis() - (86400000L * 40),
                    sunlightRequirement = SunlightType.LOW_LIGHT,
                    isHarvestable = false,
                    notes = "Extremely drought-tolerant. Best to underwater than overwater."
                ),
                PlantEntity(
                    name = "Sweet Genovese Basil",
                    species = "Ocimum basilicum",
                    category = PlantCategory.HERB,
                    isIndoor = false,
                    plantedDate = System.currentTimeMillis() - (86400000L * 45),
                    wateringIntervalDays = 2,
                    lastWateredDate = System.currentTimeMillis() - (86400000L * 2), // DUE TODAY!
                    fertilizerIntervalDays = 14,
                    lastFertilizedDate = System.currentTimeMillis() - (86400000L * 10),
                    sunlightRequirement = SunlightType.FULL_SUN,
                    isHarvestable = true,
                    maturityDays = 60,
                    notes = "Pinch top flowers to promote bushy leaf growth. Fantastic for fresh pesto."
                ),
                PlantEntity(
                    name = "Roma Tomato",
                    species = "Solanum lycopersicum",
                    category = PlantCategory.VEGETABLE,
                    isIndoor = false,
                    plantedDate = System.currentTimeMillis() - (86400000L * 65),
                    wateringIntervalDays = 2,
                    lastWateredDate = System.currentTimeMillis() - (86400000L * 1),
                    fertilizerIntervalDays = 14,
                    lastFertilizedDate = System.currentTimeMillis() - (86400000L * 14), // DUE TODAY!
                    sunlightRequirement = SunlightType.FULL_SUN,
                    isHarvestable = true,
                    maturityDays = 75,
                    notes = "Prune suckers and support with cage. Water consistently to avoid blossom end rot."
                ),
                PlantEntity(
                    name = "Peace Lily",
                    species = "Spathiphyllum wallisii",
                    category = PlantCategory.FLOWER,
                    isIndoor = true,
                    plantedDate = System.currentTimeMillis() - (86400000L * 120),
                    wateringIntervalDays = 5,
                    lastWateredDate = System.currentTimeMillis() - (86400000L * 3),
                    fertilizerIntervalDays = 45,
                    lastFertilizedDate = System.currentTimeMillis() - (86400000L * 20),
                    sunlightRequirement = SunlightType.PARTIAL_SHADE,
                    isHarvestable = false,
                    notes = "Shows dramatic drooping when thirsty. Keep away from cold exterior drafts."
                )
            )

            plantDao.insertAll(starterPlants)

            // Seed sample growth record for Monstera
            growthDao.insertGrowthRecord(
                GrowthRecordEntity(
                    plantId = 1,
                    recordedDate = System.currentTimeMillis() - (86400000L * 30),
                    heightCm = 42.0f,
                    leafCount = 7,
                    hasFlowers = false,
                    hasFruit = false,
                    milestone = "First fenestrated leaf opened!",
                    notes = "New split leaf appeared near southwest window."
                )
            )
            growthDao.insertGrowthRecord(
                GrowthRecordEntity(
                    plantId = 1,
                    recordedDate = System.currentTimeMillis() - (86400000L * 5),
                    heightCm = 46.5f,
                    leafCount = 8,
                    hasFlowers = false,
                    hasFruit = false,
                    milestone = "Healthy new central aerial root",
                    notes = "Growth accelerated after liquid fertilizer."
                )
            )

            // Seed sample diary entries
            diaryDao.insertDiaryEntry(
                DiaryEntryEntity(
                    plantId = 1,
                    plantName = "Monstera Deliciosa",
                    title = "Spring Repotting & Moss Pole",
                    description = "Transferred into an 8-inch terracotta container with chunky aroid mix (perlite, orchid bark, charcoal). Added sturdy coco coir pole.",
                    eventType = DiaryEventType.REPOTTING,
                    tags = listOf("Repotting", "Aroid Mix", "Indoor")
                )
            )
            diaryDao.insertDiaryEntry(
                DiaryEntryEntity(
                    plantId = 3,
                    plantName = "Sweet Genovese Basil",
                    title = "Generous Summer Harvest",
                    description = "Trimmed top 4 stems just above leaf nodes for homemade caprese salad. Plants responded with vigorous side shoot growth.",
                    eventType = DiaryEventType.HARVESTING,
                    tags = listOf("Harvest", "Fresh Herbs", "Kitchen")
                )
            )
        }
    }

    suspend fun getPlantById(id: Long): PlantItem? {
        return plantDao.getPlantById(id)?.toDomain()
    }

    suspend fun savePlant(plant: PlantItem): Long {
        return plantDao.insertPlant(plant.toEntity())
    }

    suspend fun deletePlant(plant: PlantItem) {
        plantDao.deletePlant(plant.toEntity())
    }

    suspend fun archivePlant(plantId: Long) {
        plantDao.archivePlant(plantId)
    }

    // Care Actions
    suspend fun markWatered(plantId: Long, soilMoisture: String = "Top 2 inches Dry", notes: String = "") {
        val now = System.currentTimeMillis()
        plantDao.updateLastWatered(plantId, now)
        careLogDao.insertWateringLog(
            WateringLogEntity(
                plantId = plantId,
                wateredDate = now,
                soilMoistureBefore = soilMoisture,
                notes = notes
            )
        )
        val plant = plantDao.getPlantById(plantId)
        if (plant != null) {
            diaryDao.insertDiaryEntry(
                DiaryEntryEntity(
                    plantId = plantId,
                    plantName = plant.name,
                    title = "Watered ${plant.name}",
                    description = "Soil condition: $soilMoisture. $notes".trim(),
                    eventType = DiaryEventType.WATERING,
                    tags = listOf("Watering", "Care Schedule")
                )
            )
        }
    }

    suspend fun markFertilized(
        plantId: Long,
        fertilizerType: String = "Balanced Organic Liquid (10-10-10)",
        amount: Float = 5f,
        unit: String = "ml",
        notes: String = ""
    ) {
        val now = System.currentTimeMillis()
        plantDao.updateLastFertilized(plantId, now)
        careLogDao.insertFertilizerLog(
            FertilizerLogEntity(
                plantId = plantId,
                appliedDate = now,
                fertilizerType = fertilizerType,
                amount = amount,
                unit = unit,
                notes = notes
            )
        )
        val plant = plantDao.getPlantById(plantId)
        if (plant != null) {
            diaryDao.insertDiaryEntry(
                DiaryEntryEntity(
                    plantId = plantId,
                    plantName = plant.name,
                    title = "Fertilized ${plant.name}",
                    description = "Applied $amount $unit of $fertilizerType. $notes".trim(),
                    eventType = DiaryEventType.FERTILIZING,
                    tags = listOf("Fertilizer", "Nutrients")
                )
            )
        }
    }

    fun getWateringLogs(plantId: Long): Flow<List<WateringLogEntity>> =
        careLogDao.getWateringLogsForPlant(plantId)

    fun getFertilizerLogs(plantId: Long): Flow<List<FertilizerLogEntity>> =
        careLogDao.getFertilizerLogsForPlant(plantId)

    // Growth
    fun getGrowthRecords(plantId: Long): Flow<List<GrowthEntry>> =
        growthDao.getGrowthRecordsForPlant(plantId).map { list ->
            list.map {
                GrowthEntry(
                    id = it.id,
                    plantId = it.plantId,
                    recordedDate = it.recordedDate,
                    heightCm = it.heightCm,
                    leafCount = it.leafCount,
                    hasFlowers = it.hasFlowers,
                    hasFruit = it.hasFruit,
                    photoUri = it.photoUri,
                    milestone = it.milestone,
                    notes = it.notes
                )
            }
        }

    suspend fun addGrowthRecord(record: GrowthEntry): Long {
        val id = growthDao.insertGrowthRecord(
            GrowthRecordEntity(
                plantId = record.plantId,
                recordedDate = record.recordedDate,
                heightCm = record.heightCm,
                leafCount = record.leafCount,
                hasFlowers = record.hasFlowers,
                hasFruit = record.hasFruit,
                photoUri = record.photoUri,
                milestone = record.milestone,
                notes = record.notes
            )
        )
        val plant = plantDao.getPlantById(record.plantId)
        if (plant != null) {
            val milestoneText = if (record.milestone.isNotBlank()) " - Milestone: ${record.milestone}" else ""
            diaryDao.insertDiaryEntry(
                DiaryEntryEntity(
                    plantId = record.plantId,
                    plantName = plant.name,
                    title = "Growth Update: ${record.heightCm} cm (${record.leafCount} leaves)",
                    description = "Height: ${record.heightCm} cm, Leaves: ${record.leafCount}$milestoneText. ${record.notes}".trim(),
                    eventType = DiaryEventType.NEW_LEAVES,
                    photoUri = record.photoUri,
                    tags = listOf("Growth", "Height Check")
                )
            )
        }
        return id
    }

    suspend fun deleteGrowthRecord(id: Long) = growthDao.deleteGrowthRecord(id)

    // Sunlight
    fun getSunlightAssessments(plantId: Long): Flow<List<SunlightAssessment>> =
        sunlightDao.getAssessmentsForPlant(plantId).map { list ->
            list.map {
                SunlightAssessment(
                    id = it.id,
                    plantId = it.plantId,
                    locationDescription = it.locationDescription,
                    locationType = it.locationType,
                    estimatedHoursDirectSun = it.estimatedHoursDirectSun,
                    exposureType = it.exposureType,
                    suitabilityRating = it.suitabilityRating,
                    recommendation = it.recommendation,
                    recordedDate = it.recordedDate
                )
            }
        }

    suspend fun recordSunlightAssessment(assessment: SunlightAssessment): Long {
        return sunlightDao.insertAssessment(
            SunlightAssessmentEntity(
                plantId = assessment.plantId,
                locationDescription = assessment.locationDescription,
                locationType = assessment.locationType,
                estimatedHoursDirectSun = assessment.estimatedHoursDirectSun,
                exposureType = assessment.exposureType,
                suitabilityRating = assessment.suitabilityRating,
                recommendation = assessment.recommendation,
                recordedDate = assessment.recordedDate
            )
        )
    }

    // Harvest
    fun getHarvestRecords(plantId: Long): Flow<List<HarvestEntry>> =
        harvestDao.getHarvestRecordsForPlant(plantId).map { list ->
            list.map {
                HarvestEntry(
                    id = it.id,
                    plantId = it.plantId,
                    harvestDate = it.harvestDate,
                    quantity = it.quantity,
                    unit = it.unit,
                    photoUri = it.photoUri,
                    notes = it.notes
                )
            }
        }

    suspend fun logHarvest(harvest: HarvestEntry): Long {
        val id = harvestDao.insertHarvestRecord(
            HarvestRecordEntity(
                plantId = harvest.plantId,
                harvestDate = harvest.harvestDate,
                quantity = harvest.quantity,
                unit = harvest.unit,
                photoUri = harvest.photoUri,
                notes = harvest.notes
            )
        )
        val plant = plantDao.getPlantById(harvest.plantId)
        if (plant != null) {
            diaryDao.insertDiaryEntry(
                DiaryEntryEntity(
                    plantId = harvest.plantId,
                    plantName = plant.name,
                    title = "Harvested ${harvest.quantity} ${harvest.unit} of ${plant.name}",
                    description = harvest.notes,
                    eventType = DiaryEventType.HARVESTING,
                    photoUri = harvest.photoUri,
                    tags = listOf("Harvest", "Produce")
                )
            )
        }
        return id
    }

    // Diary
    suspend fun addDiaryEntry(item: DiaryItem): Long {
        return diaryDao.insertDiaryEntry(
            DiaryEntryEntity(
                plantId = item.plantId,
                plantName = item.plantName,
                title = item.title,
                description = item.description,
                eventType = item.eventType,
                photoUri = item.photoUri,
                tags = item.tags,
                createdAt = item.createdAt
            )
        )
    }

    suspend fun deleteDiaryEntry(item: DiaryItem) {
        diaryDao.deleteDiaryEntry(
            DiaryEntryEntity(
                id = item.id,
                plantId = item.plantId,
                plantName = item.plantName,
                title = item.title,
                description = item.description,
                eventType = item.eventType,
                photoUri = item.photoUri,
                tags = item.tags,
                createdAt = item.createdAt
            )
        )
    }

    // Health & AI Disease Scan
    suspend fun runDiseaseAnalysis(
        plantId: Long?,
        plantName: String,
        symptoms: List<String>,
        bitmap: Bitmap?,
        photoUri: String = ""
    ): HealthScanReport {
        val geminiResult = geminiApiService.analyzePlantHealth(plantName, symptoms, bitmap)
        val finalReport = geminiResult.getOrElse {
            BotanicalExpertFallback.diagnose(plantName, symptoms)
        }.copy(
            plantId = plantId,
            plantName = plantName,
            photoUri = photoUri
        )

        // Persist to health reports
        val reportId = healthReportDao.insertHealthReport(
            HealthReportEntity(
                plantId = plantId,
                plantName = plantName,
                scanDate = finalReport.scanDate,
                symptoms = finalReport.symptoms,
                primaryDiagnosis = finalReport.primaryDiagnosis,
                severity = finalReport.severity,
                possibleCauses = finalReport.possibleCauses,
                environmentalFactors = finalReport.environmentalFactors,
                organicRemedies = finalReport.organicRemedies,
                chemicalOptions = finalReport.chemicalOptions,
                preventionTips = finalReport.preventionTips,
                needsExpertHelp = finalReport.needsExpertHelp,
                confidenceNote = finalReport.confidenceNote,
                photoUri = photoUri
            )
        )

        // Log to plant diary
        if (plantId != null) {
            diaryDao.insertDiaryEntry(
                DiaryEntryEntity(
                    plantId = plantId,
                    plantName = plantName,
                    title = "Health Scan: ${finalReport.primaryDiagnosis}",
                    description = "Severity: ${finalReport.severity}. ${finalReport.organicRemedies.firstOrNull() ?: ""}",
                    eventType = DiaryEventType.DISEASE_SYMPTOMS,
                    photoUri = photoUri,
                    tags = listOf("Health Scan", "Diagnosis")
                )
            )
        }

        return finalReport.copy(id = reportId)
    }

    // AI Assistant Chat
    suspend fun askAssistant(question: String, plantContextName: String?): String {
        val geminiResult = geminiApiService.askGardeningAssistant(question, plantContextName)
        return geminiResult.getOrElse {
            BotanicalExpertFallback.answerQuestion(question, plantContextName)
        }
    }

    // Entity Mappers
    private fun PlantEntity.toDomain() = PlantItem(
        id = id,
        name = name,
        species = species,
        category = category,
        imageUrl = imageUrl,
        isIndoor = isIndoor,
        plantedDate = plantedDate,
        wateringIntervalDays = wateringIntervalDays,
        lastWateredDate = lastWateredDate,
        fertilizerIntervalDays = fertilizerIntervalDays,
        lastFertilizedDate = lastFertilizedDate,
        sunlightRequirement = sunlightRequirement,
        isHarvestable = isHarvestable,
        maturityDays = maturityDays,
        healthStatus = healthStatus,
        notes = notes,
        isArchived = isArchived
    )

    private fun PlantItem.toEntity() = PlantEntity(
        id = id,
        name = name,
        species = species,
        category = category,
        imageUrl = imageUrl,
        isIndoor = isIndoor,
        plantedDate = plantedDate,
        wateringIntervalDays = wateringIntervalDays,
        lastWateredDate = lastWateredDate,
        fertilizerIntervalDays = fertilizerIntervalDays,
        lastFertilizedDate = lastFertilizedDate,
        sunlightRequirement = sunlightRequirement,
        isHarvestable = isHarvestable,
        maturityDays = maturityDays,
        healthStatus = healthStatus,
        notes = notes,
        isArchived = isArchived
    )

    private fun DiaryEntryEntity.toDomain() = DiaryItem(
        id = id,
        plantId = plantId,
        plantName = plantName,
        title = title,
        description = description,
        eventType = eventType,
        photoUri = photoUri,
        tags = tags,
        createdAt = createdAt
    )

    private fun HealthReportEntity.toDomain() = HealthScanReport(
        id = id,
        plantId = plantId,
        plantName = plantName,
        scanDate = scanDate,
        symptoms = symptoms,
        primaryDiagnosis = primaryDiagnosis,
        severity = severity,
        possibleCauses = possibleCauses,
        environmentalFactors = environmentalFactors,
        organicRemedies = organicRemedies,
        chemicalOptions = chemicalOptions,
        preventionTips = preventionTips,
        needsExpertHelp = needsExpertHelp,
        confidenceNote = confidenceNote,
        photoUri = photoUri
    )

    // Daily Tasks
    fun getAllDailyTasks(): Flow<List<DailyCareTask>> = dailyCareTaskDao.getAllDailyTasks().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insertTask(task: DailyCareTask): Long = dailyCareTaskDao.insertTask(task.toEntity())

    suspend fun updateTask(task: DailyCareTask) = dailyCareTaskDao.updateTask(task.toEntity())

    suspend fun deleteTask(taskId: Long) = dailyCareTaskDao.deleteTask(taskId)

    suspend fun setTaskCompletion(taskId: Long, isCompleted: Boolean, completedAt: Long?) =
        dailyCareTaskDao.setTaskCompletion(taskId, isCompleted, completedAt)

    // Entity Mappers
    private fun DailyCareTaskEntity.toDomain() = DailyCareTask(
        id = id,
        plantId = plantId,
        plantName = plantName,
        species = species,
        taskType = taskType,
        title = title,
        instructions = instructions,
        dueDate = dueDate,
        isCompleted = isCompleted,
        completedAt = completedAt,
        isCustom = isCustom
    )

    private fun DailyCareTask.toEntity() = DailyCareTaskEntity(
        id = id,
        plantId = plantId,
        plantName = plantName,
        species = species,
        taskType = taskType,
        title = title,
        instructions = instructions,
        dueDate = dueDate,
        isCompleted = isCompleted,
        completedAt = completedAt,
        isCustom = isCustom
    )

    suspend fun getDailyGardenTip(dateStr: String, seasonStr: String): DailyGardenTip {
        val geminiResult = geminiApiService.generateDailyGardenTip(dateStr, seasonStr)
        return geminiResult.getOrElse {
            BotanicalExpertFallback.getSeasonalDailyTip(dateStr, seasonStr)
        }
    }
}
