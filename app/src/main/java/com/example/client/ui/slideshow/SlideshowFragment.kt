package com.example.client.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.client.R
import com.example.client.api.DTO.GetResponse
import com.example.client.api.DTO.QuestionResponse
import com.example.client.api.RetrofitHelper
import com.example.client.question.QuestionAdapter
import com.example.client.question.ShowQuestionActivity
import com.example.client.question.WriteQuestionActivity
import kotlinx.android.synthetic.main.fragment_slideshow.*
import kotlinx.android.synthetic.main.fragment_slideshow.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlideshowFragment : Fragment() {

    var arrayList = ArrayList<QuestionResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        val items = resources.getStringArray(R.array.my_array)

        setHasOptionsMenu(true)

        root.themeSpinner.adapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_dropdown_item, items) }

        root.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setList(items[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                setList("상식")
            }
        }

        root.askList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, long ->
                val intent = Intent(context, ShowQuestionActivity::class.java)
                intent.putExtra("question_id", arrayList[position].id)
                intent.putExtra("owner_id", arrayList[position].owner_id)
                intent.putExtra("title", arrayList[position].title)
                intent.putExtra("theme", arrayList[position].theme)
                intent.putExtra("text", arrayList[position].text)
                intent.putExtra("is_solved", arrayList[position].is_solved)
                intent.putExtra("upload_time",arrayList[position].upload_time)
                startActivity(intent)
            }

        root.write_question.setOnClickListener {
            startActivity(Intent(activity, WriteQuestionActivity::class.java))
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        setList("상식")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val search = menu.findItem(R.id.action_search)
        val searchView : SearchView = search.actionView as SearchView
        searchView.queryHint = "제목 검색"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(s: String?): Boolean {
                if (s != null) {
                    if(s.isNotEmpty()) {
                        setListWithTitle(s)
                    }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
    }

    fun setList(theme : String){
        RetrofitHelper().getGetAPI().getQuestion(theme = theme).enqueue(object : Callback<GetResponse<QuestionResponse>>{
            override fun onResponse(
                call: Call<GetResponse<QuestionResponse>>,
                response: Response<GetResponse<QuestionResponse>>
            ) {
                if(response.isSuccessful) {
                    if (response.body()!!.status == 200) {


                        arrayList = response.body()!!.result
                        askList.adapter =
                            context?.let { QuestionAdapter(it, response.body()!!.result) }
                    } else {
                        askList.adapter = null
                    }
                }
            }

            override fun onFailure(call: Call<GetResponse<QuestionResponse>>, t: Throwable) {
                Log.d("Test", t.toString())
            }

        })
    }
    fun setListWithTitle(title : String){
        RetrofitHelper().getGetAPI().getQuestion(title = title).enqueue(object : Callback<GetResponse<QuestionResponse>>{
            override fun onResponse(
                call: Call<GetResponse<QuestionResponse>>,
                response: Response<GetResponse<QuestionResponse>>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200) {
                        arrayList = response.body()!!.result
                        askList.adapter =
                            context?.let { QuestionAdapter(it, response.body()!!.result) }
                    } else {
                        askList.adapter = null
                    }
                }
                else {
                    askList.adapter = null
                }
            }

            override fun onFailure(call: Call<GetResponse<QuestionResponse>>, t: Throwable) {
                Log.d("Test", t.toString())
            }

        })
    }


}