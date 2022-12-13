package com.example.brzodolokacije.Managers;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brzodolokacije.Adapters.InboxAdapter;
import com.example.brzodolokacije.Adapters.MessageAdapter;
import com.example.brzodolokacije.ModelsDto.InboxDto;
import com.example.brzodolokacije.ModelsDto.MessageDto;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.LogRecord;

import kotlin.jvm.internal.markers.KMutableList;

public class SignalRListener {
    private static SignalRListener instance;
    HubConnection hubConnection;
    List<MessageDto> listMessages;
    List<InboxDto> listInbox;
    RecyclerView recyclerView;
    FragmentActivity activity;
    Context context;
    MessageAdapter adapter;
    InboxAdapter adapterInbox;
    Boolean directMessage = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private SignalRListener()
    {
        hubConnection = HubConnectionBuilder.create("http://softeng.pmf.kg.ac.rs:10051/chathub").build();
        hubConnection.on("Logged", (message) -> {
            //
        }, String.class);

        hubConnection.on("ReceiveMessage", (sender , message) -> {
            if(directMessage == true)
            {
                MessageDto newMessage=new MessageDto(sender,message);
                listMessages.add(newMessage);
                recyclerView.post(new Runnable(){ @Override public void run(){
                    adapter.notifyItemInserted(listMessages.size()-1);
                    recyclerView.stopScroll();
                    ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).scrollToPositionWithOffset(listMessages.size()-1,0);
                }
                });
            }
            else
            {
                InboxDto newInbox = new InboxDto(sender,message);
                InboxDto old = listInbox.stream().filter(t -> t.getConvoWith().contains(sender)).findFirst().orElse(null);
                recyclerView.post(new Runnable(){ @Override public void run(){
                    if(old != null)
                    {
                        int index = listInbox.indexOf(old);
                        listInbox.remove(old);
                        adapterInbox.notifyItemRemoved(index);
                    }
                    listInbox.add(0,newInbox);
                    adapterInbox.notifyItemInserted(0);
                }
                });
            }
        }, String.class,String.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
            MessageDto newMessage=new MessageDto(sender,message);
            listMessages.add(newMessage);
            adapter.notifyItemInserted(listMessages.size()-1);
            ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).scrollToPosition(listMessages.size()-1);
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

    public void setRecycleView(RecyclerView rv)
    {
        recyclerView=rv;
    }

    public void setContext(Context context) {
        this.context=context;
    }

    public void setList(List<MessageDto> messageList) {
        listMessages=messageList;
        adapter=new MessageAdapter(listMessages,context,activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        if(listMessages.size() > 0)
        {
            ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).scrollToPosition(listMessages.size()-1);
        }
    }

    public void setListInbox(List<InboxDto> inboxList) {
        listInbox=inboxList;
        adapterInbox = new InboxAdapter(listInbox,context,activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapterInbox);
    }

    public void setActivity(FragmentActivity requireActivity) {
        activity=requireActivity;
    }

    public void setDirectMessage(Boolean status)
    {
        directMessage = status;
    }
}
