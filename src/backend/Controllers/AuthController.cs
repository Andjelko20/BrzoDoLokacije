using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Security.Cryptography;
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
        private readonly DataContext _context;
        private readonly IConfiguration _configuration;

        public AuthController(DataContext context, IConfiguration configuration)
        {
            _context = context;
            _configuration = configuration;
        }
        
        Services.EmailSender _emailSender = new Services.EmailSender();

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
                string token = CreateToken(user);
                return Ok(new {
                    error = false,
                    message = token
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
            return Ok(new
            {
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

        [HttpPost("reset-password/{email}")]
        public async Task<ActionResult<string>> sendForgotPasswdEmail(string email)
        {
            User user = _context.Users.FirstOrDefault(x => x.Email == email);
            if (user == null)
            {
                return Ok(new
                {
                    error = true,
                    message = "User with that email not exists"
                });
            }
            else
            {
                string token = CreateRandomToken();
                var userDB = await _context.Users.FirstOrDefaultAsync(u => u.PasswordResetToken == token);
                while (userDB != null)
                {
                    token = CreateRandomToken();
                    userDB = await _context.Users.FirstOrDefaultAsync(u => u.PasswordResetToken == token);
                }
                user.PasswordResetToken = token;
                await _context.SaveChangesAsync();
                string message = @"Hello, <b>" + user.Username 
                                               + @"</b>.<br> Reset your password 
                                    <a href='http://brzodolokacije.reset_password/"+user.PasswordResetToken
                                               + @"'>here</a>.";

                await _emailSender.SendEmailAsync(user.Email, "Reset Password", message);

                return Ok(new
                {
                    error = false,
                    message = "Success"
                });
            }
        }
        
        [HttpPut("reset-password")]
        public async Task<ActionResult<string>> sendForgotPasswdDto(ResetPasswordDto request)
        {
            User userDB = _context.Users.FirstOrDefault(x => x.Username == request.Username);
            if (userDB == null)
            {
                return Ok(new
                {
                    error = true,
                    message = "User with that username not exists"
                });
            }
            else
            {
                userDB.PasswordResetToken = string.Empty;
                userDB.Password=BCrypt.Net.BCrypt.HashPassword(request.Password);
                await _context.SaveChangesAsync();
                return Ok(new
                {
                    error = false,
                    message = "Password changed successfully"
                });
            }
        }
        
        [HttpPost("check-token/{token}")]
        public async Task<ActionResult<string>> checkEmailToken(string token)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.PasswordResetToken == token);
            if (user == null)
            {
                return Ok(new
                {
                    error = true,
                    message = "Token not valid"
                });
            }
            else
            {
                return Ok(new
                {
                    error = false,
                    message = user.Username
                });
            }
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

        [Authorize(Roles = "korisnik")]
        [HttpPut("change-password")]
        public async Task<ActionResult<string>> changePassword(ChangePasswordDto request)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            if (user == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            if (BCrypt.Net.BCrypt.Verify(request.CurrentPassword, user.Password) == false)
                return Ok(new
                {
                    error = true,
                    message = "Current password is not correct"
                });
            user.Password = BCrypt.Net.BCrypt.HashPassword(request.NewPassword);
            await _context.SaveChangesAsync();
            return Ok(new
            {
                error = false,
                message = "Success"
            });
        }
        
        [Authorize(Roles = "korisnik")]
        [HttpGet("check-password")]
        public async Task<ActionResult<string>> checkPassword(PasswordDto request)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            if (user == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            if (BCrypt.Net.BCrypt.Verify(request.Password, user.Password) == false)
                return Ok(new
                {
                    error = true,
                    message = "Current password is not correct"
                });
            return Ok(new
            {
                error = false,
                message = "Success"
            });
        }


        [Authorize(Roles = "korisnik")]
        [HttpGet("check-session")]
        public async Task<ActionResult<string>> checkSession()
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            if (user == null)
                return Unauthorized();
            return Ok(new {
                error = false,
                message = User?.Identity?.Name
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
                expires: DateTime.Now.AddDays(30),
                signingCredentials: creds
                );

            var jwt = new JwtSecurityTokenHandler().WriteToken(token);

            return jwt;
        }
        
        private string CreateRandomToken()
        {
            return Convert.ToHexString(RandomNumberGenerator.GetBytes(64));
        }
    }
}
