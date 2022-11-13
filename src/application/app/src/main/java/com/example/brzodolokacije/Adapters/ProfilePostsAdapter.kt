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
import com.squareup.picasso.Picasso
import java.util.concurrent.Executors

class ProfilePostsAdapter(val postsList : List<String>, val context : Context) : RecyclerView.Adapter<ProfilePostsAdapter.MainViewHolder>()
{
    var dataList = postsList

    inner class MainViewHolder(private val itemView: View) :RecyclerView.ViewHolder(itemView) {

        fun bindData(url : String, index : Int)
        {
            val image = itemView.findViewById<ImageView>(R.id.profilePost)

            Picasso.get().load(url).into(image);

//            loadImage(image, url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_profilepost, parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position],position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private fun loadImage(image : ImageView, path : String)
    {
        //image.layoutParams.height=Constants.screenHeight
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
                    //image.layoutParams.height= Constants.screenHeight
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
