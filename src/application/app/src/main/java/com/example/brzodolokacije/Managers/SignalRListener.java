package com.example.brzodolokacije.Managers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.brzodolokacije.Adapters.MessageAdapter;
import com.example.brzodolokacije.ModelsDto.MessageDto;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.List;

import kotlin.jvm.internal.markers.KMutableList;

public class SignalRListener {
    private static SignalRListener instance;
    HubConnection hubConnection;
    List<MessageDto> listMessages;
    RecyclerView.Adapter<MessageAdapter.MainViewHolder> adapter;

    private SignalRListener()
    {
        hubConnection = HubConnectionBuilder.create("http://softeng.pmf.kg.ac.rs:10051/chathub").build();

        hubConnection.on("Logged", (message) -> {
            //
        }, String.class);

        hubConnection.on("ReceiveMessage", (sender , message) -> {
            MessageDto newMessage=new MessageDto(sender,message);
            listMessages.add(newMessage);
            adapter.notifyItemInserted(listMessages.size()-1);
        }, String.class,String.class);
    }

    public static SignalRListener getInstance() {
        if(instance == null)
            instance=new SignalRListener();
        return instance;
    }

    public boolean startConnection()
    {
        if(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
        {
            hubConnection.start().blockingAwait();
            return true;
        }
        return false;
    }

    public void registerMe(String username)
    {
        if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("AddNewConnection", username);
        }
    }

    public void sendMessage(String sender, String receiver, String message)
    {
        if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            hubConnection.send("SendPrivateMessage", sender,receiver,message);
        }
    }

    public boolean stopConnection()
    {
        if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
        {
            hubConnection.stop();
            return true;
        }
        return false;
    }

    public void setListMessage(List<MessageDto> list)
    {
        listMessages=list;
    }

    public void setMessageAdapter(RecyclerView.Adapter<MessageAdapter.MainViewHolder> adapter)
    {
        this.adapter=adapter;
    }
}
