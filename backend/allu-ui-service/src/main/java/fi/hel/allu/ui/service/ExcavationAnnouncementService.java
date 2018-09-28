package fi.hel.allu.ui.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.ExcavationAnnouncementDates;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.WinterTime;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.service.*;

@Service
public class ExcavationAnnouncementService {

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private ApplicationJsonService applicationJsonService;

  @Autowired
  private SupervisionTaskService supervisionTaskService;

  public ApplicationJson reportCustomerOperationalCondition(Integer id, ApplicationDateReport dateReport) {
    Application application = applicationService.setCustomerOperationalConditionDates(id, dateReport);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.OPERATIONAL_CONDITION,
        ExcavationAnnouncementDates.operationalConditionSupervisionDate(dateReport.getReportedDate()));
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson reportCustomerWorkFinished(Integer id, ApplicationDateReport dateReport) {
    Application application = applicationService.setCustomerWorkFinishedDates(id,
        dateReport);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.FINAL_SUPERVISION,
        ExcavationAnnouncementDates.finalSupervisionDate(dateReport.getReportedDate()));
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
