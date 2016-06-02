package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.model.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QCustomer.customer;

public class CustomerDao {

    @Autowired
    private SQLQueryFactory queryFactory;

    private final QBean<Customer> customerBean = bean(Customer.class, customer.all());


    @Transactional(readOnly = true)
    public Optional<Customer> findById(int id) {
        Customer cust = queryFactory.select(customerBean).from(customer).where(customer.id.eq(id)).fetchOne();
        return Optional.ofNullable(cust);
    }

    @Transactional
    public Customer insert(Customer customerData) {
        Integer id = queryFactory.insert(customer).populate(customerData).executeWithKey(customer.id);
        return findById(id).get();
    }

    @Transactional
    public Customer update(int id, Customer customerData) {
        customerData.setId(id);
        long changed = queryFactory.update(customer).populate(customerData).where(customer.id.eq(id)).execute();
        if (changed != 1) {
            throw new QueryException("Failed to update the record");
        }
        return findById(id).get();
    }
}
