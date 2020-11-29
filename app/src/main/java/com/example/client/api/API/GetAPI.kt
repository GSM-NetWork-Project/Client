package com.example.client.api.API

import com.example.client.api.DTO.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetAPI {
    @GET("/api/get_user")
    fun getUser(
        @Query("id") id : Int? = null,
        @Query("name") name : String? = null,
        @Query("email") email : String? = null,
        @Query("password") password : String? = null
    ) : Call<GetResponse<UserResponse>>

    @GET("api/get_question")
    fun getQuestion(
        @Query("id") id : Int? = null,
        @Query("owner_id") owner_id : Int? = null,
        @Query("title") title : String? = null,
        @Query("theme") theme : String? = null,
        @Query("is_solved") is_solved : Int? = null
    ) : Call<GetResponse<QuestionResponse>>

    @GET("api/get_answer")
    fun getAnswer(
        @Query("id") id : Int? = null,
        @Query("question_id") question_id : Int? = null,
        @Query("owner_id") owner_id: Int? = null
    ) : Call<GetResponse<AnswerResponse>>

    @GET("api/get_question_comment")
    fun getQuestionComment(
        @Query("id") id : Int? = null,
        @Query("question_id") question_id : Int? = null,
        @Query("owner_id") owner_id: Int? = null
    ) : Call<GetResponse<QuestionComment>>

    @GET("api/get_answer_comment")
    fun getAnswerComment(
        @Query("id") id : Int? = null,
        @Query("answer_id") answer_id : Int? = null,
        @Query("owner_id") owner_id: Int? = null
    ) : Call<GetResponse<AnswerComment>>

    @GET("api/get_question_vote")
    fun getQuestionVote(
        @Query("question_id") question_id : Int
    ) : Call<GetResponse<QuestionVote>>

    @GET("api/get_answer_vote")
    fun getAnswerVote(
        @Query("answer_id") answer_id : Int
    ) : Call<GetResponse<AnswerVote>>

}