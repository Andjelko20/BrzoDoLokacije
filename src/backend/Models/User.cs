using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;

namespace backend.Models;

public class User
{
    [Key]
    public int Id { get; set; }
    [Required]
    public string Username { get; set; }
    [Required]
    public string Email { get; set; }
    [Required]
    public string Password { get; set; }
    public string PasswordResetToken { get; set; } = string.Empty;
    public string Name { get; set; } = "Milos Andjelkovic";
    public string Description { get; set; } = "Are you lost, babygirl?";
    public string Avatar { get; set; } = "../miscellaneous/avatars/default.png";
    public bool HasAvatar { get; set; } = false;
    public List<Post> Posts { get; set; }
    public List<Like> Likes { get; set; }
}