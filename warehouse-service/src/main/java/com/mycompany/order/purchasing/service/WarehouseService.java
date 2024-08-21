package com.mycompany.order.purchasing.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;

import com.mycompany.order.purchasing.shared.models.exceptions.OutOfStockException;
import com.mycompany.order.purchasing.shared.models.json.CheckInventoryRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Warehouse operations
 * 
 
 */
@ApplicationScoped
public class WarehouseService {

    @Inject
    Logger log;
    
    // Counter for demo
    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * Check inventory
     * 
     * @param request {@link CheckInventoryRequest}
     * @throws {@link OutOfStockException} if anything is out of stock
     */
    public void checkInventory(CheckInventoryRequest request) {
        log.infof("Checking inventory for %d products", request.getProducts().size());
        
        /**
         * Maybe for the demo we can randomly throw the OutofStock exception
         * so we can simulate and show error failures
         * 
         * Every even request cause an error
         */
        if (counter.incrementAndGet() % 2 == 0) {
            throw new OutOfStockException("Items are out of stock");
        }
        
        log.infof("All %d products are in stock", request.getProducts().size());
        
    }

    
}
