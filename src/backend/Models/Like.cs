using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace backend.Models;

public class Like
{
    [Key]
    public int Id { get; set; }
    [JsonIgnore]
    public Post Post { get; set; }
    public int PostId { get; set; }
    [JsonIgnore]
    public User User { get; set; }

    public int UserId { get; set; }
}