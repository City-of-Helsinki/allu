package fi.hel.allu.ui.domain;

import java.util.List;
import java.util.Optional;

import fi.hel.allu.servicecore.domain.CustomerJson;

public class  CustomerExportJson {

  private final CustomerJson customer;
  private final List<String> relatedApplications;

  public CustomerExportJson(CustomerJson customer, List<String> relatedApplications) {
    this.customer = customer;
    this.relatedApplications = relatedApplications;
  }

  public String getType() {
    return customer.getType().name();
  }

  public String getAlluId() {
    return customer.getId().toString();
  }

  public String getName() {
    return customer.getName();
  }

  public String getStreetAddress() {
    return customer.getPostalAddress() != null ? emptyIfNull(customer.getPostalAddress().getStreetAddress()) : "";
  }

  public String getPostalCode() {
    return customer.getPostalAddress() != null ? emptyIfNull(customer.getPostalAddress().getPostalCode()) : "";
  }

  public String getCity() {
    return customer.getPostalAddress() != null ? emptyIfNull(customer.getPostalAddress().getCity()) : "";
  }

  public String getKey() {
    return customer.getRegistryKey();
  }

  public String getSapCustomerNumber() {
    return customer.getSapCustomerNumber();
  }

  public String getInvoicingOperator() {
    return customer.getInvoicingOperator();
  }

  public String getOvt() {
    return customer.getOvt();
  }

  public String getSemicolonSeparatedApplicationIds() {
    return String.join(";", relatedApplications);
  }

  private static String emptyIfNull(String value) {
    return Optional.ofNullable(value).orElse("");
  }
}
