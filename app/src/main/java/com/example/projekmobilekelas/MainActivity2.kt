package com.example.projekmobilekelas


//import TestMbti
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity2 : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var outputText2: TextView  // New output field for algorithmic result
    private lateinit var sendButton: Button
    private lateinit var logoutButton: Button
    private lateinit var welcomeTextView: TextView
    private lateinit var gotoActivity3Button: Button

    // SeekBars for each parameter
    private lateinit var intensitasSeekBar: SeekBar
    private lateinit var durasiSeekBar: SeekBar
    private lateinit var frekuensiSeekBar: SeekBar
    private lateinit var keseharianSeekBar: SeekBar
    private lateinit var polaNegatifSeekBar: SeekBar
    private lateinit var ketakutanSeekBar: SeekBar
    private lateinit var konsentrasiSeekBar: SeekBar
    private lateinit var kelelahanSeekBar: SeekBar
    private lateinit var tidurSeekBar: SeekBar
    private lateinit var minatSeekBar: SeekBar
    private lateinit var isolasiSeekBar: SeekBar

    private val client = OkHttpClient()
    private var username: String? = null
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "OPENAI_API_KEY_REMOVED" // Replace with your actual API Key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check login
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isLoggedIn = sharedPreferences.getBoolean("logged_in", false)
        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Set layout
        setContentView(R.layout.activity_main2)

        // Initialize UI components
        inputText = findViewById(R.id.inputText)
        outputText = findViewById(R.id.outputText)
        outputText2 = findViewById(R.id.outputText2)  // Initialize second output field
        sendButton = findViewById(R.id.sendButton)
        logoutButton = findViewById(R.id.logoutButton)
        welcomeTextView = findViewById(R.id.welcomeTextView)
        gotoActivity3Button = findViewById(R.id.gotoActivity3Button)

        // Initialize SeekBars
        intensitasSeekBar = findViewById(R.id.intensitasSeekBar)
        durasiSeekBar = findViewById(R.id.durasiSeekBar)
        frekuensiSeekBar = findViewById(R.id.frekuensiSeekBar)
        keseharianSeekBar = findViewById(R.id.keseharianSeekBar)
        polaNegatifSeekBar = findViewById(R.id.polaNegatifSeekBar)
        ketakutanSeekBar = findViewById(R.id.ketakutanSeekBar)
        konsentrasiSeekBar = findViewById(R.id.konsentrasiSeekBar)
        kelelahanSeekBar = findViewById(R.id.kelelahanSeekBar)
        tidurSeekBar = findViewById(R.id.tidurSeekBar)
        minatSeekBar = findViewById(R.id.minatSeekBar)
        isolasiSeekBar = findViewById(R.id.isolasiSeekBar)

        username = sharedPreferences.getString("username", "User")
        welcomeTextView.text = "Welcome, $username!"

        sendButton.setOnClickListener {
            val userInput = inputText.text.toString()
            if (userInput.isNotEmpty()) {
                val scores = calculateScores()
                displayAlgorithmResult(scores)
                sendChatRequest(userInput, scores)
            } else {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
            }
        }

        logoutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean("logged_in", false)
            editor.remove("username")
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        gotoActivity3Button.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
    }

    private fun calculateScores(): Map<String, Int> {
        // Expanded conditions with calculated scores
        val ocdScore = (polaNegatifSeekBar.progress * 3) + (frekuensiSeekBar.progress * 2) + intensitasSeekBar.progress
        val gadScore = (konsentrasiSeekBar.progress * 2) + (keseharianSeekBar.progress * 3) + (durasiSeekBar.progress * 2)
        val ptsdScore = (ketakutanSeekBar.progress * 3) + (isolasiSeekBar.progress * 2) + (frekuensiSeekBar.progress * 2)
        val depressionScore = (keseharianSeekBar.progress * 2) + (kelelahanSeekBar.progress * 3) + minatSeekBar.progress
        val insomniaScore = (tidurSeekBar.progress * 3) + (isolasiSeekBar.progress * 2) + konsentrasiSeekBar.progress

        return mapOf(
            "OCD" to ocdScore,
            "GAD" to gadScore,
            "PTSD" to ptsdScore,
            "Depression" to depressionScore,
            "Insomnia" to insomniaScore
        )
    }

    private fun displayAlgorithmResult(scores: Map<String, Int>) {
        // Get the highest score and determine condition
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100  // Adjust scaling factor

        outputText2.text = "Condition: $condition\nConfidence: ${confidence.toInt()}%"
    }

    private fun sendChatRequest(userInput: String, scores: Map<String, Int>) {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100  // Adjust scaling factor

        val userPrompt = """
            User Message: "$userInput"
            Analysis suggests a high likelihood of $condition with a confidence of ${confidence.toInt()}%.
        """

        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userPrompt)
                })
            }
            put("messages", messages)
        }

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { outputText.text = "Request failed" }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val reply = JSONObject(responseData).getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content")

                    runOnUiThread { outputText.text = reply }
                } else {
                    runOnUiThread { outputText.text = "Response not successful" }
                }
            }
        })
    }
}
