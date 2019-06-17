package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.pdf.domain.TerminationJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TerminationJsonMapper extends AbstractDocumentMapper<TerminationJson> {

  @Autowired
  public TerminationJsonMapper(CustomerService customerService, ContactService contactService) {
    super(customerService, contactService);
  }

  public TerminationJson mapToDocumentJson(ApplicationJson application, TerminationInfo terminationInfo, boolean draft) {
    TerminationJson termination = new TerminationJson();
    termination.setDraft(draft);
    termination.setApplicationType(application.getType());
    termination.setApplicationId(application.getApplicationId());
    termination.setCustomerAddressLines(customerAddressLines(application));
    termination.setCustomerContactLines(customerContactLines(application));

    Optional.ofNullable(terminationInfo).ifPresent(info -> {
      termination.setTerminationDate(info.getTerminationTime());
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

    convertNonBreakingSpacesToSpaces(termination);
    return termination;
  }
}
