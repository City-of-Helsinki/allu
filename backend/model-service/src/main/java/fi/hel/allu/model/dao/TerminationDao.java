package fi.hel.allu.model.dao;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
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
}
