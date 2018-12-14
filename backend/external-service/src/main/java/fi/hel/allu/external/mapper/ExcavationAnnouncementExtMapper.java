package fi.hel.allu.external.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.external.domain.ExcavationAnnouncementOutExt;
import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
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
    extension.setAdditionalInfo(excavationAnnouncement.getAdditionalInfo());
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
    clientApplicationData.setContractor(customerMapper
        .mapCustomerWithContactsJson(cableReport.getContractorWithContacts(), CustomerRoleType.CONTRACTOR));
  }

  @Override
  protected String getClientApplicationKind(ExcavationAnnouncementExt excavationAnnouncement) {
    return excavationAnnouncement.getClientApplicationKind();
  }

  public ExcavationAnnouncementOutExt mapApplicationJson(ApplicationJson application) {
    if (application.getType() != ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      throw new IllegalOperationException("applicationtype.invalid");
    }
    ExcavationAnnouncementOutExt excavation = new ExcavationAnnouncementOutExt();
    setCommonFields(application, excavation);
    ExcavationAnnouncementJson extension = (ExcavationAnnouncementJson)application.getExtension();
    excavation.setOperationalConditionDate(extension.getWinterTimeOperation());
    excavation.setWorkFinishedDate(extension.getWorkFinished());
    excavation.setReportedOperationalConditionDate(extension.getCustomerWinterTimeOperation());
    excavation.setReportedWorkFinishedDate(extension.getCustomerWorkFinished());
    excavation.setReportedStartTime(extension.getCustomerStartTime());
    excavation.setReportedEndTime(extension.getCustomerEndTime());
    excavation.setWarrantyEndTime(extension.getGuaranteeEndTime());
    excavation.setCompactionAndBearingCapacityMeasurement(BooleanUtils.isTrue(extension.getCompactionAndBearingCapacityMeasurement()));
    excavation.setQualityAssuranceTest(BooleanUtils.isTrue(extension.getQualityAssuranceTest()));
    excavation.setWorkPurpose(extension.getWorkPurpose());
    excavation.setAdditionalInfo(extension.getAdditionalInfo());
    excavation.setCableReports(extension.getCableReports());
    excavation.setPlacementContracts(extension.getPlacementContracts());
    excavation.setSelfSupervision(BooleanUtils.isTrue(extension.getSelfSupervision()));
    excavation.setEmergencyWork(BooleanUtils.isTrue(extension.getEmergencyWork()));
    excavation.setTrafficArrangements(extension.getTrafficArrangements());
    return excavation;
  }

}
