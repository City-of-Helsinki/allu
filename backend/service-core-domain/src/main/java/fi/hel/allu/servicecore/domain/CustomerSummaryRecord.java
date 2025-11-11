package fi.hel.allu.servicecore.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Minimal customer details: ID and name")
public record CustomerSummaryRecord(
  @Schema(description = "Unique identifier of the customer") Long id,
  @Schema(description = "Name of the customer") String name
) {}
