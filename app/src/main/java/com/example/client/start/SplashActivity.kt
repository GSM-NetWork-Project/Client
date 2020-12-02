package com.example.client.start

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.client.MainActivity
import com.example.client.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
                if (getID() == 0) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
        }, 3000)
    }
    private fun getID() : Int{
        val sp = getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return sp.getInt("id", 0)
    }

}