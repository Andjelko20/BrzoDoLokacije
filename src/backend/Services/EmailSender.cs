using Microsoft.AspNetCore.Identity.UI.Services;
using System.Net;
using System.Net.Mail;

namespace backend.Services;

public class EmailSender : IEmailSender
{
    public EmailSender() { }
    public async Task SendEmailAsync(string email, string subject, string htmlMessage)
    { // TODO prebaciti ove informacije u config fajl
        string fromMail = "brzodolokacije@gmail.com"; 
        string fromPassword = "xarucsqhtaeomxty";
        MailMessage message = new MailMessage();
        message.From = new MailAddress(fromMail);
        message.Subject = subject;
        message.To.Add(new MailAddress(email));
        message.Body = "<html><body>" + htmlMessage + "</body></html>";
        message.IsBodyHtml = true;
        message.BodyEncoding = System.Text.Encoding.GetEncoding("utf-8");

        var smtpClient = new SmtpClient("smtp.gmail.com")
        {
            Port = 587,
            Credentials = new NetworkCredential(fromMail, fromPassword),
            EnableSsl = true,
        };
        smtpClient.Send(message);
    }
}