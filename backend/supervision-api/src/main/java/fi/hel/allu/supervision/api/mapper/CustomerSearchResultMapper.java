package fi.hel.allu.supervision.api.mapper;

import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.supervision.api.domain.CustomerSearchResult;
import org.springframework.stereotype.Component;

@Component
public class CustomerSearchResultMapper {

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

}
