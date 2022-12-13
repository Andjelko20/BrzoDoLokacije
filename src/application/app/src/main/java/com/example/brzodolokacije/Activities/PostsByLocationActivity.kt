package com.example.brzodolokacije.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Adapters.ProfilePostsAdapter
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.PostDetails
import com.example.brzodolokacije.ModelsDto.FilterDto
import com.example.brzodolokacije.ModelsDto.PinDto
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_addpost.*
import kotlinx.android.synthetic.main.activity_posts_by_location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsByLocationActivity : AppCompatActivity() {

    private var myAdapter : RecyclerView.Adapter<ProfilePostsAdapter.MainViewHolder>? = null
    private var mylayoutManager : RecyclerView.LayoutManager? = null
    private lateinit var recyclerView : RecyclerView

    private var postsIds : List<Int> = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_by_location)

        val lokacija = intent.getStringExtra("location")
        val retrofit = Client(this).buildService(Api::class.java)
        val wrapper: Context = ContextThemeWrapper(this, R.style.MyPopupMenu)
        naslovLokacija.text = lokacija

        backButtonPostsByLocation.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("backPressed", "returnToProfile");
            startActivity(intent)
        }

            optionsMeniZaPostove.setOnClickListener{
            val popupMenu = PopupMenu(wrapper, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.popularityMeni ->{
                        val intent = Intent(this, PostsByLocationActivity::class.java)
                        intent.putExtra("location",lokacija)
                        intent.putExtra("filter","")
                        startActivity(intent)
                        true
                    }
                    R.id.dateMeni ->{
                        val intent = Intent(this, PostsByLocationActivity::class.java)
                        intent.putExtra("location",lokacija)
                        intent.putExtra("filter","date")
                        startActivity(intent)
                        true
                    }
                    else -> false
                }

            }
            popupMenu.inflate(R.menu.filter)
            popupMenu.show()
        }


        var filterDto = FilterDto(lokacija,"")
        val filter = intent.getStringExtra("filter")
//        Log.d("filter",filter.toString())
        if(filter == "date")
        {
            filterDto = FilterDto(lokacija,"date")
        }
//        else if(filter == "popularity")
//        {
//            filterDto = FilterDto(lokacija,"popularity")
//        }

        retrofit.getByLocation(filterDto).enqueue(object : Callback<DefaultResponse>{
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    val listOfPosts: String = response.body()?.message.toString()
                    val typeToken = object : TypeToken<List<PostDetails>>() {}.type
                    val posts = Gson().fromJson<List<PostDetails>>(listOfPosts, typeToken)
//                    Log.d("lista",posts.toString())

                    val postsIds = mutableListOf<Int>()
                    var i = 0
                    while(i < posts.size)
                    {
                        postsIds.add(posts.get(i).id)
                        i++
                    }

                    val ids = mutableListOf<String>()
                    for(id in postsIds){
                        ids.add(Constants.BASE_URL + "Post/postPhoto/" + id.toString())
                    }

                    val PostsByLocationRv = findViewById<RecyclerView>(R.id.PostsByLocationRv)
                    PostsByLocationRv.apply {
                        recyclerView=findViewById(R.id.PostsByLocationRv)
                        mylayoutManager = GridLayoutManager(this@PostsByLocationActivity, 3)
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
                        recyclerView.layoutManager=mylayoutManager
                        recyclerView.adapter=myAdapter
                    }

                }
                else
                {
//                    Log.d("error", response.body()?.error.toString())
                    Toast.makeText(this@PostsByLocationActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                Log.d("failed", "")
                Toast.makeText(this@PostsByLocationActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }

        })
    }
}