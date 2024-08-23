package com.melloware.petstore.warehouse.temporal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;

import com.melloware.petstore.common.models.exceptions.OutOfStockException;
import com.melloware.petstore.common.models.json.CheckInventoryRequest;

import com.melloware.petstore.common.models.json.Product;
import lombok.extern.jbosslog.JBossLog;

/**
 * Warehouse operations
 */
@ApplicationScoped
@JBossLog
public class WarehouseService {

    // Counter for demo ( default to 20 items in stock)
    private AtomicInteger stock = new AtomicInteger(20);

    /**
     * Check inventory
     * 
     * @param request {@link CheckInventoryRequest}
     * @throws {@link OutOfStockException} if anything is out of stock
     */
    public void checkInventory(CheckInventoryRequest request) {
        List<Product> products = request.getProducts();
        log.infof("Checking inventory for %d products", products.size());

        /**
         * Decrement the count and if we are out of stock raise an error.
         */
        for (Product product : products) {
            if (stock.get() < product.getQuantity()) {
                throw new OutOfStockException("Items are out of stock");
            }
            stock.set(stock.get() - product.getQuantity());
        }

        log.infof("All %d products are in stock", products.size());

    }

}