package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.common.PostalAddressUtil;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplicant.applicant;
import static fi.hel.allu.QPostalAddress.postalAddress;

@Repository
public class ApplicantDao {
  @Autowired
  private SQLQueryFactory queryFactory;
  @Autowired
  PostalAddressDao postalAddressDao;

  final QBean<Applicant> applicantBean = bean(Applicant.class, applicant.all());
  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());

  @Transactional(readOnly = true)
  public Optional<Applicant> findById(int id) {
    Tuple applicantPostalAddress = queryFactory
        .select(applicantBean, postalAddressBean)
        .from(applicant)
        .leftJoin(postalAddress).on(applicant.postalAddressId.eq(postalAddress.id))
        .where(applicant.id.eq(id)).fetchOne();
    Applicant appl = null;
    if (applicantPostalAddress != null) {
      appl = PostalAddressUtil.mapPostalAddress(applicantPostalAddress).get(0, Applicant.class);
    }
    return Optional.ofNullable(appl);
  }

  @Transactional(readOnly = true)
  public List<Applicant> findAll() {
    List<Tuple> applicantPostalAddress = queryFactory
        .select(applicantBean, postalAddressBean)
        .from(applicant)
        .leftJoin(postalAddress).on(applicant.postalAddressId.eq(postalAddress.id)).fetch();

    return applicantPostalAddress.stream()
        .map(apa -> PostalAddressUtil.mapPostalAddress(apa).get(0, Applicant.class))
        .collect(Collectors.toList());
  }

  @Transactional
  public Applicant insert(Applicant applicantData) {
    applicantData.setId(null);
    Integer id = queryFactory
        .insert(applicant)
        .populate(applicantData).set(applicant.postalAddressId, postalAddressDao.insertIfNotNull(applicantData))
        .executeWithKey(applicant.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Applicant update(int id, Applicant applicantData) {
    applicantData.setId(id);

    Optional<Applicant> currentApplicantOpt = findById(id);
    if (!currentApplicantOpt.isPresent()) {
      throw new NoSuchEntityException("Attempted to update non-existent applicant", Integer.toString(id));
    }
    Applicant currentApplicant = currentApplicantOpt.get();

    Integer deletedPostalAddressId = postalAddressDao.mapAndUpdatePostalAddress(currentApplicant, applicantData);
    Integer postalAddressId = Optional.ofNullable(currentApplicant.getPostalAddress()).map(pAddress -> pAddress.getId()).orElse(null);

    long changed = queryFactory
        .update(applicant)
        .populate(applicantData, DefaultMapper.WITH_NULL_BINDINGS)
        .set(applicant.postalAddressId, postalAddressId)
        .where(applicant.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }

    if (deletedPostalAddressId != null) {
      postalAddressDao.delete(Collections.singletonList(deletedPostalAddressId));
    }

    return findById(id).get();
  }
}
