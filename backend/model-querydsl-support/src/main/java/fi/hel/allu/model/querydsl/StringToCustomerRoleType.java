package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.CustomerRoleType;

/*
 * Tells QueryDSL how to map enum type to SQL
 */
public class StringToCustomerRoleType extends EnumAsObjectType<CustomerRoleType> {

  public StringToCustomerRoleType() {
    super(CustomerRoleType.class);
  }

}
