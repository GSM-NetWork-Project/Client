package com.example.client.answer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.QuestionResponse
import com.example.client.api.DTO.Status
import com.example.client.api.RetrofitHelper
import com.example.client.question.ShowQuestionActivity
import kotlinx.android.synthetic.main.activity_show_question.*
import kotlinx.android.synthetic.main.activity_write_answer.*
import kotlinx.android.synthetic.main.activity_write_question.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WriteAnswerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_answer)

        setQuestion(intent.getIntExtra("question_id",0))
        write_answer_text.setText(intent.getStringExtra("edit_answer"))

        btn_write_answer.setOnClickListener {
            if(checkSwearing(write_answer_text.text.toString())){
                val dialog = SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.ERROR_TYPE)

                dialog.setCancelable(false)

                dialog.setTitleText("욕설은 금지입니다!!")
                    .setConfirmClickListener {
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener
            }
            if(write_answer_text.text.toString().length > 250) {
                val dialog =
                    SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.ERROR_TYPE)

                dialog.setCancelable(false)

                dialog.setTitleText("내용이 250자 이하인지 확인해주세요!")
                    .setConfirmClickListener {
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener
            } else if(write_answer_text.text.toString().isEmpty()){
                val dialog =
                    SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.ERROR_TYPE)

                dialog.setCancelable(false)

                dialog.setTitleText("내용을 적어주세요!")
                    .setConfirmClickListener {
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener

            } else {
                if(intent.getBooleanExtra("is_edit_answer", false)){
                    RetrofitHelper().getModifyAPI().modifyAnswer(intent.getIntExtra("question_id",0), getID(), write_answer_text.text.toString()).enqueue(object : Callback<Status>{
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            if(response.isSuccessful){
                                if(response.body()!!.status == 200){
                                    val dialog =
                                        SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.SUCCESS_TYPE)

                                    dialog.setCancelable(false)

                                    dialog.setTitleText("수정에 성공했습니다")
                                        .setConfirmClickListener {
                                            val intent = Intent(this@WriteAnswerActivity, ShowQuestionActivity::class.java)
                                            intent.putExtra("writeAnswer", true)
                                            setResult(0, intent)
                                            finish()
                                            dialog.dismiss()
                                        }
                                        .show()
                                }  else {
                                    val dialog =
                                        SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.ERROR_TYPE)

                                    dialog.setCancelable(false)

                                    dialog.setTitleText("수정에 실패했습니다")
                                        .setConfirmClickListener {
                                            dialog.dismiss()
                                        }
                                        .show()
                                }
                            } else {
                                val dialog =
                                    SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.ERROR_TYPE)

                                dialog.setCancelable(false)

                                dialog.setTitleText("수정에 실패했습니다")
                                    .setConfirmClickListener {
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<Status>, t: Throwable) {
                            val dialog =
                                SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.ERROR_TYPE)

                            dialog.setCancelable(false)

                            dialog.setTitleText("수정에 실패했습니다")
                                .setConfirmClickListener {
                                    dialog.dismiss()
                                }
                                .show()
                        }

                    })
                } else {
                    RetrofitHelper().getAddAPI().addAnswer(intent.getIntExtra("question_id",0), getID(), write_answer_text.text.toString()).enqueue(object : Callback<Status>{
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            if(response.isSuccessful){
                                if(response.body()!!.status == 200){
                                    val dialog =
                                        SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.SUCCESS_TYPE)

                                    dialog.setCancelable(false)

                                    dialog.setTitleText("작성에 성공했습니다")
                                        .setConfirmClickListener {
                                            val intent = Intent(this@WriteAnswerActivity, ShowQuestionActivity::class.java)
                                            intent.putExtra("writeAnswer", true)
                                            setResult(0, intent)
                                            finish()
                                            dialog.dismiss()
                                        }
                                        .show()

                                }
                            }
                        }

                        override fun onFailure(call: Call<Status>, t: Throwable) {
                            val dialog =
                                SweetAlertDialog(this@WriteAnswerActivity, SweetAlertDialog.ERROR_TYPE)

                            dialog.setCancelable(false)

                            dialog.setTitleText("작섣에 실패했습니다")
                                .setConfirmClickListener {
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    })
                }
            }
        }

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

    private fun setQuestion(id : Int){
        RetrofitHelper().getGetAPI().getQuestion(id = id).enqueue(object : Callback<GetResponse<QuestionResponse>>{
            override fun onResponse(
                call: Call<GetResponse<QuestionResponse>>,
                response: Response<GetResponse<QuestionResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        answer_question_text.text = response.body()!!.result[0].title
                        text_question.text = response.body()!!.result[0].text
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<QuestionResponse>>, t: Throwable) {}

        })
    }

    private fun getID() : Int{
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        return sp.getInt("id", 0)
    }
}