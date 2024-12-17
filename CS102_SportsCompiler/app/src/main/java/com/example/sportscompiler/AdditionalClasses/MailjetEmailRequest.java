package com.example.sportscompiler.AdditionalClasses;

import java.util.List;

public class MailjetEmailRequest
{
    private List<mailMessage> Messages;

    public MailjetEmailRequest(List<mailMessage> messages) {
        this.Messages = messages;
    }

    public static class mailMessage {
        private EmailAddress From;
        private List<EmailAddress> To;
        private String Subject;
        private String TextPart;
        private String HTMLPart;

        public mailMessage(EmailAddress from, List<EmailAddress> to, String subject, String textPart, String htmlPart) {
            this.From = from;
            this.To = to;
            this.Subject = subject;
            this.TextPart = textPart;
            this.HTMLPart = htmlPart;
        }
    }

    public static class EmailAddress {
        private String Email;
        private String Name;

        public EmailAddress(String email, String name) {
            this.Email = email;
            this.Name = name;
        }
    }
}

