package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.*;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
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

  private static final Set<StatusType> ARCHIVED_STATUSES = new HashSet<>(Arrays.asList(StatusType.FINISHED, StatusType.DECISION));
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
   * with decision-status to finished.
   */
  public void updateStatusForFinishedApplications() {
    List<Integer> readyApplications = fetchFinishedApplications();
    readyApplications.forEach(id -> applicationServiceComposer.changeStatus(id, StatusType.FINISHED));
  }

  private List<Integer> fetchFinishedApplications() {
    return applicationServiceComposer.findFinishedApplications(Collections.singletonList(StatusType.DECISION));
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
    return isFinished(application) && isInvoiced(application) && !hasOpenSupervisionTasks(application) && !hasOpenDeposits(application);
  }

  private boolean isFinished(ApplicationJson application) {
    return ARCHIVED_STATUSES.contains(application.getStatus()) && ZonedDateTime.now().isAfter(application.getEndTime());
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
