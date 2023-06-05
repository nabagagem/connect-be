package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@AllArgsConstructor
public class MailGateway implements NotificationGateway {
    private final JavaMailSender javaMailSender;

    @Override
    public void send(NotificationCommand notificationCommand, Locale locale) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gemini.richard@gmail.com");
        message.setTo("gemini.richard@gmail.com");
        message.setSubject("Voce recebeu uma mensagem");
        message.setText(notificationCommand.title().concat(" ").concat(notificationCommand.targetObjectId()));
        javaMailSender.send(message);
    }
}
