package com.example.brzodolokacije.Fragments2

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
import com.example.brzodolokacije.Adapters.ProfilePostsAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Posts.HomeFragmentState
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

private var pvAdapter : RecyclerView.Adapter<ProfilePostsAdapter.MainViewHolder>? = null
private var pvLayoutManager : RecyclerView.LayoutManager? = null
private lateinit var pvRecyclerView : RecyclerView

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileVisitPostsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileVisitPostsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var user : String

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
        val view =  inflater.inflate(R.layout.fragment_posts, container, false)
        user = arguments?.getString("username").toString()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profilePostsVisitRv = view.findViewById<RecyclerView>(R.id.profilePostsRv)

        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        if (user != "null") {
            retrofit.getUserPosts(user).enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if (response.body()?.error.toString() == "false") {
                        val json = response.body()?.message.toString()
//                        Log.d("json", json)
                        val typeToken = object : TypeToken<List<Int>>() {}.type
                        val idList = Gson().fromJson<List<Int>>(json, typeToken)

                        val ids = mutableListOf<String>()
                        for (id in idList) {
                            ids.add(Constants.BASE_URL + "Post/postPhoto/" + id.toString())
                        }

                        profilePostsVisitRv.apply {
                            pvLayoutManager = GridLayoutManager(context, 3)
                            pvRecyclerView = view.findViewById(R.id.profilePostsRv)
                            pvAdapter = this.context?.let { ProfilePostsAdapter(ids, it, object: ProfilePostsAdapter.OnItemClickListener {
                                override fun OnItemClick(position: Int) {
                                    Toast.makeText(requireActivity(), "Item $position clicked", Toast.LENGTH_SHORT).show()
                                }
                            }) }
                            pvRecyclerView.layoutManager = pvLayoutManager
                            pvRecyclerView.adapter = pvAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failure", "")
                }

            })
        }
        else
        {
            Toast.makeText(requireActivity(),"Error loading images",Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileVisitPostsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileVisitPostsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}