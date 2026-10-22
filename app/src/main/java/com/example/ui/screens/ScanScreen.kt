package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.HealthScanReport
import com.example.ui.theme.ForestGreenContainer
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SoftLimeContainer
import com.example.ui.theme.SoftLimeTertiary
import com.example.ui.theme.UrgentRed
import com.example.ui.theme.UrgentRedContainer
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.ui.viewmodel.PlantPalViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScanScreen(viewModel: PlantPalViewModel) {
    val plants by viewModel.allPlants.collectAsState()
    val targetPlant by viewModel.scanTargetPlant.collectAsState()
    val symptoms by viewModel.scanSymptoms.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val result by viewModel.scanResult.collectAsState()
    val error by viewModel.scanError.collectAsState()

    var plantDropdownExpanded by remember { mutableStateOf(false) }

    val commonSymptoms = listOf(
        "Yellowing leaves",
        "Brown crispy tips",
        "White powdery coating",
        "Fine webbing on leaf underside",
        "Dark spots with halos",
        "Curling or wilting foliage",
        "Soft or mushy stem",
        "Holes or chewed margins"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("scan_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AI Plant Health Scanner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            Text("Multimodal Botanical Diagnostics", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Identify nutrient deficiencies, fungal infections, overwatering, and pests with actionable organic remedies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }

        if (result == null) {
            // 1. Plant Selector
            item {
                OutlinedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1. Select Registered Plant", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = plantDropdownExpanded,
                            onExpandedChange = { plantDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = targetPlant?.name ?: "General Plant / Unregistered",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Plant Profile") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = plantDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = plantDropdownExpanded,
                                onDismissRequest = { plantDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("General Plant / Unregistered") },
                                    onClick = {
                                        viewModel.scanTargetPlant.value = null
                                        plantDropdownExpanded = false
                                    }
                                )
                                plants.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text("${p.name} (${p.species})") },
                                        onClick = {
                                            viewModel.scanTargetPlant.value = p
                                            plantDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Photo Attachment Card
            item {
                OutlinedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("2. Leaf / Stem Photograph", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(Alignment.Start))
                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .clickable {
                                    // Generate lightweight bitmap preview
                                    val bmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
                                    val canvas = Canvas(bmp)
                                    canvas.drawColor(android.graphics.Color.rgb(45, 90, 39))
                                    viewModel.setScanImage(bmp, "sample_plant_photo")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Capture",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Tap to attach leaf photo", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // 3. Observed Symptoms Checklist
            item {
                OutlinedCard(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("3. Observed Foliage Symptoms", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap all conditions visible on the plant:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            commonSymptoms.forEach { symptom ->
                                val selected = symptoms.contains(symptom)
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.toggleSymptom(symptom) },
                                    label = { Text(symptom, fontSize = 12.sp) },
                                    leadingIcon = if (selected) {
                                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Run Analysis Button
            item {
                Button(
                    onClick = { viewModel.runScan() },
                    enabled = !isScanning,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_run_scan")
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Analyzing Foliage...")
                    } else {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyze Plant Health", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            if (error != null) {
                item {
                    Text(
                        text = "Error: $error",
                        color = UrgentRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        } else {
            // Diagnostic Results View
            item {
                ScanReportCard(
                    report = result!!,
                    onReset = { viewModel.resetScan() }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun ScanReportCard(
    report: HealthScanReport,
    onReset: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scan_report_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Diagnosis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Preliminary Diagnosis", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(report.primaryDiagnosis, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Surface(
                    color = when (report.severity) {
                        "Severe" -> UrgentRedContainer
                        "Moderate", "Moderate to High" -> WarningAmberContainer
                        else -> SoftLimeContainer
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Severity: ${report.severity}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (report.severity) {
                            "Severe" -> UrgentRed
                            "Moderate", "Moderate to High" -> WarningAmber
                            else -> SoftLimeTertiary
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Environmental Factors
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Environmental Drivers", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(report.environmentalFactors, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Causes
            Text("Probable Causes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            report.possibleCauses.forEach { cause ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(cause, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Organic Remedies
            Text("Recommended Organic Remedies", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = ForestGreenPrimary)
            report.organicRemedies.forEach { remedy ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("🌿 ", fontSize = 12.sp)
                    Text(remedy, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Prevention Tips
            Text("Long-Term Prevention", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            report.preventionTips.forEach { tip ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("🛡️ ", fontSize = 12.sp)
                    Text(tip, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            if (report.needsExpertHelp) {
                Surface(
                    color = UrgentRedContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = UrgentRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Condition appears advanced. Consider taking a physical leaf cutting to your local agricultural extension or nursery.",
                            fontSize = 11.sp,
                            color = UrgentRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Text(
                text = "Disclaimer: ${report.confidenceNote}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = onReset,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start New Scan")
            }
        }
    }
}
