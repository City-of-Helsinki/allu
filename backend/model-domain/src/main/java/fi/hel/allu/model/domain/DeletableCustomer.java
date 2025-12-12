package fi.hel.allu.model.domain;

public class DeletableCustomer {
  private Integer id;
  private String sapCustomerNumber;

  DeletableCustomer() {
  }

  DeletableCustomer(Integer id, String sapCustomerNumber) {
    this.id = id;
    this.sapCustomerNumber = sapCustomerNumber;
  }

  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}


