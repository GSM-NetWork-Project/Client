package com.example.client.answer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.MainActivity
import com.example.client.R
import com.example.client.ShowCommentActivity
import com.example.client.api.DTO.*
import com.example.client.api.RetrofitHelper
import kotlinx.android.synthetic.main.activity_show_question.*
import kotlinx.android.synthetic.main.item_answer.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnswerAdapter(val context : Context, private val arrayList: ArrayList<AnswerResponse>, private val userID : Int) : BaseAdapter() {

    var positionVote = 0

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(p0: Int): Any{
        return arrayList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, converterView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_answer, parent,false)

        view.answer_text.text = arrayList[position].text
        view.owner_answer_id.text = getUserName()

        if(arrayList[position].owner_id != userID){
            view.btn_edit_answer.visibility = View.GONE
            view.btn_delete_answer.visibility = View.GONE
        } else {
            view.btn_edit_answer.setOnClickListener {
                val intent = Intent(context, WriteAnswerActivity::class.java)
                intent.putExtra("is_edit_answer", true)
                intent.putExtra("question_id", arrayList[position].question_id)
                intent.putExtra("edit_answer", view.answer_text.text.toString())
                context.startActivity(intent)
            }
            view.btn_delete_answer.setOnClickListener {
                RetrofitHelper().getDeleteAPI().deleteAnswer(arrayList[position].id, arrayList[position].question_id, userID).enqueue(object : Callback<Status>{
                    override fun onResponse(call: Call<Status>, response: Response<Status>) {
                        if(response.isSuccessful){
                            if(response.body()!!.status == 200){
                                val doneDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)

                                doneDialog.setCancelable(false)

                                doneDialog.setTitleText("삭제가 완료 되었습니다")
                                    .setConfirmClickListener {
                                        doneDialog.dismiss()
                                    }
                                    .show()
                                arrayList.remove(arrayList[position])
                                notifyDataSetChanged()
                            } else {
                                Log.d("TEST", 1.toString())
                                showFailDialog()
                            }
                        } else {
                            Log.d("TEST", 2.toString())
                            showFailDialog()
                        }
                    }

                    override fun onFailure(call: Call<Status>, t: Throwable) {
                        Log.d("TEST",t.toString())
                        showFailDialog()
                    }

                })
            }
        }



        var isVote = setVote(position, view)

        view.up_vote_answer.setOnClickListener {
            isVote = if(isVote.isVote){
                if(isVote.type == 1){
                    deleteVote(arrayList[position].id, 1, view)
                    IsVote(false, 0)
                } else {
                    modifyVote(arrayList[position].id, 1, view)
                    IsVote(true, 1)
                }
            } else {
                addVote(arrayList[position].id, 1, view)
                IsVote(true, 1)
            }
        }

        view.down_vote_answer.setOnClickListener {
            isVote = if(isVote.isVote){
                if(isVote.type == 1){
                    modifyVote(arrayList[position].id, -1, view)
                    IsVote(true, -1)
                } else {
                    deleteVote(arrayList[position].id, -1, view)
                    IsVote(false, 0)
                }
            } else {
                addVote(arrayList[position].id, -1, view)
                IsVote(true, -1)
            }
        }

        RetrofitHelper().getGetAPI().getUser(id = arrayList[position].owner_id).enqueue(object :
            Callback<GetResponse<UserResponse>> {
            override fun onResponse(
                call: Call<GetResponse<UserResponse>>,
                response: Response<GetResponse<UserResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        view.owner_answer_id.text = response.body()!!.result[0].name
                    } else{
                        Toast.makeText(context, "없는 사용자의 게시글 입니다", Toast.LENGTH_LONG).show()
                        view.owner_answer_id.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {}

        })

        view.btnShowComment_answer.setOnClickListener {
            val intent = Intent(context, ShowCommentActivity::class.java)
            intent.putExtra("answer_id", arrayList[position].id)
            context.startActivity(intent)
        }

        view.btnWriteComment_answer.setOnClickListener {
            val intent = Intent(context, AnswerCommentActivity::class.java)
            intent.putExtra("answer_id", arrayList[position].id)
            intent.putExtra("owner_id", userID)
            intent.putExtra("text", arrayList[position].text)
            context.startActivity(intent)
        }


        return view
    }

    fun showFailDialog(){

        val dialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)

        dialog.setCancelable(false)

        dialog.setTitleText("삭제에 실패하였습니다")
            .setConfirmClickListener {
                dialog.dismiss()
            }
            .show()
    }


    private fun getUserName() : String{
        var name : String = ""
        RetrofitHelper().getGetAPI().getUser(id = userID).enqueue(object : Callback<GetResponse<UserResponse>>{
            override fun onResponse(
                call: Call<GetResponse<UserResponse>>,
                response: Response<GetResponse<UserResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        name = response.body()!!.result[0].name
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {}

        })
        return name
    }

    private fun addVote(answer_id : Int, type : Int, view : View){

        Log.d("VOTE", positionVote.toString())
        RetrofitHelper().getAddAPI().addAnswerVote(answer_id = answer_id, owner_id = userID, type = type).enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        positionVote += type
                        view.vote_count_answer.text = positionVote.toString()
                    }
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {}

        })
    }

    private fun modifyVote(answer_id: Int, type : Int, view : View){
        RetrofitHelper().getModifyAPI().modifyAnswerVote(answer_id = answer_id, owner_id = userID, type = type).enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        positionVote += (type*2)
                        view.vote_count_answer.text = positionVote.toString()
                    }
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {}

        })
    }

    private fun deleteVote(answer_id : Int, type : Int, view : View){
        Log.d("TEST", positionVote.toString())
        RetrofitHelper().getDeleteAPI().deleteAnswerVote(answer_id = answer_id, owner_id = userID).enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        positionVote -= type
                        view.vote_count_answer.text = positionVote.toString()
                    }
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {}

        })
    }

    private fun setVote(position: Int, view : View) : IsVote{
        var vote = 0
        var isVote = IsVote(false, 0)
        RetrofitHelper().getGetAPI().getAnswerVote(arrayList[position].id).enqueue(object : Callback<GetResponse<AnswerVote>>{
            override fun onResponse(
                call: Call<GetResponse<AnswerVote>>,
                response: Response<GetResponse<AnswerVote>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        for(v in response.body()!!.result){
                            vote += v.type
                            if(v.owner_id == userID){
                                isVote = IsVote(true, v.type)
                            }
                        }
                        positionVote = vote
                        view.vote_count_answer.text = vote.toString()
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<AnswerVote>>, t: Throwable) {}

        })
        return isVote
    }

}

data class IsVote(
    val isVote: Boolean,
    val type : Int
)