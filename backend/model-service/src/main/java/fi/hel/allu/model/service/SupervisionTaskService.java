package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.SupervisionTaskToTag;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.SupervisionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.ZonedDateTime;
import java.util.List;

import static fi.hel.allu.common.domain.types.SupervisionTaskStatusType.*;

/**
 * The service class for supervision task operations
 */
@Service
public class SupervisionTaskService {
  private SupervisionTaskDao supervisionTaskDao;
  private ApplicationDao applicationDao;

  @Autowired
  public SupervisionTaskService(SupervisionTaskDao supervisionTaskDao, ApplicationDao applicationDao) {
    this.supervisionTaskDao = supervisionTaskDao;
    this.applicationDao = applicationDao;
  }

  @Transactional(readOnly = true)
  public SupervisionTask findById(int id) {
    return supervisionTaskDao.findById(id)
        .orElseThrow(() -> new NoSuchEntityException("Supervision task not found", Integer.toString(id)));
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationId(int applicationId) {
    return supervisionTaskDao.findByApplicationId(applicationId);
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
      SupervisionTaskToTag.onTaskDeleteRemoveTags(task.getType())
          .forEach(type -> applicationDao.removeTagByType(task.getApplicationId(), type));
      supervisionTaskDao.delete(id);
    });
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> search(SupervisionTaskSearchCriteria searchCriteria, Pageable pageRequest) {
    return supervisionTaskDao.search(searchCriteria, pageRequest);
  }

  @Transactional
  public SupervisionTask approve(SupervisionTask supervisionTask) {
    supervisionTask.setStatus(APPROVED);
    supervisionTask.setActualFinishingTime(ZonedDateTime.now());
    SupervisionTask updated = supervisionTaskDao.update(supervisionTask);
    updateTags(updated);
    return updated;
  }

  @Transactional
  public SupervisionTask reject(SupervisionTask task, ZonedDateTime newSupervisionDate) {
    task.setStatus(REJECTED);
    task.setActualFinishingTime(ZonedDateTime.now());
    SupervisionTask updated = supervisionTaskDao.update(task);
    updateTags(updated);
    supervisionTaskDao.insert(rejectedToNewTask(task, newSupervisionDate));
    return updated;
  }

  @Transactional
  public int updateHandler(int handlerId, List<Integer> tasks) {
    return supervisionTaskDao.updateHandler(handlerId, tasks);
  }

  @Transactional
  public int removeHandler(List<Integer> tasks) {
    return supervisionTaskDao.removeHandler(tasks);
  }

  private SupervisionTask rejectedToNewTask(SupervisionTask rejected, ZonedDateTime newDate) {
    return new SupervisionTask(
        null,
        rejected.getApplicationId(),
        rejected.getType(),
        rejected.getHandlerId(),
        rejected.getHandlerId(),
        ZonedDateTime.now(),
        newDate,
        null,
        OPEN,
        rejected.getResult(),
        null
    );
  }

  private void updateTags(SupervisionTask task) {
    ApplicationTagType tagType = SupervisionTaskToTag.getBy(task.getType(), task.getStatus());
    ApplicationTag tag = new ApplicationTag(task.getCreatorId(), tagType, ZonedDateTime.now());
    applicationDao.addTag(task.getApplicationId(), tag);
    tag.getType().getReplaces().forEach(type -> applicationDao.removeTagByType(task.getApplicationId(), type));
  }
}
