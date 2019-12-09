package fi.hel.allu.external.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.external.domain.TrafficArrangementExt;
import fi.hel.allu.servicecore.domain.ApplicationExtensionJson;
import fi.hel.allu.servicecore.domain.ClientApplicationDataJson;
import fi.hel.allu.servicecore.domain.TrafficArrangementJson;
import org.springframework.stereotype.Component;

@Component
public class TrafficArrangementExtMapper extends ApplicationExtMapper<TrafficArrangementExt> {

  @Override
  protected ApplicationExtensionJson createExtension(TrafficArrangementExt excavationAnnouncement) {
    TrafficArrangementJson extension = new TrafficArrangementJson();
    extension.setWorkPurpose(excavationAnnouncement.getWorkPurpose());
    extension.setTrafficArrangements(excavationAnnouncement.getTrafficArrangements());
    extension.setTrafficArrangementImpedimentType(excavationAnnouncement.getTrafficArrangementImpediment());
    return extension;
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS;
  }

  @Override
  protected void addApplicationTypeSpecificData(TrafficArrangementExt trafficArrangement,
      ClientApplicationDataJson clientApplicationData) {
    clientApplicationData.setContractor(customerMapper
        .mapCustomerWithContactsJson(trafficArrangement.getContractorWithContacts(), CustomerRoleType.CONTRACTOR));
    clientApplicationData.setPropertyDeveloper(customerMapper
      .mapCustomerWithContactsJson(trafficArrangement.getPropertyDeveloperWithContacts(), CustomerRoleType.PROPERTY_DEVELOPER));
  }

  @Override
  protected ApplicationKind getApplicationKind(TrafficArrangementExt trafficArrangement) {
    return trafficArrangement.getApplicationKind();
  }
}
