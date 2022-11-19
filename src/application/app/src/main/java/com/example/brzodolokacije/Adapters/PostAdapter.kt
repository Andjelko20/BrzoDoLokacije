package com.example.brzodolokacije.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.NewCommentDto
import com.example.brzodolokacije.Posts.Comment
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
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
            val date = itemView.findViewById<TextView>(R.id.postDate)
            val location = itemView.findViewById<TextView>(R.id.location)
            val caption = itemView.findViewById<TextView>(R.id.postCaption)
            var likes = itemView.findViewById<TextView>(R.id.numOfLikes)
            var comments = itemView.findViewById<TextView>(R.id.postComments)
            val image = itemView.findViewById<ImageView>(R.id.postImage)
            val likedByMe = itemView.findViewById<ImageView>(R.id.likeBtn)

            owner.text = photo.owner
            owner.setOnClickListener{
                Toast.makeText(context,"Owner: ${photo.owner}",Toast.LENGTH_SHORT).show()
            }
            date.text = convertLongToTime(photo.date)

            val text= Html.fromHtml("<i>"+photo.location+"</i>")
            location.text = text //=photo.location

            caption.text = Html.fromHtml("<i>"+photo.caption+"</i>")

            likes.text = photo.numberOfLikes.toString()
            likes.setOnClickListener{
                Toast.makeText(context,"Post ID: ${photo.id} - likes",Toast.LENGTH_SHORT).show()
            }

            if(photo.numberOfComments != 0) comments.text="View all ${photo.numberOfComments} comments"
            else comments.text="No comments yet. Add yours?"
            comments.setOnClickListener{
                val view : View = LayoutInflater.from(context).inflate(R.layout.fragment_comment,null)
                loadComments(view,photo)

                val addCommentButton = view.findViewById<ImageView>(R.id.addCommentBtn)
                val addCommentText = view.findViewById<EditText>(R.id.addCommentText)
                addCommentButton.setOnClickListener{
                    if(addCommentText.text.toString()!="")
                    {
                        val ct=addCommentText.text.toString().trim()
                        val newComment = NewCommentDto(photo.id,ct)
                        addNewComment(view,photo,newComment,itemView)
                    }
                }
                val refresh = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
                refresh.setOnRefreshListener {
                    android.os.Handler(Looper.getMainLooper()).postDelayed({

                        loadComments(view,photo)
                        refresh.isRefreshing = false
                    }, 1500)
                }
                val dialog = BottomSheetDialog(activity)
                dialog.setContentView(view)
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                //dialog.behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                dialog.show()
            }

            val imagePath=Constants.BASE_URL+"Post/postPhoto/${photo.id}"
            Picasso.get().load(imagePath).into(image);

            if(photo.likedByMe) likedByMe.setBackgroundResource(R.drawable.liked)
            else likedByMe.setBackgroundResource(R.drawable.unliked)
            likedByMe.setOnClickListener{
                likeUnlike(itemView,photo)
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
        val date = Date(time)
        Log.d("datum",time.toString())
        val format = SimpleDateFormat("HH:mm  dd/MM/yyyy")
        return format.format(date).dropLast(4)
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
                    val newNumOfLikes=response.body()?.message.toString().trim()
                    itemView.findViewById<TextView>(R.id.postComments).text = "View all ${newNumOfLikes} comments"
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
        val likes = itemView.findViewById<TextView>(R.id.numOfLikes)
        val retrofit = Client(activity).buildService(Api::class.java)
        retrofit.likPost(photo.id).enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString()=="false")
                {
                    val string = response.body()?.message.toString()
                    val list: List<String> = string.split(",")
                    //Toast.makeText(context,list.get(1),Toast.LENGTH_SHORT).show()

                    if(list.get(0)=="liked")
                        likedByMe.setBackgroundResource(R.drawable.liked)

                    else likedByMe.setBackgroundResource(R.drawable.unliked)

                    likes.text=list.get(1).toString()
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

    private fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MM/yyyy  HH:mm")
        return df.parse(date).time
    }
}