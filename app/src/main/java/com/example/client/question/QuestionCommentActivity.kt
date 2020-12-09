package com.example.client.question

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.R
import com.example.client.answer.ShowAnswerCommentActivity
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.Status
import com.example.client.api.RetrofitHelper
import kotlinx.android.synthetic.main.activity_answer_comment.*
import kotlinx.android.synthetic.main.activity_question_comment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestionCommentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_comment)

        comment_question_text.text = intent.getStringExtra("title")
        text_question_comment.text = intent.getStringExtra("text")
        write_comment_question.setText(intent.getStringExtra("comment"))

        btn_write_comment.setOnClickListener {
            if (checkSwearing(write_comment_question.text.toString())) {
                Toast.makeText(this@QuestionCommentActivity, "욕설은 금지입니다!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                if (intent.getBooleanExtra("isModify", false)) {
                    RetrofitHelper().getModifyAPI().modifyQuestionComment(
                        intent.getIntExtra("question_id", 0),
                        getID(),
                        write_comment_question.text.toString()
                    ).enqueue(object : Callback<Status> {
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            if (response.isSuccessful) {
                                if (response.body()!!.status == 200) {
                                    val doneDialog = SweetAlertDialog(
                                        this@QuestionCommentActivity,
                                        SweetAlertDialog.SUCCESS_TYPE
                                    )

                                    doneDialog.setCancelable(false)

                                    doneDialog.setTitleText("수정이 완료 되었습니다")
                                        .setConfirmClickListener {
                                            doneDialog.dismiss()
                                            val intent = Intent(
                                                this@QuestionCommentActivity,
                                                ShowAnswerCommentActivity::class.java
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
                } else {
                    RetrofitHelper().getAddAPI().addQuestionComment(
                        intent.getIntExtra("question_id", 0),
                        getID(),
                        write_comment_question.text.toString()
                    ).enqueue(object : Callback<Status> {
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            if (response.isSuccessful) {
                                if (response.body()!!.status == 200) {
                                    val doneDialog = SweetAlertDialog(
                                        this@QuestionCommentActivity,
                                        SweetAlertDialog.SUCCESS_TYPE
                                    )

                                    doneDialog.setCancelable(false)

                                    doneDialog.setTitleText("작성이 완료 되었습니다")
                                        .setConfirmClickListener {
                                            doneDialog.dismiss()
                                            finish()
                                        }
                                        .show()
                                } else {
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
            }
        }
    }

    private fun showFailAdd(){
        val doneDialog = SweetAlertDialog(this@QuestionCommentActivity, SweetAlertDialog.ERROR_TYPE)

        doneDialog.setCancelable(false)

        doneDialog.setTitleText("작성이 실패하였습니다")
            .setConfirmClickListener {
                doneDialog.dismiss()
            }
            .show()
    }

    private fun checkSwearing(text : String) : Boolean {
        var isSwearing = false
        RetrofitHelper().getCheckAPI().checkSwearing(text = text).enqueue(object : Callback<GetResponse<String>>{
            override fun onResponse(
                call: Call<GetResponse<String>>,
                response: Response<GetResponse<String>>
            ) {
                if (response.isSuccessful){
                    if(response.body()!!.status == 200){
                        isSwearing = true
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<String>>, t: Throwable) {}

        })

        return isSwearing
    }

    private fun getID() : Int{
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        return sp.getInt("id", 0)
    }
    private fun showFailModify(){
        val doneDialog = SweetAlertDialog(this@QuestionCommentActivity, SweetAlertDialog.ERROR_TYPE)

        doneDialog.setCancelable(false)

        doneDialog.setTitleText("수정이 실패하였습니다")
            .setConfirmClickListener {
                doneDialog.dismiss()
            }
            .show()
    }

}