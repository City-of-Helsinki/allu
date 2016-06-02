package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.model.domain.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QOrganization.organization;

public class OrganizationDao {

    @Autowired
    private SQLQueryFactory queryFactory;

    private final QBean<Organization> organizationBean = bean(Organization.class, organization.all());


    @Transactional(readOnly = true)
    public Optional<Organization> findById(int id) {
        Organization org = queryFactory.select(organizationBean).from(organization).where(organization.id.eq(id)).fetchOne();
        return Optional.ofNullable(org);
    }

    @Transactional
    public Organization insert(Organization organizationData) {
        Integer id = queryFactory.insert(organization).populate(organizationData).executeWithKey(organization.id);
        if (id == null) {
            throw new QueryException("Failed to insert record");
        }
        return findById(id).get();
    }

    @Transactional
    public Organization update(int id, Organization organizationData) {
        organizationData.setId(id);
        long changed = queryFactory.update(organization).populate(organizationData).where(organization.id.eq(id)).execute();
        if (changed != 1) {
            throw new QueryException("Failed to update the record");
        }
        return findById(id).get();
    }
}
