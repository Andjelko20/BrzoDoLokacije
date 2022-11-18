package com.example.brzodolokacije.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class PostAdapter(val photoList : List<Photo>, val context : Context, val activity : Context) :
    RecyclerView.Adapter<PostAdapter.MainViewHolder>() {

    var dataList = photoList

    inner class MainViewHolder(private val itemView: View) :RecyclerView.ViewHolder(itemView) {

        fun bindData(photo : Photo, index : Int)
        {
            Log.d("slikeA",dataList.toString())
            val owner = itemView.findViewById<TextView>(R.id.postOwner)
            val date = itemView.findViewById<TextView>(R.id.postDate)
            val location = itemView.findViewById<TextView>(R.id.location)
            val caption = itemView.findViewById<TextView>(R.id.postCaption)
            val likes = itemView.findViewById<TextView>(R.id.numOfLikes)
            val comments = itemView.findViewById<TextView>(R.id.postComments)
            val image = itemView.findViewById<ImageView>(R.id.postImage)

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
                val dialog = BottomSheetDialog(activity)
                dialog.setContentView(view)
                //poslati zahtev beku da vrati listu komentara i nekako da je ubacim u recyclerview u modal bottom sheet
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                dialog.show()
            }

            itemView.findViewById<ImageView>(R.id.likeBtn).setOnClickListener{
                Toast.makeText(context,"Liked post with ID: ${photo.id} - likes",Toast.LENGTH_SHORT).show()
            }

            loadImage(image,photo.image)
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

    private fun loadImage(image : ImageView, path : String)
    {
        val imageBytes = Base64.decode(path, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        image.setImageBitmap(decodedImage)
    }

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