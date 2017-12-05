package fi.hel.allu.model.service;

import java.time.ZonedDateTime;

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

  /**
   * Deposit related application tag types
   */
  private enum DepositApplicationTagType {
    UNPAID_DEPOSIT(ApplicationTagType.DEPOSIT_REQUESTED, ApplicationTagType.DEPOSIT_PAID),
    PAID_DEPOSIT(ApplicationTagType.DEPOSIT_PAID, ApplicationTagType.DEPOSIT_REQUESTED);
    private ApplicationTagType tag;
    private ApplicationTagType replacedTag;

    private DepositApplicationTagType(ApplicationTagType tag, ApplicationTagType replacedTag) {
      this.tag = tag;
      this.replacedTag = replacedTag;
    }

    private static DepositApplicationTagType forDeposit(Deposit deposit) {
      return deposit.isPaid() ? DepositApplicationTagType.PAID_DEPOSIT : DepositApplicationTagType.UNPAID_DEPOSIT;
    }
  }

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
    ApplicationTagType tagType = DepositApplicationTagType.forDeposit(deposit).tag;
    ApplicationTag tag = new ApplicationTag(deposit.getCreatorId(), tagType, ZonedDateTime.now());
    applicationDao.addTag(deposit.getApplicationId(), tag);
  }

  /**
   * Updates deposit and updates application tag according to state of deposit (is deposit paid or not)
   */
  public Deposit update(int id, Deposit deposit) {
    deposit.setId(id);
    Deposit updated = depositDao.update(deposit);
    addDepositTagForApplication(deposit);
    applicationDao.removeTagByType(deposit.getApplicationId(), DepositApplicationTagType.forDeposit(deposit).replacedTag);
    return updated;
  }

  /**
   * Deletes deposit and related tags from application
   */
  public void delete(int depositId) {
    Deposit deposit = depositDao.findById(depositId);
    depositDao.delete(depositId);
    applicationDao.removeTagByType(deposit.getApplicationId(), DepositApplicationTagType.forDeposit(deposit).tag);
  }
}
