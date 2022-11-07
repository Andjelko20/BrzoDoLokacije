using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.StaticFiles;

namespace backend.Controllers
{
    [Route("")]
    [ApiController]
    public class DownloadController : ControllerBase
    {
        [HttpGet]
        public ContentResult Index()
        {
            var html = System.IO.File.ReadAllText(@"./Assets/index.html");
            return base.Content(html, "text/html");
        }

        [HttpPost("download")]
        public async Task<ActionResult> downloadApp([FromForm]string password)
        {
            if(password == "123passwd123")
            {
                string filePath = @"../downloadable/brzodolokacije.apk";
                var provider = new FileExtensionContentTypeProvider();
                if (!provider.TryGetContentType(filePath, out var contentType))
                {
                    contentType = "application/octet-stream";
                }
    
                var bytes = await System.IO.File.ReadAllBytesAsync(filePath);
                return File(bytes, contentType, Path.GetFileName(filePath));
            }

            return BadRequest("Pogresna sifra");
        }
    }
}
