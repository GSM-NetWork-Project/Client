package com.example.client.question

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.client.MainActivity
import com.example.client.R
import com.example.client.ShowCommentActivity
import com.example.client.answer.AnswerAdapter
import com.example.client.answer.WriteAnswerActivity
import com.example.client.api.DTO.*
import com.example.client.api.RetrofitHelper
import com.example.client.start.LoginActivity
import kotlinx.android.synthetic.main.activity_show_question.*
import kotlinx.android.synthetic.main.activity_write_answer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowQuestionActivity : AppCompatActivity() {

    var vote = 0
    var isVote = false
    var myVoteType = 0

    var title = ""
    var text = ""
    var ownerId = 0
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_question)

        Log.d("TEST", intent.getIntExtra("question_id",0).toString())

        id = intent.getIntExtra("question_id",0)
        title = intent.getStringExtra("title").toString()
        text = intent.getStringExtra("text").toString()
        ownerId = intent.getIntExtra("owner_id",0)

        setQuestion()
        setList()

        if(getID() != ownerId){
            btn_modify_question.visibility = View.GONE
            btn_delete_question.visibility = View.GONE
            write_answer.setOnClickListener {
                val intent = Intent(this@ShowQuestionActivity, WriteAnswerActivity::class.java)
                intent.putExtra("title", title_question.text.toString())
                intent.putExtra("text", question_text.text.toString())
                intent.putExtra("question_id", id)
                startActivityForResult(intent, 0)
            }
        } else {
            write_answer.visibility = View.GONE
            btn_modify_question.setOnClickListener {
                val intent = Intent(this@ShowQuestionActivity, WriteQuestionActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("title", title_question.text.toString())
                intent.putExtra("text", question_text.text.toString())
                intent.putExtra("owner_id", getID())
                intent.putExtra("isModify", true)
                startActivityForResult(intent, 0)
            }
            btn_delete_question.setOnClickListener {

                RetrofitHelper().getDeleteAPI().deleteQuestion(intent.getIntExtra("question_id", 0), getID(), title_question.text.toString(),question_text.text.toString(), intent.getIntExtra("is_solved",0)).enqueue(object : Callback<Status>{
                    override fun onResponse(call: Call<Status>, response: Response<Status>) {
                        if(response.isSuccessful){
                            if(response.body()!!.status == 200){
                                val doneDialog = SweetAlertDialog(this@ShowQuestionActivity, SweetAlertDialog.SUCCESS_TYPE)

                                doneDialog.setCancelable(false)

                                doneDialog.setTitleText("삭제가 완료 되었습니다")
                                    .setConfirmClickListener {
                                        doneDialog.dismiss()
                                        val intent =
                                            Intent(this@ShowQuestionActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .show()
                            } else {
                                showFailDialog()

                                Log.d("TEST", "TEST")
                            }
                        } else {
                            showFailDialog()
                        }
                    }

                    override fun onFailure(call: Call<Status>, t: Throwable) {
                        Log.d("TEST", t.toString())
                        showFailDialog()
                    }

                })

            }
        }


        up_vote_question.setOnClickListener {
            Log.d("TEST", isVote.toString())
            if(isVote){
                if(myVoteType == 1){

                    Log.d("TEST", isVote.toString())
                    deleteVote(intent.getIntExtra("question_id",0), 1)
                } else{
                    Log.d("TEST", isVote.toString())
                    modifyVote(intent.getIntExtra("question_id",0), 1)
                }
            } else {
                Log.d("TEST", isVote.toString())
                addVote(intent.getIntExtra("question_id",0), 1)
            }
        }

        down_vote_question.setOnClickListener {
            if(isVote){
                if(myVoteType == -1){
                    deleteVote(intent.getIntExtra("question_id",0), -1)
                } else {
                    modifyVote(intent.getIntExtra("question_id",0), -1)
                }
            } else {
                addVote(intent.getIntExtra("question_id",0), -1)
            }
        }

        btnWriteComment.setOnClickListener {
            val intent = Intent(this@ShowQuestionActivity, QuestionCommentActivity::class.java)
            intent.putExtra("owner_id", getID())
            intent.putExtra("question_id", id)
            startActivity(intent)
        }

        btnShowComment.setOnClickListener {
            val intent = Intent(this@ShowQuestionActivity, ShowCommentActivity::class.java)
            intent.putExtra("owner_id", getID())
            intent.putExtra("question_id", id)
            startActivity(intent)
        }

        setVote()




    }

    fun showFailDialog(){

        val dialog = SweetAlertDialog(this@ShowQuestionActivity, SweetAlertDialog.ERROR_TYPE)

        dialog.setCancelable(false)

        dialog.setTitleText("삭제에 실패하였습니다")
            .setConfirmClickListener {
                dialog.dismiss()
            }
            .show()
    }

    private fun addVote(question_id : Int, type : Int){
        RetrofitHelper().getAddAPI().addQuestionVote(question_id = question_id, owner_id = getID(), type = type).enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        vote += type
                        vote_count.text = vote.toString()
                        isVote = true
                        myVoteType = type
                    } else {

                        Log.d("TET", response.body()!!.status.toString())
                    }
                } else {

                    Log.d("TEST", response.code().toString())
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {
                Log.d("TEST", t.toString())
            }

        })
    }

    private fun modifyVote(question_id : Int, type : Int){
        RetrofitHelper().getModifyAPI().modifyQuestionVote(question_id = question_id, owner_id = getID(), type = type).enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        vote += (type*2)
                        vote_count.text = vote.toString()
                        isVote = true
                        myVoteType = type
                    }
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {}

        })
    }

    override fun onResume() {
        super.onResume()
        setList()
    }

    private fun setList(){
        var arrayList = ArrayList<AnswerResponse>()
        RetrofitHelper().getGetAPI().getAnswer(question_id = intent.getIntExtra("question_id",0)).enqueue(object : Callback<GetResponse<AnswerResponse>>{
            override fun onResponse(
                call: Call<GetResponse<AnswerResponse>>,
                response: Response<GetResponse<AnswerResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        arrayList = response.body()!!.result
                        val adapter = AnswerAdapter(this@ShowQuestionActivity, arrayList, getID())
                        list_answer.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<AnswerResponse>>, t: Throwable) {
                Toast.makeText(this@ShowQuestionActivity, "로딩중 오류 발생", Toast.LENGTH_LONG).show()
                finish()
            }

        })

    }

    private fun deleteVote(question_id : Int, type : Int){
        RetrofitHelper().getDeleteAPI().deleteQuestionVote(question_id = question_id, owner_id = getID()).enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        vote -= type
                        vote_count.text = vote.toString()
                        isVote = false
                        myVoteType = 0
                    }
                }
            }

            override fun onFailure(call: Call<Status>, t: Throwable) {}

        })
    }

    private fun getID() : Int{
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        return sp.getInt("id", 0)
    }

    private fun setQuestion(){
        title_question.text = title
        question_text.text = text
        RetrofitHelper().getGetAPI().getUser(id = ownerId).enqueue(object : Callback<GetResponse<UserResponse>>{
            override fun onResponse(
                call: Call<GetResponse<UserResponse>>,
                response: Response<GetResponse<UserResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        owner_text.text = response.body()!!.result[0].name
                    } else{
                        Toast.makeText(this@ShowQuestionActivity, "없는 사용자의 게시글 입니다", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<UserResponse>>, t: Throwable) {
                finish()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.hasExtra("title") && data.hasExtra("text")){
                title = data.getStringExtra("title").toString()
                text = data.getStringExtra("text").toString()
                setQuestion()
            } else if(data.hasExtra("writeAnswer")) {
                setList()
            }

        }
    }

    private fun setVote(){
        RetrofitHelper().getGetAPI().getQuestionVote(question_id = intent.getIntExtra("question_id",0)).enqueue(object : Callback<GetResponse<QuestionVote>>{
            override fun onResponse(
                call: Call<GetResponse<QuestionVote>>,
                response: Response<GetResponse<QuestionVote>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        for(v in response.body()!!.result){
                            vote += v.type
                            if(v.owner_id == getID()){
                                isVote = true
                                myVoteType = v.type
                            }
                        }
                        vote_count.text = vote.toString()
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<QuestionVote>>, t: Throwable) {
                Log.d("TEST", t.toString())
            }

        })
    }
}
