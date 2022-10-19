package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Supervision task rejection data")
public class SupervisionTaskRejectionJson {

  @NotBlank(message = "{supervisiontask.result}")
  private String result;
  @NotNull(message = "{supervisiontask.newSupervisionDate}")
  private ZonedDateTime newSupervisionDate;

  @Schema(description = "Result (supervisor's comment)", required = true)
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Schema(description = "Date for new supervision task.", required = true)
  public ZonedDateTime getNewSupervisionDate() {
    return newSupervisionDate;
  }

  public void setNewSupervisionDate(ZonedDateTime newSupervisionDate) {
    this.newSupervisionDate = newSupervisionDate;
  }
}
