package com.example.client.api

import com.example.client.api.API.AddAPI
import com.example.client.api.API.DeleteAPI
import com.example.client.api.API.GetAPI
import com.example.client.api.API.ModifyAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitHelper {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://f10cc5cb776f.ngrok.io")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getAddAPI() : AddAPI = retrofit.create(AddAPI::class.java)
    fun getGetAPI() : GetAPI = retrofit.create(GetAPI::class.java)
    fun getModifyAPI() : ModifyAPI = retrofit.create(ModifyAPI::class.java)
    fun getDeleteAPI() : DeleteAPI = retrofit.create(DeleteAPI::class.java)

}