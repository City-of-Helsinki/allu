package fi.hel.allu.model.domain;

public class DeletableCustomer {
  private Integer id;
  private String sapCustomerNumber;
  private String name;

  public DeletableCustomer() {
  }

  public DeletableCustomer(Integer id, String sapCustomerNumber) {
    this.id = id;
    this.sapCustomerNumber = sapCustomerNumber;
  }

  public DeletableCustomer(Integer id, String sapCustomerNumber, String name) {
    this.id = id;
    this.sapCustomerNumber = sapCustomerNumber;
    this.name = name;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
