using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using backend.Models;
using backend.ModelsDto;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

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
            try
            {
                _context.Users.Add(user);
                await _context.SaveChangesAsync();
                return Ok(new
                {
                    error = false,
                    message = "Success"
                });
            }
            catch (Exception e)
            {
                return BadRequest(new
                {
                    error = true,
                    message = "Error occuried"
                });
            }
        }

        [HttpPost("check-email/{email}")]
        public async Task<ActionResult<string>> checkIfEmailExists(string email)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u => u.Email == email);
            if (user == null)
                return Ok(new
                {
                    error = false,
                    message = "false"
                });
            return Ok(new {
                error = false,
                message = "true"
            });
        }
        
        [HttpPost("check-username/{username}")]
        public async Task<ActionResult<string>> checkIfUsernameExists(string username)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
            if (user == null)
                return Ok(new
                {
                    error = false,
                    message = "false"
                });
            return Ok(new {
                error = false,
                message = "true"
            });
        }

        [HttpPost("login")]
        public async Task<ActionResult<string>> login(LoginDto request)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u =>
                u.Username == request.UsernameOrEmail || u.Email == request.UsernameOrEmail);
            if (user == null)
            {
                return Ok(new {
                    error = true,
                    message = "Incorrect username or E-mail"
                });
            }

            if (BCrypt.Net.BCrypt.Verify(request.Password, user.Password)== false)
            {
                return Ok(new {
                    error = true,
                    message = "Incorrect password"
                });
            }
            
            string token = CreateToken(user);
            return Ok(new {
                error = false,
                message = token
            });

        }

        private string CreateToken(User user)
        {
            List<Claim> claims = new List<Claim>
            {
                new Claim(ClaimTypes.Name, user.Username),
                new Claim(ClaimTypes.Role, "korisnik")
            };
            var key = new SymmetricSecurityKey(System.Text.Encoding.UTF8.GetBytes(
                _configuration.GetSection("AppSettings:Token").Value));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha512Signature);
            var token = new JwtSecurityToken(
                claims: claims,
                expires: DateTime.Now.AddDays(1),
                signingCredentials: creds
                );

            var jwt = new JwtSecurityTokenHandler().WriteToken(token);

            return jwt;
        }

        [HttpDelete("delete")]
        public async Task<ActionResult<string>> deleteUser(string username)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u=>u.Username == username);
            if (user == null)
            {
                return NotFound("not found");
            }

            _context.Users.Remove(user);
            _context.SaveChangesAsync();
            return Ok("deleted " + username);
        }

        [HttpGet("getAll")]
        public async Task<ActionResult<List<User>>> getAllUsers()
        {
            return Ok(await _context.Users.ToListAsync());
        }

        [Authorize(Roles = "korisnik")]
        [HttpGet("check-session")]
        public ActionResult<string> checkSession()
        {
            return Ok(new {
                error = false,
                message = "Valid"
            });
        }
    }
}
