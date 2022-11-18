using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace backend.Models;

public class Comment
{
    [Key]
    public int Id { get; set; }
    public string Content { get; set; }
    public long Date { get; set; } = DateTime.Now.Ticks;
    [JsonIgnore]
    public User User { get; set; }
    public int UserId { get; set; }
    [JsonIgnore]
    public Post Post { get; set; }
    public int PostId { get; set; }
}