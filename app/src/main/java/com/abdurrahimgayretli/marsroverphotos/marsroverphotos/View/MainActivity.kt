package com.abdurrahimgayretli.marsroverphotos.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.abdurrahimgayretli.marsroverphotos.Adapter.MarsAdapter
import com.abdurrahimgayretli.marsroverphotos.Model.Data
import com.abdurrahimgayretli.marsroverphotos.Model.Photo
import com.abdurrahimgayretli.marsroverphotos.R
import com.abdurrahimgayretli.marsroverphotos.Utilis.RetrofitClient
import com.abdurrahimgayretli.marsroverphotos.ViewModel.MarsViewModel
import com.abdurrahimgayretli.marsroverphotos.marsroverphotos.Adapter.MyViewPagerAdapter
import com.abdurrahimgayretli.marsroverphotos.marsroverphotos.View.Curiosity
import com.abdurrahimgayretli.marsroverphotos.marsroverphotos.View.Opportunity
import com.abdurrahimgayretli.marsroverphotos.marsroverphotos.View.Spirit
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.photo_cell.view.*
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity(),MarsAdapter.onItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var marsViewModel : MarsViewModel
    lateinit var marsAdapter : MarsAdapter

    var tabPosition=0
    var tabName ="Curiosity"

    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage = 1
    private var isLoading = false

    private var filterCamera = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        marsViewModel = ViewModelProviders.of(this).get(MarsViewModel::class.java)

        viewPager()

        layoutManager = LinearLayoutManager(this)
        swipeRefresh.setOnRefreshListener(this@MainActivity)
        setupRecyclerView()
        getData(false)

        recyclerView.viewTreeObserver.addOnScrollChangedListener(object:ViewTreeObserver.OnScrollChangedListener {
            override fun onScrollChanged() {
                if(layoutManager.findFirstVisibleItemPosition() == 0)
                    swipeRefresh.isEnabled = true
                else
                    swipeRefresh.isEnabled = false
            }

        })
        recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total = marsAdapter.itemCount
                if(!isLoading && page<totalPage){
                    if(visibleItemCount+pastVisibleItem >= total){
                        page++
                        getData(false)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        tabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab!!.position
                viewPager.currentItem = tab.position
                tabName = tab.text.toString()
                page = 1
                marsAdapter.clear()
                filterCamera = ""
                getData(false)
            }
            override fun onTabUnselected(tab: TabLayout.Tab){}
            override fun onTabReselected(tab: TabLayout.Tab){}
        })
    }
    private fun viewPager(){
        val adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(Curiosity(), "Curiosity")
        adapter.addFragment(Opportunity(), "Opportunity")
        adapter.addFragment(Spirit(), "Spirit")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
    private fun getData(isOnRefresh:Boolean){
        isLoading = true
        if(!isOnRefresh)progressBar.visibility = View.VISIBLE
        val pager = HashMap<String,String>()
        val filter = HashMap<String,String>()
        pager["page"] = page.toString()
        if(filterCamera != ""){
            filter["camera"] = filterCamera
        }
        Handler().postDelayed({
            RetrofitClient.retrofitService.getMarsRovers(tabName,pager,filter).enqueue(object:retrofit2.Callback<Data>{
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    val listResponse = response.body()?.photos
                    if(listResponse?.size != 0){
                        totalPage ++
                    }
                    if(listResponse!=null){
                        marsAdapter.addList(listResponse)
                    }
                    if(page == totalPage){
                        progressBar.visibility = View.GONE
                    }else{
                        progressBar.visibility = View.INVISIBLE
                    }
                    isLoading =false
                    swipeRefresh.isRefreshing = false
                }
                override fun onFailure(call: Call<Data>, t: Throwable) {
                    Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_SHORT).show()
                }

            })
        },1000)

    }
    private fun setupRecyclerView(){
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =layoutManager
        marsAdapter = MarsAdapter(this)
        recyclerView.adapter = marsAdapter
    }
    override fun onRefresh() {
        marsAdapter.clear()
        page = 1
        getData(true)
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        marsViewModel.getPostCameras(tabName).observe(this){data->
            menu?.clear()
            val a = data.photo_manifest.photos.indexOfLast { cameras -> cameras.sol == 1000}
            for(i in data.photo_manifest.photos[a].cameras){
                menu?.add(0,1,0,i)
            }
        }
        menuInflater.inflate(R.menu.filter_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        page = 1
        marsAdapter.clear()
        filterCamera = item.title.toString()
        getData(false)
        return true
    }

    override fun onItemClick(view:View?,position: Int,photos : List<Photo>) {
        buttonPopupwindow(view,position,photos)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun buttonPopupwindow(view: View?,position: Int,photos : List<Photo>) {
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewPopupwindow: View = layoutInflater.inflate(R.layout.photo_cell, null)
        val popupWindow = PopupWindow(viewPopupwindow, 1080, 1200, true)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        viewPopupwindow.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            true
        }
        if(popupWindow.isShowing){
            val image = photos[position].img_src.replace("http","https")
            Glide.with(this).load(image).into(viewPopupwindow.roverImage)
            viewPopupwindow.cameraName.text = photos[position].camera.name
            viewPopupwindow.landingDate.text = photos[position].rover.landing_date
            viewPopupwindow.launchDate.text = photos[position].rover.launch_date
            viewPopupwindow.status.text = photos[position].rover.status
            viewPopupwindow.roverName.text = photos[position].rover.name
            viewPopupwindow.earthDate.text = photos[position].earth_date
        }

    }
}