package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.model.domain.DistributionEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QDistributionEntry.distributionEntry;

/**
 * Distribution entry database access.
 */
@Repository
public class DistributionEntryDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<DistributionEntry> distributionEntryBean = bean(DistributionEntry.class, distributionEntry.all());


  @Transactional(readOnly = true)
  public List<DistributionEntry> findById(List<Integer> dEntryIds) {
    return queryFactory.select(distributionEntryBean).from(distributionEntry).where(distributionEntry.id.in(dEntryIds)).fetch();
  }

  @Transactional(readOnly = true)
  public List<DistributionEntry> findByApplicationId(int applicationId) {
    return queryFactory
        .select(distributionEntryBean)
        .from(distributionEntry)
        .where(distributionEntry.applicationId.eq(applicationId)).fetch();
  }

  @Transactional
  public List<DistributionEntry> insert(List<DistributionEntry> dEntries) {
    List<Integer> dEntryIds = new ArrayList<>();
    dEntries.forEach(dEntry -> {
      dEntry.setId(null);
      dEntryIds.add(queryFactory.insert(distributionEntry).populate(dEntry).executeWithKey(distributionEntry.id));
    });
    return findById(dEntryIds);
  }

  @Transactional
  public void deleteByApplication(int applicationId) {
    queryFactory.delete(distributionEntry).where(distributionEntry.applicationId.eq(applicationId)).execute();
  }
}
