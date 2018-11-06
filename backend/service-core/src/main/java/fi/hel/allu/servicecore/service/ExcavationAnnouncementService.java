package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.ExcavationAnnouncementDates;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

@Service
public class ExcavationAnnouncementService {

  private static final Logger logger = LoggerFactory.getLogger(ExcavationAnnouncementService.class);

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private ApplicationJsonService applicationJsonService;

  @Autowired
  private SupervisionTaskService supervisionTaskService;

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private LocationService locationService;

  @Autowired
  private ApplicationHistoryService applicationHistoryService;

  public ApplicationJson reportCustomerOperationalCondition(Integer id, ApplicationDateReport dateReport) {
    Application application = applicationService.setCustomerOperationalConditionDates(id, dateReport);
    if (supervisionTaskService.hasSupervisionTask(id, SupervisionTaskType.OPERATIONAL_CONDITION)) {
      supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.OPERATIONAL_CONDITION,
          ExcavationAnnouncementDates.operationalConditionSupervisionDate(dateReport.getReportedDate()));
    } else {
      createOperationalConditionSupervisionTask(application, dateReport.getReportedDate());
    }
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
    final Application oldApplication = applicationService.findApplicationById(id);
    final ApplicationJson oldApplicationJson = applicationJsonService.getFullyPopulatedApplication(oldApplication);

    final Application newApplication = applicationService.setCustomerValidityDates(id, dateReport);
    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(newApplication);

    applicationServiceComposer.addTag(id, new ApplicationTagJson(null, ApplicationTagType.DATE_CHANGE, null));
    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);

    return newApplicationJson;
  }

  public ApplicationJson reportOperationalCondition(Integer id, ZonedDateTime operationalConditionDate) {
    applicationService.setOperationalConditionDate(id, operationalConditionDate);
    Application application = applicationService.setTargetState(id, StatusType.OPERATIONAL_CONDITION);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson reportWorkFinished(Integer id, ZonedDateTime workFinishedDate) {
    applicationService.setWorkFinishedDate(id, ApplicationType.EXCAVATION_ANNOUNCEMENT, workFinishedDate);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.WARRANTY,
        ExcavationAnnouncementDates.warrantySupervisionDate(workFinishedDate));
    Application application = applicationService.setTargetState(id, StatusType.FINISHED);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson setRequiredTasks(Integer id, RequiredTasks requiredTasks) {
    applicationService.setRequiredTasks(id, requiredTasks);
    Application application = applicationService.findApplicationById(id);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  private void createOperationalConditionSupervisionTask(Application application, ZonedDateTime reportedDate) {
    UserJson supervisionTaskOwner = getSupervisionTaskOwner(application);
    supervisionTaskService.insert(new SupervisionTaskJson(null, application.getId(), SupervisionTaskType.OPERATIONAL_CONDITION, null,
            supervisionTaskOwner, null, ExcavationAnnouncementDates.operationalConditionSupervisionDate(reportedDate),
            null, SupervisionTaskStatusType.OPEN, null, null, null));
  }

  private UserJson getSupervisionTaskOwner(Application application) {
    Integer cityDistrict = application.getLocations().get(0).getCityDistrictId();
    UserJson supervisionTaskOwner = null;
    if (cityDistrict != null) {
      try {
        supervisionTaskOwner = locationService.findSupervisionTaskOwner(application.getType(), cityDistrict);
      } catch (NoSuchEntityException e) {
        logger.warn("Didn't find supervisor for city district " + cityDistrict);
      }
    }
    return supervisionTaskOwner;
  }


}
