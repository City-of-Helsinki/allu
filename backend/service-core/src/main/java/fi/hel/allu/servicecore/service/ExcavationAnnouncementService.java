package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.ExcavationAnnouncementDates;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;

@Service
public class ExcavationAnnouncementService {

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private ApplicationJsonService applicationJsonService;

  @Autowired
  private SupervisionTaskService supervisionTaskService;

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  public ApplicationJson reportCustomerOperationalCondition(Integer id, ApplicationDateReport dateReport) {
    Application application = applicationService.setCustomerOperationalConditionDates(id, dateReport);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.OPERATIONAL_CONDITION,
        ExcavationAnnouncementDates.operationalConditionSupervisionDate(dateReport.getReportedDate()));
    applicationServiceComposer.addTag(id, new ApplicationTagJson(null, ApplicationTagType.OPERATIONAL_CONDITION_REPORTED, null));
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson reportCustomerWorkFinished(Integer id, ApplicationDateReport dateReport) {
    Application application = applicationService.setCustomerWorkFinishedDates(id,
        dateReport);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.FINAL_SUPERVISION,
        ExcavationAnnouncementDates.finalSupervisionDate(dateReport.getReportedDate()));
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson reportCustomerValidity(Integer id, ApplicationDateReport dateReport) {
    Application application = applicationService.setCustomerValidityDates(id, dateReport);
    applicationServiceComposer.addTag(id, new ApplicationTagJson(null, ApplicationTagType.DATE_CHANGE, null));
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson reportOperationalCondition(Integer id, ZonedDateTime operationalConditionDate) {
    Application application = applicationService.findApplicationById(id);
    applicationService.setOperationalConditionDate(id, operationalConditionDate);
    application = applicationService.setTargetState(id, StatusType.OPERATIONAL_CONDITION);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson reportWorkFinished(Integer id, ZonedDateTime workFinishedDate) {
    Application application = applicationService.findApplicationById(id);
    applicationService.setWorkFinishedDate(id, workFinishedDate);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.WARRANTY,
        ExcavationAnnouncementDates.warrantySupervisionDate(workFinishedDate));
    application = applicationService.setTargetState(id, StatusType.FINISHED);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson setRequiredTasks(Integer id, RequiredTasks requiredTasks) {
    applicationService.setRequiredTasks(id, requiredTasks);
    Application application = applicationService.findApplicationById(id);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

}
