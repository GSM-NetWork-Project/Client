package com.example.client.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.client.R
import com.example.client.api.DTO.CheckQuestion
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.QuestionResponse
import com.example.client.api.DTO.UserResponse
import com.example.client.api.RetrofitHelper
import com.example.client.question.ShowQuestionActivity
import kotlinx.android.synthetic.main.item_comment_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SimilarAdapter(context: Context, val arrayList: ArrayList<CheckQuestion>) : BaseAdapter() {
    val mContext = context
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(p0: Int): Any {
        return arrayList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, converterView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.item_comment_list, parent, false)

        view.comment.text = arrayList[position].name
        RetrofitHelper().getGetAPI().getQuestion(id = arrayList[position].id).enqueue(object :
            Callback<GetResponse<QuestionResponse>> {
            override fun onResponse(
                call: Call<GetResponse<QuestionResponse>>,
                response: Response<GetResponse<QuestionResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        RetrofitHelper().getGetAPI().getUser(id = response.body()!!.result[0].owner_id).enqueue(object : Callback<GetResponse<UserResponse>>{
                            override fun onResponse(
                                call: Call<GetResponse<UserResponse>>,
                                response: Response<GetResponse<UserResponse>>
                            ) {
                                if(response.isSuccessful){
                                    if(response.body()!!.status == 200){
                                        view.comment_nickname.text = response.body()!!.result[0].name
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<GetResponse<UserResponse>>,
                                t: Throwable
                            ) {
                                Log.d("Fail", t.toString())
                            }

                        })
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<QuestionResponse>>, t: Throwable) {
                Log.d("Fail", t.toString())
            }

        })

        return view
    }
}