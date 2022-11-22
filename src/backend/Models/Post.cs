using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace backend.Models;

public class Post
{
    [Key]
    public int Id { get; set; }
    public string Location { get; set; } = string.Empty;
    public string Caption { get; set; } = string.Empty;
    public string ImagePath { get; set; } = string.Empty;
    public long Date { get; set; } = DateTime.Now.Ticks;
    [JsonIgnore]
    public User User { get; set; }
    public int UserId { get; set; }
    public List<Like> Likes { get; set; }
    public List<Comment> Commets { get; set; }
}