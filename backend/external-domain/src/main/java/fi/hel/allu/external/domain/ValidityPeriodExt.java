package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

@NotFalse(rules = {"validityPeriodStart, periodStartBeforePeriodEnd, {validityperiod.start}"})
@Schema(description = "Application validity period (voimassaolo)")
public class ValidityPeriodExt {

  private ZonedDateTime validityPeriodStart;
  private ZonedDateTime validityPeriodEnd;

  @Schema(description = "Start date of the validity period")
  public ZonedDateTime getValidityPeriodStart() {
    return validityPeriodStart;
  }

  public void setValidityPeriodStart(ZonedDateTime validityPeriodStart) {
    this.validityPeriodStart = validityPeriodStart;
  }

  @Schema(description = "End date of the validity period")
  public ZonedDateTime getValidityPeriodEnd() {
    return validityPeriodEnd;
  }

  public void setValidityPeriodEnd(ZonedDateTime validityPeriodEnd) {
    this.validityPeriodEnd = validityPeriodEnd;
  }

  @JsonIgnore
  public boolean getPeriodStartBeforePeriodEnd() {
    if (validityPeriodStart != null && validityPeriodEnd != null) {
      return !validityPeriodStart.isAfter(validityPeriodEnd);
    }
    return true;
  }
}
