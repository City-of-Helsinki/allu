package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

@NotFalse(rules = {
    "applicationKind, kindMatchType, {shorttermrental.kind}",
    "recurringEndYear, lessThanYearActivity, {shorttermrental.lessThanYearActivity}",
    "recurringEndYear, recurringKind, {shorttermrental.recurringKind}",
 })
@ApiModel("Short term rental (lyhytaikainen maanvuokraus) input model.")
public class ShortTermRentalExt extends BaseApplicationExt {

  private List<Integer> fixedLocationIds;
  private String description;
  @NotNull(message = "{application.kind}")
  private ApplicationKind applicationKind;
  private Integer recurringEndYear;

  @ApiModelProperty(value = "IDs of the fixed locations. Should be set if geometry of the application is selected from fixed locations.")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
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

  @ApiModelProperty(value = "The last year the recurring application is active. Application may recur for certain time every year. For example, an area might " +
    "be used for storing snow every year and such application should be created as a recurring application instead of creating " +
    "application for each year separately.")
  public Integer getRecurringEndYear() {
    return recurringEndYear;
  }

  public void setRecurringEndYear(Integer recurringEndYear) {
    this.recurringEndYear = recurringEndYear;
  }

  @JsonIgnore
  public boolean getKindMatchType() {
    if (applicationKind != null) {
      return applicationKind.getTypes().contains(ApplicationType.SHORT_TERM_RENTAL);
    }
    return true;
  }

  @JsonIgnore
  public boolean getLessThanYearActivity() {
    if (getRecurringEndYear() != null) {
      return getStartTime().plusYears(1).isAfter(getEndTime());
    }
    return true;
  }

  @JsonIgnore
  public boolean getRecurringKind() {
    if (getRecurringEndYear() != null) {
      return applicationKind.isTerrace();
    }
    return true;
  }
}
