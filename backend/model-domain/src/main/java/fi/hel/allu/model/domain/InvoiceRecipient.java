package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.CustomerType;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Contains a customer information at the time when an invoice was created.
 */
public class InvoiceRecipient {
  private Integer id;
  @NotNull
  private CustomerType type;
  @NotBlank
  private String name;
  private String email;
  private String phone;
  private String registryKey;
  private String ovt;
  private String streetAddress;
  private String postalCode;
  private String city;
  private String invoicingOperator;

  public InvoiceRecipient() {
  }

  public InvoiceRecipient(Customer customer) {
    this.type = customer.getType();
    this.name = customer.getName();
    this.email = customer.getEmail();
    this.phone = customer.getPhone();
    this.registryKey = customer.getRegistryKey();
    this.ovt = customer.getOvt();
    final PostalAddress postalAddress = customer.getPostalAddress();
    if (postalAddress != null) {
      this.streetAddress = postalAddress.getStreetAddress();
      this.postalCode = postalAddress.getPostalCode();
      this.city = postalAddress.getCity();
    }
    this.invoicingOperator = customer.getInvoicingOperator();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Type of the customer.
   *
   * @return  Type of the customer.
   */
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  /**
   * The name of the customer person, company or organization.
   *
   * @return  The name of the customer person, company or organization.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Email of the customer person, company or organization.
   *
   * @return Email of the customer person, company or organization.
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Phone number of the customer person, company or organization.
   *
   * @return  Phone number of the customer person, company or organization.
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * The registry key (social security number or business id i.e. Y-tunnus) of the customer person, company or organization.
   *
   * @return  The registry key (social security number or business id i.e. Y-tunnus) of the customer person, company or organization.
   */
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  /**
   * E-invoice identifier of the customer (OVT-tunnus).
   */
  public String getOvt() {
    return ovt;
  }

  public void setOvt(String ovt) {
    this.ovt = ovt;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  /**
   * in Finnish: postinumero
   */
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * in Finnish: kaupunki
   */
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getInvoicingOperator() {
    return invoicingOperator;
  }

  public void setInvoicingOperator(String invoicingOperator) {
    this.invoicingOperator = invoicingOperator;
  }

  public Customer asCustomer() {
    Customer customer = new Customer();
    customer.setType(this.type);
    customer.setName(this.name);
    customer.setEmail(this.email);
    customer.setPhone(this.phone);
    customer.setRegistryKey(this.registryKey);
    customer.setOvt(this.ovt);
    if (this.streetAddress != null) {
      final PostalAddress postalAddress = new PostalAddress(this.streetAddress, this.postalCode, this.city);
      customer.setPostalAddress(postalAddress);
    }
    customer.setInvoicingOperator(this.invoicingOperator);
    return customer;
  }
}
