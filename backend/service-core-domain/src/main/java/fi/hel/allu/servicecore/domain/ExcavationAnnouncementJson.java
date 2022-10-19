package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Excavation announcement (Kaivuilmoitus) specific data.
 */
@Schema(description ="Excavation announcement specific fields")
public class ExcavationAnnouncementJson extends ApplicationExtensionJson {
  private Boolean pksCard;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;
  private Boolean selfSupervision;
  private Boolean compactionAndBearingCapacityMeasurement;
  private Boolean qualityAssuranceTest;
  private ZonedDateTime winterTimeOperation;
  private ZonedDateTime workFinished;
  private ZonedDateTime unauthorizedWorkStartTime;
  private ZonedDateTime unauthorizedWorkEndTime;
  private ZonedDateTime guaranteeEndTime;
  private ZonedDateTime customerStartTime;
  private ZonedDateTime customerEndTime;
  private ZonedDateTime customerWinterTimeOperation;
  private ZonedDateTime customerWorkFinished;
  private ZonedDateTime operationalConditionReported;
  private ZonedDateTime workFinishedReported;
  private ZonedDateTime validityReported;
  @NotNull(message = "{application.excavationAnnouncement.workPurpose}")
  private String workPurpose;
  private String additionalInfo;
  private String trafficArrangements;
  @NotNull(message = "{application.excavationAnnouncement.trafficArrangementImpedimentType}")
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;
  private List<String> placementContracts;
  private List<String> cableReports;

  @Schema(description = "Application type (always EXCAVATION_ANNOUNCEMENT).", allowableValues="EXCAVATION_ANNOUNCEMENT", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
  }

  @Schema(description = "True, if the contractor doing the actual work has PKS-card")
  public Boolean getPksCard() {
    return pksCard;
  }

