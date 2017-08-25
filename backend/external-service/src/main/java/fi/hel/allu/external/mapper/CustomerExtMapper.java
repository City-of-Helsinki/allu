package fi.hel.allu.external.mapper;

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
    customerJson.setEmail(customerExt.getEmail());
    customerJson.setPhone(customerExt.getPhone());
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
    customerExt.setEmail(customerJson.getEmail());
    customerExt.setPhone(customerJson.getPhone());
    if (customerJson.getPostalAddress() != null) {
      customerExt.setPostalAddress(new PostalAddressExt(
          customerJson.getPostalAddress().getStreetAddress(),
          customerJson.getPostalAddress().getPostalCode(),
          customerJson.getPostalAddress().getCity()));
    }
    return customerExt;
  }

  public static CustomerJson mergeCustomerJson(CustomerJson currentCustomerJson, CustomerExt customerExt) {
    // TODO: update current customerJson with the customerExt data
    return null;
  }
}
