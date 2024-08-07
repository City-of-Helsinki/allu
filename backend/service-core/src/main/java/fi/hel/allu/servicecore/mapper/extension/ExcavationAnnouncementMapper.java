package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;

public class ExcavationAnnouncementMapper {
  public static ExcavationAnnouncementJson modelToJson(ExcavationAnnouncement excavationAnnouncement) {
    ExcavationAnnouncementJson json = new ExcavationAnnouncementJson();
    json.setWorkPurpose(excavationAnnouncement.getWorkPurpose());
    json.setAdditionalInfo(excavationAnnouncement.getAdditionalInfo());
    json.setGuaranteeEndTime(excavationAnnouncement.getGuaranteeEndTime());
    json.setWinterTimeOperation(excavationAnnouncement.getWinterTimeOperation());
    json.setWorkFinished(excavationAnnouncement.getWorkFinished());
    json.setCustomerStartTime(excavationAnnouncement.getCustomerStartTime());
    json.setCustomerEndTime(excavationAnnouncement.getCustomerEndTime());
    json.setCustomerWinterTimeOperation(excavationAnnouncement.getCustomerWinterTimeOperation());
    json.setCustomerWorkFinished(excavationAnnouncement.getCustomerWorkFinished());
    json.setOperationalConditionReported(excavationAnnouncement.getOperationalConditionReported());
    json.setWorkFinishedReported(excavationAnnouncement.getWorkFinishedReported());
    json.setValidityReported(excavationAnnouncement.getValidityReported());
    json.setTrafficArrangements(excavationAnnouncement.getTrafficArrangements());
    json.setTrafficArrangementImpedimentType(excavationAnnouncement.getTrafficArrangementImpedimentType());
    json.setPksCard(excavationAnnouncement.getPksCard());
    json.setConstructionWork(excavationAnnouncement.getConstructionWork());
    json.setMaintenanceWork(excavationAnnouncement.getMaintenanceWork());
    json.setEmergencyWork(excavationAnnouncement.getEmergencyWork());
    json.setPropertyConnectivity(excavationAnnouncement.getPropertyConnectivity());
    json.setSelfSupervision(excavationAnnouncement.getSelfSupervision());
    json.setCompactionAndBearingCapacityMeasurement(excavationAnnouncement.getCompactionAndBearingCapacityMeasurement());
    json.setQualityAssuranceTest(excavationAnnouncement.getQualityAssuranceTest());
    json.setUnauthorizedWorkStartTime(excavationAnnouncement.getUnauthorizedWorkStartTime());
    json.setUnauthorizedWorkEndTime(excavationAnnouncement.getUnauthorizedWorkEndTime());
    json.setPlacementContracts(excavationAnnouncement.getPlacementContracts());
    json.setCableReports(excavationAnnouncement.getCableReports());
    return ApplicationExtensionMapper.modelToJson(excavationAnnouncement, json);
  }

  public static ExcavationAnnouncement jsonToModel(ExcavationAnnouncementJson json) {
    ExcavationAnnouncement excavationAnnouncement = new ExcavationAnnouncement();
    excavationAnnouncement.setWorkPurpose(json.getWorkPurpose());
    excavationAnnouncement.setAdditionalInfo(json.getAdditionalInfo());
    excavationAnnouncement.setGuaranteeEndTime(json.getGuaranteeEndTime());
    excavationAnnouncement.setWinterTimeOperation(json.getWinterTimeOperation());
    excavationAnnouncement.setWorkFinished(json.getWorkFinished());
    excavationAnnouncement.setCustomerStartTime(json.getCustomerStartTime());
    excavationAnnouncement.setCustomerEndTime(json.getCustomerEndTime());
    excavationAnnouncement.setCustomerWinterTimeOperation(json.getCustomerWinterTimeOperation());
    excavationAnnouncement.setCustomerWorkFinished(json.getCustomerWorkFinished());
    excavationAnnouncement.setOperationalConditionReported(json.getOperationalConditionReported());
    excavationAnnouncement.setWorkFinishedReported(json.getWorkFinishedReported());
    excavationAnnouncement.setValidityReported(json.getValidityReported());
    excavationAnnouncement.setTrafficArrangements(json.getTrafficArrangements());
    excavationAnnouncement.setTrafficArrangementImpedimentType(json.getTrafficArrangementImpedimentType());
    excavationAnnouncement.setPksCard(json.getPksCard());
    excavationAnnouncement.setConstructionWork(json.getConstructionWork());
    excavationAnnouncement.setMaintenanceWork(json.getMaintenanceWork());
    excavationAnnouncement.setEmergencyWork(json.getEmergencyWork());
    excavationAnnouncement.setPropertyConnectivity(json.getPropertyConnectivity());
    excavationAnnouncement.setSelfSupervision(json.getSelfSupervision());
    excavationAnnouncement.setCompactionAndBearingCapacityMeasurement(json.getCompactionAndBearingCapacityMeasurement());
    excavationAnnouncement.setQualityAssuranceTest(json.getQualityAssuranceTest());
    excavationAnnouncement.setUnauthorizedWorkStartTime(json.getUnauthorizedWorkStartTime());
    excavationAnnouncement.setUnauthorizedWorkEndTime(json.getUnauthorizedWorkEndTime());
    excavationAnnouncement.setPlacementContracts(json.getPlacementContracts());
    excavationAnnouncement.setCableReports(json.getCableReports());
    return ApplicationExtensionMapper.jsonToModel(json, excavationAnnouncement);
  }
}