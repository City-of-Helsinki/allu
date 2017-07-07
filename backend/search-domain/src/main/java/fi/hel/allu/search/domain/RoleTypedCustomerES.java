package fi.hel.allu.search.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.HashMap;
import java.util.Map;

/**
 * Support for storing customer with role flattened as an attribute.
 */
public class RoleTypedCustomerES {
  private Map<CustomerRoleType, CustomerWithContactsES> roleToCustomer = new HashMap<>();

  public RoleTypedCustomerES() {
    // JSON de-serialization
  }

  public RoleTypedCustomerES(Map<CustomerRoleType, CustomerWithContactsES> roleToCustomer) {
    this.roleToCustomer = roleToCustomer;
  }

  public CustomerWithContactsES getApplicant() {
    return roleToCustomer.get(CustomerRoleType.APPLICANT);
  }

  public void setApplicant(CustomerWithContactsES cwc) {
    this.roleToCustomer.put(CustomerRoleType.APPLICANT, cwc);
  }

  public CustomerWithContactsES getPropertyDeveloper() {
    return roleToCustomer.get(CustomerRoleType.PROPERTY_DEVELOPER);
  }

  public void setPropertyDeveloper(CustomerWithContactsES cwc) {
    this.roleToCustomer.put(CustomerRoleType.PROPERTY_DEVELOPER, cwc);
  }

  public CustomerWithContactsES getContractor() {
    return roleToCustomer.get(CustomerRoleType.CONTRACTOR);
  }

  public void setContractor(CustomerWithContactsES cwc) {
    this.roleToCustomer.put(CustomerRoleType.CONTRACTOR, cwc);
  }

  public CustomerWithContactsES getRepresentative() {
    return roleToCustomer.get(CustomerRoleType.REPRESENTATIVE);
  }

  public void setRepresentative(CustomerWithContactsES cwc) {
    this.roleToCustomer.put(CustomerRoleType.REPRESENTATIVE, cwc);
  }
}
