package com.example.windsolarcast

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView

class ThirdPage : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third_page)

        val searchBtn = findViewById<Button>(R.id.buttonsearch2)
        val animatedSun = findViewById<LottieAnimationView>(R.id.animated_sun)
        val switchToWind = findViewById<SwitchCompat>(R.id.switch1)
        val cardSolarData = findViewById<CardView>(R.id.card_solar_data)

        // Receive data from MainActivity
        val city = intent.getStringExtra("city")
        val solarRadiation = intent.getDoubleExtra("SolarRadiation", 0.0)
        val clearSkySolarRadiation = intent.getDoubleExtra("ClearSkySolarRadiation", 0.0)
        val longwaveRadiation = intent.getDoubleExtra("LongwaveRadiation", 0.0)
        val topAtmosphereSolarRadiation = intent.getDoubleExtra("TopAtmosphereSolarRadiation", 0.0)

        // Animation
        animatedSun.playAnimation()
        val animator = ObjectAnimator.ofFloat(animatedSun, "translationX", -200f, 50f)
        animator.duration = 2000
        animator.start()

        // Navigate back to MainActivity
        searchBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Switch to Wind View (FourthPage)
        switchToWind.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchToWind.text = "Switch To Wind"

                val fadeOut = ObjectAnimator.ofFloat(switchToWind, "alpha", 1f, 0f)
                fadeOut.duration = 300
                fadeOut.start()

                fadeOut.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator) {
                        val intent = Intent(this@ThirdPage, FourthPage::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            } else {
                switchToWind.text = "Switch To Solar"
            }
        }

        // Navigate to SolarDataActivity and pass solar-related values
        cardSolarData.setOnClickListener {
            val intent = Intent(this, SolarDataActivity::class.java)
            intent.putExtra("city", city)
            intent.putExtra("SolarRadiation", solarRadiation)
            intent.putExtra("ClearSkySolarRadiation", clearSkySolarRadiation)
            intent.putExtra("LongwaveRadiation", longwaveRadiation)
            intent.putExtra("TopAtmosphereSolarRadiation", topAtmosphereSolarRadiation)
            startActivity(intent)
        }
    }
}
