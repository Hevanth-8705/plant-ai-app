package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenContainer
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SageGreenContainer
import com.example.ui.theme.SageGreenSecondary
import com.example.ui.theme.SoftLimeContainer
import com.example.ui.theme.SoftLimeTertiary
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueContainer

data class OnboardingSlide(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val containerColor: androidx.compose.ui.graphics.Color,
    val iconColor: androidx.compose.ui.graphics.Color
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentSlideIndex by remember { mutableIntStateOf(0) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* Proceed regardless of grant */ }
    )

    val slides = listOf(
        OnboardingSlide(
            title = "Manage All Plants in One Place",
            description = "Track indoor plants, herbs, vegetables, and succulents with personalized botanical profiles and care tips.",
            icon = Icons.Default.Eco,
            containerColor = ForestGreenContainer,
            iconColor = ForestGreenPrimary
        ),
        OnboardingSlide(
            title = "Never Forget Watering or Feeding",
            description = "Smart moisture-aware care reminders that estimate hydration intervals based on your plant species and season.",
            icon = Icons.Default.WaterDrop,
            containerColor = WaterBlueContainer,
            iconColor = WaterBlue
        ),
        OnboardingSlide(
            title = "Monitor Growth & Health Over Time",
            description = "Log height measurements, leaf counts, flowering milestones, and sunlight assessments in your visual journal.",
            icon = Icons.Default.TrendingUp,
            containerColor = SoftLimeContainer,
            iconColor = SoftLimeTertiary
        ),
        OnboardingSlide(
            title = "AI-Powered Gardening Specialist",
            description = "Scan sick leaves for instant disease diagnosis, organic remedies, and ask PlantPal AI any plant care question.",
            icon = Icons.Default.AutoAwesome,
            containerColor = SageGreenContainer,
            iconColor = SageGreenSecondary
        )
    )

    val currentSlide = slides[currentSlideIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Skip Button Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinish, modifier = Modifier.testTag("skip_onboarding_btn")) {
                Text("Skip", color = MaterialTheme.colorScheme.outline)
            }
        }

        // Center Slide Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(currentSlide.containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = currentSlide.icon,
                    contentDescription = null,
                    tint = currentSlide.iconColor,
                    modifier = Modifier.size(68.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = currentSlide.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = currentSlide.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            // Special Notification Step on slide 1 (watering)
            if (currentSlideIndex == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = WaterBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enable notifications so you get gentle watering alerts before soil dries completely.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Navigation Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                slides.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (index == currentSlideIndex) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentSlideIndex) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (currentSlideIndex < slides.size - 1) {
                        if (currentSlideIndex == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        currentSlideIndex++
                    } else {
                        onFinish()
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("next_onboarding_btn")
            ) {
                Text(
                    text = if (currentSlideIndex == slides.size - 1) "Enter My Garden" else "Next",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
