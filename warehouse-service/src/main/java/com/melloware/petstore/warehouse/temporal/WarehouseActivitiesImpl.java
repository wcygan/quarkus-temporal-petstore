package com.melloware.petstore.warehouse.temporal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.melloware.petstore.warehouse.service.WarehouseService;
import com.melloware.petstore.common.activities.warehouse.WarehouseActivities;
import com.melloware.petstore.common.models.exceptions.OutOfStockException;
import com.melloware.petstore.common.models.json.CheckInventoryRequest;

/**
 * Implementation of the Warehouse Activity.
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