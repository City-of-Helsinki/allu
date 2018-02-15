package fi.hel.allu.pdf.domain;

import java.util.List;

/**
 * DecisionJson is used to transfer all needed data to PDF service for decision
 * generation. Everything is in already-formatted form, ready to be inserted
 * into the decision template.
 */
public class DecisionJson {

  private boolean isDraft;

  private String decisionDate;
  private String decisionId;

  private List<String> customerAddressLines;

  private List<String> customerContactLines;

  private String siteAddressLine;
  private String siteAdditionalInfo;

  private String siteArea;

  private String reservationStartDate;
  private String reservationEndDate;
  private int numReservationDays;

  private String buildStartDate;
  private String buildEndDate;
  private String eventStartDate;
  private String eventEndDate;
  private String teardownStartDate;
  private String teardownEndDate;
  private int numEventDays;
  private int numBuildAndTeardownDays;

  private String reservationTimeExceptions;

  private String eventName;
  private String eventDescription;
  private String eventUrl;
  private String eventNature;
  private String structureArea;
  private String structureDescription;

  private String totalRent;
  private int vatPercentage;
  private String priceBasisText;
  private boolean notBillable;
  private String notBillableReason;
  private boolean hasEkokompassi;
  private boolean separateBill;

  private List<String> additionalConditions;

  private String decisionTimestamp;
  private String deciderTitle;
  private String deciderName;

  private String handlerTitle;
  private String handlerName;

  private List<String> attachmentNames;

  private String appealInstructions;

  private String cableReportValidUntil;

  private String workDescription;

  private List<CableInfoTexts> cableInfoEntries;

  private int mapExtractCount;

  private String cableReportOrderer;

  private List<ChargeInfoTexts> chargeInfoEntries;

  public boolean isDraft() {
    return isDraft;
  }

  public void setDraft(boolean isDraft) {
    this.isDraft = isDraft;
  }

  public String getDecisionDate() {
    return decisionDate;
  }

  public void setDecisionDate(String decisionDate) {
    this.decisionDate = decisionDate;
  }

  public String getDecisionId() {
    return decisionId;
  }

