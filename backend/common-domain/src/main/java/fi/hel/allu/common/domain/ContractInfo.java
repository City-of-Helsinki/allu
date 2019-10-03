package fi.hel.allu.common.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.ContractStatusType;

public class ContractInfo {

  private Integer id;
  private Integer applicationId;
  private ContractStatusType status;
  private String rejectionReason;
  private ZonedDateTime creationTime;
  private ZonedDateTime responseTime;
  private String signer;
  private boolean frameAgreementExists;
  private boolean contractAsAttachment;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public ContractStatusType getStatus() {
    return status;
  }

  public void setStatus(ContractStatusType status) {
    this.status = status;
  }

  public String getRejectionReason() {
    return rejectionReason;
  }

  public void setRejectionReason(String rejectionReason) {
    this.rejectionReason = rejectionReason;
  }

  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  public ZonedDateTime getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(ZonedDateTime responseTime) {
    this.responseTime = responseTime;
  }

  public String getSigner() {
    return signer;
  }

  public void setSigner(String signer) {
    this.signer = signer;
  }

  public boolean isFrameAgreementExists() {
    return frameAgreementExists;
  }

  public void setFrameAgreementExists(boolean frameAgreementExists) {
    this.frameAgreementExists = frameAgreementExists;
  }

  public boolean isContractAsAttachment() {
    return contractAsAttachment;
  }

  public void setContractAsAttachment(boolean contractAsAttachment) {
    this.contractAsAttachment = contractAsAttachment;
  }
}
