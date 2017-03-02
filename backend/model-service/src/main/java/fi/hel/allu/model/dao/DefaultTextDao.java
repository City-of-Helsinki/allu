package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.domain.DefaultText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QDefaultText.defaultText;

/**
 * Default text database access.
 */
@Repository
public class DefaultTextDao {
  private SQLQueryFactory queryFactory;

  @Autowired
  public DefaultTextDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  final QBean<DefaultText> defaultTextBean = bean(DefaultText.class, defaultText.all());

  /**
   * Get the default texts for given application type.
   *
   * @param applicationType   Application type whose default texts are fetched.
   * @return The default texts for given application type. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public List<DefaultText> getDefaultTexts(ApplicationType applicationType) {
    return queryFactory.select(defaultTextBean).from(defaultText).where(defaultText.applicationType.eq(applicationType)).fetch();
  }

  /**
   * Get the default text by id.
   *
   * @param id      Id of the default text to be fetched.
   * @return The default texts for given id. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public DefaultText getDefaultText(int id) {
    Optional<DefaultText> fromDatabase = Optional.ofNullable(
        queryFactory.select(defaultTextBean).from(defaultText).where(defaultText.id.eq(id)).fetchOne());
    return fromDatabase.orElseThrow(() -> new NoSuchEntityException("Default text not found", Integer.toString(id)));
  }

  /**
   * Delete a default text.
   *
   * @param id the ID of the default text to delete.
   */
  @Transactional
  public void delete(int id) {
    long deleteCount = queryFactory.delete(defaultText).where(defaultText.id.eq(id)).execute();
    if (deleteCount == 0) {
      throw new NoSuchEntityException("Default text not found", Integer.toString(id));
    }
  }

  /**
   * Create a default text for application type.
   *
   * @param defaultTextData     Data to be added.
   *
   * @return the resulting default text entry
   */
  @Transactional
  public DefaultText create(DefaultText defaultTextData) {
    int id = queryFactory
        .insert(defaultText)
        .set(defaultText.applicationType, defaultTextData.getApplicationType())
        .set(defaultText.textType, defaultTextData.getTextType())
        .set(defaultText.textValue, defaultTextData.getTextValue())
        .executeWithKey(defaultText.id);
    return getDefaultText(id);
  }

  /**
   * Update a default text.
   *
   * @param id                id of the default text entry to update.
   * @param defaultTextData   new text for the entry.
   *
   * @return the resulting default text after update
   */
  @Transactional
  public DefaultText update(int id, DefaultText defaultTextData) {
    long updateCount = queryFactory
        .update(defaultText)
        .set(defaultText.applicationType, defaultTextData.getApplicationType())
        .set(defaultText.textType, defaultTextData.getTextType())
        .set(defaultText.textValue, defaultTextData.getTextValue())
        .where(defaultText.id.eq(id))
        .execute();
    if (updateCount == 0) {
      throw new NoSuchEntityException("Default text not found", Integer.toString(id));
    }
    return getDefaultText(id);
  }
}
