package com.example.brzodolokacije.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import java.util.concurrent.Executors

class PostAdapter(val photoList : List<Photo>, val context : Context) :
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
            val image = itemView.findViewById<ImageView>(R.id.postImage)

            owner.text = photo.owner
            owner.setOnClickListener{
                Toast.makeText(context,"Owner ID: ${photo.ownerID}",Toast.LENGTH_SHORT).show()
            }
            date.text = photo.dateTime.toString()

            val text= Html.fromHtml("<i>"+photo.location+"</i>")
            location.text = text //=photo.location

            caption.text = photo.caption

            likes.text = photo.numOfLikes.toString()
            likes.setOnClickListener{
                Toast.makeText(context,"Post ID: ${photo.postID} - likes",Toast.LENGTH_SHORT).show()
            }

            itemView.findViewById<ImageView>(R.id.likeBtn).setOnClickListener{
                Toast.makeText(context,"Liked post with ID: ${photo.postID} - likes",Toast.LENGTH_SHORT).show()
            }

            loadImage(image,photo.path)
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
        val executor = Executors.newSingleThreadExecutor()

        val handler = android.os.Handler(Looper.getMainLooper())

        var i: Bitmap? = null
        executor.execute {

            // Image URL
            val imageURL = path
            try {
                val `in` = java.net.URL(imageURL).openStream()
                i = BitmapFactory.decodeStream(`in`)
                handler.post {
                    image.setImageBitmap(i)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}