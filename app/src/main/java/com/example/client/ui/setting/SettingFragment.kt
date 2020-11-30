package com.example.client.ui.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.MainActivity
import com.example.client.dialog.ChangeNickName
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.Status
import com.example.client.api.DTO.UserResponse
import com.example.client.api.RetrofitHelper
import com.example.client.dialog.ChangePwd
import com.example.client.start.LoginActivity
import kotlinx.android.synthetic.main.fragment_setting.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        view.btn_change_nickname_setting.setOnClickListener {
            val dialog = context?.let { it1 -> activity?.let { it2 ->
                ChangeNickName(getID(), it1, view,
                    it2
                )
            } }
            dialog?.show()
        }

        view.btn_change_pwd.setOnClickListener {
            val dialog = context?.let { it1 -> ChangePwd(getID(), it1) }
            dialog?.show()
        }

        view.btn_logout.setOnClickListener {
            deleteUser()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }

        view.delete_user.setOnClickListener {
            val dialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)

            dialog.setCancelable(true)
            dialog.progressHelper.barColor = Color.parseColor("#000064")
            dialog.setTitleText("정말 탈퇴 하실 건가요?")
                .setConfirmClickListener {
                    dialog.dismiss()
                    val sweetAlertDialog =
                        SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    sweetAlertDialog.progressHelper.barColor = Color.parseColor("#0DE930")
                    sweetAlertDialog
                        .setTitleText("삭제 중")
                        .setCancelable(false)
                    sweetAlertDialog.show()
                    RetrofitHelper().getGetAPI().getUser(id = getID()).enqueue(object : Callback<GetResponse<UserResponse>>{
                        override fun onResponse(
                            call: Call<GetResponse<UserResponse>>,
                            response: Response<GetResponse<UserResponse>>
                        ) {
                            if(response.isSuccessful){
                                if(response.body()!!.status == 200){
                                    RetrofitHelper().getDeleteAPI().deleteUser(response.body()!!.result[0].id, response.body()!!.result[0].name, response.body()!!.result[0].email, response.body()!!.result[0].password).enqueue(object : Callback<Status>{
                                        override fun onResponse(
                                            call: Call<Status>,
                                            response: Response<Status>
                                        ) {
                                            if(response.isSuccessful){
                                                if(response.body()!!.status == 200) {
                                                    dialog.dismiss()
                                                    val doneDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)

                                                    doneDialog.setCancelable(false)

                                                    doneDialog.setTitleText("삭제가 완료 되었습니다")
                                                        .setConfirmClickListener {
                                                            doneDialog.dismiss()
                                                            val intent =
                                                                Intent(context, LoginActivity::class.java)
                                                            deleteUser()
                                                            startActivity(intent)
                                                            activity?.finish()
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
                                } else {
                                    showFailDialog(sweetAlertDialog)
                                }
                            } else {
                                showFailDialog(sweetAlertDialog)
                            }
                        }

                        override fun onFailure(
                            call: Call<GetResponse<UserResponse>>,
                            t: Throwable
                        ) {
                            showFailDialog(sweetAlertDialog)
                        }

                    })

                }

                .setCancelClickListener {
                    dialog.dismiss()
                }
                .show()
        }

        return view
    }

    fun showFailDialog(sweetAlertDialog: SweetAlertDialog){
        sweetAlertDialog.dismiss()

        val dialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)

        dialog.setCancelable(false)

        dialog.setTitleText("회원탈퇴에 실패하였습니다")
            .setConfirmClickListener {
                dialog.dismiss()
            }
            .show()
    }

    private fun getID() : Int{
        val sp = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return sp!!.getInt("id", 0)
    }

    @SuppressLint("CommitPrefEdits")
    private fun deleteUser(){
        val sp = activity?.getSharedPreferences("user", Activity.MODE_PRIVATE)
        val editor = sp?.edit()
        editor!!.clear()
        editor.apply()
    }

}