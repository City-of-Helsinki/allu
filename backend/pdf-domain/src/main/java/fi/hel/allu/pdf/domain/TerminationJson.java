package fi.hel.allu.pdf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class TerminationJson {
  private boolean isDraft;
  private ApplicationType applicationType;
  private ZonedDateTime decisionDate;
  private String applicationId;
  private ZonedDateTime terminationDate;
  private String deciderTitle;
  private String deciderName;
  private String handlerTitle;
  private String handlerName;
  private List<String> terminationInfo;
  private List<String> customerAddressLines;
  private List<String> customerContactLines;

  public boolean isDraft() {
    return isDraft;
  }

  public void setDraft(boolean draft) {
    isDraft = draft;
  }

  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public String getDecisionDate() {
    return Optional.ofNullable(decisionDate)
        .map(date -> TimeUtil.dateAsString(date))
        .orElse(null);
  }

  public void setDecisionDate(ZonedDateTime decisionDate) {
    this.decisionDate = decisionDate;
  }

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public String getDecisionTimestamp() {
    return Optional.ofNullable(decisionDate)
        .map(date -> TimeUtil.dateAsDateTimeString(date))
        .orElse(null);
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getTerminationDate() {
    return Optional.ofNullable(terminationDate)
        .map(date -> TimeUtil.dateAsDateTimeString(date))
        .orElse(null);
  }

  public void setTerminationDate(ZonedDateTime terminationDate) {
    this.terminationDate = terminationDate;
  }

  public String getDeciderTitle() {
    return deciderTitle;
  }

  public void setDeciderTitle(String deciderTitle) {
    this.deciderTitle = deciderTitle;
  }

  public String getDeciderName() {
    return deciderName;
  }

  public void setDeciderName(String deciderName) {
    this.deciderName = deciderName;
  }

  public String getHandlerTitle() {
    return handlerTitle;
  }

  public void setHandlerTitle(String handlerTitle) {
    this.handlerTitle = handlerTitle;
  }

  public String getHandlerName() {
    return handlerName;
  }

  public void setHandlerName(String handlerName) {
    this.handlerName = handlerName;
  }

  public List<String> getTerminationInfo() {
    return terminationInfo;
  }

  public void setTerminationInfo(List<String> terminationInfo) {
    this.terminationInfo = terminationInfo;
  }

  public List<String> getCustomerAddressLines() {
    return customerAddressLines;
  }

  public void setCustomerAddressLines(List<String> customerAddressLines) {
    this.customerAddressLines = customerAddressLines;
  }

  public List<String> getCustomerContactLines() {
    return customerContactLines;
  }

  public void setCustomerContactLines(List<String> customerContactLines) {
    this.customerContactLines = customerContactLines;
  }
}
