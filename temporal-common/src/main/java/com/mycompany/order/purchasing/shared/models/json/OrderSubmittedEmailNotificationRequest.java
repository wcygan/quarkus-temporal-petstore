package com.mycompany.order.purchasing.shared.models.json;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@ToString
@Jacksonized
public class OrderSubmittedEmailNotificationRequest {

    @Builder.Default
    private final String type = "normal";

    @NotNull
    private final UUID transactionNumber;

    @NotBlank
    @Email
    private final String customerEmail;

    @NotBlank
    private final String orderNumber;

    @PastOrPresent
    private final ZonedDateTime orderDate;

}
