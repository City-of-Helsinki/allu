package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Supervision task rejection data")
public class SupervisionTaskRejectionJson {

  @NotBlank(message = "{supervisiontask.result}")
  private String result;
  @NotNull(message = "{supervisiontask.newSupervisionDate}")
  private ZonedDateTime newSupervisionDate;

  @ApiModelProperty(value = "Result (supervisor's comment)", required = true)
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @ApiModelProperty(value = "Date for new supervision task.", required = true)
  public ZonedDateTime getNewSupervisionDate() {
    return newSupervisionDate;
  }

  public void setNewSupervisionDate(ZonedDateTime newSupervisionDate) {
    this.newSupervisionDate = newSupervisionDate;
  }
}
