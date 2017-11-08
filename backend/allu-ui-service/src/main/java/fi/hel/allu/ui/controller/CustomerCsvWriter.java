package fi.hel.allu.ui.controller;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;

/**
 * Writes customer fields in CSV format.
 *
 */
public class CustomerCsvWriter {

  private static final String HEADER_ROW = "type,alluid,name,streetaddress,postalcode,city,key,ovt";
  private PrintWriter writer;
  private List<CustomerJson> customers;

  public CustomerCsvWriter(PrintWriter writer, List<CustomerJson> customers) {
    this.writer = writer;
    this.customers = customers;
  }

  public void write() {
    writer.println(HEADER_ROW);
    List<String> lines = customers.stream().map(c -> getCSVFields(c)).collect(Collectors.toList());
    for (String line : lines) {
      writer.println(line);
    }
  }

  private String getCSVFields(CustomerJson customer) {
    PostalAddressJson address = Optional.ofNullable(customer.getPostalAddress()).orElse(new PostalAddressJson());
    return String.join(",", customer.getType().name(), customer.getId().toString(), customer.getName(),
        emptyIfNull(address.getStreetAddress()), emptyIfNull(address.getPostalCode()), emptyIfNull(address.getCity()),
        customer.getRegistryKey(), emptyIfNull(customer.getOvt()));
  }

  private String emptyIfNull(String value) {
    return Optional.ofNullable(value).orElse("");
  }
}
