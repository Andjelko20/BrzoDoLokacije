using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;
using backend.Models;
using backend.ModelsDto;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(Roles = "korisnik")]
    public class PostController : ControllerBase
    {
        private readonly DataContext _context;
        private readonly IConfiguration _configuration;

        public PostController(DataContext context, IConfiguration configuration)
        {
            _context = context;
            _configuration = configuration;
        }

        [HttpGet("getAll")]
        public async Task<ActionResult<List<Post>>> getAll()
        {
            var posts = await _context.Posts.OrderByDescending(p => p.Date).ToListAsync();
            var me = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            if (me == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            
            List<PostDto> postsDto = new List<PostDto>();
            foreach (Post post in posts)
            {
                byte[] imageArray = await System.IO.File.ReadAllBytesAsync(post.ImagePath);
                string base64ImageRepresentation = Convert.ToBase64String(imageArray);
                List<Like> likes = await _context.Likes.Where(l => l.PostId == post.Id).ToListAsync();
                postsDto.Add(new PostDto
                {
                    Id = post.Id,
                    Image = base64ImageRepresentation,
                    Owner = (await _context.Users.FindAsync(post.UserId)).Username,
                    Date = post.Date,
                    Location = post.Location,
                    Caption = post.Caption,
                    NumberOfLikes = likes.Count,
                    NumberOfComments = 15,
                    LikedByMe = likes.Exists(l => l.UserId == me.Id)
                });
            }

            string json = JsonSerializer.Serialize(postsDto);
            return Ok(new
            {
                error = false,
                message = json
            });
        }
        
        [HttpGet("getPostsFromUser/{username}")]
        public async Task<ActionResult<List<Post>>> getAll(string username)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            if (user == null)
                return BadRequest(new
                {
                    error = false,
                    message = "User not found"
                });
            var posts = await _context.Posts
                .Where(p => p.UserId == user.Id)
                .OrderByDescending(p => p.Date)
                .ToListAsync();
            List<PostDto> postsDto = new List<PostDto>();
            foreach (Post post in posts)
            {
                byte[] imageArray = await System.IO.File.ReadAllBytesAsync(post.ImagePath);
                string base64ImageRepresentation = Convert.ToBase64String(imageArray);
                postsDto.Add(new PostDto
                {
                    Id = post.Id,
                    Image = base64ImageRepresentation,
                    Owner = username,
                    Date = post.Date,
                    Location = post.Location,
                    Caption = post.Caption,
                    NumberOfLikes = 50,
                    NumberOfComments = 15
                });
            }

            string json = JsonSerializer.Serialize(postsDto);
            return Ok(new
            {
                error = false,
                message = json
            });
        }

        [HttpPost("addNew")]
        public async Task<ActionResult<string>> addNew(AddPostDto request)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            if (user == null)
                return BadRequest(new
                {
                    error = false,
                    message = "User not found"
                });
            Post post = new Post
            {
                Caption = request.Caption,
                Location = request.Location,
                UserId = user.Id,
                User = user
            };
            _context.Posts.Add(post);
            await _context.SaveChangesAsync();
            return Ok(new
            {
                error = false,
                message = "Success"
            });
        }

        [HttpDelete("delete/{id}")]
        public async Task<ActionResult<string>> delete(int id)
        {
            Post post = await _context.Posts.FindAsync(id);
            if (post == null)
                return BadRequest("Post not found");
            _context.Posts.Remove(post);
            await _context.SaveChangesAsync();
            return Ok(new
            {
                error = false,
                message = "Post deleted"
            });
        }

        [HttpPost("like/{postId}")]
        public async Task<ActionResult<string>> likeDislike(int postId)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            var post = await _context.Posts.FindAsync(postId);
            if (user == null || post == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            var like = await _context.Likes.FirstOrDefaultAsync(l => l.UserId == user.Id && l.PostId == postId);
            if (like == null)
            {
                Like l = new Like
                {
                    User = user,
                    UserId = user.Id,
                    Post = post,
                    PostId = postId
                };
                _context.Likes.Add(l);
                await _context.SaveChangesAsync();
                return Ok(new
                {
                    error = false,
                    message = "liked"
                });
            }
            else
            {
                _context.Likes.Remove(like);
                await _context.SaveChangesAsync();
                return Ok(new
                {
                    error = false,
                    message = "unliked"
                });
            }

        }

        [HttpPost("addComment")]
        public async Task<ActionResult<string>> addComment(AddCommentDto request)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            var post = await _context.Posts.FindAsync(request.PostId);
            if (user == null || post == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            Comment comment = new Comment
            {
                Content = request.Content,
                Post = post,
                PostId = request.PostId,
                User = user,
                UserId = user.Id
            };
            _context.Comments.Add(comment);
            await _context.SaveChangesAsync();
            return Ok(new
            {
                error = false,
                message = "Success"
            });
        }

        [HttpGet("comments/{postId}")]
        public async Task<ActionResult<string>> getComments(int postId)
        {
            var comments = await _context.Comments.Where(c => c.PostId == postId).ToListAsync();
            return Ok(new
            {
                error = false,
                message = comments
            });
        }

    }
}
