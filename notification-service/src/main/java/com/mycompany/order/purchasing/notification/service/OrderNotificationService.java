package com.mycompany.order.purchasing.notification.service;

import java.util.List;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.order.purchasing.notification.mailer.EmailNotificationRequest;
import com.mycompany.order.purchasing.notification.mailer.EmailService;
import com.mycompany.order.purchasing.shared.models.json.OrderErrorEmailNotificationRequest;
import com.mycompany.order.purchasing.shared.models.json.OrderReceivedEmailNotificationRequest;
import com.mycompany.order.purchasing.shared.models.json.OrderSuccessEmailNotificationRequest;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Handles order notifications (SMTP, etc)
 * 
 
 */
@ApplicationScoped
public class OrderNotificationService {

    @Inject
    EmailService emailService;

    @Inject
    ObjectMapper jacksonMapper;

    /**
     * This provides type safe build time checking of templates so that we are
     * sure we don't miss any variables in our templates
     */
    @CheckedTemplate(basePath = "email/orders")
    static class Templates {

        static native TemplateInstance orderReceived(OrderReceivedEmailNotificationRequest item);

        static native TemplateInstance orderSuccess(OrderSuccessEmailNotificationRequest item);

        static native TemplateInstance orderError(OrderErrorEmailNotificationRequest item);

    }

    @Inject
    Logger log;

    /**
     * Sends an email stating the initial order has been accepted
     *
     * @param request
     */
    @Timed
    @Counted(description = "Counts the number of order received emails sent", value = "service.notification.order.received")
    public void sendOrderReceivedEmail(@Valid @NotNull OrderReceivedEmailNotificationRequest request) {

        // Email setup
        EmailNotificationRequest emailReq = EmailNotificationRequest.builder()
                .subject("Order Received")
                .recipients(List.of(request.getCustomerEmail()))
                .html(true)
                .content(Templates.orderReceived(request).render())
                .build();

        // Send it
        log.infof("Sending order received email to %s with TX id %s", request.getCustomerEmail(),
                request.getTransactionNumber());
        emailService.sendEmail(emailReq);

    }

    /**
     * Sends an email stating there was something wrong during the order process
     *
     * @param request
     */
    @Timed
    @Counted(description = "Counts the number of order error emails sent", value = "service.notification.order.error")
    public void sendOrderErrorEmail(@Valid @NotNull OrderErrorEmailNotificationRequest request) {

        String orderNumber = request.getOrderNumber();
        String subject;
        if (orderNumber == null || orderNumber.isBlank()) {
            subject = "Order Error - Please Contact Support";
        } else {
            subject = "Order Error - Order #%s".formatted(orderNumber);
        }

        // Email setup
        EmailNotificationRequest emailReq = EmailNotificationRequest.builder()
                .subject(subject)
                .recipients(List.of(request.getCustomerEmail()))
                .html(true)
                .content(Templates.orderError(request).render())
                .build();

        // Send it
        log.infof("Sending order error email to %s for order number %s with TX id %s", request.getCustomerEmail(),
                request.getOrderNumber(), request.getTransactionNumber());

        emailService.sendEmail(emailReq);

    }

    /**
     * Sends an email stating there was something wrong during the order process
     *
     * @param request {@link OrderSuccessEmailNotificationRequest}
     */
    @Timed
    @Counted(description = "Counts the number of order completed emails sent attempts", value = "service.notification.order.completed")
    public void sendOrderSuccessEmail(@Valid @NotNull OrderSuccessEmailNotificationRequest request) {

        // Send good case
        EmailNotificationRequest emailReq = EmailNotificationRequest.builder()
                    .subject("Order Completed - Order #%s".formatted(request.getOrderNumber()))
                    .recipients(List.of(request.getCustomerEmail()))
                    .html(true)
                    .content(Templates.orderSuccess(request).render())
                    .build();
        
        // send the mail
        log.infof("Sending order completed email to %s for order number %s with TX id %s", request.getCustomerEmail(),
                request.getOrderNumber(), request.getTransactionNumber());
        
        emailService.sendEmail(emailReq);

    }

}
