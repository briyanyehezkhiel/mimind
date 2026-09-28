package com.example.projekmobilekelas

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultText = intent.getStringExtra("MBTI_RESULT")
        val explanationText = intent.getStringExtra("MBTI_EXPLANATION")

        val resultTextView: TextView = findViewById(R.id.resultTextView)
        val explanationTextView: TextView = findViewById(R.id.explanationTextView)

        resultTextView.text = "Hasil MBTI Anda: $resultText"
        explanationTextView.text = explanationText
    }
}
