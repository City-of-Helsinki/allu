package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Supervision task approval data")
@NotFalse(rules = {
    "operationalConditionDate, operationalConditionNotInFuture, {supervisiontask.operationalCondition.invalid}",
    "workFinishedDate, workFinishedNotInFuture, {supervisiontask.workFinished.invalid}",
 })
public class SupervisionTaskApprovalJson {

  @NotNull(message = "{supervisiontask.id}")
  private Integer taskId;
  @NotBlank(message = "{supervisiontask.result}")
  private String result;
  private ZonedDateTime operationalConditionDate;
  private ZonedDateTime workFinishedDate;
  private Integer decisionMakerId;
  private String decisionNote;
  private Boolean compactionAndBearingCapacityMeasurement;
  private Boolean qualityAssuranceTest;

  @Schema(description = "Id of the supervision task", required = true)
  public Integer getTaskId() {
    return taskId;
  }

  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }

  @Schema(description = "Result (supervisor's comments)", required = true)
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Schema(description = "Date when work was in operational condition. Required when approving operational condition supervision (otherwise ignored). "
      + "Cannot be in future.")
  public ZonedDateTime getOperationalConditionDate() {
    return operationalConditionDate;
  }

  public void setOperationalConditionDate(ZonedDateTime operationalConditionDate) {
    this.operationalConditionDate = operationalConditionDate;
  }

  @Schema(description = "Date when work was finished. Required when approving final supervision of area rental or excavation announcement (otherwise ignored). "
      + "Cannot be in future.")
  public ZonedDateTime getWorkFinishedDate() {
    return workFinishedDate;
  }

  public void setWorkFinishedDate(ZonedDateTime workFinishedDate) {
    this.workFinishedDate = workFinishedDate;
  }

  @Schema(description = "Decision maker user ID. User must have ROLE_DECISION -role. Required when approving operational condition or final supervision of "
      + "area rental or excavation announcement (otherwise ignored)")
  public Integer getDecisionMakerId() {
    return decisionMakerId;
  }

  public void setDecisionMakerId(Integer decisionMakerId) {
    this.decisionMakerId = decisionMakerId;
  }

  @Schema(description = "Note for decision maker. Required when approving operational condition or final supervision of area rental or "
      + "excavation announcement (otherwise ignored)")
  public String getDecisionNote() {
    return decisionNote;
  }

  public void setDecisionNote(String decisionNote) {
    this.decisionNote = decisionNote;
  }

  @Schema(description = "Quality assurance test (päällysteen laadunvarmistuskoe) required. "
      + "Applies only to preliminary supervision of excavation announcement, otherwise ignored.")
  public Boolean getQualityAssuranceTest() {
    return qualityAssuranceTest;
  }

  public void setQualityAssuranceTest(Boolean qualityAssuranceTest) {
    this.qualityAssuranceTest = qualityAssuranceTest;
  }

  @Schema(description = "Compaction and bearing measurement (tiiveys- ja kantavuusmittaus) required. "
      + "Applies only to preliminary supervision of excavation announcement, otherwise ignored.")
  public Boolean getCompactionAndBearingCapacityMeasurement() {
    return compactionAndBearingCapacityMeasurement;
  }

  public void setCompactionAndBearingCapacityMeasurement(Boolean compactionAndBearingCapacityMeasurement) {
    this.compactionAndBearingCapacityMeasurement = compactionAndBearingCapacityMeasurement;
  }

  @JsonIgnore
  public boolean getOperationalConditionNotInFuture() {
    return getNotInFuture(operationalConditionDate);
  }

  @JsonIgnore
  public boolean getWorkFinishedNotInFuture() {
    return getNotInFuture(workFinishedDate);
  }

  @JsonIgnore
  private boolean getNotInFuture(ZonedDateTime date) {
    return date == null || date.isBefore(TimeUtil.startOfDay(TimeUtil.nextDay(ZonedDateTime.now())));
  }

}
