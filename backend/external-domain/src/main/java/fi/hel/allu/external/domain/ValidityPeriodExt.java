package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NotFalse(rules = {"validityPeriodStart, periodStartBeforePeriodEnd, {validityperiod.start}"})
@ApiModel(value = "Application validity period (voimassaolo)")
public class ValidityPeriodExt {

  private ZonedDateTime validityPeriodStart;
  private ZonedDateTime validityPeriodEnd;

  @ApiModelProperty(value = "Start date of the validity period")
  public ZonedDateTime getValidityPeriodStart() {
    return validityPeriodStart;
  }

  public void setValidityPeriodStart(ZonedDateTime validityPeriodStart) {
    this.validityPeriodStart = validityPeriodStart;
  }

  @ApiModelProperty(value = "End date of the validity period")
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
