package com.nabagagem.connectbe.services.notifications;

import com.nabagagem.connectbe.domain.notification.NotificationCommand;
import com.nabagagem.connectbe.domain.profile.ProfileMailPersonalInfo;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.PersonalInfo;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
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
@RequiredArgsConstructor
@ConditionalOnProperty("ramifica.mail.enabled")
public class MailGateway implements NotificationGateway {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Override
    public void send(NotificationCommand notificationCommand, Locale locale) {
        if (notificationCommand.action() == Action.CREATED
                && notificationCommand.payload() instanceof Message) {
            sendMessage(notificationCommand, locale);
        }
    }

    private void sendMessage(NotificationCommand notificationCommand, Locale locale) {
        Optional.ofNullable(notificationCommand.profile())
                .filter(profile -> Optional.ofNullable(profile.getPersonalInfo())
                        .map(PersonalInfo::getEnableMessageEmail)
                        .filter(enabled -> enabled)
                        .orElse(false))
                .map(ConnectProfile::getPersonalInfo)
                .map(PersonalInfo::getEmail)
                .ifPresentOrElse(email -> {
                    log.info("Sending notification email to {}", email);
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
                    try {
                        message.setFrom("no-reply@ramifica.eu");
                        message.setSubject(messageSource.getMessage("mail_message_title", null, locale));
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
                        log.warn("Failed to send email message to: {}", email, e);
                    }
                }, () -> log.info("There was no email set on the notification command"));
    }

    @SneakyThrows
    public void sendUnreadEmailNotification(ProfileMailPersonalInfo profile) {
        String email = profile.getPersonalInfo().getEmail();
        log.info("User {} has unread messages", email);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            message.setFrom("no-reply@ramifica.eu", "Ramifica");
            Locale locale = Locale.forLanguageTag("pt-BR");
            message.setSubject(messageSource.getMessage("unread_messages_title", null, locale));
            String text = messageSource.getMessage("unread_messages_text", null, locale);
            message.setText(text);
            mimeMessage.setRecipients(Message.RecipientType.TO, email);

            final Context ctx = new Context(locale);
            Optional.ofNullable(profile.getPersonalInfo())
                    .map(ProfileMailPersonalInfo.PersonalInfoInfo::getPublicName)
                    .ifPresent(name -> ctx.setVariable("name", name));
            ctx.setVariable("text", text);
            final String htmlContent = this.templateEngine.process(
                    "message",
                    ctx);
            message.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Notification email sent to {}", email);
        } catch (MessagingException e) {
            log.warn("Failed to send email message to: {}", email, e);
        }
    }
}
