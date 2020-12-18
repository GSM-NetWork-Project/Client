package com.example.client.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

class ChangeNickName(val id : Int, context: Context, val view : View, val activity: Activity) : Dialog(context) {

    private val mContext = context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_text)

        title_dialog.text = "닉네임 변경"

        btn_change.setOnClickListener {
            val sweetAlertDialog =
                SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
            sweetAlertDialog.progressHelper.barColor = Color.parseColor("#0DE930")
            sweetAlertDialog
                .setTitleText("닉네임 변경 중")
                .setCancelable(false)
            sweetAlertDialog.show()
            RetrofitHelper().getCheckAPI().checkSwearing(editText.text.toString()).enqueue(object : Callback<GetResponse<String>>{
                override fun onResponse(
                    call: Call<GetResponse<String>>,
                    response: Response<GetResponse<String>>
                ) {
                    if(response.isSuccessful){
                        if(response.body()!!.status == 200){
                            sweetAlertDialog.dismiss()
                            Toast.makeText(mContext, "욕설은 금지입니다", Toast.LENGTH_LONG).show()
                        } else {
                            RetrofitHelper().getGetAPI().getUser(name = editText.text.toString()).enqueue(object : Callback<GetResponse<UserResponse>>{
                                override fun onResponse(
                                    call: Call<GetResponse<UserResponse>>,
                                    response: Response<GetResponse<UserResponse>>
                                ) {
                                    if(response.isSuccessful){
                                        if(response.body()!!.status == 500){
                                            RetrofitHelper().getModifyAPI().modifyUser(id = id, name = editText.text.toString()).enqueue(object : Callback<Status>{
                                                override fun onResponse(
                                                    call: Call<Status>,
                                                    response: Response<Status>
                                                ) {
                                                    if(response.isSuccessful){
                                                        if(response.body()!!.status == 200){
                                                            sweetAlertDialog.dismiss()

                                                            val dialog = SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)

                                                            dialog.setCancelable(false)

                                                            dialog.setTitleText("닉네임 변경이 완료 되었습니다")
                                                                .setConfirmClickListener {
                                                                    if(view.myNickname != null) {
                                                                        view.myNickname.text =
                                                                            editText.text.toString()
                                                                    }
                                                                    setUser()

                                                                    dialog.dismiss()
                                                                    dismiss()
                                                                }
                                                                .show()
                                                        }
                                                    }
                                                }
                                                override fun onFailure(call: Call<Status>, t: Throwable) {
                                                    showFailDialog(sweetAlertDialog)
                                                }
                                            })
                                        } else {
                                            showFailDialog(sweetAlertDialog)
                                        }
                                    }
                                }
                                override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {
                                    showFailDialog(sweetAlertDialog)
                                }
                            })
                        }
                    }
                }

                override fun onFailure(call: Call<GetResponse<String>>, t: Throwable) {}

            })

        }

        btn_cancel_change.setOnClickListener {
            this.dismiss()
        }
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

    fun setUser(){
        RetrofitHelper().getGetAPI().getUser(id = id).enqueue(object : Callback<GetResponse<UserResponse>>{
            override fun onResponse(
                call: Call<GetResponse<UserResponse>>,
                response: Response<GetResponse<UserResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        val userResponse = response.body()!!.result[0]
                        val navView: NavigationView = activity.findViewById(R.id.nav_view)
                        val header = navView.getHeaderView(0)

                        header.sideEmailText.text = userResponse.email
                        header.sideNickNameText.text = userResponse.name
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
}