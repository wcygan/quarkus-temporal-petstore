package com.melloware.petstore.common.models.json;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 *
 
 */
@Builder
@Getter
@ToString
@Jacksonized
public class WorkflowInitiationResponse {
    private final UUID transactionId;
}