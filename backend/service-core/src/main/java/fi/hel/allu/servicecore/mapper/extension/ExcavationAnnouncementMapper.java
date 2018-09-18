package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;

public class ExcavationAnnouncementMapper {
  public static ExcavationAnnouncementJson modelToJson(ExcavationAnnouncement excavationAnnouncement) {
    ExcavationAnnouncementJson json = new ExcavationAnnouncementJson();
    json.setWorkPurpose(excavationAnnouncement.getWorkPurpose());
    json.setCableReportId(excavationAnnouncement.getCableReportId());
    json.setGuaranteeEndTime(excavationAnnouncement.getGuaranteeEndTime());
    json.setWinterTimeOperation(excavationAnnouncement.getWinterTimeOperation());
    json.setWorkFinished(excavationAnnouncement.getWorkFinished());
    json.setCustomerStartTime(excavationAnnouncement.getCustomerStartTime());
    json.setCustomerEndTime(excavationAnnouncement.getCustomerEndTime());
    json.setCustomerWinterTimeOperation(excavationAnnouncement.getCustomerWinterTimeOperation());
    json.setCustomerWorkFinished(excavationAnnouncement.getCustomerWorkFinished());
    json.setOperationalConditionReported(excavationAnnouncement.getOperationalConditionReported());
    json.setWorkFinishedReported(excavationAnnouncement.getWorkFinishedReported());
    json.setTrafficArrangements(excavationAnnouncement.getTrafficArrangements());
    json.setTrafficArrangementImpedimentType(excavationAnnouncement.getTrafficArrangementImpedimentType());
    json.setPksCard(excavationAnnouncement.getPksCard());
    json.setConstructionWork(excavationAnnouncement.getConstructionWork());
    json.setMaintenanceWork(excavationAnnouncement.getMaintenanceWork());
    json.setEmergencyWork(excavationAnnouncement.getEmergencyWork());
    json.setPropertyConnectivity(excavationAnnouncement.getPropertyConnectivity());
    json.setCompactionAndBearingCapacityMeasurement(excavationAnnouncement.getCompactionAndBearingCapacityMeasurement());
    json.setQualityAssuranceTest(excavationAnnouncement.getQualityAssuranceTest());
    json.setUnauthorizedWorkStartTime(excavationAnnouncement.getUnauthorizedWorkStartTime());
    json.setUnauthorizedWorkEndTime(excavationAnnouncement.getUnauthorizedWorkEndTime());
    return ApplicationExtensionMapper.modelToJson(excavationAnnouncement, json);
  }

  public static ExcavationAnnouncement jsonToModel(ExcavationAnnouncementJson json) {
    ExcavationAnnouncement excavationAnnouncement = new ExcavationAnnouncement();
    excavationAnnouncement.setWorkPurpose(json.getWorkPurpose());
    excavationAnnouncement.setCableReportId(json.getCableReportId());
    excavationAnnouncement.setGuaranteeEndTime(json.getGuaranteeEndTime());
    excavationAnnouncement.setWinterTimeOperation(json.getWinterTimeOperation());
    excavationAnnouncement.setWorkFinished(json.getWorkFinished());
    excavationAnnouncement.setCustomerStartTime(json.getCustomerStartTime());
    excavationAnnouncement.setCustomerEndTime(json.getCustomerEndTime());
    excavationAnnouncement.setCustomerWinterTimeOperation(json.getCustomerWinterTimeOperation());
    excavationAnnouncement.setCustomerWorkFinished(json.getCustomerWorkFinished());
    excavationAnnouncement.setOperationalConditionReported(json.getOperationalConditionReported());
    excavationAnnouncement.setWorkFinishedReported(json.getWorkFinishedReported());
    excavationAnnouncement.setTrafficArrangements(json.getTrafficArrangements());
    excavationAnnouncement.setTrafficArrangementImpedimentType(json.getTrafficArrangementImpedimentType());
    excavationAnnouncement.setPksCard(json.getPksCard());
    excavationAnnouncement.setConstructionWork(json.getConstructionWork());
    excavationAnnouncement.setMaintenanceWork(json.getMaintenanceWork());
    excavationAnnouncement.setEmergencyWork(json.getEmergencyWork());
    excavationAnnouncement.setPropertyConnectivity(json.getPropertyConnectivity());
    excavationAnnouncement.setCompactionAndBearingCapacityMeasurement(json.getCompactionAndBearingCapacityMeasurement());
    excavationAnnouncement.setQualityAssuranceTest(json.getQualityAssuranceTest());
    excavationAnnouncement.setUnauthorizedWorkStartTime(json.getUnauthorizedWorkStartTime());
    excavationAnnouncement.setUnauthorizedWorkEndTime(json.getUnauthorizedWorkEndTime());
    return ApplicationExtensionMapper.jsonToModel(json, excavationAnnouncement);
  }
}