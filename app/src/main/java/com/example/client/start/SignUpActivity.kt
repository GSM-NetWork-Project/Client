package com.example.client.start

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.MainActivity
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.Status
import com.example.client.api.DTO.UserResponse
import com.example.client.api.RetrofitHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.MainScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        var isDuplicateEmail = true
        var isDuplicateNickname = true
        val service = RetrofitHelper()

        checkDuplicateId.setOnClickListener {
            service.getGetAPI().getUser(email = signUpID.text.toString())
                .enqueue(object : Callback<GetResponse<UserResponse>> {
                    override fun onResponse(
                        call: Call<GetResponse<UserResponse>>,
                        response: Response<GetResponse<UserResponse>>
                    ) {
                        if (response.isSuccessful) {
                            isDuplicateEmail = response.body()!!.status == 200
                            signUpIDLayout.error = if (isDuplicateEmail) {
                                "중복된 이메일입니다"
                            } else {
                                null
                            }
                            if (!isDuplicateEmail){
                                Toast.makeText(this@SignUpActivity, "사용 가능한 이메일 입니다", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {
                        Log.d("TAG", t.toString())
                    }

                })
        }

        checkDuplicateNickName.setOnClickListener {
            service.getGetAPI().getUser(name = signUpName.text.toString())
                .enqueue(object : Callback<GetResponse<UserResponse>> {
                    override fun onResponse(
                        call: Call<GetResponse<UserResponse>>,
                        response: Response<GetResponse<UserResponse>>
                    ) {
                        if (response.isSuccessful) {
                            isDuplicateNickname = response.body()!!.status == 200
                            signUpNameLayout.error = if (isDuplicateNickname) {
                                "중복된 닉네임입니다"
                            } else {
                                null
                            }
                            if (!isDuplicateNickname){
                                Toast.makeText(this@SignUpActivity, "사용 가능한 닉네임 입니다", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {
                        Log.d("TAG", t.toString())
                    }
                })
        }

        textWatcher()

        btnAddUser.setOnClickListener {
            val sweetAlertDialog =
                SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.PROGRESS_TYPE)
            sweetAlertDialog.progressHelper.barColor = Color.parseColor("#0DE930")
            sweetAlertDialog
                .setTitleText("회원 가입 중")
                .setCancelable(false)
            sweetAlertDialog.show()
            if (!isDuplicateEmail && !isDuplicateNickname && checkPwdLayout.error == null && checkPwd.text.toString()
                    .isNotEmpty()
            ) {
                if (signUpID.text.toString().contains("@") && signUpID.text.toString()
                        .contains(".")
                ) {
                    RetrofitHelper().getAddAPI().addUser(
                        name = signUpName.text.toString(),
                        email = signUpID.text.toString(),
                        password = checkPwd.text.toString()
                    ).enqueue(object : Callback<Status> {
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            if (response.isSuccessful) {
                                if (response.body()!!.status == 200) {
                                    sweetAlertDialog.dismiss()

                                    val dialog = SweetAlertDialog(
                                        this@SignUpActivity,
                                        SweetAlertDialog.SUCCESS_TYPE
                                    )

                                    dialog.setCancelable(false)

                                    dialog.setTitleText("회원가입이 완료 되었습니다")
                                        .setConfirmClickListener {
                                            dialog.dismiss()
                                            val intent = Intent(
                                                this@SignUpActivity,
                                                LoginActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                        .show()
                                } else {
                                    showFailDialog(sweetAlertDialog, "회원가입 실패")
                                }
                            } else {
                                showFailDialog(sweetAlertDialog, "회원가입 실패")
                            }
                        }

                        override fun onFailure(call: Call<Status>, t: Throwable) {
                            showFailDialog(sweetAlertDialog, "서버 오류")
                        }

                    })

                }
            } else {
                showFailDialog(sweetAlertDialog, "양식 확인 후 다시 시도하세요")
            }
        }

    }

    fun showFailDialog(sweetAlertDialog: SweetAlertDialog, title : String){
        sweetAlertDialog.dismiss()

        val dialog = SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.ERROR_TYPE)

        dialog.setCancelable(false)

        dialog.setTitleText(title)
            .setConfirmClickListener {
                dialog.dismiss()
            }
            .show()
    }

    private fun textWatcher(){
        checkPwd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (checkPwd.text.toString() != signUpPassword.text.toString()) {
                    checkPwdLayout.error = "비밀번호가 같지 않습니다"
                } else {
                    checkPwdLayout.error = null
                }
            }


        })
    }
}