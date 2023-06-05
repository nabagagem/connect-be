package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.NotificationCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class MailGateway implements NotificationGateway {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void send(NotificationCommand notificationCommand, Locale locale) {
        Optional.ofNullable(notificationCommand)
                .map(NotificationCommand::profile)
                .map(ConnectProfile::getPersonalInfo)
                .map(PersonalInfo::getEmail)
                .ifPresentOrElse(email -> {
                    log.info("Sending notification email to {}", email);
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
                    try {
                        message.setFrom("info@ramifica.eu");
                        message.setSubject("Voce recebeu uma mensagem");
                        message.setText(Objects.requireNonNull(notificationCommand).title());
                        mimeMessage.setRecipients(Message.RecipientType.TO, email);

                        final Context ctx = new Context(locale);
                        Optional.ofNullable(Objects.requireNonNull(notificationCommand.profile()).getPersonalInfo())
                                .map(PersonalInfo::getPublicName)
                                .ifPresent(name -> ctx.setVariable("name", name));
                        ctx.setVariable("text", notificationCommand.title());
                        final String htmlContent = this.templateEngine.process(
                                "message",
                                ctx);
                        message.setText(htmlContent, true);

                        javaMailSender.send(mimeMessage);
                        log.info("Notification email sent to {}", email);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }, () -> log.info("There was no email set on the notification command"));
    }
}
