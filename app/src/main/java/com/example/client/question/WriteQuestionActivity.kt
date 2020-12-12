package com.example.client.question

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.R
import com.example.client.api.DTO.CheckQuestion
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.Status
import com.example.client.api.RetrofitHelper
import com.example.client.dialog.SimilarQuestionDialog
import kotlinx.android.synthetic.main.activity_write_question.*
import kotlinx.android.synthetic.main.fragment_slideshow.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WriteQuestionActivity : AppCompatActivity() {

    var arrayList = ArrayList<CheckQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_question)


        var theme = "상식"

        val items = resources.getStringArray(R.array.my_array)

        input_question_text.setText(intent.getStringExtra("text"))
        input_question_title.setText(intent.getStringExtra("title"))

        spinner.adapter = ArrayAdapter(this@WriteQuestionActivity, android.R.layout.simple_spinner_dropdown_item, items)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                theme = items[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                theme = items[0]
            }
        }

        btn_write_question.setOnClickListener {

            if(input_question_text.text.toString().length > 250){
                val dialog = SweetAlertDialog(this@WriteQuestionActivity, SweetAlertDialog.ERROR_TYPE)

                dialog.setCancelable(false)

                dialog.setTitleText("내용이 250자 이하인지 확인해주세요!")
                    .setConfirmClickListener {
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener
            } else if(input_question_title.text.toString().length > 20){
                val dialog = SweetAlertDialog(this@WriteQuestionActivity, SweetAlertDialog.ERROR_TYPE)

                dialog.setCancelable(false)

                dialog.setTitleText("제목이 20자 이하인지 확인해주세요!")
                    .setConfirmClickListener {
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener
            } else if(input_question_title.text.toString().isEmpty() || input_question_text.text.toString().isEmpty()){
                val dialog = SweetAlertDialog(this@WriteQuestionActivity, SweetAlertDialog.ERROR_TYPE)

                dialog.setCancelable(false)

                dialog.setTitleText("빈칸이 없는지 확인해주세요!")
                    .setConfirmClickListener {
                        dialog.dismiss()
                    }
                    .show()
                return@setOnClickListener
            } else {
                if (checkSwearing(input_question_title.text.toString()) || checkSwearing(
                        input_question_text.text.toString()
                    )
                ) {
                    val dialog =
                        SweetAlertDialog(this@WriteQuestionActivity, SweetAlertDialog.ERROR_TYPE)

                    dialog.setCancelable(false)

                    dialog.setTitleText("욕설은 금지입니다!!")
                        .setConfirmClickListener {
                            dialog.dismiss()
                        }
                        .show()
                    return@setOnClickListener
                } else {
                    checkSimilarQuestion(input_question_title.text.toString(), theme)
                }
            }
        }
    }

    private fun showSimilarQuestion(arrayList: ArrayList<CheckQuestion>){
        val similarQuestionDialog = SimilarQuestionDialog(this@WriteQuestionActivity, arrayList)
        similarQuestionDialog.show()
    }

    private fun checkSimilarQuestion(text : String, theme : String){
        RetrofitHelper().getCheckAPI().checkQuestion(text).enqueue(object : Callback<GetResponse<CheckQuestion>>{
            override fun onResponse(
                call: Call<GetResponse<CheckQuestion>>,
                response: Response<GetResponse<CheckQuestion>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        arrayList = response.body()!!.result
                        showSimilarQuestion(arrayList)
                    } else {
                        if (intent.getBooleanExtra("isModify", false)) {

                            RetrofitHelper().getModifyAPI().modifyQuestion(
                                intent.getIntExtra("id", 0),
                                getID(),
                                input_question_title.text.toString(),
                                theme,
                                input_question_text.text.toString()
                            ).enqueue(object : Callback<Status> {
                                override fun onResponse(
                                    call: Call<Status>,
                                    response: Response<Status>
                                ) {
                                    if (response.isSuccessful) {
                                        if (response.body()!!.status == 200) {
                                            val dialog = SweetAlertDialog(
                                                this@WriteQuestionActivity,
                                                SweetAlertDialog.SUCCESS_TYPE
                                            )

                                            dialog.setCancelable(false)

                                            dialog.setTitleText("수정에 성공하였습니다")
                                                .setConfirmClickListener {
                                                    val intent = Intent(
                                                        this@WriteQuestionActivity,
                                                        ShowQuestionActivity::class.java
                                                    )
                                                    intent.putExtra(
                                                        "title",
                                                        input_question_title.text.toString()
                                                    )
                                                    intent.putExtra(
                                                        "text",
                                                        input_question_text.text.toString()
                                                    )
                                                    intent.putExtra("owner_id", getID())
                                                    setResult(0, intent)
                                                    finish()
                                                    dialog.dismiss()
                                                }
                                                .show()
                                        } else {
                                            val dialog = SweetAlertDialog(
                                                this@WriteQuestionActivity,
                                                SweetAlertDialog.ERROR_TYPE
                                            )

                                            dialog.setCancelable(false)

                                            dialog.setTitleText("수정에 실패하였습니다")
                                                .setConfirmClickListener {
                                                    dialog.dismiss()
                                                }
                                                .show()
                                        }
                                    } else {
                                        val dialog = SweetAlertDialog(
                                            this@WriteQuestionActivity,
                                            SweetAlertDialog.ERROR_TYPE
                                        )

                                        dialog.setCancelable(false)

                                        dialog.setTitleText("수정에 실패하였습니다")
                                            .setConfirmClickListener {
                                                dialog.dismiss()
                                            }
                                            .show()
                                    }
                                }

                                override fun onFailure(call: Call<Status>, t: Throwable) {
                                    val dialog = SweetAlertDialog(
                                        this@WriteQuestionActivity,
                                        SweetAlertDialog.ERROR_TYPE
                                    )

                                    dialog.setCancelable(false)

                                    dialog.setTitleText("수정에 실패하였습니다")
                                        .setConfirmClickListener {
                                            dialog.dismiss()
                                        }
                                        .show()
                                }
                            })

                        } else {
                            RetrofitHelper().getAddAPI().addQuestion(
                                getID(),
                                input_question_title.text.toString(),
                                theme,
                                input_question_text.text.toString()
                            ).enqueue(object : Callback<Status> {
                                override fun onResponse(
                                    call: Call<Status>,
                                    response: Response<Status>
                                ) {
                                    if (response.isSuccessful) {
                                        if (response.body()!!.status == 200) {
                                            val dialog = SweetAlertDialog(
                                                this@WriteQuestionActivity,
                                                SweetAlertDialog.SUCCESS_TYPE
                                            )

                                            dialog.setCancelable(false)

                                            dialog.setTitleText("작성에 성공하였습니다")
                                                .setConfirmClickListener {
                                                    finish()
                                                    dialog.dismiss()
                                                }
                                                .show()
                                        } else {
                                            val dialog = SweetAlertDialog(
                                                this@WriteQuestionActivity,
                                                SweetAlertDialog.ERROR_TYPE
                                            )

                                            dialog.setCancelable(false)

                                            dialog.setTitleText("작성에 실패하였습니다")
                                                .setConfirmClickListener {
                                                    dialog.dismiss()
                                                }
                                                .show()
                                        }
                                    } else {
                                        val dialog = SweetAlertDialog(
                                            this@WriteQuestionActivity,
                                            SweetAlertDialog.ERROR_TYPE
                                        )

                                        dialog.setCancelable(false)

                                        dialog.setTitleText("작성에 실패하였습니다")
                                            .setConfirmClickListener {
                                                dialog.dismiss()
                                            }
                                            .show()
                                    }
                                }

                                override fun onFailure(call: Call<Status>, t: Throwable) {
                                    val dialog = SweetAlertDialog(
                                        this@WriteQuestionActivity,
                                        SweetAlertDialog.ERROR_TYPE
                                    )

                                    dialog.setCancelable(false)

                                    dialog.setTitleText("작성에 실패하였습니다")
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

            override fun onFailure(call: Call<GetResponse<CheckQuestion>>, t: Throwable) {}

        })
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
}