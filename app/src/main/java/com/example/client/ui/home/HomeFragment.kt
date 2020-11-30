package com.example.client.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.client.dialog.ChangeNickName
import com.example.client.R
import com.example.client.ShowMyActivity
import com.example.client.question.ShowQuestionActivity
import com.example.client.start.LoginActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.myNickname.text = getNickName()!!

        root.editNickname.setOnClickListener {
            val dialog = context?.let { it1 -> activity?.let { it2 ->
                ChangeNickName(getID(), it1, root,
                    it2
                )
            } }
            dialog?.show()
        }
        root.logout.setOnClickListener {
            deleteUser()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()

        }

        root.go_show_my_question.setOnClickListener {
            val intent = Intent(context, ShowMyActivity::class.java)
            intent.putExtra("type", "내 질문 보기")
        }

        root.go_show_my_answer.setOnClickListener {
            val intent = Intent(context, ShowMyActivity::class.java)
            intent.putExtra("type", "내 답변 보기")
        }

        root.go_show_my_comment.setOnClickListener {
            val intent = Intent(context, ShowMyActivity::class.java)
            intent.putExtra("type", "내 댓글 보기")
        }

        return root
    }

    private fun getNickName() : String?{
        val sp = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return sp!!.getString("name", "")
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