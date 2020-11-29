package com.example.client.api

import com.example.client.api.API.AddAPI
import com.example.client.api.API.GetAPI
import com.example.client.api.API.ModifyAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitHelper {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getAddAPI() : AddAPI = retrofit.create(AddAPI::class.java)
    fun getGetAPI() : GetAPI = retrofit.create(GetAPI::class.java)
    fun getModifyAPI() : ModifyAPI = retrofit.create(ModifyAPI::class.java)

}