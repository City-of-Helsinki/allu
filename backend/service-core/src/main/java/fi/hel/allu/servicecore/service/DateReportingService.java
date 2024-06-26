package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.common.util.SupervisionDates;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.GuaranteeEndTime;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class DateReportingService {

  private final ApplicationService applicationService;
  private final ApplicationJsonService applicationJsonService;
  private final SupervisionTaskService supervisionTaskService;
  private final ApplicationServiceComposer applicationServiceComposer;
  private final LocationService locationService;
  private final ApplicationHistoryService applicationHistoryService;
  private final InvoicingPeriodService invoicingPeriodService;
  private final ApplicationEventDispatcher applicationEventDispatcher;
  private final UserService userService;

  public DateReportingService(
    ApplicationService applicationService,
    ApplicationJsonService applicationJsonService,
    SupervisionTaskService supervisionTaskService,
    ApplicationServiceComposer applicationServiceComposer,
    LocationService locationService,
    ApplicationHistoryService applicationHistoryService,
    InvoicingPeriodService invoicingPeriodService,
    ApplicationEventDispatcher applicationEventDispatcher,
    UserService userService) {
    this.applicationService = applicationService;
    this.applicationJsonService = applicationJsonService;
    this.supervisionTaskService = supervisionTaskService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.locationService = locationService;
    this.applicationHistoryService = applicationHistoryService;
    this.invoicingPeriodService = invoicingPeriodService;
    this.applicationEventDispatcher = applicationEventDispatcher;
    this.userService = userService;
  }

  public ApplicationJson reportCustomerOperationalCondition(Integer id, ApplicationDateReport dateReport) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);

    final Application newApplication = applicationService.setCustomerOperationalConditionDates(id, dateReport);

    if (hasOpenOperationalConditionSupervision(id)) {
      supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.OPERATIONAL_CONDITION,
        SupervisionDates.operationalConditionSupervisionDate(dateReport.getReportedDate()));
    } else {
      createOperationalConditionSupervisionTask(newApplication, dateReport.getReportedDate());
    }
    applicationServiceComposer.addTag(id, new ApplicationTagJson(null, ApplicationTagType.OPERATIONAL_CONDITION_REPORTED, null));
    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(newApplication);
    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);
    return newApplicationJson;
  }

  private boolean hasOpenOperationalConditionSupervision(Integer id) {
    return supervisionTaskService.findByApplicationId(id).stream()
      .anyMatch(t -> t.getType() == SupervisionTaskType.OPERATIONAL_CONDITION && t.getStatus() == SupervisionTaskStatusType.OPEN);
  }

  public ApplicationJson reportCustomerWorkFinished(Integer id, ApplicationDateReport dateReport) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);

    final Application newApplication = applicationService.setCustomerWorkFinishedDates(id, dateReport);
    supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.FINAL_SUPERVISION,
      SupervisionDates.finalSupervisionDate(dateReport.getReportedDate()));
    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(newApplication);

    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);
    return newApplicationJson;
  }

  public ApplicationJson reportCustomerValidity(Integer id, ApplicationDateReport dateReport) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);
    validateApplicationNotAtFinalState(oldApplicationJson);
    final Application newApplication = applicationService.setCustomerValidityDates(id, dateReport);
    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(newApplication);

    applicationServiceComposer.addTag(id, new ApplicationTagJson(null, ApplicationTagType.DATE_CHANGE, null));
    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);
    ApplicationNotificationType type = userService.isExternalUser() ? ApplicationNotificationType.EXTERNAL_CUSTOMER_VALIDITY_PERIOD_CHANGED :
      ApplicationNotificationType.CUSTOMER_VALIDITY_PERIOD_CHANGED;
    applicationEventDispatcher.dispatchUpdateEvent(id, userService.getCurrentUser().getId(), type, newApplication.getStatus());
    return newApplicationJson;
  }

  private void validateApplicationNotAtFinalState(ApplicationJson application) {
    validateApplicationNotCancelled(application);
    validateApplicationNotFinished(application);
  }

  private void validateApplicationNotCancelled(ApplicationJson application) {
    if (application.getStatus() == StatusType.CANCELLED) {
      throw new IllegalOperationException("application.cancelled.notAllowed");
    }
  }

  private void validateApplicationNotFinished(ApplicationJson application) {
    if (application.getStatus() == StatusType.FINISHED || application.getStatus() == StatusType.ARCHIVED) {
      throw new IllegalOperationException("application.finished.notAllowed");
    }
  }

  public ApplicationJson reportOperationalCondition(Integer id, ZonedDateTime operationalConditionDate) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);
    applicationService.setOperationalConditionDate(id, operationalConditionDate);
    final Application newApplication = applicationService.setTargetState(id, StatusType.OPERATIONAL_CONDITION);
    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(newApplication);
    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);
    // If there was no previously set operational condition date for excavation announcement add
    // invoicing period for operational condition phase
    if (!hasOperationalConditionDate(oldApplicationJson)) {
      invoicingPeriodService.setPeriodsForExcavationAnnouncement(id);
    }
    return newApplicationJson;
  }

  private boolean hasOperationalConditionDate(ApplicationJson application) {
    return application.getExtension() instanceof ExcavationAnnouncementJson &&
      ((ExcavationAnnouncementJson) application.getExtension()).getWinterTimeOperation() != null;
  }

  public ApplicationJson reportWorkFinished(Integer id, ZonedDateTime workFinishedDate) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);

    adjustLocationEndDates(oldApplicationJson.getId(), workFinishedDate);
    applicationService.setWorkFinishedDate(id, workFinishedDate);
    Application application = applicationService.setTargetState(id, StatusType.FINISHED);

    if (application.getExtension() instanceof GuaranteeEndTime) {
      supervisionTaskService.updateSupervisionTaskDate(id, SupervisionTaskType.WARRANTY,
        SupervisionDates.warrantySupervisionDate(workFinishedDate));
    }

    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(application);

    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);
    return newApplicationJson;
  }

  public ApplicationJson reportCustomerLocationValidity(Integer id, Integer locationId, ApplicationDateReport dateReport) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);

    applicationService.setCustomerLocationValidity(id, locationId, dateReport);
    final ApplicationJson newApplicationJson = getApplicationJson(id);

    // Update also due date for supervision task of this location
    updateSupervisionTaskDate(id, locationId, dateReport.getReportedEndDate());

    applicationHistoryService.addLocationChanges(id,
      findLocation(locationId, oldApplicationJson.getLocations()),
      findLocation(locationId, newApplicationJson.getLocations()));

    ApplicationNotificationType type = userService.isExternalUser() ? ApplicationNotificationType.EXTERNAL_CUSTOMER_VALIDITY_PERIOD_CHANGED :
      ApplicationNotificationType.CUSTOMER_VALIDITY_PERIOD_CHANGED;
    applicationEventDispatcher.dispatchUpdateEvent(id, userService.getCurrentUser().getId(), type, newApplicationJson.getStatus());
    return newApplicationJson;
  }

  private LocationJson findLocation(Integer locationId, List<LocationJson> locations) {
    return locations.stream().filter(l -> Objects.equals(l.getId(), locationId)).findFirst().orElse(null);
  }

  private void updateSupervisionTaskDate(int applicationId, int locationId, ZonedDateTime endDate) {
    if (endDate != null && locationHasOpenSupervisionTask(locationId)) {
      if (endDate.isBefore(ZonedDateTime.now())) {
        endDate = ZonedDateTime.now().plusDays(1);
      } else {
        endDate = endDate.plusDays(1);
      }
      supervisionTaskService.updateSupervisionTaskDate(
        applicationId, SupervisionTaskType.WORK_TIME_SUPERVISION, locationId, endDate);
    }
  }

  private boolean locationHasOpenSupervisionTask(int locationId) {
    return supervisionTaskService.findByLocationId(locationId)
      .stream().anyMatch(t -> t.getType() == SupervisionTaskType.WORK_TIME_SUPERVISION &&
        t.getStatus() == SupervisionTaskStatusType.OPEN);
  }

  private void adjustLocationEndDates(int applicationId, ZonedDateTime date) {
    final ZonedDateTime firstPossibleEndDate = firstAllowedInvoicingDate(applicationId);
    if (isDateBefore(firstPossibleEndDate, date) && !isEndDate(applicationId, date)) {
      throw new IllegalArgumentException("workfinisheddate.invoiced.invoicing.period");
    }
    final ApplicationJson application = getApplicationJson(applicationId);
    final List<LocationJson> locationsEndingAfter = application.getLocations().stream()
      .filter(l -> l.getEndTime().isAfter(date))
      .collect(Collectors.toList());
    if (!locationsEndingAfter.isEmpty()) {
      if (locationsEndingAfter.stream().anyMatch(l -> l.getStartTime().isAfter(date))) {
        throw new IllegalArgumentException("workfinisheddate.before.area.start");
      }
      locationsEndingAfter.forEach(l -> l.setEndTime(date));
      applicationServiceComposer.updateApplication(application.getId(), application);
    }
  }

  private boolean isDateBefore(ZonedDateTime firstPossibleEndDate, ZonedDateTime date) {
    return firstPossibleEndDate == null || date.isBefore(firstPossibleEndDate);
  }

  private Boolean isEndDate(int applicationId, ZonedDateTime date) {
    List<InvoicingPeriod> endDates = invoicingPeriodService.getInvoicingPeriods(applicationId)
      .stream().filter(e -> e.getEndTime().toLocalDate().equals(date.toLocalDate()))
      .collect(Collectors.toList());

    return endDates.size() > 0;
  }

  private ZonedDateTime firstAllowedInvoicingDate(int applicationId) {
    final List<InvoicingPeriod> periods = invoicingPeriodService.getInvoicingPeriods(applicationId)
      .stream()
      .filter(p -> p.getStartTime() != null)
      .collect(Collectors.toList());
    if (periods.isEmpty()) {
      // No periods -> applications is not periodized -> periodization doesn't limit start time
      return ZonedDateTime.of(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC), TimeUtil.HelsinkiZoneId);
    }

    return periods.stream()
      .filter(p -> !p.isClosed())
      .min(Comparator.comparing(InvoicingPeriod::getStartTime))
      .map(InvoicingPeriod::getStartTime)
      .orElse(null);
  }

  private void createOperationalConditionSupervisionTask(Application application, ZonedDateTime reportedDate) {
    UserJson supervisionTaskOwner = getSupervisionTaskOwner(application);
    supervisionTaskService.insert(new SupervisionTaskJson(null, application.getId(), SupervisionTaskType.OPERATIONAL_CONDITION, null,
      supervisionTaskOwner, null, SupervisionDates.operationalConditionSupervisionDate(reportedDate),
      null, SupervisionTaskStatusType.OPEN, null, null, null, null));
  }

  private UserJson getSupervisionTaskOwner(Application application) {
    return locationService.findSupervisionTaskOwner(application);
  }

  private ApplicationJson getApplicationJson(Integer id) {
    final Application oldApplication = applicationService.findApplicationById(id);
    return applicationJsonService.getFullyPopulatedApplication(oldApplication);
  }
}
