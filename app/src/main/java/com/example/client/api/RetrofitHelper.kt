package com.example.client.api

import com.example.client.api.API.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitHelper {

    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://def3c747ac93.ngrok.io/")      //ngrok를 통한 임시 서버 배포 (지속적인 변경 필요)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getAddAPI() : AddAPI = retrofit.create(AddAPI::class.java)
    fun getGetAPI() : GetAPI = retrofit.create(GetAPI::class.java)
    fun getModifyAPI() : ModifyAPI = retrofit.create(ModifyAPI::class.java)
    fun getDeleteAPI() : DeleteAPI = retrofit.create(DeleteAPI::class.java)
    fun getCheckAPI() : CheckAPI = retrofit.create(CheckAPI::class.java)

}