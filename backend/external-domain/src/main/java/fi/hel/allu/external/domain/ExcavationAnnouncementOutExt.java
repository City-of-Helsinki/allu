package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Excavation annoucement (kaivuilmoitus) output model")
public class ExcavationAnnouncementOutExt extends ApplicationExt {

  private ZonedDateTime operationalConditionDate;
  private ZonedDateTime workFinishedDate;
  private ZonedDateTime reportedOperationalConditionDate;
  private ZonedDateTime reportedWorkFinishedDate;
  private ZonedDateTime reportedStartTime;
  private ZonedDateTime reportedEndTime;
  private ZonedDateTime warrantyEndTime;

  private boolean compactionAndBearingCapacityMeasurement;
  private boolean qualityAssuranceTest;

  private String workPurpose;
  private String additionalInfo;
  private List<String> cableReports;
  private List<String> placementContracts;
  private boolean selfSupervision;
  private boolean emergencyWork;
  private String trafficArrangements;

  @Schema(description = "Operational condition date")
  public ZonedDateTime getOperationalConditionDate() {
    return operationalConditionDate;
  }

  public void setOperationalConditionDate(ZonedDateTime operationalConditionDate) {
    this.operationalConditionDate = operationalConditionDate;
  }

  @Schema(description = "Work finished date")
  public ZonedDateTime getWorkFinishedDate() {
    return workFinishedDate;
  }

  public void setWorkFinishedDate(ZonedDateTime workFinishedDate) {
    this.workFinishedDate = workFinishedDate;
  }

  @Schema(description = "Operational condition date reported by the customer")
  public ZonedDateTime getReportedOperationalConditionDate() {
    return reportedOperationalConditionDate;
  }

  public void setReportedOperationalConditionDate(ZonedDateTime reportedOperationalConditionDate) {
    this.reportedOperationalConditionDate = reportedOperationalConditionDate;
  }

  @Schema(description = "Work finished date reported by the customer")
  public ZonedDateTime getReportedWorkFinishedDate() {
    return reportedWorkFinishedDate;
  }

  public void setReportedWorkFinishedDate(ZonedDateTime reportedWorkFinishedDate) {
    this.reportedWorkFinishedDate = reportedWorkFinishedDate;
  }

  @Schema(description = "Start time reported by the customer")
  public ZonedDateTime getReportedStartTime() {
    return reportedStartTime;
  }

  public void setReportedStartTime(ZonedDateTime reportedStartTime) {
    this.reportedStartTime = reportedStartTime;
  }

  @Schema(description = "End time reported by the customer")
  public ZonedDateTime getReportedEndTime() {
    return reportedEndTime;
  }

  public void setReportedEndTime(ZonedDateTime reportedEndTime) {
    this.reportedEndTime = reportedEndTime;
  }

  @Schema(description = "Compaction and bearing capacity measurement (tiiveys- ja kantavuusmittaus)")
  public boolean isCompactionAndBearingCapacityMeasurement() {
    return compactionAndBearingCapacityMeasurement;
  }

  public void setCompactionAndBearingCapacityMeasurement(boolean compactionAndBearingCapacityMeasurement) {
    this.compactionAndBearingCapacityMeasurement = compactionAndBearingCapacityMeasurement;
  }

  @Schema(description = "Quality assurance test (päällysteen laadunvarmistus)")
  public boolean isQualityAssuranceTest() {
    return qualityAssuranceTest;
  }

  public void setQualityAssuranceTest(boolean qualityAssuranceTest) {
    this.qualityAssuranceTest = qualityAssuranceTest;
  }

  @Schema(description = "Work purpose")
  public String getWorkPurpose() {
    return workPurpose;
  }

  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @Schema(description = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Schema(description = "Cable report identifiers (liittyvien johtoselvitysten hakemustunnukset)")
  public List<String> getCableReports() {
    return cableReports;
  }

  public void setCableReports(List<String> cableReports) {
    this.cableReports = cableReports;
  }

  @Schema(description = "Placement contract identifiers (liittyvien sijoitussopimusten hakemustunnukset)")
  public List<String> getPlacementContracts() {
    return placementContracts;
  }

  public void setPlacementContracts(List<String> placementContracts) {
    this.placementContracts = placementContracts;
  }

  @Schema(description = "Self supervision (omavalvonta)")
  public boolean isSelfSupervision() {
    return selfSupervision;
  }

  public void setSelfSupervision(boolean selfSupervision) {
    this.selfSupervision = selfSupervision;
  }

  @Schema(description = "Emergency work")
  public boolean isEmergencyWork() {
    return emergencyWork;
  }

  public void setEmergencyWork(boolean emergencyWork) {
    this.emergencyWork = emergencyWork;
  }

  @Schema(description = "Traffic arrangements")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @Schema(description = "Warranty end time")
  public ZonedDateTime getWarrantyEndTime() {
    return warrantyEndTime;
  }

  public void setWarrantyEndTime(ZonedDateTime warrantyEndTime) {
    this.warrantyEndTime = warrantyEndTime;
  }
}
