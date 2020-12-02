package com.example.client.my

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.client.R
import com.example.client.api.DTO.AnswerResponse
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.UserResponse
import com.example.client.api.RetrofitHelper
import kotlinx.android.synthetic.main.item_answer.view.*
import kotlinx.android.synthetic.main.item_answer_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAnswerAdapter(val context : Context, val arrayList: ArrayList<AnswerResponse>) : BaseAdapter() {
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
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_answer_list, parent, false)

        RetrofitHelper().getGetAPI().getUser(id = arrayList[position].owner_id).enqueue(object :
            Callback<GetResponse<UserResponse>> {
            override fun onResponse(
                call: Call<GetResponse<UserResponse>>,
                response: Response<GetResponse<UserResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        view.item_user_name.text = response.body()!!.result[0].name
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {}

        })
        view.item_answer_text.text = arrayList[position].text

        return view
    }

}