package com.example.brzodolokacije.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Activities.ProfileVisitActivity
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.NewCommentDto
import com.example.brzodolokacije.Posts.*
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


class PostAdapter(val photoList: MutableList<Photo?>, val context: Context, val activity: Context) :
    RecyclerView.Adapter<PostAdapter.MainViewHolder>() {

    var dataList : MutableList<Photo?> = photoList
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    inner class MainViewHolder(private val itemView: View, type : Int) :RecyclerView.ViewHolder(itemView) {
        val t = type

        fun bindData(p : Photo?, index : Int)
        {
            if(t == VIEW_TYPE_ITEM)
            {
                val photo = p!!

                val owner = itemView.findViewById<TextView>(R.id.postOwner)
                val profilePic = itemView.findViewById<CircleImageView>(R.id.userProfilePic)
                val ownerProfile = itemView.findViewById<ConstraintLayout>(R.id.ownerProfile)
                val date = itemView.findViewById<TextView>(R.id.postDate)
                val location = itemView.findViewById<TextView>(R.id.location)
                val caption = itemView.findViewById<TextView>(R.id.postCaption)
                val likes = itemView.findViewById<TextView>(R.id.numOfLikes)
                val comments = itemView.findViewById<TextView>(R.id.postComments)
                val image = itemView.findViewById<ImageView>(R.id.postImage)
                val likedByMe = itemView.findViewById<ImageView>(R.id.likeBtn)

                //owner
                owner.text = photo.owner

                //profile image
                val path : String=Constants.BASE_URL + "User/avatar/" + photo.owner
                Picasso.get().load(path).into(profilePic);

                //for visiting post owner's profile
                ownerProfile.setOnClickListener{

                    //HomeFragmentState.setVisit(photo.owner)
                    HomeFragmentState.shouldSave(true)
                    val intent = Intent(activity,ProfileVisitActivity::class.java)
                    intent.putExtra("visit",photo.owner)
                    Handler(Looper.getMainLooper()).postDelayed({
                        activity.startActivity(intent)
                    }, 30)
                }

                //date
                date.text = convertLongToTime(photo.date)

                //location
                val text= photo.location //Html.fromHtml("<i>"+photo.location+"</i>")
                location.text = text //=photo.location

                //caption
                if(photo.caption == "")
                {
                    caption.setVisibility(View.GONE)
                }
                else
                {
                    caption.setVisibility(View.VISIBLE)
                    caption.text = photo.caption
                } //Html.fromHtml("<i>"+photo.caption+"</i>")

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
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        if(viewType == VIEW_TYPE_LOADING) return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.homepage_item_load, parent, false), viewType)
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_photopost, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position],position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        if(dataList.get(position)==null) return VIEW_TYPE_LOADING
        return VIEW_TYPE_ITEM
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long): String {
        val format = SimpleDateFormat("HH:mm  dd/MM/yyyy", Locale("Serbia"))
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        val tickAtEpoche= 621355968000000000L
        val ticksPerMiliSec = 10000
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
                    refreshPost(itemView,photo)
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
                    {
                        likedByMe.setBackgroundResource(R.drawable.liked)
                        photo.likedByMe = true
                    }

                    else
                    {
                        likedByMe.setBackgroundResource(R.drawable.unliked)
                        photo.likedByMe = false
                    }

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
                   photo.numberOfComments = newStats.numOfComments.toInt()
                   photo.numberOfLikes = newStats.numOfLikes.toInt()
                   HomeFragmentState.list(dataList)
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
    private fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MM/yyyy  HH:mm")
        return df.parse(date).time
    }
}