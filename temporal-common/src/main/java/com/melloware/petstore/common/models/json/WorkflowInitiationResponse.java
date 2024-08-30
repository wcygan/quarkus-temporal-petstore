package com.melloware.petstore.common.models.json;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Response object for workflow initiation.
 */
@Builder
@Getter
@ToString
@Jacksonized
@Schema(description = "Response object for workflow initiation")
public class WorkflowInitiationResponse {
    /**
     * The unique identifier for the initiated transaction.
     */
    @Schema(description = "The unique identifier for the initiated transaction", example = "123e4567-e89b-12d3-a456-426614174000")
    private final UUID transactionId;
}