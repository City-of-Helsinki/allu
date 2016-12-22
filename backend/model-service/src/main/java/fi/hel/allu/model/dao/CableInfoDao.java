package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.CableInfoType;
import fi.hel.allu.model.domain.CableInfoText;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCableInfoText.cableInfoText;

/**
 * DAO class for Cable info texts
 */
@Repository
public class CableInfoDao {

  private SQLQueryFactory queryFactory;

  @Autowired
  public CableInfoDao(SQLQueryFactory queryFactory, ApplicationSequenceDao applicationSequenceDao) {
    this.queryFactory = queryFactory;
  }

  final QBean<CableInfoText> cableInfoTextBean = bean(CableInfoText.class, cableInfoText.all());

  /**
   * Get the standard texts for cable infos
   * @return List of texts and their cable info types
   */
  @Transactional
  public List<CableInfoText> getCableInfoTexts() {
    return queryFactory.select(cableInfoTextBean).from(cableInfoText).fetch();
  }

  /**
   * Delete a standard cable info text
   * @param id the ID of the key to delete
   */
  @Transactional
  public void deleteCableInfoText(int id) {
    long deleteCount = queryFactory.delete(cableInfoText).where(cableInfoText.id.eq(id))
        .execute();
    if (deleteCount == 0) {
      throw new NoSuchEntityException("No cable info text with id " + id);
    }
  }

  /**
   * Create a new cable info text
   * @param type the cable info type for the text
   * @param text the text
   * @return the resulting CableInfoText entry
   */
  @Transactional
  public CableInfoText createCableInfoText(CableInfoType type, String text) {
    int id = queryFactory.insert(cableInfoText).set(cableInfoText.cableInfoType, type)
        .set(cableInfoText.textValue, text)
        .executeWithKey(cableInfoText.id);
    return getCableInfoText(id);
  }

  /**
   * Update a cable info text
   * @param id id of the text entry to update
   * @param text new text for the entry
   * @return the resulting CableInfoText entry after update
   */
  @Transactional
  public CableInfoText updateCableInfoText(int id, String text) {
    long updateCount = queryFactory.update(cableInfoText).set(cableInfoText.textValue, text)
        .where(cableInfoText.id.eq(id)).execute();
    if (updateCount == 0) {
      throw new NoSuchEntityException("No cable info text with id " + id);
    }
    return getCableInfoText(id);
  }

  private CableInfoText getCableInfoText(int id) {
    Optional<CableInfoText> fromDatabase = Optional.ofNullable(
        queryFactory.select(cableInfoTextBean).from(cableInfoText).where(cableInfoText.id.eq(id)).fetchFirst());
    return fromDatabase.orElseThrow(() -> new NoSuchEntityException("CableInfoText not found"));
  }
}
