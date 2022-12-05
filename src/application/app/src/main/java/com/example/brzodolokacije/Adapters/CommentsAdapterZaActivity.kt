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
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Posts.Comment
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class CommentsAdapterZaActivity(commentList : List<Comment>, val context : Context) :
    RecyclerView.Adapter<CommentsAdapterZaActivity.MainViewHolder>() {

    var dataList = commentList

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(comment: Comment, index: Int) {

            val ownerImage = itemView.findViewById<CircleImageView>(R.id.commentOwnerPhoto)
            val owner = itemView.findViewById<TextView>(R.id.commentOwner)
            val text = itemView.findViewById<TextView>(R.id.commentText)

            Picasso.get().load(Constants.BASE_URL + "User/avatar/" + comment.owner).into(ownerImage)
            owner.text=comment.owner
            text.text=comment.content
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsAdapterZaActivity.MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_comment, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position], position)
    }
}