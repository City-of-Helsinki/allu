package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;

public class ExcavationAnnouncementMapper {
  public static ExcavationAnnouncementJson modelToJson(ExcavationAnnouncement excavationAnnouncement) {
    ExcavationAnnouncementJson json = new ExcavationAnnouncementJson();
    json.setAdditionalInfo(excavationAnnouncement.getAdditionalInfo());
    json.setCableReportId(excavationAnnouncement.getCableReportId());
    json.setGuaranteeEndTime(excavationAnnouncement.getGuaranteeEndTime());
    json.setSummerTimeOperation(excavationAnnouncement.getSummerTimeOperation());
    json.setWinterTimeOperation(excavationAnnouncement.getWinterTimeOperation());
    json.setWorkFinished(excavationAnnouncement.getWorkFinished());
    json.setTrafficArrangements(excavationAnnouncement.getTrafficArrangements());
    json.setTrafficArrangementImpedimentType(excavationAnnouncement.getTrafficArrangementImpedimentType());
    json.setPksCard(excavationAnnouncement.getPksCard());
    json.setConstructionWork(excavationAnnouncement.getConstructionWork());
    json.setMaintenanceWork(excavationAnnouncement.getMaintenanceWork());
    json.setEmergencyWork(excavationAnnouncement.getEmergencyWork());
    json.setPropertyConnectivity(excavationAnnouncement.getPropertyConnectivity());
    json.setUnauthorizedWorkStartTime(excavationAnnouncement.getUnauthorizedWorkStartTime());
    json.setUnauthorizedWorkEndTime(excavationAnnouncement.getUnauthorizedWorkEndTime());
    return ApplicationExtensionMapper.modelToJson(excavationAnnouncement, json);
  }

  public static ExcavationAnnouncement jsonToModel(ExcavationAnnouncementJson json) {
    ExcavationAnnouncement excavationAnnouncement = new ExcavationAnnouncement();
    excavationAnnouncement.setAdditionalInfo(json.getAdditionalInfo());
    excavationAnnouncement.setCableReportId(json.getCableReportId());
    excavationAnnouncement.setGuaranteeEndTime(json.getGuaranteeEndTime());
    excavationAnnouncement.setSummerTimeOperation(json.getSummerTimeOperation());
    excavationAnnouncement.setWinterTimeOperation(json.getWinterTimeOperation());
    excavationAnnouncement.setWorkFinished(json.getWorkFinished());
    excavationAnnouncement.setTrafficArrangements(json.getTrafficArrangements());
    excavationAnnouncement.setTrafficArrangementImpedimentType(json.getTrafficArrangementImpedimentType());
    excavationAnnouncement.setPksCard(json.getPksCard());
    excavationAnnouncement.setConstructionWork(json.getConstructionWork());
    excavationAnnouncement.setMaintenanceWork(json.getMaintenanceWork());
    excavationAnnouncement.setEmergencyWork(json.getEmergencyWork());
    excavationAnnouncement.setPropertyConnectivity(json.getPropertyConnectivity());
    excavationAnnouncement.setUnauthorizedWorkStartTime(json.getUnauthorizedWorkStartTime());
    excavationAnnouncement.setUnauthorizedWorkEndTime(json.getUnauthorizedWorkEndTime());
    return ApplicationExtensionMapper.jsonToModel(json, excavationAnnouncement);
  }
}