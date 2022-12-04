package com.example.brzodolokacije.Fragments2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Adapters.PostAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.PaginationResponse
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.Posts.HomeFragmentState
import com.example.brzodolokacije.R
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

    private var myAdapter : RecyclerView.Adapter<PostAdapter.MainViewHolder>? = null
    private var mylayoutManager : RecyclerView.LayoutManager? = null
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.meni,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.editProfileMeni ->{
                Log.e("inside","Selected")
            }
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager= SessionManager(requireActivity())
        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.VISIBLE)

        addPostHome.setOnClickListener{
            activity?.let{
                val intent = Intent (it, ActivityAddPost::class.java)
                it.startActivity(intent)
//                it.finish()
            }
        }

        val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayoutHome)
        refresh.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                requestLoadFeed(view)
                refresh.isRefreshing = false
            }, 1500)
        }
        if(HomeFragmentState.isSaved() && HomeFragmentState.getList() != null)
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
//                Toast.makeText(requireActivity(),isLoading.toString(),Toast.LENGTH_SHORT).show()
                if(!isLoading && page < HomeFragmentState.returnMaxPages())
                {
                    val lastCompletelyVisible = (homePostsRv.layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition()!!
                    if(lastCompletelyVisible == feed.size-1)
                    {
                        //bottom of the list, load more
                        loadMorePhotos(sessionManager,view)
                        isLoading = true
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
        page = 1
//        Toast.makeText(requireActivity(),page.toString(),Toast.LENGTH_SHORT).show()
        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        retrofit.getAll(page).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if(response.body()?.error.toString()=="false")
                {
                    val listOfPhotosStr: String = response.body()?.message.toString()
//                    savedState = listOfPhotosStr
//                    HomeFragmentState.saveFeed(savedState.toString())

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
                        myAdapter = this.context?.let { PostAdapter(feed,it,requireActivity()) }
                        recyclerView.adapter=myAdapter
                    }
                    view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)
                }
                else
                {
                    Toast.makeText(requireActivity(),"You don't follow anyone",Toast.LENGTH_SHORT).show()
                    view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(requireActivity(),"Error loading images",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadPhotos(sessionManager : SessionManager, view : View)
    {
//        savedState = HomeFragmentState.retreiveFeed()
        feed = HomeFragmentState.getList()!!
        page = HomeFragmentState.savedPage()
//        Toast.makeText(requireActivity(),page.toString(),Toast.LENGTH_SHORT).show()
//        val listOfPhotosStr: String = savedState.toString()
        HomeFragmentState.shouldSave(false)
//        val typeToken = object : TypeToken<MutableList<Photo>>() {}.type
//        val photosList = Gson().fromJson<MutableList<Photo>>(listOfPhotosStr, typeToken)
        homePostsRv.apply {
            mylayoutManager = LinearLayoutManager(context) //activity
            recyclerView=view.findViewById(R.id.homePostsRv)
            recyclerView.layoutManager=mylayoutManager
            recyclerView.setHasFixedSize(true)
            myAdapter = this.context?.let { PostAdapter(feed,it,requireActivity()) }
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
//        Log.d("adapter", myAdapter.toString())
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
                        feed.removeAt(feed.size-1)
                        val last = sessionManager.fetchLast()
                        val lastOffset= sessionManager.fetchLastOffset()
                        val listOfPhotosStr: String = response.body()?.message.toString()
                        val typeToken = object : TypeToken<PaginationResponse>() {}.type
                        val pagination = Gson().fromJson<PaginationResponse>(listOfPhotosStr, typeToken)
                        val photosList  = pagination.posts
                        //HomeFragmentState.changeMaxPages(pagination.numberOfPages)
                        var i = 0
                        var j = 0
                        var flag = true
                        while(i < photosList!!.size)
                        {
                            j=0
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
                            }
                            flag = true
                            i++
                        }
                        HomeFragmentState.list(feed)
                        myAdapter!!.notifyDataSetChanged()
                        isLoading=false
                        ScrollToPosition(last,lastOffset)

                    }, 2000)
                }
                else
                {
                    val p = feed.size -1
                    feed.removeAt(feed.size-1)
                    myAdapter!!.notifyItemInserted(p)
//                    isLoading=false
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(requireActivity(),"Error loading images",Toast.LENGTH_SHORT).show()
                val p = feed.size -1
                feed.removeAt(feed.size-1)
                myAdapter!!.notifyItemInserted(p)
//                isLoading=false
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
//        HomeFragmentState.saveFeed(savedState.toString())
        if(HomeFragmentState.isSaved())
        {
            savePosition(lastPosition,topViewRv)
            HomeFragmentState.page(page)
            savePosition(lastPosition,topViewRv)
            isLoading=false
            //HomeFragmentState.shouldSave(false)
        }
//        Log.d("saved","saved")
    }

    override fun onDestroy() {
        super.onDestroy()
//        HomeFragmentState.saveFeed(savedState.toString())
        HomeFragmentState.shouldSave(true)
        HomeFragmentState.page(page)
        savePosition(lastPosition,topViewRv)
        isLoading=false
//        Log.d("saved","destroy")
    }
    private fun ScrollToPosition(position : Int, offset : Int)
    {
        homePostsRv.stopScroll()
        (homePostsRv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position,offset)
    }
}