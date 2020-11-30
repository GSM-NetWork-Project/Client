package com.example.client.api.API

import com.example.client.api.DTO.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeleteAPI {
    @GET("/api/remove_user")
    fun deleteUser(
        @Query("id") id : Int,
        @Query("name") name : String,
        @Query("email") email : String,
        @Query("password") password : String
    ) : Call<Status>

    @GET("api/remove_question")
    fun deleteQuestion(
        @Query("id") id : Int,
        @Query("owner_id") owner_id : Int,
        @Query("title") title : String,
        @Query("theme") theme : String,
        @Query("is_solved") is_solved : Int
    ) : Call<Status>

    @GET("api/remove_answer")
    fun deleteAnswer(
        @Query("id") id : Int,
        @Query("question_id") question_id : Int,
        @Query("owner_id") owner_id: Int
    ) : Call<Status>

    @GET("api/remove_question_comment")
    fun deleteQuestionComment(
        @Query("id") id : Int,
        @Query("question_id") question_id : Int,
        @Query("owner_id") owner_id: Int
    ) : Call<Status>

    @GET("api/remove_answer_comment")
    fun deleteAnswerComment(
        @Query("id") id : Int,
        @Query("answer_id") answer_id : Int,
        @Query("owner_id") owner_id: Int
    ) : Call<Status>

    @GET("api/remove_question_vote")
    fun deleteQuestionVote(
        @Query("question_id") question_id : Int
    ) : Call<Status>

    @GET("api/remove_answer_vote")
    fun deleteAnswerVote(
        @Query("answer_id") answer_id : Int
    ) : Call<Status>
}