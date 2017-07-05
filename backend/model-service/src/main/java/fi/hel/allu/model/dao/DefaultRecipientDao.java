package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.DefaultRecipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QDefaultRecipient.defaultRecipient;

/**
 * DAO class for accessing and modifying default recipients in database
 */
@Repository
public class DefaultRecipientDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<DefaultRecipient> recipientBean = bean(DefaultRecipient.class, defaultRecipient.all());

  @Transactional(readOnly = true)
  public List<DefaultRecipient> findAll() {
    return queryFactory.select(recipientBean)
            .from(defaultRecipient)
            .fetch();
  }

  @Transactional(readOnly = true)
  public DefaultRecipient findById(int id) {
    return Optional.ofNullable(queryFactory
            .select(recipientBean)
            .from(defaultRecipient)
            .where(defaultRecipient.id.eq(id))
            .fetchOne())
      .orElseThrow(() -> new NoSuchEntityException("Default recipient not found", Integer.toString(id)));
  }

  @Transactional
  public DefaultRecipient create(DefaultRecipient recipientData) {
    int id = queryFactory.insert(defaultRecipient)
            .populate(recipientData)
            .executeWithKey(defaultRecipient.id);
    return findById(id);
  }

  @Transactional
  public DefaultRecipient update(int id, DefaultRecipient recipientData) {
    recipientData.setId(id);
    return Optional.ofNullable(queryFactory
            .update(defaultRecipient)
            .populate(recipientData)
            .where(defaultRecipient.id.eq(id))
            .execute())
      .filter(count -> count > 0)
      .map(count -> findById(id))
      .orElseThrow(() -> new NoSuchEntityException("Failed to update the record", Integer.toString(id)));
  }

  @Transactional
  public void delete(int id) {
    Optional.ofNullable(queryFactory
            .delete(defaultRecipient)
            .where(defaultRecipient.id.eq(id))
            .execute())
      .filter(count -> count > 0)
      .orElseThrow(() -> new NoSuchEntityException("Failed to update the record", Integer.toString(id)));
  }
}
