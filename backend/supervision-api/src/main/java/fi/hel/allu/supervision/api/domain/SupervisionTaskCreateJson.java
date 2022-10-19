package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

@NotFalse(rules = {"type, isAllowedType, {supervisiontask.create.type}"})
@Schema(description = "Supervision task creation model")
public class SupervisionTaskCreateJson extends SupervisionTaskModifyJson {

  @NotNull(message = "{supervisiontask.type}")
  private SupervisionTaskType type;
  @NotNull(message = "{supervisiontask.applicationId}")
  private Integer applicationId;

  @Schema(description = "Type of the supervision task", allowableValues = "SUPERVISION, PRELIMINARY_SUPERVISION", required = true)
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  @Schema(description = "Application ID", required = true)
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  @JsonIgnore
  public boolean getIsAllowedType() {
    return type == SupervisionTaskType.PRELIMINARY_SUPERVISION || type == SupervisionTaskType.SUPERVISION;
  }
}
