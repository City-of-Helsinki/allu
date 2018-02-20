package fi.hel.allu.ui.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import fi.hel.allu.servicecore.domain.CustomerJson;

public abstract class CustomerExport {

  private static enum CustomerExportField {
    TYPE("type", (CustomerJson c) -> c.getType().name()),
    ALLUID("alluid", (CustomerJson c) -> c.getId().toString()),
    NAME("name", CustomerJson::getName),
    STREETADDRESS("streetaddress", (CustomerJson c) -> c.getPostalAddress() != null ? emptyIfNull(c.getPostalAddress().getStreetAddress()) : ""),
    POSTALCODE("postalcode", (CustomerJson c) -> c.getPostalAddress() != null ? emptyIfNull(c.getPostalAddress().getPostalCode()) : ""),
    CITY("city", (CustomerJson c) -> c.getPostalAddress() != null ? emptyIfNull(c.getPostalAddress().getCity()) : ""),
    KEY("key", CustomerJson::getRegistryKey),
    OVT("ovt", CustomerJson::getOvt),
    INVOICING_OPERATOR("operator", CustomerJson::getInvoicingOperator);

    private String header;
    private Function<CustomerJson, String> valueGetter;

    private CustomerExportField(String header, Function<CustomerJson, String> valueGetter) {
      this.header = header;
      this.valueGetter = valueGetter;
    }

    public String getHeader() {
      return header;
    }

    public String getValue(CustomerJson customer) {
      return valueGetter.apply(customer);
    }

    private static String emptyIfNull(String value) {
      return Optional.ofNullable(value).orElse("");
    }
  }

  private static final List<CustomerExportField> FIELDS = Arrays.asList(CustomerExportField.values());

  protected List<String> getHeaders() {
    return FIELDS.stream().map(f -> f.getHeader()).collect(Collectors.toList());
  }

  protected List<String> getValues(CustomerJson customer) {
    return FIELDS.stream().map(f -> f.getValue(customer)).collect(Collectors.toList());
  }

}