  @UpdatableProperty
  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
  }

  @Schema(description = "Construction work")
  public Boolean getConstructionWork() {
    return constructionWork;
  }

  @UpdatableProperty
  public void setConstructionWork(Boolean constructionWork) {
    this.constructionWork = constructionWork;
  }

  @Schema(description = "Maintenance work")
  public Boolean getMaintenanceWork() {
    return maintenanceWork;
  }

  @UpdatableProperty
  public void setMaintenanceWork(Boolean maintenanceWork) {
    this.maintenanceWork = maintenanceWork;
  }

  @Schema(description = "Emergency work")
  public Boolean getEmergencyWork() {
    return emergencyWork;
  }

  @UpdatableProperty
  public void setEmergencyWork(Boolean emergencyWork) {
    this.emergencyWork = emergencyWork;
  }

  @Schema(description = "Property connectivity (tontti-/kiinteistöliitos)")
  public Boolean getPropertyConnectivity() {
    return propertyConnectivity;
  }

  @UpdatableProperty
  public void setPropertyConnectivity(Boolean propertyConnectivity) {
    this.propertyConnectivity = propertyConnectivity;
  }

  @Schema(description = "Self supervision (omavalvonta)")
  public Boolean getSelfSupervision() {
    return selfSupervision;
  }

  @UpdatableProperty
  public void setSelfSupervision(Boolean selfSupervision) {
    this.selfSupervision = selfSupervision;
  }

  @Schema(description = "Compaction and bearing capacity measurement (tiiveys- ja kantavuusmittaus)")
  public Boolean getCompactionAndBearingCapacityMeasurement() {
    return compactionAndBearingCapacityMeasurement;
  }

  @UpdatableProperty
  public void setCompactionAndBearingCapacityMeasurement(Boolean compactionAndBearingCapacityMeasurement) {
    this.compactionAndBearingCapacityMeasurement = compactionAndBearingCapacityMeasurement;
  }

  @Schema(description = "Quality assurance test (päällysteen laadunvarmistus)")
  public Boolean getQualityAssuranceTest() {
    return qualityAssuranceTest;
  }

  @UpdatableProperty
  public void setQualityAssuranceTest(Boolean qualityAssuranceTest) {
    this.qualityAssuranceTest = qualityAssuranceTest;
  }

  @Schema(description = "Operational condition date for winter time work (toiminnallinen kunto)")
  public ZonedDateTime getWinterTimeOperation() {
    return winterTimeOperation;
  }

  @UpdatableProperty
  public void setWinterTimeOperation(ZonedDateTime winterTimeOperation) {
    this.winterTimeOperation = winterTimeOperation;
  }

  @Schema(description = "Work finished date", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
  }

  @Schema(description = "Unauthorized work start time")
  public ZonedDateTime getUnauthorizedWorkStartTime() {
    return unauthorizedWorkStartTime;
  }

  @UpdatableProperty
  public void setUnauthorizedWorkStartTime(ZonedDateTime unauthorizedWorkStartTime) {
    this.unauthorizedWorkStartTime = unauthorizedWorkStartTime;
  }

  @Schema(description = "Unauthorized work end time")
  public ZonedDateTime getUnauthorizedWorkEndTime() {
    return unauthorizedWorkEndTime;
  }

  @UpdatableProperty
  public void setUnauthorizedWorkEndTime(ZonedDateTime unauthorizedWorkEndTime) {
    this.unauthorizedWorkEndTime = unauthorizedWorkEndTime;
  }

  @Schema(description = "Guarantee end time", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getGuaranteeEndTime() {
    return guaranteeEndTime;
  }

  public void setGuaranteeEndTime(ZonedDateTime guaranteeEndTime) {
    this.guaranteeEndTime = guaranteeEndTime;
  }

  @Schema(description = "Start time reported by customer", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCustomerStartTime() {
    return customerStartTime;
  }

  public void setCustomerStartTime(ZonedDateTime customerStartTime) {
    this.customerStartTime = customerStartTime;
  }

  @Schema(description = "End time reported by customer", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCustomerEndTime() {
    return customerEndTime;
  }

  public void setCustomerEndTime(ZonedDateTime customerEndTime) {
    this.customerEndTime = customerEndTime;
  }

  @Schema(description = "Operational condition date reported by customer", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCustomerWinterTimeOperation() {
    return customerWinterTimeOperation;
  }

  public void setCustomerWinterTimeOperation(ZonedDateTime customerWinterTimeOperation) {
    this.customerWinterTimeOperation = customerWinterTimeOperation;
  }

  @Schema(description = "Work finished date reported by customer", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCustomerWorkFinished() {
    return customerWorkFinished;
  }

  public void setCustomerWorkFinished(ZonedDateTime customerWorkFinished) {
    this.customerWorkFinished = customerWorkFinished;
  }

  @Schema(description = "Purpose of the work", required = true)
  public String getWorkPurpose() {
    return workPurpose;
  }

  @UpdatableProperty
  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @Schema(description = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  @UpdatableProperty
  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Schema(description = "Traffic arrangements")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  @UpdatableProperty
  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @Schema(description = "Traffic arrangement impediment", required = true)
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  @UpdatableProperty
  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }

  @Schema(description = "Date when customer reported operational condition date", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getOperationalConditionReported() {
    return operationalConditionReported;
  }

  public void setOperationalConditionReported(ZonedDateTime operationalConditionReported) {
    this.operationalConditionReported = operationalConditionReported;
  }

  @Schema(description = "Date when customer reported work finished date", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getWorkFinishedReported() {
    return workFinishedReported;
  }

  public void setWorkFinishedReported(ZonedDateTime workFinishedReported) {
    this.workFinishedReported = workFinishedReported;
  }

  @Schema(description = "Date when customer reported application validity dates", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getValidityReported() {
    return validityReported;
  }

  public void setValidityReported(ZonedDateTime validityReported) {
    this.validityReported = validityReported;
  }

  @Schema(description = "Identifiers of related placement contracts")
  public List<String> getPlacementContracts() {
    return placementContracts;
  }

  @UpdatableProperty
  public void setPlacementContracts(List<String> placementContracts) {
    this.placementContracts = placementContracts;
  }

  @Schema(description = "Identifiers of related cable reports")
  public List<String> getCableReports() {
    return cableReports;
  }

  @UpdatableProperty
  public void setCableReports(List<String> cableReports) {
    this.cableReports = cableReports;
  }
}
