package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Adapters.FollowersAdapter
import com.example.brzodolokacije.Adapters.HomePostAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.PaginationResponse
import com.example.brzodolokacije.Posts.Follower
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class FollowersListActivity : AppCompatActivity() {
    var myAdapter : RecyclerView.Adapter<FollowersAdapter.MainViewHolder>? = null
    var mylayoutManager : RecyclerView.LayoutManager? = null
    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers_list)

        val backButtonFollowersSection = findViewById<Button>(R.id.backButtonFollowersSection);
        backButtonFollowersSection.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val retrofit = Client(this).buildService(Api::class.java)
        val sessionManager = SessionManager(this)
        var usernameSm = sessionManager.fetchUsername()

        if (usernameSm != null) {
            retrofit.getFollowers(usernameSm).enqueue(object : retrofit2.Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false")
                    {
                        val followersListStr: String = response.body()?.message.toString()

                        val typeToken = object : TypeToken<List<Follower>>() {}.type
                        val followersList = Gson().fromJson<List<Follower>>(followersListStr, typeToken)

                        val followersListRv = findViewById<RecyclerView>(R.id.followersListRv)
                        followersListRv.apply {
                            mylayoutManager = LinearLayoutManager(context)
                            recyclerView=findViewById(R.id.followersListRv)
                            myAdapter = FollowersAdapter(followersList, context)
                            recyclerView.layoutManager=layoutManager
                            recyclerView.adapter=myAdapter
                        }

//                        Log.d("items", followersListRv.adapter?.getItemCount().toString())
                    }
                    else
                    {
                        Log.d("error", response.body()?.error.toString());
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failed", "");
                }

            })
        }
    }
}