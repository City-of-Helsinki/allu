package fi.hel.allu.model.dao;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import fi.hel.allu.common.util.EmptyUtil;
import fi.hel.allu.model.common.PostalAddressUtil;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.domain.PostalAddress;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QDistributionEntry.distributionEntry;
import static fi.hel.allu.QPostalAddress.postalAddress;

/**
 * Distribution entry database access.
 */
@Repository
public class DistributionEntryDao {

  private final SQLQueryFactory queryFactory;
  private final PostalAddressDao postalAddressDao;

  final QBean<DistributionEntry> distributionEntryBean = bean(DistributionEntry.class, distributionEntry.all());
  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());

  public DistributionEntryDao(SQLQueryFactory queryFactory, PostalAddressDao postalAddressDao) {
    this.queryFactory = queryFactory;
    this.postalAddressDao = postalAddressDao;
  }

  @Transactional(readOnly = true)
  public List<DistributionEntry> findById(List<Integer> dEntryIds) {
    return findByPredicate(distributionEntry.id.in(dEntryIds));
  }

  @Transactional(readOnly = true)
  public List<DistributionEntry> findByApplicationId(int applicationId) {
    return findByPredicate(distributionEntry.applicationId.eq(applicationId));
  }

  @Transactional(readOnly = true)
  public List<DistributionEntry> findByApplicationIds(Integer[] applicationIds) {
    return findByPredicate(distributionEntry.applicationId.in(applicationIds));
  }

  @Transactional
  public List<DistributionEntry> insert(List<DistributionEntry> dEntries) {
    if (EmptyUtil.isNotEmpty(dEntries)) {
      SQLInsertClause insertClause = queryFactory.insert(distributionEntry);
      for (DistributionEntry dEntry : dEntries) {
        dEntry.setId(null);
        insertClause.populate(dEntry).set(distributionEntry.postalAddressId, postalAddressDao.insertIfNotNull(dEntry))
                .addBatch();
      }
      List<Integer> dEntryIds = insertClause.executeWithKeys(distributionEntry.id);
      return findById(dEntryIds);
    } else {
      return new ArrayList<>();
    }

  }

  @Transactional
  public void deleteByApplication(int applicationId) {
    List<Integer> dEntryIds = queryFactory
        .select(distributionEntry.postalAddressId)
        .from(distributionEntry)
        .where(distributionEntry.applicationId.eq(applicationId).and(distributionEntry.postalAddressId.isNotNull())).fetch();
    queryFactory.delete(distributionEntry).where(distributionEntry.applicationId.eq(applicationId)).execute();
    if (!dEntryIds.isEmpty()) {
      postalAddressDao.delete(dEntryIds);
    }
  }

  @Transactional
  public void replaceEntries(int applicationId, List<DistributionEntry> dEntries) {
    dEntries.forEach(entry -> entry.setApplicationId(applicationId));
    deleteByApplication(applicationId);
    insert(dEntries);
  }

  @Transactional
  public void copy(int fromApplication, int toApplication) {
    List<DistributionEntry> existing = findByApplicationId(fromApplication);
    replaceEntries(toApplication, existing);
  }

  private List<DistributionEntry> findByPredicate(Predicate predicate) {
    List<Tuple> dEntryPostalAddress = queryFactory
        .select(distributionEntryBean, postalAddressBean)
        .from(distributionEntry)
        .leftJoin(postalAddress).on(distributionEntry.postalAddressId.eq(postalAddress.id))
        .where(predicate)
        .fetch();
    return dEntryPostalAddress.stream()
        .map(PostalAddressUtil::mapPostalAddress)
        .map(tuple -> tuple.get(0, DistributionEntry.class))
        .collect(Collectors.toList());
  }
}