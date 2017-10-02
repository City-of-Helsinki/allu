package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QChargeBasis.chargeBasis;

@Repository
public class ChargeBasisDao {
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

  /**
   * Set the charge basis entries for an application
   *
   * @param applicationId application ID
   * @param entries list of charge basis entries. Empty list is allowed and will
   *          remove existing entries.
   * @param manuallySet should the entries be marked as manually set or not?
   */
  @Transactional
  public void setChargeBasis(int applicationId, List<ChargeBasisEntry> entries, boolean manuallySet) {
    queryFactory.delete(chargeBasis)
        .where(chargeBasis.applicationId.eq(applicationId).and(chargeBasis.manuallySet.eq(manuallySet))).execute();
    if (!entries.isEmpty()) {
      SQLInsertClause insert = queryFactory.insert(chargeBasis);
      for (int entry = 0; entry < entries.size(); ++entry) {
        insert
            .populate(entries.get(entry),
                new ExcludingMapper(NullHandling.WITH_NULL_BINDINGS, Arrays.asList(chargeBasis.manuallySet)))
            .set(chargeBasis.applicationId, applicationId).set(chargeBasis.entryNumber, entry)
            .set(chargeBasis.manuallySet, manuallySet)
            .addBatch();
      }
      long numInserts = insert.execute();
      if (numInserts != entries.size()) {
        throw new QueryException("Failed to insert the entries, numInserts=" + numInserts);
      }
    }
  }
}
