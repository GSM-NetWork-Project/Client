package com.example.client.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.client.R
import com.example.client.api.DTO.CheckQuestion
import com.example.client.question.ShowQuestionActivity
import kotlinx.android.synthetic.main.dialog_similar_question.*

class SimilarQuestionDialog(context:Context, val arrayList: ArrayList<CheckQuestion>) : Dialog(context){
    val mContext = context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_similar_question)

        val nameList = ArrayList<String>()

        for(a in arrayList){
            nameList.add(a.name)
        }

        list_similar_question.adapter = ArrayAdapter<String>(mContext, android.R.layout.activity_list_item)

        list_similar_question.setOnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, long: Long ->
            val intent = Intent(mContext, ShowQuestionActivity::class.java)
            intent.putExtra("question_id", arrayList[position].id)
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }
}