package com.melloware.petstore.order.gateway.ui;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logmanager.MDC;

import com.melloware.petstore.common.models.enums.PaymentType;
import com.melloware.petstore.common.models.json.CreditCardInfo;
import com.melloware.petstore.common.models.json.OrderPurchaseRequest;
import com.melloware.petstore.common.models.json.Product;
import com.melloware.petstore.order.gateway.PurchaseOrderGatewayResource;
import com.melloware.petstore.order.gateway.filters.RequestIdFilters;

import lombok.Data;

/**
 * Represents the view model for handling order-related operations in the UI.
 * This class is responsible for capturing and processing order details entered
 * by users.
 *
 * <p>
 * The class is annotated with:
 * <ul>
 * <li>{@code @Data}: Lombok annotation to automatically generate getters,
 * setters, toString, etc.</li>
 * <li>{@code @Named}: CDI annotation to make this bean eligible for
 * injection.</li>
 * <li>{@code @RequestScoped}: CDI annotation to specify that this bean has a
 * request scope.</li>
 * </ul>
 *
 * <p>
 * It implements {@code Serializable} to support state management across
 * requests if needed.
 */
@Data
@Named
@RequestScoped
public class OrderView implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Inject
        PurchaseOrderGatewayResource purchaseOrderGatewayResource;

        @NotBlank(message = "SKU is required")
        private String sku;

        @NotNull(message = "Quantity is required")
        @Min(1)
        private Integer quantity;

        @NotNull(message = "Price is required")
        @DecimalMin("0.0")
        private BigDecimal price;

        @NotBlank(message = "Card holder name is required")
        private String name;

        private String cardType;

        @NotBlank(message = "Email is required")
        @Email
        private String email;

        @NotBlank(message = "Card number is required")
        private String cardNumber;

        @NotBlank(message = "Expiry date is required")
        @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/YY format")
        private String expiryDate;

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
        private String cvv;

        /**
         * Processes the order by generating a unique request ID, capturing request
         * details,
         * creating an OrderPurchaseRequest, and initiating the purchase workflow.
         * 
         * This method performs the following steps:
         * 1. Generates a unique request ID.
         * 2. Retrieves the client's IP address from the incoming request.
         * 3. Gets the logged-in user (if any).
         * 4. Retrieves the hostname of the server.
         * 5. Adds request ID, IP address, user, and hostname to the MDC context for
         * logging.
         * 6. Creates an OrderPurchaseRequest with customer and product details.
         * 7. Initiates the purchase order workflow.
         * 8. Adds a success message to the FacesContext.
         * 
         * @throws RuntimeException if there's an error processing the order purchase
         *                          request.
         */
        public void order() {
                // Generate a unique request ID
                String requestId = UUID.randomUUID().toString();

                // Retrieve IP address from the incoming request
                final HttpServletRequest httpRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
                                .getExternalContext().getRequest();
                String ipAddress = Objects.toString(httpRequest.getRemoteAddr(), "unknown");

                // Get the logged-in user (if any)
                String loggedInUser = Objects.toString(httpRequest.getRemoteUser(), "anonymous");

                // Get machine name
                String hostName = RequestIdFilters.getHostname();

                // Add request ID and IP address to the MDC context
                MDC.put(RequestIdFilters.REQUEST_ID_MDC_KEY, requestId);
                MDC.put(RequestIdFilters.REQUEST_IP_MDC_KEY, ipAddress);
                MDC.put(RequestIdFilters.REQUEST_USER_MDC_KEY, loggedInUser);
                MDC.put(RequestIdFilters.REQUEST_HOSTNAME_MDC_KEY, hostName);

                // kick off the workflow
                OrderPurchaseRequest request = OrderPurchaseRequest.builder()
                                .customerEmail(email)
                                .creditCard(CreditCardInfo.builder()
                                                .cardNumber(cardNumber)
                                                .cardHolderName(name)
                                                .expiryDate(expiryDate)
                                                .cvv(cvv)
                                                .type(PaymentType.valueOf(StringUtils.upperCase(cardType)))
                                                .build())
                                .products(List.of(Product.builder()
                                                .sku(sku)
                                                .quantity(quantity)
                                                .price(price.doubleValue())
                                                .build()))
                                .build();
                purchaseOrderGatewayResource.purchaseOrder(request);

                String subject = "Your order has been placed! Please check your email for the confirmation and tracking information.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(subject));
        }
}
