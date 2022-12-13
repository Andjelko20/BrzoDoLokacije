using Microsoft.AspNetCore.Identity.UI.Services;
using MimeKit;
using MimeKit.Text;
using MailKit.Net.Smtp;
using MailKit.Security;

namespace backend.Services;

public class EmailSender : IEmailSender
{
    public EmailSender() { }
    public async Task SendEmailAsync(string email, string subject, string htmlMessage)
    {
        string fromMail = "kakodolokacije@gmail.com"; 
        string fromPassword = "rrwkzlbcxlbnevbi";
        var message = new MimeMessage();
        message.From.Add(MailboxAddress.Parse(fromMail));
        message.Subject = subject;
        message.To.Add(MailboxAddress.Parse(email));
        message.Body = new TextPart(TextFormat.Html) { Text="<html><body>" + htmlMessage + "</body></html>" };
        //message.Body = "<html><body>" + htmlMessage + "</body></html>";
        //message.IsBodyHtml = true;
        //message.BodyEncoding = System.Text.Encoding.GetEncoding("utf-8");


        using var smtp = new SmtpClient();
        smtp.Connect("smtp.gmail.com",587,SecureSocketOptions.StartTls);
        smtp.Authenticate(fromMail,fromPassword);
        smtp.Send(message);
        smtp.Disconnect(true);

        /*var smtpClient = new SmtpClient("smtp.gmail.com")
        {
            Port = 587,
            Credentials = new NetworkCredential(fromMail, fromPassword),
            EnableSsl = true,
        };
        smtpClient.Send(message);*/
    }
}