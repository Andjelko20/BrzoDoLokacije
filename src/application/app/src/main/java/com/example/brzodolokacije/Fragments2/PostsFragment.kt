package com.example.brzodolokacije.Fragments2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Activities.MainActivity
import com.example.brzodolokacije.Activities.ShowPostActivity
import com.example.brzodolokacije.Adapters.ProfilePostsAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    private var postsIds : List<Int> = mutableListOf<Int>()

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

        val sessionManager= this.context?.let { SessionManager(it) }
        val usernameSm = sessionManager?.fetchUsername()

        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        if (usernameSm != null){
            retrofit.getUserPosts(usernameSm).enqueue(object: Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false"){
                        val json = response.body()?.message.toString()
//                        Log.d("json", json)
                        val typeToken = object : TypeToken<List<Int>>() {}.type
                        val idList = Gson().fromJson<List<Int>>(json, typeToken)
                        postsIds = idList
//                        for(postId in postsIds)
//                        {
//                            Log.d("id", postId.toString())
//                        }

                        val ids = mutableListOf<String>()
                        for(id in idList){
                            ids.add(Constants.BASE_URL + "Post/postPhoto/" + id.toString())
                        }

                        val profilePostsRv = view.findViewById<RecyclerView>(R.id.profilePostsRv)
                        profilePostsRv.apply {
                            recyclerView=view.findViewById(R.id.profilePostsRv)
                            layoutManager = GridLayoutManager(context, 3)
                            myAdapter = this.context?.let { ProfilePostsAdapter(ids, it, object: ProfilePostsAdapter.OnItemClickListener {
                                override fun OnItemClick(position: Int) {
                                    var clickedId = -1
                                    var i = 0;
                                    for(postId in postsIds)
                                    {
                                        if(i == position)
                                        {
                                            clickedId = postId
                                        }
                                        i++
                                    }
//                                    Toast.makeText(requireActivity(), "Item $position clicked, id: $clickedId", Toast.LENGTH_SHORT).show()
                                    if(clickedId != -1)
                                    {
                                        val intent = Intent(it, ShowPostActivity::class.java)
                                        intent.putExtra("showPost", clickedId.toString());
                                        startActivity(intent)
                                    }
                                }
                            }) }
                            recyclerView.layoutManager=layoutManager
                            recyclerView.adapter=myAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(),"An error occurred",Toast.LENGTH_SHORT).show()
                }

            })
        }
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