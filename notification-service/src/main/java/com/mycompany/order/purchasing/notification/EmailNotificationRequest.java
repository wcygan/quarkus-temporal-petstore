package com.mycompany.order.purchasing.notification;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Email notification request object used to send emails.
 * <p>
 * Supports the Quarkus Mailer send method parameters.
 */
@Builder
@Getter
@ToString
@Jacksonized
public class EmailNotificationRequest {

    @NotEmpty
    private final List<@Email String> recipients;

    @NotBlank
    private final String subject;

    @NotBlank
    private final String content;

    private final List<@Valid Attachment> attachments;
    private final Map<String, List<String>> headers;
    private final List<@Email String> bccs;
    private final List<@Email String> ccs;
    private final String bounceAddress;
    private final String replyTo;
    private final boolean html;

    @Builder
    @Getter
    @ToString
    @Jacksonized
    public static class Attachment {

        @NotBlank
        private final String filename;

        @NotBlank
        private final String mimeType;

        @NotEmpty
        private final byte[] data;
    }
}
