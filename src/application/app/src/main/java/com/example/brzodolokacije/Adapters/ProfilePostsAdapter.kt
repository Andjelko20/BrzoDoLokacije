package com.example.brzodolokacije.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Fragments2.ProfileVisitPostsFragment
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import com.squareup.picasso.Picasso
import java.util.concurrent.Executors

class ProfilePostsAdapter(val postsList: List<String>, val context: Context) : RecyclerView.Adapter<ProfilePostsAdapter.MainViewHolder>()
{
    inner class MainViewHolder(private val itemView: View) :RecyclerView.ViewHolder(itemView) {

        fun bindData(url : String, index : Int)
        {
            val image = itemView.findViewById<ImageView>(R.id.profilePost)

            Picasso.get().load(url).into(image);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_profilepost, parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(postsList[position],position)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }
}
