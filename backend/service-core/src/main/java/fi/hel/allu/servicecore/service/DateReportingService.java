package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.SupervisionDates;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.GuaranteeEndTime;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  private static final Logger logger = LoggerFactory.getLogger(DateReportingService.class);

  private final ApplicationService applicationService;
  private final ApplicationJsonService applicationJsonService;
  private final SupervisionTaskService supervisionTaskService;
  private final ApplicationServiceComposer applicationServiceComposer;
  private final LocationService locationService;
  private final ApplicationHistoryService applicationHistoryService;
  private final InvoicingPeriodService invoicingPeriodService;

  @Autowired
  public DateReportingService(
      ApplicationService applicationService,
      ApplicationJsonService applicationJsonService,
      SupervisionTaskService supervisionTaskService,
      ApplicationServiceComposer applicationServiceComposer,
      LocationService locationService,
      ApplicationHistoryService applicationHistoryService,
      InvoicingPeriodService invoicingPeriodService) {
    this.applicationService = applicationService;
    this.applicationJsonService = applicationJsonService;
    this.supervisionTaskService = supervisionTaskService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.locationService = locationService;
    this.applicationHistoryService = applicationHistoryService;
    this.invoicingPeriodService = invoicingPeriodService;
  }

  public ApplicationJson reportCustomerOperationalCondition(Integer id, ApplicationDateReport dateReport) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);

    final Application newApplication = applicationService.setCustomerOperationalConditionDates(id, dateReport);
    if (supervisionTaskService.hasSupervisionTask(id, SupervisionTaskType.OPERATIONAL_CONDITION)) {
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

    final Application newApplication = applicationService.setCustomerValidityDates(id, dateReport);
    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(newApplication);

    applicationServiceComposer.addTag(id, new ApplicationTagJson(null, ApplicationTagType.DATE_CHANGE, null));
    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);
    return newApplicationJson;
  }

  public ApplicationJson reportOperationalCondition(Integer id, ZonedDateTime operationalConditionDate) {
    final ApplicationJson oldApplicationJson = getApplicationJson(id);

    applicationService.setOperationalConditionDate(id, operationalConditionDate);
    final Application newApplication = applicationService.setTargetState(id, StatusType.OPERATIONAL_CONDITION);
    final ApplicationJson newApplicationJson = applicationJsonService.getFullyPopulatedApplication(newApplication);

    applicationHistoryService.addFieldChanges(id, oldApplicationJson, newApplicationJson);
    return newApplicationJson;
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
        .stream()
        .filter(t -> t.getType() == SupervisionTaskType.WORK_TIME_SUPERVISION &&
                t.getStatus() == SupervisionTaskStatusType.OPEN)
        .count() > 0;
  }

  private void adjustLocationEndDates(int applicationId, ZonedDateTime date) {
    final ZonedDateTime firstPossibleEndDate = firstAllowedInvoicingDate(applicationId);
    if (firstPossibleEndDate == null || date.isBefore(firstPossibleEndDate)) {
      throw new IllegalArgumentException("workfinisheddate.invoiced.invoicing.period");
    }

    final ApplicationJson application = getApplicationJson(applicationId);
    final List<LocationJson> locationsEndingAfter = application.getLocations().stream()
        .filter(l -> l.getEndTime().isAfter(date))
        .collect(Collectors.toList());
    if (!locationsEndingAfter.isEmpty()) {
      if (locationsEndingAfter.stream()
          .filter(l -> l.getStartTime().isAfter(date)).count() > 0) {
        throw new IllegalArgumentException("workfinisheddate.before.area.start");
      }
      locationsEndingAfter.stream().forEach(l -> l.setEndTime(date));
      applicationServiceComposer.updateApplication(application.getId(), application);
    }
  }

  private ZonedDateTime firstAllowedInvoicingDate(int applicationId) {
    final List<InvoicingPeriod> periods = invoicingPeriodService.getInvoicingPeriods(applicationId);
    if (periods.isEmpty()) {
      // No periods -> applications is not periodized -> periodization doesn't limit start time
      return ZonedDateTime.of(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC), TimeUtil.HelsinkiZoneId);
    }

    return periods.stream()
        .filter(p -> !p.isInvoiced())
        .min(Comparator.comparing(InvoicingPeriod::getStartTime))
        .map(p -> p.getStartTime())
        .orElse(null);
  }

  private void createOperationalConditionSupervisionTask(Application application, ZonedDateTime reportedDate) {
    UserJson supervisionTaskOwner = getSupervisionTaskOwner(application);
    supervisionTaskService.insert(new SupervisionTaskJson(null, application.getId(), SupervisionTaskType.OPERATIONAL_CONDITION, null,
            supervisionTaskOwner, null, SupervisionDates.operationalConditionSupervisionDate(reportedDate),
            null, SupervisionTaskStatusType.OPEN, null, null, null));
  }

  private UserJson getSupervisionTaskOwner(Application application) {
    return locationService.findSupervisionTaskOwner(application);
  }

  private ApplicationJson getApplicationJson(Integer id) {
    final Application oldApplication = applicationService.findApplicationById(id);
    return applicationJsonService.getFullyPopulatedApplication(oldApplication);
  }
}
