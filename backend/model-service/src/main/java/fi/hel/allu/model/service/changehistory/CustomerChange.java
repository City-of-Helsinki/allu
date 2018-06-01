package fi.hel.allu.model.service.changehistory;

import fi.hel.allu.model.domain.Customer;

public class CustomerChange {
  private final String customerName;
  private final Integer id;

  public CustomerChange(Customer customer) {
    if (customer != null) {
      this.customerName = customer.getName();
      this.id = customer.getId();
    } else {
      this.customerName = null;
      this.id = null;
    }
  }

  public String getCustomerName() {
    return customerName;
  }

  public Integer getId() {
    return id;
  }
}
