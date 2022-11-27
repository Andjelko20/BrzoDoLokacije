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
                List<Like> likes = await _context.Likes.Where(l => l.PostId == post.Id).ToListAsync();
                List<Comment> comments = await _context.Comments.Where(c => c.PostId == post.Id).ToListAsync();
                postsDto.Add(new PostDto
                {
                    Id = post.Id,
                    Owner = (await _context.Users.FindAsync(post.UserId)).Username,
                    Date = post.Date,
                    Location = post.Location,
                    Caption = post.Caption,
                    NumberOfLikes = likes.Count,
                    NumberOfComments = comments.Count,
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
        
        [AllowAnonymous]
        [HttpGet("postPhoto/{postId}")]
        public async Task<IActionResult> GetAvatar(int postId)
        {
            var post = await _context.Posts.FindAsync(postId);
            Byte[] b = System.IO.File.ReadAllBytes(post.ImagePath);
            string[] types = post.ImagePath.Split(".");
            string type =types[types.Length-1];
            return File(b, "image/"+type);
        }
        
        [HttpGet("profilePosts/{username}")]
        public async Task<ActionResult<string>> getProfilePosts(string username)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (user == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            var ids = (await _context.Posts.Where(p => p.UserId == user.Id)
                    .OrderByDescending(p => p.Date)
                    .ToListAsync())
                .Select(p=>p.Id).ToList();
            string json = JsonSerializer.Serialize(ids);
            return Ok(new
            {
                error = false,
                message = json
            });
        }
        /*
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
                List<Like> likes = await _context.Likes.Where(l => l.PostId == post.Id).ToListAsync();
                List<Comment> comments = await _context.Comments.Where(c => c.PostId == post.Id).ToListAsync();
                postsDto.Add(new PostDto
                {
                    Owner = username,
                    Date = post.Date,
                    Location = post.Location,
                    Caption = post.Caption,
                    NumberOfLikes = likes.Count,
                    NumberOfComments = comments.Count
                });
            }

            string json = JsonSerializer.Serialize(postsDto);
            return Ok(new
            {
                error = false,
                message = json
            });
        }
        */

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
                message = post.Id.ToString()
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
                int numOfLikes = (await _context.Likes.Where(c => c.PostId == postId).ToListAsync()).Count;
                return Ok(new
                {
                    error = false,
                    message = "unliked"
                });
            }

        }

        [HttpGet("likes/{postId}")]
        public async Task<ActionResult<string>> getLikes(int postId)
        {
            var likes = await _context.Likes.Where(l => l.PostId == postId).ToListAsync();
            List<LikeDto> likesDto = new List<LikeDto>();
            foreach (Like like in likes)
            {
                var user = await _context.Users.FindAsync(like.UserId);
                likesDto.Add(new LikeDto
                {
                    Owner = user.Username
                });
            }
            
            string json = JsonSerializer.Serialize(likesDto);
            return Ok(new
            {
                error = false,
                message = json
            });
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
            int numOfComms = (await _context.Comments.Where(c => c.PostId == request.PostId).ToListAsync()).Count;
            return Ok(new
            {
                error = false,
                message = numOfComms.ToString()
            });
        }

        [HttpGet("comments/{postId}")]
        public async Task<ActionResult<string>> getComments(int postId)
        {
            var comments = await _context.Comments.Where(c => c.PostId == postId).OrderByDescending(c=>c.Date).ToListAsync();
            List<CommentDto> commentsDto = new List<CommentDto>();
            foreach (Comment comment in comments)
            {
                var user = await _context.Users.FindAsync(comment.UserId);
                commentsDto.Add(new CommentDto
                {
                    Id = comment.Id,
                    Content = comment.Content,
                    Owner = user.Username
                });
            }

            string json = JsonSerializer.Serialize(commentsDto);
            return Ok(new
            {
                error = false,
                message = json
            });
        }

        [HttpGet("refreshPost/{postId}")]
        public async Task<ActionResult<string>> refreshLikesComments(int postId)
        {
            var data = new
            {
                numOfLikes = (await _context.Likes.Where(l => l.PostId == postId).ToListAsync()).Count,
                numOfComments = (await _context.Comments.Where(c => c.PostId == postId).ToListAsync()).Count
            };
            string json = JsonSerializer.Serialize(data);
            return Ok(new
            {
                error = false,
                message = json
            });
        }
        
        [HttpPut("uploadPhoto/{postId}")]
        public async Task<ActionResult<string>> uploadPhoto(IFormFile picture, int postId)
        {
            if (picture == null)
                return BadRequest(new
                {
                    error = false,
                    message = "Error"
                });
            var post = await _context.Posts.FirstOrDefaultAsync(p => p.Id == postId);
            if (post == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            string path = CreatePathToDataRoot(post.Id, picture.FileName);
            var stream = new FileStream(path, FileMode.Create);
            await picture.CopyToAsync(stream);
            stream.Close();
            post.ImagePath = path;
            await _context.SaveChangesAsync();
            return Ok(new
            {
                error = false,
                message = path
            });

        }
        private string CreatePathToDataRoot(int postId, string filename)
        {
            var rootDirPath = $"../miscellaneous/posts/{postId}";

            Directory.CreateDirectory(rootDirPath);

            rootDirPath = rootDirPath.Replace(@"\", "/");

            return $"{rootDirPath}/{filename}";
        }

    }
}
