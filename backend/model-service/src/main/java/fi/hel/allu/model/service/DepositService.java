package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.DepositDao;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.Deposit;

@Service
public class DepositService {

  private DepositDao depositDao;
  private ApplicationDao applicationDao;

  public static final List<ApplicationTagType> depositTags = Arrays.asList(
      ApplicationTagType.DEPOSIT_REQUESTED, ApplicationTagType.DEPOSIT_PAID);

  @Autowired
  public DepositService(DepositDao depositDao, ApplicationDao applicationDao) {
    this.depositDao = depositDao;
    this.applicationDao = applicationDao;
  }

  public Deposit findById(int id) {
    return depositDao.findById(id);
  }

  public Deposit findByApplicationId(int applicationId) {
    return depositDao.findByApplicationId(applicationId);
  }

  /**
   * Creates new deposit and adds corresponding tag for application of the deposit.
   */
  public Deposit create(Deposit deposit) {
    Deposit created = depositDao.insert(deposit);
    addDepositTagForApplication(deposit);
    return created;
  }

  private void addDepositTagForApplication(Deposit deposit) {
    deposit.getStatus().tag
      .map(tagType -> new ApplicationTag(deposit.getCreatorId(), tagType, ZonedDateTime.now()))
      .ifPresent(tag -> applicationDao.addTag(deposit.getApplicationId(), tag));
  }

  /**
   * Updates deposit and updates application tag according to state of deposit (is deposit paid or not)
   */
  public Deposit update(int id, Deposit deposit) {
    deposit.setId(id);
    Deposit updated = depositDao.update(deposit);
    applicationDao.removeTagByTypes(deposit.getApplicationId(), depositTags);
    addDepositTagForApplication(deposit);
    return updated;
  }

  /**
   * Deletes deposit and related tags from application
   */
  public void delete(int depositId) {
    Deposit deposit = depositDao.findById(depositId);
    depositDao.delete(depositId);
    applicationDao.removeTagByTypes(deposit.getApplicationId(), depositTags);
  }
}