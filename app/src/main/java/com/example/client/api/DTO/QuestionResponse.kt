package com.example.client.api.DTO

data class QuestionResponse(
    val id : Int,
    val owner_id : Int,
    val title : String,
    val theme : String,
    val text : String,
    val is_solved : Int,
    val upload_time : String
)