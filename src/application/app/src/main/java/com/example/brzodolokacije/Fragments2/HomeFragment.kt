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
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.Posts.VisitUserProfile
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
    private val key : String = "saved_state"

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        if(savedInstanceState!=null)
        {
            savedState = savedInstanceState.getString(key)
            //Log.d("saved",savedState.toString())
        }
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
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                requestLoadFeed(view)
                //Log.d("refresh",savedState.toString())
                //Toast.makeText(requireActivity(),"sa beka - refresh",Toast.LENGTH_SHORT).show()
                refresh.isRefreshing = false
            }, 1500)
        }

        if(savedState==null)
        {
            //Toast.makeText(requireActivity(),"saved state null "+VisitUserProfile.isVisited().toString(),Toast.LENGTH_SHORT).show()
            if(VisitUserProfile.isVisited()==1)
            {
                backFromVisit(sessionManager,view)
                //Log.d("visit",savedState.toString())
                //Toast.makeText(requireActivity(),"povratak sa profila "+VisitUserProfile.isVisited().toString(),Toast.LENGTH_SHORT).show()
            }
            else
            {
                requestLoadFeed(view)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    Log.d("bek",savedState.toString())
//                }, 2000)
                //Toast.makeText(requireActivity(),"sa beka",Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            loadPhotos(sessionManager,view)
//            Log.d("sacuvano",savedState.toString())
            //Toast.makeText(requireActivity(),"sacuvano",Toast.LENGTH_SHORT).show()
        }

        homePostsRv.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastPosition = (homePostsRv.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()!!
                //Toast.makeText(requireActivity(),lastPosition.toString(),Toast.LENGTH_SHORT).show()
                val v = (homePostsRv.layoutManager as? LinearLayoutManager)?.getChildAt(0)
                topViewRv = if(v == null) 0 else v.top - (homePostsRv.layoutManager as? LinearLayoutManager)?.paddingTop!!

                savePosition(lastPosition,topViewRv)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(key,savedState)
        super.onSaveInstanceState(outState)
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
//        Log.d("bekrequ","uslo u request")
        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        retrofit.getAllPosts().enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if(response.body()?.error.toString()=="false")
                {
                    val listOfPhotosStr: String = response.body()?.message.toString()
//                    Log.d("bekrequ",listOfPhotosStr)
                    savedState = listOfPhotosStr
//                    Log.d("bekrequ",savedState.toString())
                    VisitUserProfile.saveFeed(savedState.toString())

                    val typeToken = object : TypeToken<MutableList<Photo>>() {}.type
                    val photosList = Gson().fromJson<MutableList<Photo>>(listOfPhotosStr, typeToken)

                    homePostsRv.apply {
                        mylayoutManager = LinearLayoutManager(context) //activity
                        recyclerView=view.findViewById(R.id.homePostsRv)
                        recyclerView.layoutManager=mylayoutManager
                        recyclerView.setHasFixedSize(true)
                        myAdapter = this.context?.let { PostAdapter(photosList,it,requireActivity()) }
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
        val listOfPhotosStr: String = savedState.toString()

        val typeToken = object : TypeToken<MutableList<Photo>>() {}.type
        val photosList = Gson().fromJson<MutableList<Photo>>(listOfPhotosStr, typeToken)
        homePostsRv.apply {
            mylayoutManager = LinearLayoutManager(context) //activity
            recyclerView=view.findViewById(R.id.homePostsRv)
            recyclerView.layoutManager=mylayoutManager
            recyclerView.setHasFixedSize(true)
            myAdapter = this.context?.let { PostAdapter(photosList,it,requireActivity()) }
            recyclerView.adapter=myAdapter
        }
        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)

        val last = sessionManager.fetchLast()
        val lastOffset= sessionManager.fetchLastOffset()
        ScrollToPosition(last,lastOffset)
    }

    private fun backFromVisit(sessionManager : SessionManager, view : View)
    {
        savedState=VisitUserProfile.retreiveFeed()
        VisitUserProfile.profileVisit(0)
        val typeToken = object : TypeToken<MutableList<Photo>>() {}.type
        val photosList = Gson().fromJson<MutableList<Photo>>(savedState, typeToken)

        homePostsRv.apply {
            mylayoutManager = LinearLayoutManager(context) //activity
            recyclerView=view.findViewById(R.id.homePostsRv)
            recyclerView.layoutManager=mylayoutManager
            recyclerView.setHasFixedSize(true)
            myAdapter = this.context?.let { PostAdapter(photosList,it,requireActivity()) }
            recyclerView.adapter=myAdapter
        }
        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)
        val last = sessionManager.fetchLast()
        val lastOffset= sessionManager.fetchLastOffset()
        ScrollToPosition(last,lastOffset)
    }

    private fun savePosition(last : Int, lastOffset : Int)
    {
        val sessionManager = SessionManager(requireActivity())
        sessionManager.saveLast(last)
        sessionManager.saveLastOffset(lastOffset)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        savePosition()
//        Log.d("destroy","")
//    }

    override fun onStop() {
        super.onStop()
        savePosition(lastPosition,topViewRv)
        Log.d("stop","")
    }

    private fun ScrollToPosition(position : Int, offset : Int)
    {
        homePostsRv.stopScroll()
        (homePostsRv.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position,offset)
    }
}