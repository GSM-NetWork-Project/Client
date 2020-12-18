package com.example.client.answer

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.Status
import com.example.client.api.RetrofitHelper
import com.example.client.question.ShowQuestionCommentActivity
import kotlinx.android.synthetic.main.activity_answer_comment.*
import kotlinx.android.synthetic.main.activity_question_comment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnswerCommentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_comment)

        text_answer_comment.text = intent.getStringExtra("text")
        write_comment_answer.setText(intent.getStringExtra("comment"))

        btn_write_comment_answer.setOnClickListener {


            if(write_comment_answer.text.toString().isEmpty()){
                Toast.makeText(this, "댓글을 적어주세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                checkSwearing(write_comment_answer.text.toString())
            }

        }
    }

    private fun checkSwearing(text : String){
        RetrofitHelper().getCheckAPI().checkSwearing(text = text).enqueue(object : Callback<GetResponse<String>>{
            override fun onResponse(
                call: Call<GetResponse<String>>,
                response: Response<GetResponse<String>>
            ) {
                if (response.isSuccessful){
                    if(response.body()!!.status == 200){
                        Toast.makeText(this@AnswerCommentActivity, "욕설은 금지입니다!", Toast.LENGTH_LONG).show()
                    } else {
                        if(intent.getBooleanExtra("isModify", false)) {
                            //댓글 수정

                            if (write_comment_answer.text.toString().isEmpty()) {
                                Toast.makeText(this@AnswerCommentActivity, "댓글을 적어주세요", Toast.LENGTH_LONG).show()
                            } else {
                                modify(write_comment_answer.text.toString())
                            }
                        } else {
                            //댓글 추가1
                            add(text)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<String>>, t: Throwable) {}

        })
    }

    private fun add(text : String){
        RetrofitHelper().getAddAPI().addAnswerComment(intent.getIntExtra("answer_id", 0), getID(), text).enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        val doneDialog = SweetAlertDialog(this@AnswerCommentActivity, SweetAlertDialog.SUCCESS_TYPE)

                        doneDialog.setCancelable(false)

                        doneDialog.setTitleText("작성이 완료 되었습니다")
                            .setConfirmClickListener {
                                doneDialog.dismiss()
                                finish()
                            }
                            .show()
                    } else {
                        Log.d("TESt", response.body()!!.status.toString())
                        showFailAdd()
                    }
                } else {
                    showFailAdd()
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {
                showFailAdd()
            }
        })
    }

    private fun modify(text : String){
        Log.d("MODIFY", text)
        val dialog = SweetAlertDialog(this@AnswerCommentActivity, SweetAlertDialog.WARNING_TYPE)

        dialog.setCancelable(true)
        dialog.progressHelper.barColor = Color.parseColor("#000064")
        dialog.setTitleText("정말 수정 하실 건가요?")
            .setConfirmClickListener {
                dialog.dismiss()
                RetrofitHelper().getModifyAPI().modifyAnswerComment(
                    answer_id = intent.getIntExtra("answer_id", 0),
                    owner_id = getID(),
                    text = text
                ).enqueue(object :
                    Callback<Status> {
                    override fun onResponse(call: Call<Status>, response: Response<Status>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == 200) {
                                Log.d("IDIDIDID",intent.getIntExtra("answer_id", 0).toString())
                                val doneDialog = SweetAlertDialog(
                                    this@AnswerCommentActivity,
                                    SweetAlertDialog.SUCCESS_TYPE
                                )

                                doneDialog.setCancelable(false)

                                Log.d("text", response.body()!!.status.toString())

                                doneDialog.setTitleText("수정이 완료 되었습니다")
                                    .setConfirmClickListener {
                                        doneDialog.dismiss()
                                        val id = intent.getIntExtra("answer_id", 0)
                                        val intent = Intent(
                                            this@AnswerCommentActivity,
                                            ShowQuestionCommentActivity::class.java
                                        )
                                        intent.putExtra("isModify", true)
                                        setResult(0, intent)
                                        finish()
                                    }
                                    .show()
                            } else {
                                showFailModify()
                            }
                        } else {
                            showFailModify()
                        }
                    }

                    override fun onFailure(call: Call<Status>, t: Throwable) {
                        showFailModify()
                    }

                })
            }.show()
    }

    private fun showFailAdd(){
        val doneDialog = SweetAlertDialog(this@AnswerCommentActivity, SweetAlertDialog.ERROR_TYPE)

        doneDialog.setCancelable(false)

        doneDialog.setTitleText("작성이 실패하였습니다")
            .setConfirmClickListener {
                doneDialog.dismiss()
            }
            .show()
    }

    private fun getID() : Int{
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        return sp.getInt("id", 0)
    }

    private fun showFailModify(){
        val doneDialog = SweetAlertDialog(this@AnswerCommentActivity, SweetAlertDialog.ERROR_TYPE)

        doneDialog.setCancelable(false)

        doneDialog.setTitleText("수정이 실패하였습니다")
            .setConfirmClickListener {
                doneDialog.dismiss()
            }
            .show()
    }
}