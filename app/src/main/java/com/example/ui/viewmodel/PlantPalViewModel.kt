package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.FertilizerLogEntity
import com.example.data.local.WateringLogEntity
import com.example.data.repository.PlantRepository
import com.example.model.CareUrgency
import com.example.model.ChatMessage
import com.example.model.DailyCareTask
import com.example.model.DailyGardenTip
import com.example.model.DiaryEventType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.model.DiaryItem
import com.example.model.GrowthEntry
import com.example.model.HarvestEntry
import com.example.model.HealthScanReport
import com.example.model.PlantCategory
import com.example.model.PlantItem
import com.example.model.SunlightAssessment
import com.example.model.SunlightType
import com.example.model.TodayCareTask
import com.example.worker.CareReminderWorker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantPalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = PlantRepository(
        plantDao = db.plantDao(),
        careLogDao = db.careLogDao(),
        growthDao = db.growthDao(),
        sunlightDao = db.sunlightDao(),
        harvestDao = db.harvestDao(),
        diaryDao = db.diaryDao(),
        healthReportDao = db.healthReportDao(),
        dailyCareTaskDao = db.dailyCareTaskDao()
    )

    // Daily AI-generated Seasonal Tip State
    val dailyTip = MutableStateFlow<DailyGardenTip?>(null)
    val isDailyTipLoading = MutableStateFlow(false)
    
    // UI Notification Message State
    val notificationMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.initializeStarterGardenIfEmpty()
            CareReminderWorker.scheduleDailyReminders(application)
            refreshDailyTip()
        }
    }
    
    fun setNotificationMessage(message: String?) {
        notificationMessage.value = message
    }

    fun refreshDailyTip() {
        val now = Date()
        val dateStr = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(now)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val seasonStr = when (month) {
            in 2..4 -> "Spring Awakening"
            in 5..7 -> "Summer Hydration"
            in 8..10 -> "Mid-Autumn Transition"
            else -> "Winter Dormancy"
        }

        viewModelScope.launch {
            isDailyTipLoading.value = true
            try {
                dailyTip.value = repository.getDailyGardenTip(dateStr, seasonStr)
            } finally {
                isDailyTipLoading.value = false
            }
        }
    }

    // 1. Garden State
    val allPlants: StateFlow<List<PlantItem>> = repository.allPlants.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow(PlantCategory.ALL)

    val filteredPlants: StateFlow<List<PlantItem>> = combine(
        allPlants,
        searchQuery,
        selectedCategory
    ) { plants, query, category ->
        plants.filter { plant ->
            val matchesQuery = query.isBlank() ||
                    plant.name.contains(query, ignoreCase = true) ||
                    plant.species.contains(query, ignoreCase = true)
            val matchesCategory = category == PlantCategory.ALL || plant.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayCareTasks: StateFlow<List<TodayCareTask>> = allPlants.combine(MutableStateFlow(Unit)) { plants, _ ->
        val tasks = mutableListOf<TodayCareTask>()
        plants.forEach { plant ->
            // Watering task
            if (plant.wateringUrgency == CareUrgency.OVERDUE || plant.wateringUrgency == CareUrgency.DUE_TODAY) {
                tasks.add(
                    TodayCareTask(
                        plantId = plant.id,
                        plantName = plant.name,
                        species = plant.species,
                        taskType = "Watering",
                        dueDate = plant.nextWateringDate,
                        urgency = plant.wateringUrgency,
                        details = "Check soil moisture before watering. Recommended interval: every ${plant.wateringIntervalDays} days.",
                        actionLabel = "Mark Watered"
                    )
                )
            }
            // Fertilizer task
            if (plant.fertilizerUrgency == CareUrgency.OVERDUE || plant.fertilizerUrgency == CareUrgency.DUE_TODAY) {
                tasks.add(
                    TodayCareTask(
                        plantId = plant.id,
                        plantName = plant.name,
                        species = plant.species,
                        taskType = "Fertilizer",
                        dueDate = plant.nextFertilizerDate,
                        urgency = plant.fertilizerUrgency,
                        details = "Feed with balanced plant nutrients. Recommended interval: every ${plant.fertilizerIntervalDays} days.",
                        actionLabel = "Mark Fertilized"
                    )
                )
            }
        }
        // Sort by Overdue first, then Due Today
        tasks.sortedBy { it.urgency != CareUrgency.OVERDUE }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyCareTasks: StateFlow<List<DailyCareTask>> = repository.getAllDailyTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 2. Selected Plant Detail State
    val selectedPlant = MutableStateFlow<PlantItem?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPlantGrowth: StateFlow<List<GrowthEntry>> = selectedPlant.flatMapLatest { plant ->
        if (plant != null) repository.getGrowthRecords(plant.id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPlantSunlight: StateFlow<List<SunlightAssessment>> = selectedPlant.flatMapLatest { plant ->
        if (plant != null) repository.getSunlightAssessments(plant.id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPlantHarvest: StateFlow<List<HarvestEntry>> = selectedPlant.flatMapLatest { plant ->
        if (plant != null) repository.getHarvestRecords(plant.id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPlantWaterLogs: StateFlow<List<WateringLogEntity>> = selectedPlant.flatMapLatest { plant ->
        if (plant != null) repository.getWateringLogs(plant.id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedPlantFertilizerLogs: StateFlow<List<FertilizerLogEntity>> = selectedPlant.flatMapLatest { plant ->
        if (plant != null) repository.getFertilizerLogs(plant.id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 3. Diary State
    val diaryEntries: StateFlow<List<DiaryItem>> = repository.allDiaryEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val diarySearchQuery = MutableStateFlow("")
    val diaryFilterEventType = MutableStateFlow<DiaryEventType?>(null)

    val filteredDiaryEntries: StateFlow<List<DiaryItem>> = combine(
        diaryEntries,
        diarySearchQuery,
        diaryFilterEventType
    ) { entries, query, type ->
        entries.filter { entry ->
            val matchesQuery = query.isBlank() ||
                    entry.title.contains(query, ignoreCase = true) ||
                    entry.description.contains(query, ignoreCase = true) ||
                    entry.plantName.contains(query, ignoreCase = true) ||
                    entry.tags.any { it.contains(query, ignoreCase = true) }
            val matchesType = type == null || entry.eventType == type
            matchesQuery && matchesType
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 4. AI Plant Disease Detection State
    val scanTargetPlant = MutableStateFlow<PlantItem?>(null)
    val scanSymptoms = MutableStateFlow<Set<String>>(emptySet())
    val scanCapturedBitmap = MutableStateFlow<Bitmap?>(null)
    val scanCapturedPhotoUri = MutableStateFlow<String>("")
    val isScanning = MutableStateFlow(false)
    val scanResult = MutableStateFlow<HealthScanReport?>(null)
    val scanError = MutableStateFlow<String?>(null)

    // 5. Conversational Assistant State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ASSISTANT",
                text = "Hello! I am **PlantPal AI**, your botanical specialist. Ask me anything about watering schedules, diagnosing sick plants, sunlight needs, or organic pest solutions!",
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    val isChatLoading = MutableStateFlow(false)
    val chatPlantContext = MutableStateFlow<PlantItem?>(null)

    // Plant Management Actions
    fun selectPlant(plant: PlantItem?) {
        selectedPlant.value = plant
    }

    fun savePlant(plant: PlantItem, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.savePlant(plant)
            onDone()
        }
    }

    fun deletePlant(plant: PlantItem, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deletePlant(plant)
            if (selectedPlant.value?.id == plant.id) {
                selectedPlant.value = null
            }
            onDone()
        }
    }

    fun markWatered(plantId: Long, soilMoisture: String = "Top 2 inches Dry", notes: String = "") {
        viewModelScope.launch {
            repository.markWatered(plantId, soilMoisture, notes)
            // Update selectedPlant if active
            if (selectedPlant.value?.id == plantId) {
                selectedPlant.value = repository.getPlantById(plantId)
            }
            
            // Trigger Notification
            setNotificationMessage("Great job! Plant watered successfully. 🌱")
        }
    }

    fun markFertilized(
        plantId: Long,
        fertilizerType: String = "Balanced Organic Liquid (10-10-10)",
        amount: Float = 5f,
        unit: String = "ml",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.markFertilized(plantId, fertilizerType, amount, unit, notes)
            if (selectedPlant.value?.id == plantId) {
                selectedPlant.value = repository.getPlantById(plantId)
            }
        }
    }

    fun addGrowthRecord(
        plantId: Long,
        heightCm: Float,
        leafCount: Int,
        hasFlowers: Boolean,
        hasFruit: Boolean,
        milestone: String,
        notes: String,
        photoUri: String = ""
    ) {
        viewModelScope.launch {
            repository.addGrowthRecord(
                GrowthEntry(
                    plantId = plantId,
                    heightCm = heightCm,
                    leafCount = leafCount,
                    hasFlowers = hasFlowers,
                    hasFruit = hasFruit,
                    milestone = milestone,
                    notes = notes,
                    photoUri = photoUri
                )
            )
        }
    }

    fun assessSunlight(
        plantId: Long,
        locationDescription: String,
        locationType: String,
        hoursDirectSun: Float,
        exposureType: SunlightType
    ) {
        viewModelScope.launch {
            val plant = repository.getPlantById(plantId)
            val req = plant?.sunlightRequirement ?: SunlightType.BRIGHT_INDIRECT

            val (rating, rec) = when {
                hoursDirectSun < 2 && (req == SunlightType.FULL_SUN || req == SunlightType.BRIGHT_INDIRECT) -> {
                    "Low Light" to "This location receives under 2 hours of sun. For a ${plant?.name ?: "this plant"}, consider moving closer to an unshaded South or West window or adding a full-spectrum grow light."
                }
                hoursDirectSun > 6 && (req == SunlightType.LOW_LIGHT || req == SunlightType.PARTIAL_SHADE) -> {
                    "Potentially Excessive Light" to "High direct sunlight (>6 hours) may scorch the foliage of shade-tolerant species. Add a sheer curtain to diffuse harsh midday rays."
                }
                else -> {
                    "Suitable Light" to "Great exposure! The current lighting condition (${exposureType.label}, ~${hoursDirectSun}h direct) matches ${plant?.name ?: "your plant"}'s botanical requirement."
                }
            }

            repository.recordSunlightAssessment(
                SunlightAssessment(
                    plantId = plantId,
                    locationDescription = locationDescription,
                    locationType = locationType,
                    estimatedHoursDirectSun = hoursDirectSun,
                    exposureType = exposureType,
                    suitabilityRating = rating,
                    recommendation = rec
                )
            )
        }
    }

    fun logHarvest(
        plantId: Long,
        quantity: Float,
        unit: String,
        photoUri: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.logHarvest(
                HarvestEntry(
                    plantId = plantId,
                    quantity = quantity,
                    unit = unit,
                    photoUri = photoUri,
                    notes = notes
                )
            )
        }
    }

    fun addDiaryEntry(
        plantId: Long,
        plantName: String,
        title: String,
        description: String,
        eventType: DiaryEventType,
        photoUri: String = "",
        tags: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            repository.addDiaryEntry(
                DiaryItem(
                    plantId = plantId,
                    plantName = plantName,
                    title = title,
                    description = description,
                    eventType = eventType,
                    photoUri = photoUri,
                    tags = tags
                )
            )
        }
    }

    fun deleteDiaryEntry(item: DiaryItem) {
        viewModelScope.launch {
            repository.deleteDiaryEntry(item)
        }
    }

    // AI Disease Scanning
    fun toggleSymptom(symptom: String) {
        val current = scanSymptoms.value.toMutableSet()
        if (current.contains(symptom)) current.remove(symptom) else current.add(symptom)
        scanSymptoms.value = current
    }

    fun setScanImage(bitmap: Bitmap?, uri: String = "") {
        scanCapturedBitmap.value = bitmap
        scanCapturedPhotoUri.value = uri
    }

    fun runScan() {
        val plant = scanTargetPlant.value
        val plantName = plant?.name ?: "General Plant"
        val symptoms = scanSymptoms.value.toList().ifEmpty { listOf("Foliage Discoloration") }

        isScanning.value = true
        scanError.value = null

        viewModelScope.launch {
            try {
                val report = repository.runDiseaseAnalysis(
                    plantId = plant?.id,
                    plantName = plantName,
                    symptoms = symptoms,
                    bitmap = scanCapturedBitmap.value,
                    photoUri = scanCapturedPhotoUri.value
                )
                scanResult.value = report
            } catch (e: Exception) {
                scanError.value = e.message ?: "Analysis failed"
            } finally {
                isScanning.value = false
            }
        }
    }

    fun resetScan() {
        scanSymptoms.value = emptySet()
        scanCapturedBitmap.value = null
        scanCapturedPhotoUri.value = ""
        scanResult.value = null
        scanError.value = null
    }

    // AI Assistant Chat
    fun sendChatMessage(text: String) {
        if (text.isBlank() || isChatLoading.value) return

        val userMsg = ChatMessage(
            sender = "USER",
            text = text.trim(),
            plantContextName = chatPlantContext.value?.name
        )

        _chatMessages.value = _chatMessages.value + userMsg
        isChatLoading.value = true

        viewModelScope.launch {
            val reply = repository.askAssistant(
                question = text,
                plantContextName = chatPlantContext.value?.name
            )

            val assistantMsg = ChatMessage(
                sender = "ASSISTANT",
                text = reply,
                plantContextName = chatPlantContext.value?.name
            )

            _chatMessages.value = _chatMessages.value + assistantMsg
            isChatLoading.value = false
        }
    }

    fun setTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.setTaskCompletion(taskId, isCompleted, if (isCompleted) System.currentTimeMillis() else null)
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "ASSISTANT",
                text = "Chat history cleared. How can I assist your garden today?",
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
