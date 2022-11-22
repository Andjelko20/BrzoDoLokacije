using backend.Models;

namespace backend.ModelsDto;

public class UserProfileDto
{
    public string Username { get; set; }
    public string Name { get; set; }
    public string Description { get; set; }
    public int Followers { get; set; }
    public int Following { get; set; }
    public int NumberOfLikes { get; set; }
    public int NumberOfPosts { get; set; }
}