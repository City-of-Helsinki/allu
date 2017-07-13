package fi.hel.allu.model.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * REST payload for customer change requests (create and update)
 */
public class CustomerChange {
  @NotNull
  private Integer userId;

  @Valid
  @NotNull
  private Customer customer;

  // for deserialization
  public CustomerChange() {
  }

  public CustomerChange(Integer userId, Customer customer) {
    this.userId = userId;
    this.customer = customer;
  }

  /**
   * Get the requesting user ID
   *
   * @return user id
   */
  public Integer getUserId() {
    return userId;
  }

  /**
   * Get the customer data for the request
   *
   * @return customer data
   */
  public Customer getCustomer() {
    return customer;
  }

}
