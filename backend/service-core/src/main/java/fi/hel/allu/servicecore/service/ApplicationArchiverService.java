package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.CableReport;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.servicecore.domain.CableReportJson;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;

/**
 * Moves applications to ready or archived if required conditions fulfill.
 *
 */
@Service
public class ApplicationArchiverService {

  private static final List<ApplicationType> TYPES_MOVED_TO_FINISHED = Arrays.asList(
      ApplicationType.CABLE_REPORT,
      ApplicationType.EVENT,
      ApplicationType.PLACEMENT_CONTRACT,
      ApplicationType.SHORT_TERM_RENTAL,
      ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS);

  private static final Map<ApplicationType, List<StatusType>> ARCHIVABLE_STATES = new HashMap<ApplicationType, List<StatusType>>() {{
    put(ApplicationType.PLACEMENT_CONTRACT, Collections.singletonList(StatusType.TERMINATED));
    put(ApplicationType.EXCAVATION_ANNOUNCEMENT, Arrays.asList(StatusType.FINISHED, StatusType.TERMINATED));
    put(ApplicationType.AREA_RENTAL, Arrays.asList(StatusType.FINISHED, StatusType.TERMINATED));
    put(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, Arrays.asList(StatusType.DECISION, StatusType.FINISHED, StatusType.TERMINATED));
    put(ApplicationType.CABLE_REPORT, Arrays.asList(StatusType.DECISION, StatusType.FINISHED, StatusType.TERMINATED));
    put(ApplicationType.EVENT, Arrays.asList(StatusType.DECISION, StatusType.FINISHED, StatusType.TERMINATED));
    put(ApplicationType.SHORT_TERM_RENTAL, Arrays.asList(StatusType.DECISION, StatusType.FINISHED, StatusType.TERMINATED));
  }};

  private static final Set<ApplicationTagType> DEPOSIT_TAG_TYPES = new HashSet<>(
      Arrays.asList(ApplicationTagType.DEPOSIT_PAID, ApplicationTagType.DEPOSIT_REQUESTED));
  private final ApplicationServiceComposer applicationServiceComposer;
  private final SupervisionTaskService supervisionTaskService;
  private final TerminationService terminationService;

  private List<Application> activeExcavationAnnouncements = null;

  @Autowired
  public ApplicationArchiverService(ApplicationServiceComposer applicationServiceComposer,
      SupervisionTaskService supervisionTaskService, TerminationService terminationService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.supervisionTaskService = supervisionTaskService;
    this.terminationService = terminationService;
  }

  @EventListener
  public void onApplicationArchiveEvent(ApplicationArchiveEvent event) {
    archiveApplicationIfNecessary(event.getApplicationId());
  }

  /**
   * Change status of finished applications (end date before current date)
   * with decision-status to finished or if application is ready for archive
   * (no open invoices, supervision tasks or deposits) directly to
   * archived-status.
   */
  public void updateStatusForFinishedApplications() {
    List<Integer> readyApplications = fetchFinishedApplications();
    readyApplications.forEach(id -> moveToFinishedOrArchived(id));
    List<Integer> finishedNotes = fetchFinishedNotes();
    finishedNotes.forEach(id -> archiveApplication(id));
    // these might have been fetched in cableReportAssociatedWithActiveExcavationAnnouncement() while checking for archivals, release them now
    activeExcavationAnnouncements = null;
  }

  public void updateStatusForTerminatedApplications() {
    terminationService.fetchTerminatedApplications().stream()
      .map(id -> applicationServiceComposer.findApplicationById(id))
      .filter(app -> readyForArchive(app))
      .forEach(app -> archiveApplication(app.getId()));
  }

  public void moveToFinishedOrArchived(Integer applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    if (readyForArchive(application)) {
      archiveApplication(applicationId);
    } else if (readyForFinished(application)){
      applicationServiceComposer.changeStatus(applicationId, StatusType.FINISHED);
    }
  }

  private List<Integer> fetchFinishedApplications() {
    return applicationServiceComposer.findFinishedApplications(Collections.singletonList(StatusType.DECISION), TYPES_MOVED_TO_FINISHED);
  }

  private List<Integer> fetchFinishedNotes() {
    return applicationServiceComposer.findFinishedNotes();
  }

  private List<Application> fetchActiveExcavationAnnouncements() {
    return applicationServiceComposer.fetchActiveExcavationAnnouncements();
  }

  private List<Application> fetchPotentiallyAnonymizableApplications() {
    return applicationServiceComposer.fetchPotentiallyAnonymizableApplications();
  }

  private void addToAnonymizableApplications(List<Integer> applicationIds) {
    applicationServiceComposer.addToAnonymizableApplications(applicationIds);
  }

  /**
   * Archives applications if necessary.
   * See {@link #archiveApplicationIfNecessary(Integer)}
   * @param applicationIds
   */
   public void archiveApplicationsIfNecessary(List<Integer> applicationIds) {
    applicationIds.forEach(id -> archiveApplicationIfNecessary(id));
  }

  /**
   * Archives application if following conditions fulfill:
   * <ul>
   *   <li>Application is finished i.e. end date before current date</li>
   *   <li>Application is in archivable status (depends on application type)</li>
   *   <li>Application is fully invoiced or not billable</li>
   *   <li>Application does not have open supervision tasks</li>
   *   <li>Application does not have open deposit</li>
   *   <li>Application does not require a survey</li>
   *   <li>If application is a cable report, it is not associated with an active excavation announcement</li>
   * </ul>
   */
  public ApplicationJson archiveApplicationIfNecessary(Integer applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    if (readyForArchive(application)) {
      return archiveApplication(applicationId);
    } else {
      return application;
    }
  }

