package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.model.domain.Applicant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QApplicant.applicant;

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
        long changed = queryFactory.update(applicant).populate(applicantData).where(applicant.id.eq(id)).execute();
        if (changed != 1) {
            throw new QueryException("Failed to update the record");
        }
        return findById(id).get();
    }
}
