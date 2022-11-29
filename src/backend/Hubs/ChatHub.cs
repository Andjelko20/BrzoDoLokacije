using Microsoft.AspNetCore.SignalR;

namespace backend.Hubs;

public class ChatHub : Hub
{
    public async Task Send(string message)
    {
        await Clients.All.SendAsync("ReceivedMessage", message);
        //await Clients.Client(receiver).SendAsync("ReceivedMessage", sender, message);
    }
}