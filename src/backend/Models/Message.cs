using Newtonsoft.Json;

namespace backend.Models;

public class Message
{
    public int Id { get; set; }
    public string Content { get; set; }
    public long Date { get; set; } = DateTime.Now.Ticks;
    [JsonIgnore]
    public User Sender { get; set; }
    public int SenderId { get; set; }
    [JsonIgnore]
    public User Receiver { get; set; }
    public int ReceiverId { get; set; }
}