package com.mycompany.order.purchasing.temporal;

import com.mycompany.order.purchasing.service.WarehouseService;
import com.mycompany.order.purchasing.shared.activities.warehouse.WarehouseActivities;
import com.mycompany.order.purchasing.shared.models.exceptions.OutOfStockException;
import com.mycompany.order.purchasing.shared.models.json.CheckInventoryRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Implementation of the Warehouse Activity
 *
 
 */
@ApplicationScoped
public class WarehouseActivitiesImpl implements WarehouseActivities {

    @Inject
    WarehouseService service;

    /**
     * Checks inventory
     * 
     * @param request {@link CheckInventoryRequest}
     * @throws {@link OutOfStockException} if out of stock
     */
    @Override
    public void checkInventory(CheckInventoryRequest request) {
        
        /**
         * This is where you'd do your inventory logic
         * 
         * I pass in a list of Products to check.
         * 
         * If anything is out of stock you should throw an OutOfStockException
         * and then make sure the Temporal workflow doesn't try to retry the
         * activity since we want to fail the workflow quick
         */
        service.checkInventory(request);
        
    }
    
    
   
}
