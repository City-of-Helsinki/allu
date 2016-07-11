package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplicant.applicant;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Applicant;

@Repository
public class ApplicantDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Applicant> applicantBean = bean(Applicant.class, applicant.all());

  @Transactional(readOnly = true)
  public Optional<Applicant> findById(int id) {
    Applicant appl = queryFactory.select(applicantBean).from(applicant).where(applicant.id.eq(id)).fetchOne();
    return Optional.ofNullable(appl);
  }

  @Transactional
  public Applicant insert(Applicant applicantData) {
    Integer id = queryFactory.insert(applicant).populate(applicantData).executeWithKey(applicant.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Applicant update(int id, Applicant applicantData) {
    applicantData.setId(id);
    long changed = queryFactory.update(applicant).populate(applicantData, DefaultMapper.WITH_NULL_BINDINGS)
        .where(applicant.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }
}
