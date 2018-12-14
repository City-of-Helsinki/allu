package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.*;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
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

  private static final List<ApplicationType> TYPES_MOVED_TO_FINISHED = Arrays.asList(ApplicationType.CABLE_REPORT, ApplicationType.EVENT, ApplicationType.PLACEMENT_CONTRACT,
          ApplicationType.SHORT_TERM_RENTAL, ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS);
  private static final Set<ApplicationTagType> DEPOSIT_TAG_TYPES = new HashSet<>(
      Arrays.asList(ApplicationTagType.DEPOSIT_PAID, ApplicationTagType.DEPOSIT_REQUESTED));
  private final ApplicationServiceComposer applicationServiceComposer;
  private final SupervisionTaskService supervisionTaskService;

  @Autowired
  public ApplicationArchiverService(ApplicationServiceComposer applicationServiceComposer, SupervisionTaskService supervisionTaskService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.supervisionTaskService = supervisionTaskService;
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
  }

  private void moveToFinishedOrArchived(Integer applicationId) {
    if (readyForArchive(applicationServiceComposer.findApplicationById(applicationId))) {
      archiveApplication(applicationId);
    } else {
      applicationServiceComposer.changeStatus(applicationId, StatusType.FINISHED);
    }
  }

  private List<Integer> fetchFinishedApplications() {
    return applicationServiceComposer.findFinishedApplications(Collections.singletonList(StatusType.DECISION), TYPES_MOVED_TO_FINISHED);
  }

  private List<Integer> fetchFinishedNotes() {
    return applicationServiceComposer.findFinishedNotes();
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
   * <li>Application is finished i.e. end date before current date and status is finished or decision</li>
   * <li>Application is invoiced or not billable</li>
   * <li>Application does not have open supervision tasks</li>
   * <li>Application does not have open deposit</li>
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

  private boolean readyForArchive(ApplicationJson application) {
    return isArchivedStatus(application)
        && isFinished(application)
        && isInvoiced(application)
        && !hasOpenSupervisionTasks(application)
        && !hasOpenDeposits(application);
  }

  private boolean isArchivedStatus(ApplicationJson application) {
    boolean isArchivedStatus = application.getStatus() == StatusType.FINISHED;
    // Area rentals and excavation announcements never archived from decision status
    if (application.getType() != ApplicationType.EXCAVATION_ANNOUNCEMENT && application.getType() != ApplicationType.AREA_RENTAL) {
      isArchivedStatus |= application.getStatus() == StatusType.DECISION;
    }
    return isArchivedStatus;
  }

  private boolean isFinished(ApplicationJson application) {
    return ZonedDateTime.now().isAfter(application.getEndTime());
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
}
