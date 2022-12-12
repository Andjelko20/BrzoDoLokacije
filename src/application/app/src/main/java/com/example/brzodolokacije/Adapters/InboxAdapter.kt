package com.example.brzodolokacije.Adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Activities.ProfileVisitActivity
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.ModelsDto.InboxDto
import com.example.brzodolokacije.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class InboxAdapter(chatList : MutableList<InboxDto>, val context : Context, val activity : Context):
    RecyclerView.Adapter<InboxAdapter.MainViewHolder>() {

    var dataList = chatList

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun bindData(chat : InboxDto, index: Int) {
            val ownerImage = itemView.findViewById<CircleImageView>(R.id.chatOwnerPhoto)
            val owner = itemView.findViewById<TextView>(R.id.chatOwnerUsername)
            val text = itemView.findViewById<TextView>(R.id.lastMessageText)

            Picasso.get().load(Constants.BASE_URL + "User/avatar/" + chat.convoWith).into(ownerImage)
            owner.text = chat.convoWith
            text.text = chat.MessagePreview

//            owner.setOnClickListener{
//                HomeFragmentState.shouldSave(true)
//                HomeFragmentState.commentsOpened = true
//                HomeFragmentState.lastPosition = index
//                Toast.makeText(activity,index.toString(),Toast.LENGTH_SHORT).show()
//                val intent = Intent(activity, ProfileVisitActivity::class.java)
//                intent.putExtra("visit",comment.owner)
//                intent.putExtra("saveHomeState","saveIt")
//                Handler(Looper.getMainLooper()).postDelayed({
//                    activity.startActivity(intent)
//                }, 30)
//            }
//
//            ownerImage.setOnClickListener{
//                HomeFragmentState.shouldSave(true)
//                HomeFragmentState.commentsOpened = true
//                HomeFragmentState.lastPosition = index
//                val intent = Intent(activity, ProfileVisitActivity::class.java)
//                intent.putExtra("visit",comment.owner)
//                intent.putExtra("saveHomeState","saveIt")
//                Handler(Looper.getMainLooper()).postDelayed({
//                    activity.startActivity(intent)
//                }, 30)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_chat_in_inbox, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}