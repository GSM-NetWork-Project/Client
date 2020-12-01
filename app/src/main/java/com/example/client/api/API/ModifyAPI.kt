package com.example.client.api.API

import com.example.client.api.DTO.Status
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ModifyAPI {
    @GET("api/modify_user")
    fun modifyUser(
        @Query("where_id") id : Int,
        @Query("set_name") name : String? = null,
        @Query("set_password") password : String? = null
    ) : Call<Status>

    @GET("api/modify_question")
    fun modifyQuestion(
        @Query("where_id") id : Int,
        @Query("where_owner_id") owner_id : Int,
        @Query("set_title") title : String? = null,
        @Query("set_theme") theme : String? = null,
        @Query("set_text") text : String? = null
    ) : Call<Status>

    @GET("api/modify_answer")
    fun modifyAnswer(
        @Query("where_question_id") question_id : Int,
        @Query("where_owner_id") owner_id: Int,
        @Query("set_text") text : String
    ) : Call<Status>

    @GET("api/modify_question_comment")
    fun modifyQuestionComment(
        @Query("where_question_id") question_id : Int,
        @Query("where_owner_id") owner_id: Int,
        @Query("set_text") text : String
    ) : Call<Status>

    @GET("api/modify_answer_comment")
    fun modifyAnswerComment(
        @Query("where_answer_id") answer_id : Int,
        @Query("where_owner_id") owner_id: Int,
        @Query("set_text") text : String
    ) : Call<Status>

    @GET("api/modify_question_vote")
    fun modifyQuestionVote(
        @Query("where_question_id") question_id : Int,
        @Query("where_owner_id") owner_id: Int,
        @Query("set_type") type : Int
    ) : Call<Status>

    @GET("api/modify_answer_vote")
    fun modifyAnswerVote(
        @Query("where_answer_id") answer_id : Int,
        @Query("where-owner_id") owner_id: Int,
        @Query("set_type") type : Int
    ) : Call<Status>
}