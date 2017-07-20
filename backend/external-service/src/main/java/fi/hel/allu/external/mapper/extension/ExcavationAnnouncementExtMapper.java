package fi.hel.allu.external.mapper.extension;

import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;

public class ExcavationAnnouncementExtMapper {
  public static ExcavationAnnouncementJson extToJson(ExcavationAnnouncementExt excavationAnnouncementExt) {
    ExcavationAnnouncementJson excavationAnnouncementJson = new ExcavationAnnouncementJson();
    excavationAnnouncementJson.setPksCard(excavationAnnouncementExt.getPksCard());
    excavationAnnouncementJson.setConstructionWork(excavationAnnouncementExt.getConstructionWork());
    excavationAnnouncementJson.setMaintenanceWork(excavationAnnouncementExt.getMaintenanceWork());
    excavationAnnouncementJson.setEmergencyWork(excavationAnnouncementExt.getEmergencyWork());
    excavationAnnouncementJson.setPropertyConnectivity(excavationAnnouncementExt.getPropertyConnectivity());
    excavationAnnouncementJson.setCableReportId(excavationAnnouncementExt.getCableReportId());
    excavationAnnouncementJson.setAdditionalInfo(excavationAnnouncementExt.getAdditionalInfo());
    excavationAnnouncementJson.setTrafficArrangements(excavationAnnouncementExt.getTrafficArrangements());
    excavationAnnouncementJson.setTrafficArrangementImpedimentType(excavationAnnouncementExt.getTrafficArrangementImpedimentType());
    return ApplicationExtensionExtMapper.modelToJson(excavationAnnouncementExt, excavationAnnouncementJson);
  }

  public static ExcavationAnnouncementExt jsonToExt(ExcavationAnnouncementJson excavationAnnouncementJson) {
    ExcavationAnnouncementExt excavationAnnouncementExt = new ExcavationAnnouncementExt();
    excavationAnnouncementExt.setPksCard(excavationAnnouncementJson.getPksCard());
    excavationAnnouncementExt.setConstructionWork(excavationAnnouncementJson.getConstructionWork());
    excavationAnnouncementExt.setMaintenanceWork(excavationAnnouncementJson.getMaintenanceWork());
    excavationAnnouncementExt.setEmergencyWork(excavationAnnouncementJson.getEmergencyWork());
    excavationAnnouncementExt.setPropertyConnectivity(excavationAnnouncementJson.getPropertyConnectivity());
    excavationAnnouncementExt.setCableReportId(excavationAnnouncementJson.getCableReportId());
    excavationAnnouncementExt.setAdditionalInfo(excavationAnnouncementJson.getAdditionalInfo());
    excavationAnnouncementExt.setTrafficArrangements(excavationAnnouncementJson.getTrafficArrangements());
    excavationAnnouncementExt.setTrafficArrangementImpedimentType(excavationAnnouncementJson.getTrafficArrangementImpedimentType());
    return ApplicationExtensionExtMapper.jsonToModel(excavationAnnouncementJson, excavationAnnouncementExt);
  }
}