  private ApplicationJson archiveApplication(Integer applicationId) {
    // Sets owner of application to null
    return applicationServiceComposer.changeStatus(applicationId, StatusType.ARCHIVED, new StatusChangeInfoJson());
  }

  private boolean readyForFinished(ApplicationJson application) {
    return isFinished(application)
      && !requiresSurvey(application)
      && !hasOpenSupervisionTasksBlockingFinished(application)
      && !cableReportAssociatedWithActiveExcavationAnnouncement(application);
  }

  private boolean readyForArchive(ApplicationJson application) {
    return isArchivableStatus(application)
        && isFinished(application)
        && isInvoiced(application)
        && !hasOpenSupervisionTasks(application)
        && !hasOpenDeposits(application)
        && !requiresSurvey(application)
        && !cableReportAssociatedWithActiveExcavationAnnouncement(application);
  }

  /**
   * Checks whether the application is in state that can be archived.
   */
  private boolean isArchivableStatus(ApplicationJson application) {
    return ARCHIVABLE_STATES.get(application.getType()).contains(application.getStatus());
  }

  private boolean isFinished(ApplicationJson application) {
    if (application.getStatus() == StatusType.TERMINATED) {
      return isTerminatedFinished(application);
    } else {
      boolean isFinishedStatus = isFinishedByDate(application);
      if (application.getType() == ApplicationType.CABLE_REPORT
          && application.getExtension() != null && application.getExtension() instanceof CableReportJson) {
        CableReportJson extension = (CableReportJson) application.getExtension();
        isFinishedStatus &= ZonedDateTime.now().isAfter(extension.getValidityTime());
      }
      return isFinishedStatus;
    }
  }

  private boolean isFinishedByDate(ApplicationJson application) {
    ZonedDateTime effectiveEndTime = Optional.ofNullable(application.getRecurringEndTime())
        .orElse(application.getEndTime());
    return ZonedDateTime.now().isAfter(effectiveEndTime);
  }

  private boolean isTerminatedFinished(ApplicationJson application) {
    TerminationInfo info = terminationService.getTerminationInfo(application.getId());
    ZonedDateTime startOfTheDay = TimeUtil.startOfDay(ZonedDateTime.now());
    return info.getExpirationTime().isBefore(startOfTheDay);
  }

  private boolean isInvoiced(ApplicationJson application) {
    return BooleanUtils.isTrue(application.getNotBillable()) || BooleanUtils.isTrue(application.getInvoiced());
  }

  private boolean hasOpenDeposits(ApplicationJson application) {
    return application.getApplicationTags().stream().anyMatch(t -> DEPOSIT_TAG_TYPES.contains(t.getType()));
  }

  private boolean hasOpenSupervisionTasks(ApplicationJson application) {
    List<SupervisionTaskJson> tasks = supervisionTaskService.findByApplicationId(application.getId());
    return tasks.stream().anyMatch(t -> SupervisionTaskStatusType.OPEN.equals(t.getStatus()));
  }

  private boolean hasOpenSupervisionTasksBlockingFinished(ApplicationJson application) {
    // Only Temporary traffic arrangement is currently blocked by open final supervision
    if (ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS.equals(application.getType())) {
      return hasOpenSupervisionTask(application.getId(), SupervisionTaskType.FINAL_SUPERVISION);
    }
    return false;
  }

  private boolean requiresSurvey(ApplicationJson application) {
    return application.getApplicationTags().stream().anyMatch(t -> t.getType() == ApplicationTagType.SURVEY_REQUIRED);
  }

  private boolean hasOpenSupervisionTask(int applicationId, SupervisionTaskType taskType) {
    return supervisionTaskService.findByApplicationId(applicationId).stream()
      .anyMatch(t ->
        SupervisionTaskStatusType.OPEN.equals(t.getStatus())
        && taskType.equals(t.getType()));
  }

  private boolean cableReportAssociatedWithActiveExcavationAnnouncement(Application application) {
    return cableReportAssociatedWithActiveExcavationAnnouncement(application.getType(), application.getApplicationId());
  }

  private boolean cableReportAssociatedWithActiveExcavationAnnouncement(ApplicationJson application) {
    return cableReportAssociatedWithActiveExcavationAnnouncement(application.getType(), application.getApplicationId());
  }

  private boolean cableReportAssociatedWithActiveExcavationAnnouncement(ApplicationType applicationType, String applicationId) {
    if (applicationType != ApplicationType.CABLE_REPORT) return false;
    if (activeExcavationAnnouncements == null) activeExcavationAnnouncements = fetchActiveExcavationAnnouncements();
    for (Application excavationAnnouncement : activeExcavationAnnouncements) {
      if (excavationAnnouncement.getExtension() != null && excavationAnnouncement.getExtension() instanceof ExcavationAnnouncement extension) {
          if (extension.getCableReports() != null && extension.getCableReports().contains(applicationId)) {
            return true;
          }
      }
    }
    return false;
  }
  public void checkForAnonymizableApplications() {
    addToAnonymizableApplications(
      fetchPotentiallyAnonymizableApplications().stream()
      // cable reports are all we know how to handle for now, let's be sure although DB should return only them currently
      .filter(app -> app.getType() == ApplicationType.CABLE_REPORT)
      .filter(app -> app.getExtension() != null)
      .filter(app -> app.getExtension() instanceof CableReport)
      .filter(app -> ((CableReport) app.getExtension()).getValidityTime().isBefore(ZonedDateTime.now().minusYears(2)))
      .filter(app -> !cableReportAssociatedWithActiveExcavationAnnouncement(app))
      .map(Application::getId)
      .collect(Collectors.toList())
    );

    // these might have been fetched in cableReportAssociatedWithActiveExcavationAnnouncement(), release them now
    activeExcavationAnnouncements = null;
  }
}
