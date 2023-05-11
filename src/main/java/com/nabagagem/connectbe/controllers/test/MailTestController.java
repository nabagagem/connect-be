package com.nabagagem.connectbe.controllers.test;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MailTestController {
    private final JavaMailSender javaMailSender;

    @GetMapping("/test/mail")
    public void send() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gemini.richard@gmail.com");
        message.setTo("gemini.richard@gmail.com");
        message.setSubject("test");
        message.setText("test");
        javaMailSender.send(message);
    }
}
