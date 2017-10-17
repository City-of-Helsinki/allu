package fi.hel.allu.model.dao;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QSupervisionTask.supervisionTask;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class SupervisionTaskDao {

  /** Fields that won't be updated in regular updates */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(supervisionTask.id, supervisionTask.creationTime);

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<SupervisionTask> supervisionTaskBean = bean(SupervisionTask.class, supervisionTask.all());

  @Transactional(readOnly = true)
  public Optional<SupervisionTask> findById(int id) {
    SupervisionTask st = queryFactory
        .select(supervisionTaskBean).from(supervisionTask).where(supervisionTask.id.eq(id)).fetchOne();
    return Optional.ofNullable(st);
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationId(int applicationId) {
    return queryFactory.select(supervisionTaskBean).from(supervisionTask).where(supervisionTask.applicationId.eq(applicationId)).fetch();
  }

  @Transactional
  public SupervisionTask insert(SupervisionTask st) {
    st.setCreationTime(ZonedDateTime.now());
    st.setStatus(SupervisionTaskStatusType.OPEN);
    Integer id = queryFactory.insert(supervisionTask).populate(st).executeWithKey(supervisionTask.id);
    return findById(id).get();
  }

  @Transactional
  public SupervisionTask update(SupervisionTask st) {
    long changed = queryFactory.update(supervisionTask)
        .populate(st, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(supervisionTask.id.eq(st.getId())).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update supervision task", st.getId());
    }
    return findById(st.getId()).get();
  }

  @Transactional
  public void delete(int id) {
    SupervisionTask st = findById(id).orElseThrow(() -> new NoSuchEntityException("Attempted to delete non-existent supervision task", id));
    if (st.getStatus().equals(SupervisionTaskStatusType.OPEN)) {
      queryFactory.delete(supervisionTask).where(supervisionTask.id.eq(id)).execute();
    } else {
      throw new IllegalStateException("Attempted to delete processed supervision task with current state " + st.getStatus());
    }
  }

  @Transactional
  public int updateHandler(int handler, List<Integer> tasks) {
    return (int) queryFactory
        .update(supervisionTask)
        .set(supervisionTask.handlerId, handler)
        .where(supervisionTask.id.in(tasks))
        .execute();
  }

  @Transactional
  public int removeHandler(List<Integer> tasks) {
    return (int) queryFactory
        .update(supervisionTask)
        .setNull(supervisionTask.handlerId)
        .where(supervisionTask.id.in(tasks))
        .execute();
  }

  @Transactional
  public List<SupervisionTask> search(SupervisionTaskSearchCriteria searchCriteria) {
    BooleanExpression conditions = conditions(searchCriteria)
        .reduce((left, right) -> left.and(right))
        .orElse(Expressions.TRUE);

    return queryFactory.select(supervisionTaskBean)
        .from(supervisionTask)
        .join(application).on(supervisionTask.applicationId.eq(application.id))
        .where(conditions)
        .fetch();
  }

  private Stream<BooleanExpression> conditions(SupervisionTaskSearchCriteria searchCriteria) {
    return Stream.of(
        Optional.of(supervisionTask.status.eq(SupervisionTaskStatusType.OPEN)),
        values(searchCriteria.getTaskTypes()).map(supervisionTask.type::in),
        Optional.ofNullable(searchCriteria.getHandlerId()).map(supervisionTask.handlerId::eq),
        Optional.ofNullable(searchCriteria.getAfter()).map(supervisionTask.plannedFinishingTime::goe),
        Optional.ofNullable(searchCriteria.getBefore()).map(supervisionTask.plannedFinishingTime::loe),
        Optional.ofNullable(searchCriteria.getApplicationId()).map(application.applicationId::startsWith),
        values(searchCriteria.getApplicationTypes()).map(application.type::in),
        values(searchCriteria.getApplicationStatus()).map(application.status::in)
    ).filter(opt -> opt.isPresent())
     .map(opt -> opt.get());
  }


  private <T> Optional<List<T>> values(List<T> valueList) {
    return Optional.ofNullable(valueList)
        .filter(values -> !values.isEmpty());
  }
}
