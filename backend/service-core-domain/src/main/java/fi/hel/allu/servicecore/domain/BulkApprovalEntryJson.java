package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.StatusType;

import java.util.List;

public class BulkApprovalEntryJson {
  private int id;
  private String applicationId;
  private StatusType targetState;
  private List<DistributionEntryJson> distributionList;
  private Boolean bulkApprovalBlocked;
  private String bulkApprovalBlockedReason;

  public BulkApprovalEntryJson() {
  }

  /**
   * Application database id
   */
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /**
   * Human readable application identifier
   */
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Target state after approval
   */
  public StatusType getTargetState() {
    return targetState;
  }

  public void setTargetState(StatusType targetState) {
    this.targetState = targetState;
  }

  /**
   * The distribution list of the approval.
   */
  public List<DistributionEntryJson> getDistributionList() {
    return distributionList;
  }

  public void setDistributionList(List<DistributionEntryJson> distributionList) {
    this.distributionList = distributionList;
  }

  /**
   * Info if Bulk approval is not allowed for given entry
   */
  public Boolean getBulkApprovalBlocked() {
    return bulkApprovalBlocked;
  }

  public void setBulkApprovalBlocked(Boolean bulkApprovalBlocked) {
    this.bulkApprovalBlocked = bulkApprovalBlocked;
  }

  /**
   * Reason why bulk approval is not allowed for given entry
   */
  public String getBulkApprovalBlockedReason() {
    return bulkApprovalBlockedReason;
  }

  public void setBulkApprovalBlockedReason(String bulkApprovalBlockedReason) {
    this.bulkApprovalBlockedReason = bulkApprovalBlockedReason;
  }
}
