package fi.hel.allu.external.mapper;

import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;
import fi.hel.allu.servicecore.domain.ClientApplicationDataJson;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;

@Component
public class ExcavationAnnouncementExtMapper extends ApplicationExtMapper<ExcavationAnnouncementExt> {

  @Override
  protected ApplicationExtensionJson createExtension(ExcavationAnnouncementExt excavationAnnouncement) {
    ExcavationAnnouncementJson extension = new ExcavationAnnouncementJson();
    extension.setPksCard(excavationAnnouncement.getPksCard());
    extension.setConstructionWork(excavationAnnouncement.getConstructionWork());
    extension.setMaintenanceWork(excavationAnnouncement.getMaintenanceWork());
    extension.setEmergencyWork(excavationAnnouncement.getEmergencyWork());
    extension.setPropertyConnectivity(excavationAnnouncement.getPropertyConnectivity());
    extension.setSelfSupervision(excavationAnnouncement.getSelfSupervision());
    extension.setCableReportId(excavationAnnouncement.getCableReportId());
    extension.setWorkPurpose(excavationAnnouncement.getWorkPurpose());
    extension.setTrafficArrangements(excavationAnnouncement.getTrafficArrangements());
    extension.setTrafficArrangementImpedimentType(excavationAnnouncement.getTrafficArrangementImpediment());
    return extension;
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
  }

  @Override
  protected void addApplicationTypeSpecificData(ExcavationAnnouncementExt cableReport,
      ClientApplicationDataJson clientApplicationData) {
    clientApplicationData.setContractor(CustomerExtMapper
        .mapCustomerWithContactsJson(cableReport.getContractorWithContacts(), CustomerRoleType.CONTRACTOR));
  }

  @Override
  protected String getClientApplicationKind(ExcavationAnnouncementExt excavationAnnouncement) {
    return excavationAnnouncement.getClientApplicationKind();
  }

}
