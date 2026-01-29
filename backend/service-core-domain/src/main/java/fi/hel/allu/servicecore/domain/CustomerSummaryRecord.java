package fi.hel.allu.servicecore.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Minimal customer details: customer ID, SAP number and name")
public record CustomerSummaryRecord(
  @Schema(description = "Unique identifier of the customer") Integer id,
  @Schema(description = "SAP number of the customer") String sapCustomerNumber,
  @Schema(description = "Name of the customer") String name
) {}
