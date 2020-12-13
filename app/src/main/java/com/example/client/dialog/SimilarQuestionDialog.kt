package com.example.client.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.client.R
import com.example.client.api.DTO.CheckQuestion
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.QuestionResponse
import com.example.client.api.RetrofitHelper
import com.example.client.question.ShowQuestionActivity
import kotlinx.android.synthetic.main.dialog_similar_question.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SimilarQuestionDialog(context:Context, val arrayList: ArrayList<CheckQuestion>) : Dialog(context){
    val mContext = context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_similar_question)



        list_similar_question.adapter = SimilarAdapter(mContext, arrayList)

        list_similar_question.setOnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, long: Long ->

            RetrofitHelper().getGetAPI().getQuestion(id = arrayList[position].id).enqueue(object : Callback<GetResponse<QuestionResponse>>{
                override fun onResponse(
                    call: Call<GetResponse<QuestionResponse>>,
                    response: Response<GetResponse<QuestionResponse>>
                ) {
                    if(response.isSuccessful){
                        if(response.body()!!.status == 200){
                            val intent = Intent(mContext, ShowQuestionActivity::class.java)
                            intent.putExtra("question_id", arrayList[position].id)
                            intent.putExtra("title", response.body()!!.result[0].title)
                            intent.putExtra("text", response.body()!!.result[0].text)
                            intent.putExtra("owner_id", response.body()!!.result[0].owner_id)
                            mContext.startActivity(intent)
                            dismiss()

                        }
                    }
                }

                override fun onFailure(call: Call<GetResponse<QuestionResponse>>, t: Throwable) {
                    Log.d("Fail", t.toString())
                }

            })


        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }
}