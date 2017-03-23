package fi.hel.allu.model.dao;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.model.common.PostalAddressUtil;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.domain.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private SQLQueryFactory queryFactory;
  @Autowired
  PostalAddressDao postalAddressDao;

  final QBean<DistributionEntry> distributionEntryBean = bean(DistributionEntry.class, distributionEntry.all());
  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());


  @Transactional(readOnly = true)
  public List<DistributionEntry> findById(List<Integer> dEntryIds) {
    return findByPredicate(distributionEntry.id.in(dEntryIds));
  }

  @Transactional(readOnly = true)
  public List<DistributionEntry> findByApplicationId(int applicationId) {
    return findByPredicate(distributionEntry.applicationId.eq(applicationId));
  }

  @Transactional
  public List<DistributionEntry> insert(List<DistributionEntry> dEntries) {
    List<Integer> dEntryIds = new ArrayList<>();
    dEntries.forEach(dEntry -> {
      dEntry.setId(null);
      dEntryIds.add(queryFactory
          .insert(distributionEntry).populate(dEntry).set(distributionEntry.postalAddressId, postalAddressDao.insertIfNotNull(dEntry))
          .executeWithKey(distributionEntry.id));
    });
    return findById(dEntryIds);
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

  private List<DistributionEntry> findByPredicate(Predicate predicate) {
    List<Tuple> dEntryPostalAddress = queryFactory
        .select(distributionEntryBean, postalAddressBean)
        .from(distributionEntry)
        .leftJoin(postalAddress).on(distributionEntry.postalAddressId.eq(postalAddress.id))
        .where(predicate)
        .fetch();
    return dEntryPostalAddress.stream()
        .map(tuple -> PostalAddressUtil.mapPostalAddress(tuple))
        .map(tuple -> tuple.get(0, DistributionEntry.class))
        .collect(Collectors.toList());
  }
}
