package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Supervision task approval data")
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

  @ApiModelProperty(value = "Id of the supervision task", required = true)
  public Integer getTaskId() {
    return taskId;
  }

  public void setTaskId(Integer taskId) {
    this.taskId = taskId;
  }

  @ApiModelProperty(value = "Result (supervisor's comments)", required = true)
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @ApiModelProperty(value = "Date when work was in operational condition. Required when approving operational condition supervision (otherwise ignored). Cannot be in future.")
  public ZonedDateTime getOperationalConditionDate() {
    return operationalConditionDate;
  }

  public void setOperationalConditionDate(ZonedDateTime operationalConditionDate) {
    this.operationalConditionDate = operationalConditionDate;
  }

  @ApiModelProperty(value = "Date when work was finished. Required when approving final supervision of area rental or excavation announcement (otherwise ignored). Cannot be in future.")
  public ZonedDateTime getWorkFinishedDate() {
    return workFinishedDate;
  }

  public void setWorkFinishedDate(ZonedDateTime workFinishedDate) {
    this.workFinishedDate = workFinishedDate;
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
