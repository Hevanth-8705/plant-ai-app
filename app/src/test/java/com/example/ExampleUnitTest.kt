package com.example

import com.example.data.remote.BotanicalExpertFallback
import com.example.model.CareUrgency
import com.example.model.PlantCategory
import com.example.model.PlantItem
import com.example.model.SunlightType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testWateringUrgencyCalculation_overdue() {
        val pastDate = System.currentTimeMillis() - (86400000L * 10)
        val plant = PlantItem(
            name = "Monstera",
            species = "Monstera deliciosa",
            category = PlantCategory.INDOOR,
            wateringIntervalDays = 7,
            lastWateredDate = pastDate
        )

        assertEquals(CareUrgency.OVERDUE, plant.wateringUrgency)
        assertTrue(plant.daysUntilWatering < 0)
    }

    @Test
    fun testWateringUrgencyCalculation_upcoming() {
        val recentDate = System.currentTimeMillis()
        val plant = PlantItem(
            name = "Snake Plant",
            species = "Sansevieria trifasciata",
            category = PlantCategory.SUCCULENT,
            wateringIntervalDays = 14,
            lastWateredDate = recentDate
        )

        assertEquals(CareUrgency.UPCOMING, plant.wateringUrgency)
        assertTrue(plant.daysUntilWatering > 0)
    }

    @Test
    fun testBotanicalExpertDiagnostics_powderyMildew() {
        val symptoms = listOf("white powdery spots on leaf surface", "curling foliage")
        val report = BotanicalExpertFallback.diagnose("Peace Lily", symptoms)

        assertTrue(report.primaryDiagnosis.contains("Powdery Mildew"))
        assertTrue(report.organicRemedies.any { it.contains("baking soda") || it.contains("Neem oil") })
        assertFalse(report.needsExpertHelp)
    }

    @Test
    fun testBotanicalExpertDiagnostics_spiderMites() {
        val symptoms = listOf("fine webbing on leaf undersides", "yellow speckling")
        val report = BotanicalExpertFallback.diagnose("English Ivy", symptoms)

        assertTrue(report.primaryDiagnosis.contains("Spider Mite"))
        assertTrue(report.organicRemedies.any { it.contains("shower") || it.contains("soap") })
    }

    @Test
    fun testPlantAgeAndHarvestMaturity() {
        val plantedDate = System.currentTimeMillis() - (86400000L * 60)
        val plant = PlantItem(
            name = "Sweet Basil",
            species = "Ocimum basilicum",
            category = PlantCategory.HERB,
            plantedDate = plantedDate,
            isHarvestable = true,
            maturityDays = 60
        )

        assertTrue(plant.ageInDays >= 59)
    }

    @Test
    fun testCoreRoomEntitiesCreation() {
        val user = com.example.data.local.UserEntity(
            id = 1L,
            email = "gardener@plantpal.app",
            name = "Urban Gardener"
        )
        assertEquals("gardener@plantpal.app", user.email)
        assertTrue(user.notificationsEnabled)

        val plant = com.example.data.local.PlantEntity(
            id = 10L,
            name = "Monstera Deliciosa",
            species = "Monstera deliciosa",
            category = PlantCategory.INDOOR,
            wateringIntervalDays = 7
        )
        assertEquals(10L, plant.id)
        assertEquals(7, plant.wateringIntervalDays)

        val schedule = com.example.data.local.CareScheduleEntity(
            id = 100L,
            plantId = 10L,
            careType = "WATERING",
            intervalDays = 7
        )
        assertEquals("WATERING", schedule.careType)
        assertTrue(schedule.isEnabled)

        val log = com.example.data.local.WateringLogEntity(
            id = 50L,
            plantId = 10L,
            soilMoistureBefore = "Top 2 inches Dry",
            notes = "Watered thoroughly until runoff"
        )
        assertEquals(10L, log.plantId)
        assertEquals("Top 2 inches Dry", log.soilMoistureBefore)
    }
}
