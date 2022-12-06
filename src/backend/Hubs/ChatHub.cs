using backend.Models;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using NuGet.Protocol.Plugins;
using Message = backend.Models.Message;

namespace backend.Hubs;

public class ChatHub : Hub
{
    private readonly DataContext _context;
    private static List<UserChat> _connectedUsers = new List<UserChat>();
    public ChatHub(DataContext context)
    {
        _context = context;
    }

    public async override Task OnConnectedAsync()
    {
        await Clients.Caller.SendAsync("Connected", "Prijavljen");
        await base.OnConnectedAsync();
    }

    public override Task OnDisconnectedAsync(Exception? exception)
    {
        var disconnectedUser = _connectedUsers.FirstOrDefault(u => u.ConnetionId == Context.ConnectionId);
        _connectedUsers.Remove(disconnectedUser);
        return base.OnDisconnectedAsync(exception);
    }
    public async Task AddNewConnection(string username)
    {
        var connectedUser = _connectedUsers.FirstOrDefault(u => u.Username == username);
        if(connectedUser == null)
            _connectedUsers.Add(new UserChat
            {
                Username = username,
                ConnetionId = Context.ConnectionId
            });
        else
        {
            _connectedUsers.Remove(connectedUser);
            connectedUser.ConnetionId = Context.ConnectionId;
            _connectedUsers.Add(connectedUser);
        }
        await Clients.Caller.SendAsync("Logged", "Uspesno zabelezen");
    }
    
    public async Task SendPrivateMessage(string sender, string receiver, string message)
    {
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
        string receiverConnection = (_connectedUsers.FirstOrDefault(u => u.Username == receiver)).ConnetionId;
        
        await Clients.Client(receiverConnection).SendAsync("ReceiveMessage",sender,message);
        
        //await Clients.Client(receiver).SendAsync("ReceivedMessage", sender, message);
    }
}