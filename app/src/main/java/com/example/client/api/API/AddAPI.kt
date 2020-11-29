package com.example.client.api.API


import com.example.client.api.DTO.Status
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AddAPI {
    @GET("/api/add_user")
    fun addUser(
        @Query("name") name : String,
        @Query("email") email : String,
        @Query("password") password : String
    ) : Call<Status>

    @GET("api/add_question")
    fun addQuestion(
        @Query("owner_id") owner_id : Int,
        @Query("title") title : String,
        @Query("theme") theme : String,
        @Query("text") text : String
    ) : Call<Status>

    @GET("api/add_answer")
    fun addAnswer(
        @Query("question_id") question_id : Int,
        @Query("owner_id") owner_id: Int,
        @Query("text") text : String
    ) : Call<Status>

    @GET("api/add_question_comment")
    fun addQuestionComment(
        @Query("question_id") question_id : Int,
        @Query("owner_id") owner_id: Int,
        @Query("text") text : String
    ) : Call<Status>

    @GET("api/add_answer_comment")
    fun addAnswerComment(
        @Query("answer_id") answer_id : Int,
        @Query("owner_id") owner_id: Int,
        @Query("text") text : String
    ) : Call<Status>

    @GET("api/add_question_vote")
    fun addQuestionVote(
        @Query("question_id") question_id : Int,
        @Query("owner_id") owner_id: Int,
        @Query("type") type : Int
    ) : Call<Status>

    @GET("api/add_answer_vote")
    fun addAnswerVote(
        @Query("answer_id") answer_id : Int,
        @Query("owner_id") owner_id: Int,
        @Query("type") type : Int
    ) : Call<Status>

}