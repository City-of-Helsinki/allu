package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@NotFalse(rules = {
    "applicationKind, kindMatchType, {shorttermrental.kind}",
    "recurringEndYear, lessThanYearActivity, {application.lessThanYearActivity}",
    "recurringEndYear, recurringKind, {shorttermrental.recurringKind}",
    "within80cmFromWall, kindWithin80cm, {shorttermrental.kindWithin80cm}",
    "commercial, kindCommercial, {shorttermrental.kindCommercial}"
 })
@Schema(description ="Short term rental (lyhytaikainen maanvuokraus) input model.")
public class ShortTermRentalExt extends BaseApplicationExt {

  private List<Integer> fixedLocationIds;
  private String description;
  @NotNull(message = "{application.kind}")
  private ApplicationKind applicationKind;
  private Integer recurringEndYear;
  private Boolean within80cmFromWall;
  private Boolean commercial;

  @Schema(description = "IDs of the fixed locations. Should be set if geometry of the application is selected from fixed locations.")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }

  @Schema(description = "Description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Schema(description = "Application kind.", required = true)
  public ApplicationKind getApplicationKind() {
    return applicationKind;
  }

  public void setApplicationKind(ApplicationKind applicationKind) {
    this.applicationKind = applicationKind;
  }

  @Schema(description = "The last year the recurring application is active. Application may recur for certain time every year. For example, an area might " +
    "be used for storing snow every year and such application should be created as a recurring application instead of creating " +
    "application for each year separately.")
  public Integer getRecurringEndYear() {
    return recurringEndYear;
  }

  public void setRecurringEndYear(Integer recurringEndYear) {
    this.recurringEndYear = recurringEndYear;
  }

  @Schema(description = "Describes whether rental area is within 80cm from actual business space. " +
    "This can be set for SUMMER_TERRACE, WINTER_TERRACE and PROMOTION_OR_SALES.")
  public Boolean getWithin80cmFromWall() {
    return within80cmFromWall;
  }

  public void setWithin80cmFromWall(Boolean within80cmFromWall) {
    this.within80cmFromWall = within80cmFromWall;
  }

  @Schema(description = "True, if rental has commercial nature. Value allowed only for kind BRIDGE_BANNER.")
  public Boolean getCommercial() {
    return commercial;
  }

  public void setCommercial(Boolean commercial) {
    this.commercial = commercial;
  }

  @JsonIgnore
  public Boolean isBillableSalesArea() {
    return Optional.ofNullable(within80cmFromWall)
      .map(within -> !within)
      .orElse(null);
  }

  @JsonIgnore
  public boolean getKindMatchType() {
    if (applicationKind != null) {
      return applicationKind.getTypes().contains(ApplicationType.SHORT_TERM_RENTAL);
    }
    return true;
  }

  @JsonIgnore
  public boolean getKindWithin80cm() {
    if (within80cmFromWall != null) {
      return Arrays.asList(
        ApplicationKind.SUMMER_TERRACE,
        ApplicationKind.WINTER_TERRACE,
        ApplicationKind.PROMOTION_OR_SALES
      ).contains(applicationKind);
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

  @JsonIgnore
  public boolean getKindCommercial() {
    if (getCommercial() != null) {
      return applicationKind.equals(ApplicationKind.BRIDGE_BANNER);
    }
    return true;
  }
}
