package fi.hel.allu.servicecore.domain;

public class ContractApprovalInfo extends StatusChangeInfoJson {

  private boolean frameAgreementExists;
  private boolean contractAsAttachment;

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
