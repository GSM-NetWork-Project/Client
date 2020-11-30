package com.example.client.question

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.client.R
import com.example.client.api.DTO.QuestionResponse
import kotlinx.android.synthetic.main.item_question_list.view.*

class QuestionAdapter(val context : Context, private val arrayList: ArrayList<QuestionResponse>) : BaseAdapter() {
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
        val converterView = inflater.inflate(R.layout.item_question_list, parent,false)

        converterView.item_title.text = arrayList[position].title
        converterView.item_theme.text = arrayList[position].theme

        return converterView
    }
}