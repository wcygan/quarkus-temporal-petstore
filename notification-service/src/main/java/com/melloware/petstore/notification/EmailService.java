package com.melloware.petstore.notification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.mailer.Attachment;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

/**
 * Base class which handles sending emails.
 */
@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    /**
     * Send the email
     *
     * @param request {@link EmailNotificationRequest} Payload
     */
    public void sendEmail(EmailNotificationRequest request) {

        // Create message
        Mail m = new Mail();
        m.setTo(request.getRecipients());
        m.setSubject(request.getSubject());

        // Is email HTML or RAW?
        if (request.isHtml()) {
            m.setHtml(request.getContent());
        } else {
            m.setText(request.getContent());
        }

        // Headers?
        Map<String, List<String>> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            m.setHeaders(headers);
        }

        // Any attachments?
        List<EmailNotificationRequest.Attachment> attachments = request.getAttachments();

        // Add if found
        if (attachments != null && !attachments.isEmpty()) {
            List<Attachment> atts = attachments
                    .stream()
                    .map(at -> {
                        return new Attachment(at.getFilename(), at.getData(), at.getMimeType());
                    }).collect(Collectors.toList());

            // Set into the email
            if (!atts.isEmpty()) {
                m.setAttachments(atts);
            }
        }

        // send
        mailer.send(m);
    }
}