package com.mycompany.order.purchasing.shared.models.json;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class OrderReceivedEmailNotificationRequest {

    @Builder.Default
    private final String type = "normal";

    @NotNull
    private final UUID transactionNumber;

    @NotBlank
    @Email
    private final String customerEmail;

    @PastOrPresent
    private final ZonedDateTime orderDate;

    @NotEmpty
    private final List<Product> products;

    private final double orderTotal;

}
