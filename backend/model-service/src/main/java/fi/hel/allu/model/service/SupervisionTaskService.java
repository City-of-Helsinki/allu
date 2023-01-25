package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.SupervisionTaskToTag;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.hel.allu.common.domain.types.SupervisionTaskStatusType.*;

/**
 * The service class for supervision task operations
 */
@Service
public class SupervisionTaskService {
  private final SupervisionTaskDao supervisionTaskDao;
  private final LocationDao locationDao;
  private final ApplicationService applicationService;

  @Autowired
  public SupervisionTaskService(SupervisionTaskDao supervisionTaskDao, LocationDao locationDao,
      ApplicationService applicationService) {
    this.supervisionTaskDao = supervisionTaskDao;
    this.locationDao = locationDao;
    this.applicationService = applicationService;
  }

  @Transactional(readOnly = true)
  public SupervisionTask findById(int id) {
    return supervisionTaskDao.findById(id)
        .orElseThrow(() -> new NoSuchEntityException("Supervision task not found", Integer.toString(id)));
  }

  public List<SupervisionTask> findByApplicationId(int applicationId) {
    return supervisionTaskDao.findByApplicationId(applicationId);
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByLocationId(int locationId) {
    return supervisionTaskDao.findByLocationId(locationId);
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationIdAndType(int applicationId, SupervisionTaskType type) {
    return supervisionTaskDao.findByApplicationIdAndType(applicationId, type);
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationIdAndTypeAndLocation(int applicationId, SupervisionTaskType type, int location) {
    return supervisionTaskDao.findByApplicationIdAndTypeAndLocation(applicationId, type, location);
  }

  @Transactional
  public SupervisionTask insert(SupervisionTask supervisionTask) {
    SupervisionTask inserted = supervisionTaskDao.insert(supervisionTask);
    updateTags(inserted);
    return inserted;
  }

  @Transactional
  public SupervisionTask update(int id, SupervisionTask supervisionTask) {
    supervisionTask.setId(id);
    return supervisionTaskDao.update(supervisionTask);
  }

  @Transactional
  public void delete(int id) {
    supervisionTaskDao.findById(id).ifPresent(task -> {
      List<ApplicationTagType> types = SupervisionTaskToTag.onTaskDeleteRemoveTags(task.getType());
      applicationService.removeTagByTypes(task.getApplicationId(), types);
      supervisionTaskDao.delete(id);
    });
  }

  public SupervisionWorkItem getWorkItem(Integer id) {
    return supervisionTaskDao.findSupervisionWorkItem(id);
  }

  @Transactional
  public SupervisionTask approve(SupervisionTask supervisionTask) {
    if (isExcavationAnnouncement(supervisionTask.getApplicationId())
        && supervisionTask.getType() == SupervisionTaskType.FINAL_SUPERVISION) {
      handleExcavationAnnouncementFinalSupervisionApproval(supervisionTask.getApplicationId());
    }

    supervisionTask.setStatus(APPROVED);
    supervisionTask.setActualFinishingTime(ZonedDateTime.now());
    SupervisionTask updated = supervisionTaskDao.update(supervisionTask);
    saveSupervisedLocations(updated);
    updateTags(updated);
    return updated;
  }

  /**
   * If excavation announcement final supervision is approved before operational condition task
   * -> remove operational condition tasks and remove operational condition date from application
   */
  private void handleExcavationAnnouncementFinalSupervisionApproval(Integer applicationId) {
    List<SupervisionTask> openOperationalConditionTasks = supervisionTaskDao
        .findByApplicationIdAndType(applicationId, SupervisionTaskType.OPERATIONAL_CONDITION).stream()
        .filter(t -> t.getStatus() == OPEN).collect(Collectors.toList());
    if (!openOperationalConditionTasks.isEmpty()) {
      // Clear operational condition date from application
      applicationService.clearExcavationAnnouncementOperationalConditionDate(applicationId);
      // Remove open operation condition supervision tasks
      openOperationalConditionTasks.forEach(o -> supervisionTaskDao.delete(o.getId()));
    }
  }

  private boolean isExcavationAnnouncement(Integer applicationId) {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT == applicationService.getApplicationType(applicationId);
  }

  @Transactional
  public SupervisionTask reject(SupervisionTask task, ZonedDateTime newSupervisionDate) {
    task.setStatus(REJECTED);
    task.setActualFinishingTime(ZonedDateTime.now());
    SupervisionTask updated = supervisionTaskDao.update(task);
    updateTags(updated);
    saveSupervisedLocations(updated);
    supervisionTaskDao.insert(rejectedToNewTask(task, newSupervisionDate));
    return updated;
  }

  @Transactional
  public int updateOwner(int ownerId, List<Integer> tasks) {
    return supervisionTaskDao.updateOwner(ownerId, tasks);
  }

  @Transactional
  public int removeOwner(List<Integer> tasks) {
    return supervisionTaskDao.removeOwner(tasks);
  }

  public List<Integer> getSupervisionTaskCount(Integer applicationId){
    return supervisionTaskDao.getCountOfSupervisionTask(applicationId);
  }

  public Page<SupervisionWorkItem> findAll(Pageable pageRequest) {
    return supervisionTaskDao.findAll(pageRequest);
  }

  private SupervisionTask rejectedToNewTask(SupervisionTask rejected, ZonedDateTime newDate) {
    return new SupervisionTask(
        null,
        rejected.getApplicationId(),
        rejected.getType(),
        rejected.getOwnerId(),
        rejected.getOwnerId(),
        ZonedDateTime.now(),
        newDate,
        null,
        OPEN,
        rejected.getResult(),
        null,
        rejected.getLocationId()
    );
  }

  private void updateTags(SupervisionTask task) {
    Optional<ApplicationTagType> tagType = SupervisionTaskToTag.getBy(task.getType(), task.getStatus());
    tagType.ifPresent(t -> createTag(t, task.getApplicationId(), task.getCreatorId()));
  }

  private void createTag(ApplicationTagType tagType, Integer applicationId, Integer creatorId) {
    ApplicationTag tag = new ApplicationTag(creatorId, tagType, ZonedDateTime.now());
    applicationService.addTag(applicationId, tag);
    applicationService.removeTagByTypes(applicationId, tag.getType().getReplaces());
  }

  @Transactional
  public void cancelOpenTasksOfApplication(Integer applicationId) {
    supervisionTaskDao.cancelOpenTasksOfApplication(applicationId);
  }

  @Transactional(readOnly = true)
  public Map<Integer, List<SupervisionTask>> getSupervisionTaskHistoryForExternalOwner(Integer externalOwnerId,
      ZonedDateTime eventsAfter, List<Integer> includedExternalApplicationIds) {
    return supervisionTaskDao.getSupervisionTaskHistoryForExternalOwner(externalOwnerId, eventsAfter, includedExternalApplicationIds);
  }

  public String[] findAddressById(int id) {
    return supervisionTaskDao.findAddressById(id);
  }

  /**
   * Saves current state of application locations for task
   */
  private void saveSupervisedLocations(SupervisionTask task) {
    supervisionTaskDao.deleteSupervisedLocations(task.getId());
    List<Location> locations = locationDao.findByApplicationId(task.getApplicationId());
      locations.forEach(l -> saveSupervisedLocation(task.getId(), l));
    task.setSupervisedLocations(supervisionTaskDao.getSupervisedLocations(task.getId()));
  }

  private void saveSupervisedLocation(Integer supervisionTaskId, Location location) {
    supervisionTaskDao.saveSupervisedLocation(supervisionTaskId, SupervisionTaskLocation.fromApplicationLocation(location));
  }

  public List<Location> getLocationsOfSupervisionTasks(List<SupervisionTask> tasks) {
    return locationDao.findByIds(tasks.stream().map(SupervisionTask::getLocationId).collect(Collectors.toList()));
  }
}