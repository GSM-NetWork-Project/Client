package com.example.client.start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.client.MainActivity
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.UserResponse
import com.example.client.api.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if(getID() == 0) {
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