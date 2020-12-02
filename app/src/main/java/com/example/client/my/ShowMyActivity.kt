package com.example.client.my

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.client.R
import com.example.client.api.DTO.*
import com.example.client.api.RetrofitHelper
import com.example.client.question.QuestionAdapter
import kotlinx.android.synthetic.main.activity_show_my.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowMyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_my)

        show_my_type.text = intent.getStringExtra("type")

        val service = RetrofitHelper().getGetAPI()

        when(intent.getStringExtra("type")){
            "내 질문 보기" -> {
                service.getQuestion(owner_id = getID()).enqueue(object : Callback<GetResponse<QuestionResponse>>{
                    override fun onResponse(
                        call: Call<GetResponse<QuestionResponse>>,
                        response: Response<GetResponse<QuestionResponse>>
                    ) {
                        if(response.isSuccessful){
                            if(response.body()!!.status == 200){
                                my_list.adapter = QuestionAdapter(this@ShowMyActivity, response.body()!!.result)
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<GetResponse<QuestionResponse>>,
                        t: Throwable
                    ) {}

                })
            }
            "내 답변 보기" -> {
                service.getAnswer(owner_id = getID()).enqueue(object : Callback<GetResponse<AnswerResponse>>{
                    override fun onResponse(
                        call: Call<GetResponse<AnswerResponse>>,
                        response: Response<GetResponse<AnswerResponse>>
                    ) {
                        if(response.isSuccessful){
                            if(response.body()!!.status == 200){
                                my_list.adapter = MyAnswerAdapter(this@ShowMyActivity, response.body()!!.result)
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetResponse<AnswerResponse>>, t: Throwable) {}

                })
            }
            "내 댓글 보기" -> {
                val arrayList = ArrayList<Comment>()
                service.getQuestionComment(owner_id = getID()).enqueue(object : Callback<GetResponse<QuestionComment>>{
                    override fun onResponse(
                        call: Call<GetResponse<QuestionComment>>,
                        response: Response<GetResponse<QuestionComment>>
                    ) {
                        if(response.isSuccessful){
                            if(response.body()!!.status == 200){
                                for(f in response.body()!!.result){
                                    arrayList.add(Comment(f.owner_id, f.text))
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetResponse<QuestionComment>>, t: Throwable) {}

                })

                service.getAnswerComment(owner_id = getID()).enqueue(object : Callback<GetResponse<AnswerComment>>{
                    override fun onResponse(
                        call: Call<GetResponse<AnswerComment>>,
                        response: Response<GetResponse<AnswerComment>>
                    ) {
                        if(response.isSuccessful){
                            if(response.body()!!.status == 200){
                                for(f in response.body()!!.result){
                                    arrayList.add(Comment(f.owner_id, f.text))
                                }
                            }
                        }
                        my_list.adapter = MyCommentAdapter(this@ShowMyActivity, arrayList)
                    }

                    override fun onFailure(call: Call<GetResponse<AnswerComment>>, t: Throwable) {}

                })


            }
        }
    }

    private fun getID() : Int{
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        return sp.getInt("id", 0)
    }
}

data class Comment(
    val owner_id : Int,
    val text : String
)