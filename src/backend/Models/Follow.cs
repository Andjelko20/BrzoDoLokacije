using System.ComponentModel.DataAnnotations;
using Newtonsoft.Json;

namespace backend.Models;

public class Follow
{
    [Key] 
    public int Id { get; set; }
    [JsonIgnore]
    public User Follower { get; set; }
    public int FollowerId { get; set; }
    [JsonIgnore]
    public User Followee { get; set; }
    public int FolloweeId { get; set; }
}