package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Deposit;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QDeposit.deposit;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class DepositDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(deposit.id, deposit.applicationId,
      deposit.creatorId, deposit.creationTime);

  private final QBean<Deposit> depositBean = bean(Deposit.class, deposit.all());

  @Transactional
  public Deposit insert(Deposit newDeposit) {
    newDeposit.setId(null);
    newDeposit.setCreationTime(ZonedDateTime.now());
    Integer id = queryFactory.insert(deposit).populate(newDeposit).executeWithKey(deposit.id);
    return findById(id);
  }

  @Transactional(readOnly = true)
  public Deposit findById(int id) {
    Deposit d = queryFactory.select(depositBean).from(deposit).where(deposit.id.eq(id)).fetchOne();
    if (d == null) {
      throw new NoSuchEntityException("No deposit found with ID {}", id);
    }
    return d;
  }

  @Transactional(readOnly = true)
  public Deposit findByApplicationId(int applicationId) {
    return queryFactory.select(depositBean).from(deposit).where(deposit.applicationId.eq(applicationId)).fetchOne();
  }

  @Transactional
  public Deposit update(Deposit d) {
    long changed = queryFactory.update(deposit).populate(d, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(deposit.id.eq(d.getId())).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update deposit with ID {}.", d.getId());
    }
    return findById(d.getId());
  }

  @Transactional
  public void delete(int id) {
    long deleted = queryFactory.delete(deposit).where(deposit.id.eq(id)).execute();
    if (deleted == 0) {
      throw new NoSuchEntityException("Attempted to delete non-existent deposit", id);
    }
  }

  /**
   * Copy application deposit from application to another application
   */
  @Transactional
  public void copyApplicationDeposit(Integer copyFromApplicationId, Integer copyToApplicationId) {
    Optional.ofNullable(findByApplicationId(copyFromApplicationId)).ifPresent(d -> copyDepositForApplication(d, copyToApplicationId));
  }

  private void copyDepositForApplication(Deposit deposit, Integer copyToApplicationId) {
    deposit.setApplicationId(copyToApplicationId);
    insert(deposit);
  }
}
