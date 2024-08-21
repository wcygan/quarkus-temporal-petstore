package com.mycompany.order.purchasing.notification;

import io.quarkus.qute.TemplateExtension;

/**
 * Extension to format a double to dollar with 2 decimals
 */
@TemplateExtension
public class CurrencyTemplateExtension {

    static public String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}
