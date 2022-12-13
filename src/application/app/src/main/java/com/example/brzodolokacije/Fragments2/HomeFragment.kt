package com.example.brzodolokacije.Fragments2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Adapters.HomePostAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.PaginationResponse
import com.example.brzodolokacije.Posts.HomeFragmentState
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var savedState : String? = null
    private lateinit var feed : MutableList<Photo?>
    private var page : Int = 1

    private var isLoading : Boolean = false

    private var lastPosition : Int = 0
    private var topViewRv = 0

    private var myAdapter : RecyclerView.Adapter<HomePostAdapter.MainViewHolder>? = null
    private var mylayoutManager : RecyclerView.LayoutManager? = null
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)

        if (ActivityCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                ExploreFragment.LOCATION_REQUEST_CODE
            )

            return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.meni,menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<LinearLayout>(R.id.notFollowingAnyoneHomeFragment).setVisibility(View.GONE)
        view.findViewById<FloatingActionButton>(R.id.refreshPostHome).setVisibility(View.GONE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager= SessionManager(requireActivity())
        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.VISIBLE)

        addPostHome.setOnClickListener{
            activity?.let{
                val intent = Intent (it, ActivityAddPost::class.java)
                it.startActivity(intent)
            }
        }

        val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayoutHome)
        refresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                requestLoadFeed(view)
                refresh.isRefreshing = false
            }, 1500)
        }
        if(HomeFragmentState.isSaved() && HomeFragmentState.getList() != null && !(HomeFragmentState.getList())?.isEmpty()!!)
        {
            loadPhotos(sessionManager,view)
        }
        else
        {
            requestLoadFeed(view)
            val p = requireActivity().intent.getStringExtra("postAdded")
            if(p!=null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    loadPhotos(sessionManager,view)
                }, 500)
            }
        }

        homePostsRv.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastPosition = (homePostsRv.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()!!
                val v = (homePostsRv.layoutManager as? LinearLayoutManager)?.getChildAt(0)
                topViewRv = if(v == null) 0 else v.top - (homePostsRv.layoutManager as? LinearLayoutManager)?.paddingTop!!

                savePosition(lastPosition,topViewRv)
                if(lastPosition==0 && topViewRv==0)
                    view.findViewById<FloatingActionButton>(R.id.refreshPostHome).setVisibility(View.GONE)
                if(!isLoading)
                {
                    if(page+1 <= HomeFragmentState.returnMaxPages())
                    {
                        val lastCompletelyVisible = (homePostsRv.layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition()!!
                        if(lastCompletelyVisible == feed.size-1)
                        {
                            //bottom of the list, load more
                            loadMorePhotos(sessionManager,view)
                            isLoading = true
                        }
                    }
                    else
                    {
                        if(sessionManager.fetchLast()==feed.size-2)
                        {
                            val backToTop=view.findViewById<FloatingActionButton>(R.id.refreshPostHome)
                            backToTop.setVisibility(View.VISIBLE)
                            backToTop.setOnClickListener{
                                recyclerView.stopScroll()
                                ScrollToPosition(0,0)
                                refresh.isRefreshing = true
                                Handler(Looper.getMainLooper()).postDelayed({
                                    requestLoadFeed(view)
                                    refresh.isRefreshing = false
                                }, 1500)
                            }
                        }
                        else view.findViewById<FloatingActionButton>(R.id.refreshPostHome).setVisibility(View.GONE)
                    }
                }
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun requestLoadFeed(view : View)
    {
        view.findViewById<LinearLayout>(R.id.notFollowingAnyoneHomeFragment).setVisibility(View.GONE)
        page = 1
        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        retrofit.getAll(page).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if(response.body()?.error.toString()=="false")
                {
                    val listOfPhotosStr: String = response.body()?.message.toString()

                    val typeToken = object : TypeToken<PaginationResponse>() {}.type
                    val pagination = Gson().fromJson<PaginationResponse>(listOfPhotosStr, typeToken)
                    val photosList  = pagination.posts
                    HomeFragmentState.list(photosList)
                    HomeFragmentState.changeMaxPages(pagination.numberOfPages)
                    feed = HomeFragmentState.getList()!!

                    homePostsRv.apply {
                        mylayoutManager = LinearLayoutManager(context) //activity
                        recyclerView=view.findViewById(R.id.homePostsRv)
                        recyclerView.layoutManager=mylayoutManager
                        recyclerView.setHasFixedSize(true)
                        myAdapter = this.context?.let { HomePostAdapter(feed,it,requireActivity()) }
                        recyclerView.adapter=myAdapter
                    }
                    view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)
                }
                else
                {
                    val praznaLista= mutableListOf<Photo?>();
                    feed=praznaLista;
                    HomeFragmentState.list(feed)
                    homePostsRv.apply {
                        mylayoutManager = LinearLayoutManager(context) //activity
                        recyclerView=view.findViewById(R.id.homePostsRv)
                        recyclerView.layoutManager=mylayoutManager
                        recyclerView.setHasFixedSize(true)
                        myAdapter = this.context?.let { HomePostAdapter(feed,it,requireActivity()) }
                        recyclerView.adapter=myAdapter
                    }
                    view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)
                    view.findViewById<LinearLayout>(R.id.notFollowingAnyoneHomeFragment).setVisibility(View.VISIBLE)
                    view.findViewById<TextView>(R.id.endlessTextHomeFragment).isSelected = true
                }
            }
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(requireActivity(),"Error loading images",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadPhotos(sessionManager : SessionManager, view : View)
    {
        feed = HomeFragmentState.getList()!!
        page = HomeFragmentState.savedPage()
        HomeFragmentState.shouldSave(false)
        homePostsRv.apply {
            mylayoutManager = LinearLayoutManager(context) //activity
            recyclerView=view.findViewById(R.id.homePostsRv)
            recyclerView.layoutManager=mylayoutManager
            recyclerView.setHasFixedSize(true)
            myAdapter = this.context?.let { HomePostAdapter(feed,it,requireActivity()) }
            recyclerView.adapter=myAdapter
        }
        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)

        val last = sessionManager.fetchLast()
        val lastOffset= sessionManager.fetchLastOffset()
        ScrollToPosition(last,lastOffset)
    }

    private fun loadMorePhotos(sessionManager : SessionManager,view : View)
    {
        page++
        feed.add(null)
        myAdapter!!.notifyItemInserted(feed.size -1)
        ScrollToPosition(feed.size-1,0)
        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        retrofit.getAll(page).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    Handler(Looper.getMainLooper()).postDelayed({
                        val p = feed.size -1
                        feed.removeAt(feed.size-1)
                        recyclerView.post {
                            myAdapter!!.notifyItemRemoved(p)
                            recyclerView.setHasFixedSize(true)
                        }
                        val listOfPhotosStr: String = response.body()?.message.toString()
                        val typeToken = object : TypeToken<PaginationResponse>() {}.type
                        val pagination = Gson().fromJson<PaginationResponse>(listOfPhotosStr, typeToken)
                        val photosList  = pagination.posts
                        HomeFragmentState.changeMaxPages(pagination.numberOfPages)
                        var i = 0
                        var flag = true
                        while(i < photosList!!.size)
                        {
                            var j=0
                            while(j < feed.size)
                            {
                                if(photosList.get(i)!!.id == feed.get(j)!!.id)
                                {
                                    flag=false
                                    break
                                }
                                j++
                            }
                            if(flag)
                            {
                                feed.add(photosList.get(i))
                                recyclerView.post {
                                    recyclerView.stopScroll()
                                    myAdapter!!.notifyItemInserted(feed.size-1)
                                    recyclerView.setHasFixedSize(true)
                                }
                            }
                            flag = true
                            i++
                        }
                        HomeFragmentState.list(feed)
                        isLoading=false

                    }, 2000)
                }
                else
                {
                    val p = feed.size -1
                    feed.removeAt(feed.size-1)
                    myAdapter!!.notifyItemRemoved(p)
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(requireActivity(),"Error loading images",Toast.LENGTH_SHORT).show()
                val p = feed.size -1
                feed.removeAt(feed.size-1)
                myAdapter!!.notifyItemRemoved(p)
            }

        })
    }

    private fun savePosition(last : Int, lastOffset : Int)
    {
        val sessionManager = SessionManager(requireActivity())
        sessionManager.saveLast(last)
        sessionManager.saveLastOffset(lastOffset)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(HomeFragmentState.isSaved())
        {
            savePosition(lastPosition,topViewRv)
            HomeFragmentState.page(page)
            savePosition(lastPosition,topViewRv)
            isLoading=false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HomeFragmentState.shouldSave(true)
        HomeFragmentState.page(page)
        savePosition(lastPosition,topViewRv)
        isLoading=false
    }
    private fun ScrollToPosition(position : Int, offset : Int)
    {
        homePostsRv.stopScroll()
        (homePostsRv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position,offset)
    }
}