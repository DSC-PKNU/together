package com.example.together

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.UiThread
import com.example.together.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tvTitle = findViewById(R.id.tvTitle)

        splashAnimation()
        startLoading()
    }

    private fun startLoading() {
        val handler = Handler()

        handler.postDelayed(Runnable {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }, 2000)
    }

    @UiThread
    private fun splashAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.splash)
        tvTitle.startAnimation(animation)
    }
}