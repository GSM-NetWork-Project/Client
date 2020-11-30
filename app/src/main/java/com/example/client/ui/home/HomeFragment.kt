package com.example.client.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.client.R
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.myNickname.text = getNickName()!!

        return root
    }
    private fun getNickName() : String?{
        val sp = activity?.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        return sp!!.getString("name", "")
    }
}