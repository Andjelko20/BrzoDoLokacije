package com.example.brzodolokacije.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.NewCommentDto
import com.example.brzodolokacije.Models.UserProfile
import com.example.brzodolokacije.Posts.Comment
import com.example.brzodolokacije.Posts.Like
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.Posts.Stats
import com.example.brzodolokacije.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class PostAdapter(val photoList : List<Photo>, val context : Context, val activity : Context) :
    RecyclerView.Adapter<PostAdapter.MainViewHolder>() {

    var dataList = photoList

    inner class MainViewHolder(private val itemView: View) :RecyclerView.ViewHolder(itemView) {

        fun bindData(photo : Photo, index : Int)
        {
            val owner = itemView.findViewById<TextView>(R.id.postOwner)
            val profilePic = itemView.findViewById<CircleImageView>(R.id.userProfilePic)
            val ownerProfile = itemView.findViewById<ConstraintLayout>(R.id.ownerProfile)
            val date = itemView.findViewById<TextView>(R.id.postDate)
            val location = itemView.findViewById<TextView>(R.id.location)
            val caption = itemView.findViewById<TextView>(R.id.postCaption)
            var likes = itemView.findViewById<TextView>(R.id.numOfLikes)
            var comments = itemView.findViewById<TextView>(R.id.postComments)
            val image = itemView.findViewById<ImageView>(R.id.postImage)
            val likedByMe = itemView.findViewById<ImageView>(R.id.likeBtn)

            //owner
            owner.text = photo.owner

            //profile image
            val path : String=Constants.BASE_URL + "User/avatar/" + photo.owner
            Picasso.get().load(path).into(profilePic);

            //for visiting post owner's profile
            ownerProfile.setOnClickListener{
                //Toast.makeText(context,"Owner: ${photo.owner}",Toast.LENGTH_SHORT).show()
                val view : View = LayoutInflater.from(context).inflate(R.layout.fragment_profile_visit,null)
                val user = view.findViewById<TextView>(R.id.usernameProfileVisit)
                getUserInfo(photo.owner,path,view)
                val dialog = BottomSheetDialog(activity)
                dialog.setContentView(view)
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                //dialog.behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                dialog.show()
            }

            //date
            date.text = convertLongToTime(photo.date)

            //location
            val text= photo.location //Html.fromHtml("<i>"+photo.location+"</i>")
            location.text = text //=photo.location

            //caption
            caption.text = photo.caption //Html.fromHtml("<i>"+photo.caption+"</i>")

            //list of likes
            likes.text = photo.numberOfLikes.toString()
            likes.setOnClickListener{

                val view : View = LayoutInflater.from(context).inflate(R.layout.fragment_like_section,null)
                loadLikes(view,photo)

                //refreshing the list of likes
                val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayoutLikes)
                refresh.setOnRefreshListener {
                    android.os.Handler(Looper.getMainLooper()).postDelayed({

                        loadLikes(view,photo)
                        refreshPost(itemView,photo)
                        refresh.isRefreshing = false
                    }, 1500)
                }
                val dialog = BottomSheetDialog(activity)
                dialog.setContentView(view)
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                //dialog.behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                dialog.show()

                refreshPost(itemView,photo)
            }

            //list and number of comments
            if(photo.numberOfComments != 0) comments.text="View all ${photo.numberOfComments} comments"
            else comments.text="No comments yet. Add yours?"
            comments.setOnClickListener{
                val view : View = LayoutInflater.from(context).inflate(R.layout.fragment_comment,null)
                loadComments(view,photo)

                //adding a new comment
                val addCommentButton = view.findViewById<ImageView>(R.id.addCommentBtn)
                val addCommentText = view.findViewById<EditText>(R.id.addCommentText)
                addCommentButton.setOnClickListener{
                    if(addCommentText.text.toString()!="")
                    {
                        val ct=addCommentText.text.toString().trim()
                        val newComment = NewCommentDto(photo.id,ct)
                        addNewComment(view,photo,newComment,itemView)
                        refreshPost(itemView,photo)
                    }
                }
                //refreshing the list of comments
                val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayoutComments)
                refresh.setOnRefreshListener {
                    android.os.Handler(Looper.getMainLooper()).postDelayed({

                        loadComments(view,photo)
                        refreshPost(itemView,photo)
                        refresh.isRefreshing = false
                    }, 1500)
                }
                val dialog = BottomSheetDialog(activity)
                dialog.setContentView(view)
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                //dialog.behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                dialog.show()
                refreshPost(itemView,photo)
            }

            //image in the post
            val imagePath=Constants.BASE_URL+"Post/postPhoto/${photo.id}"
            Picasso.get().load(imagePath).into(image);

            //liked or not liked
            if(photo.likedByMe) likedByMe.setBackgroundResource(R.drawable.liked)
            else likedByMe.setBackgroundResource(R.drawable.unliked)
            likedByMe.setOnClickListener{
                likeUnlike(itemView,photo)
                refreshPost(itemView,photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_photopost, parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position],position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPosts(items : List<Photo>)
    {
        dataList=items
        notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long): String {
        val format = SimpleDateFormat("HH:mm  dd/MM/yyyy")
        val tickAtEpoche= 621355968000000000L
        val ticksPerMiliSec = 10000;
        Log.d("time",((time-tickAtEpoche)/ticksPerMiliSec).toString())
        return format.format(Date((time-tickAtEpoche)/ticksPerMiliSec))
    }

    private fun loadComments(view : View, photo : Photo)
    {
        val retrofit = Client(activity).buildService(Api::class.java)
        retrofit.getComments(photo.id).enqueue(object: Callback<DefaultResponse>
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
                    if(commentsList.isNotEmpty()) rvComments.adapter = CommentsAdapter(commentsList,context,activity)
                }
                else
                {
                    Toast.makeText(activity,"Error loading comments",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(activity,"Error loading comments. Something went wrong",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addNewComment(view: View, photo: Photo, newComment : NewCommentDto,itemView: View)
    {
        val retrofit = Client(activity).buildService(Api::class.java)
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
                    itemView.findViewById<TextView>(R.id.postComments).text = "View all ${newNumOfComments} comments"
                    view.findViewById<TextView>(R.id.addCommentText).text=""
                    loadComments(view,photo)
                }
                else
                {
                    Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(activity,"Something else went wrong",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun likeUnlike(itemView : View, photo : Photo)
    {
        val likedByMe = itemView.findViewById<ImageView>(R.id.likeBtn)
        val retrofit = Client(activity).buildService(Api::class.java)
        retrofit.likPost(photo.id).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString()=="false")
                {
                    val state = response.body()?.message.toString()

                    if(state=="liked")
                        likedByMe.setBackgroundResource(R.drawable.liked)

                    else likedByMe.setBackgroundResource(R.drawable.unliked)

                    refreshPost(itemView,photo)
                }
                else
                {
                    Toast.makeText(context,"Unable to like post",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadLikes(view : View, photo : Photo)
    {
        val retrofit = Client(activity).buildService(Api::class.java)
        retrofit.getLikes(photo.id).enqueue(object: Callback<DefaultResponse>
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
                    if(likesList.isNotEmpty()) rvLikes.adapter = LikesAdapter(likesList,context,activity)
                }
                else
                {
                    Toast.makeText(activity,"Error loading likes",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(activity,"Error loading likes. Something went wrong",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun refreshPost(itemView : View, photo : Photo)
    {
        var likes = itemView.findViewById<TextView>(R.id.numOfLikes)
        var comments = itemView.findViewById<TextView>(R.id.postComments)

        val retrofit = Client(activity).buildService(Api::class.java)
        retrofit.refrestPost(photo.id).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
               if(response.body()?.error.toString()=="false")
               {
                   val newStatsStr = response.body()?.message.toString()
                   val newStats : Stats = Gson().fromJson(newStatsStr, Stats :: class.java)

                   likes.text=newStats.numOfLikes.toString()
                   if(newStats.numOfComments.toInt() != 0) comments.text="View all ${newStats.numOfComments} comments"
                   else comments.text="No comments yet. Add yours?"
               }
                else
               {
                   Toast.makeText(activity,"Error refreshing",Toast.LENGTH_SHORT).show()
               }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(activity,"Something went wrong while refreshing",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getUserInfo(username : String, imagePath : String,view : View)
    {
        val retrofit = Client(activity).buildService(Api::class.java)
        val sessionManager = SessionManager(context)
        val appUser=sessionManager.fetchUsername()
        retrofit.fetchUserProfileInfo(username).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false") {
                    //                    Log.d(response.body()?.error.toString(), response.body()?.message.toString());
                    val userProfileInfoStr: String = response.body()?.message.toString();
                    val gson = Gson()
                    val userProfileInfo: UserProfile =
                        gson.fromJson(userProfileInfoStr, UserProfile::class.java)

                    val user = view.findViewById<TextView>(R.id.usernameProfileVisit)
                    val postsNum = view.findViewById<TextView>(R.id.postsNumProfileVisit)
                    val followersNum = view.findViewById<TextView>(R.id.followersNumProfileVisit)
                    val likesNum = view.findViewById<TextView>(R.id.likesNumProfileVisit)
                    val imeprezime = view.findViewById<TextView>(R.id.imeprezimeProfileVisit)
                    val opis = view.findViewById<TextView>(R.id.opisProfileVisit)
                    val pfp = view.findViewById<CircleImageView>(R.id.profilePictureProfileVisit)
                    val follow=view.findViewById<Button>(R.id.followBtnProfileVisit)
                    val message=view.findViewById<Button>(R.id.messageBtnProfileVisit)

                    if(username==appUser)
                    {
                        follow.setVisibility(View.GONE)
                        message.setVisibility(View.GONE);
                    }
                    else
                    {
                        follow.setVisibility(View.VISIBLE);
                        message.setVisibility(View.VISIBLE);
                    }

                    Picasso.get().load(imagePath).into(pfp)
                    user.text = userProfileInfo.username
                    postsNum.text = userProfileInfo.numOfPosts.toString()
                    followersNum.text = userProfileInfo.numOfFollowers.toString()
                    likesNum.text = userProfileInfo.totalNumOfLikes.toString();
                    imeprezime.text = userProfileInfo.name;
                    opis.text = userProfileInfo.description;
                }
                else
                {
                    Toast.makeText(activity,"Unable to get user info",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(activity,"Something went wrong. Try again later",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MM/yyyy  HH:mm")
        return df.parse(date).time
    }
}