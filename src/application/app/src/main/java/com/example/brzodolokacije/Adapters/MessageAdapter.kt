package com.example.brzodolokacije.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Managers.SignalRListener
import com.example.brzodolokacije.ModelsDto.MessageDto
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R

class MessageAdapter(messageList : MutableList<MessageDto>, val context : Context, val activity : Context):
    RecyclerView.Adapter<MessageAdapter.MainViewHolder>() {
    var dataList = messageList

    private val SENT_MESSAGE = 0
    private val RECEIVED_MESSAGE = 1

    private lateinit var signalRListener : SignalRListener

    inner class MainViewHolder(itemView: View, type : Int) : RecyclerView.ViewHolder(itemView){
        val t = type

        fun bindData(message : MessageDto, index : Int)
        {
            if(t==SENT_MESSAGE)
            {
                val sentMessage= itemView.findViewById<TextView>(R.id.sentMessageText)
                sentMessage.text = message.message
            }
            else if(t==RECEIVED_MESSAGE)
            {
                val receivedMessage = itemView.findViewById<TextView>(R.id.receivedMessageText)
                receivedMessage.text = message.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        if(viewType == SENT_MESSAGE) return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_sentmessage, parent, false), viewType)
        return MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_receivedmessage, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindData(dataList[position],position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        val sessionManager = SessionManager(activity)
        val appOwner = sessionManager.fetchUsername()
        if(dataList.get(position).sender == appOwner) return SENT_MESSAGE
        return RECEIVED_MESSAGE
    }
}