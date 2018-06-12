package fi.hel.allu.external.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NotFalse(rules = {
    "applicationKind, kindMatchType, {shorttermrental.kind}",
 })
@ApiModel("Short term rental (lyhytaikainen maanvuokraus).")
public class ShortTermRentalExt extends ApplicationExt {

  private Integer fixedLocationId;
  private String description;
  @NotNull(message = "{application.kind}")
  private ApplicationKind applicationKind;
  private Integer area;

  @ApiModelProperty(value = "ID of the fixed location. Should be set if geometry of the application is selected from fixed locations.")
  public Integer getFixedLocationId() {
    return fixedLocationId;
  }

  public void setFixedLocationId(Integer fixedLocationId) {
    this.fixedLocationId = fixedLocationId;
  }

  @ApiModelProperty(value = "Description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(value = "Application kind.", required = true)
  public ApplicationKind getApplicationKind() {
    return applicationKind;
  }

  public void setApplicationKind(ApplicationKind applicationKind) {
    this.applicationKind = applicationKind;
  }

  @ApiModelProperty(value = "Area in square meters")
  public Integer getArea() {
    return area;
  }

  public void setArea(Integer area) {
    this.area = area;
  }

  @JsonIgnore
  public boolean getKindMatchType() {
    if (applicationKind != null) {
      return applicationKind.getTypes().contains(ApplicationType.SHORT_TERM_RENTAL);
    }
    return true;
  }
}
