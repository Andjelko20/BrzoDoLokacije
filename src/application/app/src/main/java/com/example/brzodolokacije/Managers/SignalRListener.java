package com.example.brzodolokacije.Managers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

public class SignalRListener {
    private static SignalRListener instance;
    HubConnection hubConnection;

    private SignalRListener()
    {
        hubConnection = HubConnectionBuilder.create("http://softeng.pmf.kg.ac.rs:10051/chathub").build();

        hubConnection.on("Logged", (message) -> {
            //
        }, String.class);

        hubConnection.on("ReceiveMessage", (sender , message) -> {
            //logika kad stigne poruka
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
}
