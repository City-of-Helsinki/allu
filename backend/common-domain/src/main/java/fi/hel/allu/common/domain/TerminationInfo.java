package fi.hel.allu.common.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class TerminationInfo {
  private Integer id;
  @NotNull
  private Integer applicationId;
  private ZonedDateTime creationTime;
  @NotNull
  private ZonedDateTime terminationTime;
  @NotBlank
  private String reason;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  public ZonedDateTime getTerminationTime() {
    return terminationTime;
  }

  public void setTerminationTime(ZonedDateTime terminationTime) {
    this.terminationTime = terminationTime;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
