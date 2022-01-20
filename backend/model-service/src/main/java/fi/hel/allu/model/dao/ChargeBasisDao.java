package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling;

import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static fi.hel.allu.QChargeBasis.chargeBasis;
import static fi.hel.allu.QInvoiceRow.invoiceRow;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class ChargeBasisDao {
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(
      chargeBasis.applicationId, chargeBasis.id, chargeBasis.entryNumber, chargeBasis.referrable,
      chargeBasis.locked, chargeBasis.invoicable);

  private final SQLQueryFactory queryFactory;



  final QBean<ChargeBasisEntry> chargeBasisBean = bean(ChargeBasisEntry.class, chargeBasis.all());


  public ChargeBasisDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }


  /**
   * Get the charge basis entries for an application
   *
   * @param applicationId application ID
   * @return list of charge basis entries (empty if no items are stored)
   */
  @Transactional(readOnly = true)
  public List<ChargeBasisEntry> getChargeBasis(int applicationId) {
    return queryFactory.select(chargeBasisBean).from(chargeBasis).where(chargeBasis.applicationId.eq(applicationId))
        .orderBy(chargeBasis.manuallySet.asc(), chargeBasis.entryNumber.asc()).fetch();
  }



  @Transactional
  public List<ChargeBasisEntry> getReferencingTagEntries(int applicationId){
    return  queryFactory.select(chargeBasisBean).from(chargeBasis)
      .where(chargeBasis.referredTag.isNotNull().and(chargeBasis.applicationId.eq(applicationId))).fetch();
  }

  /**
   * Updates the charge basis entries for an application.
   *
   */
  @Transactional
  public void setChargeBasis(ChargeBasisModification modification) {
    updateEntries(modification.getEntriesToUpdate());
    deleteEntries(modification.getEntryIdsToDelete());
    insertEntries(modification.getApplicationId(), modification.getEntriesToInsert(), modification.isManuallySet(),
      nextEntryNumber(modification.getApplicationId()));
    deleteDanglingEntries(modification.getApplicationId());
  }

  private void updateEntries(Map<Integer, ChargeBasisEntry> entriesToUpdate) {
    for (Entry<Integer, ChargeBasisEntry> entry : entriesToUpdate.entrySet()) {
      updateEntry(entry.getKey(), entry.getValue());
    }
  }

  @Transactional
  public ChargeBasisEntry updateEntry(Integer id, ChargeBasisEntry entry) {
    entry.setModificationTime(ZonedDateTime.now());
    queryFactory.update(chargeBasis)
    .populate(entry, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
    .where(chargeBasis.id.eq(id)).execute();
    return findChargeBasisEntry(id);
  }

  @Transactional
  public ChargeBasisEntry insertManualEntry(int applicationId, ChargeBasisEntry entry) {
    int entryNumber = nextEntryNumber(applicationId);
    entry.setModificationTime(ZonedDateTime.now());
    Integer id = queryFactory.insert(chargeBasis)
        .populate(entry, new ExcludingMapper(NullHandling.WITH_NULL_BINDINGS, Arrays.asList(chargeBasis.manuallySet)))
        .set(chargeBasis.applicationId, applicationId)
        .set(chargeBasis.entryNumber, entryNumber)
        .set(chargeBasis.manuallySet, true)
        .executeWithKey(chargeBasis.id);
    return findChargeBasisEntry(id);
  }

  private void insertEntries(int applicationId, Collection<ChargeBasisEntry> entries, boolean manuallySet, int nextEntryNumber) {
    if (!entries.isEmpty()) {
      ZonedDateTime modificationTime = ZonedDateTime.now();
      SQLInsertClause insert = queryFactory.insert(chargeBasis);
      for (ChargeBasisEntry entry : entries) {
        entry.setModificationTime(modificationTime);
        insert
            .populate(entry,
                new ExcludingMapper(NullHandling.WITH_NULL_BINDINGS, Arrays.asList(chargeBasis.manuallySet)))
            .set(chargeBasis.applicationId, applicationId).set(chargeBasis.entryNumber, nextEntryNumber++)
            .set(chargeBasis.manuallySet, manuallySet)
            .addBatch();
      }
      long numInserts = insert.execute();
      if (numInserts != entries.size()) {
        throw new QueryException("Failed to insert the entries, numInserts=" + numInserts);
      }
    }
  }

  @Transactional
  public void deleteEntries(Collection<Integer> entryIdsToDelete) {
    // Delete invoice rows created from charge basis entry
    queryFactory.delete(invoiceRow).where(invoiceRow.chargeBasisId.in(entryIdsToDelete)).execute();
    queryFactory.delete(chargeBasis).where(chargeBasis.id.in(entryIdsToDelete)).execute();
  }

  protected void deleteDanglingEntries(int applicationId) {
    // Delete possible dangling referred tags left by above delete
    queryFactory.delete(chargeBasis)
    .where(chargeBasis.applicationId.eq(applicationId).and(chargeBasis.referredTag.isNotNull()).and(chargeBasis.referredTag.notIn(
        select(chargeBasis.tag).from(chargeBasis).where(chargeBasis.applicationId.eq(applicationId)
          .and(chargeBasis.tag.isNotNull()))))).execute();
  }

  @Transactional
  public void lockEntries(Integer applicationId) {
    setEntriesLocked(applicationId, true);
  }

  @Transactional
  public void unlockEntries(Integer applicationId) {
    setEntriesLocked(applicationId, false);
  }

  private void setEntriesLocked(Integer applicationId, boolean isLocked) {
    queryFactory.update(chargeBasis).set(chargeBasis.locked, isLocked).where(chargeBasis.applicationId.eq(applicationId)).execute();
  }

  @Transactional
  public void setEntriesLocked(List<Integer> entryIds, boolean isLocked) {
    queryFactory.update(chargeBasis).set(chargeBasis.locked, isLocked).where(chargeBasis.id.in(entryIds)).execute();
  }

  @Transactional
  public void lockEntriesOfPeriod(Integer periodId) {
    queryFactory.update(chargeBasis).set(chargeBasis.locked, true).where(chargeBasis.invoicingPeriodId.eq(periodId)).execute();
  }


  @Transactional(readOnly = true)
  public List<Integer> getLockedChargeBasisIds(int applicationId) {
    return queryFactory.select(chargeBasis.id).from(chargeBasis).where(chargeBasis.applicationId.eq(applicationId),
      chargeBasis.locked.isTrue()).fetch();
  }

  @Transactional
  public ChargeBasisEntry setInvoicable(int id, boolean invoicable) {
    queryFactory.update(chargeBasis).set(chargeBasis.invoicable, invoicable).where(chargeBasis.id.eq(id)).execute();
    return findChargeBasisEntry(id);
  }

  @Transactional
  public void setSubChargesInvoicable(boolean invoicable, String parentTag) {
    queryFactory.update(chargeBasis).set(chargeBasis.invoicable, invoicable).where(chargeBasis.referredTag.eq(parentTag)).execute();
  }

  @Transactional
  public void setEntriesInvoicable(List<Integer> ids, boolean invoicable) {
    queryFactory.update(chargeBasis).set(chargeBasis.invoicable, invoicable).where(chargeBasis.id.in(ids)).execute();
  }

  @Transactional(readOnly = true)
  public ChargeBasisEntry findChargeBasisEntry(int applicationId, int entryId) {
    return queryFactory.select(chargeBasisBean)
        .from(chargeBasis)
        .where(chargeBasis.id.eq(entryId), chargeBasis.applicationId.eq(applicationId))
        .fetchOne();

  }

  private ChargeBasisEntry findChargeBasisEntry(int id) {
    return queryFactory.select(chargeBasisBean)
        .from(chargeBasis)
        .where(chargeBasis.id.eq(id))
        .fetchOne();
  }

  @Transactional
  public void setInvoicingPeriodForManualEntries(Integer periodId, Integer applicationId) {
    queryFactory.update(chargeBasis).set(chargeBasis.invoicingPeriodId, periodId)
        .where(chargeBasis.applicationId.eq(applicationId), chargeBasis.manuallySet.isTrue(),
            chargeBasis.locked.isNull().or(chargeBasis.locked.isFalse()))
        .execute();
  }

  @Transactional
  public void copyManualChargeBasisEntries(int fromApplicationId, Integer toApplicationId, List<Integer> filteredIds) {
    List<ChargeBasisEntry> entries =
    queryFactory.select(chargeBasisBean).from(chargeBasis)
        .where(chargeBasis.applicationId.eq(fromApplicationId),
               chargeBasis.manuallySet.isTrue(),
               chargeBasis.id.notIn(filteredIds))
        .fetch();
    entries.forEach(e -> {
      e.setId(null);
      e.setLocked(false);
    });
    insertEntries(toApplicationId, entries, true, nextEntryNumber(toApplicationId));
  }

  private int nextEntryNumber(int applicationId) {
    Integer maxEntryNumber = queryFactory.select(chargeBasis.entryNumber.max()).from(chargeBasis)
      .where(chargeBasis.applicationId.eq(applicationId)).fetchFirst();
    return maxEntryNumber != null ? maxEntryNumber + 1 : 0;
  }

  @Transactional(readOnly = true)
  public Boolean isInvoicable(int applicationId, String tag, boolean manuallySet) {
    return queryFactory.select(chargeBasis.invoicable)
        .from(chargeBasis)
        .where(chargeBasis.applicationId.eq(applicationId), chargeBasis.tag.eq(tag), chargeBasis.manuallySet.eq(manuallySet))
        .fetchFirst();
  }

  public Optional<ChargeBasisEntry> findByTag(int applicationId, String tag) {
    return Optional.ofNullable(queryFactory.select(chargeBasisBean)
        .from(chargeBasis)
        .where(chargeBasis.applicationId.eq(applicationId), chargeBasis.tag.eq(tag))
        .fetchFirst());
  }
}
