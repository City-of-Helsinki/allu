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
  private ZonedDateTime expirationTime;
  @NotBlank
  private String reason;
  private Integer terminationHandler;
  @NotNull
  private Integer terminator;
  private ZonedDateTime terminationDecisionTime;

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

  public ZonedDateTime getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(ZonedDateTime expirationTime) {
    this.expirationTime = expirationTime;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Integer getTerminator() {
    return terminator;
  }

  public void setTerminator(Integer terminator) {
    this.terminator = terminator;
  }

  public Integer getTerminationHandler() {
    return terminationHandler;
  }

  public void setTerminationHandler(Integer terminationHandler) {
    this.terminationHandler = terminationHandler;
  }

  public ZonedDateTime getTerminationDecisionTime() {
    return terminationDecisionTime;
  }

  public void setTerminationDecisionTime(ZonedDateTime terminationDecisionTime) {
    this.terminationDecisionTime = terminationDecisionTime;
  }

}
