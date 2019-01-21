package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Supervision task approval data")
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

  @ApiModelProperty(value = "Date when work was in operational condition. Required when approving operational condition supervision (otherwise ignored).")
  public ZonedDateTime getOperationalConditionDate() {
    return operationalConditionDate;
  }

  public void setOperationalConditionDate(ZonedDateTime operationalConditionDate) {
    this.operationalConditionDate = operationalConditionDate;
  }

  @ApiModelProperty(value = "Date when work was finished. Required when approving final supervision of area rental or excavation announcement (otherwise ignored).")
  public ZonedDateTime getWorkFinishedDate() {
    return workFinishedDate;
  }

  public void setWorkFinishedDate(ZonedDateTime workFinishedDate) {
    this.workFinishedDate = workFinishedDate;
  }
}
