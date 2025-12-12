package fi.hel.allu.servicecore.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Minimal customer details: ID, SAP number and name")
public record CustomerSummaryRecord(
  @Schema(description = "Unique identifier of the customer") Integer customerId,
  @Schema(description = "SAP number of the customer") String sapCustomerNumber,
  @Schema(description = "Name of the customer") String name
) {}
