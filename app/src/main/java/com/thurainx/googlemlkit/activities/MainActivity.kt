package com.thurainx.googlemlkit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.thurainx.googlemlkit.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()
    }

    private fun setListeners() {
        btnTextRecognition.setOnClickListener {
            startActivity(TextRecognitionActivity.getIntent(this))
        }

        btnFaceRecognition.setOnClickListener {
            startActivity(FaceRecognitionActivity.getIntent(this))
        }
    }
}