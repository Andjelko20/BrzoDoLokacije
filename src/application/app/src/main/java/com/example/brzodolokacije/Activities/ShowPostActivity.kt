package com.example.brzodolokacije.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Fragments2.HomeFragment
import com.example.brzodolokacije.Fragments2.ProfileFragment
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.PostDetails
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ShowPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_post)

        val intent = getIntent()
        val postIdStr = intent.getStringExtra("showPost");
//        Log.d("clicked id", postIdStr.toString())

        val postId = postIdStr?.toInt()

        val backButtonPostDetails = findViewById<Button>(R.id.backButtonPostDetails)
        backButtonPostDetails.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val retrofit = Client(this).buildService(Api::class.java)
        if (postId != null) {
            retrofit.getPostData(postId).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false")
                    {
                        val postDetailsStr = response.body()?.message.toString()
                        Log.d("data", postDetailsStr)
                        val gson = Gson()
                        val postDetails: PostDetails = gson.fromJson(postDetailsStr, PostDetails::class.java)

                        val userProfilePicture = findViewById<CircleImageView>(R.id.userProfilePicture)
                        val postOwnerUsername = findViewById<TextView>(R.id.postOwnerUsername)
                        val postPicture = findViewById<ImageView>(R.id.postPicture)
                        val postLocation = findViewById<TextView>(R.id.postLocation)
                        val postNumberOfLikes = findViewById<TextView>(R.id.postNumberOfLikes)
                        val profilePostCaption = findViewById<TextView>(R.id.profilePostCaption)
                        val profilePostDate = findViewById<TextView>(R.id.profilePostDate)
                        val postPhotoComments = findViewById<TextView>(R.id.postPhotoComments)
                        val likeButton = findViewById<ImageView>(R.id.likeButton)

                        //username and pfp
                        postOwnerUsername.text = postDetails.owner
                        Picasso.get().load(Constants.BASE_URL + "User/avatar/" + postDetails.owner).into(userProfilePicture)

                        //post photo
                        Picasso.get().load(Constants.BASE_URL + "Post/postPhoto/" + postDetails.id).into(postPicture)

                        //location
                        postLocation.text = postDetails.location

                        //likes
                        postNumberOfLikes.text = postDetails.numberOfLikes.toString()

                        //caption
                        if(postDetails.caption == "")
                        {
                            profilePostCaption.setVisibility(View.GONE)
                        }
                        else
                        {
                            profilePostCaption.setVisibility(View.VISIBLE)
                            profilePostCaption.text = postDetails.caption
                        }

                        //date
                        profilePostDate.text = convertLongToTime(postDetails.date)
                    }
                    else
                    {
                        Log.d("error", response.body()?.error.toString())
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failed", "")
                }

            })
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long): String {
        val format = SimpleDateFormat("HH:mm  dd/MM/yyyy", Locale("Serbia"))
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        val tickAtEpoche= 621355968000000000L
        val ticksPerMiliSec = 10000
        return format.format(Date((time-tickAtEpoche)/ticksPerMiliSec))
    }
}