package com.example.brzodolokacije.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Models.DefaultResponse
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
            val likes = itemView.findViewById<TextView>(R.id.numOfLikes)
            val comments = itemView.findViewById<TextView>(R.id.postComments)
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

            comments.text="View all ${photo.numberOfComments} comments"
            comments.setOnClickListener{
                val view : View = LayoutInflater.from(context).inflate(R.layout.fragment_comment,null)

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

                            val dialog = BottomSheetDialog(activity)
                            dialog.setContentView(view)
                            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            //dialog.behavior.peekHeight = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                            dialog.show()
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

            val imagePath=Constants.BASE_URL+"Post/postPhoto/${photo.id}"
            Picasso.get().load(imagePath).into(image);

            if(photo.likedByMe) likedByMe.setBackgroundResource(R.drawable.liked_true)
            else likedByMe.setBackgroundResource(R.drawable.liked_false)
            likedByMe.setOnClickListener{
                Toast.makeText(context,"Liked post with ID: ${photo.id} - likes",Toast.LENGTH_SHORT).show()
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

//    private fun loadImage(image : ImageView, path : String)
//    {
//        val imageBytes = Base64.decode(path, Base64.DEFAULT)
//        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//        image.setImageBitmap(decodedImage)
//    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        Log.d("datum",time.toString())
        val format = SimpleDateFormat("HH:mm  dd/MM/yyyy")
        return format.format(date).dropLast(4)
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