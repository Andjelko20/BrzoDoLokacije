using backend.Models;
using Microsoft.EntityFrameworkCore;

namespace backend.Data;

public class DataContext : DbContext
{
    public DataContext(DbContextOptions<DataContext> options) : base(options) { }

    public DbSet<User> Users { get; set; }
    public DbSet<Post> Posts { get; set; }
    public DbSet<Like> Likes { get; set; }
    public DbSet<Comment> Comments { get; set; }
    public DbSet<Follow> Follows { get; set; }
    public DbSet<Message> Messages { get; set; }
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.Entity<User>()
            .HasIndex(u => u.Username)
            .IsUnique();
        modelBuilder.Entity<User>()
            .HasIndex(u => u.Email)
            .IsUnique();
        modelBuilder.Entity<Follow>()
            .HasOne(u => u.Followee)
            .WithMany(u => u.Followers)
            .HasForeignKey(u => u.FolloweeId)
            .OnDelete(DeleteBehavior.Restrict);
        modelBuilder.Entity<Follow>()
            .HasOne(u => u.Follower)
            .WithMany(u => u.Followees)
            .HasForeignKey(u => u.FollowerId)
            .OnDelete(DeleteBehavior.Restrict);
        modelBuilder.Entity<Message>()
            .HasOne(u => u.Receiver)
            .WithMany(u => u.Senders)
            .HasForeignKey(u => u.ReceiverId)
            .OnDelete(DeleteBehavior.Restrict);
        modelBuilder.Entity<Message>()
            .HasOne(u => u.Sender)
            .WithMany(u => u.Receivers)
            .HasForeignKey(u => u.SenderId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}