using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
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
            var messages = await _context.Messages.Where(m => (m.SenderId == sender.Id && m.ReceiverId == receiver.Id) || (m.SenderId == receiver.Id && m.ReceiverId == sender.Id))
                .OrderBy(m => m.Date).ToListAsync();
            List<MessageDto> messageDtos = new List<MessageDto>();
            foreach (var message in messages)
            {
                messageDtos.Add(new MessageDto
                {
                    Sender = message.SenderId == sender.Id ? sender.Username : receiver.Username,
                    Text = message.Content
                });
            }
            string json = JsonSerializer.Serialize(messageDtos);
            return Ok(new
            {
                error = false,
                message = json
            });
        }
        
        [HttpGet("myInbox")]
        public async Task<ActionResult<string>> MyInbox()
        {
            var me = await _context.Users.FirstOrDefaultAsync(u => u.Username == User.Identity.Name);
            var messages = await _context.Messages.Where(m => m.SenderId == me.Id || m.ReceiverId == me.Id).OrderByDescending(m=>m.Date).ToListAsync();
            List<InboxDto> messageDtos = new List<InboxDto>();
            foreach (var message in messages)
            {
                messageDtos.Add(new InboxDto()
                {
                    ConvoWith= (message.ReceiverId == me.Id) ? (await _context.Users.FindAsync(message.SenderId)).Username : (await _context.Users.FindAsync(message.ReceiverId)).Username,
                    MessagePreview = message.Content
                });
            }

            messageDtos = messageDtos.GroupBy(m => m.ConvoWith).Select(x => x.First()).ToList();
            string json = JsonSerializer.Serialize(messageDtos);
            return Ok(new
            {
                error = false,
                message = json
            });
        }

    }
}
