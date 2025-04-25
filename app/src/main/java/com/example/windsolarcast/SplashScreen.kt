package com.example.windsolarcast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class SplashScreen : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView
    private var progressStatus = 0
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        progressBar = findViewById(R.id.progress_bar)
        Thread {
            while (progressStatus < 100) {
                progressStatus += 10
                handler.post {
                    progressBar.progress = progressStatus
                }
                Thread.sleep(300)
            }
            startActivity(Intent(this, SecondPage::class.java))
            finish()
        }.start()
    }
}