package com.example.brzodolokacije.Fragments2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ProgressBar
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Activities.ActivityAddPost
import com.example.brzodolokacije.Adapters.PostAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_addpost.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
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

        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.VISIBLE)

        addPostHome.setOnClickListener{
            activity?.let{
                val intent = Intent (it, ActivityAddPost::class.java)
                it.startActivity(intent)
            }
        }

        val retrofit = Client(requireActivity()).buildService(Api::class.java)
        Log.d("search",searchFilter.text.toString())
        searchBtn.setOnClickListener{
            Intent(requireActivity(),HomeFragment::class.java).also{
                it.putExtra("location",searchFilter.text.toString().trim())
                startActivity(it)
            }
        }
        var nesto = activity?.intent!!.getStringExtra("location").toString()
        if(nesto != "null") {
            searchFilter.setText(nesto)
        }
        else{
            searchFilter.setText("")
        }
        if(searchFilter.text.toString() != "")
        {
            var location = activity?.intent!!.getStringExtra("location").toString()
            retrofit.getByLocation(location).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if (response.body()?.error.toString() == "false") {
                        val listOfPhotosStr: String = response.body()?.message.toString();

                        val typeToken = object : TypeToken<List<Photo>>() {}.type
                        val photosList = Gson().fromJson<List<Photo>>(listOfPhotosStr, typeToken)

                        homePostsRv.apply {
                            mylayoutManager = LinearLayoutManager(context) //activity
                            recyclerView = view.findViewById(R.id.homePostsRv)
                            recyclerView.layoutManager = mylayoutManager
                            recyclerView.setHasFixedSize(true)
                            myAdapter =
                                this.context?.let { PostAdapter(photosList, it, requireActivity()) }
                            recyclerView.adapter = myAdapter
                        }
                        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Error loading images",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(), "There is no location with that name", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }
        else {
            retrofit.getAllPosts().enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if (response.body()?.error.toString() == "false") {
                        val listOfPhotosStr: String = response.body()?.message.toString();

                        val typeToken = object : TypeToken<List<Photo>>() {}.type
                        val photosList = Gson().fromJson<List<Photo>>(listOfPhotosStr, typeToken)

                        homePostsRv.apply {
                            mylayoutManager = LinearLayoutManager(context) //activity
                            recyclerView = view.findViewById(R.id.homePostsRv)
                            recyclerView.layoutManager = mylayoutManager
                            recyclerView.setHasFixedSize(true)
                            myAdapter =
                                this.context?.let { PostAdapter(photosList, it, requireActivity()) }
                            recyclerView.adapter = myAdapter
                        }
                        view.findViewById<ProgressBar>(R.id.progressBar).setVisibility(View.GONE)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Error loading images",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Error loading images", Toast.LENGTH_SHORT)
                        .show()
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
}