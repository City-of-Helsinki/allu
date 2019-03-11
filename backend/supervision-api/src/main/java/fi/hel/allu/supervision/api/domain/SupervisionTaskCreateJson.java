package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NotFalse(rules = {"type, isAllowedType, {supervisiontask.create.type}"})
@ApiModel(value = "Supervision task creation model")
public class SupervisionTaskCreateJson extends SupervisionTaskModifyJson {

  @NotNull(message = "{supervisiontask.type}")
  private SupervisionTaskType type;
  @NotNull(message = "{supervisiontask.applicationId}")
  private Integer applicationId;

  @ApiModelProperty(value = "Type of the supervision task", allowableValues = "SUPERVISION, PRELIMINARY_SUPERVISION", required = true)
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Application ID", required = true)
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
