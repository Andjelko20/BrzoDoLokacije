using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using NuGet.Protocol.Plugins;
using Message = backend.Models.Message;

namespace backend.Hubs;

public class ChatHub : Hub
{
    private readonly DataContext _context;

    public ChatHub(DataContext context)
    {
        _context = context;
    }

    public async override Task OnConnectedAsync()
    {
        await Clients.All.SendAsync("Prijavljen", "Prijavljen");
        await base.OnConnectedAsync();
    }

    public async Task SendPrivateMessage(string sender, string receiver, string message)
    {
        await Clients.All.SendAsync("ReceiveMessage",message);
        var senderDb = await _context.Users.FirstOrDefaultAsync(u => u.Username == sender);
        var receiverDb = await _context.Users.FirstOrDefaultAsync(u => u.Username == receiver);
        var messageDb = new Message
        {
            Content = message,
            Sender = senderDb,
            Receiver = receiverDb,
            SenderId = senderDb.Id,
            ReceiverId = receiverDb.Id
        };
        _context.Messages.Add(messageDb);
        await _context.SaveChangesAsync();
        //await Clients.Client(receiver).SendAsync("ReceivedMessage", sender, message);
    }
}