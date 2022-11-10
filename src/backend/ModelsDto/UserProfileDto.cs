using backend.Models;

namespace backend.ModelsDto;

public class UserProfileDto
{
    public string Username { get; set; }
    public string Name { get; set; }
    public string Description { get; set; }
    public string Avatar { get; set; }
    public List<Post> Posts { get; set; }
}