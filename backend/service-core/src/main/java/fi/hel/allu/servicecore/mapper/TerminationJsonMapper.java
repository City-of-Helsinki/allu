package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.pdf.domain.TerminationJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.domain.PlacementContractJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.servicecore.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TerminationJsonMapper extends AbstractDocumentMapper<TerminationJson> {

  @Autowired
  public TerminationJsonMapper(CustomerService customerService, ContactService contactService,
                               LocationService locationService) {
    super(customerService, contactService, locationService);
  }

  public TerminationJson mapToDocumentJson(ApplicationJson application, TerminationInfo terminationInfo, boolean draft) {
    TerminationJson termination = new TerminationJson();
    termination.setDraft(draft);
    termination.setApplicationType(application.getType());
    termination.setApplicationId(application.getApplicationId());
    termination.setCustomerAddressLines(customerAddressLines(application));
    termination.setCustomerContactLines(customerContactLines(application));
    termination.setSiteAddressLine(siteAddressLine(application));
    termination.setSiteCityDistrict(siteCityDistrict(application));
    termination.setDecisionDate(application.getDecisionTime());

    Optional.ofNullable(terminationInfo).ifPresent(info -> {
      termination.setExpirationTime(info.getExpirationTime());
      termination.setTerminationInfo(splitToList(Optional.ofNullable(info.getReason())));
    });

    Optional.ofNullable(application.getHandler()).ifPresent(handler -> {
      termination.setHandlerTitle(handler.getTitle());
      termination.setHandlerName(handler.getRealName());
    });

    Optional.ofNullable(application.getDecisionMaker()).ifPresent(decisionMaker -> {
      termination.setDeciderTitle(decisionMaker.getTitle());
      termination.setDeciderName(decisionMaker.getRealName());
    });

    String additionalInfos = String.join("; ",
        streamFor(application.getLocations())
            .map(LocationJson::getAdditionalInfo)
            .filter(p -> p != null)
            .map(p -> p.trim())
            .filter(p -> !p.isEmpty())
            .collect(Collectors.toList()));
    termination.setSiteAdditionalInfo(additionalInfos);

    fillTypeSpecifics(application, termination);
    convertNonBreakingSpacesToSpaces(termination);
    return termination;
  }

  private void fillTypeSpecifics(ApplicationJson application, TerminationJson termination) {
    switch (application.getType()) {
      case SHORT_TERM_RENTAL:
        termination.setName(application.getName());
        break;
      case PLACEMENT_CONTRACT:
        termination.setName(((PlacementContractJson)application.getExtension()).getAdditionalInfo());
        break;
    }
  }
}
