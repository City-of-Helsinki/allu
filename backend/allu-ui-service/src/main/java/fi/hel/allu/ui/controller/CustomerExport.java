package fi.hel.allu.ui.controller;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import fi.hel.allu.ui.domain.CustomerExportJson;

public abstract class CustomerExport {

  private static enum CustomerExportField {
    TYPE("type", CustomerExportJson::getType),
    ALLUID("alluid", CustomerExportJson::getAlluId),
    NAME("name", CustomerExportJson::getName),
    STREETADDRESS("streetaddress", CustomerExportJson::getStreetAddress),
    POSTALCODE("postalcode", CustomerExportJson::getPostalCode),
    CITY("city", CustomerExportJson::getCity),
    KEY("key", CustomerExportJson::getKey),
    OVT("ovt", CustomerExportJson::getOvt),
    INVOICING_OPERATOR("operator", CustomerExportJson::getInvoicingOperator),
    SAP_NUMBER("sapcustomernumber", CustomerExportJson::getSapCustomerNumber),
    APPLICATIONS("applications", CustomerExportJson::getSemicolonSeparatedApplicationIds);

    private String header;
    private Function<CustomerExportJson, String> valueGetter;

    private CustomerExportField(String header, Function<CustomerExportJson, String> valueGetter) {
      this.header = header;
      this.valueGetter = valueGetter;
    }

    public String getHeader() {
      return header;
    }

    public String getValue(CustomerExportJson customer) {
      return valueGetter.apply(customer);
    }
  }

  public abstract void write(List<CustomerExportJson> customers);

  private static final List<CustomerExportField> FIELDS = Arrays.asList(CustomerExportField.values());

  protected List<String> getHeaders() {
    return FIELDS.stream().map(f -> f.getHeader()).collect(Collectors.toList());
  }

  protected List<String> getValues(CustomerExportJson customer) {
    return FIELDS.stream().map(f -> f.getValue(customer)).collect(Collectors.toList());
  }

}
