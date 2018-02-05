package fi.hel.allu.servicecore.service;



import java.util.Optional;

import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.servicecore.domain.ApplicationJson;

@Service
public class ApplicationDraftService {

  private static final PublicityType DEFAULT_PUBLICITY_TYPE = PublicityType.PUBLIC;

  private ApplicationServiceComposer applicationServiceComposer;

  public ApplicationDraftService(ApplicationServiceComposer applicationServiceComposer) {
    this.applicationServiceComposer = applicationServiceComposer;
  }

  public ApplicationJson createDraft(ApplicationJson applicationJson) {
    applicationJson.setNotBillable(Optional.ofNullable(applicationJson.getNotBillable()).orElse(Boolean.FALSE));
    applicationJson.setDecisionPublicityType(Optional.ofNullable(applicationJson.getDecisionPublicityType()).orElse(DEFAULT_PUBLICITY_TYPE));
    return applicationServiceComposer.createDraft(applicationJson);
  }

  public ApplicationJson findById(int id) {
    return applicationServiceComposer.findApplicationById(id);
  }

  public ApplicationJson updateDraft(int id, ApplicationJson applicationJson) {
    return applicationServiceComposer.updateApplication(id, applicationJson);
  }

  public void deleteDraft(int id) {
    applicationServiceComposer.deleteDraft(id);
  }

  public ApplicationJson convertToApplication(int id, ApplicationJson applicationJson) {
    applicationServiceComposer.updateApplication(id, applicationJson);
    return applicationServiceComposer.changeStatus(id, StatusType.PENDING);

  }

}
