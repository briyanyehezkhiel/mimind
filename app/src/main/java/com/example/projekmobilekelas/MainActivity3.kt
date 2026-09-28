package com.example.projekmobilekelas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createScrollViewWithQuestions())
    }

    private fun createScrollViewWithQuestions(): ScrollView {
        val scrollView = ScrollView(this)
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val questions = listOf(
            // E/I Questions
            "Saya lebih suka bekerja dalam kelompok daripada sendiri.",
            "Saya lebih suka berbicara langsung daripada melalui teks.",
            "Saya merasa energik saat berada di keramaian.",
            "Saya lebih suka mendengarkan daripada berbicara dalam diskusi besar.",
            // T/F Questions
            "Saya lebih suka berpikir logis daripada mengikuti perasaan.",
            "Saya sering menggunakan perasaan saya untuk membuat keputusan.",
            "Saya cenderung mementingkan keadilan daripada harmoni.",
            "Saya lebih peduli pada fakta daripada emosi.",
            // J/P Questions
            "Saya cenderung membuat keputusan dengan cepat daripada menunggu informasi lengkap.",
            "Saya suka menyusun rencana sebelum memulai sesuatu.",
            "Saya merasa nyaman dengan jadwal yang fleksibel.",
            "Saya lebih suka mengikuti langkah-langkah yang ditentukan.",
            // S/N Questions
            "Saya lebih nyaman mengikuti rutinitas daripada mencoba hal baru.",
            "Saya lebih suka fokus pada detail daripada gambaran besar.",
            "Saya sering memikirkan kemungkinan masa depan daripada hal-hal konkret.",
            "Saya lebih percaya pada pengalaman daripada intuisi."
        )

        // Dimensi MBTI untuk setiap soal
        val dimensions = listOf(
            "E/I", "E/I", "E/I", "E/I",
            "T/F", "T/F", "T/F", "T/F",
            "J/P", "J/P", "J/P", "J/P",
            "S/N", "S/N", "S/N", "S/N"
        )

        val radioGroups = mutableListOf<RadioGroup>()

        questions.forEachIndexed { index, question ->
            val questionText = TextView(this).apply {
                text = "${index + 1}. $question"
                textSize = 16f
                setPadding(0, 16, 0, 8)
            }
            linearLayout.addView(questionText)

            val radioGroup = RadioGroup(this).apply {
                orientation = RadioGroup.VERTICAL
            }

            val options = listOf("Sangat Tidak Setuju", "Tidak Setuju", "Netral", "Setuju", "Sangat Setuju")
            options.forEachIndexed { score, option ->
                val radioButton = RadioButton(this).apply {
                    text = option
                    tag = score - 2 // Skor dari -2 hingga +2
                }
                radioGroup.addView(radioButton)
            }

            radioGroups.add(radioGroup)
            linearLayout.addView(radioGroup)
        }

        val resultTextView = TextView(this).apply {
            textSize = 20f
            setPadding(0, 32, 0, 16)
            visibility = TextView.GONE // Awalnya tersembunyi
        }
        linearLayout.addView(resultTextView)

        val explanationTextView = TextView(this).apply {
            textSize = 16f
            visibility = TextView.GONE // Awalnya tersembunyi
        }
        linearLayout.addView(explanationTextView)

        val submitButton = Button(this).apply {
            text = "Submit"
            setOnClickListener {
                val results = mutableMapOf("E/I" to 0, "S/N" to 0, "T/F" to 0, "J/P" to 0)
                var allAnswered = true

                radioGroups.forEachIndexed { index, group ->
                    val selectedOption = group.checkedRadioButtonId
                    if (selectedOption != -1) {
                        val selectedRadioButton = group.findViewById<RadioButton>(selectedOption)
                        val score = selectedRadioButton.tag as Int
                        val dimension = dimensions[index]
                        results[dimension] = results[dimension]!! + score
                    } else {
                        allAnswered = false
                    }
                }

                if (allAnswered) {
                    val mbti = buildString {
                        append(if (results["E/I"]!! >= 0) "E" else "I")
                        append(if (results["S/N"]!! >= 0) "S" else "N")
                        append(if (results["T/F"]!! >= 0) "T" else "F")
                        append(if (results["J/P"]!! >= 0) "J" else "P")
                    }

                    val explanations = mapOf(
                        "E" to "Extraversion: Anda energik saat berinteraksi dengan orang lain.",
                        "I" to "Introversion: Anda lebih nyaman beraktivitas sendiri.",
                        "S" to "Sensing: Anda fokus pada fakta dan detail.",
                        "N" to "Intuition: Anda lebih fokus pada gambaran besar dan kemungkinan.",
                        "T" to "Thinking: Anda lebih suka logika dalam pengambilan keputusan.",
                        "F" to "Feeling: Anda lebih mempertimbangkan perasaan dalam keputusan.",
                        "J" to "Judging: Anda suka membuat rencana dan jadwal.",
                        "P" to "Perceiving: Anda fleksibel dan spontan."
                    )

                    val explanation = buildString {
                        append(explanations[mbti[0].toString()])
                        append("\n")
                        append(explanations[mbti[1].toString()])
                        append("\n")
                        append(explanations[mbti[2].toString()])
                        append("\n")
                        append(explanations[mbti[3].toString()])
                    }

                    resultTextView.text = "Hasil MBTI Anda: $mbti"
                    explanationTextView.text = explanation
                    resultTextView.visibility = TextView.VISIBLE
                    explanationTextView.visibility = TextView.VISIBLE
                } else {
                    Toast.makeText(this@MainActivity3, "Harap jawab semua soal.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        linearLayout.addView(submitButton)
        scrollView.addView(linearLayout)

        return scrollView
    }
}
