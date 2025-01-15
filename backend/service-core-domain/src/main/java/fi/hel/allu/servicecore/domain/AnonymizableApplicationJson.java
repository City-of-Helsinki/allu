package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.ChangeType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

/**
 * in Finnish: Anonymisoitava/"poistettava" hakemus
 */
@Schema(description = "Anonymizable application")
public class AnonymizableApplicationJson {
  private Integer id;
  private String applicationId;
  private ApplicationType applicationType;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ChangeType changeType;
  private String changeSpecifier;
  private ZonedDateTime changeTime;

  public AnonymizableApplicationJson() {
  }

  public AnonymizableApplicationJson(Integer id, String applicationId, ApplicationType applicationType, ZonedDateTime startTime, ZonedDateTime endTime, ChangeType changeType, String changeSpecifier, ZonedDateTime changeTime) {
    this.id = id;
    this.applicationId = applicationId;
    this.applicationType = applicationType;
    this.startTime = startTime;
    this.endTime = endTime;
    this.changeType = changeType;
    this.changeSpecifier = changeSpecifier;
    this.changeTime = changeTime;
  }

  @Schema(description = "Id of the anonymizable application")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Application id of the anonymizable application")
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  @Schema(description = "Application type of the anonymizable application")
  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  @Schema(description = "Starting time of the anonymizable application")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @Schema(description = "Ending time of the anonymizable application")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @Schema(description = "Change type of the anonymizable application")
  public ChangeType getChangeType() {
    return changeType;
  }

  public void setChangeType(ChangeType changeType) {
    this.changeType = changeType;
  }

  public String getChangeSpecifier() { return changeSpecifier; }

  public void setChangeSpecifier(String changeSpecifier) { this.changeSpecifier = changeSpecifier; }

  @Schema(description = "Changing time of the anonymizable application")
  public ZonedDateTime getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(ZonedDateTime changeTime) {
    this.changeTime = changeTime;
  }
}
