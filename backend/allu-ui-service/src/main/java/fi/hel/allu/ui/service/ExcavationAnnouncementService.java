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
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private ApplicationJsonService applicationJsonService;

  @Autowired
  private SupervisionTaskService supervisionTaskService;

  @Autowired
  private ConfigurationService configurationService;

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
    boolean requiresDecision = requiresDecisionOnOperationalCondition(application, operationalConditionDate);
    applicationService.setOperationalConditionDate(id, operationalConditionDate);
    if (!requiresDecision) {
      setInvoicableTime(id, operationalConditionDate);
    }
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  private boolean requiresDecisionOnOperationalCondition(Application application, ZonedDateTime operationalConditionDate) {
    // If operational condition date differs from original or invoice rows have been added after previous decision
    // application requires new decision
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    return !TimeUtil.isSameDate(extension.getWinterTimeOperation(), operationalConditionDate) || application.isInvoicingChanged();
  }

  public ApplicationJson reportWorkFinished(Integer id, ZonedDateTime workFinishedDate) {
    Application application = applicationService.findApplicationById(id);
    boolean requiresDecision = requiresDecisionOnWorkFinished(application, workFinishedDate);
    applicationService.setWorkFinishedDate(id, workFinishedDate);
    if (!requiresDecision) {
      setInvoicableTime(id, workFinishedDate);
    }
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.WARRANTY,
        ExcavationAnnouncementDates.warrantySupervisionDate(workFinishedDate));
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public ApplicationJson setRequiredTasks(Integer id, RequiredTasks requiredTasks) {
    applicationService.setRequiredTasks(id, requiredTasks);
    Application application = applicationService.findApplicationById(id);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  private boolean requiresDecisionOnWorkFinished(Application application, ZonedDateTime workFinishedDate) {
    // If work finished date differs from original application end time and its not winter time operation ending before winter end
    // or invoice rows have been added after previous decision application requires new decision
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    boolean winterTimeFinishedBeforeWinterEnd = false;
    if (extension.getWinterTimeOperation() != null) {
      winterTimeFinishedBeforeWinterEnd = TimeUtil.isSameDateOrLater(getWinterTimeEnd(extension.getWinterTimeOperation()), workFinishedDate);
    }
    boolean finishedAsPlanned = TimeUtil.isSameDate(application.getEndTime(), workFinishedDate) || winterTimeFinishedBeforeWinterEnd;
    return !finishedAsPlanned || application.isInvoicingChanged();
  }

  private ZonedDateTime getWinterTimeEnd(ZonedDateTime date) {
    WinterTime winterTime = new WinterTime(
        LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.WINTER_TIME_START)),
        LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.WINTER_TIME_END)));
    return winterTime.getWinterTimeEnd(date).atStartOfDay(TimeUtil.HelsinkiZoneId);
  }

  private void setInvoicableTime(Integer id, ZonedDateTime invoicableTime) {
    applicationService.setInvoicableTime(id, invoicableTime);
  }
}
