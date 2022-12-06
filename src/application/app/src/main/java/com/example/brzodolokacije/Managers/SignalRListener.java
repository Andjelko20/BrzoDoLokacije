package com.example.brzodolokacije.Managers;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

public class SignalRListener {
    private static SignalRListener instance;
    HubConnection hubConnection;

    private SignalRListener(Activity activity)
    {
        hubConnection = HubConnectionBuilder.create("http://softeng.pmf.kg.ac.rs:10051/chathub").build();

        hubConnection.on("Logged", (message) -> {
            Toast.makeText(activity, "Post uploaded", Toast.LENGTH_SHORT).show();
        }, String.class);
    }

    public static SignalRListener getInstance(Activity activity) {
        if(instance == null)
            instance=new SignalRListener(activity);
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
