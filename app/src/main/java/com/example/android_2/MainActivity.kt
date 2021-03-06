package com.example.android_2

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_2.Models.NewsApiResponse
import com.example.android_2.Models.NewsHealines



class MainActivity : AppCompatActivity(), SelectListener, View.OnClickListener {

    var mRecyclerView: RecyclerView? = null
    var mAdapter: CustomAdapter? = null
    var mLayoutManager : RecyclerView.LayoutManager? = null
    var dialog: ProgressDialog? = null

    var btn_technology: Button? = null
    var btn_science: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dialog = ProgressDialog(this,R.style.AppCompatAlertDialogStyle)


        dialogSettings()
        dialog!!.setMessage("로딩 중입니다.\n"+"잠시만 기다려주십시오.")
        dialog!!.show()

        btn_technology = findViewById(R.id.btn_technology)
        btn_technology?.setOnClickListener(this)
        btn_science = findViewById(R.id.btn_science)
        btn_science?.setOnClickListener(this)

        val manager = RequestManager(this)
        manager.getNewsHeadlines(listener, "technology", null) //여기서 매개값으로 전달하는 내용에 대한 API 호출을 진행하여 응답을 얻는다
    }

    private val listener: OnFetchDataListener<NewsApiResponse?> = object : OnFetchDataListener<NewsApiResponse?> {
        override fun onFetchData(list: List<NewsHealines?>?, message: String?) { // 리사이클러뷰에 뉴스 항목을 보여주기 위한 메소드이다.

            if(list?.isEmpty()!!){ // 응답으로 전달되는 기사가 없을 때
                Toast.makeText(this@MainActivity, "제공되는 news가 없습니다.", Toast.LENGTH_LONG).show()
            }
            else{
                showNews(list as List<NewsHealines>) //리사이클러뷰에 뉴스를 표시한 후에 dialog 종료
                dialog?.dismiss()
            }

        }

        override fun onError(message: String?) { // 에러를 다루는 메소드 --> 에러가 발생했다는 것을 토스트메세지로 알려준다
            Toast.makeText(this@MainActivity, "에러 발생", Toast.LENGTH_LONG).show()}
    }

    private fun showNews(list: List<NewsHealines>) { // 리사이클러뷰를 초기화하는 역할
        mRecyclerView = findViewById(R.id.recycler_main)

        mLayoutManager = GridLayoutManager(this,1)
        mRecyclerView!!.layoutManager = mLayoutManager
        mAdapter = CustomAdapter(this, list as List<NewsHealines>,this)
        mRecyclerView!!.adapter=mAdapter
        mRecyclerView!!.setHasFixedSize(true)

    }


    override fun OnNewsClicked(healines: NewsHealines?) { //특정 뉴스 항목에 대한 카드뷰를 눌렀을 때 동작하는 메소드이다. 해당 뉴스에 대한 헤드라인 객체를 DetailsActivity에 전달한다.
        val intent = Intent(this@MainActivity, Dictionary::class.java)
        intent.putExtra("data", healines)
        startActivity(intent)
    }

    override fun onClick(view: View) { // 버튼을 눌렀을 때 각 카테고리에 대한 뉴스 API를 호출할 수 있도록 하는 메소드
        val button = view as Button
        val category = button.text.toString()

        dialogSettings()
        dialog!!.setMessage(category + " 분야를 로딩 중입니다.")
        dialog!!.show()
        val manager = RequestManager(this)
        manager.getNewsHeadlines(listener, category, null) //여기서 뉴스 기사에 대한 응답을 얻는다
    }

    fun dialogSettings(){ //progressDialog를 띄우기위한 기본설정을 정한 메소드
        dialog!!.setTitle(R.string.loading)
        dialog!!.setCancelable(false)
    }
}