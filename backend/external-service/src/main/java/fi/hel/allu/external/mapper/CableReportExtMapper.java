package fi.hel.allu.external.mapper;

import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.external.domain.CableReportExt;
import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;
import fi.hel.allu.servicecore.domain.CableReportJson;
import fi.hel.allu.servicecore.domain.ClientApplicationDataJson;

@Component
public class CableReportExtMapper extends ApplicationExtMapper<CableReportExt> {

  @Override
  protected ApplicationExtensionJson createExtension(CableReportExt cableReport) {
    CableReportJson extension = new CableReportJson();
    extension.setWorkDescription(cableReport.getWorkDescription());
    extension.setConstructionWork(cableReport.getConstructionWork());
    extension.setMaintenanceWork(cableReport.getMaintenanceWork());
    extension.setEmergencyWork(cableReport.getEmergencyWork());
    extension.setPropertyConnectivity(cableReport.getPropertyConnectivity());
    return extension;
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.CABLE_REPORT;
  }

  @Override
  protected void addApplicationTypeSpecificData(CableReportExt cableReport,
      ClientApplicationDataJson clientApplicationData) {
    clientApplicationData.setContractor(customerMapper
        .mapCustomerWithContactsJson(cableReport.getContractorWithContacts(), CustomerRoleType.CONTRACTOR));
    clientApplicationData.setPropertyDeveloper(customerMapper.mapCustomerWithContactsJson(
        cableReport.getPropertyDeveloperWithContacts(), CustomerRoleType.PROPERTY_DEVELOPER));
  }

  @Override
  protected String getClientApplicationKind(CableReportExt application) {
    return application.getClientApplicationKind();
  }
}
