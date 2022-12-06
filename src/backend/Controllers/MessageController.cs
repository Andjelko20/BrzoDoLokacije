using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(Roles = "korisnik")]
    public class MessageController : ControllerBase
    {
        private readonly DataContext _context;

        public MessageController(DataContext context)
        {
            _context = context;
        }

        [HttpGet("directMessages/{receiverUsername}")]
        public async Task<ActionResult<string>> GetDirectMessages(string receiverUsername)
        {
            var sender = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            var receiver = await _context.Users.FirstOrDefaultAsync(u => u.Username == receiverUsername);
            if (sender == null || receiver == null)
                return BadRequest(new
                {
                    error = true,
                    message = "Error"
                });
            var messages = await _context.Messages.Where(m => m.SenderId == sender.Id || m.ReceiverId == receiver.Id)
                .OrderBy(m => m.Date).ToListAsync();
            string json = JsonSerializer.Serialize(messages);
            return Ok(new
            {
                error = false,
                message = json
            });
        }
        
    }
}
