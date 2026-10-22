package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiaryEventType
import com.example.model.PlantCategory
import com.example.model.PlantItem
import com.example.model.SoilMoisture
import com.example.model.SunlightType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlantDialog(
    initialPlant: PlantItem? = null,
    onDismiss: () -> Unit,
    onSave: (PlantItem) -> Unit
) {
    var name by remember { mutableStateOf(initialPlant?.name ?: "") }
    var species by remember { mutableStateOf(initialPlant?.species ?: "") }
    var selectedCategory by remember { mutableStateOf(initialPlant?.category ?: PlantCategory.INDOOR) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var wateringInterval by remember { mutableStateOf((initialPlant?.wateringIntervalDays ?: 7).toString()) }
    var fertilizerInterval by remember { mutableStateOf((initialPlant?.fertilizerIntervalDays ?: 30).toString()) }
    var selectedSunlight by remember { mutableStateOf(initialPlant?.sunlightRequirement ?: SunlightType.BRIGHT_INDIRECT) }
    var sunlightExpanded by remember { mutableStateOf(false) }

    var isIndoor by remember { mutableStateOf(initialPlant?.isIndoor ?: true) }
    var isHarvestable by remember { mutableStateOf(initialPlant?.isHarvestable ?: false) }
    var maturityDays by remember { mutableStateOf((initialPlant?.maturityDays ?: 60).toString()) }
    var notes by remember { mutableStateOf(initialPlant?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialPlant == null) "Add New Plant" else "Edit ${initialPlant.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plant Common Name *") },
                    placeholder = { Text("e.g. Monstera Deliciosa") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_plant_name")
                )

                OutlinedTextField(
                    value = species,
                    onValueChange = { species = it },
                    label = { Text("Botanical Species / Variety") },
                    placeholder = { Text("e.g. Monstera deliciosa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        PlantCategory.values().filter { it != PlantCategory.ALL }.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = wateringInterval,
                        onValueChange = { wateringInterval = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Water Every (days)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fertilizerInterval,
                        onValueChange = { fertilizerInterval = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Feed Every (days)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Sunlight dropdown
                ExposedDropdownMenuBox(
                    expanded = sunlightExpanded,
                    onExpandedChange = { sunlightExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSunlight.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sunlight Need") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sunlightExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = sunlightExpanded,
                        onDismissRequest = { sunlightExpanded = false }
                    ) {
                        SunlightType.values().forEach { sun ->
                            DropdownMenuItem(
                                text = { Text("${sun.label} (${sun.idealHours})") },
                                onClick = {
                                    selectedSunlight = sun
                                    sunlightExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Indoor Plant", modifier = Modifier.weight(1f))
                    Switch(checked = isIndoor, onCheckedChange = { isIndoor = it })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edible / Harvestable", modifier = Modifier.weight(1f))
                    Switch(checked = isHarvestable, onCheckedChange = { isHarvestable = it })
                }

                if (isHarvestable) {
                    OutlinedTextField(
                        value = maturityDays,
                        onValueChange = { maturityDays = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Estimated Days to Maturity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Care Notes & Tips") },
                    placeholder = { Text("e.g. prefers filtered water, sensitive to draft") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val waterDays = wateringInterval.toIntOrNull()?.coerceAtLeast(1) ?: 7
                        val fertDays = fertilizerInterval.toIntOrNull()?.coerceAtLeast(1) ?: 30
                        val matDays = maturityDays.toIntOrNull()?.coerceAtLeast(1) ?: 60

                        val newPlant = initialPlant?.copy(
                            name = name.trim(),
                            species = species.trim().ifBlank { name.trim() },
                            category = selectedCategory,
                            isIndoor = isIndoor,
                            wateringIntervalDays = waterDays,
                            fertilizerIntervalDays = fertDays,
                            sunlightRequirement = selectedSunlight,
                            isHarvestable = isHarvestable,
                            maturityDays = matDays,
                            notes = notes.trim()
                        ) ?: PlantItem(
                            name = name.trim(),
                            species = species.trim().ifBlank { name.trim() },
                            category = selectedCategory,
                            isIndoor = isIndoor,
                            wateringIntervalDays = waterDays,
                            fertilizerIntervalDays = fertDays,
                            sunlightRequirement = selectedSunlight,
                            isHarvestable = isHarvestable,
                            maturityDays = matDays,
                            notes = notes.trim()
                        )
                        onSave(newPlant)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("btn_save_plant")
            ) {
                Text("Save Plant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WaterActionDialog(
    plantName: String,
    onDismiss: () -> Unit,
    onConfirm: (moisture: String, notes: String) -> Unit
) {
    var selectedMoisture by remember { mutableStateOf(SoilMoisture.SLIGHTLY_DRY.label) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Watering: $plantName", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Soil moisture assessment prior to watering:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                SoilMoisture.values().forEach { moisture ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedMoisture == moisture.label,
                            onClick = { selectedMoisture = moisture.label }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = moisture.label, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Optional Notes") },
                    placeholder = { Text("e.g. soaked thoroughly until bottom drainage") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMoisture, notes) },
                modifier = Modifier.testTag("confirm_water_btn")
            ) {
                Text("Confirm Watered")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FertilizerActionDialog(
    plantName: String,
    onDismiss: () -> Unit,
    onConfirm: (type: String, amount: Float, unit: String, notes: String) -> Unit
) {
    var type by remember { mutableStateOf("Balanced Organic Liquid (10-10-10)") }
    var amount by remember { mutableStateOf("5") }
    var unit by remember { mutableStateOf("ml") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Feeding: $plantName", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Fertilizer Formulation") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Dilution") },
                    placeholder = { Text("e.g. diluted half strength in 1 gallon water") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amtFloat = amount.toFloatOrNull() ?: 5f
                    onConfirm(type, amtFloat, unit, notes)
                }
            ) {
                Text("Confirm Fed")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LogGrowthDialog(
    plantName: String,
    onDismiss: () -> Unit,
    onConfirm: (height: Float, leaves: Int, flowers: Boolean, fruit: Boolean, milestone: String, notes: String) -> Unit
) {
    var height by remember { mutableStateOf("45.0") }
    var leafCount by remember { mutableStateOf("8") }
    var hasFlowers by remember { mutableStateOf(false) }
    var hasFruit by remember { mutableStateOf(false) }
    var milestone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Growth: $plantName", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = leafCount,
                        onValueChange = { leafCount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Leaf Count") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = hasFlowers, onCheckedChange = { hasFlowers = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Has Blooming Flowers")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = hasFruit, onCheckedChange = { hasFruit = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Developing Fruit / Produce")
                }

                OutlinedTextField(
                    value = milestone,
                    onValueChange = { milestone = it },
                    label = { Text("Growth Milestone (Optional)") },
                    placeholder = { Text("e.g. First fenestration, new main branch") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observation Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h = height.toFloatOrNull() ?: 0f
                    val l = leafCount.toIntOrNull() ?: 0
                    onConfirm(h, l, hasFlowers, hasFruit, milestone, notes)
                }
            ) {
                Text("Save Measurement")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LogHarvestDialog(
    plantName: String,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Float, unit: String, notes: String) -> Unit
) {
    var quantity by remember { mutableStateOf("150") }
    var unit by remember { mutableStateOf("grams") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Harvest: $plantName", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Yield Amount") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (g, lbs, pcs)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Harvest Quality & Flavor Notes") },
                    placeholder = { Text("e.g. Crisp aromatic leaves, bright red ripe tomatoes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val q = quantity.toFloatOrNull() ?: 1f
                    onConfirm(q, unit, notes)
                }
            ) {
                Text("Save Harvest")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiaryEntryDialog(
    plants: List<PlantItem>,
    defaultPlantId: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (plantId: Long, plantName: String, title: String, desc: String, type: DiaryEventType, tags: List<String>) -> Unit
) {
    var selectedPlant by remember {
        mutableStateOf(plants.firstOrNull { it.id == defaultPlantId } ?: plants.firstOrNull())
    }
    var plantExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(DiaryEventType.GENERAL) }
    var typeExpanded by remember { mutableStateOf(false) }
    var tagsInput by remember { mutableStateOf("Journal, Observation") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Diary Entry", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (plants.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = plantExpanded,
                        onExpandedChange = { plantExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedPlant?.name ?: "Select Plant",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Plant") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = plantExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = plantExpanded,
                            onDismissRequest = { plantExpanded = false }
                        ) {
                            plants.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name) },
                                    onClick = {
                                        selectedPlant = p
                                        plantExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Event Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Activity / Event Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DiaryEventType.values().forEach { evt ->
                            DropdownMenuItem(
                                text = { Text(evt.label) },
                                onClick = {
                                    selectedType = evt
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Entry Title *") },
                    placeholder = { Text("e.g. New growth shoot emerged") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details & Observations") },
                    placeholder = { Text("Write about current condition, changes, or treatments...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = selectedPlant
                    if (title.isNotBlank() && p != null) {
                        val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        onConfirm(p.id, p.name, title.trim(), description.trim(), selectedType, tags)
                    }
                },
                enabled = title.isNotBlank() && selectedPlant != null
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
