package fi.hel.allu.external.domain;

public class ApplicationProgressStatusExt {
  private InspectionStatus workFinishedStatus;
  private InspectionStatus winterTimeOperationStatus;

  public ApplicationProgressStatusExt() {
    // Needed for deserialization
  }

  public ApplicationProgressStatusExt(InspectionStatus workFinishedStatus, InspectionStatus winterTimeOperationStatus) {
    this.workFinishedStatus = workFinishedStatus;
    this.winterTimeOperationStatus = winterTimeOperationStatus;
  }

  public InspectionStatus getWorkFinishedStatus() {
    return workFinishedStatus;
  }

  public void setWorkFinishedStatus(InspectionStatus workFinishedStatus) {
    this.workFinishedStatus = workFinishedStatus;
  }

  public InspectionStatus getWinterTimeOperationStatus() {
    return winterTimeOperationStatus;
  }

  public void setWinterTimeOperationStatus(InspectionStatus winterTimeOperationStatus) {
    this.winterTimeOperationStatus = winterTimeOperationStatus;
  }

}
