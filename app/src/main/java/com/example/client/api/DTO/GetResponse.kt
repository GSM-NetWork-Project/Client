package com.example.client.api.DTO

data class GetResponse<T>(
    val status : Int,
    val result : ArrayList<T>
)