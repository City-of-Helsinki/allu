package fi.hel.allu.model.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.types.StoredFilterType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.StoredFilter;
import fi.hel.allu.model.querydsl.ExcludingMapper;


import static fi.hel.allu.QStoredFilter.storedFilter;


import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class StoredFilterDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<StoredFilter> storedFilterBean = bean(StoredFilter.class, storedFilter.all());

  /** Fields that won't be updated in regular updates */
  private static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(storedFilter.id,
      storedFilter.defaultFilter, storedFilter.type, storedFilter.userId);

  @Transactional
  public Optional<StoredFilter> findById(int id) {
    StoredFilter filter = queryFactory
        .select(storedFilterBean)
        .from(storedFilter)
        .where(storedFilter.id.eq(id))
        .fetchOne();
    return Optional.ofNullable(filter);
  }

  @Transactional
  public List<StoredFilter> findByUserAndType(int userId, StoredFilterType type) {
    return queryFactory
        .select(storedFilterBean)
        .from(storedFilter)
        .where(storedFilter.userId.eq(userId)
            .and(storedFilter.type.eq(type)))
        .fetch();
  }

  @Transactional
  public StoredFilter insert(StoredFilter filter) {
    if (filter.isDefaultFilter()) {
      removeOldDefault(filter);
    }

    Integer id = queryFactory
        .insert(storedFilter)
        .populate(filter)
        .executeWithKey(storedFilter.id);
    return findById(id).get();
  }

  @Transactional
  public StoredFilter update(StoredFilter filter) {
    long changed = queryFactory
        .update(storedFilter)
        .populate(filter, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(storedFilter.id.eq(filter.getId()))
        .execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update stored filter", filter.getId());
    }
    return findById(filter.getId()).get();
  }

  @Transactional
  public void delete(int id) {
    long count = queryFactory
        .delete(storedFilter)
        .where(storedFilter.id.eq(id))
        .execute();
    if (count == 0) {
      throw new NoSuchEntityException("Deleting stored filter failed", Integer.toString(id));
    }
  }

  @Transactional
  public void setAsDefault(int filterId) {
    StoredFilter filter = findById(filterId)
        .orElseThrow(() -> new NoSuchEntityException("No filter found with id", filterId));

    removeOldDefault(filter);

    queryFactory
        .update(storedFilter)
        .set(storedFilter.defaultFilter, true)
        .where(storedFilter.id.eq(filterId))
        .execute();
  }

  private void removeOldDefault(StoredFilter newFilter) {
    queryFactory
        .update(storedFilter)
        .set(storedFilter.defaultFilter, false)
        .where(storedFilter.defaultFilter.isTrue()
            .and(storedFilter.type.eq(newFilter.getType())
                .and(storedFilter.userId.eq(newFilter.getUserId()))))
        .execute();
  }
}
