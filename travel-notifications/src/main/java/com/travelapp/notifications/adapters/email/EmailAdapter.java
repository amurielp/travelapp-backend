package com.travelapp.notifications.adapters.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class EmailAdapter {

    private final JavaMailSender mailSender;

    public EmailAdapter(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender.orElse(null);
    }

    public void send(String to, String subject, String templateId, Map<String, Object> vars) {
        if (mailSender == null) {
            log.debug("email.noop — mail not configured. subject={}", subject);
            return;
        }
        try {
            var helper = new MimeMessageHelper(mailSender.createMimeMessage(), true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@travelapp.com");
            helper.setText("<html><body>" + subject + "</body></html>", true);
            mailSender.send(helper.getMimeMessage());
            log.info("email.sent to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("email.failed to={} subject={} error={}", to, subject, e.getMessage());
        }
    }
}
