package com.example.brzodolokacije.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Adapters.CommentsAdapter
import com.example.brzodolokacije.Adapters.CommentsAdapterZaActivity
import com.example.brzodolokacije.Adapters.LikesAdapter
import com.example.brzodolokacije.Adapters.LikesAdapterZaActivity
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Fragments2.HomeFragment
import com.example.brzodolokacije.Fragments2.ProfileFragment
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.NewCommentDto
import com.example.brzodolokacije.Models.PostDetails
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.Posts.*
import com.example.brzodolokacije.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.rv_photopost.*
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
        val profileVisit = intent.getStringExtra("profileVisit")
//        Log.d("clicked id", postIdStr.toString())

        val postId = postIdStr?.toInt()

        val backButtonPostDetails = findViewById<Button>(R.id.backButtonPostDetails)
        backButtonPostDetails.setOnClickListener{
            if(profileVisit == "profileVisit")
            {
                finish()
            }
            else
            {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("backToProfile", "returnToProfile");
                startActivity(intent)
            }
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


                        postOwnerUsername.setOnClickListener{

                                val intent = Intent(this@ShowPostActivity,ProfileVisitActivity::class.java)
                                intent.putExtra("visit",postOwnerUsername.text.toString())
                                startActivity(intent)
                        }
                        //likes
                        postNumberOfLikes.text = postDetails.numberOfLikes.toString()
                        if(postDetails.likedByMe) likeButton.setBackgroundResource(R.drawable.liked)
                        else likeButton.setBackgroundResource(R.drawable.unliked)
                        likeButton.setOnClickListener{
                            likeUnlike(postDetails)
                        }

                        postNumberOfLikes.setOnClickListener{
                            val view : View = LayoutInflater.from(this@ShowPostActivity).inflate(R.layout.fragment_like_section,null)
                            loadLikes(view,postDetails)

                            //refreshing the list of likes
                            val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayoutLikes)
                            refresh.setOnRefreshListener {
                                android.os.Handler(Looper.getMainLooper()).postDelayed({

                                    loadLikes(view,postDetails)
                                    refreshPost(postDetails)
                                    refresh.isRefreshing = false
                                }, 1500)
                            }
                            val dialog = BottomSheetDialog(this@ShowPostActivity)
                            dialog.setContentView(view)
                            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            //dialog.behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                            dialog.show()

                            refreshPost(postDetails)
                        }

                        //comments
                        if(postDetails.numberOfComments != 0)
                        {
                            postPhotoComments.text="View all ${postDetails.numberOfComments} comments"
                        }
                        else postPhotoComments.text="No comments yet. Add yours?"

                        postPhotoComments.setOnClickListener{
                            val view : View = LayoutInflater.from(this@ShowPostActivity).inflate(R.layout.fragment_comment,null)
                            loadComments(view, postDetails)

                            //adding a new comment
                            val addCommentButton = view.findViewById<ImageView>(R.id.addCommentBtn)
                            val addCommentText = view.findViewById<EditText>(R.id.addCommentText)
                            addCommentButton.setOnClickListener{
                                if(addCommentText.text.toString()!="")
                                {
                                    val ct=addCommentText.text.toString().trim()
                                    val newComment = NewCommentDto(postDetails.id.toString(),ct)
                                    addNewComment(view, postDetails,newComment)
                                    refreshPost(postDetails)
                                }
                            }
                            //refreshing the list of comments
                            val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayoutComments)
                            refresh.setOnRefreshListener {
                                android.os.Handler(Looper.getMainLooper()).postDelayed({

                                    loadComments(view, postDetails)
                                    refreshPost(postDetails)
                                    refresh.isRefreshing = false
                                }, 1500)
                            }
                            val dialog = BottomSheetDialog(this@ShowPostActivity)
                            dialog.setContentView(view)
                            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            //dialog.behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                            dialog.show()
                            refreshPost(postDetails)
                        }

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

    private fun likeUnlike(postDetails: PostDetails)
    {
        val likedByMe = findViewById<ImageView>(R.id.likeButton)
        val retrofit = Client(this).buildService(Api::class.java)
        retrofit.likPost(postDetails.id.toString()).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString()=="false")
                {
                    val state = response.body()?.message.toString()

                    if(state=="liked")
                    {
                        likedByMe.setBackgroundResource(R.drawable.liked)
                        postDetails.likedByMe = true
                    }

                    else
                    {
                        likedByMe.setBackgroundResource(R.drawable.unliked)
                        postDetails.likedByMe = false
                    }

                    refreshPost(postDetails)
                }
                else
                {
                    Toast.makeText(this@ShowPostActivity,"Unable to like post", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@ShowPostActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadLikes(view : View, postDetails: PostDetails)
    {
        val retrofit = Client(this).buildService(Api::class.java)
        retrofit.getLikes(postDetails.id.toString()).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString()=="false")
                {
                    val listOfLikesStr: String = response.body()?.message.toString();

                    val typeToken = object : TypeToken<List<Like>>() {}.type
                    val likesList = Gson().fromJson<List<Like>>(listOfLikesStr, typeToken)


                    val rvLikes = view.findViewById<RecyclerView>(R.id.rv_likes)
                    if(likesList.isNotEmpty()) rvLikes.adapter = LikesAdapterZaActivity(likesList,this@ShowPostActivity)
                }
                else
                {
                    Toast.makeText(this@ShowPostActivity,"Error loading likes",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@ShowPostActivity,"Error loading likes. Something went wrong",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadComments(view: View, postDetails: PostDetails)
    {
        val retrofit = Client(this).buildService(Api::class.java)
        retrofit.getComments(postDetails.id.toString()).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString()=="false")
                {
                    val listOfCommentsStr: String = response.body()?.message.toString();

                    val typeToken = object : TypeToken<List<Comment>>() {}.type
                    val commentsList = Gson().fromJson<List<Comment>>(listOfCommentsStr, typeToken)


                    val rvComments = view.findViewById<RecyclerView>(R.id.rv_comments)
                    if(commentsList.isNotEmpty()) rvComments.adapter = CommentsAdapterZaActivity(commentsList,this@ShowPostActivity)
                }
                else
                {
                    Toast.makeText(this@ShowPostActivity,"Error loading comments",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@ShowPostActivity,"Error loading comments. Something went wrong",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addNewComment(view: View, postDetails: PostDetails, newComment : NewCommentDto)
    {
        val retrofit = Client(this).buildService(Api::class.java)
        retrofit.addComment(newComment).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString()=="false")
                {
                    //Toast.makeText(activity,"Comment added",Toast.LENGTH_SHORT).show()
                    val newNumOfComments=response.body()?.message.toString().trim()
                    findViewById<TextView>(R.id.postPhotoComments).text = "View all ${newNumOfComments} comments"
                    view.findViewById<TextView>(R.id.addCommentText).text=""
                    loadComments(view, postDetails)
                    refreshPost(postDetails)
                }
                else
                {
                    Toast.makeText(this@ShowPostActivity,"Error",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@ShowPostActivity,"Something else went wrong",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun refreshPost(postDetails: PostDetails)
    {
        var likes = findViewById<TextView>(R.id.postNumberOfLikes)
        var comments = findViewById<TextView>(R.id.postPhotoComments)

        val retrofit = Client(this).buildService(Api::class.java)
        retrofit.refrestPost(postDetails.id.toString()).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString()=="false")
                {
                    val newStatsStr = response.body()?.message.toString()
                    val newStats : Stats = Gson().fromJson(newStatsStr, Stats :: class.java)

                    likes.text = newStats.numOfLikes.toString()
                    postDetails.numberOfComments = newStats.numOfComments.toInt()
                    postDetails.numberOfLikes = newStats.numOfLikes.toInt()
                    if(newStats.numOfComments.toInt() != 0) comments.text="View all ${newStats.numOfComments} comments"
                    else comments.text="No comments yet. Add yours?"

                }
                else
                {
                    Toast.makeText(this@ShowPostActivity,"Error refreshing",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@ShowPostActivity,"Something went wrong while refreshing",Toast.LENGTH_SHORT).show()
            }

        })
    }
}