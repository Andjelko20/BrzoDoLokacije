using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using backend.Models;
using backend.ModelsDto;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly UserContext _context;
        private readonly IConfiguration _configuration;
        
        public AuthController(UserContext context, IConfiguration configuration)
        {
            _context = context;
            _configuration = configuration;
        }

        [HttpPost("register")]
        public async Task<ActionResult<string>> register(RegisterDto request)
        {
            User user = new User();
            user.Email = request.Email;
            user.Username = request.Username;
            user.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);
            _context.Users.Add(user);
            await _context.SaveChangesAsync();
            return Ok("Done");
        }

        [HttpPost("login")]
        public async Task<ActionResult<string>> login(LoginDto request)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u =>
                u.Username == request.UsernameOrEmail || u.Email == request.UsernameOrEmail);
            if (user == null)
            {
                return NotFound("not exist");
            }

            if (BCrypt.Net.BCrypt.Verify(request.Password, user.Password))
            {
                return Ok("succesful login");
            }
            else
            {
                return BadRequest("wrong password");
            }
        }

        [HttpGet]
        public async Task<ActionResult<List<User>>> getAllUsers()
        {
            return Ok(await _context.Users.ToListAsync());
        }
    }
}
