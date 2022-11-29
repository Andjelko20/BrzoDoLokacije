using Microsoft.AspNetCore.SignalR;

namespace backend.Hubs;

public class ChatHub : Hub
{
    public override async Task OnConnectedAsync()
    {
        await Groups.AddToGroupAsync(Context.ConnectionId, Context.User.Identity.Name);
        await base.OnConnectedAsync();
    }
    
    public async Task SendMessage(string sender,string receiver,string message)
    {
        await Clients.Group(receiver).SendAsync("ReceiveMessage", sender, message);
    }
}