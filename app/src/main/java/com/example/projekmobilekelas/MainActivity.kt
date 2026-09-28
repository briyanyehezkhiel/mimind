package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.DatabaseHelperFirebase
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var databaseHelper: DatabaseHelperFirebase
    private lateinit var logoutButton: Button
    private lateinit var welcomeTextView: TextView
    private lateinit var gotoActivity2Button: Button

    // Mood and Stress Level UI components
    private lateinit var moodSeekBar: SeekBar
    private lateinit var moodValue: TextView
    private lateinit var stressLevelSeekBar: SeekBar
    private lateinit var stressLevelValue: TextView

    // UI components for mood and stress level groups
    private lateinit var moodGroup: LinearLayout
    private lateinit var stressGroup: LinearLayout

    private val client = OkHttpClient()
    private var username: String? = null
    private val chatHistory = mutableListOf<ChatMessage>()
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "OPENAI_API_KEY_REMOVED"  // Replace with secure method of storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isLoggedIn = sharedPreferences.getBoolean("logged_in", false)

        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelperFirebase(this)

        username = sharedPreferences.getString("username", "User")

        inputText = findViewById(R.id.inputText)
        welcomeTextView = findViewById(R.id.welcomeTextView)
        outputText = findViewById(R.id.outputText)
        sendButton = findViewById(R.id.sendButton)
        moodSeekBar = findViewById(R.id.moodSeekBar)
        moodValue = findViewById(R.id.moodValue)
        stressLevelSeekBar = findViewById(R.id.stressLevelSeekBar)
        stressLevelValue = findViewById(R.id.stressLevelValue)
        logoutButton = findViewById(R.id.logoutButton)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        gotoActivity2Button = findViewById(R.id.gotoActivity2Button)

        // Mood and Stress Level Groups
        moodGroup = findViewById(R.id.moodGroup)
        stressGroup = findViewById(R.id.stressGroup)

        welcomeTextView.text = "Welcome, $username!"

        chatAdapter = ChatAdapter(chatHistory)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        loadChatHistory()

        // Mood SeekBar Change Listener
        moodSeekBar.setOnSeekBarChangeListener(createSeekBarListener(moodValue, arrayOf("Sangat Sedih", "Sedih", "Netral", "Bahagia", "Sangat Bahagia")))

        // Stress Level SeekBar Change Listener
        stressLevelSeekBar.setOnSeekBarChangeListener(createSeekBarListener(stressLevelValue, arrayOf("Rendah", "Sedang", "Tinggi")))

        sendButton.setOnClickListener {
            val userInput = inputText.text.toString()
            if (userInput.isNotEmpty()) {
                sendChatRequest(userInput, moodSeekBar.progress, stressLevelSeekBar.progress)
            } else {
                Toast.makeText(this@MainActivity, "Please enter text", Toast.LENGTH_SHORT).show()
            }
        }

        gotoActivity2Button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
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
    }

    // This method will handle the onClick event for the FAB
    fun onFabClick(view: View) {
        // Toggle visibility of mood and stress groups
        moodGroup.visibility = if (moodGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        stressGroup.visibility = if (stressGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun createSeekBarListener(valueTextView: TextView, labels: Array<String>) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            valueTextView.text = "${labels[progress]}"
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    private fun sendChatRequest(userInput: String, mood: Int, stressLevel: Int) {
        val moodLabels = arrayOf("Sangat Sedih", "Sedih", "Netral", "Bahagia", "Sangat Bahagia")
        val stressLevelLabels = arrayOf("Rendah", "Sedang", "Tinggi")

        val JSON = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            val messages = JSONArray().apply {
                val message = JSONObject()
                message.put("role", "user")
                message.put("content", "Anda adalah bot dukungan kesehatan mental. Berikan respons berdasarkan suasana hati dan tingkat stres pengguna. Pesan pengguna: $userInput. Suasana hati pengguna: ${moodLabels[mood]}. Tingkat stres pengguna: ${stressLevelLabels[stressLevel]}. Berdasarkan suasana hati pengguna, jika Sangat Sedih: Berikan dukungan yang mengangkat semangat. Sedih: Tawarkan dorongan dan harapan. Netral: Pertahankan nada yang seimbang dan tenang. Bahagia: Bagikan kebahagiaan dan rayakan perasaan positif. Sangat Bahagia: Dorong untuk berbagi kebahagiaan dan rasa syukur. Berdasarkan tingkat stres pengguna, jika Rendah: Tanggapi dengan dorongan yang ceria. Sedang: Tanggapi dengan dukungan yang normal. Tinggi: Tanggapi dengan nasihat yang menenangkan.")
                put(message)
            }
            put("messages", messages)
        }

        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    outputText.text = "Request failed"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val choices = jsonResponse.getJSONArray("choices")
                        val firstChoice = choices.getJSONObject(0)
                        val reply = firstChoice.getJSONObject("message").getString("content")

                        runOnUiThread {
                            outputText.text = reply
                            saveChatHistory(userInput, reply, mood, stressLevel)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    runOnUiThread {
                        outputText.text = "Response not successful"
                    }
                }
            }
        })
    }

    private fun saveChatHistory(userInput: String, aiResponse: String, mood: Int, stressLevel: Int) {
        // Save user and AI messages in the database
        databaseHelper.saveChatMessage(username!!, userInput, "$username")
        databaseHelper.saveChatMessage(username!!, aiResponse, "rusdi AI")

        // Add messages to chat history and update UI
        chatHistory.add(ChatMessage("$username", userInput))
        chatHistory.add(ChatMessage("rusdi AI", aiResponse))
        chatAdapter.notifyDataSetChanged()
    }

    private fun loadChatHistory() {
        databaseHelper.loadChatHistory(username!!) { chatHistoryFromDb ->
            chatHistory.addAll(chatHistoryFromDb)
            chatAdapter.notifyDataSetChanged()
        }
    }
}
