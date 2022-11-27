package com.example.brzodolokacije.Fragments2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Adapters.ProfilePostsAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Posts.PrivremeneSlikeZaFeed
import com.example.brzodolokacije.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var myAdapter : RecyclerView.Adapter<ProfilePostsAdapter.MainViewHolder>? = null
    private var mylayoutManager : RecyclerView.LayoutManager? = null
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profilePostsRv = view.findViewById<RecyclerView>(R.id.profilePostsRv)

        profilePostsRv.apply {
            recyclerView=view.findViewById(R.id.profilePostsRv)
            layoutManager = GridLayoutManager(context, 3)
            myAdapter = this.context?.let { ProfilePostsAdapter(getUserPosts(), it) }
            recyclerView.layoutManager=layoutManager
            recyclerView.adapter=myAdapter
        }
    }

    fun getUserPosts(): List<String>
    {
        val sessionManager= this.context?.let { SessionManager(it) }
        val usernameSm = sessionManager?.fetchUsername()

        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        if (usernameSm != null){
            retrofit.getUserPosts(usernameSm).enqueue(object: Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
//                    Log.d("response", "")
                    if(response.body()?.error.toString() == "false"){
//                        Log.d("error false", "")
                        val json = response.body()?.message.toString()
                        Log.d("json", json)
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failure", "")
                }

            })
        }

        return PrivremeneSlikeZaFeed.getPhotos();
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}