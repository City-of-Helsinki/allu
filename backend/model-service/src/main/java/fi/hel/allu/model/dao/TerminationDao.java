package fi.hel.allu.model.dao;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QTermination.termination;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class TerminationDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  private QBean<TerminationInfo> terminationInfo = bean(TerminationInfo.class, termination.all());
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS =
      Arrays.asList(termination.id, termination.applicationId, termination.creationTime);

  @Transactional(readOnly = true)
  public TerminationInfo getTerminationInfo(Integer applicationId) {
    return queryFactory.select(terminationInfo).from(termination)
        .where(termination.applicationId.eq(applicationId))
        .fetchOne();
  }

  @Transactional
  public TerminationInfo insertTerminationInfo(Integer applicationId, TerminationInfo info) {
    if (getTerminationInfo(applicationId) != null) {
      throw new NonUniqueException("Termination already exists for application", applicationId.toString());
    }

    queryFactory.insert(termination)
        .columns(termination.applicationId, termination.creationTime, termination.terminationTime, termination.reason)
        .values(applicationId, ZonedDateTime.now(), info.getTerminationTime(), info.getReason())
        .execute();
    return getTerminationInfo(applicationId);
  }

  @Transactional
  public TerminationInfo updateTerminationInfo(Integer applicationId, TerminationInfo info) {
    TerminationInfo existingInfo = getTerminationInfo(applicationId);
    if (existingInfo != null) {
      queryFactory.update(termination)
          .populate(info, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
          .where(termination.applicationId.eq(applicationId))
          .execute();
      return getTerminationInfo(applicationId);
    } else {
      throw new NoSuchEntityException("termination.notFound", applicationId);
    }
  }

  @Transactional
  public void storeTerminationDocument(Integer applicationId, byte[] data) {
    if (getTerminationInfo(applicationId) != null) {
      queryFactory.update(termination)
          .where(termination.applicationId.eq(applicationId))
          .set(termination.document, data)
          .execute();
    } else {
      throw new NoSuchEntityException("termination.notFound", applicationId);
    }
  }

  @Transactional(readOnly = true)
  public byte[] getTerminationDocument(Integer applicationId) {
    byte[] document = queryFactory.select(termination.document).from(termination)
        .where(termination.applicationId.eq(applicationId))
        .fetchOne();

    return Optional.ofNullable(document)
      .orElseThrow(() -> new NoSuchEntityException("termination.notFound", applicationId));
  }

  @Transactional(readOnly = true)
  public List<Integer> getApplicationsPendingForTermination() {
    BooleanExpression pendingShortTermRental = application.type.eq(ApplicationType.SHORT_TERM_RENTAL)
        .and(application.status.eq(StatusType.DECISION));

    BooleanExpression pendingPlacementContract = application.type.eq(ApplicationType.PLACEMENT_CONTRACT)
        .and(application.status.in(StatusType.DECISION, StatusType.ARCHIVED));

    ZonedDateTime startOfTheDay = TimeUtil.startOfDay(ZonedDateTime.now());

    return queryFactory.select(termination.applicationId).from(termination)
        .leftJoin(application).on(termination.applicationId.eq(application.id))
        .where(termination.terminationTime.before(startOfTheDay)
            .and(pendingShortTermRental.or(pendingPlacementContract)))
        .fetch();
  }
}
