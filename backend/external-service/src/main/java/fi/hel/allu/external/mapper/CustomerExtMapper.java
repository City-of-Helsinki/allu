package fi.hel.allu.external.mapper;

import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;

import fi.hel.allu.external.domain.CustomerExt;
import fi.hel.allu.external.domain.PostalAddressExt;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;

public class CustomerExtMapper {
  public static CustomerJson mapCustomerJson(CustomerExt customerExt) {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setId(customerExt.getId());
    customerJson.setName(customerExt.getName());
    customerJson.setType(customerExt.getType());
    customerJson.setRegistryKey(customerExt.getRegistryKey());
    customerJson.setOvt(customerExt.getOvt());
    customerJson.setEmail(customerExt.getEmail());
    customerJson.setPhone(customerExt.getPhone());
    customerJson.setSapCustomerNumber(customerExt.getSapCustomerNumber());
    customerJson.setInvoicingProhibited(BooleanUtils.isTrue(customerExt.getInvoicingProhibited()));
    customerJson.setInvoicingOperator(customerExt.getInvoicingOperator());
    if (customerExt.getPostalAddress() != null) {
      customerJson.setPostalAddress(new PostalAddressJson(
          customerExt.getPostalAddress().getStreetAddress(),
          customerExt.getPostalAddress().getPostalCode(),
          customerExt.getPostalAddress().getCity()));
    }
    customerJson.setActive(true);
    return customerJson;
  }

  public static CustomerExt mapCustomerExt(CustomerJson customerJson) {
    CustomerExt customerExt = new CustomerExt();
    customerExt.setId(customerJson.getId());
    customerExt.setName(customerJson.getName());
    customerExt.setType(customerJson.getType());
    customerExt.setRegistryKey(customerJson.getRegistryKey());
    customerExt.setOvt(customerJson.getOvt());
    customerExt.setEmail(customerJson.getEmail());
    customerExt.setPhone(customerJson.getPhone());
    customerExt.setSapCustomerNumber(customerJson.getSapCustomerNumber());
    customerExt.setInvoicingProhibited(customerJson.isInvoicingProhibited());
    customerExt.setInvoicingOperator(customerJson.getInvoicingOperator());
    if (customerJson.getPostalAddress() != null) {
      customerExt.setPostalAddress(new PostalAddressExt(
          customerJson.getPostalAddress().getStreetAddress(),
          customerJson.getPostalAddress().getPostalCode(),
          customerJson.getPostalAddress().getCity()));
    }
    return customerExt;
  }

  /**
   * Update current customerJson with the customerExt data. Updates only properties having non-null value
   * in customerExt
   */
  public static CustomerJson mergeCustomerJson(CustomerJson currentCustomerJson, CustomerExt customerExt) {
    Optional.ofNullable(customerExt.getName()).ifPresent(s -> currentCustomerJson.setName(s));
    Optional.ofNullable(customerExt.getType()).ifPresent(s -> currentCustomerJson.setType(s));
    Optional.ofNullable(customerExt.getRegistryKey()).ifPresent(s -> currentCustomerJson.setRegistryKey(s));
    Optional.ofNullable(customerExt.getOvt()).ifPresent(s -> currentCustomerJson.setOvt(s));
    Optional.ofNullable(customerExt.getEmail()).ifPresent(s -> currentCustomerJson.setEmail(s));
    Optional.ofNullable(customerExt.getPhone()).ifPresent(s -> currentCustomerJson.setPhone(s));
    Optional.ofNullable(customerExt.getSapCustomerNumber()).ifPresent(s -> currentCustomerJson.setSapCustomerNumber(s));
    Optional.ofNullable(customerExt.getInvoicingProhibited()).ifPresent(s -> currentCustomerJson.setInvoicingProhibited(s.booleanValue()));
    Optional.ofNullable(customerExt.getInvoicingOperator()).ifPresent(s -> currentCustomerJson.setInvoicingOperator(s));
    Optional.ofNullable(customerExt.getPostalAddress())
       .map(a -> new PostalAddressJson(
          customerExt.getPostalAddress().getStreetAddress(),
          customerExt.getPostalAddress().getPostalCode(),
          customerExt.getPostalAddress().getCity()))
       .ifPresent(a -> currentCustomerJson.setPostalAddress(a));
    return currentCustomerJson;
  }
}
