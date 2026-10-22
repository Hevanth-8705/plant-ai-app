package com.example.data.remote

import com.example.model.HealthScanReport

object BotanicalExpertFallback {

    fun diagnose(plantName: String, symptoms: List<String>): HealthScanReport {
        val lowerSymptoms = symptoms.map { it.lowercase() }
        val lowerName = plantName.lowercase()

        // 1. Powdery Mildew
        if (lowerSymptoms.any { it.contains("white") || it.contains("powder") || it.contains("dust") }) {
            return HealthScanReport(
                plantId = null,
                plantName = plantName,
                symptoms = symptoms,
                primaryDiagnosis = "Powdery Mildew (Fungal Infection)",
                severity = "Moderate",
                possibleCauses = listOf(
                    "High ambient humidity combined with stagnant air circulation",
                    "Overhead watering that leaves foliage damp overnight",
                    "Shaded conditions favoring fungal spore germination"
                ),
                environmentalFactors = "Fungal spores thrive in warm, humid microclimates with poor air circulation around dense foliage.",
                organicRemedies = listOf(
                    "Mix 1 tbsp baking soda with 1/2 tsp gentle dish soap in 1 gallon water and spray infected leaves",
                    "Apply cold-pressed organic Neem oil spray in early morning or evening",
                    "Prune heavily crowded stems to dramatically increase air flow"
                ),
                chemicalOptions = listOf("Potassium bicarbonate fungicide", "Sulfur-based botanical dust"),
                preventionTips = listOf(
                    "Always water plants directly at the soil base, avoiding leaf splashing",
                    "Space pots at least 6 inches apart for passive airflow",
                    "Keep foliage dry during cooler overnight periods"
                ),
                needsExpertHelp = false,
                confidenceNote = "Visual symptoms strongly correspond to powdery mildew, but laboratory culture is needed for definitive pathogen typing."
            )
        }

        // 2. Spider Mites / Webbing
        if (lowerSymptoms.any { it.contains("web") || it.contains("stippling") || it.contains("fine dot") || it.contains("speck") }) {
            return HealthScanReport(
                plantId = null,
                plantName = plantName,
                symptoms = symptoms,
                primaryDiagnosis = "Spider Mite Infestation (Tetranychidae)",
                severity = "Moderate to High",
                possibleCauses = listOf(
                    "Dry indoor heating or arid summer microclimates",
                    "Plant stress due to drought or dust accumulation on leaf surfaces",
                    "Introduction via newly acquired outdoor or nursery plants"
                ),
                environmentalFactors = "Spider mites reproduce exponentially in hot, arid conditions with low humidity (< 40%).",
                organicRemedies = listOf(
                    "Give the plant a thorough lukewarm shower to physically dislodge mites and webs",
                    "Spray undersides of leaves thoroughly with insecticidal soap every 4 days for 2 weeks",
                    "Apply dilute horticultural neem oil solution"
                ),
                chemicalOptions = listOf("Miticidal spray formulated for indoor ornamentals"),
                preventionTips = listOf(
                    "Elevate room humidity with a pebble tray or room humidifier (aim for 50-60%)",
                    "Wipe leaves down with a damp microfiber cloth bi-weekly",
                    "Quarantine newly purchased plants for 14 days before introducing them to your garden"
                ),
                needsExpertHelp = false,
                confidenceNote = "Fine webbing with speckled foliage is characteristic of mite activity. Inspect leaf undersides with a 10x magnifying loupe."
            )
        }

        // 3. Root Rot / Overwatering
        if (lowerSymptoms.any { it.contains("yellow") && (it.contains("soft") || it.contains("mushy") || it.contains("droop") || it.contains("black stem")) }) {
            return HealthScanReport(
                plantId = null,
                plantName = plantName,
                symptoms = symptoms,
                primaryDiagnosis = "Root Moisture Saturation / Suspected Root Rot",
                severity = "Severe",
                possibleCauses = listOf(
                    "Soil remaining waterlogged due to absence of container drainage holes",
                    "Watering on a strict calendar rather than assessing soil dryness",
                    "Heavy, compacted potting medium retaining anaerobic moisture"
                ),
                environmentalFactors = "Oxygen starvation in saturated root systems prevents nutrient absorption, causing yellowing and stem softening.",
                organicRemedies = listOf(
                    "Immediately unpot the plant, gently rinse soil from roots, and inspect for dark mushy roots",
                    "Prune away decaying, black roots with sterilized shears (dip blades in 70% isopropyl alcohol)",
                    "Repot into fresh, chunky, well-aerated potting mix containing perlite and orchid bark",
                    "Withhold watering for 3-5 days to allow root callousing"
                ),
                chemicalOptions = listOf("Hydrogen peroxide (3% diluted 1:4 with water) soil drench"),
                preventionTips = listOf(
                    "Use containers with at least one unrestricted bottom drainage hole",
                    "Always conduct the finger-depth moisture check before adding water",
                    "Empty overflow runoff from saucers within 20 minutes of watering"
                ),
                needsExpertHelp = true,
                confidenceNote = "Severe overwatering can permanently compromise the root vascular system. Prompt physical inspection of roots is essential."
            )
        }

        // 4. Underwatering / Drought Stress
        if (lowerSymptoms.any { it.contains("crisp") || it.contains("dry") || it.contains("wilted") || it.contains("curling") }) {
            return HealthScanReport(
                plantId = null,
                plantName = plantName,
                symptoms = symptoms,
                primaryDiagnosis = "Dehydration & Low Moisture Stress",
                severity = "Mild to Moderate",
                possibleCauses = listOf(
                    "Soil pulled away from container edges, causing water to channel straight through",
                    "Extended intervals between waterings in warm temperatures",
                    "Root-bound plant with insufficient soil volume to hold moisture"
                ),
                environmentalFactors = "High transpiration rates in warm or breezy locations deplete cellular turgor pressure.",
                organicRemedies = listOf(
                    "Perform bottom-watering: place pot in a basin with 2 inches of lukewarm water for 30 minutes until surface feels damp",
                    "Trim crispy brown foliage to conserve plant energy",
                    "Mulch container surface with a light layer of sphagnum moss or coconut coir"
                ),
                chemicalOptions = listOf("None required; proper rehydration resolves dehydration"),
                preventionTips = listOf(
                    "Check soil moisture twice weekly during peak summer active growth",
                    "Consider repotting into a 1-2 inch larger container if roots have filled current pot",
                    "Keep plants away from direct heating vents or air conditioning drafts"
                ),
                needsExpertHelp = false,
                confidenceNote = "Leaf curling accompanied by dry, brittle foliage is standard drought response. Recovery usually begins within 6-12 hours of thorough soaking."
            )
        }

        // 5. Leaf Spot (Bacterial or Fungal)
        if (lowerSymptoms.any { it.contains("spot") || it.contains("ring") || it.contains("brown circle") }) {
            return HealthScanReport(
                plantId = null,
                plantName = plantName,
                symptoms = symptoms,
                primaryDiagnosis = "Cercospora / Alternaria Leaf Spot",
                severity = "Moderate",
                possibleCauses = listOf(
                    "Splashing water transferring fungal or bacterial spores between leaves",
                    "Poor leaf aeration allowing moisture to sit on leaves for > 4 hours",
                    "High warmth and stagnant room conditions"
                ),
                environmentalFactors = "Water droplets remaining on leaf surfaces provide the exact germination window spores need to penetrate leaf stomata.",
                organicRemedies = listOf(
                    "Carefully prune and discard infected leaves (never compost diseased foliage)",
                    "Treat remaining healthy leaves with copper fungicide or dilute neem solution",
                    "Cease any misting or spraying of leaf canopy"
                ),
                chemicalOptions = listOf("Liquid copper octanoate fungicide"),
                preventionTips = listOf(
                    "Water strictly at the soil line with a narrow-spout watering can",
                    "Sterilize pruning shears between cuts with rubbing alcohol",
                    "Provide gentle air movement using a low oscillating fan"
                ),
                needsExpertHelp = false,
                confidenceNote = "Brown spots with yellow halos typically indicate fungal or bacterial infection. Observe whether new spots appear on fresh growth."
            )
        }

        // 6. Tomato specific: Blossom End Rot
        if (lowerName.contains("tomato") && lowerSymptoms.any { it.contains("black") || it.contains("fruit") || it.contains("bottom") }) {
            return HealthScanReport(
                plantId = null,
                plantName = plantName,
                symptoms = symptoms,
                primaryDiagnosis = "Blossom End Rot (Calcium & Moisture Imbalance)",
                severity = "Moderate",
                possibleCauses = listOf(
                    "Irregular watering cycles (fluctuating between dry soil and waterlogged soil)",
                    "Inability of plant to transport calcium to developing fruit tissue during dry spells",
                    "Excess nitrogen fertilizer promoting rapid leaf growth at the expense of fruit calcium"
                ),
                environmentalFactors = "Calcium moves with water transpiration. Inconsistent watering disrupts calcium delivery to the furthest end of growing fruit.",
                organicRemedies = listOf(
                    "Establish a steady, consistent watering routine to maintain uniform soil moisture",
                    "Add crushed eggshells or dolomitic lime around base in upcoming growing cycles",
                    "Remove and discard currently affected fruit so the vine directs nutrients to new fruits"
                ),
                chemicalOptions = listOf("Foliar calcium spray for emergency uptake"),
                preventionTips = listOf(
                    "Mulch heavily (2-3 inches of clean straw or wood chips) to regulate soil moisture",
                    "Avoid high-nitrogen fertilizers once flowering commences",
                    "Ensure potting soil has a pH between 6.2 and 6.8 for optimal calcium absorption"
                ),
                needsExpertHelp = false,
                confidenceNote = "Blossom end rot is a physiological condition, not a viral or bacterial disease. Correcting watering consistency typically protects subsequent fruit."
            )
        }

        // Default General Botanical Diagnostic
        return HealthScanReport(
            plantId = null,
            plantName = plantName,
            symptoms = symptoms,
            primaryDiagnosis = "Environmental Adaptation & Foliar Stress",
            severity = "Mild",
            possibleCauses = listOf(
                "Subtle shifts in ambient temperature, draft exposure, or relative humidity",
                "Acclimation to seasonal light variation",
                "Mineral salts accumulation from regular municipal tap water"
            ),
            environmentalFactors = "Plants respond to microclimate changes by adjusting transpiration and chlorophyll density in existing leaves.",
            organicRemedies = listOf(
                "Flush soil thoroughly with filtered or rainwater to leach excess mineral salts",
                "Prune dead tips with clean, sharp botanical scissors",
                "Evaluate pot placement relative to heating vents, exterior doors, and window drafts"
            ),
            chemicalOptions = listOf("Mild balanced organic plant fertilizer diluted to half-strength"),
            preventionTips = listOf(
                "Let tap water stand uncovered for 24 hours to allow chlorine to dissipate before watering",
                "Rotate pot a quarter turn weekly for balanced light distribution",
                "Maintain a consistent care log in PlantPal to pinpoint changes"
            ),
            needsExpertHelp = false,
            confidenceNote = "General stress symptoms observed. If symptoms worsen over the next 10 days, capture an updated photo for reassessment."
        )
    }

