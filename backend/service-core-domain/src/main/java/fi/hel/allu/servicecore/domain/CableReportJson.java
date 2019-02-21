package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.ApplicationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Cable report specific fields")
public class CableReportJson extends ApplicationExtensionJson {

  private Boolean cableSurveyRequired;
  private String cableReportId;
  private String workDescription;
  private Integer mapExtractCount;
  private List<CableInfoEntryJson> infoEntries;
  private Boolean mapUpdated;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;
  private ZonedDateTime validityTime;
  private Integer orderer;

  @ApiModelProperty(hidden = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.CABLE_REPORT;
  }

  @ApiModelProperty(value = "True, if cable survey is required (johtokartoitettava)")
  public Boolean getCableSurveyRequired() {
    return cableSurveyRequired;
  }

  public void setCableSurveyRequired(Boolean cableSurveyRequired) {
    this.cableSurveyRequired = cableSurveyRequired;
  }

  @ApiModelProperty(value = "Cable report identifier (johtoselvitystunnus)")
  public String getCableReportId() {
    return cableReportId;
  }

  public void setCableReportId(String cableReportId) {
    this.cableReportId = cableReportId;
  }


  @ApiModelProperty(value = "Work description")
  public String getWorkDescription() {
    return workDescription;
  }

  public void setWorkDescription(String workDescription) {
    this.workDescription = workDescription;
  }

  @ApiModelProperty(value = "Map extract count")
  public Integer getMapExtractCount() {
    return mapExtractCount;
  }

  public void setMapExtractCount(Integer mapExtractCount) {
    this.mapExtractCount = mapExtractCount;
  }

  @ApiModelProperty(value = "Cable info entries (johtotiedot)")
  public List<CableInfoEntryJson> getInfoEntries() {
    return infoEntries;
  }

  public void setInfoEntries(List<CableInfoEntryJson> infoEntries) {
    this.infoEntries = infoEntries;
  }

  @ApiModelProperty(value = "True if map is updated")
  public Boolean getMapUpdated() {
    return mapUpdated;
  }

  public void setMapUpdated(Boolean mapUpdated) {
    this.mapUpdated = mapUpdated;
  }

  @ApiModelProperty(value = "Construction work")
  public Boolean getConstructionWork() {
    return constructionWork;
  }

  public void setConstructionWork(Boolean constructionWork) {
    this.constructionWork = constructionWork;
  }

  @ApiModelProperty(value = "Maintenance work")
  public Boolean getMaintenanceWork() {
    return maintenanceWork;
  }

  public void setMaintenanceWork(Boolean maintenanceWork) {
    this.maintenanceWork = maintenanceWork;
  }

  @ApiModelProperty(value = "Emergency work")
  public Boolean getEmergencyWork() {
    return emergencyWork;
  }

  public void setEmergencyWork(Boolean emergencyWork) {
    this.emergencyWork = emergencyWork;
  }

  @ApiModelProperty(value = "Property connectivity (tontti-/kiinteistöliitos)")
  public Boolean getPropertyConnectivity() {
    return propertyConnectivity;
  }

  public void setPropertyConnectivity(Boolean propertyConnectivity) {
    this.propertyConnectivity = propertyConnectivity;
  }

  @ApiModelProperty(value = "Validity time of the cable report")
  public ZonedDateTime getValidityTime() {
    return validityTime;
  }

  public void setValidityTime(ZonedDateTime validityTime) {
    this.validityTime = validityTime;
  }

  @ApiModelProperty(value = "Id of the contact person who ordered the cable report.  ")
  public Integer getOrderer() {
    return orderer;
  }

  public void setOrderer(Integer orderer) {
    this.orderer = orderer;
  }
}
