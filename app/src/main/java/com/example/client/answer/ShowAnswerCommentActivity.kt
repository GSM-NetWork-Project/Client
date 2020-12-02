package com.example.client.answer

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.example.client.R
import com.example.client.api.DTO.AnswerComment
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.Status
import com.example.client.api.RetrofitHelper
import kotlinx.android.synthetic.main.activity_show_answer_comment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowAnswerCommentActivity : AppCompatActivity() {
    var id = 0
    var arrayList = ArrayList<AnswerComment>()
    var text = ""
    lateinit var adapter : AnswerCommentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_answer_comment)
        id = intent.getIntExtra("answer_id",0)

        text = intent.getStringExtra("text")!!

        setList()

        comment_list.setOnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, l: Long ->
            Log.d("TAG", arrayList[position].toString())
            if(arrayList[position].owner_id == getID()){
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("어떤 작업을 할건가요?")
                dialog.setPositiveButton("삭제") { p0, p1 ->
                    RetrofitHelper().getDeleteAPI().deleteAnswerComment(arrayList[position].id,id,getID()).enqueue(object : Callback<Status>{
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            if(response.isSuccessful){
                                if(response.body()!!.status == 200){
                                    arrayList.remove(arrayList[position])
                                    setList()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Status>, t: Throwable) {

                        }

                    })
                }
                dialog.setNeutralButton("수정") { dialogInterface: DialogInterface, i: Int ->
                    val intent = Intent(this@ShowAnswerCommentActivity, AnswerCommentActivity::class.java)
                    intent.putExtra("text",text)
                    intent.putExtra("isModify",true)
                    intent.putExtra("comment", arrayList[position].text)
                    intent.putExtra("answer_id",arrayList[position].answer_id)
                    startActivityForResult(intent,0)
                }
                dialog.setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->

                }
                dialog.show()
            }

        }
    }

    private fun setList(){
        RetrofitHelper().getGetAPI().getAnswerComment(answer_id = id).enqueue(object : Callback<GetResponse<AnswerComment>>{
            override fun onResponse(
                call: Call<GetResponse<AnswerComment>>,
                response: Response<GetResponse<AnswerComment>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        arrayList = response.body()!!.result
                        adapter = AnswerCommentAdapter(this@ShowAnswerCommentActivity, arrayList)
                        comment_list.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<AnswerComment>>, t: Throwable) {}

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.hasExtra("isModify")){
                setList()
            }
        }
    }

    private fun getID() : Int{
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        return sp.getInt("id", 0)
    }
}