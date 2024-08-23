package com.melloware.petstore.order.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;

import com.melloware.petstore.order.entity.OrderEntity;

@ApplicationScoped
public class OrderRepository extends CustomIDBaseRepository<OrderEntity, UUID> {

    /**
     * Find by order number with an option to include line items.
     *
     * @param orderNumber The order number to search for.
     * @return The LicenseOrderEntity with or without line items based on the
     *         flag.
     */
    public Optional<OrderEntity> findByOrderNumber(String orderNumber) {
        return find("orderNumber = ?1", orderNumber).firstResultOptional();
    }

    /**
     * Find by transaction id
     *
     * @param transactionId
     * @return
     */
    public Optional<OrderEntity> findByTransactionId(UUID transactionId) {
        return find("transactionId = ?1", transactionId)
                .firstResultOptional();
    }

}