    fun answerQuestion(question: String, plantContext: String?): String {
        val q = question.lowercase()
        val p = plantContext?.lowercase() ?: ""

        if (q.contains("water") && (p.contains("money") || q.contains("money plant") || p.contains("pothos") || q.contains("pothos"))) {
            return "💧 **Watering Money Plant / Golden Pothos:**\n\n" +
                    "• **Schedule:** Water approximately every 7-10 days, allowing the top 2 inches of soil to dry out completely between waterings.\n" +
                    "• **Visual Cue:** If leaves begin to look slightly limp, it's thirsty! Yellowing leaves that feel soft or mushy mean you're watering too frequently.\n" +
                    "• **Tip:** Pothos is extraordinarily resilient—it forgives mild underwatering much better than soggy soil."
        }

        if (q.contains("yellow") && (q.contains("tomato") || p.contains("tomato"))) {
            return "🍅 **Why Tomato Leaves Turn Yellow:**\n\n" +
                    "1. **Bottom Leaves First?** This is often a normal shedding of oldest leaves, or a sign of early nitrogen deficiency. Feed with a balanced vegetable fertilizer.\n" +
                    "2. **Overwatering:** Saturated roots suffocate and turn leaves pale yellow. Water deeply only when top soil feels dry.\n" +
                    "3. **Early Blight:** Watch for yellowing accompanied by brown concentric rings. Prune off lower leaves touching the soil and mulch well."
        }

        if (q.contains("sunlight") && (q.contains("rose") || p.contains("rose"))) {
            return "☀️ **Sunlight for Roses:**\n\n" +
                    "• **Requirement:** Yes! Roses are sun-loving perennials that need **at least 6 to 8 hours of direct sunlight** every day to produce vibrant blooms and resist fungal diseases like black spot.\n" +
                    "• **Morning Sun is Best:** Morning sunlight quickly evaporates morning dew from leaves, preventing fungal spore growth."
        }

        if (q.contains("fertiliz") && (q.contains("chilli") || q.contains("pepper") || p.contains("chilli") || p.contains("pepper"))) {
            return "🌶️ **Fertilizing Chilli / Pepper Plants:**\n\n" +
                    "• **Early Growth:** Feed every 2 weeks with a balanced organic liquid fertilizer (like 10-10-10) to build sturdy stems.\n" +
                    "• **Flowering & Fruit Stage:** Switch to a high-potassium & phosphorus fertilizer (such as tomato feed or bone meal). **Avoid excess nitrogen**, which creates lush green leaves but zero chillies!"
        }

        if (q.contains("brown spot") || q.contains("brown tips")) {
            return "🍂 **Causes of Brown Foliage / Spots:**\n\n" +
                    "• **Crispy Brown Leaf Tips:** Usually caused by low household humidity (< 40%) or sensitivity to chlorine/fluoride in municipal tap water. Try bottom watering or using filtered water.\n" +
                    "• **Brown Spots with Yellow Halos:** Characteristic of fungal leaf spot. Avoid overhead watering and improve room airflow.\n" +
                    "• **Brown Patches on Sun-facing Side:** Sunburn! Occurs when shade-loving plants are abruptly exposed to harsh noon rays."
        }

        if (q.contains("pest") || q.contains("bug") || q.contains("insect")) {
            return "🛡️ **Preventing & Treating Common Garden Pests:**\n\n" +
                    "• **Neem Oil Spray:** Mix 1 tsp organic neem oil + 1/2 tsp mild Castile soap in 1 liter of warm water. Spray every 7 days.\n" +
                    "• **Lukewarm Shower:** Wash undersides of leaves in the sink to dislodge aphids and spider mites.\n" +
                    "• **Sticky Traps:** Yellow sticky cards catch flying fungus gnats and thrips before they lay eggs in damp potting mix."
        }

        if (q.contains("today") || q.contains("task") || q.contains("schedule")) {
            return "📋 **Gardening Tasks Routine:**\n\n" +
                    "• Check soil moisture with your finger or moisture meter before watering any plant.\n" +
                    "• Rotate windowsill plants 90° for symmetrical, balanced growth.\n" +
                    "• Inspect leaf undersides for early signs of pests or dust buildup.\n" +
                    "• Remove spent flowers (deadheading) to encourage fresh blooming cycles."
        }

        val targetName = plantContext ?: "plant"
        return "🌿 **PlantPal Master Gardener Advice:**\n\n" +
                "For your $targetName query: Successful plant care boils down to three fundamentals—**Light**, **Water Drainage**, and **Consistency**.\n\n" +
                "• Always let the soil communicate its moisture level rather than watering solely by the calendar.\n" +
                "• Ensure all containers feature free-flowing drainage holes.\n" +
                "• If you notice sudden leaf drop or curling, check for cold drafts or sudden relocations."
    }

