using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
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
    public class UserController : ControllerBase
    {
        private readonly DataContext _context;
        private readonly IConfiguration _configuration;

        public UserController(DataContext context,IConfiguration configuration)
        {
            _context = context;
            _configuration = configuration;
        }

        [HttpGet("profileInfo")]
        public async Task<ActionResult<UserProfileDto>> getProfileInfo()
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            UserProfileDto upd = new UserProfileDto
            {
                Username = user.Username,
                Name = user.Name,
                Description = user.Description,
                Avatar = user.Avatar,
                Followers = 100,
                Following = 50,
                NumberOfLikes = 300,
                NumberOfPosts = 6
                //Posts = await _context.Posts.Where(p => p.UserId == user.Id).OrderByDescending(p => p.Date).ToListAsync()
            };
            string json = JsonSerializer.Serialize(upd);
            return Ok(new
            {
                error = false,
                message = json
            });
        }
        
        [HttpDelete("delete/{username}")]
        public async Task<ActionResult<string>> deleteUser(string username)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u=>u.Username == username);
            if (user == null)
            {
                return NotFound(new
                {
                    error = false,
                    message = "User not found"
                });
            }

            _context.Users.Remove(user);
            _context.SaveChangesAsync();
            return Ok(new
            {
                error = false,
                message = "deleted" + username
            });
        }
        
    }
}
