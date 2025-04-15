package com.smartpos.notification.service;

import com.smartpos.notification.dto.NotificationRequest;
import com.smartpos.notification.entity.Notification;
import com.smartpos.notification.repository.NotificationRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private Configuration freemarkerConfig;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Value("${spring.mail.username}")
    private String sender;
    
    public void sendEmail(NotificationRequest request) {
        try {
            // Process template
            Template template = freemarkerConfig.getTemplate(request.getTemplateName() + ".ftl");
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, 
                    request.getTemplateData() != null ? request.getTemplateData() : new HashMap<>());
            
            // Create notification record
            Notification notification = Notification.builder()
                    .type("email")
                    .recipient(request.getRecipient())
                    .subject(request.getSubject())
                    .content(content)
                    .status("pending")
                    .createdAt(LocalDateTime.now())
                    .build();
            
            notificationRepository.save(notification);
            
            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(sender);
            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject());
            helper.setText(content, true);
            
            mailSender.send(message);
            
            // Update notification record
            notification.setStatus("sent");
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Email sent successfully to {}", request.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    public List<Notification> getPendingEmails() {
        return notificationRepository.findByTypeAndStatusOrderByCreatedAtAsc("email", "pending");
    }
    
    public void processPendingEmails() {
        List<Notification> pendingEmails = getPendingEmails();
        
        for (Notification notification : pendingEmails) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                
                helper.setFrom(sender);
                helper.setTo(notification.getRecipient());
                helper.setSubject(notification.getSubject());
                helper.setText(notification.getContent(), true);
                
                mailSender.send(message);
                
                notification.setStatus("sent");
                notification.setSentAt(LocalDateTime.now());
                
                log.info("Processed pending email: {}", notification.getId());
            } catch (Exception e) {
                notification.setStatus("failed");
                notification.setErrorMessage(e.getMessage());
                
                log.error("Failed to process pending email {}: {}", notification.getId(), e.getMessage());
            }
            
            notificationRepository.save(notification);
        }
    }
}