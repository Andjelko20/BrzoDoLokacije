package com.example.brzodolokacije.Adapters

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
import java.util.concurrent.Executors


class PostAdapter(val photoList : List<Photo>, val context : Context) :
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
            val image = itemView.findViewById<ImageView>(R.id.postImage)

            owner.text = photo.owner
            owner.setOnClickListener{
                Toast.makeText(context,"Owner: ${photo.owner}",Toast.LENGTH_SHORT).show()
            }
            date.text = photo.date.toString()

            val text= Html.fromHtml("<i>"+photo.location+"</i>")
            location.text = text //=photo.location

            caption.text = photo.caption

            likes.text = photo.numberOfLikes.toString()
            likes.setOnClickListener{
                Toast.makeText(context,"Post ID: ${photo.id} - likes",Toast.LENGTH_SHORT).show()
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
        //image.layoutParams.height=Constants.screenHeight
//        val executor = Executors.newSingleThreadExecutor()
//
//        val handler = android.os.Handler(Looper.getMainLooper())
//
//        var i: Bitmap? = null
//        executor.execute {
//
//            // Image URL
//            val imageURL = path
//            try {
//                val `in` = java.net.URL(imageURL).openStream()
//                i = BitmapFactory.decodeStream(`in`)
//                handler.post {
//                    image.setImageBitmap(i)
//                    //image.layoutParams.height= Constants.screenHeight
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }
}