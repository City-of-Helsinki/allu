package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;
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

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<ChargeBasisEntry> chargeBasisBean = bean(ChargeBasisEntry.class, chargeBasis.all());

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

  @Transactional(readOnly = true)
  public ChargeBasisModification getModifications(int applicationId, List<ChargeBasisEntry> entries, boolean manuallySet) {
    List<ChargeBasisEntry> oldEntries = getChargeBasis(applicationId).stream()
        .filter(e -> e.getManuallySet() == manuallySet).collect(Collectors.toList());
    Map<Integer, ChargeBasisEntry> entriesToUpdate = getEntriesToUpdate(entries, oldEntries);
    List<ChargeBasisEntry> entriesToAdd = entries.stream().filter(e -> !hasEntryWithKey(oldEntries, e)).collect(Collectors.toList());
    Set<Integer> entryIdsToDelete = oldEntries.stream().filter(oe -> !hasEntryWithKey(entries, oe)).map(e -> e.getId()).collect(Collectors.toSet());
    return new ChargeBasisModification(applicationId, entriesToAdd, entryIdsToDelete, entriesToUpdate, manuallySet);
  }

  /**
   * Returns map containing entries to update - existing entry ID as key and new entry as value
   */
  private Map<Integer, ChargeBasisEntry> getEntriesToUpdate(List<ChargeBasisEntry> entries,
      List<ChargeBasisEntry> oldEntries) {
    Map<Integer, ChargeBasisEntry> result = new HashMap<>();
    for (ChargeBasisEntry e : entries) {
      ChargeBasisEntry existing = getExistingEntry(e, oldEntries);
      if (existing != null && hasChanges(e, existing)) {
         result.put(existing.getId(), e);
      }
    }
    return result;
  }

  private ChargeBasisEntry getExistingEntry(ChargeBasisEntry entry, List<ChargeBasisEntry> oldEntries) {
    return oldEntries.stream()
        .filter(oe -> hasSameKey(oe, entry))
        .findFirst()
        .orElse(null);
  }

  private boolean hasEntryWithKey(List<ChargeBasisEntry> entries, ChargeBasisEntry entry) {
     return entries.stream().anyMatch(e -> hasSameKey(e, entry));
  }

  private boolean hasSameKey(ChargeBasisEntry entry1, ChargeBasisEntry entry2) {
    return entry1.getManuallySet() ? Objects.equal(entry1.getId(), entry2.getId())
        : Objects.equal(entry1.getTag(), entry2.getTag());
  }

  /**
   * Updates the charge basis entries for an application.
   *
   */
  @Transactional
  public void setChargeBasis(ChargeBasisModification modification) {
    updateEntries(modification.getEntriesToUpdate());
    deleteEntries(modification.getEntryIdsToDelete(), modification.getApplicationId());
    insertEntries(modification.getApplicationId(), modification.getEntriesToInsert(), modification.isManuallySet(), nextEntryNumber(modification.getApplicationId(), modification.isManuallySet()));
    deleteDanglingEntries(modification.getApplicationId());
  }

  private void updateEntries(Map<Integer, ChargeBasisEntry> entriesToUpdate) {
    ZonedDateTime modificationTime = ZonedDateTime.now();
    for (Entry<Integer, ChargeBasisEntry> entry : entriesToUpdate.entrySet()) {
      entry.getValue().setModificationTime(modificationTime);
      queryFactory.update(chargeBasis)
      .populate(entry.getValue(), new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
      .where(chargeBasis.id.eq(entry.getKey())).execute();
    }
  }

  private boolean hasChanges(ChargeBasisEntry entry, ChargeBasisEntry old) {
    return !entry.equals(old);
  }

  private int nextEntryNumber(int applicationId, boolean manuallySet) {
    Integer maxEntryNumber = queryFactory.select(chargeBasis.entryNumber.max()).from(chargeBasis)
        .where(chargeBasis.applicationId.eq(applicationId)).fetchFirst();
    return maxEntryNumber != null ? maxEntryNumber + 1 : 0;
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

  private void deleteEntries(Set<Integer> entryIdsToDelete, int applicationId) {
    // Delete invoice rows created from charge basis entry
    queryFactory.delete(invoiceRow).where(invoiceRow.chargeBasisId.in(entryIdsToDelete)).execute();
    queryFactory.delete(chargeBasis).where(chargeBasis.id.in(entryIdsToDelete)).execute();
  }

  protected void deleteDanglingEntries(int applicationId) {
    // Delete possible dangling referred tags left by above delete
    queryFactory.delete(chargeBasis)
    .where(chargeBasis.applicationId.eq(applicationId).and(chargeBasis.referredTag.isNotNull()).and(chargeBasis.referredTag.notIn(
        select(chargeBasis.tag).from(chargeBasis).where(chargeBasis.applicationId.eq(applicationId).and(chargeBasis.tag.isNotNull()))))).execute();
  }

  @Transactional
  public void lockEntries(Integer applicationId) {
    queryFactory.update(chargeBasis).set(chargeBasis.locked, true).where(chargeBasis.applicationId.eq(applicationId)).execute();
  }

  @Transactional(readOnly = true)
  public List<Integer> getLockedChargeBasisIds(int applicationId) {
    return queryFactory.select(chargeBasis.id).from(chargeBasis).where(chargeBasis.applicationId.eq(applicationId), chargeBasis.locked.isTrue()).fetch();
  }

  @Transactional
  public ChargeBasisEntry setInvoicable(int id, boolean invoicable) {
    queryFactory.update(chargeBasis).set(chargeBasis.invoicable, invoicable).where(chargeBasis.id.eq(id)).execute();
    return findChargeBasisEntry(id);
  }

  private ChargeBasisEntry findChargeBasisEntry(int id) {
    return queryFactory.select(chargeBasisBean)
        .from(chargeBasis)
        .where(chargeBasis.id.eq(id))
        .fetchOne();
  }
}
