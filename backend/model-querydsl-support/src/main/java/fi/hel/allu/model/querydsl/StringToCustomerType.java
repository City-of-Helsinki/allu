package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;

import fi.hel.allu.common.types.CustomerType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToCustomerType extends EnumAsObjectType<CustomerType> {

  public StringToCustomerType() {
    super(CustomerType.class);
  }

}