    fun getSeasonalDailyTip(dateStr: String, seasonStr: String): com.example.model.DailyGardenTip {
        val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        return if (month in 8..10) {
            com.example.model.DailyGardenTip(
                title = "Dial Back Watering as Sunlight Hours Wane",
                category = "Houseplants & Balcony",
                season = seasonStr,
                tip = "As daylight hours shorten in autumn, plant transpiration slows down significantly. Potting soil remains damp much longer, so extend intervals between watering to protect delicate root systems from cold rot.",
                actionItem = "Wipe dust off broad leaves with a damp cloth to maximize fading autumn light absorption.",
                funFact = "Dust on leaf surfaces can decrease photosynthetically active radiation (PAR) absorption by up to 35%!",
                dateStr = dateStr,
                isLiveAi = false
            )
        } else if (month in 2..4) {
            com.example.model.DailyGardenTip(
                title = "Spring Awakening: Gentle Feeding & Rotation",
                category = "Active Growth",
                season = seasonStr,
                tip = "Expanding daylight triggers active vegetative flushes. Resume feeding houseplants with half-strength organic kelp or balanced liquid plant food every 2-3 weeks.",
                actionItem = "Rotate windowsill pots 90 degrees clockwise for balanced, upright stem growth.",
                funFact = "Plant auxin hormones accumulate on shaded stem sides, stimulating faster elongation toward sunlight.",
                dateStr = dateStr,
                isLiveAi = false
            )
        } else {
            com.example.model.DailyGardenTip(
                title = "Microclimate Humidity & Temperature Protection",
                category = "Microclimate & Soil",
                season = seasonStr,
                tip = "Indoor heating systems lower relative humidity down to 20-30%, causing crispy leaf margins on tropical plants. Grouping pots together creates a collective humidity bubble.",
                actionItem = "Set up a shallow pebble tray filled with water beneath moisture-loving ferns and calatheas.",
                funFact = "Up to 97% of water taken up by plant roots is transpired back into the immediate air envelope!",
                dateStr = dateStr,
                isLiveAi = false
            )
        }
    }
}
