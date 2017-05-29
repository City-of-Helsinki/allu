package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationSpecifier;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;

import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Search parameters for application deadline search.
 */
public class DeadlineCheckParams {
  public DeadlineCheckParams() {
    // For jackson
  }

  public DeadlineCheckParams(List<ApplicationType> typeSelector, List<StatusType> statusSelector,
      ZonedDateTime endsAfter, ZonedDateTime endsBefore) {
    this.typeSelector = typeSelector;
    this.statusSelector = statusSelector;
    this.endsAfter = endsAfter;
    this.endsBefore = endsBefore;
  }

  @NotNull
  private List<ApplicationType> typeSelector;
  @NotNull
  private List<StatusType> statusSelector;
  @NotNull
  private ZonedDateTime endsAfter;
  @NotNull
  private ZonedDateTime endsBefore;

  /**
   * Get the application type selector for search. If the selector is not empty,
   * results are filtered by application specifier and only applications whose
   * type is included in the list are returned.
   *
   * @return list of desired {@link ApplicationSpecifier} values
   */
  public List<ApplicationType> getTypeSelector() {
    return typeSelector;
  }

  public void setTypeSelector(List<ApplicationType> typeSelector) {
    this.typeSelector = typeSelector;
  }

  /**
   * Get the application status selector for the search. If the selector is not
   * empty, search results are filtered by application status ans only
   * applications whose status is included in the list are returned.
   *
   * @return list of desired {@link StatusType} values
   */
  public List<StatusType> getStatusSelector() {
    return statusSelector;
  }

  public void setStatusSelector(List<StatusType> statusSelector) {
    this.statusSelector = statusSelector;
  }

  /**
   * Get lower limit for end date. Only applications that end after the given
   * date are returned.
   */
  public ZonedDateTime getEndsAfter() {
    return endsAfter;
  }

  public void setEndsAfter(ZonedDateTime endsAfter) {
    this.endsAfter = endsAfter;
  }

  /**
   * Get the upper limit for the end date. Only applications that end before the
   * given date are returned.
   */
  public ZonedDateTime getEndsBefore() {
    return endsBefore;
  }

  public void setEndsBefore(ZonedDateTime endsBefore) {
    this.endsBefore = endsBefore;
  }

}
