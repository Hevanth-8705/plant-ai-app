package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.model.DailyGardenTip
import com.example.model.HealthScanReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun analyzePlantHealth(
        plantName: String,
        symptoms: List<String>,
        bitmap: Bitmap?
    ): Result<HealthScanReport> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("No valid Gemini API key configured in secrets."))
        }

        try {
            val prompt = """
                You are PlantPal AI, a botanical specialist and master gardener.
                Analyze the plant leaf/stem issue based on the provided data.
                Plant Name/Species: $plantName
                Observed User Symptoms: ${symptoms.joinToString(", ")}

                Respond STRICTLY with a valid JSON object matching this schema (no markdown formatting, no code fences):
                {
                  "primaryDiagnosis": "Name of disease or physical issue",
                  "severity": "Mild or Moderate or Severe or Inconclusive",
                  "possibleCauses": ["cause 1", "cause 2"],
                  "environmentalFactors": "Explanation of watering, light, humidity, or soil impact",
                  "organicRemedies": ["remedy 1", "remedy 2"],
                  "chemicalOptions": ["safe commercial option 1", "option 2"],
                  "preventionTips": ["tip 1", "tip 2"],
                  "needsExpertHelp": false,
                  "confidenceNote": "Cautious disclaimer on visual diagnosis accuracy"
                }
            """.trimIndent()

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()

            // Text part
            val textPart = JSONObject().put("text", prompt)
            partsArray.put(textPart)

            // Image part if available
            bitmap?.let { bmp ->
                val base64 = bmp.toBase64()
                val inlineData = JSONObject()
                    .put("mimeType", "image/jpeg")
                    .put("data", base64)
                val imagePart = JSONObject().put("inlineData", inlineData)
                partsArray.put(imagePart)
            }

            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)

            val requestJson = JSONObject()
                .put("contents", contentsArray)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(mediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "HTTP ${response.code}"
                return@withContext Result.failure(Exception("Gemini API error ($errorBody)"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val responseObj = JSONObject(body)
            val candidateText = responseObj
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val cleanedJson = candidateText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val parsed = JSONObject(cleanedJson)
            val causes = mutableListOf<String>()
            val causesArr = parsed.optJSONArray("possibleCauses")
            if (causesArr != null) {
                for (i in 0 until causesArr.length()) causes.add(causesArr.getString(i))
            }

            val organic = mutableListOf<String>()
            val organicArr = parsed.optJSONArray("organicRemedies")
            if (organicArr != null) {
                for (i in 0 until organicArr.length()) organic.add(organicArr.getString(i))
            }

            val chemical = mutableListOf<String>()
            val chemArr = parsed.optJSONArray("chemicalOptions")
            if (chemArr != null) {
                for (i in 0 until chemArr.length()) chemical.add(chemArr.getString(i))
            }

            val prevention = mutableListOf<String>()
            val prevArr = parsed.optJSONArray("preventionTips")
            if (prevArr != null) {
                for (i in 0 until prevArr.length()) prevention.add(prevArr.getString(i))
            }

            Result.success(
                HealthScanReport(
                    plantId = null,
                    plantName = plantName,
                    symptoms = symptoms,
                    primaryDiagnosis = parsed.optString("primaryDiagnosis", "Visual Leaf Stress Detected"),
                    severity = parsed.optString("severity", "Moderate"),
                    possibleCauses = if (causes.isEmpty()) listOf("Environmental stress", "Root moisture imbalance") else causes,
                    environmentalFactors = parsed.optString("environmentalFactors", "Check soil drainage and ambient light levels."),
                    organicRemedies = if (organic.isEmpty()) listOf("Trim damaged foliage with sanitized shears", "Allow top soil to dry before watering") else organic,
                    chemicalOptions = chemical,
                    preventionTips = if (prevention.isEmpty()) listOf("Ensure proper drainage holes in container") else prevention,
                    needsExpertHelp = parsed.optBoolean("needsExpertHelp", false),
                    confidenceNote = parsed.optString("confidenceNote", "AI visual screening is preliminary. Consult local nursery extension for severe issues.")
                )
            )
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Diagnosis failed", e)
            Result.failure(e)
        }
    }

    suspend fun askGardeningAssistant(
        question: String,
        plantContext: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("No Gemini API key"))
        }

        try {
            val systemContext = "You are PlantPal AI, a warm, knowledgeable master gardener and plant care specialist. " +
                    "Provide practical, science-based plant care tips. " +
                    (if (!plantContext.isNullOrBlank()) "The user is asking about their specific plant: $plantContext. " else "") +
                    "Keep explanations friendly, concise (under 3 paragraphs), and format with clear bullet points where helpful."

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()

            partsArray.put(JSONObject().put("text", "$systemContext\n\nUser Question: $question"))
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)

            val requestJson = JSONObject().put("contents", contentsArray)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(mediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP error ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val responseObj = JSONObject(body)
            val text = responseObj
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateDailyGardenTip(
        dateStr: String,
        seasonStr: String
    ): Result<DailyGardenTip> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("No Gemini API key"))
        }

        try {
            val prompt = """
                You are PlantPal AI, a master horticulturist and plant biologist.
                Today's date is $dateStr ($seasonStr).
                Provide a daily, highly actionable gardening tip and seasonal plant care advice tailored for today's exact date and season.
                
                Respond STRICTLY with a valid JSON object matching this schema (no markdown formatting, no code fences):
                {
                  "title": "A catchy, expert 4-7 word headline",
                  "category": "One of: Houseplants, Balcony & Patio, Herbs & Edibles, Soil Health, or Microclimate",
                  "tip": "2-3 sentences of clear, practical, expert advice relevant to this seasonal window.",
                  "actionItem": "A specific 5-minute task the gardener can do today.",
                  "funFact": "A 1-sentence botanical or plant biology science insight related to this advice."
                }
            """.trimIndent()

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", prompt))
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)

            val requestJson = JSONObject().put("contents", contentsArray)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(mediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP error ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val responseObj = JSONObject(body)
            val candidateText = responseObj
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val cleaned = candidateText.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleaned)

            val tip = DailyGardenTip(
                title = json.optString("title", "Seasonal Plant Care & Light Adaptation"),
                category = json.optString("category", "Houseplants"),
                season = seasonStr,
                tip = json.optString("tip", "Adjust watering and inspect soil moisture before hydrating."),
                actionItem = json.optString("actionItem", "Perform a 2-inch knuckle soil test on your houseplants."),
                funFact = json.optString("funFact", "Transpiration slows down as ambient temperatures cool in autumn."),
                dateStr = dateStr,
                isLiveAi = true
            )
            Result.success(tip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        val scaled = if (width > 1024 || height > 1024) {
            val scale = 1024f / Math.max(width, height)
            Bitmap.createScaledBitmap(this, (width * scale).toInt(), (height * scale).toInt(), true)
        } else this
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
