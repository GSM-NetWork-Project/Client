package com.example.client.question

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.QuestionComment
import com.example.client.api.DTO.UserResponse
import com.example.client.api.RetrofitHelper
import kotlinx.android.synthetic.main.item_comment_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.zip.Inflater

class QuestionCommentAdapter(val context : Context, val arrayList: ArrayList<QuestionComment>) : BaseAdapter() {
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
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val root = inflater.inflate(R.layout.item_comment_list, parent, false)

        RetrofitHelper().getGetAPI().getUser(id = arrayList[position].owner_id).enqueue(object :
            Callback<GetResponse<UserResponse>> {
            override fun onResponse(
                call: Call<GetResponse<UserResponse>>,
                response: Response<GetResponse<UserResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        root.comment_nickname.text = response.body()!!.result[0].name
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable){}
        })
        root.comment.text = arrayList[position].text


        return root
    }
}