package fi.hel.allu.model.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
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
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QChargeBasis.chargeBasis;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class ChargeBasisDao {
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS =
      Arrays.asList(chargeBasis.applicationId, chargeBasis.id, chargeBasis.entryNumber);

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
    Set<ChargeBasisEntry> entriesToUpdate = entries.stream().filter(l -> isExistingEntry(l, oldEntries) && hasChanges(l, oldEntries)).collect(Collectors.toSet());
    List<ChargeBasisEntry> entriesToAdd = entries.stream().filter(e -> !hasEntryWithId(oldEntries, e.getId())).collect(Collectors.toList());
    Set<Integer> entryIdsToDelete = oldEntries.stream().filter(oe -> !hasEntryWithId(entries, oe.getId())).map(e -> e.getId()).collect(Collectors.toSet());
    return new ChargeBasisModification(applicationId, entriesToAdd, entryIdsToDelete, entriesToUpdate, manuallySet);
  }

  private boolean hasEntryWithId(List<ChargeBasisEntry> entries, Integer id) {
     return entries.stream().anyMatch(e -> Objects.equal(e.getId(), id));
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
  }

  private void updateEntries(Set<ChargeBasisEntry> entriesToUpdate) {
    for (ChargeBasisEntry entry : entriesToUpdate) {
      queryFactory.update(chargeBasis).populate(entry, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
          .where(chargeBasis.id.eq(entry.getId())).execute();
    }
  }

  private boolean hasChanges(ChargeBasisEntry entry, List<ChargeBasisEntry> oldEntries) {
    ChargeBasisEntry old = oldEntries.stream().filter(oe -> oe.getId().equals(entry.getId())).findFirst().get();
    return !entry.equals(old);

  }

  private int nextEntryNumber(int applicationId, boolean manuallySet) {
    Integer maxEntryNumber = queryFactory.select(chargeBasis.entryNumber.max()).from(chargeBasis)
        .where(chargeBasis.applicationId.eq(applicationId)).fetchFirst();
    return maxEntryNumber != null ? maxEntryNumber + 1 : 0;
  }

  private void insertEntries(int applicationId, Collection<ChargeBasisEntry> entries, boolean manuallySet, int nextEntryNumber) {
    if (!entries.isEmpty()) {
      SQLInsertClause insert = queryFactory.insert(chargeBasis);
      for (ChargeBasisEntry entry : entries) {
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
    queryFactory.delete(chargeBasis).where(chargeBasis.id.in(entryIdsToDelete)).execute();
    // Delete possible dangling referred tags left by above delete
    queryFactory.delete(chargeBasis)
    .where(chargeBasis.applicationId.eq(applicationId).and(chargeBasis.referredTag.isNotNull()).and(chargeBasis.referredTag.notIn(
        select(chargeBasis.tag).from(chargeBasis).where(chargeBasis.applicationId.eq(applicationId).and(chargeBasis.tag.isNotNull()))))).execute();
  }

  private boolean isExistingEntry(ChargeBasisEntry entry, List<ChargeBasisEntry> oldEntries) {
    return oldEntries.stream().anyMatch(oe -> oe.getId().equals(entry.getId()));
  }
}
