package fi.hel.allu.supervision.api.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.supervision.api.domain.ContactSearchResult;
import fi.hel.allu.supervision.api.domain.CustomerSearchResult;

@Component
public class CustomerSearchResultMapper {

  @Autowired
  private CustomerService customerService;

  public CustomerSearchResult mapToSearchResult(CustomerJson customerJson) {
    CustomerSearchResult result = new CustomerSearchResult();

    result.setId(customerJson.getId());
    result.setType(customerJson.getType());
    result.setName(customerJson.getName());
    result.setPostalAddress(customerJson.getPostalAddress());
    result.setEmail(customerJson.getEmail());
    result.setPhone(customerJson.getPhone());

    result.setRegistryKey(customerJson.getRegistryKey());
    result.setOvt(customerJson.getOvt());
    result.setActive(customerJson.isActive());
    result.setSapCustomerNumber(customerJson.getSapCustomerNumber());

    result.setInvoicingProhibited(customerJson.isInvoicingProhibited());
    result.setInvoicingOperator(customerJson.getInvoicingOperator());
    result.setInvoicingOnly(customerJson.isInvoicingOnly());

    result.setCountry(customerJson.getCountry());
    result.setProjectIdentifierPrefix(customerJson.getProjectIdentifierPrefix());

    return result;
  }

  public ContactSearchResult mapToSearchResult(ContactJson contactJson) {
    ContactSearchResult result = new ContactSearchResult();
    result.setId(contactJson.getId());
    result.setName(contactJson.getName());
    result.setEmail(contactJson.getEmail());
    result.setCustomerId(contactJson.getCustomerId());
    result.setCustomerName(customerService.findCustomerById(contactJson.getCustomerId()).getName());
    return result;
  }
}
