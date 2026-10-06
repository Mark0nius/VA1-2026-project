package cz.czmendelu.studymate.ai

import cz.czmendelu.studymate.BuildConfig
import cz.czmendelu.studymate.database.tables.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servic pro import otázek pomocí Gemini API.
 *
 * Text zadaný uživatelem odešle modelu, z odpovědi vytáhne JSON
 * a převede ho na lokální entity Question uložené k danému předmětu - v teorii :).
 */
@Singleton
class GeminiQuestionImportService @Inject constructor() {

    private val client = OkHttpClient()

    suspend fun generateQuestions(
        subjectId: Long,
        sourceText: String
    ): List<Question> {
        // Síťové volání a parsování odpovědi.
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY

            if (apiKey.isBlank()) {
                throw IllegalStateException("Gemini API key is missing.")
            }

            val prompt = createPrompt(sourceText)
            val requestBody = createRequestBody(prompt)

            // API klíč se vkládá do URL až při sestavení požadavku.
            val request = Request.Builder()
                .url(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent?key=$apiKey"
                )
                .post(requestBody)
                .build()

            val response = client
                .newCall(request)
                .execute()

            val responseText = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                throw IllegalStateException(
                    "Gemini request failed: ${response.code} $responseText"
                )
            }

            val generatedText = extractGeneratedText(responseText)
            val jsonText = cleanJson(generatedText)

            //JSON pole se mapuje na databázové otázky aplikace.
            parseQuestions(
                subjectId = subjectId,
                jsonText = jsonText
            )
        }
    }

    private fun createPrompt(sourceText: String): String {
        // Prompt omezuje na JSON.
        return """
        Generate 5 study flashcards in the same language as the text.
        Return only JSON:
        [{"questionText":"...","answerText":"...","questionType":"FLASHCARD"}]

        Text:
        $sourceText
    """.trimIndent()
    }

    private fun createRequestBody(prompt: String) =
        JSONObject()
            .put(
                "contents",
                JSONArray()
                    .put(
                        JSONObject()
                            .put(
                                "parts",
                                JSONArray()
                                    .put(
                                        JSONObject()
                                            .put("text", prompt)
                                    )
                            )
                    )
            )
            .put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.3)
                    .put("maxOutputTokens", 2048)
                    .put("responseMimeType", "application/json")
            )
            .toString()
            .toRequestBody("application/json".toMediaType())

    private fun extractGeneratedText(responseText: String): String {
        val root = JSONObject(responseText)

        val candidates = root.optJSONArray("candidates")
            ?: throw IllegalStateException("Gemini returned no candidates.")

        if (candidates.length() == 0) {
            throw IllegalStateException("Gemini returned empty candidates.")
        }

        val content = candidates
            .getJSONObject(0)
            .getJSONObject("content")

        val parts = content.getJSONArray("parts")

        if (parts.length() == 0) {
            throw IllegalStateException("Gemini returned no text parts.")
        }

        return parts
            .getJSONObject(0)
            .getString("text")
    }

    private fun cleanJson(text: String): String {
        // Některé modely mohou vrátit JSON v markdown bloku
        val cleaned = text
            .replace("```json", "")
            .replace("```", "")
            .trim()

        val arrayStart = cleaned.indexOf("[")
        val arrayEnd = cleaned.lastIndexOf("]")

        if (arrayStart == -1 || arrayEnd == -1 || arrayEnd <= arrayStart) {
            throw IllegalStateException("Gemini did not return a valid JSON array.")
        }

        return cleaned.substring(
            startIndex = arrayStart,
            endIndex = arrayEnd + 1
        )
    }

    private fun parseQuestions(
        subjectId: Long,
        jsonText: String
    ): List<Question> {
        val jsonArray = JSONArray(jsonText)
        val questions = mutableListOf<Question>()

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(index)

            val questionText = item
                .optString("questionText")
                .trim()

            val answerText = item
                .optString("answerText")
                .trim()

            val questionType = item
                .optString("questionType", "FLASHCARD")
                .trim()
                .ifBlank {
                    "FLASHCARD"
                }

            if (questionText.isNotBlank() && answerText.isNotBlank()) {
                questions.add(
                    Question(
                        subjectId = subjectId,
                        questionText = questionText,
                        answerText = answerText,
                        questionType = questionType,
                        imagePath = null,
                        correctAnswer = null
                    )
                )
            }
        }

        return questions
    }
}
