package com.example.brzodolokacije.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Posts.Comment
import com.example.brzodolokacije.Posts.Like
import com.example.brzodolokacije.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class LikesAdapter(val likesList : List<Like>, val context : Context, val activity : Context):
    RecyclerView.Adapter<LikesAdapter.MainViewHolder>() {

    var dataList = likesList

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(like: Like, index: Int) {

            val ownerImage = itemView.findViewById<CircleImageView>(R.id.likeOwnerPhoto)
            val owner = itemView.findViewById<TextView>(R.id.likeOwner)

            Picasso.get().load(Constants.BASE_URL + "User/avatar/" + like.owner).into(ownerImage)
            owner.text=like.owner
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_like, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}