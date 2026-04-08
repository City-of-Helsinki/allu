package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Minimal customer details for the deletable customer list")
public record CustomerSummaryRecord(
  @Schema(description = "Unique identifier of the customer") Integer id,
  @Schema(description = "SAP number of the customer") String sapCustomerNumber,
  @Schema(description = "Name of the customer. Null for private individuals (PERSON type) to protect personal data") String name,
  @Schema(description = "Type of the customer") CustomerType type
) {}
