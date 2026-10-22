package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CareUrgency
import com.example.model.PlantItem
import com.example.ui.components.AddEditPlantDialog
import com.example.ui.components.CareUrgencyBadge
import com.example.ui.components.FertilizerActionDialog
import com.example.ui.components.LogGrowthDialog
import com.example.ui.components.LogHarvestDialog
import com.example.ui.components.WaterActionDialog
import com.example.ui.theme.ForestGreenContainer
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SageGreenContainer
import com.example.ui.theme.SageGreenSecondary
import com.example.ui.theme.SoftLimeContainer
import com.example.ui.theme.SoftLimeTertiary
import com.example.ui.theme.SunGold
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedContainer
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueContainer
import com.example.ui.viewmodel.PlantPalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    plant: PlantItem,
    viewModel: PlantPalViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showWaterDialog by remember { mutableStateOf(false) }
    var showFertilizerDialog by remember { mutableStateOf(false) }
    var showGrowthDialog by remember { mutableStateOf(false) }
    var showHarvestDialog by remember { mutableStateOf(false) }

    val growthRecords by viewModel.selectedPlantGrowth.collectAsState()
    val sunlightAssessments by viewModel.selectedPlantSunlight.collectAsState()
    val harvestRecords by viewModel.selectedPlantHarvest.collectAsState()
    val waterLogs by viewModel.selectedPlantWaterLogs.collectAsState()
    val fertilizerLogs by viewModel.selectedPlantFertilizerLogs.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    val tabs = remember(plant.isHarvestable) {
        if (plant.isHarvestable) {
            listOf("Care & Schedule", "Growth Tracker", "Sunlight", "Harvest Yield", "Health Log")
        } else {
            listOf("Care & Schedule", "Growth Tracker", "Sunlight", "Health Log")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant.name, fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Plant")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Plant", tint = UrgentRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("plant_detail_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card Header
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (plant.isHarvestable) Icons.Default.Eco else Icons.Default.LocalFlorist,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plant.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = plant.species,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (plant.isIndoor) "Indoor" else "Outdoor",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = plant.category.label,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = SoftLimeContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${plant.ageInDays}d old",
                                            fontSize = 11.sp,
                                            color = SoftLimeTertiary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (plant.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💡 ${plant.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showWaterDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WaterBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("action_water_btn")
                            ) {
                                Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Water", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showFertilizerDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("action_feed_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Feed", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showGrowthDialog = true },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Growth", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Tabs Row
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Care & Schedule
                    item {
                        CareScheduleContent(
                            plant = plant,
                            waterLogs = waterLogs,
                            fertilizerLogs = fertilizerLogs,
                            dateFormatter = dateFormatter
                        )
                    }
                }
                1 -> {
                    // Growth Tracker
                    item {
                        GrowthTrackerContent(
                            growthRecords = growthRecords,
                            onAddRecord = { showGrowthDialog = true },
                            dateFormatter = dateFormatter
                        )
                    }
                }
                2 -> {
                    // Sunlight
                    item {
                        SunlightContent(
                            plant = plant,
                            sunlightAssessments = sunlightAssessments,
                            onAssess = { loc, type, hours, exp ->
                                viewModel.assessSunlight(plant.id, loc, type, hours, exp)
                            }
                        )
                    }
                }
                3 -> {
                    if (plant.isHarvestable) {
                        // Harvest Yield
                        item {
                            HarvestContent(
                                plant = plant,
                                harvestRecords = harvestRecords,
                                onLogHarvest = { showHarvestDialog = true },
                                dateFormatter = dateFormatter
                            )
                        }
                    } else {
                        // Health Log
                        item {
                            HealthHistoryContent(plant = plant)
                        }
                    }
                }
                4 -> {
                    // Health Log for harvestable plant
                    item {
                        HealthHistoryContent(plant = plant)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Dialogs
    if (showWaterDialog) {
        WaterActionDialog(
            plantName = plant.name,
            onDismiss = { showWaterDialog = false },
            onConfirm = { moisture, notes ->
                viewModel.markWatered(plant.id, moisture, notes)
                showWaterDialog = false
            }
        )
    }

    if (showFertilizerDialog) {
        FertilizerActionDialog(
            plantName = plant.name,
            onDismiss = { showFertilizerDialog = false },
            onConfirm = { type, amount, unit, notes ->
                viewModel.markFertilized(plant.id, type, amount, unit, notes)
                showFertilizerDialog = false
            }
        )
    }

    if (showGrowthDialog) {
        LogGrowthDialog(
            plantName = plant.name,
            onDismiss = { showGrowthDialog = false },
            onConfirm = { height, leaves, flowers, fruit, milestone, notes ->
                viewModel.addGrowthRecord(plant.id, height, leaves, flowers, fruit, milestone, notes)
                showGrowthDialog = false
            }
        )
    }

    if (showHarvestDialog) {
        LogHarvestDialog(
            plantName = plant.name,
            onDismiss = { showHarvestDialog = false },
            onConfirm = { quantity, unit, notes ->
                viewModel.logHarvest(plant.id, quantity, unit, "", notes)
                showHarvestDialog = false
            }
        )
    }

    if (showEditDialog) {
        AddEditPlantDialog(
            initialPlant = plant,
            onDismiss = { showEditDialog = false },
            onSave = { updatedPlant ->
                viewModel.savePlant(updatedPlant)
                viewModel.selectPlant(updatedPlant)
                showEditDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Plant Profile?") },
            text = { Text("Are you sure you want to remove ${plant.name} and all its associated growth and care history? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePlant(plant) { onBack() }
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UrgentRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CareScheduleContent(
    plant: PlantItem,
    waterLogs: List<com.example.data.local.WateringLogEntity>,
    fertilizerLogs: List<com.example.data.local.FertilizerLogEntity>,
    dateFormatter: SimpleDateFormat
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Schedule Overview Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = WaterBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Water Schedule", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Every ${plant.wateringIntervalDays} days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Next: ${dateFormatter.format(Date(plant.nextWateringDate))}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    CareUrgencyBadge(urgency = plant.wateringUrgency, daysLeft = plant.daysUntilWatering)
                }
            }

            OutlinedCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Feed Schedule", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Every ${plant.fertilizerIntervalDays} days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Next: ${dateFormatter.format(Date(plant.nextFertilizerDate))}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    CareUrgencyBadge(urgency = plant.fertilizerUrgency, daysLeft = plant.daysUntilFertilizer)
                }
            }
        }

        // Sunlight Card
        OutlinedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = SunGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Sunlight Requirement", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("${plant.sunlightRequirement.label} (${plant.sunlightRequirement.idealHours})", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        }

        // Recent Watering History
        Text("Recent Watering Logs (${waterLogs.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        if (waterLogs.isEmpty()) {
            Text("No recorded watering logs yet. Tap 'Water' to log the first hydration event.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            waterLogs.take(5).forEach { log ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = WaterBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dateFormatter.format(Date(log.wateredDate)), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Moisture: ${log.soilMoistureBefore}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (log.notes.isNotBlank()) {
                            Text(log.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GrowthTrackerContent(
    growthRecords: List<com.example.model.GrowthEntry>,
    onAddRecord: () -> Unit,
    dateFormatter: SimpleDateFormat
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Growth Progression", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Button(onClick = onAddRecord, shape = RoundedCornerShape(12.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Measurement")
            }
        }

        if (growthRecords.isEmpty()) {
            OutlinedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No growth measurements recorded yet.", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Track height and leaf counts over time to visualize your plant's progress!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            // Latest stats banner
            val latest = growthRecords.last()
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Current Height", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("${latest.heightCm} cm", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Leaves", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("${latest.leafCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Flowering", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(if (latest.hasFlowers) "Yes 🌸" else "No", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Timeline Entries
            growthRecords.reversed().forEach { record ->
                OutlinedCard(shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(dateFormatter.format(Date(record.recordedDate)), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${record.heightCm} cm • ${record.leafCount} leaves", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        if (record.milestone.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🌟 Milestone: ${record.milestone}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        if (record.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(record.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SunlightContent(
    plant: PlantItem,
    sunlightAssessments: List<com.example.model.SunlightAssessment>,
    onAssess: (loc: String, type: String, hours: Float, exp: com.example.model.SunlightType) -> Unit
) {
    var loc by remember { mutableStateOf("South-Facing Window") }
    var hours by remember { mutableStateOf("5") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sunlight Calculator & Location Check", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Evaluate whether your plant's current spot provides adequate photosynthetic light without scorch risk.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val h = hours.toFloatOrNull() ?: 4f
                        onAssess(loc, "Indoor Window", h, plant.sunlightRequirement)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = SunGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Check Light Suitability")
                }
            }
        }

        if (sunlightAssessments.isNotEmpty()) {
            val latest = sunlightAssessments.first()
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (latest.suitabilityRating) {
                        "Suitable Light" -> MaterialTheme.colorScheme.primaryContainer
                        "Low Light" -> SoftLimeContainer
                        else -> UrgentRedContainer
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Assessment Result: ${latest.suitabilityRating}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(latest.recommendation, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun HarvestContent(
    plant: PlantItem,
    harvestRecords: List<com.example.model.HarvestEntry>,
    onLogHarvest: () -> Unit,
    dateFormatter: SimpleDateFormat
) {
    val progress = (plant.ageInDays.toFloat() / plant.maturityDays.toFloat()).coerceIn(0f, 1f)
    val isReady = progress >= 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Maturity Window", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Surface(
                        color = if (isReady) MaterialTheme.colorScheme.primaryContainer else SoftLimeContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isReady) "Ready to Harvest!" else "${plant.maturityDays - plant.ageInDays}d remaining",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isReady) MaterialTheme.colorScheme.primary else SoftLimeTertiary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Planted ${plant.ageInDays} days ago (Typical maturity: ~${plant.maturityDays} days). Note: maturity depends on sunlight and weather conditions.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onLogHarvest,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBasket, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Harvest Yield")
                }
            }
        }

        Text("Harvest History (${harvestRecords.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        harvestRecords.forEach { record ->
            OutlinedCard(shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ShoppingBasket, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${record.quantity} ${record.unit}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(dateFormatter.format(Date(record.harvestDate)), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (record.notes.isNotBlank()) {
                        Text(record.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthHistoryContent(plant: PlantItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Health & Diagnostics History", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Current Status: ${plant.healthStatus}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Run scans in the 'Scan' tab to analyze yellowing, leaf spots, and pests using PlantPal AI multimodal vision.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
