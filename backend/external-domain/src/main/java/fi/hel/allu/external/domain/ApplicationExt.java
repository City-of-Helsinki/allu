package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * External API application output data
 *
 */
@Schema(description = "Application output data")
public class ApplicationExt {

  private Integer id;
  private String name;
  private String applicationId;
  private StatusType status;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private UserExt owner;
  private Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers;
  private String terms;
  private String customerReference;
  private boolean surveyRequired;

  @Schema(description = "Id of the application")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Name of the application")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Application identifier (hakemustunniste)")
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  @Schema(description = "Status of the application")
  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  @Schema(description = "End time of the application" )
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @Schema(description = "Owner of the application")
  public UserExt getOwner() {
    return owner;
  }

  public void setOwner(UserExt owner) {
    this.owner = owner;
  }

  @Schema(description = "Start time of the application" )
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @Schema(description = "Application kinds with specifiers.")
  public Map<ApplicationKind, List<ApplicationSpecifier>> getKindsWithSpecifiers() {
    return kindsWithSpecifiers;
  }

  public void setKindsWithSpecifiers(Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers) {
    this.kindsWithSpecifiers = kindsWithSpecifiers;
  }

  @Schema(description = "Application terms")
  public String getTerms() {
    return terms;
  }

  public void setTerms(String terms) {
    this.terms = terms;
  }

  @Schema(description = "Customer reference to the invoice")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  public boolean isSurveyRequired() {
    return surveyRequired;
  }

  public void setSurveyRequired(boolean surveyRequired) {
    this.surveyRequired = surveyRequired;
  }
}
