package com.example.client.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.client.R
import com.example.client.api.DTO.CheckQuestion

class SimilarQuestionDialog(context:Context, val arrayList: ArrayList<CheckQuestion>) : Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_similar_question)

        

    }
}