  public void setDecisionId(String decisionId) {
    this.decisionId = decisionId;
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

  public String getSiteAddressLine() {
    return siteAddressLine;
  }

  public void setSiteAddressLine(String siteAddressLine) {
    this.siteAddressLine = siteAddressLine;
  }

  public String getSiteAdditionalInfo() {
    return siteAdditionalInfo;
  }

  public void setSiteAdditionalInfo(String siteAdditionalInfo) {
    this.siteAdditionalInfo = siteAdditionalInfo;
  }

  public String getSiteArea() {
    return siteArea;
  }

  public void setSiteArea(String siteArea) {
    this.siteArea = siteArea;
  }

  public String getReservationStartDate() {
    return reservationStartDate;
  }

  public void setReservationStartDate(String reservationStartDate) {
    this.reservationStartDate = reservationStartDate;
  }

  public String getReservationEndDate() {
    return reservationEndDate;
  }

  public void setReservationEndDate(String reservationEndDate) {
    this.reservationEndDate = reservationEndDate;
  }

  public int getNumReservationDays() {
    return numReservationDays;
  }

  public void setNumReservationDays(int numReservationDays) {
    this.numReservationDays = numReservationDays;
  }

  public String getBuildStartDate() {
    return buildStartDate;
  }

  public void setBuildStartDate(String buildStartDate) {
    this.buildStartDate = buildStartDate;
  }

  public String getBuildEndDate() {
    return buildEndDate;
  }

  public void setBuildEndDate(String buildEndDate) {
    this.buildEndDate = buildEndDate;
  }

  public String getEventStartDate() {
    return eventStartDate;
  }

  public void setEventStartDate(String eventStartDate) {
    this.eventStartDate = eventStartDate;
  }

  public String getEventEndDate() {
    return eventEndDate;
  }

  public void setEventEndDate(String eventEndDate) {
    this.eventEndDate = eventEndDate;
  }

  public String getTeardownStartDate() {
    return teardownStartDate;
  }

  public void setTeardownStartDate(String teardownStartDate) {
    this.teardownStartDate = teardownStartDate;
  }

  public String getTeardownEndDate() {
    return teardownEndDate;
  }

  public void setTeardownEndDate(String teardownEndDate) {
    this.teardownEndDate = teardownEndDate;
  }

  public int getNumEventDays() {
    return numEventDays;
  }

  public void setNumEventDays(int numEventDays) {
    this.numEventDays = numEventDays;
  }

  public int getNumBuildAndTeardownDays() {
    return numBuildAndTeardownDays;
  }

  public void setNumBuildAndTeardownDays(int numBuildAndTeardownDays) {
    this.numBuildAndTeardownDays = numBuildAndTeardownDays;
  }

  public String getReservationTimeExceptions() {
    return reservationTimeExceptions;
  }

  public void setReservationTimeExceptions(String reservationTimeExceptions) {
    this.reservationTimeExceptions = reservationTimeExceptions;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getEventDescription() {
    return eventDescription;
  }

  public void setEventDescription(String eventDescription) {
    this.eventDescription = eventDescription;
  }

  public String getEventUrl() {
    return eventUrl;
  }

  public void setEventUrl(String eventUrl) {
    this.eventUrl = eventUrl;
  }

  public String getEventNature() {
    return eventNature;
  }

  public void setEventNature(String eventNature) {
    this.eventNature = eventNature;
  }

  public String getStructureArea() {
    return structureArea;
  }

  public void setStructureArea(String structureArea) {
    this.structureArea = structureArea;
  }

  public String getStructureDescription() {
    return structureDescription;
  }

  public void setStructureDescription(String structureDescription) {
    this.structureDescription = structureDescription;
  }

  public String getTotalRent() {
    return totalRent;
  }

  public void setTotalRent(String totalRent) {
    this.totalRent = totalRent;
  }

  public int getVatPercentage() {
    return vatPercentage;
  }

  public void setVatPercentage(int vatPercentage) {
    this.vatPercentage = vatPercentage;
  }

  /**
   * Get the price basis text. NOTE: this text will be output raw, no HTML
   * escaping is done.
   */
  public String getPriceBasisText() {
    return priceBasisText;
  }

  public void setPriceBasisText(String priceBasisText) {
    this.priceBasisText = priceBasisText;
  }

  /**
   * Is the application marked as "not billable"?
   */
  public boolean isNotBillable() {
    return notBillable;
  }

  public void setNotBillable(boolean notBillable) {
    this.notBillable = notBillable;
  }

  /**
   * The reason for not being billable
   */
  public String getNotBillableReason() {
    return notBillableReason;
  }

  public void setNotBillableReason(String notBillableReason) {
    this.notBillableReason = notBillableReason;
  }

  public boolean isHasEkokompassi() {
    return hasEkokompassi;
  }

  public void setHasEkokompassi(boolean hasEkokompassi) {
    this.hasEkokompassi = hasEkokompassi;
  }

  public boolean isSeparateBill() {
    return separateBill;
  }

  public void setSeparateBill(boolean separateBill) {
    this.separateBill = separateBill;
  }

  public List<String> getAdditionalConditions() {
    return additionalConditions;
  }

  public void setAdditionalConditions(List<String> additionalConditions) {
    this.additionalConditions = additionalConditions;
  }

  public String getDecisionTimestamp() {
    return decisionTimestamp;
  }

  public void setDecisionTimestamp(String decisionTimestamp) {
    this.decisionTimestamp = decisionTimestamp;
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

  public List<String> getAttachmentNames() {
    return attachmentNames;
  }

  public void setAttachmentNames(List<String> attachmentNames) {
    this.attachmentNames = attachmentNames;
  }

  public String getAppealInstructions() {
    return appealInstructions;
  }

  public void setAppealInstructions(String appealInstructions) {
    this.appealInstructions = appealInstructions;
  }

  /**
   * Get cable report's validity end time
   */
  public String getCableReportValidUntil() {
    return cableReportValidUntil;
  }

  public void setCableReportValidUntil(String cableReportValidUntil) {
    this.cableReportValidUntil = cableReportValidUntil;
  }

  /**
   * Get work description (for cable reports)
   */
  public String getWorkDescription() {
    return workDescription;
  }

  public void setWorkDescription(String workDescription) {
    this.workDescription = workDescription;
  }

  /*
   * Get the cable info entries
   */
  public List<CableInfoTexts> getCableInfoEntries() {
    return cableInfoEntries;
  }

  public void setCableInfoEntries(List<CableInfoTexts> cableInfoTexts) {
    this.cableInfoEntries = cableInfoTexts;
  }

  public int getMapExtractCount() {
    return mapExtractCount;
  }

  public void setMapExtractCount(int mapExtractCount) {
    this.mapExtractCount = mapExtractCount;
  }

  /* Get the name of the cable report orderer */
  public String getCableReportOrderer() {
    return cableReportOrderer;
  }

  public void setCableReportOrderer(String cableReportOrderer) {
    this.cableReportOrderer = cableReportOrderer;
  }

  public List<ChargeInfoTexts> getChargeInfoEntries() {
    return chargeInfoEntries;
  }

  public void setChargeInfoEntries(List<ChargeInfoTexts> chargeInfoEntries) {
    this.chargeInfoEntries = chargeInfoEntries;
  }

}
