package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class AreaRentalService {
  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private ApplicationJsonService applicationJsonService;

  public ApplicationJson reportWorkFinished(Integer id, ZonedDateTime workFinishedDate) {
    applicationService.setWorkFinishedDate(id, ApplicationType.AREA_RENTAL, workFinishedDate);
    Application application = applicationService.setTargetState(id, StatusType.FINISHED);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }
}
