package com.example.client.question

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.client.R
import com.example.client.answer.AnswerCommentActivity
import com.example.client.answer.AnswerCommentAdapter
import com.example.client.api.DTO.AnswerComment
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.QuestionComment
import com.example.client.api.DTO.Status
import com.example.client.api.RetrofitHelper
import kotlinx.android.synthetic.main.activity_show_answer_comment.*
import kotlinx.android.synthetic.main.activity_show_answer_comment.comment_list
import kotlinx.android.synthetic.main.activity_show_question_comment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowQuestionCommentActivity : AppCompatActivity() {
    var id = 0
    var arrayList = ArrayList<QuestionComment>()
    lateinit var adapter : QuestionCommentAdapter
    var text = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_question_comment)
        id = intent.getIntExtra("question_id",0)
        text = intent.getStringExtra("text")!!
        show_comment_question.text = intent.getStringExtra("title")

        question_comment_list.setOnItemClickListener { adapterView, view, position, l ->
            if(arrayList[position].owner_id == getID()){
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("어떤 작업을 할건가요?")
                dialog.setPositiveButton("삭제") { p0, p1 ->
                    RetrofitHelper().getDeleteAPI().deleteQuestionComment(arrayList[position].id,id,getID()).enqueue(object : Callback<Status>{
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            if(response.isSuccessful){
                                if(response.body()!!.status == 200){
                                    arrayList.remove(arrayList[position])
                                    setList()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Status>, t: Throwable) {

                        }

                    })
                }
                dialog.setNeutralButton("수정") { dialogInterface: DialogInterface, i: Int ->
                    val intent = Intent(this@ShowQuestionCommentActivity, QuestionCommentActivity::class.java)
                    intent.putExtra("text",text)
                    intent.putExtra("isModify",true)
                    intent.putExtra("comment", arrayList[position].text)
                    intent.putExtra("question_id",arrayList[position].question_id)
                    startActivityForResult(intent,0)
                }
                dialog.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                }
                dialog.show()
            }

        }

        setList()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.hasExtra("isModify")){
                setList()
            }
        }
    }

    private fun getID() : Int{
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        return sp.getInt("id", 0)
    }

    private fun setList(){
        RetrofitHelper().getGetAPI().getQuestionComment(question_id = id).enqueue(object :
            Callback<GetResponse<QuestionComment>> {
            override fun onResponse(
                call: Call<GetResponse<QuestionComment>>,
                response: Response<GetResponse<QuestionComment>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        arrayList = response.body()!!.result
                        adapter = QuestionCommentAdapter(this@ShowQuestionCommentActivity, arrayList)
                        question_comment_list.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<QuestionComment>>, t: Throwable) {}

        })
    }
}