package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.SupervisionTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QSupervisionTask.supervisionTask;

@Repository
public class SupervisionTaskDao {
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
    Integer id = queryFactory.insert(supervisionTask).populate(st).executeWithKey(supervisionTask.id);
    return findById(id).get();
  }

  @Transactional
  public SupervisionTask update(SupervisionTask st) {
    long changed = queryFactory.update(supervisionTask).populate(st).where(supervisionTask.id.eq(st.getId())).execute();
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
}
