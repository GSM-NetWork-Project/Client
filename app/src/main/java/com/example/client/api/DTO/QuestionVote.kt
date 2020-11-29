package com.example.client.api.DTO

data class QuestionVote(
    val question_id : Int,
    val owner_id : Int,
    val type : Int
)