package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Cable report specific fields")
public class CableReportJson extends ApplicationExtensionJson {

  private String cableReportId;
  private String workDescription;
  private Integer mapExtractCount;
  private List<CableInfoEntryJson> infoEntries;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;
  private ZonedDateTime validityTime;
  private Integer orderer;

  @Schema(description = "Application type (always CABLE_REPORT).", allowableValues="CABLE_REPORT", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.CABLE_REPORT;
  }

  @Schema(description = "Cable report identifier (johtoselvitystunnus)")
  public String getCableReportId() {
    return cableReportId;
  }

  @UpdatableProperty
  public void setCableReportId(String cableReportId) {
    this.cableReportId = cableReportId;
  }


  @Schema(description = "Work description")
  public String getWorkDescription() {
    return workDescription;
  }

  @UpdatableProperty
  public void setWorkDescription(String workDescription) {
    this.workDescription = workDescription;
  }

  @Schema(description = "Map extract count")
  public Integer getMapExtractCount() {
    return mapExtractCount;
  }

  @UpdatableProperty
  public void setMapExtractCount(Integer mapExtractCount) {
    this.mapExtractCount = mapExtractCount;
  }

  @Schema(description = "Cable info entries (johtotiedot)")
  public List<CableInfoEntryJson> getInfoEntries() {
    return infoEntries;
  }

  @UpdatableProperty
  public void setInfoEntries(List<CableInfoEntryJson> infoEntries) {
    this.infoEntries = infoEntries;
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

  @Schema(description = "Validity time of the cable report")
  public ZonedDateTime getValidityTime() {
    return validityTime;
  }

  @UpdatableProperty
  public void setValidityTime(ZonedDateTime validityTime) {
    this.validityTime = validityTime;
  }

  @Schema(description = "Id of the contact person who ordered the cable report.  ")
  public Integer getOrderer() {
    return orderer;
  }

  @UpdatableProperty
  public void setOrderer(Integer orderer) {
    this.orderer = orderer;
  }
}
