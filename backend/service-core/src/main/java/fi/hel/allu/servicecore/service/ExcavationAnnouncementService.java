package fi.hel.allu.servicecore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;

@Service
public class ExcavationAnnouncementService {

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private ApplicationJsonService applicationJsonService;

  public ApplicationJson setRequiredTasks(Integer id, RequiredTasks requiredTasks) {
    applicationService.setRequiredTasks(id, requiredTasks);
    Application application = applicationService.findApplicationById(id);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }
}
