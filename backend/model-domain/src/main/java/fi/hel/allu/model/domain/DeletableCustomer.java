package fi.hel.allu.model.domain;

public class DeletableCustomer {
  private Integer customerId;
  private String sapCustomerNumber;
  private String name;

  public DeletableCustomer() {
  }

  public DeletableCustomer(Integer customerId, String sapCustomerNumber) {
    this.customerId = customerId;
    this.sapCustomerNumber = sapCustomerNumber;
  }

  public DeletableCustomer(Integer customerId, String sapCustomerNumber, String name) {
    this.customerId = customerId;
    this.sapCustomerNumber = sapCustomerNumber;
    this.name = name;
  }

  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
