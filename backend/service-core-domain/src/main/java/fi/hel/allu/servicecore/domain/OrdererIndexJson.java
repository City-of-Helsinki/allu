package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import javax.validation.constraints.NotNull;

/**
 * Represents information how to identify orderer from cable report
 */
public class OrdererIndexJson {
  @NotNull
  private CustomerRoleType customerRoleType;
  @NotNull
  private Integer index;

  /**
   * Roletype of the customer whose contact list is indexed by index
   */
  public CustomerRoleType getCustomerRoleType() {
    return customerRoleType;
  }

  public void setCustomerRoleType(CustomerRoleType customerRoleType) {
    this.customerRoleType = customerRoleType;
  }

  /**
   * Marks the index of orderer from customer's list of contacts
   */
  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }
}
