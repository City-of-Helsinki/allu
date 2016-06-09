package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCustomer.customer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Customer;

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
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Customer update(int id, Customer customerData) {
    customerData.setId(id);
    long changed = queryFactory.update(customer).populate(customerData).where(customer.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }
}
