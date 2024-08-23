package com.melloware.petstore.common.models.json;

import java.time.ZonedDateTime;
import java.util.UUID;

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
public class OrderErrorEmailNotificationRequest {

    @Builder.Default
    private final String type = "error";

    @NotBlank
    private final String customerEmail;

    // Might not have an order number at the time of error
    private final String orderNumber;

    @NotNull
    private final UUID transactionNumber;

    @PastOrPresent
    private final ZonedDateTime orderDate;
}