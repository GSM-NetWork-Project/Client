package com.example.client.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.Status
import com.example.client.api.DTO.UserResponse
import com.example.client.api.RetrofitHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.dialog_edit_text.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePwd(val id : Int, context: Context) : Dialog(context) {
    val mContext = context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_text)

        title_dialog.text = "비밀번호 변경"

        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        editText.setRawInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        editText.transformationMethod = PasswordTransformationMethod.getInstance()

        btn_change.setOnClickListener {
            val sweetAlertDialog =
                SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
            sweetAlertDialog.progressHelper.barColor = Color.parseColor("#0DE930")
            sweetAlertDialog
                .setTitleText("비밀번호 변경 중")
                .setCancelable(false)
            sweetAlertDialog.show()
            RetrofitHelper().getModifyAPI().modifyUser(id = id, password = editText.text.toString()).enqueue(object : Callback<Status>{
                override fun onResponse(call: Call<Status>, response: Response<Status>) {
                    if(response.isSuccessful){
                        if(response.body()!!.status == 200){
                            sweetAlertDialog.dismiss()
                            val dialog = SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)

                            dialog.setCancelable(false)

                            dialog.setTitleText("비밀번호 변경이 완료 되었습니다")
                                .setConfirmClickListener {
                                    setUser()
                                    dialog.dismiss()
                                    dismiss()
                                }
                                .show()
                        } else {
                            showFailDialog(sweetAlertDialog)
                        }
                    } else {
                        showFailDialog(sweetAlertDialog)
                    }
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    showFailDialog(sweetAlertDialog)
                }

            })
        }

        btn_cancel_change.setOnClickListener {
            dismiss()
        }
    }

    fun setUser(){
        RetrofitHelper().getGetAPI().getUser(id = id).enqueue(object :
            Callback<GetResponse<UserResponse>> {
            override fun onResponse(
                call: Call<GetResponse<UserResponse>>,
                response: Response<GetResponse<UserResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        val userResponse = response.body()!!.result[0]
                        val pref = mContext.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putInt("id", userResponse.id)
                        editor.putString("name", userResponse.name)
                        editor.putString("email", userResponse.email)
                        editor.putString("pwd", userResponse.password)
                        editor.apply()
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {}

        })

    }

    fun showFailDialog(sweetAlertDialog: SweetAlertDialog){
        sweetAlertDialog.dismiss()

        val dialog = SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)

        dialog.setCancelable(false)

        dialog.setTitleText("닉네임 변경에 실패하였습니다")
            .setConfirmClickListener {
                dialog.dismiss()
            }
            .show()
    }
}